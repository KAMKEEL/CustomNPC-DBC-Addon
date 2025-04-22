package kamkeel.npcdbc.data.form;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.data.MagicEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FormMagicData {
    private static final String TAG_FORM_MAGIC = "formMagicData";
    private final Map<Integer, MagicEntry> magics = new HashMap<>();

    private final Form parent;
    public boolean overridePlayerMagic = true;

    public FormMagicData(Form parent) {
        this.parent = parent;
    }

    public void writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound list = new NBTTagCompound();
        for (Map.Entry<Integer, MagicEntry> e : magics.entrySet()) {
            list.setTag(String.valueOf(e.getKey()), e.getValue().writeToNBT());
        }
        tag.setTag("Data", list);
        tag.setBoolean("Override", overridePlayerMagic);

        compound.setTag(TAG_FORM_MAGIC, tag);
    }

    /** Read them back in; must be called in Form.readFromNBT(...) */
    public void readFromNBT(NBTTagCompound compound) {
        magics.clear();
        if (!compound.hasKey(TAG_FORM_MAGIC)) {
            return;
        }
        NBTTagCompound tag = compound.getCompoundTag(TAG_FORM_MAGIC);
        this.overridePlayerMagic = tag.getBoolean("Override");

        NBTTagCompound data = tag.getCompoundTag("Data");
        Set<String> keys = data.func_150296_c();
        for (String k : keys) {
            try {
                int id = Integer.parseInt(k);
                MagicEntry entry = new MagicEntry();
                entry.readToNBT(data.getCompoundTag(k));
                magics.put(id, entry);
            } catch (NumberFormatException ex) {
            }
        }
    }

    public void addMagic(int id, float damage, float split) {
        MagicEntry entry = new MagicEntry();
        entry.damage = damage;
        entry.split = split;
        magics.put(id, entry);
    }

    public void removeMagic(int id) {
        magics.remove(id);
    }

    public boolean hasMagic(int id) {
        return magics.containsKey(id);
    }

    public float getMagicDamage(int id) {
        MagicEntry e = magics.get(id);
        return e == null ? 0f : e.damage;
    }

    public float getMagicSplit(int id) {
        MagicEntry e = magics.get(id);
        return e == null ? 0f : e.split;
    }

    public Map<Integer, MagicEntry> getAll() {
        return magics;
    }

    public boolean isEmpty() {
        return magics.isEmpty();
    }

    public void clear() {
        magics.clear();
    }

    public boolean isOverridePlayerMagic() {
        return overridePlayerMagic;
    }

    public void setOverridePlayerMagic(boolean overridePlayerMagic) {
        this.overridePlayerMagic = overridePlayerMagic;
    }
}
