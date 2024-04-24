package kamkeel.npcdbc.client.gui.inventory;

import kamkeel.npcdbc.client.gui.component.GuiFormScroll;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.DBCSelectAura;
import kamkeel.npcdbc.network.packets.DBCSelectForm;
import kamkeel.npcdbc.network.packets.RequestAura;
import kamkeel.npcdbc.network.packets.RequestForm;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.player.inventory.GuiCNPCInventory;
import noppes.npcs.client.gui.util.*;
import org.lwjgl.opengl.GL11;
import tconstruct.client.tabs.AbstractTab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class GuiDBC extends GuiCNPCInventory implements IGuiData, ICustomScrollListener, IScrollData {
    private final ResourceLocation resource = new ResourceLocation("customnpcs", "textures/gui/standardbg.png");
    private GuiFormScroll guiScroll;
    private String selected = null;
    private String search = "";
    private Form selectedForm;
    private Aura selectedAura;
    private HashMap<String, Integer> loadedData = new HashMap<String, Integer>();
    private static int activePage = 0;

    public GuiDBC() {
        super();
        xSize = 280;
        ySize = 180;
        this.drawDefaultBackground = false;
        title = "";
        if(activePage == 0)
            PacketHandler.Instance.sendToServer(new RequestForm(-1, true).generatePacket());
        else
            PacketHandler.Instance.sendToServer(new RequestAura(-1,true).generatePacket());
    }

    @Override
    public void initGui() {
        super.initGui();
        if (guiScroll == null) {
            guiScroll = new GuiFormScroll(this, 0);
            guiScroll.setSize(135, 118);
        }

        GuiNpcButton formsButton = new GuiNpcButton(40, guiLeft + 5, guiTop + 4, "Forms");
        formsButton.width = 65;
        formsButton.enabled = activePage != 0;
        formsButton.packedFGColour = 0xffc626;
        this.addButton(formsButton);

        GuiNpcButton auras = new GuiNpcButton(41, guiLeft + 75, guiTop + 4, "Auras");
        auras.packedFGColour = 0xf04fff;
        auras.enabled = activePage != 1;
        auras.width = 65;
        this.addButton(auras);

        guiScroll.guiLeft = guiLeft + 4;
        guiScroll.guiTop = guiTop + 26;
        this.addScroll(guiScroll);
        addTextField(new GuiNpcTextField(55, this, fontRendererObj, guiLeft + 5, guiTop + ySize - 34, 134, 20, search));

        GuiNpcButton selectButton = new GuiNpcButton(1, guiLeft + 5, guiTop + ySize - 11, "form.select");
        selectButton.width = 65;
        this.addButton(selectButton);

        GuiNpcButton clearButton = new GuiNpcButton(2, guiLeft + 75, guiTop + ySize - 11, "form.clear");
        clearButton.width = 65;
        this.addButton(clearButton);
    }

    @Override
    public void drawScreen(int i, int j, float f) {
        drawDefaultBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(resource);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 252, 195);
        drawTexturedModalRect(guiLeft + 252, guiTop, 188, 0, 67, 195);
        renderScreen();

        super.drawScreen(i, j, f);
    }

    @Override
    public void setSelected(String selected) {
        this.selected = selected;
        guiScroll.setSelected(selected);
    }

    private void renderScreen() {
        drawGradientRect(guiLeft + 140, guiTop + 4, guiLeft + xSize + 36 ,guiTop + 24, 0xC0101010, 0xC0101010);
        drawHorizontalLine(guiLeft + 140, guiLeft + xSize + 35, guiTop + 25, 0xFF000000 + CustomNpcResourceListener.DefaultTextColor);
        drawGradientRect(guiLeft + 140, guiTop + 27, guiLeft + xSize + 36 ,guiTop + ySize - 14, 0xA0101010, 0xA0101010);

        if(activePage == 0){
            String drawString = "§fNo Form Selected";
            if (selectedForm != null) {
                drawString = selectedForm.getMenuName();
            }
            int textWidth = getStringWidthWithoutColor(drawString);
            int centerX = guiLeft + 140 + ((xSize - textWidth + 30 - 140) / 2); // Adjusted centerX calculation
            fontRendererObj.drawString(drawString, centerX, guiTop + 10, CustomNpcResourceListener.DefaultTextColor, true);
            if (selectedForm != null) {
                DBCData dbcData = DBCData.getClient();
                int race = dbcData.Race;
                int y = guiTop + 18;
                if(selectedForm.requiredForm.containsKey(race)){
                    String parent = "§fPrev: " + DBCUtils.getFormattedStateName(race, selectedForm.requiredForm.get(race));
                    parent = Utility.removeBoldColorCode(parent);
                    fontRendererObj.drawString(parent, guiLeft + 143, y+=12, CustomNpcResourceListener.DefaultTextColor, true);
                } else if(selectedForm.hasParent() && selectedForm.getParent() != null){
                    String parent = "§fPrev: " + selectedForm.getParent().getMenuName();
                    parent = Utility.removeBoldColorCode(parent);
                    fontRendererObj.drawString(parent, guiLeft + 143, y+=12, CustomNpcResourceListener.DefaultTextColor, true);
                }
                if(selectedForm.hasChild() && selectedForm.getChild() != null){
                    String child = "§fNext: " + selectedForm.getChild().getMenuName();
                    child = Utility.removeBoldColorCode(child);
                    fontRendererObj.drawString(child, guiLeft + 143, y+=12, CustomNpcResourceListener.DefaultTextColor, true);
                }
            }
        }
        else {
            String drawString = "§fNo Aura Selected";
            if (selectedAura != null) {
                drawString = selectedAura.getMenuName();
            }
            int textWidth = getStringWidthWithoutColor(drawString);
            int centerX = guiLeft + 140 + ((xSize - textWidth + 30 - 140) / 2);
            fontRendererObj.drawString(drawString, centerX, guiTop + 10, CustomNpcResourceListener.DefaultTextColor, true);
            if (selectedAura != null) {

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
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton instanceof AbstractTab)
            return;

        if (guibutton.id >= 100 && guibutton.id <= 105) {
            super.actionPerformed(guibutton);
            return;
        }
        if(guibutton.id == 40 && activePage != 0){
            activePage = 0;
            PacketHandler.Instance.sendToServer(new RequestForm(-1, true).generatePacket());
        }
        if(guibutton.id == 41 && activePage != 1){
            activePage = 1;
            PacketHandler.Instance.sendToServer(new RequestAura(-1, true).generatePacket());
        }

        if(activePage == 0){
            if (guibutton.id == 1) {
                if (selected != null) {
                    if (loadedData.containsKey(selected)) {
                        int formID = loadedData.get(selected);
                        PacketHandler.Instance.sendToServer(new DBCSelectForm(formID).generatePacket());
                    }
                }
            } else if (guibutton.id == 2) {
                PacketHandler.Instance.sendToServer(new DBCSelectForm(-1).generatePacket());
                selected = null;
                guiScroll.selected = -1;
            }
        }
        else {
            if (guibutton.id == 1) {
                if (selected != null) {
                    if (loadedData.containsKey(selected)) {
                        int auraID = loadedData.get(selected);
                        PacketHandler.Instance.sendToServer(new DBCSelectAura(auraID).generatePacket());
                    }
                }
            } else if (guibutton.id == 2) {
                PacketHandler.Instance.sendToServer(new DBCSelectAura(-1).generatePacket());
                selected = null;
                guiScroll.selected = -1;
            }
        }
    }


    @Override
    public void keyTyped(char c, int i) {
        if ((i == 1 || isInventoryKey(i)) && !getTextField(55).isFocused())
            close();
        if (getTextField(55).isFocused() && i == 1) //empties search field on escape
            getTextField(55).setText("");
        super.keyTyped(c, i);
        if (getTextField(55) != null && getTextField(55).isFocused()) {
            if (search.equals(getTextField(55).getText()))
                return;
            search = getTextField(55).getText().toLowerCase();
            guiScroll.resetScroll();
            guiScroll.setList(getSearch());
            setSelected(selectedForm.name); //so list keeps selectedForm highlighted despite search

        }
    }

    public void mouseClicked(int i, int j, int k) {
        if (getTextField(55).isFocused() && k == 1) { //empty search field on right click
            getTextField(55).setText("");
            search = "";
            guiScroll.setList(getSearch());
            setSelected(selected);
        }

        super.mouseClicked(i, j, k);
    }

    private List<String> getSearch() {
        if (search.isEmpty()) {
            return new ArrayList<String>(this.loadedData.keySet());
        }
        List<String> list = new ArrayList<String>();
        for (String name : this.loadedData.keySet()) {
            if (name.toLowerCase().contains(search))
                list.add(name);
        }
        return list;
    }


    @Override
    public void setData(Vector<String> list, HashMap<String, Integer> data) {
        String name = guiScroll.getSelected();
        this.loadedData = data;
        if (guiScroll != null)
            guiScroll.setList(getSearch());
        if (guiScroll != null && name != null)
            guiScroll.setSelected(name);
        initGui();
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        if (compound.hasNoTags()) {
            this.selectedAura = null;
            this.selectedForm = null;
            selected = null;
            setSelected(selected);
        }
        else if (compound.hasKey("attributes")) {
            this.selectedForm = new Form();
            this.selectedAura = null;
            selectedForm.readFromNBT(compound);
            setSelected(selectedForm.name);
        } else {
            this.selectedAura = new Aura();
            this.selectedForm = null;
            selectedAura.readFromNBT(compound);
            setSelected(selectedAura.name);
        }
        initGui();
    }

    @Override
    public void customScrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        if (guiScroll != null && guiCustomScroll.id == guiScroll.id) {
            //clicking a selected item in list deselects it (super unnecessary but my neurodivergent brain requires this level of detail)
            if (selected != null && selected.equals(guiScroll.getSelected())) {
                selected = "";
                guiScroll.selected = -1;
                return;
            }

            selected = guiScroll.getSelected();
            initGui();
        }
    }

    @Override
    public void customScrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }
}
