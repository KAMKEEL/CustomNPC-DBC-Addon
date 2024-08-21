package kamkeel.npcdbc.client.gui.global.auras;

import kamkeel.npcdbc.client.gui.component.SubGuiSelectAura;
import kamkeel.npcdbc.client.gui.component.SubGuiSelectOutline;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes2D;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes3D;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.aura.AuraDisplay;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.entity.EntityAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.SubGuiColorSelector;
import noppes.npcs.client.gui.select.GuiSoundSelection;
import noppes.npcs.client.gui.util.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import static kamkeel.npcdbc.client.ClientEventHandler.spawnAura;
import static kamkeel.npcdbc.client.ClientEventHandler.spawnKaiokenAura;
import static kamkeel.npcdbc.constants.enums.EnumAuraTypes3D.*;

public class SubGuiAuraDisplay extends GuiNPCInterface implements ISubGuiListener, GuiSelectionListener, ITextfieldListener {
    public static boolean useGUIAura;
    public static Aura aura;
    public static int auraTicks = 1;
    private final GuiNPCManageAuras parent;
    private DBCDisplay visualDisplay;
    public AuraDisplay display;
    public int lastColorClicked = 0;
    public int xOffset = 0;
    public int yOffset = 0;
    public GuiScrollWindow scrollWindow;
    boolean showAura = false;
    int selectedTab = 0;
    private float rotation = 0.0F;
    private GuiNpcButton left, right, zoom, unzoom;
    private float zoomed = 60.0F;
    private boolean renderAura;
    private int revampedAura;
    boolean setNormalSound = true;

    public SubGuiAuraDisplay(GuiNPCManageAuras parent) {
        super(parent.npc);
        SubGuiAuraDisplay.aura = parent.aura;
        this.display = parent.display;
        this.parent = parent;
        this.visualDisplay = parent.visualDisplay;

        xSize = 360;
        ySize = 216;

        this.xOffset = 120;
        this.yOffset = -20;

        zoomed = 50;
        parent.playSound(false);
    }

    public void initGui() {
        super.initGui();
        int x = guiLeft - 27;
        int y = guiTop - 30;
        int rightOffset = 25;

        if (scrollWindow == null) {
            scrollWindow = new GuiScrollWindow(this, x, y, 230 + rightOffset, 215, 0);
        } else {
            scrollWindow.xPos = x;
            scrollWindow.yPos = y;
            scrollWindow.clipWidth = 230 + rightOffset;
            scrollWindow.clipHeight = 215;
        }
        scrollWindow.scrollSpeed = 3.5f;

        addScrollableGui(0, scrollWindow);
        int maxScroll = -20;


        int guiX = -5;
        y = 7;

        scrollWindow.addLabel(new GuiNpcLabel(101, "gui.name", 3, y + 5));
        scrollWindow.getLabel(101).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(101, this, this.fontRendererObj, guiX + 100 + rightOffset, y, 120, 20, aura.name));
        scrollWindow.getTextField(101).setMaxStringLength(40);

        y += 26;
        scrollWindow.addLabel(new GuiNpcLabel(102, "general.menuName", 3, y + 5));
        scrollWindow.getLabel(102).color = 0xffffff;
        scrollWindow.addTextField(new GuiNpcTextField(102, this, this.fontRendererObj, guiX + 100 + rightOffset, y, 120, 20, aura.menuName.replaceAll("§", "&")));
        scrollWindow.getTextField(102).setMaxStringLength(40);

        y += 26;
        scrollWindow.addLabel(new GuiNpcLabel(3004, "display.overrideDBC", 3, y + 5));
        scrollWindow.getLabel(3004).color = 0xffffff;
        scrollWindow.addButton(new GuiNpcButtonYesNo(3004, guiX + 140 + rightOffset, y, 80, 20, display.overrideDBCAura));

        //        y += 26;
        //        scrollWindow.addLabel(new GuiNpcLabel(1, "general.auraSound", 3, y + 5));
        //        scrollWindow.getLabel(1).color = 0xffffff;
        //        y += 16;
        //        scrollWindow.addTextField(new GuiNpcTextField(1, this, fontRendererObj, 3, y, 145 + rightOffset, 20, display.auraSound));
        //        scrollWindow.addButton(new GuiNpcButton(11, 3 + 179 + 13 + rightOffset, y, 20, 20, "X"));
        //        scrollWindow.addButton(new GuiNpcButton(1, 3 + 133 + 13 + rightOffset, y, 47, 20, "gui.select"));
        //        scrollWindow.getButton(11).enabled = !display.auraSound.equals("jinryuudragonbc:DBC.aura") && !display.auraSound.isEmpty();
        //        maxScroll += 16;
        //
        //
        //        y += 23;
        //        scrollWindow.addLabel(new GuiNpcLabel(2, "general.kaiokenSound", 3, y + 5));
        //        scrollWindow.getLabel(2).color = 0xffffff;
        //        y += 16;
        //        scrollWindow.addTextField(new GuiNpcTextField(2, this, fontRendererObj, 3, y, 145 + rightOffset, 20, display.kaiokenSound));
        //        scrollWindow.addButton(new GuiNpcButton(21, 3 + 179 + 13 + rightOffset, y, 20, 20, "X"));
        //        scrollWindow.addButton(new GuiNpcButton(2, 3 + 133 + 13 + rightOffset, y, 47, 20, "gui.select"));
        //        scrollWindow.getButton(21).enabled = !display.kaiokenSound.isEmpty();
        //        maxScroll += 23;

