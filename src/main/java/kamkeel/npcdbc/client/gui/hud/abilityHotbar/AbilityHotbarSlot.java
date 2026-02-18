package kamkeel.npcdbc.client.gui.hud.abilityHotbar;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.gui.hud.abilityWheel.icon.AbilityIcon;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.ChainedAbility;
import kamkeel.npcs.controllers.data.ability.IAbilityAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.controllers.data.PlayerData;
import org.lwjgl.opengl.GL11;

/**
 * A single slot in the Ability Hotbar HUD.
 * Shows the ability icon, name, and cooldown overlay.
 */
public class AbilityHotbarSlot extends Gui {
    private static final int SELECT_TIME = 200;
    public static final ResourceLocation SLOT_TEXTURE = new ResourceLocation(CustomNpcPlusDBC.ID, "textures/gui/ability_hotbar.png");

    public HUDAbilityHotbar parent;
    public int index;

    public String abilityKey = null;
    public Ability ability = null;
    public IAbilityAction action = null;
    private AbilityIcon icon = null;

    public boolean isSelected = false;
    public long startSelectTime = 0;
    public long stopSelectTime = 0;
    public float selectScale = 0;

    // Layout constants
    private static final int SLOT_SIZE = 40;
    private static final int SLOT_GAP = 4;
    private static final int SLOT_X_OFFSET = 8;
    private static final int ICON_SIZE = 32;

    // Cooldown tracking
    private float lastCooldownProgress = 0;

    public AbilityHotbarSlot(HUDAbilityHotbar parent, int index) {
        this.parent = parent;
        this.index = index;
    }

    public void setAbility(String key, Ability ability, IAbilityAction action) {
        this.abilityKey = key;
        this.ability = ability;
        this.action = action;
        if (ability != null) {
            this.icon = new AbilityIcon(ability);
        } else if (action instanceof ChainedAbility) {
            this.icon = new AbilityIcon((ChainedAbility) action);
        } else {
            this.icon = null;
        }
    }

    /**
     * Easing function for smooth animations.
     */
    public double easeInOutCirc(float x) {
        return x < 0.5
            ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2
            : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2;
    }

    /**
     * Draw this slot at its position.
     */
    public void draw(Minecraft mc, ScaledResolution sr, float cooldownProgress) {
        int screenH = sr.getScaledHeight();

        // Calculate total height of all slots
        int totalHeight = 6 * SLOT_SIZE + 5 * SLOT_GAP;

        // Center vertically
        int baseY = (screenH - totalHeight) / 2;

        int x = SLOT_X_OFFSET;
        int y = baseY + index * (SLOT_SIZE + SLOT_GAP);

        // Calculate selection scale
        float scale = 1.0f + 0.15f * getSegmentScale();

        GL11.glPushMatrix();

        // Apply selection scale centered on slot
        GL11.glTranslatef(x + SLOT_SIZE / 2f, y + SLOT_SIZE / 2f, 0);
        GL11.glScalef(scale, scale, 1);
        GL11.glTranslatef(-SLOT_SIZE / 2f, -SLOT_SIZE / 2f, 0);

        // Draw slot background
        drawSlotBackground(isSelected ? 1.0f : 0.7f);

        // Draw ability icon
        if (icon != null) {
            GL11.glPushMatrix();
            int iconOffset = (SLOT_SIZE - ICON_SIZE) / 2;
            GL11.glTranslatef(iconOffset + ICON_SIZE / 2f, iconOffset + ICON_SIZE / 2f, 0);
            icon.draw();
            GL11.glPopMatrix();
        }

        // Draw cooldown overlay
        if (cooldownProgress > 0 && abilityKey != null) {
            drawCooldownOverlay(cooldownProgress);
        }

        GL11.glPopMatrix();

        // Draw ability name (outside the scaled matrix)
        if (action != null && isSelected) {
            FontRenderer fr = mc.fontRenderer;
            String name = ability != null ? ability.getDisplayName()
                : (action instanceof ChainedAbility ? ((ChainedAbility) action).getDisplayName() : action.getName());
            if (name != null && !name.isEmpty()) {
                int nameX = x + SLOT_SIZE + 4;
                int nameY = y + (SLOT_SIZE - fr.FONT_HEIGHT) / 2;
                // Draw with shadow for visibility, toggle-aware coloring
                fr.drawStringWithShadow(name, nameX, nameY, getNameColor());
            }
        }
    }

