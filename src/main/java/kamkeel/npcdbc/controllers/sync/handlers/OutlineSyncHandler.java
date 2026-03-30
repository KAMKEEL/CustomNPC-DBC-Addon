package kamkeel.npcdbc.controllers.sync.handlers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.controllers.sync.DBCSyncHandler;
import kamkeel.npcdbc.data.outline.Outline;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;

public class OutlineSyncHandler implements DBCSyncHandler {

    @Override
    public NBTTagCompound serializeAll() {
        NBTTagList list = new NBTTagList();
        for (Outline outline : OutlineController.getInstance().customOutlines.values()) {
            list.appendTag(outline.writeToNBT());
        }
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("Data", list);
        return compound;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleReload(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("Data", 10);
        OutlineController oc = OutlineController.getInstance();
        for (int i = 0; i < list.tagCount(); i++) {
            Outline outline = new Outline();
            outline.readFromNBT(list.getCompoundTagAt(i));
            oc.customOutlinesSync.put(outline.id, outline);
        }
        oc.customOutlines = oc.customOutlinesSync;
        oc.customOutlinesSync = new HashMap<>();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleUpdate(NBTTagCompound compound) {
        Outline outline = new Outline();
        outline.readFromNBT(compound);
        OutlineController.getInstance().customOutlines.put(outline.id, outline);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleRemove(int id) {
        OutlineController.getInstance().customOutlines.remove(id);
    }

    @Override public boolean supportsUpdate() { return true; }
    @Override public boolean supportsRemove() { return true; }
}
