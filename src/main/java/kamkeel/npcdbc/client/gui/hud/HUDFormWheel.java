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
        if(!Keyboard.isKeyDown(KeyHandler.FormWheelKey.getKeyCode())){
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {

        super.drawScreen(mouseX, mouseY, partialTicks);
        int index = 0;
        double width = 124;
        double height = 124-28;
        mc.getTextureManager().bindTexture(resourceLocation);
        GL11.glPushMatrix();
//        GL11.glTranslatef((float) (-width/2), -28.0f - 69f, 0);

//        WheelSegment segment = new WheelSegment(2);


        GL11.glTranslatef((float) this.width /2, (float) this.height /2, 0);
        GL11.glScalef(1.4f, 1.4f, 0);
        for(int i = 0; i < 6; i++) {
            GL11.glPushMatrix();
//            GL11.glTranslatef(0, , 0);


            GL11.glRotatef(i * -60, 0, 0, 1);
            if (i % 3 == 0) {
                GL11.glTranslatef(0, -85f, 0);
            } else {
                GL11.glTranslatef(0, -100f, 0);
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
