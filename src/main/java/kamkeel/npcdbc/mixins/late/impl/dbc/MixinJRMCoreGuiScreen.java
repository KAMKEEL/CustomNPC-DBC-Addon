package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.JRMCoreClient;
import JinRyuu.JRMCore.JRMCoreGuiButtons00;
import JinRyuu.JRMCore.JRMCoreGuiScreen;
import JinRyuu.JRMCore.JRMCoreH;
import cpw.mods.fml.common.FMLCommonHandler;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.gui.dbc.StatSheetGui;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.mixins.late.IDBCGuiScreen;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;
import java.util.List;

@Mixin(value = JRMCoreGuiScreen.class, remap = false)

public class MixinJRMCoreGuiScreen extends GuiScreen implements IDBCGuiScreen {

    @Shadow
    protected static List<Object[]> detailList;

    @Shadow
    public static String wish;
    @Shadow
    public static String button1;


    @Shadow
    public int guiID;
    @Unique
    private int newGuiID;
    @Unique
    private boolean ignoreInit = false;

    @Inject(method = "updateScreen", at=@At("HEAD"), remap = true)
    private void onUpdateScreen(CallbackInfo ci){
        if(this.guiID == 10 && ConfigDBCClient.EnhancedGui && DBCData.getClient().Powertype == 1)
            FMLCommonHandler.instance().showGuiScreen(new StatSheetGui());
    }

    @Inject(method = "drawDetails", at = @At("HEAD"), remap = false, cancellable = true)
    private static void onDrawDetails(String s1, String s2, int xpos, int ypos, int x, int y, FontRenderer var8, CallbackInfo ci) {

        if (PlayerDataUtil.getClientDBCInfo() == null) {
            return;
        }

        boolean isDrawingAttributes = (s1.contains("STR:") || s1.contains("DEX:") || s1.contains("WIL:")) && s1.contains("§");
        boolean isDrawingStats = s1.contains(JRMCoreH.trl("jrmc", "mleDB") + ":") || s1.contains(JRMCoreH.trl("jrmc", "DefDB") + ":") || s1.contains(JRMCoreH.trl("jrmc", "Passive") + ":") || s1.contains(JRMCoreH.trl("jrmc", "EnPwDB") + ":") && s1.contains("§");
        if (PlayerDataUtil.getClientDBCInfo().isInCustomForm()) {
            Form form = PlayerDataUtil.getClientDBCInfo().getCurrentForm();
            PlayerDBCInfo formData = PlayerDataUtil.getClientDBCInfo();
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
            } else if (isDrawingAttributes) {
                String currentColor = formData.getFormColorCode(formData.getCurrentForm());
                currentColor = Utility.removeBoldColorCode(currentColor);

                String[] data = getAdjustedAttributeData(s1, s2, currentColor);
                s1 = data[0];
                s2 = data[1];

                // adds the "xMulti" after CON: AttributeValue
            } else if (s1.contains("CON:")) {
                float multi = (float) DBCData.getClient().stats.getCurrentMulti();
                if (s1.contains("x"))
                    s1 = s1.substring(0, s1.indexOf("x") - 1);
                s1 = s1 + (JRMCoreH.round(multi, 1) != 1.0 ? formData.getFormColorCode(formData.getCurrentForm()) + " x" + JRMCoreH.round(multi, 1) : "");

                //Corrects Statistics colors
            } else if (isDrawingStats) {
                s1 = replaceFormColor(s1, formData.getFormColorCode(formData.getCurrentForm()));
            }

        } else if (DBCData.getClient().isForm(DBCForm.Legendary) && isInBaseForm(DBCData.getClient())) {
            if (!DBCData.getClient().containsSE(19)) { //If in UI, do not change colors
                return;
            }
            String legendColor = "§a";

            if (isDrawingAttributes) {
                String[] data = getAdjustedAttributeData(s1, s2, legendColor);
                s1 = data[0];
                s2 = data[1];
            } else if (isDrawingStats) {
                s1 = replaceFormColor(s1, legendColor);
            }

        }

