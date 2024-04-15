package kamkeel.npcdbc.mixin.impl;

import JinRyuu.JRMCore.JRMCoreGuiScreen;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.data.SyncedData.CustomFormData;
import kamkeel.npcdbc.data.SyncedData.DBCData;
import kamkeel.npcdbc.util.DBCUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = JRMCoreGuiScreen.class, remap = false)

public class MixinJRMCoreGuiScreen extends GuiScreen {
    @Shadow
    protected static List<Object[]> detailList;

    /**
     * For displaying Custom Form name in DBC GUI
     *
     * @author
     * @reason
     */
    @Overwrite
    private static void drawDetails(String s1, String s2, int xpos, int ypos, int x, int y, FontRenderer var8) {
        if (s1.contains(JRMCoreH.trl("jrmc", "TRState") + ":")) {
            final String TRState2 = JRMCoreH.trl("jrmc", "TRState"); // "Form : SS4"
            s1 = TRState2 + ":" + getFormName();
            String[] e = s1.split(":");
            String e0 = e[0] + ": ";
            int pos = var8.getStringWidth(e0);
            var8.drawString(e0 + "", xpos, ypos, 0);
            var8.drawString(e[1], xpos + pos, ypos, DBCUtils.getCurrentFormColor());
        } else {

            var8.drawString(s1, xpos, ypos, 0);
        }
        int wpos = var8.getStringWidth(s1);
        if (xpos < x && xpos + wpos > x && ypos - 3 < y && ypos + 10 > y) {
            int ll = 200;
            Object[] txt = new Object[]{s2, "§8", 0, true, x + 5, y + 5, Integer.valueOf(ll)};
            detailList.add(txt);
        }
    }

    @Unique
    private static String getFormName() {
        boolean ui = JRMCoreH.StusEfctsMe(19);
        String name = null;
        if (CustomFormData.getClient().isInCustomForm())
            name = CustomFormData.getClient().getCurrentForm().getMenuName();
        else {
            DBCData d = DBCData.getClient();
            name = DBCUtils.getCurrentFormFullName(d.Race, d.State, d.isForm(DBCForm.Legendary), d.isForm(DBCForm.Divine), JRMCoreH.StusEfctsMe(13), ui, DBCUtils.isUIWhite(ui, JRMCoreH.State2), JRMCoreH.StusEfctsMe(20));
        }

        return name;
    }
}
