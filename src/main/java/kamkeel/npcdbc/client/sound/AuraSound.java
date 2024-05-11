package kamkeel.npcdbc.client.sound;

import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixin.INPCDisplay;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.entity.EntityNPCInterface;

public class AuraSound extends Sound {

    public AuraSound(String soundDir, Entity entity) {
        super(soundDir, entity);
        range = 32;
    }

    @Override
    public String toString() {
        return entity.getEntityId() + ":AURA:" + soundDir;
    }


    @Override
    public void update() {
        super.update();

        Aura aura = null;
        if (entity instanceof EntityNPCInterface) {
            DBCDisplay display = ((INPCDisplay) ((EntityNPCInterface) entity).display).getDBCDisplay();
            aura = display.getAur();
        } else if (entity instanceof EntityPlayer) {
            DBCData dbcData = DBCData.get((EntityPlayer) entity);
            aura = dbcData.getAura();
        }

        if (aura == null)
            stop(false);


    }
}
