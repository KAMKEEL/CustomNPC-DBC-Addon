package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.DragonBC.common.DBCKiTech;
import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import JinRyuu.JRMCore.JRMCoreH;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import kamkeel.npcdbc.CommonProxy;
import kamkeel.npcdbc.client.ClientCache;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.enums.EnumPlayerAuraTypes;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.mixin.IEntityAura;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.TransformPacket;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DBCKiTech.class, remap = false)
public class MixinDBCKiTech {
    @Inject(method = "chargePart(Lnet/minecraft/entity/player/EntityPlayer;IIIIIZLjava/lang/String;)V", at = @At(value = "INVOKE", target = "LJinRyuu/JRMCore/JRMCoreH;rc_nam(I)Z", ordinal = 0, shift = At.Shift.BEFORE))
    private static void setAuraType(EntityPlayer p, int r, int a, int c, int s, int k, boolean b, String se, CallbackInfo ci, @Local(name = "state") LocalFloatRef state, @Local(name = "state2") LocalFloatRef state2, @Local(name = "kk") LocalBooleanRef kk, @Local(name = "ssb") LocalBooleanRef ssb, @Local(name = "ssg") LocalBooleanRef ssg, @Local(name = "ssbs") LocalBooleanRef ssbs, @Local(name = "v") LocalBooleanRef divine, @Local(name = "oozar") LocalBooleanRef oozaru, @Local(name = "ui") LocalBooleanRef ui, @Local(name = "gd") LocalBooleanRef godestruction, @Local(name = "auf") LocalBooleanRef auf) {
        DBCData dbcData = DBCData.get(p);
        Aura aura = dbcData.getAura();
        if (aura != null) {
            if (aura.display.type == EnumPlayerAuraTypes.SaiyanGod)
                ssg.set(true);
            else if (aura.display.type == EnumPlayerAuraTypes.SaiyanBlue)
                ssb.set(true);
            else if (aura.display.type == EnumPlayerAuraTypes.SaiyanBlueKK) {
                kk.set(true);
                ssb.set(true);
            } else if (aura.display.type == EnumPlayerAuraTypes.SaiyanBlueEvo)
                ssbs.set(true);
            else if (aura.display.type == EnumPlayerAuraTypes.SaiyanRose) {
                ssb.set(true);
                divine.set(true);
            } else if (aura.display.type == EnumPlayerAuraTypes.UltimateArco) {
                auf.set(true);
            } else if (aura.display.type == EnumPlayerAuraTypes.SaiyanRoseEvo) {
                ssbs.set(true);
                divine.set(true);
            } else if (aura.display.type == EnumPlayerAuraTypes.UI)
                ui.set(true);
            else if (aura.display.type == EnumPlayerAuraTypes.GoD)
                godestruction.set(true);
        }
    }

    /**
     * Prevents player from transforming to other DBC forms if they are in custom form, except stackable ones
     */
    @Inject(method = "Ascend", at = @At("HEAD"), cancellable = true)
    private static void Ascend(KeyBinding K, CallbackInfo ci) {
        if (K.getIsKeyPressed()) {
            PlayerDBCInfo formData = PlayerDataUtil.getClientDBCInfo();
            if (formData != null && formData.isInCustomForm()) {
                Form form = formData.getCurrentForm();
                if (form != null) {
                    if (JRMCoreH.PlyrSettingsB(0) && form.stackable.isFormStackable(DBCForm.Kaioken)) {
                    } else if (JRMCoreH.PlyrSettingsB(11) && form.stackable.isFormStackable(DBCForm.UltraInstinct)) {
                    } else if (JRMCoreH.PlyrSettingsB(16) && form.stackable.isFormStackable(DBCForm.GodOfDestruction)) {
                    } else if (JRMCoreH.PlyrSettingsB(6) && form.stackable.isFormStackable(DBCForm.Mystic)) {
                    } else
                        ci.cancel();
                }
            }
        }
    }

