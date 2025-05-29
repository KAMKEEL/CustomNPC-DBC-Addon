package kamkeel.npcdbc.mixins.late.impl.npc;

import kamkeel.npcdbc.mixins.late.IKiResistance;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import noppes.npcs.Resistances;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Resistances.class, remap = false)
public abstract class MixinResistances implements IKiResistance {

    @Shadow(remap = false)
    public boolean disableDamage;

    @Unique
    public float ki = 1.0f;

    @Inject(method = "writeToNBT", at = @At(value = "TAIL"))
    public void writeNBT(CallbackInfoReturnable<NBTTagCompound> cir) {
        NBTTagCompound compound = cir.getReturnValue();
        if (compound != null) {
            compound.setFloat("KiRes", ki);
        }
    }

    @Inject(method = "readToNBT", at = @At("TAIL"), remap = false)
    public void readNBT(NBTTagCompound compound, CallbackInfo ci) {
        if (compound.hasKey("KiRes"))
            ki = compound.getFloat("KiRes");
    }

    @Inject(method = "applyResistance", at = @At("HEAD"), cancellable = true)
    public void applyKiRes(DamageSource source, float damage, CallbackInfoReturnable<Float> cir) {
        if (!disableDamage) {
            if (source.damageType.equals("EnergyAttack")) {
                damage *= (2 - ki);
                cir.setReturnValue(damage);
                cir.cancel();
            }
        }
    }

    public float getKiResistance() {
        return ki;
    }

    public void setKiResistance(float newKi) {
        ki = newKi;
    }

    ;
}
