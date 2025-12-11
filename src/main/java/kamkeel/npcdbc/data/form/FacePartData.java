package kamkeel.npcdbc.data.form;

import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static kamkeel.npcdbc.data.form.FacePartData.Part.*;

public class FacePartData {

    public final HashMap<Integer, HashSet<Integer>> disabledParts = new HashMap<>();
    public boolean enabled;

    public FacePartData() {
        for (int i = 0; i < 7; i++)
            disabledParts.put(i, new HashSet<>());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void disable(Part part, int faceType, boolean disable) {
        disable(faceType, part.ordinal(), disable);
    }

    public void disable(int faceType, int part, boolean disabled) {
        if (disabled)
            disabledParts.get(faceType).add(part);
        else
            disabledParts.get(faceType).remove(part);
    }

    public boolean disabled(Part part, int faceType) {
        return disabled(faceType, part.ordinal());
    }

    public boolean disabled(int faceType, int part) {
        if (!enabled)
            return false;

        return disabledParts.get(faceType).contains(part) || disabledParts.get(6).contains(part);
    }

    public void toggle(int part, int faceType) {
        boolean isDisabled = disabled(faceType, part);
        disable(faceType, part, !isDisabled);
    }

    public boolean disabledEyebrows(int faceType) {
        return disabled(Eyebrows, faceType);
    }

    public boolean disabledWhite(int faceType) {
        return disabled(EyeWhite, faceType);
    }

    public boolean disabledLeftEye(int faceType) {
        return disabled(LeftEye, faceType);
    }

    public boolean disabledRightEye(int faceType) {
        return disabled(RightEye, faceType);
    }

    public boolean disabledNose(int faceType) {
        return disabled(Nose, faceType);
    }

    public boolean disabledMouth(int faceType) {
        return disabled(Mouth, faceType);
    }

    public void disableEyebrows(int faceType, boolean disable) {
        disable(Eyebrows, faceType, disable);
    }

    public void disableWhite(int faceType, boolean disable) {
        disable(EyeWhite, faceType, disable);
    }

    public void disableLeftEye(int faceType, boolean disable) {
        disable(LeftEye, faceType, disable);
    }

    public void disableRightEye(int faceType, boolean disable) {
        disable(RightEye, faceType, disable);
    }

    public void disableNose(int faceType, boolean disable) {
        disable(Nose, faceType, disable);
    }

    public void disableMouth(int faceType, boolean disable) {
        disable(Mouth, faceType, disable);
    }

    public Integer[] getFacePartsRemoved(int faceType) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < Part.values().length; i++) {
            if (disabledParts.get(faceType).contains(i))
                list.add(i);
        }

        return list.toArray(new Integer[0]);
    }

    public void readFromNBT(NBTTagCompound compound, boolean isDBCDisplay) {
        NBTTagCompound faceData = compound.getCompoundTag(isDBCDisplay ? "DBCFaceData" : "faceData");
        enabled = compound.getBoolean("enabled");

        for (int i = 0; i < 7; i++) {
            String appendix = i == 6 ? "All" : i + "";
            String faceName = isDBCDisplay ? "Face_" + appendix : "face" + appendix;

            if (!faceData.hasKey(faceName)) {
                disabledParts.get(i).clear();
                continue;
            }

            NBTTagCompound faceCompound = faceData.getCompoundTag(faceName);
            HashSet<Integer> removed = disabledParts.get(i);
            removed.clear();

            for (int j = 0; j < Part.values().length; j++) {
                String name = Part.values()[j].name();
                if (faceCompound.getBoolean(name)) {
                    removed.add(j);
                }
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound, boolean isDBCDisplay) {
        NBTTagCompound faceData = new NBTTagCompound();
        compound.setBoolean("enabled", enabled);

        for (int i = 0; i < 7; i++) {
            HashSet<Integer> removed = disabledParts.get(i);

            if (removed == null || removed.isEmpty())
                continue;

            String appendix = i == 6 ? "All" : i + "";
            NBTTagCompound faceCompound = new NBTTagCompound();
            for (int j = 0; j < Part.values().length; j++) {
                boolean isRemoved = removed.contains(j);
                faceCompound.setBoolean(Part.values()[j].name(), isRemoved);
            }

            faceData.setTag(isDBCDisplay ? "Face_" + appendix : "face" + appendix, faceCompound);
        }

        compound.setTag(isDBCDisplay ? "DBCFaceData" : "faceData", faceData);
        return compound;
    }

    public enum Part {
        Eyebrows("EYEBROW"),
        EyeWhite("EYEBASE"),
        LeftEye("EYELEFT"),
        RightEye("EYERIGHT"),
        Nose("FACENOSE"),
        Mouth("FACEMOUTH");

        private final String partId;

        Part(String partId) {
            this.partId = partId;
        }

        public String getPartId() {
            return this.partId;
        }
    }
}
