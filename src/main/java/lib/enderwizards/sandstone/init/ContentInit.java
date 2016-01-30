package lib.enderwizards.sandstone.init;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ContentInit is just an indicator that ContentHandler should initialize/register the block or item.
 * <p/>
 * Takes an optional itemBlock value, for custom ItemBlocks with Blocks.
 *
 * @author TheMike
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ContentInit {
}
