package kamkeel.npcdbc.api.easing;

import java.util.Arrays;

/**
 * Collection of easings / timing functions.
 */
public final class ProgressFunctions {

    public static final class Premade {
        public static final Easing LINEAR = t -> t;
        public static final Easing IN_SINE = t -> (1 - Math.cos(t*Math.PI * 0.5));
        public static final Easing OUT_SINE = t -> Math.sin(t*Math.PI * 0.5);
        public static final Easing IN_OUT_SINE = t -> (-(Math.cos(Math.PI * t) - 1) * 0.5);
        public static final Easing IN_CUBIC = t -> t*t*t;
        public static final Easing OUT_CUBIC = t -> (1 - Math.pow(1-t, 3));
    }

    /**
     * Creates a standard cubic Bézier easing function with start point (0,0) and end point (1,1).
     * <p>
     * This matches the common CSS {@code cubic-bezier(x0, y0, x1, y1)} syntax used in
     * {@code transition-timing-function} and {@code animation-timing-function}.
     * <p>
     * Common examples:
     * <ul>
     *   <li>ease:       {@code cubicBezier(0.25, 0.1, 0.25, 1.0)}</li>
     *   <li>ease-in:    {@code cubicBezier(0.42, 0.0, 1.0, 1.0)}</li>
     *   <li>ease-out:   {@code cubicBezier(0.0, 0.0, 0.58, 1.0)}</li>
     *   <li>ease-in-out:{@code cubicBezier(0.42, 0.0, 0.58, 1.0)}</li>
     * </ul>
     *
     * @param x0 x-coordinate of the first control point (typically in [0,1])
     * @param y0 y-coordinate of the first control point
     * @param x1 x-coordinate of the second control point (typically in [0,1])
     * @param y1 y-coordinate of the second control point
     * @return a new {@linkplain CubicBezier} easing instance
     */
    public static Easing cubicBezier(double x0, double y0, double x1, double y1) {
        return new CubicBezier(x0, y0, x1, y1);
    }

    /**
     * Creates a cubic Bézier easing function with custom start and end Y values.
     * <p>
     * This extended version allows easings that do not begin at 0 or end at 1,
     * useful for animating properties with non-zero ranges or for step-like effects.
     * <p>
     * The curve still progresses from time 0 → 1, but output values range from
     * {@code startY} at t=0 to {@code endY} at t=1.
     *
     * @param startY y-value at time 0
     * @param x0     x-coordinate of the first control point
     * @param y0     y-coordinate of the first control point
     * @param x1     x-coordinate of the second control point
     * @param y1     y-coordinate of the second control point
     * @param endY   y-value at time 1
     * @return a new {@linkplain CubicBezier} easing instance with custom Y bounds
     */
    public static Easing cubicBezier(double startY, double x0, double y0, double x1, double y1, double endY) {
        return new CubicBezier(startY, x0, y0, x1, y1, endY);
    }

    /**
     * Creates a linear easing function, supporting multiple styles similar to CSS timing functions.
     * <p>
     * This method provides a unified entry point for creating fast, linear-based easings:
     * <ul>
     *   <li>No arguments or empty array: Standard linear from 0 to 1</li>
     *   <li>Single value: Constant output (always returns that value)</li>
     *   <li>Two values: Simple linear interpolation from startY to endY</li>
     *   <li>Multiple paired values (x0, y0, x1, y1, ...): Piecewise linear with explicit stops (like CSS {@code linear()} function)</li>
     * </ul>
     * <p>
     * When providing paired stops (4 or more values), the input must follow this format:
     * <pre>
     * x0, y0, x1, y1, x2, y2, ..., xn, yn
     * </pre>
     * where:
     * <ul>
     *   <li>Each {@code x} is a time position in the range [0.0, 1.0]</li>
     *   <li>{@code x} values must be strictly increasing</li>
     *   <li>The first {@code x} should ideally be 0.0 and the last 1.0 (automatically added if missing, using nearest y)</li>
     *   <li>{@code y} is the output value at that time position</li>
     * </ul>
     * <p>
     * This matches the behavior of the modern CSS {@code linear()} easing function, allowing precise control
     * over timing without the computational cost of cubic Bézier solving.
     *
     * @param values the input values defining the easing behavior
     *               <ul>
     *                 <li>0 values: linear from 0 → 1</li>
     *                 <li>1 value: constant easing returning that value</li>
     *                 <li>2 values: linear from values[0] → values[1]</li>
     *                 <li>4+ values (even count): explicit stops as x/y pairs</li>
     *               </ul>
     * @return an {@linkplain Easing} instance: either {@linkplain Linear}, {@linkplain ConstantEasing}, or {@linkplain LinearPiecewise}
     * @throws IllegalArgumentException if paired stops are provided with an odd number of values
     *                                  or if x positions are invalid (out of [0,1] or not increasing)
     *
     * @example Simple linear (default)
     * Easing easing = TimeFunctions.linear(); // 0 → 1 over time
     *
     * @example Constant value
     * Easing constant = TimeFunctions.linear(42.0); // always returns 42.0
     *
     * @example Custom linear range
     * Easing ramp = TimeFunctions.linear(10.0, 100.0); // from 10 → 100
     *
     * @example CSS-style piecewise linear
     * Easing custom = TimeFunctions.linear(
     *     0.0, 0.0,
     *     0.25, 0.8,
     *     0.75, 0.2,
     *     1.0, 1.0
     * );
     * // Fast acceleration, then deceleration — all with linear segments
     */
    public static Easing linear(double... values) {
        if (values == null || values.length == 0) {
            return new Linear(0, 1);
        }
        if (values.length == 1) {
            return new ConstantEasing(values[0]);
        }
        if (values.length == 2) {
            return new Linear(values[0], values[1]);
        }

        if (values.length % 2 != 0) {
            throw new IllegalArgumentException("Linear stops must come in pairs: x, y");
        }

        return new LinearPiecewise(values);
    }

