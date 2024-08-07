package kamkeel.npcdbc.mixins.late.impl.dbc;


import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.server.config.dbc.JGConfigDBCFormMastery;
import JinRyuu.JRMCore.server.config.dbc.JGConfigRaces;
import JinRyuu.JRMCore.server.config.dbc.JGConfigUltraInstinct;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.DBCAttribute;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormMastery;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import noppes.npcs.util.ValueUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static JinRyuu.JRMCore.JRMCoreH.*;
import static kamkeel.npcdbc.util.DBCUtils.lastSetDamage;

@Mixin(value = JRMCoreH.class, remap = false)
public abstract class MixinJRMCoreH {

    @Shadow
    public static float[][] TransSaiStBnP;
    @Shadow
    public static float[][] TransNaStBnP;
    @Shadow
    public static float[][] TransMaStBnP;
    private static boolean calculatingKi;

    @Inject(method = "techDBCkic([Ljava/lang/String;I[B)I", at = @At("HEAD"))
    private static void fix10xKiCost(String[] listOfAttacks, int playerStat, byte[] kiAttackStats, CallbackInfoReturnable<Integer> cir, @Local(ordinal = 0) LocalIntRef stat) {
        calculatingKi = true;
        EntityPlayer player = Utility.isServer() ? CommonProxy.CurrentJRMCTickPlayer : CustomNpcPlusDBC.proxy.getClientPlayer();

        DBCData data = DBCData.get(player);
        boolean majin = JRMCoreH.StusEfcts(12, data.StatusEffects);
        boolean fusion = (JRMCoreH.StusEfcts(10, data.StatusEffects) || JRMCoreH.StusEfcts(11, data.StatusEffects));
        boolean legendary = JRMCoreH.StusEfcts(14, data.StatusEffects);
        boolean kaioken = JRMCoreH.StusEfcts(5, data.StatusEffects);
        boolean mystic = JRMCoreH.StusEfcts(13, data.StatusEffects);
        boolean ui = JRMCoreH.StusEfcts(19, data.StatusEffects);
        boolean GoD = JRMCoreH.StusEfcts(20, data.StatusEffects);

        int wil = JRMCoreH.getPlayerAttribute(data.player, data.stats.getAllAttributes(), 3, 0, 0, data.Race, data.RacialSkills, data.Release, data.ArcReserve, legendary, majin, kaioken, mystic, ui, GoD, data.Powertype, data.Skills.split(","), fusion, data.MajinAbsorptionData);
        int stat2 = JRMCoreH.stat(player, 3, data.Powertype, 4, wil, data.Race, data.Class, 0.0F);

        stat.set(stat2);
        calculatingKi = false;


    }


