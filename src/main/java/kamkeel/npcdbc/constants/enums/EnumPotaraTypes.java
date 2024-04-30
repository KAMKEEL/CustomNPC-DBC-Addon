package kamkeel.npcdbc.constants.enums;

import static kamkeel.npcdbc.config.ConfigCapsules.*;

public enum EnumPotaraTypes {

    One(0, "One", 15),
    Two(1, "Two", 15),
    Three(2, "Three", 15);

    private final int meta;
    private final String name;
    private final int length;


    private EnumPotaraTypes(int meta, String name, int length){
        this.meta = meta;
        this.name = name;
        this.length = length;
    }

    public int getMeta(){
        return this.meta;
    }

    public String getName(){
        return this.name;
    }

    public static int count(){
        return values().length;
    }
}
