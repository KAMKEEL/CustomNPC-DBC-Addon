package kamkeel.npcdbc.client.gui.component;

import kamkeel.npcdbc.client.gui.GuiDBCDisplayColor;
import kamkeel.npcdbc.client.gui.GuiModelDBC;
import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.data.npc.KiWeaponData;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityCustomNpc;
import org.lwjgl.opengl.GL11;

public class SubGuiKiWeapon extends SubGuiInterface implements ITextfieldListener {
    private DBCDisplay display;
    public KiWeaponData left, right;
    public GuiModelDBC parent;

    public SubGuiKiWeapon(GuiModelDBC parent, DBCDisplay display) {
        this.npc = display.npc;
        this.parent = parent;
        this.display = display;
        left = display.kiWeaponLeft;
        right = display.kiWeaponRight;
        xSize = 200;
        ySize = 276;
        closeOnEsc = true;
        drawDefaultBackground = false;
    }

    public void initGui() {
        super.initGui();
        int y = guiTop + 4;

        y += 3;
        addLabel(new GuiNpcLabel(1, "display.leftArm", guiLeft + 5, y + 5));
        getLabel(1).color = 0xffffff;
        addButton(new GuiButtonBiDirectional(1, guiLeft + 80, y, 110, 20, new String[]{"gui.no", "Ki Blade", "Ki Scythe"}, left.weaponType));
        if (left.isEnabled()) {
            y += 22;
            addLabel(new GuiNpcLabel(2, "display.color", guiLeft + 5, y + 5));
            getLabel(2).color = 0xffffff;
            addButton(new GuiNpcButton(2, guiLeft + 88, y, 74, 20, Color.getColor(left.color.color, left.color.alpha)));
            getButton(2).packedFGColour = left.color.color;

            addButton(new GuiNpcButton(3, guiLeft + 162, y, 20, 20, "X"));
            getButton(3).enabled = left.color.color != -1 || left.color.alpha != 0.6f;

            y += 25;
            addLabel(new GuiNpcLabel(4, "display.scalexyz", guiLeft + 5, y + 5));
            getLabel(4).color = 0xffffff;
            int x = 0;
            addTextField(new GuiNpcTextField(4, this, x = guiLeft + 85, y, 28, 18, left.scaleX + ""));
            getTextField(4).setMaxStringLength(6);
            getTextField(4).floatsOnly = true;
            getTextField(4).setMinMaxDefaultFloat(0, 10, 1);

            addTextField(new GuiNpcTextField(401, this, x += 31, y, 28, 18, left.scaleY + ""));
            getTextField(401).setMaxStringLength(6);
            getTextField(401).floatsOnly = true;
            getTextField(401).setMinMaxDefaultFloat(0, 10, 1);

            addTextField(new GuiNpcTextField(402, this, x += 31, y, 28, 18, left.scaleZ + ""));
            getTextField(402).setMaxStringLength(6);
            getTextField(402).floatsOnly = true;
            getTextField(402).setMinMaxDefaultFloat(0, 10, 1);

            addButton(new GuiNpcButton(4, guiLeft + 176, y - 1, 20, 20, "X"));
            getButton(4).enabled = left.scaleX != 1 || left.scaleY != 1 || left.scaleZ != 1;

            y += 22;
            addLabel(new GuiNpcLabel(5, "display.offsetxyz", guiLeft + 5, y + 5));
            getLabel(5).color = 0xffffff;
            addTextField(new GuiNpcTextField(5, this, x = guiLeft + 85, y, 28, 18, left.offsetX + ""));
            getTextField(5).setMaxStringLength(6);
            getTextField(5).floatsOnly = true;
            getTextField(5).setMinMaxDefaultFloat(-10, 10, 0);

            addTextField(new GuiNpcTextField(501, this, x += 31, y, 28, 18, left.offsetY + ""));
            getTextField(501).setMaxStringLength(6);
            getTextField(501).floatsOnly = true;
            getTextField(501).setMinMaxDefaultFloat(-10, 10, 0);

            addTextField(new GuiNpcTextField(502, this, x += 31, y, 28, 18, left.offsetZ + ""));
            getTextField(502).setMaxStringLength(6);
            getTextField(502).floatsOnly = true;
            getTextField(502).setMinMaxDefaultFloat(-10, 10, 0);

            addButton(new GuiNpcButton(5, guiLeft + 176, y - 1, 20, 20, "X"));
            getButton(5).enabled = left.offsetX != 0 || left.offsetY != 0 || left.offsetZ != 0;


        }

        y += 50;
        addLabel(new GuiNpcLabel(11, "display.rightArm", guiLeft + 5, y + 5));
        getLabel(11).color = 0xffffff;
        addButton(new GuiButtonBiDirectional(11, guiLeft + 80, y, 110, 20, new String[]{"gui.no", "Ki Blade", "Ki Scythe"}, right.weaponType));
        if (right.isEnabled()) {
            y += 22;
            addLabel(new GuiNpcLabel(12, "display.color", guiLeft + 5, y + 5));
            getLabel(12).color = 0xffffff;
            addButton(new GuiNpcButton(12, guiLeft + 88, y, 74, 20, Color.getColor(right.color.color, right.color.alpha)));
            getButton(12).packedFGColour = right.color.color;

            addButton(new GuiNpcButton(13, guiLeft + 162, y, 20, 20, "X"));
            getButton(13).enabled = right.color.color != -1 || right.color.alpha != 0.6f;

            y += 25;
            addLabel(new GuiNpcLabel(14, "display.scalexyz", guiLeft + 5, y + 5));
            getLabel(14).color = 0xffffff;
            int x = 0;
            addTextField(new GuiNpcTextField(14, this, x = guiLeft + 85, y, 28, 18, right.scaleX + ""));
            getTextField(14).setMaxStringLength(6);
            getTextField(14).floatsOnly = true;
            getTextField(14).setMinMaxDefaultFloat(0, 10, 1);

            addTextField(new GuiNpcTextField(1401, this, x += 31, y, 28, 18, right.scaleY + ""));
            getTextField(1401).setMaxStringLength(6);
            getTextField(1401).floatsOnly = true;
            getTextField(1401).setMinMaxDefaultFloat(0, 10, 1);

            addTextField(new GuiNpcTextField(1402, this, x += 31, y, 28, 18, right.scaleZ + ""));
            getTextField(1402).setMaxStringLength(6);
            getTextField(1402).floatsOnly = true;
            getTextField(1402).setMinMaxDefaultFloat(0, 10, 1);

            addButton(new GuiNpcButton(14, guiLeft + 176, y - 1, 20, 20, "X"));
            getButton(14).enabled = right.scaleX != 1 || right.scaleY != 1 || right.scaleZ != 1;

            y += 22;
            addLabel(new GuiNpcLabel(15, "display.offsetxyz", guiLeft + 5, y + 5));
            getLabel(15).color = 0xffffff;
            addTextField(new GuiNpcTextField(15, this, x = guiLeft + 85, y, 28, 18, right.offsetX + ""));
            getTextField(15).setMaxStringLength(6);
            getTextField(15).floatsOnly = true;
            getTextField(15).setMinMaxDefaultFloat(-10, 10, 0);

            addTextField(new GuiNpcTextField(1501, this, x += 31, y, 28, 18, right.offsetY + ""));
            getTextField(1501).setMaxStringLength(6);
            getTextField(1501).floatsOnly = true;
            getTextField(1501).setMinMaxDefaultFloat(-10, 10, 0);

            addTextField(new GuiNpcTextField(1502, this, x += 31, y, 28, 18, right.offsetZ + ""));
            getTextField(1502).setMaxStringLength(6);
            getTextField(1502).floatsOnly = true;
            getTextField(1502).setMinMaxDefaultFloat(-10, 10, 0);

            addButton(new GuiNpcButton(15, guiLeft + 176, y - 1, 20, 20, "X"));
            getButton(15).enabled = right.offsetX != 0 || right.offsetY != 0 || right.offsetZ != 0;


        }


        //    addButton(new GuiNpcButton(66, guiLeft + xSize - 24, guiTop + 3, 20, 20, "X"));
    }


    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (button.id == 1) {
            left.weaponType = (byte) button.getValue();
            initGui();
        } else if (button.id == 2) {
            setSubGui(new GuiDBCDisplayColor(parent, null, display, (EntityCustomNpc) npc, 7, button.id).hasAlphaSlider(left.color.alpha));

        } else if (button.id == 3) {
            left.color.color = -1;
            left.color.alpha = 0.6f;
            initGui();
        } else if (button.id == 4) {
            left.scaleX = left.scaleY = left.scaleZ = 1;
            initGui();
        } else if (button.id == 5) {
            left.offsetX = left.offsetY = left.offsetZ = 0;
            initGui();
        }


