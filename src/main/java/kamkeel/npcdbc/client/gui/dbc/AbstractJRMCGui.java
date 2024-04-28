package kamkeel.npcdbc.client.gui.dbc;

import JinRyuu.JRMCore.*;
import cpw.mods.fml.common.FMLCommonHandler;
import kamkeel.npcdbc.client.gui.dbc.constants.GuiInfo;
import kamkeel.npcdbc.mixin.IDBCGuiScreen;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractJRMCGui extends GuiScreen implements GuiYesNoCallback {
    private static final ResourceLocation menuTexture = new ResourceLocation("jinryuumodscore:gui2.png");
    protected List<Gui> dynamicElements = new ArrayList<>();
    protected final int guiID;
    protected int menuImageWidth = 256;
    protected int menuImageHeight = 159;

    protected int guiWidthOffset;
    protected int guiHeightOffset;

    private URI clickedUrl;

    /**
     * @param guiReplacementID ID of the JRMC Gui this object is replacing
     */
    public AbstractJRMCGui(int guiReplacementID){
        this.guiID = guiReplacementID;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        super.drawScreen(mouseX, mouseY, partialTicks);

        for (Gui element : this.dynamicElements) {
            if(element instanceof GuiLabel)
                ((GuiLabel) element).func_146159_a(this.mc, mouseX, mouseY);
            else if(element instanceof GuiButton)
                ((GuiButton) element).drawButton(this.mc, mouseX, mouseY);
        }

        //Going over these twice specifically because of wrong layering
        for(Object label : this.labelList){
            if(label instanceof HoverableLabel)
                ((HoverableLabel) label).hover(this.mc, mouseX, mouseY);
        }
        for (Gui element : this.dynamicElements) {
            if(element instanceof HoverableLabel)
                ((HoverableLabel) element).hover(this.mc, mouseX, mouseY);
        }
    }

    protected void drawBackground(){
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(menuTexture);
        this.drawTexturedModalRect((this.width-menuImageWidth)/2, (this.height-menuImageHeight)/2,0, 0, menuImageWidth, menuImageHeight);
    }

    @Override
    public void initGui(){
        this.buttonList.clear();
        this.labelList.clear();

        this.guiWidthOffset = (this.width - menuImageWidth) / 2;
        this.guiHeightOffset = (this.height - menuImageHeight) / 2;

        addCloseButton();
        addNavbarButtons();
        addClientHelpButtons();
    }

    @Override
    public boolean doesGuiPauseGame(){
        return false;
    }

    @Override
    protected void actionPerformed(GuiButton button){
        int id = button.id;

        switch(id){
            case GuiInfo.EXIT:
                this.mc.thePlayer.closeScreen();
                return;
            case GuiInfo.FAMILY_C_ID:
                JRMCoreHJFC.openGui(1, this.mc.thePlayer);
                return;
            case GuiInfo.YEARS_C_ID:
                JRMCoreHJYC.openGui(1, this.mc.thePlayer);
                return;
            case GuiInfo.SERVER_SITE_ID:
                this.openUrlBox("http://dbcserver1710.jingames.net");
            default:
                break;
        }

        for(GuiInfo.ReferenceIDs ref : GuiInfo.ReferenceIDs.values()){
            if(ref.getButtonId() == id){
                JRMCoreGuiScreen DBCScreen = new JRMCoreGuiScreen(0);
                ((IDBCGuiScreen) DBCScreen).setGuiIDPostInit(ref.getGuiID());
                FMLCommonHandler.instance().showGuiScreen(DBCScreen);
                return;
            }
        }
    }

    private void openUrlBox(String url) {
        try{
            this.clickedUrl = new URI(url);
        } catch (Exception ignored) {}
        mc.displayGuiScreen(new GuiConfirmOpenLink(this, url, 0, false));
    }

    protected void addClientHelpButtons(){
        this.guiWidthOffset = (this.width - menuImageWidth) / 2;
        this.guiHeightOffset = (this.height - menuImageHeight) / 2;

        GuiInfo.ReferenceIDs[] referenceArr = new GuiInfo.ReferenceIDs[]{
                GuiInfo.ReferenceIDs.CLIENT_SETTINGS,
                GuiInfo.ReferenceIDs.HELP_MENU,
                GuiInfo.ReferenceIDs.NOTIFICATIONS
        };
        int offsetX = -60 + 25;
        int offsetY = -60;

        for(GuiInfo.ReferenceIDs ref : referenceArr){
            String name = ref.getTranslation();
            GuiButton button = new JRMCoreGuiButtons03(ref.getButtonId(), guiWidthOffset+offsetX, guiHeightOffset+menuImageHeight+2+offsetY, name.substring(0, 2).toUpperCase(), 0, 8046079, ref.getIconID());
            buttonList.add(button);
            this.labelList.add(new JRMCoreLabel(button, name));
            offsetY -= 20;
        }
    }

    protected void addServerButtons(){
        this.guiWidthOffset = (this.width - menuImageWidth) / 2;
        this.guiHeightOffset = (this.height - menuImageHeight) / 2;

        String s = "Official DBC Server's site";
        int i = this.fontRendererObj.getStringWidth(s);
        this.buttonList.add(new JRMCoreGuiButtons00(GuiInfo.SERVER_SITE_ID, guiWidthOffset + 260, guiHeightOffset + 85 - 40, i + 8, 20, s, 0));

        if (!JRMCoreConfig.ssurl.contains("empty") && JRMCoreConfig.ssurl.contains("ttp")) {
            s = GuiInfo.ReferenceIDs.SERVER_SHOP.getTranslation();
            i = this.fontRendererObj.getStringWidth(s);
            this.buttonList.add(new JRMCoreGuiButtons00(3099, guiWidthOffset + 260, guiHeightOffset + 85, i + 8, 20, s, 0));
        }

    }

    protected void addCloseButton(){
        this.guiWidthOffset = (this.width - menuImageWidth) / 2;
        this.guiHeightOffset = (this.height - menuImageHeight) / 2;
        this.buttonList.add(new JRMCoreGuiButtons00(GuiInfo.EXIT, guiWidthOffset + menuImageWidth/2 -150, guiHeightOffset+menuImageHeight/2+66, 20, 20, "X", 0));

    }

    protected void addNavbarButtons(){
        GuiInfo.ReferenceIDs[] guiReferences = GuiInfo.ReferenceIDs.values();
        int xOffset = 0;



        if (!JRMCoreConfig.ssurl.contains("empty") && JRMCoreConfig.ssurl.contains("ttp")) {
            GuiInfo.ReferenceIDs ref = GuiInfo.ReferenceIDs.SERVER_SHOP;
            //String name = "Server Shop";
            GuiButton button = new JRMCoreGuiButtons02(3099, guiWidthOffset, guiHeightOffset+menuImageHeight+2, "$", ref.getGuiID() == guiID ? 1 : 0, Color.GREEN.darker().darker().getRGB());
            buttonList.add(button);
            labelList.add(new JRMCoreLabel(button, ref.getTranslation()));
            //drawDetails(JRMCoreH.cct(name), guiLeft + i * 21, guiTop + ySize + 2 + 1, 20, 20, x, y, fontRendererObj);

            xOffset++;
        }

        for(int i = 0; i < guiReferences.length; i++){
            GuiInfo.ReferenceIDs ref = guiReferences[i];
            if(ref == GuiInfo.ReferenceIDs.DIFFICULTY)
                break;

            boolean isSelected = ref.getGuiID() == this.guiID;
            GuiButton button = new JRMCoreGuiButtons03(ref.getButtonId(), guiWidthOffset + xOffset*21, guiHeightOffset+menuImageHeight+2, "",  (isSelected ? 1 : 0), 8046079,  ref.getIconID());
            this.buttonList.add(button);
            this.labelList.add(new JRMCoreLabel(button, ref.getTranslation()));
            xOffset++;
        }

        if(JRMCoreH.JYC()){
            String buttonName = GuiInfo.translateButtonLocale(GuiInfo.YEARS_C_TRANSLATION);
            GuiButton button = new JRMCoreGuiButtons02(GuiInfo.YEARS_C_ID, guiWidthOffset + xOffset*21, guiHeightOffset+menuImageHeight+2, buttonName.substring(0, 2).toUpperCase(), 0, 8046079);
            this.buttonList.add(button);
            this.labelList.add(new JRMCoreLabel(button, buttonName));
            xOffset++;
        }
        if(JRMCoreH.JFC()){
            String buttonName = GuiInfo.translateButtonLocale(GuiInfo.FAMILY_C_TRANSLATION);
            GuiButton button = new JRMCoreGuiButtons02(GuiInfo.FAMILY_C_ID, guiWidthOffset + xOffset*21, guiHeightOffset+menuImageHeight+2, buttonName.substring(0, 2).toUpperCase(), 0, 8046079);
            this.buttonList.add(button);
            this.labelList.add(new JRMCoreLabel(button, buttonName));
            xOffset++;
        }


    }
    @Override
    public void confirmClicked(boolean result, int id){
        if(id == 0){
            if(result && this.clickedUrl != null){
                try {
                    Desktop.getDesktop().browse(this.clickedUrl);
                } catch (Throwable var3) {
                }
            }
            this.clickedUrl = null;
            mc.displayGuiScreen(this);

        }
    }
}
