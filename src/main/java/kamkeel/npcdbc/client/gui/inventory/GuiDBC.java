package kamkeel.npcdbc.client.gui.inventory;

import kamkeel.npcdbc.client.gui.component.GuiFormAuraScroll;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.get.aura.DBCGetAura;
import kamkeel.npcdbc.network.packets.player.aura.DBCSelectAura;
import kamkeel.npcdbc.network.packets.player.aura.DBCSetAura;
import kamkeel.npcdbc.network.packets.player.aura.DBCRequestAura;
import kamkeel.npcdbc.network.packets.get.form.DBCGetForm;
import kamkeel.npcdbc.network.packets.player.form.DBCSelectForm;
import kamkeel.npcdbc.network.packets.player.form.DBCRequestForm;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.player.inventory.GuiCNPCInventory;
import noppes.npcs.client.gui.util.*;
import org.lwjgl.opengl.GL11;
import tconstruct.client.tabs.AbstractTab;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class GuiDBC extends GuiCNPCInventory implements IGuiData, ICustomScrollListener, IScrollData {
    public static int activePage = 0;
    private final ResourceLocation resource = new ResourceLocation("customnpcs", "textures/gui/standardbg.png");
    public int prevAura = 0;
    public int currentAura = 0;
    public int showingAura = 0;
    private GuiFormAuraScroll guiScroll;
    private boolean loaded = false;
    private String selected = null;
    private String search = "";
    private PlayerDBCInfo dbcInfo;
    private Form selectedForm;
    private Form viewingForm;
    private ArrayList<String> stackables = new ArrayList<>();
    private Aura selectedAura;
    private Aura viewingAura;
    private HashMap<String, Integer> loadedData = new HashMap<String, Integer>();

    private static DecimalFormat decimalFormat = new DecimalFormat("#.###");

    public GuiDBC() {
        super();
        xSize = 280;
        ySize = 180;
        this.drawDefaultBackground = false;
        title = "";
        if (activePage == 0)
            PacketHandler.Instance.sendToServer(new DBCRequestForm(-1, true,false).generatePacket());
        else
            PacketHandler.Instance.sendToServer(new DBCRequestAura(-1, true).generatePacket());

        this.dbcInfo = PlayerDataUtil.getClientDBCInfo();
        if (dbcInfo != null) {
            showingAura = dbcInfo.currentAura != -1 ? 0 : 1;
            currentAura = dbcInfo.currentAura;
            prevAura = dbcInfo.currentAura;
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        if (guiScroll == null) {
            guiScroll = new GuiFormAuraScroll(this, 0);
            guiScroll.setSize(135, 118);
        }

        GuiNpcButton formsButton = new GuiNpcButton(40, guiLeft + 5, guiTop + 4, "global.customforms");
        formsButton.width = 65;
        formsButton.enabled = activePage != 0;
        formsButton.packedFGColour = 0xffc626;
        this.addButton(formsButton);

        GuiNpcButton auras = new GuiNpcButton(41, guiLeft + 75, guiTop + 4, "global.customauras");
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

        if (activePage == 1) {
            addLabel(new GuiNpcLabel(1,"Revamp Aura", guiLeft + 170, guiTop + ySize - 11 - 24 + 5));
            GuiNpcButton revampAura = new GuiNpcButtonYesNo(5, guiLeft + 250, guiTop + ySize - 11 - 24, ConfigDBCClient.RevampAura);
            revampAura.width = 65;
            this.addButton(revampAura);

            GuiNpcButton hideAura = new GuiNpcButton(3, guiLeft + 250, guiTop + ySize - 11, new String[]{"aura.shown", "aura.hidden"}, showingAura);
            hideAura.width = 65;
            this.addButton(hideAura);
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
        guiScroll.setSelected(selected);
    }

    private void renderScreen() {
        drawGradientRect(guiLeft + 140, guiTop + 4, guiLeft + xSize + 36, guiTop + 24, 0xC0101010, 0xC0101010);
        drawHorizontalLine(guiLeft + 140, guiLeft + xSize + 35, guiTop + 25, 0xFF000000 + CustomNpcResourceListener.DefaultTextColor);
        drawGradientRect(guiLeft + 140, guiTop + 27, guiLeft + xSize + 36, guiTop + ySize - 14, 0xA0101010, 0xA0101010);
        drawGradientRect(guiLeft + 140, guiTop + ySize - 12, guiLeft + xSize + 36, guiTop + ySize + 9, 0xC0101010, 0xC0101010);
        if (activePage == 0 && loaded) {
            String drawString = "";
            if (viewingForm != null) {
                drawString = viewingForm.getMenuName();
            }
            int textWidth = getStringWidthWithoutColor(drawString);
            int centerX = guiLeft + 140 + ((xSize - textWidth + 30 - 140) / 2); // Adjusted centerX calculation
            fontRendererObj.drawString(drawString, centerX, guiTop + 10, CustomNpcResourceListener.DefaultTextColor, true);
            if (viewingForm != null) {
                DBCData dbcData = DBCData.getClient();
                int race = dbcData.Race;
                int y = guiTop + 18;
                if (viewingForm.requiredForm.containsKey(race)) {
                    String parent = "§fPrev: " + DBCUtils.getFormattedStateName(race, viewingForm.requiredForm.get(race));
                    parent = Utility.removeBoldColorCode(parent);
                    fontRendererObj.drawString(parent, guiLeft + 143, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
                } else if (viewingForm.hasParent() && viewingForm.getParent() != null) {
                    String parent = "§fPrev: " + viewingForm.getParent().getMenuName();
                    parent = Utility.removeBoldColorCode(parent);
                    fontRendererObj.drawString(parent, guiLeft + 143, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
                }
                if (viewingForm.hasChild() && viewingForm.getChild() != null) {
                    String child = "§fNext: " + viewingForm.getChild().getMenuName();
                    child = Utility.removeBoldColorCode(child);
                    fontRendererObj.drawString(child, guiLeft + 143, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
                }


                double masteryMulti = viewingForm.mastery.calculateMulti("attribute", dbcInfo.getFormLevel(viewingForm.id));

                int stats = guiTop + 18 + 48;
                String label = "§f" + StatCollector.translateToLocal("npcdbc.inventory.strength") + ":";
                String info = "§4x§c" + decimalFormat.format(viewingForm.strengthMulti * masteryMulti);
                fontRendererObj.drawString(label, guiLeft + 143, stats, CustomNpcResourceListener.DefaultTextColor, true);
                fontRendererObj.drawString(info, guiLeft + 200, stats, CustomNpcResourceListener.DefaultTextColor, true);

                label = "§f" + StatCollector.translateToLocal("npcdbc.inventory.dexterity") + ":";
                info = "§3x§b" + decimalFormat.format(viewingForm.dexMulti * masteryMulti);
                fontRendererObj.drawString(label, guiLeft + 143, stats += 12, CustomNpcResourceListener.DefaultTextColor, true);
                fontRendererObj.drawString(info, guiLeft + 200, stats, CustomNpcResourceListener.DefaultTextColor, true);

                label = "§f" + StatCollector.translateToLocal("npcdbc.inventory.willpower") + ":";
                info = "§6x§e" + decimalFormat.format(viewingForm.willMulti * masteryMulti);
                fontRendererObj.drawString(label, guiLeft + 143, stats += 12, CustomNpcResourceListener.DefaultTextColor, true);
                fontRendererObj.drawString(info, guiLeft + 200, stats, CustomNpcResourceListener.DefaultTextColor, true);

                if (this.dbcInfo != null && this.dbcInfo.formLevels.containsKey(viewingForm.id)) {
                    label = "§f" + StatCollector.translateToLocal("npcdbc.inventory.mastery") + ":";
                    double masteryValue = this.dbcInfo.formLevels.get(viewingForm.id);
                    String roundedMastery = String.format("%.2f", masteryValue);
                    info = "§a" + roundedMastery + " §7/ §a" + viewingForm.mastery.maxLevel;
                    fontRendererObj.drawString(label, guiLeft + 143, stats += 12, CustomNpcResourceListener.DefaultTextColor, true);
                    fontRendererObj.drawString(info, guiLeft + 200, stats, CustomNpcResourceListener.DefaultTextColor, true);
                }
                if (!stackables.isEmpty()) {
                    label = "§f" + StatCollector.translateToLocal("npcdbc.inventory.stackable") + ":";
                    info = String.join("§7, ", stackables);
                    fontRendererObj.drawString(label, guiLeft + 143, stats += 12, CustomNpcResourceListener.DefaultTextColor, true);
                    fontRendererObj.drawString(info, guiLeft + 200, stats, CustomNpcResourceListener.DefaultTextColor, true);
                }
                if (viewingForm.mastery.hasDodge()) {
                    label = "§f" + StatCollector.translateToLocal("npcdbc.inventory.dodge") + ":";
                    double dodgeChance = viewingForm.mastery.getDodgeChance();
                    double dodgeMultiplier = viewingForm.mastery.calculateMulti("dodge", dbcInfo.getFormLevel(viewingForm.id));
                    double result = dodgeChance * dodgeMultiplier;

                    String resultString = String.format("%.1f", result);
                    info = String.join("§6, ", resultString + "%");
                    fontRendererObj.drawString(label, guiLeft + 143, stats += 12, CustomNpcResourceListener.DefaultTextColor, true);
                    fontRendererObj.drawString("§6" + info, guiLeft + 200, stats, CustomNpcResourceListener.DefaultTextColor, true);
                }
            }
            if (selectedForm != null) {
                String drawSelected = "§f" + StatCollector.translateToLocal("npcdbc.inventory.selected") + ": " + selectedForm.getMenuName();
                fontRendererObj.drawString(drawSelected, guiLeft + 145, guiTop + ySize - 5, CustomNpcResourceListener.DefaultTextColor, true);
            }
        } else if (loaded) {
            String drawString = "§f" + StatCollector.translateToLocal("npcdbc.inventory.noauraselected");
            if (viewingAura != null) {
                drawString = viewingAura.getMenuName();
            }
            int textWidth = getStringWidthWithoutColor(drawString);
            int centerX = guiLeft + 140 + ((xSize - textWidth + 30 - 140) / 2);
            fontRendererObj.drawString(drawString, centerX, guiTop + 10, CustomNpcResourceListener.DefaultTextColor, true);
            if (viewingAura != null) {

            }
            if (selectedAura != null) {
                String drawSelected = selectedAura.getMenuName();
                fontRendererObj.drawString(drawSelected, guiLeft + 145, guiTop + ySize - 5, CustomNpcResourceListener.DefaultTextColor, true);
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
        if (guibutton.id == 40 && activePage != 0) {
            activePage = 0;
            PacketHandler.Instance.sendToServer(new DBCRequestForm(-1, true,false).generatePacket());
            loaded = false;
        }
        if (guibutton.id == 41 && activePage != 1) {
            activePage = 1;
            PacketHandler.Instance.sendToServer(new DBCRequestAura(-1, true).generatePacket());
            loaded = false;
        }

        if (activePage == 0) {
            if (guibutton.id == 1) {
                if (selected != null) {
                    if (loadedData.containsKey(selected)) {
                        int formID = loadedData.get(selected);
                        PacketHandler.Instance.sendToServer(new DBCSelectForm(formID, false).generatePacket());
                        loaded = false;
                    }
                }
            } else if (guibutton.id == 2) {
                PacketHandler.Instance.sendToServer(new DBCSelectForm(-1, false).generatePacket());
                selected = null;
                guiScroll.selected = -1;
                loaded = false;
            }
        } else {
            if (guibutton.id == 1) {
                if (selected != null) {
                    if (loadedData.containsKey(selected)) {
                        int auraID = loadedData.get(selected);
                        PacketHandler.Instance.sendToServer(new DBCSelectAura(auraID).generatePacket());
                        currentAura = auraID;
                        showingAura = 0;
                        loaded = false;
                    }
                }
            } else if (guibutton.id == 2) {
                PacketHandler.Instance.sendToServer(new DBCSelectAura(-1).generatePacket());
                selected = null;
                guiScroll.selected = -1;
                currentAura = -1;
                showingAura = 1;
                loaded = false;
            } else if (guibutton.id == 3) {
                showingAura = ((GuiNpcButton) guibutton).getValue();
                if (showingAura == 0) {
                    // Show
                    if (viewingAura != null) {
                        currentAura = viewingAura.id;
                    }
                } else {
                    // Hide
                    currentAura = -1;
                }
            }
            else if (guibutton.id == 5) {
                ConfigDBCClient.RevampAura = ((GuiNpcButton) guibutton).getValue() == 1;
                ConfigDBCClient.RevampAuraProperty.set(ConfigDBCClient.RevampAura);
                ConfigDBCClient.config.save();
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
            if (viewingForm != null)
                setSelected(viewingForm.name);
            else if (viewingAura != null)
                setSelected(viewingAura.name);
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
            this.viewingAura = null;
            this.viewingForm = null;
            this.selectedAura = null;
            this.selectedForm = null;
            selected = null;
            setSelected(selected);
            registerStackables();
        } else if (compound.hasKey("Skip")) {
        } else if (compound.hasKey("Type") && compound.getString("Type").equals("ViewAura")) {
            this.viewingAura = new Aura();
            this.viewingForm = null;
            viewingAura.readFromNBT(compound);
            setSelected(viewingAura.name);
            registerStackables();
        } else if (compound.hasKey("Type") && compound.getString("Type").equals("ViewForm")) {
            this.viewingForm = new Form();
            this.viewingAura = null;
            viewingForm.readFromNBT(compound);
            setSelected(viewingForm.name);
            registerStackables();
        } else if (compound.hasKey("attributes")) {
            this.viewingForm = new Form();
            this.selectedForm = new Form();
            this.viewingAura = null;
            this.selectedAura = null;
            selectedForm.readFromNBT(compound);
            viewingForm.readFromNBT(compound);
            setSelected(viewingForm.name);
            registerStackables();
        } else {
            this.selectedAura = new Aura();
            this.viewingAura = new Aura();
            this.selectedForm = null;
            this.viewingForm = null;
            selectedAura.readFromNBT(compound);
            viewingAura.readFromNBT(compound);
            setSelected(viewingAura.name);
            registerStackables();
        }
        loaded = true;
        initGui();
    }

    @Override
    public void customScrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        if (guiScroll != null && guiCustomScroll.id == guiScroll.id) {
            if (activePage == 0) {
                if (selected != null && selected.equals(guiScroll.getSelected())) {
                    selected = "";
                    guiScroll.selected = -1;
                    viewingForm = null;
                    return;
                }
                selected = guiScroll.getSelected();
                PacketHandler.Instance.sendToServer(new DBCGetForm(loadedData.get(selected)).generatePacket());
            } else {
                if (selected != null && selected.equals(guiScroll.getSelected())) {
                    selected = "";
                    guiScroll.selected = -1;
                    viewingAura = null;
                    return;
                }
                selected = guiScroll.getSelected();
                PacketHandler.Instance.sendToServer(new DBCGetAura(loadedData.get(selected)).generatePacket());
            }
        }
    }

    @Override
    public void customScrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }

    public void close() {
        if (prevAura != currentAura)
            PacketHandler.Instance.sendToServer(new DBCSetAura(currentAura).generatePacket());
        super.close();
    }

    public void registerStackables() {
        stackables.clear();
        if (viewingForm != null) {
            if (viewingForm.stackable.vanillaStackable) {
                stackables.add("§e"+StatCollector.translateToLocal("npcdbc.inventory.stackable.dbc"));
            }
            if (viewingForm.stackable.kaiokenStackable) {
                stackables.add("§c"+StatCollector.translateToLocal("npcdbc.inventory.stackable.kk"));
            }
            if (viewingForm.stackable.uiStackable) {
                stackables.add("§7"+StatCollector.translateToLocal("npcdbc.inventory.stackable.ui"));
            }
            if (viewingForm.stackable.godStackable) {
                stackables.add("§5"+StatCollector.translateToLocal("npcdbc.inventory.stackable.godofdestruction"));
            }
            if (viewingForm.stackable.mysticStackable) {
                stackables.add("§d"+StatCollector.translateToLocal("npcdbc.inventory.stackable.mystic"));
            }
        }
    }
}
