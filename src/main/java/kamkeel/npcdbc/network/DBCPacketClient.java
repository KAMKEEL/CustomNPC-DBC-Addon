package kamkeel.npcdbc.network;

public class DBCPacketClient extends DBCPacketHandler {

    public static void sendClient(AbstractPacket packet) {
        DBCPacketHandler.Instance.sendToServer(packet);
    }
}
