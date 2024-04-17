package kamkeel.npcdbc.config;

import java.io.File;

public class LoadConfiguration {
    public static File capsuleConfig;

    public static void init(String configpath)
    {
        capsuleConfig = new File(configpath + "capsules.cfg");
        ConfigCapsules.init(capsuleConfig);
    }
}
