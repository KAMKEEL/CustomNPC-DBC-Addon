package kamkeel.npcdbc.client.gui.hud;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.KeyHandler;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class HUDFormWheel extends GuiScreen {

    List<Gui> guiList = new ArrayList<>();
    ResourceLocation resourceLocation = new ResourceLocation(CustomNpcPlusDBC.ID + ":/textures/gui/hud/formwheel/GuiWheel.png");

    @Override
    public void initGui() {

    }

    @Override
    public void updateScreen() {
//        if(!Keyboard.isKeyDown(KeyHandler.FormWheelKey.getKeyCode())){
//            mc.displayGuiScreen(null);
//        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {

        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawGradientRect(0, 0, this.width, this.height, 0x33000000, 0x33000000);
        int index = 0;
        double width = 124;
        double height = 124-28;
        GL11.glPushMatrix();
//        GL11.glTranslatef((float) (-width/2), -28.0f - 69f, 0);

//        WheelSegment segment = new WheelSegment(2);

        final float HALF_WIDTH = (float) this.width /2;
        final float HALF_HEIGHT = (float) this.height /2;

        final float deltaX = HALF_WIDTH - mouseX;
        final float deltaY = HALF_HEIGHT - mouseY;

        final float radians = (float) Math.atan2(deltaY, deltaX);
        final float degree = Math.round(radians * (180 / Math.PI));

        index = (int) ((degree-180) / -60)-1;
        if(index == -1)
            index = 5;



        mc.getTextureManager().bindTexture(resourceLocation);
        GL11.glTranslatef(HALF_WIDTH, HALF_HEIGHT, 0);
        GL11.glScalef(1.4f, 1.4f, 0);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        for(int i = 0; i < 6; i++) {
            GL11.glPushMatrix();
//            GL11.glTranslatef(0, , 0);


            GL11.glRotatef(i * -60, 0, 0, 1);
            if(i == index){
                GL11.glScalef(1.1f, 1.1f, 0);
                GL11.glColor4f(173f/255, 216f/255, 230f/255, 0.9f);
            }else{
                GL11.glColor4f(1, 1, 1, 0.6f);
            }
            if (i % 3 == 0) {
                GL11.glTranslatef(0, -80f, 0);
            } else {
                GL11.glTranslatef(0, -95f, 0);
            }


            GL11.glRotatef(i * 60, 0, 0, 1);
            if (i == 0 || i == 3){

            }else if(i == 1 || i == 2){
                GL11.glTranslatef(10, 0, 0);
            }else{
                GL11.glTranslatef(-10, 0, 0);
            }
            new WheelSegment(i).draw();
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
        drawCenteredString(fontRendererObj, degree+"", mouseX, mouseY, 0xFFFFFFFF);
        GL11.glDisable(GL11.GL_BLEND);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode)
    {
        super.keyTyped(typedChar, keyCode);
    }

    /**
     * Called when the mouse is clicked.
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {

    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

}
