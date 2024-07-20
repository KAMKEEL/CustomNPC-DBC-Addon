package kamkeel.npcdbc.client.gui.global.form;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.form.DBCGetForm;
import kamkeel.npcdbc.network.packets.form.DBCRemoveForm;
import kamkeel.npcdbc.network.packets.form.DBCRequestForm;
import kamkeel.npcdbc.network.packets.form.DBCSaveForm;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcdbc.util.Utility;
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

public class GuiNPCManageForms extends GuiNPCInterface2 implements ICustomScrollListener, IScrollData, IGuiData, ISubGuiListener, GuiYesNoCallback {
    public GuiCustomScroll scrollForms;
    public HashMap<String, Integer> data = new HashMap<>();
    private Form customForm = new Form();
    private final ArrayList<String> stackables = new ArrayList<>();
    private String selected = null;
    private String search = "";

    public int parentForm = -1;
    public int childForm = -1;

    public GuiNPCManageForms(EntityNPCInterface npc) {
        super(npc);
        PacketHandler.Instance.sendToServer(new DBCRequestForm(-1, false).generatePacket());
    }

    public void initGui() {
        super.initGui();
        addButton(new GuiNpcButton(0, guiLeft + 368, guiTop + 8, 45, 20, "gui.add"));

        addButton(new GuiNpcButton(1, guiLeft + 368, guiTop + 32, 45, 20, "gui.remove"));
        getButton(1).enabled = customForm != null && customForm.id != -1;

        addButton(new GuiNpcButton(2, guiLeft + 368, guiTop + 56, 45, 20, "gui.edit"));
        getButton(2).enabled = customForm != null && customForm.id != -1;

        if (scrollForms == null) {
            scrollForms = new GuiCustomScroll(this, 0, 0);
            scrollForms.setSize(143, 185);
        }
        scrollForms.guiLeft = guiLeft + 220;
        scrollForms.guiTop = guiTop + 4;
        addScroll(scrollForms);
        scrollForms.setList(getSearchList());

        addTextField(new GuiNpcTextField(55, this, fontRendererObj, guiLeft + 220, guiTop + 4 + 3 + 185, 143, 20, search));

        registerStackables();
        if(customForm != null && customForm.id != -1) {
            addLabel(new GuiNpcLabel(10, "ID", guiLeft + 368, guiTop + 4 + 3 + 185));
            addLabel(new GuiNpcLabel(11, customForm.id + "", guiLeft + 368, guiTop + 4 + 3 + 195));
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        switch(button.id){
            case 0:
                save();
                String name = "New";
                while (data.containsKey(name))
                    name += "_";
                Form form = new Form(-1, name);
                PacketHandler.Instance.sendToServer(new DBCSaveForm(form.writeToNBT()).generatePacket());
                break;

            case 1:
                if (data.containsKey(scrollForms.getSelected())) {
                    GuiYesNo guiyesno = new GuiYesNo(this, scrollForms.getSelected(), StatCollector.translateToLocal("gui.delete"), 1);
                    displayGuiScreen(guiyesno);
                }
                break;
            case 2:
                if (data.containsKey(scrollForms.getSelected()) && customForm != null && customForm.id >= 0) {
                    setSubGui(new SubGuiFormGeneral(this, customForm));
                }
                break;
        }
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        this.customForm = new Form();
        customForm.readFromNBT(compound);
        parentForm = customForm.parentID;
        childForm = customForm.childID;
        setSelected(customForm.name);
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
        if (customForm != null && customForm.id != -1) {
            String drawString = customForm.getMenuName();
            int textWidth = getStringWidthWithoutColor(drawString);
            int centerX = guiLeft + 5 + ((218 - 10 - textWidth) / 2); // Adjusted centerX calculation
            fontRendererObj.drawString(drawString, centerX, guiTop + 10, CustomNpcResourceListener.DefaultTextColor, true);
            int y = guiTop + 18;

            String race = "§fRace: §e" + (customForm.getRace() == -1 ? "All" : customForm.getRace() == DBCRace.ALL_SAIYANS ? "All Saiyans" : customForm.getRace() == 1 ? "Pure Saiyan" : JRMCoreH.Races[customForm.race()]);
            fontRendererObj.drawString(race, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);

            String label = "§fStrength:";
            String info = "§4x§c" + customForm.strengthMulti;
            fontRendererObj.drawString(label, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
            fontRendererObj.drawString(info, guiLeft + 65, y, CustomNpcResourceListener.DefaultTextColor, true);

            label = "§fDexterity:";
            info = "§3x§b" + customForm.dexMulti;
            fontRendererObj.drawString(label, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
            fontRendererObj.drawString(info, guiLeft + 65, y, CustomNpcResourceListener.DefaultTextColor, true);

            label = "§fWillpower:";
            info = "§6x§e" + customForm.willMulti;
            fontRendererObj.drawString(label, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
            fontRendererObj.drawString(info, guiLeft + 65, y, CustomNpcResourceListener.DefaultTextColor, true);

            if (!stackables.isEmpty()) {
                label = "§fStackable:";
                info = String.join("§7, ", stackables);
                fontRendererObj.drawString(label, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
                fontRendererObj.drawString(info, guiLeft + 65, y, CustomNpcResourceListener.DefaultTextColor, true);
            }

            if(!customForm.requiredForm.isEmpty() || customForm.hasParent() || customForm.hasChild()){
                String parent = "§f------- Links -------";
                fontRendererObj.drawString(parent, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
            }
            if (customForm.hasChild() && customForm.getChild() != null) {
                String child = "§fChild Form: ";
                String childName = "§7" + customForm.getChild().getName();
                fontRendererObj.drawString(child, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
                fontRendererObj.drawString(childName, guiLeft + 80, y, CustomNpcResourceListener.DefaultTextColor, true);
            }
            if (customForm.race() != -1) {
                if (customForm.requiredForm.containsKey(customForm.race())) {
                    String parent = "§fParent Form: ";
                    String parentName = Utility.removeBoldColorCode(DBCUtils.getFormattedStateName(customForm.race(), customForm.requiredForm.get(customForm.race())));
                    fontRendererObj.drawString(parent, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
                    fontRendererObj.drawString(parentName, guiLeft + 80, y, CustomNpcResourceListener.DefaultTextColor, true);
                } else if (customForm.hasParent() && customForm.getParent() != null) {
                    String parent = "§fParent Form: ";
                    String parentName = "§7" + customForm.getParent().getName();
                    fontRendererObj.drawString(parent, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
                    fontRendererObj.drawString(parentName, guiLeft + 80, y, CustomNpcResourceListener.DefaultTextColor, true);
                }
            } else if (!customForm.requiredForm.isEmpty() || (customForm.hasParent() && customForm.getParent() != null)) {
                if (customForm.hasParent() && customForm.getParent() != null) {
                    String parent = "§fParent Form: ";
                    String parentName = "§7" + customForm.getParent().getName();
                    fontRendererObj.drawString(parent, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
                    fontRendererObj.drawString(parentName, guiLeft + 80, y, CustomNpcResourceListener.DefaultTextColor, true);
                }
                if(!customForm.requiredForm.isEmpty()){
                    String ok = "§f-- Parent Forms --";
                    fontRendererObj.drawString(ok, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
                    for(int i = 0; i < 6; i++){
                        if(customForm.requiredForm.containsKey(i)){
                            String parent = "§f" + JRMCoreH.Races[i] + "§f: ";
                            String parentName = Utility.removeBoldColorCode(DBCUtils.getFormattedStateName(i , customForm.requiredForm.get(i)));
                            fontRendererObj.drawString(parent, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
                            fontRendererObj.drawString(parentName, guiLeft + 80, y, CustomNpcResourceListener.DefaultTextColor, true);
                        }
                    }
                }
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


    @Override
    public void keyTyped(char c, int i) {
        super.keyTyped(c, i);
        if (getTextField(55) != null) {
            if (getTextField(55).isFocused()) {
                if (search.equals(getTextField(55).getText()))
                    return;
                search = getTextField(55).getText().toLowerCase();
                scrollForms.resetScroll();
                scrollForms.setList(getSearchList());
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
        String name = scrollForms.getSelected();
        this.data = data;
        scrollForms.setList(getSearchList());

        if (name != null)
            scrollForms.setSelected(name);
    }

    @Override
    public void setSelected(String selected) {
        this.selected = selected;
        scrollForms.setSelected(selected);
    }

    @Override
    public void customScrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll.id == 0) {
            save();
            selected = scrollForms.getSelected();
            if(selected != null && !selected.isEmpty())
                PacketHandler.Instance.sendToServer(new DBCGetForm(data.get(selected)).generatePacket());
        }
    }

    @Override
    public void save() {}

    public void registerStackables() {
        stackables.clear();
        if (customForm != null) {
            if (customForm.stackable.vanillaStackable) {
                stackables.add("§eDBC");
            }
            if (customForm.stackable.kaiokenStackable) {
                stackables.add("§cKK");
            }
            if (customForm.stackable.uiStackable) {
                stackables.add("§7UI");
            }
            if (customForm.stackable.godStackable) {
                stackables.add("§5GoD");
            }
            if (customForm.stackable.mysticStackable) {
                stackables.add("§dMystic");
            }
        }
    }

    @Override
    public void subGuiClosed(SubGuiInterface subgui) {}

    @Override
    public void confirmClicked(boolean result, int id) {
        NoppesUtil.openGUI(player, this);
        if (!result)
            return;
        if (id == 1) {
            if (data.containsKey(scrollForms.getSelected())) {
                PacketHandler.Instance.sendToServer(new DBCRemoveForm(data.get(scrollForms.getSelected())).generatePacket());
                scrollForms.clear();
                customForm = new Form();
                initGui();
            }
        }
    }
}
