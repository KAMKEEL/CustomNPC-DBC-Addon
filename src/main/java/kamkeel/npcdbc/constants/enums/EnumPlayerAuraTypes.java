package kamkeel.npcdbc.constants.enums;

import java.util.ArrayList;
import java.util.List;

public enum EnumPlayerAuraTypes {
    None(""),
    SaiyanGod("ssgod"),
    SaiyanBlue("ssb"),
    SaiyanBlueKK("ssbkk"),
    SaiyanBlueEvo("shinka"),
    SaiyanRose("ssrose"),
    SaiyanRoseEvo("ssroseevo"),
    UI("ui"),
    GoD("godofdestruction");

    private final String name;

    private EnumPlayerAuraTypes(String name) {
        this.name = name;
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

    public String getName() {
        return this.name;
    }

}
