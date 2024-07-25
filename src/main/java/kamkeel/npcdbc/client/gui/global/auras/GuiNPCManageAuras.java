package kamkeel.npcdbc.client.gui.global.auras;

import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.aura.DBCGetAura;
import kamkeel.npcdbc.network.packets.aura.DBCRemoveAura;
import kamkeel.npcdbc.network.packets.aura.DBCRequestAura;
import kamkeel.npcdbc.network.packets.aura.DBCSaveAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.select.GuiSoundSelection;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class GuiNPCManageAuras extends GuiNPCInterface2 implements ICustomScrollListener, IScrollData, IGuiData, ISubGuiListener, GuiYesNoCallback, ITextfieldListener {
    public GuiCustomScroll scrollAuras;
    public HashMap<String, Integer> data = new HashMap<>();
    boolean setNormalSound = true;
    private Aura aura = new Aura();
    private String selected = null;
    private String search = "";
    private String originalName = "";

    public GuiNPCManageAuras(EntityNPCInterface npc) {
        super(npc);
        PacketHandler.Instance.sendToServer(new DBCRequestAura(-1, false).generatePacket());
    }

    public void initGui() {
        super.initGui();
        addButton(new GuiNpcButton(0, guiLeft + 368, guiTop + 8, 45, 20, "gui.add"));

        addButton(new GuiNpcButton(1, guiLeft + 368, guiTop + 32, 45, 20, "gui.remove"));
        getButton(1).enabled = aura != null && aura.id != -1;

        addButton(new GuiNpcButton(2, guiLeft + 368, guiTop + 56, 45, 20, "gui.clone"));
        getButton(2).enabled = aura != null && aura.id != -1;

        if (scrollAuras == null) {
            scrollAuras = new GuiCustomScroll(this, 0, 0);
            scrollAuras.setSize(143, 185);
        }
        scrollAuras.guiLeft = guiLeft + 220;
        scrollAuras.guiTop = guiTop + 4;
        addScroll(scrollAuras);
        scrollAuras.setList(getSearchList());

        addTextField(new GuiNpcTextField(55, this, fontRendererObj, guiLeft + 220, guiTop + 4 + 3 + 185, 143, 20, search));
        if (aura != null && aura.id != -1) {
            addLabel(new GuiNpcLabel(10, "ID", guiLeft + 368, guiTop + 4 + 3 + 185));
            addLabel(new GuiNpcLabel(11, aura.id + "", guiLeft + 368, guiTop + 4 + 3 + 195));

            int y = guiTop + 3;

            addTextField(new GuiNpcTextField(13, this, this.fontRendererObj, guiLeft + 36, y, 180, 20, aura.name));
            addLabel(new GuiNpcLabel(13, "gui.name", guiLeft + 4, y + 5));

            y += 23;

            addTextField(new GuiNpcTextField(14, this, guiLeft + 70, y, 146, 20, aura.menuName.replaceAll("§", "&")));
            getTextField(14).setMaxStringLength(20);
            addLabel(new GuiNpcLabel(14, "general.menuName", guiLeft + 4, y + 5));

            y += 60;
            addButton(new GuiNpcButton(1500, guiLeft + 7, y, 208, 20, "display.displaySettings"));

            y += 40;

            addLabel(new GuiNpcLabel(30, "general.auraSound", guiLeft + 5, y + 5));
            y += 16;
            addTextField(new GuiNpcTextField(30, this, fontRendererObj, guiLeft + 5, y, 151, 20, aura.display.auraSound));
            addButton(new GuiNpcButton(30, guiLeft + 158, y, 60, 20, "gui.select"));

            y += 23;
            addLabel(new GuiNpcLabel(31, "general.kaiokenSound", guiLeft + 5, y + 5));
            y += 16;
            addTextField(new GuiNpcTextField(31, this, fontRendererObj, guiLeft + 5, y, 151, 20, aura.display.kaiokenSound));
            addButton(new GuiNpcButton(31, guiLeft + 158, y, 60, 20, "gui.select"));
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
            Aura aura = new Aura(-1, name);
            PacketHandler.Instance.sendToServer(new DBCSaveAura(aura.writeToNBT(), "").generatePacket());
        }

        if (button.id == 1) {
            if (data.containsKey(scrollAuras.getSelected())) {
                GuiYesNo guiyesno = new GuiYesNo(this, scrollAuras.getSelected(), StatCollector.translateToLocal("gui.delete"), 1);
                displayGuiScreen(guiyesno);
            }
        }
        if (button.id == 2) {
            Aura aura = (Aura) this.aura.clone();
            while (data.containsKey(aura.name))
                aura.name += "_";
            PacketHandler.Instance.sendToServer(new DBCSaveAura(aura.writeToNBT(), "").generatePacket());
        }

        if (aura == null)
            return;

        if (button.id == 30) {
            setNormalSound = true;
            setSubGui(new GuiSoundSelection((getTextField(30).getText())));
        }
        if (button.id == 31) {
            setNormalSound = false;
            setSubGui(new GuiSoundSelection((getTextField(31).getText())));
        }
        if (button.id == 1500) {
            EntityCustomNpc npc = new EntityCustomNpc(Minecraft.getMinecraft().theWorld);
            Minecraft.getMinecraft().displayGuiScreen(new SubGuiAuraDisplay(this, npc, aura));
        }
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        this.aura = new Aura();
        aura.readFromNBT(compound);
        setSelected(aura.name);
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
    }


    @Override
    public void keyTyped(char c, int i) {
        super.keyTyped(c, i);

        if (getTextField(55) != null) {
            if (getTextField(55).isFocused()) {
                if (search.equals(getTextField(55).getText()))
                    return;
                search = getTextField(55).getText().toLowerCase();
                scrollAuras.resetScroll();
                scrollAuras.setList(getSearchList());
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
        String name = scrollAuras.getSelected();
        this.data = data;
        scrollAuras.setList(getSearchList());

        if (name != null)
            scrollAuras.setSelected(name);
    }

    @Override
    public void setSelected(String selected) {
        this.selected = selected;
        scrollAuras.setSelected(selected);
        originalName = scrollAuras.getSelected();
    }

    @Override
    public void customScrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll.id == 0) {
            save();
            selected = scrollAuras.getSelected();
            originalName = scrollAuras.getSelected();
            if (selected != null && !selected.isEmpty()) {
                PacketHandler.Instance.sendToServer(new DBCGetAura(data.get(selected)).generatePacket());
            }
        }
    }

    @Override
    public void customScrollDoubleClicked(String selection, GuiCustomScroll scroll) {
        ICustomScrollListener.super.customScrollDoubleClicked(selection, scroll);
    }

    @Override
    public void save() {
        if (this.selected != null && this.data.containsKey(this.selected) && this.aura != null) {
            PacketHandler.Instance.sendToServer(new DBCSaveAura(aura.writeToNBT(), originalName).generatePacket());
        }
    }


    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof GuiSoundSelection) {
            GuiSoundSelection gss = (GuiSoundSelection) subgui;
            if (gss.selectedResource != null) {
                if (setNormalSound) {
                    getTextField(30).setText(gss.selectedResource.toString());
                    unFocused(getTextField(30));
                } else {
                    getTextField(31).setText(gss.selectedResource.toString());
                    unFocused(getTextField(31));
                }
                initGui();
            }
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        NoppesUtil.openGUI(player, this);
        if (!result)
            return;
        if (id == 1) {
            if (data.containsKey(scrollAuras.getSelected())) {
                PacketHandler.Instance.sendToServer(new DBCRemoveAura(data.get(scrollAuras.getSelected())).generatePacket());
                scrollAuras.clear();
                aura = new Aura();
                initGui();
            }
        }
    }

    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {
        if (aura == null || aura.id == -1)
            return;
        if (guiNpcTextField.id == 13) {
            String name = guiNpcTextField.getText();
            if (!name.isEmpty() && !this.data.containsKey(name)) {
                String old = this.aura.name;
                this.data.remove(this.aura.name);
                this.aura.name = name;
                this.data.put(this.aura.name, this.aura.id);
                this.selected = name;
                this.scrollAuras.replace(old, this.aura.name);
            } else
                guiNpcTextField.setText(aura.name);
        }
        if (guiNpcTextField.id == 14) {
            String menuName = guiNpcTextField.getText();
            if (!menuName.isEmpty()) {
                aura.menuName = menuName.replaceAll("&", "§");
            }
        }
        if (guiNpcTextField.id == 30) {
            aura.display.auraSound = guiNpcTextField.getText();
        }
        if (guiNpcTextField.id == 31) {
            aura.display.kaiokenSound = guiNpcTextField.getText();
        }
    }
}
