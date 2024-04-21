package kamkeel.npcdbc.config;

import java.io.File;

public class LoadConfiguration {
    public static File capsuleConfig;
    public static File dbcClientConfig;

    public static void init(String configpath)
    {
        capsuleConfig = new File(configpath + "dbc_capsules.cfg");
        dbcClientConfig = new File(configpath + "dbc_client.cfg");

        ConfigCapsules.init(capsuleConfig);
        ConfigDBCClient.init(dbcClientConfig);
    }
}