        if (button.id == 11) {
            right.weaponType = (byte) button.getValue();
            initGui();
        } else if (button.id == 12) {
            setSubGui(new GuiDBCDisplayColor(parent, null, display, (EntityCustomNpc) npc, 8, button.id).hasAlphaSlider(right.color.alpha));
        } else if (button.id == 13) {
            right.color.color = -1;
            right.color.alpha = 0.6f;
            initGui();
        } else if (button.id == 14) {
            right.scaleX = right.scaleY = right.scaleZ = 1;
            initGui();
        } else if (button.id == 15) {
            right.offsetX = right.offsetY = right.offsetZ = 0;
            initGui();
        }

        if (button.id == 66) {
            close();
        }
    }


    @Override
    public void unFocused(GuiNpcTextField textfield) {
        if (textfield.id == 4) {
            left.scaleX = textfield.getFloat();
            getButton(4).enabled = left.scaleX != 1 || left.scaleY != 1 || left.scaleZ != 1;
        }
        if (textfield.id == 401) {
            left.scaleY = textfield.getFloat();
            getButton(4).enabled = left.scaleX != 1 || left.scaleY != 1 || left.scaleZ != 1;
        }
        if (textfield.id == 402) {
            left.scaleZ = textfield.getFloat();
            getButton(4).enabled = left.scaleX != 1 || left.scaleY != 1 || left.scaleZ != 1;
        }
        if (textfield.id == 5) {
            left.offsetX = textfield.getFloat();
            getButton(5).enabled = left.offsetX != 0 || left.offsetY != 0 || left.offsetZ != 0;
        }
        if (textfield.id == 501) {
            left.offsetY = textfield.getFloat();
            getButton(5).enabled = left.offsetX != 0 || left.offsetY != 0 || left.offsetZ != 0;
        }
        if (textfield.id == 502) {
            left.offsetZ = textfield.getFloat();
            getButton(5).enabled = left.offsetX != 0 || left.offsetY != 0 || left.offsetZ != 0;
        }

        if (textfield.id == 14) {
            right.scaleX = textfield.getFloat();
            getButton(14).enabled = right.scaleX != 1 || right.scaleY != 1 || right.scaleZ != 1;
        }
        if (textfield.id == 1401) {
            right.scaleY = textfield.getFloat();
            getButton(14).enabled = right.scaleX != 1 || right.scaleY != 1 || right.scaleZ != 1;
        }
        if (textfield.id == 1402) {
            right.scaleZ = textfield.getFloat();
            getButton(14).enabled = right.scaleX != 1 || right.scaleY != 1 || right.scaleZ != 1;
        }
        if (textfield.id == 15) {
            right.offsetX = textfield.getFloat();
            getButton(15).enabled = right.offsetX != 0 || right.offsetY != 0 || right.offsetZ != 0;
        }
        if (textfield.id == 1501) {
            right.offsetY = textfield.getFloat();
            getButton(15).enabled = right.offsetX != 0 || right.offsetY != 0 || right.offsetZ != 0;
        }
        if (textfield.id == 1502) {
            right.offsetZ = textfield.getFloat();
            getButton(15).enabled = right.offsetX != 0 || right.offsetY != 0 || right.offsetZ != 0;
        }
    }

    public void drawScreen(int i, int j, float f) {
        drawGradientRect(guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, 0xf1101010, 0xf1101010);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        super.drawScreen(i, j, f);
    }

}
