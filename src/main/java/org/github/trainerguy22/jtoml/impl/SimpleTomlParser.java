package org.github.trainerguy22.jtoml.impl;

import org.github.trainerguy22.jtoml.TomlParser;
import org.github.trainerguy22.jtoml.Util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builtin Toml parser.
 * <p/>
 * <p>Uses a first pass to make multi-line arrays one-liner, then iterate line by line, matching against known regular expressions,
 * to extract content and store it into a context map.</p>
 *
 * @author <a href="mailto:a.grison@gmail.com">$Author: Alexandre Grison$</a>
 */
public class SimpleTomlParser implements TomlParser {
    /**
     * Encapsulate both a Matcher and a method to cast the retrieved value to the according type.
     */
    static abstract class Handler {
        // Keep them to avoid recreating it. Patterns are thread safe
        static final Map<String, Pattern> PATTERNS = new HashMap<String, Pattern>();
        final Matcher matcher;

        public Handler(String regex) {
            this.matcher = getPattern(regex).matcher("");
        }

        public Pattern getPattern(String regex) {
            if (!PATTERNS.containsKey(regex))
                PATTERNS.put(regex, Pattern.compile(regex));
            return PATTERNS.get(regex);
        }

        Matcher matcher() {
            return this.matcher;
        }

        abstract Object cast(String v);
    }

    // String regex utils
    private static final String SPACES = "\\s*";
    private static final String POSSIBLE_COMMENT = "(#.*)?";
    private static final String KEY_EQUALS = "(" + SPACES + "(\\w[a-zA-Z_0-9\\-]+)" + SPACES + "=" + SPACES + ")?";
    private static final String ARRAY = SPACES + "\\[" + SPACES + "(.*)" + SPACES + "\\]" + SPACES;
    private static final String DATE = "(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*)";
    private static final String DOUBLE = "([-+]?\\d*\\.\\d+([eE][-+]?\\d+)?)";
    private static final String DIGITS = "(\\d+)";
    private static final String STRING = "\"(.*)\"";
    private static final String BOOLEAN = "(true|false)";
    // Common patterns
    private static final Pattern ARRAY_LINE_PATTERN = Pattern.compile(KEY_EQUALS + ARRAY, Pattern.DOTALL);
    private static final Pattern GROUP_PATTERN = Pattern.compile(SPACES + "\\[(.*)\\]" + SPACES);
    private static final Pattern COMMENT_PATTERN = Pattern.compile("(,|\"|\\])\\s*(#.*)");
    private static final Pattern LINES_PATTERN = Pattern.compile("([^\n]+)\n?");
    // Current instance matchers
    private final Matcher arrayLineMatcher = ARRAY_LINE_PATTERN.matcher("");
    private final Matcher groupMatcher = GROUP_PATTERN.matcher("");
    private final Matcher commentMatcher = COMMENT_PATTERN.matcher("");
    private final Matcher lineMatcher = LINES_PATTERN.matcher("");
    /**
     * The list of handlers
     */
    private final List<Handler> handlers = new ArrayList<Handler>() {{
        // dates
        add(new Handler(KEY_EQUALS + DATE) {
            Object cast(String v) {
                try {
                    return Util.ISO8601.toCalendar(v);
                } catch (Exception e) {
                    return null;
                }
            }
        });
        // doubles
        add(new Handler(KEY_EQUALS + DOUBLE + SPACES + POSSIBLE_COMMENT) {
            Object cast(String v) {
                return Double.valueOf(v);
            }
        });
        // integers
        add(new Handler(KEY_EQUALS + DIGITS + SPACES + POSSIBLE_COMMENT) {
            Object cast(String v) {
                return (int) Math.max(Math.min(Integer.MAX_VALUE, Long.valueOf(v)), Integer.MIN_VALUE);
            }
        });
        // strings
        add(new Handler(KEY_EQUALS + STRING + SPACES) {
            Object cast(String v) {
                return Util.TomlString.unescape(v.trim());
            }
        });
        // booleans
        add(new Handler(KEY_EQUALS + BOOLEAN + SPACES + POSSIBLE_COMMENT) {
            Object cast(String v) {
                return Boolean.parseBoolean(v);
            }
        });
    }};

