package kamkeel.npcdbc.api.easing;

final class CubicBezier implements Easing {

    private static final int NEWTON_RAPHSON_ITERATIONS = 8;
    private static final int BISECTION_LIMIT = 30;

    private static final double MAX_DELTA_TO_EVALUATE = 1e-10;
    private static final double EPSILON = 1e-12; // Tolerance for "essentially zero" due to floating-point precision

    private final double x1, y1, x2, y2;
    private final double startY, endY;

    // Polynomial coeffs for X(t) and Y(t)
    private double cx, bx, ax;
    private double cy, by, ay;

    private static final int SAMPLE_SIZE = 11;
    private final double[] sampleValues = new double[SAMPLE_SIZE];

    // Standard constructor: startY=0, endY=1
    public CubicBezier(double x1, double y1, double x2, double y2) {
        this(0, x1, y1, x2, y2, 1);
    }

    // Extended: custom startY/endY (rare for easing, but supported)
    public CubicBezier(double startY, double x1, double y1, double x2, double y2, double endY) {
        this.startY = startY;
        this.endY = endY;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        computeCoefficients();
        precomputeSamples();
    }

    private void computeCoefficients() {
        // Standard cubic form: X(t) = ax*t^3 + bx*t^2 + cx*t + dx  (dx = 0 for standard easing)
        // But for generality with custom startY/endY on Y
        cx = 3 * x1;
        bx = 3 * (x2 - x1) - cx;
        ax = 1 - cx - bx;  // for X: ax*t^3 + bx*t^2 + cx*t

        cy = 3 * (y1 - startY);
        by = 3 * (y2 - y1) - cy;
        ay = endY - startY - cy - by;  // ay*t^3 + by*t^2 + cy*t + startY
    }

    private void precomputeSamples() {
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            double t = i / (SAMPLE_SIZE - 1.0);
            sampleValues[i] = evalX(t);
        }
    }

    private double evalX(double t) {
        return ((ax * t + bx) * t + cx) * t;
    }

    private double evalY(double t) {
        return ((ay * t + by) * t + cy) * t + startY;
    }

    private double evalXDerivative(double t) {
        return (3 * ax * t + 2 * bx) * t + cx;
    }

    private double getInitialGuess(double x) {
        int low = 0, high = SAMPLE_SIZE - 1;
        while (low < high) {
            int mid = (low + high + 1) >>> 1;
            if (sampleValues[mid] < x) {
                low = mid;
            } else {
                high = mid - 1;
            }
        }
        return low / (SAMPLE_SIZE - 1.0);
    }

    @Override
    public double ease(double time) {
        if (time <= 0.0) return startY;
        if (time >= 1.0) return endY;

        // Fast linear path if the X curve is (almost) straight
        if (Math.abs(ax) < EPSILON && Math.abs(bx) < EPSILON) {
            return startY + time * (endY - startY);
        }

        double t = getInitialGuess(time);

        // Newton-Raphson
        for (int i = 0; i < NEWTON_RAPHSON_ITERATIONS; i++) {
            double xDiff = evalX(t) - time;
            double dx = evalXDerivative(t);

            if (Math.abs(dx) < EPSILON) {
                // Derivative too flat — fall back to bisection early
                break;
            }

            double delta = xDiff / dx;
            t -= delta;

            if (Math.abs(delta) < MAX_DELTA_TO_EVALUATE) {
                return evalY(t);
            }

            // Clamp to [0,1] to prevent runaway
            if (t < 0.0 || t > 1.0) {
                break;
            }
        }

        // bisection fallback
        double low = 0.0, high = 1.0;
        for (int i = 0; i < BISECTION_LIMIT; i++) {
            double mid = (low + high) * 0.5;
            double val = evalX(mid);
            if (val < time) {
                low = mid;
            } else {
                high = mid;
            }
            if (high - low < EPSILON) break;
        }
        t = (low + high) * 0.5;

        return evalY(t);
    }
}
