package kamkeel.npcdbc.client.gui.global.auras;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import kamkeel.npcdbc.client.model.part.hair.DBCHair;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes2D;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes3D;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.aura.AuraDisplay;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.gui.SubGuiColorSelector;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.util.ValueUtil;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import static kamkeel.npcdbc.client.ClientEventHandler.spawnAura;

public class SubGuiAuraDisplay extends SubGuiInterface implements ISubGuiListener, GuiSelectionListener,ITextfieldListener
{
    private final GuiNPCManageAuras parent;
    public static boolean useGUIAura;
    public static Aura aura;
    public AuraDisplay display;
    private final DBCDisplay visualDisplay;
    public int lastColorClicked = 0;
    boolean showAura = false;
    public int auraTicks = 1;
    public EntityCustomNpc npc;
    private float rotation = 0.0F;
    private GuiNpcButton left, right, zoom, unzoom;
    private float zoomed = 60.0F;
    public int xOffset = 0;
    public int yOffset = 0;
    int selectedTab = 0;
    DBCDisplay origDisplay = null;

    public GuiScrollWindow scrollWindow;


    public SubGuiAuraDisplay(GuiNPCManageAuras parent, Aura aura)
	{
		this.aura = aura;
        this.display = aura.display;
        this.parent = parent;

        EntityCustomNpc originalNPC = ((EntityCustomNpc) parent.npc);
        if (originalNPC != null)
            origDisplay = ((INPCDisplay) originalNPC.display).getDBCDisplay();

		setBackground("menubg.png");
		xSize = 360;
		ySize = 216;

        xOffset = -120;
        yOffset = -10;

        npc = new EntityCustomNpc(Minecraft.getMinecraft().theWorld);
        npc.display.name = "aura man";
        npc.display.texture = "customnpcs:textures/entity/humanmale/AnimationBody.png";
        visualDisplay = ((INPCDisplay) npc.display).getDBCDisplay();
        visualDisplay.enabled = true;
        visualDisplay.useSkin = true;
        if (origDisplay != null && origDisplay.enabled && origDisplay.useSkin) {
            visualDisplay.readFromNBT(origDisplay.writeToNBT(new NBTTagCompound()));
            visualDisplay.setRacialExtras();

        } else {
            visualDisplay.race = DBCData.getClient().Race;
            visualDisplay.setDefaultColors();
            updateDisplay();
            visualDisplay.setRacialExtras();

        }
        visualDisplay.setAura(aura);
        visualDisplay.auraOn = true;

    }

    public void updateDisplay() {
        DBCData data = DBCData.getClient();
        boolean isSaiyan = DBCRace.isSaiyan(visualDisplay.race);
        if (isSaiyan) {
            visualDisplay.tailState = (data.Tail == 0 || data.Tail == 1) ? data.Tail : (byte) (data.Tail == -1 ? 0 : 1);
        }

        if (visualDisplay.race == DBCRace.ARCOSIAN || visualDisplay.race == DBCRace.NAMEKIAN) {
            visualDisplay.hairCode = "";
        } else if (visualDisplay.race == DBCRace.MAJIN) {
            visualDisplay.hairCode = DBCHair.MAJIN_HAIR;
            visualDisplay.hairColor = visualDisplay.bodyCM;
        }

    }

