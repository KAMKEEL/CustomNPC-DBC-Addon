package kamkeel.npcdbc.data.race.races.android;

import kamkeel.npcdbc.api.client.overlay.IOverlay;

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

    public static IOverlay.Type overlayType(AndroidPartSlot s) {
        if (!s.isPhysical()) return null;

        switch(s) {
            case ARM_LEFT: return IOverlay.Type.LeftArm;
            case ARM_RIGHT: return IOverlay.Type.RightArm;
            case LEG_LEFT: return IOverlay.Type.LeftLeg;
            case LEG_RIGHT: return IOverlay.Type.RightLeg;
            default: return IOverlay.Type.ALL;
        }
    }

    private boolean isLeg(AndroidPartSlot s) {
        return s == LEG_LEFT || s == LEG_RIGHT || s == BOTH_LEGS;
    }

    private boolean isArm(AndroidPartSlot s) {
        return s == ARM_LEFT || s == ARM_RIGHT || s == BOTH_ARMS;
    }
}

