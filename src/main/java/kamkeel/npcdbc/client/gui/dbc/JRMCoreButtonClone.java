package kamkeel.npcdbc.client.gui.dbc;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class JRMCoreButtonClone extends GuiButton {
    public static String icons;
    public static float rotation;
    private int col;
    private int type;
    private int resourceID;

    public JRMCoreButtonClone(int par1, int par2, int par3, String par6Str, int type, int col, int resourceID) {
        super(par1, par2, par3, 20, 20, par6Str);
        this.width = 20;
        this.height = 20;
        this.col = col;
        this.type = type;
        this.resourceID = resourceID;
    }

    public void drawButton(Minecraft par1Minecraft, int par2, int par3) {
        if (this.visible) {
            ResourceLocation txx = new ResourceLocation(JRMCoreH.tjjrmc + ":icons3.png");
            par1Minecraft.getTextureManager().bindTexture(txx);
            GL11.glColor4f(0.7F, 0.7F, 0.7F, 1.0F);
            this.field_146123_n = par2 >= this.xPosition && par3 >= this.yPosition && par2 < this.xPosition + this.width && par3 < this.yPosition + this.height;
            int var5 = this.getHoverState(this.field_146123_n);
            int j = this.col == 0 ? 16449280 : this.col;
            float h2 = (float) (j >> 16 & 255) / 255.0F;
            float h3 = (float) (j >> 8 & 255) / 255.0F;
            float h4 = (float) (j & 255) / 255.0F;
            float h1 = 2.0F;
            int var6;
            int r;
            int g;
            float hue;
            float saturation;
            int bonusY;
            if (var5 == 2) {
                var6 = (int) (h2 * 254.0F);
                r = (int) (h3 * 254.0F);
                g = (int) (h4 * 254.0F);
                float[] hsb = Color.RGBtoHSB(var6, r, g, (float[]) null);
                hue = 0.33F;
                saturation = hsb[2];
                bonusY = Color.HSBtoRGB(hue, hue, saturation);
                h2 = (float) (bonusY >> 16 & 255) / 255.0F;
                h3 = (float) (bonusY >> 8 & 255) / 255.0F;
                h4 = (float) (bonusY & 255) / 255.0F;
            }

            GL11.glColor3f(h1 * h2, h1 * h3, h1 * h4);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, var5 * 20 + 80 - var5 * 20, 156 + this.type * 20 + var5 * 20, this.width, this.height);
            this.mouseDragged(par1Minecraft, par2, par3);
            float color = 1.0F;
            GL11.glColor4f(color, color, color, 1.0F);
            txx = new ResourceLocation(icons);
            par1Minecraft.getTextureManager().bindTexture(txx);
            GL11.glPushMatrix();
            GL11.glTranslatef((float) (this.xPosition + 2), (float) (this.yPosition + 2), 0.0F);
            this.func_152125_a(0, 0, 0, 48, 18, 18, 16, 16, 256, 256); // Render the cropped 18x18 texture within the 16x16 area
            GL11.glPopMatrix();
        }
    }

    static {
        icons = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        rotation = 0.0F;
    }
}
