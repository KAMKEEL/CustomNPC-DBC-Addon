package kamkeel.npcdbc.client.gui.global.form;

import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.client.gui.component.SubGuiFormFaceParts;
import kamkeel.npcdbc.client.gui.component.SubGuiOverlays;
import kamkeel.npcdbc.client.gui.component.SubGuiSelectAura;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.form.OverlayManager;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.client.ClientEventHandler;
import noppes.npcs.client.gui.SubGuiColorSelector;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityCustomNpc;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.awt.datatransfer.*;

import static JinRyuu.JRMCore.JRMCoreH.dnsHairG1toG2;

public class SubGuiFormDisplay extends SubGuiInterface implements ISubGuiListener, GuiSelectionListener, ITextfieldListener, ClipboardOwner {
    private final String[] arrRace = new String[]{"display.human", "display.saiyan", "display.halfsaiyan", "display.namekian", "display.arcosian", "display.majin"};
    private final String[] arcoForms = new String[]{"display.arcofirst", "display.arcosecond", "display.arcothird", "display.arcofinal", "display.arcoultimatecooler", "display.arcogoldenform"};
    private final String[] hairTypes = new String[]{"display.base", "display.ssj", "display.ssj2", "display.ssj3", "display.ssj4", "display.oozaru", "display.raditz"};
    private final GuiNpcFormMenu menu;
    public Form form;
    public FormDisplay display;
    public OverlayManager overlays;
    private final DBCDisplay visualDisplay;
    public EntityCustomNpc npc;

    boolean hasRace;
    boolean showAura = false;
    public int auraTicks = 1;
    byte racePage = 0;
    private float rotation = 0.0F;
    private GuiNpcButton left;
    private GuiNpcButton right;
    private float zoomed = 60.0F;
    public int xOffset = 0;
    public int yOffset = 0;
    public int lastColorClicked = 0;

    private GuiScrollWindow window;

    public SubGuiFormDisplay(GuiNPCManageForms parent, Form form) {
        menu = new GuiNpcFormMenu(parent, this, -2, form);

        this.npc = (EntityCustomNpc) parent.npc;
        this.form = parent.form;
        this.display = parent.display;
        this.overlays = display.overlays;
        this.visualDisplay = parent.visualDisplay;

        setBackground("menubg.png");
        xSize = 360;
        ySize = 216;
        xOffset = 100;
        yOffset = -10;

        hasRace = form.race() != -1;
        if (hasRace) {
            racePage = (byte) form.race();
        }
        visualDisplay.race = racePage;
        visualDisplay.setDefaultColors();
        visualDisplay.setRacialExtras();
        visualDisplay.setDefaultHair();
    }

    public void initGui() {
        super.initGui();
        guiTop += 7;
        menu.initGui(guiLeft, guiTop, xSize);

        int y = guiTop + 5;

        raceButtons(y);
        controlButtons();
        visualDisplay.setRacialExtras();
        visualDisplay.setDefaultColors();
    }

