package kamkeel.npcdbc.data.dbcdata;

import JinRyuu.DragonBC.common.DBCKiTech;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreHDBC;
import JinRyuu.JRMCore.entity.EntityCusPar;
import JinRyuu.JRMCore.server.config.dbc.JGConfigUltraInstinct;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.api.outline.IOutline;
import kamkeel.npcdbc.client.utils.SimplifiedDBCData;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.DBCSettings;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.IAuraData;
import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.form.FacePartData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.data.overlay.OverlayChain;
import kamkeel.npcdbc.data.overlay.OverlayManager;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.*;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import noppes.npcs.controllers.CustomEffectController;
import noppes.npcs.controllers.data.EffectKey;
import noppes.npcs.controllers.data.PlayerEffect;
import noppes.npcs.scripted.CustomNPCsException;
import noppes.npcs.util.ValueUtil;

import java.util.*;

import static kamkeel.npcdbc.constants.DBCForm.*;
import static kamkeel.npcdbc.controllers.DBCEffectController.DBC_EFFECT_INDEX;

public class    DBCData extends DBCDataUniversal implements IAuraData {

    public static String DBCPersisted = "PlayerPersisted";
    public final Side side;
    public EntityPlayer player;

    // Original DBC
    public int STR, DEX, CON, WIL, MND, SPI, TP, Body, Ki, Stamina, KOforXSeconds, Rage, Heat, Pain, AuraColor, ArcReserve, hasSSJ4;
    public byte Class, Race, Powertype, Accept, State, State2, Release, Alignment, Tail;
    public boolean Alive, isKO;
    public String Skills = "", RacialSkills = "", StatusEffects = "", Settings = "", FormMasteryRacial = "", FormMasteryNR = "", DNS = "", DNSHair = "", MajinAbsorptionData = "", Fusion = "";

    // Custom Form / Custom Aura
    public int addonFormID = -1, auraID = -1, outlineID = -1;
    public float addonFormLevel = 0, addonCurrentHeat = 0;

    /**
     * Client-side bonus store. Needed for proper Battle Power calculations on the client.
     * <p>
     * Server-side stores them in BonusController
     */
    @SideOnly(Side.CLIENT)
    public Map<String, PlayerBonus> currentBonuses;

    @SideOnly(Side.CLIENT)
    public byte potaraFusionLevel;

    @SideOnly(Side.CLIENT)
    public FormDisplay.BodyColor currentCustomizedColors;

    // NON VANILLA DBC
    public float baseFlightSpeed = 1.0f, dynamicFlightSpeed = 1.0f, sprintSpeed = 1.0f;
    public int flightSpeedRelease = 100;
    public boolean isFlying, flightEnabled = true, flightGravity = true;
    public boolean isFnPressed;

    public DBCDataStats stats = new DBCDataStats(this);
    public DBCDataBonus bonus = new DBCDataBonus(this);

    /**
     * ALL overlay chains that were drawn this tick, forms and everything
     */
    public List<OverlayChain> cachedOverlays = new ArrayList<>();

    //RENDERING DATA
    public float XZSize, YSize, age;
    public int renderingHairColor;
    public byte skinType;
    public boolean useStencilBuffer;
    public boolean renderGoD, renderKK, renderUI;
    public EntityAura auraEntity;
    public int activeAuraColor = -1;
    public List<EntityCusPar> particleRenderQueue = new LinkedList<>();
    // Some servers tend to repeat one tick multiple times (up to 3-4 times in under a second)
    public int lastTicked = -1;

    public final SimplifiedDBCData simplifiedDBCData = new SimplifiedDBCData(this);

    public DBCData() {
        this.side = Side.SERVER;
    }

