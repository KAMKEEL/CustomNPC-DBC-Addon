package kamkeel.npcdbc.client.gui.dbc.skill;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.network.DBCPacketClient;
import kamkeel.npcdbc.network.packets.player.skill.CustomSkillPacket;

/**
 * Single action gateway for all skill-screen interactions.
 * <p>
 * Every button press / user action on the addon skill screen routes
 * through this gateway. No rendering or pane code is allowed to call
 * {@code JRMCoreH.Skll(...)}, {@code JRMCoreH.jrmct(...)}, or
 * {@link CustomSkillPacket} directly.
 * <p>
 * <h2>Action Routing Matrix</h2>
 * <pre>
 * ┌──────────────────────────────────────┬────────────────────────┬──────────────────────────────────┐
 * │ Action                               │ Row Type               │ Backend Route                    │
 * ├──────────────────────────────────────┼────────────────────────┼──────────────────────────────────┤
 * │ Upgrade vanilla skill                │ VANILLA_OWNED          │ JRMCoreH.Skll(3, index)          │
 * │                                      │                        │ + JRMCoreH.jrmct(3)              │
 * ├──────────────────────────────────────┼────────────────────────┼──────────────────────────────────┤
 * │ Unlearn vanilla skill (confirm)      │ VANILLA_OWNED          │ JRMCoreH.Skll(2, index)          │
 * │                                      │                        │ + JRMCoreH.jrmct(3)              │
 * ├──────────────────────────────────────┼────────────────────────┼──────────────────────────────────┤
 * │ Request delete confirmation          │ VANILLA_OWNED          │ State: confirmationActive = true  │
 * │                                      │ CUSTOM_OWNED           │                                  │
 * ├──────────────────────────────────────┼────────────────────────┼──────────────────────────────────┤
 * │ Cancel confirmation                  │ any                    │ State: confirmationActive = false │
 * ├──────────────────────────────────────┼────────────────────────┼──────────────────────────────────┤
 * │ Upgrade racial skill (X slot)        │ RACIAL                 │ JRMCoreH.Skll(3, 100)            │
 * │                                      │                        │ + JRMCoreH.jrmct(3)              │
 * │                                      │                        │ (custom-race: same packet,       │
 * │                                      │                        │  server mixin handles override)  │
 * ├──────────────────────────────────────┼────────────────────────┼──────────────────────────────────┤
 * │ Upgrade custom addon skill           │ CUSTOM_OWNED           │ CustomSkillPacket(id, UPGRADE)   │
 * │                                      │                        │ + JRMCoreH.jrmct(3)              │
 * ├──────────────────────────────────────┼────────────────────────┼──────────────────────────────────┤
 * │ Unlearn custom addon skill (confirm) │ CUSTOM_OWNED           │ CustomSkillPacket(id, UNLEARN)   │
 * │                                      │                        │ + JRMCoreH.jrmct(3)              │
 * ├──────────────────────────────────────┼────────────────────────┼──────────────────────────────────┤
 * │ Learn new skill (purchase)           │ LEARNABLE              │ JRMCoreH.Skll(1, index)          │
 * │                                      │                        │ → mode returns to OVERVIEW       │
 * │                                      │                        │ → learnPage reset to 0           │
 * │                                      │                        │ (NO jrmct — vanilla omits it)    │
 * ├──────────────────────────────────────┼────────────────────────┼──────────────────────────────────┤
 * │ Upgrade racial skill (Y slot)        │ RACIAL_Y               │ JRMCoreH.Skll(3, 101)            │
 * │                                      │                        │ + JRMCoreH.jrmct(3)              │
 * ├──────────────────────────────────────┼────────────────────────┼──────────────────────────────────┤
 * │ Navigate to learn skills             │ n/a (menu button)      │ State: mode = LEARN_SKILLS       │
 * ├──────────────────────────────────────┼────────────────────────┼──────────────────────────────────┤
 * │ Navigate back to overview            │ n/a (close/back)       │ State: mode = OVERVIEW           │
 * └──────────────────────────────────────┴────────────────────────┴──────────────────────────────────┘
 * </pre>
 *
 * <h3>jrmct(3) policy</h3>
 * <ul>
 *   <li>{@code jrmct(3)} = vanilla stat/skill sync refresh. Called after upgrade/unlearn of vanilla, racial, and custom addon skills.</li>
 *   <li>NOT called after learn/purchase — vanilla omits it (line 2329-2334 of JRMCoreGuiScreen).</li>
 *   <li>NOT called after custom addon skill packets — they have their own server-side handling,
 *       but jrmct(3) is still used to sync the updated PlayerPersisted (including customSkills) back to the client.</li>
 * </ul>
 */
