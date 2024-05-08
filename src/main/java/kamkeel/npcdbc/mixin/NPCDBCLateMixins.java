package kamkeel.npcdbc.mixin;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@LateMixin
public class NPCDBCLateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.npcdbc.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        List<String> mixins = new ArrayList<>();
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
            mixins.add("npc.client.MixinGuiScriptPlayers");
            mixins.add("npc.client.MixinDBCClient");
            mixins.add("npc.client.MixinModelMPM");
            mixins.add("npc.client.MixinGuiCreationScreen");

            mixins.add("dbc.MixinDBCKiTech");
            mixins.add("dbc.MixinJRMCoreCliTickH");
            mixins.add("dbc.MixinJRMCoreGuiBars");
            mixins.add("dbc.MixinJRMCoreGuiScreen");
            mixins.add("dbc.MixinModelBipedDBC");
            mixins.add("dbc.MixinRenderAura2");
            mixins.add("dbc.MixinRenderPlayerJBRA");

            mixins.add("dbc.recolor.MixinJRMCoreGuiButtons");
            mixins.add("dbc.recolor.MixinJRMCoreGuiScreenColor");
        }
        mixins.add("npc.MixinDBCStats");
        mixins.add("npc.MixinPlayerData");
        mixins.add("npc.MixinScriptPlayer");

        mixins.add("dbc.MixinEntityAura2");
        mixins.add("dbc.MixinJGPlayerMP");
        mixins.add("dbc.MixinJRMCoreComTickH");
        mixins.add("dbc.MixinJRMCoreEH");
        mixins.add("dbc.MixinJRMCoreH");
        mixins.add("dbc.MixinJRMCoreHDBC");
        mixins.add("npc.MixinDBCAddon");
        mixins.add("npc.MixinDBCDisplay");

        return mixins;
    }
}
