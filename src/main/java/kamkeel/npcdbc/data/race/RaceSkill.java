package kamkeel.npcdbc.data.race;

public class RaceSkill {
    private int maxLevel;
    private int[] tpCosts;
    private int[] mindCosts;

    public RaceSkill(int maxLevel, int[] tpCosts, int[] mindCosts) {
        this.maxLevel = Math.min(Math.max(maxLevel, 1), 10);
        this.tpCosts = normalizeCosts(tpCosts, this.maxLevel);
        this.mindCosts = normalizeCosts(mindCosts, this.maxLevel);
    }

    private int[] normalizeCosts(int[] source, int size) {
        int[] result = new int[size];
        if (source != null && source.length > 0) {
            for (int i = 0; i < size; i++) {
                int j = Math.min(i, source.length - 1);
                result[i] = source[j];
            }
        }
        return result;
    }

    public int getMaxLevel() { return maxLevel; }

    public int getTPCost(int level) {
        level = Math.min(Math.max(1, level), maxLevel);
        return tpCosts[level - 1];
    }

    public int getMindCost(int level) {
        level = Math.min(Math.max(1, level), maxLevel);
        return mindCosts[level - 1];
    }

    public int getTotalTPCost(int level) {
        int cap = Math.min(Math.max(level, 1), maxLevel);
        int sum = 0;
        for (int i = 1; i <= cap; i++) sum += getTPCost(i);
        return sum;
    }

    public int getTotalMindCost(int level) {
        int cap = Math.min(Math.max(level, 1), maxLevel);
        int sum = 0;
        for (int i = 1; i <= cap; i++) sum += getMindCost(i);
        return sum;
    }
}
