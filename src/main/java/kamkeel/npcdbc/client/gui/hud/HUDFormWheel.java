package kamkeel.npcdbc.client.gui.hud;

import kamkeel.npcdbc.client.KeyHandler;
import kamkeel.npcdbc.client.gui.component.SubGuiSelectForm;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.data.FormWheelData;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.form.DBCRequestFormWheel;
import kamkeel.npcdbc.network.packets.form.DBCSelectForm;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.util.ValueUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.Iterator;

import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;

public class HUDFormWheel extends GuiNPCInterface implements IGuiData, ISubGuiListener {

    public static float BLUR_INTENSITY = 0;
    public static float MAX_BLUR = 2;
    public static boolean renderingPlayer = false;
    ScaledResolution scaledResolution;
    public FormWheelSegment[] wheelSlot = new FormWheelSegment[6];

    private float zoomed = 70, rotation;
    float guiAnimationScale = 0, animationScaleFactor, undoMCScaling = 1;
    long timeOpened, timeClosedSubGui, timeSinceM1;

    public int hoveredSlot = -1;
    boolean keyDown, unpressedAllKeys = false;
    boolean configureEnabled;

    public HUDFormWheel() {
        mc = Minecraft.getMinecraft();
        setBackground("menubg.png");


        PlayerDBCInfo data = PlayerDataUtil.getClientDBCInfo();
        for (int i = 0; i < 6; i++) {
            wheelSlot[i] = new FormWheelSegment(this, i);
            wheelSlot[i].setForm(data.formWheel[i], false);
        }
        PacketHandler.Instance.sendToServer(new DBCRequestFormWheel().generatePacket());

        // Stops the GUI from un-pressing all keys for you.
        mc.inGameHasFocus = false;
        mc.mouseHelper.ungrabMouseCursor();
    }

    @Override
    public void initGui() {
        super.initGui();
        // Prevents replaying the open animation on screen resize
        if (timeOpened == 0)
            timeOpened = Minecraft.getSystemTime();

        scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        // FormWheelSegment.variant = 1;


        int x = (this.width / 2) + 94;
        int y = this.height - 22;
        addButton(new GuiNpcButton(6, x, y, 60, 20, new String[]{"Configure", "Done"}, !configureEnabled ? 0 : 1));

        if (configureEnabled) {
            addButton(new GuiNpcButton(7, x += 62, y, 80, 20, "Switch Wheel"));


            float undoMCScaling = 1;
            // TODO: Add more support for higher scale factors / GUI sizes when people start complaining.
            //       Use a switch case
            if (scaledResolution.getScaleFactor() == 2) {
                if (mc.displayHeight < 720) {
                    undoMCScaling = 1f / scaledResolution.getScaleFactor() * 1;
                }
            }

            x = (int) ((this.width / 2) * undoMCScaling + 190);
            y = (this.height / 2) - 100;
            if (undoMCScaling < 1) {
                x += 20;
                y -= 25;
            }


            Form form = wheelSlot[0].form;


            addLabel(new GuiNpcLabel(0, "Slot 1", x, y + 5));
            getLabel(0).color = 14737632;
            addButton(new GuiNpcButton(100, x + 80, y, 16, 20, "X"));
            addButton(new GuiNpcButton(0, x, y, 80, 20, "general.noForm"));
            getButton(0).setDisplayText(getFormName(0));
            getButton(100).enabled = wheelSlot[0].data.formID != -1;

            y += 22;
            addLabel(new GuiNpcLabel(1, "Slot 2", x + 2, y + 5));
            getLabel(1).color = 14737632;
            y += 15;
            addButton(new GuiNpcButton(11, x + 80, y, 16, 20, "X"));
            addButton(new GuiNpcButton(1, x, y, 80, 20, "general.noForm"));
            getButton(1).setDisplayText(getFormName(1));
            getButton(11).enabled = wheelSlot[1].data.formID != -1;

            y += 22;
            addLabel(new GuiNpcLabel(2, "Slot 3", x + 2, y + 5));
            getLabel(2).color = 14737632;
            y += 15;
            addButton(new GuiNpcButton(22, x + 80, y, 16, 20, "X"));
            addButton(new GuiNpcButton(2, x, y, 80, 20, "general.noForm"));
            getButton(2).setDisplayText(getFormName(2));
            getButton(22).enabled = wheelSlot[2].data.formID != -1;

            y += 22;
            addLabel(new GuiNpcLabel(3, "Slot 4", x + 2, y + 5));
            getLabel(3).color = 14737632;
            y += 15;
            addButton(new GuiNpcButton(33, x + 80, y, 16, 20, "X"));
            addButton(new GuiNpcButton(3, x, y, 80, 20, "general.noForm"));
            getButton(3).setDisplayText(getFormName(3));
            getButton(33).enabled = wheelSlot[3].data.formID != -1;

            y += 22;
            addLabel(new GuiNpcLabel(4, "Slot 5", x + 2, y + 5));
            getLabel(4).color = 14737632;
            y += 15;
            addButton(new GuiNpcButton(44, x + 80, y, 16, 20, "X"));
            addButton(new GuiNpcButton(4, x, y, 80, 20, "general.noForm"));
            getButton(4).setDisplayText(getFormName(4));
            getButton(44).enabled = wheelSlot[4].data.formID != -1;

            y += 22;
            addLabel(new GuiNpcLabel(5, "Slot 6", x + 2, y + 5));
            getLabel(5).color = 14737632;
            y += 15;
            addButton(new GuiNpcButton(55, x + 80, y, 16, 20, "X"));
            addButton(new GuiNpcButton(5, x, y, 80, 20, "general.noForm"));
            getButton(5).setDisplayText(getFormName(5));
            getButton(55).enabled = wheelSlot[5].data.formID != -1;
        }

        BLUR_INTENSITY = 0;
    }

