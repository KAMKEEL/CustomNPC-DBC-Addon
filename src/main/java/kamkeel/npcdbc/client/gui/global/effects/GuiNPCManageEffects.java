package kamkeel.npcdbc.client.gui.global.effects;

import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.statuseffect.custom.CustomEffect;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.get.effect.DBCGetEffect;
import kamkeel.npcdbc.network.packets.request.effect.DBCRemoveEffect;
import kamkeel.npcdbc.network.packets.player.effect.DBCRequestEffect;
import kamkeel.npcdbc.network.packets.request.effect.DBCSaveEffect;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class GuiNPCManageEffects extends GuiNPCInterface2 implements ICustomScrollListener, IScrollData, IGuiData, ISubGuiListener, GuiYesNoCallback {
    public GuiCustomScroll scrollEffects;
    public HashMap<String, Integer> data = new HashMap<>();
    public CustomEffect effect = new CustomEffect();
    public String selected = null;
    public String search = "";
    public String originalName = "";
    boolean setNormalSound = true;

    public GuiNPCManageEffects(EntityNPCInterface npc) {
        super(npc);

        PacketHandler.Instance.sendToServer(new DBCRequestEffect(-1));
    }

    public void initGui() {
        super.initGui();
        addButton(new GuiNpcButton(0, guiLeft + 368, guiTop + 8, 45, 20, "gui.add"));

        addButton(new GuiNpcButton(1, guiLeft + 368, guiTop + 32, 45, 20, "gui.remove"));
        getButton(1).enabled = effect != null && effect.id != -1;

        addButton(new GuiNpcButton(2, guiLeft + 368, guiTop + 56, 45, 20, "gui.clone"));
        getButton(2).enabled = effect != null && effect.id != -1;

        addButton(new GuiNpcButton(3, guiLeft + 368, guiTop + 80, 45, 20, "gui.edit"));
        getButton(3).enabled = effect != null && effect.id != -1;

        if (scrollEffects == null) {
            scrollEffects = new GuiCustomScroll(this, 0, 0);
            scrollEffects.setSize(143, 185);
        }
        scrollEffects.guiLeft = guiLeft + 220;
        scrollEffects.guiTop = guiTop + 4;
        addScroll(scrollEffects);
        scrollEffects.setList(getSearchList());

        addTextField(new GuiNpcTextField(55, this, fontRendererObj, guiLeft + 220, guiTop + 4 + 3 + 185, 143, 20, search));
        if (effect != null && effect.id != -1) {
            addLabel(new GuiNpcLabel(10, "ID", guiLeft + 368, guiTop + 4 + 3 + 185));
            addLabel(new GuiNpcLabel(11, effect.id + "", guiLeft + 368, guiTop + 4 + 3 + 195));
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (button.id == 0) {
            save();
            String name = "New";
            while (data.containsKey(name))
                name += "_";
            CustomEffect effect = new CustomEffect(-1, name);
            PacketHandler.Instance.sendToServer(new DBCSaveEffect(effect.writeToNBT(false), effect.id, ""));
        }

        if (button.id == 1) {
            if (data.containsKey(scrollEffects.getSelected())) {
                GuiYesNo guiyesno = new GuiYesNo(this, scrollEffects.getSelected(), StatCollector.translateToLocal("gui.delete"), 1);
                displayGuiScreen(guiyesno);
            }
        }
        if (button.id == 2) {
            CustomEffect effect = this.effect.clone();
            while (data.containsKey(effect.name))
                effect.name += "_";
            PacketHandler.Instance.sendToServer(new DBCSaveEffect(effect.writeToNBT(false), effect.id, ""));
        }
        if (button.id == 3) {
            if (data.containsKey(scrollEffects.getSelected()) && effect != null && effect.id >= Effects.CUSTOM_EFFECT) {
                setSubGui(new SubGuiEffectGeneral(this, effect));
            }
        }


    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        this.effect = new CustomEffect();
        effect.readFromNBT(compound, false);
        setSelected(effect.name);

        if (effect.id != -1) {
            StatusEffectController.getInstance().customEffects.replace(effect.id, effect);
        }
        initGui();
    }



    @Override
    public void drawScreen(int i, int j, float f) {
        super.drawScreen(i, j, f);
    }

    @Override
    public void drawBackground() {
        super.drawBackground();
        renderScreen();
    }

    private void renderScreen() {
        drawGradientRect(guiLeft + 5, guiTop + 4, guiLeft + 218, guiTop + 24, 0xC0101010, 0xC0101010);
        drawHorizontalLine(guiLeft + 5, guiLeft + 218, guiTop + 25, 0xFF000000 + CustomNpcResourceListener.DefaultTextColor);
        drawGradientRect(guiLeft + 5, guiTop + 27, guiLeft + 218, guiTop + ySize + 9, 0xA0101010, 0xA0101010);

        if (effect != null && effect.id != -1) {
            String drawString = effect.getMenuName();
            int textWidth = getStringWidthWithoutColor(drawString);
            int centerX = guiLeft + 5 + ((218 - 10 - textWidth) / 2); // Adjusted centerX calculation
            fontRendererObj.drawString(drawString, centerX, guiTop + 10, CustomNpcResourceListener.DefaultTextColor, true);
            int y = guiTop + 18;
        }
    }


    @Override
    public void keyTyped(char c, int i) {
        super.keyTyped(c, i);

        if (getTextField(55) != null) {
            if (getTextField(55).isFocused()) {
                if (search.equals(getTextField(55).getText()))
                    return;
                search = getTextField(55).getText().toLowerCase();
                scrollEffects.resetScroll();
                scrollEffects.setList(getSearchList());
            }
        }
    }

    private List<String> getSearchList() {
        if (search.isEmpty()) {
            return new ArrayList<String>(this.data.keySet());
        }
        List<String> list = new ArrayList<String>();
        for (String name : this.data.keySet()) {
            if (name.toLowerCase().contains(search))
                list.add(name);
        }
        return list;
    }

    @Override
    public void setData(Vector<String> list, HashMap<String, Integer> data) {
        String name = scrollEffects.getSelected();
        this.data = data;
        scrollEffects.setList(getSearchList());

        if (name != null)
            scrollEffects.setSelected(name);
    }

    @Override
    public void setSelected(String selected) {
        this.selected = selected;
        scrollEffects.setSelected(selected);
        originalName = scrollEffects.getSelected();
    }

    @Override
    public void customScrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll.id == 0) {
            save();
            effect = null;
            selected = scrollEffects.getSelected();
            originalName = scrollEffects.getSelected();
            if (selected != null && !selected.isEmpty()) {
                PacketHandler.Instance.sendToServer(new DBCGetEffect(data.get(selected)));
            }
        }
    }

    @Override
    public void customScrollDoubleClicked(String selection, GuiCustomScroll scroll) {
//        ICustomScrollListener.super.customScrollDoubleClicked(selection, scroll);
    }

    @Override
    public void save() {
        if (this.selected != null && this.data.containsKey(this.selected) && this.effect != null) {
            PacketHandler.Instance.sendToServer(new DBCSaveEffect(effect.writeToNBT(false), effect.id, originalName));
        }
    }


    @Override
    public void subGuiClosed(SubGuiInterface subgui) {

    }

    @Override
    public void confirmClicked(boolean result, int id) {
        NoppesUtil.openGUI(player, this);
        if (!result)
            return;
        if (id == 1) {
            if (data.containsKey(scrollEffects.getSelected())) {
                PacketHandler.Instance.sendToServer(new DBCRemoveEffect(data.get(scrollEffects.getSelected())));
                scrollEffects.clear();
                effect = new CustomEffect();
                initGui();
            }
        }
    }

    public int getStringWidthWithoutColor(String text) {
        int width = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '§') {
                if (i < text.length() - 1) {
                    i += 1;
                }
            } else {
                // If not a color code, calculate the width
                width += fontRendererObj.getCharWidth(c);
            }
        }
        return width;
    }
}
