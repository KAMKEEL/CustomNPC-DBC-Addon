package kamkeel.npcdbc.client.gui.dbc;

import JinRyuu.DragonBC.common.DBCConfig;
import JinRyuu.JRMCore.*;
import JinRyuu.JRMCore.server.config.dbc.JGConfigDBCFormMastery;
import JinRyuu.JRMCore.server.config.dbc.JGConfigRaces;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.ClientCache;
import kamkeel.npcdbc.client.gui.dbc.constants.GuiInfo;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCAttribute;
import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.mixin.IDBCGuiScreen;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;
import java.util.Arrays;

import static JinRyuu.JRMCore.JRMCoreGuiScreen.kqGW3Z;
import static JinRyuu.JRMCore.JRMCoreH.*;

@SideOnly(Side.CLIENT)
public class StatSheetGui extends AbstractJRMCGui {

    private static final ResourceLocation icons = new ResourceLocation("jinryuumodscore:icons.png");
    private static final ResourceLocation icons3 = new ResourceLocation("jinryuumodscore:icons3.png");
    private int upgradeCounter;
    private GuiIcon genderIcon;
    private final GuiButton[] upgradeButtons = new GuiButton[7];

    private static final String DARKMODE_ACCENT = "§7";

    private static final int VANITY_UPDATE_COOLDOWN = 2000;
    private static int VANITY_TIMER = 0;
    private boolean hasWeight = false;

    private GuiButton UPDATE_VANITY_BUTTON;

    public StatSheetGui() {
        super(10);
    }

    protected StatSheetGui(int guiReplacementID){
        super(guiReplacementID);
    }

    private String getDescription(String attrName, float inc, String maxRelease, String passive, String charging, String extraOutput, int dmgReduction, int statReduction){
        String format = JRMCoreH.trl("jrmc", "StatIncreaseDesc");
        if(maxRelease != null)
            format += JRMCoreH.trl("jrmc", "StatIncreaseDesc2");
        if(passive != null)
            format += JRMCoreH.trl("jrmc", "StatIncreaseDesc3");
        if(charging != null)
            format += "\n§8Charging Output §5"+charging+"§8"; //@TODO add localization
        if(extraOutput != null)
            format += JRMCoreH.trl("jrmc", "StatIncreaseDesc4");
        format = String.format(format, attrName, inc, maxRelease, passive, extraOutput);

        if(statReduction != 0)
            format += "\n"+JRMCoreH.trl("jrmc", "weightreduction")+": §c"+statReduction+"% §7";

        if(dmgReduction > 0)
            format += String.format(JRMCoreH.trl("jrmc", "StatIncreaseDesc5"), "", "", dmgReduction);
        return format;

    }

