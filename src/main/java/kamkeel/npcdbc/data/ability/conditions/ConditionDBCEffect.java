package kamkeel.npcdbc.data.ability.conditions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.constants.DBCStatusEffects;
import kamkeel.npcdbc.data.ability.DBCAbilityFieldProvider;
import kamkeel.npcs.controllers.data.ability.conditions.AbilityCondition;
import kamkeel.npcs.controllers.data.ability.enums.UserType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.gui.builder.FieldDef;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

import java.util.List;

public class ConditionDBCEffect extends AbilityCondition {

    private int effectId = -1;
    private int effectType = 0;

    public ConditionDBCEffect() {
        this.typeId = "condition.npcdbc.dbc_effect";
        this.name = "condition.npcdbc.dbc_effect";
        this.userType = UserType.PLAYER_ONLY;
    }

    @Override
    protected boolean checkEntity(EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer)) return false;
        if (!isEffectValid(effectId)) return true;

        DBCStatusEffects effect = DBCStatusEffects.byOrdinal(effectId);
        if (effect == null) return false;

        return effect.has((EntityPlayer) entity);
    }

    private boolean isEffectValid(int id) {
        return id >= 0 && id < DBCStatusEffects.values().length;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getConditionDefinitions(List<FieldDef> defs) {
        defs.add(DBCAbilityFieldProvider.statusEffectSubGui("condition.dbceffect_id",
            this::getEffectId, this::setEffectId,
            this::getEffectType, this::setEffectType));

        defs.add(FieldDef.labelField("condition.dbceffect_type", () -> "\u00A7e" + DBCStatusEffects.getTypeName(getEffectType())));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getConditionSummary() {
        String filterLabel = StatCollector.translateToLocal(getFilter().toString());
        String skillName = StatCollector.translateToLocal("gui.none");
        if (isEffectValid(effectId)) {
            DBCStatusEffects effect = DBCStatusEffects.byOrdinal(effectId);
            skillName = effect != null ? effect.name() : skillName;
        }
        return "[" + filterLabel + "] DBC Effect: " + skillName;
    }

    @Override
    public void writeTypeNBT(NBTTagCompound nbt) {
        nbt.setInteger("effectId", effectId);
        nbt.setInteger("effectType", effectType);
    }

    @Override
    public void readTypeNBT(NBTTagCompound nbt) {
        effectId = nbt.getInteger("effectId");
        effectType = nbt.getInteger("effectType");
    }

    public int getEffectId() {
        return effectId;
    }

    public void setEffectId(int effectId) {
        if (effectId < 0) {
            this.effectId = -1;
            return;
        }
        if (!isEffectValid(effectId)) return;
        this.effectId = effectId;
    }

    public int getEffectType() {
        return effectType;
    }

    public void setEffectType(int effectType) {
        this.effectType = ValueUtil.clamp(effectType, 0, 1);
    }
}
