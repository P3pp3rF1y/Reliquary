package lib.enderwizards.sandstone.mod;

/**
 * ModIntegration, a simple class for integrating with other mods.
 * Call Sandstone.addModIntegration(ModIntegration) to register.
 *
 * @author TheMike
 */
public abstract class ModIntegration {

    public final String modId;

    public ModIntegration(String modId) {
        this.modId = modId;
    }

    /**
     * Will be called in the post initiation phase.
     *
     * @param status true if the mod is loaded, false otherwise.
     */
    public abstract void onLoad(boolean status);

}
