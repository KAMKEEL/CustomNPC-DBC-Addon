package kamkeel.npcdbc.data.SyncedData;

import kamkeel.npcdbc.util.u;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.IExtendedEntityProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Store all player CustomForms Data here
 */
public class CustomFormData extends PerfectSync<CustomFormData> implements IExtendedEntityProperties {
    public static String dn = "CustomFormData";

    // 0 for any non CNPC+ custom form, form ID if player is in custom form
    public int currentForm = 0;

    //forms given to player/player has unlocked
    public List<String> accessibleForms;


    public CustomFormData(Entity player) {
        super(player);
        this.DATA_NAME = dn;
        accessibleForms = new ArrayList<>();
    }

    public static CustomFormData get(Entity player) {
        return get(player, dn);

    }

    public static boolean eligibleForCustomForms(Entity p) {
        if (p instanceof EntityPlayer)
            return true;
        return false;
    }

    public static boolean has(Entity p) {
        return get(p, dn) != null;
    }

    public void addForm(String name) {
        if (!accessibleForms.contains(name)) {
            accessibleForms.add(name);
            saveNBTData(null);
            this.save(true);
        }
    }

    public void removeForm(String name) {
        if (accessibleForms.contains(name)) {
            accessibleForms.remove(name);
            saveNBTData(null);
            this.save(true);
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound c = compound(p, dn);

        c.setInteger("currentForm", currentForm);

        StringBuilder sb = new StringBuilder();
        for (String s : accessibleForms)
            sb.append(s + ";");
        c.setString("accessibleForms", sb.toString().substring(0, sb.length() - 1)); //removes the last ;


    }

    public void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound c = u.isServer() ? compound(p, dn) : compound;

        currentForm = c.getInteger("currentForm");

        String s = c.getString("accessibleForms");
        List<String> newForms = new ArrayList<>();
        if (s.contains(";")) {
            String[] forms = s.split(";");
            for (String str : forms)
                newForms.add(str);
        } else
            newForms.add(s);
        accessibleForms = newForms;


    }
}