    @Inject(method = "getPlayerAttribute(Lnet/minecraft/entity/player/EntityPlayer;[IIIIILjava/lang/String;IIZZZZZZI[Ljava/lang/String;ZLjava/lang/String;)I", at = @At("HEAD"), remap = false, cancellable = true)
    private static void onGetPlayerAttribute(EntityPlayer player, int[] currAttributes, int attribute, int st, int st2, int race, String SklX, int currRelease, int arcRel, boolean legendOn, boolean majinOn, boolean kaiokenOn, boolean mysticOn, boolean uiOn, boolean GoDOn, int powerType, String[] Skls, boolean isFused, String majinAbs, CallbackInfoReturnable<Integer> info) {
        if (calculatingKi)
            return;

        if (player == null)
            return;

        DBCData dbcData = DBCData.get(player);

        if (dbcData == null)
            return;

        Form form = (Form) FormController.getInstance().get(dbcData.addonFormID);
        if (form == null)
            return;

        int skillX = powerType == 1 ? JRMCoreH.SklLvlX(1, SklX) : 0;
        int mysticLvl = powerType == 1 ? JRMCoreH.SklLvl(10, 1, Skls) : 0;
        int result = 0;

        int state = form.stackable.vanillaStackable ? st : 0;
        boolean masteryCalc = JGConfigDBCFormMastery.FM_Enabled;
        JGConfigDBCFormMastery.FM_Enabled = masteryCalc && form.stackable.vanillaStackable;

        float oldValue = -1f;

        float absorptionMulti = 1;

        if(!form.stackable.vanillaStackable){
            oldValue = replaceOldMulti(race, attribute);
        }

        switch (race) {
            case 0:
                result = JRMCoreH.getAttributeHuman(player, currAttributes, attribute, state, skillX, false, mysticLvl, isFused, false, powerType, false);
                break;
            case 1:
                result = JRMCoreH.getAttributeSaiyan(player, currAttributes, attribute, state, skillX, false, mysticLvl, isFused, false, powerType, false);
                break;
            case 2:
                result = JRMCoreH.getAttributeHalfSaiyan(player, currAttributes, attribute, state, skillX, false, mysticLvl, isFused, false, powerType, false);
                break;
            case 3:
                result = JRMCoreH.getAttributeNamekian(player, currAttributes, attribute, state, skillX, false, mysticLvl, isFused, false, powerType, false);
                break;
            case 4:
                result = JRMCoreH.getAttributeArcosian(player, currAttributes, attribute, state, currRelease, arcRel, skillX, false, mysticLvl, isFused, false, powerType, false);
                break;
            case 5:
                result = JRMCoreH.getAttributeMajin(player, currAttributes, attribute, state, skillX, false, mysticLvl, isFused, false, powerType, false, majinAbs);
                break;
            default:
                result = currAttributes[attribute];
        }
        if(!form.stackable.vanillaStackable && oldValue > 0){
            resetOldMulti(race, attribute, oldValue);
        }


        JGConfigDBCFormMastery.FM_Enabled = masteryCalc;

        if (race == DBCRace.ARCOSIAN) {
            if(powerType == 1 && currRelease >= 100 && arcRel > 0){
                result = customNPC_DBC_Addon$calculateArcosianPowerPoint(result, form, arcRel);
            }
        }

        if (race == DBCRace.MAJIN) {
            if(powerType == 1 && majinAbs.length() > 0 && JGConfigRaces.CONFIG_MAJIN_ENABLED && JGConfigRaces.CONFIG_MAJIN_ABSORPTION_ENABLED){
                absorptionMulti = customNPC_DBC_Addon$calculateMajnAbsorption(form, majinAbs);
            }
        }

        DBCData d = DBCData.get(player);
        float[] formMulti = form.getAllMulti();
        float stackableMulti = d.isForm(DBCForm.Kaioken) ? form.stackable.getFormMulti(DBCForm.Kaioken) : d.isForm(DBCForm.UltraInstinct) ? form.stackable.getFormMulti(DBCForm.UltraInstinct) : d.isForm(DBCForm.GodOfDestruction) ? form.stackable.getFormMulti(DBCForm.GodOfDestruction) : d.isForm(DBCForm.Mystic) ? form.stackable.getFormMulti(DBCForm.Mystic) : 1.0f;
        double fmvalue = 1.0f;

        //don't forget to multiply this by legend/divine/majin formMulti
        if (kaiokenOn && d.State2 > 0) {
            fmvalue = JRMCoreH.getFormMasteryAttributeMulti(player, "Kaioken", st, st2, race, kaiokenOn, mysticOn, uiOn, GoDOn);
            stackableMulti += stackableMulti * form.stackable.getState2Factor(DBCForm.Kaioken) * d.State2 / (JRMCoreH.TransKaiDmg.length - 1);
        } else if (uiOn && d.State2 > 0) {
            fmvalue = JRMCoreH.getFormMasteryAttributeMulti(player, "UltraInstict", st, st2, race, kaiokenOn, mysticOn, uiOn, GoDOn);
            stackableMulti += stackableMulti * form.stackable.getState2Factor(DBCForm.UltraInstinct) * d.State2 / JGConfigUltraInstinct.CONFIG_UI_LEVELS;
        } else if (GoDOn) {
            fmvalue = JRMCoreH.getFormMasteryAttributeMulti(player, "GodOfDestruction", st, st2, race, kaiokenOn, mysticOn, uiOn, GoDOn);
        } else if (mysticOn) {
            fmvalue = JRMCoreH.getFormMasteryAttributeMulti(player, "Mystic", st, st2, race, kaiokenOn, mysticOn, uiOn, GoDOn);
        }

        stackableMulti *= (float) fmvalue;

        if(race == DBCRace.MAJIN){
            if (JGConfigRaces.CONFIG_MAJIN_ABSORPTON_MULTIPLIES_BONUS_ATTRIBUTE_MULTIPLIERS) {
                stackableMulti *= absorptionMulti;
            } else {
                stackableMulti += absorptionMulti;
            }
        }

        float statusMulti = 1;

        if (majinOn)
            statusMulti += form.stackable.useConfigMulti(DBCForm.Majin) ? JRMCoreConfig.mjn * 0.01F : form.stackable.majinStrength - 1;
        if (legendOn)
            statusMulti += form.stackable.useConfigMulti(DBCForm.Legendary) ? JRMCoreConfig.lgnd * 0.01F : form.stackable.legendaryStrength - 1;
        if (d.isForm(DBCForm.Divine))
            statusMulti += (form.stackable.useConfigMulti(DBCForm.Divine) ? ConfigDBCEffects.getDivineMulti() : form.stackable.divineStrength) - 1;


        float currentFormLevel = dbcData.addonFormLevel;

        float[] multiBonus = dbcData.bonus.getMultiBonus();

        if (attribute == 0 || attribute == 1 || attribute == 3) {
            result *= (stackableMulti * ((FormMastery) dbcData.getForm().getMastery()).calculateMulti("attribute", currentFormLevel));
        }

        if (attribute == DBCAttribute.Strength) // STR
            result = (int) (result * (formMulti[0] * statusMulti + multiBonus[0]));
        else if (attribute == DBCAttribute.Dexterity) // DEX
            result = (int) (result * (formMulti[1] * statusMulti + multiBonus[1]));
        else if (attribute == DBCAttribute.Willpower) // WIL
            result = (int) (result * (formMulti[2] * statusMulti + multiBonus[2]));


        // Add Bonus Multi to Base Attributes
        if (attribute == DBCAttribute.Strength) // STR
            result += (currAttributes[0] * multiBonus[0]);
        else if (attribute == DBCAttribute.Dexterity) // DEX
            result += (currAttributes[1] * multiBonus[1]);
        else if (attribute == DBCAttribute.Willpower) // WIL
            result += (currAttributes[3] * multiBonus[2]);

        float[] flatBonus = dbcData.bonus.getFlatBonus();
        // Add Bonus Flat to Base Attributes at the end
        if (attribute == DBCAttribute.Strength) // STR
            result += flatBonus[0];
        else if (attribute == DBCAttribute.Dexterity) // DEX
            result += flatBonus[1];
        else if (attribute == DBCAttribute.Willpower) // WIL
            result += flatBonus[2];
        else if (attribute == DBCAttribute.Constitution) // CON
            result += flatBonus[3];
        else if (attribute == DBCAttribute.Spirit) // SPI
            result += flatBonus[4];

        result = ValueUtil.clamp(result, 0, Integer.MAX_VALUE);
        info.setReturnValue(result);


    }

