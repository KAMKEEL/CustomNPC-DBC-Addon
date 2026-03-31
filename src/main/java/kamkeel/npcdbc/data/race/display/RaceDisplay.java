package kamkeel.npcdbc.data.race.display;

import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.api.client.overlay.IOverlay;
import kamkeel.npcdbc.constants.BodyLayer;
import kamkeel.npcdbc.data.overlay.DisplayChain;
import kamkeel.npcdbc.data.overlay.DisplayChainGroup;
import kamkeel.npcdbc.data.overlay.DisplayLayer;
import kamkeel.npcdbc.data.overlay.Overlay;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;

import java.util.*;

public class RaceDisplay implements DataSerializable {

    /** Canonical body layer order for DBC metadata. */
    private static final String[] BODY_LAYER_ORDER = {
        BodyLayer.BODY_CM, BodyLayer.BODY_C1, BodyLayer.BODY_C2, BodyLayer.BODY_C3
    };

    // ── Chains ─────────────────────────────────────────────────────────────────
    private final Map<String, DisplayChainGroup> groups = new LinkedHashMap<>();

    // ── Creator metadata ───────────────────────────────────────────────────────
    public String rendererKey;

    private int[] skinLimits = {1, 1, 5, 5, 6, 2};

    private int genderCount = 2;

    private String hairType = "H";

    public int bodyColorPresetCount = 1;

    public String allowedPowerTypes = "012";

    private int customSkinMode = 2;

    public int fixedHairColor = -1;

    private String raceAllow = "DBC";

    // ── Constructor ────────────────────────────────────────────────────────────

    public RaceDisplay() {}

    // ── Chain management ───────────────────────────────────────────────────────

    public void addChain(DisplayChain chain) {
        groups.computeIfAbsent(chain.stateKey, k -> DisplayChainGroup.of()).add(chain);
    }

    public void addGroup(DisplayChainGroup group) {
        for (DisplayChain chain : group.getChains())
            addChain(chain);
    }

    public DisplayChainGroup getGroup(String stateKey) {
        DisplayChainGroup group = groups.get(stateKey);
        return group != null ? group : DisplayChainGroup.of();
    }

    public List<DisplayChain> getChains(String stateKey) {
        return getGroup(stateKey).getChains();
    }

    public List<DisplayChain> getBaseChains() {
        return getChains("base");
    }

    public Map<String, DisplayChainGroup> getAllGroups() {
        return Collections.unmodifiableMap(groups);
    }

    // ── Layer convenience ──────────────────────────────────────────────────────

    public DisplayLayer getLayer(String key) {
        String lower = key.toLowerCase();
        for (DisplayChainGroup group : groups.values()) {
            DisplayLayer found = group.getLayer(lower);
            if (found != null) return found;
        }
        return null;
    }

    public boolean hasLayer(String key) {
        return getLayer(key) != null;
    }

    // ── Properties ─────────────────────────────────────────────────────────────

    public int getGenderCount() { return genderCount; }

    public void setGenderCount(int genderCount) {
        this.genderCount = Math.max(1, Math.min(2, genderCount));
    }

    public String getHairType() { return hairType; }

    public void setHairType(String hairType) {
        if (isHairTypeValid(hairType)) this.hairType = hairType;
    }

    private boolean isHairTypeValid(String h) {
        return h != null && (h.equals("H") || h.equals("A") || h.equals("R") || h.equals("X"));
    }

    public String getAllowedPowerTypes() { return allowedPowerTypes; }

    public void setAllowedPowerTypes(String allowedPowerTypes) {
        if (isPowerTypeValid(allowedPowerTypes)) this.allowedPowerTypes = allowedPowerTypes;
    }

    private boolean isPowerTypeValid(String pwt) {
        if (pwt == null || pwt.isEmpty() || pwt.length() > 3) return false;
        for (int i = 0; i < pwt.length(); i++) {
            char c = pwt.charAt(i);
            if (c < '0' || c > '2') return false;
        }
        return true;
    }

    public int getCustomSkinMode() { return customSkinMode; }

