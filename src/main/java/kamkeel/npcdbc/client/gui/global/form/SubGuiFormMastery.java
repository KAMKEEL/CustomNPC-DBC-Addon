package kamkeel.npcdbc.client.gui.global.form;

import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormMastery;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.util.ValueUtil;

public class SubGuiFormMastery extends SubGuiInterface implements ISubGuiListener, GuiSelectionListener,ITextfieldListener
{
    private GuiNpcFormMenu menu;
	public Form form;
    public FormMastery mastery;

    public boolean showLevelGain = false;
    public boolean showUpdateGain = false;
    public boolean showAttackGain = false;
    public boolean showDamagedGain = false;
    public boolean showFireKiGain = false;

    public boolean showHealthRequirement = false;
    public boolean showKiDrain = false;
    public boolean showHeat = false;
    public boolean showPain = false;
    public boolean showDodge = false;
    public boolean showDamageNegation = false;

    public SubGuiFormMastery(GuiNPCManageForms parent, Form form)
	{
		this.form = form;
        this.mastery = form.mastery;

		setBackground("menubg.png");
		xSize = 360;
		ySize = 216;

        menu = new GuiNpcFormMenu(parent, this, -3, form);
	}

    public void initGui()
    {
        super.initGui();
        guiTop += 7;
        menu.initGui(guiLeft, guiTop, xSize);

        int y = guiTop + 3;

        // TODO: Fix Resizing Bug
        GuiScrollWindow scrollWindow = new GuiScrollWindow(this, guiLeft +4, y, xSize-9, ySize-7, 0);
        addScrollableGui(0, scrollWindow);
        int maxScroll = -20;

        y -= 15;

        scrollWindow.addLabel(new GuiNpcLabel(1,"mastery.maxLevel", 4, y + 5));
        scrollWindow.addTextField(new GuiNpcTextField(1, this, 135, y, 40, 20, String.valueOf(form.mastery.maxLevel)));
        scrollWindow.getTextField(1).setMaxStringLength(10);
        scrollWindow.getTextField(1).floatsOnly = true;
        scrollWindow.getTextField(1).setMinMaxDefaultFloat(-10000, 10000, 1);
        scrollWindow.getLabel(1).color = 0xffffff;

        scrollWindow.addLabel(new GuiNpcLabel(8,"mastery.multiFlat", 180, y + 5));
        scrollWindow.addTextField(new GuiNpcTextField(8, this, 295, y, 40, 20, String.valueOf(form.mastery.attributeMultiFlat)));
        scrollWindow.getTextField(8).setMaxStringLength(10);
        scrollWindow.getTextField(8).floatsOnly = true;
        scrollWindow.getTextField(8).setMinMaxDefaultFloat(-10000, 10000, 1);
        scrollWindow.getLabel(8).color = 0xffffff;

        maxScroll += 23;
        y += 23;

        scrollWindow.addLabel(new GuiNpcLabel(10,"mastery.multiMinMax", 4, y + 5));
        scrollWindow.addTextField(new GuiNpcTextField(10, this, 135, y, 40, 20, String.valueOf(form.mastery.attributeMultiMinOrMax)));
        scrollWindow.getTextField(10).setMaxStringLength(10);
        scrollWindow.getTextField(10).floatsOnly = true;
        scrollWindow.getTextField(10).setMinMaxDefaultFloat(-10000, 10000, 1);
        scrollWindow.getLabel(10).color = 0xffffff;

        scrollWindow.addLabel(new GuiNpcLabel(9,"mastery.multiPerLevel", 180, y + 5));
        scrollWindow.addTextField(new GuiNpcTextField(9, this, 295, y, 40, 20, String.valueOf(form.mastery.attributeMultiPerLevel)));
        scrollWindow.getTextField(9).setMaxStringLength(10);
        scrollWindow.getTextField(9).floatsOnly = true;
        scrollWindow.getTextField(9).setMinMaxDefaultFloat(-10000, 10000, 1);
        scrollWindow.getLabel(9).color = 0xffffff;

        maxScroll += 23;
        y += 23;
        scrollWindow.addLabel(new GuiNpcLabel(2,"mastery.instantTransform", 4, y + 5));
        scrollWindow.addTextField(new GuiNpcTextField(2, this, 135, y, 40, 20, String.valueOf(form.mastery.instantTransformationUnlockLevel)));
        scrollWindow.getTextField(2).setMaxStringLength(10);
        scrollWindow.getTextField(2).floatsOnly = true;
        scrollWindow.getTextField(2).setMinMaxDefaultFloat(-10000, 10000, 1);
        scrollWindow.getLabel(2).color = 0xffffff;

        maxScroll += 23;
        y += 23;
        scrollWindow.addLabel(new GuiNpcLabel(100,"mastery.healthReqSettings", 4, y + 5));
        scrollWindow.getLabel(100).color = 0xffffff;
        scrollWindow.addButton(new GuiNpcButton(100, 200, y, 120, 20, new String[]{"display.hide", "display.show"}, showHealthRequirement ? 1 : 0));
        if(showHealthRequirement){
            maxScroll += 23 * 2;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(101,"mastery.requirement", 4, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(101, this, 135, y, 40, 20, String.valueOf(mastery.healthRequirement)));
            scrollWindow.getTextField(101).setMaxStringLength(10);
            scrollWindow.getTextField(101).floatsOnly = true;
            scrollWindow.getTextField(101).setMinMaxDefaultFloat(0f, 100f, 100f);
            scrollWindow.getLabel(101).color = 0xffffff;

            scrollWindow.addLabel(new GuiNpcLabel(102,"mastery.flatMulti", 180, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(102, this, 295, y, 40, 20, String.valueOf(mastery.healthRequirementMultiFlat)));
            scrollWindow.getTextField(102).setMaxStringLength(10);
            scrollWindow.getTextField(102).floatsOnly = true;
            scrollWindow.getTextField(102).setMinMaxDefaultFloat(-10000f, 10000f, 1.0f);
            scrollWindow.getLabel(102).color = 0xffffff;

            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(103,"mastery.perLevel", 4, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(103, this, 135, y, 40, 20, String.valueOf(mastery.healthRequirementMultiPerLevel)));
            scrollWindow.getTextField(103).setMaxStringLength(10);
            scrollWindow.getTextField(103).floatsOnly = true;
            scrollWindow.getTextField(103).setMinMaxDefaultFloat(-10000f, 10000f, 0.01f);
            scrollWindow.getLabel(103).color = 0xffffff;

            scrollWindow.addLabel(new GuiNpcLabel(104,"mastery.minMax", 180, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(104, this, 295, y, 40, 20, String.valueOf(mastery.healthRequirementMultiMinOrMax)));
            scrollWindow.getTextField(104).setMaxStringLength(10);
            scrollWindow.getTextField(104).floatsOnly = true;
            scrollWindow.getTextField(104).setMinMaxDefaultFloat(-10000f, 10000f, 5f);
            scrollWindow.getLabel(104).color = 0xffffff;
        }

        maxScroll += 23;
        y += 23;
        scrollWindow.addLabel(new GuiNpcLabel(200,"mastery.kiDrainSettings", 4, y + 5));
        scrollWindow.getLabel(200).color = 0xffffff;
        scrollWindow.addButton(new GuiNpcButton(200, 200, y, 120, 20, new String[]{"display.hide", "display.show"}, showKiDrain ? 1 : 0));
        if(showKiDrain){
            maxScroll += 23 * 3;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(201,"mastery.drainPercent", 4, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(201, this, 135, y, 40, 20, String.valueOf(mastery.kiDrain)));
            scrollWindow.getTextField(201).setMaxStringLength(10);
            scrollWindow.getTextField(201).floatsOnly = true;
            scrollWindow.getTextField(201).setMinMaxDefaultFloat(0, 100, 1);
            scrollWindow.getLabel(201).color = 0xffffff;

            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(202,"mastery.drainTimer", 4, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(202, this, 135, y, 40, 20, String.valueOf(mastery.kiDrainTimer)));
            scrollWindow.getTextField(202).setMaxStringLength(10);
            scrollWindow.getTextField(202).integersOnly = true;
            scrollWindow.getTextField(202).setMinMaxDefault(10, 200, 100);
            scrollWindow.getLabel(202).color = 0xffffff;

            scrollWindow.addLabel(new GuiNpcLabel(203,"mastery.flatMulti", 180, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(203, this, 295, y, 40, 20, String.valueOf(mastery.kiDrainMultiFlat)));
            scrollWindow.getTextField(203).setMaxStringLength(10);
            scrollWindow.getTextField(203).floatsOnly = true;
            scrollWindow.getTextField(203).setMinMaxDefaultFloat(-10000f, 10000f, 1.0f);
            scrollWindow.getLabel(203).color = 0xffffff;

            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(204,"mastery.perLevel", 4, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(204, this, 135, y, 40, 20, String.valueOf(mastery.kiDrainMultiPerLevel)));
            scrollWindow.getTextField(204).setMaxStringLength(10);
            scrollWindow.getTextField(204).floatsOnly = true;
            scrollWindow.getTextField(204).setMinMaxDefaultFloat(-10000f, 10000f, -0.01f);
            scrollWindow.getLabel(204).color = 0xffffff;

            scrollWindow.addLabel(new GuiNpcLabel(205,"mastery.minMax", 180, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(205, this, 295, y, 40, 20, String.valueOf(mastery.kiDrainMultiMinOrMax)));
            scrollWindow.getTextField(205).setMaxStringLength(10);
            scrollWindow.getTextField(205).floatsOnly = true;
            scrollWindow.getTextField(205).setMinMaxDefaultFloat(-10000f, 10000f, 0.1f);
            scrollWindow.getLabel(205).color = 0xffffff;
        }

        maxScroll += 23;
        y += 23;
        scrollWindow.addLabel(new GuiNpcLabel(300,"mastery.heatSettings", 4, y + 5));
        scrollWindow.getLabel(300).color = 0xffffff;
        scrollWindow.addButton(new GuiNpcButton(300, 200, y, 120, 20, new String[]{"display.hide", "display.show"}, showHeat ? 1 : 0));
        if(showHeat){
            maxScroll += 23 * 2;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(301,"mastery.heatTime", 4, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(301, this, 135, y, 40, 20, String.valueOf(mastery.maxHeat)));
            scrollWindow.getTextField(301).setMaxStringLength(10);
            scrollWindow.getTextField(301).integersOnly = true;
            scrollWindow.getTextField(301).setMinMaxDefault(0, 600, 0);
            scrollWindow.getLabel(301).color = 0xffffff;

            scrollWindow.addLabel(new GuiNpcLabel(302,"mastery.flatMulti", 180, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(302, this, 295, y, 40, 20, String.valueOf(mastery.heatMultiFlat)));
            scrollWindow.getTextField(302).setMaxStringLength(10);
            scrollWindow.getTextField(302).floatsOnly = true;
            scrollWindow.getTextField(302).setMinMaxDefaultFloat(-10000f, 10000f, 1.0f);
            scrollWindow.getLabel(302).color = 0xffffff;

            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(303,"mastery.perLevel", 4, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(303, this, 135, y, 40, 20, String.valueOf(mastery.heatMultiPerLevel)));
            scrollWindow.getTextField(303).setMaxStringLength(10);
            scrollWindow.getTextField(303).floatsOnly = true;
            scrollWindow.getTextField(303).setMinMaxDefaultFloat(0f, 10000f, 0.01f);
            scrollWindow.getLabel(303).color = 0xffffff;

            scrollWindow.addLabel(new GuiNpcLabel(304,"mastery.minMax", 180, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(304, this, 295, y, 40, 20, String.valueOf(mastery.heatMultiMinOrMax)));
            scrollWindow.getTextField(304).setMaxStringLength(10);
            scrollWindow.getTextField(304).floatsOnly = true;
            scrollWindow.getTextField(304).setMinMaxDefaultFloat(-10000f, 10000f, 0f);
            scrollWindow.getLabel(304).color = 0xffffff;
        }

        maxScroll += 23;
        y += 23;
        scrollWindow.addLabel(new GuiNpcLabel(400,"mastery.painSettings", 4, y + 5));
        scrollWindow.getLabel(400).color = 0xffffff;
        scrollWindow.addButton(new GuiNpcButton(400, 200, y, 120, 20, new String[]{"display.hide", "display.show"}, showPain ? 1 : 0));
        if(showPain){
            maxScroll += 23 * 2;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(401,"mastery.painTime", 4, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(401, this, 135, y, 40, 20, String.valueOf(mastery.painTime)));
            scrollWindow.getTextField(401).setMaxStringLength(10);
            scrollWindow.getTextField(401).integersOnly = true;
            scrollWindow.getTextField(401).setMinMaxDefault(0, 600, 0);
            scrollWindow.getLabel(401).color = 0xffffff;

            scrollWindow.addLabel(new GuiNpcLabel(402,"mastery.flatMulti", 180, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(402, this, 295, y, 40, 20, String.valueOf(mastery.painMultiFlat)));
            scrollWindow.getTextField(402).setMaxStringLength(10);
            scrollWindow.getTextField(402).floatsOnly = true;
            scrollWindow.getTextField(402).setMinMaxDefaultFloat(-10000f, 10000f, 1.0f);
            scrollWindow.getLabel(402).color = 0xffffff;

            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(403,"mastery.perLevel", 4, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(403, this, 135, y, 40, 20, String.valueOf(mastery.painMultiPerLevel)));
            scrollWindow.getTextField(403).setMaxStringLength(10);
            scrollWindow.getTextField(403).floatsOnly = true;
            scrollWindow.getTextField(403).setMinMaxDefaultFloat(0f, 10000f, -0.01f);
            scrollWindow.getLabel(403).color = 0xffffff;

            scrollWindow.addLabel(new GuiNpcLabel(404,"mastery.minMax", 180, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(404, this, 295, y, 40, 20, String.valueOf(mastery.painMultiMinOrMax)));
            scrollWindow.getTextField(404).setMaxStringLength(10);
            scrollWindow.getTextField(404).floatsOnly = true;
            scrollWindow.getTextField(404).setMinMaxDefaultFloat(-10000f, 10000f, 0f);
            scrollWindow.getLabel(404).color = 0xffffff;
        }

        maxScroll += 23;
        y += 23;
        scrollWindow.addLabel(new GuiNpcLabel(500,"mastery.dodgeSettings", 4, y + 5));
        scrollWindow.getLabel(500).color = 0xffffff;
        scrollWindow.addButton(new GuiNpcButton(500, 200, y, 120, 20, new String[]{"display.hide", "display.show"}, showDodge ? 1 : 0));
        if(showDodge){
            maxScroll += 23 * 2;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(501,"mastery.dodgeChance", 4, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(501, this, 135, y, 40, 20, String.valueOf(mastery.dodgeChance)));
            scrollWindow.getTextField(501).setMaxStringLength(10);
            scrollWindow.getTextField(501).floatsOnly = true;
            scrollWindow.getTextField(501).setMinMaxDefaultFloat(0, 100f, 0);
            scrollWindow.getLabel(501).color = 0xffffff;

            scrollWindow.addLabel(new GuiNpcLabel(502,"mastery.flatMulti", 180, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(502, this, 295, y, 40, 20, String.valueOf(mastery.dodgeMultiFlat)));
            scrollWindow.getTextField(502).setMaxStringLength(10);
            scrollWindow.getTextField(502).floatsOnly = true;
            scrollWindow.getTextField(502).setMinMaxDefaultFloat(-10000f, 10000f, 1.0f);
            scrollWindow.getLabel(502).color = 0xffffff;

            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(503,"mastery.perLevel", 4, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(503, this, 135, y, 40, 20, String.valueOf(mastery.dodgeMultiPerLevel)));
            scrollWindow.getTextField(503).setMaxStringLength(10);
            scrollWindow.getTextField(503).floatsOnly = true;
            scrollWindow.getTextField(503).setMinMaxDefaultFloat(0f, 10000f, 0.01f);
            scrollWindow.getLabel(503).color = 0xffffff;

            scrollWindow.addLabel(new GuiNpcLabel(504,"mastery.minMax", 180, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(504, this, 295, y, 40, 20, String.valueOf(mastery.dodgeMultiMinOrMax)));
            scrollWindow.getTextField(504).setMaxStringLength(10);
            scrollWindow.getTextField(504).floatsOnly = true;
            scrollWindow.getTextField(504).setMinMaxDefaultFloat(0f, 10000f, 1f);
            scrollWindow.getLabel(504).color = 0xffffff;
        }

        maxScroll += 23;
        y += 23;
        scrollWindow.addLabel(new GuiNpcLabel(600,"mastery.damageNegationSettings", 4, y + 5));
        scrollWindow.getLabel(600).color = 0xffffff;
        scrollWindow.addButton(new GuiNpcButton(600, 200, y, 120, 20, new String[]{"display.hide", "display.show"}, showDamageNegation ? 1 : 0));
        if(showDamageNegation){
            maxScroll += 23 * 2;
            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(601,"mastery.damageNegation", 4, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(601, this, 135, y, 40, 20, String.valueOf(mastery.damageNegation)));
            scrollWindow.getTextField(601).setMaxStringLength(10);
            scrollWindow.getTextField(601).floatsOnly = true;
            scrollWindow.getTextField(601).setMinMaxDefaultFloat(0, 100f, 0);
            scrollWindow.getLabel(601).color = 0xffffff;

            scrollWindow.addLabel(new GuiNpcLabel(602,"mastery.flatMulti", 180, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(602, this, 295, y, 40, 20, String.valueOf(mastery.damageNegationMultiFlat)));
            scrollWindow.getTextField(602).setMaxStringLength(10);
            scrollWindow.getTextField(602).floatsOnly = true;
            scrollWindow.getTextField(602).setMinMaxDefaultFloat(-10000f, 10000f, 1.0f);
            scrollWindow.getLabel(602).color = 0xffffff;

            y += 23;
            scrollWindow.addLabel(new GuiNpcLabel(603,"mastery.perLevel", 4, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(603, this, 135, y, 40, 20, String.valueOf(mastery.damageNegationMultiPerLevel)));
            scrollWindow.getTextField(603).setMaxStringLength(10);
            scrollWindow.getTextField(603).floatsOnly = true;
            scrollWindow.getTextField(603).setMinMaxDefaultFloat(0f, 10000f, 0.01f);
            scrollWindow.getLabel(603).color = 0xffffff;

            scrollWindow.addLabel(new GuiNpcLabel(604,"mastery.minMax", 180, y + 5));
            scrollWindow.addTextField(new GuiNpcTextField(604, this, 295, y, 40, 20, String.valueOf(mastery.damageNegationMultiMinOrMax)));
            scrollWindow.getTextField(604).setMaxStringLength(10);
            scrollWindow.getTextField(604).floatsOnly = true;
            scrollWindow.getTextField(604).setMinMaxDefaultFloat(0f, 10000f, 1f);
            scrollWindow.getLabel(604).color = 0xffffff;
        }

        y += 23;
        scrollWindow.addLabel(new GuiNpcLabel(19,"mastery.levelGainSettings", 4, y + 5));
        scrollWindow.getLabel(19).color = 0xffffff;
        scrollWindow.addButton(new GuiNpcButton(19, 200, y, 120, 20, new String[]{"display.hide", "display.show"}, showLevelGain ? 1 : 0));
        if(showLevelGain){
            scrollWindow.addLabel(new GuiNpcLabel(20201,"--------------------------------------------------", 4, y + 28 - 7));
            scrollWindow.getLabel(20201).color = 0xffffff;

            maxScroll += 30;
            y += 30;
            scrollWindow.addLabel(new GuiNpcLabel(20,"mastery.updateGainSettings", 4, y + 5));
            scrollWindow.getLabel(20).color = 0xffffff;
            scrollWindow.addButton(new GuiNpcButton(20, 135, y, 120, 20, new String[]{"display.hide", "display.show"}, showUpdateGain ? 1 : 0));
            if(showUpdateGain){
                maxScroll += 23 * 3;
                y += 23;
                scrollWindow.addLabel(new GuiNpcLabel(21,"mastery.updateGain", 4, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(21, this, 135, y, 40, 20, String.valueOf(mastery.updateGain)));
                scrollWindow.getTextField(21).setMaxStringLength(10);
                scrollWindow.getTextField(21).floatsOnly = true;
                scrollWindow.getTextField(21).setMinMaxDefaultFloat(0f, 10000f, 0.01f);
                scrollWindow.getLabel(21).color = 0xffffff;

                scrollWindow.addLabel(new GuiNpcLabel(22,"mastery.flatMind", 180, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(22, this, 295, y, 40, 20, String.valueOf(mastery.updateMindBonusFlat)));
                scrollWindow.getTextField(22).setMaxStringLength(10);
                scrollWindow.getTextField(22).floatsOnly = true;
                scrollWindow.getTextField(22).setMinMaxDefaultFloat(0f, 10000f, 1.0f);
                scrollWindow.getLabel(22).color = 0xffffff;

                y += 23;
                scrollWindow.addLabel(new GuiNpcLabel(23,"mastery.perMind", 4, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(23, this, 135, y, 40, 20, String.valueOf(mastery.updateMindBonusPerMind)));
                scrollWindow.getTextField(23).setMaxStringLength(10);
                scrollWindow.getTextField(23).floatsOnly = true;
                scrollWindow.getTextField(23).setMinMaxDefaultFloat(0f, 10000f, 0.001f);
                scrollWindow.getLabel(23).color = 0xffffff;

                scrollWindow.addLabel(new GuiNpcLabel(24,"mastery.maxMind", 180, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(24, this, 295, y, 40, 20, String.valueOf(mastery.updateMindBonusMax)));
                scrollWindow.getTextField(24).setMaxStringLength(10);
                scrollWindow.getTextField(24).floatsOnly = true;
                scrollWindow.getTextField(24).setMinMaxDefaultFloat(0f, 10000f, 5f);
                scrollWindow.getLabel(24).color = 0xffffff;

                y += 23;
                scrollWindow.addLabel(new GuiNpcLabel(25,"mastery.multiDivPlus", 4, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(25, this, 135, y, 40, 20, String.valueOf(mastery.updateMultiDivPlus)));
                scrollWindow.getTextField(25).setMaxStringLength(10);
                scrollWindow.getTextField(25).floatsOnly = true;
                scrollWindow.getTextField(25).setMinMaxDefaultFloat(0f, 10000f, 100f);
                scrollWindow.getLabel(25).color = 0xffffff;
            }
            scrollWindow.addLabel(new GuiNpcLabel(20202,"--------------------------------------------------", 4, y + 22));
            scrollWindow.getLabel(20202).color = 0xffffff;

            maxScroll += 30;
            y += 30;
            scrollWindow.addLabel(new GuiNpcLabel(30,"mastery.attackGainSettings", 4, y + 5));
            scrollWindow.getLabel(30).color = 0xffffff;
            scrollWindow.addButton(new GuiNpcButton(30, 135, y, 120, 20, new String[]{"display.hide", "display.show"}, showAttackGain ? 1 : 0));
            if(showAttackGain){
                maxScroll += 23 * 3;
                y += 23;
                scrollWindow.addLabel(new GuiNpcLabel(31,"mastery.attackGain", 4, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(31, this, 135, y, 40, 20, String.valueOf(mastery.attackGain)));
                scrollWindow.getTextField(31).setMaxStringLength(10);
                scrollWindow.getTextField(31).floatsOnly = true;
                scrollWindow.getTextField(31).setMinMaxDefaultFloat(0f, 10000f, 0.03f);
                scrollWindow.getLabel(31).color = 0xffffff;

                scrollWindow.addLabel(new GuiNpcLabel(32,"mastery.flatMind", 180, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(32, this, 295, y, 40, 20, String.valueOf(mastery.attackMindBonusFlat)));
                scrollWindow.getTextField(32).setMaxStringLength(10);
                scrollWindow.getTextField(32).floatsOnly = true;
                scrollWindow.getTextField(32).setMinMaxDefaultFloat(0f, 10000f, 1.0f);
                scrollWindow.getLabel(32).color = 0xffffff;

                y += 23;
                scrollWindow.addLabel(new GuiNpcLabel(33,"mastery.perMind", 4, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(33, this, 135, y, 40, 20, String.valueOf(mastery.attackMindBonusPerMind)));
                scrollWindow.getTextField(33).setMaxStringLength(10);
                scrollWindow.getTextField(33).floatsOnly = true;
                scrollWindow.getTextField(33).setMinMaxDefaultFloat(0f, 10000f, 0.001f);
                scrollWindow.getLabel(33).color = 0xffffff;

                scrollWindow.addLabel(new GuiNpcLabel(34,"mastery.maxMind", 180, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(34, this, 295, y, 40, 20, String.valueOf(mastery.attackMindBonusMax)));
                scrollWindow.getTextField(34).setMaxStringLength(10);
                scrollWindow.getTextField(34).floatsOnly = true;
                scrollWindow.getTextField(34).setMinMaxDefaultFloat(0f, 10000f, 5f);
                scrollWindow.getLabel(34).color = 0xffffff;

                y += 23;
                scrollWindow.addLabel(new GuiNpcLabel(35,"mastery.multiDivPlus", 4, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(35, this, 135, y, 40, 20, String.valueOf(mastery.attackMultiDivPlus)));
                scrollWindow.getTextField(35).setMaxStringLength(10);
                scrollWindow.getTextField(35).floatsOnly = true;
                scrollWindow.getTextField(35).setMinMaxDefaultFloat(0f, 10000f, 100);
                scrollWindow.getLabel(35).color = 0xffffff;
            }
            scrollWindow.addLabel(new GuiNpcLabel(30302,"--------------------------------------------------", 4, y + 22));
            scrollWindow.getLabel(30302).color = 0xffffff;

            maxScroll += 23;
            y += 30;
            scrollWindow.addLabel(new GuiNpcLabel(40,"mastery.damagedGainSettings", 4, y + 5));
            scrollWindow.getLabel(40).color = 0xffffff;
            scrollWindow.addButton(new GuiNpcButton(40, 135, y, 120, 20, new String[]{"display.hide", "display.show"}, showDamagedGain ? 1 : 0));
            if(showDamagedGain){
                maxScroll += 23 * 3;
                y += 23;
                scrollWindow.addLabel(new GuiNpcLabel(41,"mastery.damagedGain", 4, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(41, this, 135, y, 40, 20, String.valueOf(mastery.damagedGain)));
                scrollWindow.getTextField(41).setMaxStringLength(10);
                scrollWindow.getTextField(41).floatsOnly = true;
                scrollWindow.getTextField(41).setMinMaxDefaultFloat(0f, 10000f, 0.03f);
                scrollWindow.getLabel(41).color = 0xffffff;

                scrollWindow.addLabel(new GuiNpcLabel(42,"mastery.flatMind", 180, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(42, this, 295, y, 40, 20, String.valueOf(mastery.damagedMindBonusFlat)));
                scrollWindow.getTextField(42).setMaxStringLength(10);
                scrollWindow.getTextField(42).floatsOnly = true;
                scrollWindow.getTextField(42).setMinMaxDefaultFloat(0f, 10000f, 1.0f);
                scrollWindow.getLabel(42).color = 0xffffff;

                y += 23;
                scrollWindow.addLabel(new GuiNpcLabel(43,"mastery.perMind", 4, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(43, this, 135, y, 40, 20, String.valueOf(mastery.damagedMindBonusPerMind)));
                scrollWindow.getTextField(43).setMaxStringLength(10);
                scrollWindow.getTextField(43).floatsOnly = true;
                scrollWindow.getTextField(43).setMinMaxDefaultFloat(0f, 10000f, 0.001f);
                scrollWindow.getLabel(43).color = 0xffffff;

                scrollWindow.addLabel(new GuiNpcLabel(44,"mastery.maxMind", 180, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(44, this, 295, y, 40, 20, String.valueOf(mastery.damagedMindBonusMax)));
                scrollWindow.getTextField(44).setMaxStringLength(10);
                scrollWindow.getTextField(44).floatsOnly = true;
                scrollWindow.getTextField(44).setMinMaxDefaultFloat(0f, 10000f, 5f);
                scrollWindow.getLabel(44).color = 0xffffff;

                y += 23;
                scrollWindow.addLabel(new GuiNpcLabel(45,"mastery.multiDivPlus", 4, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(45, this, 135, y, 40, 20, String.valueOf(mastery.damagedMultiDivPlus)));
                scrollWindow.getTextField(45).setMaxStringLength(10);
                scrollWindow.getTextField(45).floatsOnly = true;
                scrollWindow.getTextField(45).setMinMaxDefaultFloat(0f, 10000f, 100f);
                scrollWindow.getLabel(45).color = 0xffffff;
            }
            scrollWindow.addLabel(new GuiNpcLabel(40402,"--------------------------------------------------", 4, y + 22));
            scrollWindow.getLabel(40402).color = 0xffffff;

            maxScroll += 30;
            y += 30;
            scrollWindow.addLabel(new GuiNpcLabel(50,"mastery.fireKiGainSettings", 4, y + 5));
            scrollWindow.getLabel(50).color = 0xffffff;
            scrollWindow.addButton(new GuiNpcButton(50, 135, y, 120, 20, new String[]{"display.hide", "display.show"}, showFireKiGain ? 1 : 0));
            if(showFireKiGain){
                maxScroll += 23 * 3;
                y += 23;
                scrollWindow.addLabel(new GuiNpcLabel(51,"mastery.fireKiGain", 4, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(51, this, 135, y, 40, 20, String.valueOf(mastery.fireKiGain)));
                scrollWindow.getTextField(51).setMaxStringLength(10);
                scrollWindow.getTextField(51).floatsOnly = true;
                scrollWindow.getTextField(51).setMinMaxDefaultFloat(0f, 10000f, 0.03f);
                scrollWindow.getLabel(51).color = 0xffffff;

                scrollWindow.addLabel(new GuiNpcLabel(52,"mastery.flatMind", 180, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(52, this, 295, y, 40, 20, String.valueOf(mastery.fireKiMindBonusFlat)));
                scrollWindow.getTextField(52).setMaxStringLength(10);
                scrollWindow.getTextField(52).floatsOnly = true;
                scrollWindow.getTextField(52).setMinMaxDefaultFloat(0f, 10000f, 1.0f);
                scrollWindow.getLabel(52).color = 0xffffff;

                y += 23;
                scrollWindow.addLabel(new GuiNpcLabel(53,"mastery.perMind", 4, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(53, this, 135, y, 40, 20, String.valueOf(mastery.fireKiMindBonusPerMind)));
                scrollWindow.getTextField(53).setMaxStringLength(10);
                scrollWindow.getTextField(53).floatsOnly = true;
                scrollWindow.getTextField(53).setMinMaxDefaultFloat(0f, 10000f, 0.001f);
                scrollWindow.getLabel(53).color = 0xffffff;

                scrollWindow.addLabel(new GuiNpcLabel(54,"mastery.maxMind", 180, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(54, this, 295, y, 40, 20, String.valueOf(mastery.fireKiMindBonusMax)));
                scrollWindow.getTextField(54).setMaxStringLength(10);
                scrollWindow.getTextField(54).floatsOnly = true;
                scrollWindow.getTextField(54).setMinMaxDefaultFloat(0f, 10000f, 5f);
                scrollWindow.getLabel(54).color = 0xffffff;

                y += 23;
                scrollWindow.addLabel(new GuiNpcLabel(55,"mastery.multiDivPlus", 4, y + 5));
                scrollWindow.addTextField(new GuiNpcTextField(55, this, 135, y, 40, 20, String.valueOf(mastery.fireKiMultiDivPlus)));
                scrollWindow.getTextField(55).setMaxStringLength(10);
                scrollWindow.getTextField(55).floatsOnly = true;
                scrollWindow.getTextField(55).setMinMaxDefaultFloat(0f, 10000f, 100f);
                scrollWindow.getLabel(55).color = 0xffffff;
            }
        }

        scrollWindow.maxScrollY = maxScroll;
	}

	public void buttonEvent(GuiButton guibutton)
    {
		GuiNpcButton button = (GuiNpcButton) guibutton;
        float prevY =  getScrollableGui(0).scrollY;

        if(button.id == 19){
            showLevelGain = !showLevelGain;
        }
        if(button.id == 20){
            showUpdateGain = !showUpdateGain;
        }
        if(button.id == 30){
            showAttackGain = !showAttackGain;
        }
        if(button.id == 40){
            showDamagedGain = !showDamagedGain;
        }
        if(button.id == 50){
            showFireKiGain = !showFireKiGain;
        }
        if(button.id == 100){
            showHealthRequirement = !showHealthRequirement;
        }
        if(button.id == 200){
            showKiDrain = !showKiDrain;
        }
        if(button.id == 300){
            showHeat = !showHeat;
        }
        if(button.id == 400){
            showPain = !showPain;
        }
        if(button.id == 500){
            showDodge = !showDodge;
        }
        if(button.id == 600){
            showDamageNegation = !showDamageNegation;
        }
        initGui();
        prevY = ValueUtil.clamp(prevY, 0,  getScrollableGui(0).maxScrollY);
        getScrollableGui(0).nextScrollY = getScrollableGui(0).scrollY = prevY;
	}


	@Override
    public void unFocused(GuiNpcTextField txtField) {
        if(txtField.id == 1){
            form.mastery.maxLevel = txtField.getFloat();
        } else if(txtField.id == 8){
            form.mastery.attributeMultiFlat = txtField.getFloat();
        } else if(txtField.id == 10){
            form.mastery.attributeMultiMinOrMax = txtField.getFloat();
        } else if(txtField.id == 9){
            form.mastery.attributeMultiPerLevel = txtField.getFloat();
        } else if(txtField.id == 2){
            form.mastery.instantTransformationUnlockLevel = txtField.getFloat();
        } else if(txtField.id == 101){
            mastery.healthRequirement = txtField.getFloat();
        } else if(txtField.id == 102){
            mastery.healthRequirementMultiFlat = txtField.getFloat();
        } else if(txtField.id == 103){
            mastery.healthRequirementMultiPerLevel = txtField.getFloat();
        } else if(txtField.id == 104){
            mastery.healthRequirementMultiMinOrMax = txtField.getFloat();
        } else if(txtField.id == 201){
            mastery.kiDrain = txtField.getFloat();
        } else if(txtField.id == 202){
            mastery.kiDrainTimer = txtField.getInteger();
        } else if(txtField.id == 203){
            mastery.kiDrainMultiFlat = txtField.getFloat();
        } else if(txtField.id == 204){
            mastery.kiDrainMultiPerLevel = txtField.getFloat();
        } else if(txtField.id == 205){
            mastery.kiDrainMultiMinOrMax = txtField.getFloat();
        } else if(txtField.id == 301){
            mastery.maxHeat = txtField.getInteger();
        } else if(txtField.id == 302){
            mastery.heatMultiFlat = txtField.getFloat();
        } else if(txtField.id == 303){
            mastery.heatMultiPerLevel = txtField.getFloat();
        } else if(txtField.id == 304){
            mastery.heatMultiMinOrMax = txtField.getFloat();
        } else if(txtField.id == 21){
            mastery.updateGain = txtField.getFloat();
        } else if(txtField.id == 22){
            mastery.updateMindBonusFlat = txtField.getFloat();
        } else if(txtField.id == 23){
            mastery.updateMindBonusPerMind = txtField.getFloat();
        } else if(txtField.id == 24){
            mastery.updateMindBonusMax = txtField.getFloat();
        } else if(txtField.id == 25){
            mastery.updateMultiDivPlus = txtField.getFloat();
        } else if(txtField.id == 31){
            mastery.attackGain = txtField.getFloat();
        } else if(txtField.id == 32){
            mastery.attackMindBonusFlat = txtField.getFloat();
        } else if(txtField.id == 33){
            mastery.attackMindBonusPerMind = txtField.getFloat();
        } else if(txtField.id == 34){
            mastery.attackMindBonusMax = txtField.getFloat();
        } else if(txtField.id == 35){
            mastery.attackMultiDivPlus = txtField.getFloat();
        } else if(txtField.id == 41){
            mastery.damagedGain = txtField.getFloat();
        } else if(txtField.id == 42){
            mastery.damagedMindBonusFlat = txtField.getFloat();
        } else if(txtField.id == 43){
            mastery.damagedMindBonusPerMind = txtField.getFloat();
        } else if(txtField.id == 44){
            mastery.damagedMindBonusMax = txtField.getFloat();
        } else if(txtField.id == 45){
            mastery.damagedMultiDivPlus = txtField.getFloat();
        } else if(txtField.id == 51){
            mastery.fireKiGain = txtField.getFloat();
        } else if(txtField.id == 52){
            mastery.fireKiMindBonusFlat = txtField.getFloat();
        } else if(txtField.id == 53){
            mastery.fireKiMindBonusPerMind = txtField.getFloat();
        } else if(txtField.id == 54){
            mastery.fireKiMindBonusMax = txtField.getFloat();
        } else if(txtField.id == 55){
            mastery.fireKiMultiDivPlus = txtField.getFloat();
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
	public void subGuiClosed(SubGuiInterface subgui){}

    public void drawScreen(int i, int j, float f) {
        super.drawScreen(i, j, f);
        menu.drawElements(fontRendererObj, i, j, mc, f);
    }

	@Override
	public void selected(int id, String name) {}

	public void save(){}
}
