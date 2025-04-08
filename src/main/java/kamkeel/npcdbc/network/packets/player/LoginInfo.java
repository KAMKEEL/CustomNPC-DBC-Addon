package kamkeel.npcdbc.network.packets.player;

import JinRyuu.DragonBC.common.DBCConfig;
import JinRyuu.JRMCore.server.config.dbc.JGConfigRaces;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.client.ClientCache;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.config.ConfigDBCGeneral;
import kamkeel.npcdbc.constants.DBCClass;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public final class LoginInfo extends AbstractPacket {
    public static final String packetName = "NPC|Login";
    private final float ma;
    private final float spi;
    private final float war;
    private final boolean chargeDex;
    private final boolean transformBypass;
    private final double kiProtectionValue;
    private final double kiFistValue;
    private final boolean kiRevamp;
    private final float divineMulti;
    private final int maxAbsorptionLevel;
    private final String discordURL;

    public LoginInfo() {
        this.chargeDex = ConfigDBCGameplay.EnableChargingDex;
        this.ma = ConfigDBCGameplay.MartialArtistCharge;
        this.spi = ConfigDBCGameplay.SpiritualistCharge;
        this.war = ConfigDBCGameplay.WarriorCharge;
        this.transformBypass = ConfigDBCGameplay.InstantTransform;
        this.kiProtectionValue = DBCConfig.ccnfKDd;
        this.kiFistValue = DBCConfig.ccnfKFd;
        this.kiRevamp = ConfigDBCGameplay.RevampKiCharging;
        this.divineMulti = ConfigDBCEffects.getDivineMulti();
        this.maxAbsorptionLevel = JGConfigRaces.CONFIG_MAJIN_ABSORPTON_MAX_LEVEL;
        this.discordURL = ConfigDBCGeneral.getDiscordURL();
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.LoginInfo;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeBoolean(this.chargeDex);
        out.writeFloat(this.ma);
        out.writeFloat(this.spi);
        out.writeFloat(this.war);

        out.writeBoolean(this.transformBypass);

        out.writeDouble(this.kiProtectionValue);
        out.writeDouble(this.kiFistValue);

        out.writeBoolean(this.kiRevamp);

        out.writeFloat(this.divineMulti);
        out.writeInt(this.maxAbsorptionLevel);
        ByteBufUtils.writeUTF8String(out, discordURL);

        HashMap<Integer, HashMap<String, Boolean>> divineRaces = ConfigDBCEffects.getDivineApplicableForms();

        NBTTagList divineApplicableRaces = new NBTTagList();
        for (int i : divineRaces.keySet()) {
            HashMap<String, Boolean> divineForms = divineRaces.get(i);
            if (divineForms == null)
                continue;
            NBTTagCompound divineApplicableForms = new NBTTagCompound();
            for (String form : divineForms.keySet()) {
                divineApplicableForms.setBoolean(form, divineForms.getOrDefault(form, false));
            }
            divineApplicableRaces.appendTag(divineApplicableForms);
        }

        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("divineRaces", divineApplicableRaces);
        ByteBufUtils.writeNBT(out, compound);

    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        if (player.worldObj.isRemote) {
            ClientCache.hasChargingDex = in.readBoolean();
            float martialArtist = in.readFloat();
            float spiritualist = in.readFloat();
            float warrior = in.readFloat();

            ClientCache.chargingDexValues.put(DBCClass.MartialArtist, martialArtist);
            ClientCache.chargingDexValues.put(DBCClass.Spiritualist, spiritualist);
            ClientCache.chargingDexValues.put(DBCClass.Warrior, warrior);

            ClientCache.allowTransformBypass = in.readBoolean();

            DBCConfig.cnfKDd = in.readDouble();
            DBCConfig.cnfKFd = in.readDouble();

            ClientCache.kiRevamp = in.readBoolean();

            ClientCache.divineMulti = in.readFloat();
            ClientCache.maxAbsorptionLevel = in.readInt();

            ClientCache.discordURL = ByteBufUtils.readUTF8String(in);

            ClientCache.divineApplicableForms.clear();
            NBTTagCompound compound = ByteBufUtils.readNBT(in);
            NBTTagList divineRaces = compound.getTagList("divineRaces", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < divineRaces.tagCount(); i++) {
                NBTTagCompound forms = divineRaces.getCompoundTagAt(i);
                HashMap<String, Boolean> formMap = new HashMap<>();

                for (String key : (Set<String>) forms.func_150296_c()) {
                    formMap.put(key, forms.getBoolean(key));
                }
                ClientCache.divineApplicableForms.put(i, formMap);
            }

        }
    }
}
