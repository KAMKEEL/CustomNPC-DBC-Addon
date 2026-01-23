package kamkeel.npcdbc.constants.enums;

import static kamkeel.npcdbc.config.ConfigCapsules.AbsorptionCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.ExhaustedCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.HeatCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.KOCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.NoFuseCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.PowerPointCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.ReviveCooldown;
import static kamkeel.npcdbc.config.ConfigCapsules.StrainCooldown;

public enum EnumMiscCapsules {

    KO("KO", KOCooldown),
    Revive("Revive", ReviveCooldown),
    Heat("Heat", HeatCooldown),
    PowerPoint("PowerPoint", PowerPointCooldown),
    Absorption("Absorption", AbsorptionCooldown),
    Strain("Strain", StrainCooldown),
    NoFuse("NoFuse", NoFuseCooldown),
    Exhausted("Exhausted", ExhaustedCooldown);


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
