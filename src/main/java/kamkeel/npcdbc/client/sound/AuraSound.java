package kamkeel.npcdbc.client.sound;

import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixin.INPCDisplay;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.entity.EntityNPCInterface;

public class AuraSound extends Sound {
    private final Aura aura;
    public boolean isKaiokenSound = false;

    public AuraSound(Aura aura, String soundDir, Entity entity) {
        super(soundDir, entity);
        volume = 0.01f;
        maxVolume = 0.5f;
        fadeIn = true;
        range = 32;
        this.aura = aura;

        onlyOneCanExist = true;
    }

    @Override
    public String toString() {
        return entity.getEntityId() + ":AURA:" + soundDir;
    }


    @Override
    public void update() {
        super.update();

        Aura aura = null;
        boolean isInKaioken = false;
        if (entity instanceof EntityNPCInterface) {
            DBCDisplay display = ((INPCDisplay) ((EntityNPCInterface) entity).display).getDBCDisplay();
            aura = display.getToggledAura();
        } else if (entity instanceof EntityPlayer) {
            DBCData dbcData = DBCData.get((EntityPlayer) entity);
            aura = dbcData.getToggledAura();
            isInKaioken = dbcData.isForm(DBCForm.Kaioken);

        }

        if (aura != this.aura || !isInKaioken && isKaiokenSound) {
            if (aura != null)
                fadeFactor = 0.1f;
            fadeOut = true;
        }


    }

    public static void play(Entity entity, Aura aura) {
        if (entity == null || aura == null)
            return;

        boolean isTransforming = false;
        boolean isInKaioken = false;

        if (entity instanceof EntityPlayer) {
            DBCData dbcData = DBCData.get((EntityPlayer) entity);
            isTransforming = dbcData.isTransforming();
            isInKaioken = dbcData.isForm(DBCForm.Kaioken);

        } else if (entity instanceof EntityNPCInterface) {
            DBCDisplay display = ((INPCDisplay) ((EntityNPCInterface) entity).display).getDBCDisplay();
            isTransforming = display.isTransforming;
        }


        String sound = aura.display.getFinalSound();
        String kkSound = isInKaioken ? aura.display.getFinalKKSound() : null;
        String secondSound = aura.hasSecondaryAura() ? aura.getSecondaryAur().display.getFinalSound() : null;

        if (!SoundHandler.isPlayingSound(entity, sound)) {
            AuraSound auraSound = new AuraSound(aura, sound, entity);
            if (isTransforming)
                auraSound.setVolume(0.2f);

            auraSound.setRepeat(true).play(false);
        }
        if (secondSound != null && !SoundHandler.isPlayingSound(entity, secondSound)) {
            AuraSound secondarySound = new AuraSound(aura, secondSound, entity);
            if (isTransforming)
                secondarySound.setVolume(0.2f);
            secondarySound.setRepeat(true).play(false);
        }

        if (kkSound != null && !SoundHandler.isPlayingSound(entity, kkSound)) {
            AuraSound kaiokenSound = new AuraSound(aura, kkSound, entity);

            kaiokenSound.isKaiokenSound = true;
            kaiokenSound.setRepeat(true).play(false);
        }
    }
}
