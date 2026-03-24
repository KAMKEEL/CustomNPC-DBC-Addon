package kamkeel.npcdbc.client.gui.dbc.creator;

import JinRyuu.JRMCore.JRMCoreGuiScreen;
import JinRyuu.JRMCore.JRMCoreH;
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
    }

    public void applyPreviewWithRaceSync() {
        session.syncToVanillaStatics();
        byte raceValue = (byte) (session.isCustomRace()
            ? session.getVanillaRaceIndex()
            : JRMCoreGuiScreen.RaceSlcted);
        JRMCoreH.Char((byte) 0, raceValue);
        pushPreviewDns();
    }

    public void applyPreviewWithStateSync() {
        session.syncToVanillaStatics();
        JRMCoreH.Char((byte) 106, (byte) JRMCoreGuiScreen.StateSlcted);
        pushPreviewDns();
    }

    public void applyPreviewWithYearsSync() {
        session.syncToVanillaStatics();
        pushPreviewDns();
        JRMCoreH.Char((byte) 7, (byte) JRMCoreGuiScreen.YearsSlcted);
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
