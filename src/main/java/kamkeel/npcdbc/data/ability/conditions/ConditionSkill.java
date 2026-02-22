package kamkeel.npcdbc.data.ability.conditions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.api.skill.ICustomSkill;
import kamkeel.npcdbc.client.gui.component.SubGuiSelectSkill;
import kamkeel.npcdbc.constants.DBCSkills;
import kamkeel.npcdbc.controllers.SkillController;
import kamkeel.npcdbc.data.ability.DBCAbilityFieldProvider;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcs.controllers.data.ability.enums.UserType;
import kamkeel.npcs.controllers.data.ability.conditions.AbilityCondition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.gui.builder.FieldDef;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.List;

public class ConditionSkill extends AbilityCondition {
    private int skillId = -1;
    private int skillLevel = 0;
    private boolean isCustom = false;

    public ConditionSkill() {
        this.typeId = "condition.npcdbc.skill";
        this.name = "condition.npcdbc.skill";
        this.userType = UserType.PLAYER_ONLY;
    }

    @Override
    protected boolean checkEntity(EntityLivingBase entity) {
        if (entity instanceof EntityNPCInterface) return false;
        if (!isSkillValid(getSkillId())) return false;

        EntityPlayer player = (EntityPlayer) entity;
        DBCData data = DBCData.get(player);

        if (isCustom()) {
            return data.hasCustomSkill(skillId) && (skillLevel == 0 || data.getCustomSkillLevel(skillId) == skillLevel);
        } else {
            return data.hasSkill(skillId) && (skillLevel == 0 || data.getSkillLevel(skillId) == skillLevel);
        }
    }

    private boolean isSkillValid(int id) {
        if (id <= 0) return false;

        if (isCustom()) {
            return SkillController.Instance.getSkill(id) != null;
        } else {
            return id < DBCSkills.values().length;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getConditionDefinitions(List<FieldDef> defs) {
        defs.add(DBCAbilityFieldProvider.skillSubGui("condition.skill_id", this::getSkillId, this::setSkillId,
            () -> isCustom() ? SubGuiSelectSkill.MODE_CUSTOM : SubGuiSelectSkill.MODE_DBC,
            mode -> setCustom(mode == SubGuiSelectSkill.MODE_CUSTOM)));

        defs.add(FieldDef.intField("condition.skill_level", this::getSkillLevel, this::setSkillLevel).range(0, 10));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getConditionSummary() {
        String filterLabel = StatCollector.translateToLocal(getFilter().toString());
        String skillName = "None";
        if (skillId > 0) {
            if (isCustom()) {
                ICustomSkill skill = SkillController.Instance.getSkill(skillId);
                skillName = skill != null ? skill.getStringId() : "ID:" + skillId;
            } else {
                skillName = "DBC Skill " + skillId;
            }
        }
        return "[" + filterLabel + "] Skill: " + skillName;
    }

    @Override
    public void writeTypeNBT(NBTTagCompound nbt) {
        nbt.setInteger("skillId", skillId);
        nbt.setInteger("skillLevel", skillLevel);
        nbt.setBoolean("isCustom", isCustom);
    }

    @Override
    public void readTypeNBT(NBTTagCompound nbt) {
        skillId = nbt.getInteger("skillId");
        skillLevel = nbt.getInteger("skillLevel");
        isCustom = nbt.getBoolean("isCustom");
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        if (!isSkillValid(skillId)) return;
        this.skillId = skillId;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(int skillLevel) {
        this.skillLevel = skillLevel;
    }

    private boolean isCustom() {
        return isCustom;
    }

    private void setCustom(boolean custom) {
        isCustom = custom;
    }
}
