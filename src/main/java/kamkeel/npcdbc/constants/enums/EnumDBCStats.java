package kamkeel.npcdbc.constants.enums;

public enum EnumDBCStats {
    MELEE("Melee"),
    DEFENSE("Defense"),
    BODY("Body"),
    STAMINA("Stamina"),
    ENERGY_POWER("EnergyPower"),
    ENERGY_POOL("EnergyPool"),
    MAX_SKILLS("MaxSkills"),
    SPEED("Speed"),
    REGEN_RATE_BODY("RegenRateBody"),
    REGEN_RATE_STAMINA("RegenRateStamina"),
    REGEN_RATE_ENERGY("RegenRateEnergy"),
    FLY_SPEED("FlySpeed");

    public final String displayName;

    EnumDBCStats(String displayName) {
        this.displayName = displayName;
    }

    public static String[] getDisplayNames() {
        EnumDBCStats[] values = values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++)
            names[i] = values[i].displayName;
        return names;
    }
}
