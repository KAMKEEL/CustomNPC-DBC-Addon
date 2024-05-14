package kamkeel.npcdbc.mixin.impl.dbc;


import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.server.config.dbc.JGConfigDBCFormMastery;
import JinRyuu.JRMCore.server.config.dbc.JGConfigUltraInstinct;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.DBCAttribute;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormMastery;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import noppes.npcs.util.ValueUtil;
import org.spongepowered.asm.mixin.Mixin;
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

    @Inject(method = "getPlayerAttribute(Lnet/minecraft/entity/player/EntityPlayer;[IIIIILjava/lang/String;IIZZZZZZI[Ljava/lang/String;ZLjava/lang/String;)I", at = @At("HEAD"), remap = false, cancellable = true)
    private static void onGetPlayerAttribute(EntityPlayer player, int[] currAttributes, int attribute, int st, int st2, int race, String SklX, int currRelease, int arcRel, boolean legendOn, boolean majinOn, boolean kaiokenOn, boolean mysticOn, boolean uiOn, boolean GoDOn, int powerType, String[] Skls, boolean isFused, String majinAbs, CallbackInfoReturnable<Integer> info) {
        if (player != null && DBCData.get(player) != null) {
            DBCData dbcData = DBCData.get(player);
            Form form = dbcData.getForm();

            if (form == null)
                return;

            int skillX = powerType == 1 ? JRMCoreH.SklLvlX(1, SklX) - 1 : 0;
            int mysticLvl = powerType == 1 ? JRMCoreH.SklLvl(10, 1, Skls) : 0;
            int result = 0;
            mysticOn = false;
            uiOn = false;
            GoDOn = false;

            boolean masteryCalc = JGConfigDBCFormMastery.FM_Enabled;
            JGConfigDBCFormMastery.FM_Enabled = masteryCalc && form.stackable.vanillaStackable;
            int state = form.stackable.vanillaStackable ? st : 0;
            switch (race) {
                case 0:
                    result = JRMCoreH.getAttributeHuman(player, currAttributes, attribute, state, skillX, mysticOn, mysticLvl, isFused, uiOn, powerType, GoDOn);
                    break;
                case 1:
                    result = JRMCoreH.getAttributeSaiyan(player, currAttributes, attribute, state, skillX, mysticOn, mysticLvl, isFused, uiOn, powerType, GoDOn);
                    break;
                case 2:
                    result = JRMCoreH.getAttributeHalfSaiyan(player, currAttributes, attribute, state, skillX, mysticOn, mysticLvl, isFused, uiOn, powerType, GoDOn);
                    break;
                case 3:
                    result = JRMCoreH.getAttributeNamekian(player, currAttributes, attribute, state, skillX, mysticOn, mysticLvl, isFused, uiOn, powerType, GoDOn);
                    break;
                case 4:
                    result = JRMCoreH.getAttributeArcosian(player, currAttributes, attribute, state, currRelease, arcRel, skillX, mysticOn, mysticLvl, isFused, uiOn, powerType, GoDOn);
                    break;
                case 5:
                    result = JRMCoreH.getAttributeMajin(player, currAttributes, attribute, state, skillX, mysticOn, mysticLvl, isFused, uiOn, powerType, GoDOn, majinAbs);
                    break;
                default:
                    result = currAttributes[attribute];
            }
            JGConfigDBCFormMastery.FM_Enabled = masteryCalc;

            DBCData d = DBCData.get(player);
            float[] multis = form.getAllMulti();
            float stackableMulti = d.isForm(DBCForm.Kaioken) ? form.stackable.getFormMulti(DBCForm.Kaioken) : d.isForm(DBCForm.UltraInstinct) ? form.stackable.getFormMulti(DBCForm.UltraInstinct) : d.isForm(DBCForm.GodOfDestruction) ? form.stackable.getFormMulti(DBCForm.GodOfDestruction) : d.isForm(DBCForm.Mystic) ? form.stackable.getFormMulti(DBCForm.Mystic) : 1.0f;
            double fmvalue = 1.0f;

            //don't forget to multiply this by legend/divine/majin multis
            if (d.isForm(DBCForm.Kaioken) && d.State2 > 1) {
                fmvalue = JRMCoreH.getFormMasteryAttributeMulti(player, "Kaioken", st, st2, race, kaiokenOn, mysticOn, uiOn, GoDOn);
                stackableMulti += stackableMulti * form.stackable.getState2Factor(DBCForm.Kaioken) * d.State2 / (JRMCoreH.TransKaiDmg.length - 1);
            } else if (d.isForm(DBCForm.UltraInstinct) && d.State2 > 1) {
                fmvalue = JRMCoreH.getFormMasteryAttributeMulti(player, "UltraInstict", st, st2, race, kaiokenOn, mysticOn, uiOn, GoDOn);
                stackableMulti += stackableMulti * form.stackable.getState2Factor(DBCForm.UltraInstinct) * d.State2 / JGConfigUltraInstinct.CONFIG_UI_LEVELS;
            } else if (d.isForm(DBCForm.GodOfDestruction))
                fmvalue = JRMCoreH.getFormMasteryAttributeMulti(player, "GodOfDestruction", st, st2, race, kaiokenOn, mysticOn, uiOn, GoDOn);
            else if (d.isForm(DBCForm.Mystic))
                fmvalue = JRMCoreH.getFormMasteryAttributeMulti(player, "Mystic", st, st2, race, kaiokenOn, mysticOn, uiOn, GoDOn);

            stackableMulti *= (float) fmvalue;

            float currentFormLevel = dbcData.addonFormLevel;

            float[] multiBonus = dbcData.bonus.getMultiBonus();
            if(!DBCUtils.noBonusEffects){
                if (attribute == DBCAttribute.Strength) // STR
                    result *= (multis[0] + multiBonus[0]);
                else if (attribute == DBCAttribute.Dexterity) // DEX
                    result *= (multis[1] + multiBonus[1]);
                else if (attribute == DBCAttribute.Willpower) // WIL
                    result *= (multis[2] + multiBonus[2]);
            }

            if (attribute == 0 || attribute == 1 || attribute == 3)
                result *= (stackableMulti * ((FormMastery) form.getMastery()).calculateMulti("attribute", currentFormLevel));

            if(!DBCUtils.noBonusEffects){
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
            }

            result = ValueUtil.clamp(result, 0, Integer.MAX_VALUE);
            info.setReturnValue(result);
        }

    }

    @Inject(method = "getPlayerAttribute(Lnet/minecraft/entity/player/EntityPlayer;[IIIIILjava/lang/String;IIZZZZZZI[Ljava/lang/String;ZLjava/lang/String;)I", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreConfig;OverAtrLimit:Z"), remap = false, cancellable = true)
    private static void applyBonusToDBC(EntityPlayer player, int[] currAttributes, int attribute, int st, int st2, int race, String SklX, int currRelease, int arcRel, boolean legendOn, boolean majinOn, boolean kaiokenOn, boolean mysticOn, boolean uiOn, boolean GoDOn, int powerType, String[] Skls, boolean isFused, String majinAbs, CallbackInfoReturnable<Integer> info, @Local(name = "result") LocalIntRef result) {
        if (player == null)
            return;
        DBCData dbcData = DBCData.get(player);
        int resultOriginal = result.get();

        if(!DBCUtils.noBonusEffects){
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
        PlayerDataUtil.getDBCInfo(p).resetAllForm();
        PlayerDataUtil.getDBCInfo(p).resetAllAuras();
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
}

