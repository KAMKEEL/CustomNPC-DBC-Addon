package kamkeel.npcdbc.client.gui.global.ability;

import kamkeel.npcdbc.controllers.AbilityController;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.get.ability.DBCGetAbility;
import kamkeel.npcdbc.network.packets.player.ability.DBCRequestAbility;
import kamkeel.npcdbc.network.packets.request.ability.DBCRemoveAbility;
import kamkeel.npcdbc.network.packets.request.ability.DBCSaveAbility;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.ClientCacheHandler;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.client.renderer.ImageData;
import noppes.npcs.constants.EnumScrollData;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import static noppes.npcs.client.gui.player.inventory.GuiCNPCInventory.specialIcons;

public class GuiNPCManageAbilities extends GuiNPCInterface2 implements ICustomScrollListener, IScrollData, IGuiData, ISubGuiListener, GuiYesNoCallback {
    public GuiCustomScroll scrollAbilities;
    public HashMap<String, Integer> data = new HashMap<>();
    public Ability ability = new Ability();
    public String selected = null;
    public String search = "";
    public String originalName = "";

    public GuiNPCManageAbilities(EntityNPCInterface npc) {
        super(npc);

        DBCPacketHandler.Instance.sendToServer(new DBCRequestAbility(-1, false, false));
    }

