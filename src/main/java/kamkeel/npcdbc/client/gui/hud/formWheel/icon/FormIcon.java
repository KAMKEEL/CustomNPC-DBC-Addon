package kamkeel.npcdbc.client.gui.hud.formWheel.icon;

import JinRyuu.JRMCore.server.config.dbc.JGConfigUltraInstinct;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.gui.hud.formWheel.HUDFormWheel;
import kamkeel.npcdbc.client.utils.Color;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes2D;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes3D;
import kamkeel.npcdbc.data.aura.AuraDisplay;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.npc.KiWeaponData;
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
    public float width = 16, height = 16;

    private AuraIconType aura;
    private BodyIconType body;
    private HairIconType hair;


    public FormIcon(HUDFormWheel parent, Form formToCopy){
        this.parent = parent;
        aura = AuraIconType.DEFAULT;
        body = BodyIconType.HUMANOID;
        hair = HairIconType.HUMANOID;
        auraColor = new Color(0xADD8E6, 1);
        hairColor = new Color(0, 1);

        FormDisplay display = formToCopy.display;
        int race = parent.dbcData.Race;

        boolean isEligible = formToCopy.raceEligible(parent.player);

        boolean isHumanoid = race == DBCRace.HUMAN || race == DBCRace.ALL_SAIYANS || race == DBCRace.HALFSAIYAN || race == DBCRace.SAIYAN;
        if(isHumanoid && isEligible){
            boolean buff = false;
            if(display.formSize >= 1.1f)
                buff = true;

            if(display.hairColor != -1){
                hairColor = new Color(display.hairColor, 1);
            }
            if (!display.hairType.isEmpty()) {

                if (display.hairType.toLowerCase().contains("ssj3")) {
                    hair = buff ? HairIconType.SS3_BUFF : HairIconType.SS3;
                    body = buff ? BodyIconType.BUFF : BodyIconType.HUMANOID;
                }else if (display.hairType.toLowerCase().contains("ssj4")) {
                    hair = HairIconType.SS4;
                    body = BodyIconType.SS4;
                }else if (display.hairType.toLowerCase().contains("oozaru")) {
                    hair = HairIconType.OOZARU;
                    body = BodyIconType.OOZARU;
                    hairColor = new Color(display.furColor == -1 ? 0x6F4E37 : display.furColor, 1);
                }else {
                    body = buff ? BodyIconType.BUFF : BodyIconType.HUMANOID;
                }
            }
        } else if(race == DBCRace.ARCOSIAN && isEligible){
            auraColor = new Color(0xb70d0e, 1);
            hairColor = new Color(display.bodyC2 == -1 ? 0x992b95 : display.bodyC2, 1);
            if (!display.bodyType.isEmpty()) {
                if (display.bodyType.toLowerCase().contains("first")) {
                    body = BodyIconType.ARC_FIRST;
                    hair = HairIconType.ARC_FIRST;
                }else if (display.bodyType.toLowerCase().contains("second")) {
                    body = BodyIconType.ARC_SECOND;
                    hair = HairIconType.ARC_SECOND;
                }else if (display.bodyType.toLowerCase().contains("third")) {
                    body = BodyIconType.ARC_THIRD;
                    hair = HairIconType.ARC_THIRD;
                }else if (display.bodyType.toLowerCase().contains("final")) {
                    body = BodyIconType.ARC_FOURTH;
                    hair = HairIconType.ARC_FOURTH;
                }else if (display.bodyType.toLowerCase().contains("ultimate")) {
                    body = BodyIconType.ARC_FIFTH;
                    hair = HairIconType.ARC_FIFTH;
                }
            }else{
                body = BodyIconType.ARC_FIRST;
                hair = HairIconType.ARC_FIRST;
            }
        }else if(race == DBCRace.NAMEKIAN && isEligible){
            hair = HairIconType.NAMEK;
            body = BodyIconType.NAMEK;
            hairColor = new Color(display.bodyCM == -1 ? 0x6DB43A : display.bodyCM, 1);
        }else if(race == DBCRace.MAJIN && isEligible){
            if(display.formSize <= 0.9f){
                hair = HairIconType.MAJIN_PURE;
                body = BodyIconType.MAJIN_PURE;
            }else{
                hair = HairIconType.MAJIN;
                body = BodyIconType.MAJIN;
            }
            hairColor = new Color(display.bodyCM == -1 ? 0xfaaacc : display.bodyCM, 1);
        }else{
            hair = HairIconType.NONE;
            if(display.formSize >= 1.1f)
                body = BodyIconType.BUFF;
        }

        int formAuraColor = formToCopy.display.auraColor;
        if(display.hasAura())
            setAuraType((AuraDisplay) display.getAura().getDisplay(), formAuraColor);
        else if(formAuraColor != -1)
            auraColor = new Color(formAuraColor, 1);
    }
    public FormIcon(HUDFormWheel parent, int DBCFormID){
        this.parent = parent;
        boolean isLegendary = parent.dbcData.isForm(DBCForm.Legendary);
        boolean isDivine = parent.dbcData.isForm(DBCForm.Divine);

        if(DBCFormID >= 20){
            setNonRacialForms(DBCFormID);
            return;
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
        GL11.glPushMatrix();

        // TODO Replace textures
        //      The current textures are too small (only take up 1/3rd of the space)
        //      So I have to scale up the result.
        GL11.glScalef(3.5f, 3.5f, 3.5f);
        if (aura != AuraIconType.NONE) {
            renderEngine.bindTexture(auraIcon);
            auraColor.glColor();
            drawTexturedRect(-width/2, -height/2, width/2, height/2, aura.ordinal(), AuraIconType.values().length-1);
        }

        if (body != BodyIconType.NONE) {
            renderEngine.bindTexture(bodyIcon);
            GL11.glColor4f(1, 1, 1, 1);
            drawTexturedRect(-width/2, -height/2, width/2, height/2, body.ordinal(), BodyIconType.values().length-1);
        }

        if(hair != HairIconType.NONE){
            renderEngine.bindTexture(hairIcon);
            hairColor.glColor();
            drawTexturedRect(-width/2, -height/2, width/2, height/2, hair.ordinal(), HairIconType.values().length-1);
        }
        GL11.glPopMatrix();
    }

    private void setAuraType(AuraDisplay auraDisplay, int formAuraColor){
        auraColor = new Color(0xADD8E6, 1);
        if(formAuraColor == -1){
            auraColor = new Color(KiWeaponData.getColorByAuraType(auraDisplay), 1);
        }else{
            auraColor = new Color(formAuraColor, 1);
        }
        aura = AuraIconType.getAuraType(auraDisplay);

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
        aura = AuraIconType.DEFAULT;
        body = BodyIconType.HUMANOID;
        hair = HairIconType.NONE;
        auraColor = new Color(0xADD8E6, 1);
        hairColor = new Color(0, 1);


        if(state >= DBCForm.UltraInstinct){
            hair = HairIconType.HUMANOID;
            aura = AuraIconType.UI;
            auraColor = new Color(0xFFFFFF, 1);

            int index = state - DBCForm.UltraInstinct;
            boolean[] whiteHair = JGConfigUltraInstinct.CONFIG_UI_HAIR_WHITE;

            if(whiteHair.length > index && whiteHair[index]){
                hairColor = new Color(0xD0D0D0, 1);
            }

            return;
        }



        switch(state){
            case DBCForm.Kaioken:
            case DBCForm.Kaioken2:
            case DBCForm.Kaioken3:
            case DBCForm.Kaioken4:
            case DBCForm.Kaioken5:
            case DBCForm.Kaioken6:
                auraColor = new Color(0xb62049, 1).lerpRGBA(new Color(0xFF0000, 1), (state+0.5f-30f) / 6f);
                break;
            case DBCForm.Mystic:
                auraColor = new Color(0xFFFFFF, 1);
                break;
            case DBCForm.GodOfDestruction:
                aura = AuraIconType.DESTROYER;
                auraColor = new Color(0x902377, 1);
                break;
        }
    }

    private void setHumanForms(int state){
        hairColor = new Color(0, 1);
        auraColor = new Color(0xADD8E6, 1);
        body = BodyIconType.HUMANOID;
        hair = HairIconType.NONE;
        aura = AuraIconType.DEFAULT;
        if(state == DBCForm.HumanBuffed){
            auraColor = new Color(0xFCE892, 1);
            body = BodyIconType.BUFF;
        }
        if(state == DBCForm.HumanGod){
            aura = AuraIconType.GOD;
            auraColor = new Color(0xFFC125, 1);
        }
    }

    private void setSaiyanForms(int state, boolean isLegendary, boolean isDivine){
        Color basicSSJAuraColor = new Color(isLegendary ? 0x99FF66 : 0xFCE892, 1);
        Color basicSSJHairColor = basicSSJAuraColor.lerpRGBA(new Color(0xFFFF00, 1), 0.3f);

        Color ssbAuraColor = new Color(isDivine ? 0x730015 : 0x2ACDEE, 1);
        Color ssbHairColor = isDivine ? new Color(0xE4A8AE, 1) : ssbAuraColor.lerpRGBA(new Color(0, 1), 0.2f);

        body = BodyIconType.HUMANOID;
        hair = HairIconType.HUMANOID;
        aura = AuraIconType.DEFAULT;
        auraColor = new Color(0xADD8E6, 1);
        hairColor = new Color(0, 1);
        switch(state){
            case DBCForm.SuperSaiyanG2:
            case DBCForm.SuperSaiyanG3:
                body = BodyIconType.BUFF;
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
                hair = HairIconType.SS3;
                body = BodyIconType.SS3;
                auraColor = basicSSJAuraColor;
                hairColor = basicSSJHairColor;
                break;
            case DBCForm.GreatApe:
                hair = HairIconType.OOZARU;
                body = BodyIconType.OOZARU;
                hairColor = new Color(0x42280E, 1);
                break;
            case DBCForm.SuperGreatApe:
                hair = HairIconType.OOZARU;
                body = BodyIconType.OOZARU;
                auraColor = basicSSJAuraColor;
                hairColor = basicSSJHairColor;
                break;
            case DBCForm.SuperSaiyanGod:
                aura = AuraIconType.GOD;
                auraColor = new Color(0xFFC125, 1);
                hairColor = new Color(0xD60D6B, 1);
                break;
            case DBCForm.SuperSaiyanBlue:
                aura = AuraIconType.GOD;
                auraColor = ssbAuraColor;
                hairColor = ssbHairColor;
                break;
            case DBCForm.BlueEvo:
                aura = AuraIconType.GOD;
                body = BodyIconType.BUFF;
                auraColor = isDivine ? ssbAuraColor.lerpRGBA(new Color(0xFC6C85, 1), 0.6f) : new Color(0x2233FF, 1);
                hairColor = isDivine ? ssbHairColor.lerpRGBA(new Color(0x090909, 1), 0.2f) : new Color(0x2233FF, 1).lerpRGBA(new Color(0, 1), 0.15f);
                break;
            case DBCForm.SuperSaiyan4:
                body = BodyIconType.SS4;
                hair = HairIconType.SS4;
                hairColor = new Color(0, 1);
                auraColor = basicSSJAuraColor;
                break;
        }

    }

    private void setNamekForms(int state){
        hairColor = new Color(0x6DB43A, 1);
        auraColor = new Color(0xFCE892, 1);
        aura = AuraIconType.DEFAULT;
        body = BodyIconType.NAMEK;
        hair = HairIconType.NAMEK;
        if(state == DBCForm.NamekGiant){
            auraColor = new Color(0xADD8E6, 1);
        }
        if(state == DBCForm.NamekGod){
            aura = AuraIconType.GOD;
            auraColor = new Color(0xFFC125, 1);
        }
    }

    private void setArcoForms(int state){
        hairColor = new Color(0x992b95, 1);
        auraColor = new Color(0xb70d0e, 1);
        aura = AuraIconType.DEFAULT;
        body = BodyIconType.ARC_FIRST;
        hair = HairIconType.ARC_FIRST;
        switch(state){
            case DBCForm.SecondForm:
                body = BodyIconType.ARC_SECOND;
                hair = HairIconType.ARC_SECOND;
                break;
            case DBCForm.ThirdForm:
                body = BodyIconType.ARC_THIRD;
                hair = HairIconType.ARC_THIRD;
                break;
            case DBCForm.FinalForm:
                body = BodyIconType.ARC_FOURTH;
                hair = HairIconType.ARC_FOURTH;
                break;
            case DBCForm.SuperForm:
                body = BodyIconType.ARC_FIFTH;
                hair = HairIconType.ARC_FIFTH;
                auraColor = new Color(0xADD8E6, 1);
                break;
            case DBCForm.UltimateForm:
                body = BodyIconType.ARC_FOURTH;
                hair = HairIconType.ARC_FOURTH;
                aura = AuraIconType.DESTROYER;
                auraColor = new Color(0xFFBF00, 1);
                break;
            case DBCForm.ArcoGod:
                body = BodyIconType.ARC_FOURTH;
                hair = HairIconType.ARC_FOURTH;
                aura = AuraIconType.GOD;
                auraColor = new Color(0xFFC125, 1);
                break;
        }

    }

    private void setMajinForms(int state){
        hairColor = new Color(0xfaaacc, 1);
        auraColor = new Color(0xADD8E6, 1);
        aura = AuraIconType.DEFAULT;
        body = BodyIconType.MAJIN;
        hair = HairIconType.MAJIN;
        switch(state){
            case DBCForm.MajinEvil:
                auraColor = new Color(0xa6a6a6, 1);
                hairColor = new Color(0xa6a6a6, 1);
                break;
            case DBCForm.MajinPure:
                auraColor = new Color(0xFFB6C1, 1);
                body = BodyIconType.MAJIN_PURE;
                hair = HairIconType.MAJIN_PURE;
                break;
            case DBCForm.MajinGod:
                aura = AuraIconType.GOD;
                auraColor = new Color(0xFFC125, 1);
                break;
        }

    }

    private enum AuraIconType {
        DEFAULT,
        GOD,
        DESTROYER,
        UI,

        NONE;

        static AuraIconType getAuraType(AuraDisplay display){
            String name = null;
            if(name == null && display.type != EnumAuraTypes3D.None){
                name = display.type.getName();
            }

            if(name == null && display.type2D != EnumAuraTypes2D.None){
                name = display.type2D.getName();
            }
            if(name == null)
                name = "";
            return getTypeByName(name);
        }
        private static AuraIconType getTypeByName(String name) {
            switch (name) {
                case "default":
                case "base":
                    return DEFAULT;
                case "ssgod":
                case "ssb":
                case "shinka":
                case "ssrose":
                case "ssroseevo":
                    return GOD;
                case "jiren":
                case "mui":
                case "ui":
                    return UI;
                case "ultimate":
                case "godofdestructiontoppo":
                case "godofdestruction":
                    return DESTROYER;
                default:
                    return NONE;
            }
        }
    }

    private enum BodyIconType {
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
        MAJIN_PURE,



        NONE
    }

    private enum HairIconType {
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
        MAJIN_PURE,



        NONE
    }
}
