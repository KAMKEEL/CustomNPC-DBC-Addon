package kamkeel.npcdbc.client.gui;

import kamkeel.npcdbc.data.npc.DBCStats;
import kamkeel.npcdbc.mixins.late.INPCStats;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityNPCInterface;

public class SubGuiDBCProperties extends SubGuiInterface implements ITextfieldListener
{
    public SubGuiDBCProperties(EntityNPCInterface stats)
    {
        this.npc = stats;
		setBackground("menubg.png");
		xSize = 256;
		ySize = 216;
		closeOnEsc = true;
    }

    public void initGui()
    {
        super.initGui();
        int y = guiTop + 4;

        addLabel(new GuiNpcLabel(-1, "stats.lockOnState", guiLeft + 5, y + 5));
        addButton(new GuiNpcButtonYesNo(-1, guiLeft + 145, y, 50, 20, getDBCData(npc).canBeLockedOn()));

        y+=22;

        addLabel(new GuiNpcLabel(0,"gui.enabled", guiLeft + 5, y + 5));
        addButton(new GuiNpcButtonYesNo(0, guiLeft + 145, y, 50, 20, getDBCData(npc).enabled));

        if(getDBCData(npc).enabled){
            addButton(new GuiNpcButtonYesNo(8, guiLeft + 145, y+=22, 50, 20, getDBCData(npc).isFriendlyFist()));
            addLabel(new GuiNpcLabel(8,"stats.friendlyFist", guiLeft + 5, y + 5));

            if(getDBCData(npc).isFriendlyFist()){
                addTextField(new GuiNpcTextField(9, this, fontRendererObj, guiLeft + 200, y, 50, 20, getDBCData(npc).getFriendlyFistAmount() + ""));
                getTextField(9).integersOnly = true;
                getTextField(9).setMinMaxDefault(1, 60, 6);
            }

            addButton(new GuiNpcButtonYesNo(1, guiLeft + 145, y+=22, 50, 20, getDBCData(npc).isIgnoreDex()));
            addLabel(new GuiNpcLabel(1,"stats.ignoreDex", guiLeft + 5, y + 5));

            addButton(new GuiNpcButtonYesNo(2, guiLeft + 145, y+=22, 50, 20, getDBCData(npc).isIgnoreBlock()));
            addLabel(new GuiNpcLabel(2,"stats.ignoreBlock", guiLeft + 5, y + 5));

            addButton(new GuiNpcButtonYesNo(3, guiLeft + 145, y+=22, 50, 20, getDBCData(npc).isIgnoreEndurance()));
            addLabel(new GuiNpcLabel(3,"stats.ignoreEndurance", guiLeft + 5, y + 5));

            addButton(new GuiNpcButtonYesNo(4, guiLeft + 145, y+=22, 50, 20, getDBCData(npc).isIgnoreKiProtection()));
            addLabel(new GuiNpcLabel(4,"stats.ignoreKiDamage",guiLeft + 5, y + 5));

            addButton(new GuiNpcButtonYesNo(7, guiLeft + 145, y+=22, 50, 20, getDBCData(npc).isIgnoreFormReduction()));
            addLabel(new GuiNpcLabel(7,"stats.ignoreFormDefense",guiLeft + 5, y + 5));

            addButton(new GuiNpcButtonYesNo(5, guiLeft + 145, y+=22, 50, 20, getDBCData(npc).hasDefensePenetration()));
            addLabel(new GuiNpcLabel(5,"stats.hasDefensePenetration",guiLeft + 5, y + 5));

            if(getDBCData(npc).hasDefensePenetration()){
                addTextField(new GuiNpcTextField(6, this, fontRendererObj, guiLeft + 200, y, 50, 20, getDBCData(npc).getDefensePenetration() + ""));
                getTextField(6).integersOnly = true;
                getTextField(6).setMinMaxDefault(0, 100, 10);
            }
        }

		addButton(new GuiNpcButton(66, guiLeft + 230, guiTop + 190, 20, 20, "X"));
    }

	protected void actionPerformed(GuiButton guibutton)
    {
		GuiNpcButton button = (GuiNpcButton) guibutton;
        if(button.id == -1){
            getDBCData(npc).setLockOnState(((GuiNpcButtonYesNo) button).getBoolean());
        }
        if (button.id == 0) {
            getDBCData(npc).enabled = ((GuiNpcButtonYesNo) button).getBoolean();
            initGui();
        }
        if (button.id == 1) {
            getDBCData(npc).setIgnoreDex(((GuiNpcButtonYesNo) button).getBoolean());
            initGui();
        }
        if (button.id == 2) {
            getDBCData(npc).setIgnoreBlock(((GuiNpcButtonYesNo) button).getBoolean());
            initGui();
        }
        if (button.id == 3) {
            getDBCData(npc).setIgnoreEndurance(((GuiNpcButtonYesNo) button).getBoolean());
            initGui();
        }
        if (button.id == 4) {
            getDBCData(npc).setIgnoreKiProtection(((GuiNpcButtonYesNo) button).getBoolean());
            initGui();
        }
        if (button.id == 5) {
            getDBCData(npc).setHasDefensePenetration(((GuiNpcButtonYesNo) button).getBoolean());
            initGui();
        }
        if (button.id == 7) {
            getDBCData(npc).setIgnoreFormReduction(((GuiNpcButtonYesNo) button).getBoolean());
            initGui();
        }
        if (button.id == 8) {
            getDBCData(npc).setFriendlyFist(((GuiNpcButtonYesNo) button).getBoolean());
            initGui();
        }
        if(button.id == 66)
        {
        	close();
        }
    }

    public DBCStats getDBCData(EntityNPCInterface npc){
        return ((INPCStats)npc.stats).getDBCStats();
    }

    @Override
    public void unFocused(GuiNpcTextField textfield) {
        if(textfield.id == 6){
            getDBCData(npc).setDefensePenetration(textfield.getInteger());
        }
        if(textfield.id == 9){
            getDBCData(npc).setFriendlyFistAmount(textfield.getInteger());
        }
    }
}
