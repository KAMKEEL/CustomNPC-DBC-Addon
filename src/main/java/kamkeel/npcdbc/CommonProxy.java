package kamkeel.npcdbc;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import kamkeel.npcdbc.api.AbstractDBCAPI;
import noppes.npcs.scripted.NpcAPI;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent ev){}

    public void init(FMLInitializationEvent ev){
        NpcAPI.Instance().addGlobalObject("DBCAPI", AbstractDBCAPI.Instance());
    }
}
