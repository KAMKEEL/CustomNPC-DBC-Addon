package kamkeel.npcdbc.client.gui.global.form;

import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormMastery;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.*;

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

        GuiScrollWindow scollWindow = new GuiScrollWindow(this, guiLeft +4, y, xSize-9, ySize-7, 200);
        addScrollableGui(0, scollWindow);
        int maxScroll = 100;

        y -= 15;

        scollWindow.addLabel(new GuiNpcLabel(1,"Max Level", 4, y + 5));
        scollWindow.addTextField(new GuiNpcTextField(1, this, 135, y, 40, 20, String.valueOf(form.mastery.maxLevel)));
        scollWindow.getTextField(1).setMaxStringLength(10);
        scollWindow.getTextField(1).floatsOnly = true;
        scollWindow.getTextField(1).setMinMaxDefaultFloat(-10000, 10000, 1);
        scollWindow.getLabel(1).color = 0xd3d3d3;

        scollWindow.addLabel(new GuiNpcLabel(8,"Multi Flat", 180, y + 5));
        scollWindow.addTextField(new GuiNpcTextField(8, this, 295, y, 40, 20, String.valueOf(form.mastery.attributeMultiFlat)));
        scollWindow.getTextField(8).setMaxStringLength(10);
        scollWindow.getTextField(8).floatsOnly = true;
        scollWindow.getTextField(8).setMinMaxDefaultFloat(-10000, 10000, 1);
        scollWindow.getLabel(8).color = 0xd3d3d3;

        y += 23;

        scollWindow.addLabel(new GuiNpcLabel(10,"Multi Min/Max", 4, y + 5));
        scollWindow.addTextField(new GuiNpcTextField(10, this, 135, y, 40, 20, String.valueOf(form.mastery.attributeMultiMinOrMax)));
        scollWindow.getTextField(10).setMaxStringLength(10);
        scollWindow.getTextField(10).floatsOnly = true;
        scollWindow.getTextField(10).setMinMaxDefaultFloat(-10000, 10000, 1);
        scollWindow.getLabel(10).color = 0xd3d3d3;

        scollWindow.addLabel(new GuiNpcLabel(9,"Multi Per Level", 180, y + 5));
        scollWindow.addTextField(new GuiNpcTextField(9, this, 295, y, 40, 20, String.valueOf(form.mastery.attributeMultiPerLevel)));
        scollWindow.getTextField(9).setMaxStringLength(10);
        scollWindow.getTextField(9).floatsOnly = true;
        scollWindow.getTextField(9).setMinMaxDefaultFloat(-10000, 10000, 1);
        scollWindow.getLabel(9).color = 0xd3d3d3;

        y += 23;
        scollWindow.addLabel(new GuiNpcLabel(2,"Instant Transform LvL", 4, y + 5));
        scollWindow.addTextField(new GuiNpcTextField(2, this, 135, y, 40, 20, String.valueOf(form.mastery.instantTransformationUnlockLevel)));
        scollWindow.getTextField(2).setMaxStringLength(10);
        scollWindow.getTextField(2).floatsOnly = true;
        scollWindow.getTextField(2).setMinMaxDefaultFloat(-10000, 10000, 1);
        scollWindow.getLabel(2).color = 0xd3d3d3;

        y += 23;
        scollWindow.addLabel(new GuiNpcLabel(19,"Level Gain Settings", 4, y + 5));
        scollWindow.getLabel(19).color = 0xffffff;
        scollWindow.addButton(new GuiNpcButton(19, 135, y, 120, 20, new String[]{"gui.hide", "gui.show"}, showLevelGain ? 1 : 0));
        if(showLevelGain){
            scollWindow.addLabel(new GuiNpcLabel(20201,"--------------------------------------------------", 4, y + 28 - 7));
            scollWindow.getLabel(20201).color = 0xffffff;

            maxScroll += 30;
            y += 30;
            scollWindow.addLabel(new GuiNpcLabel(20,"Update Gain Settings", 4, y + 5));
            scollWindow.getLabel(20).color = 0xffffff;
            scollWindow.addButton(new GuiNpcButton(20, 135, y, 120, 20, new String[]{"gui.hide", "gui.show"}, showUpdateGain ? 1 : 0));
            if(showUpdateGain){
                maxScroll += 23 * 3;
                y += 23;
                scollWindow.addLabel(new GuiNpcLabel(21,"Update Gain", 4, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(21, this, 135, y, 40, 20, String.valueOf(mastery.updateGain)));
                scollWindow.getTextField(21).setMaxStringLength(10);
                scollWindow.getTextField(21).floatsOnly = true;
                scollWindow.getTextField(21).setMinMaxDefaultFloat(0f, 10000f, 0.01f);
                scollWindow.getLabel(21).color = 0xd3d3d3;

                scollWindow.addLabel(new GuiNpcLabel(22,"Flat Mind", 180, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(22, this, 295, y, 40, 20, String.valueOf(mastery.updateMindBonusFlat)));
                scollWindow.getTextField(22).setMaxStringLength(10);
                scollWindow.getTextField(22).floatsOnly = true;
                scollWindow.getTextField(22).setMinMaxDefaultFloat(0f, 10000f, 1.0f);
                scollWindow.getLabel(22).color = 0xd3d3d3;

                y += 23;
                scollWindow.addLabel(new GuiNpcLabel(23,"Per Mind", 4, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(23, this, 135, y, 40, 20, String.valueOf(mastery.updateMindBonusPerMind)));
                scollWindow.getTextField(23).setMaxStringLength(10);
                scollWindow.getTextField(23).floatsOnly = true;
                scollWindow.getTextField(23).setMinMaxDefaultFloat(0f, 10000f, 0.001f);
                scollWindow.getLabel(23).color = 0xd3d3d3;

                scollWindow.addLabel(new GuiNpcLabel(24,"Max Mind", 180, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(24, this, 295, y, 40, 20, String.valueOf(mastery.updateMindBonusMax)));
                scollWindow.getTextField(24).setMaxStringLength(10);
                scollWindow.getTextField(24).floatsOnly = true;
                scollWindow.getTextField(24).setMinMaxDefaultFloat(0f, 10000f, 5f);
                scollWindow.getLabel(24).color = 0xd3d3d3;

                y += 23;
                scollWindow.addLabel(new GuiNpcLabel(25,"Multi Div Plus", 4, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(25, this, 135, y, 40, 20, String.valueOf(mastery.updateMultiDivPlus)));
                scollWindow.getTextField(25).setMaxStringLength(10);
                scollWindow.getTextField(25).floatsOnly = true;
                scollWindow.getTextField(25).setMinMaxDefaultFloat(0f, 10000f, 100f);
                scollWindow.getLabel(25).color = 0xd3d3d3;
            }
            scollWindow.addLabel(new GuiNpcLabel(20202,"--------------------------------------------------", 4, y + 22));
            scollWindow.getLabel(20202).color = 0xffffff;

            maxScroll += 30;
            y += 30;
            scollWindow.addLabel(new GuiNpcLabel(30,"Attack Gain Settings", 4, y + 5));
            scollWindow.getLabel(30).color = 0xffffff;
            scollWindow.addButton(new GuiNpcButton(30, 135, y, 120, 20, new String[]{"gui.hide", "gui.show"}, showAttackGain ? 1 : 0));
            if(showAttackGain){
                maxScroll += 23 * 3;
                y += 23;
                scollWindow.addLabel(new GuiNpcLabel(31,"Attack Gain", 4, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(31, this, 135, y, 40, 20, String.valueOf(mastery.attackGain)));
                scollWindow.getTextField(31).setMaxStringLength(10);
                scollWindow.getTextField(31).floatsOnly = true;
                scollWindow.getTextField(31).setMinMaxDefaultFloat(0f, 10000f, 0.03f);
                scollWindow.getLabel(31).color = 0xd3d3d3;

                scollWindow.addLabel(new GuiNpcLabel(32,"Flat Mind", 180, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(32, this, 295, y, 40, 20, String.valueOf(mastery.attackMindBonusFlat)));
                scollWindow.getTextField(32).setMaxStringLength(10);
                scollWindow.getTextField(32).floatsOnly = true;
                scollWindow.getTextField(32).setMinMaxDefaultFloat(0f, 10000f, 1.0f);
                scollWindow.getLabel(32).color = 0xd3d3d3;

                y += 23;
                scollWindow.addLabel(new GuiNpcLabel(33,"Per Mind", 4, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(33, this, 135, y, 40, 20, String.valueOf(mastery.attackMindBonusPerMind)));
                scollWindow.getTextField(33).setMaxStringLength(10);
                scollWindow.getTextField(33).floatsOnly = true;
                scollWindow.getTextField(33).setMinMaxDefaultFloat(0f, 10000f, 0.001f);
                scollWindow.getLabel(33).color = 0xd3d3d3;

                scollWindow.addLabel(new GuiNpcLabel(34,"Max Mind", 180, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(34, this, 295, y, 40, 20, String.valueOf(mastery.attackMindBonusMax)));
                scollWindow.getTextField(34).setMaxStringLength(10);
                scollWindow.getTextField(34).floatsOnly = true;
                scollWindow.getTextField(34).setMinMaxDefaultFloat(0f, 10000f, 5f);
                scollWindow.getLabel(34).color = 0xd3d3d3;

                y += 23;
                scollWindow.addLabel(new GuiNpcLabel(35,"Multi Div Plus", 4, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(35, this, 135, y, 40, 20, String.valueOf(mastery.attackMultiDivPlus)));
                scollWindow.getTextField(35).setMaxStringLength(10);
                scollWindow.getTextField(35).floatsOnly = true;
                scollWindow.getTextField(35).setMinMaxDefaultFloat(0f, 10000f, 100);
                scollWindow.getLabel(35).color = 0xd3d3d3;
            }
            scollWindow.addLabel(new GuiNpcLabel(30302,"--------------------------------------------------", 4, y + 22));
            scollWindow.getLabel(30302).color = 0xffffff;

            maxScroll += 23;
            y += 30;
            scollWindow.addLabel(new GuiNpcLabel(40,"Damaged Gain Settings", 4, y + 5));
            scollWindow.getLabel(40).color = 0xffffff;
            scollWindow.addButton(new GuiNpcButton(40, 135, y, 120, 20, new String[]{"gui.hide", "gui.show"}, showDamagedGain ? 1 : 0));
            if(showDamagedGain){
                maxScroll += 23 * 3;
                y += 23;
                scollWindow.addLabel(new GuiNpcLabel(41,"Damaged Gain", 4, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(41, this, 135, y, 40, 20, String.valueOf(mastery.damagedGain)));
                scollWindow.getTextField(41).setMaxStringLength(10);
                scollWindow.getTextField(41).floatsOnly = true;
                scollWindow.getTextField(41).setMinMaxDefaultFloat(0f, 10000f, 0.03f);
                scollWindow.getLabel(41).color = 0xd3d3d3;

                scollWindow.addLabel(new GuiNpcLabel(42,"Flat Mind", 180, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(42, this, 295, y, 40, 20, String.valueOf(mastery.damagedMindBonusFlat)));
                scollWindow.getTextField(42).setMaxStringLength(10);
                scollWindow.getTextField(42).floatsOnly = true;
                scollWindow.getTextField(42).setMinMaxDefaultFloat(0f, 10000f, 1.0f);
                scollWindow.getLabel(42).color = 0xd3d3d3;

                y += 23;
                scollWindow.addLabel(new GuiNpcLabel(43,"Per Mind", 4, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(43, this, 135, y, 40, 20, String.valueOf(mastery.damagedMindBonusPerMind)));
                scollWindow.getTextField(43).setMaxStringLength(10);
                scollWindow.getTextField(43).floatsOnly = true;
                scollWindow.getTextField(43).setMinMaxDefaultFloat(0f, 10000f, 0.001f);
                scollWindow.getLabel(43).color = 0xd3d3d3;

                scollWindow.addLabel(new GuiNpcLabel(44,"Max Mind", 180, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(44, this, 295, y, 40, 20, String.valueOf(mastery.damagedMindBonusMax)));
                scollWindow.getTextField(44).setMaxStringLength(10);
                scollWindow.getTextField(44).floatsOnly = true;
                scollWindow.getTextField(44).setMinMaxDefaultFloat(0f, 10000f, 5f);
                scollWindow.getLabel(44).color = 0xd3d3d3;

                y += 23;
                scollWindow.addLabel(new GuiNpcLabel(45,"Multi Div Plus", 4, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(45, this, 135, y, 40, 20, String.valueOf(mastery.damagedMultiDivPlus)));
                scollWindow.getTextField(45).setMaxStringLength(10);
                scollWindow.getTextField(45).floatsOnly = true;
                scollWindow.getTextField(45).setMinMaxDefaultFloat(0f, 10000f, 100f);
                scollWindow.getLabel(45).color = 0xd3d3d3;
            }
            scollWindow.addLabel(new GuiNpcLabel(40402,"--------------------------------------------------", 4, y + 22));
            scollWindow.getLabel(40402).color = 0xffffff;

            maxScroll += 30;
            y += 30;
            scollWindow.addLabel(new GuiNpcLabel(50,"Fire Ki Gain Settings", 4, y + 5));
            scollWindow.getLabel(50).color = 0xffffff;
            scollWindow.addButton(new GuiNpcButton(50, 135, y, 120, 20, new String[]{"gui.hide", "gui.show"}, showFireKiGain ? 1 : 0));
            if(showFireKiGain){
                maxScroll += 23 * 3;
                y += 23;
                scollWindow.addLabel(new GuiNpcLabel(51,"Fire Ki Gain", 4, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(51, this, 135, y, 40, 20, String.valueOf(mastery.fireKiGain)));
                scollWindow.getTextField(51).setMaxStringLength(10);
                scollWindow.getTextField(51).floatsOnly = true;
                scollWindow.getTextField(51).setMinMaxDefaultFloat(0f, 10000f, 0.03f);
                scollWindow.getLabel(51).color = 0xd3d3d3;

                scollWindow.addLabel(new GuiNpcLabel(52,"Flat Mind", 180, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(52, this, 295, y, 40, 20, String.valueOf(mastery.fireKiMindBonusFlat)));
                scollWindow.getTextField(52).setMaxStringLength(10);
                scollWindow.getTextField(52).floatsOnly = true;
                scollWindow.getTextField(52).setMinMaxDefaultFloat(0f, 10000f, 1.0f);
                scollWindow.getLabel(52).color = 0xd3d3d3;

                y += 23;
                scollWindow.addLabel(new GuiNpcLabel(53,"Per Mind", 4, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(53, this, 135, y, 40, 20, String.valueOf(mastery.fireKiMindBonusPerMind)));
                scollWindow.getTextField(53).setMaxStringLength(10);
                scollWindow.getTextField(53).floatsOnly = true;
                scollWindow.getTextField(53).setMinMaxDefaultFloat(0f, 10000f, 0.001f);
                scollWindow.getLabel(53).color = 0xd3d3d3;

                scollWindow.addLabel(new GuiNpcLabel(54,"Max Mind", 180, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(54, this, 295, y, 40, 20, String.valueOf(mastery.fireKiMindBonusMax)));
                scollWindow.getTextField(54).setMaxStringLength(10);
                scollWindow.getTextField(54).floatsOnly = true;
                scollWindow.getTextField(54).setMinMaxDefaultFloat(0f, 10000f, 5f);
                scollWindow.getLabel(54).color = 0xd3d3d3;

                y += 23;
                scollWindow.addLabel(new GuiNpcLabel(55,"Multi Div Plus", 4, y + 5));
                scollWindow.addTextField(new GuiNpcTextField(55, this, 135, y, 40, 20, String.valueOf(mastery.fireKiMultiDivPlus)));
                scollWindow.getTextField(55).setMaxStringLength(10);
                scollWindow.getTextField(55).floatsOnly = true;
                scollWindow.getTextField(55).setMinMaxDefaultFloat(0f, 10000f, 100f);
                scollWindow.getLabel(55).color = 0xd3d3d3;
            }
        }


        y += 23; y += 23; y += 23; y += 23; y += 23;

        scollWindow.addLabel(new GuiNpcLabel(4,"Max Heat", 190, y + 5));
        scollWindow.addTextField(new GuiNpcTextField(4, this, 300, y, 50, 20, String.valueOf(form.mastery.maxHeat)));
        scollWindow.getTextField(4).setMaxStringLength(10);
        scollWindow.getTextField(4).floatsOnly = true;
        scollWindow.getTextField(4).setMinMaxDefaultFloat(-10000, 10000, 1);
        scollWindow.getLabel(4).color = 0xd3d3d3;

        y += 23;

        scollWindow.addLabel(new GuiNpcLabel(11,"Ki Drain", 4, y + 5));
        scollWindow.addTextField(new GuiNpcTextField(11, this, 135, y, 50, 20, String.valueOf(form.mastery.kiDrain)));
        scollWindow.getTextField(11).setMaxStringLength(10);
        scollWindow.getTextField(11).floatsOnly = true;
        scollWindow.getTextField(11).setMinMaxDefaultFloat(-10000, 10000, 1);
        scollWindow.getLabel(11).color = 0xd3d3d3;

        scollWindow.addLabel(new GuiNpcLabel(12,"Ki Drain Ticks", 190, y + 5));
        scollWindow.addTextField(new GuiNpcTextField(12, this, 300, y, 50, 20, String.valueOf(form.mastery.kiDrainTimer)));
        scollWindow.getTextField(12).setMaxStringLength(10);
        scollWindow.getTextField(12).floatsOnly = true;
        scollWindow.getTextField(12).setMinMaxDefaultFloat(-10000, 10000, 1);
        scollWindow.getLabel(12).color = 0xd3d3d3;

        y += 23;

        scollWindow.addLabel(new GuiNpcLabel(5,"Damage Negation", 4, y + 5));
        scollWindow.addTextField(new GuiNpcTextField(5, this, 135, y, 50, 20, String.valueOf(form.mastery.damageNegation)));
        scollWindow.getTextField(5).setMaxStringLength(10);
        scollWindow.getTextField(5).floatsOnly = true;
        scollWindow.getTextField(5).setMinMaxDefaultFloat(-10000, 10000, 1);
        scollWindow.getLabel(5).color = 0xd3d3d3;

        scollWindow.addLabel(new GuiNpcLabel(6,"Dodge Chance", 190, y + 5));
        scollWindow.addTextField(new GuiNpcTextField(6, this, 300, y, 50, 20, String.valueOf(form.mastery.dodgeChance)));
        scollWindow.getTextField(6).setMaxStringLength(10);
        scollWindow.getTextField(6).floatsOnly = true;
        scollWindow.getTextField(6).setMinMaxDefaultFloat(-10000, 10000, 1);
        scollWindow.getLabel(6).color = 0xd3d3d3;

        y += 23;
        scollWindow.addLabel(new GuiNpcLabel(7,"Pain Time", 4, y + 5));
        scollWindow.addTextField(new GuiNpcTextField(7, this, 135, y, 50, 20, String.valueOf(form.mastery.painTime)));
        scollWindow.getTextField(7).setMaxStringLength(10);
        scollWindow.getTextField(7).floatsOnly = true;
        scollWindow.getTextField(7).setMinMaxDefaultFloat(-10000, 10000, 1);
        scollWindow.getLabel(7).color = 0xd3d3d3;

        scollWindow.addLabel(new GuiNpcLabel(3,"Health Requirement", 190, y + 5));
        scollWindow.addTextField(new GuiNpcTextField(3, this, 300, y, 50, 20, String.valueOf(form.mastery.healthRequirement)));
        scollWindow.getTextField(3).setMaxStringLength(10);
        scollWindow.getTextField(3).floatsOnly = true;
        scollWindow.getTextField(3).setMinMaxDefaultFloat(-10000, 10000, 1);
        scollWindow.getLabel(3).color = 0xd3d3d3;

        y += 23;

        y += 23;

        scollWindow.maxScrollY = maxScroll;

        scollWindow.addButton(new GuiNpcButton(1010101, 200, 192, 50, 20, "RELOAD"));
	}

	public void buttonEvent(GuiButton guibutton)
    {
		GuiNpcButton button = (GuiNpcButton) guibutton;

        if(button.id == 1010101){
            initGui();
        }
	}


	@Override
	public void unFocused(GuiNpcTextField guiNpcTextField) {

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
