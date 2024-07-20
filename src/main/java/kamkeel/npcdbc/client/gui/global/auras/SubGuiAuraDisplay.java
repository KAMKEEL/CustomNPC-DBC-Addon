package kamkeel.npcdbc.client.gui.global.auras;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes2D;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes3D;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.aura.AuraDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.client.gui.SubGuiColorSelector;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.util.ValueUtil;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class SubGuiAuraDisplay extends SubGuiInterface implements ISubGuiListener, GuiSelectionListener,ITextfieldListener
{
    private final GuiNPCManageAuras parent;
	public Aura aura;
    public AuraDisplay auraDisplay;
    public int lastColorClicked = 0;
    boolean showAura = false;
    public int auraTicks = 1;
    public EntityCustomNpc npc;
    private float rotation = 0.0F;
    private GuiNpcButton left;
    private GuiNpcButton right;
    private float zoomed = 60.0F;
    public int xOffset = 0;
    public int yOffset = 0;
    int selectedTab = 0;

	public SubGuiAuraDisplay(GuiNPCManageAuras parent, Aura aura)
	{
		this.aura = aura;
        this.auraDisplay = aura.display;
        this.parent = parent;

		setBackground("menubg.png");
		xSize = 360;
		ySize = 216;

        xOffset = -120;
        yOffset = -10;

        npc = new EntityCustomNpc(Minecraft.getMinecraft().theWorld);
        npc.display.texture = "customnpcs:textures/entity/humanmale/AnimationBody.png";
        ((INPCDisplay) npc.display).getDBCDisplay().enabled = true;
    }

    public void initGui()
    {
        super.initGui();
        int y = guiTop + 5;

        addButton(new GuiNpcButton(2000, guiLeft + 138, y, 69, 20, "display.general"));
        getButton(2000).enabled = selectedTab != 0;

        addButton(new GuiNpcButton(2001, guiLeft + 212, y, 69, 20, "display.lightning"));
        getButton(2001).enabled = selectedTab != 1;

        addButton(new GuiNpcButton(2002, guiLeft + 287, y, 69, 20, "display.extra"));
        getButton(2002).enabled = selectedTab != 2;

        y += 23;

        if(selectedTab == 0){
            addLabel(new GuiNpcLabel(3003, "display.alpha", guiLeft + 138, y + 5));
            addTextField(new GuiNpcTextField(3003, this, guiLeft + 190, y, 40, 20, String.valueOf(auraDisplay.alpha)));
            getTextField(3003).setMaxStringLength(4);
            getTextField(3003).integersOnly = true;
            getTextField(3003).setMinMaxDefault(-1, 255, -1);

            y += 23;
            addLabel(new GuiNpcLabel(3004, "display.overrideDBC", guiLeft + 138, y + 5));
            addButton(new GuiNpcButtonYesNo(3004, guiLeft + 265, y, 90, 20, auraDisplay.overrideDBCAura));

            y += 23;
            addLabel(new GuiNpcLabel(3000, "display.color1", guiLeft + 138, y + 5));
            addButton(new GuiNpcButton(3000, guiLeft + 265, y, 50, 20, getColor(auraDisplay.color1)));
            getButton(3000).packedFGColour = auraDisplay.color1;
            addButton(new GuiNpcButton(3100, guiLeft + 320, y, 20, 20, "X"));
            getButton(3100).enabled = auraDisplay.color1 != -1;

            y += 23;
            addLabel(new GuiNpcLabel(3001, "display.color2", guiLeft + 138, y + 5));
            addButton(new GuiNpcButton(3001, guiLeft + 265, y, 50, 20, getColor(auraDisplay.color2)));
            getButton(3001).packedFGColour = auraDisplay.color2;
            addButton(new GuiNpcButton(3101, guiLeft + 320, y, 20, 20, "X"));
            getButton(3101).enabled = auraDisplay.color2 != -1;

            y += 23;
            addLabel(new GuiNpcLabel(3002, "display.color3", guiLeft + 138, y + 5));
            addButton(new GuiNpcButton(3002, guiLeft + 265, y, 50, 20, getColor(auraDisplay.color3)));
            getButton(3002).packedFGColour = auraDisplay.color3;
            addButton(new GuiNpcButton(3102, guiLeft + 320, y, 20, 20, "X"));
            getButton(3102).enabled = auraDisplay.color3 != -1;

            y += 23;

            addLabel(new GuiNpcLabel(200, "display.size", guiLeft + 138, y + 5));
            addTextField(new GuiNpcTextField(200, this, guiLeft + 190, y, 40, 20, String.valueOf(auraDisplay.size)));
            getTextField(200).setMaxStringLength(10);
            getTextField(200).floatsOnly = true;
            getTextField(200).setMinMaxDefaultFloat(-10000f, 10000f, 1.0f);

            addLabel(new GuiNpcLabel(201, "display.speed", guiLeft + 240, y + 5));
            addTextField(new GuiNpcTextField(201, this, guiLeft + 315, y, 40, 20, String.valueOf(auraDisplay.speed)));
            getTextField(201).setMaxStringLength(10);
            getTextField(201).integersOnly = true;
            getTextField(201).setMinMaxDefaultFloat(-10000f, 10000f, 1.0f);

            y += 23;
            addLabel(new GuiNpcLabel(3006, "display.3DType", guiLeft + 138, y + 5));
            addButton(new GuiNpcButton(3006, guiLeft + 245, y, 110, 20,
                new String[]{"auratype.none", "auratype.default","auratype.ssgod","auratype.ssb","auratype.shinka",
                    "auratype.ssrose","auratype.ssroseevo","auratype.ultimate","auratype.ui","auratype.god"}, auraDisplay.type.ordinal()));

            y += 23;
            addLabel(new GuiNpcLabel(3007, "display.2DType", guiLeft + 138, y + 5));
            addButton(new GuiNpcButton(3007, guiLeft + 245, y, 110, 20,
                new String[]{"auratype.none", "auratype.default","auratype.base","auratype.god","auratype.god_toppo",
                    "auratype.ui","auratype.mui","auratype.ultimate","auratype.legendary","auratype.ssj",
                    "auratype.ssgod", "auratype.ssb", "auratype.shinka", "auratype.ssrose", "auratype.ssroseevo", "auratype.jiren"}, auraDisplay.type2D.ordinal()));
        }
        if(selectedTab == 1){
            addLabel(new GuiNpcLabel(202, "display.lightning", guiLeft + 138, y + 5));
            addButton(new GuiNpcButtonYesNo(202, guiLeft + 220, y, 40, 20, auraDisplay.hasLightning));

            if(auraDisplay.hasLightning){
                addButton(new GuiNpcButton(109, guiLeft + 265, y, 50, 20, getColor(auraDisplay.lightningColor)));
                getButton(109).packedFGColour = auraDisplay.lightningColor;
                addButton(new GuiNpcButton(1109, guiLeft + 320, y, 20, 20, "X"));
                getButton(1109).enabled = auraDisplay.lightningColor != -1;

                y += 23;

                addLabel(new GuiNpcLabel(203, "display.lAlpha", guiLeft + 138, y + 5));
                addTextField(new GuiNpcTextField(203, this, guiLeft + 220, y, 40, 20, String.valueOf(auraDisplay.lightningAlpha)));
                getTextField(203).setMaxStringLength(4);
                getTextField(203).integersOnly = true;
                getTextField(203).setMinMaxDefault(-1, 255, -1);

                y += 23;

                addLabel(new GuiNpcLabel(204, "display.lSpeed", guiLeft + 138, y + 5));
                addTextField(new GuiNpcTextField(204, this, guiLeft + 220, y, 40, 20, String.valueOf(auraDisplay.lightningSpeed)));
                getTextField(204).setMaxStringLength(4);
                getTextField(204).integersOnly = true;
                getTextField(204).setMinMaxDefault(-1, 100, -1);

                y += 23;

                addLabel(new GuiNpcLabel(205, "display.lIntensity", guiLeft + 138, y + 5));
                addTextField(new GuiNpcTextField(205, this, guiLeft + 220, y, 40, 20, String.valueOf(auraDisplay.lightningAlpha)));
                getTextField(205).setMaxStringLength(4);
                getTextField(205).integersOnly = true;
                getTextField(205).setMinMaxDefault(-1, 100, -1);
            }
        }
        if(selectedTab == 2){
            addLabel(new GuiNpcLabel(302, "display.kaioken", guiLeft + 138, y + 5));
            addButton(new GuiNpcButtonYesNo(302, guiLeft + 220, y, 40, 20, auraDisplay.hasKaiokenAura));

            if(auraDisplay.hasKaiokenAura){
                addButton(new GuiNpcButton(309, guiLeft + 265, y, 50, 20, getColor(auraDisplay.kaiokenColor)));
                getButton(309).packedFGColour = auraDisplay.kaiokenColor;
                addButton(new GuiNpcButton(1309, guiLeft + 320, y, 20, 20, "X"));
                getButton(1309).enabled = auraDisplay.kaiokenColor != -1;

                y += 23;

                addLabel(new GuiNpcLabel(303, "display.kAlpha", guiLeft + 138, y + 5));
                addTextField(new GuiNpcTextField(303, this, guiLeft + 190, y, 40, 20, String.valueOf(auraDisplay.kaiokenAlpha)));
                getTextField(303).setMaxStringLength(4);
                getTextField(303).integersOnly = true;
                getTextField(303).setMinMaxDefault(-1, 255, -1);

                addLabel(new GuiNpcLabel(304, "display.kSize", guiLeft + 240, y + 5));
                addTextField(new GuiNpcTextField(304, this, guiLeft + 315, y, 40, 20, String.valueOf(auraDisplay.kaiokenSize)));
                getTextField(304).setMaxStringLength(4);
                getTextField(304).integersOnly = true;
                getTextField(304).setMinMaxDefault(-1, 100, -1);

                y += 23;

                addLabel(new GuiNpcLabel(305, "display.kaiokenOverride", guiLeft + 138, y + 5));
                addButton(new GuiNpcButtonYesNo(305, guiLeft + 280, y, 40, 20, auraDisplay.kaiokenOverrides));
            }

            y += 23;
            y += 23;
            addLabel(new GuiNpcLabel(400, "display.kettleModeCharging", guiLeft + 138, y + 5));
            addButton(new GuiNpcButtonYesNo(400, guiLeft + 280, y, 40, 20, auraDisplay.kettleModeCharging));

            y += 23;
            addLabel(new GuiNpcLabel(401, "display.kettleModeAura", guiLeft + 138, y + 5));
            addButton(new GuiNpcButtonYesNo(401, guiLeft + 280, y, 40, 20, auraDisplay.kettleModeAura));

            y += 23;
            addLabel(new GuiNpcLabel(402, "display.kettleModeType", guiLeft + 138, y + 5));
            addButton(new GuiNpcButton(402, guiLeft + 280, y, 40, 20,
                new String[]{"0", "1"}, auraDisplay.kettleModeType));
        }

        addButton(new GuiNpcButton(10000, guiLeft + 60, guiTop + 200 + this.yOffset, 75, 20, "gui.done"));
        controlButtons();
    }

    private void controlButtons() {
        addButton(this.left = new GuiNpcButton(668, this.guiLeft + 5, this.guiTop + 200 + this.yOffset, 20, 20, "<"));
        addButton(this.right = new GuiNpcButton(669, this.guiLeft + 30, this.guiTop + 200 + this.yOffset, 20, 20, ">"));
    }

	public void buttonEvent(GuiButton guibutton)
    {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if(button.id == 10000){
            parent.closeSubGui(null); // Close the GUI
        } else if(button.id == 2000) {
           selectedTab = 0;
           initGui();
        } else if(button.id == 2001) {
            selectedTab = 1;
            initGui();
        } else if(button.id == 2002) {
            selectedTab = 2;
            initGui();
        } else if(button.id == 3004) {
            auraDisplay.overrideDBCAura = !auraDisplay.overrideDBCAura;
            initGui();
        } else if(button.id == 3000) {
            // Change color1
            lastColorClicked = 0;
            setSubGui(new SubGuiColorSelector(auraDisplay.color1));
        } else if(button.id == 3100) {
            // Clear color1
            auraDisplay.color1 = -1;
            initGui();
        } else if(button.id == 3001) {
            lastColorClicked = 1;
            setSubGui(new SubGuiColorSelector(auraDisplay.color2));
        } else if(button.id == 3101) {
            // Clear color2
            auraDisplay.color2 = -1;
            initGui();
        } else if(button.id == 3002) {
            // Change color3
            lastColorClicked = 2;
            setSubGui(new SubGuiColorSelector(auraDisplay.color3));
        } else if(button.id == 3102) {
            // Clear color3
            auraDisplay.color3 = -1;
            initGui();
        } else if(button.id == 3006) {
            // Change 3D Type
            auraDisplay.type = EnumAuraTypes3D.values()[button.getValue()];
        } else if(button.id == 3007) {
            // Change 2D Type
            auraDisplay.type2D = EnumAuraTypes2D.values()[button.getValue()];
        } else if(button.id == 202) {
            // Toggle Lightning
            auraDisplay.hasLightning = !auraDisplay.hasLightning;
            initGui();
        } else if(button.id == 109) {
            // Change Lightning color
            lastColorClicked = 3;
            setSubGui(new SubGuiColorSelector(auraDisplay.lightningColor));
        } else if(button.id == 1109) {
            // Clear Lightning color
            auraDisplay.lightningColor = -1;
            initGui();
        } else if(button.id == 302) {
            // Toggle Kaioken Aura
            auraDisplay.hasKaiokenAura = !auraDisplay.hasKaiokenAura;
            initGui();
        } else if(button.id == 309) {
            // Change Kaioken color
            lastColorClicked = 4;
            setSubGui(new SubGuiColorSelector(auraDisplay.kaiokenColor));
        } else if(button.id == 1309) {
            // Clear Kaioken color
            auraDisplay.kaiokenColor = -1;
            initGui();
        } else if(button.id == 305) {
            // Toggle Kaioken Override
            auraDisplay.kaiokenOverrides = !auraDisplay.kaiokenOverrides;
            initGui();
        } else if(button.id == 400) {
            // Toggle Kettle Charging
            auraDisplay.kettleModeCharging = !auraDisplay.kettleModeCharging;
            initGui();
        } else if(button.id == 401) {
            // Toggle Kettle Charging
            auraDisplay.kettleModeAura = !auraDisplay.kettleModeAura;
            initGui();
        } else if(button.id == 402) {
            // Toggle Kettle Charging
            auraDisplay.kettleModeType = (byte) ((GuiNpcButton) guibutton).getValue();
            initGui();
        }
    }

    @Override
    public void keyTyped(char c, int i) {
        super.keyTyped(c,i);
        if (i == 1)
            parent.closeSubGui(null);

    }

	@Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {
        if(guiNpcTextField.id == 200) {
            auraDisplay.size = guiNpcTextField.getFloat();
        }
        else if(guiNpcTextField.id == 201) {
            // Speed
            auraDisplay.speed = guiNpcTextField.getInteger();
        }
        else if(guiNpcTextField.id == 203) {
            // TextField for lightning alpha in the "Lightning" tab
            auraDisplay.lightningAlpha = guiNpcTextField.getInteger();
        }
        else if(guiNpcTextField.id == 204) {
            // TextField for lightning speed in the "Lightning" tab
            auraDisplay.lightningSpeed = guiNpcTextField.getInteger();
        }
        else if(guiNpcTextField.id == 205) {
            // TextField for lightning intensity in the "Lightning" tab
            auraDisplay.lightningIntensity = guiNpcTextField.getInteger();
            auraDisplay.lightningIntensity = ValueUtil.clamp(auraDisplay.lightningIntensity, 0 , 8);
        }
        else if(guiNpcTextField.id == 3003) {
            // TextField for alpha in the "General" tab
            auraDisplay.alpha = guiNpcTextField.getInteger();
        }
        else if(guiNpcTextField.id == 303) {
            // TextField for kaioken alpha in the "Extra" tab
            auraDisplay.kaiokenAlpha = guiNpcTextField.getInteger();
        }
        else if(guiNpcTextField.id == 304) {
            // TextField for kaioken size in the "Extra" tab
            auraDisplay.kaiokenSize = guiNpcTextField.getInteger();
        }
    }

    @Override
    public void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
    }

	@Override
	public void subGuiClosed(SubGuiInterface subgui){
        if(subgui instanceof  SubGuiColorSelector){
            int color = ((SubGuiColorSelector) subgui).color;
            if(lastColorClicked == 0){
                auraDisplay.color1 = color;
            } else if(lastColorClicked == 1){
                auraDisplay.color2 = color;
            } else if(lastColorClicked == 2){
                auraDisplay.color3 = color;
            } else if(lastColorClicked == 3){
                auraDisplay.lightningColor = color;
            } else if(lastColorClicked == 4){
                auraDisplay.kaiokenColor = color;
            }
            initGui();
        }
	}

    @Override
    protected void drawBackground() {
        super.drawBackground();

        int xPosGradient = guiLeft + 5;
        int yPosGradient = guiTop + 5;
        drawGradientRect(xPosGradient, yPosGradient, 130 + xPosGradient ,180 + yPosGradient, 0xc0101010, 0xd0101010);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void drawScreen(int par1, int par2, float par3) {
        if (Mouse.isButtonDown(0)) {
            if (this.left.mousePressed(this.mc, par1, par2)) {
                rotation += par3 * 2.0F;
            } else if (this.right.mousePressed(this.mc, par1, par2)) {
                rotation -= par3 * 2.0F;
            }
        }
        super.drawScreen(par1, par2, par3);
        if(hasSubGui())
            return;

        GL11.glColor4f(1, 1, 1, 1);
        EntityLivingBase entity = this.npc;

        int l = guiLeft + 190 + xOffset;
        int i1 =  guiTop + 180 + yOffset;
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef(l, i1, 50F);

        GL11.glScalef(-zoomed, zoomed, zoomed);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f7 = entity.rotationYawHead;
        float f5 = (float)(l) - par1;
        float f6 = (float)(i1 - 50) - par2;
        GL11.glRotatef(135F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-(float)Math.atan(f6 / 80F) * 20F, 1.0F, 0.0F, 0.0F);
        entity.prevRenderYawOffset = entity.renderYawOffset = rotation;
        entity.prevRotationYaw = entity.rotationYaw = (float)Math.atan(f5 / 80F) * 40F + rotation;
        entity.rotationPitch = -(float)Math.atan(f6 / 80F) * 20F;
        entity.prevRotationYawHead = entity.rotationYawHead = entity.rotationYaw;
        GL11.glTranslatef(0.0F, entity.yOffset, 0.0F);
        RenderManager.instance.playerViewY = 180F;

        if(showAura && this.aura != null) {
            // Render Aura
            EntityAura2 aur = new EntityAura2(entity.worldObj, Utility.getEntityID(entity), 0, 0, 0, 100, false);
            aur.setAlp(0.5F);
            aur.setInner(true);
            aur.setCol(auraDisplay.color1);

            Aura aura = this.aura;
            if (aura.display.type == EnumAuraTypes3D.SaiyanGod) {
                aur.setAlp(0.2F);
                aur.setTex("aurai");
                aur.setTexL2("aurai2");
                aur.setColL2(16747301);
            } else if (aura.display.type == EnumAuraTypes3D.SaiyanBlue) {
                aur.setSpd(40);
                aur.setAlp(0.5F);
                aur.setTex("aurag");
                aur.setColL3(15727354);
                aur.setTexL3("auragb");
            } else if (aura.display.type == EnumAuraTypes3D.SaiyanBlueEvo) {
                aur.setSpd(40);
                aur.setAlp(0.5F);
                aur.setTex("aurag");
                aur.setColL3(12310271);
                aur.setTexL3("auragb");
            } else if (aura.display.type == EnumAuraTypes3D.SaiyanRose) {
                aur.setSpd(30);
                aur.setAlp(0.2F);
                aur.setTex("aurai");
                aur.setTexL2("aurai2");
                aur.setColL2(7872713);
            } else if (aura.display.type == EnumAuraTypes3D.SaiyanRoseEvo) {
                aur.setSpd(30);
                aur.setAlp(0.2F);
                aur.setTex("aurai");
                aur.setTexL2("aurai2");
                aur.setColL2(8592109);
            } else if (aura.display.type == EnumAuraTypes3D.UI) {
                aur.setSpd(100);
                aur.setAlp(0.15F);
                aur.setTex("auras");
                aur.setCol(15790320);
                aur.setColL3(4746495);
                aur.setTexL3("auragb");
            } else if (aura.display.type == EnumAuraTypes3D.GoD) {
                aur.setSpd(100);
                aur.setAlp(0.2F);
                aur.setTex("aurag");
                aur.setTexL3("auragb");
                aur.setColL2(12464847);
            } else if (aura.display.type == EnumAuraTypes3D.UltimateArco) {
                aur.setAlp(0.5F);
                aur.setTex("aurau");
                aur.setTexL2("aurau2");
                aur.setColL2(16776724);
            }
            if(aura.display.hasColor("color1"))
                aur.setCol(aura.display.color1);
            if (aura.display.hasColor("color2"))
                aur.setColL2(aura.display.color2);
            if (aura.display.hasColor("color3"))
                aur.setColL3(aura.display.color3);

            if (aura.display.hasAlpha("aura"))
                aur.setAlp((float) aura.display.alpha / 255);

            if (aura.display.hasSpeed())
                aur.setSpd(aura.display.speed);

            try {
                GL11.glPushMatrix();
                RenderHelper.disableStandardItemLighting();
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE); // Additive blending for brightness
                GL11.glDisable(GL11.GL_LIGHTING); // Disable standard lighting
                GL11.glScalef(0.8f, 0.8f, 0.8f);


//                int add = auraTicks % 5;
//                aur.setSpd(5 + add);
//                RenderManager.instance.renderEntityWithPosYaw((Entity) aur, 0.0, 1.0, 0.0, 0.0F, 1.0F);
//                aur.setSpd((5 * 2) + add);
//                RenderManager.instance.renderEntityWithPosYaw((Entity) aur, 0.0, 1.0, 0.0, 0.0F, 1.0F);
//                aur.setSpd((5 * 3) + add);
//                RenderManager.instance.renderEntityWithPosYaw((Entity) aur, 0.0, 1.0, 0.0, 0.0F, 1.0F);
//                aur.setSpd((5 * 4) + add);
                RenderManager.instance.renderEntityWithPosYaw((Entity) aur, 0.0, 1.0, 0.0, 0.0F, 1.0F);

                // Restore the settings
                GL11.glEnable(GL11.GL_LIGHTING); // Re-enable standard lighting
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
                RenderHelper.enableStandardItemLighting();
            } catch (Exception ignored) {
            }
        }
        auraTicks++;

        // Render Entity
        try {
            RenderManager.instance.renderEntityWithPosYaw((Entity)entity, 0.0, 0.0, 0.0, 0.0F, 1.0F);
        } catch (Exception ignored) {}


        entity.prevRenderYawOffset = entity.renderYawOffset = f2;
        entity.prevRotationYaw = entity.rotationYaw = f3;
        entity.rotationPitch = f4;
        entity.prevRotationYawHead = entity.rotationYawHead = f7;

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPopMatrix();
    }

	@Override
	public void selected(int id, String name) {}

	public void save(){}

    public String getColor(int input) {
        String str;
        for(str = Integer.toHexString(input); str.length() < 6; str = "0" + str) {}
        return str;
    }
}
