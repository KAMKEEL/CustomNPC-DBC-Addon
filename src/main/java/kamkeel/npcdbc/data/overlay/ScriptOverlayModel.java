package kamkeel.npcdbc.data.overlay;

import kamkeel.npcdbc.controllers.base.IControllerSerializable;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.data.JaninoScriptHandler;

public class ScriptOverlayModel implements IControllerSerializable {

    public String key;
    public String name;

    private JaninoScriptHandler<OverlayModelScript> scriptHandler = new JaninoScriptHandler<>(OverlayModelScript::new, OverlayModelScript.class);;

    public ScriptOverlayModel() {
        this.key = "";
        this.name = "";
    }

    public ScriptOverlayModel(String name) {
        setName(name);
    }   
    
    public void setName(String name) {
        this.name = name;
        this.key = deriveKey(name);
    }

    // -------------------- IControllerSerializable --------------------

    @Override
    public String getKey() { return key; }

    @Override
    public String getDisplayName() { return name; }

    @Override
    public void setDisplayName(String name) { setName(name); }

    // -------------------- Script handler --------------------

    public JaninoScriptHandler<OverlayModelScript> getScriptHandler() {
        return scriptHandler;
    }

    // -------------------- Key derivation --------------------

    public static String deriveKey(String displayName) {
        if (displayName == null || displayName.trim().isEmpty())
            return "cnpc:custom/unnamed";

        String slug = displayName;

        if (slug.isEmpty())
            slug = "unnamed";

        return "cnpc:custom/" + slug;
    }

    // -------------------- DataSerializable --------------------

    @Override
    public DataCompound serialize(DataCompound data) {
        data.putString("Name", name);
        scriptHandler.writeToNBT(data.toNbt());
        return data;
    }

    @Override
    public void deserialize(DataCompound data) {
        setName(data.getString("Name", ""));
        scriptHandler.readFromNBT(data.toNbt());
    }

    @Override
    public String toString() {
        return "ScriptOverlayModel{key='" + key + "', name='" + name + "'}";
    }
}
