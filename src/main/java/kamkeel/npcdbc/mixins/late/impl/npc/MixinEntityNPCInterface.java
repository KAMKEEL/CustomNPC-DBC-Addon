package kamkeel.npcdbc.mixins.late.impl.npc;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.world.World;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityNPCInterface.class)
public abstract class MixinEntityNPCInterface extends EntityCreature implements IEntityAdditionalSpawnData, ICommandSender, IRangedAttackMob, IBossDisplayData {


    private MixinEntityNPCInterface(World p_i1602_1_) {
        super(p_i1602_1_);
    }

    @Inject(method="entityInit", at=@At("RETURN"))
    private void addWatchableObjects(CallbackInfo ci){
        this.dataWatcher.addObject(31, Integer.valueOf(1));
    }
}
