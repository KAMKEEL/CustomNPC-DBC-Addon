package kamkeel.npcdbc.client.gui.dbc;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public abstract class AbstractJRMCGui extends GuiScreen {
    private static final ResourceLocation menuTexture = new ResourceLocation("jinryuumodscore:gui.png");
    private final int guiID;
    protected int menuImageWidth = 256;
    protected int menuImageHeight = 159;

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
        addNavbarButtons();
    }

    @Override
    public boolean doesGuiPauseGame(){
        return false;
    }

    @Override
    protected void actionPerformed(GuiButton button){

    }

    protected void addNavbarButtons(){
        //@TODO Add proper navbar generation buttons
    }
}
