package kamkeel.npcdbc.data.race;

import net.minecraft.nbt.NBTTagCompound;

public class RaceDisplay {

    private HairType hairType = HairType.HAIR_NORMAL;
    private RaceSkinParts skinParts = new RaceSkinParts();

    // Skin
    private int colorPresetLimit = 1;
    private int colorMinRacial = -1;

    // Gender
    private int genders = 2; // 1 = male only, 2 = both

    // Eye color presets (3 presets, 1 color each)
    private int[] eyeColorPresets = new int[]{1};

    // Body color presets (7 presets, 4 colors each: bodycm, bodyc1, bodyc2, bodyc3)
    private int[][] bodyColorPresets = new int[][]{{-1, -1, -1, -1}};

    public RaceDisplay() {}

    // -------------------------
    // Getters
    // -------------------------

    public String getHairType() {
        return hairType.id;
    }

    public HairType getHairTypeEnum() {
        return hairType;
    }

    public int getColorPresetLimit() {
        return colorPresetLimit;
    }

    public int getColorMinRacial() {
        return colorMinRacial;
    }

    public int getGenders() {
        return genders;
    }

    public int[] getEyeColorPresets() {
        return eyeColorPresets;
    }

    public int[][] getBodyColorPresets() {
        return bodyColorPresets;
    }

    // -------------------------
    // Setters (fluent)
    // -------------------------

    public RaceDisplay setHairType(String hairType) {
        this.hairType = HairType.byId(hairType);
        return this;
    }

    public RaceDisplay setHairType(HairType hairType) {
        this.hairType = hairType;
        return this;
    }

    public RaceDisplay setColorPresetLimit(int colorPresetLimit) {
        this.colorPresetLimit = colorPresetLimit;
        return this;
    }

    public RaceDisplay setColorMinRacial(int colorMinRacial) {
        this.colorMinRacial = colorMinRacial;
        return this;
    }

    public RaceDisplay setGenders(int genders) {
        this.genders = genders;
        return this;
    }

    public RaceDisplay setEyeColorPresets(int[] eyeColorPresets) {
        if (eyeColorPresets.length != 3)
            throw new IllegalArgumentException("eyeColorPresets must have exactly 3 values");
        this.eyeColorPresets = eyeColorPresets;
        return this;
    }

    public RaceDisplay setBodyColorPresets(int[][] bodyColorPresets) {
        if (bodyColorPresets.length != 7)
            throw new IllegalArgumentException("bodyColorPresets must have exactly 7 presets");
        for (int[] preset : bodyColorPresets)
            if (preset.length != 4)
                throw new IllegalArgumentException("each bodyColorPreset must have exactly 4 colors");
        this.bodyColorPresets = bodyColorPresets;
        return this;
    }

    public RaceSkinParts getSkinParts() {
        return skinParts;
    }

    public RaceDisplay setSkinParts(RaceSkinParts skinParts) {
        this.skinParts = skinParts;
        return this;
    }

    public int[] getSkinLimits() {
        return skinParts.toSkinLimits();
    }

    // -------------------------
    // NBT
    // -------------------------

    public void readFromNBT(NBTTagCompound tag) {
        hairType = HairType.byId(tag.getString("hairType"));
        colorPresetLimit = tag.getInteger("colorPresetLimit");
        colorMinRacial = tag.getInteger("colorMinRacial");
        genders = tag.getInteger("genders");
        eyeColorPresets = tag.getIntArray("eyeColorPresets");

        for (int i = 0; i < 7; i++)
            bodyColorPresets[i] = tag.getIntArray("bodyColorPresets_" + i);
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("hairType", hairType.id);
        tag.setInteger("colorPresetLimit", colorPresetLimit);
        tag.setInteger("colorMinRacial", colorMinRacial);
        tag.setInteger("genders", genders);
        tag.setIntArray("eyeColorPresets", eyeColorPresets);

        for (int i = 0; i < 7; i++)
            tag.setIntArray("bodyColorPresets_" + i, bodyColorPresets[i]);

        return tag;
    }

    public enum HairType {
        HAIR_NORMAL("H"),
        HAIR_ARCOSIAN("A"),
        HAIR_NAMEKIAN("R"),
        HAIR_NONE("X");

        public final String id;

        HairType(String id) {
            this.id = id;
        }

        public static HairType byId(String id) {
            for (HairType t : HairType.values())
                if (t.id.equals(id)) return t;

            return null;
        }
    }
}
