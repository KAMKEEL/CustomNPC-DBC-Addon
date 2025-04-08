package kamkeel.npcdbc.client.gui.dbc;

import JinRyuu.DragonBC.common.DBCConfig;
import JinRyuu.JRMCore.*;
import JinRyuu.JRMCore.server.config.dbc.JGConfigDBCFormMastery;
import JinRyuu.JRMCore.server.config.dbc.JGConfigRaces;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.LocalizationHelper;
import kamkeel.npcdbc.client.ClientCache;
import kamkeel.npcdbc.client.gui.dbc.constants.GuiInfo;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.config.ConfigDBCGeneral;
import kamkeel.npcdbc.constants.DBCAttribute;
import kamkeel.npcdbc.constants.DBCStats;
import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.mixins.late.IDBCGuiScreen;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.NoppesUtil;
import org.lwjgl.opengl.GL11;

import java.net.URI;
import java.text.DecimalFormat;
import java.util.Arrays;

import static JinRyuu.JRMCore.JRMCoreGuiScreen.kqGW3Z;
import static JinRyuu.JRMCore.JRMCoreH.*;

@SideOnly(Side.CLIENT)
public class StatSheetGui extends AbstractJRMCGui implements GuiYesNoCallback {

    private static final ResourceLocation icons = new ResourceLocation("jinryuumodscore:icons.png");
    private static final ResourceLocation icons3 = new ResourceLocation("jinryuumodscore:icons3.png");
    private int upgradeCounter;
    private GuiIcon genderIcon;
    private GuiIcon.Button customizeFormButton;
    private final GuiButton[] upgradeButtons = new GuiButton[7];

    private static final String DARKMODE_ACCENT = "§7";

    private static final int VANITY_UPDATE_COOLDOWN = 2000;
    private static int VANITY_TIMER = 0;
    private boolean hasWeight = false;

    private GuiButton UPDATE_VANITY_BUTTON;

    private Form currentForm;

    public StatSheetGui() {
        super(10);
    }

    protected StatSheetGui(int guiReplacementID) {
        super(guiReplacementID);
    }

    /**
     * @param attrName
     * @param inc
     * @param maxRelease
     * @param passive
     * @param charging
     * @param extraOutput
     * @param dmgReduction
     * @param statReduction
     * @return
     */
    private String getDescription(String attrName, float inc, String maxRelease, String passive, String charging, String extraOutput, int dmgReduction, int statReduction) {
        String format = trl("jrmc", "StatIncreaseDesc");
        if (maxRelease != null)
            format += trl("jrmc", "StatIncreaseDesc2");
        if (passive != null)
            format += trl("jrmc", "StatIncreaseDesc3");
        if (charging != null)
            format += LocalizationHelper.getLocalizedString("statsheet.stat.charging.description");
        if (extraOutput != null)
            format += trl("jrmc", "StatIncreaseDesc4");
        format = String.format(format, attrName, inc, maxRelease, passive, extraOutput, charging);

        if (statReduction != 0)
            format += "\n" + trl("jrmc", "weightreduction") + ": §c" + statReduction + "% §7";

        if (dmgReduction > 0)
            format += String.format(trl("jrmc", "StatIncreaseDesc5"), "", "", dmgReduction);
        return format;

    }

