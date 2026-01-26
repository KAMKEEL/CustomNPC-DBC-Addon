package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.api.util.IDBCSettingsHandler;
import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.entity.IPlayer;

public class DBCSettingsController implements IDBCSettingsHandler {
    public static DBCSettingsController Instance;

    public DBCSettingsController() {
        Instance = this;
    }

    public static DBCSettingsController getInstance() {
        return Instance;
    }

    private static EntityPlayer eP(IPlayer player) {
        return (EntityPlayer) player.getMCEntity();
    }

    public String getSettings(IPlayer player) {
        return DBCSettingsUtil.settings(eP(player));
    }

    public boolean isFusion(IPlayer player) {
        return DBCSettingsUtil.isFusion(eP(player));
    }

    public void setFusion(IPlayer player, boolean enabled) {
        DBCSettingsUtil.setFusion(eP(player), enabled);
    }

    public boolean isPotentialUnleashed(IPlayer player) {
        return DBCSettingsUtil.isPotentialUnleashed(eP(player));
    }

    public void setPotentialUnleashed(IPlayer player, boolean enabled) {
        DBCSettingsUtil.setPotentialUnleashed(eP(player), enabled);
    }

    public boolean isKaioken(IPlayer player) {
        return DBCSettingsUtil.isKaioken(eP(player));
    }

    public void setKaioken(IPlayer player, boolean enabled) {
        DBCSettingsUtil.setKaioken(eP(player), enabled);
    }

    public boolean isUI(IPlayer player) {
        return DBCSettingsUtil.isUI(eP(player));
    }

    public void setUI(IPlayer player, boolean enabled) {
        DBCSettingsUtil.setUI(eP(player), enabled);
    }

    public boolean isGOD(IPlayer player) {
        return DBCSettingsUtil.isGOD(eP(player));
    }

    public void setGOD(IPlayer player, boolean enabled) {
        DBCSettingsUtil.setGOD(eP(player), enabled);
    }

    public boolean isFriendlyFist(IPlayer player) {
        return DBCSettingsUtil.isFriendlyFist(eP(player));
    }

    public void setFriendlyFist(IPlayer player, boolean enabled) {
        DBCSettingsUtil.setFriendlyFist(eP(player), enabled);
    }

    public boolean isSwoop(IPlayer player) {
        return DBCSettingsUtil.isSwoop(eP(player));
    }

    public void setSwoop(IPlayer player, boolean enabled) {
        DBCSettingsUtil.setSwoop(eP(player), enabled);
    }

    public boolean isKiProtection(IPlayer player) {
        return DBCSettingsUtil.isKiProtection(eP(player));
    }

    public void setKiProtection(IPlayer player, boolean enabled) {
        DBCSettingsUtil.setKiProtection(eP(player), enabled);
    }

    public boolean isKiFist(IPlayer player) {
        return DBCSettingsUtil.isKiFist(eP(player));
    }

    public void setKiFist(IPlayer player, boolean enabled) {
        DBCSettingsUtil.setKiFist(eP(player), enabled);
    }

    public boolean hasKiWeapon(IPlayer player) {
        return DBCSettingsUtil.hasKiWeapon(eP(player));
    }

    public int getKiWeapon(IPlayer player) {
        return DBCSettingsUtil.getKiWeapon(eP(player));
    }

    public void setKiWeapon(IPlayer player, boolean enabled) {
        DBCSettingsUtil.setKiWeapon(eP(player), enabled);
    }

    public void setKiWeapon(IPlayer player, boolean enabled, int mode) {
        DBCSettingsUtil.setKiWeapon(eP(player), enabled, mode);
    }

    public void setKiWeaponMode(IPlayer player, int mode) {
        DBCSettingsUtil.setKiWeaponMode(eP(player), mode);
    }

    public int getITRangeMode(IPlayer player) {
        return DBCSettingsUtil.getITRangeMode(eP(player));
    }

    public void setITRangeMode(IPlayer player, int mode) {
        DBCSettingsUtil.setITRangeMode(eP(player), mode);
    }

    public int getITSurroundMode(IPlayer player) {
        return DBCSettingsUtil.getITSurroundMode(eP(player));
    }

    public void setITSurroundMode(IPlayer player, int mode) {
        DBCSettingsUtil.setITSurroundMode(eP(player), mode);
    }
}
