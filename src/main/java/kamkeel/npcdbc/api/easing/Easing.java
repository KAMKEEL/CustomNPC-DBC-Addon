package kamkeel.npcdbc.api.easing;

/**
 * Functional interface for easing (timing) functions used in animations and transitions.
 * <p>
 * An {@code Easing} transforms a normalized time value (progress from 0.0 to 1.0)
 * into an eased output value, allowing control over acceleration, deceleration,
 * bouncing, elasticity, etc.
 * <p>
 * This interface matches common easing patterns found in CSS transitions/animations,
 * Unity, Unreal Engine, and other animation systems.
 */
public interface Easing {

    /**
     * Clamps a time value to the range [0.0, 1.0].
     * <p>
     * Values below 0 are clamped to 0, values above 1 are clamped to 1.
     *
     * @param t the input time/progress value
     * @return the clamped value in [0.0, 1.0]
     */
    static double clamp(double t) {
        if (t < 0) return 0;
        if (t > 1) return 1;
        return t;
    }

    /**
     * Computes the eased value for a normalized time.
     *
     * @param timeNormalized a value in the range [0.0, 1.0] representing animation progress
     *                       (0 = start, 1 = end)
     * @return the eased output value
     */
    double ease(double timeNormalized);

    /**
     * Convenience method to ease using absolute time and duration.
     *
     * @param currentValue eg. current elapsed time
     * @param maxValue eg. total duration of the animation
     * @return eased value
     */
    default double ease(double currentValue, double maxValue) {
        double t = currentValue / maxValue;
        return ease(Easing.clamp(t));
    }

    /**
     * Convenience method using start time, current time, and duration.
     *
     * @param startTime      time when animation began
     * @param totalDuration  total duration of the animation
     * @param currentTime    current time
     * @return eased value
     */
    default double ease(double startTime, double totalDuration, double currentTime) {
        return ease(currentTime - startTime, totalDuration);
    }

    /**
     * Long-variant for high-precision timing (e.g., milliseconds or nanoseconds).
     *
     * @param currentValue eg. current elapsed time
     * @param maxValue  eg. total duration
     * @return eased value
     */
    default double ease(long currentValue, long maxValue) {
        double t = (double) currentValue / maxValue;
        return ease(Easing.clamp(t));
    }

    /**
     * Long-variant using start/current time.
     *
     * @param startTime      animation start time
     * @param totalDuration  total duration
     * @param currentTime    current time
     * @return eased value
     */
    default double ease(long startTime, long totalDuration, long currentTime) {
        return ease(currentTime - startTime, totalDuration);
    }

    default Easing bake(int segments) {
        if (this instanceof Linear)
            return this;
        if (this instanceof LinearPiecewise) {
            if (segments >= ((LinearPiecewise) this).getSegmentCount())
                return this;
        }
        return ProgressFunctions.bakeEasing(this, segments);
    }
}
