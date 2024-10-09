package kamkeel.npcdbc.data.statuseffect;

import java.util.HashMap;
import java.util.UUID;

import static kamkeel.npcdbc.config.ConfigDBCGameplay.DamageRequiredSeconds;
import static kamkeel.npcdbc.config.ConfigDBCGameplay.PercentDamageRequired;

public class DamageTracker {
    private final UUID playerId;
    private final HashMap<Long, Double> damageRecords; // Records of damage taken with timestamps
    private long lastCleanTime;

    public DamageTracker(UUID playerId) {
        this.playerId = playerId;
        this.damageRecords = new HashMap<>();
        this.lastCleanTime = System.currentTimeMillis();
    }

    public void recordDamage(double damage) {
        long currentTime = System.currentTimeMillis();
        damageRecords.put(currentTime, damage); // Record damage with the current timestamp
    }

    public boolean checkForHumanSpirit(int maxHealth) {
        long currentTime = System.currentTimeMillis();
        cleanOldEntries(currentTime); // Clean up old damage records

        double totalDamage = damageRecords.values().stream().mapToDouble(Double::doubleValue).sum();
        double damageThreshold = (maxHealth * PercentDamageRequired) / 100.0; // Calculate required damage

        // Check if the total damage is above the required threshold within the allowed time frame
        return totalDamage >= damageThreshold && (currentTime - lastCleanTime <= DamageRequiredSeconds * 1000L);
    }

    public void cleanOldEntries(long currentTime) {
        // Remove entries older than the specified time frame
        damageRecords.entrySet().removeIf(entry -> (currentTime - entry.getKey()) > (DamageRequiredSeconds * 1000L));
        lastCleanTime = currentTime; // Update last clean time
    }
}
