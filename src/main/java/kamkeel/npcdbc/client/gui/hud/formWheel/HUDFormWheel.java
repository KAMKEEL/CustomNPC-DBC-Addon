package kamkeel.npcdbc.client.gui.hud.formWheel;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.client.ClientProxy;
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
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.ISubGuiListener;
import noppes.npcs.client.gui.util.SubGuiInterface;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.HashMap;

import static kamkeel.npcdbc.constants.DBCForm.*;
import static org.lwjgl.opengl.GL11.*;

public class HUDFormWheel extends GuiNPCInterface implements ISubGuiListener {

    public static float BLUR_INTENSITY = 0;
    public static float MAX_BLUR = 4;
    public static boolean renderingPlayer = false;
    ScaledResolution scaledResolution;
    public FormWheelSegment[] wheelSlot = new FormWheelSegment[6];

    private float zoomed = 70, rotation;
    float guiAnimationScale = 0, animationScaleFactor, undoMCScaling = 1;
    long timeOpened, timeClosedSubGui, timeSinceM1, timeClosed;

    public int hoveredSlot = -1;
    boolean keyDown, unpressedAllKeys = false;
    boolean configureEnabled;
    public HashMap<Integer, String> dbcForms;
    public DBCData dbcData;
    public PlayerDBCInfo dbcInfo;

    public boolean isClosing;

    public static final int CLOSE_TIME = 600;
    public static final int OPEN_TIME = 1500;

    public double easeOutExpo(double x) {
        return x == 1 ? 1 : 1 - Math.pow(2, -10 * x);
    }

    public HUDFormWheel() {
        mc = Minecraft.getMinecraft();
        dbcData = DBCData.getClient();
        dbcForms = dbcData.getUnlockedDBCFormsMap();
        dbcInfo = PlayerDataUtil.getClientDBCInfo();

        for (int i = 0; i < 6; i++) {
            wheelSlot[i] = new FormWheelSegment(this, i);
            wheelSlot[i].setForm(dbcInfo.formWheel[i], false);
        }
        PacketHandler.Instance.sendToServer(new DBCRequestFormWheel().generatePacket());

        // Stops the GUI from un-pressing all keys for you.
        mc.inGameHasFocus = false;
        mc.mouseHelper.ungrabMouseCursor();

        BLUR_INTENSITY = 0;
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
        addButton(new GuiNpcButton(6, x, y, 60, 20, new String[]{"Configure", "Done"}, !configureEnabled ? 0 : 1));

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
            addButton(new GuiNpcButton(8, x - 94 - 75, y - 25, 150, 20, "Edit"));
            addButton(new GuiNpcButton(7, x += 62, y, 80, 20, "Switch Wheel"));


            x = (int) ((this.width / 2) * undoMCScaling + 190);
            y = (this.height / 2) - 100;
            if (undoMCScaling < 1) {
                x += 20;
                y -= 25;
            }


            //
            //            addLabel(new GuiNpcLabel(0, "Slot 1", x, y + 5));
            //            getLabel(0).color = 14737632;
            //            addButton(new GuiNpcButton(100, x + 80, y, 16, 20, "X"));
            //            addButton(new GuiNpcButton(0, x, y, 80, 20, "general.noForm"));
            //            getButton(0).setDisplayText(wheelSlot[0].getFormName());
            //            getButton(100).enabled = wheelSlot[0].data.formID != -1;
            //
            //            y += 22;
            //            addLabel(new GuiNpcLabel(1, "Slot 2", x + 2, y + 5));
            //            getLabel(1).color = 14737632;
            //            y += 15;
            //            addButton(new GuiNpcButton(11, x + 80, y, 16, 20, "X"));
            //            addButton(new GuiNpcButton(1, x, y, 80, 20, "general.noForm"));
            //            getButton(1).setDisplayText(wheelSlot[1].getFormName());
            //            getButton(11).enabled = wheelSlot[1].data.formID != -1;
            //
            //            y += 22;
            //            addLabel(new GuiNpcLabel(2, "Slot 3", x + 2, y + 5));
            //            getLabel(2).color = 14737632;
            //            y += 15;
            //            addButton(new GuiNpcButton(22, x + 80, y, 16, 20, "X"));
            //            addButton(new GuiNpcButton(2, x, y, 80, 20, "general.noForm"));
            //            getButton(2).setDisplayText(wheelSlot[2].getFormName());
            //            getButton(22).enabled = wheelSlot[2].data.formID != -1;
            //
            //            y += 22;
            //            addLabel(new GuiNpcLabel(3, "Slot 4", x + 2, y + 5));
            //            getLabel(3).color = 14737632;
            //            y += 15;
            //            addButton(new GuiNpcButton(33, x + 80, y, 16, 20, "X"));
            //            addButton(new GuiNpcButton(3, x, y, 80, 20, "general.noForm"));
            //            getButton(3).setDisplayText(wheelSlot[3].getFormName());
            //            getButton(33).enabled = wheelSlot[3].data.formID != -1;
            //
            //            y += 22;
            //            addLabel(new GuiNpcLabel(4, "Slot 5", x + 2, y + 5));
            //            getLabel(4).color = 14737632;
            //            y += 15;
            //            addButton(new GuiNpcButton(44, x + 80, y, 16, 20, "X"));
            //            addButton(new GuiNpcButton(4, x, y, 80, 20, "general.noForm"));
            //            getButton(4).setDisplayText(wheelSlot[4].getFormName());
            //            getButton(44).enabled = wheelSlot[4].data.formID != -1;
            //
            //            y += 22;
            //            addLabel(new GuiNpcLabel(5, "Slot 6", x + 2, y + 5));
            //            getLabel(5).color = 14737632;
            //            y += 15;
            //            addButton(new GuiNpcButton(55, x + 80, y, 16, 20, "X"));
            //            addButton(new GuiNpcButton(5, x, y, 80, 20, "general.noForm"));
            //            getButton(5).setDisplayText(wheelSlot[5].getFormName());
            //            getButton(55).enabled = wheelSlot[5].data.formID != -1;


        }


