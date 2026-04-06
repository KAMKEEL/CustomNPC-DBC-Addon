package kamkeel.npcdbc.client.gui.hud.android;

import kamkeel.npcdbc.client.gui.component.SubGuiSelectAndroidPart;
import kamkeel.npcdbc.client.render.RenderEventHandler;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.race.races.android.AndroidPartSlot;
import kamkeel.npcdbc.data.race.races.android.AndroidPartType;
import kamkeel.npcdbc.data.race.races.android.AndroidUtil;
import kamkeel.npcdbc.data.race.races.android.DBCDataAndroid;
import kamkeel.npcdbc.items.android.ItemAndroidPart;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.race.AndroidEquipPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.LogWriter;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.client.gui.util.ISubGuiListener;
import noppes.npcs.client.gui.util.SubGuiInterface;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class HUDAndroidParts extends GuiNPCInterface implements ISubGuiListener {

    private static final Map<AndroidPartSlot, int[]> SLOT_HITBOX = new LinkedHashMap<>();
    static {
        SLOT_HITBOX.put(AndroidPartSlot.CORE,      new int[]{ -12, -10, 24, 28 });
        SLOT_HITBOX.put(AndroidPartSlot.BATTERY,   new int[]{ -10, -60, 20, 20 });
        SLOT_HITBOX.put(AndroidPartSlot.ARM_LEFT,  new int[]{  30, -10, 18, 40 });
        SLOT_HITBOX.put(AndroidPartSlot.ARM_RIGHT, new int[]{ -48, -10, 18, 40 });
        SLOT_HITBOX.put(AndroidPartSlot.LEG_LEFT,  new int[]{   2,  18, 16, 40 });
        SLOT_HITBOX.put(AndroidPartSlot.LEG_RIGHT, new int[]{ -18,  18, 16, 40 });
    }

    private final DBCData dbcData;
    private AndroidPartSlot hoveredSlot = null;

    private float guiAnimationScale = 0;
    private long timeOpened;
    private float rotation = 0;

    public HUDAndroidParts() {
        mc = Minecraft.getMinecraft();
        dbcData = DBCData.getClient();
        timeOpened = Minecraft.getSystemTime();
        closeOnEsc = true;

        mc.inGameHasFocus = false;
        mc.mouseHelper.ungrabMouseCursor();
    }

    // ── Ease ──
    private double easeOutExpo(double x) {
        return x == 1 ? 1 : 1 - Math.pow(2, -10 * x);
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void updateScreen() {
        if (Mouse.isButtonDown(1)) {
            close();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (guiAnimationScale < 1) {
            float t = (float)(Minecraft.getSystemTime() - timeOpened) / 1500f;
            t = Math.min(1, t);
            guiAnimationScale = (float) easeOutExpo(t);
        }

        int grad = ((int)(255 * 0.5f * guiAnimationScale) << 24);
        drawGradientRect(0, 0, width, height, grad, grad);

        int cx = width / 2;
        int cy = height / 2;

        glPushMatrix();
        GL11.glTranslatef(cx, cy, 0);
        GL11.glScalef(guiAnimationScale, guiAnimationScale, 1);
        GL11.glTranslatef(-cx, -cy, 0);

        renderPlayer(cx, cy + 60, mouseX, mouseY, partialTicks);

        hoveredSlot = null;
        for (Map.Entry<AndroidPartSlot, int[]> entry : SLOT_HITBOX.entrySet()) {
            DBCDataAndroid data = AndroidUtil.getData(dbcData);
            if (data == null) {
                LogWriter.error("Android Data is null");
            }

            AndroidPartSlot slot = entry.getKey();
            int[] hb = entry.getValue();
            int hx = cx + hb[0];
            int hy = cy + hb[1];
            int hw = hb[2];
            int hh = hb[3];

            boolean hovered = mouseX >= hx && mouseX <= hx + hw && mouseY >= hy && mouseY <= hy + hh;
            if (hovered) hoveredSlot = slot;

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_TEXTURE_2D);

            if (hovered) {
                GL11.glColor4f(1f, 1f, 1f, 0.25f);
            } else {
                AndroidPartType equipped = AndroidUtil.getEquippedPart(dbcData, slot);
                GL11.glColor4f(equipped != null ? 0f : 1f, equipped != null ? 1f : 0f, 0f, 0.12f);
            }

            drawRect(hx, hy, hx + hw, hy + hh, hovered ? 0x44FFFFFF : (AndroidUtil.getEquippedPart(dbcData, slot) != null ? 0x2200FF00 : 0x22FF0000));

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);

            String label = slotLabel(slot);
            AndroidPartType equipped = AndroidUtil.getEquippedPart(dbcData, slot);
            String display = equipped != null ? equipped.getName() : "Empty";
            if (hovered) {
                drawCenteredString(fontRendererObj, "§e" + label, hx + hw / 2, hy - 10, 0xFFFFFF);
                drawCenteredString(fontRendererObj, "§7" + display, hx + hw / 2, hy + hh + 2, 0xAAAAAA);
            }
        }

        glPopMatrix();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && hoveredSlot != null) {
            setSubGui(new SubGuiSelectAndroidPart(hoveredSlot));
        }
    }

    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof SubGuiSelectAndroidPart) {
            SubGuiSelectAndroidPart sub = (SubGuiSelectAndroidPart) subgui;
            if (sub.confirmed) {
                if (sub.removeEquip) {
                    DBCPacketHandler.Instance.sendToServer(new AndroidEquipPart(sub.slot, null));
                } else if (sub.selectedStack != null) {
                    AndroidPartType type = ItemAndroidPart.getPartType(sub.selectedStack);
                    DBCPacketHandler.Instance.sendToServer(new AndroidEquipPart(sub.slot, type));
                }
            }
        }
        initGui();
    }

    private String slotLabel(AndroidPartSlot slot) {
        switch (slot) {
            case CORE:      return "Core";
            case BATTERY:   return "Battery";
            case ARM_LEFT:  return "Arm (L)";
            case ARM_RIGHT: return "Arm (R)";
            case LEG_LEFT:  return "Leg (L)";
            case LEG_RIGHT: return "Leg (R)";
            default:        return slot.name();
        }
    }

    public void renderPlayer(int cx, int cy, int mouseX, int mouseY, float partialTicks) {
        if (Mouse.isButtonDown(0) && !hasSubGui()) {
            rotation -= Mouse.getDX() * 0.75f;
        }

        RenderEventHandler.renderingPlayerInGUI = true;
        EntityLivingBase entity = mc.thePlayer;

        float oldLimbSwing = entity.limbSwingAmount;
        boolean isInvisible = entity.isInvisible();
        boolean isImmuneToFire = entity.isImmuneToFire;
        Entity oldRiding = entity.ridingEntity;

        entity.limbSwingAmount = entity.prevLimbSwingAmount = 0;
        entity.ridingEntity = null;
        entity.setInvisible(false);
        entity.isImmuneToFire = true;

        InventoryPlayer inv = ((EntityPlayer) entity).inventory;
        ItemStack oldItem = inv.mainInventory[inv.currentItem];
        inv.mainInventory[inv.currentItem] = null;

        float oldSwingProgress = entity.swingProgress;
        float oldPrevSwingProgress = entity.prevSwingProgress;
        entity.swingProgress = entity.prevSwingProgress = 0;

        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f7 = entity.rotationYawHead;

        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        glPushMatrix();
        GL11.glTranslatef(cx, cy, 60f);
        GL11.glScalef(-70f, 70f, 70f);
        GL11.glRotatef(180f, 0f, 0f, 1f);

        entity.prevRenderYawOffset = entity.renderYawOffset = rotation;
        entity.prevRotationYaw = entity.rotationYaw = rotation;
        entity.rotationPitch = entity.prevRotationPitch = 0;
        entity.prevRotationYawHead = entity.rotationYawHead = rotation;

        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(135f, 0f, 1f, 0f);
        GL11.glRotatef(-135f, 0f, 1f, 0f);
        GL11.glTranslatef(0f, entity.yOffset, 1f);
        RenderManager.instance.playerViewY = 180f;

        try {
            RenderManager.instance.renderEntityWithPosYaw(entity, 0, 0, 0, 0f, partialTicks);
        } catch (Exception ignored) {}

        entity.prevRenderYawOffset = entity.renderYawOffset = f2;
        entity.prevRotationYaw = entity.rotationYaw = f3;
        entity.rotationPitch = entity.prevRotationPitch = f4;
        entity.prevRotationYawHead = entity.rotationYawHead = f7;
        entity.swingProgress = oldSwingProgress;
        entity.prevSwingProgress = oldPrevSwingProgress;

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        glPopMatrix();

        entity.limbSwingAmount = entity.prevLimbSwingAmount = oldLimbSwing;
        entity.ridingEntity = oldRiding;
        entity.setInvisible(isInvisible);
        entity.isImmuneToFire = isImmuneToFire;
        inv.mainInventory[inv.currentItem] = oldItem;
        RenderEventHandler.renderingPlayerInGUI = false;
    }

    @Override
    public void drawDefaultBackground() {}

    @Override
    public boolean doesGuiPauseGame() { return false; }

    @Override
    public void save() {}
}
