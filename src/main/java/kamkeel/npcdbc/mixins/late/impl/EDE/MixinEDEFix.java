package kamkeel.npcdbc.mixins.late.impl.EDE;

import JinRyuu.JRMCore.JRMCoreConfig;
import com.entitydamageeditor.events.EventSystem;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(EventSystem.class)
public class MixinEDEFix {

    @Redirect(method = "onEntityHurt", at = @At(value = "FIELD", target = "LJinRyuu/JRMCore/JRMCoreConfig;cStatPasDef:I"), remap = false)
    private int fixChargingDex(LivingAttackEvent event) {
        EntityPlayer player = (EntityPlayer) event.entity;
        DBCData dbcData = DBCData.getData(player);
        if(dbcData.stats.isChargingKiAttack()){
            switch(dbcData.Class){
                case 0:
                    return ConfigDBCGameplay.MartialArtistCharge;
                case 1:
                    return ConfigDBCGameplay.SpiritualistCharge;
                case 2:
                    return ConfigDBCGameplay.WarriorCharge;
                default:
                    return JRMCoreConfig.StatPasDef;
            }
        }else{
            return JRMCoreConfig.StatPasDef;
        }
    }
}
