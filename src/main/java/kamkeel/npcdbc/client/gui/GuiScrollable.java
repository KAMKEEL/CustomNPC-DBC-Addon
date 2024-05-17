package kamkeel.npcdbc.client.gui;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;


public class GuiScrollable extends GuiScreen {

    private GuiScreen parent;
    protected int xPos;
    protected int yPos;

    protected int clipWidth;
    protected int clipHeight;
    protected ScaledResolution scaledResolution;
    protected int scrollY = 0;

    /*
     * @TODO
     *  -Copy GuiControls smoother scrolling
     *  -Draw components nicely
     *  -Make it CNPC compatible
     */

    public GuiScrollable(GuiScreen parent){
        this.parent = parent;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        GL11.glPushMatrix();
        drawDefaultBackground();

        //Enable clipping
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        setClip((width-200)/2, (height-100)/2, 200, 100);
        drawRect((width-200)/2, scrollY, (width-200)/2+100, 100+scrollY, 0xFFFF0000);


        //Disable clipping (VERY IMPORTANT)
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }

    @Override
    public void initGui(){
        scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
    }

    @Override
    public void updateScreen(){

    }

    @Override
    protected void keyTyped(char typedChar, int keyCode){
        if(keyCode == Keyboard.KEY_ESCAPE)
            FMLCommonHandler.instance().showGuiScreen(parent);
    }

    @Override
    public void handleMouseInput(){
        super.handleMouseInput();
        if(Mouse.getEventDWheel() != 0){
            scrollY += Mouse.getEventDWheel() > 0 ? 14 : -14;
            if(scrollY < 0)
                scrollY = 0;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton){

    }

    @Override
    protected void actionPerformed(GuiButton button){

    }

    protected void setClip(){
        setClip(xPos, yPos, clipWidth, clipHeight);
    }

    protected void setClip(int x, int y, int width, int height){
        int scaleFactor = scaledResolution.getScaleFactor();

        //Correct the positions for Screen Space in OpenGL
        x*=scaleFactor;
        y*=scaleFactor;
        width*=scaleFactor;
        height*=scaleFactor;

        //Adjust position to Top-Left origin (OpenGL window/screen space uses bottom-left origin)
        y=mc.displayHeight-y;

        //Set clip
        GL11.glScissor(x, y-height, width, height);
    }



}
