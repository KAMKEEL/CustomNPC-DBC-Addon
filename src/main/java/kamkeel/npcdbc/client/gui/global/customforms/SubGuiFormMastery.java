package kamkeel.npcdbc.client.gui.global.customforms;

import kamkeel.npcdbc.api.form.IFormMastery;
import kamkeel.npcdbc.data.form.FormMastery;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.client.gui.util.SubGuiInterface;

public class SubGuiFormMastery extends SubGuiInterface implements ITextfieldListener {

    FormMastery mastery;

    public SubGuiFormMastery(FormMastery formMastery){
        this.mastery = formMastery;
        this.closeOnEsc = true;
        this.drawDefaultBackground = false;
        this.setBackground("menubg.png");
    }
    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {

    }
}
