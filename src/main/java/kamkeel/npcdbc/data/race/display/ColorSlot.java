package kamkeel.npcdbc.data.race.display;

public class ColorSlot {
    public static final String EYES = "eye";
    public static final String LEFT_EYE = "lefteye";
    public static final String RIGHT_EYE = "righteye";
    public static final String HAIR = "hair";
    public static final String FUR = "fur";
    public static final String BODY_CM = "bodycm";
    public static final String BODY_C1 = "bodyc1";
    public static final String BODY_C2 = "bodyc2";
    public static final String BODY_C3 = "bodyc3";

    public final String id;
    public final String displayName;

    public ColorSlot(String id, String displayName) {
        this.id = id.toLowerCase();
        this.displayName = displayName;
    }
}
