package kamkeel.npcdbc.client.gui.component;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.client.gui.inventory.GuiDBC;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.util.PlayerDataUtil;
import noppes.npcs.client.gui.util.GuiNpcLabel;

public class GuiFormLabel extends GuiDBC {
    public PlayerDBCInfo formData;
    GuiDBC parent;
    Form form;

    int formDataID = 200;

    public GuiFormLabel(GuiDBC parent, Form form) {
        this.parent = parent;
        this.form = form;
        xSize = 135;
        ySize = 150;
        guiLeft = parent.guiLeft + 5;
        guiTop = parent.guiTop + 5;
        formData = PlayerDataUtil.getClientDBCInfo();

    }

    //WIP, i haven't touched the label positions at all
    public void initGui() {
        String fColor = formData.getFormColorCode(form);

        int y = 0;
        parent.addLabel(new GuiNpcLabel(++formDataID, formData.getColoredName(form), guiLeft + 8, guiTop + 14 + y)); //form name
        y += 15;
        parent.addLabel(new GuiNpcLabel(++formDataID, "Attribute Multipliers", guiLeft + 8, guiTop + 14 + y));
        y += 15;
        parent.addLabel(new GuiNpcLabel(++formDataID, "STR: " + fColor + JRMCoreH.round(form.getAttributeMulti(0), 1) + "x", guiLeft + 8, guiTop + 14 + y));
        y += 15;
        parent.addLabel(new GuiNpcLabel(++formDataID, "DEX: " + fColor + JRMCoreH.round(form.getAttributeMulti(1), 1) + "x", guiLeft + 8, guiTop + 14 + y));
        y += 15;
        parent.addLabel(new GuiNpcLabel(++formDataID, "CON: " + fColor + JRMCoreH.round(form.getAttributeMulti(3), 1) + "x", guiLeft + 8, guiTop + 14 + y));
        y += 15;
        parent.addLabel(new GuiNpcLabel(++formDataID, "Stackable Forms", guiLeft + 8, guiTop + 14 + y));
        y += 15;
        parent.addLabel(new GuiNpcLabel(++formDataID, "Kaioken: " + form.stackable.isFormStackable(DBCForm.Kaioken), guiLeft + 8, guiTop + 14 + y));
        y += 15;
        parent.addLabel(new GuiNpcLabel(++formDataID, "Mystic: " + form.stackable.isFormStackable(DBCForm.Mystic), guiLeft + 8, guiTop + 14 + y));
        y += 15;
        parent.addLabel(new GuiNpcLabel(++formDataID, "Ultra Instinct: " + form.stackable.isFormStackable(DBCForm.UltraInstinct), guiLeft + 8, guiTop + 14 + y));
        y += 15;
        parent.addLabel(new GuiNpcLabel(++formDataID, "God Of Destruction:  " + form.stackable.isFormStackable(DBCForm.GodOfDestruction), guiLeft + 8, guiTop + 14 + y));

    }

    public void drawScreen(int i, int j, float f) {
        super.drawScreen(i, j, f);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 252, 195);

    }
}