        y += 50;
        maxScroll += 50;
        scrollWindow.addLabel(new GuiNpcLabel(1206, "display.secondaryAura", 3, y + 5));
        scrollWindow.getLabel(1206).color = 0xffffff;
        scrollWindow.addButton(new GuiNpcButton(1306, guiX + 100 + rightOffset, y, 100, 20, "display.selectAura"));
        if (aura.secondaryAuraID != -1 && AuraController.getInstance().has(aura.secondaryAuraID))
            scrollWindow.getButton(1306).setDisplayText(AuraController.getInstance().get(aura.secondaryAuraID).getName());

        scrollWindow.addButton(new GuiNpcButton(1406, guiX + 200 + rightOffset, y, 20, 20, "X"));
        scrollWindow.getButton(1406).enabled = aura.secondaryAuraID != -1;

        maxScroll += 23;
        y += 23;
        scrollWindow.addLabel(new GuiNpcLabel(2206, "display.outline", 3, y + 5));
        scrollWindow.getLabel(2206).color = 0xffffff;
        scrollWindow.addButton(new GuiNpcButton(2306, guiX + 100 + rightOffset, y, 100, 20, "display.selectOutline"));
        if (display.outlineID != -1 && OutlineController.getInstance().has(display.outlineID))
            scrollWindow.getButton(2306).setDisplayText(OutlineController.getInstance().get(display.outlineID).getName());
        scrollWindow.addButton(new GuiNpcButton(2406, guiX + 200 + rightOffset, y, 20, 20, "X"));
        scrollWindow.getButton(2406).enabled = display.outlineID != -1;

        maxScroll += 50;
        y += 50;
        scrollWindow.addLabel(new GuiNpcLabel(3006, "display.3DType", 3, y + 5));
        scrollWindow.getLabel(3006).color = 0xffffff;
        scrollWindow.addButton(new GuiButtonBiDirectional(3006, guiX + 120 + rightOffset, y, 100, 20, new String[]{"auratype.none", "auratype.base", "auratype.ssgod", "auratype.ssb", "auratype.ssbevo", "auratype.ssrose", "auratype.ssroseevo", "auratype.ultimate", "auratype.ui", "auratype.god"}, display.type.ordinal()));

        maxScroll += 23;
        y += 23;
        scrollWindow.addLabel(new GuiNpcLabel(3007, "display.2DType", 3, y + 5));
        scrollWindow.getLabel(3007).color = 0xffffff;
        scrollWindow.addButton(new GuiButtonBiDirectional(3007, guiX + 120 + rightOffset, y, 100, 20, new String[]{"auratype.none", "auratype.default", "auratype.base", "auratype.ssgod", "auratype.ssb", "auratype.ssbevo", "auratype.ssrose", "auratype.ssroseevo", "auratype.ultimate", "auratype.ui", "auratype.mui", "auratype.god", "auratype.god_toppo", "auratype.jiren"}, display.type2D.ordinal()));

        maxScroll += 2;
        y += 2;


        //  y += 10;
        y += 23;
        //  maxScroll += 23;

