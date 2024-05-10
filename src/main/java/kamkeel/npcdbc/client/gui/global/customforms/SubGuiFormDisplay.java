package kamkeel.npcdbc.client.gui.global.customforms;

import kamkeel.npcdbc.data.form.FormDisplay;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.client.gui.util.SubGuiInterface;

public class SubGuiFormDisplay extends SubGuiInterface implements ITextfieldListener {

    FormDisplay display;


    public SubGuiFormDisplay(FormDisplay display){
        this.display = display;
        this.closeOnEsc = true;
        this.drawDefaultBackground = true;
        xSize = 256;
        this.setBackground("menubg.png");
    }

    @Override
    public void initGui(){
        super.initGui();
    }

    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {

    }
}
