package kamkeel.npcdbc.client.gui.global.outline;

import kamkeel.npcdbc.client.gui.component.SubGuiSelectOutline;
import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.get.outline.DBCGetOutline;
import kamkeel.npcdbc.network.packets.request.outline.DBCRemoveOutline;
import kamkeel.npcdbc.network.packets.player.outline.DBCRequestOutline;
import kamkeel.npcdbc.network.packets.request.outline.DBCSaveOutline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.select.GuiSoundSelection;
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

public class GuiNPCManageOutlines extends GuiNPCInterface2 implements ICustomScrollListener, IScrollData, IGuiData, ISubGuiListener, GuiYesNoCallback, ITextfieldListener {
    public GuiCustomScroll scrollOutlines;
    public HashMap<String, Integer> data = new HashMap<>();
    public Outline outline = new Outline();
    public String selected = null;
    public String search = "";
    public String originalName = "";
    public DBCDisplay visualDisplay;
    boolean setNormalSound = true;
    private float zoomed = 70.0F, rotation;

    public GuiNPCManageOutlines(EntityNPCInterface npc) {
        super(npc);
        this.npc = DBCDisplay.setupGUINPC((EntityCustomNpc) npc);
        this.npc.display.name = "outline man";


        visualDisplay = ((INPCDisplay) this.npc.display).getDBCDisplay();
        visualDisplay.auraOn = false;
        visualDisplay.formID = -1;

        DBCPacketHandler.Instance.sendToServer(new DBCRequestOutline(-1));
    }

    public void initGui() {
        super.initGui();
        addButton(new GuiNpcButton(0, guiLeft + 368, guiTop + 8, 45, 20, "gui.add"));

        addButton(new GuiNpcButton(1, guiLeft + 368, guiTop + 32, 45, 20, "gui.remove"));
        getButton(1).enabled = outline != null && outline.id != -1;

        addButton(new GuiNpcButton(2, guiLeft + 368, guiTop + 56, 45, 20, "gui.clone"));
        getButton(2).enabled = outline != null && outline.id != -1;

        addButton(new GuiNpcButton(3, guiLeft + 368, guiTop + 80, 45, 20, "gui.edit"));
        getButton(3).enabled = outline != null && outline.id != -1;

        if (scrollOutlines == null) {
            scrollOutlines = new GuiCustomScroll(this, 0, 0);
            scrollOutlines.setSize(143, 185);
        }
        scrollOutlines.guiLeft = guiLeft + 220;
        scrollOutlines.guiTop = guiTop + 4;
        addScroll(scrollOutlines);
        scrollOutlines.setList(getSearchList());

        addTextField(new GuiNpcTextField(55, this, fontRendererObj, guiLeft + 220, guiTop + 4 + 3 + 185, 143, 20, search));
        if (outline != null && outline.id != -1) {
            //   addButton(new GuiNpcButton(1500, guiLeft + 8, guiTop + 192, 203, 20, "display.displaySettings"));
            addLabel(new GuiNpcLabel(10, "ID", guiLeft + 368, guiTop + 4 + 3 + 185));
            addLabel(new GuiNpcLabel(11, outline.id + "", guiLeft + 368, guiTop + 4 + 3 + 195));
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
            DBCPacketHandler.Instance.sendToServer(new DBCSaveOutline(outline.writeToNBT(), ""));
        }

        if (button.id == 1) {
            if (data.containsKey(scrollOutlines.getSelected())) {
                GuiYesNo guiyesno = new GuiYesNo(this, scrollOutlines.getSelected(), StatCollector.translateToLocal("gui.delete"), 1);
                displayGuiScreen(guiyesno);
            }
        }
        if (button.id == 2) {
            Outline outline = (Outline) this.outline.clone();
            while (data.containsKey(outline.name))
                outline.name += "_";
            DBCPacketHandler.Instance.sendToServer(new DBCSaveOutline(outline.writeToNBT(), ""));
        }

        if (button.id == 3) {
            Minecraft.getMinecraft().displayGuiScreen(new SubGuiOutlineDisplay(this, npc, outline));

        }

        if (outline == null)
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
            EntityCustomNpc npc = (EntityCustomNpc) this.npc;
            if (npc == null) {
                npc = new EntityCustomNpc(Minecraft.getMinecraft().theWorld);

            }
            //    Minecraft.getMinecraft().displayGuiScreen(new SubGuiOutlineDisplay(this, npc, outline));
        }
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        this.outline = new Outline();
        outline.readFromNBT(compound);
        setSelected(outline.name);

