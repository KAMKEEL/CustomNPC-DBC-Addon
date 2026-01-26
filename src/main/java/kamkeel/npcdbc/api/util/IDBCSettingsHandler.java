package kamkeel.npcdbc.api.util;

import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.entity.IPlayer;

public interface IDBCSettingsHandler {

    String getSettings(IPlayer player);

    boolean isFusion(IPlayer player);
    void setFusion(IPlayer player, boolean enabled);

    boolean isPotentialUnleashed(IPlayer player);
    void setPotentialUnleashed(IPlayer player, boolean enabled);

    boolean isKaioken(IPlayer player);
    void setKaioken(IPlayer player, boolean enabled);

    boolean isUI(IPlayer player);
    void setUI(IPlayer player, boolean enabled);

    boolean isGOD(IPlayer player);
    void setGOD(IPlayer player, boolean enabled);

    boolean isFriendlyFist(IPlayer player);
    void setFriendlyFist(IPlayer player, boolean enabled);

    boolean isSwoop(IPlayer player);
    void setSwoop(IPlayer player, boolean enabled);

    boolean isKiProtection(IPlayer player);
    void setKiProtection(IPlayer player, boolean enabled);

    boolean isKiFist(IPlayer player);
    void setKiFist(IPlayer player, boolean enabled);

    boolean hasKiWeapon(IPlayer player);
    int getKiWeapon(IPlayer player);
    void setKiWeapon(IPlayer player, boolean enabled);
    void setKiWeapon(IPlayer player, boolean enabled, int mode);
    void setKiWeaponMode(IPlayer player, int mode);

    int getITRangeMode(IPlayer player);
    void setITRangeMode(IPlayer player, int mode);

    int getITSurroundMode(IPlayer player);
    void setITSurroundMode(IPlayer player, int mode);
}