        EnumAuraTypes3D t = display.type;
        EnumAuraTypes2D t2 = display.type2D;
        if (t != None || t2 != EnumAuraTypes2D.None) {
            if (t != None || t == None && (t2 == EnumAuraTypes2D.Default || t2 == EnumAuraTypes2D.Base)) {
                scrollWindow.addLabel(new GuiNpcLabel(3000, "display.color1", 3, y + 5));
                scrollWindow.getLabel(3000).color = 0xffffff;
                scrollWindow.addButton(new GuiNpcButton(3000, guiX + 140 + rightOffset, y, 60, 20, getColor(display.color1)));
                scrollWindow.getButton(3000).enabled = t != None || (t2 == EnumAuraTypes2D.Default || t2 == EnumAuraTypes2D.Base);

                scrollWindow.getButton(3000).packedFGColour = display.color1;
                scrollWindow.addButton(new GuiNpcButton(3100, guiX + 200 + rightOffset, y, 20, 20, "X"));
                scrollWindow.getButton(3100).enabled = display.color1 != -1;

                y += 23;
                scrollWindow.addLabel(new GuiNpcLabel(3001, "display.color2", 3, y + 5));
                scrollWindow.getLabel(3001).color = 0xffffff;
                scrollWindow.addButton(new GuiNpcButton(3001, guiX + 140 + rightOffset, y, 60, 20, getColor(display.color2)));
                scrollWindow.getButton(3001).enabled = t == SaiyanGod || t == SaiyanRose || t == UltimateArco;

                scrollWindow.getButton(3001).packedFGColour = display.color2;
                scrollWindow.addButton(new GuiNpcButton(3101, guiX + 200 + rightOffset, y, 20, 20, "X"));
                scrollWindow.getButton(3101).enabled = display.color2 != -1;

                maxScroll += 23;
                y += 23;
                scrollWindow.addLabel(new GuiNpcLabel(3002, "display.color3", 3, y + 5));
                scrollWindow.getLabel(3002).color = 0xffffff;
                scrollWindow.addButton(new GuiNpcButton(3002, guiX + 140 + rightOffset, y, 60, 20, getColor(display.color3)));
                scrollWindow.getButton(3002).enabled = t == None && (t2 == EnumAuraTypes2D.Default || t2 == EnumAuraTypes2D.Base) || t == Base && (t2 == EnumAuraTypes2D.Default || t2 == EnumAuraTypes2D.Base) || t == SaiyanBlue || t == SaiyanBlueEvo || t == SaiyanRoseEvo || t == UI || t == GoD;

                scrollWindow.getButton(3002).packedFGColour = display.color3;
                scrollWindow.addButton(new GuiNpcButton(3102, guiX + 200 + rightOffset, y, 20, 20, "X"));
                scrollWindow.getButton(3102).enabled = display.color3 != -1;

                if (t != None) {
                    maxScroll += 23;
                    y += 23;
                    scrollWindow.addLabel(new GuiNpcLabel(2102, "display.alpha", 3, y + 5));
                    scrollWindow.getLabel(2102).color = 0xffffff;
                    scrollWindow.addTextField(new GuiNpcTextField(202, this, guiX + 165 + rightOffset, y, 33, 18, String.valueOf(display.alpha)));
                    scrollWindow.getTextField(202).setMaxStringLength(4);
                    scrollWindow.getTextField(202).integersOnly = true;
                    scrollWindow.getTextField(202).setMinMaxDefault(-1, 255, -1);
                    scrollWindow.addButton(new GuiNpcButton(2102, guiX + 200 + rightOffset, y - 1, 20, 20, "X"));
                    scrollWindow.getButton(2102).enabled = display.alpha != -1;


                    maxScroll += 23;
                    y += 23;
                    scrollWindow.addLabel(new GuiNpcLabel(200, "display.size", 3, y + 5));
                    scrollWindow.getLabel(200).color = 0xffffff;
                    scrollWindow.addTextField(new GuiNpcTextField(200, this, guiX + 165 + rightOffset, y, 33, 18, String.valueOf(display.size)));
                    scrollWindow.getTextField(200).setMaxStringLength(10);
                    scrollWindow.getTextField(200).floatsOnly = true;
                    scrollWindow.getTextField(200).setMinMaxDefaultFloat(-10000f, 10, 1.0f);
                    scrollWindow.addButton(new GuiNpcButton(2100, guiX + 200 + rightOffset, y - 1, 20, 20, "X"));
                    scrollWindow.getButton(2100).enabled = display.size != 1;


                    maxScroll += 23;
                    y += 23;
                    scrollWindow.addLabel(new GuiNpcLabel(201, "display.speed", 3, y + 5));
                    scrollWindow.getLabel(201).color = 0xffffff;
                    scrollWindow.addTextField(new GuiNpcTextField(201, this, guiX + 165 + rightOffset, y, 33, 18, String.valueOf(display.speed)));
                    scrollWindow.getTextField(201).setMaxStringLength(10);
                    scrollWindow.getTextField(201).integersOnly = true;
                    scrollWindow.getTextField(201).setMinMaxDefaultFloat(-10000f, 10000f, 1.0f);
                    scrollWindow.addButton(new GuiNpcButton(2101, guiX + 200 + rightOffset, y - 1, 20, 20, "X"));
                    scrollWindow.getButton(2101).enabled = display.speed != -1;
                }
                y += 50;
            } else {
                y += 23;
                maxScroll -= 50;
            }
        } else {
            y += 23;
            maxScroll -= 50;
        }


        scrollWindow.addLabel(new GuiNpcLabel(202, "display.hasLightning", 3, y + 5));
        scrollWindow.getLabel(202).color = 0xffffff;
        scrollWindow.addButton(new GuiNpcButtonYesNo(202, guiX + 140 + rightOffset, y, 80, 20, display.hasLightning));

