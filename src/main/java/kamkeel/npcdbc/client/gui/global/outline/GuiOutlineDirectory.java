package kamkeel.npcdbc.client.gui.global.outline;

import noppes.npcs.client.gui.util.GuiDirectoryCategorized;
import net.minecraft.client.gui.GuiScreen;
import kamkeel.npcdbc.constants.DBCSyncType;
import kamkeel.npcdbc.controllers.OutlineController;
import net.minecraft.client.Minecraft;
import noppes.npcs.controllers.data.Category;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.get.outline.DBCGetOutline;
import kamkeel.npcdbc.network.packets.request.category.DBCCategoryMoveItem;
import kamkeel.npcdbc.network.packets.request.category.DBCCategoryRemove;
import kamkeel.npcdbc.network.packets.request.category.DBCCategorySave;
import kamkeel.npcdbc.network.packets.request.category.DBCRequestCategories;
import kamkeel.npcdbc.network.packets.request.category.DBCRequestCategoryItems;
import kamkeel.npcdbc.network.packets.request.outline.DBCRemoveOutline;
import kamkeel.npcdbc.network.packets.request.outline.DBCSaveOutline;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.HashMap;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiOutlineDirectory extends GuiDirectoryCategorized implements IOutlineManagerGui {
    public Outline outline = new Outline();
    public DBCDisplay visualDisplay;
    public EntityNPCInterface npc;
    private EntityNPCInterface originalNpc;

    public GuiOutlineDirectory(EntityNPCInterface npc) {
        super();
        this.originalNpc = npc;
        this.npc = DBCDisplay.setupGUINPC((EntityCustomNpc) npc);
        this.npc.display.name = "outline man";
        this.npc.height = 1.62f;
        this.npc.width = 0.43f;
        visualDisplay = ((INPCDisplay) this.npc.display).getDBCDisplay();
        visualDisplay.auraID = -1;
        visualDisplay.outlineID = -1;
        visualDisplay.setDefaultColors();
        visualDisplay.setRacialExtras();
        visualDisplay.setDefaultHair();
        zoomed = 70;
    }

    @Override
    protected String getTitle() { return "Outlines"; }

    @Override
    protected void requestCategoryList() {
        DBCPacketHandler.Instance.sendToServer(new DBCRequestCategories(DBCSyncType.OUTLINE));
    }

    @Override
    protected void requestItemsInCategory(int catId) {
        DBCPacketHandler.Instance.sendToServer(new DBCRequestCategoryItems(DBCSyncType.OUTLINE, catId));
    }

    @Override
    protected void requestItemData(int itemId) {
        DBCPacketHandler.Instance.sendToServer(new DBCGetOutline(itemId));
    }

    @Override
    protected void onSaveCategory(Category cat) {
        DBCPacketHandler.Instance.sendToServer(new DBCCategorySave(DBCSyncType.OUTLINE, cat.writeNBT(new NBTTagCompound())));
    }

    @Override
    protected void onRemoveCategory(int catId) {
        DBCPacketHandler.Instance.sendToServer(new DBCCategoryRemove(DBCSyncType.OUTLINE, catId));
    }

    @Override
    protected void onAddItem(int catId) {
        String name = "New";
        while (itemData.containsKey(name)) name += "_";
        Outline newOutline = new Outline();
        newOutline.name = name;
        DBCPacketHandler.Instance.sendToServer(new DBCSaveOutline(newOutline.writeToNBT(), name));
    }

    @Override
    protected void onRemoveItem(int itemId) {
        DBCPacketHandler.Instance.sendToServer(new DBCRemoveOutline(itemId));
        outline = new Outline();
        visualDisplay.outlineID = -1;
    }

    @Override
    protected void onEditItem() {
        if (outline != null && outline.id >= 0) {
            Minecraft.getMinecraft().displayGuiScreen(new SubGuiOutlineDisplay((IOutlineManagerGui) this, outline));
        }
    }

    @Override
    protected void onCloneItem() {
        if (outline != null && outline.id >= 0) {
            Outline clone = (Outline) outline.clone();
            while (itemData.containsKey(clone.name)) clone.name += "_";
            DBCPacketHandler.Instance.sendToServer(new DBCSaveOutline(clone.writeToNBT(), clone.name));
        }
    }

    @Override
    protected void onItemReceived(NBTTagCompound compound) {
        outline = new Outline();
        outline.readFromNBT(compound);
        setPrevItemName(outline.name);
        if (outline.id != -1) {
            OutlineController.getInstance().customOutlines.replace(outline.id, outline);
            visualDisplay.outlineID = outline.id;
        }
    }

    @Override
    protected boolean hasSelectedItem() {
        return outline != null && outline.id >= 0;
    }

    @Override
    protected int getSelectedItemId() {
        return outline != null ? outline.id : -1;
    }

    @Override
    protected void sendMovePacket(int itemId, int destCatId) {
        DBCPacketHandler.Instance.sendToServer(new DBCCategoryMoveItem(DBCSyncType.OUTLINE, itemId, destCatId));
    }

    @Override
    protected GuiScreen getWindowedVariant() {
        return new GuiNPCManageOutlines(originalNpc);
    }

    // ========== IOutlineManagerGui ==========

    @Override
    public HashMap<String, Integer> getOutlineData() { return itemData; }

    @Override
    public GuiCustomScroll getOutlineScroll() { return itemScroll; }

    @Override
    public EntityNPCInterface getOutlineNPC() { return npc; }

    @Override
    public Outline getOutline() { return outline; }

    @Override
    public DBCDisplay getOutlineVisualDisplay() { return visualDisplay; }

    @Override
    public String getOutlineSelected() { return itemScroll != null ? itemScroll.getSelected() : null; }

    @Override
    public void setOutlineSelected(String selected) {
        if (itemScroll != null) itemScroll.setSelected(selected);
    }

    @Override
    public void closeOutlineSubGui(Object obj) {
        saveCurrentItem();
        if (selectedCatId >= 0) requestItemsInCategory(selectedCatId);
        NoppesUtil.openGUI(player, this);
    }

    @Override
    protected void saveCurrentItem() {
        if (outline != null && outline.id >= 0 && prevItemName != null && !prevItemName.isEmpty()) {
            DBCPacketHandler.Instance.sendToServer(new DBCSaveOutline(outline.writeToNBT(), prevItemName));
            prevItemName = outline.name;
        }
    }

    @Override
    protected void drawItemPreview(int centerX, int centerY, int mouseX, int mouseY, float partialTicks) {
        if (outline.id == -1) return;

        int savedOutlineID = visualDisplay.outlineID;
        visualDisplay.outlineID = outline.id;
        int hideName = npc.display.showName;
        int size = npc.display.modelSize;
        npc.display.showName = 1;
        npc.display.modelSize = 5;

        GL11.glColor4f(1, 1, 1, 1);
        EntityLivingBase entity = this.npc;

        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        float scale = Math.min(previewW, previewH) / 200f;
        float renderZoom = zoomed * scale;

        GL11.glTranslatef(centerX, centerY, 60F);
        GL11.glScalef(-renderZoom, renderZoom, renderZoom);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f7 = entity.rotationYawHead;
        float f5 = (float) centerX - mouseX;
        float f6 = (float) (centerY - 50) - mouseY;

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
        visualDisplay.outlineID = savedOutlineID;
    }
}
