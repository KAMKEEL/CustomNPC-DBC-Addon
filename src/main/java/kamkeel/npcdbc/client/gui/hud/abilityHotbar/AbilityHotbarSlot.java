package kamkeel.npcdbc.client.gui.hud.abilityHotbar;

import kamkeel.npcdbc.client.gui.hud.abilityWheel.icon.AbilityIcon;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.ChainedAbility;
import kamkeel.npcs.controllers.data.ability.IAbilityAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import noppes.npcs.controllers.data.PlayerData;
import org.lwjgl.opengl.GL11;

/**
 * A single slot in the Ability Hotbar HUD.
 * Rendered as a circle with the ability icon centered inside.
 */
public class AbilityHotbarSlot extends Gui {
    private static final int SELECT_TIME = 200;

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

    // Per-slot name fade — independent of position
    public float nameAlpha = 0f;
    private boolean nameFadingIn = false;
    private long nameFadeStartTime = 0;
    private static final long NAME_FADE_DURATION = 180; // ms

    private static final int CIRCLE_SEGMENTS = 32;

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

    public double easeInOutCirc(float x) {
        return x < 0.5
            ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2
            : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2;
    }

    /**
     * Draw this slot as a circle centered at (cx, cy) with given diameter.
     *
     * @param cx            center X in screen coords
     * @param cy            center Y in screen coords
     * @param size          diameter of the circle
     * @param isCenter      whether this is the currently selected/center slot
     * @param slotAlpha     overall alpha for this slot (0=invisible, 1=full)
     */
    public void drawCarousel(Minecraft mc, ScaledResolution sr, float cooldownProgress,
                             int cx, int cy, int size, boolean isCenter,
                             float slotAlpha) {
        float nameAlpha = this.nameAlpha;
        boolean isHorizontal = ConfigDBCClient.AbilityHotbarHorizontal;
        if (size <= 0 || slotAlpha <= 0) return;
        float radius = size / 2f;
        float baseAlpha = isCenter ? 0.9f : 0.6f;
        float alpha = baseAlpha * slotAlpha;

        GL11.glPushMatrix();
        GL11.glTranslatef(cx, cy, 0);

        boolean altTexture = ConfigDBCClient.AlternateHotbarTexture;

        if (altTexture) {
            drawRoundedRect(-radius, -radius, radius, radius, 3f, 0.5f, 0.5f, 0.5f, 0.75f * alpha, false);

            if (isCenter) {
                drawRoundedRect(-radius, -radius, radius, radius, 3f, 0.8f, 0.8f, 0.2f, alpha, true);
            } else {
                drawRoundedRect(-radius, -radius, radius, radius, 3f, 0.4f, 0.4f, 0.4f, alpha * 0.8f, true);
            }
        } else {

            if (isCenter) {
                drawCircle(radius, 0.8f, 0.8f, 0.2f, 0.66f * alpha, false);
                drawCircle(radius, 0.8f, 0.8f, 0.2f, alpha, true);
            } else {
                drawCircle(radius, 1f, 1f, 1f, 0.66f * alpha, false);
                drawCircle(radius, 0.4f, 0.4f, 0.4f, alpha * 0.8f, true);
            }
        }

        // Ability icon, scaled to fit inside circle
        if (icon != null) {
            GL11.glPushMatrix();
            float targetSize = size * 0.55f;
            float iconNaturalSize = Math.max(icon.width, icon.height);
            if (iconNaturalSize <= 0) iconNaturalSize = 16f;
            float iconScale = targetSize / iconNaturalSize;
            GL11.glScalef(iconScale, iconScale, 1);
            // Apply slot alpha to icon color
            GL11.glColor4f(1, 1, 1, slotAlpha);
            icon.draw(getToggleState());
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glPopMatrix();
        }

        // Cooldown arc overlay (pie from top, clockwise)
        if (cooldownProgress > 0 && abilityKey != null) {
            drawCooldownArc(radius, cooldownProgress);
        }

        GL11.glPopMatrix();

        // Ability name — only on center slot, with externally managed fade alpha
        if (isCenter && action != null && nameAlpha > 0) {
            FontRenderer fr = mc.fontRenderer;
            String name = getAbilityName();
            if (name != null && !name.isEmpty()) {
                int a = (int)(nameAlpha * 255) & 0xFF;
                int nameColor = (a << 24) | (getNameColor() & 0x00FFFFFF);
                int nameX, nameY;
                if (isHorizontal) {
                    // Centered above the slot
                    nameX = cx - fr.getStringWidth(name) / 2;
                    nameY = cy - (int) radius - fr.FONT_HEIGHT - 2;
                } else {
                    // To the right of the slot
                    nameX = cx + (int) radius + 4;
                    nameY = cy - fr.FONT_HEIGHT / 2;
                }
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glColor4f(1, 1, 1, nameAlpha);
                fr.drawStringWithShadow(name, nameX, nameY, nameColor);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glColor4f(1, 1, 1, 1);
            }
        }
    }

