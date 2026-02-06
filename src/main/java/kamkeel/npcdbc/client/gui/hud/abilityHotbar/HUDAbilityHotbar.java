package kamkeel.npcdbc.client.gui.hud.abilityHotbar;

import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.data.AbilityWheelData;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.mixins.late.IPlayerDBCInfo;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.AbilityController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import noppes.npcs.client.ClientCacheHandler;
import noppes.npcs.controllers.data.PlayerData;

import java.util.List;

/**
 * HUD overlay that shows the player's 6 ability wheel slots as a vertical hotbar
 * on the left side of the screen. Shows cooldown indicators.
 */
public class HUDAbilityHotbar extends Gui {
    private static HUDAbilityHotbar instance;

    public AbilityHotbarSlot[] hotbarSlots = new AbilityHotbarSlot[6];
    public int selectedSlot = -1;

    private Minecraft mc;
    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 500; // ms between ability data refreshes

    private HUDAbilityHotbar() {
        mc = Minecraft.getMinecraft();
        for (int i = 0; i < 6; i++) {
            hotbarSlots[i] = new AbilityHotbarSlot(this, i);
        }
    }

    public static HUDAbilityHotbar getInstance() {
        if (instance == null) {
            instance = new HUDAbilityHotbar();
        }
        return instance;
    }

    /**
     * Check if the hotbar should be rendered.
     */
    public boolean shouldRender() {
        if (!ConfigDBCClient.EnableAbilityHotbar) return false;
        if (mc.thePlayer == null) return false;
        if (mc.currentScreen != null) return false;
        return true;
    }

    /**
     * Update ability data from the wheel configuration.
     */
    public void updateAbilities() {
        PlayerData playerData = ClientCacheHandler.playerData;
        if (playerData == null) return;

        // Get ability wheel data from PlayerDBCInfo
        PlayerDBCInfo dbcInfo = ((IPlayerDBCInfo) playerData).getPlayerDBCInfo();
        if (dbcInfo != null) {
            boolean hasWheelConfig = false;
            for (int i = 0; i < 6; i++) {
                if (!dbcInfo.abilityWheel[i].isEmpty()) {
                    hasWheelConfig = true;
                    break;
                }
            }

            if (hasWheelConfig) {
                for (int i = 0; i < 6; i++) {
                    AbilityWheelData wheelData = dbcInfo.abilityWheel[i];
                    String key = wheelData.isEmpty() ? null : wheelData.abilityKey;
                    Ability ability = null;
                    if (key != null && AbilityController.Instance != null) {
                        ability = AbilityController.Instance.resolveAbility(key);
                    }
                    hotbarSlots[i].setAbility(key, ability);
                }
                return;
            }
        }

        // Fallback: load from unlocked abilities
        if (playerData.abilityData != null) {
            List<String> abilities = playerData.abilityData.getUnlockedAbilityList();
            for (int i = 0; i < 6; i++) {
                if (i < abilities.size()) {
                    String key = abilities.get(i);
                    Ability ability = AbilityController.Instance != null ?
                        AbilityController.Instance.resolveAbility(key) : null;
                    hotbarSlots[i].setAbility(key, ability);
                } else {
                    hotbarSlots[i].setAbility(null, null);
                }
            }
        }

        // Update selected index
        if (playerData.abilityData != null) {
            selectedSlot = playerData.abilityData.getSelectedIndex();
            if (selectedSlot >= 0 && selectedSlot < 6) {
                for (int i = 0; i < 6; i++) {
                    hotbarSlots[i].setSelectedState(i == selectedSlot);
                }
            }
        }
    }

    /**
     * Called every render tick to draw the hotbar.
     */
    public void onRender() {
        if (!shouldRender()) return;

        // Periodically update ability data
        long currentTime = Minecraft.getSystemTime();
        if (currentTime - lastUpdateTime > UPDATE_INTERVAL) {
            updateAbilities();
            lastUpdateTime = currentTime;
        }

        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

        // Calculate cooldown progress
        float cooldownProgress = 0;
        PlayerData playerData = ClientCacheHandler.playerData;
        if (playerData != null && playerData.abilityData != null) {
            // For now, we'll show a simple cooldown indicator
            // The actual cooldown system tracks a universal cooldown
            if (playerData.abilityData.isOnCooldown()) {
                // Estimate cooldown progress based on remaining time
                // This is a simplification - ideally we'd track start time and duration
                cooldownProgress = 0.5f; // Show as 50% for now when on cooldown
            }
        }

        // Draw each slot
        for (int i = 0; i < 6; i++) {
            hotbarSlots[i].draw(mc, sr, cooldownProgress);
        }
    }

    /**
     * Get cooldown progress for a specific ability (0 = ready, 1 = full cooldown).
     * Returns -1 if ability is not on cooldown.
     */
    public float getCooldownProgress(int slotIndex) {
        PlayerData playerData = ClientCacheHandler.playerData;
        if (playerData == null || playerData.abilityData == null) return 0;

        // Universal cooldown check
        if (playerData.abilityData.isOnCooldown()) {
            return 0.5f; // Simplified - actual implementation would track time
        }
        return 0;
    }

    /**
     * Force refresh ability data.
     */
    public void refresh() {
        updateAbilities();
        lastUpdateTime = Minecraft.getSystemTime();
    }
}
