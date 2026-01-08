package kamkeel.npcdbc.util;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import static kamkeel.npcdbc.constants.DBCStatusEffects.*;

public class StatusEffectUtil {

    public static NBTTagCompound nbt(EntityPlayer player) {
        if (player == null)
            return null;
        return data(player).getRawCompound();
    }

    private static DBCData data(EntityPlayer player) {
        return DBCData.get(player);
    }

    public static boolean contains(EntityPlayer player, int id) {
        return data(player).containsSE(id);
    }

    public static boolean contains(EntityPlayer player, String id) {
        if (nbt(player) == null) return false;

        if (!id.equals(NoFuse)) {
            return nbt(player).getInteger(id) > 0;
        }

        try {
            int result = Integer.parseInt(nbt(player).getString(NoFuse));
            return result > 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void set(EntityPlayer player, int id, boolean enable) {
        data(player).StatusEffects = data(player).setSE(id, enable);
    }

    public static void set(EntityPlayer player, String id, int time) {
        time *= 12;

        if (!id.equals(NoFuse)) {
            nbt(player).setInteger(id, time);
        } else {
            nbt(player).setString(NoFuse, time + "");
        }
    }

    public static byte getState(EntityPlayer player) {
        return nbt(player).getByte("jrmcState2");
    }

    public static void setState(EntityPlayer player, int state) {
        state = Math.max(0, Math.min(6, state));
        nbt(player).setByte("jrmcState2", (byte) state);
    }

    //---------------- PERMANENT EFFECTS ----------------

    public static boolean isTransforming(EntityPlayer player) {
        return contains(player, Transforming);
    }

    public static boolean isReleasing(EntityPlayer player) {
        return contains(player, Release);
    }

    public static boolean isMeditating(EntityPlayer player) {
        return data(player).Skills.contains("MD") && isReleasing(player);
    }

    public static boolean isTurbo(EntityPlayer player) {
        return contains(player, Turbo);
    }

    public static void setTurbo(EntityPlayer player, boolean enable) {
        data(player).setTurboState(enable);
    }

    public static boolean isSwooping(EntityPlayer player) {
        return contains(player, Swooping);
    }

    public static void setSwooping(EntityPlayer player, boolean enable) {
        set(player, Swooping, enable);
    }

    public static boolean isMajin(EntityPlayer player) {
        return contains(player, Majin);
    }

    public static void setMajin(EntityPlayer player, boolean enable) {
        set(player, Majin, enable);
    }

    public static boolean isLegendary(EntityPlayer player) {
        return contains(player, Legendary);
    }

    public static void setLegendary(EntityPlayer player, boolean enable) {
        set(player, Legendary, enable);
    }

    public static boolean isDivine(EntityPlayer player) {
        return contains(player, Divine);
    }

    public static void setDivine(EntityPlayer player, boolean enable) {
        set(player, Divine, enable);
    }

    public static boolean isKaioken(EntityPlayer player) {
        return contains(player, Kaioken);
    }

    public static void setKaioken(EntityPlayer player, boolean enable) {
        set(player, Kaioken, enable);
        setState(player, 1);
    }

    public static void setKaioken(EntityPlayer player, boolean enable, int state) {
        set(player, Kaioken, enable);
        setState(player, state);
    }

    public static boolean isMystic(EntityPlayer player) {
        return contains(player, Mystic);
    }

    public static void setMystic(EntityPlayer player, boolean enable) {
        set(player, Mystic, enable);
    }

    public static boolean isUI(EntityPlayer player) {
        return contains(player, UI);
    }

    public static void setUI(EntityPlayer player, boolean enable) {
        set(player, UI, enable);
        setState(player, 1);
    }

    public static void setUI(EntityPlayer player, boolean enable, int state) {
        set(player, UI, enable);
        setState(player, state);
    }

    public static boolean isGOD(EntityPlayer player) {
        return contains(player, GoD);
    }

    public static void setGOD(EntityPlayer player, boolean enable) {
        set(player, GoD, enable);
    }

    public static boolean isFused(EntityPlayer player) {
        return data(player).stats.isFused();
    }

    public static boolean isFusionController(EntityPlayer player) {
        return data(player).stats.isFusionController();
    }

    public static boolean isFusionSpectator(EntityPlayer player) {
        return data(player).stats.isFusionSpectator();
    }

    //---------------- TIMER EFFECTS ----------------

    public static boolean isKO(EntityPlayer player) {
        return data(player).isKO;
    }

    public static void setKO(EntityPlayer player, int time) {
        set(player, KO, time);
    }

    public static boolean hasStrain(EntityPlayer player) {
        return contains(player, Strain);
    }

    public static void setStrain(EntityPlayer player, int time) {
        set(player, Strain, time);
    }

    public static boolean hasFatigue(EntityPlayer player) {
        return contains(player, Fatigue);
    }

    public static void setFatigue(EntityPlayer player, int time) {
        set(player, Fatigue, time);
    }

    public static boolean hasGodPower(EntityPlayer player) {
        return contains(player, GodPower);
    }

    public static void setGodPower(EntityPlayer player, int time) {
        set(player, GodPower, time);
    }

    public static boolean hasPain(EntityPlayer player) {
        return contains(player, Pain);
    }

    public static void setPain(EntityPlayer player, int time) {
        set(player, Pain, time);
    }

    public static boolean hasNoFuse(EntityPlayer player) {
        return contains(player, NoFuse);
    }

    public static void setNoFuse(EntityPlayer player, int time) {
        set(player, NoFuse, time);
    }

    public static boolean hasHeat(EntityPlayer player) {
        return contains(player, Heat);
    }

    public static void setHeat(EntityPlayer player, int time) {
        set(player, Heat, time);
    }
}
