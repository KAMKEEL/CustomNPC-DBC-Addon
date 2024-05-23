package kamkeel.npcdbc.constants.enums;

import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.config.ConfigDBCGameplay;

public enum EnumPotaraTypes {

    One(0, "One", ConfigDBCGameplay.PotaraOneTime, (float) ConfigDBCEffects.TierOneMulti),
    Two(1, "Two", ConfigDBCGameplay.PotaraTwoTime, (float) ConfigDBCEffects.TierTwoMulti),
    Three(2, "Three", ConfigDBCGameplay.PotaraThreeTime, (float) ConfigDBCEffects.TierThreeMulti);

    private final int meta;
    private final String name;
    private final int length;
    private final float multi;

    private EnumPotaraTypes(int meta, String name, int length, float multi){
        this.meta = meta;
        this.name = name;
        this.length = length;
        this.multi = multi;
    }

    public int getMeta(){
        return this.meta;
    }

    public String getName(){
        return this.name;
    }

    public int getLength(){
        return this.length;
    }

    public float getMulti(){
        return this.multi;
    }

    public static int count(){
        return values().length;
    }

    public static EnumPotaraTypes getPotaraFromMeta(int meta){
        for(EnumPotaraTypes potaraTypes : EnumPotaraTypes.values()){
            if(potaraTypes.getMeta() == meta){
                return potaraTypes;
            }
        }
        return One;
    }
}
