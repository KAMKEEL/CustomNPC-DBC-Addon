package kamkeel.npcdbc.client.gui.dbc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiIcon extends JRMCoreLabel {
    private final ResourceLocation texture;
    public int xPosition;
    public final int yPosition;
    public int textureX;
    public int textureY;
    private final int width;
    private final int height;

    public GuiIcon(ResourceLocation texture, int xPosition, int yPosition, int textureX, int textureY, int width, int height) {

        this.texture = texture;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.textureX = textureX;
        this.textureY = textureY;
        this.width = width;
        this.height = height;
    }


    /**
     * The draw function for the label
     *
     * @param client
     * @param mouseX
     * @param mouseY
     */
    public void drawLabel(Minecraft client, int mouseX, int mouseY) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        client.getTextureManager().bindTexture(texture);
        this.drawTexturedModalRect(xPosition, yPosition, textureX, textureY, width, height);
    }

    public static class Button extends GuiButton {
        private final ResourceLocation texture;

        public Button(int buttonId, int x, int y, int width, int height, ResourceLocation texture) {
            super(buttonId, x, y, width, height, null);
            this.texture = texture;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (!visible)
                return;
            mc.getTextureManager().bindTexture(texture);
            func_152125_a(xPosition, yPosition, 0, 0, 16, 16, width, height, 16, 16);
        }
    }

}