    @Override
    public void updateScreen(){
        DBCData dbcClient = DBCData.getClient();
        PlayerDBCInfo dataClient = PlayerDataUtil.getClientDBCInfo();
        if(dbcClient == null || dataClient == null) {
            return;
        }

        if(!ConfigDBCClient.EnhancedGui || dbcClient.Accept == 0 || dbcClient.Powertype != 1){
            JRMCoreGuiScreen DBCScreen = new JRMCoreGuiScreen(0);
            ((IDBCGuiScreen) DBCScreen).setGuiIDPostInit(10);
            FMLCommonHandler.instance().showGuiScreen(DBCScreen);
            return;
        }

        VANITY_TIMER++;
        if(!JRMCoreEH.aw && JRMCoreEH.dt && VANITY_TIMER >= VANITY_UPDATE_COOLDOWN && UPDATE_VANITY_BUTTON != null){
            VANITY_TIMER = 0;
            UPDATE_VANITY_BUTTON.enabled = true;
        }

        String formColor = "";
        String formName;
        String formTooltip = null;
        Form customForm = dbcClient.getForm();

        boolean isLegendary = dbcClient.containsSE(14);
        boolean isLegendaryEnabled = JRMCoreH.lgndb(dbcClient.Race, dbcClient.State);

        boolean isMajin = dbcClient.containsSE(12);
        boolean isUI = dbcClient.containsSE(19);
        boolean isGoD = dbcClient.containsSE(20);
        boolean isRose = dbcClient.containsSE(17);
        boolean isMystic = dbcClient.containsSE(13);

        boolean isInKaioken = JRMCoreH.StusEfctsMe(5);

        if(isMajin && isLegendary && isLegendaryEnabled){
            formColor = "§5";
        }else if(isMajin){
            formColor = "§c";
        }else if(isLegendary && isLegendaryEnabled){
            formColor = "§2";
        }

        boolean isFused = dbcClient.containsSE(10) || dbcClient.containsSE(11);
        if(isFused){
            formColor = "§d";
        }

        if(customForm != null){
            formName = Utility.removeColorCodes(customForm.getMenuName());
            formColor = dataClient.getFormColorCode(customForm);
        }else {
            formName = JRMCoreH.trl("jrmc", JRMCoreH.getTransformationName(JRMCoreH.Race, JRMCoreH.isPowerTypeChakra() ? 0 : JRMCoreH.State, isRose, isMystic, isUI, isGoD));


            boolean ascendedAboveBase = (dbcClient.Race == 4 && dbcClient.State > 4) || dbcClient.State > 0;
            if (formColor.isEmpty()){
                if(ascendedAboveBase)
                    formColor = "§6";
                if(ascendedAboveBase && isRose)
                    formColor = "§5";
                if(isMystic)
                    formColor = "§3";
                if(isUI)
                    formColor = "§b";
                if(isGoD)
                    formColor = "§5";
            }
        }
        formColor = (formColor.equals("§4") ? "" : formColor); //Makes stats pop out when your form color is the same as the default stat color
        String darkFormColor = Utility.getDarkColorCode(formColor);
        if(!ConfigDBCClient.DarkMode)
            formColor = darkFormColor;

        //Form Mastery
        if(JGConfigDBCFormMastery.FM_Enabled){
            DecimalFormat formatter = new DecimalFormat("#.##");

            float curLevel = 0;

            //Custom form handling
            if(dataClient != null && dataClient.isInCustomForm()){
                curLevel = dataClient.getFormLevel(dataClient.currentForm);
            //DBC Form handling
            }else{
                curLevel = Float.parseFloat(JRMCoreH.getFormMasteryData().split(",")[1]);
            }
            formTooltip = Utility.removeBoldColorCode(formName) + " §8Mastery Lvl: §4" + formatter.format(curLevel);

            if(isInKaioken){
                int kaiokenID = JRMCoreH.getFormID("Kaioken", JRMCoreH.Race);
                double kaiokenLevel = JRMCoreH.getFormMasteryValue(JRMCoreClient.mc.thePlayer, kaiokenID);
                String kaiokenString = "\n" + JRMCoreH.cldgy + "§cKaioken §8Mastery Lvl: " + JRMCoreH.cldr + formatter.format(kaiokenLevel);

                formTooltip += kaiokenString;
            }
        }

        boolean isMaxLevel = JRMCoreH.getPlayerLevel(JRMCoreH.PlyrAttrbts) >= JRMCoreH.getPlayerLevel(kqGW3Z(false) * 6);
        dynamicLabels.get("level")
            .updateDisplay(JRMCoreH.numSep(JRMCoreH.getPlayerLevel(JRMCoreH.PlyrAttrbts)))
            .updateTooltip(isMaxLevel ? JRMCoreH.trl("jrmc", "LevelMax") : JRMCoreH.trl("jrmc", "LevelNext", JRMCoreH.cllr + JRMCoreH.attrLvlNext(JRMCoreH.PlyrAttrbts) + JRMCoreH.cldgy));

        dynamicLabels.get("tp")
            .updateDisplay(JRMCoreH.numSep(JRMCoreH.curTP))
            .updateTooltip(JRMCoreH.cllr + JRMCoreH.numSep(JRMCoreH.attrCst(JRMCoreH.PlyrAttrbts, 0)) + JRMCoreH.cldgy);

        dynamicLabels.get("race")
            .updateDisplay(JRMCoreH.trl(JRMCoreH.Races[dbcClient.Race]));

        genderIcon.xPosition = guiWidthOffset+5+Minecraft.getMinecraft().fontRenderer.getStringWidth(dynamicLabels.get("race").display);
        genderIcon.textureY = (JRMCoreH.dnsGender(JRMCoreH.dns) < 1 ? 128 : 112);

        dynamicLabels.get("form")
            .updateDisplay(formColor+formName)
            .setTooltip(formTooltip);

        dynamicLabels.get("class")
            .updateDisplay(JRMCoreH.trl("jrmc", JRMCoreH.ClassesDBC[dbcClient.Class]))
            .updateTooltip(JRMCoreH.trl("jrmc", JRMCoreH.ClassesDBCDesc[JRMCoreH.Class]));

        dynamicLabels.get("alignment")
            .updateDisplay(JRMCoreH.algnCur(JRMCoreH.align))
            .updateTooltip(JRMCoreH.align+"%");

        int upgradeCost = JRMCoreH.attrCst(JRMCoreH.PlyrAttrbts, this.upgradeCounter);
        boolean canAffordUpgrade = JRMCoreH.curTP >= upgradeCost;
        boolean allMaxed = JRMCoreH.acm(JRMCoreH.PlyrAttrbts);

        int[] statVals = new int[6];
        if ((int)JRMCoreH.WeightOn > 0) {
            hasWeight = true;
//            wDex = JRMCoreH.weightPerc(1);
//            wStr = JRMCoreH.weightPerc(0);
        }

        for(int i = 0; i < 6; i++){
            boolean isMaxed = !(JRMCoreGuiScreen.kqGW3Z(isFused) > JRMCoreH.PlyrAttrbts[i]);

            upgradeButtons[i].enabled = !isFused && !isMaxed && canAffordUpgrade;

            String upgradeTooltip = null;

            if(isFused){
                upgradeTooltip = JRMCoreH.trl("dbc", "cantupgradef");
            }else if(isMaxed){
                upgradeTooltip = JRMCoreH.trl("jrmc", "AttributeMaxed");
            }else if(!canAffordUpgrade){
                upgradeTooltip = JRMCoreH.trl("jrmc", "cantupgrade")+ "\n" + JRMCoreH.trl("jrmc", "RequiredTP", "§4"+JRMCoreH.numSep(upgradeCost));
            }
            dynamicLabels.get("attr_"+i+"_button_desc").setTooltip(upgradeTooltip);

            boolean isSTRDEXWIL = (i < 2 || i == 3);

            int originalStatVal = JRMCoreH.PlyrAttrbts[i];
            int modifiedStatVal = originalStatVal;
            if(isSTRDEXWIL){
                modifiedStatVal = JRMCoreH.getPlayerAttribute(JRMCoreClient.mc.thePlayer, JRMCoreH.PlyrAttrbts, i, JRMCoreH.State, JRMCoreH.State2, JRMCoreH.Race, JRMCoreH.PlyrSkillX, JRMCoreH.curRelease, JRMCoreH.getArcRsrv(), JRMCoreH.StusEfctsMe(14), JRMCoreH.StusEfctsMe(12), JRMCoreH.StusEfctsMe(5), JRMCoreH.StusEfctsMe(13), JRMCoreH.StusEfctsMe(19), JRMCoreH.StusEfctsMe(20), 1, JRMCoreH.PlyrSkills, isFused, JRMCoreH.getMajinAbsorption());
            }
            statVals[i] = modifiedStatVal;

            String statDisplay = JRMCoreH.numSep(modifiedStatVal);
            String attributeDesc = "§9" + JRMCoreH.attrNms(1, i) + "§8: "+ JRMCoreH.trl("jrmc", JRMCoreH.attrDsc[1][i]);
            if(originalStatVal != modifiedStatVal){
                attributeDesc += "\n" + JRMCoreH.trl("jrmc", "Modified") +": §4" + darkFormColor+statDisplay+"\n§8"
                    + JRMCoreH.trl("jrmc", "Original") +": §4" + JRMCoreH.numSep(originalStatVal)+"§8";

                float multi = (float) modifiedStatVal / originalStatVal;
                if(ConfigDBCClient.AdvancedGui){
                    float formMulti = customForm != null ? customForm.getAttributeMulti(i) : (float) DBCFormMulti(i);
                    String multiString = "";
                    multiString += "\n> Multi: §4x" +   JRMCoreH.round(formMulti, 2) + "§8 (Form)";
                    if(JGConfigDBCFormMastery.FM_Enabled){
                        float masteryMulti = JRMCoreH.round((float) getFormMasteryMulti(), 2);
                        if(masteryMulti > 0)
                            multiString += " * §4x" + masteryMulti + "§8 (Mastery)";
                    }
                    if(customForm != null && customForm.stackable.vanillaStackable){
                        float dbcMulti = (float) DBCFormMulti(i);
                        float stackMulti = dbcMulti * (JGConfigDBCFormMastery.FM_Enabled ? (float) JRMCoreH.getFormMasteryAttributeMulti(JRMCoreClient.mc.thePlayer, JRMCoreH.State, JRMCoreH.State2, JRMCoreH.Race, JRMCoreH.StusEfctsMe(5), JRMCoreH.StusEfctsMe(13), JRMCoreH.StusEfctsMe(19), JRMCoreH.StusEfctsMe(20)) :1);
                        multiString += "\n* §4x" +   JRMCoreH.round(stackMulti, 2) + "§8 (" + JRMCoreH.trl("jrmc", JRMCoreH.getTransformationName(JRMCoreH.Race, JRMCoreH.isPowerTypeChakra() ? 0 : JRMCoreH.State, isRose, isMystic, isUI, isGoD)) + ")";
                    }
                    attributeDesc += multiString;
                }

                if((JRMCoreH.round(multi, 2) != 1))
                    statDisplay += " §4x"+JRMCoreH.round(multi, 2);
            }
            if(i == DBCAttribute.Strength || i == DBCAttribute.Dexterity)
                attributeDesc += (hasWeight ? "\n" + JRMCoreH.trl("jrmc", "trainingweightworn") + ": §c" + (int)JRMCoreH.WeightOn + "§8": "");

            if(ConfigDBCClient.AdvancedGui){
                attributeDesc += "\nRace-Class Multiplier: " + JGConfigRaces.CONFIG_RACES_ATTRIBUTE_MULTI[JRMCoreH.Race][JRMCoreH.Class][i];
                attributeDesc += getAddonBonus(i);
            }

            attributeDesc += getAttributeBonusDescription(i);

            dynamicLabels.get("attr_"+i)
                .updateDisplay((isSTRDEXWIL ? formColor : "")+statDisplay)
                .updateTooltip(attributeDesc);


        }

        upgradeButtons[6].enabled = (!isFused && !allMaxed);

        String upgradeDescription = JRMCoreH.trl("jrmc", "UCnam");
        int descriptionWidth = this.mc.fontRenderer.getStringWidth(upgradeDescription+" ");

        if (allMaxed) {
            upgradeDescription += "\n§c" + JRMCoreH.cct(JRMCoreH.trl("jrmc", "AttributeAllMaxed"));
            descriptionWidth = 150;
        } else if (upgradeCost == 0 || !canAffordUpgrade) {
            upgradeDescription += "\n§c" + JRMCoreH.cct(JRMCoreH.trl("jrmc", "cantupgrade"));
        } else if (isFused) {
            upgradeDescription += "\n§c" + JRMCoreH.cct(JRMCoreH.trl("dbc", "cantupgradef"));
        }else if(upgradeCounter > 0){
            upgradeDescription += ", "+ "x"+JRMCoreH.attributeMultiplier(this.upgradeCounter);
        }
        dynamicLabels.get("upgradeAmount")
            .updateTooltip(upgradeDescription)
            .updateDisplay(JRMCoreH.clbe + (upgradeCost <= 0 ? JRMCoreH.trl("jrmc", "LimitReached") : (allMaxed ? JRMCoreH.trl("jrmc", "AttributeAllMaxed") : JRMCoreH.numSep(upgradeCost)+" TP "+(upgradeCounter > 0 ? "x"+JRMCoreH.attributeMultiplier(this.upgradeCounter) : ""))))
            .tooltipWidth = descriptionWidth;


        this.dynamicLabels.get("release")
            .updateDisplay(dbcClient.Release);


        int SPI = JRMCoreH.stat(JRMCoreClient.mc.thePlayer, 5, 1, 5, statVals[5], dbcClient.Race, dbcClient.Class, JRMCoreH.SklLvl_KiBs(JRMCoreH.PlyrSkills, 1));
        int stat = JRMCoreH.stat(mc.thePlayer, 0, 1, 0, statVals[0], dbcClient.Race, dbcClient.Class, 0);
        float incrementVal = JRMCoreH.statInc(1, 0, 1, JRMCoreH.Race, JRMCoreH.Class, 0.0F);
        int curAtr = (int)((double)stat * 0.01D * (double)JRMCoreH.curRelease * (double)JRMCoreH.weightPerc(0));
        int bonusOutput = 0;
        if(!JRMCoreH.PlyrSettingsB(9))
            bonusOutput = (int)((double)JRMCoreH.SklLvl(12) * 0.0025D * SPI * (double)JRMCoreH.curRelease * 0.01D * DBCConfig.cnfKFd);
        long longValue = (long)curAtr + bonusOutput;

        if (longValue > 2147483647L) {
            longValue = 2147483647L;
        }
        dynamicLabels.get("melee")
            .updateDisplay(formColor+JRMCoreH.numSep(longValue))
            .setTooltip(
                getDescription(
                    JRMCoreH.trl("jrmc", JRMCoreH.attrNms(1, 0)),
                    incrementVal,
                    JRMCoreH.numSep(stat),
                    null,
                    null,
                    (bonusOutput > 0 ? JRMCoreH.numSep(bonusOutput) : null),
                    0,
                    (int) (100.0F - JRMCoreH.weightPerc(0) * 100.0F)
                )
            );

        stat = JRMCoreH.stat(mc.thePlayer, 1, 1, 1, statVals[1], dbcClient.Race, dbcClient.Class, 0);
        incrementVal = JRMCoreH.statInc(1, 1, 1, JRMCoreH.Race, JRMCoreH.Class, 0.0F);
        curAtr = (int)((double)stat * 0.01D * (double)JRMCoreH.curRelease * (double)JRMCoreH.weightPerc(1));
        bonusOutput = 0;
        if(!JRMCoreH.PlyrSettingsB(10)){
            bonusOutput = (int)((double)JRMCoreH.SklLvl(11) *  0.005D * SPI * (double)JRMCoreH.curRelease * 0.01D);
            if(bonusOutput < 1)
                bonusOutput = 1;
            bonusOutput *= DBCConfig.cnfKDd;
        }
        longValue = (long)curAtr + bonusOutput;
        if (longValue > 2147483647L) {
            longValue = 2147483647L;
        }

        String passiveDef = JRMCoreH.numSep((int) (longValue * ((float) JRMCoreConfig.cStatPasDef / 100)));
        String chargingDef = null;
        if(ClientCache.hasChargingDex)
            chargingDef = JRMCoreH.numSep((int) (longValue * (ClientCache.chargingDexValues.get((int) dbcClient.Class) / 100)));

        String defDesc = getDescription(
            JRMCoreH.trl("jrmc", JRMCoreH.attrNms(1, 1)),
            incrementVal,
            JRMCoreH.numSep(stat),
            passiveDef,
            chargingDef,
            (bonusOutput > 0 ? JRMCoreH.numSep(bonusOutput) : null),
            0,
                (int) (100.0F - JRMCoreH.weightPerc(1) * 100.0F)
        );
        dynamicLabels.get("defense")
            .updateDisplay(formColor+JRMCoreH.numSep(longValue))
            .setTooltip(defDesc);


        dynamicLabels.get("passive")
            .updateDisplay(formColor+passiveDef)
            .setTooltip(defDesc);


        if(dynamicLabels.get("charging") != null) {
            dynamicLabels.get("charging")
                .updateDisplay(formColor + chargingDef)
                .setTooltip(defDesc);
        }


        stat = JRMCoreH.stat(mc.thePlayer, 2, 1, 2, statVals[2], dbcClient.Race, dbcClient.Class, 0);
        incrementVal = JRMCoreH.statInc(1, 2, 1, JRMCoreH.Race, JRMCoreH.Class, 0.0F);

        int scaling = JRMCoreH.getPlayerAttribute(JRMCoreClient.mc.thePlayer, JRMCoreH.PlyrAttrbts, 2, JRMCoreH.State, JRMCoreH.State2, JRMCoreH.Race, JRMCoreH.PlyrSkillX, JRMCoreH.curRelease, JRMCoreH.getArcRsrv(), JRMCoreH.StusEfctsMe(14), JRMCoreH.StusEfctsMe(12), JRMCoreH.StusEfctsMe(5), JRMCoreH.StusEfctsMe(13), JRMCoreH.StusEfctsMe(19), JRMCoreH.StusEfctsMe(20), 1, JRMCoreH.PlyrSkills, isFused, JRMCoreH.getMajinAbsorption());
        double percentile = ((double) (Math.max(scaling, statVals[2])) /statVals[2]);

        int dmgReduction = (int) ((1.0D - 1.0D / percentile) * 100);

        boolean isReductionWorthDisplaying = JRMCoreH.round(percentile, 1) != 1.0D;
        dynamicLabels.get("body")
            .updateDisplay(JRMCoreH.numSep(stat) + (isReductionWorthDisplaying ? " R" + dmgReduction + "%" : ""))
            .setTooltip(
                getDescription(
                    JRMCoreH.trl("jrmc", JRMCoreH.attrNms(1, 2)),
                    incrementVal,
                    null,
                    null,
                    null,
                    null,
                    (isReductionWorthDisplaying ? dmgReduction : 0),
                    0
                )
            );

        stat = JRMCoreH.stat(mc.thePlayer, 2, 1, 3, statVals[2], dbcClient.Race, dbcClient.Class, 0);
        incrementVal = JRMCoreH.statInc(1, 3, 1, JRMCoreH.Race, JRMCoreH.Class, 0.0F);

        dynamicLabels.get("actionTime")
            .updateDisplay(JRMCoreH.numSep(stat))
            .setTooltip(
                getDescription(
                    JRMCoreH.trl("jrmc", JRMCoreH.attrNms(1, 2)),
                    incrementVal,
                    null,
                    null,
                    null,
                    null,
                    0,
                    0
                )
            );

        stat = JRMCoreH.stat(mc.thePlayer, 3, 1, 4, statVals[3], dbcClient.Race, dbcClient.Class, 0);
        incrementVal = JRMCoreH.statInc(1, 4, 1, JRMCoreH.Race, JRMCoreH.Class, 0.0F);
        curAtr = (int) (stat*0.01*dbcClient.Release);
        dynamicLabels.get("kiPower")
            .updateDisplay(formColor+JRMCoreH.numSep(curAtr))
            .setTooltip(
                getDescription(
                    JRMCoreH.attrNms(1, 3),
                    incrementVal,
                    JRMCoreH.numSep(stat),
                    null,
                    null,
                    null,
                    0,
                    0
                )
            );


        stat = JRMCoreH.stat(mc.thePlayer, 5, 1, 5, statVals[5], dbcClient.Race, dbcClient.Class, JRMCoreH.SklLvl_KiBs(1));
        incrementVal = JRMCoreH.statInc(1, 5, 1, JRMCoreH.Race, JRMCoreH.Class, 0.0F);
        bonusOutput = stat - JRMCoreH.stat(mc.thePlayer, 5, 1, 5, statVals[5], dbcClient.Race, dbcClient.Class, 0);


        dynamicLabels.get("maxKi")
            .updateDisplay(JRMCoreH.numSep(stat))
            .setTooltip(
                getDescription(
                    JRMCoreH.attrNms(1, 5),
                    incrementVal,
                    null,
                    null,
                    null,
                    (bonusOutput > 0 ? JRMCoreH.numSep(bonusOutput) : null),
                    0,
                    0
                )
            );

        int formID = JRMCoreH.StusEfctsMe(13) ? (JRMCoreH.rc_sai(JRMCoreH.Race) ? JRMCoreH.mstc_sai(JRMCoreH.SklLvlX(1, JRMCoreH.PlyrSkillX) - 1) : (JRMCoreH.rc_arc(JRMCoreH.Race) ? JRMCoreH.mstc_arc() : (JRMCoreH.rc_humNam(JRMCoreH.Race) ? JRMCoreH.mstc_humnam() : 1))) : JRMCoreH.State;
        incrementVal = JRMCoreH.statInc(1, 7, 100, JRMCoreH.Race, JRMCoreH.Class, 0.0F) * 0.01F;
        stat = (int)(JRMCoreH.spdFrm(JRMCoreH.PlyrAttrbts[1], JRMCoreH.SklLvl(2, (byte) 1), 100.0F, true, false, formID, JRMCoreH.State2, incrementVal) * 100.0F);

        int speedReduction = (int) (100.0F - JRMCoreH.weightPerc(1) * 100.0F);

        String statDesc = String.format(JRMCoreH.trl("jrmc", "SpDBDesc"), "§2"+stat+"§8", "§c"+JRMCoreH.attrNms(1, 1)+"§8", "§9"+JRMCoreH.trl("dbc", "Dash")+"§8");
        if(speedReduction > 0)
            statDesc += "\n" + JRMCoreH.trl("jrmc", "weightreduction") + ": §c"+speedReduction+"% §7";



        dynamicLabels.get("running")
            .updateDisplay(stat)
            .setTooltip(statDesc);


        incrementVal = JRMCoreH.statInc(1, 11, 100, JRMCoreH.Race, JRMCoreH.Class, 0.0F) * 0.01F;
        stat = (int)(JRMCoreH.spdFrm(JRMCoreH.PlyrAttrbts[4], JRMCoreH.SklLvl(3, (byte) 1), 100.0F, true, false, formID, JRMCoreH.State2, incrementVal) * 100.0F);


        statDesc = String.format(JRMCoreH.trl("jrmc", "FSDBDesc"), "§2"+stat+"§8", "§c"+JRMCoreH.attrNms(1, 4)+"§8", "§9"+JRMCoreH.trl("dbc", "Fly")+"§8");
        if(speedReduction > 0)
            statDesc += "\n" + JRMCoreH.trl("jrmc", "weightreduction") + ": §c"+speedReduction+"% §7";


        dynamicLabels.get("flying")
            .updateDisplay(stat)
            .setTooltip(statDesc);

    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawAlignmentBar(guiWidthOffset + 8, guiHeightOffset - 6);
        drawStatusEffects(this.width/4, guiHeightOffset - 40);
        drawDBCLabels(mouseX, mouseY);
    }

