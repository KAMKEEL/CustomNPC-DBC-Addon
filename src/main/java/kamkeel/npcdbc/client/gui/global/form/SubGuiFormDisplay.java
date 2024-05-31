package kamkeel.npcdbc.client.gui.global.form;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import kamkeel.npcdbc.client.gui.component.SubGuiSelectAura;
import kamkeel.npcdbc.client.gui.component.SubGuiSelectForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes3D;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.IEntityAura;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.gui.SubGuiColorSelector;
import noppes.npcs.client.gui.select.GuiSoundSelection;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.data.ModelData;
import noppes.npcs.entity.data.ModelPartData;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.awt.datatransfer.*;
import java.util.Iterator;

import static JinRyuu.JRMCore.JRMCoreH.dnsHairG1toG2;

public class SubGuiFormDisplay extends SubGuiInterface implements ISubGuiListener, GuiSelectionListener,ITextfieldListener, ClipboardOwner
{
    private final String[] arrRace = new String[]{"Human", "Saiyan", "HalfSaiyan", "Namekian", "Arcosian", "Majin"};
    private final String[] arcoForms = new String[]{"None", "First", "Second", "Third", "Final", "Ultimate"};
    private final String[] hairTypes = new String[]{"None", "Base", "SSJ", "SSJ2", "SSJ3", "SSJ4", "OOZARU"};
    private final GuiNpcFormMenu menu;
	public Form form;
    public FormDisplay display;
    private final DBCDisplay visualDisplay;
    public EntityCustomNpc npc;

    boolean hasRace;
    boolean showAura = false;
    public int auraTicks = 1;
    int racePage = 0;
    private float rotation = 0.0F;
    private GuiNpcButton left;
    private GuiNpcButton right;
    private float zoomed = 60.0F;
    public int xOffset = 0;
    public int yOffset = 0;
    public int lastColorClicked = 0;

    public Form spoofForm;

    public SubGuiFormDisplay(GuiNPCManageForms parent, Form form)
	{
        if(DBCDisplay.fakeForm == null)
            DBCDisplay.fakeForm = new Form(-100, "EXTREME_FAKE_FORM");
        spoofForm = DBCDisplay.fakeForm;

        this.form = form;
        this.display = form.display;

		setBackground("menubg.png");
		xSize = 360;
		ySize = 216;
        xOffset = 100;
        yOffset = -10;

        npc = new EntityCustomNpc(Minecraft.getMinecraft().theWorld);
        npc.display.texture = "customnpcs:textures/entity/humanmale/AnimationBody.png";
        visualDisplay = ((INPCDisplay) npc.display).getDBCDisplay();
        visualDisplay.enabled = true;
        visualDisplay.useSkin = true;
        hasRace = form.race != -1;
        visualDisplay.race = (byte) (form.race == -1 ? 0 : form.race);
        racePage = visualDisplay.race;

        visualDisplay.formID = spoofForm.id;
        refreshValues();

        menu = new GuiNpcFormMenu(parent, this, -2, form);
	}

    public void initGui()
    {
        super.initGui();
        guiTop += 7;
        menu.initGui(guiLeft, guiTop, xSize);

        int y = guiTop + 5;

        raceButtons(y);
        controlButtons();
    }

