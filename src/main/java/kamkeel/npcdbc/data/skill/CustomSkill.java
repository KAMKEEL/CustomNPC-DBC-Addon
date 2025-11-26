package kamkeel.npcdbc.data.skill;

import kamkeel.npcdbc.api.skill.ICustomSkill;
import kamkeel.npcdbc.controllers.SkillController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.entity.IPlayer;

public class CustomSkill implements ICustomSkill {
    public int id;
    public String stringLiteralId, displayName;

    private int maxLevel;

    private int[] tpCosts;
    private int[] mindCosts;

    public CustomSkill() {

    }

    public CustomSkill(int ID, String stringID, int maxLevel, int[] tpCosts, int[] mindCosts) {
        this.id = ID;
        this.stringLiteralId = stringID;
        this.maxLevel = Math.min(Math.max(maxLevel, 1), 10);

        this.tpCosts = tpCosts;
        this.mindCosts = mindCosts;

        updateSkillCosts();
    }

    private void updateSkillCosts() {
        int[] oldTP = tpCosts;
        int[] oldMind = mindCosts;

        tpCosts = new int[maxLevel];
        mindCosts = new int[maxLevel];

        if (oldTP != null && oldTP.length != 0) {
            for (int i = 0; i < tpCosts.length; i++) {
                int j = Math.max(Math.min(i, oldTP.length-1), 0);
                tpCosts[i] = oldTP[j];
            }
        }

        if (oldMind != null && oldMind.length != 0) {
            for (int i = 0; i < mindCosts.length; i++) {
                int j = Math.max(Math.min(i, oldMind.length-1), 0);
                mindCosts[i] = oldMind[j];
            }
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getStringId() {
        return this.stringLiteralId;
    }

    @Override
    public String getDisplayName() {
        if (displayName == null)
            return stringLiteralId;
        return displayName;
    }

    @Override
    public void setDisplayName(String name) {
        this.displayName = name;
    }

    @Override
    public int getTPCost(int level) {
        level = Math.max(Math.min(1, level), getMaxLevel());
        return tpCosts[level-1];
    }

    @Override
    public int getMindCost(int level) {
        level = Math.max(Math.min(1, level), getMaxLevel());
        return mindCosts[level-1];
    }

    @Override
    public int getMaxLevel() {
        return this.maxLevel;
    }

    @Override
    public void setMaxLevel(int level) {
        this.maxLevel = level;
    }

    @Override
    public int getTotalTPCost(int level) {
        int sum = 0;
        for (int i = 1; i <= this.getMaxLevel(); i++) {
            sum += getTPCost(i);
        }
        return sum;
    }

    @Override
    public int getTotalMindCost(int level) {
        int sum = 0;
        for (int i = 1; i <= this.getMaxLevel(); i++) {
            sum += getMindCost(i);
        }
        return sum;
    }

    @Override
    public boolean doesPlayerHaveSkill(IPlayer player) {
        return doesPlayerHaveSkill(player, 1);
    }

    @Override
    public boolean doesPlayerHaveSkill(IPlayer player, int level) {
        DBCData data = dataForIPlayer(player);
        SkillContainer container = data.customSkills.get(id);
        if (container == null)
            return false;

        return container.getLevel() >= level;
    }

    @Override
    public void teachPlayerSkill(IPlayer player) {
        teachPlayerSkill(player, 1);
    }

    @Override
    public void teachPlayerSkill(IPlayer player, int level) {
        DBCData data = dataForIPlayer(player);
        SkillContainer container = data.customSkills.get(id);

        if (container == null) {
            container = new SkillContainer(player, this, level);
            data.customSkills.put(id, container);
        } else {
            container.setLevel(level);
        }

    }

    @Override
    public void setTPCostsArray(int[] array) {
        this.tpCosts = array;
        updateSkillCosts();
    }

    @Override
    public void setMindCostsArray(int[] array) {
        this.mindCosts = array;
        updateSkillCosts();
    }

    @Override
    public void save() {
        SkillController.Instance.saveSkill(this);
    }

    public static DBCData dataForIPlayer(IPlayer player) {
        return DBCData.getData((EntityPlayer) player.getMCEntity());
    }

    public void readFromNBT(NBTTagCompound tag) {
        id = tag.getInteger("id");
        stringLiteralId = tag.getString("s_id");
        if (tag.hasKey("name"))
            displayName = tag.getString("name");
        maxLevel = tag.getInteger("maxLevel");
        tpCosts = tag.getIntArray("tpCosts");
        mindCosts = tag.getIntArray("mindCosts");
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound comp = new NBTTagCompound();
        comp.setInteger("id", id);
        comp.setString("s_id", stringLiteralId);
        if (displayName != null)
            comp.setString("name", displayName);
        comp.setInteger("maxLevel", maxLevel);
        comp.setIntArray("tpCosts", tpCosts);
        comp.setIntArray("mindCosts", mindCosts);

        return comp;
    }
}