    @Override
    public void updateScreen() {
        DBCData dbcClient = DBCData.getClient();
        PlayerDBCInfo dataClient = PlayerDataUtil.getClientDBCInfo();
        Form currentForm = null;
        if (dbcClient == null || dataClient == null) {
            return;
        }

        if ((!ConfigDBCClient.EnhancedGui && ConfigDBCClient.EnableDebugStatSheetSwitching) || dbcClient.Accept == 0 || dbcClient.Powertype != 1) {
            JRMCoreGuiScreen DBCScreen = new JRMCoreGuiScreen(0);
            ((IDBCGuiScreen) DBCScreen).setGuiIDPostInit(10);
            FMLCommonHandler.instance().showGuiScreen(DBCScreen);
            return;
        }

        VANITY_TIMER++;
        if (!JRMCoreEH.aw && JRMCoreEH.dt && VANITY_TIMER >= VANITY_UPDATE_COOLDOWN && UPDATE_VANITY_BUTTON != null) {
            VANITY_TIMER = 0;
            UPDATE_VANITY_BUTTON.enabled = true;
        }

        String formStatColor = "";
        String formName;
        String formTooltip = null;
        Form customForm = dbcClient.getForm();

        boolean isLegendary = dbcClient.containsSE(14);
        boolean isLegendaryEnabled = lgndb(dbcClient.Race, dbcClient.State);

        boolean isMajin = dbcClient.containsSE(12);
        boolean isUI = dbcClient.containsSE(19);
        boolean isGoD = dbcClient.containsSE(20);
        boolean isRose = dbcClient.containsSE(17);
        boolean isMystic = dbcClient.containsSE(13);

        boolean isInKaioken = StusEfctsMe(5);

        if (isMajin && isLegendary && isLegendaryEnabled) {
            formStatColor = "§5";
        } else if (isMajin) {
            formStatColor = "§c";
        } else if (isLegendary && isLegendaryEnabled) {
            formStatColor = "§2";
        }

        boolean isFused = dbcClient.containsSE(10) || dbcClient.containsSE(11);
        if (isFused) {
            formStatColor = "§d";
        }

        String formNameColor = "";

        if (customForm == null) {
            formName = trl("jrmc", getTransformationName(Race, isPowerTypeChakra() ? 0 : State, isRose, isMystic, isUI, isGoD));


            boolean ascendedAboveBase = dbcClient.Race == 4 ? dbcClient.State != 4 : dbcClient.State != 0;
            if (formStatColor.isEmpty()) {
                if (isInKaioken)
                    formStatColor = "§c";
                if (ascendedAboveBase)
                    formStatColor = "§6";
                if (ascendedAboveBase && isRose)
                    formStatColor = "§5";
                if (isMystic)
                    formStatColor = "§3";
                if (isUI)
                    formStatColor = "§b";
                if (isGoD)
                    formStatColor = "§5";
            }

        } else {
            formName = Utility.removeColorCodes(customForm.getMenuName());
            formStatColor = dataClient.getFormColorCode(customForm);

            formNameColor = formStatColor;

            if (formStatColor.isEmpty() || formStatColor.equals("§4")) {
                formStatColor = "§6";
                if (isInKaioken)
                    formStatColor = "§c";
                if (isRose)
                    formStatColor = "§5";
                if (isMystic)
                    formStatColor = "§3";
                if (isUI)
                    formStatColor = "§b";
                if (isGoD)
                    formStatColor = "§5";
            }
        }
        if (formNameColor.isEmpty() && isInKaioken) {
            formNameColor = formStatColor;
        }


        formStatColor = (formStatColor.equals("§4") ? "" : formStatColor); //Makes stats pop out when your form color is the same as the default stat color
        String darkFormColor = Utility.getDarkColorCode(formStatColor);
        if (!ConfigDBCClient.DarkMode) {
            formStatColor = darkFormColor;
            formNameColor = Utility.getDarkColorCode(formNameColor);
        }

        //Form Mastery
        if (JGConfigDBCFormMastery.FM_Enabled) {
            DecimalFormat formatter = new DecimalFormat("#.##");

            float curLevel = 0;

            //Custom form handling
            if (dataClient != null && dataClient.isInCustomForm()) {
                curLevel = dbcClient.addonFormLevel;
                currentForm = dataClient.getCurrentForm();
                formTooltip = currentForm.menuName + " §8Mastery Lvl: §4" + formatter.format(curLevel);
                //DBC Form handling
            } else {
                curLevel = Float.parseFloat(getFormMasteryData().split(",")[1]);
                formTooltip = formName + " §8Mastery Lvl: §4" + formatter.format(curLevel);
            }

            if (isInKaioken) {
                int kaiokenID = getFormID("Kaioken", Race);
                double kaiokenLevel = getFormMasteryValue(JRMCoreClient.mc.thePlayer, kaiokenID);
                String kaiokenString = "\n" + cldgy + "§cKaioken §8Mastery Lvl: " + cldr + formatter.format(kaiokenLevel);

                formTooltip += kaiokenString;
            }
        }

        boolean isMaxLevel = getPlayerLevel(PlyrAttrbts) >= getPlayerLevel(kqGW3Z(false) * 6);
        dynamicLabels.get("level")
            .updateDisplay(numSep(getPlayerLevel(PlyrAttrbts)))
            .updateTooltip(isMaxLevel ? trl("jrmc", "LevelMax") : trl("jrmc", "LevelNext", cllr + attrLvlNext(PlyrAttrbts) + cldgy));


        dynamicLabels.get("tp")
            .updateDisplay(numSep(curTP))
            .updateTooltip(cllr + numSep(attrCst(PlyrAttrbts, 0)) + cldgy);

        dynamicLabels.get("race")
            .updateDisplay(trl("jrmc", Races[dbcClient.Race]));

        genderIcon.xPosition = guiWidthOffset + 5 + Minecraft.getMinecraft().fontRenderer.getStringWidth(dynamicLabels.get("race").display);
        genderIcon.textureY = (dnsGender(dns) < 1 ? 128 : 112);


        dynamicLabels.get("form")
            .updateDisplay(formNameColor + formName)
            .setTooltip(formTooltip);

        if (currentForm != null && currentForm.display.isCustomizable()) {
            JRMCoreLabel formlabelref = dynamicLabels.get("form");
            customizeFormButton.xPosition = formlabelref.xPosition + fontRendererObj.getStringWidth(formlabelref.display) + 1;
            customizeFormButton.visible = true;
            this.currentForm = currentForm;
        } else {
            customizeFormButton.visible = false;
            this.currentForm = null;
        }


        dynamicLabels.get("class")
            .updateDisplay(trl("jrmc", ClassesDBC[dbcClient.Class]))
            .updateTooltip(trl("jrmc", ClassesDBCDesc[JRMCoreH.Class]));

        dynamicLabels.get("alignment")
            .updateDisplay(algnCur(align))
            .updateTooltip(align + "%");

        int upgradeCost = attrCst(PlyrAttrbts, this.upgradeCounter);
        boolean canAffordUpgrade = curTP >= upgradeCost;
        boolean allMaxed = acm(PlyrAttrbts);

        int[] statVals = new int[6];
        if ((int) WeightOn > 0) {
            hasWeight = true;
//            wDex = JRMCoreH.weightPerc(1);
//            wStr = JRMCoreH.weightPerc(0);
        }

        for (int i = 0; i < 6; i++) {
            boolean isMaxed = !(kqGW3Z(isFused) > PlyrAttrbts[i]);

            upgradeButtons[i].enabled = !isFused && !isMaxed && canAffordUpgrade;

            String upgradeTooltip = null;

            if (isFused) {
                upgradeTooltip = trl("dbc", "cantupgradef");
            } else if (isMaxed) {
                upgradeTooltip = trl("jrmc", "AttributeMaxed");
            } else if (!canAffordUpgrade) {
                upgradeTooltip = trl("jrmc", "cantupgrade") + "\n" + trl("jrmc", "RequiredTP", "§4" + numSep(upgradeCost));
            }
            dynamicLabels.get("attr_" + i + "_button_desc").setTooltip(upgradeTooltip);

            boolean isSTRDEXWIL = (i < 2 || i == 3);

            int originalStatVal = PlyrAttrbts[i];
            int modifiedStatVal = originalStatVal;
            if (isSTRDEXWIL) {
                modifiedStatVal = getPlayerAttribute(JRMCoreClient.mc.thePlayer, PlyrAttrbts, i, State, State2, Race, PlyrSkillX, curRelease, getArcRsrv(), StusEfctsMe(14), StusEfctsMe(12), StusEfctsMe(5), StusEfctsMe(13), StusEfctsMe(19), StusEfctsMe(20), 1, PlyrSkills, isFused, getMajinAbsorption());
            }
            statVals[i] = modifiedStatVal;

            if (!isSTRDEXWIL) {
                modifiedStatVal *= (1 + getAddonBonusMulti(i));
            }

            int flatBonus = (int) getAddonBonusStat(i);
            String statDisplay = numSep((modifiedStatVal + (!isSTRDEXWIL ? flatBonus : 0)));
            String attributeDesc = "§9" + attrNms(1, i) + "§8: " + trl("jrmc", attrDsc[1][i]);
            if (originalStatVal != modifiedStatVal) {
                attributeDesc += "\n" + trl("jrmc", "Modified") + ": §4" + darkFormColor + statDisplay + "\n§8"
                    + trl("jrmc", "Original") + ": §4" + numSep(originalStatVal) + "§8";

                float multi = (float) (modifiedStatVal - (isSTRDEXWIL ? flatBonus : 0)) / originalStatVal;
                if (ConfigDBCClient.AdvancedGui && isSTRDEXWIL) {
                    float formMulti = currentForm != null ? currentForm.getAttributeMulti(i) : (float) DBCFormMulti(i);
                    String multiString = "";
                    multiString += "\n> Multi: §4x" + round(formMulti, 2) + "§8 (Form)";
                    if (JGConfigDBCFormMastery.FM_Enabled) {
                        float masteryMulti = round((float) getFormMasteryMulti(), 2);
                        if (masteryMulti > 0)
                            multiString += " * §4x" + masteryMulti + "§8 (Mastery)";
                    }
                    if (currentForm != null && currentForm.stackable.vanillaStackable) {
                        float dbcMulti = (float) DBCFormMulti(i);
                        float stackMulti = dbcMulti * (JGConfigDBCFormMastery.FM_Enabled ? (float) getFormMasteryAttributeMulti(JRMCoreClient.mc.thePlayer, State, State2, Race, StusEfctsMe(5), StusEfctsMe(13), StusEfctsMe(19), StusEfctsMe(20)) : 1);
                        multiString += "\n* §4x" + round(stackMulti, 2) + "§8 (" + trl("jrmc", getTransformationName(Race, isPowerTypeChakra() ? 0 : State, isRose, isMystic, isUI, isGoD)) + ")";
                    }
                    attributeDesc += multiString;
                }

                if ((round(multi, 2) != 1))
                    statDisplay += " §4x" + round(multi, 2);
            }
            if (i == DBCAttribute.Strength || i == DBCAttribute.Dexterity)
                attributeDesc += (hasWeight ? "\n" + trl("jrmc", "trainingweightworn") + ": §c" + (int) WeightOn + "§8" : "");

            if (ConfigDBCClient.AdvancedGui) {
                attributeDesc += "\nRace-Class Multiplier: " + JGConfigRaces.CONFIG_RACES_ATTRIBUTE_MULTI[Race][JRMCoreH.Class][i];
                attributeDesc += getAddonBonus(i);
            }

            attributeDesc += getAttributeBonusDescription(i);

            dynamicLabels.get("attr_" + i)
                .updateDisplay((isSTRDEXWIL ? formStatColor : "") + statDisplay)
                .updateTooltip(attributeDesc);


        }

        upgradeButtons[6].enabled = (!isFused && !allMaxed);

        String upgradeDescription = trl("jrmc", "UCnam");
        int descriptionWidth = this.mc.fontRenderer.getStringWidth(upgradeDescription + " ");

        if (allMaxed) {
            upgradeDescription += "\n§c" + cct(trl("jrmc", "AttributeAllMaxed"));
            descriptionWidth = 150;
        } else if (upgradeCost == 0 || !canAffordUpgrade) {
            upgradeDescription += "\n§c" + cct(trl("jrmc", "cantupgrade"));
        } else if (isFused) {
            upgradeDescription += "\n§c" + cct(trl("dbc", "cantupgradef"));
        } else if (upgradeCounter > 0) {
            upgradeDescription += ", " + "x" + attributeMultiplier(this.upgradeCounter);
        }
        dynamicLabels.get("upgradeAmount")
            .updateTooltip(upgradeDescription)
            .updateDisplay(clbe + (upgradeCost <= 0 ? trl("jrmc", "LimitReached") : (allMaxed ? trl("jrmc", "AttributeAllMaxed") : numSep(upgradeCost) + " TP " + (upgradeCounter > 0 ? "x" + attributeMultiplier(this.upgradeCounter) : ""))))
            .tooltipWidth = descriptionWidth;


        this.dynamicLabels.get("release")
            .updateDisplay(dbcClient.Release);


        int SPI = stat(JRMCoreClient.mc.thePlayer, 5, 1, 5, statVals[5], dbcClient.Race, dbcClient.Class, SklLvl_KiBs(PlyrSkills, 1));
        int stat = stat(mc.thePlayer, 0, 1, 0, statVals[0], dbcClient.Race, dbcClient.Class, 0);
        float incrementVal = statInc(1, 0, 1, Race, JRMCoreH.Class, 0.0F);
        int curAtr = (int) ((double) stat * 0.01D * (double) curRelease * (double) weightPerc(0));
        int bonusOutput = 0;
        if (!PlyrSettingsB(9))
            bonusOutput = (int) ((double) SklLvl(12) * 0.0025D * SPI * (double) curRelease * 0.01D * DBCConfig.cnfKFd);
        long longValue = (long) curAtr + bonusOutput;

        if (longValue > 2147483647L) {
            longValue = 2147483647L;
        }
        dynamicLabels.get("melee")
            .updateDisplay(formStatColor + numSep(longValue))
            .setTooltip(
                getDescription(
                    attrNms(1, 0),
                    incrementVal,
                    numSep(stat),
                    null,
                    null,
                    (bonusOutput > 0 ? numSep(bonusOutput) : null),
                    0,
                    (int) (100.0F - weightPerc(0) * 100.0F)
                ) + getFormStatBonus(DBCStats.Melee)
            );

        stat = stat(mc.thePlayer, 1, 1, 1, statVals[1], dbcClient.Race, dbcClient.Class, 0);
        incrementVal = statInc(1, 1, 1, Race, JRMCoreH.Class, 0.0F);
        curAtr = (int) ((double) stat * 0.01D * (double) curRelease * (double) weightPerc(1));
        // Ki Protection Amount
        bonusOutput = 0;
        if (!PlyrSettingsB(10)) {
            bonusOutput = (int) ((double) SklLvl(11) * 0.005D * SPI * (double) curRelease * 0.01D);
            if (bonusOutput < 1)
                bonusOutput = 1;
            bonusOutput *= DBCConfig.cnfKDd;
        }
        longValue = curAtr + bonusOutput;
        if (longValue > 2147483647L) {
            longValue = 2147483647L;
        }

        int passiveDefInt = (int) ((float) ((longValue - bonusOutput) * JRMCoreConfig.StatPasDef) * 0.01F) + bonusOutput;
        String passiveDef = numSep(passiveDefInt);
        String chargingDef = null;
        if (ClientCache.hasChargingDex)
            chargingDef = numSep((int) ((longValue - bonusOutput) * (ClientCache.chargingDexValues.get((int) dbcClient.Class) / 100)) + bonusOutput);

        String defDesc = getDescription(
            attrNms(1, 1),
            incrementVal,
            numSep(stat),
            passiveDef,
            chargingDef,
            (bonusOutput > 0 ? numSep(bonusOutput) : null),
            0,
            (int) (100.0F - weightPerc(1) * 100.0F)
        ) +  getFormStatBonus(DBCStats.Defense);
        dynamicLabels.get("defense")
            .updateDisplay(formStatColor + numSep(longValue))
            .setTooltip(defDesc);


        dynamicLabels.get("passive")
            .updateDisplay(formStatColor + passiveDef)
            .setTooltip(defDesc);


        if (dynamicLabels.get("charging") != null) {
            dynamicLabels.get("charging")
                .updateDisplay(formStatColor + chargingDef)
                .setTooltip(defDesc);
        }


        stat = stat(mc.thePlayer, 2, 1, 2, statVals[2], dbcClient.Race, dbcClient.Class, 0);
        incrementVal = statInc(1, 2, 1, Race, JRMCoreH.Class, 0.0F);

        int scaling = getPlayerAttribute(JRMCoreClient.mc.thePlayer, PlyrAttrbts, 2, State, State2, Race, PlyrSkillX, curRelease, getArcRsrv(), StusEfctsMe(14), StusEfctsMe(12), StusEfctsMe(5), StusEfctsMe(13), StusEfctsMe(19), StusEfctsMe(20), 1, PlyrSkills, isFused, getMajinAbsorption());
        double percentile = ((double) (Math.max(scaling, statVals[2])) / statVals[2]);

        int dmgReduction = (int) ((1.0D - 1.0D / percentile) * 100);

        boolean isReductionWorthDisplaying = round(percentile, 1) != 1.0D;
        dynamicLabels.get("body")
            .updateDisplay(numSep(stat) + (isReductionWorthDisplaying ? " R" + dmgReduction + "%" : ""))
            .setTooltip(
                getDescription(
                    attrNms(1, 2),
                    incrementVal,
                    null,
                    null,
                    null,
                    null,
                    (isReductionWorthDisplaying ? dmgReduction : 0),
                    0
                ) +  getFormStatBonus(DBCStats.Body)
            );

        stat = stat(mc.thePlayer, 2, 1, 3, statVals[2], dbcClient.Race, dbcClient.Class, 0);
        incrementVal = statInc(1, 3, 1, Race, JRMCoreH.Class, 0.0F);

        dynamicLabels.get("actionTime")
            .updateDisplay(numSep(stat))
            .setTooltip(
                getDescription(
                    attrNms(1, 2),
                    incrementVal,
                    null,
                    null,
                    null,
                    null,
                    0,
                    0
                ) + getFormStatBonus(DBCStats.Stamina)
            );

        stat = stat(mc.thePlayer, 3, 1, 4, statVals[3], dbcClient.Race, dbcClient.Class, 0);
        incrementVal = statInc(1, 4, 1, Race, JRMCoreH.Class, 0.0F);
        curAtr = (int) (stat * 0.01 * dbcClient.Release);
        dynamicLabels.get("kiPower")
            .updateDisplay(formStatColor + numSep(curAtr))
            .setTooltip(
                getDescription(
                    attrNms(1, 3),
                    incrementVal,
                    numSep(stat),
                    null,
                    null,
                    null,
                    0,
                    0
                ) + getFormStatBonus(DBCStats.EnergyPower)
            );


        stat = stat(mc.thePlayer, 5, 1, 5, statVals[5], dbcClient.Race, dbcClient.Class, SklLvl_KiBs(1));
        incrementVal = statInc(1, 5, 1, Race, JRMCoreH.Class, 0.0F);
        bonusOutput = stat - stat(mc.thePlayer, 5, 1, 5, statVals[5], dbcClient.Race, dbcClient.Class, 0);


        dynamicLabels.get("maxKi")
            .updateDisplay(numSep(stat))
            .setTooltip(
                getDescription(
                    attrNms(1, 5),
                    incrementVal,
                    null,
                    null,
                    null,
                    (bonusOutput > 0 ? numSep(bonusOutput) : null),
                    0,
                    0
                ) + getFormStatBonus(DBCStats.EnergyPool)
            );

        float speedScaling = (customForm == null ? 1 : customForm.mastery.movementSpeed * customForm.mastery.calculateMulti("movementspeed", dbcClient.addonFormLevel));
        int formID = StusEfctsMe(13) ? (rc_sai(Race) ? mstc_sai(SklLvlX(1, PlyrSkillX) - 1) : (rc_arc(Race) ? mstc_arc() : (rc_humNam(Race) ? mstc_humnam() : 1))) : State;
        incrementVal = statInc(1, 7, 100, Race, JRMCoreH.Class, 0.0F) * 0.01F;
        stat = (int) (spdFrm(PlyrAttrbts[1], SklLvl(2, (byte) 1), 100.0F, true, false, formID, State2, incrementVal) * 100.0F * speedScaling);

        int speedReduction = (int) (100.0F - weightPerc(1) * 100.0F);

        String statDesc = String.format(trl("jrmc", "SpDBDesc"), "§2" + stat + "§8", "§c" + attrNms(1, 1) + "§8", "§9" + trl("dbc", "Dash") + "§8");
        if (speedReduction > 0)
            statDesc += "\n" + trl("jrmc", "weightreduction") + ": §c" + speedReduction + "% §7";


        dynamicLabels.get("running")
            .updateDisplay(stat)
            .setTooltip(statDesc);


        incrementVal = statInc(1, 11, 100, Race, JRMCoreH.Class, 0.0F) * 0.01F;
        stat = (int) (spdFrm(PlyrAttrbts[4], SklLvl(3, (byte) 1), 100.0F, true, false, formID, State2, incrementVal) * 100.0F * speedScaling);


        statDesc = String.format(trl("jrmc", "FSDBDesc"), "§2" + stat + "§8", "§c" + attrNms(1, 4) + "§8", "§9" + trl("dbc", "Fly") + "§8");
        if (speedReduction > 0)
            statDesc += "\n" + trl("jrmc", "weightreduction") + ": §c" + speedReduction + "% §7";


        dynamicLabels.get("flying")
            .updateDisplay(stat)
            .setTooltip(statDesc);

    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawAlignmentBar(guiWidthOffset + 8, guiHeightOffset - 6);
        drawStatusEffects(this.width / 4, guiHeightOffset - 40);
        drawDBCLabels(mouseX, mouseY);
    }

