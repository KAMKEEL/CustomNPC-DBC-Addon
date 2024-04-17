package kamkeel.npcdbc.client.gui;

import kamkeel.npcdbc.data.CustomForm;
import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.util.Utility;
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

    private FormSelectionScroll formSelectionScroll;
    private String selected = null;
    private String search = "";
    private CustomForm selectedForm;
    private HashMap<String, Integer> formData = new HashMap<String, Integer>();
    PlayerCustomFormData playerFormData;

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
        if (formSelectionScroll == null) {
            formSelectionScroll = new FormSelectionScroll(this, 0);
            formSelectionScroll.setSize(135, 140);
        }

        formSelectionScroll.guiLeft = guiLeft + 4;
        formSelectionScroll.guiTop = guiTop + 4;
        this.addScroll(formSelectionScroll);
        addTextField(new GuiNpcTextField(55, this, fontRendererObj, guiLeft + 5, guiTop + ySize - 34, 134, 20, search));

        GuiNpcButton selectButton = new GuiNpcButton(1, guiLeft + 5, guiTop + ySize - 11, "form.select");
        selectButton.width = 65;
        this.addButton(selectButton);

        GuiNpcButton clearButton = new GuiNpcButton(2, guiLeft + 75, guiTop + ySize - 11, "form.clear");
        clearButton.width = 65;
        this.addButton(clearButton);

        // Add Details
        if (selectedForm != null) {
            setSelected(playerFormData.getColoredName(selectedForm));
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
        formSelectionScroll.setSelected(selected);
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

                Client.sendData(EnumPacketServer.CustomFormSet, formData.get(selected));
            }
        } else if (guibutton.id == 2) {
            Client.sendData(EnumPacketServer.CustomFormSet, -1);
            selected = "";
            formSelectionScroll.selected = -1;
        }
    }


    @Override
    public void keyTyped(char c, int i) {
        if (i == 1 || isInventoryKey(i))
            close();
        super.keyTyped(c, i);
        if (getTextField(55) != null) {
            if (getTextField(55).isFocused()) {
                if (search.equals(getTextField(55).getText()))
                    return;
                search = getTextField(55).getText().toLowerCase();
                formSelectionScroll.resetScroll();
                formSelectionScroll.setList(getFormSearch());
            }
        }
    }

    private List<String> getFormSearch() {
        if (search.isEmpty()) {
            return playerFormData.getAllForms();
        }
        List<String> list = new ArrayList<String>();
        for (String name : playerFormData.getAllForms()) {
            if (name.toLowerCase().contains(search))
                list.add(name);
        }
        return list;
    }


    @Override
    public void setData(Vector<String> list, HashMap<String, Integer> data) {
        String name = formSelectionScroll.getSelected();
        this.formData = data;
        playerFormData = Utility.getFormDataClient();
        formSelectionScroll.setList(getFormSearch());

        if (name != null)
            formSelectionScroll.setSelected(name);

        initGui();
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        if (compound.hasNoTags()) {
            this.selectedForm = null;
            setSelected(null);
        } else {
            this.selectedForm = new CustomForm();
            selectedForm.readFromNBT(compound);
            setSelected(selectedForm.menuName);
        }
        initGui();
    }

    @Override
    public void customScrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll.id == 0) {
            if (selected != null) {
                if (selected.equals(formSelectionScroll.getSelected())) {
                    selected = "";
                    formSelectionScroll.selected = -1;
                    return;
                }
            }


            // status.label = "";
            selected = formSelectionScroll.getSelected();
        }
    }

    @Override
    public void customScrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }
}
