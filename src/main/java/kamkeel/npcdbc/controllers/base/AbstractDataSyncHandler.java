package kamkeel.npcdbc.controllers.base;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcs.controllers.sync.SyncHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDataSyncHandler<T extends IControllerSerializable>
        implements SyncHandler {

    // ═══════════════════════════════════════════════════════
    //  TEMPLATE METHODS — Impls MUST override
    // ═══════════════════════════════════════════════════════

    protected abstract AbstractDataController<T> getController();

    // ═══════════════════════════════════════════════════════
    //  CONCRETE — Full implementation
    // ═══════════════════════════════════════════════════════

    @Override
    public NBTTagCompound serializeAll() {
        NBTTagList list = new NBTTagList();
        for (T item : getController().customData.values()) {
            list.appendTag(item.serialize(DataCompound.create()).toNbt());
        }
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("Data", list);
        return compound;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleReload(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("Data", 10);
        Map<String, T> newData = new HashMap<>();
        for (int i = 0; i < list.tagCount(); i++) {
            T item = getController().createNew();
            item.deserialize(DataCompound.ofNbt(list.getCompoundTagAt(i)));
            if (item.hasKey())
                newData.put(item.getKey(), item);
        }
        getController().setAllData(newData);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleUpdate(NBTTagCompound compound, String key) {
        T item = getController().createNew();
        item.deserialize(DataCompound.ofNbt(compound));
        getController().put(item);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleRemove(String key) {
        getController().remove(key);
    }

    @Override
    public boolean supportsUpdate() { return true; }

    @Override
    public boolean supportsRemove() { return true; }
}
