package kamkeel.npcdbc.constants;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;

public enum DBCStatusEffects {
    Transforming("F", 1),
    Turbo("B", 3),
    Release("A", 4),
    Kaioken("K", 5),
    Majin("J", 12),
    PotentialUnleashed("C", 13),
    Legendary("L", 14),
    Divine("V", 17),
    UltraInstinct("N", 19),
    GodOfDestruction("G", 20),
    Strain("jrmcStrain"),
    Fatigue("jrmcGodStrain"),
    GodPower("jrmcGodPwr"),
    Pain("jrmcGyJ7dp"),
    Heat("jrmcEf8slc"),
    KO("jrmcHar4va");

    private final int effectId;
    private final String stringId;
    private final int type;

    DBCStatusEffects(String stringId, int effectId) {
        this.effectId = effectId;
        this.stringId = stringId;
        this.type = stringId.length() == 1 ? 0 : 1;
    }

    DBCStatusEffects(String stringId) {
        this.effectId = -1;
        this.stringId = stringId;
        this.type = stringId.length() == 1 ? 0 : 1;
    }

    public void set(EntityPlayer player, boolean enable) {
        if (this.type != 0) return;

        DBCData.get(player).setSE(this.effectId, enable);
    }

    public void set(EntityPlayer player, boolean enable, int time) {
        if (this.type != 1) return;

        DBCData.get(player).getRawCompound().setInteger(this.stringId, enable ? time / 5 : 0);
    }

    public boolean has(EntityPlayer player) {
        if (this.type == 0) {
            return DBCData.get(player).containsSE(this.effectId);
        } else {
            return DBCData.get(player).getRawCompound().getInteger(this.stringId) > 0;
        }
    }

    public static DBCStatusEffects byOrdinal(int id) {
        for (DBCStatusEffects effect : values())
            if (id == effect.ordinal())
                return effect;

        return null;
    }

    public int getType() { return type; }

    public static String getTypeName(int type) {
        String name;
        if (type == 0) name = "effect.dbc.permanent";
        else name = "effect.dbc.temporary";

        return StatCollector.translateToLocal(name);
    }
}
