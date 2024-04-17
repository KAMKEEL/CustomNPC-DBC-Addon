package kamkeel.npcdbc.client.gui;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.data.CustomForm;
import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.util.Utility;
import noppes.npcs.client.gui.util.GuiNpcLabel;

public class GuiFormData extends GuiDBC {
    public PlayerCustomFormData formData;
    GuiDBC parent;
    CustomForm form;

    int formDataID = 200;

    public GuiFormData(GuiDBC parent, CustomForm form) {
        this.parent = parent;
        this.form = form;
        xSize = 135;
        ySize = 150;
        guiLeft = parent.guiLeft + 5;
        guiTop = parent.guiTop + 5;
        formData = Utility.getFormDataClient();

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
        parent.addLabel(new GuiNpcLabel(++formDataID, "Kaioken: " + form.isFormStackable(DBCForm.Kaioken), guiLeft + 8, guiTop + 14 + y));
        y += 15;
        parent.addLabel(new GuiNpcLabel(++formDataID, "Mystic: " + form.isFormStackable(DBCForm.Mystic), guiLeft + 8, guiTop + 14 + y));
        y += 15;
        parent.addLabel(new GuiNpcLabel(++formDataID, "Ultra Instinct: " + form.isFormStackable(DBCForm.UltraInstinct), guiLeft + 8, guiTop + 14 + y));
        y += 15;
        parent.addLabel(new GuiNpcLabel(++formDataID, "God Of Destruction:  " + form.isFormStackable(DBCForm.GodOfDestruction), guiLeft + 8, guiTop + 14 + y));

    }

    public void drawScreen(int i, int j, float f) {
        super.drawScreen(i, j, f);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 252, 195);

    }
}