        if (display.hasLightning) {
            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(109, "display.lightningColor", 3, y + 5));
            scrollWindow.getLabel(109).color = 0xffffff;
            scrollWindow.addButton(new GuiNpcButton(109, guiX + 140 + rightOffset, y, 60, 20, getColor(display.lightningColor)));
            scrollWindow.getButton(109).packedFGColour = display.lightningColor;
            scrollWindow.addButton(new GuiNpcButton(1109, guiX + 200 + rightOffset, y, 20, 20, "X"));
            scrollWindow.getButton(1109).enabled = display.lightningColor != -1;

            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(203, "display.lightningAlpha", 3, y + 5));
            scrollWindow.getLabel(203).color = 0xffffff;
            scrollWindow.addTextField(new GuiNpcTextField(203, this, guiX + 165 + rightOffset, y, 33, 18, String.valueOf(display.lightningAlpha)));
            scrollWindow.getTextField(203).setMaxStringLength(4);
            scrollWindow.getTextField(203).integersOnly = true;
            scrollWindow.getTextField(203).setMinMaxDefault(0, 255, -1);
            scrollWindow.addButton(new GuiNpcButton(2103, guiX + 200 + rightOffset, y - 1, 20, 20, "X"));
            scrollWindow.getButton(2103).enabled = display.lightningAlpha != 255;

            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(204, "display.lightningSpeed", 3, y + 5));
            scrollWindow.getLabel(204).color = 0xffffff;
            scrollWindow.addTextField(new GuiNpcTextField(204, this, guiX + 165 + rightOffset, y, 33, 18, String.valueOf(display.lightningSpeed)));
            scrollWindow.getTextField(204).setMaxStringLength(4);
            scrollWindow.getTextField(204).integersOnly = true;
            scrollWindow.getTextField(204).setMinMaxDefault(-1, 8, -1);
            scrollWindow.addButton(new GuiNpcButton(2104, guiX + 200 + rightOffset, y - 1, 20, 20, "X"));
            scrollWindow.getButton(2104).enabled = display.lightningSpeed != -1;

            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(205, "display.lightningIntensity", 3, y + 5));
            scrollWindow.getLabel(205).color = 0xffffff;
            scrollWindow.addTextField(new GuiNpcTextField(205, this, guiX + 165 + rightOffset, y, 33, 18, String.valueOf(display.lightningIntensity)));
            scrollWindow.getTextField(205).setMaxStringLength(4);
            scrollWindow.getTextField(205).integersOnly = true;
            scrollWindow.getTextField(205).setMinMaxDefault(-1, 8, -1);
            scrollWindow.addButton(new GuiNpcButton(2105, guiX + 200 + rightOffset, y - 1, 20, 20, "X"));
            scrollWindow.getButton(2105).enabled = display.lightningIntensity != -1;
        }

        y += 50;
        scrollWindow.addLabel(new GuiNpcLabel(302, "display.hasKaioken", 3, y + 5));
        scrollWindow.getLabel(302).color = 0xffffff;
        scrollWindow.addButton(new GuiNpcButtonYesNo(302, guiX + 140 + rightOffset, y, 80, 20, display.hasKaiokenAura));

        if (display.hasKaiokenAura) {
            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(310, "display.viewKaioken", 3, y + 5));
            scrollWindow.getLabel(310).color = 0xffffff;
            scrollWindow.addButton(new GuiNpcButtonYesNo(310, guiX + 140 + rightOffset, y, 80, 20, visualDisplay.isKaioken));

            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(305, "display.kaiokenOverride", 3, y + 5));
            scrollWindow.getLabel(305).color = 0xffffff;
            scrollWindow.addButton(new GuiNpcButtonYesNo(305, guiX + 140 + rightOffset, y, 80, 20, display.kaiokenOverrides));

            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(309, "display.kaiokenColor", 3, y + 5));
            scrollWindow.getLabel(309).color = 0xffffff;
            scrollWindow.addButton(new GuiNpcButton(309, guiX + 140 + rightOffset, y, 60, 20, getColor(display.kaiokenColor)));
            scrollWindow.getButton(309).packedFGColour = display.kaiokenColor;
            scrollWindow.addButton(new GuiNpcButton(1309, guiX + 200 + rightOffset, y, 20, 20, "X"));
            scrollWindow.getButton(1309).enabled = display.kaiokenColor != -1;

            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(303, "display.kaiokenAlpha", 3, y + 5));
            scrollWindow.getLabel(303).color = 0xffffff;
            scrollWindow.addTextField(new GuiNpcTextField(303, this, guiX + 165 + rightOffset, y, 33, 18, String.valueOf(display.kaiokenAlpha)));
            scrollWindow.getTextField(303).setMaxStringLength(4);
            scrollWindow.getTextField(303).integersOnly = true;
            scrollWindow.getTextField(303).setMinMaxDefault(-1, 255, -1);
            scrollWindow.addButton(new GuiNpcButton(3103, guiX + 200 + rightOffset, y - 1, 20, 20, "X"));
            scrollWindow.getButton(3103).enabled = display.kaiokenAlpha != -1;

            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(304, "display.kaiokenSize", 3, y + 5));
            scrollWindow.getLabel(304).color = 0xffffff;
            scrollWindow.addTextField(new GuiNpcTextField(304, this, guiX + 165 + rightOffset, y, 33, 18, String.valueOf(display.kaiokenSize)));
            scrollWindow.getTextField(304).setMaxStringLength(4);
            scrollWindow.getTextField(304).floatsOnly = true;
            scrollWindow.getTextField(304).setMinMaxDefault(-1, 5, 1);
            scrollWindow.addButton(new GuiNpcButton(3104, guiX + 200 + rightOffset, y - 1, 20, 20, "X"));
            scrollWindow.getButton(3104).enabled = display.kaiokenSize != 1;
        }


        maxScroll += 10;
        y += 50;
        scrollWindow.addLabel(new GuiNpcLabel(402, "display.kettleModeEnabled", 3, y + 5));
        scrollWindow.getLabel(402).color = 0xffffff;
        scrollWindow.addButton(new GuiNpcButtonYesNo(402, guiX + 140 + rightOffset, y, 80, 20, display.kettleModeEnabled));

        if (display.kettleModeEnabled) {
            maxScroll += 23;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(400, "display.kettleModeCharging", 3, y + 5));
            scrollWindow.getLabel(400).color = 0xffffff;
            scrollWindow.addButton(new GuiNpcButtonYesNo(400, guiX + 140 + rightOffset, y, 80, 20, display.kettleModeCharging));
        }

        maxScroll += 23 * 3 + 10;

        y = guiTop + 186;

        addLabel(new GuiNpcLabel(1, "general.auraSound", guiLeft - 25, y + 5));
        getLabel(1).color = 0xffffff;
        y += 16;
        addTextField(new GuiNpcTextField(1, this, fontRendererObj, guiLeft - 25, y, 186, 20, display.auraSound));
        addButton(new GuiNpcButton(1, guiLeft + 163, y, 47, 20, "gui.select"));
        addButton(new GuiNpcButton(11, guiLeft + 209, y, 16, 20, "X"));
        getButton(11).enabled = !display.auraSound.equals("jinryuudragonbc:DBC.aura");

        y += 23;
        addLabel(new GuiNpcLabel(2, "general.kaiokenSound", guiLeft - 25, y + 5));
        getLabel(2).color = 0xffffff;
        y += 16;
        addTextField(new GuiNpcTextField(2, this, fontRendererObj, guiLeft - 25, y, 186, 20, display.kaiokenSound));
        addButton(new GuiNpcButton(2, guiLeft + 163, y, 47, 20, "gui.select"));
        addButton(new GuiNpcButton(21, guiLeft + 209, y, 16, 20, "X"));
        getButton(21).enabled = !display.kaiokenSound.isEmpty();

        //   scrollWindow.addButton(new GuiNpcButton(10000, guiLeft + 60, guiTop + 200 + this.yOffset, 75, 20, "gui.done"));
        int yOffset = this.yOffset + 10;
        this.addButton(this.unzoom = new GuiNpcButton(666, this.guiLeft + 148 + this.xOffset, this.guiTop + 200 + yOffset, 20, 20, "-"));
        this.addButton(this.zoom = new GuiNpcButton(667, this.guiLeft + 214 + this.xOffset, this.guiTop + 200 + yOffset, 20, 20, "+"));
        this.addButton(this.left = new GuiNpcButton(668, this.guiLeft + 170 + this.xOffset, this.guiTop + 200 + yOffset, 20, 20, "<"));
        this.addButton(this.right = new GuiNpcButton(669, this.guiLeft + 192 + this.xOffset, this.guiTop + 200 + yOffset, 20, 20, ">"));

        addButton(new GuiNpcButton(670, this.guiLeft + 165 + this.xOffset, this.guiTop + 222 + yOffset, 55, 20, new String[]{"display.dbcAura", "display.revampAura"}, revampedAura));
        scrollWindow.maxScrollY = maxScroll;
    }

    public void buttonEvent(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (button.id == 10000) {
            parent.closeSubGui(null); // Close the GUI
        } else if (button.id == 2000) {
            selectedTab = 0;
            initGui();
        } else if (button.id == 2001) {
            selectedTab = 1;
            initGui();
        } else if (button.id == 2002) {
            selectedTab = 2;
            initGui();
        } else if (button.id == 3004) {
            display.overrideDBCAura = !display.overrideDBCAura;
            initGui();
        } else if (button.id == 3000) {
            // Change color1
            lastColorClicked = 0;
            setSubGui(new SubGuiColorSelector(display.color1));
        } else if (button.id == 3100) {
            // Clear color1
            display.color1 = -1;
            initGui();
        } else if (button.id == 3001) {
            lastColorClicked = 1;
            setSubGui(new SubGuiColorSelector(display.color2));
        } else if (button.id == 3101) {
            // Clear color2
            display.color2 = -1;
            initGui();
        } else if (button.id == 3002) {
            // Change color3
            lastColorClicked = 2;
            setSubGui(new SubGuiColorSelector(display.color3));
        } else if (button.id == 3102) {
            // Clear color3
            display.color3 = -1;
            initGui();
        } else if (button.id == 3006) {
            // Change 3D Type
            display.type = EnumAuraTypes3D.values()[button.getValue()];
            initGui();
        } else if (button.id == 3007) {
            // Change 2D Type
            int val = button.getValue();
            display.type2D = EnumAuraTypes2D.values()[val <= 13 ? val : 0];
            initGui();
        } else if (button.id == 202) {
            // Toggle Lightning
            display.hasLightning = !display.hasLightning;
            initGui();
        } else if (button.id == 109) {
            // Change Lightning color
            lastColorClicked = 3;
            setSubGui(new SubGuiColorSelector(display.lightningColor));
        } else if (button.id == 1109) {
            // Clear Lightning color
            display.lightningColor = -1;
            initGui();
        } else if (button.id == 302) {
            // Toggle Kaioken Aura
            boolean on = button.getValue() == 1;
            display.hasKaiokenAura = !display.hasKaiokenAura;
            if (!on) {
                visualDisplay.isKaioken = false;
                parent.stopSound(parent.kaiokenSound, false);
            }

            initGui();
        } else if (button.id == 310) {
            // Toggle Kaioken Aura
            boolean is = button.getValue() == 1;
            visualDisplay.isKaioken = is;

            if (is)
                parent.playSound(false);
            else if (parent.kaiokenSound != null) {
                parent.stopSound(parent.kaiokenSound, false);
            }

            initGui();
        } else if (button.id == 309) {
            // Change Kaioken color
            lastColorClicked = 4;
            setSubGui(new SubGuiColorSelector(display.kaiokenColor));
        } else if (button.id == 1309) {
            // Clear Kaioken color
            display.kaiokenColor = -1;
            initGui();
        } else if (button.id == 305) {
            // Toggle Kaioken Override
            display.kaiokenOverrides = !display.kaiokenOverrides;
            initGui();
        } else if (button.id == 400) {
            // Toggle Kettle Charging
            display.kettleModeCharging = !display.kettleModeCharging;
            initGui();
        } else if (button.id == 401) {
            // Toggle Kettle Charging
            display.kettleModeEnabled = !display.kettleModeEnabled;
            initGui();
        } else if (button.id == 402) {
            // Toggle Kettle Charging
            display.kettleModeEnabled = !display.kettleModeEnabled;
            if (display.kettleModeEnabled)
                parent.playSound(false);
            else if (parent.kettleSound != null) {
                parent.stopSound(parent.kettleSound, false);
            }
            initGui();
        } else if (button.id == 670) {
            revampedAura = button.getValue();
        } else if (button.id == 2102) {
            // Toggle Kettle Charging
            display.alpha = -1;
            initGui();
        } else if (button.id == 2100) {
            // Toggle Kettle Charging
            display.size = 1;
            initGui();
        } else if (button.id == 2101) {
            // Toggle Kettle Charging
            display.speed = -1;
            initGui();
        } else if (button.id == 2103) {
            // Toggle Kettle Charging
            display.lightningAlpha = 255;
            initGui();
        } else if (button.id == 2104) {
            // Toggle Kettle Charging
            display.lightningSpeed = -1;
            initGui();
        } else if (button.id == 2105) {
            // Toggle Kettle Charging
            display.lightningIntensity = -1;
            initGui();
        } else if (button.id == 3103) {
            // Toggle Kettle Charging
            display.kaiokenAlpha = -1;
            initGui();
        } else if (button.id == 3104) {
            // Toggle Kettle Charging
            display.kaiokenSize = 1;
            initGui();
        } else if (button.id == 1306) {
            this.setSubGui(new SubGuiSelectAura());
        } else if (button.id == 1406) {
            aura.secondaryAuraID = -1;
            parent.stopSound(parent.secondarySound, false);
            parent.stopSound(parent.secondaryKettleSound, false);
            initGui();
        } else if (button.id == 2306) {
            this.setSubGui(new SubGuiSelectOutline());
        } else if (button.id == 2406) {
            display.outlineID = -1;
            visualDisplay.outlineID = -1;
            initGui();
        } else if (button.id == 1) {
            setNormalSound = true;
            setSubGui(new GuiSoundSelection((getTextField(1).getText())));
        } else if (button.id == 11) {
            display.auraSound = "jinryuudragonbc:DBC.aura";
            parent.stopSound(parent.auraSound, false);
            parent.playSound(false);
            initGui();
        } else if (button.id == 2) {
            setNormalSound = false;
            setSubGui(new GuiSoundSelection((getTextField(2).getText())));
        } else if (button.id == 21) {
            display.kaiokenSound = "";
            parent.stopSound(parent.kaiokenSound, false);
            parent.playSound(false);
            initGui();
        }
    }

    @Override
    public void keyTyped(char c, int i) {
        super.keyTyped(c, i);
        if (i == 1) {
            close();
        }
    }

    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {
        if (guiNpcTextField.id == 101) {
            String name = guiNpcTextField.getText();
            if (!name.isEmpty() && !parent.data.containsKey(name)) {
                String old = parent.aura.name;
                parent.data.remove(parent.aura.name);
                parent.aura.name = name;
                parent.data.put(parent.aura.name, parent.aura.id);
                parent.selected = name;
                parent.scrollAuras.replace(old, parent.aura.name);
            } else
                guiNpcTextField.setText(aura.name);
        } else if (guiNpcTextField.id == 102) {
            String menuName = guiNpcTextField.getText();
            if (!menuName.isEmpty()) {
                parent.aura.menuName = menuName.replaceAll("&", "§");
            }
        }
        if (guiNpcTextField.id == 202) {
            // TextField for alpha in the "General" tab
            display.alpha = guiNpcTextField.getInteger();
            scrollWindow.getButton(2102).enabled = display.alpha != -1;
        } else if (guiNpcTextField.id == 200) {
            display.setSize(guiNpcTextField.getFloat());
            scrollWindow.getButton(2100).enabled = display.size != 1;
        } else if (guiNpcTextField.id == 201) {
            // Speed
            display.speed = guiNpcTextField.getInteger();
            scrollWindow.getButton(2101).enabled = display.speed != -1;
        } else if (guiNpcTextField.id == 203) {
            // TextField for lightning alpha in the "Lightning" tab
            display.lightningAlpha = guiNpcTextField.getInteger();
            scrollWindow.getButton(2103).enabled = display.lightningAlpha != 255;
        } else if (guiNpcTextField.id == 204) {
            // TextField for lightning speed in the "Lightning" tab
            display.lightningSpeed = guiNpcTextField.getInteger();
            scrollWindow.getButton(2104).enabled = display.lightningSpeed != -1;
        } else if (guiNpcTextField.id == 205) {
            // TextField for lightning intensity in the "Lightning" tab
            display.lightningIntensity = guiNpcTextField.getInteger();
            scrollWindow.getButton(2105).enabled = display.lightningIntensity != -1;
        } else if (guiNpcTextField.id == 303) {
            // TextField for kaioken alpha in the "Extra" tab
            display.kaiokenAlpha = guiNpcTextField.getInteger();
            scrollWindow.getButton(3103).enabled = display.kaiokenAlpha != -1;
        } else if (guiNpcTextField.id == 304) {
            // TextField for kaioken size in the "Extra" tab
            display.kaiokenSize = guiNpcTextField.getFloat();
            scrollWindow.getButton(3104).enabled = display.kaiokenSize != 1;
        } else if (guiNpcTextField.id == 1) {
            display.auraSound = guiNpcTextField.getText();
            scrollWindow.getButton(11).enabled = !display.auraSound.equals("jinryuudragonbc:DBC.aura");
            parent.stopSound(parent.auraSound, false);
            parent.playSound(false);
        } else if (guiNpcTextField.id == 2) {
            display.kaiokenSound = guiNpcTextField.getText();
            scrollWindow.getButton(21).enabled = !display.kaiokenSound.isEmpty();
            parent.stopSound(parent.kaiokenSound, false);
            parent.playSound(false);
        }
    }

    @Override
    public void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
    }

    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof SubGuiColorSelector) {
            int color = ((SubGuiColorSelector) subgui).color;
            if (lastColorClicked == 0) {
                display.color1 = color;
            } else if (lastColorClicked == 1) {
                display.color2 = color;
            } else if (lastColorClicked == 2) {
                display.color3 = color;
            } else if (lastColorClicked == 3) {
                display.lightningColor = color;
            } else if (lastColorClicked == 4) {
                display.kaiokenColor = color;
            }
            initGui();
        } else if (subgui instanceof SubGuiSelectAura) {
            if (aura != null) {
                SubGuiSelectAura guiSelectForm = ((SubGuiSelectAura) subgui);
                if (guiSelectForm.confirmed) {
                    if (guiSelectForm.selectedAuraID == aura.secondaryAuraID)
                        return;
                    aura.secondaryAuraID = guiSelectForm.selectedAuraID;
                    parent.playSound(false);
                    initGui();
                }
            }
        } else if (subgui instanceof SubGuiSelectOutline) {
            if (aura != null) {
                SubGuiSelectOutline selectOutline = ((SubGuiSelectOutline) subgui);
                if (selectOutline.confirmed) {
                    if (selectOutline.selectedOutlineID == display.outlineID)
                        return;
                    display.outlineID = selectOutline.selectedOutlineID;
                    visualDisplay.outlineID = display.outlineID;
                    initGui();
                }
            }
        } else if (subgui instanceof GuiSoundSelection) {
            GuiSoundSelection gss = (GuiSoundSelection) subgui;
            if (gss.selectedResource != null) {
                String soundLoc = gss.selectedResource.toString();
                if (setNormalSound) {
                    if (display.auraSound.equals(soundLoc))
                        return;
                    getTextField(1).setText(soundLoc);
                    unFocused(getTextField(1));
                } else {
                    if (display.kaiokenSound.equals(soundLoc))
                        return;
                    getTextField(2).setText(gss.selectedResource.toString());
                    unFocused(getTextField(2));
                }
                //  initGui();
            }
        }
    }

    @Override
    protected void drawBackground() {
        super.drawBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public boolean isMouseOverRenderer(int x, int y) {
        return x >= guiLeft + xOffset + 110 && x <= guiLeft + xOffset + 300 && y >= guiTop - 120 + yOffset - 5 && y <= guiTop + 50 + yOffset - 5 + 310;
    }

    public void drawScreen(int par1, int par2, float par3) {
        if (Mouse.isButtonDown(0)) {
            if (this.left.mousePressed(this.mc, par1, par2)) {
                rotation += par3 * 1.5F;
            } else if (this.right.mousePressed(this.mc, par1, par2)) {
                rotation -= par3 * 1.5F;
            } else if (this.zoom.mousePressed(this.mc, par1, par2) && zoomed < 100.0F) {
                zoomed += par3 * 1.0F;
            } else if (this.unzoom.mousePressed(this.mc, par1, par2) && zoomed > 10.0F) {
                zoomed -= par3 * 1.0F;
            }
        }

        if (isMouseOverRenderer(par1, par2)) {
            zoomed += Mouse.getDWheel() * 0.035f;
            if (zoomed > 100)
                zoomed = 100;
            if (zoomed < 10)
                zoomed = 10;

            if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
                rotation -= Mouse.getDX() * 0.75f;
            }
        }

        super.drawScreen(par1, par2, par3);
        if (hasSubGui())
            return;

        int ticks = Minecraft.getMinecraft().thePlayer.ticksExisted;
        if (auraTicks != ticks) {
            renderAura = true;
        }

        useGUIAura = true;
        if (ticks % 5 == 0 && renderAura) {
            EntityAura enhancedAura = visualDisplay.auraEntity;
            boolean kaioken = visualDisplay.isKaioken;
            if (revampedAura == 1) {
                if (enhancedAura == null) {
                    enhancedAura = new EntityAura(npc, aura).load(true).spawn();
                    enhancedAura.isInKaioken = kaioken;
                    enhancedAura.isGUIAura = true;
                } else {
                    if (ticks % 10 == 0) {
                        enhancedAura.load(true);
                        if (kaioken) {
                            EntityAura kaiokenAura = enhancedAura.children.get("Kaioken");
                            if (kaiokenAura != null)
                                kaiokenAura.loadKaioken();
                        }
                    }
                }
            } else {
                if (enhancedAura != null)
                    enhancedAura.despawn();

                if (kaioken && display.kaiokenOverrides) {
                    spawnKaiokenAura(aura, visualDisplay);
                } else {
                    spawnAura(npc, aura);
                    if (aura.hasSecondaryAura())
                        spawnAura(npc, aura.getSecondaryAur());
                    if (kaioken)
                        spawnKaiokenAura(aura, visualDisplay);
                }
            }
            auraTicks = ticks;
            renderAura = false;
        }

        //display.outlineID = -1;
        GL11.glColor4f(1, 1, 1, 1);
        EntityLivingBase entity = this.npc; // DBCData.getClient().player;//

        int l = guiLeft + 190 + xOffset;
        int i1 = guiTop + 180 + yOffset - 5;

        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef(l, i1, 60F);

        GL11.glScalef(-zoomed, zoomed, zoomed);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f7 = entity.rotationYawHead;
        float f5 = (float) (l) - par1;
        float f6 = (float) (i1 - 50) - par2;
        GL11.glRotatef(135F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-(float) Math.atan(f6 / 800F) * 20F, 1.0F, 0.0F, 0.0F);
        entity.prevRenderYawOffset = entity.renderYawOffset = rotation;
        entity.prevRotationYaw = entity.rotationYaw = (float) Math.atan(f5 / 80F) * 40F + rotation;
        entity.rotationPitch = -(float) Math.atan(f6 / 80F) * 20F;
        entity.prevRotationYawHead = entity.rotationYawHead = entity.rotationYaw;
        GL11.glTranslatef(0.0F, entity.yOffset, 1F);
        RenderManager.instance.playerViewY = 180F;


        // Render Entity
        try {
            RenderManager.instance.renderEntityWithPosYaw(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F);
        } catch (Exception ignored) {
        }
        useGUIAura = false;

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

    /**
     * FIXME: Remove this function after ScrollWindow gets fixed in main CNPC+.
     */
    @Override
    public void updateScreen() {
        if (scrollWindow != null) {
            if (scrollWindow.scrollY > scrollWindow.maxScrollY || scrollWindow.nextScrollY > scrollWindow.maxScrollY) {
                scrollWindow.nextScrollY = scrollWindow.maxScrollY;
                scrollWindow.scrollY = scrollWindow.maxScrollY;
            }

            if (scrollWindow.nextScrollY < 0.0F) {
                scrollWindow.nextScrollY = 0.0F;
            }
        }

        super.updateScreen();
    }

    @Override
    public void selected(int id, String name) {
    }

    public void save() {
    }

    public void close() {
        NoppesUtil.openGUI(player, parent);
        if (visualDisplay.auraEntity != null)
            visualDisplay.auraEntity.despawn();


        parent.visualDisplay.auraID = aura.id;

        parent.visualDisplay.isKaioken = false;
        parent.stopSound(parent.kaiokenSound, false);
        aura = null;
        save();
    }

    public String getColor(int input) {
        String str;
        for (str = Integer.toHexString(input); str.length() < 6; str = "0" + str) {
        }
        return str;
    }
}