    private void raceButtons(int y) {
        int width = 113 + this.xOffset - 10;
        int height = ySize - (y - guiTop) - 2;
        if (window == null) {
            window = new GuiScrollWindow(this, guiLeft + 4, y - 2, width, height, 0);
        } else {
            window.initGui();
            window.xPos = guiLeft + 4;
            window.yPos = y - 2;
            window.clipWidth = width;
            window.clipHeight = height;
            window.maxScrollY = 0;
        }
        width = width - 7;
        addScrollableGui(1000, window);

        IAura aura = AuraController.getInstance().get(display.auraID);

        y = 10;
        int x = 7;

        GuiNpcTextField field;
        GuiNpcButton button;

        window.addLabel(new GuiNpcLabel(200, "display.formSize", x, y, 0xFFFFFF));
        field = new GuiNpcTextField(200, this, width - x - 40, y - 5, 40, 20, String.valueOf(display.formSize));
        field.setMaxStringLength(10);
        field.floatsOnly = true;
        field.setMinMaxDefaultFloat(-10000f, 10000f, 1.0f);
        window.addTextField(field);
        y += 25;

        window.addLabel(new GuiNpcLabel(300, "display.formWidth", x, y, 0xFFFFFF));
        field = new GuiNpcTextField(300, this, width - x - 40, y - 5, 40, 20, String.valueOf(display.formWidth));
        field.setMaxStringLength(10);
        field.floatsOnly = true;
        field.setMinMaxDefaultFloat(-10000f, 10000f, 1.0f);
        window.addTextField(field);

        y += 30;

        window.addLabel(new GuiNpcLabel(5000, "display.formCanFormbeCustomized", x, y, 0xFFFFFF));
        window.addButton(new GuiNpcButtonYesNo(5000, width - x - 40, y - 5, 40, 20, form.display.isCustomizable));

        y += 25;

        button = new GuiNpcButton(1306, x, y, width - x - 20 - 10, 20, "display.selectAura");
        if (aura != null)
            button.setDisplayText(aura.getName());
        window.addButton(button);

        button = new GuiNpcButton(1406, width - x - 20, y, 20, 20, "X");
        button.enabled = aura != null;
        window.addButton(button);

        y += 30;

        window.addLabel(new GuiNpcLabel(106, "display.aura", x, y, 0xFFFFFF));

        button = new GuiNpcButton(106, width - x - 75, y - 5, 50, 20, getColor(display.auraColor));
        button.packedFGColour = display.auraColor;
        button.enabled = aura == null;
        window.addButton(button);

        button = new GuiNpcButton(1106, width - x - 20, y - 5, 20, 20, "X");
        button.enabled = display.auraColor != -1;
        window.addButton(button);

        window.addButton(new GuiNpcButton(1206, x + 50, y - 5, 54, 20, new String[]{"display.hide", "display.show"}, showAura ? 1 : 0));

        y += 30;
        window.addLabel(new GuiNpcLabel(124, "display.kiBarColor", x, y, 0xFFFFFF));

        button = new GuiNpcButton(124, width - x - 75, y - 5, 50, 20, getColor(display.kiBarColor));
        button.packedFGColour = display.kiBarColor;
        window.addButton(button);

        button = new GuiNpcButton(1124, width - x - 20, y - 5, 20, 20, "X");
        button.enabled = display.kiBarColor != -1;
        window.addButton(button);


        y += 30;

        window.addLabel(new GuiNpcLabel(107, "display.eye", x, y, 0xFFFFFF));
        button = new GuiNpcButton(107, width - x - 75, y - 5, 50, 20, getColor(display.bodyColors.eyeColor));
        button.packedFGColour = display.bodyColors.eyeColor;
        window.addButton(button);

        button = new GuiNpcButton(1107, width - x - 20, y - 5, 20, 20, "X");
        button.enabled = display.bodyColors.eyeColor != -1;
        window.addButton(button);

        boolean addPupilOption = ConfigDBCClient.EnableHDTextures && (DBCRace.isSaiyan(visualDisplay.race) || visualDisplay.race == DBCRace.HUMAN);
        int currentIndex = ConfigDBCClient.EnableHDTextures && form.display.hasPupils ? 2 : form.display.isBerserk ? 1 : 0;
        String[] eyeSettingString = addPupilOption ?
            new String[]{"display.normalEye", "display.isBerserk", "display.hasPupils"} :
            new String[]{"display.normalEye", "display.isBerserk"};

        window.addButton(new GuiNpcButton(1072, x + 50, y - 5, 54, 20, eyeSettingString, currentIndex));
        if (DBCRace.isSaiyan(visualDisplay.race) || visualDisplay.race == DBCRace.HUMAN) {
            y += 25;
            window.addLabel(new GuiNpcLabel(1082, "display.hasEyebrows", x, y, 0xFFFFFF));
            window.addButton(new GuiNpcButtonYesNo(1082, width - x - 75, y - 5, 50, 20, form.display.hasEyebrows));
        }

        y += 30;
        window.addLabel(new GuiNpcLabel(108, "model.body", x, y, 0xFFFFFF));
        button = new GuiNpcButton(108, width - x - 75, y - 5, 50, 20, getColor(display.bodyColors.bodyCM));
        button.packedFGColour = display.bodyColors.bodyCM;
        window.addButton(button);

        button = new GuiNpcButton(1108, width - x - 20, y - 5, 20, 20, "X");
        button.enabled = display.bodyColors.bodyCM != -1;
        window.addButton(button);

        if (visualDisplay.race == DBCRace.NAMEKIAN || visualDisplay.race == DBCRace.ARCOSIAN) {
            y = addBodyColors(x, y);
        }

        if (DBCRace.isSaiyan(visualDisplay.race)) {
            y += 25;
            window.addLabel(new GuiNpcLabel(112, "display.fur", x, y, 0xFFFFFF));
            button = new GuiNpcButton(112, width - x - 75, y - 5, 50, 20, getColor(display.bodyColors.furColor));
            button.packedFGColour = display.bodyColors.furColor;
            window.addButton(button);
            button = new GuiNpcButton(1112, width - x - 20, y - 5, 20, 20, "X");
            button.enabled = display.bodyColors.furColor != -1;
            window.addButton(button);

            window.addButton(new GuiNpcButtonYesNo(123, x + 50, y - 5, 54, 20, form.display.hasBodyFur));

            if (display.hasBodyFur) {
                y += 25;
                window.addLabel(new GuiNpcLabel(1113, "Type", x, y, 0xFFFFFF));
                window.addButton(new GuiNpcButton(11132, width - x - 75, y - 5, 50, 20, new String[]{"GT", "Daima", "Legend"}, display.furType));
            }
        }


        if (visualDisplay.race == DBCRace.MAJIN) {
            y += 25;
            window.addLabel(new GuiNpcLabel(115, "display.majinHair", x, y, 0xFFFFFF));
            window.addButton(new GuiNpcButtonYesNo(115, width - x - 75, y - 5, 50, 20, form.display.effectMajinHair));
        }

        if (visualDisplay.race == DBCRace.HUMAN || DBCRace.isSaiyan(visualDisplay.race) || (visualDisplay.race == DBCRace.MAJIN && display.effectMajinHair)) {
            y = addHairOptions(x, y);
        }


        //
        if (visualDisplay.race == DBCRace.ARCOSIAN) {
            y += 30;
            window.addLabel(new GuiNpcLabel(113, "display.arcoMask", x, y, 0xFFFFFF));
            window.addButton(new GuiNpcButtonYesNo(113, width - x - 75, y - 5, 50, 20, display.hasArcoMask));

            int index = getArcoForm();
            y += 25;
            window.addLabel(new GuiNpcLabel(114, "display.form", x, y, 0xFFFFFF));
            window.addButton(new GuiButtonBiDirectional(114, width - x - 75 - 12, y - 5, 73, 20, arcoForms, index));
        }

        y += 30;

        window.addLabel(new GuiNpcLabel(116, "Face Parts", x, y, 0xFFFFFF)); // put translation here
        window.addButton(new GuiNpcButton(116, width - x - 75, y - 5, 50, 20, "Edit"));

        y += 30;

        window.addLabel(new GuiNpcLabel(117, "display.overlays", x, y, 0xFFFFFF));
        window.addButton(new GuiNpcButtonYesNo(117, width - x - 75, y - 5, 50, 20, overlays.enabled));

        if (overlays.enabled) {
            y+= 25;

            window.addButton(new GuiNpcButton(118, width - x - 75, y - 5, 50, 20, "Edit"));
        }

        window.maxScrollY = (y - height) + 20 + 5;
    }

