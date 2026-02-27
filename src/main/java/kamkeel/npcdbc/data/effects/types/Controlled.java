package kamkeel.npcdbc.data.effects.types;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.controllers.DBCEffectController;
import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.effects.AddonEffect;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.items.ItemControlCrown;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import noppes.npcs.controllers.data.PlayerEffect;
import noppes.npcs.scripted.event.player.PlayerEvent;
import noppes.npcs.util.ValueUtil;

public class Controlled extends AddonEffect {
    public PlayerBonus debuff;

    public Controlled() {
        name = "Controlled";
        langName = "effect.controlled";
        id = Effects.CONTROLLED;
        iconX = 0;
        iconY = 16;
        length = -100;
        lossOnDeath = false;
        everyXTick = 10;

        debuff = new PlayerBonus(name, (byte) 1);
    }

    @Override
    public void onAdded(EntityPlayer player, PlayerEffect playerEffect) {
        updateBonus(player);
    }

    @Override
    public void onTick(EntityPlayer player, PlayerEffect playerEffect) {
        damageCrown(player);

        if (player.ticksExisted % 60 == 0)
            updateBonus(player);
    }

    @Override
    public void onRemoved(EntityPlayer player, PlayerEffect playerEffect, PlayerEvent.EffectEvent.ExpirationType type) {
        BonusController.getInstance().removeBonus(player, name);
    }

    private void updateBonus(EntityPlayer player) {
        if (player.getEquipmentInSlot(4) == null) {
            BonusController.getInstance().removeBonus(player, name);
            return;
        }

        if (!(player.getEquipmentInSlot(4).getItem() instanceof ItemControlCrown)) {
            BonusController.getInstance().removeBonus(player, name);
            return;
        }

        float decrement = (float) ConfigDBCEffects.CONTROLLED_DEBUFF;
        DBCData data = DBCData.get(player);

        float wilDec = (data.WIL - decrement <= 0) ? (data.WIL - 1) : decrement;
        float strDec = (data.STR - decrement <= 0) ? (data.STR - 1) : decrement;
        float dexDec = (data.DEX - decrement <= 0) ? (data.DEX - 1) : decrement;

        debuff.setWillpower(-wilDec);
        debuff.setStrength(-strDec);
        debuff.setDexterity(-dexDec);

        BonusController.getInstance().applyBonus(player, debuff);
    }

    private void damageCrown(EntityPlayer player) {
        if (player.getEquipmentInSlot(4) == null) {
            return;
        }

        if (!(player.getEquipmentInSlot(4).getItem() instanceof ItemControlCrown)) {
            return;
        }

        ItemStack stack = player.getEquipmentInSlot(4);
        boolean isReleasing = DBCData.get(player).StatusEffects.contains(JRMCoreH.StusEfcts[4]);
        int base = Math.round(ConfigDBCEffects.CONTROLLED_RATE / 10f) * 10;
        int breakRate = Math.max(1, isReleasing ? base / 2 : base);

        if (player.ticksExisted % breakRate == 0) {
            stack.damageItem(calculateDamage(player), player);
        }

        if (stack.getItemDamage() >= stack.getMaxDamage()) {
            player.setCurrentItemOrArmor(4, null);
            DBCEffectController.Instance.removeEffect(player, Effects.CONTROLLED);
            DBCEffectController.Instance.applyEffect(player, Effects.OVERPOWER, 180);
        }
    }

    private int calculateDamage(EntityPlayer player) {
        DBCData dbcData = DBCData.get(player);
        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);

        float release = dbcData.Release;
        float formMultiplier = info.currentForm != -1 ? getFormMultiAverage(info.getCurrentForm()) : 1.0f;
        float divineMulti = ConfigDBCEffects.getDivineMulti();

        return Math.round(ValueUtil.clamp((( release / 10) + formMultiplier + divineMulti) / 3, 1, 20));
    }

    private float getFormMultiAverage(Form form) {
        float acc = 0;
        for (int i = 0; i <= 3; i++) {
            if (i == 2) continue;
            acc += form.getAttributeMulti(i);
        }
        return acc / 3;
    }
}
