package kamkeel.npcdbc.controllers.sync.handlers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.sync.DBCSyncHandler;
import kamkeel.npcdbc.data.aura.Aura;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;

public class AuraSyncHandler implements DBCSyncHandler {

    @Override
    public NBTTagCompound serializeAll() {
        NBTTagList list = new NBTTagList();
        for (Aura aura : AuraController.getInstance().customAuras.values()) {
            list.appendTag(aura.writeToNBT());
        }
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("Data", list);
        return compound;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleReload(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("Data", 10);
        AuraController ac = AuraController.getInstance();
        for (int i = 0; i < list.tagCount(); i++) {
            Aura aura = new Aura();
            aura.readFromNBT(list.getCompoundTagAt(i));
            ac.customAurasSync.put(aura.id, aura);
        }
        ac.customAuras = ac.customAurasSync;
        ac.customAurasSync = new HashMap<>();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleUpdate(NBTTagCompound compound) {
        Aura aura = new Aura();
        aura.readFromNBT(compound);
        AuraController.getInstance().customAuras.put(aura.id, aura);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleRemove(int id) {
        AuraController.getInstance().customAuras.remove(id);
    }

    @Override public boolean supportsUpdate() { return true; }
    @Override public boolean supportsRemove() { return true; }
}
