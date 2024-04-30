package kamkeel.npcdbc.mixin.impl.dbc.recolor;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.config.ConfigDBCClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = JRMCoreH.class, remap = false)

public class MixinJRMText  {

    @ModifyVariable(method = "txt(Ljava/lang/String;Ljava/lang/String;IZIII)I", at = @At(value = "HEAD"), ordinal = 1, argsOnly = true)
    private static String Text2(String value){
        if(ConfigDBCClient.EnhancedGui){
            if(ConfigDBCClient.DarkMode){
//                value = value.replace("&8", "&7").replace("§8", "§7").replace("&0", "&f").replace("§0", "§f");
            }
        }
        return value;
    }

    @ModifyVariable(method = "txt(Ljava/lang/String;Ljava/lang/String;IZIIIII)I", at = @At(value = "HEAD"), ordinal = 1, argsOnly = true)
    private static String Text(String value){
        if(ConfigDBCClient.EnhancedGui){
            if(ConfigDBCClient.DarkMode){
//                value = value.replace("&8", "&7").replace("§8", "§7").replace("&0", "&f").replace("§0", "§f");
            }
        }
        return value;
    }
}
