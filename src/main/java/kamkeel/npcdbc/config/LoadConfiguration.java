package kamkeel.npcdbc.config;

import java.io.File;

public class LoadConfiguration {
    public static File capsuleConfig;
    public static File dbcClientConfig;
    public static File dbcGameplayConfig;
    public static File effectsConfig;

    public static void init(String configpath)
    {
        capsuleConfig = new File(configpath + "capsules.cfg");
        dbcClientConfig = new File(configpath + "client.cfg");
        dbcGameplayConfig = new File(configpath + "gameplay.cfg");
        effectsConfig = new File(configpath + "effects.cfg");

        ConfigCapsules.init(capsuleConfig);
        ConfigDBCClient.init(dbcClientConfig);
        ConfigDBCGameplay.init(dbcGameplayConfig);
        ConfigDBCEffects.init(effectsConfig);
    }
}
