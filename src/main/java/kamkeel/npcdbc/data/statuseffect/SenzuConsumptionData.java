package kamkeel.npcdbc.data.statuseffect;

import java.util.LinkedList;

public class SenzuConsumptionData {
    private final LinkedList<Long> consumptionTimestamps = new LinkedList<>();
    private long lastConsumptionTimestamp;

    // Add a consumption timestamp for tracking.
    public void addConsumption(long timestamp) {
        consumptionTimestamps.add(timestamp);
        lastConsumptionTimestamp = timestamp;
    }

    // Remove old entries that are outside the defined window.
    public void cleanOldConsumption(long currentTime, int decreaseTime) {
        long timeWindow = decreaseTime * 50L; // Convert ticks to milliseconds
        while (!consumptionTimestamps.isEmpty() && (currentTime - consumptionTimestamps.peek()) > timeWindow) {
            consumptionTimestamps.poll(); // Remove the oldest entry
        }
    }

    // Get the excess consumption amount over the threshold.
    public int getExcessConsumption(int threshold) {
        int currentConsumption = consumptionTimestamps.size();
        return Math.max(0, currentConsumption - threshold);
    }

    // Returns the current consumption count
    public int getCurrentConsumption() {
        return consumptionTimestamps.size();
    }

    // Decrease the consumption count gradually based on defined rate.
    public void decreaseConsumption(int decreaseTime) {
        if (!consumptionTimestamps.isEmpty()) {
            consumptionTimestamps.poll(); // Remove the oldest consumption entry.
        }
    }
}
