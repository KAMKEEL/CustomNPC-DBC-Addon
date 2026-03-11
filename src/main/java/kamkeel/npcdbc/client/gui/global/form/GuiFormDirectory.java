package kamkeel.npcdbc.client.gui.global.form;

import JinRyuu.JRMCore.JRMCoreH;
import noppes.npcs.client.gui.util.GuiDirectoryCategorized;
import net.minecraft.client.gui.GuiScreen;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.DBCSyncType;
import kamkeel.npcdbc.controllers.FormController;
import noppes.npcs.controllers.data.Category;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.get.form.DBCGetForm;
import kamkeel.npcdbc.network.packets.request.category.DBCCategoryMoveItem;
import kamkeel.npcdbc.network.packets.request.category.DBCCategoryRemove;
import kamkeel.npcdbc.network.packets.request.category.DBCCategorySave;
import kamkeel.npcdbc.network.packets.request.category.DBCRequestCategories;
import kamkeel.npcdbc.network.packets.request.category.DBCRequestCategoryItems;
import kamkeel.npcdbc.network.packets.request.form.DBCRemoveForm;
import kamkeel.npcdbc.network.packets.request.form.DBCSaveForm;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.ClientEventHandler;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.SubGuiInterface;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.HashMap;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiFormDirectory extends GuiDirectoryCategorized implements IFormManagerGui {
    public Form form = new Form();
    public int parentForm = -1;
    public int childForm = -1;
    public FormDisplay display;
    public DBCDisplay visualDisplay;
    public int originalRace;
    public EntityNPCInterface npc;
    private EntityNPCInterface originalNpc;

    public GuiFormDirectory(EntityNPCInterface npc) {
        super();
        this.originalNpc = npc;
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
        display = form.display;
        zoomed = 70;
    }

    @Override
    protected String getTitle() { return "Forms"; }

    @Override
    protected void requestCategoryList() {
        DBCPacketHandler.Instance.sendToServer(new DBCRequestCategories(DBCSyncType.FORM));
    }

    @Override
    protected void requestItemsInCategory(int catId) {
        DBCPacketHandler.Instance.sendToServer(new DBCRequestCategoryItems(DBCSyncType.FORM, catId));
    }

    @Override
    protected void requestItemData(int itemId) {
        DBCPacketHandler.Instance.sendToServer(new DBCGetForm(itemId));
    }

    @Override
    protected void onSaveCategory(Category cat) {
        DBCPacketHandler.Instance.sendToServer(new DBCCategorySave(DBCSyncType.FORM, cat.writeNBT(new NBTTagCompound())));
    }

    @Override
    protected void onRemoveCategory(int catId) {
        DBCPacketHandler.Instance.sendToServer(new DBCCategoryRemove(DBCSyncType.FORM, catId));
    }

    @Override
    protected void onAddItem(int catId) {
        String name = "New";
        while (itemData.containsKey(name)) name += "_";
        Form newForm = new Form(-1, name);
        DBCPacketHandler.Instance.sendToServer(new DBCSaveForm(newForm.writeToNBT(), name));
    }

    @Override
    protected void onRemoveItem(int itemId) {
        DBCPacketHandler.Instance.sendToServer(new DBCRemoveForm(itemId));
        form = new Form();
        visualDisplay.formID = -1;
    }

    @Override
    protected void onEditItem() {
        if (form != null && form.id >= 0) {
            setSubGui(new SubGuiFormGeneral(this, form));
        }
    }

    @Override
    protected void onCloneItem() {
        if (form != null && form.id >= 0) {
            Form clone = (Form) form.clone();
            while (itemData.containsKey(clone.name)) clone.name += "_";
            DBCPacketHandler.Instance.sendToServer(new DBCSaveForm(clone.writeToNBT(), clone.name));
        }
    }

    @Override
    protected void onItemReceived(NBTTagCompound compound) {
        int oldID = form != null ? form.id : -1;
        form = new Form();
        form.readFromNBT(compound);
        parentForm = form.parentID;
        childForm = form.childID;
        setPrevItemName(form.name);

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
    }

    @Override
    protected boolean hasSelectedItem() {
        return form != null && form.id >= 0;
    }

    @Override
    protected int getSelectedItemId() {
        return form != null ? form.id : -1;
    }

    @Override
    protected void sendMovePacket(int itemId, int destCatId) {
        DBCPacketHandler.Instance.sendToServer(new DBCCategoryMoveItem(DBCSyncType.FORM, itemId, destCatId));
    }

    @Override
    protected void drawItemPreview(int centerX, int centerY, int mouseX, int mouseY, float partialTicks) {
        if (form.id == -1) return;

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

        ClientEventHandler.renderingEntityInGUI = true;
        GL11.glPushMatrix();
        try {
            float formWidth = display.formWidth;
            float cr = 100;
            float diff = (formWidth - 1.0F) * cr * 0.02F + 1.0F;
            formWidth = formWidth > 1.0F ? diff : formWidth;
            GL11.glScalef(formWidth, 1, formWidth);
            RenderManager.instance.renderEntityWithPosYaw(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F);
        } catch (Exception ignored) {
        }
        GL11.glPopMatrix();
        ClientEventHandler.renderingEntityInGUI = false;

        entity.prevRenderYawOffset = entity.renderYawOffset = f2;
        entity.prevRotationYaw = entity.rotationYaw = f3;
        entity.rotationPitch = f4;
        entity.prevRotationYawHead = entity.rotationYawHead = f7;

        GL11.glPopMatrix();

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    protected void drawItemDetails(int x, int y, int w) {
        if (form == null || form.id == -1) return;

        String drawString = form.getMenuName();
        fontRendererObj.drawString(drawString, x, y, 0xFFFFFF, true);

        String race = "\u00A7fRace: \u00A7e" + (form.getRace() == -1 ? "All" :
            form.getRace() == DBCRace.ALL_SAIYANS ? "All Saiyans" :
            form.getRace() == 1 ? "Pure Saiyan" : JRMCoreH.Races[form.race()]);
        fontRendererObj.drawString(race, x, y + 12, 0xFFFFFF, true);

        int ty = y + 28;
        fontRendererObj.drawString("\u00A7fSTR: \u00A74x\u00A7c" + form.strengthMulti, x, ty, 0xFFFFFF, true);
        ty += 12;
        fontRendererObj.drawString("\u00A7fDEX: \u00A73x\u00A7b" + form.dexMulti, x, ty, 0xFFFFFF, true);
        ty += 12;
        fontRendererObj.drawString("\u00A7fWIL: \u00A76x\u00A7e" + form.willMulti, x, ty, 0xFFFFFF, true);

        ty += 16;
        if (form.hasChild() && form.getChild() != null) {
            fontRendererObj.drawString("\u00A7fChild: \u00A77" + form.getChild().getName(), x, ty, 0xFFFFFF, true);
            ty += 12;
        }
        if (form.race() != -1 && form.requiredForm.containsKey(form.race())) {
            String parentName = Utility.removeBoldColorCode(DBCUtils.getFormattedStateName(form.race(), form.requiredForm.get(form.race())));
            fontRendererObj.drawString("\u00A7fParent: " + parentName, x, ty, 0xFFFFFF, true);
        } else if (form.hasParent() && form.getParent() != null) {
            fontRendererObj.drawString("\u00A7fParent: \u00A77" + form.getParent().getName(), x, ty, 0xFFFFFF, true);
        }
    }

    @Override
    protected GuiScreen getWindowedVariant() {
        return new GuiNPCManageForms(originalNpc);
    }

    // ========== IFormManagerGui ==========
    @Override
    public HashMap<String, Integer> getFormData() { return itemData; }

    @Override
    public GuiCustomScroll getFormScroll() { return itemScroll; }

    @Override
    public EntityNPCInterface getFormNPC() { return npc; }

    @Override
    public Form getForm() { return form; }

    @Override
    public FormDisplay getFormDisplay() { return display; }

    @Override
    public DBCDisplay getFormVisualDisplay() { return visualDisplay; }

    @Override
    protected void onSubGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof SubGuiFormGeneral) {
            if (form != null && form.id >= 0) {
                setPrevItemName(form.name);
                if (selectedCatId >= 0) requestItemsInCategory(selectedCatId);
            }
        }
    }

    @Override
    protected void saveCurrentItem() {
        if (form != null && form.id >= 0 && prevItemName != null && !prevItemName.isEmpty()) {
            DBCPacketHandler.Instance.sendToServer(new DBCSaveForm(form.writeToNBT(), prevItemName));
            prevItemName = form.name;
        }
    }

}
