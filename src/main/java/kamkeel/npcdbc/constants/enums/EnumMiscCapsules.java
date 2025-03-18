package kamkeel.npcdbc.constants.enums;

import static kamkeel.npcdbc.config.ConfigCapsules.*;

public enum EnumMiscCapsules {

    KO("KO", KOCooldown),
    Revive("Revive", ReviveCooldown),
    Heat("Heat", HeatCooldown),
    PowerPoint("PowerPoint", PowerPointCooldown),
    Absorption("Absorption", AbsorptionCooldown);

    private final String name;
    private final int cooldown;


    EnumMiscCapsules(String name, int cooldown) {
        this.name = name;
        this.cooldown = cooldown;
    }

    public int getMeta() {
        return this.ordinal();
    }

    public String getName() {
        return this.name;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public static int count() {
        return values().length;
    }
}
