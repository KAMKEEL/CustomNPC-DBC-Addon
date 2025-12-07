package kamkeel.npcdbc.data.form;

import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FormFaceData {
    public HashMap<Integer, HashSet<Integer>> facePartsRemoved;

    public FormFaceData() {
        facePartsRemoved = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            facePartsRemoved.put(i, new HashSet<>());
        }
    }

    public void readFromNBT(NBTTagCompound compound, boolean isDBCDisplay) {
        NBTTagCompound faceData = compound.getCompoundTag(isDBCDisplay ? "DBCFaceData" : "faceData");
        for (int i = 0; i < 7; i++) {
            String appendix = i == 6 ? "All" : i + "";
            String faceName = isDBCDisplay ? "Face_" + appendix : "face" + appendix;

            if (!faceData.hasKey(faceName)) {
                facePartsRemoved.get(i).clear();
                continue;
            }

            NBTTagCompound faceCompound = faceData.getCompoundTag(faceName);
            HashSet<Integer> removed = facePartsRemoved.get(i);
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
        for (int i = 0; i < 7; i++) {
            HashSet<Integer> removed = facePartsRemoved.get(i);

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

    public Integer[] getFacePartsRemoved(int faceType) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < Part.values().length; i++) {
            if (facePartsRemoved.get(faceType).contains(i))
                list.add(i);
        }

        return list.toArray(new Integer[0]);
    }

    public void setFacePartsRemoved(int faceType, Part part, boolean enable) {
        setFacePartRemoved(faceType, part.ordinal(), enable);
    }

    public void setFacePartRemoved(int faceType, int part, boolean enable) {
        if (enable) {
            facePartsRemoved.get(faceType).remove(part);
        } else {
            facePartsRemoved.get(faceType).add(part);
        }
    }

    public void setFacePartRemoved(int faceType, int part) {
        setFacePartRemoved(faceType, part, false);
    }

    public boolean hasRemoved(int faceType, Part part) {
        return hasRemoved(faceType, part.ordinal());
    }

    public boolean hasRemoved(int faceType, int part) {
        return facePartsRemoved.get(faceType).contains(part) || facePartsRemoved.get(6).contains(part);
    }

    public void toggleFacePart(int faceType, int part) {
        boolean isRemoved = hasRemoved(faceType, part);
        setFacePartRemoved(faceType, part, isRemoved);
    }

    public boolean hasEyebrowsRemoved(int faceType) {
        return hasRemoved(faceType, Part.Eyebrows.ordinal());
    }

    public boolean hasWhiteRemoved(int faceType) {
        return hasRemoved(faceType, Part.White.ordinal());
    }

    public boolean hasLeftEyeRemoved(int faceType) {
        return hasRemoved(faceType, Part.LeftEye.ordinal());
    }

    public boolean hasRightEyeRemoved(int faceType) {
        return hasRemoved(faceType, Part.RightEye.ordinal());
    }

    public boolean hasNoseRemoved(int faceType) {
        return hasRemoved(faceType, Part.Nose.ordinal());
    }

    public boolean hasMouthRemoved(int faceType) {
        return hasRemoved(faceType, Part.Mouth.ordinal());
    }

    public void setEyebrowsRemoved(int faceType, boolean enable) {
        setFacePartRemoved(faceType, Part.Eyebrows.ordinal(), enable);
    }

    public void setWhiteRemoved(int faceType, boolean enable) {
        setFacePartRemoved(faceType, Part.White.ordinal(), enable);
    }

    public void setLeftEyeRemoved(int faceType, boolean enable) {
        setFacePartRemoved(faceType, Part.LeftEye.ordinal(), enable);
    }

    public void setRightEyeRemoved(int faceType, boolean enable) {
        setFacePartRemoved(faceType, Part.RightEye.ordinal(), enable);
    }

    public void setNoseRemoved(int faceType, boolean enable) {
        setFacePartRemoved(faceType, Part.Nose.ordinal(), enable);
    }

    public void setMouthRemoved(int faceType, boolean enable) {
        setFacePartRemoved(faceType, Part.Mouth.ordinal(), enable);
    }

    public enum Part {
        Eyebrows("EYEBROW"),
        White("EYEBASE"),
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
