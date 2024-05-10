package kamkeel.npcdbc.client.sound;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixin.INPCDisplay;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AuraSound extends Sound {
    public AuraSound(String soundDir) {
        super(soundDir);
    }

    public AuraSound(String soundDir, Entity entity) {
        super(soundDir, entity);
    }

    public void update() {
        super.update();
        boolean auraOn = true;
        if (entity instanceof EntityNPCInterface) {
            DBCDisplay display = ((INPCDisplay) ((EntityNPCInterface) entity).display).getDBCDisplay();
            if (!display.auraOn)
                auraOn = false;

        } else if (entity instanceof EntityPlayer) {
            DBCData dbcData = DBCData.get((EntityPlayer) entity);
            if (!dbcData.isDBCAuraOn())
                auraOn = false;
        }
        if (!auraOn) {
            stop(false);
        }
    }
}
