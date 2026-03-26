package kamkeel.npcdbc.client.gui.dbc.action;

/**
 * Single mutable source of truth for the addon-owned DBC action menu.
 * <p>
 * Mirrors vanilla state scattered across {@code JRMCoreCliTicH} fields:
 * <ul>
 *   <li>{@code actionSelectID} - currently hovered slot ID (-1 = none)</li>
 *   <li>{@code actionNPA} - current page index (0 or 1)</li>
 *   <li>{@code actionNBO} - debounce flag for center MORE button</li>
 * </ul>
 *
 * <h3>Slot Map (vanilla {@code actionInitDBC})</h3>
 * <pre>
 * Page 0 (actionNPA=0):
 *   Slot 0 → Action  1 (Kaioken)        Slot 1 → Action  2 (Transform)     Slot 2 → Action  6 (Descend/Absorb)
 *   Slot 3 → Action  5 (Dodge/Swoop)     Slot 4 → MORE (page toggle)        Slot 5 → Action  7 (Flight)
 *   Slot 6 → Action  9 (Ki Charge)       Slot 7 → Action  4 (Tail Mode)     Slot 8 → Action 10 (Mystic)
 *
 * Page 1 (actionNPA=1):
 *   Slot  9 → Action 11 (Ki Fist)        Slot 10 → Action 12 (Ki Protection) Slot 11 → Action 13 (UI)
 *   Slot 12 → Action 14 (Friendly Fist)  Slot 13 → MORE (page toggle)        Slot 14 → Action 15 (Ki Weapon)
 *   Slot 15 → Action 16 (IT Short TP)    Slot 16 → Action 17 (IT Surround)   Slot 17 → Action 18 (GoD)
 * </pre>
 *
 * Note: Slot indices 4 and 13 (center cells, id % 9 == 4) are always
 * the MORE page-toggle and never map to an action ID.
 */
public final class ActionMenuState {

    // ════════════════════════════════════════════════════════════════
    // Page
    // ════════════════════════════════════════════════════════════════

    /** Current page index. Vanilla: {@code JRMCoreCliTicH.actionNPA}. 0 or 1. */
    private int pageIndex = 0;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int page) {
        this.pageIndex = page;
    }

    // ════════════════════════════════════════════════════════════════
    // Hovered slot / selection
    // ════════════════════════════════════════════════════════════════

    /**
     * Currently selected (hovered) slot ID, or -1 if no slot is hovered.
     * Vanilla: {@code JRMCoreCliTicH.actionSelectID}.
     * <p>
     * This is the <em>absolute</em> slot index (0–8 on page 0, 9–17 on page 1),
     * computed as {@code i + j*3 + pageIndex*9} per the vanilla grid loop.
     */
    private int actionSelectID = -1;

    public int getActionSelectID() {
        return actionSelectID;
    }

    public void setActionSelectID(int id) {
        this.actionSelectID = id;
    }

    /** Whether ANY non-empty slot is currently hovered. Vanilla: local {@code doAction}. */
    private boolean anySlotHovered = false;

    public boolean isAnySlotHovered() {
        return anySlotHovered;
    }

    public void setAnySlotHovered(boolean hovered) {
        this.anySlotHovered = hovered;
    }

    // ════════════════════════════════════════════════════════════════
    // Center-button debounce
    // ════════════════════════════════════════════════════════════════

    /**
     * Debounce flag for the center MORE button.
     * Vanilla: {@code JRMCoreCliTicH.actionNBO}.
     * <p>
     * Prevents repeated page toggles while attack key is held over the center cell.
     * Set to true when a page toggle fires; cleared when attack key is released.
     */
    private boolean centerDebounce = false;

    public boolean isCenterDebounce() {
        return centerDebounce;
    }

    public void setCenterDebounce(boolean debounce) {
        this.centerDebounce = debounce;
    }

    // ════════════════════════════════════════════════════════════════
    // Lifecycle helpers
    // ════════════════════════════════════════════════════════════════

    /**
     * Check if the given absolute slot id is a center (MORE) cell.
     * Vanilla: {@code id % 9 == 4}.
     */
    public static boolean isCenterSlot(int slotId) {
        return slotId >= 0 && slotId % 9 == 4;
    }
}
