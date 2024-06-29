package kamkeel.npcdbc;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;

public class NPCEventHandler {

    public void DBCAttackEvent(DBCPlayerEvent.DamagedEvent event) {
        if (event.player == null || event.player == null || event.player.worldObj.isRemote)
            return;

        Form form = DBCData.getForm(event.player);
        if (form != null) {
            float formLevel = PlayerDataUtil.getFormLevel(event.player);

            if (form.mastery.hasDamageNegation()) {
                float damageNegation = form.mastery.damageNegation * form.mastery.calculateMulti("damageNegation", formLevel);
                float newDamage = event.getDamage() * (100 - damageNegation) / 100;
                event.setDamage(newDamage);
            }

        }
    }
}