    private int addBodyColors(int x, int y) {
        y += 25;
        int width = window.clipWidth - 7;
        GuiNpcButton button;

        window.addLabel(new GuiNpcLabel(109, "display.bodyc1", x, y, 0xFFFFFF));
        button = new GuiNpcButton(109, width - x - 75, y - 5, 50, 20, getColor(display.bodyColors.bodyC1));
        button.packedFGColour = display.bodyColors.bodyC1;
        window.addButton(button);
        button = new GuiNpcButton(1109, width - x - 20, y - 5, 20, 20, "X");
        button.enabled = display.bodyColors.bodyC1 != -1;
        window.addButton(button);

        y += 25;


        window.addLabel(new GuiNpcLabel(110, "display.bodyc2", x, y, 0xFFFFFF));
        button = new GuiNpcButton(110, width - x - 75, y - 5, 50, 20, getColor(display.bodyColors.bodyC2));
        button.packedFGColour = display.bodyColors.bodyC2;
        window.addButton(button);
        button = new GuiNpcButton(1110, width - x - 20, y - 5, 20, 20, "X");
        button.enabled = display.bodyColors.bodyC2 != -1;
        window.addButton(button);

        y += 25;

        window.addLabel(new GuiNpcLabel(111, "display.bodyc3", x, y, 0xFFFFFF));
        button = new GuiNpcButton(111, width - x - 75, y - 5, 50, 20, getColor(display.bodyColors.bodyC3));
        button.packedFGColour = display.bodyColors.bodyC3;
        window.addButton(button);
        button = new GuiNpcButton(111, width - x - 20, y - 5, 20, 20, "X");
        button.enabled = display.bodyColors.bodyC3 != -1;
        window.addButton(button);

        return y;
    }