public final class SkillActionGateway {

    private final SkillScreenState state;

    public SkillActionGateway(SkillScreenState state) {
        this.state = state;
    }

    // ════════════════════════════════════════════════════════════════
    // Upgrade actions
    // ════════════════════════════════════════════════════════════════

    /**
     * Upgrade the given skill row. Routes to the correct backend based on row type.
     */
    public void upgrade(SkillRowModel row) {
        if (row == null || !row.canUpgrade()) return;

        switch (row.getType()) {
            case VANILLA_OWNED:
                JRMCoreH.Skll((byte) 3, (byte) row.getVanillaIndex());
                JRMCoreH.jrmct(3);
                break;

            case RACIAL:
                JRMCoreH.Skll((byte) 3, (byte) row.getVanillaIndex());
                JRMCoreH.jrmct(3);
                break;

            case RACIAL_Y:
                JRMCoreH.Skll((byte) 3, (byte) row.getVanillaIndex());
                JRMCoreH.jrmct(3);
                break;

            case CUSTOM_OWNED:
                DBCPacketClient.sendClient(new CustomSkillPacket(
                    row.getSkillId(), CustomSkillPacket.Action.UPGRADE));
                JRMCoreH.jrmct(3);
                break;

            default:
                break;
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Unlearn / Delete actions
    // ════════════════════════════════════════════════════════════════

    /**
     * Request delete confirmation for the given row.
     * Shows the confirmation modal via state.
     */
    public void requestDelete(SkillRowModel row) {
        if (row == null) return;
        state.requestConfirmation(row);
    }

    /**
     * Confirm and execute the pending delete/unlearn.
     */
    public void confirmDelete() {
        SkillRowModel target = state.getConfirmationTarget();
        if (target == null) return;

        switch (target.getType()) {
            case VANILLA_OWNED:
                JRMCoreH.Skll((byte) 2, (byte) target.getVanillaIndex());
                JRMCoreH.jrmct(3);
                break;

            case CUSTOM_OWNED:
                DBCPacketClient.sendClient(new CustomSkillPacket(
                    target.getSkillId(), CustomSkillPacket.Action.UNLEARN));
                JRMCoreH.jrmct(3);
                break;

            default:
                // Racial skills are not deletable via the skill screen
                break;
        }

        state.completeConfirmation();
    }

    /**
     * Cancel the pending confirmation modal.
     */
    public void cancelDelete() {
        state.cancelConfirmation();
    }

    // ════════════════════════════════════════════════════════════════
    // Learn actions (screen 16)
    // ════════════════════════════════════════════════════════════════

    /**
     * Purchase/learn a skill from the learn-skills view.
     * Returns to overview mode after learning (vanilla: guiID = 11, ipg = 0).
     * Vanilla does NOT call jrmct(3) after purchase.
     */
    public void learnSkill(SkillRowModel row) {
        if (row == null || row.getType() != SkillRowModel.RowType.LEARNABLE) return;
        if (row.isOwned()) return; // already owned

        JRMCoreH.Skll((byte) 1, (byte) row.getVanillaIndex());
        // Match vanilla: return to overview with learn page reset
        state.setLearnPage(0);
        state.setMode(SkillScreenState.Mode.OVERVIEW);
    }

    // ════════════════════════════════════════════════════════════════
    // Navigation
    // ════════════════════════════════════════════════════════════════

    /** Switch to learn-skills mode. */
    public void navigateToLearnSkills() {
        state.setMode(SkillScreenState.Mode.LEARN_SKILLS);
    }

    /** Switch to overview mode. */
    public void navigateToOverview() {
        state.setMode(SkillScreenState.Mode.OVERVIEW);
    }
}
