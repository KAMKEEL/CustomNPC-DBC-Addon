package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityAura2.class, remap = false)
public class MixinEntityAura2 {
    private int color;
    private int colorl2;
    private int colorl3;
    private float state;
    private float state2;
    private String tex;
    private String texl2;
    private String texl3;
    private int speed;
    private float alpha;
    private boolean bol4a;
    private boolean bol7;

    @Inject(method = "<init>(Lnet/minecraft/world/World;Ljava/lang/String;IFFI)V", at = @At("TAIL"))
    public void onCustomAura(World par1World, String dbcCharger, int c, float s, float s2, int cr, CallbackInfo ci) {
        PlayerDBCInfo formData = Utility.getSelfData();
        if (formData != null && formData.getCurrentAura() != null) {
            Aura aura = formData.getCurrentAura();

            if (aura.display.hasLightning)
                state = 5;
        }
    }

}
