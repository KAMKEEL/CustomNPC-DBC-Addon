package kamkeel.npcdbc.util;

import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.i.ExtendedPlayer;
import kamkeel.npcdbc.constants.DBCAttribute;
import kamkeel.npcdbc.constants.DBCSettings;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import static JinRyuu.JRMCore.JRMCoreH.*;
import static kamkeel.npcdbc.util.DBCUtils.*;

/**
 * Snapshot of a player's DBC combat state. Built once per damage calculation
 * and passed to all helper methods, replacing the duplicated 30-line extraction
 * blocks and eliminating 18+ parameter method signatures.
 */
public class DBCPlayerContext {

    // ==================== CORE REFERENCES ====================
    public final EntityPlayer player;
    public final DBCData data;

    // ==================== IDENTITY ====================
    public final int race;
    public final int state;
    public final int state2;
    public final int classID;
    public final int powerType;
    public final int release;

    // ==================== RESOURCES ====================
    public final int currentEnergy;
    public final int currentStamina;

    // ==================== DBC DATA STRINGS ====================
    public final String racialSkills;
    public final int arcReserve;
    public final String absorption;
    public final String statusEffects;

    // ==================== ATTRIBUTES & SKILLS ====================
    public final int[] attributes;
    public final String[] skills;

    // ==================== STATUS FLAGS ====================
    public final boolean isMajin;
    public final boolean isLegendary;
    public final boolean isMystic;
    public final boolean isKaioken;
    public final boolean isUltraInstinct;
    public final boolean isGoD;
    public final boolean isFused;

    // ==================== COMBAT STATE ====================
    public final boolean isBlocking;
    public final boolean isChargingKi;

    private DBCPlayerContext(EntityPlayer player) {
        this.player = player;
        this.data = DBCData.get(player);

        NBTTagCompound nbt = nbt(player, "pres");
        this.race = nbt.getByte("jrmcRace");
        this.state = nbt.getByte("jrmcState");
        this.state2 = nbt.getByte("jrmcState2");
        this.classID = nbt.getByte("jrmcClass");
        this.powerType = nbt.getByte("jrmcPwrtyp");
        this.release = getByte(player, "jrmcRelease");

        this.currentEnergy = getInt(player, "jrmcEnrgy");
        this.currentStamina = getInt(player, "jrmcStamina");

        this.racialSkills = getString(player, "jrmcSSltX");
        this.arcReserve = getInt(player, "jrmcArcRsrv");
        this.absorption = getString(player, "jrmcMajinAbsorptionData");
        this.statusEffects = getString(player, "jrmcStatusEff");

        this.attributes = PlyrAttrbts(player);
        this.skills = PlyrSkills(player);

        this.isMajin = StusEfcts(12, statusEffects);
        this.isLegendary = StusEfcts(14, statusEffects);
        this.isMystic = StusEfcts(13, statusEffects);
        this.isKaioken = StusEfcts(5, statusEffects);
        this.isUltraInstinct = StusEfcts(19, statusEffects);
        this.isGoD = StusEfcts(20, statusEffects);
        this.isFused = StusEfcts(10, statusEffects) || StusEfcts(11, statusEffects);

        ExtendedPlayer props = ExtendedPlayer.get(player);
        this.isBlocking = props.getBlocking() == 1;
        this.isChargingKi = data.stats.isChargingKiAttack();
    }

    /**
     * Create a new context snapshot for the given player.
     */
    public static DBCPlayerContext create(EntityPlayer player) {
        return new DBCPlayerContext(player);
    }

    /**
     * Get a form-modified attribute value (applies state, racial skills,
     * release, status effects, etc.).
     */
    public int getModifiedAttribute(int attrIndex) {
        return JRMCoreH.getPlayerAttribute(player, attributes, attrIndex,
            state, state2, race, racialSkills, release, arcReserve,
            isLegendary, isMajin, isKaioken, isMystic, isUltraInstinct, isGoD,
            powerType, skills, isFused, absorption);
    }

    /**
     * Whether this player is a fusion spectator (can't deal damage).
     */
    public boolean isFusionSpectator() {
        return data.isFusionSpectator();
    }

    /**
     * Whether the player's power type uses ki.
     */
    public boolean isPowerTypeKi() {
        return JRMCoreH.isPowerTypeKi(powerType);
    }
}
