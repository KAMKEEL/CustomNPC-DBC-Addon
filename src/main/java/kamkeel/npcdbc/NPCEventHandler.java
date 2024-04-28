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

public class NPCEventHandler {

    @SubscribeEvent
    public void DBCAttackEvent(DBCPlayerEvent.DamagedEvent event) {
        if (event.player == null || event.player.getMCEntity() == null || event.player.getMCEntity().worldObj.isRemote)
            return;

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
}