    private void raceButtons(int y) {
        addLabel(new GuiNpcLabel(200, "display.formSize", guiLeft + 7, y + 5));
        addTextField(new GuiNpcTextField(200, this, guiLeft + 61, y, 30, 20, String.valueOf(display.formSize)));
        getTextField(200).setMaxStringLength(10);
        getTextField(200).floatsOnly = true;
        getTextField(200).setMinMaxDefaultFloat(-10000f, 10000f, 1.0f);

        addButton(new GuiNpcButton(1306, guiLeft + 101, y, 100, 20,  "display.selectAura"));
        if(display.auraID != -1 && AuraController.getInstance().has(display.auraID))
            getButton(1306).setDisplayText(AuraController.getInstance().get(display.auraID).getName());
        addButton(new GuiNpcButton(1406, guiLeft + 202, y, 20, 20, "X"));
        getButton(1406).enabled = display.auraID != -1;

        y += 22;
        addLabel(new GuiNpcLabel(106, "display.aura", guiLeft + 7, y + 5));
        addButton(new GuiNpcButton(106, guiLeft + 61, y, 50, 20, getColor(display.auraColor)));
        getButton(106).packedFGColour = display.auraColor;
        getButton(106).enabled = display.auraID == -1;

        addButton(new GuiNpcButton(1106, guiLeft + 112, y, 20, 20, "X"));
        getButton(1106).enabled = display.auraColor != -1;
        addButton(new GuiNpcButton(1206, guiLeft + 133, y, 50, 20,  new String[]{"display.hide", "display.show"}, showAura ? 1 : 0));

        y += 22;
        addLabel(new GuiNpcLabel(124, "display.hudColor", guiLeft + 7, y + 5));
        addButton(new GuiNpcButton(124, guiLeft + 61, y, 50, 20, getColor(display.hudColor)));
        getButton(124).packedFGColour = display.hudColor;
        addButton(new GuiNpcButton(1124, guiLeft + 112, y, 20, 20, "X"));
        getButton(1124).enabled = display.hudColor != -1;

        y += 22;
        addLabel(new GuiNpcLabel(107, "display.eye", guiLeft + 7, y + 5));
        addButton(new GuiNpcButton(107, guiLeft + 61, y, 50, 20, getColor(display.eyeColor)));
        getButton(107).packedFGColour = display.eyeColor;
        addButton(new GuiNpcButton(1107, guiLeft + 112, y, 20, 20, "X"));
        getButton(1107).enabled = display.eyeColor != -1;

        y += 22;
        addLabel(new GuiNpcLabel(108, "model.body", guiLeft + 7, y + 5));
        addButton(new GuiNpcButton(108, guiLeft + 61, y, 50, 20, getColor(display.bodyCM)));
        getButton(108).packedFGColour = display.bodyCM;
        addButton(new GuiNpcButton(1108, guiLeft + 112, y, 20, 20, "X"));
        getButton(1108).enabled = display.bodyCM != -1;

        if (visualDisplay.race == DBCRace.NAMEKIAN || visualDisplay.race == DBCRace.ARCOSIAN) {
            y = addBodyColors(y);
        }

        if (visualDisplay.race == DBCRace.SAIYAN || visualDisplay.race == DBCRace.HALFSAIYAN) {
            y += 22;
            addLabel(new GuiNpcLabel(112, "display.fur", guiLeft + 7, y + 5));
            addButton(new GuiNpcButton(112, guiLeft + 61, y, 50, 20, getColor(display.furColor)));
            getButton(112).packedFGColour = display.furColor;
            addButton(new GuiNpcButton(1112, guiLeft + 112, y, 20, 20, "X"));
            getButton(1112).enabled = display.furColor != -1;
        }

        if(visualDisplay.race == DBCRace.MAJIN){
            y += 22;
            addLabel(new GuiNpcLabel(115, "display.majinHair", guiLeft + 7, y + 5));
            addButton(new GuiNpcButtonYesNo(115, guiLeft + 61, y, 50, 20, display.effectMajinHair));
        }

        if (visualDisplay.race == DBCRace.HUMAN || visualDisplay.race == DBCRace.SAIYAN || visualDisplay.race == DBCRace.HALFSAIYAN ||  display.effectMajinHair) {
            y = addHairOptions(y);
        }

        if (visualDisplay.race == DBCRace.SAIYAN || visualDisplay.race == DBCRace.HALFSAIYAN) {
            y = addFurOptions(y);
        }

        if(visualDisplay.race == DBCRace.ARCOSIAN){
            y += 22;
            addLabel(new GuiNpcLabel(113, "display.arcoMask", guiLeft + 7, y + 5));
            addButton(new GuiNpcButtonYesNo(113, guiLeft + 61, y, 50, 20, visualDisplay.hasArcoMask));

            int index = getArcoForm();
            addLabel(new GuiNpcLabel(114, "display.form", guiLeft + 130, y + 5));
            addButton(new GuiNpcButton(114, guiLeft + 167, y, 50, 20, arcoForms, index));
        }
    }

