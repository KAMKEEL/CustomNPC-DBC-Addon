package kamkeel.npcdbc.data.dbcdata;


import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreHDBC;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.controllers.*;
import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.DBCSetFlight;
import kamkeel.npcdbc.network.packets.PingPacket;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.scripted.CustomNPCsException;
import noppes.npcs.util.ValueUtil;

import java.util.HashMap;

public class DBCData extends DBCDataUniversal {

    public static String DBCPersisted = "PlayerPersisted";
    public final Side side;
    public EntityPlayer player;

    // Original DBC
    public int STR, DEX, CON, WIL, MND, SPI, TP, Body, Ki, Stamina, KOforXSeconds, Rage, Heat, Pain, AuraColor, ArcReserve;
    public byte Class, Race, Powertype, Accept, State, State2, Release, Alignment;
    public boolean Alive, isKO;
    public String Skills = "", RacialSkills = "", StatusEffects = "", Settings = "", FormMasteryRacial = "", FormMasteryNR = "", DNS = "", DNSHair = "", MajinAbsorptionData = "", Fusion = "";

    // Custom Form / Custom Aura
    public int addonFormID, auraID;
    public float addonFormLevel, addonCurrentHeat;
    public HashMap<Integer, PlayerEffect> currentEffects = new HashMap<>();
    public HashMap<String, PlayerBonus> currentBonuses = new HashMap<>();

    //NON VANILLA DBC
    public float baseFlightSpeed = 1.0f, dynamicFlightSpeed = 1.0f;
    public int flightSpeedRelease = 100;
    public boolean isFlying, flightEnabled = true, flightGravity = true;
    
    public EntityAura auraEntity;

    public DBCDataStats stats = new DBCDataStats(this);
    public DBCDataBonus bonus = new DBCDataBonus(this);

    public DBCData() {
        this.side = Side.SERVER;
    }

    public DBCData(EntityPlayer player) {
        this.player = player;
        this.side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;

        if (side == Side.SERVER)
            loadNBTData(true);
    }


    public NBTTagCompound saveFromNBT(NBTTagCompound comp) {
        comp.setInteger("jrmcStrI", STR);
        comp.setInteger("jrmcDexI", DEX);
        comp.setInteger("jrmcCnsI", CON);
        comp.setInteger("jrmcWilI", WIL);
        comp.setInteger("jrmcIntI", MND);
        comp.setInteger("jrmcCncI", SPI);
        comp.setInteger("jrmcEnrgy", Ki);
        comp.setInteger("jrmcStamina", Stamina);
        comp.setInteger("jrmcBdy", Body);
        comp.setInteger("jrmcHar4va", KOforXSeconds);
        comp.setInteger("jrmcSaiRg", Rage);
        comp.setInteger("jrmcEf8slc", Heat);
        comp.setInteger("jrmcGyJ7dp", Pain);
        comp.setInteger("jrmcAuraColor", AuraColor);
        comp.setInteger("jrmcArcRsrv", ArcReserve);


        comp.setByte("jrmcState", State);
        comp.setByte("jrmcState2", State2);
        comp.setByte("jrmcRelease", Release);
        comp.setByte("jrmcPwrtyp", Powertype);
        comp.setByte("jrmcRace", Race);
        comp.setByte("jrmcClass", Class);
        comp.setByte("jrmcAccept", Accept);
        comp.setByte("jrmcAlign", Alignment);

        comp.setString("jrmcStatusEff", StatusEffects);
        comp.setString("jrmcSSltX", RacialSkills);
        comp.setString("jrmcSSlts", Skills);
        comp.setString("jrmcSettings", Settings);
        comp.setString("jrmcFormMasteryRacial_" + JRMCoreH.Races[Race], FormMasteryRacial);
        comp.setString("jrmcFormMasteryNonRacial", FormMasteryNR);
        comp.setString("jrmcDNS", DNS);
        comp.setString("jrmcDNSH", DNSHair);
        comp.setString("jrmcMajinAbsorptionData", MajinAbsorptionData);
        comp.setString("jrmcFuzion", Fusion);
        // DBC Addon
        comp.setInteger("addonFormID", addonFormID);
        comp.setInteger("auraID", auraID);
        comp.setFloat("addonFormLevel", addonFormLevel);
        comp.setFloat("addonCurrentHeat", addonCurrentHeat);

        comp.setFloat("DBCBaseFlightSpeed", baseFlightSpeed);
        comp.setFloat("DBCDynamicFlightSpeed", dynamicFlightSpeed);
        comp.setInteger("DBCFlightSpeedRelease", flightSpeedRelease);
        comp.setBoolean("DBCisFlying", isFlying);
        comp.setBoolean("DBCFlightEnabled", flightEnabled);
        comp.setBoolean("DBCFlightGravity", flightGravity);
        stats.saveEffectsNBT(comp);
        bonus.saveBonusNBT(comp);
        return comp;
    }