    public String getFormName(int wheelSlot) {
        DBCData dbcData = DBCData.getClient();
        FormWheelData data = this.wheelSlot[wheelSlot].data;
        Form form = this.wheelSlot[wheelSlot].form;

        return !data.isDBC ? form != null ? form.menuName : "general.noForm" : DBCForm.getMenuName(dbcData.Race, data.formID);
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        for (int i = 0; i < 6; i++) {
            FormWheelData data = new FormWheelData(i);
            data.readFromNBT(compound.getCompoundTag("FormWheel" + i));
            wheelSlot[i].setForm(data, false);
        }

        initGui();
    }

    public void buttonEvent(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (button.id <= 5) {
            this.setSubGui(new SubGuiSelectForm(button.id, true, true).displayDBCForms(DBCData.getClient()));
        } else if (button.id == 100) {
            wheelSlot[0].removeForm();
        } else if (button.id == 11) {
            wheelSlot[1].removeForm();
        } else if (button.id == 22) {
            wheelSlot[2].removeForm();
        } else if (button.id == 33) {
            wheelSlot[3].removeForm();
        } else if (button.id == 44) {
            wheelSlot[4].removeForm();
        } else if (button.id == 55) {
            wheelSlot[5].removeForm();
        } else if (button.id == 6) {
            configureEnabled = button.getValue() == 1;
            if (configureEnabled) {
                selectSlot(-1);
                timeClosedSubGui = Minecraft.getSystemTime();
            }

        } else if (button.id == 7) {
            ConfigDBCClient.AlteranteSelectionWheelTexture = !ConfigDBCClient.AlteranteSelectionWheelTexture;
            ConfigDBCClient.AlternateSelectionWheelTextureProperty.set((ConfigDBCClient.AlteranteSelectionWheelTexture));
            timeClosedSubGui = Minecraft.getSystemTime();

        }
        initGui();
    }


    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof SubGuiSelectForm) {
            SubGuiSelectForm selectForm = ((SubGuiSelectForm) subgui);
            if (selectForm.confirmed) {
                int slotID = selectForm.buttonID;
                FormWheelSegment slot = wheelSlot[slotID];
                Form form = slot.form;

                selectSlot(slotID);
                timeClosedSubGui = Minecraft.getSystemTime();

                if (form != null && selectForm.selectedFormID == form.id)
                    return;


                slot.setForm(selectForm.selectedFormID, selectForm.isDBC, true);
            }
        }
        initGui();
    }


    @Override
    public void updateScreen() {

        if (Mouse.isButtonDown(1)) {
            if (configureEnabled) {
                if (timeSinceM1 == 0)
                    timeSinceM1 = Minecraft.getSystemTime();

                boolean singleClick = Minecraft.getSystemTime() - timeSinceM1 < 75;
                if (singleClick) {
                    selectSlot(-1);
                } else {
                    mc.inGameHasFocus = true;
                    mc.mouseHelper.grabMouseCursor();
                }
            } else {
                PacketHandler.Instance.sendToServer(new DBCSelectForm(-1, false).generatePacket());
                close();
            }
        } else {
            if (timeSinceM1 != 0)
                timeSinceM1 = 0;
            if (mc.inGameHasFocus) {
                mc.inGameHasFocus = false;
                Mouse.setGrabbed(false);
            }
        }
        keyDown = Keyboard.isKeyDown(KeyHandler.FormWheelKey.getKeyCode());
        if (!keyDown && !hasSubGui() && !configureEnabled) {
            if (hoveredSlot != -1)
                wheelSlot[hoveredSlot].selectForm();

            close();
        }
        mc.thePlayer.movementInput.updatePlayerMoveState();
        float updateTime = (float) (Minecraft.getSystemTime() - timeOpened) / 250;
        animationScaleFactor = Math.min(updateTime, 1);
        guiAnimationScale = (guiAnimationScale + 0.2f * (animationScaleFactor - guiAnimationScale));
        guiAnimationScale = ValueUtil.clamp(guiAnimationScale, 0, 1);
        BLUR_INTENSITY = guiAnimationScale * MAX_BLUR;
    }

    public void drawButtons(int i, int j) {

        boolean subGui = this.hasSubGui();
        Iterator var5 = this.labels.values().iterator();
        while (var5.hasNext()) {
            GuiNpcLabel label = (GuiNpcLabel) var5.next();
            label.drawLabel(this, this.fontRendererObj);
        }

        var5 = this.buttons.values().iterator();
        while (var5.hasNext()) {
            GuiNpcButton button = (GuiNpcButton) var5.next();
            button.updateSubGUI(subGui);
            if (!button.hoverableText.isEmpty()) {
                button.drawHover(i, j, subGui);
            }
        }

        int k;

        for (k = 0; k < this.buttonList.size(); ++k) {
            ((GuiButton) this.buttonList.get(k)).drawButton(this.mc, mouseX, mouseY);
        }

        for (k = 0; k < this.labelList.size(); ++k) {
            ((GuiLabel) this.labelList.get(k)).func_146159_a(this.mc, mouseX, mouseY);
        }

    }

    public void calculateHoveredSlot(float HALF_WIDTH, float HALF_HEIGHT, boolean configureEnabled) {
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

            if (tempHoveredSlot != hoveredSlot && !configureEnabled)
                selectSlot(tempHoveredSlot);

        }
    }

    public void selectSlot(int slotID) {
        if (hoveredSlot != -1)
            wheelSlot[hoveredSlot].setHoveredState(false);
        if (slotID != -1)
            wheelSlot[slotID].setHoveredState(true);
        hoveredSlot = slotID;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        int gradientColor = ((int) (255 * 0.2f * guiAnimationScale) << 24);
        this.drawGradientRect(0, 0, this.width, this.height, gradientColor, gradientColor);
        if (hasSubGui()) {
            super.drawScreen(mouseX, mouseY, partialTicks);
            return;
        }


        super.drawScreen(mouseX, mouseY, partialTicks);

        //       hoveredSlot = -1;
        double width = 124;
        double height = 124 - 28;
        GL11.glPushMatrix();
//        GL11.glTranslatef((float) (-width/2), -28.0f - 69f, 0);

//        WheelSegment segment = new WheelSegment(2);

        final float HALF_WIDTH = (float) this.width / 2;
        final float HALF_HEIGHT = (float) this.height / 2;


        undoMCScaling = 1;

        // TODO: Add more support for higher scale factors / GUI sizes when people start complaining.
        //       Use a switch case
        if (scaledResolution.getScaleFactor() == 2) {
            if (mc.displayHeight < 720) {
                undoMCScaling = 1f / scaledResolution.getScaleFactor() * 1;
            }
        }
        calculateHoveredSlot(HALF_WIDTH, HALF_HEIGHT, configureEnabled);

        GL11.glTranslatef(HALF_WIDTH, HALF_HEIGHT, 0);
        GL11.glScalef(undoMCScaling, undoMCScaling, 0);


//        GL11.glPushMatrix();
//        glTranslatef(190, -100, 0);
//
//        int mousX = Mouse.getX();
//        int mousY = mc.displayHeight - Mouse.getY();
//
//        int adjustedMouseX = (int) ((mouseX - HALF_WIDTH) * 1 - 190);
//        int adjustedMouseY = (int) ((mouseY - HALF_HEIGHT) * 1 + 100);
//        super.drawScreen((int) (adjustedMouseX), (int) (adjustedMouseY), partialTicks);
//        GL11.glPopMatrix();


//        if(scaledResolution.getScaleFactor() == 1) {
//            float undoMCScaling = 1f / scaledResolution.getScaleFactor();
//            GL11.glScalef(undoMCScaling, undoMCScaling, 0);
//        }
        GL11.glScalef(guiAnimationScale, guiAnimationScale, 0);
        float scale = 1.4f;
        GL11.glScalef(scale, scale, 0);
        //  new Color(0x8f8a86,0.5f).glColor();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        for (int i = 0; i < 6; i++) {
            FormWheelData data = wheelSlot[i].data;
            GL11.glPushMatrix();
//            GL11.glTranslatef(0, , 0);


            GL11.glRotatef(i * -60, 0, 0, 1);
            float segmentScale = 1f + 0.1f * wheelSlot[i].getSegmentScale();
            GL11.glScalef(segmentScale, segmentScale, 0);

            if (i % 3 == 0) {
                GL11.glTranslatef(0, -80f, 0);
            } else {
                GL11.glTranslatef(0, -95f, 0);
            }


            GL11.glRotatef(i * 60, 0, 0, 1);
            if (i == 0 || i == 3) {

            } else if (i == 1 || i == 2) {
                GL11.glTranslatef(10, 0, 0);
            } else {
                GL11.glTranslatef(-10, 0, 0);
            }
            wheelSlot[i].draw();

            if (i == 0) {
//                GL11.glTranslatef(0, 5, 0);
            } else if (i == 3) {
//                GL11.glTranslatef(0, -5, 0);
            } else if (i == 1 || i == 5) {
                GL11.glTranslatef(0, 10, 0);
            } else {
                GL11.glTranslatef(0, -10, 0);
            }
            if (data.formID != -1) {

                if (ConfigDBCClient.AlteranteSelectionWheelTexture) {
                    glScaled(0.7, 0.7, 1);
                    if (i == 0) {
                        glTranslatef(0, -15f, 0);
                    } else if (i == 1) {
                        glTranslatef(-17, -5, 0);
                    } else if (i == 2) {
                        glTranslatef(-11, 3, 0);
                    } else if (i == 3) {
                        glTranslatef(0, 12f, 0);
                    } else if (i == 4) {
                        glTranslatef(10, 3, 0);
                    } else {
                        glTranslatef(13, -5, 0);
                    }

                }

                drawCenteredString(fontRendererObj, getFormName(i), 0, 0, 0xFFFFFFFF);
            }

            GL11.glPopMatrix();
        }

        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPushMatrix();
        GL11.glTranslatef(HALF_WIDTH, HALF_HEIGHT, 0);
        GL11.glScalef(undoMCScaling, undoMCScaling, undoMCScaling);
        float guiVariantScale = (FormWheelSegment.variant == 0 ? 0.75f : 0.9f);
        float playerScale = guiAnimationScale * guiVariantScale * (ConfigDBCClient.AlteranteSelectionWheelTexture ? 1.5f : 1);
        GL11.glScalef(playerScale, playerScale, playerScale);
        GL11.glTranslatef(-HALF_WIDTH, -HALF_HEIGHT + (ConfigDBCClient.AlteranteSelectionWheelTexture ? 5 : 0), 0);


        renderPlayer(mouseX, mouseY);
        GL11.glPopMatrix();