        ci.cancel();
        int wpos = var8.getStringWidth(s1);
        var8.drawString(s1, xpos, ypos, 0);
        if (xpos < x && xpos + wpos > x && ypos - 3 < y && ypos + 10 > y) {
            int ll = 200;
            Object[] txt = new Object[]{s2, "§8", 0, true, x + 5, y + 5, ll};
            detailList.add(txt);
        }
    }

    private static boolean isInBaseForm(DBCData client) {
        if (client.Race == 4) {
            return client.State == 4;
        } else {
            return client.State == 0;
        }
    }

    /**
     * Adjusts the rendered text to apply correct form colors.
     *
     * @param s1               Main string
     * @param s2               Tooltip
     * @param replacementColor Color to replace the wrong stat color with.
     * @return String array of size 2, corresponding to modified s1 and s2 (need those to correct the rendering);
     */
    private static String[] getAdjustedAttributeData(String s1, String s2, String replacementColor) {
        s1 = replaceFormColor(s1, replacementColor);

        if (s2.contains(JRMCoreH.trl("jrmc", "Modified"))) {
            s2 = replaceFormColor(s2, replacementColor);
        } else {
            int attributeId = getAttributeIdByName(s1);
            int modified = DBCData.getClient().stats.getFullAttribute(attributeId);
            int original = JRMCoreH.PlyrAttrbts()[attributeId];

            String tooltipData = JRMCoreH.cldgy + JRMCoreH.trl("jrmc", "Modified") + ": " + replacementColor + modified
                + "\n" + JRMCoreH.cldgy + JRMCoreH.trl("jrmc", "Original") + ": " + JRMCoreH.cldr + original
                + "\n" + JRMCoreH.cldgy;

            s2 = tooltipData + s2;
        }

        return new String[]{s1, s2};
    }

    private static int getAttributeIdByName(String s1) {
        if (s1.contains("STR:"))
            return 0;
        if (s1.contains("DEX:"))
            return 1;
        if (s1.contains("WIL"))
            return 3;
        return 0;
    }

    private static String replaceFormColor(String s1, String replacementColor) {
        int secondIndex = 0;
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) == '§' && !s1.substring(i, i + 2).equals("§8")) {
                secondIndex = i;
                break;
            }
        }

        String originalColor = s1.substring(secondIndex, secondIndex + 2);
        s1 = s1.replace(originalColor, replacementColor);
        return s1;
    }


    @Inject(method = "initGui", at=@At("RETURN"), remap = true)
    private void onInitGui(CallbackInfo ci){
        if(ignoreInit)
            this.guiID = newGuiID;

        if(ConfigDBCClient.EnhancedGui){
            if(ConfigDBCClient.DarkMode){
                wish = CustomNpcPlusDBC.ID + ":textures/gui/gui_dark.png";
                button1 = CustomNpcPlusDBC.ID + ":textures/gui/button_dark.png";
            }
            else {
                wish = CustomNpcPlusDBC.ID + ":textures/gui/gui_light.png";
                button1 = CustomNpcPlusDBC.ID + ":textures/gui/button_light.png";
            }
        }
    }


    @Inject(method = "drawScreen", at=@At(value = "INVOKE", target = "Ljava/util/List;clear()V", shift = At.Shift.AFTER), remap = true)
    private void onDrawScreen(CallbackInfo ci){
        if(this.guiID != 10)
            return;
        String s = (!ConfigDBCClient.EnhancedGui ? "Old" : "§aModern") +" GUI";
        int i = this.fontRendererObj.getStringWidth(s)+10;
        this.buttonList.add(new JRMCoreGuiButtons00(303030303, (this.width -i)/2 + 154, (this.height-159)/2 + 65, i + 8, 20, s, 0));
    }

    @Inject(method="actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V", at=@At("HEAD"), remap = true)
    public void onActionPerformed(GuiButton button, CallbackInfo ci){
        if(button.id == 303030303){
            ConfigDBCClient.EnhancedGui = true;
            ConfigDBCClient.EnhancedGuiProperty.set(true);
        }
    }

    @Unique
    public void setGuiIDPostInit(int id) {
        this.newGuiID = id;
        this.ignoreInit = true;
    }
}
