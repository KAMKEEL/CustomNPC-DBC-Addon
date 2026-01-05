package kamkeel.npcdbc;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.data.AnimationData;
import noppes.npcs.scripted.event.AnimationEvent;

public class NPCEventsHandler {
    @SubscribeEvent
    public void onAnimationEvent(AnimationEvent.Started event) {
        if (((AnimationData) event.getAnimationData()).getMCEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) ((AnimationData) event.getAnimationData()).getMCEntity();
            PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);
            if (info != null) {
                info.dbcAbilityData.onAnimationEvent(event);
            }
        }
    }

    @SubscribeEvent
    public void onAnimationEvent(AnimationEvent.Ended event) {
        if (((AnimationData) event.getAnimationData()).getMCEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) ((AnimationData) event.getAnimationData()).getMCEntity();
            PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);
            if (info != null) {
                info.dbcAbilityData.onAnimationEvent(event);
            }
        }
    }

    @SubscribeEvent
    public void onAnimationEvent(AnimationEvent.FrameEvent.Entered event) {
        if (((AnimationData) event.getAnimationData()).getMCEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) ((AnimationData) event.getAnimationData()).getMCEntity();
            PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);
            if (info != null) {
                info.dbcAbilityData.onAnimationEvent(event);
            }
        }
    }

    @SubscribeEvent
    public void onAnimationEvent(AnimationEvent.FrameEvent.Exited event) {
        if (((AnimationData) event.getAnimationData()).getMCEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) ((AnimationData) event.getAnimationData()).getMCEntity();
            PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);
            if (info != null) {
                info.dbcAbilityData.onAnimationEvent(event);
            }
        }
    }
}
