package kamkeel.npcdbc.client.gui.dbc.constants;

public class GuiButtonConstants {
    public final static int EXIT = 0;

    public enum ReferenceIDs{
        NEWS(31, 0),

        STAT_SHEET(10, 1),
        SKILLS(11, 2),
        KI_ATTACKS(12, 3),
        TRAINING(17, 6),
        STORY(60, 7),
        GROUP_MANAGEMENT(70, 4),
        SERVER_CONFIGS(40, 5),

        DIFFICULTY(14),
        SERVER_SHOP(80),
        CLIENT_SETTINGS(10000, 9),
        HELP_MENU(10001, 8),
        NOTIFICATIONS(10011, 10);

        private final int guiIDReference;
        private final int iconID;
        ReferenceIDs(int guiID){
            this(guiID, -1);
        }
        ReferenceIDs(int guiID, int iconID){
            this.guiIDReference = guiID;
            this.iconID = iconID;
        }


        public int getGuiID(){
            return guiIDReference;
        }
        public int getIconID(){
            return iconID;
        }
    }
}
