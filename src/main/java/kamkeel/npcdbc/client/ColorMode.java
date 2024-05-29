package kamkeel.npcdbc.client;

import kamkeel.npcdbc.config.ConfigDBCClient;
import noppes.npcs.util.ValueUtil;
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

    public static void glColorInt(int color, float alpha) {
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;

        GL11.glColor4f(r, g, b, alpha);
    }

    public static int mixColors(int color1, int color2, float alpha) {
        alpha = ValueUtil.clamp(alpha, 0, 1);

        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r = (int) ((1 - alpha) * r1 + alpha * r2);
        int g = (int) ((1 - alpha) * g1 + alpha * g2);
        int b = (int) ((1 - alpha) * b1 + alpha * b2);

        return (r << 16) | (g << 8) | b;
    }

    public static int mixColors(int color1, int color2, int color3, float alpha1, float alpha2) {
        alpha1 = ValueUtil.clamp(alpha1, 0, 1);
        alpha2 = ValueUtil.clamp(alpha2, 0, 1);

        float remainingAlpha = ValueUtil.clamp(1 - alpha1 - alpha2, 0, 1);

        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r3 = (color3 >> 16) & 0xFF;
        int g3 = (color3 >> 8) & 0xFF;
        int b3 = color3 & 0xFF;

        int r = (int) (remainingAlpha * r1 + alpha1 * r2 + alpha2 * r3);
        int g = (int) (remainingAlpha * g1 + alpha1 * g2 + alpha2 * g3);
        int b = (int) (remainingAlpha * b1 + alpha1 * b2 + alpha2 * b3);

        return (r << 16) | (g << 8) | b;
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
