package kamkeel.npcdbc.api.easing;

public final class LinearPiecewise implements Easing {

    private final double[] xStops;
    private double[] yValues;
    private final int segments;

    /**
     * @param stops paired values: x0, y0, x1, y1, ..., xn, yn
     */
    LinearPiecewise(double... stops) {
        int temp_Segments;
        double[] temp_xStops;
        if (stops.length % 2 != 0) {
            throw new IllegalArgumentException("Stops must be paired: x, y");
        }

        temp_Segments = stops.length / 2;
        temp_xStops = new double[temp_Segments];
        yValues = new double[temp_Segments];

        // Validate and copy
        double prevX = -1;
        for (int i = 0; i < temp_Segments; i++) {
            double x = stops[i * 2];
            double y = stops[i * 2 + 1];

            if (x < 0 || x > 1) {
                throw new IllegalArgumentException("x values must be in [0,1]: " + x);
            }
            if (i > 0 && x <= prevX) {
                throw new IllegalArgumentException("x values must be strictly increasing: " + x + " <= " + prevX);
            }

            temp_xStops[i] = x;
            yValues[i] = y;
            prevX = x;
        }

        // Auto-add start if missing
        if (temp_xStops[0] > 0) {
            // Prepend (0, y0)
            double[] newX = new double[temp_Segments + 1];
            double[] newY = new double[temp_Segments + 1];
            newX[0] = 0;
            newY[0] = yValues[0];
            System.arraycopy(temp_xStops, 0, newX, 1, temp_Segments);
            System.arraycopy(yValues, 0, newY, 1, temp_Segments);
            temp_xStops = newX;
            yValues = newY;
            temp_Segments++;
        }

        // Auto-add end if missing
        if (temp_xStops[temp_Segments - 1] < 1.0) {
            double[] newX = new double[temp_Segments + 1];
            double[] newY = new double[temp_Segments + 1];
            System.arraycopy(temp_xStops, 0, newX, 0, temp_Segments);
            System.arraycopy(yValues, 0, newY, 0, temp_Segments);
            newX[temp_Segments] = 1.0;
            newY[temp_Segments] = yValues[temp_Segments - 1];
            temp_xStops = newX;
            yValues = newY;
            temp_Segments++;
        }
        segments = temp_Segments;
        xStops = temp_xStops;
    }

    @Override
    public double ease(double time) {
        if (time <= 0) return yValues[0];
        if (time >= 1) return yValues[segments - 1];

        int low = 0;
        int high = segments - 1;

        while (low < high) {
            int mid = (low + high) >>> 1;
            if (xStops[mid] < time) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        int index = low - 1; // segment is between low-1 and low
        if (index < 0) index = 0;

        double x1 = xStops[index];
        double x2 = xStops[index + 1];
        double y1 = yValues[index];
        double y2 = yValues[index + 1];

        if (x2 == x1) return y1; // avoid div0 (degenerate)

        double fraction = (time - x1) / (x2 - x1);
        return y1 + fraction * (y2 - y1);
    }

    public double[] getXStops() {
        return xStops.clone();
    }

    public double[] getYValues() {
        return yValues.clone();
    }

    public int getSegmentCount() {
        return segments - 1;
    }
}
