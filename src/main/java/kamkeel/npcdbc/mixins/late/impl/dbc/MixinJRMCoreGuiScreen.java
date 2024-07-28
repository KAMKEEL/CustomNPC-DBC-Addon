package kamkeel.npcdbc.mixins.late.impl.dbc;

import JinRyuu.JRMCore.*;
import cpw.mods.fml.common.FMLCommonHandler;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.client.ColorMode;
import kamkeel.npcdbc.client.gui.dbc.StatSheetGui;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.mixins.late.IDBCGuiScreen;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scala.Tuple2;
import scala.Tuple4;

import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Function;

@Mixin(value = JRMCoreGuiScreen.class, remap = false)

public class MixinJRMCoreGuiScreen extends GuiScreen implements IDBCGuiScreen {

    private static final int GUI_CHANGE_BUTTON = 303030303;
    private static final int CLIENT_FIRST_PERSON_3D_OPACITY_ADD = GUI_CHANGE_BUTTON + 1;
    private static final int CLIENT_FIRST_PERSON_3D_OPACITY_REMOVE = GUI_CHANGE_BUTTON + 2;

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

    @Shadow
    private static int cs_mode;
    @Shadow
    private static int cs_page;

    @Shadow
    private int wid;

    @Shadow
    private int hei;

    @Shadow
    public static JRMCoreGuiScreen instance;

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
            DBCData dbcData = DBCData.getClient();
            Form form = dbcData.getForm();
            PlayerDBCInfo formData = PlayerDataUtil.getClientDBCInfo();
            if (s1.contains(JRMCoreH.trl("jrmc", "TRState") + ":")) {
                final String TRState2 = JRMCoreH.trl("jrmc", "TRState"); // "Form : SS4"
                if (form != null) {

                    //Form : menuName
                    String name = form.getMenuName();
                    s1 = TRState2 + ": " + name;

                    //Form Mastery
                    DecimalFormat formatter = new DecimalFormat("#.##");
                    float curLevel = dbcData.addonFormLevel;

                    boolean removeBase = s2.contains(JRMCoreH.trl("jrmc", "Base"));
                    boolean isInKaioken = JRMCoreH.StusEfctsMe(5);
                    int kaiokenID = JRMCoreH.getFormID("Kaioken", JRMCoreH.Race);
                    double kaiokenLevel = JRMCoreH.getFormMasteryValue(JRMCoreClient.mc.thePlayer, kaiokenID);
                    String kaiokenString = "\n" + JRMCoreH.cldgy + "§cKaioken §8Mastery Lvl: " + JRMCoreH.cldr + formatter.format(kaiokenLevel);

                    s2 = Utility.removeBoldColorCode(formData.getCurrentForm().menuName) + " §8Mastery Lvl: §4" + formatter.format(curLevel) + (removeBase ? (isInKaioken ? kaiokenString : "") : "\n§8" + s2);
                }
                //adds the form color to STR,DEX and WIL attribute values
            } else if (isDrawingAttributes) {
                String currentColor = formData.getFormColorCode(form);
                currentColor = Utility.removeBoldColorCode(currentColor);

                String[] data = getAdjustedAttributeData(s1, s2, currentColor);
                s1 = data[0];
                s2 = data[1];

                // adds the "xMulti" after CON: AttributeValue
            } else if (s1.contains("CON:")) {
                float multi = (float) DBCData.getClient().stats.getCurrentMulti();
                if (s1.contains("x"))
                    s1 = s1.substring(0, s1.indexOf("x") - 1);
                s1 = s1 + (JRMCoreH.round(multi, 1) != 1.0 ? formData.getFormColorCode(form) + " x" + JRMCoreH.round(multi, 1) : "");

                //Corrects Statistics colors
            } else if (isDrawingStats) {
                s1 = replaceFormColor(s1, formData.getFormColorCode(form));
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
        if(button.id == GUI_CHANGE_BUTTON){
            ConfigDBCClient.EnhancedGui = true;
            ConfigDBCClient.EnhancedGuiProperty.set(true);
            ConfigDBCClient.config.save();
        }

        if(button.id == CLIENT_FIRST_PERSON_3D_OPACITY_ADD){
            int value = Math.min(ConfigDBCClient.FirstPerson3DAuraOpacity + 10, 100);
            ConfigDBCClient.FirstPerson3DAuraOpacity = value;
            ConfigDBCClient.FirstPerson3DAuraOpacityProperty.set(value);
            ConfigDBCClient.config.save();
        }
        if(button.id == CLIENT_FIRST_PERSON_3D_OPACITY_REMOVE){
            int value = Math.max(ConfigDBCClient.FirstPerson3DAuraOpacity - 10, 0);
            ConfigDBCClient.FirstPerson3DAuraOpacity = value;
            ConfigDBCClient.FirstPerson3DAuraOpacityProperty.set(value);
            ConfigDBCClient.config.save();
        }
    }

    @Inject(method="drawHUD_clntsett", at=@At("RETURN"), remap=false)
    public void addClientSettings(int posX, int posY, ScaledResolution var5, int var6, FontRenderer var8, CallbackInfo ci){

        int xSize = 256;
        int ySize = 159;
        int guiLeft = (this.width - xSize) / 2;
        int guiTop = (this.height - ySize) / 2 + 7;

        if(cs_mode == 0 && cs_page == 3){
            enhancedGUIdrawString(var8, StatCollector.translateToLocalFormatted("dbc.clientsettings.aura.firstPersonOpacity", ConfigDBCClient.FirstPerson3DAuraOpacity), guiLeft+5, guiTop + 3*15 , 0);
            this.buttonList.add(new JRMCoreGuiButtonsA2(CLIENT_FIRST_PERSON_3D_OPACITY_REMOVE, guiLeft - 10 - 13, guiTop - 2 + 3*15, "<"));
            this.buttonList.add(new JRMCoreGuiButtonsA2(CLIENT_FIRST_PERSON_3D_OPACITY_ADD, guiLeft - 10, guiTop - 2 + 3*15, ">"));
        }
    }

    private static int enhancedGUIdrawString(FontRenderer instance, String text, int x, int y, int color){
        if(ConfigDBCClient.EnhancedGui){
            return instance.drawString(ColorMode.skimColors(text), x, y, ColorMode.textColor(), ConfigDBCClient.DarkMode);
        }
        return instance.drawString(text, x, y, color);
    }

    @Unique
    public void setGuiIDPostInit(int id) {
        this.newGuiID = id;
        this.ignoreInit = true;
    }
}
