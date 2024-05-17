package kamkeel.npcdbc.entity;

import JinRyuu.DragonBC.common.Npcs.EntityAuraRing;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.EntityCusPar;
import kamkeel.npcdbc.client.render.AuraRenderer;
import kamkeel.npcdbc.client.sound.AuraSound;
import kamkeel.npcdbc.client.sound.SoundHandler;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
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
import net.minecraft.client.entity.EntityPlayerSP;
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

    public int color1 = -1, color2 = -1, color3 = -1, speed = 10, renderPass;
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
            maxAlpha = 0.5F;
            tex1 = auraDir + "aurag.png";
            tex3 = auraDir + "auragb.png";
            color3 = 15727354;
        } else if (display.type == EnumPlayerAuraTypes.SaiyanBlueEvo) {
            maxAlpha = 0.5F;
            tex1 = auraDir + "aurag.png";
            tex3 = auraDir + "auragb.png";
            color3 = 12310271;
            renderPass = 1;
        } else if (display.type == EnumPlayerAuraTypes.SaiyanRose) {
            maxAlpha = 0.2F;
            tex1 = auraDir + "aurai.png";
            tex2 = auraDir + "auraj.png";
            color2 = 7872713;
        } else if (display.type == EnumPlayerAuraTypes.SaiyanRoseEvo) {
            maxAlpha = 0.2F;
            tex1 = auraDir + "aurai.png";
            tex2 = auraDir + "auraj.png";
            color2 = 8592109;
        } else if (display.type == EnumPlayerAuraTypes.UI) {
            maxAlpha = 0.15F;
            color1 = 15790320;
            tex1 = auraDir + "auras.png";
            color3 = 4746495;
            tex3 = auraDir + "auragb.png";
        } else if (display.type == EnumPlayerAuraTypes.GoD) {
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
        maxAlpha = 0.05f;


        if (display.hasSpeed())
            speed = (int) display.speed;
        light = new EntityLightController(entity);

        setPositionAndRotation(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
    }

    protected void entityInit() {
        ignoreFrustumCheck = true;
        renderPass = 1;

    }

    public void updateColor() {
        color1 = isPlayer ? dbcData.AuraColor > 0 ? dbcData.AuraColor : JRMCoreH.Algnmnt_rc(dbcData.Alignment) : 11075583; //alignment color

        if (isPlayer && dbcData.State > 0)//vanilla DBC form colors
            color1 = dbcData.getDBCColor();

        Form form = PlayerDataUtil.getForm(entity);
        if (form != null && form.display.hasColor("aura")) //IForm color
            color1 = form.display.auraColor;
    }

    public void onUpdate() {

        Aura currentAura = PlayerDataUtil.getToggledAura(entity);
        if (entity == null || currentAura == null || aura != currentAura) //aura death condition
            fadeOut = true;
        
        light.onUpdate();
        // light.addLitBlockUnder();
        if (isPlayer) {
            isTransforming = dbcData.isTransforming();
            isCharging = dbcData.containsSE(4) || isTransforming;

        } else {
            isTransforming = display.isTransforming;
            isCharging = isTransforming;
        }
        updateColor();
        
        if (fadeIn && !fadeOut)
            if (alpha < maxAlpha)
                alpha = Math.min(alpha + fadeFactor, maxAlpha);

        if (fadeOut) {
            alpha -= fadeFactor;
            if (alpha <= 0)
                setDead();
        }
        double posXOth = entity.posX;
        double posYOth = entity.posY + (double) (entity instanceof EntityPlayerSP ?2 : 0.0F);
        double posZOth = entity.posZ;
        float red = this.alpha;
        float green = 1.0F;
        float blue = (float) (this.color1 >> 16 & 255) / 255.0F;
        float red2 = (float) (this.color1 >> 8 & 255) / 255.0F;
        float green2 = (float) (this.color1 & 255) / 255.0F;
        red = green * blue;
        green = green * red2;
        blue = green * green2;
        if (red > 1.0F) {
            red = 1.0F;
        }

        if (green > 1.0F) {
            green = 1.0F;
        }

        if (blue > 1.0F) {
            blue = 1.0F;
        }

        for (float gh = 0; gh < 5; ++gh) {
          //  float life = 0.8F * entity.height;
            float extra_scale = 1.0F + (entity.height > 2.1F ? entity.height / 2.0F : 0.0F) / 5.0F;
            float life = entity.width * 2.20F;
            double x = (Math.random() * 1.0 - 0.5) * (double) (life * 1.3F);
            double y = Math.random() * (double) (entity.height * 1.4F) - (double) (entity.height / 2.0F) - 0.30000001192092896 + 0.5;
            double z = (Math.random() * 1.0 - 0.5) * (double) (life * 1.3F);
            double motx = Math.random() * 0.019999999552965164 - 0.009999999776482582;
            double moty = (Math.random() * 0.8999999761581421 + 0.8999999761581421) * (double) (life * extra_scale) * 0.07;
            double motz = Math.random() * 0.019999999552965164 - 0.009999999776482582;
            Entity par = new EntityCusPar("jinryuumodscore:bens_particles.png", entity.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, motx, moty, motz, 0.0F, (int) (Math.random() * 3.0) + 32, 8, 3, 32, false, 0.0F, false, 0.0F, 1, "", (int) (30.0F * life * 0.5F), 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * extra_scale, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * extra_scale, 0.2F * life * extra_scale, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.08F, false, -1, true, entity);
            entity.worldObj.spawnEntityInWorld(par);
            par = new EntityCusPar("jinryuudragonbc:bens_particles.png", entity.worldObj, 0.2F, 0.2F, posXOth, posYOth, posZOth, x, y, z, motx, moty, motz, 0.0F, (int) (Math.random() * 8.0) + 32, 32, 8, 32, false, 0.0F, false, 0.0F, 1, "", (int) (30.0F * life * 0.5F), 2, ((float) (Math.random() * 0.029999999329447746) + 0.03F) * life * extra_scale, ((float) (Math.random() * 0.009999999776482582) + 0.02F) * life * extra_scale, 0.1F * life * extra_scale, 0, red, green, blue, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 2, 0.0F, 0.0F, 0.4F, 0.45F, 0.08F, false, -1, true, entity);
            entity.worldObj.spawnEntityInWorld(par);
        }
        setPositionAndRotation(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
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


    public EntityAuraRing spawnAuraRing(Entity entity, int color) {
        if (entity.ticksExisted % 20 != 0)
            return null;

        boolean isPlayer = entity instanceof EntityPlayer;
        EntityAuraRing ring = new EntityAuraRing(entity.worldObj, isPlayer ? entity.getCommandSenderName() : Utility.getEntityID(entity), color, 0, 0, 0);

        entity.worldObj.spawnEntityInWorld(ring);
        return ring;

    }

    public boolean shouldRenderInPass(int pass) {
        return pass == renderPass;
    }

    public double getYOffset() { //for correctly scaling aura size
        float sizeFactor = size < 1.5 ? 1 : 1.2f;
        float offsetFactor = 0.1f;
        int race = dbcData.Race;
        int state = dbcData.State;

        if (state == DBCForm.Base)
            offsetFactor *= dbcData.Release * 0.075;

        if (race == DBCRace.SAIYAN || race == DBCRace.HALFSAIYAN) {
            if (state == DBCForm.SuperSaiyan3)
                offsetFactor = 0.02f;
            else if (state == DBCForm.SuperSaiyan4)
                offsetFactor = 0.0075f;
        }
        float stateFactor = AuraRenderer.getStateSizeFactor(dbcData.State, dbcData.Race) * offsetFactor;

        return 1.75f * size * sizeFactor + stateFactor;
    }

    public void spawn() {
        entity.worldObj.spawnEntityInWorld(this);
        playSound();
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
}