    public void loadFromNBT(NBTTagCompound c) {
        STR = c.getInteger("jrmcStrI");
        DEX = c.getInteger("jrmcDexI");
        CON = c.getInteger("jrmcCnsI");
        WIL = c.getInteger("jrmcWilI");
        MND = c.getInteger("jrmcIntI");
        SPI = c.getInteger("jrmcCncI");
        Ki = c.getInteger("jrmcEnrgy");
        Stamina = c.getInteger("jrmcStamina");
        Body = c.getInteger("jrmcBdy");
        KOforXSeconds = c.getInteger("jrmcHar4va");
        Rage = c.getInteger("jrmcSaiRg");
        Heat = c.getInteger("jrmcEf8slc");
        Pain = c.getInteger("jrmcGyJ7dp");
        isKO = c.getInteger("jrmcHar4va") > 0;
        AuraColor = c.getInteger("jrmcAuraColor");
        ArcReserve = c.getInteger("jrmcArcRsrv");

        State = c.getByte("jrmcState");
        State2 = c.getByte("jrmcState2");
        Release = c.getByte("jrmcRelease");
        Powertype = c.getByte("jrmcPwrtyp");
        Race = c.getByte("jrmcRace");
        Class = c.getByte("jrmcClass");
        Accept = c.getByte("jrmcAccept");
        Alignment = c.getByte("jrmcAlign");

        StatusEffects = c.getString("jrmcStatusEff");
        RacialSkills = c.getString("jrmcSSltX");
        Skills = c.getString("jrmcSSlts");
        Settings = c.getString("jrmcSettings");
        FormMasteryRacial = c.getString("jrmcFormMasteryRacial_" + JRMCoreH.Races[Race]);
        FormMasteryNR = c.getString("jrmcFormMasteryNonRacial");
        DNS = c.getString("jrmcDNS");
        DNSHair = c.getString("jrmcDNSH");
        MajinAbsorptionData = c.getString("jrmcMajinAbsorptionData");
        Fusion = c.getString("jrmcFuzion");

        // DBC Addon
        addonFormID = c.getInteger("addonFormID");
        addonFormLevel = c.getFloat("addonFormLevel");
        addonCurrentHeat = c.getFloat("addonCurrentHeat");
        auraID = c.getInteger("auraID");

        if (!c.hasKey("DBCBaseFlightSpeed"))
            c.setFloat("DBCBaseFlightSpeed", baseFlightSpeed);
        baseFlightSpeed = c.getFloat("DBCBaseFlightSpeed");

        if (!c.hasKey("DBCBaseFlightSpeed"))
            c.setFloat("DBCDynamicFlightSpeed", dynamicFlightSpeed);
        dynamicFlightSpeed = c.getFloat("DBCDynamicFlightSpeed");

        if (!c.hasKey("DBCBaseFlightSpeed"))
            c.setInteger("DBCFlightSpeedRelease", flightSpeedRelease);
        flightSpeedRelease = c.getInteger("DBCFlightSpeedRelease");

        if (!c.hasKey("DBCBaseFlightSpeed"))
            c.setBoolean("DBCFlightEnabled", flightEnabled);
        flightEnabled = c.getBoolean("DBCFlightEnabled");

        if (!c.hasKey("DBCBaseFlightSpeed"))
            c.setBoolean("DBCFlightGravity", flightGravity);
        flightGravity = c.getBoolean("DBCFlightGravity");


        this.currentEffects.clear();
        if (c.hasKey("addonActiveEffects", 9)) {
            NBTTagList nbttaglist = c.getTagList("addonActiveEffects", 10);
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                PlayerEffect playerEffect = PlayerEffect.readEffectData(nbttagcompound1);
                if (playerEffect != null) {
                    this.currentEffects.put(playerEffect.id, playerEffect);
                }
            }
        }

