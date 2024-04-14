package kamkeel.npcdbc.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.data.SyncedData.DBCData;
import kamkeel.npcdbc.data.SyncedData.PerfectSync;
import kamkeel.npcdbc.util.u;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;

public class ServerEvents {
    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public void sAmkp2(EntityConstructing e) {
        if (!(e.entity instanceof EntityPlayer)) {
            PerfectSync.registerAllDatas(e.entity); // entity registry
        }

    }

    /**
     * Saves/registers all PerfectSync datas
     */
    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public void worldTick(TickEvent.WorldTickEvent e) {
        if (!u.isServer())
            return;
        if (!e.phase.equals(TickEvent.Phase.END))
            return;

        World w = e.world;
        for (Object en : w.loadedEntityList) {
            Entity p = (Entity) en;

            if (p instanceof EntityPlayer) {// checks if player is eligible for data or not, incase they weren't before
                PerfectSync.registerAllDatas(p);
                if (DBCData.has(p))
                    DBCData.get(p).save(true); // saved every tick instead of X ticks, as multiple sources can change PlayerPersisted outside of this mod
            }

            if (p.ticksExisted == 1) {
                PerfectSync.saveAllDatas(p, true); // initial save
            }
            if (p.ticksExisted % PerfectSync.SaveEveryXTicks == 0) //saves once every SaveEveryXTicks
                PerfectSync.saveAllDatas(p, true);


        }

    }

}