        //        BLUR_INTENSITY = 0;
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
        if (button.id == 8) {
            this.setSubGui(new SubGuiSelectForm(button.id, true, true).displayDBCForms(DBCData.getClient()));
        }
        initGui();
    }

    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof SubGuiSelectForm) {
            SubGuiSelectForm selectForm = ((SubGuiSelectForm) subgui);
            if (selectForm.confirmed) {
                int slotID = selectForm.buttonID == 8 ? hoveredSlot : selectForm.buttonID;

                FormWheelSegment slot = wheelSlot[slotID];
                Form form = slot.form;

                selectSlot(slotID);
                timeClosedSubGui = Minecraft.getSystemTime();

                if (form != null && selectForm.selectedFormID == form.id)
                    return;


                slot.setForm(selectForm.selectedFormID, selectForm.isDBC, true);
            } else if (selectForm.removeForm) {
                int slotID = selectForm.buttonID == 8 ? hoveredSlot : selectForm.buttonID;
                FormWheelSegment slot = wheelSlot[slotID];

                selectSlot(slotID);

                slot.removeForm();
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
        if (mc.thePlayer.ticksExisted % 10 == 0)
            dbcForms = dbcData.getUnlockedDBCFormsMap();

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
            if (!isClosing && mc.inGameHasFocus) {
                mc.inGameHasFocus = false;
                Mouse.setGrabbed(false);
            }
        }

        int code = KeyHandler.FormWheelKey.getKeyCode();
        keyDown = isMouseButton() ? Mouse.isButtonDown(code + 100) : Keyboard.isKeyDown(code);
        if (!keyDown && !hasSubGui() && !configureEnabled && !isClosing) {
            if (hoveredSlot != -1)
                wheelSlot[hoveredSlot].selectForm();

            mc.inGameHasFocus = true;
            mc.mouseHelper.grabMouseCursor();
            isClosing = true;
            timeClosed = (long) (Minecraft.getSystemTime() - (1 - guiAnimationScale) * CLOSE_TIME);
        }
    }

    public static boolean isMouseButton() {
        return KeyHandler.FormWheelKey.getKeyCode() < 0;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        if (!hasSubGui()) {
            int mouseScrolled = Mouse.getDWheel();

            if (mouseScrolled != 0 && hoveredSlot != -1) {
                int newForm = -1;
                FormWheelSegment slot = wheelSlot[hoveredSlot];
                Form form = slot.form;
                if (mouseScrolled > 0) {
                    if (form != null && form.hasParent() && dbcInfo.hasFormUnlocked(form.parentID))
                        newForm = form.parentID;
                    else if (slot.data.isDBC) {
                        newForm = DBCForm.getParent(dbcData.Race, slot.data.formID, dbcData);
                        if (!dbcData.isDBCFormUnlocked(newForm))
                            newForm = -1;
                    }
                } else {
                    if (form != null && form.hasChild() && dbcInfo.hasFormUnlocked(form.childID))
                        newForm = form.childID;
                    else if (slot.data.isDBC) {
                        newForm = DBCForm.getChild(dbcData.Race, slot.data.formID, dbcData);
                        if (!dbcData.isDBCFormUnlocked(newForm))
                            newForm = -1;
                    }
                }

                if (newForm != -1)
                    slot.setForm(newForm, slot.data.isDBC, true);
            }
        }
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
            FormWheelData data = wheelSlot[i].data;
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
            if (i == 1 || i == 2) {
                GL11.glTranslatef(10, 0, 0);
            } else if (i == 4 || i == 5) {
                GL11.glTranslatef(-10, 0, 0);
            }
            wheelSlot[i].draw(fontRendererObj);


            glPopMatrix();
        }

        glPopMatrix();
        GL11.glDisable(GL11.GL_BLEND);

        glPushMatrix();
        GL11.glTranslatef(HALF_WIDTH, HALF_HEIGHT, 0);
        GL11.glScalef(undoMCScaling, undoMCScaling, undoMCScaling);
        float guiVariantScale = (FormWheelSegment.variant == 0 ? 0.75f : 0.9f);
        float playerScale = guiAnimationScale * guiVariantScale * (ConfigDBCClient.AlteranteSelectionWheelTexture ? 1.5f : 1);
        GL11.glScalef(playerScale, playerScale, playerScale);
        GL11.glTranslatef(-HALF_WIDTH, -HALF_HEIGHT + (ConfigDBCClient.AlteranteSelectionWheelTexture ? 8 : 0), 0);

        renderPlayer(mouseX, mouseY, partialTicks);

        glPopMatrix();

        glPopMatrix();

        super.

            drawScreen(mouseX, mouseY, partialTicks);
        //        String text = mouseX + "," + mouseY + ", " + hoveredSlot + "," + (keyDown ? "HOLDING KEY" : "NOT HOLDING");
        //        drawCenteredString(fontRendererObj, text, mouseX, mouseY, 0xFFFFFFFF);
    }

    public void drawDefaultBackground() {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
        boolean enoughTimeSinceClose = Minecraft.getSystemTime() - timeClosedSubGui > 50;
        if (keyCode == 1 && configureEnabled && enoughTimeSinceClose)
            configureEnabled = false;
    }

    public boolean isMouseOverRenderer(int x, int y) {
        int width = this.width / 2;
        int height = this.height / 2;
        return x >= width - 60 && x <= width + 60 && y >= height - 90 && y <= height + 90;
    }

    public void renderPlayer(int i, int j, float partialTicks) {
        if (isMouseOverRenderer(i, j) && Mouse.isButtonDown(0)) {
            rotation -= Mouse.getDX() * 0.75f;
        }

        renderingPlayer = true;
        EntityLivingBase entity = mc.thePlayer;
        DBCData data = DBCData.getClient();

        int l = this.width / 2;
        int i1 = this.height / 2 + 60;

        float oldLimbSwing = entity.limbSwingAmount;
        boolean isInvisible = entity.isInvisible(), isImmunetoFire = entity.isImmuneToFire;
        Entity oldRidingEntity = entity.ridingEntity;
        entity.limbSwingAmount = entity.prevLimbSwingAmount = 0; // Removes moving animation
        entity.ridingEntity = null; // Removes riding animation
        entity.setInvisible(false); // Removes invisibility
        entity.isImmuneToFire = true; // Prevents burning

        InventoryPlayer inv = ((EntityPlayer) entity).inventory;
        ItemStack oldItem = inv.mainInventory[inv.currentItem];
        inv.mainInventory[inv.currentItem] = null; //Removes held item

        boolean changeForm = hoveredSlot != -1, isGoD = false, isKaioken = false, isUI = false;
        int oldForm = data.addonFormID;
        byte oldState = data.State, oldState2 = data.State2;
        data.addonFormID = -1; // Removes addon forms
        data.State = 0; // Removes DBC state
        data.State2 = 0; // Removes DBC state 2

        if (changeForm) {
            FormWheelData wheelData = wheelSlot[hoveredSlot].data;
            int id = wheelData.formID;

            if (!wheelData.isDBC)
                data.addonFormID = wheelSlot[hoveredSlot].data.formID;
            else if (id < 20)
                data.State = (byte) id;
            else if (isKaioken = id >= Kaioken && id <= Kaioken6) {
                data.renderKK = true;
                data.State2 = (byte) (id - Kaioken + 1);
            } else if (isUI = id >= UltraInstinct && id <= UltraInstinct + 10) {
                data.renderUI = true;
                data.State2 = (byte) (id - UltraInstinct + 1);
            } else if (isGoD = id == GodOfDestruction) {
                data.renderGoD = true;
            }
        }


        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        glPushMatrix();
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
        entity.rotationPitch = entity.prevRotationPitch = -(float) Math.atan(f6 / 80F) * 20F;
        entity.prevRotationYawHead = entity.rotationYawHead = entity.rotationYaw;
        GL11.glTranslatef(0.0F, entity.yOffset, 1F);
        RenderManager.instance.playerViewY = 180F;


        // Render Entity
        try {
            RenderManager.instance.renderEntityWithPosYaw(entity, 0.0, 0.0, 0.0, 0.0F, partialTicks);
        } catch (Exception ignored) {
        }

        entity.prevRenderYawOffset = entity.renderYawOffset = f2;
        entity.prevRotationYaw = entity.rotationYaw = f3;
        entity.rotationPitch = entity.prevRotationPitch = f4;
        entity.prevRotationYawHead = entity.rotationYawHead = f7;

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        glPopMatrix();


        data.addonFormID = oldForm;
        data.State = oldState;
        data.State2 = oldState2;
        if (isKaioken)
            data.renderKK = false;
        else if (isUI)
            data.renderUI = false;
        else if (isGoD)
            data.renderGoD = false;

        entity.limbSwingAmount = entity.prevLimbSwingAmount = oldLimbSwing;
        entity.ridingEntity = oldRidingEntity;
        entity.setInvisible(isInvisible);
        entity.isImmuneToFire = isImmunetoFire;
        inv.mainInventory[inv.currentItem] = oldItem;
        renderingPlayer = false;

        if (ClientProxy.lastRendererGUIPlayerID >= 0 && JRMCoreH.data2.length > ClientProxy.lastRendererGUIPlayerID)
            JRMCoreH.data2[ClientProxy.lastRendererGUIPlayerID] = data.State + ";" + data.State2;
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
        if (configureEnabled && !hasSubGui() && enoughTimeSinceClose) {
            calculateHoveredSlot((float) this.width / 2, (float) this.height / 2, false);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
