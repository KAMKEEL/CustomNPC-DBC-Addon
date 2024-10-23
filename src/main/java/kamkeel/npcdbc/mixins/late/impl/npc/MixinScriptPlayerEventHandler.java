package kamkeel.npcdbc.mixins.late.impl.npc;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.util.DBCUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import noppes.npcs.ScriptPlayerEventHandler;
import noppes.npcs.scripted.event.NpcEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ScriptPlayerEventHandler.class)
public abstract class MixinScriptPlayerEventHandler {
    @Unique
    private float originalDamage;

    @Inject(method = "invoke(Lnet/minecraftforge/event/entity/living/LivingAttackEvent;)V", at = @At(value = "FIELD", target = "Lnoppes/npcs/controllers/ScriptController;playerScripts:Lnoppes/npcs/controllers/data/PlayerDataScript;", shift = At.Shift.BEFORE))
    public void fixDamagedEventDBCDamage(LivingAttackEvent event, CallbackInfo ci) {
        if (event.source.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.source.getEntity();
            DBCData data = DBCData.get(player);
            if (data.Powertype == 1) {

                originalDamage = event.ammount;

               event.ammount = DBCUtils.calculateAttackStat(player, event.ammount, event.source);
            }
        }
    }

    @Redirect(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnoppes/npcs/scripted/event/NpcEvent$DamagedEvent;getDamage()F"))
    public float fixDamagedEventDBCDamage(NpcEvent.DamagedEvent instance) {
        DBCUtils.npcLastSetDamage = (int) instance.getDamage();
        return originalDamage;
    }

}
