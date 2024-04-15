package kamkeel.npcdbc.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.data.SyncedData.DBCData;
import kamkeel.npcdbc.data.SyncedData.PerfectSync;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;

public class ServerEvents {
    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public void registerExtended(EntityConstructing event) {
        if (!(event.entity instanceof EntityPlayer))
            PerfectSync.registerAllDatas(event.entity); // entity registry
    }

    /**
     * loads/registers all PerfectSync datas from server NBTs
     */
    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public void worldTick(TickEvent.WorldTickEvent event) {
        if (!Utility.isServer())
            return;
        if (!event.phase.equals(TickEvent.Phase.END))
            return;

        World w = event.world;
        for (Object en : w.loadedEntityList) {
            Entity player = (Entity) en;

            if (player instanceof EntityPlayer) {// checks if player is eligible for data or not, incase they weren't before
                PerfectSync.registerAllDatas(player);
                if (DBCData.has(player))
                    DBCData.get(player).loadFromNBT(true); // loaded from server NBT every tick instead of X ticks, as multiple sources can change PlayerPersisted outside of this mod
            }

            if (player.ticksExisted == 1) {
                PerfectSync.saveAllDatas(player, true); // initial save
            }
            if (player.ticksExisted % PerfectSync.SaveEveryXTicks == 0) //saves once every SaveEveryXTicks
                PerfectSync.saveAllDatas(player, true);
        }
    }
}
