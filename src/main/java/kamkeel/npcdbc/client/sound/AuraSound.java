package kamkeel.npcdbc.client.sound;

import kamkeel.npcdbc.client.gui.global.auras.SubGuiAuraDisplay;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.data.SoundSource;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.entity.EntityNPCInterface;

public class AuraSound extends ClientSound {
    private final Aura aura;
    public boolean isKaiokenSound = false, isGUIAura;

    public AuraSound(Aura aura, SoundSource soundSource) {
        super(soundSource);
        volume = 0.01f;
        soundSource.maxVolume = 0.5f;
        soundSource.fadeIn = true;
        soundSource.range = 32;
        this.aura = aura;

        soundSource.onlyOneCanExist = true;
        soundSource.fadeFactor = 0.02f;
        soundSource.key += ":AURA";
    }

    @Override
    public void update() {
        super.update();

        // DBC Aura Logic
        if (!ConfigDBCClient.RevampAura) {
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

            if (aura == null) {
                soundSource.fadeFactor = isGUIAura ? 0.075f : soundSource.fadeFactor;
                soundSource.fadeOut = true;
            } else if (aura.id != this.aura.id || !isInKaioken && isKaiokenSound) {

                soundSource.fadeFactor = isGUIAura ? 0.075f : 0.1f;
                soundSource.fadeOut = true;
            }
        }
    }

    public float getXPosF() {
        if (isGUIAura)
            return (float) Minecraft.getMinecraft().thePlayer.posX;
        return this.xPosF;
    }

    public float getYPosF() {
        if (isGUIAura)
            return (float) Minecraft.getMinecraft().thePlayer.posY;
        return this.yPosF;
    }

    public float getZPosF() {
        if (isGUIAura)
            return (float) Minecraft.getMinecraft().thePlayer.posZ;
        return this.zPosF;
    }
    public static void play(Entity entity, Aura aura) {
        if (entity == null || aura == null || SubGuiAuraDisplay.useGUIAura)
            return;

        boolean isTransforming = false;

        if (entity instanceof EntityPlayer) {
            DBCData dbcData = DBCData.get((EntityPlayer) entity);
            isTransforming = dbcData.isTransforming();

        } else if (entity instanceof EntityNPCInterface) {
            DBCDisplay display = ((INPCDisplay) ((EntityNPCInterface) entity).display).getDBCDisplay();
            isTransforming = display.isTransforming;
        }


        String sound = aura.display.getFinalSound();
        String secondSound = aura.hasSecondaryAura() ? aura.getSecondaryAur().display.getFinalSound() : null;

        if (!SoundHandler.isPlayingSound(entity, sound)) {
            AuraSound auraSound = new AuraSound(aura, new SoundSource(sound, entity));
            if (isTransforming)
                auraSound.setVolume(0.2f);

            auraSound.setRepeat(true).play(false);
        }

        if (secondSound != null && !SoundHandler.isPlayingSound(entity, secondSound)) {
            AuraSound secondarySound = new AuraSound(aura, new SoundSource(secondSound, entity));
            if (isTransforming)
                secondarySound.setVolume(0.2f);
            secondarySound.setRepeat(true).play(false);
        }
    }
}