//        String text = mouseX + "," + mouseY + ", " + hoveredSlot + "," + (keyDown ? "HOLDING KEY" : "NOT HOLDING");
//        drawCenteredString(fontRendererObj, text, mouseX, mouseY, 0xFFFFFFFF);

    }

    @Override
    protected void drawBackground() {
        //  super.drawBackground();

        int xPosGradient = 0;
        int yPosGradient = 0;
        //    drawGradientRect(xPosGradient, yPosGradient, 100 + xPosGradient, 223 + yPosGradient, 0xc0101010, 0xd0101010);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

    }

    public void drawDefaultBackground() {
        //     super.drawDefaultBackground();
    }


    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);

    }

    public boolean isMouseOverRenderer(int x, int y) {
        int width = this.width / 2;
        int height = this.height / 2;
        return x >= width - 60 && x <= width + 60 && y >= height - 90 && y <= height + 90;
    }

    public void renderPlayer(int i, int j) {

        if (isMouseOverRenderer(i, j)) {
//            zoomed += Mouse.getDWheel() * 0.035f;
//            if (zoomed > 100)
//                zoomed = 100;
//            if (zoomed < 10)
//                zoomed = 10;

            if (Mouse.isButtonDown(0)) {
                rotation -= Mouse.getDX() * 0.75f;
            }
        }

        renderingPlayer = true;
        EntityLivingBase entity = mc.thePlayer;
        DBCData data = DBCData.getClient();

        int l = this.width / 2;
        int i1 = this.height / 2 + 60;

        float oldLimbSwing = entity.limbSwingAmount;
        boolean isSneaking = entity.isSneaking(), isInvisible = entity.isInvisible(), isImmunetoFire = entity.isImmuneToFire;
        int fire = entity.fire;
        Entity oldRidingEntity = entity.ridingEntity;
        entity.limbSwingAmount = 0; // Removes moving animation
        entity.setSneaking(false); // Removes sneaking animation
        entity.ridingEntity = null; // Removes riding animation
        entity.setInvisible(false); // Removes invisibility
        entity.isImmuneToFire = true; // Prevents burning

        InventoryPlayer inv = ((EntityPlayer) entity).inventory;
        ItemStack oldItem = inv.mainInventory[inv.currentItem];
        inv.mainInventory[inv.currentItem] = null; //Removes held item

        boolean changeForm = hoveredSlot != -1;
        int oldForm = data.addonFormID;
        byte oldState = data.State, oldState2 = data.State2;
        data.addonFormID = -1; // Removes addon forms
        data.State = 0; // Removes DBC state
        data.State2 = 0; // Removes DBC state 2

        if (changeForm) {
            FormWheelData wheelData = wheelSlot[hoveredSlot].data;
            if (!wheelData.isDBC)
                data.addonFormID = wheelSlot[hoveredSlot].data.formID;
            else
                data.State = (byte) wheelData.formID;
        }


        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef(l, i1, 60F);

        GL11.glScalef(-zoomed, zoomed, zoomed);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f7 = entity.rotationYawHead;
        float f5 = (float) (l) - i;
        float f6 = (float) (i1 - 50) - j;
        GL11.glRotatef(135F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-(float) Math.atan(f6 / 800F) * 20F, 1.0F, 0.0F, 0.0F);
        entity.prevRenderYawOffset = entity.renderYawOffset = rotation;
        entity.prevRotationYaw = entity.rotationYaw = (float) Math.atan(f5 / 80F) * 40F + rotation;
        entity.rotationPitch = -(float) Math.atan(f6 / 80F) * 20F;
        entity.prevRotationYawHead = entity.rotationYawHead = entity.rotationYaw;
        GL11.glTranslatef(0.0F, entity.yOffset, 1F);
        RenderManager.instance.playerViewY = 180F;


        // Render Entity
        try {
            RenderManager.instance.renderEntityWithPosYaw(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F);
        } catch (Exception ignored) {
        }

        entity.prevRenderYawOffset = entity.renderYawOffset = f2;
        entity.prevRotationYaw = entity.rotationYaw = f3;
        entity.rotationPitch = f4;
        entity.prevRotationYawHead = entity.rotationYawHead = f7;

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPopMatrix();


        data.addonFormID = oldForm;
        data.State = oldState;
        data.State2 = oldState2;

        entity.limbSwingAmount = oldLimbSwing;
        entity.setSneaking(isSneaking);
        entity.ridingEntity = oldRidingEntity;
        entity.setInvisible(isInvisible);
        entity.isImmuneToFire = isImmunetoFire;
        inv.mainInventory[inv.currentItem] = oldItem;
        renderingPlayer = false;

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


    /**
     * Called when the mouse is clicked.
     */
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        boolean enoughTimeSinceClose = Minecraft.getSystemTime() - timeClosedSubGui > 50;
        if (configureEnabled && enoughTimeSinceClose) {
            calculateHoveredSlot((float) this.width / 2, (float) this.height / 2, false);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
