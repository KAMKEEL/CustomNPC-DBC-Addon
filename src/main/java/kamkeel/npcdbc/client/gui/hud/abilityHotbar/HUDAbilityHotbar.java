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
 * HUD overlay that shows all 12 ability slots as a vertical or horizontal carousel.
 * Shows 5 slots at a time: small - medium - selected - medium - small.
 * All 12 slots are always shown, including empty ones.
 * Scrolling onto an empty slot clears the selection (sends "").
 * Selection is done via Ctrl+Scroll.
 */
public class HUDAbilityHotbar extends Gui {
    private static HUDAbilityHotbar instance;

    public static final int TOTAL_SLOTS = 12;

    public AbilityHotbarSlot[] hotbarSlots = new AbilityHotbarSlot[TOTAL_SLOTS];

    // The slot index (0-11) that is currently at the center of the carousel.
    // -1 means nothing selected yet; defaults to 0 on first render.
    private int centerSlot = 0;

    private Minecraft mc;
    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 500;

    // --- Smooth scroll state ---
    private float scrollOffset = 0f;
    private long scrollStartTime = 0;
    private static final long SCROLL_DURATION = 150;

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
                if (ability == null && action == null) key = null;
            }
            hotbarSlots[i].setAbility(key, ability, action);
        }

        // Sync selected state from server
        if (playerData.abilityData != null) {
            String selectedKey = playerData.abilityData.getSelectedAbilityKey();
            for (int i = 0; i < TOTAL_SLOTS; i++) {
                AbilityWheelData wheelData = dbcInfo.abilityWheel[i];
                boolean isSelected = selectedKey != null && !wheelData.isEmpty()
                    && selectedKey.equals(wheelData.abilityKey);
                hotbarSlots[i].setSelectedState(isSelected);
            }
        }
    }

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

        // scroll up (delta > 0) -> previous slot, scroll down -> next slot
        int direction = delta > 0 ? -1 : 1;
        int nextSlot = (centerSlot + direction + TOTAL_SLOTS) % TOTAL_SLOTS;

        // Kick off smooth scroll animation
        scrollOffset = direction;
        scrollStartTime = Minecraft.getSystemTime();
        centerSlot = nextSlot;

        // Update selected states locally
        String nextKey = hotbarSlots[nextSlot].abilityKey;
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            hotbarSlots[i].setSelectedState(i == nextSlot && nextKey != null);
        }

        // Send to server: empty string if slot has no ability
        String keyToSend = nextKey != null ? nextKey : "";
        DBCPacketHandler.Instance.sendToServer(new DBCSelectAbility(keyToSend));

        return true;
    }

    private void updateScrollAnimation() {
        if (scrollOffset == 0f) return;

        long now = Minecraft.getSystemTime();
        float t = (float)(now - scrollStartTime) / SCROLL_DURATION;
        t = Math.min(t, 1f);

        float eased = easeOutCubic(t);
        float initialOffset = scrollOffset < 0 ? -1f : 1f;
        scrollOffset = initialOffset * (1f - eased);

        if (t >= 1f) scrollOffset = 0f;
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
        boolean isHorizontal = ConfigDBCClient.AbilityHotbarHorizontal;

        int baseSize = 24;
        int spacing = 28;

        int screenW = sr.getScaledWidth();
        int screenH = sr.getScaledHeight();

        int vanillaHotbarLeft = screenW / 2 - 91;
        int carouselHalfWidth = 2 * spacing;
        int anchorX = isHorizontal ? vanillaHotbarLeft - carouselHalfWidth - spacing / 2 : 18;
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

        // Draw offsets -2 to +2 relative to centerSlot
        for (int offset = -2; offset <= 2; offset++) {
            // Wrap around the 12 slots
            int slotIndex = ((centerSlot + offset) % TOTAL_SLOTS + TOTAL_SLOTS) % TOTAL_SLOTS;

            float visualPos = offset + scrollOffset;
            float absDist = Math.abs(visualPos);

            float scale;
            if (absDist <= 1f) {
                scale = 1.0f - 0.25f * absDist;
            } else {
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

            if (absDist > 2.5f) continue;

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
