package kamkeel.npcdbc.config;

import java.io.File;

public class LoadConfiguration {
    public static File capsuleConfig;
    public static File dbcClientConfig;
    public static File dbcGameplayConfig;

    public static void init(String configpath)
    {
        capsuleConfig = new File(configpath + "dbc_capsules.cfg");
        dbcClientConfig = new File(configpath + "dbc_client.cfg");
        dbcGameplayConfig = new File(configpath + "dbc_gameplay.cfg");

        ConfigCapsules.init(capsuleConfig);
        ConfigDBCClient.init(dbcClientConfig);
        ConfigDBCGameplay.init(dbcGameplayConfig);
    }
}
