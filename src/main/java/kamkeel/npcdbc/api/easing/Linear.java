package kamkeel.npcdbc.api.easing;

/**
 * Simple constant-speed linear from startY to endY (default 0 to 1)
 */
public final class Linear implements Easing {
    private final double startY;
    private final double deltaY; // endY - startY

    Linear() {
        this(0, 0);
    }

    Linear(double startY, double endY) {
        this.startY = startY;
        this.deltaY = endY - startY;
    }

    @Override
    public double ease(double time) {
        if (time <= 0) return startY;
        if (time >= 1) return startY + deltaY;
        return startY + time * deltaY;
    }
}
