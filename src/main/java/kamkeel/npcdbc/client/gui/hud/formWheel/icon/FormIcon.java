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

    public static final ResourceLocation bodyIcon = new ResourceLocation(PREFIX + "/body.png");
    public static final ResourceLocation hairIcon = new ResourceLocation(PREFIX + "/hairs.png");
    public static final ResourceLocation auraIcon = new ResourceLocation(PREFIX + "/auras.png");

    public HUDFormWheel parent;

    public Color hairColor = new Color(0, 1), auraColor = new Color(0xADD8E6, 1);
    public int width = 16, height = 16;

    private AuraIcon aura;
    private BodyIcon body;
    private HairIcon hair;


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
        if (aura != null) {
            renderEngine.bindTexture(auraIcon);
            auraColor.glColor();
            drawTexturedRect(-width/2, -height/2, width/2, height/2, aura.ordinal(), AuraIcon.values().length);
        }

        if (body != null) {
            renderEngine.bindTexture(bodyIcon);
            GL11.glColor4f(1, 1, 1, 1);
            drawTexturedRect(-width/2, -height/2, width/2, height/2, body.ordinal(), BodyIcon.values().length);
        }

        if(hair != null){
            renderEngine.bindTexture(hairIcon);
            hairColor.glColor();
            drawTexturedRect(-width/2, -height/2, width/2, height/2, hair.ordinal(), HairIcon.values().length);
        }
        GL11.glPopMatrix();
    }

    private void drawTexturedRect(float left, float top, float right, float bottom, int index, int maxIndex){
        if(maxIndex <= 0){
            maxIndex = 1;
        }
        if(index < 0){
            index = 0;
        }
        if(index >= maxIndex){
            index = maxIndex - 1;
        }
        float step = 1f / maxIndex;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(left, bottom, (double)this.zLevel, step*index, 1);
        tessellator.addVertexWithUV(right, bottom, (double)this.zLevel, step*(index+1), 1);
        tessellator.addVertexWithUV(right, top, (double)this.zLevel, step*(index+1), 0);
        tessellator.addVertexWithUV(left, top, (double)this.zLevel, step*index, 0);
        tessellator.draw();
    }

    private void setNonRacialForms(int state) {

    }

    private void setHumanForms(int state){
        hairColor = new Color(0, 1);
        auraColor = new Color(0xADD8E6, 1);
        body = BodyIcon.HUMANOID;
        hair = HairIcon.HUMANOID;
        aura = AuraIcon.DEFAULT;
        if(state == DBCForm.HumanBuffed){
            auraColor = new Color(0xFCE892, 1);
            body = BodyIcon.BUFF;
        }
        if(state == DBCForm.HumanGod){
            aura = AuraIcon.GOD;
            auraColor = new Color(0xFFC125, 1);
        }
    }
    private void setSaiyanForms(int state, boolean isLegendary, boolean isDivine){
        Color basicSSJAuraColor = new Color(isLegendary ? 0x99FF66 : 0xFCE892, 1);
        Color basicSSJHairColor = basicSSJAuraColor.lerpRGBA(new Color(0, 1), 0.2f);

        Color ssbAuraColor = new Color(isDivine ? 0x730015 : 0x2ACDEE, 1);
        Color ssbHairColor = isDivine ? new Color(0xE4A8AE, 1) : ssbAuraColor.lerpRGBA(new Color(0, 1), 0.2f);

        body = BodyIcon.HUMANOID;
        hair = HairIcon.HUMANOID;
        aura = AuraIcon.DEFAULT;
        auraColor = new Color(0xADD8E6, 1);
        hairColor = new Color(0, 1);
        switch(state){
            case DBCForm.SuperSaiyanG2:
            case DBCForm.SuperSaiyanG3:
                body = BodyIcon.BUFF;
                auraColor = basicSSJAuraColor;
                hairColor = basicSSJHairColor;
                break;
            case DBCForm.SuperSaiyan:
            case DBCForm.MasteredSuperSaiyan:
            case DBCForm.SuperSaiyan2:
                auraColor = basicSSJAuraColor;
                hairColor = basicSSJHairColor;
                break;
            case DBCForm.SuperSaiyan3:
                hair = HairIcon.SS3;
                body = BodyIcon.SS3;
                auraColor = basicSSJAuraColor;
                hairColor = basicSSJHairColor;
                break;
            case DBCForm.GreatApe:
                hair = HairIcon.OOZARU;
                body = BodyIcon.OOZARU;
                hairColor = new Color(0x42280E, 1);
                break;
            case DBCForm.SuperGreatApe:
                hair = HairIcon.OOZARU;
                body = BodyIcon.OOZARU;
                auraColor = basicSSJAuraColor;
                hairColor = basicSSJHairColor;
                break;
            case DBCForm.SuperSaiyanGod:
                aura = AuraIcon.GOD;
                auraColor = new Color(0xFFC125, 1);
                hairColor = new Color(0xD60D6B, 1);
                break;
            case DBCForm.SuperSaiyanBlue:
                aura = AuraIcon.GOD;
                auraColor = ssbAuraColor;
                hairColor = ssbHairColor;
                break;
            case DBCForm.BlueEvo:
                aura = AuraIcon.GOD;
                body = BodyIcon.BUFF;
                auraColor = isDivine ? ssbAuraColor.lerpRGBA(new Color(0xFC6C85, 1), 0.6f) : new Color(0x2233FF, 1);
                hairColor = isDivine ? ssbHairColor.lerpRGBA(new Color(0x090909, 1), 0.2f) : new Color(0x2233FF, 1).lerpRGBA(new Color(0, 1), 0.15f);
                break;
            case DBCForm.SuperSaiyan4:
                body = BodyIcon.SS4;
                hair = HairIcon.SS4;
                hairColor = new Color(0, 1);
                auraColor = basicSSJAuraColor;
                break;
        }

    }
    private void setNamekForms(int state){
        hairColor = new Color(0x6DB43A, 1);
        auraColor = new Color(0xADD8E6, 1);
        aura = AuraIcon.DEFAULT;
        body = BodyIcon.NAMEK;
        hair = HairIcon.NAMEK;
        if(state == DBCForm.NamekGiant){
            auraColor = new Color(0xFCE892, 1);
        }
        if(state == DBCForm.NamekGod){
            aura = AuraIcon.GOD;
            auraColor = new Color(0xFFC125, 1);
        }
    }
    private void setArcoForms(int state){

    }
    private void setMajinForms(int state){

    }

    private enum AuraIcon {
        DEFAULT,
        GOD,
        DESTROYER,
        UI
    }
    private enum BodyIcon {
        HUMANOID,
        SS4,
        SS3,
        BUFF,
        OOZARU,

        ARC_FIRST,
        ARC_SECOND,
        ARC_THIRD,
        ARC_FOURTH,
        ARC_FIFTH,

        NAMEK,

        MAJIN,
        MAJIN_PURE
    }
    private enum HairIcon {
        HUMANOID,
        SS4,
        SS3,
        SS3_BUFF,
        OOZARU,

        ARC_FIRST,
        ARC_SECOND,
        ARC_THIRD,
        ARC_FOURTH,
        ARC_FIFTH,

        NAMEK,

        MAJIN,
        MAJIN_PURE
    }
}
