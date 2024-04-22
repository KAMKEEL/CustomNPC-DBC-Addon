package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import kamkeel.npcdbc.data.Aura;
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
            if (aura.hasLightning)
                state = 5;
            if (aura.hasColor1)
                color = aura.color1;
            if (aura.hasColor2)
                colorl2 = aura.color2;
            if (aura.hasColor3)
                colorl3 = aura.color3;
            if (aura.hasTexture1)
                tex = aura.texture1;
            if (aura.hasTexture2)
                texl2 = aura.texture2;
            if (aura.hasTexture3)
                texl3 = aura.texture3;
            if (aura.hasSpeed)
                speed = aura.speed;
            if (aura.hasAlpha)
                alpha = aura.alpha;
        }
    }

}
