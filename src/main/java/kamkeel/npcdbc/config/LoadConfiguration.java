package kamkeel.npcdbc.config;

import java.io.File;

public class LoadConfiguration {
    public static File capsuleConfig;
    public static File clientConfig;
    public static File gameplayConfig;
    public static File effectsConfig;
    public static File generalConfig;

    public static void init(String configpath) {
        capsuleConfig = new File(configpath + "capsules.cfg");
        clientConfig = new File(configpath + "client.cfg");
        gameplayConfig = new File(configpath + "gameplay.cfg");
        effectsConfig = new File(configpath + "effects.cfg");
        generalConfig = new File(configpath + "general.cfg");

        ConfigCapsules.init(capsuleConfig);
        ConfigDBCClient.init(clientConfig);
        ConfigDBCGameplay.init(gameplayConfig);
        ConfigDBCEffects.init(effectsConfig);
        ConfigDBCGeneral.init(generalConfig);
    }
}