    /**
     * Descends from custom form when DBC descend key is pressed
     */
    @Inject(method = "Descend", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreH;kiInSuper:I", shift = At.Shift.AFTER), cancellable = true)
    private static void DescendModified(KeyBinding K, CallbackInfo ci) {
        PlayerDBCInfo formData = PlayerDataUtil.getClientDBCInfo();
        DBCData d = DBCData.get(Minecraft.getMinecraft().thePlayer);

        boolean returnEarly = true;
        if (d != null && formData != null && formData.isInCustomForm()) {
            Form form = formData.getCurrentForm();
            if (d.formSettingOn(DBCForm.Kaioken)) {
                if (d.isForm(DBCForm.Kaioken))
                    returnEarly = false;
            } else if (d.formSettingOn(DBCForm.UltraInstinct)) {
                if (d.isForm(DBCForm.UltraInstinct))
                    returnEarly = false;
            } else if (d.formSettingOn(DBCForm.GodOfDestruction)) {
                if (d.isForm(DBCForm.GodOfDestruction))
                    returnEarly = false;
            } else if (d.formSettingOn(DBCForm.Mystic))
                if (d.isForm(DBCForm.Mystic))
                    returnEarly = false;


            if (returnEarly) {
                if (form.requiredForm.containsKey((int) JRMCoreH.Race)) {
                    int id = d.stats.getJRMCPlayerID();
                    JRMCoreH.State = form.requiredForm.get((int) JRMCoreH.Race);
                    JRMCoreH.data2[id] = JRMCoreH.State + JRMCoreH.data2[id].substring(1);
                }
                if (form.hasParent() && formData.hasFormUnlocked(form.getParentID()))
                    PacketHandler.Instance.sendToServer(new TransformPacket(Minecraft.getMinecraft().thePlayer, form.getParentID(), false).generatePacket());
                else
                    PacketHandler.Instance.sendToServer(new TransformPacket(Minecraft.getMinecraft().thePlayer, -1, false).generatePacket());
                DBCKiTech.soundAsc(form.getDescendSound());
                ci.cancel();
            }
        }
    }

    @Inject(method = "triForce", at = @At("HEAD"), cancellable = true)
    private static void fixRage(int i, int j, int k, CallbackInfo ci) {
        if (i == 1 && j == 1 && k == 100 && TransformController.ascending)
            ci.cancel();
    }

    @Inject(method = "chargePart(Lnet/minecraft/entity/player/EntityPlayer;IIIIIZLjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntityInWorld(Lnet/minecraft/entity/Entity;)Z", shift = At.Shift.BEFORE, remap = true))
    private static void setAuraFields(EntityPlayer p, int r, int a, int c, int s, int k, boolean b, String se, CallbackInfo ci, @Local(name = "aura") LocalRef<Entity> Aura) {
        DBCData dbcData = DBCData.get(p);
        Aura aura = dbcData.getAura();
        if (aura != null) {
            if (Aura.get() instanceof EntityAura2) {
                EntityAura2 aur = (EntityAura2) Aura.get();
                int mimicColor = EnumPlayerAuraTypes.getManualAuraColor(aura.display.type);
                if(mimicColor != -1)
                    aur.setCol(mimicColor);

                if (aura.display.hasColor("color1"))
                    aur.setCol(aura.display.color1);
                if (aura.display.hasColor("color2"))
                    aur.setColL2(aura.display.color2);
                if (aura.display.hasColor("color3"))
                    aur.setColL3(aura.display.color3);
//            if (aura.display.hasTexture1)
//                tex = aura.display.texture1;
//            if (aura.display.hasTexture2)
//                texl2 = aura.display.texture2;
//            if (aura.display.hasTexture3)
//                texl3 = aura.display.texture3;
                if (aura.display.hasSpeed())
                    aur.setSpd((int) aura.display.speed);
                if (aura.display.hasAlpha("aura"))
                    aur.setAlp((float) aura.display.alpha / 255);

                if (aura.display.hasSize())
                    ((IEntityAura) aur).setSize(aura.display.size);

                ((IEntityAura) aur).setHasLightning(aura.display.hasLightning);
                ((IEntityAura) aur).setLightningColor(aura.display.lightningColor);

                if (aura.display.hasAlpha("lightning"))
                    ((IEntityAura) aur).setLightningAlpha(aura.display.lightningAlpha);
                else
                    ((IEntityAura) aur).setLightningAlpha(255);
            }
        }
    }


    /**
     * Methods Below so we don't need
     * to constantly scan stack traces
     */
    @Inject(method = "chargePart(Lnet/minecraft/entity/player/EntityPlayer;IIIIIZLjava/lang/String;)V", at = @At("HEAD"))
    private static void setFromRenderPlayerJBRA(EntityPlayer p, int r, int a, int c, int s, int k, boolean b, String se, CallbackInfo ci) {
        CommonProxy.CurrentAuraPlayer = p;
        ClientCache.isChangePart = true;
    }

    @Inject(method = "chargePart(Lnet/minecraft/entity/player/EntityPlayer;IIIIIZLjava/lang/String;)V", at = @At("TAIL"))
    private static void clearFromRenderPlayerJBRA(EntityPlayer p, int r, int a, int c, int s, int k, boolean b, String se, CallbackInfo ci) {
        CommonProxy.CurrentAuraPlayer = null;
        ClientCache.isChangePart = false;
    }



}