    private static float[][] getRightMultiArray(int race){
        switch(race){
            case 0:
                return TransHmStBnP;
            case 1:
                return TransSaiStBnP;
            case 2:
                return TransHalfSaiStBnP;
            case 3:
                return TransNaStBnP;
            case 4:
                return TransFrStBnP;
            case 5:
                return TransMaStBnP;

        }
        return null;
    }

    private static void resetOldMulti(int race, int attribute, float oldValue) {
        float[] array = getRightMultiArray(race)[0];
        if(attribute < 0)
            attribute = 0;
        if(attribute >= array.length)
            attribute = array.length - 1;

        array[attribute] = oldValue;
    }

    private static float replaceOldMulti(int race, int attribute) {
        float[] array = getRightMultiArray(race)[0];
        if(attribute < 0)
            attribute = 0;
        if(attribute >= array.length)
            attribute = array.length - 1;

        float oldValue = array[attribute];
        array[attribute] = 1;
        return oldValue;
    }

    @Unique
    private static int customNPC_DBC_Addon$calculateArcosianPowerPoint(int original, Form form, int arcRel) {
        boolean addPointValue = form.mastery.powerPointMultiBasedOnPoints != -1;
        return (int) ((float) original * form.mastery.powerPointMultiNormal * (addPointValue ? 1.0F + getArcosianReserveMaxPointPercentage(arcRel) * form.mastery.powerPointMultiBasedOnPoints : 1f));
    }

    @Unique
    private static float customNPC_DBC_Addon$calculateMajnAbsorption(Form form, String majinAbs) {
        return form.mastery.absorptionMulti * ((float)getMajinAbsorptionValueS(majinAbs) / JGConfigRaces.CONFIG_MAJIN_ABSORPTON_MAX_LEVEL);
    }