    @Override
    public Map<String, Object> parse(String tomlString) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        Map<String, Object> context = result;
        tomlString = prepareArrays(tomlString);
        // match lines
        lineMatcher.reset(tomlString);
        while (lineMatcher.find()) {
            String line = lineMatcher.group().trim();
            if (commentMatcher.reset(line).find()) {
                line = line.replace(commentMatcher.group(2), "");
            }
            if (groupMatcher.reset(line).matches()) {
                context = createContextIfNeeded(result, groupMatcher.group(1));
            }
            Object[] val = readObject(line);
            if (val != null && val[0] != null)
                context.put((String) val[0], val[1]);
        }
        return result;
    }

    /**
     * Find every arrays in the given String and make them one liner.
     * prepareArrays('foo = [\n 1, 2, 3,\n 4,\n 5, #this is ok\n ]')
     * -> 'foo = [ 1, 2, 3, 4, 5 ]'
     *
     * @return the given String with arrays on one line
     */
    private String prepareArrays(String s) {
        StringBuffer buffer = new StringBuffer();
        String currentLine = "";
        for (String l : s.split("\n")) {
            currentLine = currentLine + l;
            if (Util.TomlString.countOccurrences(currentLine, "[") == Util.TomlString.countOccurrences(currentLine, "]")) {
                if (l.equals(currentLine)) { // nothing done
                    buffer.append(currentLine);
                } else { // multiline -> single line
                    buffer.append(
                            currentLine.replaceAll("#[^],]+", "") // skip comments
                                    .replaceAll("\\[\\s*", "[").replaceAll("\\s*\\]", "]") // remove spaces around brackets
                                    .replaceAll(",\\s*", ",").replaceAll(",,", ",") // spaces and empty commas
                    );
                }
                buffer.append("\n");
                currentLine = "";
            }
        }
        return buffer.toString();
    }

    /**
     * Create the context if needed.
     * createContextIfNeeded({}, "foo.bar.bazz")
     * -> {"foo": {"bar": {"bazz": {}}}}
     *
     * @param context the context
     * @param key     the key
     * @return the newly created level
     */
    private Map<String, Object> createContextIfNeeded(Map<String, Object> context, String key) {
        Map<String, Object> visitor = context;
        for (String part : key.split("[.]")) {
            if (!visitor.containsKey(part)) {
                visitor.put(part, new LinkedHashMap<String, Object>());
            }
            if (!(visitor.get(part) instanceof Map)) {
                throw new IllegalArgumentException(//
                        "Overwriting a previous key is forbidden. Trying to overwrite key `" + key + "` having value `" + visitor.get(part) + "`");
            }
            visitor = (Map<String, Object>) visitor.get(part);
        }
        return visitor;
    }

    /**
     * Read the given line and returns an array of Object like the following:
     * index 0: the key
     * index 1: the value
     *
     * @param line the line where to extract key/value
     */
    private Object[] readObject(String line) {
        for (Handler handler : this.handlers) {
            if (handler.matcher().reset(line).matches()) {
                String key = handler.matcher().group(2);
                Object value = handler.cast(handler.matcher().group(3));
                return new Object[]{key, value};
            }
        }
        // it might be an array
        if (arrayLineMatcher.reset(line).matches()) {
            String key = arrayLineMatcher.group(2);
            String array = arrayLineMatcher.group(3);
            List<Object> values = new ArrayList<Object>();
            // find nested arrays
            if (array.matches(".*(?:\\]),.*")) {
                for (String nested : array.split("(?:\\]),")) {
                    nested += "]";
                    Object nestedArray[] = readObject(nested.trim());
                    if (nestedArray != null)
                        values.add(nestedArray[1]);
                }
            } else {
                for (String value : array.split(",")) {
                    value = value.trim();
                    if (value.endsWith("]"))
                        value = value.substring(0, value.length() - 1);
                    Object[] nested = readObject(value.trim());
                    if (nested != null)
                        values.add(nested[1]);
                }
            }
            // Check all values have the same type
            if (values != null && values.size() > 0) {
                Set<Class<?>> types = new HashSet<Class<?>>();
                for (Object o : values) {
                    types.add(o.getClass());
                }
                if (types.size() > 1) {
                    throw new IllegalArgumentException("Inconsistent types found while parsing array. " + //
                            "Found all the following types in the same array declaration: " + types);
                }
            }
            return new Object[]{key, values};
        }
        return null;
    }
}
