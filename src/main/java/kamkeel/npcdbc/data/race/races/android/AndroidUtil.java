package kamkeel.npcdbc.data.race.races.android;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.overlay.OverlayChain;
import kamkeel.npcdbc.data.overlay.OverlayContext;

import java.util.List;

public class AndroidUtil {

    public static boolean isAndroid(DBCData data) {
        return data != null && data.addonRace.isCustomRace() && "android".equals(data.currentRaceKey);
    }

    public static DBCDataAndroid getData(DBCData data) {
        if (!isAndroid(data))
            return null;

        return (DBCDataAndroid) data.addonRace.customData.get(DBCDataAndroid.KEY);
    }

    public static void tickIfAndroid(DBCData dbcData) {
        if (!isAndroid(dbcData)) return;

        DBCDataAndroid data = getData(dbcData);
        if (data == null) return;

        data.tick();
    }

    public static void applyPartOverlayChains(List<OverlayChain> chains, OverlayContext ctx) {
        if (!isAndroid(ctx.dbcData)) return;

        DBCDataAndroid data = getData(ctx.dbcData);
        if (data == null) return;

        List<OverlayChain> overlays = data.getOverlays();
        if (overlays == null) return;

        chains.addAll(overlays);
    }

    public static AndroidPartType getEquippedPart(DBCData dbcData, AndroidPartSlot slot) {
        if (!slot.isPhysical()) return null;
        if (!isAndroid(dbcData)) return null;

        DBCDataAndroid data = getData(dbcData);
        if (data == null) return null;

        return data.getEquipped(slot);
    }
}