    @Inject(method = "getPlayerAttribute(Lnet/minecraft/entity/player/EntityPlayer;[IIIIILjava/lang/String;IIZZZZZZI[Ljava/lang/String;ZLjava/lang/String;)I", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreConfig;OverAtrLimit:Z"), remap = false, cancellable = true)
    private static void applyBonusToDBC(EntityPlayer player, int[] currAttributes, int attribute, int st, int st2, int race, String SklX, int currRelease, int arcRel, boolean legendOn, boolean majinOn, boolean kaiokenOn, boolean mysticOn, boolean uiOn, boolean GoDOn, int powerType, String[] Skls, boolean isFused, String majinAbs, CallbackInfoReturnable<Integer> info, @Local(name = "result") LocalIntRef result) {
        if (player == null)
            return;
        DBCData dbcData = DBCData.get(player);
        int resultOriginal = result.get();

        if (!DBCUtils.noBonusEffects) {
            float[] bonus = dbcData.bonus.getMultiBonus();
            if (attribute == 0 && bonus[0] != 0) //str
                resultOriginal += (currAttributes[0] * bonus[0]);
            else if (attribute == 1 && bonus[0] != 0) //dex
                resultOriginal += (currAttributes[1] * bonus[1]);
            else if (attribute == 3 && bonus[0] != 0) //will
                resultOriginal += (currAttributes[3] * bonus[2]);


            float[] flatBonus = dbcData.bonus.getFlatBonus();
            // Add Bonus Flat to Base Attributes at the end
            if (attribute == DBCAttribute.Strength) // STR
                resultOriginal += flatBonus[0];
            else if (attribute == DBCAttribute.Dexterity) // DEX
                resultOriginal += flatBonus[1];
            else if (attribute == DBCAttribute.Willpower) // WIL
                resultOriginal += flatBonus[2];
            else if (attribute == DBCAttribute.Constitution) // CON
                resultOriginal += flatBonus[3];
            else if (attribute == DBCAttribute.Spirit) // SPI
                resultOriginal += flatBonus[4];
        }

        result.set(resultOriginal);
    }

    // fix for descending from a CF sets release to 0 as game registers you as base in a CF
    @Inject(method = "Rls", at = @At("HEAD"), cancellable = true)
    private static void fix0ReleaseOnCFDescend(byte b, CallbackInfo ci) {
        if (b == 0) {
            PlayerDBCInfo formData = PlayerDataUtil.getClientDBCInfo();
            if (formData != null && formData.isInCustomForm())
                ci.cancel();
        }
    }

    //delete all player CF data on jrmc startnew
    @Inject(method = "resetChar(Lnet/minecraft/entity/player/EntityPlayer;ZZZF)V", at = @At("TAIL"))
    private static void resetChar(EntityPlayer p, boolean keepSkills, boolean keepTechs, boolean keepMasteries, float perc, CallbackInfo ci) {
        PlayerDataUtil.getDBCInfo(p).resetChar();
        if (!keepMasteries) {
            NBTTagCompound PlayerPersisted = nbt(p);
            for (int i = 0; i < Races.length; i++)
                if (PlayerPersisted.hasKey(getNBTFormMasteryRacialKey(i))) //remove all form mastery tags that are not player's race
                    PlayerPersisted.removeTag(getNBTFormMasteryRacialKey(i));

            if (PlayerPersisted.hasKey("jrmcFormMasteryNonRacial"))
                PlayerPersisted.removeTag("jrmcFormMasteryNonRacial");

        }
    }

    //if release becomes 0%, force descend player from CF on server side
    @Inject(method = "setByte(ILnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    private static void descendOn0Release(int s, EntityPlayer Player, String string, CallbackInfo ci) {
        if (s == 0 && string.equals("jrmcRelease")) {
            PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(Player);
            Form form = DBCData.getForm(Player);
            if (form != null) {
                formData.currentForm = -1;
                formData.updateClient();
            }
        }
    }

    //if ki becomes 0, force descend player from CF on server side
    @Inject(method = "setInt(ILnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    private static void descendOn0Ki(int s, EntityPlayer Player, String string, CallbackInfo ci) {
        if (s == 0 && string.equals("jrmcEnrgy")) {
            PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(Player);
            Form form = DBCData.getForm(Player);
            if (form != null) {
                formData.currentForm = -1;
                formData.updateClient();
            }
        }
    }

    @Inject(method = "addToFormMasteriesValue(Lnet/minecraft/entity/player/EntityPlayer;DDIIIZZZZI)V", at = @At("HEAD"), cancellable = true)
    private static void addFormMasteries(EntityPlayer player, double value, double valueKK, int race, int state, int state2, boolean isKaiokenOn, boolean isMysticOn, boolean isUltraInstinctOn, boolean isGoDOn, int gainMultiID, CallbackInfo ci) {
        if (gainMultiID == 0)
            PlayerDataUtil.getDBCInfo(player).updateCurrentFormMastery("update");
        else if (gainMultiID == 1)
            PlayerDataUtil.getDBCInfo(player).updateCurrentFormMastery("attack");
        else if (gainMultiID == 2)
            PlayerDataUtil.getDBCInfo(player).updateCurrentFormMastery("damaged");
        else if (gainMultiID == 3)
            PlayerDataUtil.getDBCInfo(player).updateCurrentFormMastery("fireki");
    }

    @ModifyArgs(method = "jrmcDam(Lnet/minecraft/entity/Entity;ILnet/minecraft/util/DamageSource;)I", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;setInt(ILnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V"))
    private static void setDamage(Args args) {
        String type = args.get(2);
        if (lastSetDamage != -1 && type.equals("jrmcBdy")) {
            int curBody = getInt(args.get(1), "jrmcBdy");
            int newHealth = curBody - lastSetDamage;
            args.set(0, Math.max(0, newHealth));
            lastSetDamage = -1;
        }
    }


    @Inject(method = "jrmcDam(Lnet/minecraft/entity/Entity;ILnet/minecraft/util/DamageSource;B)I", at = @At("HEAD"), cancellable = true)
    private static void tailCutMastery(Entity Player, int dbcA, DamageSource s, byte t, CallbackInfoReturnable<Integer> cir) {
        if (!Player.worldObj.isRemote && Player instanceof EntityPlayer && t == 2) {
            DBCData dbcData = DBCData.get((EntityPlayer) Player);
            Form form = dbcData.getForm();
            if (form != null) {
                if (form.display.hairType.equals("ssj4") || form.display.hairType.equals("oozaru")) {
                    float cutChance = form.mastery.tailCutChance * form.mastery.calculateMulti("tailcutchance", dbcData.addonFormLevel);
                    double rand = Math.random();
                    if ((cutChance / 100) >= rand) {
                        Player.worldObj.playSoundAtEntity(Player, "jinryuudragonbc:DBC4.disckill", 1.0F, 1.0F);
                        setByte((byte) 4, (EntityPlayer) Player, "jrmcTlmd");
                        int state = getByte((EntityPlayer) Player, "jrmcState");
                        if (state == 7 || state == 8 || state == 14) {
                            setByte(0, (EntityPlayer) Player, "jrmcState");
                        }
                    }
                    cir.setReturnValue(jrmcDam(Player, dbcA, s));
                }
            }
        }
    }

    @Inject(method = "jrmcDam(Lnet/minecraft/entity/Entity;ILnet/minecraft/util/DamageSource;)I", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;setByte(BLnet/minecraft/entity/player/EntityPlayer;Ljava/lang/String;)V", ordinal = 0, shift = At.Shift.BEFORE))
    private static void callKOEvent(Entity Player, int dbcA, DamageSource s, CallbackInfoReturnable<Integer> cir) {
        DBCEventHooks.onKnockoutEvent(new DBCPlayerEvent.KnockoutEvent(PlayerDataUtil.getIPlayer((EntityPlayer) Player), s));
    }

    @Inject(method = "jrmcDam(Lnet/minecraft/entity/Entity;ILnet/minecraft/util/DamageSource;)I", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreConfig;StatPasDef:I", shift = At.Shift.AFTER))
    private static void applyChargingDex(Entity Player, int dbcA, DamageSource s, CallbackInfoReturnable<Integer> cir, @Local(name = "def") LocalIntRef def, @Local(name = "kiProtection") LocalIntRef kiProtection) {
        DBCData dbcData = DBCData.get((EntityPlayer) Player);
        byte classID = dbcData.Class;
        boolean isChargingKi = DBCData.get((EntityPlayer) Player).stats.isChargingKiAttack();
        float newDef = def.get();
        int kiProt = kiProtection.get();
        if (isChargingKi && ConfigDBCGameplay.EnableChargingDex) {
            // Charging Dex
            switch (classID) {
                case 0:
                    newDef = ((newDef - kiProt) * ConfigDBCGameplay.MartialArtistCharge * 0.01F) + kiProt;
                    break;
                case 1:
                    newDef = ((newDef - kiProt) * ConfigDBCGameplay.SpiritualistCharge * 0.01F) + kiProt;
                    break;
                case 2:
                    newDef = ((newDef - kiProt) * ConfigDBCGameplay.WarriorCharge * 0.01F) + kiProt;
                    break;
                default:
                    newDef = ((newDef - kiProt) * JRMCoreConfig.StatPasDef * 0.01F) + kiProt;
                    break;
            }
        }
        def.set((int) newDef);
    }

    @Inject(method = "getHeatPercentageClient", at = @At("HEAD"), cancellable = true)
    private static void customHeat(CallbackInfoReturnable<Float> cir) {
        DBCData dbcData = DBCData.getClient();
        if (dbcData == null)
            return;

        Form form = dbcData.getForm();
        if (form != null && form.mastery.hasHeat()) {
            float currentHeat = ValueUtil.clamp(dbcData.addonCurrentHeat, 0, form.mastery.maxHeat);
            cir.setReturnValue(currentHeat / form.mastery.maxHeat * 100);
        }

    }

    @Inject(method = "KaiKCost", at=@At("HEAD"), cancellable = true)
    private static void customFormKaiokenDrain(EntityPlayer p, CallbackInfoReturnable<Double> cir){

        DBCData dbcData = DBCData.get(p);
        if(dbcData == null)
            return;
        Form form = dbcData.getForm();
        if(form == null)
            return;

        if(form.stackable.kaiokenMultipliesCurrentFormDrain)
            return;

        int race = dbcData.Race;
        int state2 = dbcData.State2;

        if(state2 <= 0)
            return;

        int strain = getInt(p, "jrmcStrain");
        int might = dbcData.STR / 2 + dbcData.WIL / 2;
        int cons = dbcData.CON;
        double c = (double)(10 - SklLvl(8, p) + state2) * 0.01;
        float kc = kaiokenBalanceValue(form, state2, strain > 0);
        c += JRMCoreConfig.sskai ? 0.0F : kc;
        double cost = 1.0 / (double)cons * (double)might * c * (double)TransKaiDrainRace[race] * (double)TransKaiDrainLevel[state2] * (double)(DBC() ? form.stackable.getKaioDrain() : 1.0F);

        if (JGConfigDBCFormMastery.FM_Enabled) {
            int kkID = getFormID("Kaioken", race);
            double kkMasteryLevel = getFormMasteryValue(p, kkID);
            float costMulti = (float)JGConfigDBCFormMastery.getCostMulti(kkMasteryLevel, race, kkID, JGConfigDBCFormMastery.DATA_ID_KAIOKEN_HEALTH_COST_MULTI);
            cost *= costMulti;
        }

        cir.setReturnValue(cost);
    }

    @Inject(method="KaiKCost", at=@At("RETURN"), cancellable = true)
    private static void customFormKaiokenDrainMultiply(EntityPlayer p, CallbackInfoReturnable<Double> cir){
        DBCData dbcData = DBCData.get(p);
        if(dbcData == null)
            return;
        Form form = dbcData.getForm();
        if(form == null)
            return;

        if(!form.stackable.kaiokenMultipliesCurrentFormDrain)
            return;
        cir.setReturnValue(cir.getReturnValueD() * form.stackable.kaiokenDrainMulti);
    }

    private static float kaiokenBalanceValue(Form form, int state2, boolean strained){
        return form.stackable.getKaioState2Balance(state2-1, strained);
    }

    @Inject(method = "configToClient(Lio/netty/buffer/ByteBuf;)V", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/server/config/dbc/JGConfigUltraInstinct;cCONFIG_UI_HEAT_DURATION:[I", shift = At.Shift.BEFORE))
    private static void configPacket(ByteBuf b, CallbackInfo ci, @Local(name = "i") LocalIntRef i) {
        ByteBufUtils.writeUTF8String(b, DBCUtils.cCONFIG_UI_NAME[i.get()]);
        b.writeBoolean(JGConfigUltraInstinct.cCONFIG_UI_SKIP[i.get()]);

    }

}

