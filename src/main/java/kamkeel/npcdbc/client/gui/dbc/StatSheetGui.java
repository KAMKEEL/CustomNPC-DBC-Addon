package kamkeel.npcdbc.client.gui.dbc;

import JinRyuu.JRMCore.JRMCoreGuiButtons00;
import JinRyuu.JRMCore.JRMCoreGuiScreen;
import cpw.mods.fml.common.FMLCommonHandler;
import kamkeel.npcdbc.client.gui.dbc.constants.GuiButtonConstants;
import kamkeel.npcdbc.mixin.IDBCGuiScreen;
import net.minecraft.client.gui.GuiButton;

public class StatSheetGui extends AbstractJRMCGui {

    public static boolean overrideBaseDBC = false;

    public StatSheetGui() {
        super(10);
    }

    protected StatSheetGui(int guiReplacementID){
        super(guiReplacementID);
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        this.drawBackground();

        addStatusEffects();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui(){
        super.initGui();

        addServerButtons();



    }

    @Override
    protected void actionPerformed(GuiButton button){
        super.actionPerformed(button);

//        JRMCoreGuiScreen DBCScreen = new JRMCoreGuiScreen(0);
//        ((IDBCGuiScreen) (Object) DBCScreen).setGuiIDPostInit(button.id);
//        FMLCommonHandler.instance().showGuiScreen(DBCScreen);
    }
}
