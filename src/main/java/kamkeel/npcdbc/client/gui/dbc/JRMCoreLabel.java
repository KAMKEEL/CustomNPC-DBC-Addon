package kamkeel.npcdbc.client.gui.dbc;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.client.ColorMode;
import kamkeel.npcdbc.config.ConfigDBCClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class JRMCoreLabel extends Gui implements HoverableLabel {

    protected static final ResourceLocation background = new ResourceLocation("jinryuumodscore:allw.png");

    protected String displayFormat;
    protected String tooltipFormat;


    protected String display;
    protected String tooltip;
    protected int xPosition;
    protected int yPosition;
    protected int hoverableAreaWidth;
    protected int hoverableAreaHeight;
    protected int tooltipWidth;
    protected int tooltipHeight;

    public JRMCoreLabel(String displayFormat, String tooltipFormat, int x, int y){
        this.displayFormat = displayFormat;
        this.tooltipFormat = tooltipFormat;
        this.xPosition = x;
        this.yPosition = y;
    }

    public JRMCoreLabel(GuiButton button, String name) {
        this.xPosition = button.xPosition;
        this.yPosition = button.yPosition;
        this.hoverableAreaWidth = button.width;
        this.hoverableAreaHeight = button.height;

        this.setTooltip(name);
    }
    public JRMCoreLabel(GuiButton button, String displayFormat, String name) {
        this(button, displayFormat, null, name);
    }
    public JRMCoreLabel(GuiButton button, String displayFormat, String tooltipFormat, String name) {
        this.xPosition = button.xPosition;
        this.yPosition = button.yPosition;
        this.hoverableAreaWidth = button.width;
        this.hoverableAreaHeight = button.height;
        this.displayFormat = displayFormat;
        this.tooltipFormat = tooltipFormat;
        if(this.tooltipFormat != null)
            this.tooltipFormat = tooltipFormat.replaceAll("/n", "\n");
        this.setTooltip(name);
    }

    public JRMCoreLabel(String displayText, String tooltipText, int x, int y, int hoverableAreaWidth, int hoverableAreaHeight){
        setTooltip(tooltipText);
        setDisplay(displayText);
        this.xPosition = x;
        this.yPosition = y;
        this.hoverableAreaWidth = hoverableAreaWidth;
        this.hoverableAreaHeight = hoverableAreaHeight;
    }

    protected JRMCoreLabel() {

    }

    public JRMCoreLabel updateDisplay(Object... arguments){
        setDisplay(String.format(displayFormat, arguments));
        return this;
    }

    public JRMCoreLabel updateTooltip(Object... arguments){
        setTooltip(String.format(tooltipFormat, arguments));
        return this;
    }

    public JRMCoreLabel setTooltip(String text) {
        if(text != null)
            this.tooltip = text.replaceAll("/n", "\n");
        else
            this.tooltip = null;
        this.tooltipWidth = 0;
        this.tooltipHeight = 0;
        return this;
    }

    public JRMCoreLabel setDisplay(String text){
        this.display = text;
        this.hoverableAreaWidth = 0;
        this.hoverableAreaHeight = 0;
        return this;
    }


    /**
     * The draw function for the label
     * @param client
     * @param mouseX
     * @param mouseY
     */
    public void drawLabel(Minecraft client, int mouseX, int mouseY){
        client.fontRenderer.drawString(display, xPosition, yPosition, ColorMode.textColor(), ConfigDBCClient.DarkMode);
    }

    protected boolean isHovered(int mouseX, int mouseY){
        return xPosition <= mouseX && xPosition + hoverableAreaWidth > mouseX && yPosition - 1 <= mouseY && yPosition + hoverableAreaHeight > mouseY;
    }

    @Override
    public void hover(Minecraft client, int mouseX, int mouseY) {
        if(tooltip == null)
            return;

        if(display != null && (hoverableAreaHeight <= 0 || hoverableAreaWidth <= 0)){
            hoverableAreaWidth = client.fontRenderer.getStringWidth(display);
            hoverableAreaHeight = 8;
        }

        if(isHovered(mouseX, mouseY)) {

            client.getTextureManager().bindTexture(background);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.8f);

            if (tooltipWidth <= 0) {

                int maxLineWidth = 0;
                String[] lines = tooltip.split("\n");
                for(String line : lines){
                    int lineWidth = client.fontRenderer.getStringWidth(line);
                    maxLineWidth = Math.max(maxLineWidth, lineWidth);
                }
                tooltipWidth = Math.min(maxLineWidth, 200);
            }


            List<String> toolTipSplit = (List<String>) client.fontRenderer.listFormattedStringToWidth(tooltip, tooltipWidth);

            if (tooltipHeight <= 0) {
                tooltipHeight = toolTipSplit.size() * 10;
            }

            int tooltipY = mouseY + 10;

            this.drawTexturedModalRect(mouseX, tooltipY, 0, 0, tooltipWidth + 10, tooltipHeight + 10);

            int linesWritten = 0;
            for (String text : toolTipSplit) {
                client.fontRenderer.drawString(JRMCoreH.cldgy + text, mouseX + 5, tooltipY + 5 + linesWritten * 10, 0);
                linesWritten++;
            }
        }
    }
}