    @Override
    public void initGui() {
        super.initGui();

        if (ConfigDBCClient.EnableDebugStatSheetSwitching && !ConfigDBCClient.EnhancedGui) {
            return;
        }
        addServerButtons();

        //Button to adjust GUI

        if (ConfigDBCClient.EnableDebugStatSheetSwitching) {
            String s = (!ConfigDBCClient.EnhancedGui ? "Old" : "§aModern") + " GUI";
            int button1Width = this.fontRendererObj.getStringWidth(s) + 10;
            this.buttonList.add(new JRMCoreGuiButtons00(303030303, guiWidthOffset + 260, height / 2 - 10, button1Width + 8, 20, s, 0));
        }

        //Difficulty button
        GuiInfo.ReferenceIDs ref = GuiInfo.ReferenceIDs.DIFFICULTY;
        String translation = ref.getTranslation();
        int stringWidth = fontRendererObj.getStringWidth(translation) + 15;
        this.buttonList.add(new JRMCoreGuiButtons00(ref.getButtonId(), guiWidthOffset + 260, height / 2 + 55, stringWidth, 20, translation, 0));

        String dark = ConfigDBCClient.DarkMode ? "statsheet.light" : "statsheet.dark";
        dark = StatCollector.translateToLocal(dark);
        stringWidth = fontRendererObj.getStringWidth(dark) + 15;
        this.buttonList.add(new JRMCoreGuiButtons00(404040404, guiWidthOffset + 260, height / 2 + 12, stringWidth, 20, dark, 0));

        String advan = ConfigDBCClient.AdvancedGui ? "statsheet.simple" : "statsheet.advanced";
        advan = StatCollector.translateToLocal(advan);
        stringWidth = fontRendererObj.getStringWidth(advan) + 15;
        GuiButton button4 = new JRMCoreGuiButtons00(505050505, guiWidthOffset + 260, height / 2 + 34, stringWidth, 20, advan, 0);
        this.buttonList.add(button4);
        String hover = StatCollector.translateToLocal("statsheet.tooltip");
        hoverableStaticLabels.add(new JRMCoreLabel(button4, hover));

        int index = 0;

        dynamicLabels.put("level", new JRMCoreLabel(
            trl("jrmc", "Level") + ": " + (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + "%s",
            "%s",
            guiWidthOffset + 6,
            guiHeightOffset + index * 10 + 6
        ));
        index++;

        dynamicLabels.put("tp", new JRMCoreLabel(
            trl("jrmc", "TP") + ": " + (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + "%s",
            trl("jrmc", "TrainingPoints") + "\n" + trl("jrmc", "RequiredTP"),
            guiWidthOffset + 6,
            guiHeightOffset + index * 10 + 6
        ));
        index++;

        dynamicLabels.put("race", new JRMCoreLabel(
            trl("jrmc", "Race") + ": " + (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + "%s",
            null,
            guiWidthOffset + 6,
            guiHeightOffset + index * 10 + 6
        ));
        genderIcon = new GuiIcon(
            icons3,
            0,
            guiHeightOffset + 2 + index * 10 + 1,
            0,
            (dnsGender(dns) < 1 ? 128 : 112),
            16,
            16
        );
        this.hoverableStaticLabels.add(genderIcon);
        index++;
        ;

        customizeFormButton = new GuiIcon.Button(10000, guiWidthOffset + 6, guiHeightOffset + index * 10 + 6 - 3, 12, 12, new ResourceLocation("npcdbc:textures/gui/customizeFormIcon.png"));
        buttonList.add(customizeFormButton);
        customizeFormButton.visible = false;
        dynamicLabels.put("form", new JRMCoreLabel(
            trl("jrmc", "TRState") + ":" + " " + (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + "%s",
            null,
            guiWidthOffset + 6,
            guiHeightOffset + index * 10 + 6

        ));
        index++;

        dynamicLabels.put("class", new JRMCoreLabel(
            trl("jrmc", "Class") + ": " + (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + "%s",
            "%s",
            this.guiWidthOffset + 6,
            this.guiHeightOffset + index * 10 + 6
        ));
        index++;

        dynamicLabels.put("alignment", new JRMCoreLabel(
            trl("jrmc", "Alignment") + ": " + (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + "%s",
            trl("jrmc", "AlignmentDesc"),
            this.guiWidthOffset + 6,
            this.guiHeightOffset + index * 10 + 6
        ));
        index++;

        hoverableStaticLabels.add(new JRMCoreLabel(
            trl("jrmc", "Attributes") + ":",
            "%s",
            this.guiWidthOffset + 6,
            this.guiHeightOffset + index * 10 + 11
        ).updateDisplay());
        index++;

        String[] statNames = new String[]{
            "STR", "DEX", "CON", "WIL", "MND", "SPI"
        };

        for (int i = 0; i < 6; i++) {
            int yPos = guiHeightOffset + i * 10 + index * 10 + 6;

            GuiButton button = new JRMCoreGuiButtonsA3(i, guiWidthOffset + 5, yPos + 3, 10, 2, false);

            dynamicLabels.put("attr_" + i + "_button_desc", new JRMCoreLabel(button, "%s", null));
            upgradeButtons[i] = button;
            dynamicLabels.put("attr_" + i, new JRMCoreLabel(
                (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + LocalizationHelper.getLocalizedString("statsheet.attribute." + statNames[i]) + ": §4%s",
                "%s",
                guiWidthOffset + 17,
                yPos + 5
            ));


        }
        index += 6;
        GuiButton amountButton = new JRMCoreGuiButtonsA3(6, guiWidthOffset + 5, guiHeightOffset + index * 10 + 12, 10, 2, false);
        upgradeButtons[6] = amountButton;
        this.buttonList.addAll(Arrays.asList(upgradeButtons));

        dynamicLabels.put("upgradeAmount", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + "UC: %s",
            "%s",
            guiWidthOffset + 17,
            guiHeightOffset + index * 10 + 14
        ).setDisplay("Testing"));

        index = 0;
        this.dynamicLabels.put("release", new JRMCoreLabel(
            trl("jrmc", "PowerRelease") + ": " + (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + "%s%%",
            null,
            guiWidthOffset + 133,
            guiHeightOffset + index * 10 + 6
        ));
        index++;

        this.hoverableStaticLabels.add(new JRMCoreLabel(
            null,
            null,
            guiWidthOffset + 133,
            guiHeightOffset + index * 10 + 11
        ).setDisplay(trl("jrmc", "Stats")));
        index++;


        this.dynamicLabels.put("melee", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + trl("jrmc", "mleDB") + ": §4%s",
            trl("jrmc", "StatIncreaseDesc") + trl("jrmc", "StatIncreaseDesc2"),
            guiWidthOffset + 133,
            guiHeightOffset + index * 10 + 11
        ));
        index++;

        this.dynamicLabels.put("defense", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + trl("jrmc", "DefDB") + ": §4%s",
            "%s",
            guiWidthOffset + 133,
            guiHeightOffset + index * 10 + 11
        ));
        index++;

        this.dynamicLabels.put("passive", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + trl("jrmc", "Passive") + ": §4%s",
            "%s",
            guiWidthOffset + 138,
            guiHeightOffset + index * 10 + 11
        ));
        index++;

        if (ClientCache.hasChargingDex) {
            this.dynamicLabels.put("charging", new JRMCoreLabel(
                (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + LocalizationHelper.getLocalizedString("statsheet.stat.charging") + ": §4%s",
                "%s",
                guiWidthOffset + 138,
                guiHeightOffset + index * 10 + 11
            ));
            index++;
        }

        this.dynamicLabels.put("body", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + trl("jrmc", "BdDB") + ": §4%s",
            "%s",
            guiWidthOffset + 133,
            guiHeightOffset + index * 10 + 11
        ));
        index++;

        this.dynamicLabels.put("actionTime", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + trl("jrmc", "StDB") + ": §4%s",
            "%s",
            guiWidthOffset + 133,
            guiHeightOffset + index * 10 + 11
        ));
        index++;

        this.dynamicLabels.put("kiPower", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + trl("jrmc", "EnPwDB") + ": §4%s",
            "%s",
            guiWidthOffset + 133,
            guiHeightOffset + index * 10 + 11
        ));
        index++;

        this.dynamicLabels.put("maxKi", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + trl("jrmc", "EnPlDB") + ": §4%s",
            "%s",
            guiWidthOffset + 133,
            guiHeightOffset + index * 10 + 11
        ));
        index++;
        this.dynamicLabels.put("running", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + trl("jrmc", "SpDB") + ": §4%s%%",
            "%s",
            guiWidthOffset + 133,
            guiHeightOffset + index * 10 + 11
        ));
        index++;
        this.dynamicLabels.put("flying", new JRMCoreLabel(
            (ConfigDBCClient.DarkMode ? DARKMODE_ACCENT : "") + trl("jrmc", "FSDB") + ": §4%s%%",
            "%s",
            guiWidthOffset + 133,
            guiHeightOffset + index * 10 + 11
        ));
        index++;


        if (JRMCoreEH.dt) {
            String name = "Update vanity";
            int width = this.fontRendererObj.getStringWidth(name);
            UPDATE_VANITY_BUTTON = new JRMCoreGuiButtons00(100, guiWidthOffset - 78, guiHeightOffset, width + 8, 20, name, 0);
            buttonList.add(UPDATE_VANITY_BUTTON);

            name = (JRMCoreEH.gk ? "Hide" : "Show") + " own vanity";
            width = this.fontRendererObj.getStringWidth(name);
            buttonList.add(new JRMCoreGuiButtons00(101, guiWidthOffset - 88, guiHeightOffset + 21, width + 8, 20, name, 0));
        }

        updateScreen();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        int id = button.id;
        if (id == 303030303) {
            ConfigDBCClient.EnhancedGui = false;
            ConfigDBCClient.EnhancedGuiProperty.set(false);
            ConfigDBCClient.config.save();
        }
        if (id == 404040404) {
            ConfigDBCClient.DarkMode = !ConfigDBCClient.DarkMode;
            ConfigDBCClient.DarkModeProperty.set(ConfigDBCClient.DarkMode);
            ConfigDBCClient.config.save();
            initGui();
        }
        if (id == 505050505) {
            ConfigDBCClient.AdvancedGui = !ConfigDBCClient.AdvancedGui;
            ConfigDBCClient.AdvancedGuiModeProperty.set(ConfigDBCClient.AdvancedGui);
            ConfigDBCClient.config.save();
            initGui();
        }
        if (id == 707070707) {
            mc.displayGuiScreen(new GuiConfirmOpenLink(this, ConfigDBCGeneral.getDiscordURL(), 0, true));
        }
        if (id >= 0 && id <= 5) {
            if (!isFused()) {
                Upg((byte) (id + upgradeCounter * 6));
            }
        }
        if (id == 6) {
            this.upgradeCounter++;
            if (this.upgradeCounter > 3)
                this.upgradeCounter = 0;
        }

        if (id == 100) {
            JRMCoreEH.aw = true;
            VANITY_TIMER = 0;
            button.enabled = false;
        }
        if (id == 101) {
            JRMCoreEH.gk = !JRMCoreEH.gk;
            String name = (JRMCoreEH.gk ? "Hide" : "Show") + " own vanity";
            button.displayString = name;
            button.width = fontRendererObj.getStringWidth(name) + 8;
        }

        if (id == 10000) {
            NoppesUtil.openGUI(mc.thePlayer, new GuiFormCustomizer(currentForm));
        }

    }

