package kamkeel.npcdbc.client.gui.dbc;

import JinRyuu.JRMCore.JRMCoreGuiScreen;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.data.form.Form;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class GuiFormCustomizer extends AbstractJRMCGui {
    private final GuiScreen parent;
    private final Form form;

    public GuiFormCustomizer(GuiScreen parentScreen, Form currentForm) {
        super(-1);
        this.parent = parentScreen;
        this.form = currentForm;
    }
    @Override
    public void initGui() {
        if (ConfigDBCClient.DarkMode) {
            menuTexture = new ResourceLocation(CustomNpcPlusDBC.ID + ":textures/gui/gui_dark.png");
            JRMCoreGuiScreen.wish = CustomNpcPlusDBC.ID + ":textures/gui/gui_dark.png";
            JRMCoreGuiScreen.button1 = CustomNpcPlusDBC.ID + ":textures/gui/button_dark.png";
        } else {
            menuTexture = new ResourceLocation(CustomNpcPlusDBC.ID + ":textures/gui/gui_light.png");
            JRMCoreGuiScreen.wish = CustomNpcPlusDBC.ID + ":textures/gui/gui_light.png";
            JRMCoreGuiScreen.button1 = CustomNpcPlusDBC.ID + ":textures/gui/button_light.png";
        }

        this.buttonList.clear();
        this.labelList.clear();
        this.hoverableStaticLabels.clear();
        this.dynamicLabels.clear();

        this.guiWidthOffset = (this.width - menuImageWidth) / 2;
        this.guiHeightOffset = (this.height - menuImageHeight) / 2;


    }

}
