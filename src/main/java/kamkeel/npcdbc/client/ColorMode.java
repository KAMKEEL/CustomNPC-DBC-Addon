package kamkeel.npcdbc.client;

import kamkeel.npcdbc.config.ConfigDBCClient;

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
}