    protected void drawAlignmentBar(int x, int y) {
        mc.getTextureManager().bindTexture(icons);
        GL11.glPushMatrix();
        int alignment1;
        int alignment2;

        if (Algnmnt_Good(align)) {
            alignment1 = 654591;
            alignment2 = 6028287;
        } else if (Algnmnt_Neut(align)) {
            alignment1 = 9127101;
            alignment2 = 11042302;
        } else {
            alignment1 = 16726090;
            alignment2 = 16544131;
        }

        float h2 = (float) (alignment1 >> 16 & 255) / 255.0F;
        float h3 = (float) (alignment1 >> 8 & 255) / 255.0F;
        float h4 = (float) (alignment1 & 255) / 255.0F;
        float h1 = 1.0F;
        GL11.glColor4f(h1 * h2, h1 * h3, h1 * h4, 0.5F);
        this.drawTexturedModalRect(x, y, 8, 174, 241, 7);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
        this.drawTexturedModalRect(x - 8, y + 1, 0, 169, menuImageWidth, 5);
        int max = menuImageWidth - 20;
        if (max < 1) {
            max = 1;
        }

        double maxperc = (double) max * 0.01D;
        int alignmentAdjusted = (int) (maxperc * (double) align);
        if (alignmentAdjusted > menuImageWidth) {
            alignmentAdjusted = menuImageWidth;
        }

        h2 = (float) (alignment2 >> 16 & 255) / 255.0F;
        h3 = (float) (alignment2 >> 8 & 255) / 255.0F;
        h4 = (float) (alignment2 & 255) / 255.0F;
        GL11.glColor4f(h1 * h2, h1 * h3, h1 * h4, 1.0F);
        this.drawTexturedModalRect((this.width - 5) / 2 - max / 2 + alignmentAdjusted - 4, guiHeightOffset - 9, 0, 182, 11, 13);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected void drawStatusEffects(int x, int y) {
        JRMCoreClient.bars.showSE(x, y, 0, 0);
    }

    public String getAttributeBonusDescription(int attributeID) {
        if (JRMCoreConfig.JRMCABonusOn) {
            String description = "\nBonus Attributes:";
            String[] bonuses = getBonusAttributes(attributeID).split("\\|");
            String[] var5 = bonuses;
            int var6 = bonuses.length;

            for (int var7 = 0; var7 < var6; ++var7) {
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

    public String getFormStatBonus(int statID) {
        DBCData dbcData = DBCData.get(Minecraft.getMinecraft().thePlayer);
        Form form = dbcData.getForm();
        if(form != null && form.stats.isStatEnabled(statID)){
            String description = "\n";
            //  + trl("jrmc", "Modified") + ": §4" + darkFormColor + statDisplay + "\n§8"
            //                + trl("jrmc", "Original") + ": §4" + numSep(originalStatVal) + "§8";
            if(ConfigDBCClient.AdvancedGui){
                description += "\n§8" + Utility.removeColorCodes(form.getMenuName()) + ":";
                description += "§8\n> ";
                int bonus = form.stats.getStat(statID).getBonus();
                String sign = bonus > 0 ? "§2+" : "§4";
                description += sign + bonus;

                description += "§8\n> ";
                float multi = form.stats.getStat(statID).getMultiplier();
                String multiplier = String.format("%.2f", multi);

                if(multi > 1.0f)
                    description += "§2";
                else
                    description += "§4";
                description += "x" + multiplier;
            }
            return description;
        }
        return "";
    }

    public String getAddonBonus(int attributeID) {
        String description = "";
        DBCData dbcData = DBCData.get(Minecraft.getMinecraft().thePlayer);
        if (!dbcData.bonus.getCurrentBonuses().isEmpty()) {
            description += "\nBonus Stats:";
            for (PlayerBonus playerBonus : dbcData.bonus.getCurrentBonuses().values()) {
                if (attributeID == DBCAttribute.Strength && playerBonus.strength != 0) {
                    description += "\n>> " + playerBonus.name + ": " + (playerBonus.type == 1 ? " " : "x ") + playerBonus.strength;
                } else if (attributeID == DBCAttribute.Dexterity && playerBonus.dexterity != 0) {
                    description += "\n>> " + playerBonus.name + ": " + (playerBonus.type == 1 ? " " : "x ") + playerBonus.dexterity;
                } else if (attributeID == DBCAttribute.Constitution && playerBonus.constituion != 0) {
                    description += "\n>> " + playerBonus.name + ": " + (playerBonus.type == 1 ? " " : "x ") + playerBonus.constituion;
                } else if (attributeID == DBCAttribute.Willpower && playerBonus.willpower != 0) {
                    description += "\n>> " + playerBonus.name + ": " + (playerBonus.type == 1 ? " " : "x ") + playerBonus.willpower;
                } else if (attributeID == DBCAttribute.Spirit && playerBonus.spirit != 0) {
                    description += "\n>> " + playerBonus.name + ": " + (playerBonus.type == 1 ? " " : "x ") + playerBonus.spirit;
                }
            }
        }
        return description;
    }

    public long getAddonBonusStat(int attributeID) {
        DBCData dbcData = DBCData.get(Minecraft.getMinecraft().thePlayer);
        long extra = 0;
        if (!dbcData.bonus.getCurrentBonuses().isEmpty()) {
            for (PlayerBonus playerBonus : dbcData.bonus.getCurrentBonuses().values()) {
                if (playerBonus.type == 0)
                    continue;

                if (attributeID == DBCAttribute.Strength && playerBonus.strength != 0) {
                    extra += (long) playerBonus.strength;
                } else if (attributeID == DBCAttribute.Dexterity && playerBonus.dexterity != 0) {
                    extra += (long) playerBonus.dexterity;
                } else if (attributeID == DBCAttribute.Constitution && playerBonus.constituion != 0) {
                    extra += (long) playerBonus.constituion;
                } else if (attributeID == DBCAttribute.Spirit && playerBonus.spirit != 0) {
                    extra += (long) playerBonus.spirit;
                } else if (attributeID == DBCAttribute.Willpower && playerBonus.willpower != 0) {
                    extra += (long) playerBonus.willpower;
                }
            }
        }
        return extra;
    }

    public float getAddonBonusMulti(int attributeID) {
        DBCData dbcData = DBCData.get(Minecraft.getMinecraft().thePlayer);
        float extra = 0;
        if (!dbcData.bonus.getCurrentBonuses().isEmpty()) {
            for (PlayerBonus playerBonus : dbcData.bonus.getCurrentBonuses().values()) {
                if (playerBonus.type == 1)
                    continue;

                if (attributeID == DBCAttribute.Strength && playerBonus.strength != 0) {
                    extra += playerBonus.strength;
                } else if (attributeID == DBCAttribute.Dexterity && playerBonus.dexterity != 0) {
                    extra += playerBonus.dexterity;
                } else if (attributeID == DBCAttribute.Constitution && playerBonus.constituion != 0) {
                    extra += playerBonus.constituion;
                } else if (attributeID == DBCAttribute.Spirit && playerBonus.spirit != 0) {
                    extra += playerBonus.spirit;
                } else if (attributeID == DBCAttribute.Willpower && playerBonus.willpower != 0) {
                    extra += playerBonus.willpower;
                }
            }
        }
        return extra;
    }

    public double DBCFormMulti(int atr) {
        switch (Race) {
            case 0:
                return ev_oob(TransHmStBnP, State, atr);
            case 1:
                return ev_oob(TransSaiStBnP, State, atr);
            case 2:
                return ev_oob(TransHalfSaiStBnP, State, atr);
            case 3:
                return ev_oob(TransNaStBnP, State, atr);
            case 4:
                return ev_oob(TransFrStBnP, State, atr);
            case 5:
                return ev_oob(TransMaStBnP, State, atr);
        }
        return 0;
    }

    public double DBCFormFlat(int atr) {
        switch (Race) {
            case 0:
                return ev_oob(TransHmStBnF, State, atr);
            case 1:
                return ev_oob(TransSaiStBnF, State, atr);
            case 2:
                return ev_oob(TransHalfSaiStBnF, State, atr);
            case 3:
                return ev_oob(TransNaStBnF, State, atr);
            case 4:
                return ev_oob(TransFrStBnF, State, atr);
            case 5:
                return ev_oob(TransMaStBnF, State, atr);
        }
        return 0;
    }

    public double getFormMasteryMulti() {
        PlayerDBCInfo dataClient = PlayerDataUtil.getClientDBCInfo();
        if (dataClient != null && dataClient.getCurrentForm() != null) {
            Form form = dataClient.getCurrentForm();
            return form.mastery.calculateMulti("attribute", dataClient.formLevels.getOrDefault(form.id, 0f));
        }
        return getFormMasteryAttributeMulti(JRMCoreClient.mc.thePlayer, State, State2, Race, StusEfctsMe(5), StusEfctsMe(13), StusEfctsMe(19), StusEfctsMe(20));
    }

    public void confirmClicked(boolean flag, int i) {
        if (flag) {
            if (i == 0) {
                String link = ConfigDBCGeneral.getDiscordURL();
                try {
                    Class oclass = java.lang.Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object) null, new Object[0]);
                    oclass.getMethod("browse", new Class[]{URI.class}).invoke(object, new Object[]{new URI(link)});
                } catch (Throwable ignored) {
                }
            }
        }

        mc.displayGuiScreen(this);
    }
}
