package xreliquary.init;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XRInit {
   Class itemBlock() default XRInit.class;
}
