package kamkeel.npcdbc.client.gui.hud.abilityHotbar;

import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.data.AbilityWheelData;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.mixins.late.IPlayerDBCInfo;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.ability.DBCSelectAbility;
import kamkeel.npcs.controllers.AbilityController;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.IAbilityAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import noppes.npcs.client.ClientCacheHandler;
import noppes.npcs.controllers.data.PlayerData;
import org.lwjgl.input.Keyboard;

/**
 * HUD overlay that shows the player's 12 ability slots as a vertical or horizontal carousel.
 * Shows 5 slots at a time: small - medium - selected - medium - small.
 * Empty slots are hidden.
 * Selection is done via Ctrl+Scroll, with smooth animation between slots.
 */
public class HUDAbilityHotbar extends Gui {
    private static HUDAbilityHotbar instance;

    public static final int TOTAL_SLOTS = 12;

    public AbilityHotbarSlot[] hotbarSlots = new AbilityHotbarSlot[TOTAL_SLOTS];
    public int selectedSlot = -1;

    private Minecraft mc;
    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 500;

    // --- Smooth scroll state ---
    // visibleSelectedIndex is the "logical" center we're animating toward.
    // scrollOffset is a float in [-1, 1] that represents how far we are
    // mid-animation. 0.0 = settled, -1.0 = fully scrolled up one slot,
    // +1.0 = fully scrolled down one slot.
    private int visibleSelectedIndex = 0;
    private float scrollOffset = 0f;         // current animated offset
    private float scrollTarget = 0f;         // target (always 0 once settled)
    private long scrollStartTime = 0;
    private static final long SCROLL_DURATION = 150; // ms for one slot transition

    // Name alpha is derived per-slot from visualPos during render — no extra state needed.

