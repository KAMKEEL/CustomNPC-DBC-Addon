package kamkeel.npcdbc.client.sound;

import kamkeel.npcdbc.constants.enums.EnumPlayerAuraTypes;
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
        volume = 0.01f;
        maxVolume = 0.5f;
        fadeIn = true;
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
            aura = display.getToggledAura();
        } else if (entity instanceof EntityPlayer) {
            DBCData dbcData = DBCData.get((EntityPlayer) entity);
            aura = dbcData.getToggledAura();
        }

        if (aura == null)
            fadeOut = true;


    }

    public static void play(Entity entity, Aura aura, boolean isTransforming) {
        if (entity == null || aura == null)
            return;

        String sound = "jinryuudragonbc:DBC.aura";
        if (aura.display.type == EnumPlayerAuraTypes.SaiyanGod)
            sound = "jinryuudragonbc:1610.aurag";
        else if (aura.display.type == EnumPlayerAuraTypes.UI)
            sound = "jinryuudragonbc:DBC5.aura_ui";
        else if (aura.display.type == EnumPlayerAuraTypes.GoD)
            sound = "jinryuudragonbc:DBC5.aura_destroyer";
        else if (EnumPlayerAuraTypes.isBlue(aura.display.type))
            if (aura.display.hasKaiokenAura)
                sound = "jinryuudragonbc:1610.aurabk";
            else
                sound = "jinryuudragonbc:1610.aurab";

        if (aura.display.hasSound())
            sound = aura.display.auraSound;

        if (!SoundHandler.isPlayingSound(entity, sound)) {
            AuraSound auraSound = new AuraSound(sound, entity);

            if (isTransforming)
                auraSound.setVolume(0.2f);


            auraSound.setRepeat(true);
            auraSound.play(false);
        }
    }
}
