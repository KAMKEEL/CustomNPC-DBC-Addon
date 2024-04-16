package kamkeel.npcdbc.mixin.impl;

import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.server.config.dbc.JGConfigUltraInstinct;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.data.CustomForm;
import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.data.SyncedData.DBCData;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = JRMCoreH.class, remap = false)
public class MixinJRMCoreH {

    @Inject(method = "getPlayerAttribute(Lnet/minecraft/entity/player/EntityPlayer;[IIIIILjava/lang/String;IIZZZZZZI[Ljava/lang/String;ZLjava/lang/String;)I", at = @At("HEAD"), remap = false, cancellable = true)
    private static void go1(EntityPlayer player, int[] currAttributes, int attribute, int st, int st2, int race, String SklX, int currRelease, int arcRel, boolean legendOn, boolean majinOn, boolean kaiokenOn, boolean mysticOn, boolean uiOn, boolean GoDOn, int powerType, String[] Skls, boolean isFused, String majinAbs, CallbackInfoReturnable<Integer> info) {
        {
            PlayerCustomFormData formData = Utility.isServer() ? Utility.getFormData(player) : Utility.getFormDataClient();
            System.out.println("Hi");
            if (formData != null && formData.isInCustomForm()) {
                int skillX = powerType == 1 ? JRMCoreH.SklLvlX(1, SklX) - 1 : 0;
                int mysticLvl = powerType == 1 ? JRMCoreH.SklLvl(10, 1, Skls) : 0;
                int result = 0;
                mysticOn = false;
                uiOn = false;
                GoDOn = false;
                switch (race) {
                    case 0:
                        result = JRMCoreH.getAttributeHuman(player, currAttributes, attribute, st, skillX, mysticOn, mysticLvl, isFused, uiOn, powerType, GoDOn);
                        break;
                    case 1:
                        result = JRMCoreH.getAttributeSaiyan(player, currAttributes, attribute, st, skillX, mysticOn, mysticLvl, isFused, uiOn, powerType, GoDOn);
                        break;
                    case 2:
                        result = JRMCoreH.getAttributeHalfSaiyan(player, currAttributes, attribute, st, skillX, mysticOn, mysticLvl, isFused, uiOn, powerType, GoDOn);
                        break;
                    case 3:
                        result = JRMCoreH.getAttributeNamekian(player, currAttributes, attribute, st, skillX, mysticOn, mysticLvl, isFused, uiOn, powerType, GoDOn);
                        break;
                    case 4:
                        result = JRMCoreH.getAttributeArcosian(player, currAttributes, attribute, st, currRelease, arcRel, skillX, mysticOn, mysticLvl, isFused, uiOn, powerType, GoDOn);
                        break;
                    case 5:
                        result = JRMCoreH.getAttributeMajin(player, currAttributes, attribute, st, skillX, mysticOn, mysticLvl, isFused, uiOn, powerType, GoDOn, majinAbs);
                        break;
                    default:
                        result = currAttributes[attribute];
                }
                System.out.println("Hi2");

                DBCData d = DBCData.get(player);
                System.out.println("Hi52");
                CustomForm f = formData.getCurrentForm();
                System.out.println("Hi53");
                float[] multis = formData.getCurrentForm().getAllMulti();
                System.out.println("Hi54");
                float stackableMulti = d.isForm(DBCForm.Kaioken) ? f.getFormMulti(DBCForm.Kaioken) : d.isForm(DBCForm.UltraInstinct) ? f.getFormMulti(DBCForm.UltraInstinct) : d.isForm(DBCForm.GodOfDestruction) ? f.getFormMulti(DBCForm.GodOfDestruction) : d.isForm(DBCForm.Mystic) ? f.getFormMulti(DBCForm.Mystic) : 1.0f;
                System.out.println("Hi5");

                if (d.isForm(DBCForm.Kaioken) && d.State2 > 1)
                    stackableMulti += stackableMulti * f.getState2Factor(DBCForm.Kaioken) * d.State2 / (JRMCoreH.TransKaiDmg.length - 1);
                if (d.isForm(DBCForm.UltraInstinct) && d.State2 > 1)
                    stackableMulti += stackableMulti * f.getState2Factor(DBCForm.UltraInstinct) * d.State2 / JGConfigUltraInstinct.CONFIG_UI_LEVELS;
                System.out.println("Hi34");

                if (attribute == 0) //str
                    result *= multis[0];
                if (attribute == 1) //dex
                    result *= multis[1];
                if (attribute == 3) //will
                    result *= multis[2];
                System.out.println("hi3");
                result *= stackableMulti;
                result = (int) ((double) result > Double.MAX_VALUE ? Double.MAX_VALUE : (double) result);

                info.setReturnValue(result);
            }
        }
    }
}

