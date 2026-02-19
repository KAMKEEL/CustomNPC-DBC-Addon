package kamkeel.npcdbc.client.gui.hud.abilityWheel;

import kamkeel.npcdbc.client.KeyHandler;
import kamkeel.npcdbc.client.gui.component.SubGuiSelectAbility;
import kamkeel.npcdbc.client.gui.hud.abilityHotbar.HUDAbilityHotbar;
import kamkeel.npcdbc.client.shader.ShaderHelper;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.ability.DBCRequestAbilityWheel;
import kamkeel.npcdbc.network.packets.player.ability.DBCSelectAbility;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.ISubGuiListener;
import noppes.npcs.client.gui.util.SubGuiInterface;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 * HUD for the Ability Wheel - allows players to select from their unlocked abilities.
 * Similar to the Form Wheel, but works with CustomNPC+ PlayerAbilityData.
 */
public class HUDAbilityWheel extends GuiNPCInterface implements ISubGuiListener {

    public static float BLUR_INTENSITY = 0;
    public static float MAX_BLUR = 4;
    public static boolean BLUR_ENABLED = true;

    ScaledResolution scaledResolution;
    public AbilityWheelSegment[] wheelSlot = new AbilityWheelSegment[6];
    public PlayerDBCInfo dbcInfo;

    float guiAnimationScale = 0, undoMCScaling = 1;
    long timeOpened, timeClosedSubGui, timeSinceM1, timeClosed;

    public int hoveredSlot = -1;
    boolean keyDown, unpressedAllKeys = false;
    boolean configureEnabled;

    public static final int SLOTS_PER_PAGE = 6;
    public static final int TOTAL_PAGES = 2;
    public static int currentPage = 0;

    public boolean isClosing;

    public static final int CLOSE_TIME = 600;
    public static final int OPEN_TIME = 1500;

    public double easeOutExpo(double x) {
        return x == 1 ? 1 : 1 - Math.pow(2, -10 * x);
    }

    public HUDAbilityWheel() {
        mc = Minecraft.getMinecraft();
        dbcInfo = PlayerDataUtil.getClientDBCInfo();

        int offset = currentPage * SLOTS_PER_PAGE;
        for (int i = 0; i < SLOTS_PER_PAGE; i++) {
            wheelSlot[i] = new AbilityWheelSegment(this, offset + i);
            wheelSlot[i].setAbility(dbcInfo.abilityWheel[offset + i], false);
        }
        DBCPacketHandler.Instance.sendToServer(new DBCRequestAbilityWheel());

        // Stops the GUI from un-pressing all keys for you.
        mc.inGameHasFocus = false;
        mc.mouseHelper.ungrabMouseCursor();

        BLUR_INTENSITY = 0;
    }

    /**
     * Reload wheel from server data (called when GUI data packet received).
     */
    public void reloadFromWheelData() {
        if (dbcInfo != null) {
            int offset = currentPage * SLOTS_PER_PAGE;
            for (int i = 0; i < SLOTS_PER_PAGE; i++) {
                wheelSlot[i].updateIndex(offset + i);
                wheelSlot[i].setAbility(dbcInfo.abilityWheel[offset + i], false);
            }
        }
    }

    /**
     * Switch to a different page of abilities.
     */
    public void switchPage(int page) {
        if (page < 0 || page >= TOTAL_PAGES || page == currentPage) return;
        currentPage = page;
        selectSlot(-1);
        reloadFromWheelData();
    }

    @Override
    public void initGui() {
        super.initGui();
        // Prevents replaying the open animation on screen resize
        if (timeOpened == 0)
            timeOpened = Minecraft.getSystemTime();

        scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);


        int x = (this.width / 2) + 94;
        int y = this.height - 22;
        addButton(new GuiNpcButton(6, x, y, 60, 20, new String[]{"wheel.configure", "gui.done"}, !configureEnabled ? 0 : 1));

