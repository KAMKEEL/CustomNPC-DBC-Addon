package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.entity.EntityCusPar;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.IEntityCusPar;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityCusPar.class, remap = false)
public class MixinEntityCusPar implements IEntityCusPar {

    @Shadow
    private Entity ent;
    @Unique
    private boolean enhancedRendering;
    
    @Override
    public boolean isEnhancedRendering() {
        return enhancedRendering;
    }

    @Unique
    public boolean shouldRenderInPass(int pass) {
        if (ConfigDBCClient.RevampAura && ent != null && !ent.isInWater())
            return pass == 1;
        return pass == 0;
    }


    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/world/World;FFDDDDDDDDDFIIIIZFZFILjava/lang/String;IIFFFIFFFFFFFFFIFFFFFZIZLnet/minecraft/entity/Entity;)V", at = @At("RETURN"))
    private void disableLightMap(String data3, World w, float box1, float box2, double poX, double poY, double poZ, double start_poX, double start_poY, double start_poZ, double moX, double moY, double moZ, float data29, int id, int id_min, int id_max, int data32, boolean rotate, float max_rotation_sp, boolean rotate2, float max_rotation_sp2, int data1, String rr, int data2, int data4, float data5, float data6, float data7, int data31, float data8, float data9, float data10, float data11, float data12, float data13, float data14, float data15, float data16, int data20, float data21, float data22, float data23, float data24, float data25, boolean data33, int data34, boolean data35, Entity ent, CallbackInfo ci) {
        if (ent instanceof EntityPlayer && DBCData.get((EntityPlayer) ent).auraEntity != null) {
            DBCData.get((EntityPlayer) ent).particleRenderQueue.add((EntityCusPar) (Object) this);
            enhancedRendering = true;
        } else if (ent instanceof EntityNPCInterface) {
            DBCDisplay display = ((INPCDisplay) ((EntityNPCInterface) ent).display).getDBCDisplay();
            if (display != null && display.auraEntity != null) {
                display.particleRenderQueue.add((EntityCusPar) (Object) this);
                enhancedRendering = true;
            }
        }
    }

    @Inject(method = "<init>(Ljava/lang/String;FFIIIZFILjava/lang/String;IIFFFIFFFFFFFFFIFFFFF)V", at = @At("RETURN"))
    private void disableLightMap(String data3, float box1, float box2, int id_min, int id_max, int data32, boolean rotate, float max_rotation_sp, int data1, String rr, int data2, int data4, float data5, float data6, float data7, int data31, float data8, float data9, float data10, float data11, float data12, float data13, float data14, float data15, float data16, int data20, float data21, float data22, float data23, float data24, float data25, CallbackInfo ci) {
        if (ent instanceof EntityPlayer && DBCData.get((EntityPlayer) ent).auraEntity != null) {
            DBCData.get((EntityPlayer) ent).particleRenderQueue.add((EntityCusPar) (Object) this);
            enhancedRendering = true;
        } else if (ent instanceof EntityNPCInterface) {
            DBCDisplay display = ((INPCDisplay) ((EntityNPCInterface) ent).display).getDBCDisplay();
            if (display != null && display.auraEntity != null) {
                display.particleRenderQueue.add((EntityCusPar) (Object) this);
                enhancedRendering = true;
            }
        }
    }
}