    public void setCustomSkinMode(int customSkinMode) {
        this.customSkinMode = Math.max(0, Math.min(2, customSkinMode));
    }

    public String getRaceAllow() { return raceAllow; }

    public void setRaceAllow(String raceAllow) {
        if (isRaceAllowValid(raceAllow)) this.raceAllow = raceAllow;
    }

    private boolean isRaceAllowValid(String ra) {
        return ra != null && (ra.equals("All") || ra.equals("DBC") || ra.equals("HHC"));
    }

    public int[] getSkinLimits() {
        return Arrays.copyOf(skinLimits, skinLimits.length);
    }

    public void setSkinLimits(int bodyType, int colorSlots, int nose, int mouth, int eyes, int eyeColorSlots) {
        skinLimits = new int[]{bodyType, colorSlots, nose, mouth, eyes, eyeColorSlots};
    }

    // ── Body color introspection ───────────────────────────────────────────────

    public int getBodyColorLayerCount() {
        List<DisplayChain> bodyChains = getBaseChains();
        int count = 0;
        for (String id : BODY_LAYER_ORDER) {
            for (DisplayChain chain : bodyChains) {
                if (chain.getLayer(id) != null) { count++; break; }
            }
        }
        return Math.max(count, 1);
    }

    public List<String> getBodyColorLayerIds() {
        List<DisplayChain> bodyChains = getBaseChains();
        List<String> ids = new ArrayList<>();
        for (String id : BODY_LAYER_ORDER) {
            for (DisplayChain chain : bodyChains) {
                if (chain.getLayer(id) != null) { ids.add(id); break; }
            }
        }
        if (ids.isEmpty()) ids.add(BodyLayer.BODY_CM);
        return ids;
    }

    public int getEyeColorLayerCount() {
        List<DisplayChain> faceChains = getBaseChains();
        boolean hasLeft = false, hasRight = false;
        for (DisplayChain chain : faceChains) {
            if (chain.getLayer(BodyLayer.EYE_LEFT) != null || chain.getLayer(BodyLayer.EYES) != null) hasLeft = true;
            if (chain.getLayer(BodyLayer.EYE_RIGHT) != null || chain.getLayer(BodyLayer.EYES) != null) hasRight = true;
        }
        return (hasLeft ? 1 : 0) + (hasRight ? 1 : 0);
    }

    public int getTextureVariantCount(String layerId) {
        DisplayLayer layer = getLayer(layerId);
        return (layer != null && layer.getTextureVariantCount() > 0)
            ? layer.getTextureVariantCount()
            : 1;
    }

    public int getFixedHairColor() {
        DisplayLayer hairLayer = getLayer(BodyLayer.HAIR);
        if (hairLayer != null && hairLayer.isFixedColor()) {
            return hairLayer.getDefaultColor();
        }
        return -1;
    }

    // ── Metadata sync ──────────────────────────────────────────────────────────

    public void syncCreatorMetadata() {
        skinLimits[1] = getBodyColorLayerCount();
        skinLimits[2] = getTextureVariantCount("nose");
        skinLimits[3] = getTextureVariantCount("mouth");
        skinLimits[4] = getTextureVariantCount("eye_left");
        skinLimits[5] = getEyeColorLayerCount();
        fixedHairColor = getFixedHairColor();

        List<DisplayChain> bodyChains = getBaseChains();
        int maxPresets = 0;
        boolean foundExplicit = false;
        for (DisplayChain chain : bodyChains) {
            if (chain.presetCount >= 0) {
                bodyColorPresetCount = Math.max(1, chain.presetCount);
                foundExplicit = true;
                break;
            }
            for (Overlay overlay : chain.overlays) {
                if (overlay instanceof DisplayLayer) {
                    maxPresets = Math.max(maxPresets, ((DisplayLayer) overlay).getColorPresetCount());
                }
            }
        }
        if (!foundExplicit) {
            bodyColorPresetCount = Math.max(1, maxPresets);
        }
    }

