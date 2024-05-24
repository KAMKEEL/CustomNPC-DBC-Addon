package kamkeel.npcdbc.client;

import kamkeel.npcdbc.config.ConfigDBCClient;
import org.lwjgl.opengl.GL11;

public class ColorMode {

    public static int DARKMODE_TEXT = 0x7C7C7C;
    public static int DARKMODE_TEXT_ALTERNATE = 0xB2B2B2;

    public static int LIGHTMODE_TEXT = 0x2F2F2F;
    public static int LIGHTMODE_TEXT_ALTERNATE = 0x181818;

    public static int textColor(){
        if(ConfigDBCClient.DarkMode){
            return DARKMODE_TEXT;
        }
        return LIGHTMODE_TEXT;
    }

    public static int textAlt(){
        if(ConfigDBCClient.DarkMode){
            return DARKMODE_TEXT_ALTERNATE;
        }
        return LIGHTMODE_TEXT_ALTERNATE;
    }

    public static String skimColors(String input){
        if(ConfigDBCClient.DarkMode){
            return input.replace("§0", "").replace("§8", "§7").replace("&0", "");
        }
        return input.replace("§f", "").replace("§7", "§8").replace("&f", "");
    }

    public static void colorToHex(int c) {
        float h2 = (float)(c >> 16 & 255) / 255.0F;
        float h3 = (float)(c >> 8 & 255) / 255.0F;
        float h4 = (float)(c & 255) / 255.0F;
        float h1 = 1.0F;
        float r = h1 * h2;
        float g = h1 * h3;
        float b = h1 * h4;
        GL11.glColor3f(r, g, b);
    }

    public static void applyModelColor(int color, boolean isHurt) {
        applyModelColor(color, 1.0f, isHurt);
    }

    public static void applyModelColor(int color, float alpha, boolean isHurt) {
        float red = (color >> 16 & 255) / 255f;
        float green = (color >> 8 & 255) / 255f;
        float blue = (color & 255) / 255f;

        if (isHurt) {
            red = (float) Math.min(red + 0.2, 1.0f);
            green = (float) Math.max(green - 0.2, 0);
            blue = (float) Math.max(blue - 0.2, 0);
        }

        GL11.glColor4f(red, green, blue, alpha);
    }
}
