package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreGuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor for private static DNS fields in JRMCoreGuiScreen.
 * Used by VanillaCreatorBridge to read dns/dnsH after calling setdns().
 */
@Mixin(value = JRMCoreGuiScreen.class, remap = false)
public interface IJRMCoreGuiScreenAccessor {

    @Accessor("dns")
    static String npcdbc$getDns() {
        throw new AssertionError();
    }

    @Accessor("dnsH")
    static String npcdbc$getDnsH() {
        throw new AssertionError();
    }
}
