package kamkeel.npcdbc.client.gui.hud;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.config.ConfigDBCClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public abstract class WheelSegment extends Gui {

    private static final int HOVER_TIME = 200;
    public static ResourceLocation variant1 = new ResourceLocation(CustomNpcPlusDBC.ID + ":textures/gui/hud/formwheel/GuiWheelVariant1.png");
    public static ResourceLocation variant2 = new ResourceLocation(CustomNpcPlusDBC.ID + ":textures/gui/hud/formwheel/GuiWheelVariant2.png");

    public static Color HOVERED = new Color(0xADD8E6, 0.65f);
    public static Color NOT_HOVERED = new Color(0xFFFFFF, 0.35f);

    public static double width = 124;
    public static double height = 124;
    public static double f = 124.0 / 744;

    public static int variant = 0;

    public Color currentColor;

    public int posX;
    public int posY;
    public int index;
    public boolean selected;

    public boolean isHovered = false;
    public long startHoverTime = 0;
    public long stopHoverTime = 0;
    public float hoverScale = 0;


    public double easeInSine(float x) {
        return x < 0.5
            ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2
            : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2;
    }

    public WheelSegment(int index) {
        this(0, 0, index);

    }

    public WheelSegment(int posX, int posY, int index) {
        this.posX = posX;
        this.posY = posY;
        this.index = index;
    }

    public void setHoveredState(boolean newHoverState) {

        if (!isHovered && newHoverState) {
            startHoverTime = (long) (Minecraft.getSystemTime() - (Math.max(hoverScale - 1, 0)) * HOVER_TIME);
        }
        if (isHovered && !newHoverState) {
            stopHoverTime = (long) (Minecraft.getSystemTime() - (1 - hoverScale) * HOVER_TIME);
        }
        isHovered = newHoverState;
    }

    public float getSegmentScale() {
        float updateTime;
        if (isHovered) {
            updateTime = (float) (Minecraft.getSystemTime() - startHoverTime) / HOVER_TIME;
            updateTime = Math.min(updateTime, 1);
            hoverScale = (float) easeInSine(updateTime);
        } else {
            updateTime = (float) (Minecraft.getSystemTime() - stopHoverTime) / HOVER_TIME;
            updateTime = Math.min(updateTime, 1);
            hoverScale = (float) easeInSine(1 - updateTime);
        }

        hoverScale = Math.min(1, Math.max(hoverScale, 0));

        return hoverScale;
    }

    public void draw(FontRenderer fontRenderer) {
        currentColor = Color.lerpRGBA(NOT_HOVERED, HOVERED, hoverScale);
        currentColor.glColor();
        drawIndexedTexture();
        drawWheelItem(fontRenderer);
    }

    protected abstract void drawWheelItem(FontRenderer fontRenderer);


    private void drawIndexedTexture() {

        if (!ConfigDBCClient.AlteranteSelectionWheelTexture)
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
