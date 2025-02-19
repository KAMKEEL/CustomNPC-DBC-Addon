package kamkeel.npcdbc.client.gui.component;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.client.ClientProxy;
import kamkeel.npcdbc.client.render.RenderEventHandler;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.FormDisplay;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import static kamkeel.npcdbc.constants.DBCForm.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

public class GuiRenderPlayer extends GuiScreen {

    public EntityPlayer player;
    public int DBCForm = -1;
    public int customFormID = -1;
    private float rotation = 0;
    private float zoomed = 50;
    public int x, y, width, height;
    public FormDisplay.BodyColor playerColors;

    private GuiScreen parent;

    public GuiRenderPlayer(GuiScreen parent, int x, int y, int width, int height, FormDisplay.BodyColor playerColors) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.playerColors = playerColors;
        this.mc = parent.mc;
        this.parent = parent;
    }

    @Override
    public void drawScreen(int xPosMouse, int yPosMouse, float partialTicks) {

        if (isMouseOverRenderer(xPosMouse, yPosMouse)) {
            zoomed += Mouse.getDWheel() * 0.035f;
            if (zoomed > 100)
                zoomed = 100;
            if (zoomed < 10)
                zoomed = 10;

            if (Mouse.isButtonDown(0))
                rotation -= Mouse.getDX() * 0.75f;
        }

        RenderEventHandler.renderingPlayerInGUI = true;
        EntityLivingBase entity = mc.thePlayer;
        DBCData data = DBCData.getClient();

        int l = x + (this.width) / 2;
        int i1 = y + (height) / 2 + 55;

        float oldLimbSwing = entity.limbSwingAmount;
        boolean isInvisible = entity.isInvisible(), isImmunetoFire = entity.isImmuneToFire;
        Entity oldRidingEntity = entity.ridingEntity;
        entity.limbSwingAmount = entity.prevLimbSwingAmount = 0; // Removes moving animation
        entity.ridingEntity = null; // Removes riding animation
        entity.setInvisible(false); // Removes invisibility
        entity.isImmuneToFire = true; // Prevents burning

        InventoryPlayer inv = ((EntityPlayer) entity).inventory;
        ItemStack oldItem = inv.mainInventory[inv.currentItem];
        inv.mainInventory[inv.currentItem] = null; //Removes held item

        boolean changeForm = (DBCForm != -1 || customFormID != -1), isGoD = false, isKaioken = false, isUI = false;
        int oldForm = data.addonFormID;
        byte oldState = data.State, oldState2 = data.State2;
        FormDisplay.BodyColor oldColors = data.currentCustomizedColors;
        data.addonFormID = -1; // Removes addon forms
        data.State = 0; // Removes DBC state
        data.State2 = 0; // Removes DBC state 2

        if (playerColors != null)
            data.currentCustomizedColors = playerColors;

        if (changeForm) {
            int id = DBCForm;

            if (customFormID != -1)
                data.addonFormID = customFormID;
            else if (id < 20)
                data.State = (byte) id;
            else if (isKaioken = id >= Kaioken && id <= Kaioken6) {
                data.renderKK = true;
                data.State2 = (byte) (id - Kaioken + 1);
            } else if (isUI = id >= UltraInstinct && id <= UltraInstinct + 10) {
                data.renderUI = true;
                data.State2 = (byte) (id - UltraInstinct + 1);
            } else if (isGoD = id == GodOfDestruction) {
                data.renderGoD = true;
            }
        }


        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        glPushMatrix();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        setClip(x, y, width, height);

        GL11.glTranslatef(l, i1, 60F);

        GL11.glScalef(-zoomed, zoomed, zoomed);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f7 = entity.rotationYawHead;
        float f5 = (float) (l) - xPosMouse;
        float f6 = (float) (i1) - yPosMouse - 55;
        GL11.glRotatef(135F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-(float) Math.atan(f6 / 800F) * 20F, 1.0F, 0.0F, 0.0F);
        entity.prevRenderYawOffset = entity.renderYawOffset = rotation;
        entity.prevRotationYaw = entity.rotationYaw = (float) Math.atan(f5 / 80F) * 40F + rotation;
        entity.rotationPitch = entity.prevRotationPitch = -(float) Math.atan(f6 / 80F) * 20F;
        entity.prevRotationYawHead = entity.rotationYawHead = entity.rotationYaw;
        GL11.glTranslatef(0.0F, entity.yOffset, 1F);
        RenderManager.instance.playerViewY = 180F;


        // Render Entity
        try {
            RenderManager.instance.renderEntityWithPosYaw(entity, 0.0, 0.0, 0.0, 0.0F, partialTicks);
        } catch (Exception ignored) {
        }

        entity.prevRenderYawOffset = entity.renderYawOffset = f2;
        entity.prevRotationYaw = entity.rotationYaw = f3;
        entity.rotationPitch = entity.prevRotationPitch = f4;
        entity.prevRotationYawHead = entity.rotationYawHead = f7;

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        glPopMatrix();


        data.addonFormID = oldForm;
        data.State = oldState;
        data.State2 = oldState2;
        data.currentCustomizedColors = oldColors;
        if (isKaioken)
            data.renderKK = false;
        else if (isUI)
            data.renderUI = false;
        else if (isGoD)
            data.renderGoD = false;

        entity.limbSwingAmount = entity.prevLimbSwingAmount = oldLimbSwing;
        entity.ridingEntity = oldRidingEntity;
        entity.setInvisible(isInvisible);
        entity.isImmuneToFire = isImmunetoFire;
        inv.mainInventory[inv.currentItem] = oldItem;
        RenderEventHandler.renderingPlayerInGUI = false;

        if (ClientProxy.lastRendererGUIPlayerID >= 0 && JRMCoreH.data2.length > ClientProxy.lastRendererGUIPlayerID)
            JRMCoreH.data2[ClientProxy.lastRendererGUIPlayerID] = data.State + ";" + data.State2;
    }

    private boolean isMouseOverRenderer(int mouseX, int mouseY  ) {
        return x <= mouseX && x + width > mouseX && y - 1 <= mouseY && y + height > mouseY;
    }
    protected void setClip(int x, int y, int width, int height) {
        int scaleFactor = (mc.displayWidth / parent.width);
        x *= scaleFactor;
        y *= scaleFactor;
        width *= scaleFactor;
        height *= scaleFactor;
        y = this.mc.displayHeight - y;
        GL11.glScissor(x, y - height, width, height);
    }

}