    public void initGui()
    {
        super.initGui();
        int y = guiTop + 5;

        if (scrollWindow == null) {
            scrollWindow = new GuiScrollWindow(this, guiLeft + 139, y, 215, ySize - 10, 0);
        } else {
            scrollWindow.xPos = guiLeft + 139;
            scrollWindow.yPos = y;
            scrollWindow.clipWidth = 215;
            scrollWindow.clipHeight = ySize - 10;
        }

        addScrollableGui(0, scrollWindow);
        int maxScroll = -20;

//        scrollWindow.addButton(new GuiNpcButton(2000, 3, y, 69, 20, "display.general"));
//        scrollWindow.getButton(2000).enabled = selectedTab != 0;
//
//        scrollWindow.addButton(new GuiNpcButton(2001, guiLeft + 212, y, 69, 20, "display.lightning"));
//        scrollWindow.getButton(2001).enabled = selectedTab != 1;
//
//        scrollWindow.addButton(new GuiNpcButton(2002, guiLeft + 287, y, 69, 20, "display.extra"));
//        scrollWindow.getButton(2002).enabled = selectedTab != 2;

        // y += 23;

        maxScroll += 23;
        int guiX = 0;
        y = 0;

        if(selectedTab == 0){
            scrollWindow.addLabel(new GuiNpcLabel(3004, "display.overrideDBC", 3, y + 5));
            scrollWindow.getLabel(3004).color = 0xffffff;
            scrollWindow.addButton(new GuiNpcButtonYesNo(3004, guiX + 110, y, 80, 20, display.overrideDBCAura));


            y += 23;

            scrollWindow.addLabel(new GuiNpcLabel(3000, "display.color1", 3, y + 5));
            scrollWindow.getLabel(3000).color = 0xffffff;
            scrollWindow.addButton(new GuiNpcButton(3000, guiX + 110, y, 60, 20, getColor(display.color1)));
            scrollWindow.getButton(3000).packedFGColour = display.color1;
            scrollWindow.addButton(new GuiNpcButton(3100, guiX + 170, y, 20, 20, "X"));
            scrollWindow.getButton(3100).enabled = display.color1 != -1;

            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(3001, "display.color2", 3, y + 5));
            scrollWindow.getLabel(3001).color = 0xffffff;
            scrollWindow.addButton(new GuiNpcButton(3001, guiX + 110, y, 60, 20, getColor(display.color2)));
            scrollWindow.getButton(3001).packedFGColour = display.color2;
            scrollWindow.addButton(new GuiNpcButton(3101, guiX + 170, y, 20, 20, "X"));
            scrollWindow.getButton(3101).enabled = display.color2 != -1;

            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(3002, "display.color3", 3, y + 5));
            scrollWindow.getLabel(3002).color = 0xffffff;
            scrollWindow.addButton(new GuiNpcButton(3002, guiX + 110, y, 60, 20, getColor(display.color3)));
            scrollWindow.getButton(3002).packedFGColour = display.color3;
            scrollWindow.addButton(new GuiNpcButton(3102, guiX + 170, y, 20, 20, "X"));
            scrollWindow.getButton(3102).enabled = display.color3 != -1;

            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(3006, "display.3DType", 3, y + 5));
            scrollWindow.getLabel(3006).color = 0xffffff;
            scrollWindow.addButton(new GuiButtonBiDirectional(3006, guiX + 100, y, 100, 20, new String[]{"auratype.none", "auratype.default", "auratype.ssgod", "auratype.ssb", "auratype.shinka", "auratype.ssrose", "auratype.ssroseevo", "auratype.ultimate", "auratype.ui", "auratype.god"}, display.type.ordinal()));

            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(3007, "display.2DType", 3, y + 5));
            scrollWindow.getLabel(3007).color = 0xffffff;
            scrollWindow.addButton(new GuiButtonBiDirectional(3007, guiX + 100, y, 100, 20, new String[]{"auratype.none", "auratype.default", "auratype.base", "auratype.god", "auratype.god_toppo", "auratype.ui", "auratype.mui", "auratype.ultimate", "auratype.legendary", "auratype.ssj", "auratype.ssgod", "auratype.ssb", "auratype.shinka", "auratype.ssrose", "auratype.ssroseevo", "auratype.jiren"}, display.type2D.ordinal()));

            maxScroll += 2;
            y += 2;


            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(3003, "display.alpha", 1, y + 5));
            scrollWindow.getLabel(3003).color = 0xffffff;
            scrollWindow.addTextField(new GuiNpcTextField(3003, this, guiX + 130, y, 40, 18, String.valueOf(display.alpha)));
            scrollWindow.getTextField(3003).setMaxStringLength(4);
            scrollWindow.getTextField(3003).integersOnly = true;
            scrollWindow.getTextField(3003).setMinMaxDefault(-1, 255, -1);

            maxScroll += 23;
            y += 23;

            scrollWindow.addLabel(new GuiNpcLabel(200, "display.size", 1, y + 5));
            scrollWindow.getLabel(200).color = 0xffffff;
            scrollWindow.addTextField(new GuiNpcTextField(200, this, guiX + 130, y, 40, 18, String.valueOf(display.size)));
            scrollWindow.getTextField(200).setMaxStringLength(10);
            scrollWindow.getTextField(200).floatsOnly = true;
            scrollWindow.getTextField(200).setMinMaxDefaultFloat(-10000f, 10, 1.0f);

            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(201, "display.speed", 1, y + 5));
            scrollWindow.getLabel(201).color = 0xffffff;
            scrollWindow.addTextField(new GuiNpcTextField(201, this, guiX + 130, y, 40, 18, String.valueOf(display.speed)));
            scrollWindow.getTextField(201).setMaxStringLength(10);
            scrollWindow.getTextField(201).integersOnly = true;
            scrollWindow.getTextField(201).setMinMaxDefaultFloat(-10000f, 10000f, 1.0f);

        }
        y += 50;
        scrollWindow.addLabel(new GuiNpcLabel(202, "display.hasLightning", 1, y + 5));
        scrollWindow.getLabel(202).color = 0xffffff;
        scrollWindow.addButton(new GuiNpcButtonYesNo(202, guiX + 110, y, 80, 20, display.hasLightning));

            if (display.hasLightning) {
                maxScroll += 23;
                y += 23;
                scrollWindow.addLabel(new GuiNpcLabel(109, "display.lightningColor", 1, y + 5));
                scrollWindow.getLabel(109).color = 0xffffff;
                scrollWindow.addButton(new GuiNpcButton(109, guiX + 110, y, 60, 20, getColor(display.lightningColor)));
                scrollWindow.getButton(109).packedFGColour = display.lightningColor;
                scrollWindow.addButton(new GuiNpcButton(1109, guiX + 170, y, 20, 20, "X"));
                scrollWindow.getButton(1109).enabled = display.lightningColor != -1;

                maxScroll += 23;
                y += 23;

                scrollWindow.addLabel(new GuiNpcLabel(203, "display.lightningAlpha", 1, y + 5));
                scrollWindow.getLabel(203).color = 0xffffff;
                scrollWindow.addTextField(new GuiNpcTextField(203, this, guiX + 135, y, 30, 18, String.valueOf(display.lightningAlpha)));
                scrollWindow.getTextField(203).setMaxStringLength(4);
                scrollWindow.getTextField(203).integersOnly = true;
                scrollWindow.getTextField(203).setMinMaxDefault(-1, 255, -1);

                maxScroll += 23;
                y += 23;

                scrollWindow.addLabel(new GuiNpcLabel(205, "display.lightningIntensity", 1, y + 5));
                scrollWindow.getLabel(205).color = 0xffffff;
                scrollWindow.addTextField(new GuiNpcTextField(205, this, guiX + 135, y, 30, 18, String.valueOf(display.lightningAlpha)));
                scrollWindow.getTextField(205).setMaxStringLength(4);
                scrollWindow.getTextField(205).integersOnly = true;
                scrollWindow.getTextField(205).setMinMaxDefault(-1, 100, -1);

                maxScroll += 23;
                y += 23;

                scrollWindow.addLabel(new GuiNpcLabel(204, "display.lightningSpeed", 1, y + 5));
                scrollWindow.getLabel(204).color = 0xffffff;
                scrollWindow.addTextField(new GuiNpcTextField(204, this, guiX + 135, y, 30, 18, String.valueOf(display.lightningSpeed)));
                scrollWindow.getTextField(204).setMaxStringLength(4);
                scrollWindow.getTextField(204).integersOnly = true;
                scrollWindow.getTextField(204).setMinMaxDefault(-1, 100, -1);


            }
        y += 50;
        scrollWindow.addLabel(new GuiNpcLabel(302, "display.hasKaioken", 1, y + 5));
        scrollWindow.getLabel(302).color = 0xffffff;
        scrollWindow.addButton(new GuiNpcButtonYesNo(302, guiX + 110, y, 80, 20, display.hasKaiokenAura));

            if (display.hasKaiokenAura) {
                maxScroll += 23;
                y += 23;
                scrollWindow.addLabel(new GuiNpcLabel(305, "display.kaiokenOverride", 1, y + 5));
                scrollWindow.getLabel(305).color = 0xffffff;
                scrollWindow.addButton(new GuiNpcButtonYesNo(305, guiX + 110, y, 80, 20, display.kaiokenOverrides));

                maxScroll += 23;
                y += 23;
                scrollWindow.addLabel(new GuiNpcLabel(309, "display.kaiokenColor", 1, y + 5));
                scrollWindow.getLabel(309).color = 0xffffff;
                scrollWindow.addButton(new GuiNpcButton(309, guiX + 110, y, 60, 20, getColor(display.kaiokenColor)));
                scrollWindow.getButton(309).packedFGColour = display.kaiokenColor;
                scrollWindow.addButton(new GuiNpcButton(1309, guiX + 170, y, 20, 20, "X"));
                scrollWindow.getButton(1309).enabled = display.kaiokenColor != -1;

                maxScroll += 23;
                y += 23;

                scrollWindow.addLabel(new GuiNpcLabel(303, "display.kaiokenAlpha", 1, y + 5));
                scrollWindow.getLabel(303).color = 0xffffff;
                scrollWindow.addTextField(new GuiNpcTextField(303, this, guiX + 135, y, 30, 18, String.valueOf(display.kaiokenAlpha)));
                scrollWindow.getTextField(303).setMaxStringLength(4);
                scrollWindow.getTextField(303).integersOnly = true;
                scrollWindow.getTextField(303).setMinMaxDefault(-1, 255, -1);

                maxScroll += 23;
                y += 23;

                scrollWindow.addLabel(new GuiNpcLabel(304, "display.kaiokenSize", 1, y + 5));
                scrollWindow.getLabel(304).color = 0xffffff;
                scrollWindow.addTextField(new GuiNpcTextField(304, this, guiX + 135, y, 30, 18, String.valueOf(display.kaiokenSize)));
                scrollWindow.getTextField(304).setMaxStringLength(4);
                scrollWindow.getTextField(304).integersOnly = true;
                scrollWindow.getTextField(304).setMinMaxDefault(-1, 100, -1);

            }


