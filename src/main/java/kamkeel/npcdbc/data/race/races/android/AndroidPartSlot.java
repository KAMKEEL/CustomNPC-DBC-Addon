package kamkeel.npcdbc.data.race.races.android;

public enum AndroidPartSlot {
    CORE,
    BATTERY,
    ARM_LEFT,
    ARM_RIGHT,
    BOTH_ARMS,
    LEG_LEFT,
    LEG_RIGHT,
    BOTH_LEGS,
    ALL_LIMBS,
    ANY;

    public boolean fitsSlot(AndroidPartSlot s) {
        if (s == ANY) return true;

        switch (this) {
            case BOTH_ARMS:
                return isArm(s);
            case BOTH_LEGS:
                return isLeg(s);
            case ALL_LIMBS:
                return isArm(s) || isLeg(s);
            default:
                return s == this;
        }
    }

    private boolean isLeg(AndroidPartSlot s) {
        return s == LEG_LEFT || s == LEG_RIGHT || s == BOTH_LEGS;
    }

    private boolean isArm(AndroidPartSlot s) {
        return s == ARM_LEFT || s == ARM_RIGHT || s == BOTH_ARMS;
    }
}