    public DBCData(EntityPlayer player) {
        this.player = player;
        this.side = player.worldObj.isRemote ? Side.CLIENT : Side.SERVER;

        if (side == Side.SERVER)
            loadNBTData(true);
        else
            potaraFusionLevel = -1;
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
        comp.setInteger("jrmcAfGFtStFT", hasSSJ4);
        comp.setInteger("jrmcTpint", TP);

        comp.setByte("jrmcState", State);
        comp.setByte("jrmcState2", State2);
        comp.setByte("jrmcRelease", Release);
        comp.setByte("jrmcPwrtyp", Powertype);
        comp.setByte("jrmcRace", Race);
        comp.setByte("jrmcClass", Class);
        comp.setByte("jrmcAccept", Accept);
        comp.setByte("jrmcAlign", Alignment);
        comp.setByte("jrmcTlmd", Tail);

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
        comp.setInteger("outlineID", outlineID);

        comp.setFloat("addonFormLevel", addonFormLevel);
        comp.setFloat("addonCurrentHeat", addonCurrentHeat);

        comp.setFloat("DBCBaseFlightSpeed", baseFlightSpeed);
        comp.setFloat("DBCDynamicFlightSpeed", dynamicFlightSpeed);
        comp.setFloat("DBCSprintSpeed", sprintSpeed);
        comp.setInteger("DBCFlightSpeedRelease", flightSpeedRelease);
        comp.setBoolean("DBCisFlying", isFlying);
        comp.setBoolean("DBCFlightEnabled", flightEnabled);
        comp.setBoolean("DBCFlightGravity", flightGravity);

        comp.setBoolean("DBCIsFnPressed", isFnPressed);

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
        hasSSJ4 = c.getInteger("jrmcAfGFtStFT");
        AuraColor = c.getInteger("jrmcAuraColor");
        ArcReserve = c.getInteger("jrmcArcRsrv");
        TP = c.getInteger("jrmcTpint");

        State = c.getByte("jrmcState");
        State2 = c.getByte("jrmcState2");
        Release = c.getByte("jrmcRelease");
        Powertype = c.getByte("jrmcPwrtyp");
        Race = c.getByte("jrmcRace");
        Class = c.getByte("jrmcClass");
        Accept = c.getByte("jrmcAccept");
        Alignment = c.getByte("jrmcAlign");
        Tail = c.getByte("jrmcTlmd");

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

        isFlying = c.getBoolean("DBCisFlying");

        // DBC Addon
        if (!c.hasKey("addonFormID"))
            c.setInteger("addonFormID", addonFormID);
        addonFormID = c.getInteger("addonFormID");

        addonFormLevel = c.getFloat("addonFormLevel");
        addonCurrentHeat = c.getFloat("addonCurrentHeat");

        if (!c.hasKey("auraID"))
            c.setInteger("auraID", auraID);
        auraID = c.getInteger("auraID");

        if (!c.hasKey("outlineID"))
            c.setInteger("outlineID", outlineID);
        outlineID = c.getInteger("outlineID");

        if (!c.hasKey("DBCBaseFlightSpeed"))
            c.setFloat("DBCBaseFlightSpeed", baseFlightSpeed);
        baseFlightSpeed = c.getFloat("DBCBaseFlightSpeed");

        if (!c.hasKey("DBCDynamicFlightSpeed"))
            c.setFloat("DBCDynamicFlightSpeed", dynamicFlightSpeed);
        dynamicFlightSpeed = c.getFloat("DBCDynamicFlightSpeed");

        if (!c.hasKey("DBCSprintSpeed"))
            c.setFloat("DBCSprintSpeed", sprintSpeed);
        sprintSpeed = c.getFloat("DBCSprintSpeed");

        if (!c.hasKey("DBCFlightSpeedRelease"))
            c.setInteger("DBCFlightSpeedRelease", flightSpeedRelease);
        flightSpeedRelease = c.getInteger("DBCFlightSpeedRelease");

        if (!c.hasKey("DBCFlightEnabled"))
            c.setBoolean("DBCFlightEnabled", flightEnabled);
        flightEnabled = c.getBoolean("DBCFlightEnabled");

        if (!c.hasKey("DBCFlightGravity"))
            c.setBoolean("DBCFlightGravity", flightGravity);
        flightGravity = c.getBoolean("DBCFlightGravity");

        if (!c.hasKey("DBCIsFnPressed"))
            c.setBoolean("DBCIsFnPressed", isFnPressed);
        isFnPressed = c.getBoolean("DBCIsFnPressed");
    }

    public List<OverlayChain> getOverlayChains() {
        List<OverlayChain> chains = new ArrayList<>();

        OverlayManager overlays = getDBCInfo().overlayManager;
        if (overlays.enabled)
            chains.addAll(overlays.getChains());

        return chains;
    }

