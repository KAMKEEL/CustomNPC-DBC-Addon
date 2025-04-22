package kamkeel.npcdbc.client.gui.global.form;

import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.request.form.DBCSaveForm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiMenuTopButton;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.SubGuiInterface;
import org.lwjgl.input.Keyboard;

public class GuiNpcFormMenu {

    public final GuiNPCManageForms formsParent;
    public final SubGuiInterface parent;
    private GuiMenuTopButton[] topButtons;
    private int activeMenu;
    private final String originalName;
    private Form form;

    public GuiNpcFormMenu(GuiNPCManageForms formsParent, SubGuiInterface parent, int activeMenu, Form form) {
        this.formsParent = formsParent;
        this.parent = parent;
        this.activeMenu = activeMenu;
        this.form = form;
        this.originalName = form.name;
    }


    public void initGui(int guiLeft, int guiTop, int width) {
        Keyboard.enableRepeatEvents(true);
        GuiMenuTopButton close = new GuiMenuTopButton(-5, guiLeft + width - 22, guiTop - 17, "X");

        GuiMenuTopButton general = new GuiMenuTopButton(-1, guiLeft + 4, guiTop - 17, "display.general");
        GuiMenuTopButton display = new GuiMenuTopButton(-2, general.xPosition + general.getWidth(), guiTop - 17, "menu.display");
        GuiMenuTopButton mastery = new GuiMenuTopButton(-3, display.xPosition + display.getWidth(), guiTop - 17, "display.mastery");
        GuiMenuTopButton stackable = new GuiMenuTopButton(-4, mastery.xPosition + mastery.getWidth(), guiTop - 17, "display.stackable");
        GuiMenuTopButton advanced   = new GuiMenuTopButton(-6, stackable.xPosition + stackable.getWidth(), guiTop - 17, "display.adv");
        GuiMenuTopButton attributes = new GuiMenuTopButton(-7, advanced.xPosition + advanced.getWidth(), guiTop - 17, "display.attri");

        this.topButtons = new GuiMenuTopButton[]{ general, display, mastery, stackable, advanced, attributes, close };
        GuiMenuTopButton[] var12 = this.topButtons;
        int var13 = var12.length;

        for (int var14 = 0; var14 < var13; ++var14) {
            stackable = var12[var14];
            stackable.active = stackable.id == this.activeMenu;
        }
    }

    private void topButtonPressed(GuiMenuTopButton button) {
        if (!button.displayString.equals(this.activeMenu)) {
            NoppesUtil.clickSound();
            int id = button.id;
            if (id == -5) {
                close();
            } else {
                if (id == -1) {
                    formsParent.setSubGui(new SubGuiFormGeneral(formsParent, form));
                } else if (id == -2) {
                    formsParent.setSubGui(new SubGuiFormDisplay(formsParent, form));
                } else if (id == -3) {
                    formsParent.setSubGui(new SubGuiFormMastery(formsParent, form));
                } else if (id == -4) {
                    formsParent.setSubGui(new SubGuiFormStackable(formsParent, form));
                } else if (id == -6) {
                    formsParent.setSubGui(new SubGuiFormAdvanced(formsParent, form));
                } else if (id == -7) {
                    formsParent.setSubGui(new SubGuiFormAttributes(formsParent, form));
                }

                this.activeMenu = id;
            }
        }
    }

    public void close() {
        Keyboard.enableRepeatEvents(false);
        if (this.parent != null) {
            GuiNpcTextField.unfocus();
            ((SubGuiInterface) this.parent).close();
            DBCPacketHandler.Instance.sendToServer(new DBCSaveForm(form.writeToNBT(), this.originalName));
        }
    }

    public void mouseClicked(int i, int j, int k) {
        if (k == 0) {
            Minecraft mc = Minecraft.getMinecraft();
            GuiMenuTopButton[] var5 = this.topButtons;
            int var6 = var5.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                GuiMenuTopButton button = var5[var7];
                if (button.mousePressed(mc, i, j)) {
                    this.topButtonPressed(button);
                }
            }
        }

    }

    public void drawElements(FontRenderer fontRenderer, int i, int j, Minecraft mc, float f) {
        GuiMenuTopButton[] var6 = this.topButtons;
        int var7 = var6.length;

        for (int var8 = 0; var8 < var7; ++var8) {
            GuiMenuTopButton button = var6[var8];
            button.drawButton(mc, i, j);
        }
    }

}