    @Override
    public void initGui(){
        super.initGui();

        if(!ConfigDBCClient.EnhancedGui){
            return;
        }
        addServerButtons();

        //Button to adjust GUI
        String s = (!ConfigDBCClient.EnhancedGui ? "Old" : "§aModern") +" GUI";
        int button1Width = this.fontRendererObj.getStringWidth(s)+10;
        this.buttonList.add(new JRMCoreGuiButtons00(303030303, (this.width -button1Width)/2, guiHeightOffset - 50, button1Width + 8, 20, s, 0));

        //Difficulty button
        GuiInfo.ReferenceIDs ref = GuiInfo.ReferenceIDs.DIFFICULTY;
        String translation = ref.getTranslation();
        int stringWidth = fontRendererObj.getStringWidth(translation)+15;
        this.buttonList.add(new JRMCoreGuiButtons00(ref.getButtonId(), guiWidthOffset + 260, height/2 + 55, stringWidth, 20, translation, 0));

        String dark = ConfigDBCClient.DarkMode ? "Light" : "Dark";
        this.buttonList.add(new JRMCoreGuiButtons00(404040404, guiWidthOffset + 260, height/2 + 12, stringWidth, 20, dark, 0));

        String advan = ConfigDBCClient.AdvancedGui ? "Advanced" : "Simple";
        this.buttonList.add(new JRMCoreGuiButtons00(505050505, guiWidthOffset + 260, height/2 + 34, stringWidth, 20, advan, 0));

        int index = 0;

        dynamicLabels.put("level", new JRMCoreLabel(
            JRMCoreH.trl("jrmc", "Level")+": "+ (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "")+ "%s",
            "%s",
            guiWidthOffset+6,
            guiHeightOffset+index*10+6
        ));
        index++;

        dynamicLabels.put("tp", new JRMCoreLabel(
            JRMCoreH.trl("jrmc", "TP")+": "+ (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "")+ "%s",
            JRMCoreH.trl("jrmc", "TrainingPoints")+"\n"+JRMCoreH.trl("jrmc", "RequiredTP"),
            guiWidthOffset+6,
            guiHeightOffset+index*10+6
        ));
        index++;

        dynamicLabels.put("race", new JRMCoreLabel(
            JRMCoreH.trl("jrmc", "Race")+": "+ (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "")+ "%s",
            null,
            guiWidthOffset+6,
            guiHeightOffset+index*10+6
        ));
        genderIcon = new GuiIcon(
            icons3,
            0,
            guiHeightOffset+2+index*10+1,
            0,
            (JRMCoreH.dnsGender(JRMCoreH.dns) < 1 ? 128 : 112),
            16,
            16
        );
        this.hoverableStaticLabels.add(genderIcon);
        index++;

        dynamicLabels.put("form", new JRMCoreLabel(
            JRMCoreH.trl("jrmc", "TRState")+": "+ (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "")+ "%s",
            null,
            guiWidthOffset+6,
            guiHeightOffset+index*10+6

        ));
        index++;

        dynamicLabels.put("class", new JRMCoreLabel(
            JRMCoreH.trl("jrmc", "Class")+": "+ (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "")+ "%s",
            "%s",
            this.guiWidthOffset+6,
            this.guiHeightOffset+index*10+6
        ));
        index++;

        dynamicLabels.put("alignment", new JRMCoreLabel(
            JRMCoreH.trl("jrmc", "Alignment")+": "+ (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "")+ "%s",
            JRMCoreH.trl("jrmc", "AlignmentDesc"),
            this.guiWidthOffset+6,
            this.guiHeightOffset+index*10+6
        ));
        index++;

        hoverableStaticLabels.add(new JRMCoreLabel(
            JRMCoreH.trl("jrmc", "Attributes")+":",
            "%s",
            this.guiWidthOffset+6,
            this.guiHeightOffset+index*10+11
        ).updateDisplay());
        index++;

        String[] statNames = new String[]{
            "STR", "DEX", "CON", "WIL", "MND", "SPI"
        };

        for(int i = 0; i < 6; i++){
            int yPos = guiHeightOffset+i*10+index*10+6;

            GuiButton button = new JRMCoreGuiButtonsA3(i, guiWidthOffset + 5, yPos+3, 10, 2, false);

            dynamicLabels.put("attr_"+i+"_button_desc", new JRMCoreLabel(button, "%s", null));
            upgradeButtons[i] = button;
            dynamicLabels.put("attr_"+i, new JRMCoreLabel(
    (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + statNames[i]+": §4%s",
    "%s",
            guiWidthOffset+17,
            yPos+5
            ));


        }
        index+=6;
        GuiButton amountButton = new JRMCoreGuiButtonsA3(6, guiWidthOffset+5, guiHeightOffset+index*10+12, 10, 2, false);
        upgradeButtons[6] = amountButton;
        this.buttonList.addAll(Arrays.asList(upgradeButtons));

        dynamicLabels.put("upgradeAmount", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + "UC: %s",
            "%s",
            guiWidthOffset+17,
            guiHeightOffset+index*10+14
        ).setDisplay("Testing"));

        index = 0;
        this.dynamicLabels.put("release", new JRMCoreLabel(
            JRMCoreH.trl("jrmc", "PowerRelease")+": "+(ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "")+"%s%%",
            null,
            guiWidthOffset+133,
            guiHeightOffset+index*10+6
        ));
        index++;

        this.hoverableStaticLabels.add(new JRMCoreLabel(
            null,
            null,
            guiWidthOffset+133,
            guiHeightOffset+index*10+11
        ).setDisplay(JRMCoreH.trl("jrmc", "Stats")));
        index++;


        this.dynamicLabels.put("melee", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + JRMCoreH.trl("jrmc", "mleDB")+": §4%s",
            JRMCoreH.trl("jrmc", "StatIncreaseDesc")+JRMCoreH.trl("jrmc", "StatIncreaseDesc2"),
            guiWidthOffset+133,
            guiHeightOffset+index*10+11
        ));
        index++;