    private int addBodyColors(int y) {
        y += 22;
        addLabel(new GuiNpcLabel(109, "display.bodyc1", guiLeft + 7, y + 5));
        addButton(new GuiNpcButton(109, guiLeft + 61, y, 50, 20, getColor(display.bodyC1)));
        getButton(109).packedFGColour = display.bodyC1;
        addButton(new GuiNpcButton(1109, guiLeft + 112, y, 20, 20, "X"));
        getButton(1109).enabled = display.bodyC1 != -1;

        y += 22;
        addLabel(new GuiNpcLabel(110, "display.bodyc2", guiLeft + 7, y + 5));
        addButton(new GuiNpcButton(110, guiLeft + 61, y, 50, 20, getColor(display.bodyC2)));
        getButton(110).packedFGColour = display.bodyC2;
        addButton(new GuiNpcButton(1110, guiLeft + 112, y, 20, 20, "X"));
        getButton(1110).enabled = display.bodyC2 != -1;

        y += 22;
        addLabel(new GuiNpcLabel(111, "display.bodyc3", guiLeft + 7, y + 5));
        addButton(new GuiNpcButton(111, guiLeft + 61, y, 50, 20, getColor(display.bodyC3)));
        getButton(111).packedFGColour = display.bodyC3;
        addButton(new GuiNpcButton(1111, guiLeft + 112, y, 20, 20, "X"));
        getButton(1111).enabled = display.bodyC3 != -1;

        return y;
    }

    private int addHairOptions(int y) {
        y += 23;
        addLabel(new GuiNpcLabel(100, "display.hair", guiLeft + 7, y + 5));
        addButton(new GuiNpcButton(103, guiLeft + 102, y, 45, 20, "gui.clear"));
        if (display.hairCode.isEmpty()) {
            addButton(new GuiNpcButton(101, guiLeft + 51, y, 50, 20, "gui.paste"));
            getButton(103).enabled = false;
        } else {
            addButton(new GuiNpcButton(102, guiLeft + 51, y, 45, 20, "gui.copy"));
        }
        addButton(new GuiNpcButton(104, guiLeft + 147, y, 50, 20, getColor(visualDisplay.hairColor)));
        getButton(104).packedFGColour = visualDisplay.hairColor;
        addButton(new GuiNpcButton(1104, guiLeft + 199, y, 20, 20, "X"));
        getButton(1104).enabled = display.hairColor != -1;

        y += 22;
        int index = getHairType();
        addLabel(new GuiNpcLabel(140, "display.hairType", guiLeft + 7, y + 5));
        addButton(new GuiNpcButton(140, guiLeft + 61, y, 50, 20, hairTypes, index));
        return y;
    }

    private int addFurOptions(int y) {
        y += 22;
        addLabel(new GuiNpcLabel(123, "display.bodyFur", guiLeft + 7, y + 5));
        addButton(new GuiNpcButtonYesNo(123, guiLeft + 61, y, 50, 20, display.hasBodyFur));

        return y;
    }