//        y += 43;
//        scrollWindow.addLabel(new GuiNpcLabel(401, "display.kettleModeAura", 1, y + 5));
//        scrollWindow.getLabel(401).color = 0xffffff;
//        scrollWindow.addButton(new GuiNpcButtonYesNo(401, guiX + 110, y, 80, 20, display.kettleModeAura));

        y += 43;
        scrollWindow.addLabel(new GuiNpcLabel(402, "display.kettleModeType", 1, y + 5));
        scrollWindow.getLabel(402).color = 0xffffff;
        scrollWindow.addButton(new GuiButtonBiDirectional(402, guiX + 98, y, 104, 20, new String[]{"display.kettleOff", "display.kettleOnly", "display.kettlePlusAura"}, display.kettleModeType));


        if (display.kettleModeType > 0) {
            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(400, "display.kettleModeCharging", 1, y + 5));
            scrollWindow.getLabel(400).color = 0xffffff;
            scrollWindow.addButton(new GuiNpcButtonYesNo(400, guiX + 110, y, 80, 20, display.kettleModeCharging));

        }
        maxScroll += 10;

        //   scrollWindow.addButton(new GuiNpcButton(10000, guiLeft + 60, guiTop + 200 + this.yOffset, 75, 20, "gui.done"));
        addButton(this.unzoom = new GuiNpcButton(666, this.guiLeft + 5, this.guiTop + 200 + this.yOffset, 15, 20, "-"));
        addButton(this.left = new GuiNpcButton(668, this.guiLeft + 20, this.guiTop + 200 + this.yOffset, 15, 20, "<"));
        addButton(this.right = new GuiNpcButton(669, this.guiLeft + 35, this.guiTop + 200 + this.yOffset, 15, 20, ">"));
        addButton(this.zoom = new GuiNpcButton(667, this.guiLeft + 50, this.guiTop + 200 + this.yOffset, 15, 20, "+"));

        scrollWindow.maxScrollY = maxScroll;

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
            display.overrideDBCAura = !display.overrideDBCAura;
            initGui();
        } else if(button.id == 3000) {
            // Change color1
            lastColorClicked = 0;
            setSubGui(new SubGuiColorSelector(display.color1));
        } else if(button.id == 3100) {
            // Clear color1
            display.color1 = -1;
            initGui();
        } else if(button.id == 3001) {
            lastColorClicked = 1;
            setSubGui(new SubGuiColorSelector(display.color2));
        } else if(button.id == 3101) {
            // Clear color2
            display.color2 = -1;
            initGui();
        } else if(button.id == 3002) {
            // Change color3
            lastColorClicked = 2;
            setSubGui(new SubGuiColorSelector(display.color3));
        } else if(button.id == 3102) {
            // Clear color3
            display.color3 = -1;
            initGui();
        } else if(button.id == 3006) {
            // Change 3D Type
            display.type = EnumAuraTypes3D.values()[button.getValue()];
        } else if(button.id == 3007) {
            // Change 2D Type
            display.type2D = EnumAuraTypes2D.values()[button.getValue()];
        } else if(button.id == 202) {
            // Toggle Lightning
            display.hasLightning = !display.hasLightning;
            initGui();
        } else if(button.id == 109) {
            // Change Lightning color
            lastColorClicked = 3;
            setSubGui(new SubGuiColorSelector(display.lightningColor));
        } else if(button.id == 1109) {
            // Clear Lightning color
            display.lightningColor = -1;
            initGui();
        } else if(button.id == 302) {
            // Toggle Kaioken Aura
            display.hasKaiokenAura = !display.hasKaiokenAura;
            initGui();
        } else if(button.id == 309) {
            // Change Kaioken color
            lastColorClicked = 4;
            setSubGui(new SubGuiColorSelector(display.kaiokenColor));
        } else if(button.id == 1309) {
            // Clear Kaioken color
            display.kaiokenColor = -1;
            initGui();
        } else if(button.id == 305) {
            // Toggle Kaioken Override
            display.kaiokenOverrides = !display.kaiokenOverrides;
            initGui();
        } else if(button.id == 400) {
            // Toggle Kettle Charging
            display.kettleModeCharging = !display.kettleModeCharging;
            initGui();
        } else if(button.id == 401) {
            // Toggle Kettle Charging
            display.kettleModeAura = !display.kettleModeAura;
            initGui();
        } else if(button.id == 402) {
            // Toggle Kettle Charging
            display.kettleModeType = (byte) ((GuiNpcButton) guibutton).getValue();
            scrollWindow.mouseScroll = 1;
            if (scrollWindow.nextScrollY > (float) scrollWindow.maxScrollY) {
                scrollWindow.nextScrollY = (float) scrollWindow.maxScrollY;
                scrollWindow.clipHeight = scrollWindow.maxScrollY;

            }
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
            display.setSize(guiNpcTextField.getFloat());
        }
        else if(guiNpcTextField.id == 201) {
            // Speed
            display.speed = guiNpcTextField.getInteger();
        }
        else if(guiNpcTextField.id == 203) {
            // TextField for lightning alpha in the "Lightning" tab
            display.lightningAlpha = guiNpcTextField.getInteger();
        }
        else if(guiNpcTextField.id == 204) {
            // TextField for lightning speed in the "Lightning" tab
            display.lightningSpeed = guiNpcTextField.getInteger();
        }
        else if(guiNpcTextField.id == 205) {
            // TextField for lightning intensity in the "Lightning" tab
            display.lightningIntensity = guiNpcTextField.getInteger();
            display.lightningIntensity = ValueUtil.clamp(display.lightningIntensity, 0, 8);
        }
        else if(guiNpcTextField.id == 3003) {
            // TextField for alpha in the "General" tab
            display.alpha = guiNpcTextField.getInteger();
        }
        else if(guiNpcTextField.id == 303) {
            // TextField for kaioken alpha in the "Extra" tab
            display.kaiokenAlpha = guiNpcTextField.getInteger();
        }
        else if(guiNpcTextField.id == 304) {
            // TextField for kaioken size in the "Extra" tab
            display.kaiokenSize = guiNpcTextField.getInteger();
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
                display.color1 = color;
            } else if(lastColorClicked == 1){
                display.color2 = color;
            } else if(lastColorClicked == 2){
                display.color3 = color;
            } else if(lastColorClicked == 3){
                display.lightningColor = color;
            } else if(lastColorClicked == 4){
                display.kaiokenColor = color;
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
                rotation += 0.2 * 2.0F;
            } else if (this.right.mousePressed(this.mc, par1, par2)) {
                rotation -= 0.2 * 2.0F;
            } else if (this.zoom.mousePressed(this.mc, par1, par2) && zoomed < 100.0F) {
                zoomed += 0.05 * 2.0F;
            } else if (this.unzoom.mousePressed(this.mc, par1, par2) && zoomed > 10.0F) {
                zoomed -= 0.05 * 2.0F;
            }
        }
        super.drawScreen(par1, par2, par3);
        if(hasSubGui())
            return;

        EntityAura enhancedAura = visualDisplay.auraEntity;
        useGUIAura = true;
        boolean isInKaioken = false;
        if (ConfigDBCClient.RevampAura) {
            if (enhancedAura == null) {
                enhancedAura = new EntityAura(npc, aura).load(true).spawn();
                enhancedAura.isGUIAura = true;
            } else {
                if (enhancedAura.ticksExisted % 10 == 0)
                    enhancedAura.load(true);
            }
        } else {
            if (enhancedAura != null)
                enhancedAura.despawn();

            if (isInKaioken && aura.display.kaiokenOverrides) {
                //     spawnKaiokenAura(aura, dbcData);
            } else {
                spawnAura(npc, aura);
                if (aura.hasSecondaryAura())
                    spawnAura(npc, aura.getSecondaryAur());
                //   if (isInKaioken)
                // spawnKaiokenAura(aura, dbcData);
            }
        }
        useGUIAura = false;


        GL11.glColor4f(1, 1, 1, 1);
        EntityLivingBase entity = this.npc; // DBCData.getClient().player;//

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
        GL11.glRotatef(-(float) Math.atan(f6 / 800F) * 20F, 1.0F, 0.0F, 0.0F);
        entity.prevRenderYawOffset = entity.renderYawOffset = rotation;
        entity.prevRotationYaw = entity.rotationYaw = (float)Math.atan(f5 / 80F) * 40F + rotation;
        entity.rotationPitch = -(float)Math.atan(f6 / 80F) * 20F;
        entity.prevRotationYawHead = entity.rotationYawHead = entity.rotationYaw;
        GL11.glTranslatef(0.0F, entity.yOffset, 1F);
        RenderManager.instance.playerViewY = 180F;

        if(showAura && this.aura != null) {
            // Render Aura
            EntityAura2 aur = new EntityAura2(entity.worldObj, Utility.getEntityID(entity), 0, 0, 0, 100, false);
            aur.setAlp(0.5F);
            aur.setInner(true);
            aur.setCol(display.color1);

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
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE); //  scrollWindow.additive blending for brightness
                GL11.glDisable(GL11.GL_LIGHTING); // Disable standard lighting
                GL11.glScalef(0.8f, 0.8f, 0.8f);


//                int  scrollWindow.add = auraTicks % 5;
//                aur.setSpd(5 +  scrollWindow.add);
//                RenderManager.instance.renderEntityWithPosYaw((Entity) aur, 0.0, 1.0, 0.0, 0.0F, 1.0F);
//                aur.setSpd((5 * 2) +  scrollWindow.add);
//                RenderManager.instance.renderEntityWithPosYaw((Entity) aur, 0.0, 1.0, 0.0, 0.0F, 1.0F);
//                aur.setSpd((5 * 3) +  scrollWindow.add);
//                RenderManager.instance.renderEntityWithPosYaw((Entity) aur, 0.0, 1.0, 0.0, 0.0F, 1.0F);
//                aur.setSpd((5 * 4) +  scrollWindow.add);
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