    private int addHairOptions(int x, int y) {
        y += 30;

        int width = window.clipWidth - 7;
        GuiNpcButton button;

        window.addLabel(new GuiNpcLabel(100, "display.hair", x, y, 0xFFFFFF));

        button = new GuiNpcButton(104, width - x - 75, y - 5, 50, 20, getColor(display.bodyColors.hairColor));
        button.packedFGColour = display.bodyColors.hairColor;
        window.addButton(button);

        button = new GuiNpcButton(1104, width - x - 20, y - 5, 20, 20, "X");
        button.enabled = display.bodyColors.hairColor != -1;
        window.addButton(button);

        boolean clearEnabled = true;
        if (display.hairCode.isEmpty()) {
            clearEnabled = false;
            window.addButton(new GuiNpcButton(101, x + 50, y - 5, 54, 20, "gui.paste"));
        } else {
            window.addButton(new GuiNpcButton(102, x + 50, y - 5, 54, 20, "gui.copy"));
        }


        y += 25;

        button = new GuiNpcButton(103, x + 50, y - 5, 54, 20, "gui.clear");
        button.enabled = clearEnabled;
        window.addButton(button);


        //        y += 25;


        int index = getHairType();

        window.addButton(new GuiButtonBiDirectional(140, width - x - 75, y - 5, 73, 20, hairTypes, index));

        return y;
    }


