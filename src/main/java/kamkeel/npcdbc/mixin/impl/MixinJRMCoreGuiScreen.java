package kamkeel.npcdbc.mixin.impl;

import JinRyuu.JRMCore.JRMCoreGuiScreen;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.data.PlayerCustomFormData;
import kamkeel.npcdbc.data.SyncedData.DBCData;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = JRMCoreGuiScreen.class, remap = false)

public class MixinJRMCoreGuiScreen extends GuiScreen {

    @Shadow
    protected static List<Object[]> detailList;


    @Unique
    private static String getFormName() {
        boolean ui = JRMCoreH.StusEfctsMe(19);
        String name = null;
        PlayerCustomFormData formData = Utility.getFormDataClient();
        if (formData.isInCustomForm())
            name = formData.getCurrentForm().getMenuName();
        else {
            DBCData d = DBCData.getClient();
            name = DBCUtils.getCurrentFormFullName(d.Race, d.State, d.isForm(DBCForm.Legendary), d.isForm(DBCForm.Divine), JRMCoreH.StusEfctsMe(13), ui, DBCUtils.isUIWhite(ui, JRMCoreH.State2), JRMCoreH.StusEfctsMe(20));
        }

        return name;
    }

    @Inject(method = "drawDetails", at = @At("HEAD"), remap = false, cancellable = true)
    private static void onDrawDetails(String s1, String s2, int xpos, int ypos, int x, int y, FontRenderer var8, CallbackInfo ci) {
        if (s1.contains(JRMCoreH.trl("jrmc", "TRState") + ":")) {
            final String TRState2 = JRMCoreH.trl("jrmc", "TRState"); // "Form : SS4"
            s1 = TRState2 + ":" + getFormName();
            String[] e = s1.split(":");
            String e0 = e[0] + ": ";
            int pos = var8.getStringWidth(e0);
            var8.drawString(e0, xpos, ypos, 0);
            var8.drawString(e[1], xpos + pos, ypos, DBCUtils.getCurrentFormColor());
            int wpos = var8.getStringWidth(s1);
            if (xpos < x && xpos + wpos > x && ypos - 3 < y && ypos + 10 > y) {
                int ll = 200;
                Object[] txt = new Object[]{s2, "§8", 0, true, x + 5, y + 5, ll};
                detailList.add(txt);
            }
            ci.cancel();
        } else if (Utility.getFormDataClient().isInCustomForm()) {
            PlayerCustomFormData c = Utility.getFormDataClient();
            if ((s1.contains("STR:") || s1.contains("DEX:") || s1.contains("WIL:")) && s1.contains("§")) { //adds the form color to STR,DEX and WIL attribute values
                int secondIndex = 0;
                for (int i = 0; i < s1.length(); i++) {
                    if (s1.charAt(i) == '§' && !s1.substring(i, i + 2).equals("§8")) {
                        secondIndex = i;
                        break;
                    }
                }

                String originalColor = s1.substring(secondIndex, secondIndex + 2);
                s1 = s1.replace(originalColor, c.getFormColorCode(c.getCurrentForm()));
            } else if (s1.contains("CON:")) { // adds the "xMulti" after CON: AttributeValue
                float multi = (float) DBCUtils.getCurFormMulti(Minecraft.getMinecraft().thePlayer);
                if (s1.contains("x"))
                    s1 = s1.substring(0, s1.indexOf("x") - 1);
                s1 = s1 + (JRMCoreH.round(multi, 1) != 1.0 ? c.getFormColorCode(c.getCurrentForm()) + " x" + JRMCoreH.round(multi, 1) : "");
            }
            int wpos = var8.getStringWidth(s1);
            var8.drawString(s1, xpos, ypos, 0);
            if (xpos < x && xpos + wpos > x && ypos - 3 < y && ypos + 10 > y) {
                int ll = 200;
                Object[] txt = new Object[]{s2, "§8", 0, true, x + 5, y + 5, ll};
                detailList.add(txt);
            }
            ci.cancel();


        }
    }
}
