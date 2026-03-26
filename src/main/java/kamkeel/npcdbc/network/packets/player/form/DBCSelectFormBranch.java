package kamkeel.npcdbc.network.packets.player.form;

import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.dbcdata.DBCDataRace;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.network.AbstractPacket;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.PacketChannel;
import kamkeel.npcdbc.network.packets.EnumPacketPlayer;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public final class DBCSelectFormBranch extends AbstractPacket {

    private int branchIndex;

    public DBCSelectFormBranch(int branchIndex) {
        this.branchIndex = branchIndex;
    }

    public DBCSelectFormBranch() {
    }

    @Override
    public Enum getType() {
        return EnumPacketPlayer.FormBranchSelect;
    }

    @Override
    public PacketChannel getChannel() {
        return DBCPacketHandler.PLAYER_PACKETS;
    }

    @Override
    public void sendData(ByteBuf out) throws IOException {
        out.writeInt(this.branchIndex);
    }

    @Override
    public void receiveData(ByteBuf in, EntityPlayer player) throws IOException {
        branchIndex = in.readInt();
        if (branchIndex < 0)
            return;

        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);
        info.selectedFormBranch = branchIndex;

        DBCDataRace raceData = DBCData.get(player).addonRace;
        if (raceData != null && raceData.isCustomRace()) {
            Race race = raceData.getRace();
            Form firstForm = race.skill.getFirstUnlockedFormInBranch(race.formTree, raceData.getRacialSkillLevel(),branchIndex);
            if (firstForm != null) {
                info.setSelectedForm(firstForm);
                info.selectedDBCForm = info.tempSelectedDBCForm = -1;
                info.updateClient();
            }
        }

    }
}
