package kamkeel.npcdbc.client.gui.hud;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

class WheelSegment extends Gui {

    public static ResourceLocation variant1 = new ResourceLocation(CustomNpcPlusDBC.ID + ":textures/gui/hud/formwheel/GuiWheelVariant1.png");
    public static ResourceLocation variant2 = new ResourceLocation(CustomNpcPlusDBC.ID + ":textures/gui/hud/formwheel/GuiWheelVariant2.png");

    public static double width = 124;
    public static double height = 124;
    public static double f = 124.0 / 744;

    public static int variant = 0;

    public int posX;
    public int posY;
    public int index;
    public boolean selected;

    WheelSegment(int index) {
        this(0, 0, index);

    }

    WheelSegment(int posX, int posY, int index) {
        this.posX = posX;
        this.posY = posY;
        this.index = index;
    }

    public void draw() {
        drawIndexedTexture();
    }


    private void drawIndexedTexture() {

        if (variant == 0)
            Minecraft.getMinecraft().getTextureManager().bindTexture(variant1);
        else
            Minecraft.getMinecraft().getTextureManager().bindTexture(variant2);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(posX - width / 2, posY + (height / 2), (double) this.zLevel + 1, (index) * f, 1);
        tessellator.addVertexWithUV(posX + width / 2, posY + (height / 2), (double) this.zLevel + 1, (index + 1) * f, 1);
        tessellator.addVertexWithUV(posX + width / 2, posY - (height / 2), (double) this.zLevel + 1, (index + 1) * f, 0);
        tessellator.addVertexWithUV(posX - width / 2, posY - (height / 2), (double) this.zLevel + 1, (index) * f, 0);
        tessellator.draw();

    }

}
