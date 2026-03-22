package kamkeel.npcdbc.data.race.display;

import net.minecraft.nbt.NBTTagCompound;

public class RaceDisplay {

    private HairType hairType = HairType.HAIR_NORMAL;
    private RaceSkinParts skinParts = new RaceSkinParts();
    private RaceColors eyeColorPresets = new RaceColors(RaceColors.PartType.EYE);
    private RaceColors bodyColorPresets = new RaceColors(RaceColors.PartType.BODY);

    // Gender
    private int genders = 2; // 1 = male only, 2 = both

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

    public int getGenders() {
        return genders;
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

    public RaceDisplay setGenders(int genders) {
        this.genders = genders;
        return this;
    }

    public RaceSkinParts getSkinParts() {
        return skinParts;
    }

    public RaceDisplay setSkinParts(RaceSkinParts skinParts) {
        this.skinParts = skinParts;
        return this;
    }

    public RaceColors getEyeColorPresets() {
        return eyeColorPresets;
    }

    public RaceDisplay setEyeColorPresets(RaceColors colors) {
        if (colors.type != RaceColors.PartType.EYE) return this;

        this.eyeColorPresets = colors;
        return this;
    }

    public RaceColors getBodyColorPresets() {
        return bodyColorPresets;
    }

    public RaceDisplay setBodyColorPresets(RaceColors colors) {
        if (colors.type != RaceColors.PartType.EYE) return this;

        this.bodyColorPresets = colors;
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
        genders = tag.getInteger("genders");
        eyeColorPresets.readNBT(tag);
        bodyColorPresets.readNBT(tag);
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("hairType", hairType.id);
        tag.setInteger("genders", genders);

        eyeColorPresets.writeNBT(tag);
        bodyColorPresets.writeNBT(tag);

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