        float factor = scaledResolution.getScaleFactor();
        undoMCScaling = 1f / factor * 2f;
        if (factor == 1) {
            if (mc.displayHeight < 260)
                undoMCScaling = 0.3f;
            else if (mc.displayHeight < 350)
                undoMCScaling = 0.45f;
            else if (mc.displayHeight < 720)
                undoMCScaling = 0.7f;
            else if (mc.displayWidth < 650)
                undoMCScaling = 1f;
        } else if (factor == 2) {
            if (mc.displayHeight < 530)
                undoMCScaling = 0.413f;
            else if (mc.displayHeight < 600)
                undoMCScaling = 0.475f;
            else if (mc.displayHeight < 720)
                undoMCScaling = 1f / scaledResolution.getScaleFactor() * 1;
            else
                undoMCScaling = 1;
        } else if (factor == 3) {
            if (mc.displayHeight < 730)
                undoMCScaling = 1f / 3;
            else if (mc.displayHeight < 930)
                undoMCScaling = 0.425f;
            else if (mc.displayHeight < 1000)
                undoMCScaling = 0.6f;
            else if (mc.displayWidth < 1300)
                undoMCScaling = 0.8f;
            else if (mc.displayHeight < 1250) {
                undoMCScaling = 0.75f;
                glTranslatef(0, -15, 0);
            } else
                undoMCScaling = 0.99f;
        }

        if (configureEnabled) {
            addButton(new GuiNpcButton(8, x - 94 - 75, y - 25, 150, 20, "gui.edit"));
            addButton(new GuiNpcButton(7, x += 62, y, 80, 20, "wheel.switchWheel"));
            addButton(new GuiNpcButton(9, x - 250 - 150, y, 150, 20,
                new String[]{"display.horizontalHotbar", "display.verticalHotbar"}, ConfigDBCClient.AbilityHotbarHorizontal ? 0 : 1));


            x = (int) ((this.width / 2) * undoMCScaling + 190);
            y = (this.height / 2) - 100;
            if (undoMCScaling < 1) {
                x += 20;
                y -= 25;
            }
        }