        this.currentBonuses.clear();
        if (c.hasKey("addonBonus", 9)) {
            NBTTagList nbttaglist = c.getTagList("addonBonus", 10);
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                PlayerBonus bonus = PlayerBonus.readBonusData(nbttagcompound1);
                this.currentBonuses.put(bonus.name, bonus);
            }
        }
    }

    public void saveNBTData(boolean syncTracking) {
        NBTTagCompound nbt = this.saveFromNBT(this.player.getEntityData().getCompoundTag(DBCPersisted));

        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(player);
        addonFormID = formData.currentForm;
        addonFormLevel = formData.getCurrentLevel();
        auraID = formData.currentAura;
        stats.setCurrentEffects(StatusEffectController.Instance.playerEffects.get(Utility.getUUID(player)));
        bonus.setCurrentBonuses(BonusController.Instance.playerBonus.get(Utility.getUUID(player)));
        nbt.setInteger("addonFormID", addonFormID);
        nbt.setFloat("addonFormLevel", addonFormLevel);
        nbt.setInteger("auraID", auraID);
        stats.saveEffectsNBT(nbt);
        bonus.saveBonusNBT(nbt);
        this.player.getEntityData().setTag(DBCPersisted, nbt);

        // Send to Tracking Only
        if (syncTracking)
            syncTracking();
    }

    public void loadNBTData(boolean syncALL) {
        NBTTagCompound dbc = this.player.getEntityData().getCompoundTag(DBCPersisted);

        // Save the DBC Addon tags to PlayerPersisted before loading it to fields
        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(player);
        dbc.setInteger("addonFormID", formData.currentForm);
        dbc.setInteger("auraID", formData.currentAura);
        dbc.setFloat("addonFormLevel", formData.getCurrentLevel());
        stats.saveEffectsNBT(dbc);
        bonus.saveBonusNBT(dbc);
        //dbc.setFloat("DBCFlightSpeedRelease", 100f);
        // dbc.setBoolean("DBCFlightGravity", false);
        loadFromNBT(dbc);
        if (syncALL)
            syncTracking();
    }

    public void syncTracking() {
        PacketHandler.Instance.sendToTrackingPlayers(player, new PingPacket(this).generatePacket());
    }

    public NBTTagCompound getRawCompound() {
        return this.player.getEntityData().getCompoundTag(DBCPersisted);
    }


    public boolean isForm(int dbcForm) {
        switch (dbcForm) {
            case DBCForm.Base:
                return State == 0 && !isForm(DBCForm.Kaioken) && !isForm(DBCForm.UltraInstinct) && !isForm(DBCForm.GodOfDestruction) && !isForm(DBCForm.Mystic);
            case DBCForm.Kaioken:
                return State2 > 0 && JRMCoreH.StusEfcts(5, StatusEffects);
            case DBCForm.UltraInstinct:
                return State2 > 0 && JRMCoreH.StusEfcts(19, StatusEffects);
            case DBCForm.GodOfDestruction:
                return JRMCoreH.StusEfcts(20, StatusEffects);
            case DBCForm.Mystic:
                return JRMCoreH.StusEfcts(13, StatusEffects);
            //the following doesn't count as "forms" but they can be checked from this method as well
            case DBCForm.Legendary:
                return JRMCoreH.StusEfcts(14, StatusEffects);
            case DBCForm.Divine:
                return JRMCoreH.StusEfcts(17, StatusEffects);
            case DBCForm.Majin:
                return JRMCoreH.StusEfcts(12, StatusEffects);
            default:
                return false;
        }
    }


    public boolean containsSE(int id) {
        return JRMCoreH.StusEfcts(id, StatusEffects);
    }

    public void setSE(int id, boolean bo) {
        JRMCoreH.StusEfcts(id, StatusEffects, player, bo);
    }

    public void setForm(int dbcForm, boolean on) {
        switch (dbcForm) {
            case DBCForm.Kaioken:
                setSE(5, on);
                if (on)
                    State2 = 1;
                break;
            case DBCForm.UltraInstinct:
                setSE(19, on);
                if (on)
                    State2 = 1;
                break;
            case DBCForm.GodOfDestruction:
                setSE(20, on);
                break;
            case DBCForm.Mystic:
                setSE(13, on);
                break;
            case DBCForm.Legendary:
                setSE(14, on);
                break;
            case DBCForm.Divine:
                setSE(17, on);
                break;
            case DBCForm.Majin:
                setSE(12, on);
                break;
        }
    }

    public boolean settingOn(int id) {
        return Utility.isServer(player) ? JRMCoreH.PlyrSettingsB(player, id) : JRMCoreH.PlyrSettingsB(id);
    }

    public boolean formSettingOn(int dbcForm) {
        switch (dbcForm) {
            case DBCForm.Kaioken:
                return settingOn(0);
            case DBCForm.UltraInstinct:
                return settingOn(11);
            case DBCForm.GodOfDestruction:
                return settingOn(16);
            case DBCForm.Mystic:
                return settingOn(6);
            default:
                return false;
        }
    }

    public boolean isDBCAuraOn() {
        return containsSE(1) || containsSE(3) || containsSE(4) || containsSE(5) || containsSE(7);
    }

    public boolean isTransforming() {
        if (!Utility.isServer(player) && TransformController.ascending)
            return true;

        return containsSE(1);
    }

    public int getDBCColor() {
        return JRMCoreHDBC.getPlayerColor2(2, JRMCoreH.Algnmnt_rc(Alignment), 1, Race, State, isForm(DBCForm.Divine), isForm(DBCForm.Legendary), isForm(DBCForm.UltraInstinct), isForm(DBCForm.GodOfDestruction));

    }

    public Aura getToggledAura() {
        if (!isDBCAuraOn() && !isTransforming())
            return null;
        return getAura();
    }

    public Aura getAura() {
        Form form = getForm();
        Aura aura = null;


        if (form != null && form.display.hasAura())
            aura = form.display.getAur();
        else if (aura == null)
            aura = (Aura) AuraController.Instance.get(auraID);

        if (aura != null) {
            if (!aura.display.overrideDBCAura && (!isForm(DBCForm.Base) && !(isForm(DBCForm.Kaioken) && aura.display.hasKaiokenAura && State == 0)))
                return null;
            else
                return aura;


        }
        return null;
    }

    public Form getForm() {
        return (Form) FormController.getInstance().get(addonFormID);
    }

    public void setFlight(boolean flightOn) {
        PacketHandler.Instance.sendToPlayer(new DBCSetFlight(flightOn).generatePacket(), (EntityPlayerMP) player);
    }

    /**
     * Fuse with another player
     *
     * @param spectator player that is supposed to be the spectator
     * @param time      time in minutes
     */
    public void fuseWith(DBCData spectator, float time) {
        if (spectator == null || this.player == null)
            return;

        if (spectator == this)
            throw new CustomNPCsException("Tried to fuse the player with themselves");

        time *= 12; //Gets DBC time

        //EntityPlayer specPlayer = spectator.player;

        String controllerName = this.player.getCommandSenderName();
        String spectatorName = spectator.player.getCommandSenderName();

        NBTTagCompound controllerTag = this.getRawCompound();

        NBTTagCompound spectatorTag = spectator.getRawCompound();

        String fusionString = String.format("%s,%s,%d", controllerName, spectatorName, ((int) time));

        controllerTag.setString("jrmcFuzion", fusionString);
        spectatorTag.setString("jrmcFuzion", fusionString);

        this.setSE(10, true);
        spectator.setSE(11, true);

        JRMCoreH.PlyrSettingsRem(this.player, 4);
        JRMCoreH.PlyrSettingsRem(spectator.player, 4);

        controllerTag.setByte("jrmcState2", (byte) 0);
        spectatorTag.setByte("jrmcState2", (byte) 0);

        spectator.setSE(3, false);
        spectator.setSE(4, false);
        spectator.setSE(5, false);

        this.stats.restoreHealthPercent(100);
        spectator.stats.restoreHealthPercent(100);
        this.stats.restoreKiPercent(100);
        spectator.stats.restoreKiPercent(100);
    }

    public float addFusionMastery(DBCData controller, DBCData spectator) {
        float mastery = 0f;

        Form form = controller.getForm();
        if (form != null) {
            mastery = controller.getDBCInfo().getFormLevel(form.id);
            if (spectator.getDBCInfo().hasFormUnlocked(form.id))
                mastery = ValueUtil.clamp(mastery + spectator.getDBCInfo().getFormLevel(form.id), 0, form.mastery.getMaxLevel());
        }
        return mastery;
    }

    public PlayerDBCInfo getDBCInfo() {
        return PlayerDataUtil.getDBCInfo(player);
    }
}