    /**
     * Draw the slot background. Uses a simple filled rectangle with border.
     */
    private void drawSlotBackground(float alpha) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Draw dark background
        GL11.glColor4f(0.1f, 0.1f, 0.1f, alpha * 0.8f);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(0, 0);
        GL11.glVertex2f(SLOT_SIZE, 0);
        GL11.glVertex2f(SLOT_SIZE, SLOT_SIZE);
        GL11.glVertex2f(0, SLOT_SIZE);
        GL11.glEnd();

        // Draw border
        GL11.glColor4f(0.4f, 0.4f, 0.4f, alpha);
        GL11.glLineWidth(2.0f);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(0, 0);
        GL11.glVertex2f(SLOT_SIZE, 0);
        GL11.glVertex2f(SLOT_SIZE, SLOT_SIZE);
        GL11.glVertex2f(0, SLOT_SIZE);
        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);
    }

    /**
     * Draw a grey overlay representing cooldown progress.
     * The overlay shrinks from top to bottom as cooldown decreases.
     */
    private void drawCooldownOverlay(float progress) {
        if (progress <= 0) return;

        // Clamp progress
        progress = Math.min(1, Math.max(0, progress));

        // Calculate overlay height (from top)
        int overlayHeight = (int) (SLOT_SIZE * progress);
        if (overlayHeight <= 0) return;

        // Draw semi-transparent grey overlay
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Grey color with transparency
        GL11.glColor4f(0.2f, 0.2f, 0.2f, 0.7f);

        // Draw from top down
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(0, 0);
        GL11.glVertex2f(SLOT_SIZE, 0);
        GL11.glVertex2f(SLOT_SIZE, overlayHeight);
        GL11.glVertex2f(0, overlayHeight);
        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);
    }

    /**
     * Get the color for the ability name.
     * Toggleable abilities show green if active, red if inactive.
     * Regular abilities show white.
     */
    private int getNameColor() {
        if (ability != null && ability.isToggleable()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer != null) {
                PlayerData playerData = PlayerData.get(mc.thePlayer);
                if (playerData != null && playerData.abilityData != null
                    && playerData.abilityData.isAbilityToggled(abilityKey)) {
                    return 0xFF55FF55; // Green - active
                }
            }
            return 0xFFFF5555; // Red - inactive
        }
        return 0xFFFFFFFF; // White - normal
    }

    public void setSelectedState(boolean newSelectState) {
        if (!isSelected && newSelectState) {
            startSelectTime = (long) (Minecraft.getSystemTime() - (Math.max(selectScale - 1, 0)) * SELECT_TIME);
        }
        if (isSelected && !newSelectState) {
            stopSelectTime = (long) (Minecraft.getSystemTime() - (1 - selectScale) * SELECT_TIME);
        }
        isSelected = newSelectState;
    }

    public float getSegmentScale() {
        float updateTime;
        if (isSelected) {
            updateTime = (float) (Minecraft.getSystemTime() - startSelectTime) / SELECT_TIME;
            updateTime = Math.min(updateTime, 1);
            selectScale = (float) easeInOutCirc(updateTime);
        } else {
            updateTime = (float) (Minecraft.getSystemTime() - stopSelectTime) / SELECT_TIME;
            updateTime = Math.min(updateTime, 1);
            selectScale = (float) easeInOutCirc(1 - updateTime);
        }

        selectScale = Math.min(1, Math.max(selectScale, 0));

        return selectScale;
    }
}
