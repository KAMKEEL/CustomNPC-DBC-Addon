package kamkeel.npcdbc.data.overlay;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.data.JaninoScriptHandler;

/**
 * Server-persisted wrapper for a scripted overlay model.
 */
public class ScriptOverlayModel {

    public String key;

    /** Editable display name shown in the GUI. */
    public String name;

    /**
     * Lazily created handler. {@code null} until {@link #getScriptHandler()}
     * is called (typically only on the client).
     */
    private JaninoScriptHandler<OverlayModelScript> scriptHandler = new JaninoScriptHandler<>(OverlayModelScript::new, OverlayModelScript.class);;

    // -------------------- Construction --------------------

    public ScriptOverlayModel() {
        this.key = "";
        this.name = "";
    }

    /**
     * Creates a new overlay model with a display name.
     * The canonical key is derived from the name and locked immediately.
     */
    public ScriptOverlayModel(String name) {
        setName(name);
    }   
    
    public void setName(String name) {
        this.name = name;
        this.key = deriveKey(name);
    }

    // -------------------- Script handler (lazy) --------------------

    /**
     * Returns the live script handler.
     * <b>Client-only in practice</b>: server code should never need the
     * to compile the handler as it contains raw GL and client rendering calls
     */
    public JaninoScriptHandler<OverlayModelScript> getScriptHandler() {
        return scriptHandler;
    }
    

    // -------------------- Key derivation --------------------

    /**
     * Derives the immutable canonical key from a display name.
     * <p>
     * Format: {@code cnpc:custom/{slug}} where slug is the lowercased name
     * with spaces replaced by underscores and non-alphanumeric/underscore
     * characters stripped.
     */
    public static String deriveKey(String displayName) {
        if (displayName == null || displayName.trim().isEmpty())
            return "cnpc:custom/unnamed";

        String slug = displayName;

        if (slug.isEmpty())
            slug = "unnamed";

        return "cnpc:custom/" + slug;
    }

    // -------------------- NBT --------------------

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setString("Name", name);
        scriptHandler.writeToNBT(compound);
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        setName(compound.getString("Name"));
        scriptHandler.readFromNBT(compound);
    }

    // -------------------- Helpers --------------------

    /**
     * Returns whether this model has been saved at least once
     * (i.e. has a non-empty key).
     */
    public boolean hasKey() {
        return key != null && !key.isEmpty();
    }

    @Override
    public String toString() {
        return "ScriptOverlayModel{key='" + key + "', name='" + name + "'}";
    }
}
