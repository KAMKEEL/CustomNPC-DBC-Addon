package kamkeel.npcdbc.client.gui.dbc;

import JinRyuu.JRMCore.JRMCoreGuiButtons00;
import JinRyuu.JRMCore.JRMCoreGuiButtons02;
import JinRyuu.JRMCore.JRMCoreGuiButtons03;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.client.gui.dbc.constants.GuiButtonConstants;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public abstract class AbstractJRMCGui extends GuiScreen {
    private static final ResourceLocation menuTexture = new ResourceLocation("jinryuumodscore:gui.png");
    private final int guiID;
    protected int menuImageWidth = 256;
    protected int menuImageHeight = 159;

    protected int guiWidthOffset;
    protected int guiHeightOffset;

    /**
     * @param guiReplacementID ID of the JRMC Gui this object is replacing
     */
    public AbstractJRMCGui(int guiReplacementID){
        this.guiID = guiReplacementID;
    }

    protected void drawBackground(){
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(menuTexture);
        this.drawTexturedModalRect((this.width-menuImageWidth)/2, (this.height-menuImageHeight)/2,0, 0, menuImageWidth, menuImageHeight);
    }

    @Override
    public void initGui(){
        this.guiWidthOffset = (this.width - menuImageWidth) / 2;
        this.guiHeightOffset = (this.height - menuImageHeight) / 2;

        addNavbarButtons();
    }

    @Override
    public boolean doesGuiPauseGame(){
        return false;
    }

    @Override
    protected void actionPerformed(GuiButton button){
        switch(button.id){
            case 0:
                this.mc.thePlayer.closeScreen();
                return;
            default:
                break;
        }
    }

    protected void addNavbarButtons(){
        //@TODO Add proper navbar generation buttons
        this.guiWidthOffset = (this.width - menuImageWidth) / 2;
        this.guiHeightOffset = (this.height - menuImageHeight) / 2;

        this.buttonList.add(new JRMCoreGuiButtons00(GuiButtonConstants.EXIT, guiWidthOffset + menuImageWidth/2 -150, guiHeightOffset+menuImageHeight/2+65, 20, 20, "X", 0));

        GuiButtonConstants.ReferenceIDs[] guiReferences = GuiButtonConstants.ReferenceIDs.values();
        int i = 0;
        for(; i < guiReferences.length; i++){
            GuiButtonConstants.ReferenceIDs ref = guiReferences[i];
            if(ref == GuiButtonConstants.ReferenceIDs.DIFFICULTY)
                break;

            boolean isSelected = ref.getGuiID() == this.guiID;
            this.buttonList.add(new JRMCoreGuiButtons03(ref.getGuiID(), guiWidthOffset + i*21, guiHeightOffset+menuImageHeight+2, "",  (isSelected ? 1 : 0), 8046079,  ref.getIconID()));
            //@TODO ADD TOOLTIP
        }

        if(JRMCoreH.JYC()){
            //@TODO get name
            this.buttonList.add(new JRMCoreGuiButtons02(11, guiWidthOffset + i*21, guiHeightOffset+menuImageHeight+2, "CA", 0, 8046079));
            //@TODO add tooltip
            i++;
        }
        if(JRMCoreH.JFC()){
            //@TODO get name
            this.buttonList.add(new JRMCoreGuiButtons02(12, guiWidthOffset + i*21, guiHeightOffset+menuImageHeight+2, "FA", 0, 8046079));
            //@TODO add tooltip
            i++;
        }

    }
}
