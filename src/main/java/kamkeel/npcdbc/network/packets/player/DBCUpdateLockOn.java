package kamkeel.npcdbc.network.packets.player;

import JinRyuu.JRMCore.JRMCoreCliTicH;
import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.PacketChannel;

import java.io.IOException;

public class DBCUpdateLockOn extends AbstractPacket {

    public static final String packetName = "NPC|LockOnUpdate";

    private final boolean remove;
    private final int entityId;

    public DBCUpdateLockOn(){
        this.remove = true;
        this.entityId = -1;
    }
    public DBCUpdateLockOn(int newEntityId){
        this.remove = false;
        this.entityId = newEntityId;
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.LockOn;
    }

    @Override
    public PacketChannel getChannel() {
        return PacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            return;

        out.writeBoolean(remove);
        if(!remove)
            out.writeInt(entityId);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
            return;

        boolean remove = in.readBoolean();
        if(remove) {
            JRMCoreCliTicH.lockOn = null;
            return;
        }
        int entityId = in.readInt();

        processEntity(entityId);
    }

    @SideOnly(Side.CLIENT)
    private void processEntity(int entityId) {
        Entity newEntity = Minecraft.getMinecraft().theWorld.getEntityByID(entityId);

        if(newEntity instanceof EntityLivingBase)
            setLockOnTarget((EntityLivingBase) newEntity);
    }

    @SideOnly(Side.CLIENT)
    public static void setLockOnTarget(EntityLivingBase newEntity){
        if(newEntity != JRMCoreCliTicH.lockOn && JRMCoreH.SklLvl(6) > 0 && JRMCoreConfig.lockon) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if(player.getDistanceSqToEntity(newEntity) >= 35*35)
                return;

            JRMCoreCliTicH.lockOn = newEntity;

            player.worldObj.playSound(player.posX, player.posY, player.posZ, "jinryuudragonbc:DBC4.lockon", 1.0F, 1.0F, true);
        }
    }
}
