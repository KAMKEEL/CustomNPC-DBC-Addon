package kamkeel.npcdbc.client.gui.dbc.creator;

import JinRyuu.JRMCore.*;
import kamkeel.npcdbc.data.race.helper.RaceSelectorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public final class PowerTypePage extends CreatorPage {

    private static final int POWER_PREV = 200, POWER_NEXT = 201;
    private static final int CLASS_PREV = 202, CLASS_NEXT = 203;
    private static final int AURA_COLOR_BTN = 204;

    private int guiLeft, guiTop;

    PowerTypePage(CharacterCreationGui parent, CreatorSession session, VanillaCreatorBridge bridge) {
        super(parent, session, bridge);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void initPage(List buttonList, int guiLeft, int guiTop) {
        this.guiLeft = guiLeft;
        this.guiTop = guiTop;

        enforceConstraints();

        int row = 1;
        int controlX = guiLeft + 130 - 125;
        int arrowRight = guiLeft + 240 - 125;

        // Power type
        boolean canPwr = canPowerType();
        if (canPwr) {
            boolean showButtons = JRMCoreH.DBC() || JRMCoreH.NC() || JRMCoreH.SAOC();
            if (showButtons) {
                buttonList.add(new JRMCoreGuiButtonsA2(POWER_PREV, controlX, guiTop + 4 + row * 10, "<"));
                buttonList.add(new JRMCoreGuiButtonsA2(POWER_NEXT, arrowRight, guiTop + 4 + row * 10, ">"));
            }
        }
        row++;

        // Class
        if (canClass()) {
            buttonList.add(new JRMCoreGuiButtonsA2(CLASS_PREV, controlX, guiTop + 4 + row * 10, "<"));
            buttonList.add(new JRMCoreGuiButtonsA2(CLASS_NEXT, arrowRight, guiTop + 4 + row * 10, ">"));
        }
        row++;

        // Aura color (Ki power type only)
        if (session.powerType == 1) {
            String auraLabel = JRMCoreH.trl("jrmc", "AuraColor");
            int sw = Minecraft.getMinecraft().fontRenderer.getStringWidth(auraLabel) / 2;
            buttonList.add(new JRMCoreGuiButtons01(AURA_COLOR_BTN, guiLeft + 64 - sw, guiTop + 5 + row * 10, sw,
                auraLabel, JRMCoreH.techNCCol[1]).setShadow(false));
        }
    }

    @Override
    public void drawPage(int mouseX, int mouseY, float partialTicks) {
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        int statRace = RaceSelectorHelper.clampRaceForStats(session.raceIndex);

        font.drawString(JRMCoreH.trl("jrmc", "PowerType"), guiLeft + 5, guiTop + 5, 0);

        int row = 1;
        int labelCenterX = guiLeft + 64;

        // Power type label
        if (canPowerType()) {
            String pwrLabel = JRMCoreH.trl("jrmc", JRMCoreH.Pwrtyps[session.powerType]);
            drawCentered(font, pwrLabel, labelCenterX, guiTop + 5 + row * 10);
        }
        row++;

        // Class label
        if (canClass()) {
            String classLabel = JRMCoreH.trl("jrmc", getClassNames()[session.classType]);
            drawCentered(font, classLabel, labelCenterX, guiTop + 5 + row * 10);
        }
        row++;

        // Aura color swatch
        if (session.powerType == 1) {
            int col = session.kiColor > 0 ? session.kiColor : 11075583;
            drawColorSwatch(col, guiLeft + 64 - 50 + 1, guiTop + 4 + row * 10, 100, 10);
        }

        // Stats section
        row++;
        font.drawString(JRMCoreH.trl("jrmc", "Stats"), guiLeft + 5, guiTop + 5 + row * 10, 0);
        font.drawString(JRMCoreH.trl("jrmc", "StartAttr"), guiLeft + 5 + 128, guiTop + 5 + row * 10, 0);
        row++;

        drawStatPreview(font, row, statRace);
    }

    private void drawStatPreview(FontRenderer font, int startRow, int statRace) {
        int row = startRow;
        int pwr = session.powerType;
        int cls = session.classType;

        int STR = JRMCoreH.attributeStart(pwr, 0, statRace, cls);
        int DEX = JRMCoreH.attributeStart(pwr, 1, statRace, cls);
        int CON = JRMCoreH.attributeStart(pwr, 2, statRace, cls);
        int WIL = JRMCoreH.attributeStart(pwr, 3, statRace, cls);
        int MND = JRMCoreH.attributeStart(pwr, 4, statRace, cls);
        int SPI = JRMCoreH.attributeStart(pwr, 5, statRace, cls);

        if (pwr == 1 || pwr == 2) {
            String[] attrNames = {"STR", "DEX", "CON", "WIL", "MND", "SPI"};
            int[] attrStart = {STR, DEX, CON, WIL, MND, SPI};
            int[] statTypes = {0, 1, 2, 2, 3, 5};

            for (int i = 0; i < 6; i++) {
                int stat = JRMCoreH.stat(Minecraft.getMinecraft().thePlayer, statTypes[i], pwr, i, attrStart[i], statRace, cls, 0.0F);
                float inc = JRMCoreH.statInc(pwr, i, 1, statRace, cls, 0.0F);
                String statName = JRMCoreH.trl("jrmc", JRMCoreH.attrStat[pwr][i]);
                String statText = JRMCoreH.cldgy + statName + ": " + JRMCoreH.cldr + stat;
                font.drawString(statText, guiLeft + 5, guiTop + 5 + row * 10, 0);

                String attrText = JRMCoreH.cldgy + attrNames[i] + ": " + JRMCoreH.cldr + attrStart[i];
                font.drawString(attrText, guiLeft + 5 + 128, guiTop + 5 + row * 10, 0);
                row++;
            }
        } else if (pwr == 3) {
            int stat = JRMCoreH.stat(Minecraft.getMinecraft().thePlayer, 0, pwr, 0, STR, statRace, cls, 0.0F);
            String statName = JRMCoreH.trl("saoc", JRMCoreH.attrStat[pwr][0]);
            font.drawString(JRMCoreH.cldgy + statName + ": " + JRMCoreH.cldr + stat, guiLeft + 5, guiTop + 5 + row * 10, 0);
            font.drawString(JRMCoreH.cldgy + "STR: " + JRMCoreH.cldr + STR, guiLeft + 5 + 128, guiTop + 5 + row * 10, 0);
            row++;
            stat = JRMCoreH.stat(Minecraft.getMinecraft().thePlayer, 2, pwr, 2, CON, statRace, cls, 0.0F);
            statName = JRMCoreH.trl("saoc", JRMCoreH.attrStat[pwr][2]);
            font.drawString(JRMCoreH.cldgy + statName + ": " + JRMCoreH.cldr + stat, guiLeft + 5, guiTop + 5 + row * 10, 0);
            font.drawString(JRMCoreH.cldgy + "AGI: " + JRMCoreH.cldr + DEX, guiLeft + 5 + 128, guiTop + 5 + row * 10, 0);
            row++;
            font.drawString(JRMCoreH.cldgy + "VIT: " + JRMCoreH.cldr + CON, guiLeft + 5 + 128, guiTop + 5 + row * 10, 0);
        } else {
            int stat = JRMCoreH.stat(Minecraft.getMinecraft().thePlayer, 0, pwr, 0, STR, statRace, cls, 0.0F);
            String statName = JRMCoreH.trl("jrmc", JRMCoreH.attrStat[pwr][0]);
            font.drawString(JRMCoreH.cldgy + statName + ": " + JRMCoreH.cldr + stat, guiLeft + 5, guiTop + 5 + row * 10, 0);
            font.drawString(JRMCoreH.cldgy + "STR: " + JRMCoreH.cldr + STR, guiLeft + 5 + 128, guiTop + 5 + row * 10, 0);
            row++;
            stat = JRMCoreH.stat(Minecraft.getMinecraft().thePlayer, 2, pwr, 2, CON, statRace, cls, 0.0F);
            statName = JRMCoreH.trl("jrmc", JRMCoreH.attrStat[pwr][2]);
            font.drawString(JRMCoreH.cldgy + statName + ": " + JRMCoreH.cldr + stat, guiLeft + 5, guiTop + 5 + row * 10, 0);
            font.drawString(JRMCoreH.cldgy + "CON: " + JRMCoreH.cldr + CON, guiLeft + 5 + 128, guiTop + 5 + row * 10, 0);
        }
    }

    @Override
    public boolean actionPerformed(GuiButton button) {
        switch (button.id) {
            case POWER_NEXT: cyclePowerType(true); return true;
            case POWER_PREV: cyclePowerType(false); return true;
            case CLASS_NEXT: cycleClass(true); return true;
            case CLASS_PREV: cycleClass(false); return true;
            case AURA_COLOR_BTN:
                bridge.applyPreview();
                parent.openVanillaColorPicker(5015);
                return true;
            default: return false;
        }
    }

    @Override
    public void onPageEnter() {
        enforceConstraints();
        bridge.applyPreview();
    }

    private void enforceConstraints() {
        String[] raceCanHavePwr = RaceSelectorHelper.getRaceCanHavePwr();

        if (!JRMCoreH.Allow(JRMCoreH.PwrtypAllow[session.powerType])) {
            session.powerType = 0;
        }

        if (session.powerType == 3 || !JRMCoreH.Allow(JRMCoreH.PwrtypAllow[session.powerType])) {
            session.classType = 0;
        }

        String[] classes = getClassNames();
        if (classes.length - 1 < session.classType) {
            session.classType = 0;
        }

        if (session.raceIndex < raceCanHavePwr.length
            && !raceCanHavePwr[session.raceIndex].contains("" + session.powerType)) {
            session.syncToVanillaStatics();
            session.powerType = JRMCoreGuiScreen.Slct("B", session.powerType, JRMCoreH.PwrtypAllow);
        }
    }

    private boolean canPowerType() {
        return JRMCoreH.Allow(JRMCoreH.PwrtypAllow[session.powerType]);
    }

    private boolean canClass() {
        return canPowerType() && session.powerType != 3;
    }

    private String[] getClassNames() {
        return JRMCoreH.cl(session.powerType);
    }

    private void cyclePowerType(boolean forward) {
        session.syncToVanillaStatics();
        if (forward) {
            session.powerType = JRMCoreGuiScreen.Slct("F", session.powerType, JRMCoreH.PwrtypAllow);
        } else {
            session.powerType = JRMCoreGuiScreen.Slct("B", session.powerType, JRMCoreH.PwrtypAllow);
        }
        enforceConstraints();
        syncAndRefresh();
    }

    private void cycleClass(boolean forward) {
        String[] classes = getClassNames();
        session.syncToVanillaStatics();
        if (forward) {
            session.classType = JRMCoreGuiScreen.Slct2("F", session.classType, JRMCoreH.PwrtypAllow, session.powerType, classes);
        } else {
            session.classType = JRMCoreGuiScreen.Slct2("B", session.classType, JRMCoreH.PwrtypAllow, session.powerType, classes);
        }
        syncAndRefresh();
    }

    private void drawCentered(FontRenderer font, String text, int centerX, int y) {
        font.drawString(text, centerX - font.getStringWidth(text) / 2, y, 0);
    }

    private void drawColorSwatch(int color, int x, int y, int w, int h) {
        float r = (float)(color >> 16 & 0xFF) / 255.0F;
        float g = (float)(color >> 8 & 0xFF) / 255.0F;
        float b = (float)(color & 0xFF) / 255.0F;
        GL11.glColor4f(r, g, b, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(JRMCoreGuiScreen.button1));
        parent.drawTexturedModalRect(x, y, 0, 0, w, h);
    }

    private void syncAndRefresh() {
        bridge.applyPreview();
        parent.refreshPage();
    }
}
