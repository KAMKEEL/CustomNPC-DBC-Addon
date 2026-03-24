package kamkeel.npcdbc.client.gui.dbc;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.client.ClientEventHandler;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * Reusable entity-preview renderer for GUI screens.
 * <p>
 * Encapsulates the zoom / rotation / mouse-interaction / GL state logic
 * originally found in {@code GuiNPCInterface.drawNpc(...)} without bringing
 * any rotate/zoom <em>buttons</em> along. All interaction is mouse-driven:
 * <ul>
 *   <li>Mouse wheel &mdash; zoom in / out</li>
 *   <li>Left-drag &mdash; rotate the entity</li>
 *   <li>Right-click &mdash; reset rotation &amp; zoom</li>
 * </ul>
 * <p>
 * Usage:
 * <pre>{@code
 * // Create once (e.g. in initGui)
 * renderer = new EntityPreviewRenderer();
 * renderer.setAnchor(guiLeft + 51, guiTop + 155);
 *
 * // In drawScreen:
 * renderer.draw(entity, mouseX, mouseY, partialTicks);
 * }</pre>
 */
public class EntityPreviewRenderer {

    // ── Anchor (screen-space pixel position of the entity feet) ──
    private int anchorX;
    private int anchorY;

    // ── Hit-area around the anchor for mouse interactions ──
    private int xMouseRange = 50;
    private int yMouseRange = 150;

    // ── Zoom ──
    private float defaultZoom = 1.0f;
    private float zoom = 1.0f;
    private float minZoom = 0.5f;
    private float maxZoom = 2.5f;

    // ── Rotation ──
    private float rotation = 0.0f;

    // ── Behaviour toggles ──
    private boolean followMouse = true;
    private boolean allowRotate = true;

    // ─────────────────── Configuration API ───────────────────

    /** Sets the screen-space anchor where the entity stands. */
    public EntityPreviewRenderer setAnchor(int x, int y) {
        this.anchorX = x;
        this.anchorY = y;
        return this;
    }

    /** Sets the rectangular hit-area half-extents (from anchor) for mouse interaction. */
    public EntityPreviewRenderer setMouseRange(int xRange, int yRange) {
        this.xMouseRange = xRange;
        this.yMouseRange = yRange;
        return this;
    }

    public EntityPreviewRenderer setDefaultZoom(float defaultZoom) {
        this.defaultZoom = defaultZoom;
        this.zoom = defaultZoom;
        return this;
    }

    public EntityPreviewRenderer setZoomBounds(float min, float max) {
        this.minZoom = min;
        this.maxZoom = max;
        return this;
    }

    public EntityPreviewRenderer setFollowMouse(boolean follow) {
        this.followMouse = follow;
        return this;
    }

    public EntityPreviewRenderer setAllowRotate(boolean allow) {
        this.allowRotate = allow;
        return this;
    }

    /** Direct write access for external rotation overrides (e.g. animation). */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getRotation() {
        return rotation;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = clampZoom(zoom);
    }

    /** Resets zoom and rotation to defaults. */
    public void reset() {
        this.zoom = defaultZoom;
        this.rotation = 0.0f;
    }

    // ─────────────────── Main draw entry ───────────────────

    /**
     * Process mouse interaction and render the entity at the configured anchor.
     *
     * @param entity       the living entity to draw
     * @param mouseX       current screen-space mouse X
     * @param mouseY       current screen-space mouse Y
     * @param partialTicks render partial tick
     */
    public void draw(EntityLivingBase entity, int mouseX, int mouseY, float partialTicks) {
        // ── Mouse interaction ──
        if (isMouseOver(mouseX, mouseY)) {
            zoom += Mouse.getDWheel() * 0.001f;
            if (Mouse.isButtonDown(0)) {
                rotation -= Mouse.getDX() * 0.75f;
            } else if (Mouse.isButtonDown(1)) {
                rotation = 0;
                zoom = defaultZoom;
            }
        }
        zoom = clampZoom(zoom);

        // ── Render ──
        renderEntity(entity, mouseX, mouseY, anchorX, anchorY, zoom, rotation);
    }

    // ─────────────────── Core renderer ───────────────────

    /**
     * Pure rendering method &mdash; no mouse state mutation.
     * Can be called directly if you want manual control over zoom / rotation.
     */
    public void renderEntity(EntityLivingBase entity, int mouseX, int mouseY,
                             int x, int y, float zoomed, float rot) {
        EntityNPCInterface npc = entity instanceof EntityNPCInterface ? (EntityNPCInterface) entity : null;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (npc != null) npc.isDrawn = true;
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();

        GL11.glTranslatef(x, y, 90F);
        float scale = 1.0f;
        if (entity.height > 2.4f)
            scale = 2.0f / entity.height;

        GL11.glScalef(-30 * scale * zoomed, 30 * scale * zoomed, 30 * scale * zoomed);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        // Save original rotations for restoration
        float origYawOffset  = entity.renderYawOffset;
        float origYaw         = entity.rotationYaw;
        float origPitch       = entity.rotationPitch;
        float origYawHead     = entity.rotationYawHead;

        float f5 = (float) x - mouseX;
        float f6 = y - 50 * scale * zoomed - mouseY;

        int origOrientation = 0;
        if (npc != null) {
            origOrientation = npc.ais.orientation;
            npc.ais.orientation = (int) rot;
        }

        GL11.glRotatef(135F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-(float) Math.atan(f6 / 400F) * 20F, 1.0F, 0.0F, 0.0F);

        entity.renderYawOffset = rot;
        entity.rotationYaw     = followMouse ? (float) Math.atan(f5 / 80F) * 40F + rot : 0;
        entity.rotationPitch   = followMouse ? -(float) Math.atan(f6 / 40F) * 20F : 0;
        entity.rotationYawHead = entity.rotationYaw;
        GL11.glTranslatef(0.0F, entity.yOffset, 0.0F);
        RenderManager.instance.playerViewY = 180F;

        ClientEventHandler.renderingEntityInGUI = true;
        try {
            RenderManager.instance.renderEntityWithPosYaw(entity, 0, 0, 0, 0, 1);
        } catch (Exception ignored) {
        }
        ClientEventHandler.renderingEntityInGUI = false;

        // Restore entity rotation state
        entity.prevRenderYawOffset = entity.renderYawOffset = origYawOffset;
        entity.prevRotationYaw     = entity.rotationYaw     = origYaw;
        entity.prevRotationPitch   = entity.rotationPitch   = origPitch;
        entity.prevRotationYawHead = entity.rotationYawHead = origYawHead;
        if (npc != null) {
            npc.ais.orientation = origOrientation;
        }

        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        if (npc != null) npc.isDrawn = false;
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    // ─────────────────── Internals ───────────────────

    private boolean isMouseOver(int mouseX, int mouseY) {
        if (!allowRotate) return false;
        return mouseX >= anchorX - xMouseRange && mouseX <= anchorX + xMouseRange
            && mouseY >= anchorY - yMouseRange && mouseY <= anchorY + yMouseRange;
    }

    private float clampZoom(float z) {
        if (z > maxZoom) return maxZoom;
        if (z < minZoom) return minZoom;
        return z;
    }
}
