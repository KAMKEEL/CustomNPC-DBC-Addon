package kamkeel.npcdbc.client.gui.dbc;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.client.gui.component.GuiRenderPlayer;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.player.SaveFormCustomization;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.SubGuiColorSelector;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.ISubGuiListener;
import noppes.npcs.client.gui.util.SubGuiInterface;

public class GuiFormCustomizer extends AbstractJRMCGui implements ISubGuiListener {
    private static final String[] COLOR_TYPES = new String[] {
        "bodycm", "bodyc1", "bodyc2", "bodyc3",
        "fur", "hair", "eye"
    };
    private static final String[] COLOR_NAMES = new String[] {
        "model.body", "display.bodyc1",
        "display.bodyc2", "display.bodyc3",
        "display.bodyFur", "display.hair", "display.eye"
    };

    private final int dbcRace;
    private final Form form;
    private final FormDisplay.BodyColor colors;

    private GuiRenderPlayer playerRenderer;
    private int lastClickedID;

    public GuiFormCustomizer(Form currentForm) {
        super(-1);
        this.form = currentForm;
        DBCData dataClient = DBCData.getClient();
        this.colors = dataClient.currentCustomizedColors;
        this.dbcRace = dataClient.Race;
    }
    @Override
    public void initGui() {
        this.addDefaultButtons = false;
        super.initGui();

        addCloseButton();

        this.playerRenderer = new GuiRenderPlayer(this, this.guiWidthOffset+4, this.guiHeightOffset+4, menuImageWidth/2 - 6, menuImageHeight - 8, colors);
        this.playerRenderer.customFormID = form.id;


        JRMCoreLabel formLabel = new JRMCoreLabel("%1s's colors", null, 0, this.guiHeightOffset-12);
        formLabel.updateDisplay(form.menuName);
        formLabel.xPosition = (this.guiWidthOffset+4) + (menuImageWidth -6 - this.fontRendererObj.getStringWidth(formLabel.display)) / 2;
        this.hoverableStaticLabels.add(formLabel);

        int y = guiHeightOffset+12;
        int x = (this.guiWidthOffset + 4 + menuImageWidth/2) - 5;
        for (int i = 0; i < COLOR_TYPES.length; i++) {
            if (FormDisplay.BodyColor.canBeCustomized(COLOR_TYPES[i], dbcRace, form)) {
                JRMCoreLabel label = new JRMCoreLabel(null, null, x, y);
                label.setDisplay(JRMCoreH.trl(COLOR_NAMES[i]));
                hoverableStaticLabels.add(label);

                int color = getColor(COLOR_TYPES[i]);
                GuiNpcButton removeButton = new GuiNpcButton(200 + i, guiWidthOffset + menuImageWidth - 5 - 20, y - 5, 20, 20, "X");
                this.buttonList.add(removeButton);
                GuiNpcButton button = new GuiNpcButton(100 + i, removeButton.xPosition - 55, y - 5, 55, 20, color != -1 ? "#"+Integer.toString(color, 16) : "NONE");
                button.packedFGColour = color;
                this.buttonList.add(button);
                y += 22;
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        int id = button.id;

        if (id >= 200) {
            int action = id - 200;

            colors.setColor(COLOR_TYPES[action], -1);
            initGui();
            return;
        }
        if (id >= 100) {
            int action = id - 100;
            lastClickedID = action;
            openSubGui(new SubGuiColorSelector(getColor(COLOR_TYPES[action])) {
                @Override
                public void close() {
                    if (this.parent instanceof ISubGuiListener) {
                        ((ISubGuiListener)this.parent).subGuiClosed(this);
                    }
                }
            });
            initGui();
            return;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.playerRenderer.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        save();
    }

    private void save() {
        PacketHandler.Instance.sendToServer(new SaveFormCustomization(form, colors).generatePacket());
    }

    @Override
    public void subGuiClosed(SubGuiInterface subGuiInterface) {
        if (subGuiInterface instanceof SubGuiColorSelector) {
            handleColorSelection((SubGuiColorSelector) subGuiInterface, lastClickedID);
        }

        NoppesUtil.openGUI(mc.thePlayer, this);
        lastClickedID = -1;
    }

    private void handleColorSelection(SubGuiColorSelector subGuiInterface, int colorID) {
        colors.setColor(COLOR_TYPES[colorID], subGuiInterface.color);
    }

    public void openSubGui(SubGuiInterface subGuiInterface) {
        subGuiInterface.parent = this;
        subGuiInterface.setWorldAndResolution(mc, width, height);
        mc.currentScreen = subGuiInterface;
    }
    public int getColor(String type) {
        return colors.getProperColor(form.display, type);
    }
}
