package kamkeel.npcdbc.client.gui.global.customforms;

import kamkeel.npcdbc.data.form.FormStackable;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.client.gui.util.SubGuiInterface;

public class SubGuiFormStackables extends SubGuiInterface implements ITextfieldListener {

    FormStackable stackable;

    public SubGuiFormStackables(FormStackable stackable){
        this.stackable = stackable;
        this.closeOnEsc = true;
        this.drawDefaultBackground = true;
        xSize = 256;
        this.setBackground("menubg.png");
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {

    }
}
