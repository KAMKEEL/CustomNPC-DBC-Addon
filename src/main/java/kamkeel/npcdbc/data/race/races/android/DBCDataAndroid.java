package kamkeel.npcdbc.data.race.races.android;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.race.races.android.AndroidPartSlot;
import kamkeel.npcdbc.data.race.races.android.AndroidPartType;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;
import net.minecraft.nbt.NBTTagCompound;

import java.util.LinkedHashMap;
import java.util.Map;

public class DBCDataAndroid {

    private final DBCData data;

    // slot -> part id string (null = empty)
    private final Map<AndroidPartSlot, String> equippedParts = new LinkedHashMap<>();

    public DBCDataAndroid(DBCData data) {
        this.data = data;
    }

    // ──────────────────── Public API ────────────────────

    public AndroidPartType getEquipped(AndroidPartSlot slot) {
        String id = equippedParts.get(slot);
        if (id == null || id.isEmpty()) return null;
        return AndroidPartType.byId(id);
    }

    public boolean isSlotEmpty(AndroidPartSlot slot) {
        return getEquipped(slot) == null;
    }

    public void equip(AndroidPartSlot slot, AndroidPartType part) {
        if (part != null && !part.fitsSlot(slot))
            throw new IllegalArgumentException("Part " + part.getId() + " does not fit slot " + slot.name());
        equippedParts.put(slot, part != null ? part.getId() : "");
    }

    public void unequip(AndroidPartSlot slot) {
        equippedParts.put(slot, "");
    }

    // ──────────────────── NBT ────────────────────

    public void saveToNBT(NBTTagCompound comp) {
        NBTTagCompound tag = new NBTTagCompound();
        for (AndroidPartSlot slot : AndroidPartSlot.values()) {
            String id = equippedParts.getOrDefault(slot, "");
            tag.setString(slot.name(), id);
        }
        comp.setTag("androidParts", tag);
    }

    public void loadFromNBT(NBTTagCompound comp) {
        equippedParts.clear();
        if (!comp.hasKey("androidParts")) return;

        NBTTagCompound tag = comp.getCompoundTag("androidParts");
        for (AndroidPartSlot slot : AndroidPartSlot.values()) {
            if (tag.hasKey(slot.name())) {
                equippedParts.put(slot, tag.getString(slot.name()));
            }
        }
    }
}
