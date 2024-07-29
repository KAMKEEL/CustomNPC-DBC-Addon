package kamkeel.npcdbc.client.gui.hud;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

class WheelSegment extends Gui {

    static ResourceLocation segments = new ResourceLocation(CustomNpcPlusDBC.ID + ":textures/gui/hud/formwheel/GuiWheel.png");

    static double width = 124;
    static double height = 124;
    static double f = 124.0 / 744;

    int posX;
    int posY;
    int index;

    WheelSegment(int index){
        this(0, 0, index);
    }

    WheelSegment(int posX, int posY, int index){
        this.posX = posX;
        this.posY = posY;
        this.index = index;
    }

    public void draw(){
        drawIndexedTexture();
    }


    private void drawIndexedTexture(){

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(posX-width/2, posY+(height/2), (double)this.zLevel, (index)*f, 1);
        tessellator.addVertexWithUV(posX+width/2, posY+(height/2), (double)this.zLevel, (index+1)*f, 1);
        tessellator.addVertexWithUV(posX+width/2, posY-(height/2), (double)this.zLevel, (index+1)*f, 0);
        tessellator.addVertexWithUV(posX-width/2, posY-(height/2), (double)this.zLevel, (index)*f, 0);
        tessellator.draw();

    }

}
