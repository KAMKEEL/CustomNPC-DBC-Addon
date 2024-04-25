package kamkeel.npcdbc.constants.enums;

import static kamkeel.npcdbc.config.ConfigCapsules.*;

public enum EnumMiscCapsules {

    KO(0, "KO", KOCooldown),
    Revive(1, "Revive", ReviveCooldown),
    Heat(2, "Heat", HeatCooldown),
    PowerPoint(3, "PowerPoint", PowerPointCooldown);

    private final int meta;
    private final String name;
    private final int cooldown;


    private EnumMiscCapsules(int meta, String name, int cooldown){
        this.meta = meta;
        this.name = name;
        this.cooldown = cooldown;
    }

    public int getMeta(){
        return this.meta;
    }

    public String getName(){
        return this.name;
    }

    public int getCooldown(){
        return this.cooldown;
    }

    public static int count(){
        return values().length;
    }
}
