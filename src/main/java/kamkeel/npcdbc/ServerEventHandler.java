package kamkeel.npcdbc;

import JinRyuu.JRMCore.entity.EntityCusPar;
import JinRyuu.JRMCore.server.config.dbc.JGConfigDBCFormMastery;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.combat.Dodge;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.DBCSyncType;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.*;
import kamkeel.npcdbc.data.IAuraData;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.mixins.late.IPlayerDBCInfo;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.get.CapsuleInfo;
import kamkeel.npcdbc.network.packets.get.DBCInfoSyncPacket;
import kamkeel.npcdbc.network.packets.player.LoginInfo;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import kamkeel.npcs.network.enums.EnumSyncAction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class ServerEventHandler {

    @SubscribeEvent
    public void loginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player == null || event.player.worldObj == null || event.player.worldObj.isRemote || event.player instanceof FakePlayer)
            return;
        DBCData dbcData = DBCData.get(event.player);
        dbcData.loadNBTData(true);
        DBCPacketHandler.Instance.sendToPlayer(new CapsuleInfo(CapsuleInfo.InfoType.COOLDOWN), (EntityPlayerMP) event.player);
        DBCPacketHandler.Instance.sendToPlayer(new CapsuleInfo(CapsuleInfo.InfoType.STRENGTH), (EntityPlayerMP) event.player);
        DBCPacketHandler.Instance.sendToPlayer(new CapsuleInfo(CapsuleInfo.InfoType.EFFECT_TIME), (EntityPlayerMP) event.player);
        DBCPacketHandler.Instance.sendToPlayer(new LoginInfo(), (EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void joinWorld(EntityJoinWorldEvent event) {
        Entity e = event.entity;


        if (e instanceof EntityPlayer || e instanceof EntityCustomNpc) {
            IAuraData auraData = PlayerDataUtil.getAuraData(e);
            EntityAura aura = auraData.getAuraEntity();
            if (aura != null) {
                aura.setDead();
                for (EntityAura child : aura.children.values()) //children of root cannot have children
                    child.setDead();
            }
            for (Iterator<EntityCusPar> iter = auraData.getParticles().iterator(); iter.hasNext(); ) {
                iter.next().setDead();
                iter.remove();
            }
        }
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
                    DBCPacketHandler.Instance.sendToPlayer(new DBCInfoSyncPacket(DBCSyncType.PLAYERDATA, EnumSyncAction.RELOAD, -1, formCompound), (EntityPlayerMP) player);
                    ((IPlayerDBCInfo) playerData).endDBCInfo();
                }
            }

            if (ConfigDBCEffects.AUTO_BLOATED)
                if (player.ticksExisted % ConfigDBCEffects.DECREASE_TIME == 0)
                    DBCEffectController.Instance.decreaseSenzuConsumption(player);

            if (ConfigDBCGameplay.WearableEarrings)
                if (player.ticksExisted % 60 == 0)
                    FusionHandler.checkNearbyPlayers(player);

            if (player.ticksExisted % 10 == 0) {
                // Keep the Player informed on their own data
                DBCData dbcData = DBCData.get(player);

                // NO LONGER NEEDED, HANDLED THROUGH ABILITY
//                if (ConfigDBCGameplay.EnableNamekianRegen && dbcData.Race == DBCRace.NAMEKIAN)
//                    dbcData.stats.applyNamekianRegen();

                if (ConfigDBCGameplay.EnableHumanSpirit && dbcData.Race == DBCRace.HUMAN)
                    DBCEffectController.Instance.checkHumanSpirit(player);

                dbcData.syncTracking();
                // ChargeKi
            }
            handleFormProcesses(player);
            handleAbilityProcesses(player);

            if (ConfigDBCGameplay.RevampKiCharging) {
                chargeKi(player);
            }
        }
    }

    public void chargeKi(EntityPlayer player) {
        DBCData dbcData = DBCData.getData(player);
        dbcData.loadCharging();
        if (!dbcData.isChargingKi())
            return;

        int chargeTick = 16 - (ConfigDBCGameplay.KiPotentialUnlock ? dbcData.stats.getPotentialUnlockLevel() : 6);
        if (player.ticksExisted % chargeTick != 0)
            return;

        int releaseFactor = ConfigDBCGameplay.KiChargeRate;
        boolean powerDown = dbcData.isFnPressed;
        byte release = dbcData.Release;

        byte maxRelease = (byte) ((byte) (50 + dbcData.stats.getPotentialUnlockLevel() * 5) + (byte) (DBCEffectController.Instance.hasEffect(player, Effects.OVERPOWER) ? ConfigDBCEffects.OVERPOWER_AMOUNT : 0));

        int newRelease = ValueUtil.clamp(!powerDown ? release + releaseFactor : release - releaseFactor, (byte) releaseFactor, maxRelease);
        dbcData.getRawCompound().setByte("jrmcRelease", (byte) newRelease);
    }

    @SubscribeEvent
    public void playerInteractEvent(EntityInteractEvent event) {
        if (event.entityPlayer == null || event.entityPlayer.worldObj == null)
            return;

        if (!event.entityPlayer.worldObj.isRemote && event.entityPlayer.worldObj instanceof WorldServer && event.entityPlayer instanceof EntityPlayerMP) {
            if (event.entityPlayer.isSneaking() && event.target instanceof EntityPlayerMP) {
                FusionHandler.requestMetamoranFusion(event.entityPlayer, (EntityPlayerMP) event.target);
            }
        }
    }

    @SubscribeEvent
    public void playerDeathEvent(LivingDeathEvent event) {
        if (event.entityLiving == null || event.entityLiving.worldObj == null || event.entityLiving.worldObj.isRemote)
            return;

        if (event.entityLiving.worldObj instanceof WorldServer && event.entityLiving instanceof EntityPlayer) {
            PlayerDBCInfo dbcInfo = PlayerDataUtil.getDBCInfo((EntityPlayer) event.entityLiving);
            DBCData dbcData = DBCData.get((EntityPlayer) event.entityLiving);
            dbcData.addonFormID = -1;
            dbcInfo.currentForm = -1;
            dbcInfo.updateClient();
        }
    }

    public void handleAbilityProcesses(EntityPlayer player) {
        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);

        if (player.ticksExisted % 20 == 0) {
            if (!info.customAbilityData.abilityTimers.isEmpty())
                for (Map.Entry<Integer, Integer> entry : info.customAbilityData.abilityTimers.entrySet()) {
                    if (info.customAbilityData.hasAbility(entry.getKey())) {
                        info.customAbilityData.decrementCooldown(entry.getKey());
                    }
                }

            if (!info.dbcAbilityData.abilityTimers.isEmpty())
                for (Map.Entry<Integer, Integer> entry : info.dbcAbilityData.abilityTimers.entrySet()) {
                    if (info.dbcAbilityData.hasAbility(entry.getKey())) {
                        info.dbcAbilityData.decrementCooldown(entry.getKey());
                    }
                }
        }
    }

    public void handleFormProcesses(EntityPlayer player) {
        DBCData dbcData = DBCData.get(player);
        Form form = dbcData.getForm();

        if (form == null) {
            return;
        }

        if (dbcData.lastTicked == player.ticksExisted) {
            return;
        }
        dbcData.lastTicked = player.ticksExisted;

        boolean isInSurvival = !player.capabilities.isCreativeMode;
        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(player);
        // Reverts player from form when ki or release are 0
        if (dbcData.Release <= 0 || dbcData.Ki <= 0) {
            formData.currentForm = -1;
            formData.updateClient();
            dbcData.loadNBTData(true);
        }

        if (!form.stackable.godStackable && dbcData.isForm(DBCForm.GodOfDestruction)) {
            dbcData.setForm(DBCForm.GodOfDestruction, false);
        }

        if (!form.stackable.mysticStackable && dbcData.isForm(DBCForm.Mystic)) {
            dbcData.setForm(DBCForm.Mystic, false);
        }

        if (!form.stackable.uiStackable && dbcData.isForm(DBCForm.UltraInstinct)) {
            dbcData.setForm(DBCForm.UltraInstinct, false);
        }

        if (!form.stackable.kaiokenStackable && dbcData.isForm(DBCForm.Kaioken)) {
            dbcData.setForm(DBCForm.Kaioken, false);
        }


        // Updates form Timer
        if (formData.hasTimer(form.id)) {
            formData.decrementTimer(form.id);
            if (player.ticksExisted % 20 == 0)
                formData.updateClient();
        }

        if (form.mastery.hasKiDrain() && isInSurvival) {
            if (player.ticksExisted % 10 == 0) {
                double might = DBCUtils.calculateKiDrainMight(dbcData, player);

                double cost = might * form.mastery.getKiDrain();

                if (JGConfigDBCFormMastery.FM_Enabled) {
                    cost *= form.mastery.calculateMulti("kiDrain", formData.getCurrentLevel());
                }

                int actualCost = (int) Math.floor((-cost / form.mastery.kiDrainTimer) * 10);

                dbcData.stats.restoreKiFlat(actualCost);
            }
        }

        if (form.mastery.hasHeat() && player.ticksExisted % 20 == 0) {
            float heatToAdd = form.mastery.calculateMulti("heat", formData.getCurrentLevel());
            float newHeat = ValueUtil.clamp(dbcData.addonCurrentHeat + heatToAdd, 0, form.mastery.maxHeat);

            if (newHeat == form.mastery.maxHeat) {
                int painTime = (int) (form.mastery.painTime * 60f / 5f * form.mastery.calculateMulti("pain", formData.getCurrentLevel()));
                dbcData.getRawCompound().setInteger("jrmcGyJ7dp", painTime);
                newHeat = 0;
                TransformController.handleFormDescend(player, -10);
            }

            dbcData.getRawCompound().setFloat("addonCurrentHeat", newHeat);
        }

        if ((form.display.hairType.equals("ssj4") || form.display.hairType.equals("oozaru")) && DBCRace.isSaiyan(dbcData.Race) && !dbcData.hasTail()) {
            TransformController.handleFormDescend(player, -10);
        }
    }

    @SubscribeEvent
    public void NPCAscend(LivingEvent.LivingUpdateEvent event) {
        if (event.entity instanceof EntityCustomNpc && Utility.isServer(event.entity)) {
            EntityCustomNpc npc = (EntityCustomNpc) event.entity;
            DBCDisplay display = ((INPCDisplay) npc.display).getDBCDisplay();
            if (!display.enabled)
                return;
            if (display.isTransforming && display.selectedForm != -1 && display.selectedForm != display.formID) {
                TransformController.npcAscend(npc, (Form) FormController.Instance.get(display.selectedForm));
            } else
                TransformController.npcDecrementRage(npc, display);
        }
    }

    @SubscribeEvent
    public void attackEvent(LivingAttackEvent event) {
        if (event.entity.worldObj.isRemote)
            return;

        boolean isNPC = event.entity instanceof EntityNPCInterface;
        float dodgeChance = 0;
        if (event.entity instanceof EntityPlayer || isNPC) {
            Form form = PlayerDataUtil.getForm(event.entity);

            if (form != null) {
                float formLevel = PlayerDataUtil.getFormLevel(event.entity);
                if (form.mastery.hasDodge())
                    dodgeChance = form.mastery.dodgeChance * form.mastery.calculateMulti("dodge", formLevel);
            } else if (isNPC)
                dodgeChance = PlayerDataUtil.getDBCData((EntityNPCInterface) event.entity).getDodgeChance();
        }
        if (dodgeChance > 0) {
            Random rand = new Random();
            if (dodgeChance >= rand.nextInt(100)) {
                Entity attacker = event.source.getEntity();
                if (Dodge.dodge(event.entity, attacker)) {
                    event.setCanceled(true);
                }
            }
        }
    }
}