    public void initGui() {
        super.initGui();
        addButton(new GuiNpcButton(0, guiLeft + 368, guiTop + 8, 45, 20, "gui.add"));

        addButton(new GuiNpcButton(1, guiLeft + 368, guiTop + 32, 45, 20, "gui.remove"));
        getButton(1).enabled = ability != null && ability.id != -1;

        addButton(new GuiNpcButton(2, guiLeft + 368, guiTop + 56, 45, 20, "gui.copy"));
        getButton(2).enabled = ability != null && ability.id != -1;

        addButton(new GuiNpcButton(3, guiLeft + 368, guiTop + 80, 45, 20, "gui.edit"));
        getButton(3).enabled = ability != null && ability.id != -1;

        if (scrollAbilities == null) {
            scrollAbilities = new GuiCustomScroll(this, 0, 0);
            scrollAbilities.setSize(143, 185);
        }
        scrollAbilities.guiLeft = guiLeft + 220;
        scrollAbilities.guiTop = guiTop + 4;
        addScroll(scrollAbilities);
        scrollAbilities.setList(getSearchList());

        addTextField(new GuiNpcTextField(55, this, fontRendererObj, guiLeft + 220, guiTop + 4 + 3 + 185, 143, 20, search));
        if (ability != null && ability.id != -1) {
            addLabel(new GuiNpcLabel(10, "ID", guiLeft + 368, guiTop + 4 + 3 + 185));
            addLabel(new GuiNpcLabel(11, ability.id + "", guiLeft + 368, guiTop + 4 + 3 + 195));
        }
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


        if (ability == null)
            return;
        if (ability.id == -1)
            return;

        String drawString = ability.getMenuName();
        int textWidth = getStringWidthWithoutColor(drawString);
        int centerX = guiLeft + 5 + ((218 - 10 - textWidth) / 2); // Adjusted centerX calculation
        fontRendererObj.drawString(drawString, centerX, guiTop + 10, CustomNpcResourceListener.DefaultTextColor, true);
        int y = guiTop + 33;
        int x = guiLeft + 12;

        int iconRenderSize = 48;

        TextureManager textureManager = mc.getTextureManager();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        ImageData data = ClientCacheHandler.getImageData(ability.icon);
        if (data.imageLoaded()) {
            data.bindTexture();
            int iconX = ability.iconX;
            int iconY = ability.iconY;
            int iconWidth = ability.getWidth();
            int iconHeight = ability.getHeight();
            int width = data.getTotalWidth();
            int height = data.getTotalWidth();


            func_152125_a(x, y, iconX, iconY, iconWidth, iconHeight, iconRenderSize, iconRenderSize, width, height);

        } else {
            textureManager.bindTexture(new ResourceLocation("customnpcs", "textures/marks/question.png"));
            func_152125_a(x, y, 0, 0, 1, 1, iconRenderSize, iconRenderSize, 1, 1);
        }
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        textureManager.bindTexture(specialIcons);
        func_152125_a(x, y, 0, 224, 16, 16, iconRenderSize, iconRenderSize, 256, 256);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        x += iconRenderSize + 3;
        y += 2;

        String translated = StatCollector.translateToLocal("gui.name") + ": " + ability.name;
        fontRendererObj.drawString(translated, x, y, 0xFFFFFF, false);
        int transLength = getStringWidthWithoutColor(translated);
        fontRendererObj.drawString(" (ID: " + ability.id + ")", x + transLength, y, 0xB5B5B5, false);
        y += 12;

        fontRendererObj.drawString(StatCollector.translateToLocal("general.menuName") + ": " + ability.menuName, x, y, 0xFFFFFF, false);
        y += 12;

        fontRendererObj.drawString(StatCollector.translateToLocal("ability.type") + ": §a" + StatCollector.translateToLocal(getTypeName(ability.type)), x, y, 0xFFFFFF, false);
        y += 12;

        fontRendererObj.drawString(StatCollector.translateToLocal("ability.cooldown") + ": " + (ability.cooldown <= -1 ? "None" : ability.cooldown), x, y, 0xFFFFFF, false);
        y += 12;

        fontRendererObj.drawString(StatCollector.translateToLocal("ability.kiCost") + ": §b" + (ability.kiCost <= -1 ? "None" : ability.kiCost), x, y, 0xFFFFFF, false);
        y += 12;
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (button.id == 0) {
            save();
            String name = "New";
            while (data.containsKey(name))
                name += "_";
            Ability ability = new Ability(-1, name);
            DBCPacketHandler.Instance.sendToServer(new DBCSaveAbility(ability.writeToNBT(false), ""));
        } else if (button.id == 1) {
            if (data.containsKey(scrollAbilities.getSelected())) {
                GuiYesNo guiyesno = new GuiYesNo(this, scrollAbilities.getSelected(), StatCollector.translateToLocal("gui.delete"), 1);
                displayGuiScreen(guiyesno);
            }
        } else if (button.id == 2) {
            Ability ability = this.ability.cloneAbility();
            while (data.containsKey(ability.name))
                ability.name += "_";
            DBCPacketHandler.Instance.sendToServer(new DBCSaveAbility(ability.writeToNBT(false), ""));
        }

        if (ability == null)
            return;

        if (button.id == 3) {
            if (data.containsKey(scrollAbilities.getSelected()) && ability != null && ability.id >= 0) {
                //setSubGui(new SubGuiAbilityGeneral(this, ability));
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
    public void setData(Vector<String> list, HashMap<String, Integer> data, EnumScrollData type) {
        String name = scrollAbilities.getSelected();
        this.data = data;
        scrollAbilities.setList(getSearchList());

        if (name != null)
            scrollAbilities.setSelected(name);
    }

    @Override
    public void setSelected(String selected) {
        this.selected = selected;
        scrollAbilities.setSelected(selected);
        originalName = scrollAbilities.getSelected();
    }

    @Override
    public void customScrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll.id == 0) {
            save();
            ability = null;
            selected = scrollAbilities.getSelected();
            originalName = scrollAbilities.getSelected();
            if (selected != null && !selected.isEmpty()) {
                DBCPacketHandler.Instance.sendToServer(new DBCGetAbility(data.get(selected)));
            }
        }
    }

    @Override
    public void save() {
        if (this.selected != null && this.data.containsKey(this.selected) && this.ability != null) {
            DBCPacketHandler.Instance.sendToServer(new DBCSaveAbility(ability.writeToNBT(false), originalName));
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        NoppesUtil.openGUI(player, this);
        if (!result)
            return;
        if (id == 1) {
            if (data.containsKey(scrollAbilities.getSelected())) {
                DBCPacketHandler.Instance.sendToServer(new DBCRemoveAbility(data.get(scrollAbilities.getSelected())));
                scrollAbilities.clear();
                ability = new Ability();
                initGui();
            }
        }
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        this.ability = new Ability();
        ability.readFromNBT(compound);
        setSelected(ability.name);
        if (ability.id != -1) {
            AbilityController.getInstance().abilities.replace(ability.id, ability);
        }
        initGui();
    }

    @Override
    public void subGuiClosed(SubGuiInterface subGuiInterface) {

    }

    private int getStringWidthWithoutColor(String text) {
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

    private String getTypeName(Ability.Type type) {
        if (type == Ability.Type.Active)
            return "ability.active";
        else
            return "ability.toggle";
    }
}
