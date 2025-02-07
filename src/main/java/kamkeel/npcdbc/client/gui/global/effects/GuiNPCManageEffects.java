package kamkeel.npcdbc.client.gui.global.effects;

import akka.japi.Effect;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.data.statuseffect.CustomEffect;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.outline.DBCGetOutline;
import kamkeel.npcdbc.network.packets.outline.DBCRemoveOutline;
import kamkeel.npcdbc.network.packets.outline.DBCRequestOutline;
import kamkeel.npcdbc.network.packets.outline.DBCSaveOutline;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class GuiNPCManageEffects extends GuiNPCInterface2 implements ICustomScrollListener, IScrollData, IGuiData, ISubGuiListener, GuiYesNoCallback, ITextfieldListener {
    public GuiCustomScroll scrollEffects;
    public HashMap<String, Integer> data = new HashMap<>();
    public CustomEffect effect = new CustomEffect();
    public String selected = null;
    public String search = "";
    public String originalName = "";
    boolean setNormalSound = true;

    public GuiNPCManageEffects(EntityNPCInterface npc) {
        super(npc);
        PacketHandler.Instance.sendToServer(new DBCRequestOutline(-1).generatePacket());
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
            scrollEffects.setSize(123, 185);
        }
        scrollEffects.guiLeft = guiLeft + 240;
        scrollEffects.guiTop = guiTop + 4;
        addScroll(scrollEffects);
        scrollEffects.setList(getSearchList());

        addTextField(new GuiNpcTextField(55, this, fontRendererObj, guiLeft + 240, guiTop + 4 + 3 + 185, 123, 20, search));
        if (effect != null && effect.id != -1) {
            //   addButton(new GuiNpcButton(1500, guiLeft + 8, guiTop + 192, 203, 20, "display.displaySettings"));
            addLabel(new GuiNpcLabel(10, "ID", guiLeft + 368, guiTop + 4 + 3 + 185));
            addLabel(new GuiNpcLabel(11, effect.id + "", guiLeft + 368, guiTop + 4 + 3 + 195));
//
//            int y = guiTop + 3;
//
//            addTextField(new GuiNpcTextField(13, this, this.fontRendererObj, guiLeft + 36, y, 180, 20, outline.name));
//            addLabel(new GuiNpcLabel(13, "gui.name", guiLeft + 4, y + 5));
//
//            y += 23;
//
//            addTextField(new GuiNpcTextField(14, this, guiLeft + 70, y, 146, 20, outline.menuName.replaceAll("§", "&")));
//            getTextField(14).setMaxStringLength(20);
//            addLabel(new GuiNpcLabel(14, "general.menuName", guiLeft + 4, y + 5));
//
//            y += 60;
//            addButton(new GuiNpcButton(1500, guiLeft + 7, y, 208, 20, "display.displaySettings"));
//
//            y += 40;


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
            Outline outline = new Outline(-1, name);
            PacketHandler.Instance.sendToServer(new DBCSaveOutline(outline.writeToNBT(), "").generatePacket());
        }

        if (button.id == 1) {
            if (data.containsKey(scrollEffects.getSelected())) {
                GuiYesNo guiyesno = new GuiYesNo(this, scrollEffects.getSelected(), StatCollector.translateToLocal("gui.delete"), 1);
                displayGuiScreen(guiyesno);
            }
        }
        if (button.id == 2) {
         //   Outline outline = (Outline) this.effect.clone();
          //  while (data.containsKey(outline.name))
             //   outline.name += "_";
         //   PacketHandler.Instance.sendToServer(new DBCSaveOutline(outline.writeToNBT(), "").generatePacket());
        }


    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
//        this.effect = new CustomEffect();
//        effect.readFromNBT(compound);
//        setSelected(effect.name);
//
//        if (effect.id != -1) {
//            OutlineController.getInstance().customOutlines.replace(effect.id, effect);
//        }
        initGui();
    }



    @Override
    public void drawScreen(int i, int j, float f) {
        super.drawScreen(i, j, f);
    }

    @Override
    public void drawBackground() {
        super.drawBackground();
        int xPosGradient = guiLeft + 10;
        int yPosGradient = guiTop + 6;
        drawGradientRect(xPosGradient, yPosGradient, 220 + xPosGradient, 204 + yPosGradient, 0xc0101010, 0xd0101010);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
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
            selected = scrollEffects.getSelected();
            originalName = scrollEffects.getSelected();
            if (selected != null && !selected.isEmpty()) {
                PacketHandler.Instance.sendToServer(new DBCGetOutline(data.get(selected)).generatePacket());
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
            PacketHandler.Instance.sendToServer(new DBCSaveOutline(effect.writeToNBT(true), originalName).generatePacket());
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
                PacketHandler.Instance.sendToServer(new DBCRemoveOutline(data.get(scrollEffects.getSelected())).generatePacket());
                scrollEffects.clear();
                effect = new CustomEffect();
                initGui();
            }
        }
    }

    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {
        if (effect == null || effect.id == -1)
            return;
        if (guiNpcTextField.id == 13) {
            String name = guiNpcTextField.getText();
            if (!name.isEmpty() && !this.data.containsKey(name)) {
                String old = this.effect.name;
                this.data.remove(this.effect.name);
                this.effect.name = name;
                this.data.put(this.effect.name, this.effect.id);
                this.selected = name;
                this.scrollEffects.replace(old, this.effect.name);
            } else
                guiNpcTextField.setText(effect.name);
        }


    }
}
