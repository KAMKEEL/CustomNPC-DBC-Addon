package kamkeel.npcdbc.data.race.display;


import kamkeel.npcdbc.api.Color;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

public class RaceColors {

    public enum PartType {
        EYE(4, 6),
        BODY(5, 6);

        public final int maxColors;
        public final int maxPresets;

        PartType(int maxColors, int maxPresets) {
            this.maxColors = maxColors;
            this.maxPresets = maxPresets;
        }
    }

    public final PartType type;
    public final List<Color[]> colors;

    public RaceColors(PartType type) {
        this.type = type;
        this.colors = new ArrayList<>();

        for (int i = 0; i < type.maxColors; i++) {
            List<Color> list = new ArrayList<>();
            for (int j = 0; j < type.maxPresets; j++) {
                list.add(new Color(0xFFFFFF));
            }
            colors.add(list.toArray(new Color[0]));
        }
    }

    public Color getColor(int index, int preset) {
        if (index >= type.maxColors || preset >= type.maxPresets) return null;

        return colors.get(index)[preset];
    }

    public void setColor(int index, int preset, Color color) {
        if (index >= type.maxColors || preset >= type.maxPresets) return;

        colors.get(index)[preset] = color;
    }

    public Color[] getPresetColors(int index) {
        if (index >= type.maxColors) return null;

        return colors.get(index);
    }

    public void setPresetColors(int index, Color... presets) {
        if (index >= type.maxColors || presets.length != type.maxPresets) return;

        colors.set(index, presets);
    }

    public int[][] toIntArray() {
        List<int[]> list = new ArrayList<>();

        for (int i = 0; i < type.maxColors; i++) {
            int[] currentArray = new int[type.maxPresets];

            for (int j = 0; j < type.maxPresets; j++) {
                Color c = getColor(i, j);
                currentArray[j] = (c != null ? c.color : 0xFFFFFF);
            }

            list.add(currentArray);
        }

        return list.toArray(new int[0][]);
    }

    private void fillColors() {
        for (int i = 0; i < type.maxColors; i++) {
            List<Color> list = new ArrayList<>();
            for (int j = 0; j < type.maxPresets; j++) {
                list.add(new Color(0xFFFFFF));
            }
            colors.add(list.toArray(new Color[0]));
        }
    }

    public void writeNBT(NBTTagCompound nbt) {
        NBTTagCompound c = new NBTTagCompound();
        int[][] intArray = toIntArray();

        for (int i = 0; i < type.maxColors; i++) {
            c.setIntArray("preset_" + i, intArray[i]);
        }

        nbt.setTag(type.name().toLowerCase() + "Colors", c);
    }

    public void readNBT(NBTTagCompound nbt) {
        if (!nbt.hasKey(type.name().toLowerCase() + "Colors")) {
            fillColors();
            return;
        }

        NBTTagCompound c = nbt.getCompoundTag(type.name().toLowerCase() + "Colors");

        for (int i = 0; i < type.maxColors; i++) {
            int[] arr = c.getIntArray("preset_" + i);

            for (int j = 0; j < type.maxPresets && j < arr.length; j++) {
                this.setColor(i, j, new Color(arr[j]));
            }
        }
    }
}