        if (outline.id != -1) {
            OutlineController.getInstance().customOutlines.replace(outline.id, outline);
        }
        initGui();
    }

    public boolean isMouseOverRenderer(int x, int y) {
        return x >= guiLeft + 10 && x <= guiLeft + 10 + 200 && y >= guiTop + 6 && y <= guiTop + 6 + 204;
    }

    @Override
    public void drawScreen(int i, int j, float f) {
        if (isMouseOverRenderer(i, j)) {
            zoomed += Mouse.getDWheel() * 0.035f;
            if (zoomed > 100)
                zoomed = 100;
            if (zoomed < 10)
                zoomed = 10;
        }

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
        super.drawScreen(i, j, f);

        if (hasSubGui())
            return;

        int outlineID = visualDisplay.outlineID;
        visualDisplay.outlineID = outline.id;
        int hideName = npc.display.showName;
        int size = npc.display.modelSize;
        npc.display.showName = 1;
        npc.display.modelSize = 5;
        GL11.glColor4f(1, 1, 1, 1);
        EntityLivingBase entity = this.npc; // DBCData.getClient().player;//
        //  entity = DBCData.getClient().player;
        int l = guiLeft + 110;
        int i1 = guiTop + 187;

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
        npc.display.showName = hideName;
        npc.display.modelSize = size;
        visualDisplay.outlineID = outlineID;
    }

    @Override
    public void drawBackground() {
        super.drawBackground();
        int xPosGradient = guiLeft + 10;
        int yPosGradient = guiTop + 6;
        drawGradientRect(xPosGradient, yPosGradient, 200 + xPosGradient, 204 + yPosGradient, 0xc0101010, 0xd0101010);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
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
                scrollOutlines.resetScroll();
                scrollOutlines.setList(getSearchList());
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
        String name = scrollOutlines.getSelected();
        this.data = data;
        scrollOutlines.setList(getSearchList());

        if (name != null)
            scrollOutlines.setSelected(name);
    }

    @Override
    public void setSelected(String selected) {
        this.selected = selected;
        scrollOutlines.setSelected(selected);
        originalName = scrollOutlines.getSelected();
    }

    @Override
    public void customScrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll.id == 0) {
            save();
            selected = scrollOutlines.getSelected();
            originalName = scrollOutlines.getSelected();
            if (selected != null && !selected.isEmpty()) {
                DBCPacketHandler.Instance.sendToServer(new DBCGetOutline(data.get(selected)));
            }
        }
    }

    @Override
    public void customScrollDoubleClicked(String selection, GuiCustomScroll scroll) {
        ICustomScrollListener.super.customScrollDoubleClicked(selection, scroll);
    }

    @Override
    public void save() {
        if (this.selected != null && this.data.containsKey(this.selected) && this.outline != null) {
            DBCPacketHandler.Instance.sendToServer(new DBCSaveOutline(outline.writeToNBT(), originalName));
        }
    }


    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof SubGuiSelectOutline) {
//            if(outline != null){
//                SubGuiSelectOutline guiSelectForm = ((SubGuiSelectOutline)subgui);
//                if(guiSelectForm.confirmed){
//                    if(guiSelectForm.selectedOutlineID == outline.secondaryOutlineID)
//                        return;
//                    outline.secondaryOutlineID = guiSelectForm.selectedOutlineID;
//                }
//            }
        } else if (subgui instanceof GuiSoundSelection) {
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
            if (data.containsKey(scrollOutlines.getSelected())) {
                DBCPacketHandler.Instance.sendToServer(new DBCRemoveOutline(data.get(scrollOutlines.getSelected())));
                scrollOutlines.clear();
                outline = new Outline();

                visualDisplay.outlineID = -1;
                initGui();
            }
        }
    }

    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {
        if (outline == null || outline.id == -1)
            return;
        if (guiNpcTextField.id == 13) {
            String name = guiNpcTextField.getText();
            if (!name.isEmpty() && !this.data.containsKey(name)) {
                String old = this.outline.name;
                this.data.remove(this.outline.name);
                this.outline.name = name;
                this.data.put(this.outline.name, this.outline.id);
                this.selected = name;
                this.scrollOutlines.replace(old, this.outline.name);
            } else
                guiNpcTextField.setText(outline.name);
        }
        if (guiNpcTextField.id == 14) {
            String menuName = guiNpcTextField.getText();
            if (!menuName.isEmpty()) {
                outline.menuName = menuName.replaceAll("&", "§");
            }
        }

    }
}
