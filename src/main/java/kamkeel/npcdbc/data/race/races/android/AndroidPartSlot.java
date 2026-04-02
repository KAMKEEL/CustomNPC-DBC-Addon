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
    ANY_LIMBS,
    ANY;

    public static final AndroidPartSlot[] PHYSICAL = {
        CORE, BATTERY, ARM_LEFT, ARM_RIGHT, LEG_LEFT, LEG_RIGHT
    };

    public boolean isPhysical() {
        switch (this) {
            case CORE:
            case BATTERY:
            case ARM_LEFT:
            case ARM_RIGHT:
            case LEG_LEFT:
            case LEG_RIGHT:
                return true;
            default:
                return false;
        }
    }

    public boolean fitsSlot(AndroidPartSlot s) {
        if (s == ANY) return true;
        switch (this) {
            case BOTH_ARMS: return isArm(s);
            case BOTH_LEGS: return isLeg(s);
            case ANY_LIMBS: return isLeg(s) || isArm(s);
            default: return s == this;
        }
    }

    private boolean isLeg(AndroidPartSlot s) {
        return s == LEG_LEFT || s == LEG_RIGHT || s == BOTH_LEGS;
    }

    private boolean isArm(AndroidPartSlot s) {
        return s == ARM_LEFT || s == ARM_RIGHT || s == BOTH_ARMS;
    }
}

