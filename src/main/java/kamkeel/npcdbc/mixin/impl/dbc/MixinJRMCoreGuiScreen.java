package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.JRMCoreClient;
import JinRyuu.JRMCore.JRMCoreGuiScreen;
import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.util.DBCUtils;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;
import java.util.List;

@Mixin(value = JRMCoreGuiScreen.class, remap = false)

public class MixinJRMCoreGuiScreen extends GuiScreen {

    @Shadow
    protected static List<Object[]> detailList;


    @Inject(method = "drawDetails", at = @At("HEAD"), remap = false, cancellable = true)
    private static void onDrawDetails(String s1, String s2, int xpos, int ypos, int x, int y, FontRenderer var8, CallbackInfo ci) {
        if (Utility.getSelfData() != null && Utility.getSelfData().isInCustomForm()) {
            Form form = Utility.getSelfData().getCurrentForm();
            PlayerDBCInfo formData = Utility.getSelfData();
            if (s1.contains(JRMCoreH.trl("jrmc", "TRState") + ":")) {
                final String TRState2 = JRMCoreH.trl("jrmc", "TRState"); // "Form : SS4"
                if (formData != null && formData.isInCustomForm()) {

                    //Form : menuName
                    String name = formData.getCurrentForm().getMenuName();
                    s1 = TRState2 + ": " + name;

                    //Form Mastery
                    DecimalFormat formatter = new DecimalFormat("#.##");
                    float curLevel = formData.getFormLevel(formData.currentForm);

                    boolean removeBase = s2.contains(JRMCoreH.trl("jrmc", "Base"));
                    boolean isInKaioken = JRMCoreH.StusEfctsMe(5);
                    int kaiokenID = JRMCoreH.getFormID("Kaioken", JRMCoreH.Race);
                    double kaiokenLevel = JRMCoreH.getFormMasteryValue(JRMCoreClient.mc.thePlayer, kaiokenID);
                    String kaiokenString = "\n" + JRMCoreH.cldgy + "§cKaioken §8Mastery Lvl: " + JRMCoreH.cldr + formatter.format(kaiokenLevel);

                    s2 = Utility.removeBoldColorCode(name) + " §8Mastery Lvl: §4" + formatter.format(curLevel) + (removeBase ? (isInKaioken ? kaiokenString : "") : "\n§8" + s2);
                }
                //adds the form color to STR,DEX and WIL attribute values
            } else if ((s1.contains("STR:") || s1.contains("DEX:") || s1.contains("WIL:")) || s1.contains(JRMCoreH.trl("jrmc", "mleDB")+":") || s1.contains(JRMCoreH.trl("jrmc", "DefDB")+":") || s1.contains(JRMCoreH.trl("jrmc", "Passive")+":") || s1.contains(JRMCoreH.trl("jrmc", "EnPwDB")+":") && s1.contains("§")) {
                int secondIndex = 0;
                for (int i = 0; i < s1.length(); i++) {
                    if (s1.charAt(i) == '§' && !s1.substring(i, i + 2).equals("§8")) {
                        secondIndex = i;
                        break;
                    }
                }

                String originalColor = s1.substring(secondIndex, secondIndex + 2);
                s1 = s1.replace(originalColor, formData.getFormColorCode(formData.getCurrentForm()));
                // adds the "xMulti" after CON: AttributeValue
            } else if (s1.contains("CON:")) {
                float multi = (float) DBCUtils.getCurFormMulti(Minecraft.getMinecraft().thePlayer);
                if (s1.contains("x"))
                    s1 = s1.substring(0, s1.indexOf("x") - 1);
                s1 = s1 + (JRMCoreH.round(multi, 1) != 1.0 ? formData.getFormColorCode(formData.getCurrentForm()) + " x" + JRMCoreH.round(multi, 1) : "");
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
