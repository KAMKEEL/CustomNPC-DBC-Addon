package kamkeel.npcdbc.client.gui.global.form;

import kamkeel.npcdbc.data.form.Form;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.*;

public class SubGuiFormMastery extends SubGuiInterface implements ISubGuiListener, GuiSelectionListener,ITextfieldListener
{
    private GuiNpcFormMenu menu;
	public Form form;

	public SubGuiFormMastery(GuiNPCManageForms parent, Form form)
	{
		this.form = form;

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


        addButton(new GuiNpcButton(1010101, guiLeft + 200, guiTop + 192, 50, 20, "RELOAD"));
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
