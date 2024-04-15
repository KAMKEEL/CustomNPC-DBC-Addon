package kamkeel.npcdbc.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.data.SyncedData.DBCData;
import kamkeel.npcdbc.data.SyncedData.PerfectSync;
import kamkeel.npcdbc.mixin.IPlayerFormData;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import noppes.npcs.CustomItems;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.constants.EnumQuestType;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.*;
import noppes.npcs.scripted.NpcAPI;
import noppes.npcs.scripted.item.ScriptCustomItem;

import static noppes.npcs.config.ConfigMain.TrackedQuestUpdateFrequency;

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

    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public void onServerTick(TickEvent.PlayerTickEvent event) {
        if (event.player == null || event.player.worldObj == null)
            return;

        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.START) {
            EntityPlayer player = event.player;
            if (PlayerDataController.Instance != null) {
                PlayerData playerData = PlayerDataController.Instance.getPlayerData(player);
                if(((IPlayerFormData)  playerData).getFormUpdate()) {
                    NBTTagCompound formCompound = new NBTTagCompound();
                    playerData.getDBCSync(formCompound);
                    NoppesUtilServer.sendDBCCompound((EntityPlayerMP)player, formCompound);
                    ((IPlayerFormData) playerData).finishFormInfo();
                }
            }
        }
    }
}
