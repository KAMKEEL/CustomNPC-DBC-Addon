package kamkeel.npcdbc.network.packets.player.skill;

import JinRyuu.JRMCore.JRMCoreH;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.api.skill.ICustomSkill;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCSettings;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.SkillController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.skill.CustomSkill;
import kamkeel.npcdbc.data.skill.SkillContainer;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcs.network.packets.data.large.GuiDataPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

import java.io.IOException;

import static kamkeel.npcdbc.constants.DBCForm.*;

public final class CustomSkillPacket extends AbstractPacket {
    public static final String packetName = "DBC|Skill";
    private int skillID;
    private Action action;

    public enum Action {
        UPGRADE,
        UNLEARN;
    }

    public CustomSkillPacket(int skillID, Action action) {
        this.skillID = skillID;
        this.action = action;
    }

    public CustomSkillPacket() {
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.CustomSkill;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(skillID);
        out.writeInt(action.ordinal());
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        int skillID = in.readInt();
        Action action = Action.values()[in.readInt()];

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            return;

        ICustomSkill skill = SkillController.Instance.getSkill(skillID);
        if (skill == null)
            return;

        DBCData data = DBCData.getData(player);
        if (data == null)
            return;

        IPlayer iplayer = PlayerDataUtil.getIPlayer(player);

        switch (action) {
            case UNLEARN:
                DBCPlayerEvent.SkillEvent.Unlearn unlearnEvent = new DBCPlayerEvent.SkillEvent.Unlearn(iplayer, 2, skillID);
                if (DBCEventHooks.onSkillEvent(unlearnEvent))
                    return;
                data.customSkills.remove(skillID);

                break;
            case UPGRADE:
                SkillContainer container = data.customSkills.get(skillID);
                if (container == null)
                    return;
                if (container.getLevel() >= skill.getMaxLevel())
                    return;
                int newLevel = container.getLevel() + 1;
                int cost = skill.getTPCost(newLevel);
                int mindCost = skill.getMindCost(newLevel);

                if (cost < 0 || cost > data.TP || data.getAvailableMind() < mindCost)
                    return;

                DBCPlayerEvent.SkillEvent.Upgrade upgradeEvent = new DBCPlayerEvent.SkillEvent.Upgrade(iplayer, 2, skillID, cost, newLevel);
                if (DBCEventHooks.onSkillEvent(upgradeEvent))
                    return;

                cost = upgradeEvent.getCost();
                if (cost > data.TP)
                    return;
                data.TP -= Math.max(0, cost);
                container.setLevel(newLevel);
                break;
        }

        data.saveNBTData(true);
    }
}
