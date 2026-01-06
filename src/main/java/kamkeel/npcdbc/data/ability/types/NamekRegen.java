package kamkeel.npcdbc.data.ability.types;

import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.DBCAbilities;
import kamkeel.npcdbc.constants.DBCAnimations;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.DBCEffectController;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.dbcdata.DBCDataStats;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.AnimationData;

public class NamekRegen extends AddonAbility {
    public NamekRegen() {
        super();
        name = "NamekRegen";
        langName = "ability.namekregen";
        langDescription = "ability.namekregenDesc";
        id = DBCAbilities.NAMEK_REGEN;
        iconX = 336;
        iconY = 0;
        type = Type.Animated;
        cooldown = 300;
    }

    @Override
    protected void setupAnimations() {
        animation = DBCAnimations.NAMEK_REGEN.get();

        onFrame((frame, anim) -> {
            if (frame == 6 && ((AnimationData) anim.getParent()).getMCEntity() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) ((AnimationData) anim.getParent()).getMCEntity();
                DBCEffectController.getInstance().applyEffect(player, Effects.NAMEK_REGEN, -100);
            }
        });
    }

    @Override
    protected boolean canFireEvent(EntityPlayer player, DBCPlayerEvent.AbilityEvent event) {
        if (!ConfigDBCGameplay.EnableNamekianRegen || DBCData.get(player).Race != DBCRace.NAMEKIAN)
            return false;

        if (DBCData.get(player).stats.getCurrentBodyPercentage() >= ConfigDBCGameplay.NamekianRegenMin)
            return false;

        if (DBCEffectController.getInstance().hasEffect(player, Effects.NAMEK_REGEN))
            return false;

        return true;
    }

//    @Override
//    public boolean onAnimationEvent(AbilityData data, AnimationEvent event) {
//        if (!(event instanceof AnimationEvent.FrameEvent.Entered))
//            return false;
//
//        if (!event.getAnimation().getName().equals(DBCAnimations.NAMEKREGEN.getFileName()))
//            return false;
//
//        if (((AnimationEvent.FrameEvent.Entered) event).getIndex() != 6)
//            return false;
//
//        EntityPlayer player = (EntityPlayer) ((AnimationData) event.getAnimationData()).getMCEntity();
//        //DBCEffectController.getInstance().applyEffect(player, Effects.NAMEK_REGEN, -100);
//
//        return true;
//    }
}