    private void controlButtons() {
        addButton(new GuiButtonBiDirectional(1, this.guiLeft + 113 + this.xOffset, this.guiTop + 200 + this.yOffset, 94, 20, arrRace, racePage));
        getButton(1).enabled = !hasRace || form.getRace() == 12;
        addButton(this.left = new GuiNpcButton(668, this.guiLeft + 210 + this.xOffset, this.guiTop + 200 + this.yOffset, 20, 20, "<"));
        addButton(this.right = new GuiNpcButton(669, this.guiLeft + 235 + this.xOffset, this.guiTop + 200 + this.yOffset, 20, 20, ">"));
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        GuiNpcButton button = (GuiNpcButton) btn;
        if (button.id == 1) {
            if (form.getRace() == DBCRace.ALL_SAIYANS) {
                if (racePage == DBCRace.SAIYAN) {
                    racePage = DBCRace.HALFSAIYAN;
                } else {
                    racePage = DBCRace.SAIYAN;
                }
                // Update Button Display to only cycle between Saiyan and Half Saiyan
                button.setDisplay(racePage);
            } else {
                racePage = (byte) button.getValue();
            }
            visualDisplay.race = racePage;
            visualDisplay.setDefaultColors();
            visualDisplay.setRacialExtras();
            visualDisplay.setDefaultHair();
            updateButtons();
        }

        // Can player customize this form
        if (button.id == 5000) {
            display.isCustomizable = button.getValue() == 1;
        }

        // Aura Color
        if (button.id == 106) {
            lastColorClicked = 0;
            setSubGui(new SubGuiColorSelector(display.auraColor));
        }
        // Aura Color Clear
        if (button.id == 1106) {
            display.auraColor = -1;
            updateButtons();
        }
        // Aura Show
        if (button.id == 1206) {
            showAura = !showAura;
            auraTicks = 1;
            updateButtons();
        }
        if (button.id == 1306) {
            this.setSubGui(new SubGuiSelectAura());
        }
        if (button.id == 1406) {
            display.auraID = -1;
            updateButtons();
        }
        // Hud Color
        if (button.id == 124) {
            lastColorClicked = 8;
            setSubGui(new SubGuiColorSelector(display.kiBarColor));
        }
        // Hud Color Clear
        if (button.id == 1124) {
            display.kiBarColor = -1;
            updateButtons();
        }
        // Eye Color
        if (button.id == 107) {
            lastColorClicked = 1;
            setSubGui(new SubGuiColorSelector(display.bodyColors.eyeColor));
        }
        // Eye Color Clear
        if (button.id == 1107) {
            display.bodyColors.eyeColor = -1;
            updateButtons();
        }
        //Berserk
        if (button.id == 1072) {
            display.isBerserk = button.getValue() == 1;
            display.hasPupils = ConfigDBCClient.EnableHDTextures && button.getValue() == 2;
        }
        // Body
        if (button.id == 108) {
            lastColorClicked = 2;
            setSubGui(new SubGuiColorSelector(display.bodyColors.bodyCM));
        }
        // Body Clear
        if (button.id == 1108) {
            display.bodyColors.bodyCM = -1;
            updateButtons();
        }
        //Has Eyebrows
        if (button.id == 1082) {
            display.hasEyebrows = button.getValue() == 1;
        }
        // Body C1
        if (button.id == 109) {
            lastColorClicked = 3;
            setSubGui(new SubGuiColorSelector(display.bodyColors.bodyC1));
        }
        // Body C1 Clear
        if (button.id == 1109) {
            display.bodyColors.bodyC1 = -1;
            updateButtons();
        }
        // Body C2
        if (button.id == 110) {
            lastColorClicked = 4;
            setSubGui(new SubGuiColorSelector(display.bodyColors.bodyC2));
        }
        // Body C2 Clear
        if (button.id == 1110) {
            display.bodyColors.bodyC2 = -1;
            updateButtons();
        }
        // Body C3
        if (button.id == 111) {
            lastColorClicked = 5;
            setSubGui(new SubGuiColorSelector(display.bodyColors.bodyC3));
        }
        // Body C3 Clear
        if (button.id == 1111) {
            display.bodyColors.bodyC3 = -1;
            updateButtons();
        }
        // Fur Color
        if (button.id == 112) {
            lastColorClicked = 6;
            setSubGui(new SubGuiColorSelector(display.bodyColors.furColor));
        }
        // Fur Color Clear
        if (button.id == 1112) {
            display.bodyColors.furColor = -1;
            updateButtons();
        }
        // Majin Hair
        if (button.id == 115) {
            display.effectMajinHair = !display.effectMajinHair;
            updateButtons();
        }
        // Arco Mask
        if (button.id == 113) {
            display.hasArcoMask = !display.hasArcoMask;
            updateButtons();
        }
        // Body Fur
        if (button.id == 123) {
            display.hasBodyFur = !display.hasBodyFur;
            display.furType = 0;
            updateButtons();
        }

        // Form
        if (button.id == 114) {
            display.bodyType = getArcoString(button.getValue());
            updateButtons();
        }

        if (button.id == 11132) {
            display.furType = (display.furType + 1) % 3;
            updateButtons();
        }

        // Disable Face
        if (button.id == 116) {
            setSubGui(new SubGuiFormFaceParts(this));
            updateButtons();
        }

        // Form Overlays
        if (button.id == 117) {
            overlays.enabled = button.getValue() == 1;
            updateButtons();
        }

        if (button.id == 118) {
            setSubGui(new SubGuiOverlays(this));
            updateButtons();
        }

        // Hair Clear
        if (button.id == 103) {
            display.hairCode = "";
            updateButtons();
        }
        // Hair Paste
        if (button.id == 101) {
            String newDNSHair = getClipboardContents();
            if (newDNSHair.length() == 786 || newDNSHair.length() == 784 || newDNSHair.length() == 392) {
                display.hairCode = dnsHairG1toG2(newDNSHair);
                updateButtons();
            } else if (newDNSHair.equalsIgnoreCase("bald")) {
                display.hairCode = "bald";
                updateButtons();
            }
        }
        // Hair Copy
        if (button.id == 102) {
            setClipboardContents(display.hairCode);
        }
        // Hair Color
        if (button.id == 104) {
            lastColorClicked = 7;
            setSubGui(new SubGuiColorSelector(display.bodyColors.hairColor));
        }
        // Hair Color Clear
        if (button.id == 1104) {
            display.bodyColors.hairColor = -1;
            updateButtons();
        }
        //Hair Type
        if (button.id == 140) {
            display.hairType = getHairString(button.getValue());
            visualDisplay.hairType = display.hairType;
            updateButtons();
        }
    }

