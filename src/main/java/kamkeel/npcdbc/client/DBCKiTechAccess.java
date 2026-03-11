package kamkeel.npcdbc.client;

import JinRyuu.DragonBC.common.DBCKiTech;

import java.lang.reflect.Field;

/**
 * Reflection-based access to DBCKiTech's private static fields.
 * Used by DBCMoveHelper for the enhanced movement system.
 */
public class DBCKiTechAccess {
    private static final Field f_dodge_forwDash_STE;
    private static final Field f_jumpToFly;
    private static final Field f_sec;
    private static final Field f_sent;

    static {
        try {
            f_dodge_forwDash_STE = DBCKiTech.class.getDeclaredField("dodge_forwDash_STE");
            f_dodge_forwDash_STE.setAccessible(true);
            f_jumpToFly = DBCKiTech.class.getDeclaredField("jumpToFly");
            f_jumpToFly.setAccessible(true);
            f_sec = DBCKiTech.class.getDeclaredField("sec");
            f_sec.setAccessible(true);
            f_sent = DBCKiTech.class.getDeclaredField("sent");
            f_sent.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to access DBCKiTech private fields", e);
        }
    }

    public static boolean getDodgeForwDashSTE() {
        try { return f_dodge_forwDash_STE.getBoolean(null); }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }

    public static void setDodgeForwDashSTE(boolean v) {
        try { f_dodge_forwDash_STE.setBoolean(null, v); }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }

    public static boolean getJumpToFly() {
        try { return f_jumpToFly.getBoolean(null); }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }

    public static void setJumpToFly(boolean v) {
        try { f_jumpToFly.setBoolean(null, v); }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }

    public static float getSec() {
        try { return f_sec.getFloat(null); }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }

    public static void setSec(float v) {
        try { f_sec.setFloat(null, v); }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }

    public static float getSent() {
        try { return f_sent.getFloat(null); }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }

    public static void setSent(float v) {
        try { f_sent.setFloat(null, v); }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }
}
