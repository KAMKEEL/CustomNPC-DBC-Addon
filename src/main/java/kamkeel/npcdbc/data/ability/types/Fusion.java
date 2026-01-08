package kamkeel.npcdbc.data.ability.types;

import kamkeel.npcdbc.constants.DBCAbilities;
import kamkeel.npcdbc.constants.DBCAnimations;
import kamkeel.npcdbc.controllers.DBCAnimationController;
import kamkeel.npcdbc.controllers.FusionHandler;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.dbcdata.DBCDataStats;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.DBCSettingsUtil;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.Animation;
import noppes.npcs.controllers.data.AnimationData;

public class Fusion extends AddonAbility {
    public Fusion() {
        super();
        name = "Fusion";
        langName = "ability.fusion";
        langDescription = "ability.fusionDesc";
        id = DBCAbilities.FUSION;
        skillId = 0;
        iconX = 384;
        iconY = 0;
        type = Type.Active;
    }

    @Override
    public int getWidth() {
        return 64;
    }

    @Override
    public float getScale() {
        return 1.25f;
    }

    @Override
    protected void fireEvent(EntityPlayer player, DBCPlayerEvent.AbilityEvent event) {
        if (player.isSneaking()) {
            if (!FusionHandler.checkNearbyPlayersMetamoran(player)) {
                event.setCooldown(3);
            }
        } else {
            DBCSettingsUtil.setFusionEnabled(player, !DBCSettingsUtil.isFusionEnabled(player));
            cooldownCancelled = true;
        }
    }

    @Override
    protected boolean canFireEvent(EntityPlayer player, DBCPlayerEvent.AbilityEvent event) {
        DBCData data = DBCData.getData(player);

        if (data.isFusionSpectator() || data.stats.isFused())
            return false;

        return true;
    }
}
