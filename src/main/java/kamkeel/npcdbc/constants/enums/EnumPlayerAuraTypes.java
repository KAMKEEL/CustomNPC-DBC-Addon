package kamkeel.npcdbc.constants.enums;

import java.util.ArrayList;
import java.util.List;

public enum EnumPlayerAuraTypes {
    None(""),
    SaiyanGod("ssgod"),
    GoD("godofdestruction"),
    UI("ui"),
    SaiyanBlue("ssb"),
    SaiyanRose("ssrose"),
    SaiyanRoseEvo("ssroseevo"),
    SaiyanBlueKK("ssbkk"),
    Shinka("shinka");

    private final String name;

    private EnumPlayerAuraTypes(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public static EnumPlayerAuraTypes getEnumFromName(String name) {
        for (EnumPlayerAuraTypes auraType : EnumPlayerAuraTypes.values()) {
            if (auraType.getName().equalsIgnoreCase(name)) {
                return auraType;
            }
        }
        return null;
    }

    public static List<String> getAllNames() {
        List<String> names = new ArrayList<>();
        for (EnumPlayerAuraTypes auraType : EnumPlayerAuraTypes.values()) {
            names.add(auraType.getName());
        }
        return names;
    }

}
