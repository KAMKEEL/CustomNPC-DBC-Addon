package kamkeel.npcdbc.client.gui.dbc.action;

import JinRyuu.JRMCore.JRMCoreA;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreHDBC;
import JinRyuu.JRMCore.JRMCoreHNC;
import JinRyuu.JRMCore.client.config.jrmc.JGConfigClientSettings;

/**
 * Label generation and page-toggle gateway for the addon action menu.
 * <p>
 * Action <em>execution</em> on release is handled by vanilla's
 * {@code JRMCoreCliTicH.onRenderTick} via the synced static fields;
 * this gateway is only responsible for rendering labels and page toggles.
 * <p>
 * Vanilla label routing (mirrors {@code JRMCoreGui.renderActionMenu} lines 133-141, 158-166):
 * <pre>
 * Pwrtyp 1 (DBC) → JRMCoreHDBC.action(id, false, black)
 * Pwrtyp 2 (NC)  → JRMCoreHNC.action(id, false, black)
 * </pre>
 */
public final class ActionMenuActionGateway {

    public ActionMenuActionGateway() {
    }

    // ════════════════════════════════════════════════════════════════
    // Label generation (for rendering)
    // ════════════════════════════════════════════════════════════════

    public String getSlotLabel(int absoluteSlotId) {
        Object actionObj = JRMCoreA.actions.get(absoluteSlotId);
        if (actionObj == null) {
            return "";
        }
        int actionId = (Integer) actionObj;

        if (JRMCoreH.Pwrtyp == 1) {
            return JRMCoreHDBC.action(actionId, false, false);
        }
        if (JRMCoreH.Pwrtyp == 2) {
            return JRMCoreHNC.action(actionId, false, false);
        }
        return "";
    }

    public String getSlotBorderLabel(int absoluteSlotId) {
        Object actionObj = JRMCoreA.actions.get(absoluteSlotId);
        if (actionObj == null) {
            return "";
        }
        int actionId = (Integer) actionObj;

        if (JRMCoreH.Pwrtyp == 1) {
            return JRMCoreHDBC.action(actionId, false, true);
        }
        if (JRMCoreH.Pwrtyp == 2) {
            return JRMCoreHNC.action(actionId, false, true);
        }
        return "";
    }

    // ════════════════════════════════════════════════════════════════
    // Query helpers
    // ════════════════════════════════════════════════════════════════

    public boolean hasAction(int absoluteSlotId) {
        return JRMCoreA.actions.get(absoluteSlotId) != null;
    }

    public String getHoveredTextColor() {
        return JGConfigClientSettings.CLIENT_GR13 ? JRMCoreH.clgy : JRMCoreH.clgd;
    }
}
