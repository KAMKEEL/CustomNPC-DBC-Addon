package kamkeel.npcdbc.client.gui.dbc;

import JinRyuu.JRMCore.JRMCoreGuiScreen;
import cpw.mods.fml.common.FMLCommonHandler;
import kamkeel.npcdbc.mixin.IDBCGuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class StatSheetGUI extends GuiScreen {

    public StatSheetGUI(){
        System.out.println("RENDERING LOLOOLOOL");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui(){
        this.buttonList.clear();

        GuiButton takeMeBack = new GuiButton(60, 0, 0, "Story");
        GuiButton ki = new GuiButton(17, 0, 20, "Training");
//        takeMeBack.visible = true;
//        ki.visible = true;
        this.buttonList.add(takeMeBack);
        this.buttonList.add(ki);


    }

    @Override
    public boolean doesGuiPauseGame(){
        return false;
    }

    @Override
    protected void actionPerformed(GuiButton button){
        JRMCoreGuiScreen DBCScreen = new JRMCoreGuiScreen(0);
        ((IDBCGuiScreen) (Object) DBCScreen).setGuiIDPostInit(button.id);
        FMLCommonHandler.instance().showGuiScreen(DBCScreen);
    }
}
