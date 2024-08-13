package kamkeel.npcdbc.client.gui.hud.formWheel.icon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.gui.hud.formWheel.HUDFormWheel;
import kamkeel.npcdbc.client.utils.Color;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.data.form.Form;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class FormIcon extends Gui {
    public static final String PREFIX = CustomNpcPlusDBC.ID + ":textures/gui/hud/formwheel/icon";

    public ResourceLocation bodyHairIcon = new ResourceLocation(PREFIX + "/humanoid/normal.png");
    public ResourceLocation auraIcon = new ResourceLocation(PREFIX + "/aura.png");
    public HUDFormWheel parent;

    public Color hairColor = new Color(0, 1), auraColor = new Color(0xADD8E6, 1);
    public int width = 16, height = 16;

    public FormIcon(HUDFormWheel parent, Form formToCopy){
        this.parent = parent;
    }
    public FormIcon(HUDFormWheel parent, int DBCFormID){
        this.parent = parent;
        boolean isLegendary = parent.dbcData.isForm(DBCForm.Legendary);
        boolean isDivine = parent.dbcData.isForm(DBCForm.Divine);

        if(DBCFormID >= 20){
            setNonRacialForms(DBCFormID);
        }

        switch(parent.dbcData.Race){
            case 0:
                setHumanForms(DBCFormID);
                break;
            case 1:
            case 2:
                setSaiyanForms(DBCFormID, isLegendary, isDivine);
                break;
            case 3:
                setNamekForms(DBCFormID);
                break;
            case 4:
                setArcoForms(DBCFormID);
                break;
            case 5:
                setMajinForms(DBCFormID);
                break;
        }

    }

    public void draw(){
        TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
//        drawRect(-width/2, -height/2, width/2, height/2, 0x55FF0000);
//        GL11.glEnable(GL11.GL_BLEND);
        GL11.glPushMatrix();

        // TODO Replace textures
        //      The current textures are too small (only take up 1/3rd of the size)
        //      So I have to scale up the result.
        GL11.glScalef(3, 3, 3);
        if (auraIcon != null) {
            renderEngine.bindTexture(auraIcon);
            auraColor.glColor();
            drawTexturedRect(-width/2, -height/2, width/2, height/2, 0, 1, 0, 1);
        }

        if (bodyHairIcon != null) {
            renderEngine.bindTexture(bodyHairIcon);
            GL11.glColor4f(1, 1, 1, 1);
            drawTexturedRect(-width/2, -height/2, width/2, height/2, 0, 1, 0, 0.5f);
            hairColor.glColor();
            drawTexturedRect(-width/2, -height/2, width/2, height/2, 0, 1, 0.5f, 1f);
        }
        GL11.glPopMatrix();
    }

    private void drawTexturedRect(float left, float top, float right, float bottom, float minU, float maxU, float minV, float maxV){
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(left, bottom, (double)this.zLevel, minU, maxV);
        tessellator.addVertexWithUV(right, bottom, (double)this.zLevel, maxU, maxV);
        tessellator.addVertexWithUV(right, top, (double)this.zLevel, maxU, minV);
        tessellator.addVertexWithUV(left, top, (double)this.zLevel, minU, minV);
        tessellator.draw();
    }

    private void setNonRacialForms(int state) {

    }

    private void setHumanForms(int state){
        hairColor = new Color(0, 1);
        auraColor = new Color(0xADD8E6, 1);
        auraIcon = new ResourceLocation(PREFIX + "/aura.png");
        bodyHairIcon = new ResourceLocation(PREFIX + "/humanoid/normal.png");
        if(state == DBCForm.HumanBuffed){
            auraColor = new Color(0xFCE892, 1);
            bodyHairIcon = new ResourceLocation(PREFIX + "/humanoid/buff.png");
        }
        if(state == DBCForm.HumanGod){
            auraIcon = new ResourceLocation(PREFIX + "/auraGod.png");
            auraColor = new Color(0xFFC125, 1);
        }
    }
    private void setSaiyanForms(int state, boolean isLegendary, boolean isDivine){
        Color basicSSJAuraColor = new Color(isLegendary ? 0x99FF66 : 0xFCE892, 1);
        Color basicSSJHairColor = basicSSJAuraColor.lerpRGBA(new Color(0, 1), 0.2f);

        Color ssbAuraColor = new Color(isDivine ? 0x730015 : 0x2ACDEE, 1);
        Color ssbHairColor = isDivine ? new Color(0xE4A8AE, 1) : ssbAuraColor.lerpRGBA(new Color(0, 1), 0.2f);

        bodyHairIcon = new ResourceLocation(PREFIX + "/humanoid/normal.png");
        auraIcon = new ResourceLocation(PREFIX + "/aura.png");
        auraColor = new Color(0xADD8E6, 1);
        hairColor = new Color(0, 1);
        switch(state){
            case DBCForm.SuperSaiyanG2:
            case DBCForm.SuperSaiyanG3:
                bodyHairIcon = new ResourceLocation(PREFIX + "/humanoid/buff.png");
                auraColor = basicSSJAuraColor;
                hairColor = basicSSJHairColor;
                break;
            case DBCForm.SuperSaiyan:
            case DBCForm.MasteredSuperSaiyan:
            case DBCForm.SuperSaiyan2:
            case DBCForm.SuperSaiyan3:
                auraColor = basicSSJAuraColor;
                hairColor = basicSSJHairColor;
                break;
            case DBCForm.GreatApe:
                bodyHairIcon = new ResourceLocation(PREFIX + "/humanoid/oozaru.png");
                hairColor = new Color(0x42280E, 1);
                break;
            case DBCForm.SuperGreatApe:
                bodyHairIcon = new ResourceLocation(PREFIX + "/humanoid/oozaru.png");
                auraColor = basicSSJAuraColor;
                hairColor = basicSSJHairColor;
                break;
            case DBCForm.SuperSaiyanGod:
                auraIcon = new ResourceLocation(PREFIX + "/auraGod.png");
                auraColor = new Color(0xFFC125, 1);
                hairColor = new Color(0xD60D6B, 1);
                break;
            case DBCForm.SuperSaiyanBlue:
                auraIcon = new ResourceLocation(PREFIX + "/auraGod.png");
                auraColor = ssbAuraColor;
                hairColor = ssbHairColor;
                break;
            case DBCForm.BlueEvo:
                auraIcon = new ResourceLocation(PREFIX + "/auraGod.png");
                bodyHairIcon = new ResourceLocation(PREFIX + "/humanoid/buff.png");
                auraColor = isDivine ? ssbAuraColor.lerpRGBA(new Color(0xFC6C85, 1), 0.6f) : new Color(0x2233FF, 1);
                hairColor = isDivine ? ssbHairColor.lerpRGBA(new Color(0x090909, 1), 0.2f) : new Color(0x2233FF, 1).lerpRGBA(new Color(0, 1), 0.15f);
                break;
            case DBCForm.SuperSaiyan4:
                bodyHairIcon = new ResourceLocation(PREFIX + "/humanoid/ss4.png");
                hairColor = new Color(0, 1);
                auraColor = basicSSJAuraColor;
                break;
        }

    }
    private void setNamekForms(int state){
        hairColor = new Color(0x6DB43A, 1);
        auraColor = new Color(0xADD8E6, 1);
        auraIcon = new ResourceLocation(PREFIX + "/aura.png");
        bodyHairIcon = new ResourceLocation(PREFIX + "/namek/body.png");
        if(state == DBCForm.NamekGiant){
            auraColor = new Color(0xFCE892, 1);
        }
        if(state == DBCForm.NamekGod){
            auraIcon = new ResourceLocation(PREFIX + "/auraGod.png");
            auraColor = new Color(0xFFC125, 1);
        }
    }
    private void setArcoForms(int state){

    }
    private void setMajinForms(int state){

    }
}