    private void controlButtons() {
        addButton(new GuiNpcButton(1, this.guiLeft + 125 + this.xOffset, this.guiTop + 200 + this.yOffset, 60, 20, arrRace, racePage));
        getButton(1).enabled = !hasRace;
        addButton(this.left = new GuiNpcButton(668, this.guiLeft + 210 + this.xOffset, this.guiTop + 200 + this.yOffset, 20, 20, "<"));
        addButton(this.right = new GuiNpcButton(669, this.guiLeft + 235 + this.xOffset, this.guiTop + 200 + this.yOffset, 20, 20, ">"));
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        GuiNpcButton button = (GuiNpcButton) btn;
        if (button.id == 1) {
            racePage = button.getValue();
            refreshValues();
            updateButtons();
        }
        // Aura Color
        if(button.id == 106){
            lastColorClicked = 0;
            setSubGui(new SubGuiColorSelector(display.auraColor));
        }
        // Aura Color Clear
        if(button.id == 1106){
            display.auraColor = -1;
            refreshValues();
            updateButtons();
        }
        // Aura Show
        if(button.id == 1206){
            showAura = !showAura;
            auraTicks = 1;
            refreshValues();
            updateButtons();
        }
        if(button.id == 1306){
            this.setSubGui(new SubGuiSelectAura());
        }
        if(button.id == 1406){
            display.auraID = -1;
            refreshValues();
            updateButtons();
        }
        // Hud Color
        if(button.id == 124){
            lastColorClicked = 8;
            setSubGui(new SubGuiColorSelector(display.hudColor));
        }
        // Hud Color Clear
        if(button.id == 1124){
            display.hudColor = -1;
            refreshValues();
            updateButtons();
        }
        // Eye Color
        if(button.id == 107){
            lastColorClicked = 1;
            setSubGui(new SubGuiColorSelector(display.eyeColor));
        }
        // Eye Color Clear
        if(button.id == 1107){
            display.eyeColor = -1;
            refreshValues();
            updateButtons();
        }
        // Body
        if(button.id == 108){
            lastColorClicked = 2;
            setSubGui(new SubGuiColorSelector(display.bodyCM));
        }
        // Body Clear
        if(button.id == 1108){
            display.bodyCM = -1;
            refreshValues();
            updateButtons();
        }
        // Body C1
        if(button.id == 109){
            lastColorClicked = 3;
            setSubGui(new SubGuiColorSelector(display.bodyC1));
        }
        // Body C1 Clear
        if(button.id == 1109){
            display.bodyC1 = -1;
            refreshValues();
            updateButtons();
        }
        // Body C2
        if(button.id == 110){
            lastColorClicked = 4;
            setSubGui(new SubGuiColorSelector(display.bodyC2));
        }
        // Body C2 Clear
        if(button.id == 1110){
            display.bodyC2 = -1;
            refreshValues();
            updateButtons();
        }
        // Body C3
        if(button.id == 111){
            lastColorClicked = 5;
            setSubGui(new SubGuiColorSelector(display.bodyC3));
        }
        // Body C3 Clear
        if(button.id == 1111){
            display.bodyC3 = -1;
            refreshValues();
            updateButtons();
        }
        // Fur Color
        if(button.id == 112){
            lastColorClicked = 6;
            setSubGui(new SubGuiColorSelector(display.furColor));
        }
        // Fur Color Clear
        if(button.id == 1112){
            display.furColor = -1;
            refreshValues();
            updateButtons();
        }
        // Majin Hair
        if(button.id == 115){
            display.effectMajinHair = !display.effectMajinHair;
            refreshValues();
            updateButtons();
        }
        // Arco Mask
        if(button.id == 113){
            display.hasArcoMask = !display.hasArcoMask;
            refreshValues();
            updateButtons();
        }
        // Body Fur
        if(button.id == 123){
            display.hasBodyFur = !display.hasBodyFur;
            refreshValues();
            updateButtons();
        }

        // Form
        if(button.id == 114){
            display.bodyType = getArcoString(button.getValue());
            refreshValues();
        }
        if(button.id == 140){
            display.hairType = getHairString(button.getValue());
            visualDisplay.hairType = display.hairType;
            refreshValues();
            updateButtons();
        }
        // Hair Clear
        if(button.id == 103){
            display.hairCode = "";
            refreshValues();
            updateButtons();
        }
        // Hair Paste
        if(button.id == 101){
            String newDNSHair = getClipboardContents();
            display.hairCode = newDNSHair.length() != 786 ? dnsHairG1toG2(newDNSHair) : newDNSHair;
            refreshValues();
            updateButtons();
        }
        // Hair Copy
        if(button.id == 102){
            setClipboardContents(display.hairCode);
        }
        // Hair Color
        if(button.id == 104){
            lastColorClicked = 7;
            setSubGui(new SubGuiColorSelector(display.hairColor));
        }
        // Hair Color Clear
        if(button.id == 1104){
            display.hairColor = -1;
            refreshValues();
            updateButtons();
        }
    }

    private void updateButtons() {
        // Clear existing buttons
        // Clear only the race-related buttons
        Iterator<GuiButton> iterator = this.buttonList.iterator();
        while (iterator.hasNext()) {
            GuiButton button = iterator.next();
            if (button.id != 1 && button.id != 668 && button.id != 669) {
                iterator.remove();
            }
        }
        this.labels.clear();
        raceButtons(guiTop + 5);
    }


	@Override
    public void unFocused(GuiNpcTextField txtField) {
        if(txtField.id == 200){
            display.formSize = txtField.getFloat();
        }
    }

