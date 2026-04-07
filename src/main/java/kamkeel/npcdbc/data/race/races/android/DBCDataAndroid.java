package kamkeel.npcdbc.data.race.races.android;

import kamkeel.npcdbc.data.overlay.Overlay;
import kamkeel.npcdbc.data.overlay.OverlayChain;
import kamkeel.npcdbc.data.race.progression.RaceDataHolder;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import noppes.npcs.LogWriter;
import noppes.npcs.api.entity.IPlayer;

import java.util.*;

public class DBCDataAndroid extends RaceDataHolder {

    // slot -> part id string (null = empty)
    private final Map<AndroidPartSlot, String> equippedParts = new LinkedHashMap<>();

    // ──────────────────── Public API ────────────────────

    public AndroidPartType getEquipped(AndroidPartSlot slot) {
        String id = equippedParts.get(slot);
        if (id == null || id.isEmpty()) return null;
        return AndroidPartType.byId(id);
    }

    public AndroidPartType[] getAllEquipped() {
        List<AndroidPartType> list = new ArrayList<>();
        for (String key : equippedParts.values()) {
            AndroidPartType type = AndroidPartType.byId(key);
            if (type == null) continue;

            list.add(type);
        }

        return list.toArray(new AndroidPartType[0]);
    }

    public boolean isSlotEmpty(AndroidPartSlot slot) {
        return getEquipped(slot) == null;
    }

    public void equip(AndroidPartSlot slot, AndroidPartType part) {
        if (part == null) return;

        if (!part.fitsSlot(slot)) {
            LogWriter.error("Part " + part.getId() + " does not fit slot " + slot.name());
            return;
        }

        if (!slot.isPhysical()) {
            LogWriter.error("Slot " + slot.name() + " is not physical");
            return;
        }

        IPlayer player = PlayerDataUtil.getIPlayer(dbcData.player);
        DBCPlayerEvent.AndroidPartEvent event = new DBCPlayerEvent.AndroidPartEvent.Equip(player, part.getId(), slot.ordinal());

        DBCEventHooks.onAndroidPartEvent(event);
        equippedParts.put(slot, part.getId());
        part.onEquip(dbcData.player);
    }

    public void unequip(AndroidPartSlot slot) {
        if (isSlotEmpty(slot)) return;

        AndroidPartType part = getEquipped(slot);
        String partId = part.getId();
        IPlayer player = PlayerDataUtil.getIPlayer(dbcData.player);
        DBCPlayerEvent.AndroidPartEvent event = new DBCPlayerEvent.AndroidPartEvent.Unequip(player, partId, slot.ordinal());

        DBCEventHooks.onAndroidPartEvent(event);
        equippedParts.put(slot, "");
        part.onUnequip(dbcData.player);
    }

    public void tick() {
        for (AndroidPartType part : getAllEquipped()) {
            part.onTick(dbcData.player);
        }
    }

    public List<OverlayChain> getOverlays() {
        List<OverlayChain> chains = new ArrayList<>();
        for (AndroidPartSlot slot : AndroidPartSlot.PHYSICAL) {
            AndroidPartType type = getEquipped(slot);
            if (type == null) continue;

            AndroidPartData part = type.getData();
            if (part.hasOverlays()) {
                OverlayChain chain = part.getOverlays();

                if (part.isMatchSlot()) {
                    OverlayChain cloned = chain.copy();
                    for (Overlay overlay : cloned.getOverlays()) {
                        overlay.type(AndroidPartSlot.overlayType(slot));
                    }
                    chains.add(cloned);
                } else {
                    chains.add(chain);
                }
            }
        }

        return chains.isEmpty() ? null : chains;
    }

    // ──────────────────── SERIALIZATION ────────────────────

    @Override
    public DataCompound serialize(DataCompound data) {
        DataCompound c = DataCompound.create();

        for (AndroidPartSlot slot : AndroidPartSlot.PHYSICAL) {
            c.putString(slot.name(), equippedParts.getOrDefault(slot, ""));
        }

        data.put("androidParts", c);
        return data;
    }

    @Override
    public void deserialize(DataCompound data) {
        if (!data.has("androidParts")) return;

        DataCompound c = data.get("androidParts");
        for (AndroidPartSlot slot : AndroidPartSlot.PHYSICAL) {
            if (c.has(slot.name()))
                equippedParts.put(slot, c.getString(slot.name(), ""));
        }
    }
}
