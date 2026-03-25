package kamkeel.npcdbc.client.gui.dbc.skill;

/**
 * Single mutable source of truth for the addon-owned skill screen.
 * <p>
 * Owns mode, scroll position, selection/hover, confirmation state,
 * and derived display values. No pane class should invent its own
 * scroll or selection source of truth.
 * <p>
 * Mirrors the vanilla state scattered across {@code JRMCoreGuiScreen}
 * fields: {@code guiID}, {@code pgSkls}, {@code ipg}, {@code lp},
 * {@code scroll}, {@code scrollMouseJump}, {@code confirmationWindow},
 * {@code IDtoProcessConfirmFor}.
 */
public final class SkillScreenState {

    // ════════════════════════════════════════════════════════════════
    // Mode
    // ════════════════════════════════════════════════════════════════

    /** Current subview of the skill screen. */
    public enum Mode {
        /** Screen 11 equivalent: owned skills overview. */
        OVERVIEW,
        /** Screen 16 equivalent: learn new skills. */
        LEARN_SKILLS
    }

    private Mode mode = Mode.OVERVIEW;

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        // Reset scroll/selection when switching modes
        this.scrollOffset = 0;
        this.selectedIndex = -1;
        this.learnPage = 0;
    }

    // ════════════════════════════════════════════════════════════════
    // Scroll (Overview)
    // ════════════════════════════════════════════════════════════════

    /** Scroll offset for overview skill list. Vanilla: {@code scroll} field. */
    private int scrollOffset = 0;

    /** Vanilla: {@code scrollMouseJump}. Number of items that fit visible. */
    private int visibleRowCount = 10;

    /** Whether the mouse is currently pressing the scrollbar. */
    private boolean scrollbarPressed = false;

    /** Normalized scrollbar position [0..1]. Vanilla: {@code scrollSide}. */
    private float scrollbarPosition = 0.0f;

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setScrollOffset(int offset) {
        this.scrollOffset = Math.max(0, offset);
    }

    public int getVisibleRowCount() {
        return visibleRowCount;
    }

    public void setVisibleRowCount(int count) {
        this.visibleRowCount = count;
    }

    public boolean isScrollbarPressed() {
        return scrollbarPressed;
    }

    public void setScrollbarPressed(boolean pressed) {
        this.scrollbarPressed = pressed;
    }

    public float getScrollbarPosition() {
        return scrollbarPosition;
    }

    public void setScrollbarPosition(float pos) {
        this.scrollbarPosition = Math.max(0.0f, Math.min(1.0f, pos));
    }

    /**
     * Clamps scroll offset against total row count.
     *
     * @param totalRows total number of skill rows in the current list
     */
    public void clampScroll(int totalRows) {
        int maxScroll = Math.max(0, totalRows - visibleRowCount);
        if (scrollOffset > maxScroll) {
            scrollOffset = maxScroll;
        }
        if (scrollOffset < 0) {
            scrollOffset = 0;
        }
        if (totalRows > visibleRowCount && maxScroll > 0) {
            scrollbarPosition = (float) scrollOffset / maxScroll;
        } else {
            scrollbarPosition = 0.0f;
        }
    }

    /**
     * Updates scroll offset from a normalized scrollbar drag value.
     *
     * @param totalRows total number of skill rows
     * @param normalized scrollbar position [0..1]
     */
    public void scrollFromBar(int totalRows, float normalized) {
        int maxScroll = Math.max(0, totalRows - visibleRowCount);
        scrollOffset = Math.round(normalized * maxScroll);
        scrollbarPosition = normalized;
    }

    // ════════════════════════════════════════════════════════════════
    // Learn-skills paging (Screen 16)
    // ════════════════════════════════════════════════════════════════

    /** Page index for learn-skills view. Vanilla: {@code ipg}. */
    private int learnPage = 0;

    /** Items per page in learn-skills. Vanilla constant = 13. */
    public static final int LEARN_PAGE_SIZE = 13;

    public int getLearnPage() {
        return learnPage;
    }

    public void setLearnPage(int page) {
        this.learnPage = Math.max(0, page);
    }

    /**
     * Clamp learn page against total learnable skill count.
     *
     * @param totalLearnable total number of learnable skills
     */
    public void clampLearnPage(int totalLearnable) {
        int maxPage = Math.max(0, (totalLearnable - 1) / LEARN_PAGE_SIZE);
        if (learnPage > maxPage) {
            learnPage = maxPage;
        }
    }

    /** Start index for current learn page. */
    public int learnPageStart() {
        return learnPage * LEARN_PAGE_SIZE;
    }

    /** End index (exclusive) for current learn page, capped to totalLearnable. */
    public int learnPageEnd(int totalLearnable) {
        return Math.min(learnPageStart() + LEARN_PAGE_SIZE, totalLearnable);
    }

    // ════════════════════════════════════════════════════════════════
    // Selection / Hover
    // ════════════════════════════════════════════════════════════════

    /** Currently selected row index, or -1 if none. */
    private int selectedIndex = -1;

    /** Currently hovered row index, or -1 if none. */
    private int hoveredIndex = -1;

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int index) {
        this.selectedIndex = index;
    }

    public int getHoveredIndex() {
        return hoveredIndex;
    }

    public void setHoveredIndex(int index) {
        this.hoveredIndex = index;
    }

    // ════════════════════════════════════════════════════════════════
    // Confirmation modal
    // ════════════════════════════════════════════════════════════════

    /** Whether the delete/unlearn confirmation modal is showing. */
    private boolean confirmationActive = false;

    /**
     * The row model being confirmed for deletion/unlearn.
     * Null when no confirmation is active.
     */
    private SkillRowModel confirmationTarget = null;

    public boolean isConfirmationActive() {
        return confirmationActive;
    }

    public SkillRowModel getConfirmationTarget() {
        return confirmationTarget;
    }

    /** Show the confirmation modal for a specific skill row. */
    public void requestConfirmation(SkillRowModel target) {
        this.confirmationActive = true;
        this.confirmationTarget = target;
    }

    /** Dismiss the confirmation modal without acting. */
    public void cancelConfirmation() {
        this.confirmationActive = false;
        this.confirmationTarget = null;
    }

    /** Clear confirmation state after an action has been performed. */
    public void completeConfirmation() {
        this.confirmationActive = false;
        this.confirmationTarget = null;
    }

    // ════════════════════════════════════════════════════════════════
    // Full reset
    // ════════════════════════════════════════════════════════════════

    /** Reset all state to defaults (e.g. when reopening the screen). */
    public void reset() {
        mode = Mode.OVERVIEW;
        scrollOffset = 0;
        visibleRowCount = 10;
        scrollbarPressed = false;
        scrollbarPosition = 0.0f;
        learnPage = 0;
        selectedIndex = -1;
        hoveredIndex = -1;
        confirmationActive = false;
        confirmationTarget = null;
    }
}
