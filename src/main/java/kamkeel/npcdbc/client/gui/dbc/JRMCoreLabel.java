package kamkeel.npcdbc.client.gui.dbc;

import JinRyuu.JRMCore.JRMCoreH;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class JRMCoreLabel extends GuiLabel {

    protected static final ResourceLocation background = new ResourceLocation("jinryuumodscore:allw.png");

    protected String displayString;
    protected String tooltip;
    protected int xPosition;
    protected int yPosition;
    protected int hoverableAreaWidth;
    protected int hoverableAreaHeight;
    protected int tooltipWidth;
    protected int tooltipHeight;

    public JRMCoreLabel(String text, String tooltipText, int x, int y, int hoverableAreaWidth, int hoverableAreaHeight, int tooltipWidth, int tooltipHeight){
        this.displayString = text;
        if(tooltip != null)
            this.tooltip = tooltipText.replaceAll("/n", "\n");
        this.xPosition = x;
        this.yPosition = y;
        this.hoverableAreaWidth = hoverableAreaWidth;
        this.hoverableAreaHeight = hoverableAreaHeight;
        this.tooltipWidth = tooltipWidth;
        this.tooltipHeight = tooltipHeight;
    }

    public JRMCoreLabel(String text, String tooltipText, int x, int y, int tooltipWidth){
        this(text, tooltipText, x, y, -1, -1, tooltipWidth, -1);
    }

    public JRMCoreLabel(String text, String tooltipText, int x, int y){
        this(text, tooltipText, x, y, -1, -1, 200, -1);
    }

    public JRMCoreLabel(GuiButton button, String tooltipText, int tooltipWidth){
        this(null, tooltipText, button.xPosition, button.yPosition, button.width, button.height, tooltipWidth, -1);
    }
    public JRMCoreLabel(GuiButton button, String tooltipText){
        this(null, tooltipText, button.xPosition, button.yPosition, button.width, button.height, -1, -1);
    }

    /**
     * The draw function for the button
     * @param client
     * @param mouseX
     * @param mouseY
     */
    @Override
    public void func_146159_a(Minecraft client, int mouseX, int mouseY){
        if(displayString != null && (hoverableAreaHeight < 0 || hoverableAreaWidth < 0)){
            hoverableAreaWidth = client.fontRenderer.getStringWidth(displayString);
            hoverableAreaHeight = 10;
        }


        client.fontRenderer.drawString(displayString, xPosition, yPosition, 0);
        if(tooltip == null)
            return;

        if(isHovered(mouseX, mouseY)){
            client.getTextureManager().bindTexture(background);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.8f);

            if(tooltipWidth < 0){
                if(tooltip.contains("\n"))
                    tooltipWidth = 100;
                else
                    tooltipWidth = client.fontRenderer.getStringWidth(tooltip);
            }

            List<String> toolTipSplit = (List<String>) client.fontRenderer.listFormattedStringToWidth(tooltip, tooltipWidth);

            if(tooltipHeight < 0){
                tooltipHeight = toolTipSplit.size()*10;
            }

            int tooltipX = mouseX;
            int tooltipY = mouseY+10;

            this.drawTexturedModalRect(tooltipX, tooltipY, 0, 0, tooltipWidth+10, tooltipHeight+10);
//            client.fontRenderer.drawString("\u00a74Inside text", mouseX+5, mouseY+15, 0);

            int linesWritten = 0;
            for(String text : toolTipSplit){
                client.fontRenderer.drawString( JRMCoreH.cldgy+text, tooltipX+5, tooltipY+5 + linesWritten*10, 0);
                linesWritten++;
            }

        }
    }

    protected boolean isHovered(int mouseX, int mouseY){
        return xPosition < mouseX && xPosition + hoverableAreaWidth > mouseX && yPosition -3 < mouseY && yPosition + hoverableAreaHeight > mouseY;
    }
}
