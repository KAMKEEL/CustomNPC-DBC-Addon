package kamkeel.npcdbc.client.gui.component;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormStackable;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.util.*;

public class SubGuiKaiokenDrain extends SubGuiInterface implements ITextfieldListener {
    public Form form;
    public FormStackable stackable;
    public boolean editStrained;
    public SubGuiKaiokenDrain(Form form) {
        this.form = form;
        this.stackable = form.stackable;

        setBackground("menubg.png");
        xSize = 300;
    }

    @Override
    public void initGui(){
        super.initGui();

        int y = guiTop + 5;
        addLabel(new GuiNpcLabel(1, "Drain multi scaling: ", guiLeft+8, y + 5));
        addTextField(new GuiNpcTextField(1, this, guiLeft+150, y, 50, 20, ""+stackable.kaiokenDrainMulti));
        getTextField(1).setMaxStringLength(22);
        getTextField(1).floatsOnly = true;
        getTextField(1).setMinMaxDefaultFloat(-50, 50, 1);
        addButton(new GuiNpcButton(10, guiLeft + 240, y, 50, 20, "gui.close"));

        y += 23;
        addLabel(new GuiNpcLabel(11, "Multiply current form drain:", guiLeft+8, y+5));
        addButton(new GuiNpcButtonYesNo(11, guiLeft+150, y, stackable.kaiokenMultipliesCurrentFormDrain));
        if(stackable.kaiokenMultipliesCurrentFormDrain)
            return;
        y += 23;
        addLabel(new GuiNpcLabel(2, "Strained Kaioken Multis:", guiLeft+8, y+5));
        addButton(new GuiNpcButtonYesNo(2, guiLeft+150, y, editStrained));
        y += 23;
        int color = editStrained ? 0xFF5555 : CustomNpcResourceListener.DefaultTextColor;
        for(int i = 0; i < 6; i++){
            addLabel(new GuiNpcLabel(3+i, "Kaioken " + JRMCoreH.TransKaiNms[i+1] + " Drain Multi: ", guiLeft+8, y+5, color));
            addTextField(new GuiNpcTextField(3+i, this, fontRendererObj, guiLeft+150, y, 50, 20, ""+stackable.getKaioState2Balance(i, editStrained)));
            getTextField(3+i).setMaxStringLength(22);
            getTextField(3+i).floatsOnly = true;
            getTextField(3+i).setMinMaxDefaultFloat(-50, 50, 1);
            y += 23;
        }

    }

    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {
        int id = guiNpcTextField.id;
        if(id == 1){
            stackable.setKaioDrain(guiNpcTextField.getFloat());
        }
        if(id >= 3 && id <= 8){
            stackable.setKaioState2Balance(id-3, editStrained, guiNpcTextField.getFloat());
        }

    }

    @Override
    public void actionPerformed(GuiButton button){
        if(button.id == 11){
            stackable.kaiokenMultipliesCurrentFormDrain = !stackable.kaiokenMultipliesCurrentFormDrain;
            initGui();
        }
        if(button.id == 2){
            editStrained = !editStrained;
            initGui();
        }
        if(button.id == 10){
            close();
        }
    }
}
