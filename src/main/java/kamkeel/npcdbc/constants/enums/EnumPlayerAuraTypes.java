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
    UltimateArco("ultimate"),
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

    public static int getManualAuraColor(EnumPlayerAuraTypes playerAuraTypes) {
        int clr = -1;
        if (playerAuraTypes == EnumPlayerAuraTypes.GoD) {
            clr = 12464847;
        } else if (playerAuraTypes == EnumPlayerAuraTypes.UI) {
            clr = 15790320;
        } else if (playerAuraTypes == EnumPlayerAuraTypes.SaiyanRose) {
            clr = 7536661;
        } else if (playerAuraTypes == EnumPlayerAuraTypes.SaiyanRoseEvo) {
            clr = 14030412;
        } else if (playerAuraTypes == EnumPlayerAuraTypes.SaiyanBlue || playerAuraTypes == EnumPlayerAuraTypes.SaiyanBlueKK) {
            clr = 2805230;
        } else if (playerAuraTypes == EnumPlayerAuraTypes.SaiyanBlueEvo) {
            clr = 32767;
        } else if (playerAuraTypes == EnumPlayerAuraTypes.SaiyanGod) {
            clr = 16761125;
        } else if (playerAuraTypes == EnumPlayerAuraTypes.UltimateArco) {
            clr = 16430355;
        }
        return clr;
    }

    public static boolean isBlue(EnumPlayerAuraTypes type) {
        if (type == SaiyanBlue || type == SaiyanBlueEvo || type == SaiyanRose || type == SaiyanRoseEvo)
            return true;
        return false;
    }

    public String getName() {
        return this.name;
    }

}
