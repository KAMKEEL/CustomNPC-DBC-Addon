package kamkeel.npcdbc.client.gui.component;

import kamkeel.npcdbc.client.render.RenderEventHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.SubGuiInterface;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.data.ModelData;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class SubGuiModelInterface extends SubGuiInterface {

    public ModelData playerdata;

    private static float rotation = 0;

    private GuiNpcButton left, right, zoom, unzoom;

    public float zoomed = 60;
    public float minZoom = 10, maxZoom = 100;

    public int xOffsetNpc = 0, xOffsetButton = 0, xMouseRange=100;
    public int yOffsetNpc = 0, yOffsetButton = 0,yMouseRange=150;
    public boolean followMouse = true, drawNPConSub = false;

    public boolean allowRotate = true;
    public boolean drawRenderButtons = true, drawXButton = true;

    public EntityCustomNpc npc;

    public SubGuiModelInterface(EntityCustomNpc npc) {
        this.npc = npc;
        playerdata = npc.modelData;
        xSize = 380;
        drawDefaultBackground = false;
        closeOnEsc = true;
    }

    @Override
    public void initGui() {
        super.initGui();


        if (drawRenderButtons) {
            addButton(unzoom = new GuiNpcButton(666, guiLeft + 148 + xOffsetNpc + xOffsetButton, guiTop + 200 + yOffsetNpc + yOffsetButton, 20, 20, "-"));
            addButton(zoom = new GuiNpcButton(667, guiLeft + 214 + xOffsetNpc + xOffsetButton, guiTop + 200 + yOffsetNpc + yOffsetButton, 20, 20, "+"));
            addButton(left = new GuiNpcButton(668, guiLeft + 170 + xOffsetNpc + xOffsetButton, guiTop + 200 + yOffsetNpc + yOffsetButton, 20, 20, "<"));
            addButton(right = new GuiNpcButton(669, guiLeft + 192 + xOffsetNpc + xOffsetButton, guiTop + 200 + yOffsetNpc + yOffsetButton, 20, 20, ">"));
        }

        if (drawXButton) {
            addButton(new GuiNpcButton(670, width - 22, 2, 20, 20, "X"));
        }
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);
        if (btn.id == 670) {
            close();
        }
    }

    public boolean isMouseOverRenderer(int x, int y) {
        if (!allowRotate) {
            return false;
        }
        // Center of the entity rendering
        int centerX = guiLeft + 190 + xOffsetNpc; // Matches l in drawScreen()
        int centerY = guiTop + 180 + yOffsetNpc; // Matches i1 in drawScreen()

        // Range from the center to start considering mouse is over renderer.
        int xRange = xMouseRange; // Horizontal range (Left and right of center)
        int yRange = yMouseRange; // Vertical range (Up and down of center)

        // Check if the mouse is within the range area
        return mouseX >= centerX - xRange && mouseX <= centerX + xRange && mouseY >= centerY - yRange && mouseY <= centerY + yRange;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX,mouseY,partialTick);
         drawNpc(mouseX, mouseY, partialTick);
    }

    public void drawNpc(int mouseX, int mouseY, float partialTicks) {
        if (Mouse.isButtonDown(0) && drawRenderButtons) {
            if (this.left.mousePressed(this.mc, mouseX, mouseY)) {
                rotation += partialTicks * 1.5F;
            } else if (this.right.mousePressed(this.mc, mouseX, mouseY)) {
                rotation -= partialTicks * 1.5F;
            } else if (this.zoom.mousePressed(this.mc, mouseX, mouseY) && zoomed < maxZoom) {
                zoomed += partialTicks * 1.0F;
            } else if (this.unzoom.mousePressed(this.mc, mouseX, mouseY) && zoomed > minZoom) {
                zoomed -= partialTicks * 1.0F;
            }
        }

        if (isMouseOverRenderer(mouseX, mouseY)) {
            zoomed += Mouse.getDWheel() * 0.035f;
            if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
                rotation -= Mouse.getDX() * 0.75f;
            }
        }

        if (zoomed > maxZoom)
            zoomed = maxZoom;
        if (zoomed < minZoom)
            zoomed = minZoom;

        if (hasSubGui() && !drawNPConSub)
            return;

        GL11.glColor4f(1, 1, 1, 1);

        EntityLivingBase entity = playerdata.getEntity(npc);
        if (entity == null)
            entity = this.npc;

        preRender(entity);

        int l = guiLeft + 190 + xOffsetNpc;
        int i1 = guiTop + 180 + yOffsetNpc;
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef(l, i1, 60F);


        GL11.glScalef(-zoomed, zoomed, zoomed);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f7 = entity.rotationYawHead;
        float f5 = (float) (l) - mouseX;
        float f6 = (float) (i1 - 50) - mouseY;
        GL11.glRotatef(135F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-(float) Math.atan(f6 / 800F) * 20F, 1.0F, 0.0F, 0.0F);
        entity.prevRenderYawOffset = entity.renderYawOffset = rotation;
        entity.prevRotationYaw = entity.rotationYaw = (float) Math.atan(f5 / 80F) * 40F + rotation;
        entity.rotationPitch = entity.prevRotationPitch = followMouse ? -(float) Math.atan(f6 / 40F) * 20F : 0;
        entity.prevRotationYawHead = entity.rotationYawHead = followMouse ? entity.rotationYaw : rotation;
        GL11.glTranslatef(0.0F, entity.yOffset, 1F);
        RenderManager.instance.playerViewY = 180F;

        try {
            RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
        } catch (Exception e) {
            playerdata.setEntityClass(null);
        }
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
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0f, 500.065F);
        super.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glPopMatrix();

        postRender(entity);
    }

    public void preRender(EntityLivingBase entity) {
        EntityUtil.Copy(npc, entity);
        RenderEventHandler.renderingNPCInGUI = true;
    }

    public void postRender(EntityLivingBase entity) {
        RenderEventHandler.renderingNPCInGUI = false;
    }
}
