package lib.enderwizards.sandstone.util.misc;

/**
 * Duo, a simple generic class for holding two objects. The two objects held in the Duo are immutable.
 *
 * @author TheMike
 */
public class Duo<K, V> {

    public final K one;
    public final V two;

    public Duo(K par1, V par2) {
        one = par1;
        two = par2;
    }

}