    @Override
    public void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
        if(!hasSubGui())
            menu.mouseClicked(i, j, k);
    }

	@Override
	public void subGuiClosed(SubGuiInterface subgui){
        if(subgui instanceof  SubGuiColorSelector){
            int color = ((SubGuiColorSelector) subgui).color;
            if(lastColorClicked == 0){
                display.auraColor = color;
            } else if(lastColorClicked == 1){
                display.eyeColor = color;
            } else if(lastColorClicked == 2){
                display.bodyCM = color;
            } else if(lastColorClicked == 3){
                display.bodyC1 = color;
            } else if(lastColorClicked == 4){
                display.bodyC2 = color;
            } else if(lastColorClicked == 5){
                display.bodyC3 = color;
            } else if(lastColorClicked == 6){
                display.furColor = color;
            } else if(lastColorClicked == 7){
                display.hairColor = color;
            } else if(lastColorClicked == 8){
                display.hudColor = color;
            }
            refreshValues();
            updateButtons();
        }
        if (subgui instanceof SubGuiSelectAura) {
            if(form != null){
                SubGuiSelectAura guiSelectForm = ((SubGuiSelectAura)subgui);
                if(guiSelectForm.confirmed){
                    if(guiSelectForm.selectedAuraID == display.auraID)
                        return;

                    display.auraID = guiSelectForm.selectedAuraID;
                }
            }
            refreshValues();
            updateButtons();
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
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

        menu.drawElements(fontRendererObj, par1, par2, mc, par3);
        GL11.glColor4f(1, 1, 1, 1);

        if(visualDisplay.race == DBCRace.HUMAN || visualDisplay.race == DBCRace.SAIYAN
            || visualDisplay.race == DBCRace.HALFSAIYAN || (visualDisplay.race == DBCRace.MAJIN && display.effectMajinHair)){
            GL11.glPushMatrix();
            GL11.glScalef(0.7f, 0.7f, 0.7f);
            // Calculate dynamic positions based on GUI dimensions
            // Calculate the dynamic positions based on the scaled dimensions
            int scaledWidth = (int) (this.width / 0.7f);
            int scaledHeight = (int) (this.height / 0.7f);
            int xPos = (scaledWidth - this.xSize) / 2 + 280;
            int yPos = (scaledHeight - this.ySize) / 2 - 15;
            if(visualDisplay.hairType.equals("ssj3")){
                fontRendererObj.drawString(StatCollector.translateToLocal("editor.invalid") + " SSJ3", xPos, yPos, 0xffffff);
            }
            if(visualDisplay.hairType.equals("oozaru")){
                fontRendererObj.drawString(StatCollector.translateToLocal("editor.invalid") + " Oozaru", xPos, yPos, 0xffffff);
            }
            GL11.glPopMatrix();
        }

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

        if(showAura) {
            // Render Aura
            EntityAura2 aur = new EntityAura2(entity.worldObj, Utility.getEntityID(entity), 0, 0, 0, 100, false);
            aur.setAlp(0.5F);
            aur.setInner(true);
            aur.setCol(display.auraColor);

            if(display.auraID != -1 && AuraController.getInstance().has(display.auraID)){
                Aura aura = AuraController.getInstance().customAuras.get(display.auraID);
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
                    aur.setSpd((int) aura.display.speed);
            }
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
    protected void drawBackground() {
        super.drawBackground();

        int xPosGradient = guiLeft + 225;
        int yPosGradient = guiTop + 5;
        drawGradientRect(xPosGradient, yPosGradient, 130 + xPosGradient ,180 + yPosGradient, 0xc0101010, 0xd0101010);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

	@Override
	public void selected(int id, String name) {}

    public void refreshValues() {
        if (hasRace)
            visualDisplay.race = (byte) form.race;
        else
            visualDisplay.race = (byte) racePage;

        visualDisplay.hasArcoMask = false;
        if(visualDisplay.race == DBCRace.ARCOSIAN){
            visualDisplay.hasArcoMask = display.hasArcoMask;
        }

        visualDisplay.hairType = display.hairType;
        visualDisplay.hairColor = display.hairColor == -1 ? 0xffffff : display.hairColor;
        if(visualDisplay.race == DBCRace.NAMEKIAN || visualDisplay.race == DBCRace.ARCOSIAN || visualDisplay.race == DBCRace.MAJIN)
            visualDisplay.hairColor = display.bodyCM;

        visualDisplay.eyeColor = 0x000000;
        if (visualDisplay.race < 3) {
            visualDisplay.bodyCM = 16297621;
        } else if (visualDisplay.race == DBCRace.NAMEKIAN) {
            visualDisplay.hairColor = 5095183;
            visualDisplay.bodyCM = 5095183;
            visualDisplay.bodyC1 = 13796998;
            visualDisplay.bodyC2 = 12854822;
        } else if (visualDisplay.race == DBCRace.ARCOSIAN) {
            visualDisplay.bodyCM = 15460342;
            visualDisplay.bodyC1 = 16111595;
            visualDisplay.bodyC2 = 8533141;
            visualDisplay.bodyC3 = 16550015;
            visualDisplay.eyeColor = 0xFF0000;
        } else if (visualDisplay.race == DBCRace.MAJIN){
            visualDisplay.bodyCM = 16757199;
            visualDisplay.eyeColor = 0xFF0000;
        }

        visualDisplay.hairCode = "";
        if(visualDisplay.race == DBCRace.HUMAN || visualDisplay.race == DBCRace.SAIYAN || visualDisplay.race == DBCRace.HALFSAIYAN) {
            visualDisplay.hairCode = display.hairCode;
            if (visualDisplay.hairCode.isEmpty())
                visualDisplay.hairCode = "255625542850212261234927501822325618275021283063192850180147507467503248505072675043255250726750360150505667501922475071675038255050716750380152507167503202475071675032025250716750300050507167503000505047655036205250276550362250502765503620475027655036225250306550363150503065503622475030655034015250276550250147502765503000505027655036175050505050803150505050508028505050505080225050505050801750505050508022505050505080255050505050801750505050508011505050505080115050505050801150505050508011505050505080005050505050800050505050508000505050505080005050505050803154508067504931545080615028285450766150472854506561506551525080675038655250806150786052507861503451525069615050625050806950528250508061503485505078615030625050696150585149508069506157495080615080624950786150805149506961504920";
        } else if (visualDisplay.race == DBCRace.MAJIN){
            visualDisplay.hairCode = "005050555050000050505550500000505055505000005050455050000050505250500000505052505000005050555050000050505450500000505052505000005050525050000150433450500000505055505000005050525050000054395050500000505045505000005050475050000050504750500000505047505000015043655050000050504750500000505047505000005050475050000050504750500000544545505000005250505050000052505050500000525050505000005250505050000050505050500000505050505000005050505050000052505050500000525050505000005250505050000052505050500000525050505000005245505050000054505050500000525050505000005252505050000070505050500000705050505000007050505050000070505050500000705050505000347050505050003470505050500000705050505000007050505050000069505050500000695050505000007050505050000070505050500000705050505000007050505050000070505050500020";
            if(display.effectMajinHair){
                visualDisplay.hairCode = display.hairCode;
                visualDisplay.hairColor = display.hairColor;
            }
        }

        visualDisplay.bodyCM = display.bodyCM == -1 ? visualDisplay.bodyCM : display.bodyCM;
        visualDisplay.bodyC1 = display.bodyC1 == -1 ? visualDisplay.bodyC1 : display.bodyC1;
        visualDisplay.bodyC2 = display.bodyC2 == -1 ? visualDisplay.bodyC2 : display.bodyC2;
        visualDisplay.bodyC3 = display.bodyC3 == -1 ? visualDisplay.bodyC3 : display.bodyC3;
        visualDisplay.eyeColor = display.eyeColor == -1 ? visualDisplay.eyeColor : display.eyeColor;

        ModelData data = npc.modelData;
        data.removePart("dbcHorn");
        data.removePart("tail");
        data.removePart("dbcArms");
        data.removePart("dbcBody");

        if(visualDisplay.race == DBCRace.SAIYAN || visualDisplay.race == DBCRace.HALFSAIYAN || visualDisplay.race == DBCRace.ARCOSIAN){
            ModelPartData tail = data.getOrCreatePart("tail");
            tail.setTexture("tail/monkey1", 8);
            if(visualDisplay.race == DBCRace.SAIYAN || visualDisplay.race == DBCRace.HALFSAIYAN){
                tail.pattern = 0;
                tail.color = display.hairColor;
                if(display.furColor != -1){
                    tail.color = display.furColor;
                }
            }
            if(visualDisplay.race == DBCRace.ARCOSIAN){
                tail.pattern = 2;
                tail.color = visualDisplay.bodyC3;
            }
        }

        if(visualDisplay.race == DBCRace.ARCOSIAN){
            ModelPartData horn = data.getOrCreatePart("dbcHorn");
            ModelPartData arms;
            ModelPartData tail;
            switch (display.bodyType){
                case "firstform":
                    visualDisplay.arcoState = 0;
                    horn.setTexture("tail/monkey1", 1);
                    break;
                case "secondform":
                    visualDisplay.arcoState = 2;
                    horn.setTexture("tail/monkey1", 2);
                    break;
                case "thirdform":
                    visualDisplay.arcoState = 3;
                    horn.setTexture("tail/monkey1", 3);
                    arms = data.getOrCreatePart("dbcArms");
                    arms.setTexture("tail/monkey1", 2);
                    break;
                case "finalform":
                    visualDisplay.arcoState = 4;
                    data.removePart("dbcHorn");
                    tail = data.getOrCreatePart("tail");
                    tail.color = visualDisplay.bodyCM;
                    break;
                case "ultimatecooler":
                    visualDisplay.arcoState = 5;
                    horn.setTexture("tail/monkey1", 4);
                    arms = data.getOrCreatePart("dbcArms");
                    arms.setTexture("tail/monkey1", 1);
                    ModelPartData body = data.getOrCreatePart("dbcBody");
                    body.setTexture("tail/monkey1", 1);
                    tail = data.getOrCreatePart("tail");
                    tail.color = visualDisplay.bodyCM;
                    break;
                default:
                    visualDisplay.arcoState = 0;
                    data.removePart("dbcHorn");
                    break;
            }
        }

        if(visualDisplay.race == DBCRace.NAMEKIAN){
            ModelPartData horn = data.getOrCreatePart("dbcHorn");
            horn.setTexture("tail/monkey1", 5);
        }

        // Copy Form to Fake Form
        spoofForm.race = form.race;
        spoofForm.display.hairType = display.hairType;
        spoofForm.display.hairCode = display.hairCode;
        spoofForm.display.hairColor = display.hairColor;
        spoofForm.display.bodyCM = display.bodyCM;
        spoofForm.display.bodyC1 = display.bodyC1;
        spoofForm.display.bodyC2 = display.bodyC2;
        spoofForm.display.bodyC3 = display.bodyC3;
        spoofForm.display.bodyType = display.bodyType;
        spoofForm.display.hudColor = display.hudColor;
        spoofForm.display.eyeColor = display.eyeColor;
        spoofForm.display.effectMajinHair = display.effectMajinHair;
        spoofForm.display.furColor = display.furColor;
        spoofForm.display.auraColor = display.auraColor;
        spoofForm.display.auraID = display.auraID;
        spoofForm.display.keepOriginalSize = display.keepOriginalSize;
        spoofForm.display.formSize = display.formSize;
        spoofForm.display.hasArcoMask = display.hasArcoMask;
        spoofForm.display.hasBodyFur = display.hasBodyFur;

        visualDisplay.formID = spoofForm.id;
    }

    public String getColor(int input) {
        String str;
        for(str = Integer.toHexString(input); str.length() < 6; str = "0" + str) {}
        return str;
    }

    public String getClipboardContents() {
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents((Object)null);
        boolean hasTransferableText = contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                result = (String)contents.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception ignored) {}
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
        if(!display.bodyType.isEmpty()){
            if(display.bodyType.toLowerCase().contains("first"))
                index = 1;
            else if(display.bodyType.toLowerCase().contains("second"))
                index = 2;
            else if(display.bodyType.toLowerCase().contains("third"))
                index = 3;
            else if(display.bodyType.toLowerCase().contains("final"))
                index = 4;
            else if(display.bodyType.toLowerCase().contains("ultimate"))
                index = 5;
        }
        return index;
    }

    private String getArcoString(int i) {
        switch (i) {
            case 1:
                return "firstform";
            case 2:
                return "secondform";
            case 3:
                return "thirdform";
            case 4:
                return "finalform";
            case 5:
                return "ultimatecooler";
            default:
                return "";
        }
    }


    private int getHairType() {
        int index = 0;
        //  "base", "ssj", "ssj2", "ssj3", "ssj4", "oozaru"
        if(!display.hairType.isEmpty()){
            if(display.hairType.toLowerCase().contains("base"))
                index = 1;
            else if(display.hairType.equalsIgnoreCase("ssj"))
                index = 2;
            else if(display.hairType.toLowerCase().contains("ssj2"))
                index = 3;
            else if(display.hairType.toLowerCase().contains("ssj3"))
                index = 4;
            else if(display.hairType.toLowerCase().contains("ssj4"))
                index = 5;
            else if(display.hairType.toLowerCase().contains("oozaru"))
                index = 6;
        }
        return index;
    }

    private String getHairString(int i) {
        switch (i) {
            case 1:
                return "base";
            case 2:
                return "ssj";
            case 3:
                return "ssj2";
            case 4:
                return "ssj3";
            case 5:
                return "ssj4";
            case 6:
                return "oozaru";
            default:
                return "";
        }
    }


    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {}
}