        this.dynamicLabels.put("defense", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + JRMCoreH.trl("jrmc", "DefDB")+": §4%s",
            "%s",
            guiWidthOffset+133,
            guiHeightOffset+index*10+11
        ));
        index++;

        this.dynamicLabels.put("passive", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + JRMCoreH.trl("jrmc", "Passive")+": §4%s",
            "%s",
            guiWidthOffset+138,
            guiHeightOffset+index*10+11
        ));
        index++;

        if(ClientCache.hasChargingDex){
            this.dynamicLabels.put("charging", new JRMCoreLabel(
                (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + "Charging: §4%s",
                "%s",
                guiWidthOffset+138,
                guiHeightOffset+index*10+11
            ));
            index++;
        }

        this.dynamicLabels.put("body", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + JRMCoreH.trl("jrmc", "BdDB")+": §4%s",
            "%s",
            guiWidthOffset+133,
            guiHeightOffset+index*10+11
        ));
        index++;

        this.dynamicLabels.put("actionTime", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + JRMCoreH.trl("jrmc", "StDB")+": §4%s",
            "%s",
            guiWidthOffset+133,
            guiHeightOffset+index*10+11
        ));
        index++;

        this.dynamicLabels.put("kiPower", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + JRMCoreH.trl("jrmc", "EnPwDB")+": §4%s",
            "%s",
            guiWidthOffset+133,
            guiHeightOffset+index*10+11
        ));
        index++;

        this.dynamicLabels.put("maxKi", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + JRMCoreH.trl("jrmc", "EnPlDB")+": §4%s",
            "%s",
            guiWidthOffset+133,
            guiHeightOffset+index*10+11
        ));
        index++;
        this.dynamicLabels.put("running", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + JRMCoreH.trl("jrmc", "SpDB")+": §4%s%%",
            "%s",
            guiWidthOffset+133,
            guiHeightOffset+index*10+11
        ));
        index++;
        this.dynamicLabels.put("flying", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + JRMCoreH.trl("jrmc", "FSDB")+": §4%s%%",
            "%s",
            guiWidthOffset+133,
            guiHeightOffset+index*10+11
        ));
        index++;


        if(JRMCoreEH.dt){
            String name = "Update vanity";
            int width = this.fontRendererObj.getStringWidth(name);
            UPDATE_VANITY_BUTTON = new JRMCoreGuiButtons00(100, guiWidthOffset + 260, guiHeightOffset + 3, width + 8, 20, name, 0);
            buttonList.add(UPDATE_VANITY_BUTTON);

            name = (JRMCoreEH.gk ? "Hide" : "Show") + " own vanity";
            width = this.fontRendererObj.getStringWidth(name);
            buttonList.add(new JRMCoreGuiButtons00(101, guiWidthOffset + 260, guiHeightOffset + 24, width + 8, 20, name, 0));
        }

        updateScreen();
    }

    @Override
    protected void actionPerformed(GuiButton button){
        super.actionPerformed(button);
        int id = button.id;
        if(id == 303030303){
            ConfigDBCClient.EnhancedGui = false;
            ConfigDBCClient.EnhancedGuiProperty.set(false);
            ConfigDBCClient.config.save();
        }
        if(id == 404040404){
            ConfigDBCClient.DarkMode = !ConfigDBCClient.DarkMode;
            ConfigDBCClient.DarkModeProperty.set(ConfigDBCClient.DarkMode);
            ConfigDBCClient.config.save();
            initGui();
        }
        if(id == 505050505){
            ConfigDBCClient.AdvancedGui = !ConfigDBCClient.AdvancedGui;
            ConfigDBCClient.AdvancedGuiModeProperty.set(ConfigDBCClient.AdvancedGui);
            ConfigDBCClient.config.save();
            initGui();
        }
        if(id >= 0 && id <= 5){
            if(!JRMCoreH.isFused()){
                JRMCoreH.Upg((byte) (id+upgradeCounter*6));
            }
        }
        if(id == 6){
            this.upgradeCounter++;
            if(this.upgradeCounter > 3)
                this.upgradeCounter = 0;
        }

        if(id == 100){
            JRMCoreEH.aw = true;
            VANITY_TIMER = 0;
            button.enabled = false;
        }
        if(id == 101){
            JRMCoreEH.gk = !JRMCoreEH.gk;
            String name = (JRMCoreEH.gk ? "Hide" : "Show") + " own vanity";
            button.displayString = name;
            button.width = fontRendererObj.getStringWidth(name)+8;
        }

    }

    protected void drawAlignmentBar(int x, int y){
        mc.getTextureManager().bindTexture(icons);
        GL11.glPushMatrix();
        int alignment1;
        int alignment2;

        if(JRMCoreH.Algnmnt_Good(JRMCoreH.align)){
            alignment1 = 654591;
            alignment2 = 6028287;
        } else if (JRMCoreH.Algnmnt_Neut(JRMCoreH.align)) {
            alignment1 = 9127101;
            alignment2 = 11042302;
        } else {
            alignment1 = 16726090;
            alignment2 = 16544131;
        }

        float h2 = (float)(alignment1 >> 16 & 255) / 255.0F;
        float h3 = (float)(alignment1 >> 8 & 255) / 255.0F;
        float h4 = (float)(alignment1 & 255) / 255.0F;
        float h1 = 1.0F;
        GL11.glColor4f(h1 * h2, h1 * h3, h1 * h4, 0.5F);
        this.drawTexturedModalRect(x, y, 8, 174, 241, 7);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
        this.drawTexturedModalRect(x-8, y+1, 0, 169, menuImageWidth, 5);
        int max = menuImageWidth - 20;
        if (max < 1) {
            max = 1;
        }

        double maxperc = (double)max * 0.01D;
        int alignmentAdjusted = (int)(maxperc * (double)JRMCoreH.align);
        if (alignmentAdjusted > menuImageWidth) {
            alignmentAdjusted = menuImageWidth;
        }

        h2 = (float)(alignment2 >> 16 & 255) / 255.0F;
        h3 = (float)(alignment2 >> 8 & 255) / 255.0F;
        h4 = (float)(alignment2 & 255) / 255.0F;
        GL11.glColor4f(h1 * h2, h1 * h3, h1 * h4, 1.0F);
        this.drawTexturedModalRect((this.width - 5) / 2 - max / 2 + alignmentAdjusted - 4, guiHeightOffset-9, 0, 182, 11, 13);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected void drawStatusEffects(int x, int y){
        JRMCoreClient.bars.showSE(x, y, 0, 0);
    }

    public String getAttributeBonusDescription(int attributeID) {
        if (JRMCoreConfig.JRMCABonusOn) {
            String description = "\nBonus Attributes:";
            String[] bonuses = JRMCoreH.getBonusAttributes(attributeID).split("\\|");
            String[] var5 = bonuses;
            int var6 = bonuses.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String bonus = var5[var7];
                if (bonus.equals("n")) {
                    return "";
                } else {
                    description = description + "\n " + bonus.replace(";", ": ");
                }
            }

            return description;
        } else {
            return "";
        }
    }

    public String getAddonBonus(int attributeID) {
        String description = "";
        DBCData dbcData = DBCData.get(Minecraft.getMinecraft().thePlayer);
        if(!dbcData.currentBonuses.isEmpty()){
            description += "\nBonus Stats:";
            for(PlayerBonus playerBonus : dbcData.currentBonuses.values()){
                if(attributeID == DBCAttribute.Strength && playerBonus.strength != 0){
                    description += "\n>> " + playerBonus.name + ": " + (playerBonus.type == 1 ? " " : "x ") + playerBonus.strength;
                } else if(attributeID == DBCAttribute.Dexterity && playerBonus.dexterity != 0){
                    description += "\n>> " + playerBonus.name + ": " + (playerBonus.type == 1 ? " " : "x ") + playerBonus.dexterity;
                } else if(attributeID == DBCAttribute.Constitution && playerBonus.constituion != 0){
                    description += "\n>> " + playerBonus.name + ": " + (playerBonus.type == 1 ? " " : "x ") + playerBonus.constituion;
                } else if(attributeID == DBCAttribute.Willpower && playerBonus.willpower != 0){
                    description += "\n>> " + playerBonus.name + ": " + (playerBonus.type == 1 ? " " : "x ") + playerBonus.willpower;
                } else if(attributeID == DBCAttribute.Spirit && playerBonus.spirit != 0){
                    description += "\n>> " + playerBonus.name + ": " + (playerBonus.type == 1 ? " " : "x ") + playerBonus.spirit;
                }
            }
        }
        return description;
    }

    public double DBCFormMulti(int atr){
        switch (JRMCoreH.Race){
            case 0:
                return ev_oob(TransHmStBnP, JRMCoreH.State, atr);
            case 1:
                return ev_oob(TransSaiStBnP, JRMCoreH.State, atr);
            case 2:
                return ev_oob(TransHalfSaiStBnP, JRMCoreH.State, atr);
            case 3:
                return ev_oob(TransNaStBnP, JRMCoreH.State, atr);
            case 4:
                return ev_oob(TransFrStBnP, JRMCoreH.State, atr);
            case 5:
                return ev_oob(TransMaStBnP, JRMCoreH.State, atr);
        }
        return 0;
    }

    public double DBCFormFlat(int atr){
        switch (JRMCoreH.Race){
            case 0:
                return ev_oob(TransHmStBnF, JRMCoreH.State, atr);
            case 1:
                return ev_oob(TransSaiStBnF, JRMCoreH.State, atr);
            case 2:
                return ev_oob(TransHalfSaiStBnF, JRMCoreH.State, atr);
            case 3:
                return ev_oob(TransNaStBnF, JRMCoreH.State, atr);
            case 4:
                return ev_oob(TransFrStBnF, JRMCoreH.State, atr);
            case 5:
                return ev_oob(TransMaStBnF, JRMCoreH.State, atr);
        }
        return 0;
    }

    public double getFormMasteryMulti(){
        Form form = DBCData.getClient().getForm();
        PlayerDBCInfo dataClient = PlayerDataUtil.getClientDBCInfo();
        if(form != null && dataClient != null){
            return form.mastery.calculateMulti("attribute", dataClient.formLevels.get(form.id));
        }
        return JRMCoreH.getFormMasteryAttributeMulti(JRMCoreClient.mc.thePlayer, JRMCoreH.State, JRMCoreH.State2, JRMCoreH.Race, JRMCoreH.StusEfctsMe(5), JRMCoreH.StusEfctsMe(13), JRMCoreH.StusEfctsMe(19), JRMCoreH.StusEfctsMe(20));
    }
}
