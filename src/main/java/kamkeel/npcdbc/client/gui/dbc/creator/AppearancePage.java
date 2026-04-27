package kamkeel.npcdbc.client.gui.dbc.creator;

import JinRyuu.JRMCore.*;
import kamkeel.npcdbc.client.gui.dbc.EntityPreviewRenderer;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.helper.RaceSelectorHelper;
import kamkeel.npcdbc.data.race.properties.RaceProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Note on breast-size slider (ID {@link #BUST_SLIDER}):
 * <p>
 * {@link JRMCoreGuiSlider01} fires {@code actionPerformed} on mouse-press only.
 * During dragging, the slider updates its {@code sliderValue} internally in
 * {@code mouseDragged} (called from {@code drawButton}), but no second
 * {@code actionPerformed} fires on release. To keep the session state and
 * preview in sync with continuous drag, {@link #drawPage} polls the slider
 * value each frame while dragging is active and pushes updates through
 * {@link #syncAndRefresh()}.
 */

public final class AppearancePage extends CreatorPage {

    // Button IDs — namespaced to avoid collision with nav buttons (900-902)
    private static final int RACE_PREV = 100, RACE_NEXT = 101;
    private static final int GENDER_PREV = 102, GENDER_NEXT = 103;
    private static final int HAIR_PREV = 104, HAIR_NEXT = 105;
    private static final int CUSTOM_HAIR_BTN = 5100;
    private static final int COLOR_BTN = 106;
    private static final int YEARS_PREV = 107, YEARS_NEXT = 108;
    private static final int TAIL_BTN = 127;
    private static final int SKIN_PREV = 110, SKIN_NEXT = 111;
    private static final int BODY_PREV = 112, BODY_NEXT = 113;
    private static final int BODYCOL_PRESET_PREV = 114, BODYCOL_PRESET_NEXT = 115;
    private static final int NOSE_PREV = 116, NOSE_NEXT = 117;
    private static final int MOUTH_PREV = 118, MOUTH_NEXT = 119;
    private static final int EYES_PREV = 120, EYES_NEXT = 121;
    private static final int EYECOL_PRESET_PREV = 122, EYECOL_PRESET_NEXT = 123;
    private static final int EYE_MATCH = 124;
    private static final int STATE_PREV = 125, STATE_NEXT = 126;
    private static final int BODYCOL_MAIN = 130, BODYCOL_SUB1 = 131, BODYCOL_SUB2 = 132, BODYCOL_SUB3 = 133;
    private static final int EYECOL1 = 134, EYECOL2 = 135;
    private static final int BUST_SLIDER = 5001;
    private static final int PROP_PREV  = 140; // cycle to previous property
    private static final int PROP_NEXT  = 141; // cycle to next property
    private static final int PROP_VAL_PREV = 142; // decrease property value
    private static final int PROP_VAL_NEXT = 143; // increase property value

    private int guiLeft, guiTop;
    private JRMCoreGuiSlider01 bustSlider;

    private int lastMouseX, lastMouseY;
    private final EntityPreviewRenderer previewRenderer = new EntityPreviewRenderer()
        .setDefaultZoom(2.5f)
        .setZoomBounds(1f, 5f)
        .setFollowMouse(true)
        .setAllowRotate(true);

    AppearancePage(CharacterCreationGui parent, CreatorSession session, VanillaCreatorBridge bridge) {
        super(parent, session, bridge);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void initPage(List buttonList, int guiLeft, int guiTop) {
        this.guiLeft = guiLeft;
        this.guiTop = guiTop;

        CreatorSession.AppearanceFlags flags = session.resolveAppearanceFlags();
        enforceConstraints(flags);

        int row = 0;
        int controlX = guiLeft + 130;
        int labelCenterX = guiLeft + 190;
        int arrowRight = guiLeft + 240;

        // Race
        if (flags.canRace) {
            buttonList.add(new JRMCoreGuiButtonsA2(RACE_PREV, controlX, guiTop + 5 + row * 10, "<"));
            buttonList.add(new JRMCoreGuiButtonsA2(RACE_NEXT, arrowRight, guiTop + 5 + row * 10, ">"));
        }
        row++;

        // Gender
        if (flags.canGender) {
            buttonList.add(new JRMCoreGuiButtonsA2(GENDER_PREV, controlX, guiTop + 5 + row * 10, "<"));
            buttonList.add(new JRMCoreGuiButtonsA2(GENDER_NEXT, arrowRight, guiTop + 5 + row * 10, ">"));
        }
        row++;

        // Hair
        if (flags.canHair) {
            buttonList.add(new JRMCoreGuiButtonsA2(HAIR_PREV, controlX, guiTop + 5 + row * 10, "<"));
            buttonList.add(new JRMCoreGuiButtonsA2(HAIR_NEXT, arrowRight, guiTop + 5 + row * 10, ">"));
            if (session.hairBack == 12) {
                String customHairLabel = JRMCoreH.trl("jrmc", "CustomHair");
                int sw = Minecraft.getMinecraft().fontRenderer.getStringWidth(customHairLabel) / 2;
                buttonList.add(new JRMCoreGuiButtons01(CUSTOM_HAIR_BTN, labelCenterX - sw, guiTop + 5 + row * 10, sw,
                    customHairLabel, JRMCoreH.techNCCol[1]).setShadow(false));
            }
        }
        row++;

        // Hair color / Arcosian TRState row
        if (flags.canColor) {
            String colorLabel = JRMCoreH.trl("jrmc", "Color");
            int sw = Minecraft.getMinecraft().fontRenderer.getStringWidth(colorLabel) / 2;
            buttonList.add(new JRMCoreGuiButtons01(COLOR_BTN, labelCenterX - sw, guiTop + 5 + row * 10, sw,
                colorLabel, JRMCoreH.techNCCol[1]).setShadow(false));
        } else if (session.getVanillaRaceIndex() == 4) {
            buttonList.add(new JRMCoreGuiButtonsA2(STATE_PREV, controlX, guiTop + 4 + row * 10, "<"));
            buttonList.add(new JRMCoreGuiButtonsA2(STATE_NEXT, arrowRight, guiTop + 4 + row * 10, ">"));
        }
        row++;

        // Breast size slider (female only — matches vanilla JRMCoreGuiScreen behavior)
        bustSlider = null;
        if (session.gender == 1) {
            bustSlider = new JRMCoreGuiSlider01(BUST_SLIDER,
                labelCenterX - 25, guiTop + 4 + row * 10,
                50, 10, "", (float) session.breastSize * 0.1F, 1.0F);
            buttonList.add(bustSlider);
        }
        row++;

        // Years
        if (flags.canYears) {
            buttonList.add(new JRMCoreGuiButtonsA2(YEARS_PREV, controlX, guiTop + 5 + row * 10, "<"));
            buttonList.add(new JRMCoreGuiButtonsA2(YEARS_NEXT, arrowRight, guiTop + 5 + row * 10, ">"));
        }
        row++;

        // Tail
        if (flags.canTail) {
            String tailLabel = JRMCoreH.trl("jrmc", "Tail") + " " +
                (session.tail ? JRMCoreH.trl("jrmc", "Enabled") : JRMCoreH.trl("jrmc", "Disabled"));
            int sw = Minecraft.getMinecraft().fontRenderer.getStringWidth(tailLabel) / 2;
            buttonList.add(new JRMCoreGuiButtons01(TAIL_BTN, labelCenterX - sw, guiTop + 5 + row * 10, sw,
                tailLabel, session.tail ? 3452672 : 4210752).setShadow(false));
        }

        List<RaceProperty<?>> props = session.getAvailableProperties();
        if (!props.isEmpty()) {
            // Property selector row
            if (props.size() > 1) {
                buttonList.add(new JRMCoreGuiButtonsA2(PROP_PREV, controlX, guiTop + 5 + row * 10, "<"));
                buttonList.add(new JRMCoreGuiButtonsA2(PROP_NEXT, arrowRight, guiTop + 5 + row * 10, ">"));
            }
            row++;

            // Property value row
            RaceProperty<?> currentProp = props.get(session.selectedPropertyIndex);
            Object raw = session.racePropertyValues.getOrDefault(currentProp.key, currentProp.getDefault());

            switch (currentProp.getButtonType()) {
                case TOGGLE:
                    boolean value = Boolean.TRUE.equals(raw);
                    String label = currentProp.toString(raw);
                    int sw = Minecraft.getMinecraft().fontRenderer.getStringWidth(label) / 2;
                    buttonList.add(new JRMCoreGuiButtons01(PROP_VAL_NEXT, labelCenterX - sw, guiTop + 5 + row * 10, sw,
                        label, value ? 3452672 : 4210752).setShadow(false));
                    break;
                case ARROW:
                default:
                    buttonList.add(new JRMCoreGuiButtonsA2(PROP_VAL_PREV, controlX, guiTop + 5 + row * 10, "<"));
                    buttonList.add(new JRMCoreGuiButtonsA2(PROP_VAL_NEXT, arrowRight, guiTop + 5 + row * 10, ">"));
                    break;
            }

            row++;
        }

        if(session.getVanillaRaceIndex() == DBCRace.SAIYAN && !session.tail) {
            session.tail = true;
            syncAndRefreshTail();
        }
        row++;

        // Skin type
        if (flags.canCustomSkin) {
            buttonList.add(new JRMCoreGuiButtonsA2(SKIN_PREV, controlX, guiTop + 5 + row * 10, "<"));
            buttonList.add(new JRMCoreGuiButtonsA2(SKIN_NEXT, arrowRight, guiTop + 5 + row * 10, ">"));
        }
        row++;

        // Custom skin detail controls (only when skinType == 1)
        if (session.skinType == 1) {
            initCustomSkinControls(buttonList, guiLeft, guiTop, row, flags);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void initCustomSkinControls(List buttonList, int guiLeft, int guiTop, int row, CreatorSession.AppearanceFlags flags) {
        int controlX = guiLeft + 130;
        int arrowRight = guiLeft + 240;
        // Body type
        buttonList.add(new JRMCoreGuiButtonsA2(BODY_PREV, controlX, guiTop + 5 + row * 10, "<"));
        buttonList.add(new JRMCoreGuiButtonsA2(BODY_NEXT, arrowRight, guiTop + 5 + row * 10, ">"));
        row++;

        // Body color preset
        buttonList.add(new JRMCoreGuiButtonsA2(BODYCOL_PRESET_PREV, controlX, guiTop + 5 + row * 10, "<"));
        buttonList.add(new JRMCoreGuiButtonsA2(BODYCOL_PRESET_NEXT, arrowRight, guiTop + 5 + row * 10, ">"));

        addColorSwatches(buttonList, row, flags.bodyColorSlots,
            new int[]{BODYCOL_MAIN, BODYCOL_SUB1, BODYCOL_SUB2, BODYCOL_SUB3},
            new int[]{session.bodyColMain, session.bodyColSub1, session.bodyColSub2, session.bodyColSub3});
        row++;

        // Nose
        buttonList.add(new JRMCoreGuiButtonsA2(NOSE_PREV, controlX, guiTop + 5 + row * 10, "<"));
        buttonList.add(new JRMCoreGuiButtonsA2(NOSE_NEXT, arrowRight, guiTop + 5 + row * 10, ">"));
        row++;

        // Mouth
        buttonList.add(new JRMCoreGuiButtonsA2(MOUTH_PREV, controlX, guiTop + 5 + row * 10, "<"));
        buttonList.add(new JRMCoreGuiButtonsA2(MOUTH_NEXT, arrowRight, guiTop + 5 + row * 10, ">"));
        row++;

        // Eyes
        buttonList.add(new JRMCoreGuiButtonsA2(EYES_PREV, controlX, guiTop + 5 + row * 10, "<"));
        buttonList.add(new JRMCoreGuiButtonsA2(EYES_NEXT, arrowRight, guiTop + 5 + row * 10, ">"));
        row++;

        // Eye colors
        if (flags.eyeColorSlots > 0) {
            buttonList.add(new JRMCoreGuiButtonsA2(EYECOL_PRESET_PREV, controlX, guiTop + 5 + row * 10, "<"));
            buttonList.add(new JRMCoreGuiButtonsA2(EYECOL_PRESET_NEXT, arrowRight, guiTop + 5 + row * 10, ">"));

            addColorSwatches(buttonList, row, flags.eyeColorSlots,
                new int[]{EYECOL1, EYECOL2},
                new int[]{session.eyeCol1, session.eyeCol2});

            if (flags.eyeColorSlots >= 2) {
                row++;
                String matchLabel = JRMCoreH.trl("jrmc", "Match");
                int sw = Minecraft.getMinecraft().fontRenderer.getStringWidth(matchLabel) / 2;
                buttonList.add(new JRMCoreGuiButtons01(EYE_MATCH, guiLeft + 190 - sw, guiTop + 5 + row * 10, sw,
                    matchLabel, JRMCoreH.techNCCol[1]).setShadow(false));
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void addColorSwatches(List buttonList, int row, int slotCount, int[] buttonIds, int[] colors) {
        int startX = guiLeft + 190 - 10 + ((slotCount - 1) * -10 - (slotCount > 1 ? slotCount - 2 : 0));
        for (int i = 0; i < Math.min(slotCount, buttonIds.length); i++) {
            buttonList.add(new JRMCoreGuiButtonC1(buttonIds[i],
                startX + i * 21, guiTop + 4 + row * 10, 20, 10, "", colors[i]));
        }
    }

    private void enforceConstraints(CreatorSession.AppearanceFlags flags) {
        if (!flags.canRace && session.raceIndex != 0) {
            int previousRaceIndex = session.raceIndex;
            session.raceIndex = 0;
            session.applyRaceChange(previousRaceIndex);
        }

        if (!flags.canGender && session.gender != 0) {
            session.gender = 0;
        }

        if (!flags.canYears && session.years != 0) {
            session.years = 0;
        }

        int[] raceHairColor = RaceSelectorHelper.getRaceHairColor();
        if (JRMCoreH.isRaceMajin(session.getVanillaRaceIndex())) {
            session.syncMajinHairColor();
            if (session.hairBack < 10) {
                session.hairBack = 12;
            }
        } else if (!flags.canColor && session.raceIndex < raceHairColor.length) {
            int fixedColor = raceHairColor[session.raceIndex];
            if (fixedColor != -1) session.hairColor = fixedColor;
        }

        int[] raceCustomSkin = RaceSelectorHelper.getRaceCustomSkin();
        if (session.raceIndex < raceCustomSkin.length) {
            int mode = raceCustomSkin[session.raceIndex];
            if (mode == 1 && session.skinType != 1) {
                session.skinType = 1;
            } else if (mode == 0 && session.skinType != 0) {
                session.skinType = 0;
            }
        }
    }

    @Override
    public void drawPage(int mouseX, int mouseY, float partialTicks) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        CreatorSession.AppearanceFlags flags = session.resolveAppearanceFlags();

        int labelCenterX = guiLeft + 190;
        int row = 0;

        // Race
        String[] races = RaceSelectorHelper.getRaces();
        String raceLabel = session.raceIndex < races.length
            ? JRMCoreH.trl("jrmc", races[session.raceIndex]) : "Unknown";
        drawCentered(font, raceLabel, labelCenterX, guiTop + 5 + row * 10);
        row++;

        // Gender
        if (flags.canGender) {
            String genderLabel = JRMCoreH.trl("jrmc", JRMCoreH.Genders[session.gender]);
            drawCentered(font, genderLabel, labelCenterX, guiTop + 5 + row * 10);
        }
        row++;

        // Hair
        if (flags.canHair) {
            if (session.hairBack != 12) {
                String hairLabel = JRMCoreH.trl("jrmc", "Hair") + " " + (session.hairBack + 1);
                drawCentered(font, hairLabel, labelCenterX, guiTop + 5 + row * 10);
            }
        }
        row++;

        // Hair color swatch / Arcosian TRState
        if (flags.canColor) {
            drawColorSwatch(session.hairColor, labelCenterX - 25, guiTop + 4 + row * 10, 50, 10);
        } else if (session.getVanillaRaceIndex() == 4) {
            int stateIdx = Math.min(session.stateSelected, JRMCoreH.TransNms[4].length - 1);
            String stateLabel = JRMCoreH.trl("jrmc", "TRState") + ": " +
                JRMCoreH.cldgy + JRMCoreH.trl("jrmc", JRMCoreH.TransNms[4][stateIdx]);
            drawCentered(font, stateLabel, labelCenterX, guiTop + 5 + row * 10);
        } else if (flags.canHair) {
            drawColorSwatch(session.hairColor, labelCenterX - 25, guiTop + 4 + row * 10, 50, 10);
        }
        row++;

        // Breast size — poll slider value each frame to handle dragging
        if (bustSlider != null && bustSlider.dragging) {
            int newSize = (int) (bustSlider.sliderValue * 10.0F);
            if (newSize != session.breastSize) {
                session.breastSize = newSize;
                bridge.applyPreview();
            }
        }
        row++;

        // Years
        if (flags.canYears) {
            String yearsLabel = JRMCoreH.trl("jrmc", JRMCoreH.Years[session.years]);
            drawCentered(font, yearsLabel, labelCenterX, guiTop + 5 + row * 10);
        }
        row++;

        List<RaceProperty<?>> drawProps = session.getAvailableProperties();
        if (!drawProps.isEmpty()) {
            // Clamp index in case properties changed
            if (session.selectedPropertyIndex >= drawProps.size()) {
                session.selectedPropertyIndex = 0;
            }

            RaceProperty<?> currentProp = drawProps.get(session.selectedPropertyIndex);

            // Property name row
            drawCentered(font, currentProp.displayName, labelCenterX, guiTop + 5 + row * 10);
            row++;

            // Property value row

            Object currentVal = session.racePropertyValues.getOrDefault(currentProp.key, currentProp.getDefault());
            switch (currentProp.getButtonType()) {
                case TOGGLE:
                case COLOR:
                case SLIDER:
                    break;
                case ARROW:
                default:
                    drawCentered(font, currentProp.toString(currentVal), labelCenterX, guiTop + 5 + row * 10);
                    break;
            }

            row++;
        }

        // Tail (button already shows label)
        row++;

        // Skin type
        String skinLabel = JRMCoreH.trl("jrmc", JRMCoreH.skinTyps[session.skinType]);
        drawCentered(font, skinLabel, labelCenterX, guiTop + 5 + row * 10);
        row++;

        // Custom skin details
        if (session.skinType == 1) {
            drawCustomSkinLabels(font, row);
        }

        // Title
        String title = JRMCoreH.trl("jrmc", "Appearance");
        font.drawString(title, guiLeft + 7, guiTop + 5, 0);

        // Player preview
        drawPlayerPreview();
    }

    private void drawCustomSkinLabels(FontRenderer font, int row) {
        int labelCenterX = guiLeft + 190;

        String bodyLabel = JRMCoreH.trl("jrmc", "BodyType") + " " + (session.bodyType + 1);
        drawCentered(font, bodyLabel, labelCenterX, guiTop + 5 + row * 10);
        row++;

        // Body color preset row — swatches are buttons
        row++;

        String noseLabel = JRMCoreH.trl("jrmc", "Nose") + " " + (session.faceNose + 1);
        drawCentered(font, noseLabel, labelCenterX, guiTop + 5 + row * 10);
        row++;

        String mouthLabel = JRMCoreH.trl("jrmc", "Mouth") + " " + (session.faceMouth + 1);
        drawCentered(font, mouthLabel, labelCenterX, guiTop + 5 + row * 10);
        row++;

        String eyesLabel = JRMCoreH.trl("jrmc", "Eyes") + " " + (session.eyes + 1);
        drawCentered(font, eyesLabel, labelCenterX, guiTop + 5 + row * 10);
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

    private void drawPlayerPreview() {
        previewRenderer.setAnchor(guiLeft + 60, guiTop + 152);
        previewRenderer.draw(Minecraft.getMinecraft().thePlayer, lastMouseX, lastMouseY, 0);
    }

    @Override
    public boolean actionPerformed(GuiButton button) {
        switch (button.id) {
            case RACE_NEXT: cycleRace(true); return true;
            case RACE_PREV: cycleRace(false); return true;
            case GENDER_NEXT: case GENDER_PREV: cycleGender(); return true;
            case BUST_SLIDER:
                session.breastSize = (int) (((JRMCoreGuiSlider01) button).sliderValue * 10.0F);
                syncAndRefresh();
                return true;
            case HAIR_NEXT: cycleHair(true); return true;
            case HAIR_PREV: cycleHair(false); return true;
            case CUSTOM_HAIR_BTN: parent.openVanillaCustomHairEditor(); return true;
            case YEARS_NEXT: cycleYears(true); return true;
            case YEARS_PREV: cycleYears(false); return true;
            case TAIL_BTN: session.tail = !session.tail; syncAndRefreshTail(); return true;
            case SKIN_NEXT: cycleSkinType(true); return true;
            case SKIN_PREV: cycleSkinType(false); return true;
            case BODY_NEXT: cycleBodyType(true); return true;
            case BODY_PREV: cycleBodyType(false); return true;
            case BODYCOL_PRESET_NEXT: cycleBodyColorPreset(true); return true;
            case BODYCOL_PRESET_PREV: cycleBodyColorPreset(false); return true;
            case NOSE_NEXT: cycleFace(true, 0); return true;
            case NOSE_PREV: cycleFace(false, 0); return true;
            case MOUTH_NEXT: cycleFace(true, 1); return true;
            case MOUTH_PREV: cycleFace(false, 1); return true;
            case EYES_NEXT: cycleFace(true, 2); return true;
            case EYES_PREV: cycleFace(false, 2); return true;
            case EYECOL_PRESET_NEXT: cycleEyeColorPreset(true); return true;
            case EYECOL_PRESET_PREV: cycleEyeColorPreset(false); return true;
            case EYE_MATCH: session.eyeCol2 = session.eyeCol1; syncAndRefresh(); return true;
            case STATE_NEXT: cycleState(true); return true;
            case STATE_PREV: cycleState(false); return true;
            case COLOR_BTN:
            case BODYCOL_MAIN: case BODYCOL_SUB1: case BODYCOL_SUB2: case BODYCOL_SUB3:
            case EYECOL1: case EYECOL2:
                openColorPicker(button.id);
                return true;
            case PROP_PREV:
                cycleProperty(false);
                return true;
            case PROP_NEXT:
                cycleProperty(true);
                return true;
            case PROP_VAL_PREV:
                cyclePropertyValue(false);
                return true;
            case PROP_VAL_NEXT:
                cyclePropertyValue(true);
                return true;
            default: return false;
        }
    }

    // ── Cycling helpers ──

    private void cycleRace(boolean forward) {
        String[] races = RaceSelectorHelper.getRaces();
        String[] raceAllow = RaceSelectorHelper.getRaceAllow();
        int total = races.length;

        int start = session.raceIndex;
        int next = forward ? start + 1 : start - 1;

        for (int i = 0; i < total; i++) {
            if (next >= total) next = 0;
            if (next < 0) next = total - 1;
            if (next < raceAllow.length && JRMCoreH.Allow(raceAllow[next])) {
                int previousRaceIndex = session.raceIndex;
                session.raceIndex = next;
                if (RaceSelectorHelper.isCustomRaceIndex(next)) {
                    Race race = RaceSelectorHelper.getCustomRaceByIndex(next);
                    session.currentRaceKey = race != null ? race.getName() : null;
                } else {
                    session.currentRaceKey = null;
                }
                session.applyRaceChange(previousRaceIndex);
                syncAndRefreshRace();
                return;
            }
            next += forward ? 1 : -1;
        }
    }

    private void cycleGender() {
        int next = session.gender + 1;
        if (next >= JRMCoreH.Genders.length || !JRMCoreH.Allow(JRMCoreH.GenderAllow[next])) {
            next = 0;
        }
        session.gender = next;
        syncAndRefresh();
    }

    private void cycleHair(boolean forward) {
        if (forward) {
            int next = session.hairBack + 1;
            session.hairBack = next < JRMCoreH.Hairs.length ? next :
                (JRMCoreH.isRaceMajin(session.getVanillaRaceIndex()) ? 10 : 0);
        } else {
            int next = session.hairBack - 1;
            session.hairBack = next >= 0 ? next : JRMCoreH.Hairs.length - 1;
        }
        syncAndRefresh();
    }

    private void cycleYears(boolean forward) {
        if (forward) {
            session.years = session.years + 1 < JRMCoreH.YearsD.length ? session.years + 1 : 0;
        } else {
            session.years = session.years - 1 >= 0 ? session.years - 1 : JRMCoreH.YearsD.length - 1;
        }
        syncAndRefreshYears();
    }

    private void cycleSkinType(boolean forward) {
        int previousRaceIndex = session.raceIndex;
        session.skinType = forward ? JRMCoreGuiScreen.SlctF(session.skinType, 2)
            : JRMCoreGuiScreen.SlctB(session.skinType, 2);
        session.applyRaceChange(previousRaceIndex);
        syncAndRefresh();
    }

    private void cycleBodyType(boolean forward) {
        int[][] sknLimits = RaceSelectorHelper.getCustomSknLimits();
        int max = session.raceIndex < sknLimits.length ? sknLimits[session.raceIndex][0] : 1;
        session.bodyType = forward ? JRMCoreGuiScreen.SlctF(session.bodyType, max)
            : JRMCoreGuiScreen.SlctB(session.bodyType, max);
        syncAndRefresh();
    }

    private void cycleBodyColorPreset(boolean forward) {
        int[] sknLimitsBCP = RaceSelectorHelper.getCustomSknLimitsBCP();
        int max = session.raceIndex < sknLimitsBCP.length ? sknLimitsBCP[session.raceIndex] : 1;
        session.bodyColPreset = forward ? JRMCoreGuiScreen.SlctF(session.bodyColPreset, max)
            : JRMCoreGuiScreen.SlctB(session.bodyColPreset, max);
        session.applyBodyColorPreset();
        session.syncMajinHairColor();
        syncAndRefresh();
    }

    private void cycleFace(boolean forward, int type) {
        int[][] sknLimits = RaceSelectorHelper.getCustomSknLimits();
        int idx = type == 0 ? 2 : (type == 1 ? 3 : 4);
        int max = session.raceIndex < sknLimits.length ? sknLimits[session.raceIndex][idx] : 1;
        switch (type) {
            case 0:
                session.faceNose = forward ? JRMCoreGuiScreen.SlctF(session.faceNose, max)
                    : JRMCoreGuiScreen.SlctB(session.faceNose, max);
                break;
            case 1:
                session.faceMouth = forward ? JRMCoreGuiScreen.SlctF(session.faceMouth, max)
                    : JRMCoreGuiScreen.SlctB(session.faceMouth, max);
                break;
            case 2:
                session.eyes = forward ? JRMCoreGuiScreen.SlctF(session.eyes, max)
                    : JRMCoreGuiScreen.SlctB(session.eyes, max);
                break;
        }
        syncAndRefresh();
    }

    private void cycleEyeColorPreset(boolean forward) {
        int[][] eyeCols = RaceSelectorHelper.getDefEyeCols();
        int max = eyeCols.length;
        session.eyeColPreset = forward ? JRMCoreGuiScreen.SlctF(session.eyeColPreset, max)
            : JRMCoreGuiScreen.SlctB(session.eyeColPreset, max);
        session.applyEyeColorPreset();
        syncAndRefresh();
    }

    private void cycleState(boolean forward) {
        if (forward) {
            JRMCoreGuiScreen.StateViewF();
        } else {
            JRMCoreGuiScreen.StateViewB();
        }
        session.stateSelected = JRMCoreGuiScreen.StateSlcted;
        syncAndRefreshState();
    }

    private void cycleProperty(boolean forward) {
        List<RaceProperty<?>> props = session.getAvailableProperties();
        if (props.isEmpty()) return;
        int next = session.selectedPropertyIndex + (forward ? 1 : -1);
        if (next >= props.size()) next = 0;
        if (next < 0) next = props.size() - 1;
        session.selectedPropertyIndex = next;
        parent.refreshPage();
    }

    private void cyclePropertyValue(boolean forward) {
        List<RaceProperty<?>> props = session.getAvailableProperties();
        if (props.isEmpty()) return;
        if (session.selectedPropertyIndex >= props.size()) return;

        RaceProperty<?> prop = props.get(session.selectedPropertyIndex);
        Object current = session.racePropertyValues.getOrDefault(prop.key, prop.getDefault());

        if (prop instanceof RaceProperty.Int) {
            RaceProperty.Int intProp = (RaceProperty.Int) prop;
            int val = (current instanceof Integer) ? (Integer) current : intProp.defaultValue;
            int next = val + (forward ? 1 : -1);
            if (next < intProp.min) next = intProp.min;
            if (next > intProp.max) next = intProp.max;
            session.racePropertyValues.put(prop.key, next);

        } else if (prop instanceof RaceProperty.Bool) {
            RaceProperty.Bool boolProp = (RaceProperty.Bool) prop;
            boolean val = (current instanceof Boolean) ? (Boolean) current : boolProp.defaultValue;
            session.racePropertyValues.put(prop.key, !val);

        } else if (prop instanceof RaceProperty.Str) {
            RaceProperty.Str strProp = (RaceProperty.Str) prop;
            List<String> allowed = strProp.allowedValues;
            String val = (current instanceof String) ? (String) current : strProp.defaultValue;
            int idx = allowed.indexOf(val);
            if (idx < 0) idx = 0;
            int next = idx + (forward ? 1 : -1);
            if (next >= allowed.size()) next = 0;
            if (next < 0) next = allowed.size() - 1;
            session.racePropertyValues.put(prop.key, allowed.get(next));
        }

        parent.refreshPage();
    }

    private void openColorPicker(int buttonId) {
        // Color picker integration: sync to vanilla statics, open vanilla color picker GUI
        // The color picker writes directly to vanilla statics; on return we re-read them
        bridge.applyPreview();
        int vanillaColorType;
        switch (buttonId) {
            case COLOR_BTN: vanillaColorType = 4; break;
            case BODYCOL_MAIN: vanillaColorType = 5003; break;
            case BODYCOL_SUB1: vanillaColorType = 5004; break;
            case BODYCOL_SUB2: vanillaColorType = 5005; break;
            case BODYCOL_SUB3: vanillaColorType = 5014; break;
            case EYECOL1: vanillaColorType = 5009; break;
            case EYECOL2: vanillaColorType = 5010; break;
            default: return;
        }
        // Store the color type and open the DBC color picker
        // This delegates to the vanilla color picker mechanism
        parent.openVanillaColorPicker(vanillaColorType);
    }

    private void syncAndRefresh() {
        bridge.applyPreview();
        parent.refreshPage();
    }

    private void syncAndRefreshRace() {
        bridge.applyPreviewWithRaceSync();
        parent.refreshPage();
    }

    private void syncAndRefreshYears() {
        bridge.applyPreviewWithYearsSync();
        parent.refreshPage();
    }

    private void syncAndRefreshTail() {
        bridge.applyPreviewWithTailSync();
        parent.refreshPage();
    }

    private void syncAndRefreshState() {
        bridge.applyPreviewWithStateSync();
        parent.refreshPage();
    }
}