    /**
     * Bakes any easing function into a high-performance {@linkplain LinearPiecewise} approximation
     * using a uniform number of segments.
     * <p>
     * This is useful for converting computationally expensive easings (e.g., {@linkplain CubicBezier})
     * into extremely fast piecewise linear versions that are visually nearly identical.
     * Ideal for particle systems, UI elements with thousands of instances, or any scenario
     * where the easing is evaluated many times per frame.
     * <p>
     * The resulting {@linkplain LinearPiecewise} has no iteration or root-finding overhead —
     * just a binary search and linear interpolation.
     * <p>
     * If the input easing is already a {@linkplain ConstantEasing}, it is returned directly (no baking needed).
     *
     * @param easing   the source easing function to bake
     * @param segments the number of evenly spaced samples along the curve (minimum 1).
     *                 Higher values = more accurate approximation.
     *                 Typical values: 8–32 (visually indistinguishable from original for most curves).
     * @return a {@linkplain LinearPiecewise} that closely matches the input easing
     */
    public static Easing bakeEasing(Easing easing, int segments) {
        if (easing instanceof ConstantEasing) {
            return easing;
        }
        if (segments < 1) {
            segments = 1;
        }

        double[] xPos = new double[segments + 1];
        for (int i = 0; i <= segments; i++) {
            xPos[i] = i / (double) segments;
        }
        return bakeEasing(easing, xPos);
    }

    /**
     * Bakes any easing function into a high-performance {@linkplain LinearPiecewise} approximation
     * using explicitly specified x-positions (stops).
     * <p>
     * This overload allows full control over where the curve is sampled.
     * Useful for adaptive sampling (more points where the curve changes rapidly)
     * or when matching exact CSS {@code linear()} stops.
     * <p>
     * The provided {@code xPositions} are automatically:
     * <ul>
     *   <li>Sorted</li>
     *   <li>Clamped to [0.0, 1.0]</li>
     *   <li>Used to evaluate the original easing at those exact times</li>
     * </ul>
     * <p>
     * If the input easing is already a {@linkplain ConstantEasing}, it is returned directly.
     *
     * @param easing      the source easing function to bake
     * @param xPositions  one or more time positions in [0.0, 1.0] where the curve should be sampled.
     *                    Duplicates are ignored (due to sorting), and values outside [0,1] are clamped.
     * @return a {@linkplain LinearPiecewise} with stops at the requested x-positions
     * @throws IllegalArgumentException if no xPositions are provided
     */
    public static Easing bakeEasing(Easing easing, double... xPositions) {
        if (easing instanceof ConstantEasing) {
            return easing;
        }
        if (xPositions == null || xPositions.length == 0) {
            throw new IllegalArgumentException("At least one xPosition must be provided");
        }

        Arrays.sort(xPositions);
        double[] stops = new double[xPositions.length * 2];
        for (int i = 0; i < xPositions.length; i++) {
            double x = Math.max(0, Math.min(1, xPositions[i]));
            stops[i * 2]     = x;
            stops[i * 2 + 1] = easing.ease(x);
        }
        return new LinearPiecewise(stops);
    }
}
