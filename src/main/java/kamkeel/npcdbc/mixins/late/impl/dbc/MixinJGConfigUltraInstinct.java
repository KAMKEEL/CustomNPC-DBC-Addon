package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.server.config.dbc.JGConfigUltraInstinct;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static kamkeel.npcdbc.util.DBCUtils.CONFIG_UI_NAME;
import static kamkeel.npcdbc.util.DBCUtils.cCONFIG_UI_NAME;

@Mixin(value = JGConfigUltraInstinct.class, remap = false)
public class MixinJGConfigUltraInstinct {

    @Shadow
    public static byte CONFIG_UI_LEVELS;
    @Shadow
    public static boolean[] CONFIG_UI_HAIR_WHITE;


    @Inject(method = "init_form_ultra_instinct", at = @At("TAIL"))
    private static void disableRendering(Configuration config, CallbackInfo ci) {
        CONFIG_UI_NAME = new String[CONFIG_UI_LEVELS];
        cCONFIG_UI_NAME = new String[CONFIG_UI_LEVELS];

        for (int i = 0; i < CONFIG_UI_LEVELS; ++i) {
            int id = i + 1;
            String name = "Ultra Instinct " + (CONFIG_UI_HAIR_WHITE[i] ? "(Mastered)" : "(Sign)") + " (Level " + id + ")";
            Property property = config.get("Ultra Instinct Level " + id, "Ultra Instinct Level Name", name);
            property.comment = "(CNPC+ DBC Addon) Server Sided! Set this level's name.";

            cCONFIG_UI_NAME[i] = property.getString();
            CONFIG_UI_NAME[i] = cCONFIG_UI_NAME[i];
        }
    }


}
