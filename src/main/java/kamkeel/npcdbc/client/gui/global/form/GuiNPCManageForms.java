package kamkeel.npcdbc.client.gui.global.form;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
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
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class GuiNPCManageForms extends GuiNPCInterface2 implements ICustomScrollListener, IScrollData, IGuiData, ISubGuiListener, GuiYesNoCallback {
    public GuiCustomScroll scrollForms;
    public HashMap<String, Integer> data = new HashMap<>();
    private final ArrayList<String> stackables = new ArrayList<>();
    private String selected = null;
    private String search = "";

    public Form form = new Form();
    public int parentForm = -1;
    public int childForm = -1;

    public FormDisplay display;
    public DBCDisplay visualDisplay;
    public int originalRace;

    private float zoomed = 70, rotation;


    public GuiNPCManageForms(EntityNPCInterface npc) {
        super(npc);
        this.npc = DBCDisplay.setupGUINPC((EntityCustomNpc) npc);
        this.npc.display.name = "form man";
        this.npc.height = 1.62f;
        this.npc.width = 0.43f;
        visualDisplay = ((INPCDisplay) this.npc.display).getDBCDisplay();

        visualDisplay.auraID = -1;
        visualDisplay.outlineID = -1;
        visualDisplay.arcoState = 0;
        visualDisplay.setDefaultColors();
        visualDisplay.setRacialExtras();
        visualDisplay.setDefaultHair();
        originalRace = visualDisplay.race;

        this.display = form.display;

        PacketHandler.Instance.sendToServer(new DBCRequestForm(-1, false).generatePacket());
    }

    public void initGui() {
        super.initGui();
        addButton(new GuiNpcButton(0, guiLeft + 368, guiTop + 8, 45, 20, "gui.add"));

        addButton(new GuiNpcButton(1, guiLeft + 368, guiTop + 32, 45, 20, "gui.remove"));
        getButton(1).enabled = form != null && form.id != -1;

        addButton(new GuiNpcButton(3, guiLeft + 368, guiTop + 56, 45, 20, "gui.clone"));
        getButton(3).enabled = form != null && form.id != -1;

        addButton(new GuiNpcButton(2, guiLeft + 368, guiTop + 80, 45, 20, "gui.edit"));
        getButton(2).enabled = form != null && form.id != -1;

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
        if (form != null && form.id != -1) {
            addLabel(new GuiNpcLabel(10, "ID", guiLeft + 368, guiTop + 4 + 3 + 185));
            addLabel(new GuiNpcLabel(11, form.id + "", guiLeft + 368, guiTop + 4 + 3 + 195));
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
            Form form = new Form(-1, name);
            PacketHandler.Instance.sendToServer(new DBCSaveForm(form.writeToNBT()).generatePacket());
        }
        if (button.id == 1) {
            if (data.containsKey(scrollForms.getSelected())) {
                GuiYesNo guiyesno = new GuiYesNo(this, scrollForms.getSelected(), StatCollector.translateToLocal("gui.delete"), 1);
                displayGuiScreen(guiyesno);
            }
        }
        if (button.id == 2) {
            if (data.containsKey(scrollForms.getSelected()) && form != null && form.id >= 0) {
                setSubGui(new SubGuiFormGeneral(this, form));
            }
        }
        if (button.id == 3) {
            Form form = (Form) this.form.clone();
            while (data.containsKey(form.name))
                form.name += "_";
            PacketHandler.Instance.sendToServer(new DBCSaveForm(form.writeToNBT()).generatePacket());
        }

    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        int oldID = form != null ? form.id : -1;

        this.form = new Form();
        form.readFromNBT(compound);
        parentForm = form.parentID;
        childForm = form.childID;
        setSelected(form.name);

        if (form.id != oldID && form.id != -1) {
            FormController.getInstance().customForms.replace(form.id, form);
            display = form.display;
            visualDisplay.formID = form.id;
            if (form.race() != -1)
                visualDisplay.race = (byte) form.race();
            else
                visualDisplay.race = (byte) originalRace;

            visualDisplay.setDefaultColors();
            visualDisplay.setRacialExtras();
            visualDisplay.setDefaultHair();
        }
        initGui();
    }

    public boolean isMouseOverRenderer(int x, int y) {
        return x >= guiLeft + 10 && x <= guiLeft + 10 + 200 && y >= guiTop + 6 && y <= guiTop + 6 + 204;
    }

    @Override
    public void drawScreen(int i, int j, float f) {
        super.drawScreen(i, j, f);

        if (hasSubGui() || form.id == -1)
            return;

        if (isMouseOverRenderer(i, j)) {
            zoomed += Mouse.getDWheel() * 0.035f;
            if (zoomed > 100)
                zoomed = 100;
            if (zoomed < 10)
                zoomed = 10;

            if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
                rotation -= Mouse.getDX() * 0.75f;
            }
        }

        GL11.glColor4f(1, 1, 1, 1);

        EntityLivingBase entity = this.npc;

        int l = guiLeft + 150;
        int i1 = guiTop + 198;


        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef(l, i1, 60F);

        GL11.glScalef(-zoomed, zoomed, zoomed);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f7 = entity.rotationYawHead;
        float f5 = (float) (l) - i;
        float f6 = (float) (i1 - 50) - j;
        GL11.glRotatef(135F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-(float) Math.atan(f6 / 800F) * 20F, 1.0F, 0.0F, 0.0F);
        entity.prevRenderYawOffset = entity.renderYawOffset = rotation;
        entity.prevRotationYaw = entity.rotationYaw = (float) Math.atan(f5 / 80F) * 40F + rotation;
        entity.rotationPitch = -(float) Math.atan(f6 / 80F) * 20F;
        entity.prevRotationYawHead = entity.rotationYawHead = entity.rotationYaw;
        GL11.glTranslatef(0.0F, entity.yOffset, 1F);
        RenderManager.instance.playerViewY = 180F;


        // Render Entity
        try {
            RenderManager.instance.renderEntityWithPosYaw(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F);
        } catch (Exception ignored) {
        }

        entity.prevRenderYawOffset = entity.renderYawOffset = f2;
        entity.prevRotationYaw = entity.rotationYaw = f3;
        entity.rotationPitch = f4;
        entity.prevRotationYawHead = entity.rotationYawHead = f7;

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPopMatrix();


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
        if (form != null && form.id != -1) {
            String drawString = form.getMenuName();
            int textWidth = getStringWidthWithoutColor(drawString);
            int centerX = guiLeft + 5 + ((218 - 10 - textWidth) / 2); // Adjusted centerX calculation
            fontRendererObj.drawString(drawString, centerX, guiTop + 10, CustomNpcResourceListener.DefaultTextColor, true);
            int y = guiTop + 18;

            String race = "§fRace: §e" + (form.getRace() == -1 ? "All" : form.getRace() == DBCRace.ALL_SAIYANS ? "All Saiyans" : form.getRace() == 1 ? "Pure Saiyan" : JRMCoreH.Races[form.race()]);
            fontRendererObj.drawString(race, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);

            String label = "";
            String info = "";

            y += 6;
            if (!stackables.isEmpty()) {
                label = "§fStackable:";
                info = String.join("§7, ", stackables);
                // fontRendererObj.drawString(label, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
                //   fontRendererObj.drawString(info, guiLeft + 65, y, CustomNpcResourceListener.DefaultTextColor, true);
            }
            label = "§fSTR:";
            info = "§4x§c" + form.strengthMulti;
            fontRendererObj.drawString(label, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
            fontRendererObj.drawString(info, guiLeft + 32, y, CustomNpcResourceListener.DefaultTextColor, true);

            label = "§fDEX:";
            info = "§3x§b" + form.dexMulti;
            fontRendererObj.drawString(label, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
            fontRendererObj.drawString(info, guiLeft + 32, y, CustomNpcResourceListener.DefaultTextColor, true);

            label = "§fWIL:";
            info = "§6x§e" + form.willMulti;
            fontRendererObj.drawString(label, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
            fontRendererObj.drawString(info, guiLeft + 32, y, CustomNpcResourceListener.DefaultTextColor, true);

            y += 6;
            if (!form.requiredForm.isEmpty() || form.hasParent() || form.hasChild()) {
                String parent = "§f------- Links -------";
                // fontRendererObj.drawString(parent, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
            }
            if (form.hasChild() && form.getChild() != null) {
                String child = "§fChild: ";
                String childName = "§7" + form.getChild().getName();
                fontRendererObj.drawString(child, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
                fontRendererObj.drawString(childName, guiLeft + 50, y, CustomNpcResourceListener.DefaultTextColor, true);
            }
            if (form.race() != -1) {
                if (form.requiredForm.containsKey(form.race())) {
                    String parent = "§fParent: ";
                    String parentName = Utility.removeBoldColorCode(DBCUtils.getFormattedStateName(form.race(), form.requiredForm.get(form.race())));
                    fontRendererObj.drawString(parent, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
                    fontRendererObj.drawString(parentName, guiLeft + 50, y, CustomNpcResourceListener.DefaultTextColor, true);
                } else if (form.hasParent() && form.getParent() != null) {
                    String parent = "§fParent: ";
                    String parentName = "§7" + form.getParent().getName();
                    fontRendererObj.drawString(parent, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
                    fontRendererObj.drawString(parentName, guiLeft + 50, y, CustomNpcResourceListener.DefaultTextColor, true);
                }
            } else if (!form.requiredForm.isEmpty() || (form.hasParent() && form.getParent() != null)) {
                if (form.hasParent() && form.getParent() != null) {
                    String parent = "§fParent: ";
                    String parentName = "§7" + form.getParent().getName();
                    fontRendererObj.drawString(parent, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
                    fontRendererObj.drawString(parentName, guiLeft + 50, y, CustomNpcResourceListener.DefaultTextColor, true);
                }
                if (!form.requiredForm.isEmpty()) {
                    String ok = "§f-- Parent Forms --";
                    fontRendererObj.drawString(ok, guiLeft + 8, y += 12, CustomNpcResourceListener.DefaultTextColor, true);
                    for (int i = 0; i < 6; i++) {
                        if (form.requiredForm.containsKey(i)) {
                            String parent = "§f" + JRMCoreH.Races[i] + "§f: ";
                            String parentName = Utility.removeBoldColorCode(DBCUtils.getFormattedStateName(i, form.requiredForm.get(i)));
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

        if (form.id != -1) {
            FormController.getInstance().customForms.replace(form.id, form);
        }
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
            if (selected != null && !selected.isEmpty())
                PacketHandler.Instance.sendToServer(new DBCGetForm(data.get(selected)).generatePacket());
        }
    }

    @Override
    public void save() {
    }

    public void registerStackables() {
        stackables.clear();
        if (form != null) {
            if (form.stackable.vanillaStackable) {
                stackables.add("§eDBC");
            }
            if (form.stackable.kaiokenStackable) {
                stackables.add("§cKK");
            }
            if (form.stackable.uiStackable) {
                stackables.add("§7UI");
            }
            if (form.stackable.godStackable) {
                stackables.add("§5GoD");
            }
            if (form.stackable.mysticStackable) {
                stackables.add("§dMystic");
            }
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
            if (data.containsKey(scrollForms.getSelected())) {
                PacketHandler.Instance.sendToServer(new DBCRemoveForm(data.get(scrollForms.getSelected())).generatePacket());
                scrollForms.clear();
                form = new Form();

                visualDisplay.formID = -1;
                initGui();
            }
        }
    }
}
