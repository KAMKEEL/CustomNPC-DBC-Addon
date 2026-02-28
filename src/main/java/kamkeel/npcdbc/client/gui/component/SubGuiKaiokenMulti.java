package kamkeel.npcdbc.client.gui.component;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormKaiokenStackableData;
import kamkeel.npcdbc.data.form.FormStackable;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcButtonYesNo;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.client.gui.util.SubGuiInterface;

public class SubGuiKaiokenMulti extends SubGuiInterface implements ITextfieldListener {
    public Form form;
    public FormStackable stackable;
    FormKaiokenStackableData kaioStackable;

    public SubGuiKaiokenMulti(Form form) {
        this.form = form;
        this.stackable = form.stackable;
        this.kaioStackable = form.stackable.kaiokenData;

        setBackground("menubg.png");
        xSize = 300;
    }


    @Override
    public void initGui() {
        super.initGui();

        int y = guiTop + 5;

        addLabel(new GuiNpcLabel(1, "kaioken.multiScaling", guiLeft + 8, y + 5));
        addTextField(new GuiNpcTextField(1, this, guiLeft + 150, y, 50, 20, "" + kaioStackable.attributeMultiScalar));
        getTextField(1).setMaxStringLength(22);
        getTextField(1).floatsOnly = true;
        getTextField(1).setMinMaxDefaultFloat(0, 50, 1);
        addButton(new GuiNpcButton(10, guiLeft + 240, y, 50, 20, "gui.close"));

        y += 23;
        addLabel(new GuiNpcLabel(11, "kaioken.useGlobalMulti", guiLeft + 8, y + 5));
        addButton(new GuiNpcButtonYesNo(11, guiLeft + 150, y, kaioStackable.isUsingGlobalAttributeMultis));
        if (kaioStackable.isUsingGlobalAttributeMultis)
            return;
        y += 23;
        y += 23;
        for (int i = 0; i < 6; i++) {
            addLabel(new GuiNpcLabel(3 + i, StatCollector.translateToLocalFormatted("kaioken.multi", JRMCoreH.TransKaiNms[i + 1]), guiLeft + 8, y + 5));
            addTextField(new GuiNpcTextField(3 + i, this, fontRendererObj, guiLeft + 150, y, 50, 20, "" + kaioStackable.getKaiokenAttributeMulti(i)));
            getTextField(3 + i).setMaxStringLength(22);
            getTextField(3 + i).floatsOnly = true;
            getTextField(3 + i).setMinMaxDefaultFloat(0, 50, 1);
            y += 23;
        }

    }

    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {
        int id = guiNpcTextField.id;
        if (id == 1) {
            kaioStackable.setKaiokenMultiScalar(guiNpcTextField.getFloat());
        }
        if (id >= 3 && id <= 8) {
            kaioStackable.setKaiokenAttributeMulti(id - 3, guiNpcTextField.getFloat());
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 11) {
            kaioStackable.isUsingGlobalAttributeMultis = !kaioStackable.isUsingGlobalAttributeMultis;
            initGui();
        }
        if (button.id == 10) {
            close();
        }
    }
}