    @SideOnly(Side.CLIENT)
    public void loadClientSideData(NBTTagCompound c) {
        this.potaraFusionLevel = c.getByte("potaraLevel");

        if (this.currentBonuses == null)
            this.currentBonuses = new HashMap<>();
        else
            this.currentBonuses.clear();

        if (c.hasKey("addonBonus", 9)) {
            NBTTagList nbttaglist = c.getTagList("addonBonus", 10);
            for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                PlayerBonus bonus = PlayerBonus.readBonusData(nbttagcompound1);
                this.currentBonuses.put(bonus.name, bonus);
            }
        }

        loadClientSideFormColorData(c);
    }

    @SideOnly(Side.CLIENT)
    public void loadClientSideFormColorData(NBTTagCompound c) {
        currentCustomizedColors = new FormDisplay.BodyColor();
        if (c.hasKey("CustomFormColors", Constants.NBT.TAG_COMPOUND)) {
            currentCustomizedColors.readFromNBT(c.getCompoundTag("CustomFormColors"));
        }
    }

    public void saveNBTData(boolean syncTracking) {
        NBTTagCompound nbt = this.saveFromNBT(this.player.getEntityData().getCompoundTag(DBCPersisted));

        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(player);
        addonFormID = formData.currentForm;
        addonFormLevel = formData.getCurrentLevel();
        auraID = formData.currentAura;
        nbt.setInteger("addonFormID", addonFormID);
        nbt.setFloat("addonFormLevel", addonFormLevel);
        nbt.setInteger("auraID", auraID);
        nbt.setInteger("outlineID", outlineID);

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
        loadFromNBT(dbc);
        if (syncALL)
            syncTracking();
    }

    // Get all Necessary Charging Information
    public void loadCharging() {
        NBTTagCompound dbc = this.player.getEntityData().getCompoundTag(DBCPersisted);
        Skills = dbc.getString("jrmcSSlts");
        Release = dbc.getByte("jrmcRelease");
        isFnPressed = dbc.getBoolean("DBCIsFnPressed");
    }

    public void syncTracking() {
        NBTTagCompound dataNeededOnClient = new NBTTagCompound();

        bonus.saveBonusNBT(dataNeededOnClient);
        PlayerEffect potara = CustomEffectController.getInstance().getPlayerEffects(this.player).get(new EffectKey(Effects.POTARA, DBC_EFFECT_INDEX));
        if (potara != null)
            dataNeededOnClient.setByte("potaraLevel", potara.level);
        else
            dataNeededOnClient.setByte("potaraLevel", (byte) -1);

        FormDisplay.BodyColor currentColors = getCurrentFormColorCustomization();
        if (currentColors != null) {
            NBTTagCompound colorCompound = new NBTTagCompound();
            currentColors.writeToNBT(colorCompound);
            dataNeededOnClient.setTag("CustomFormColors", colorCompound);
        }

        DBCPacketHandler.Instance.sendTracking(new PingPacket(this, dataNeededOnClient), player);

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
                return State2 > 0 && JRMCoreH.StusEfcts(19, StatusEffects) && !JRMCoreH.StusEfcts(5, StatusEffects);
            case DBCForm.MasteredUltraInstinct:
                return isForm(DBCForm.UltraInstinct) && JGConfigUltraInstinct.CONFIG_UI_LEVELS >= State2 ? JGConfigUltraInstinct.CONFIG_UI_HAIR_WHITE[State2 - 1] : false;
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

    public boolean hasForm(int dbcForm) {
        if (dbcForm == DBCForm.Kaioken)
            return JRMCoreH.SklLvl(8) > 0;
        if (dbcForm == DBCForm.UltraInstinct)
            if (dbcForm == DBCForm.MasteredUltraInstinct)
                return JRMCoreH.SklLvl(16) > 0;
        if (dbcForm == DBCForm.GodOfDestruction)
            return JRMCoreH.SklLvl(18) > 0;
        if (dbcForm == DBCForm.Mystic)
            return JRMCoreH.SklLvl(10) > 0;

        int racialSkill = JRMCoreH.SklLvlX(Powertype, RacialSkills) - 1;
        int godSkill = JRMCoreH.SklLvl(9);
        switch (Race) {
            case 0:
                switch (dbcForm) {
                    case HumanFullRelease:
                        return racialSkill >= 1;
                    case HumanBuffed:
                        return racialSkill >= 2;
                    case HumanGod:
                        return racialSkill >= 2 && godSkill >= 1;
                }
                return false;

            case 1:
            case 2:
                switch (dbcForm) {
                    case SuperSaiyan:
                        return racialSkill >= 1 && racialSkill < 4;
                    case SuperSaiyanG2:
                        return racialSkill >= 2;
                    case SuperSaiyanG3:
                        return racialSkill >= 3;
                    case MasteredSuperSaiyan:
                        return racialSkill >= 4;
                    case SuperSaiyan2:
                        return racialSkill >= 5;
                    case SuperSaiyan3:
                        return racialSkill >= 6;
                    case SuperSaiyan4:
                        return racialSkill >= 7 && hasSSJ4 > 0 && hasTail();

                    case SuperSaiyanGod:
                        return racialSkill >= 5 && godSkill >= 1;
                    case SuperSaiyanBlue:
                        return racialSkill >= 5 && godSkill >= 2;
                    case BlueEvo:
                        return racialSkill >= 5 && godSkill >= 3;
                    default:
                        return false;
                }
            case 3:
                switch (dbcForm) {
                    case NamekFullRelease:
                        return racialSkill >= 1;
                    case NamekGiant:
                        return racialSkill >= 2;
                    case NamekGod:
                        return racialSkill >= 2 && godSkill >= 1;
                }
                return false;

            case 4:
                switch (dbcForm) {
                    case Minimal:
                    case FirstForm:
                    case SecondForm:
                    case ThirdForm:
                    case FinalForm:
                        return true;
                    case SuperForm:
                        return racialSkill >= 3;
                    case UltimateForm:
                        return racialSkill >= 6;
                    case ArcoGod:
                        return racialSkill >= 6 && godSkill >= 1;
                }
                return false;

            case 5:
                switch (dbcForm) {
                    case MajinEvil:
                        return racialSkill >= 2;
                    case MajinFullPower:
                        return racialSkill >= 3;
                    case MajinPure:
                        return racialSkill >= 5;
                    case MajinGod:
                        return racialSkill >= 5 && godSkill >= 1;
                }
                return false;

            default:
                return false;
        }
    }

    public boolean isDBCFormUnlocked(int formID) {
        return getUnlockedDBCFormsMap().containsKey(formID);
    }

    public HashMap<Integer, String> getUnlockedDBCFormsMap() {
        HashMap<Integer, String> dbcForms = new LinkedHashMap<>();
        int race = Race;
        int racialSkill = JRMCoreH.SklLvlX(1, RacialSkills) - 1;
        int godSkill = JRMCoreH.SklLvl(9);

        if (race == DBCRace.HUMAN) {
            if (racialSkill >= 1)
                dbcForms.put(HumanBuffed, "§3Buffed");
            if (racialSkill >= 2)
                dbcForms.put(HumanFullRelease, "§4Full Release");

            if (racialSkill >= 2 && godSkill >= 1)
                dbcForms.put(HumanGod, "§cGod");
        } else if (race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN) {
            if (racialSkill >= 1 && racialSkill < 4)
                dbcForms.put(SuperSaiyan, "§eSuper Saiyan");
            if (racialSkill >= 4)
                dbcForms.put(MasteredSuperSaiyan, "§eSuper Saiyan (FP)");
            if (racialSkill >= 2)
                dbcForms.put(SuperSaiyanG2, "§eSuper Saiyan G2");
            if (racialSkill >= 3)
                dbcForms.put(SuperSaiyanG3, "§eSuper Saiyan G3");
            if (racialSkill >= 5)
                dbcForms.put(SuperSaiyan2, "§eSuper Saiyan 2");
            if (racialSkill >= 6)
                dbcForms.put(SuperSaiyan3, "§eSuper Saiyan 3");
            if (racialSkill >= 7 && hasSSJ4 > 0 && hasTail())
                dbcForms.put(SuperSaiyan4, "§4Super Saiyan 4");
            if (racialSkill >= 5 && godSkill >= 1)
                dbcForms.put(SuperSaiyanGod, "§cSuper Saiyan God");
            if (racialSkill >= 5 && godSkill >= 2)
                dbcForms.put(SuperSaiyanBlue, !isForm(Divine) ? "§bSuper Saiyan Blue" : "§5Super Saiyan Rosé");
            if (racialSkill >= 5 && godSkill >= 3)
                dbcForms.put(BlueEvo, !isForm(Divine) ? "§1Super Saiyan Blue Evo" : "§dSuper Saiyan Rosé Evo");
        } else if (race == DBCRace.NAMEKIAN) {
            if (racialSkill >= 1)
                dbcForms.put(NamekGiant, "§2Giant");
            if (racialSkill >= 2)
                dbcForms.put(NamekFullRelease, "§aFull Release");
            if (racialSkill >= 2 && godSkill >= 1)
                dbcForms.put(NamekGod, "§cGod");
        } else if (race == DBCRace.ARCOSIAN) {
            dbcForms.put(FirstForm, "§5First Form");
            dbcForms.put(SecondForm, "§5Second Form");
            dbcForms.put(ThirdForm, "§5Third Form");
            dbcForms.put(FinalForm, "§5Final Form");
            if (racialSkill >= 3)
                dbcForms.put(SuperForm, "§5Super Form");
            if (racialSkill >= 6)
                dbcForms.put(UltimateForm, "§6Ultimate Form");
            if (racialSkill >= 6 && godSkill >= 1)
                dbcForms.put(ArcoGod, "§cGod");
        } else if (race == DBCRace.MAJIN) {
            if (racialSkill >= 2)
                dbcForms.put(MajinEvil, "§8Evil");
            if (racialSkill >= 3)
                dbcForms.put(MajinFullPower, "§5Full Power");
            if (racialSkill >= 5)
                dbcForms.put(MajinPure, "§dPure");
            if (racialSkill >= 5 && godSkill >= 1)
                dbcForms.put(MajinGod, "§cGod");
        }
        if (JRMCoreH.SklLvl(10) > 0)
            dbcForms.put(Mystic, "Mystic");

        int kaiokenSkill = JRMCoreH.SklLvl(8);
        for (int i = 0; i < 6; i++) {
            if (kaiokenSkill >= i + 1)
                dbcForms.put(Kaioken + i, "§cKaioken " + JRMCoreH.TransKaiNms[i + 1]);
        }

        int uiSkill = JRMCoreH.SklLvl(16);
        for (int i = 0; i < JGConfigUltraInstinct.CONFIG_UI_LEVELS; i++) {
            if (uiSkill >= i + 1 && !JGConfigUltraInstinct.CONFIG_UI_SKIP[i])
                dbcForms.put(UltraInstinct + i, "§7" + DBCUtils.CONFIG_UI_NAME[i]);
        }

        if (JRMCoreH.SklLvl(18) > 0)
            dbcForms.put(GodOfDestruction, "§dGod of Destruction");

        return dbcForms;
    }

    public boolean containsSE(int id) {
        return JRMCoreH.StusEfcts(id, StatusEffects);
    }

    public String setSE(int id, boolean bo) {
        return JRMCoreH.StusEfcts(id, StatusEffects, getRawCompound(), bo);
    }

    public String setForm(int dbcForm, boolean on) {

        switch (dbcForm) {
            case DBCForm.Kaioken:
                StatusEffects = setSE(5, on);
                State2 = (byte) (on ? 1 : 0);
                getRawCompound().setByte("jrmcState2", State2);
                break;
            case DBCForm.UltraInstinct:
                StatusEffects = setSE(19, on);
                State2 = (byte) (on ? 1 : 0);
                getRawCompound().setByte("jrmcState2", State2);
                break;
            case DBCForm.GodOfDestruction:
                StatusEffects = setSE(20, on);
                break;
            case DBCForm.Mystic:
                StatusEffects = setSE(13, on);
                break;
            case DBCForm.Legendary:
                StatusEffects = setSE(14, on);
                break;
            case DBCForm.Divine:
                StatusEffects = setSE(17, on);
                break;
            case DBCForm.Majin:
                StatusEffects = setSE(12, on);
                break;
        }
        return StatusEffects;
    }

    public boolean settingOn(int id) {
        return JRMCoreH.PlyrSettingsB(getRawCompound(), id);
    }

    public boolean settingIsValue(int id, int value) {
        return JRMCoreH.PlyrSettingsI(getRawCompound(), id, value);
    }

    public void setSetting(int id, int value) {
        JRMCoreH.PlyrSettingsSet(getRawCompound(), id, value);
    }

    public boolean formSettingOn(int dbcForm) {
        switch (dbcForm) {
            case SuperSaiyanG2:
                return settingIsValue(1, -1);
            case SuperSaiyan2:
                return settingIsValue(1, 0);
            case SuperSaiyanGod:
                return settingIsValue(1, 1);
            case SuperSaiyanBlue:
                return settingIsValue(1, 2);
            case SuperSaiyan4:
                return settingIsValue(1, 3);
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

    public boolean isAuraOn() {
        return isTransforming() || containsSE(3) || containsSE(4) || containsSE(5) || containsSE(7);
    }

    @Override
    public boolean isFusionSpectator() {
        return stats.isFusionSpectator();
    }

    @Override
    public void useStencilBuffer(boolean use) {
        this.useStencilBuffer = use;
    }

    public boolean isTransforming() {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient() && player == Minecraft.getMinecraft().thePlayer && TransformController.ascending)
            return true;

        return containsSE(1);
    }

    public boolean isChargingKi() {
        return containsSE(4);
    }

    public void setTurboState(boolean on) {
        StatusEffects = setSE(3, on);

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            DBCPacketHandler.Instance.sendToPlayer(new TurboPacket(on), (EntityPlayerMP) player);
        } else {
            DBCKiTech.turbo = on;
        }
    }

    @Override
    public boolean isInKaioken() {
        return isForm(DBCForm.Kaioken);
    }

    public int getDBCColor() {
        return JRMCoreHDBC.getPlayerColor2(2, JRMCoreH.Algnmnt_rc(Alignment), 1, Race, State, isForm(DBCForm.Divine), isForm(DBCForm.Legendary), isForm(DBCForm.UltraInstinct), isForm(DBCForm.GodOfDestruction));
    }

    public Aura getToggledAura() {
        if (!isAuraOn() && !isTransforming())
            return null;
        return getAura();
    }

    public boolean hasTail() {
        if (DBCRace.isSaiyan(Race))
            return Tail == -1 || Tail == 0 || Tail == 1;
        else if (Race == DBCRace.ARCOSIAN)
            return true;

        return false;
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
        Form form = (Form) FormController.getInstance().get(addonFormID);
        if (form != null) {
            Form fusionForm = (Form) FormController.getInstance().get(form.stackable.fusionID);
            if (fusionForm != null && stats.isFused())
                form = fusionForm;

            if (form.stackable.divineID != -1 && isForm(DBCForm.Divine)) {
                Form divine = (Form) FormController.getInstance().get(form.stackable.divineID);
                if (divine != null)
                    return divine;
            }
            if (form.stackable.legendaryID != -1 && isForm(DBCForm.Legendary)) {
                Form legendary = (Form) FormController.getInstance().get(form.stackable.legendaryID);
                if (legendary != null)
                    return legendary;
            }
            if (form.stackable.majinID != -1 && isForm(DBCForm.Majin)) {
                Form majin = (Form) FormController.getInstance().get(form.stackable.majinID);
                if (majin != null)
                    return majin;
            }
        }
        return form;
    }

    /**
     * Order of getting outline from player:
     * <p>
     * >Currently toggled aura
     * >Currently selected form outline / selected form aura's outline
     * >Currently manually selected aura
     * >Force-set outline
     *
     * @return The outline object a player is using
     */
    public Outline getOutline() {
        OutlineController OC = OutlineController.getInstance();

        Aura aura = getToggledAura();

        if (aura != null && OC.has(aura.display.outlineID))
            return (Outline) OC.get(aura.display.outlineID);

        Form form = getForm();
        if (form != null && OC.has(form.display.outlineID))
            return (Outline) OC.get(form.display.outlineID);

        IAura formAura = form != null ? form.display.getAura() : null;

        if (formAura != null) {
            aura = (Aura) formAura;
            if (aura.display.outlineAlwaysOn && OC.has(aura.display.outlineID)) {
                return (Outline) OC.get(aura.display.outlineID);
            }
        }

        aura = (Aura) AuraController.Instance.get(auraID);
        if (aura != null && aura.display.outlineAlwaysOn && OC.has(aura.display.outlineID)) {
            return (Outline) OC.get(aura.display.outlineID);
        }

        return (Outline) OC.get(outlineID);
    }

    public void setOutline(IOutline outline) {
        int id = outline != null ? outline.getID() : -1;
        setOutline(id);
    }

    public void setOutline(int outlineID) {
        getRawCompound().setInteger("outlineID", outlineID);
    }

    public void setFlight(boolean flightOn) {
        DBCPacketHandler.Instance.sendToPlayer(new DBCSetFlight(flightOn), (EntityPlayerMP) player);
    }

    public float getBaseFlightSpeed() {
        float formSpeed = 1;
        Form form = getForm();
        if (form != null) {
            formSpeed = form.mastery.movementSpeed * form.mastery.calculateMulti("movementspeed", addonFormLevel);
        }
        return baseFlightSpeed * formSpeed;
    }

    public float getDynamicFlightSpeed() {
        float formSpeed = 1;
        Form form = getForm();
        if (form != null) {
            formSpeed = form.mastery.movementSpeed * form.mastery.calculateMulti("movementspeed", addonFormLevel);
        }
        return dynamicFlightSpeed * formSpeed;
    }

    public float getSprintSpeed() {
        float formSpeed = 1;
        Form form = getForm();
        if (form != null) {
            formSpeed = form.mastery.movementSpeed * form.mastery.calculateMulti("movementspeed", addonFormLevel);
        }
        return sprintSpeed * formSpeed;
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

        if (spectator.player == this.player)
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

        JRMCoreH.PlyrSettingsRem(this.player, DBCSettings.FUSION_ENABLED);
        JRMCoreH.PlyrSettingsRem(spectator.player, DBCSettings.FUSION_ENABLED);

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

    public float getFusionMastery(DBCData controller, DBCData spectator) {
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

    @Override
    public EntityAura getAuraEntity() {
        return this.auraEntity;
    }

    @Override
    public void setAuraEntity(EntityAura aura) {
        this.auraEntity = aura;
    }

    @Override
    public Entity getEntity() {
        return player;
    }

    @Override
    public int getAuraColor() {
        return AuraColor > 0 ? AuraColor : JRMCoreH.Algnmnt_rc(Alignment);
    }

    @Override
    public byte getRace() {
        return Race;
    }

    @Override
    public int getFormID() {
        return addonFormID;
    }

    @Override
    public byte getRelease() {
        return Release;
    }

    @Override
    public byte getState() {
        return State;
    }

    @Override
    public byte getState2() {
        return State2;
    }

    /**
     * Set a players lock on state!
     *
     * @param lockOnTarget Reference to new target Entity or null to remove lock on.
     */
    public void setLockOnTarget(EntityLivingBase lockOnTarget) {

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            DBCUpdateLockOn packet;
            if (lockOnTarget == null) {
                packet = new DBCUpdateLockOn();
            } else {
                packet = new DBCUpdateLockOn(lockOnTarget.getEntityId());
            }
            DBCPacketHandler.Instance.sendToPlayer(packet, (EntityPlayerMP) player);
            return;
        }


        if (side == Side.CLIENT) {
            if (player == Minecraft.getMinecraft().thePlayer) {
                DBCUpdateLockOn.setLockOnTarget(lockOnTarget);
            }
            return;
        }
    }

    @Override
    public HashMap getDBCAuras(boolean secondary) {
        return null;
    }

    @Override
    public List<EntityCusPar> getParticles() {
        return particleRenderQueue;
    }

    @Override
    public int getActiveAuraColor() {
        return activeAuraColor;
    }

    @Override
    public void setActiveAuraColor(int color) {
        activeAuraColor = color;
    }

    public int getUsedMind() {
        int[][] rSklsMR = (int[][]) null;
        int[][] cSklsMR = (int[][]) null;
        String[] cSkls;
        String[] skls;
        int[][] sklsMR;


        if (JRMCoreH.isPowerTypeChakra(this.Powertype)) {
            cSkls = JRMCoreH.ncCSkls;
            cSklsMR = JRMCoreH.NCRacialSkillMindCost;
            skls = JRMCoreH.NCSkillIDs;
            sklsMR = JRMCoreH.NCSkillMindCost;
        } else {
            rSklsMR = JRMCoreH.DBCRacialSkillMindCost;
            cSkls = JRMCoreH.vlblCSkls;
            skls = JRMCoreH.DBCSkillsIDs;
            sklsMR = JRMCoreH.DBCSkillMindCost;
        }

        int mindSpentOnSkills = JRMCoreH.skillSlot_SpentMindRequirement(this.Skills.split(","), skls, sklsMR);
        int mindSpentOnRacialForms = JRMCoreH.skillSlot_SpentMindRequirement_X(this.RacialSkills, this.Race, rSklsMR);
        int raceStuff = JRMCoreH.skillSlot_SpentMindRequirement(this.getRawCompound().getString("jrmcSSltY"), cSkls, cSklsMR);
        int addonTakenAwayMind = calculateMindBonuses();

        return mindSpentOnSkills + mindSpentOnRacialForms + raceStuff - addonTakenAwayMind;
    }

    public int calculateMindBonuses() {
        int mindBonus = 0;
        // TODO: Think of a nice way to implement mind bonuses / penalties.
        //      I do not quite like the idea of using `PlayerBonus`es.

        PlayerDBCInfo formData = getDBCInfo();
        FormController formController = FormController.getInstance();
        for (int formID : formData.unlockedForms) {
            IForm form = formController.get(formID);
            if (form != null)
                mindBonus -= form.getMindRequirement();
        }

        return mindBonus;
    }

    public int getAvailableMind() {
        return this.MND - this.getUsedMind();
    }

    public FormDisplay.BodyColor getCurrentFormColorCustomization() {
        if (side.isClient())
            return currentCustomizedColors;

        Form form = getForm();
        if (form == null || !form.display.isCustomizable())
            return null;

        PlayerDBCInfo info = getDBCInfo();
        return info.configuredFormColors.get(form.id);
    }

    public int getColor(String type) {
        int customCol = currentCustomizedColors.getColor(type);
        if (customCol != -1)
            return customCol;

        Form form = getForm();
        if (form != null) {
            int formCol = form.display.getColor(type);
            if (formCol != -1)
                return formCol;
        }

        switch (type.toLowerCase()) {
            case "hair":
                return JRMCoreH.dnsHairC(DNS);
            case "eye":
                return JRMCoreH.dnsEyeC1(DNS);
            case "bodycm":
                return JRMCoreH.dnsBodyCM(DNS);
            case "bodyc1":
                return JRMCoreH.dnsBodyC1(DNS);
            case "bodyc2":
                return JRMCoreH.dnsBodyC2(DNS);
            case "bodyc3":
                return JRMCoreH.dnsBodyC3(DNS);
            case "fur":
                int oozaruFur = skinType == 1 ? JRMCoreH.dnsBodyC1(DNS) : JRMCoreH.dnsBodyC1_0(DNS);
                if (oozaruFur != 0x632700) // 0x632700 is oozaru brown, this means is half saiyan and has custom hair/fur color
                    return oozaruFur;
                return 0xDA152C; //ssj4 red
        }
        return -1;
    }

    public Set<FacePartData.Part> getDisabledFaceParts() {
        return FacePartData.getDisabledParts(null, getForm(), cachedOverlays, JRMCoreH.dnsEyes(DNS));
    }

    public void sendCurrentFormColorData() {
        FormDisplay.BodyColor currentColors = getCurrentFormColorCustomization();
        NBTTagCompound dataNeededOnClient = new NBTTagCompound();

        if (currentColors != null) {
            NBTTagCompound colorCompound = new NBTTagCompound();
            currentColors.writeToNBT(colorCompound);
            dataNeededOnClient.setTag("CustomFormColors", colorCompound);
        }

        DBCPacketHandler.Instance.sendTracking(new PingFormColorPacket(this, dataNeededOnClient), player);
    }
}
