package kamkeel.npcdbc.data.race.races.android;

import kamkeel.npcdbc.client.model.ModelDBC;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.overlay.Overlay;
import kamkeel.npcdbc.data.overlay.OverlayChain;
import kamkeel.npcdbc.data.overlay.OverlayContext;
import kamkeel.npcdbc.data.overlay.OverlayManager;
import kamkeel.npcdbc.data.race.races.Android;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.LogWriter;
import noppes.npcs.api.entity.IPlayer;

import java.util.*;

public class DBCDataAndroid implements DataSerializable {

    private final DBCData data;

    // slot -> part id string (null = empty)
    private final Map<AndroidPartSlot, String> equippedParts = new LinkedHashMap<>();

    public DBCDataAndroid(DBCData data) {
        this.data = data;
    }

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

        IPlayer player = PlayerDataUtil.getIPlayer(data.player);
        DBCPlayerEvent.AndroidPartEvent event = new DBCPlayerEvent.AndroidPartEvent.Equip(player, part.getId(), slot.ordinal());

        DBCEventHooks.onAndroidPartEvent(event);
        equippedParts.put(slot, part.getId());
        part.onEquip(data.player);
    }

    public void unequip(AndroidPartSlot slot) {
        if (isSlotEmpty(slot)) return;

        AndroidPartType part = getEquipped(slot);
        String partId = part.getId();
        IPlayer player = PlayerDataUtil.getIPlayer(data.player);
        DBCPlayerEvent.AndroidPartEvent event = new DBCPlayerEvent.AndroidPartEvent.Unequip(player, partId, slot.ordinal());

        DBCEventHooks.onAndroidPartEvent(event);
        equippedParts.put(slot, "");
        part.onUnequip(data.player);
    }

    public void tick() {
        for (AndroidPartType part : getAllEquipped()) {
            part.onTick(data.player);
        }
    }

    public List<OverlayChain> getOverlays() {
        List<OverlayChain> chains = new ArrayList<>();
        for (AndroidPartType type : getAllEquipped()) {
            AndroidPartData part = type.getData();
            if (part.hasOverlays())
                chains.add(part.getOverlays());
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
        equippedParts.clear();

        if (!data.has("androidParts")) return;

        DataCompound c = data.get("androidParts");
        for (AndroidPartSlot slot : AndroidPartSlot.PHYSICAL) {
            if (c.has(slot.name()))
                equippedParts.put(slot, c.getString(slot.name(), ""));
        }
    }
}
