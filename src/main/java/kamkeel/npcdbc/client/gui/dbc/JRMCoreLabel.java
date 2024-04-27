package kamkeel.npcdbc.client.gui.dbc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class JRMCoreLabel extends GuiLabel {

    protected static final ResourceLocation background = new ResourceLocation("jinryuumodscore:allw.png");

    protected String displayString;
    protected String tooltip;
    protected int xPos;
    protected int yPos;
    protected int width;
    protected int height;

    protected int textWidth;

    public JRMCoreLabel(String text, String tooltipText, int x, int y, int width, int height){
        this.displayString = text;
        this.tooltip = tooltipText.replaceAll("/n", "\n");
        this.xPos = x;
        this.yPos = y;
        this.width = width;
        this.height = height;
    }

    public JRMCoreLabel(String text, String tooltipText, int x, int y, int width){
        this(text, tooltipText, x, y, width, -1);
    }

    public JRMCoreLabel(String text, String tooltipText, int x, int y){
        this(text, tooltipText, x, y, -1, -1);
    }

    /**
     * The draw function for the button
     * @param client
     * @param mouseX
     * @param mouseY
     */
    @Override
    public void func_146159_a(Minecraft client, int mouseX, int mouseY){
        textWidth = client.fontRenderer.getStringWidth(displayString);
        client.fontRenderer.drawString(displayString, xPos, yPos, 0);
        if(isHovered(mouseX, mouseY)){
            client.getTextureManager().bindTexture(background);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.8f);

            int toolTipWidth = width;
            int toolTipHeight = height;

            if(toolTipWidth < 0){
                toolTipWidth = textWidth;
            }

            List<String> toolTipSplit = (List<String>) client.fontRenderer.listFormattedStringToWidth(tooltip, toolTipWidth);

            if(toolTipHeight < 0){
                toolTipHeight = toolTipSplit.size()*10;
            }

            int tooltipX = mouseX;
            int tooltipY = mouseY+10;

            this.drawTexturedModalRect(tooltipX, tooltipY, 0, 0, toolTipWidth+10, toolTipHeight+10);
//            client.fontRenderer.drawString("\u00a74Inside text", mouseX+5, mouseY+15, 0);

            int linesWritten = 0;
            for(String text : toolTipSplit){
                client.fontRenderer.drawString(text, tooltipX+5, tooltipY+5 + linesWritten*10, 0);
                linesWritten++;
            }

        }
    }

    protected boolean isHovered(int mouseX, int mouseY){
        return xPos < mouseX && xPos+textWidth > mouseX && yPos-3 < mouseY && yPos + 10 > mouseY;
    }
}
