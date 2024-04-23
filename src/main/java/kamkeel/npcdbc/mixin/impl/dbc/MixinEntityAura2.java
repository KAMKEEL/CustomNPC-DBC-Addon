package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import kamkeel.npcdbc.data.aura.Aura;
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
        Aura aura = null; //pls make the framework for this
      //  state = 5;
        bol7 = true;
        if (aura != null) {
            if (aura.display.hasLightning)
                state = 5;
            if (aura.display.hasColor("color1"))
                color = aura.display.color1;
            if (aura.display.hasColor("color2"))
                colorl2 = aura.display.color2;
            if (aura.display.hasColor("color3"))
                colorl3 = aura.display.color3;
//            if (aura.display.hasTexture1)
//                tex = aura.display.texture1;
//            if (aura.display.hasTexture2)
//                texl2 = aura.display.texture2;
//            if (aura.display.hasTexture3)
//                texl3 = aura.display.texture3;
            if (aura.display.hasSpeed())
                speed = (int) aura.display.speed;
            if (aura.display.hasAlpha("lightning"))
                alpha = aura.display.alpha;
        }
    }

}
