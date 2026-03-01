package kamkeel.npcdbc.util;

import JinRyuu.JRMCore.JRMCoreH;

public class DBCColorUtil {

    /**
     * Finds the closest DBC palette index (0-30) for a given ARGB color.
     * Compares against JRMCoreH.techCol[] using Euclidean RGB distance.
     * Skips index 0 (black/transparent) to avoid defaulting to it.
     */
    public static int findClosestPaletteIndex(int argbColor) {
        int r = (argbColor >> 16) & 0xFF;
        int g = (argbColor >> 8) & 0xFF;
        int b = argbColor & 0xFF;

        int bestIndex = 1;
        int bestDistSq = Integer.MAX_VALUE;

        for (int i = 1; i < JRMCoreH.techCol.length; i++) {
            int pr = (JRMCoreH.techCol[i] >> 16) & 0xFF;
            int pg = (JRMCoreH.techCol[i] >> 8) & 0xFF;
            int pb = JRMCoreH.techCol[i] & 0xFF;

            int dr = r - pr;
            int dg = g - pg;
            int db = b - pb;
            int distSq = dr * dr + dg * dg + db * db;

            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                bestIndex = i;
            }
        }

        return bestIndex;
    }
}