    private HUDAbilityHotbar() {
        mc = Minecraft.getMinecraft();
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            hotbarSlots[i] = new AbilityHotbarSlot(this, i);
        }
    }

    public static HUDAbilityHotbar getInstance() {
        if (instance == null) {
            instance = new HUDAbilityHotbar();
        }
        return instance;
    }

    public boolean shouldRender() {
        if (!ConfigDBCClient.EnableAbilityHotbar) return false;
        if (mc.thePlayer == null) return false;
        if (mc.currentScreen != null) return false;
        return true;
    }

    public void updateAbilities() {
        PlayerData playerData = ClientCacheHandler.playerData;
        if (playerData == null) return;

        PlayerDBCInfo dbcInfo = ((IPlayerDBCInfo) playerData).getPlayerDBCInfo();
        if (dbcInfo == null) return;

        for (int i = 0; i < TOTAL_SLOTS; i++) {
            AbilityWheelData wheelData = dbcInfo.abilityWheel[i];
            String key = wheelData.isEmpty() ? null : wheelData.abilityKey;
            Ability ability = null;
            IAbilityAction action = null;
            if (key != null && AbilityController.Instance != null) {
                if (wheelData.isChainKey()) {
                    action = AbilityController.Instance.resolveChainedAbility(wheelData.getResolveKey());
                } else {
                    ability = AbilityController.Instance.resolveAbility(key);
                    action = ability;
                }
            }
            hotbarSlots[i].setAbility(key, ability, action);
        }

        if (playerData.abilityData != null) {
            String selectedKey = playerData.abilityData.getSelectedAbilityKey();
            int prevSelected = selectedSlot;
            selectedSlot = -1;
            for (int i = 0; i < TOTAL_SLOTS; i++) {
                AbilityWheelData wheelData = dbcInfo.abilityWheel[i];
                boolean isSelected = selectedKey != null && !wheelData.isEmpty()
                    && selectedKey.equals(wheelData.abilityKey);
                hotbarSlots[i].setSelectedState(isSelected);
                if (isSelected) {
                    selectedSlot = i;
                }
            }
            // If selection changed from outside (e.g. server), sync index
            if (selectedSlot != prevSelected) {
                int[] visible = getVisibleSlotIndices();
                visibleSelectedIndex = getSelectedVisibleIndex(visible);
            }
        }
    }

    /** Returns only non-empty slot indices, in order. */
    public int[] getVisibleSlotIndices() {
        int count = 0;
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            if (hotbarSlots[i].abilityKey != null) count++;
        }
        int[] indices = new int[count];
        int j = 0;
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            if (hotbarSlots[i].abilityKey != null) indices[j++] = i;
        }
        return indices;
    }

    /** Find the index of the selected slot within the visible slots array. */
    public int getSelectedVisibleIndex(int[] visibleIndices) {
        for (int i = 0; i < visibleIndices.length; i++) {
            if (visibleIndices[i] == selectedSlot) return i;
        }
        return visibleIndices.length > 0 ? 0 : -1;
    }

    /**
     * Easing function: ease out cubic — fast start, slows down at end.
     * x in [0,1] -> result in [0,1]
     */
    private float easeOutCubic(float x) {
        float v = 1 - x;
        return 1 - v * v * v;
    }

    /**
     * Called from the scroll event handler whenever the mouse wheel moves.
     * Only acts when Left or Right Ctrl is held.
     *
     * @param delta raw scroll delta — positive = up, negative = down
     * @return true if the event was consumed
     */
    public boolean onScroll(int delta) {
        if (!shouldRender()) return false;
        if (!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && !Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
            return false;
        if (delta == 0) return false;

        int[] visible = getVisibleSlotIndices();
        if (visible.length == 0) return false;
        if (visibleSelectedIndex < 0) return false;

        // scroll up (delta > 0) -> previous slot, scroll down -> next slot
        int direction = delta > 0 ? -1 : 1;
        int nextVI = (visibleSelectedIndex + direction + visible.length) % visible.length;

        String nextKey = hotbarSlots[visible[nextVI]].abilityKey;
        if (nextKey == null) return false;

        // Kick off smooth scroll animation:
        // scrollOffset starts at +direction (one slot away) and will ease to 0.
        // This makes the carousel appear to slide from direction toward center.
        scrollOffset = direction;   // start displaced
        scrollTarget = 0f;          // animate back to center
        scrollStartTime = Minecraft.getSystemTime();

        // Update logical selection
        visibleSelectedIndex = nextVI;
        selectedSlot = visible[nextVI];

        // Update slot selected states locally (optimistic)
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            hotbarSlots[i].setSelectedState(i == selectedSlot);
        }

        // Send to server
        DBCPacketHandler.Instance.sendToServer(new DBCSelectAbility(nextKey));

        return true;
    }

    /** Update animated scroll offset. Called every render frame. */
    private void updateScrollAnimation() {
        if (scrollOffset == scrollTarget) return;

        long now = Minecraft.getSystemTime();
        float t = (float)(now - scrollStartTime) / SCROLL_DURATION;
        t = Math.min(t, 1f);

        float eased = easeOutCubic(t);
        // Interpolate from initial offset (direction) toward 0
        // At t=0, scrollOffset = direction (e.g. 1 or -1)
        // At t=1, scrollOffset = 0
        float initialOffset = scrollOffset < 0 ? -1f : 1f;
        scrollOffset = initialOffset * (1f - eased);

        if (t >= 1f) {
            scrollOffset = 0f;
        }
    }

    public void onRender() {
        if (!shouldRender()) return;

        long currentTime = Minecraft.getSystemTime();
        if (currentTime - lastUpdateTime > UPDATE_INTERVAL) {
            updateAbilities();
            lastUpdateTime = currentTime;
        }

        updateScrollAnimation();

        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

        int[] visibleIndices = getVisibleSlotIndices();
        if (visibleIndices.length == 0) return;

        // Sync visibleSelectedIndex if needed
        if (visibleSelectedIndex < 0 || visibleSelectedIndex >= visibleIndices.length) {
            visibleSelectedIndex = getSelectedVisibleIndex(visibleIndices);
        }

        boolean isHorizontal = false;

        // Base slot diameter and spacing
        int baseSize = 24;
        int spacing = 28;

        int screenW = sr.getScaledWidth();
        int screenH = sr.getScaledHeight();

        int anchorX = isHorizontal ? screenW / 2 : 18;
        int anchorY = isHorizontal ? screenH - 18 : screenH / 2;

        float cooldownProgress = 0;
        PlayerData playerData = ClientCacheHandler.playerData;
        if (playerData != null && playerData.abilityData != null) {
            if (playerData.abilityData.isOnCooldown()) {
                cooldownProgress = 0.5f;
            }
        }

        // Update name fade for all slots every frame
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            hotbarSlots[i].updateNameFade();
        }

        // Draw offsets -2 to +2 relative to center.
        // scrollOffset shifts all slots: when scrollOffset = 1.0, everything is
        // pushed one spacing unit in the positive direction (appears to slide down),
        // and the slot that was at +2 moves out of view naturally.
        for (int offset = -2; offset <= 2; offset++) {
            int vi = visibleSelectedIndex + offset;
            // Wrap around
            vi = ((vi % visibleIndices.length) + visibleIndices.length) % visibleIndices.length;

            int slotIndex = visibleIndices[vi];

            // Visual position: the fractional offset shifts the whole carousel
            float visualPos = offset + scrollOffset;

            // Scale: 1.0 at center, falls off with distance.
            // Uses a smooth curve so intermediate positions look natural.
            // scale(pos) = 1.0 - 0.25*|pos| at pos=1, = 0.75; at pos=2, = 0.5
            float absDist = Math.abs(visualPos);
            float scale;
            if (absDist <= 1f) {
                // Between center and ±1: lerp from 1.0 to 0.75
                scale = 1.0f - 0.25f * absDist;
            } else {
                // Between ±1 and ±2: lerp from 0.75 to 0.5
                scale = 0.75f - 0.25f * (absDist - 1f);
            }
            scale = Math.max(0f, scale);

            int scaledSize = Math.max(1, (int)(baseSize * scale));

            int cx, cy;
            if (isHorizontal) {
                cx = anchorX + (int)(visualPos * spacing);
                cy = anchorY;
            } else {
                cx = anchorX;
                cy = anchorY + (int)(visualPos * spacing);
            }

            // Only draw if within visible range (±2.5 slots)
            if (absDist > 2.5f) continue;

            // Alpha also fades as slot moves toward the edges
            float slotAlpha = absDist > 1.5f ? 1f - (absDist - 1.5f) : 1f;

            boolean isCenter = (offset == 0 && scrollOffset == 0f);
            float slotCooldown = isCenter ? cooldownProgress : 0;

            hotbarSlots[slotIndex].drawCarousel(mc, sr, slotCooldown, cx, cy, scaledSize, isCenter, slotAlpha);
        }
    }

    public void refresh() {
        updateAbilities();
        lastUpdateTime = Minecraft.getSystemTime();
    }
}
