package kamkeel.npcdbc.data.ability.types;

import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.DBCAbilities;
import kamkeel.npcdbc.constants.DBCAnimations;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.DBCAnimationController;
import kamkeel.npcdbc.controllers.DBCEffectController;
import kamkeel.npcdbc.data.ability.AbilityData;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.dbcdata.DBCDataStats;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.handler.data.IAnimation;
import noppes.npcs.controllers.data.AnimationData;
import noppes.npcs.scripted.event.AnimationEvent;

public class NamekRegen extends AddonAbility {
    public NamekRegen() {
        super();
        name = "NamekRegen";
        langName = "ability.namekregen";
        langDescription = "ability.namekregenDesc";
        id = DBCAbilities.NAMEK_REGEN;
        iconX = 384;
        iconY = 0;
        type = Type.Active;
        cooldown = -1;
    }

    @Override
    public boolean onActivate(EntityPlayer player) {
        if (!super.onActivate(player))
            return false;

        // If this ability is already animating, return false
        if (PlayerDataUtil.getDBCInfo(player).dbcAbilityData.isAnimatingAbility())
            return false;

        IAnimation anim = DBCAnimationController.getInstance().get(DBCAnimations.NAMEKREGEN.ordinal());

        if (anim == null)
            return false;

        AnimationData data = AnimationData.getData(player);
        data.setEnabled(true);
        data.setAnimation(anim);
        data.updateClient();

        return true;
    }

    @Override
    protected boolean canFireEvent(EntityPlayer player) {
        if (!ConfigDBCGameplay.EnableNamekianRegen || DBCData.get(player).Race != DBCRace.NAMEKIAN)
            return false;

        DBCDataStats stats = new DBCDataStats(DBCData.get(player));

        if (stats.getCurrentBodyPercentage() >= ConfigDBCGameplay.NamekianRegenMin)
            return false;

        if (DBCEffectController.getInstance().hasEffect(player, Effects.NAMEK_REGEN))
            return false;

        return true;
    }

    @Override
    public boolean onAnimationEvent(AbilityData data, AnimationEvent event) {
        if (!(event instanceof AnimationEvent.FrameEvent.Entered))
            return false;

        if (!event.getAnimation().getName().equals(DBCAnimations.NAMEKREGEN.getFileName()))
            return false;

        if (((AnimationEvent.FrameEvent.Entered) event).getIndex() != 6)
            return false;

        EntityPlayer player = (EntityPlayer) ((AnimationData) event.getAnimationData()).getMCEntity();
        DBCEffectController.getInstance().applyEffect(player, Effects.NAMEK_REGEN, -100);

        return true;
    }
}
