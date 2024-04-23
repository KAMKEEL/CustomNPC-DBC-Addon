package kamkeel.npcdbc.client.gui.inventory;

import kamkeel.npcdbc.client.gui.component.GuiFormScroll;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.DBCSelectForm;
import kamkeel.npcdbc.network.packets.TransformPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.client.Client;
import noppes.npcs.client.gui.player.inventory.GuiCNPCInventory;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.constants.EnumPlayerPacket;
import org.lwjgl.opengl.GL11;
import tconstruct.client.tabs.AbstractTab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class GuiDBC extends GuiCNPCInventory implements IGuiData, ICustomScrollListener, IScrollData {
    private final ResourceLocation resource = new ResourceLocation("customnpcs", "textures/gui/standardbg.png");
    private GuiFormScroll guiFormScroll;
    private String selected = null;
    private String search = "";
    private Form selectedForm;
    private HashMap<String, Integer> unlockedForms = new HashMap<String, Integer>();

    public GuiDBC() {
        super();
        xSize = 280;
        ySize = 180;
        this.drawDefaultBackground = false;
        title = "";
        NoppesUtilPlayer.sendData(EnumPlayerPacket.CustomFormsGet);
    }

    @Override
    public void initGui() {
        super.initGui();
        if (guiFormScroll == null) {
            guiFormScroll = new GuiFormScroll(this, 0);
            guiFormScroll.setSize(135, 140);
        }

        guiFormScroll.guiLeft = guiLeft + 4;
        guiFormScroll.guiTop = guiTop + 4;
        this.addScroll(guiFormScroll);
        addTextField(new GuiNpcTextField(55, this, fontRendererObj, guiLeft + 5, guiTop + ySize - 34, 134, 20, search));

        GuiNpcButton selectButton = new GuiNpcButton(1, guiLeft + 5, guiTop + ySize - 11, "form.select");
        selectButton.width = 65;
        this.addButton(selectButton);

        GuiNpcButton clearButton = new GuiNpcButton(2, guiLeft + 75, guiTop + ySize - 11, "form.clear");
        clearButton.width = 65;
        this.addButton(clearButton);

        // Add Details
        if (selectedForm != null) {
        }
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
        guiFormScroll.setSelected(selected);
    }

    private void renderScreen() {
        int size = 10;
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton instanceof AbstractTab)
            return;

        if (guibutton.id >= 100 && guibutton.id <= 105) {
            super.actionPerformed(guibutton);
            return;
        }

        if (guibutton.id == 1) {
            if (selected != null) {
                if (unlockedForms.containsKey(selected)) {
                    int formID = unlockedForms.get(selected);
                    PacketHandler.Instance.sendToServer(new DBCSelectForm(formID).generatePacket());
                }
            }
        } else if (guibutton.id == 2) {
            PacketHandler.Instance.sendToServer(new DBCSelectForm(-1).generatePacket());
            selected = null;
            guiFormScroll.selected = -1;
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
            guiFormScroll.resetScroll();
            guiFormScroll.setList(getFormSearch());
            setSelected(selectedForm.name); //so list keeps selectedForm highlighted despite search

        }
    }

    public void mouseClicked(int i, int j, int k) {
        if (getTextField(55).isFocused() && k == 1) { //empty search field on right click
            getTextField(55).setText("");
            search = "";
            guiFormScroll.setList(getFormSearch());
            setSelected(selected);
        }

        super.mouseClicked(i, j, k);
    }

    private List<String> getFormSearch() {
        if (search.isEmpty()) {
            return new ArrayList<String>(this.unlockedForms.keySet());
        }
        List<String> list = new ArrayList<String>();
        for (String name : this.unlockedForms.keySet()) {
            if (name.toLowerCase().contains(search))
                list.add(name);
        }
        return list;
    }


    @Override
    public void setData(Vector<String> list, HashMap<String, Integer> data) {
        String name = guiFormScroll.getSelected();
        this.unlockedForms = data;
        if (guiFormScroll != null)
            guiFormScroll.setList(getFormSearch());
        if (name != null)
            guiFormScroll.setSelected(name);
        initGui();
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        if (compound.hasNoTags()) {
            this.selectedForm = null;
            selected = null;
            setSelected(selected);
        } else {
            this.selectedForm = new Form();
            selectedForm.readFromNBT(compound);
            setSelected(selectedForm.name);
        }
        initGui();
    }

    @Override
    public void customScrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll.id == guiFormScroll.id && guiFormScroll != null) {
            //clicking a selected item in list deselects it (super unnecessary but my neurodivergent brain requires this level of detail)
            if (selected != null && selected.equals(guiFormScroll.getSelected())) {
                selected = "";
                guiFormScroll.selected = -1;
                return;
            }

            selected = guiFormScroll.getSelected();
            initGui();
        }
    }

    @Override
    public void customScrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }
}
