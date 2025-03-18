package kamkeel.npcdbc.client.gui.dbc.constants;

import JinRyuu.JRMCore.JRMCoreH;

public class GuiInfo {
    //How many non-gui referencing button actions exist
    private final static int reference_offset = 20;

    public final static int EXIT = -1;
    public final static int FAMILY_C_ID = -2;
    public final static int YEARS_C_ID = -3;
    public final static int SERVER_SITE_ID = -4;


    public final static String FAMILY_C_TRANSLATION = "jrmc:Family";
    public final static String YEARS_C_TRANSLATION = "jrmc:Calendar";

    public enum ReferenceIDs {
        NEWS(31, 0, "jrmc:News"),

        STAT_SHEET(10, 1, "jrmc:CharSheet"),
        SKILLS(11, 2, "jrmc:Skills"),
        KI_ATTACKS(12, 3, "jrmc:KiTechniques"),
        TRAINING(17, 6, "jrmc:Training"),
        STORY(60, 7, "dbc:SagaSystem"),
        GROUP_MANAGEMENT(70, 4, "jrmc:GroupManager"),
        SERVER_CONFIGS(40, 5, "jrmc:ServerConfig"),

        DIFFICULTY(14, "jrmc:Difficulty"),
        SERVER_SHOP(80, "Server Shop"),
        CLIENT_SETTINGS(10000, 9, "jrmc:CltSettings"),
        HELP_MENU(10001, 8, "jrmc:Help"),
        NOTIFICATIONS(10011, 10, "jrmc:Notifications");

        private final int guiIDReference;
        private final int iconID;
        private final String translation;

        ReferenceIDs(int guiID, String translationName) {
            this(guiID, -1, translationName);
        }

        ReferenceIDs(int guiID, int iconID, String translationName) {
            this.guiIDReference = guiID;
            this.iconID = iconID;
            this.translation = translationName;
        }


        public int getGuiID() {
            return guiIDReference;
        }

        public int getIconID() {
            return iconID;
        }

        public int getButtonId() {
            return -reference_offset - this.ordinal();
        }

        public String getTranslation() {
            return GuiInfo.translateButtonLocale(translation);
        }
    }

    public static String translateButtonLocale(String translation) {
        if (translation.contains(":")) {
            String[] locale = translation.split(":");

            switch (locale[0]) {
                case "dbc":
                case "jrmc":
                    return JRMCoreH.trl(locale[0], locale[1]);
            }
        }

        return translation;
    }
}
