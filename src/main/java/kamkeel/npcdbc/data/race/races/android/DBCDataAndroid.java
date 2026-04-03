package kamkeel.npcdbc.data.race.races.android;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.overlay.Overlay;
import kamkeel.npcdbc.data.overlay.OverlayChain;
import kamkeel.npcdbc.data.overlay.OverlayManager;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.LogWriter;
import noppes.npcs.api.entity.IPlayer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DBCDataAndroid {

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
        addOverlays(slot, part.getData());
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
        removeOverlays(part.getData());
    }

    public void tick() {
        for (AndroidPartType part : getAllEquipped()) {
            part.onTick(data.player);
        }
    }

    private void addOverlays(AndroidPartSlot slot, AndroidPartData part) {
        if (!part.hasOverlays()) return;

        OverlayManager manager = data.getDBCInfo().overlayManager;
        if (manager.getChains().isEmpty()) {
            manager.getChains().add(modifyChainType(part.getOverlays(), slot));
            return;
        }

        for (OverlayChain chain : manager.getChains()) {
            if (chain.getName().equals(part.getId()))
                continue;
            manager.add(modifyChainType(part.getOverlays(), slot));
        }
    }

    private void removeOverlays(AndroidPartData part) {
        if (!part.hasOverlays()) return;

        for (OverlayChain chain : data.getDBCInfo().overlayManager.getChains()) {
            if (chain.getName().equals(part.getId())) {
                data.getDBCInfo().overlayManager.remove(chain);
            }
        }
    }

    private OverlayChain modifyChainType(OverlayChain chain, AndroidPartSlot slot) {
        for (Overlay overlay : chain.getOverlays()) {
            overlay.type(AndroidPartSlot.overlayType(slot));
        }
        return chain;
    }

    // ──────────────────── NBT ────────────────────

    public void saveToNBT(NBTTagCompound comp) {
        NBTTagCompound tag = new NBTTagCompound();
        for (AndroidPartSlot slot : AndroidPartSlot.PHYSICAL) {
            tag.setString(slot.name(), equippedParts.getOrDefault(slot, ""));
        }
        comp.setTag("androidParts", tag);
    }

    public void loadFromNBT(NBTTagCompound comp) {
        equippedParts.clear();
        if (!comp.hasKey("androidParts")) return;
        NBTTagCompound tag = comp.getCompoundTag("androidParts");
        for (AndroidPartSlot slot : AndroidPartSlot.PHYSICAL) {
            if (tag.hasKey(slot.name()))
                equippedParts.put(slot, tag.getString(slot.name()));
        }
    }
}
