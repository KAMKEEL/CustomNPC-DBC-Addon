package kamkeel.npcdbc.data;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcs.controllers.data.EnumProfileOperation;
import kamkeel.npcs.controllers.data.IProfileData;
import kamkeel.npcs.controllers.data.InfoEntry;
import kamkeel.npcs.controllers.data.ProfileOperation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

import static JinRyuu.JRMCore.JRMCoreH.*;
import static JinRyuu.JRMCore.JRMCoreH.PlyrAttrbts;
import static kamkeel.npcdbc.data.dbcdata.DBCData.DBCPersisted;

public class DBCProfileData implements IProfileData {

    @Override
    public String getTagName() {
        return "DBC";
    }

    @Override
    public NBTTagCompound getCurrentNBT(EntityPlayer player) {
        DBCData dbcData = DBCData.get(player);
        return (NBTTagCompound) dbcData.getRawCompound().copy();
    }

    @Override
    public void save(EntityPlayer player) {
        DBCData dbcData = DBCData.get(player);
        dbcData.loadNBTData(true);
        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);
        info.updateClient();
    }

    @Override
    public void setNBT(EntityPlayer player, NBTTagCompound replace) {
        DBCData.get(player);
        player.getEntityData().setTag(DBCPersisted, replace.copy());
    }

    @Override
    public int getSwitchPriority() {
        return 1;
    }

    @Override
    public ProfileOperation verifySwitch(EntityPlayer entityPlayer) {
        DBCData dbcData = DBCData.get(entityPlayer);
        if(dbcData.stats.isFused())
            return ProfileOperation.error("Player is fused");

        // If Player has Legendary
        if(ConfigDBCGameplay.ProfileSwitchingRemovesLegendary && StusEfcts(14, dbcData.StatusEffects)){
            dbcData.setSE(14, false);
        }
        return ProfileOperation.success("");
    }

    @Override
    public List<InfoEntry> getInfo(EntityPlayer entityPlayer, NBTTagCompound nbtTagCompound) {
        DBCData dbcData = new DBCData(entityPlayer);
        dbcData.loadFromNBT(nbtTagCompound);
        List<InfoEntry> info = new ArrayList<>();
        info.add(new InfoEntry("DBC", 0xFFFFFF, "", 0xFFFFFF));
        info.add(new InfoEntry("------------------", 0xFFFFFF, "", 0xFFFFFF));
        info.add(new InfoEntry("jinryuujrmcore.Level", 0x47acf5,
            getPlayerLevel(dbcData.STR + dbcData.DEX + dbcData.CON + dbcData.WIL + dbcData.MND + dbcData.SPI),
            0xFFFFFF));
        info.add(new InfoEntry("jinryuujrmcore.TP", 0x47acf5, dbcData.TP, 0xFFFFFF));
        info.add(new InfoEntry("jinryuujrmcore.Class", 0x60fa57, JRMCoreH.ClassesDBC[dbcData.Class], 0xFFFFFF));
        info.add(new InfoEntry("jinryuujrmcore.Race", 0xf75336, Races[dbcData.Race], 0xFFFFFF));
        info.add(new InfoEntry("------------------", 0xFFFFFF, "", 0xFFFFFF));
        info.add(new InfoEntry("jinryuujrmcore.Strength", 0x47acf5, dbcData.STR, 0xFFFFFF));
        info.add(new InfoEntry("jinryuujrmcore.Dexterity", 0x47acf5, dbcData.DEX, 0xFFFFFF));
        info.add(new InfoEntry("jinryuujrmcore.Constitution", 0x47acf5, dbcData.CON, 0xFFFFFF));
        info.add(new InfoEntry("jinryuujrmcore.WillPower", 0x47acf5, dbcData.WIL, 0xFFFFFF));
        info.add(new InfoEntry("jinryuujrmcore.Mind", 0x47acf5, dbcData.MND, 0xFFFFFF));
        info.add(new InfoEntry("jinryuujrmcore.Spirit", 0x47acf5, dbcData.SPI, 0xFFFFFF));
        return info;
    }
}
