package kamkeel.npcdbc.entity;

import JinRyuu.DragonBC.common.Npcs.EntityAuraRing;
import kamkeel.npcdbc.client.sound.AuraSound;
import kamkeel.npcdbc.client.sound.SoundHandler;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixin.INPCDisplay;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAura extends Entity {

    public final Entity entity;
    public final Aura aura;
    public DBCData dbcData = null;
    DBCDisplay display = null;

    public EntityAura parent;

    public EntityAura secondaryAura;
    public EntityAura kaioken;
    public AuraSound sound;

    public boolean isKaioken;
    public boolean isTransforming;
    public boolean isCharging;
    public boolean isPlayer;

    public int speed = -1, renderPass;

    public EntityAura(Entity entity, Aura aura) {
        super(entity.worldObj);
        this.entity = entity;
        this.aura = aura;
        //  height = 0;
        ignoreFrustumCheck = true;
        /// boundingBox.setBB(entity.boundingBox); 
        renderPass = 1;
        if (entity instanceof EntityPlayer) {
            dbcData = DBCData.get((EntityPlayer) entity);
            isPlayer = true;
            dbcData.auraEntity = this;
        } else if (entity instanceof EntityNPCInterface)
            display = ((INPCDisplay) ((EntityNPCInterface) entity).display).getDBCDisplay();


        setPositionAndRotation(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
    }


    public void onUpdate() {

        Aura currentAura = null;

        if (isPlayer) {
            isTransforming = dbcData.isTransforming();
            isCharging = dbcData.containsSE(4) || isTransforming;
            currentAura = dbcData.getToggledAura();

        } else {
            isTransforming = display.isTransforming;
            isCharging = isTransforming;
            currentAura = display.getToggledAura();
        }

        int age = ticksExisted % speed+1;

        if (entity == null || currentAura == null || ticksExisted >= speed && speed != -1)
            setDead();
        //if ((ticksExisted) % speed == speed - 1)
               

        setPositionAndRotation(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);

    }

    public boolean shouldRenderInPass(int pass) {
        return pass == renderPass;
    }

    public void setDead() {
        super.setDead();
        if (isPlayer && dbcData.auraEntity == this)
            dbcData.auraEntity = null;

    }

    protected void entityInit() {

    }


    protected void readEntityFromNBT(NBTTagCompound tagCompund) {

    }

    protected void writeEntityToNBT(NBTTagCompound tagCompound) {

    }

    public void playSound() {
        if (entity == null || aura == null)
            return;


        String sound = aura.display.getFinalSound();
        String secondSound = aura.hasSecondaryAura() ? aura.getSecondaryAur().display.getFinalSound() : null;

        if (sound != null && !SoundHandler.isPlayingSound(entity, sound)) {
            AuraSound auraSound = new AuraSound(aura, sound, entity);
            if (isTransforming)
                auraSound.setVolume(0.2f);

            auraSound.isKaiokenSound = isKaioken;
            auraSound.setRepeat(true).play(false);
        }

        if (secondSound != null && !SoundHandler.isPlayingSound(entity, secondSound)) {
            AuraSound secondarySound = new AuraSound(aura, secondSound, entity);
            if (isTransforming)
                secondarySound.setVolume(0.2f);

            secondarySound.setRepeat(true).play(false);
        }

        if (!dbcData.isForm(DBCForm.Kaioken) || !aura.display.hasKaiokenAura)
            return;

        String kkSound = aura.display.getFinalKKSound();
        if (kkSound != null && !SoundHandler.isPlayingSound(dbcData.player, kkSound)) {
            AuraSound kaiokenSound = new AuraSound(aura, kkSound, dbcData.player);

            kaiokenSound.isKaiokenSound = true;
            kaiokenSound.setRepeat(true).play(false);
        }

    }

    public void spawn() {
        entity.worldObj.spawnEntityInWorld(this);
        // playSound();
    }

    public EntityAuraRing spawnAuraRing(Entity entity, int color) {
        if (entity.ticksExisted % 20 != 0)
            return null;

        boolean isPlayer = entity instanceof EntityPlayer;
        EntityAuraRing ring = new EntityAuraRing(entity.worldObj, isPlayer ? entity.getCommandSenderName() : Utility.getEntityID(entity), color, 0, 0, 0);

        entity.worldObj.spawnEntityInWorld(ring);
        return ring;

    }
}
