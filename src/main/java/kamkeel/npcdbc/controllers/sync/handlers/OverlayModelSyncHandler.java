package kamkeel.npcdbc.controllers.sync.handlers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.controllers.OverlayModelController;
import kamkeel.npcdbc.data.overlay.ScriptOverlayModel;
import kamkeel.npcs.controllers.sync.SyncHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;
import java.util.Map;

public class OverlayModelSyncHandler implements SyncHandler {

    @Override
    public NBTTagCompound serializeAll() {
        NBTTagList list = new NBTTagList();
        for (ScriptOverlayModel model : OverlayModelController.getInstance().customModels.values()) {
            list.appendTag(model.writeToNBT(new NBTTagCompound()));
        }
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("Data", list);
        return compound;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleReload(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("Data", 10);
        Map<String, ScriptOverlayModel> newModels = new HashMap<>();
        for (int i = 0; i < list.tagCount(); i++) {
            ScriptOverlayModel model = new ScriptOverlayModel();
            model.readFromNBT(list.getCompoundTagAt(i));
            if (model.hasKey())
                newModels.put(model.key, model);
        }
        OverlayModelController.getInstance().setCustomModelData(newModels);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleUpdate(NBTTagCompound compound, String key) {
        ScriptOverlayModel model = new ScriptOverlayModel();
        model.readFromNBT(compound);
        OverlayModelController.getInstance().put(model);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleRemove(String key) {
        OverlayModelController.getInstance().remove(key);
    }

    @Override
    public boolean supportsUpdate() { return true; }

    @Override
    public boolean supportsRemove() { return true; }
}