    // ── Color / texture build helpers ──────────────────────────────────────────

    public int[] buildDefaultBodyColorRow() {
        List<String> layerIds = getBodyColorLayerIds();
        int[] row = new int[layerIds.size()];
        for (int i = 0; i < layerIds.size(); i++) {
            DisplayLayer layer = getLayer(layerIds.get(i));
            row[i] = (layer != null) ? layer.getDefaultColor() : 0;
        }
        return row;
    }

    public int[] buildBodyColorRowForPreset(int presetIndex) {
        List<String> layerIds = getBodyColorLayerIds();
        int[] row = new int[layerIds.size()];
        for (int i = 0; i < layerIds.size(); i++) {
            DisplayLayer layer = getLayer(layerIds.get(i));
            if (layer != null) {
                List<Color> presets = layer.getColorPresets();
                row[i] = (presetIndex < presets.size()) ? presets.get(presetIndex).color : layer.getDefaultColor();
            }
        }
        return row;
    }

    public int[] buildEyeColorRows() {
        DisplayLayer eyeLayer  = getLayer(BodyLayer.EYES);
        DisplayLayer leftLayer  = getLayer(BodyLayer.EYE_LEFT);
        DisplayLayer rightLayer = getLayer(BodyLayer.EYE_RIGHT);

        int genericEye = (eyeLayer  != null) ? eyeLayer.getDefaultColor()  : 0;
        int leftEye    = (leftLayer  != null && leftLayer.hasDefaultColor())  ? leftLayer.getDefaultColor()  : genericEye;
        int rightEye   = (rightLayer != null && rightLayer.hasDefaultColor()) ? rightLayer.getDefaultColor() : genericEye;
        return new int[]{genericEye, leftEye, rightEye};
    }

    // ── Serialization ──────────────────────────────────────────────────────────

    @Override
    public DataCompound serialize(DataCompound data) {
        data.putString("rendererKey", rendererKey);
        data.putInt("genderCount", genderCount);
        data.putString("hairType", hairType);
        data.putString("allowedPowerTypes", allowedPowerTypes);
        data.putInt("customSkinMode", customSkinMode);
        data.putString("raceAllow", raceAllow);
        data.putIntArray("skinLimits", skinLimits);

        int total = 0;
        for (DisplayChainGroup group : groups.values()) {
            for (DisplayChain chain : group.getChains()) {
                DataCompound chainData = data.child();
                chain.serialize(chainData);
                data.put("chain_" + total, chainData);
                total++;
            }
        }
        data.putInt("chainCount", total);
        return data;
    }

    @Override
    public void deserialize(DataCompound data) {
        rendererKey = data.getString("rendererKey", rendererKey);
        setGenderCount(data.getInt("genderCount", genderCount));
        setHairType(data.getString("hairType", hairType));
        setAllowedPowerTypes(data.getString("allowedPowerTypes", allowedPowerTypes));
        setCustomSkinMode(data.getInt("customSkinMode", customSkinMode));
        setRaceAllow(data.getString("raceAllow", raceAllow));
        if (data.has("skinLimits")) {
            int[] limits = data.getIntArray("skinLimits", skinLimits);
            if (limits.length == 6) setSkinLimits(limits[0], limits[1], limits[2], limits[3], limits[4], limits[5]);
        }

        if (data.has("chainCount")) {
            int count = data.getInt("chainCount", 0);
            if (groups.isEmpty()) {
                for (int i = 0; i < count; i++) {
                    if (data.has("chain_" + i)) {
                        DisplayChain chain = new DisplayChain();
                        chain.deserialize(data.get("chain_" + i));
                        addChain(chain);
                    }
                }
            } else {
                List<DisplayChain> flat = new ArrayList<>();
                for (DisplayChainGroup g : groups.values())
                    flat.addAll(g.getChains());
                for (int i = 0; i < count && i < flat.size(); i++) {
                    if (data.has("chain_" + i))
                        flat.get(i).deserialize(data.get("chain_" + i));
                }
            }
        }
        syncCreatorMetadata();
    }
}