    private void updateButtons() {
        initGui();
    }

    @Override
    public void keyTyped(char c, int i) {
        super.keyTyped(c, i);
    }

    @Override
    public void unFocused(GuiNpcTextField txtField) {
        if (txtField.id == 200) {
            display.formSize = txtField.getFloat();
        }
        if (txtField.id == 300) {
            display.formWidth = txtField.getFloat();
        }
    }

    @Override
    public void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        if (!hasSubGui())
            menu.mouseClicked(i, j, k);
    }

    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof SubGuiColorSelector) {
            int color = ((SubGuiColorSelector) subgui).color;
            if (lastColorClicked == 0) {
                display.auraColor = color;
            } else if (lastColorClicked == 1) {
                display.bodyColors.eyeColor = color;
            } else if (lastColorClicked == 2) {
                display.bodyColors.bodyCM = color;
            } else if (lastColorClicked == 3) {
                display.bodyColors.bodyC1 = color;
            } else if (lastColorClicked == 4) {
                display.bodyColors.bodyC2 = color;
            } else if (lastColorClicked == 5) {
                display.bodyColors.bodyC3 = color;
            } else if (lastColorClicked == 6) {
                display.bodyColors.furColor = color;
            } else if (lastColorClicked == 7) {
                display.bodyColors.hairColor = color;
            } else if (lastColorClicked == 8) {
                display.kiBarColor = color;
            }

            updateButtons();
        }
        if (subgui instanceof SubGuiSelectAura) {
            if (form != null) {
                SubGuiSelectAura guiSelectForm = ((SubGuiSelectAura) subgui);
                if (guiSelectForm.confirmed) {
                    if (guiSelectForm.selectedAuraID == display.auraID)
                        return;

                    display.auraID = guiSelectForm.selectedAuraID;
                }
            }

            updateButtons();
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public boolean isMouseOverRenderer(int x, int y) {
        return x >= guiLeft + 225 && x <= guiLeft + 225 + 130 && y >= guiTop + 5 && y <= guiTop + 5 + 180;
    }

    public void drawScreen(int par1, int par2, float par3) {
        super.drawScreen(par1, par2, par3);
        if (hasSubGui())
            return;

        if (Mouse.isButtonDown(0)) {
            if (this.left.mousePressed(this.mc, par1, par2)) {
                rotation += par3 * 2.0F;
            } else if (this.right.mousePressed(this.mc, par1, par2)) {
                rotation -= par3 * 2.0F;
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
        menu.drawElements(fontRendererObj, par1, par2, mc, par3);
        GL11.glColor4f(1, 1, 1, 1);

        EntityLivingBase entity = this.npc;

        int l = guiLeft + 190 + xOffset;
        int i1 = guiTop + 180 + yOffset;
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef(l, i1, 50F);

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
        entity.rotationPitch = -(float) Math.atan(f6 / 40F) * 20F;
        entity.prevRotationYawHead = entity.rotationYawHead = entity.rotationYaw;
        GL11.glTranslatef(0.0F, entity.yOffset, 1F);
        RenderManager.instance.playerViewY = 180F;

        auraTicks++;
        ClientEventHandler.renderingEntityInGUI = true;

        // Render Entity
        GL11.glPushMatrix();
        try {
            float formWidth = display.formWidth;
            float formSize = 1;
            float cr = 100;
            float diff = (formWidth - 1.0F) * (float) cr * 0.02F + 1.0F;
            formWidth = formWidth > 1.0F ? diff : formWidth;
            float formScale = formWidth * formSize;
            GL11.glScalef(formScale, formSize, formScale);
            RenderManager.instance.renderEntityWithPosYaw(entity, 0.0, 0.0, 0.0, 0.0F, 1F);
        } catch (Exception ignored) {
        }
        GL11.glPopMatrix();

        ClientEventHandler.renderingEntityInGUI = false;

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
    protected void drawBackground() {
        super.drawBackground();

        int xPosGradient = guiLeft + 225;
        int yPosGradient = guiTop + 5;
        drawGradientRect(xPosGradient, yPosGradient, 130 + xPosGradient, 180 + yPosGradient, 0xc0101010, 0xd0101010);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void selected(int id, String name) {
    }

    public String getColor(int input) {
        String str;
        for (str = Integer.toHexString(input); str.length() < 6; str = "0" + str) {
        }
        return str;
    }

    public String getClipboardContents() {
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents((Object) null);
        boolean hasTransferableText = contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception ignored) {
            }
        }
        return result;
    }

    public void setClipboardContents(String aString) {
        StringSelection stringSelection = new StringSelection(aString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, this);
    }

    private int getArcoForm() {
        int index = 0;
        if (!display.bodyType.isEmpty()) {
            if (display.bodyType.toLowerCase().contains("first"))
                index = 0;
            else if (display.bodyType.toLowerCase().contains("second"))
                index = 1;
            else if (display.bodyType.toLowerCase().contains("third"))
                index = 2;
            else if (display.bodyType.toLowerCase().contains("final"))
                index = 3;
            else if (display.bodyType.toLowerCase().contains("ultimate"))
                index = 4;
            else if (display.bodyType.toLowerCase().contains("golden"))
                index = 5;
        }
        return index;
    }

    private String getArcoString(int i) {
        switch (i) {
            case 0:
                return "firstform";
            case 1:
                return "secondform";
            case 2:
                return "thirdform";
            case 3:
                return "finalform";
            case 4:
                return "ultimatecooler";
            case 5:
                return "golden";
            default:
                return "";
        }
    }

    private int getHairType() {
        int index = 0;
        //  "base", "ssj", "ssj2", "ssj3", "ssj4", "oozaru"
        if (!display.hairType.isEmpty()) {
            if (display.hairType.toLowerCase().contains("base"))
                index = 0;
            else if (display.hairType.equalsIgnoreCase("ssj"))
                index = 1;
            else if (display.hairType.toLowerCase().contains("ssj2"))
                index = 2;
            else if (display.hairType.toLowerCase().contains("ssj3"))
                index = 3;
            else if (display.hairType.toLowerCase().contains("ssj4"))
                index = 4;
            else if (display.hairType.toLowerCase().contains("oozaru"))
                index = 5;
            else if (display.hairType.toLowerCase().contains("raditz"))
                index = 6;
        }
        return index;
    }

    private String getHairString(int i) {
        switch (i) {
            case 0:
                return "base";
            case 1:
                return "ssj";
            case 2:
                return "ssj2";
            case 3:
                return "ssj3";
            case 4:
                return "ssj4";
            case 5:
                return "oozaru";
            case 6:
                return "raditz";
            default:
                return "";
        }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
}
