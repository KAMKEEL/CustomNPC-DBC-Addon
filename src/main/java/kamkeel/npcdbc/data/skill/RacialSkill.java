package kamkeel.npcdbc.data.skill;

import net.minecraft.nbt.NBTTagCompound;

public class RacialSkill {
    private int maxLevel;
    private int[] tpCosts;
    private int[] mindCosts;
    private String displayName;
    private String description;

    public RacialSkill() {
        this.maxLevel = 5;
        this.tpCosts = new int[]{10000, 20000, 30000, 40000, 50000};
        this.mindCosts = new int[]{15, 15, 15, 15, 15};
    }

    public RacialSkill(int maxLevel, int[] tpCosts, int[] mindCosts) {
        this.maxLevel = Math.min(Math.max(maxLevel, 1), 10);
        this.tpCosts = tpCosts;
        this.mindCosts = mindCosts;
        normalizeCosts();
    }

    private void normalizeCosts() {
        int[] oldTP = tpCosts;
        int[] oldMind = mindCosts;

        tpCosts = new int[maxLevel];
        mindCosts = new int[maxLevel];

        if (oldTP != null && oldTP.length != 0)
            for (int i = 0; i < tpCosts.length; i++) {
                int j = Math.max(Math.min(i, oldTP.length - 1), 0);
                tpCosts[i] = oldTP[j];
            }

        if (oldMind != null && oldMind.length != 0)
            for (int i = 0; i < mindCosts.length; i++) {
                int j = Math.max(Math.min(i, oldMind.length - 1), 0);
                mindCosts[i] = oldMind[j];
            }
    }

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
        for (int i = 1; i <= cap; i++)
            sum += getTPCost(i);
        return sum;
    }

    public int getTotalMindCost(int level) {
        int cap = Math.min(Math.max(level, 1), maxLevel);
        int sum = 0;
        for (int i = 1; i <= cap; i++)
            sum += getMindCost(i);
        return sum;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int level) {
        this.maxLevel = Math.min(Math.max(level, 1), 10);
        normalizeCosts();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int[] getTPCosts() {
        return tpCosts;
    }

    public int[] getMindCosts() {
        return mindCosts;
    }

    public void setTPCosts(int[] tpCosts) {
        this.tpCosts = tpCosts;
        normalizeCosts();
    }

    public void setMindCosts(int[] mindCosts) {
        this.mindCosts = mindCosts;
        normalizeCosts();
    }

    public void readFromNBT(NBTTagCompound tag) {
        maxLevel = tag.getInteger("maxLevel");
        tpCosts = tag.getIntArray("tpCosts");
        mindCosts = tag.getIntArray("mindCosts");
        if (tag.hasKey("displayName"))
            displayName = tag.getString("displayName");
        if (tag.hasKey("description"))
            description = tag.getString("description");
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("maxLevel", maxLevel);
        tag.setIntArray("tpCosts", tpCosts);
        tag.setIntArray("mindCosts", mindCosts);
        if (displayName != null)
            tag.setString("displayName", displayName);
        if (description != null)
            tag.setString("description", description);
        return tag;
    }
}