        // Only start open animation once (first initGui call)
        if (wheelSlot[0].startOpenTime < 0) {
            long now = Minecraft.getSystemTime();
            for (AbilityWheelSegment seg : wheelSlot) {
                seg.startOpenAnimation(now);
            }
        }
    }

    public void buttonEvent(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;

        if (button.id == 6) {
            // Configure/Done toggle
            configureEnabled = button.getValue() == 1;
            if (configureEnabled) {
                selectSlot(-1);
                timeClosedSubGui = Minecraft.getSystemTime();
            }
        } else if (button.id == 7) {
            // Switch Wheel texture
            ConfigDBCClient.AlteranteSelectionWheelTexture = !ConfigDBCClient.AlteranteSelectionWheelTexture;
            ConfigDBCClient.AlternateSelectionWheelTextureProperty.set(ConfigDBCClient.AlteranteSelectionWheelTexture);
            timeClosedSubGui = Minecraft.getSystemTime();
        } else if (button.id == 8) {
            // Edit button - open ability selection for hovered slot
            this.setSubGui(new SubGuiSelectAbility(button.id));
        } else if (button.id == 9) {
            ConfigDBCClient.AbilityHotbarHorizontal = ((GuiNpcButton) guibutton).getValue() == 0;
            ConfigDBCClient.AbilityHotbarHorizontalProperty.set(ConfigDBCClient.AbilityHotbarHorizontal);
            ConfigDBCClient.config.save();
        }

        initGui();
    }

    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof SubGuiSelectAbility) {
            SubGuiSelectAbility selectAbility = (SubGuiSelectAbility) subgui;
            if (selectAbility.confirmed && selectAbility.selectedAbilityKey != null) {
                int slotID = selectAbility.buttonID == 8 ? hoveredSlot : selectAbility.buttonID;
                if (slotID < 0 || slotID >= SLOTS_PER_PAGE) {
                    timeClosedSubGui = Minecraft.getSystemTime();
                    initGui();
                    return;
                }
                AbilityWheelSegment slot = wheelSlot[slotID];

                selectSlot(slotID);
                timeClosedSubGui = Minecraft.getSystemTime();

                // Don't reassign the same ability
                if (slot.action != null && selectAbility.selectedAbilityKey.equals(slot.data.abilityKey))
                    return;

                slot.setAbility(selectAbility.selectedAbilityKey, true);
            } else if (selectAbility.removeAbility) {
                int slotID = selectAbility.buttonID == 8 ? hoveredSlot : selectAbility.buttonID;
                if (slotID < 0 || slotID >= SLOTS_PER_PAGE) {
                    timeClosedSubGui = Minecraft.getSystemTime();
                    initGui();
                    return;
                }
                AbilityWheelSegment slot = wheelSlot[slotID];

                selectSlot(slotID);
                slot.removeAbility();
            }
            timeClosedSubGui = Minecraft.getSystemTime();
        }
        initGui();
    }

    public void calculateHoveredSlot(float HALF_WIDTH, float HALF_HEIGHT, boolean configureEnabled) {
        if (isClosing)
            return;
        final float deltaX = HALF_WIDTH - mouseX;
        final float deltaY = HALF_HEIGHT - mouseY;
        float radius = ConfigDBCClient.AlteranteSelectionWheelTexture ? 98 : 74;
        radius *= undoMCScaling;
        if (Math.sqrt(deltaX * deltaX + deltaY * deltaY) > radius) {
            final float radians = (float) Math.atan2(deltaY, deltaX);
            final float degree = Math.round(radians * (180 / Math.PI));

            int tempHoveredSlot = (int) ((degree - 180) / -60) - 1;
            if (tempHoveredSlot == -1)
                tempHoveredSlot = 5;

            boolean justOpened = Minecraft.getSystemTime() - timeOpened < 50;
            if (!justOpened && tempHoveredSlot != hoveredSlot && !configureEnabled)
                selectSlot(tempHoveredSlot);
        }
    }

    public void selectSlot(int slotID) {
        if (hoveredSlot == slotID)
            return;

        if (hoveredSlot != -1)
            wheelSlot[hoveredSlot].setHoveredState(false);
        if (slotID != -1)
            wheelSlot[slotID].setHoveredState(true);
        hoveredSlot = slotID;
    }

    @Override
    public void updateScreen() {
        // Enable/disable Edit button based on hovered slot
        if (getButton(8) != null)
            getButton(8).enabled = hoveredSlot != -1;

        if (Mouse.isButtonDown(1)) {
            if (configureEnabled) {
                if (timeSinceM1 == 0)
                    timeSinceM1 = Minecraft.getSystemTime();

                boolean singleClick = Minecraft.getSystemTime() - timeSinceM1 < 75;
                if (singleClick) {
                    selectSlot(-1);
                } else if (!hasSubGui()) {
                    BLUR_ENABLED = false;
                    mc.inGameHasFocus = true;
                    mc.mouseHelper.grabMouseCursor();
                }
            } else {
                // Right click to cancel selection
                DBCPacketHandler.Instance.sendToServer(new DBCSelectAbility(""));
                close();
            }
        } else {
            if (timeSinceM1 != 0)
                timeSinceM1 = 0;
            if (!isClosing && mc.inGameHasFocus) {
                mc.inGameHasFocus = false;
                Mouse.setGrabbed(false);
                BLUR_ENABLED = true;
            }
        }

        int code = KeyHandler.AbilityWheelKey.getKeyCode();
        keyDown = isMouseButton() ? Mouse.isButtonDown(code + 100) : Keyboard.isKeyDown(code);
        if (!keyDown && !hasSubGui() && !configureEnabled && !isClosing) {
            if (hoveredSlot != -1 && !wheelSlot[hoveredSlot].data.isEmpty())
                wheelSlot[hoveredSlot].selectAbility();

            mc.inGameHasFocus = true;
            mc.mouseHelper.grabMouseCursor();
            isClosing = true;
            timeClosed = (long) (Minecraft.getSystemTime() - (1 - guiAnimationScale) * CLOSE_TIME);
        }
    }

    public static boolean isMouseButton() {
        return KeyHandler.AbilityWheelKey.getKeyCode() < 0;
    }

    protected void drawGradientRectWithFade(int left, int top, int right, int bottom, int startColor, int endColor, float fade) {
        int red = (startColor >> 16) & 0xFF;
        int green = (startColor >> 8) & 0xFF;
        int blue = (startColor) & 0xFF;
        int alpha = (startColor >> 24 & 255);
        int newAlpha = Math.min(255, Math.max(0, (int) (alpha * fade)));
        int newStart = (newAlpha << 24) | (red << 16) | (green << 8) | blue;

        red = (endColor >> 16) & 0xFF;
        green = (endColor >> 8) & 0xFF;
        blue = (endColor) & 0xFF;
        alpha = (endColor >> 24 & 255);
        newAlpha = Math.min(255, Math.max(0, (int) (alpha * fade)));
        int newEnd = (newAlpha << 24) | (red << 16) | (green << 8) | blue;

        drawGradientRect(left, top, right, bottom, newStart, newEnd);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (isClosing && guiAnimationScale >= 0) {
            float updateTime = (float) (Minecraft.getSystemTime() - timeClosed) / CLOSE_TIME;
            updateTime = Math.min(1, updateTime);
            guiAnimationScale = (float) (1 - easeOutExpo(updateTime));
            if (guiAnimationScale <= 0.05)
                close();
        } else if (guiAnimationScale < 1) {
            float updateTime = (float) (Minecraft.getSystemTime() - timeOpened) / OPEN_TIME;
            updateTime = Math.min(1, updateTime);

            guiAnimationScale = (float) easeOutExpo(updateTime);
        }

        BLUR_INTENSITY = guiAnimationScale * MAX_BLUR;

        int gradientColor = ((int) (255 * 0.2f * guiAnimationScale) << 24);
        this.

            drawGradientRect(0, 0, this.width, this.height, gradientColor, gradientColor);

        if (!ShaderHelper.shadersEnabled())
            drawGradientRectWithFade(0, 0, width, height, 0x88000000, 0xfa000000, guiAnimationScale);


        glPushMatrix();
        final float HALF_WIDTH = (float) this.width / 2;
        final float HALF_HEIGHT = (float) this.height / 2;

        calculateHoveredSlot(HALF_WIDTH, HALF_HEIGHT, configureEnabled);

        glPushMatrix();
        GL11.glTranslatef(HALF_WIDTH, HALF_HEIGHT, 0);
        GL11.glScalef(undoMCScaling, undoMCScaling, 0);
        GL11.glScalef(guiAnimationScale, guiAnimationScale, 0);
        float wheelDiameter = 1.4f;
        GL11.glScalef(wheelDiameter, wheelDiameter, 0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        for (int i = 0; i < 6; i++) {
            glPushMatrix();
            GL11.glRotatef(i * -60, 0, 0, 1);
            float segmentScale = 1f + 0.1f * wheelSlot[i].getSegmentScale();
            GL11.glScalef(segmentScale, segmentScale, 0);

            if (i % 3 == 0) {
                GL11.glTranslatef(0, -80f, 0);
            } else {
                GL11.glTranslatef(0, -95f, 0);
            }


            GL11.glRotatef(i * 60, 0, 0, 1);
            if (i == 0) {
                GL11.glTranslatef(3, -15, 0);
            } else if (i == 3) {
                GL11.glTranslatef(0, 15, 0);
            }
            wheelSlot[i].draw(fontRendererObj);


            glPopMatrix();
        }

        glPopMatrix();
        GL11.glDisable(GL11.GL_BLEND);

        glPushMatrix();
        GL11.glTranslatef(HALF_WIDTH, HALF_HEIGHT, 0);
        GL11.glScalef(undoMCScaling, undoMCScaling, undoMCScaling);
        float guiVariantScale = (AbilityWheelSegment.variant == 0 ? 0.75f : 0.9f);
        float playerScale = guiAnimationScale * guiVariantScale * (ConfigDBCClient.AlteranteSelectionWheelTexture ? 1.5f : 1);
        GL11.glScalef(playerScale, playerScale, playerScale);
        GL11.glTranslatef(-HALF_WIDTH, -HALF_HEIGHT + (ConfigDBCClient.AlteranteSelectionWheelTexture ? 8 : 0), 0);

        glPopMatrix();

        glPopMatrix();

        // Draw page indicator
        if (guiAnimationScale > 0.5f) {
            int pageAlpha = (int) (255 * Math.min(1, (guiAnimationScale - 0.5f) * 2));
            String pageText = (currentPage + 1) + " / " + TOTAL_PAGES;
            int pageColor = (pageAlpha << 24) | 0xCCCCCC;
            drawCenteredString(fontRendererObj, pageText, this.width / 2, this.height - 40, pageColor);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void drawDefaultBackground() {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
        boolean enoughTimeSinceClose = Minecraft.getSystemTime() - timeClosedSubGui > 50;
        if (keyCode == 1 && configureEnabled && enoughTimeSinceClose) {
            configureEnabled = false;
            initGui();
        }
    }

    @Override
    public void save() {
    }

    @Override
    public void handleKeyboardInput() {
        super.handleKeyboardInput();

        // Handles keeping movement keys still fluid.
        if (hasSubGui()) {
            if (!unpressedAllKeys) {
                KeyBinding.unPressAllKeys();
                unpressedAllKeys = true;
            }
            return;
        }
        KeyBinding.setKeyBindState(Keyboard.getEventKey(), Keyboard.getEventKeyState());
        unpressedAllKeys = false;
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();

        int scroll = Mouse.getEventDWheel();
        if (scroll != 0 && !isClosing && !hasSubGui()) {
            if (scroll > 0) {
                switchPage(currentPage - 1);
            } else {
                switchPage(currentPage + 1);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        boolean enoughTimeSinceClose = Minecraft.getSystemTime() - timeClosedSubGui > 50;
        if (configureEnabled && !hasSubGui() && enoughTimeSinceClose) {
            // Allow clicking to select slot in configure mode
            calculateHoveredSlot((float) this.width / 2, (float) this.height / 2, false);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
