package kamkeel.npcdbc;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.combatmode.Dodge;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.mixin.IPlayerDBCInfo;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.CapsuleInfo;
import kamkeel.npcdbc.network.packets.ChargingDexInfo;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.util.ValueUtil;

import java.util.Random;

public class ServerEventHandler {

    @SubscribeEvent
    public void loginEvent(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player == null || event.player.worldObj == null || event.player.worldObj.isRemote || event.player instanceof FakePlayer)
            return;
        DBCData dbcData = DBCData.get(event.player);
        dbcData.loadNBTData(true);
        PacketHandler.Instance.sendToPlayer(new CapsuleInfo(true).generatePacket(), (EntityPlayerMP) event.player);
        PacketHandler.Instance.sendToPlayer(new CapsuleInfo(false).generatePacket(), (EntityPlayerMP) event.player);
        PacketHandler.Instance.sendToPlayer(new ChargingDexInfo().generatePacket(), (EntityPlayerMP) event.player);
        StatusEffectController.getInstance().loadEffects(event.player);
    }


    @SubscribeEvent
    public void onServerTick(TickEvent.PlayerTickEvent event) {
        if (event.player == null || event.player.worldObj == null || event.player.worldObj.isRemote || event.player instanceof FakePlayer)
            return;

        EntityPlayer player = event.player;
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.START) {
            // Send Form Information
            if (PlayerDataController.Instance != null) {
                PlayerData playerData = PlayerDataController.Instance.getPlayerData(player);
                if (((IPlayerDBCInfo) playerData).getDBCInfoUpdate()) {
                    NBTTagCompound formCompound = new NBTTagCompound();
                    playerData.getDBCSync(formCompound);
                    NoppesUtilServer.sendDBCCompound((EntityPlayerMP) player, formCompound);
                    ((IPlayerDBCInfo) playerData).endDBCInfo();
                }
            }

            if (player.ticksExisted % ConfigDBCGameplay.CheckEffectsTick == 0)
                StatusEffectController.Instance.runEffects(player);

            if (player.ticksExisted % 10 == 0) {
                // Keep the Player informed on their own data
                DBCData dbcData = DBCData.get(player);
                if (ConfigDBCGameplay.EnableNamekianRegen && dbcData.Race == DBCRace.NAMEKIAN)
                    dbcData.stats.applyNamekianRegen();

                if (player.ticksExisted % 20 == 0)
                    dbcData.stats.decrementActiveEffects();

                dbcData.syncTracking();
            }
            handleFormProcesses(player);
        }
    }

    @SubscribeEvent
    public void playerDeathEvent(LivingDeathEvent event) {
        if (event.entityLiving == null || event.entityLiving.worldObj == null || event.entityLiving.worldObj.isRemote)
            return;

        if (event.entityLiving.worldObj instanceof WorldServer && event.entityLiving instanceof EntityPlayer) {
            StatusEffectController.getInstance().killEffects((EntityPlayer) event.entityLiving);
        }
    }

    @SubscribeEvent
    public void DBCAttackEvent(DBCPlayerEvent.DamagedEvent event) {
        Form form = DBCData.getForm((EntityPlayer) event.player);
        if (form != null) {
            PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo((EntityPlayer) event.player);
            if (form.mastery.hasDodge()) {
                Random rand = new Random();
                float dodgeChance = form.mastery.dodgeChance * form.mastery.calculateMulti("dodge", formData.getCurrentLevel());
                if (dodgeChance >= rand.nextInt(100)) {
                    event.setCanceled(true);
                    Dodge.dodge((Entity) event.player, event.damageSource.getMCDamageSource().getEntity());
                    return;
                }
            }
            if (form.mastery.hasDamageNegation()) {
                float damageNegation = form.mastery.damageNegation * form.mastery.calculateMulti("damageNegation", formData.getCurrentLevel());
                float newDamage = event.getDamage() * (100 - damageNegation) / 100;
                event.setDamage(newDamage);
            }

        }
    }

    public void handleFormProcesses(EntityPlayer player) {
        Form form = DBCData.getForm(player);
        if (form != null) {
            PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(player);
            DBCData dbcData = DBCData.get(player);

            if (dbcData.Release <= 0 || dbcData.Ki <= 0) { //reverts player from CF when ki or release are 0
                formData.currentForm = -1;
                formData.updateClient();
                dbcData.loadNBTData(true);
            }

            if (formData.hasTimer(form.id)) {
                formData.decrementTimer(form.id);
                if (player.ticksExisted % 20 == 0)
                    formData.updateClient();
            }
            if (form.mastery.hasKiDrain()) {
                if (player.ticksExisted % 10 == 0) {
                    float toDrain = form.mastery.kiDrain * form.mastery.calculateMulti("kiDrain", formData.getCurrentLevel());
                    dbcData.stats.restoreKiPercent(-toDrain / form.mastery.kiDrainTimer * 10);
                }
            }

            if (form.mastery.hasHeat() && player.ticksExisted % 20 == 0) {
                float heatToAdd = 1.0f * form.mastery.calculateMulti("heat", formData.getCurrentLevel());
                float newHeat = ValueUtil.clamp(dbcData.addonCurrentHeat + heatToAdd, 0, form.mastery.maxHeat);

                if (newHeat == form.mastery.maxHeat) {
                    int painTime = (int) (form.mastery.painTime * 60 / 5 * form.mastery.calculateMulti("pain", formData.getCurrentLevel()));
                    dbcData.getRawCompound().setInteger("jrmcGyJ7dp", painTime);
                    formData.currentForm = -1;
                    formData.updateClient();

                    newHeat = 0;
                }

                dbcData.getRawCompound().setFloat("addonCurrentHeat", newHeat);
            }
        }
    }

}
