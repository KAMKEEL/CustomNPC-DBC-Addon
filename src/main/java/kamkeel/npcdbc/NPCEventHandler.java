package kamkeel.npcdbc;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import kamkeel.npcdbc.combatmode.Dodge;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Random;

public class NPCEventHandler {

    @SubscribeEvent
    public void DBCAttackEvent(DBCPlayerEvent.DamagedEvent event) {
        if (event.player == null || event.player.getMCEntity() == null || event.player.getMCEntity().worldObj.isRemote)
            return;

        Form form = DBCData.getForm((EntityPlayer) event.player.getMCEntity());
        if (form != null) {
            PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo((EntityPlayer) event.player.getMCEntity());
            if (form.mastery.hasDodge()) {
                Random rand = new Random();
                float dodgeChance = form.mastery.dodgeChance * form.mastery.calculateMulti("dodge", formData.getCurrentLevel());
                if (dodgeChance >= rand.nextInt(100)) {
                    if (Dodge.dodge((Entity) event.player, event.damageSource.getMCDamageSource().getEntity())) {
                        event.setCanceled(true);
                        return;
                    }
                }
            }
            if (form.mastery.hasDamageNegation()) {
                float damageNegation = form.mastery.damageNegation * form.mastery.calculateMulti("damageNegation", formData.getCurrentLevel());
                float newDamage = event.getDamage() * (100 - damageNegation) / 100;
                event.setDamage(newDamage);
            }

        }
    }
}
