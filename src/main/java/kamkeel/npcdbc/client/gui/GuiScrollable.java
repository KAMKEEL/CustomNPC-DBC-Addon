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
    protected float scrollY = 0;
    protected float nextScrollY = 0;

    /*
     * @TODO
     *  -Copy GuiControls smoother scrolling
     *  -Draw components nicely
     *  -Make it CNPC compatible
     */

    public GuiScrollable(GuiScreen parent){
        this.parent = parent;
    }

    private double lerp(double a, double b, double lambda){
        return a + lambda * (b - a);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        GL11.glPushMatrix();


        scrollY = (float) lerp(scrollY, nextScrollY, partialTicks);
        if(Float.isNaN(scrollY))
            scrollY = 0;

        drawDefaultBackground();
        drawRect(xPos-5, yPos-5, xPos+clipWidth+5, yPos+clipHeight+5, 0xFFD3D3D3);
        drawGradientRect(xPos, yPos, xPos+clipWidth, yPos+clipHeight, -1072689136, -804253680);

        GL11.glTranslatef(xPos, scrollY, 0);
        //Enable clipping
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        setClip();
        drawRect(0, 0, 100, 200, 0xFFFF0000);

        super.drawScreen(mouseX-xPos, (int) (mouseY-scrollY), partialTicks);
        drawString(fontRendererObj, "Test:", 0, 1, 0xFF00FF);

        //Disable clipping (VERY IMPORTANT)
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        GL11.glTranslatef(0, 0, 0);


        GL11.glPopMatrix();
    }

    @Override
    public void initGui(){
        scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        buttonList.add(new GuiButton(0, 0, 9, 50, 20, "Testing"));

        float scrollPerc = 0;
        if((yPos+clipHeight) != 0){
            scrollPerc = nextScrollY / (yPos+clipHeight);
        }

        xPos = (width-200)/2;
        yPos = (height-250)/2;
        clipWidth = 200;
        clipHeight = 250;

        nextScrollY = scrollPerc * (yPos+clipHeight);
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

        nextScrollY -= (float) Mouse.getEventDWheel()/25;
        if(nextScrollY < yPos)
            nextScrollY = yPos;
        if(nextScrollY > yPos+clipHeight-10){
            nextScrollY = yPos+clipHeight-10;
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
