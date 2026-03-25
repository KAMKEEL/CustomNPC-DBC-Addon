package kamkeel.npcdbc.client.gui.dbc.creator;

import JinRyuu.JRMCore.JRMCoreGuiScreen;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.helper.RaceSelectorHelper;
import kamkeel.npcdbc.mixins.late.impl.dbc.IJRMCoreGuiScreenAccessor;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.DBCRaceSelect;
import net.minecraft.client.Minecraft;

public final class VanillaCreatorBridge {

    private final CreatorSession session;
    private boolean cleanedUp = false;

    public VanillaCreatorBridge(CreatorSession session) {
        this.session = session;
    }

    public void applyPreview() {
        session.syncToVanillaStatics();
        pushPreviewDns();
        syncLocalPreviewCache();
    }

    public void applyPreviewWithRaceSync() {
        session.syncToVanillaStatics();
        byte raceValue = (byte) (session.isCustomRace()
            ? session.getVanillaRaceIndex()
            : JRMCoreGuiScreen.RaceSlcted);
        JRMCoreH.Char((byte) 0, raceValue);
        pushPreviewDns();
        syncLocalPreviewCache();
    }

    public void applyPreviewWithStateSync() {
        session.syncToVanillaStatics();
        JRMCoreH.Char((byte) 106, (byte) JRMCoreGuiScreen.StateSlcted);
        pushPreviewDns();
        syncLocalPreviewCache();
    }

    public void applyPreviewWithYearsSync() {
        session.syncToVanillaStatics();
        pushPreviewDns();
        JRMCoreH.Char((byte) 7, (byte) JRMCoreGuiScreen.YearsSlcted);
        syncLocalPreviewCache();
    }

    public void applyPreviewWithTailSync() {
        session.syncToVanillaStatics();
        pushPreviewDns();
        JRMCoreH.Char((byte) 103, (byte) (session.tail ? 1 : 0));
        syncLocalPreviewCache();
    }

    private void pushPreviewDns() {
        JRMCoreGuiScreen.setdns();
        JRMCoreH.jrmcDataFC(0, IJRMCoreGuiScreenAccessor.npcdbc$getDns());
        JRMCoreH.jrmcDataFC(1, IJRMCoreGuiScreenAccessor.npcdbc$getDnsH());

        if (session.isCustomRace()) {
            RaceSelectorHelper.setPreviewRaceIndex(session.raceIndex);
        } else {
            RaceSelectorHelper.clearPreviewRace();
        }
    }

    /**
     * Immediately patches the local client-side DBC render arrays so the
     * player preview updates within the same frame, without waiting for the
     * server round trip (~700 ms).
     * <p>
     * The renderer ({@code RenderPlayerJBRA}) reads appearance data from:
     * <ul>
     *   <li>{@code JRMCoreH.data1[pl]} — semicolon-separated:
     *       race;dns;powerType;class;accept;weight;bodysuit;headgear;vanillaArmor;kiWeapon; </li>
     *   <li>{@code JRMCoreH.data2[pl]} — "state;state2"</li>
     * </ul>
     * This method finds the local player's index in {@code JRMCoreH.plyrs},
     * then splices the session's race, dns, and state values into those arrays.
     * <p>
     * The existing packet path ({@code Char(...)}, {@code jrmcDataFC(...)}) is
     * preserved and still sends updates to the server for authoritative sync.
     */
    private void syncLocalPreviewCache() {
        String[] plyrs = JRMCoreH.plyrs;
        String[] data1 = JRMCoreH.data1;
        String[] data2 = JRMCoreH.data2;
        if (plyrs == null || data1 == null || data2 == null)
            return;

        String localName = Minecraft.getMinecraft().thePlayer.getCommandSenderName();
        int pl = -1;
        for (int i = 0; i < plyrs.length; i++) {
            if (localName.equals(plyrs[i])) {
                pl = i;
                break;
            }
        }
        if (pl < 0 || pl >= data1.length || pl >= data2.length)
            return;

        // --- data1: splice race and dns into the existing semicolon string ---
        String dns = IJRMCoreGuiScreenAccessor.npcdbc$getDns();
        int race = session.isCustomRace()
            ? session.getVanillaRaceIndex()
            : session.raceIndex;

        String[] parts = data1[pl].split(";");
        if (parts.length >= 2) {
            parts[0] = String.valueOf(race);
            parts[1] = dns;
            data1[pl] = String.join(";", parts);
        }

        // --- data2: splice state into index 0, preserve existing state2 ---
        String[] d2 = data2[pl].split(";");
        d2[0] = String.valueOf(session.stateSelected);
        JRMCoreH.data2[pl] = String.join(";", d2);

        DBCData clientData = DBCData.getClient();
        clientData.Race = (byte) race;
        clientData.DNS = dns;
        clientData.State = (byte) session.stateSelected;
        if (d2.length > 1) {
            try {
                clientData.State2 = Byte.parseByte(d2[1]);
            } catch (NumberFormatException ignored) {
            }
        }
        clientData.DNSHair = IJRMCoreGuiScreenAccessor.npcdbc$getDnsH();
        clientData.skinType = (byte) JRMCoreGuiScreen.SkinTypeSlcted;
        clientData.renderingHairColor = session.hairColor;
        clientData.addonRaceID = session.addonRaceId;
    }

    public void commit() {
        session.syncToVanillaStatics();

        int addonRaceId = -1;
        if (session.isCustomRace()) {
            Race customRace = session.getSelectedCustomRace();
            if (customRace != null) {
                addonRaceId = customRace.id;
            }
            JRMCoreGuiScreen.RaceSlcted = 0;
        }

        DBCPacketHandler.Instance.sendToServer(new DBCRaceSelect(addonRaceId));

        JRMCoreGuiScreen.setdns();
        JRMCoreH.jrmcDataFC(0, IJRMCoreGuiScreenAccessor.npcdbc$getDns());
        JRMCoreH.jrmcDataFC(1, IJRMCoreGuiScreenAccessor.npcdbc$getDnsH());
        JRMCoreH.jrmcDataFC(2, JRMCoreGuiScreen.KiColorSlcted + "");
        JRMCoreH.Char((byte) 7, (byte) JRMCoreGuiScreen.YearsSlcted);
        JRMCoreH.Char((byte) 0, (byte) JRMCoreGuiScreen.RaceSlcted);
        JRMCoreH.Char((byte) 2, (byte) JRMCoreGuiScreen.PwrtypSlcted);
        JRMCoreH.Char((byte) 3, (byte) JRMCoreGuiScreen.ClassSlcted);
        JRMCoreH.Char((byte) 4, (byte) 1);

        cleanup();
        Minecraft.getMinecraft().thePlayer.closeScreen();
    }

    public void cleanup() {
        if (cleanedUp) return;
        cleanedUp = true;
        RaceSelectorHelper.clearPreviewRace();
        RaceSelectorHelper.setPreviewActive(false);
    }
}
