package kamkeel.npcdbc.client.gui.dbc;

import JinRyuu.JRMCore.JRMCoreGuiScreen;
import cpw.mods.fml.common.FMLCommonHandler;
import kamkeel.npcdbc.mixin.IDBCGuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class StatSheetGUI extends GuiScreen {

    public static boolean overrideBaseDBC = false;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui(){
        this.buttonList.clear();


        this.buttonList.add(new GuiButton(31, 0, 0, "News"));

        //Only render these if powertype == 1
        //BEGIN
        this.buttonList.add(new GuiButton(11, 0, 20, "Skills"));
        this.buttonList.add(new GuiButton(12, 0, 40, "ki"));
        this.buttonList.add(new GuiButton(17, 0, 60, "Training"));
        this.buttonList.add(new GuiButton(60, 0, 80, "Story"));
        this.buttonList.add(new GuiButton(70, 0, 100, "Group Management"));
        //END


        this.buttonList.add(new GuiButton(40, 0, 140, "ServerConfig"));


        this.buttonList.add(new GuiButton(10000, 0, 180, "ClientSettings"));
        this.buttonList.add(new GuiButton(10001, 0, 200, "Help"));
        this.buttonList.add(new GuiButton(10011, 0, 220, "Notifications"));

        this.buttonList.add(new GuiButton(14, 0, 260, "Difficulty"));
        this.buttonList.add(new GuiButton(80, 0, 280, "Server Shop"));



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
