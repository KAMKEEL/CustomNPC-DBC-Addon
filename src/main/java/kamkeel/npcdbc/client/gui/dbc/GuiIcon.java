package kamkeel.npcdbc.client.gui.dbc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiIcon extends GuiLabel {
    private final ResourceLocation texture;
    public int xPosition;
    public final int yPosition;
    public int textureX;
    public int textureY;
    private final int width;
    private final int height;

    public GuiIcon(ResourceLocation texture, int xPosition, int yPosition, int textureX, int textureY, int width, int height){

        this.texture = texture;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.textureX = textureX;
        this.textureY = textureY;
        this.width = width;
        this.height = height;
    }


    /**
     * The draw function for the button
     * @param client
     * @param mouseX
     * @param mouseY
     */
    @Override
    public void func_146159_a(Minecraft client, int mouseX, int mouseY){
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        client.getTextureManager().bindTexture(texture);
        this.drawTexturedModalRect(xPosition, yPosition, textureX, textureY, width, height);
    }

}
