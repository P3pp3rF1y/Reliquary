package org.github.trainerguy22.jtoml;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Toml Utilities.
 *
 * @author <a href="mailto:a.grison@gmail.com">$Author: Alexandre Grison$</a>
 */
public class Util {
    /**
     * <p>Helper class for handling ISO 8601 strings of the following format:</p>
     * <p>"2008-03-01T13:00:00+01:00". It also supports parsing the "Z" timezone.</p>
     * <p/>
     * <p>Author: <a href="http://stackoverflow.com/users/1010931/wrygiel">wrigiel</a></p>
     * <p>Taken from: http://stackoverflow.com/questions/2201925/converting-iso8601-compliant-string-to-java-util-date</p>
     */
    public static class ISO8601 {
        /** Transform ISO 8601 string to Calendar. */
        public static Calendar toCalendar(final String iso8601string) throws ParseException {
            Calendar calendar = GregorianCalendar.getInstance();
            String s = iso8601string.replace("Z", "+00:00");
            try {
                s = s.substring(0, 22) + s.substring(23);
            } catch (IndexOutOfBoundsException e) {
                throw new ParseException("Invalid length", 0);
            }
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
            calendar.setTime(date);
            return calendar;
        }
         /** Transform Calendar to ISO 8601 string */
        public static String fromCalendar(final Calendar calendar) {
            Date date = calendar.getTime();
            String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date);
            return (formatted.substring(0, 22) + ":" + formatted.substring(22)).replace("+00:00", "Z");
        }
    }

    /**
     * Toml String Utilities.
     * <p/>
     * Note: This is to avoid dependency on Apache commons for such limited features.
     */
    public static class TomlString {
        private static Map<Character, Character> ESCAPE = new HashMap<Character, Character>();
        private static Map<Character, Character> UNESCAPE = new HashMap<Character, Character>() {{
            put('0', '\u0000');
            put('t', '\t');
            put('n', '\n');
            put('r', '\r');
            put('\\', '\\');
            put('"', '"');
        }};
        static {
            for (Map.Entry<Character, Character> e: UNESCAPE.entrySet()) {
                ESCAPE.put(e.getValue(), e.getKey());
            }
        }

        /**
         * Unescape a list of literals found in the given String.
         * It will replace characters (<code>'\' + 't'</code>, <code>'\' + 'n'</code>, ...) to there equivalent (<code>'\t'</code>, <code>'\n'</code>).
         *
         * <ul>
         *     <li><code>'\'</code> + <code>'0'</code> -> <code>'\0'</code> null character (0x00)</li>
         *     <li><code>'\'</code> + <code>'t'</code> -> <code>'\t'</code> tab character (0x09)</li>
         *     <li><code>'\'</code> + <code>'n'</code> -> <code>'\n'</code> newline character (0x0a)</li>
         *     <li><code>'\'</code> + <code>'r'</code> -> <code>'\r'</code> carriage return character (0x0d)</li>
         *     <li><code>'\'</code> + <code>'"'</code> -> <code>'"'</code> quote character (0x22)</li>
         *     <li><code>'\'</code> + <code>'\'</code> -> <code>'\'</code> backslash character (0x5c)</li>
         * </ul>
         *
         * @param input the String to unescape
         * @return the unescaped String
         */
        public static String unescape(String input) {
            StringBuffer buffer = new StringBuffer(input.length());
            for (int i = 0; i < input.length(); i++) {
                char ch = input.charAt(i);
                if (ch != '\\') {
                    buffer.append(ch);
                } else {
                    if (i == input.length() - 1) {
                        throw new IllegalStateException("Invalid escape sequence at the end of the input.");
                    }
                    ch = input.charAt(++i);
                    if (UNESCAPE.containsKey(ch)) {
                        buffer.append(UNESCAPE.get(ch));
                    } else if (ch == 'u') {
                        String unicodeHexValue = String.valueOf(input.charAt(++i)) + String.valueOf(input.charAt(++i)) +
                                String.valueOf(input.charAt(++i)) + String.valueOf(input.charAt(++i));
                        if (unicodeHexValue.matches("[0-9a-fA-F]{4}")) {
                            buffer.append((char)Integer.parseInt(unicodeHexValue, 16));
                        }
                    } else {
                        throw new IllegalArgumentException("Escape sequence \\ " + ch + " in isn't known. " + //
                                "Known sequences are: " + "\\0, \\t, \\n, \\b, \\r, \\\\, \\\".\n" + //
                                "Offending string: " + input + "\n" + "                 " + createWhitespaceString(i) + "^");
                    }
                }
            }
            return buffer.toString();
        }

        /**
         * Escapes a list of literals found in the given String.
         * It will replace characters (<code>'\t'</code>, <code>'\n'</code>, ...) to there equivalent (<code>'\' + 't'</code>, <code>'\' + 'n'</code>).
         *
         * <ul>
         *     <li><code>'\0'</code> null character (0x00) -> <code>'\'</code> + <code>'0'</code></li>
         *     <li><code>'\t'</code> tab character (0x09) -> <code>'\'</code> + <code>'t'</code></li>
         *     <li><code>'\n'</code> newline character (0x0a) -> <code>'\'</code> + <code>'n'</code></li>
         *     <li><code>'\r'</code> carriage return character (0x0d) -> <code>'\'</code> + <code>'r'</code></li>
         *     <li><code>'\"'</code> quote character (0x22) -> <code>'\'</code> + <code>'"'</code> </li>
         *     <li><code>'\\'</code> backslash character (0x5c) -> <code>'\'</code> + <code>'\'</code></li>
         * </ul>
         *
         * @param input the String to escape
         * @return the escaped String
         */
        public static String escape(String input) {
            StringBuffer buffer = new StringBuffer(input.length());
            for (int i = 0; i < input.length(); i++) {
                char ch = input.charAt(i);
                if (ESCAPE.containsKey(ch)) {
                    buffer.append("\\").append(ESCAPE.get(ch));
                } else if ((ch >= 0x00 && ch < 0x20 /* SPACE */) || (ch >= 0x7F /* DEL and above */)) {
                    buffer.append("\\u").append(Integer.toHexString(ch));
                } else {
                    buffer.append(ch);
                }
            }
            return buffer.toString();
        }

        /**
         * Creates a String made of spaces repeated for the given amount of time.
         * <p/>
         * <code>createWhitespaceString(3) -> "&nbsp;&nbsp;&nbsp;" // 3 spaces</code>
         *
         * @param length the length of the resulting String
         * @return a String made of a number of spaces equals to the given `length` parameter.
         */
        private static String createWhitespaceString(int length) {
            char[] charArray = new char[length];
            Arrays.fill(charArray, ' ');
            return String.valueOf(charArray);
        }

        /**
         * Counts the number of occurrences of a String in an other String.
         *
         * @param str the String to search in.
         * @param needle the String to count the occurrences of.
         * @return the number of occurrences found.
         */
        public static int countOccurrences(String str, String needle) {
            int index = 0, count = 0;
            while (index != -1) {
                index = str.indexOf(needle, index);
                if (index != -1) {
                    count++;
                    index += needle.length();
                }
            }
            return count;
        }
    }

    /**
     * Utilities around File to String conversions.
     * <p/>
     * Note: This is to avoid dependency on Apache commons for such limited features.
     */
    public static class FileToString {
        public static String read(File file) throws FileNotFoundException {
            try {
                return new Scanner(file).useDelimiter("\\Z").next();
            } catch (NoSuchElementException e) {
                return "";
            }
        }
    }

    /**
     * Utilities around Reflection.
     */
    public static class Reflection {
        /** List of types supported Natively by TOML + Map */
        static final Set<Class<?>> TOML_SUPPORTED = new HashSet<Class<?>>(Arrays.asList(
                int.class, Integer.class, //
                long.class, Long.class, //
                double.class, Double.class, //
                Calendar.class, //
                char.class, char[].class,
                boolean.class, Boolean.class, String.class, List.class, Map.class));

        /**
         * Returns whether the given type is a built-in toml supported type.
         *
         * @param clazz a class object.
         * @return whether it is a built-in toml type
         */
        public static boolean isTomlSupportedType(Class<?> clazz) {
            return TOML_SUPPORTED.contains(clazz);
        }

        public static boolean isTomlSupportedTypeExceptMap(Class<?> clazz) {
            return TOML_SUPPORTED.contains(clazz) && !clazz.equals(Map.class);
        }

        /**
         * Set the value of the given field on given object.
         *
         * @param field the field
         * @param object the object
         * @param value the value
         * @throws IllegalAccessException
         */
        public static void setFieldValue(Field field, Object object, Object value) throws IllegalAccessException {
            boolean isAccessible = field.isAccessible();
            field.setAccessible(true);
            field.set(object, value);
            field.setAccessible(isAccessible);
        }

        /**
         * Get the value of the given field on given object.
         *
         * @param field the field
         * @param object the object
         * @throws IllegalAccessException
         */
        public static Object getFieldValue(Field field, Object object) throws IllegalAccessException {
            boolean isAccessible = field.isAccessible();
            field.setAccessible(true);
            Object result = field.get(object);
            field.setAccessible(isAccessible);
            return result;
        }

        /**
         * Returns a {@link TomlFieldComparator} instantiated with the given list of Fields.
         *
         * @param fields the list of fields of an object.
         * @return the {@link TomlFieldComparator}
         */
        public static TomlFieldComparator newTomlFieldComparator(List<Field> fields) {
            return new TomlFieldComparator(fields);
        }
    }

    /**
     * Custom comparator to sort fields of a class depending on its type.
     * <ul>
     *     <li>Keeps ordering of the original list of declared fields</li>
     *     <li>Primitive types comes before complex types ({@link Map} and Custom types).</li>
     * </ul>
     */
    public static class TomlFieldComparator implements Comparator<Field> {
        List<Field> originalFields;

        public TomlFieldComparator(List<Field> fields) {
            this.setOriginalFields(fields);
        }

        public void setOriginalFields(List<Field> originalFields) {
            this.originalFields = originalFields;
        }

        @Override
        public int compare(Field field1, Field field2) {
            boolean o1Supported = Util.Reflection.isTomlSupportedTypeExceptMap(field1.getType());
            boolean o2Supported = Util.Reflection.isTomlSupportedTypeExceptMap(field2.getType());
            // if built-in types or both complex types, keep original ordering
            if ((o1Supported && o2Supported) || (!o1Supported && !o2Supported)) {
                return Integer.valueOf(originalFields.indexOf(field1)).compareTo(originalFields.indexOf(field2));
            } else { // put complex types at the end
                return o1Supported ? -1 : 1;
            }
        }
    };
}