    /**
     * Draw a square (filled or outline) centered at origin.
     */
    private void drawRoundedRect(float x1, float y1, float x2, float y2, float cr,
                                 float r, float g, float b, float a, boolean outline) {

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(r, g, b, a);

        if (outline) {
            GL11.glLineWidth(2.0f);
            GL11.glBegin(GL11.GL_LINE_LOOP);
        } else {
            GL11.glBegin(GL11.GL_QUADS);
        }

        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x2, y1);
        GL11.glVertex2f(x2, y2);
        GL11.glVertex2f(x1, y2);

        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glColor4f(1, 1, 1, 1);
    }

    /** Draw a circle (filled or outline) centered at origin. */
    private void drawCircle(float radius, float r, float g, float b, float a, boolean outline) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(r, g, b, a);

        if (outline) {
            GL11.glLineWidth(1.5f);
            GL11.glBegin(GL11.GL_LINE_LOOP);
        } else {
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex2f(0, 0);
        }

        for (int i = 0; i <= CIRCLE_SEGMENTS; i++) {
            double angle = 2 * Math.PI * i / CIRCLE_SEGMENTS;
            GL11.glVertex2f((float)(Math.cos(angle) * radius), (float)(Math.sin(angle) * radius));
        }
        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glColor4f(1, 1, 1, 1);
    }

    /** Draw a pie-shaped cooldown overlay from top, clockwise. */
    private void drawCooldownArc(float radius, float progress) {
        progress = Math.min(1, Math.max(0, progress));
        if (progress <= 0) return;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(0.1f, 0.1f, 0.1f, 0.7f);

        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(0, 0);
        int steps = (int)(CIRCLE_SEGMENTS * progress) + 1;
        for (int i = 0; i <= steps; i++) {
            double angle = -Math.PI / 2 + 2 * Math.PI * i * progress / steps;
            GL11.glVertex2f((float)(Math.cos(angle) * radius), (float)(Math.sin(angle) * radius));
        }
        GL11.glEnd();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);
    }

    private String getAbilityName() {
        if (ability != null) {
            int state = getToggleState();
            if (state > 0) {
                String label = ability.getToggleStateLabel(state);
                if (label != null) return label;
            }
            return ability.getDisplayName();
        }
        if (action instanceof ChainedAbility) return ((ChainedAbility) action).getDisplayName();
        return action != null ? action.getName() : "";
    }

    private int getToggleState() {
        if (ability == null || !ability.isToggleable()) return 0;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return 0;
        PlayerData playerData = PlayerData.get(mc.thePlayer);
        if (playerData == null || playerData.abilityData == null) return 0;
        return playerData.abilityData.getToggleState(abilityKey);
    }

    private int getNameColor() {
        if (ability != null && ability.isToggleable()) {
            return getToggleState() > 0 ? 0xFF55FF55 : 0xFFFF5555;
        }
        return 0xFFFFFFFF;
    }

    public void setSelectedState(boolean newSelectState) {
        if (!isSelected && newSelectState) {
            startSelectTime = (long)(Minecraft.getSystemTime() - (Math.max(selectScale - 1, 0)) * SELECT_TIME);
            // Start name fade-in
            nameFadingIn = true;
            nameFadeStartTime = Minecraft.getSystemTime();
        }
        if (isSelected && !newSelectState) {
            stopSelectTime = (long)(Minecraft.getSystemTime() - (1 - selectScale) * SELECT_TIME);
            // Start name fade-out
            nameFadingIn = false;
            nameFadeStartTime = Minecraft.getSystemTime();
        }
        isSelected = newSelectState;
    }

    /** Call every frame to update this slot's nameAlpha. */
    public void updateNameFade() {
        long now = Minecraft.getSystemTime();
        float t = (float)(now - nameFadeStartTime) / NAME_FADE_DURATION;
        t = Math.min(1f, Math.max(0f, t));
        if (nameFadingIn) {
            nameAlpha = t;
        } else {
            nameAlpha = 1f - t;
        }
    }

    public float getSegmentScale() {
        float updateTime;
        if (isSelected) {
            updateTime = (float)(Minecraft.getSystemTime() - startSelectTime) / SELECT_TIME;
            updateTime = Math.min(updateTime, 1);
            selectScale = (float) easeInOutCirc(updateTime);
        } else {
            updateTime = (float)(Minecraft.getSystemTime() - stopSelectTime) / SELECT_TIME;
            updateTime = Math.min(updateTime, 1);
            selectScale = (float) easeInOutCirc(1 - updateTime);
        }
        selectScale = Math.min(1, Math.max(selectScale, 0));
        return selectScale;
    }
}
