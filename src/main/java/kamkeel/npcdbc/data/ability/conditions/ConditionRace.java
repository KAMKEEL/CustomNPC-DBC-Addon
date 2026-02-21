package kamkeel.npcdbc.data.ability.conditions;

import kamkeel.npcdbc.constants.enums.EnumDBCRaces;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcs.controllers.data.ability.conditions.AbilityCondition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.gui.builder.FieldDef;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.List;

public class ConditionRace extends AbilityCondition {
    private EnumDBCRaces race = EnumDBCRaces.HUMAN;

    public ConditionRace() {
        this.typeId = "condition.npcdbc.race";
        this.name = "condition.npcdbc.race";
    }

    @Override
    public boolean checkEntity(EntityLivingBase entity) {
        if (entity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface) entity;

            DBCDisplay display = ((INPCDisplay) npc.display).getDBCDisplay();
            if (!display.isEnabled()) return false;

            return display.getRace() == getRace().ordinal();
        }

        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;

            DBCData data = DBCData.get(player);

            return data.Race == getRace().ordinal();
        }

        return false;
    }

    @Override
    public void getConditionDefinitions(List<FieldDef> defs) {
        defs.add(FieldDef.enumField("condition.race_id", EnumDBCRaces.class, this::getRace, this::setRace));
    }

    @Override
    public void writeTypeNBT(NBTTagCompound nbt) {
        nbt.setInteger("race", race.ordinal());
    }

    @Override
    public void readTypeNBT(NBTTagCompound nbt) {
        race = EnumDBCRaces.fromOrdinal(nbt.getInteger("race"));
    }

    public EnumDBCRaces getRace() {
        return race;
    }

    public void setRace(EnumDBCRaces race) {
        this.race = race;
    }
}
