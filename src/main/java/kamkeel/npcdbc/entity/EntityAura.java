package kamkeel.npcdbc.entity;

import JinRyuu.DragonBC.common.Npcs.EntityAuraRing;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.client.sound.AuraSound;
import kamkeel.npcdbc.client.sound.SoundHandler;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.enums.EnumPlayerAuraTypes;
import kamkeel.npcdbc.controllers.EntityLightController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.aura.AuraDisplay;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixin.INPCDisplay;
import kamkeel.npcdbc.util.PlayerDataUtil;
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

    public EntityLightController light;


    public boolean isKaioken;
    public boolean isTransforming;
    public boolean isCharging;
    public boolean isPlayer;

    public int color1 = -1, color2 = -1, color3 = -1, speed = 20, renderPass;
    public String tex1 = "jinryuudragonbc:aura.png", tex2 = "", tex3 = "";
    public float alpha, maxAlpha = 0.2f, size = 1f;

    public boolean isInner, doneUsing, fadeOut = false, fadeIn = true;
    public float fadeFactor = 0.005f;

    public EntityAura(Entity entity, Aura aura) {
        super(entity.worldObj);
        this.entity = entity;
        this.aura = aura;

        if (entity instanceof EntityPlayer) {
            dbcData = DBCData.get((EntityPlayer) entity);
            isPlayer = true;
            dbcData.auraEntity = this;
        } else if (entity instanceof EntityNPCInterface)
            display = ((INPCDisplay) ((EntityNPCInterface) entity).display).getDBCDisplay();

        color1 = isPlayer ? dbcData.AuraColor > 0 ? dbcData.AuraColor : JRMCoreH.Algnmnt_rc(dbcData.Alignment) : 11075583; //alignment color

        AuraDisplay display = aura.display;
        int mimicColor = EnumPlayerAuraTypes.getManualAuraColor(display.type);
        if (mimicColor != -1)
            color1 = mimicColor;

        String auraDir = "jinryuudragonbc:";
        if (display.type == EnumPlayerAuraTypes.SaiyanGod) {
            maxAlpha = 0.2f;
            tex1 = auraDir + "aurai.png";
            tex2 = auraDir + "auraj.png";
            color2 = 16747301;
        } else if (display.type == EnumPlayerAuraTypes.SaiyanBlue) {
            // speed = 40;
            maxAlpha = 0.5F;
            tex1 = auraDir + "aurag.png";
            tex3 = auraDir + "auragb.png";
            color3 = 15727354;
        } else if (display.type == EnumPlayerAuraTypes.SaiyanBlueEvo) {
            // speed = 40;
            maxAlpha = 0.5F;
            tex1 = auraDir + "aurag.png";
            tex3 = auraDir + "auragb.png";
            color3 = 12310271;
        } else if (display.type == EnumPlayerAuraTypes.SaiyanRose) {
            // speed = 30;
            maxAlpha = 0.2F;
            tex1 = auraDir + "aurai.png";
            tex2 = auraDir + "auraj.png";
            color2 = 7872713;
        } else if (display.type == EnumPlayerAuraTypes.SaiyanRoseEvo) {
            //speed = 30;
            maxAlpha = 0.2F;
            tex1 = auraDir + "aurai.png";
            tex2 = auraDir + "auraj.png";
            color2 = 8592109;
        } else if (display.type == EnumPlayerAuraTypes.UI) {
            // speed = 100;
            maxAlpha = 0.15F;
            color1 = 15790320;
            tex1 = auraDir + "auras.png";
            color3 = 4746495;
            tex3 = auraDir + "auragb.png";
        } else if (display.type == EnumPlayerAuraTypes.GoD) {
            // speed = 100;
            maxAlpha = 0.2F;
            tex1 = auraDir + "aurag.png";
            tex3 = auraDir + "auragb.png";
            color2 = 12464847;
        } else if (display.type == EnumPlayerAuraTypes.UltimateArco) {
            maxAlpha = 0.5F;
            tex1 = auraDir + "aurau.png";
            tex2 = auraDir + "aurau2.png";
            color2 = 16776724;
        }

        if (isPlayer && dbcData.State > 0)//vanilla DBC form colors
            color1 = dbcData.getDBCColor();

        if (display.hasColor("color1")) //IAura color
            color1 = display.color1;

        Form form = PlayerDataUtil.getForm(entity);
        if (form != null && form.display.hasColor("aura")) //IForm color
            color1 = form.display.auraColor;


        if (display.hasColor("color2"))
            color2 = display.color2;
        if (display.hasColor("color3"))
            color3 = display.color3;


        if (display.hasAlpha("aura"))
            maxAlpha = (float) display.alpha / 255;
        maxAlpha = 0.3f;
        fadeFactor *= 10;

        alpha = fadeFactor;

        if (display.hasSpeed())
            speed = (int) display.speed;
        light = new EntityLightController(entity);
        setPositionAndRotation(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);


    }

    protected void entityInit() {
        ignoreFrustumCheck = true;
        renderPass = 1;

    }

    public void onUpdate() {
        light.onUpdate();
       // light.addLitBlockUnder();
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

        if (entity == null || currentAura == null || aura != currentAura)
            fadeOut = true;

        if (fadeIn && !fadeOut)
            if (alpha < maxAlpha)
                alpha = Math.min(alpha + fadeFactor, maxAlpha);


        if (fadeOut) {
            alpha -= fadeFactor;
            if (alpha <= 0)
                setDead();
        }
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
