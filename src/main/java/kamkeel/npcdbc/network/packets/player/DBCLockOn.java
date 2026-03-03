package kamkeel.npcdbc.network.packets.player;

import JinRyuu.JRMCore.JRMCoreCliTicH;
import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.io.IOException;

public abstract class DBCLockOn extends AbstractPacket {

    protected final boolean remove;
    protected final int entityId;

    public DBCLockOn() {
        this.remove = true;
        this.entityId = -1;
    }

    public DBCLockOn(int entityId) {
        this.remove = false;
        this.entityId = entityId;
    }

    protected abstract Side getSide();

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        if (FMLCommonHandler.instance().getEffectiveSide() != getSide())
            return;

        out.writeBoolean(remove);
        if (!remove)
            out.writeInt(entityId);
    }

    public static class Sync extends DBCLockOn {
        public static final String packetName = "NPC|LockOnSync";

        public Sync() { super(); }
        public Sync(int entityId) { super(entityId); }

        @Override
        protected Side getSide() {
            return Side.CLIENT;
        }

        @Override
        public Enum getType() {
            return EnumPacketPlayer.LockOnSync;
        }

        @Override
        public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
                return;

            boolean remove = in.readBoolean();
            if (remove) {
                DBCData.getData(player).LockOn = null;
                return;
            }

            int entityId = in.readInt();

            World world = player.worldObj;
            Entity entity = world.getEntityByID(entityId);

            if (entity instanceof EntityLivingBase) {
                DBCData.get(player).LockOn = (EntityLivingBase) entity;
            }
        }
    }

    public static class Update extends DBCLockOn {
        public static final String packetName = "NPC|LockOnUpdate";

        public Update() { super(); }
        public Update(int entityId) { super(entityId); }

        @Override
        protected Side getSide() {
            return Side.SERVER;
        }

        @Override
        public Enum getType() {
            return EnumPacketPlayer.LockOnUpdate;
        }

        @Override
        public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
                return;

            boolean remove = in.readBoolean();
            if (remove) {
                JRMCoreCliTicH.lockOn = null;
                return;
            }
            int entityId = in.readInt();

            processEntity(entityId);
        }

        @SideOnly(Side.CLIENT)
        private void processEntity(int entityId) {
            Entity newEntity = Minecraft.getMinecraft().theWorld.getEntityByID(entityId);

            if (newEntity instanceof EntityLivingBase)
                setLockOnTarget((EntityLivingBase) newEntity);

        }

        @SideOnly(Side.CLIENT)
        public static void setLockOnTarget(EntityLivingBase newEntity) {
            if (newEntity != JRMCoreCliTicH.lockOn && JRMCoreH.SklLvl(6) > 0 && JRMCoreConfig.lockon) {
                EntityPlayer player = Minecraft.getMinecraft().thePlayer;
                if (player.getDistanceSqToEntity(newEntity) >= 35 * 35)
                    return;

                JRMCoreCliTicH.lockOn = newEntity;
                DBCData.get(player).LockOn = newEntity;

                player.worldObj.playSound(player.posX, player.posY, player.posZ, "jinryuudragonbc:DBC4.lockon", 1.0F, 1.0F, true);
            }
        }
    }
}
