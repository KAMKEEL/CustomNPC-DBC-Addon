package kamkeel.npcdbc.client.gui.hud;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.KeyHandler;
import kamkeel.npcdbc.client.gui.component.SubGuiSelectForm;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.form.DBCRequestFormWheel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.NBTTags;
import noppes.npcs.client.gui.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HUDFormWheel extends GuiNPCInterface implements IGuiData, ISubGuiListener {

    public static float BLUR_INTENSITY = 0;
    public static float MAX_BLUR = 3;
    List<Gui> guiList = new ArrayList<>();
    ResourceLocation resourceLocation = new ResourceLocation(CustomNpcPlusDBC.ID + ":/textures/gui/hud/formwheel/GuiWheel.png");
    public FormWheelSegment[] wheelSlot = new FormWheelSegment[6];

    private float zoomed = 70, rotation;
    float guiAnimationScale = 0, animationScaleFactor;
    long timeOpened;

    public int hoveredSlot = -1;


    public HUDFormWheel() {
        setBackground("menubg.png");

        for (int i = 0; i < 6; i++)
            wheelSlot[i] = new FormWheelSegment(this, i);
        PacketHandler.Instance.sendToServer(new DBCRequestFormWheel().generatePacket());
    }

    @Override
    public void initGui() {
        // Prevents replaying the open animation on screen resize
        if (timeOpened == 0)
            timeOpened = Minecraft.getSystemTime();

        int y = guiTop + 5;
        int guiX = guiTop + 4;

        Form form = wheelSlot[0].form;
        addLabel(new GuiNpcLabel(0, "Slot 1", guiX + 2, y + 5));
        getLabel(0).color = 0xffffff;
        addButton(new GuiNpcButton(0, guiX, y += 15, 80, 20, "general.noForm"));
        if (form != null)
            getButton(0).setDisplayText(form.getMenuName());
        addButton(new GuiNpcButton(100, guiX + 80, y, 16, 20, "X"));
        getButton(100).enabled = form != null;

        y += 22;
        form = wheelSlot[1].form;
        addLabel(new GuiNpcLabel(1, "Slot 2", guiX + 2, y + 5));
        getLabel(1).color = 0xffffff;
        addButton(new GuiNpcButton(1, guiX, y += 15, 80, 20, "general.noForm"));
        if (form != null)
            getButton(1).setDisplayText(form.getMenuName());
        addButton(new GuiNpcButton(11, guiX + 80, y, 16, 20, "X"));
        getButton(11).enabled = form != null;

        y += 22;
        form = wheelSlot[2].form;
        addLabel(new GuiNpcLabel(2, "Slot 3", guiX + 2, y + 5));
        getLabel(2).color = 0xffffff;
        addButton(new GuiNpcButton(2, guiX, y += 15, 80, 20, "general.noForm"));
        if (form != null)
            getButton(2).setDisplayText(form.getMenuName());
        addButton(new GuiNpcButton(22, guiX + 80, y, 16, 20, "X"));
        getButton(22).enabled = form != null;

        y += 22;
        form = wheelSlot[3].form;
        addLabel(new GuiNpcLabel(3, "Slot 4", guiX + 2, y + 5));
        getLabel(3).color = 0xffffff;
        addButton(new GuiNpcButton(3, guiX, y += 15, 80, 20, "general.noForm"));
        if (form != null)
            getButton(3).setDisplayText(form.getMenuName());
        addButton(new GuiNpcButton(33, guiX + 80, y, 16, 20, "X"));
        getButton(33).enabled = form != null;

        y += 22;
        form = wheelSlot[4].form;
        addLabel(new GuiNpcLabel(4, "Slot 5", guiX + 2, y + 5));
        getLabel(4).color = 0xffffff;
        addButton(new GuiNpcButton(4, guiX, y += 15, 80, 20, "general.noForm"));
        if (form != null)
            getButton(4).setDisplayText(form.getMenuName());
        addButton(new GuiNpcButton(44, guiX + 80, y, 16, 20, "X"));
        getButton(44).enabled = form != null;

        y += 22;
        form = wheelSlot[5].form;
        addLabel(new GuiNpcLabel(5, "Slot 6", guiX + 2, y + 5));
        getLabel(5).color = 0xffffff;
        addButton(new GuiNpcButton(5, guiX, y += 15, 80, 20, "general.noForm"));
        if (form != null)
            getButton(5).setDisplayText(form.getMenuName());
        addButton(new GuiNpcButton(55, guiX + 80, y, 16, 20, "X"));
        getButton(55).enabled = form != null;


        BLUR_INTENSITY = 0;
    }


    @Override
    public void setGuiData(NBTTagCompound compound) {
        Map<Integer, Integer> playerWheel = NBTTags.getIntegerIntegerMap(compound.getTagList("FormWheel", 10));
        for (int i = 0; i < 6; i++) {
            int formID = playerWheel.get(i);
            wheelSlot[i].formID = formID;
            wheelSlot[i].form = (Form) FormController.getInstance().get(formID);
        }

        initGui();
    }

    public void buttonEvent(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (button.id <= 6) {
            this.setSubGui(new SubGuiSelectForm(button.id, true));
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
                if (form != null && selectForm.selectedFormID == form.id)
                    return;

                slot.setForm(selectForm.selectedFormID);
            }
        }
        initGui();
    }


    @Override
    public void updateScreen() {
        if (!Keyboard.isKeyDown(KeyHandler.FormWheelKey.getKeyCode())) {
            if (hoveredSlot != -1)
                wheelSlot[hoveredSlot].selectForm();

            close();
        }
        float updateTime = (float) (Minecraft.getSystemTime() - timeOpened) / 250;
        animationScaleFactor = Math.min(updateTime, 1);
        guiAnimationScale = (guiAnimationScale + 0.25f * (animationScaleFactor - guiAnimationScale));
        BLUR_INTENSITY = guiAnimationScale * MAX_BLUR;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (hasSubGui())
            return;

        ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int gradientColor = ((int) (255 * 0.2f * guiAnimationScale) << 24);
        this.drawGradientRect(0, 0, this.width, this.height, gradientColor, gradientColor);
        //       hoveredSlot = -1;
        double width = 124;
        double height = 124 - 28;
        GL11.glPushMatrix();
//        GL11.glTranslatef((float) (-width/2), -28.0f - 69f, 0);

//        WheelSegment segment = new WheelSegment(2);

        final float HALF_WIDTH = (float) this.width / 2;
        final float HALF_HEIGHT = (float) this.height / 2;

        final float deltaX = HALF_WIDTH - mouseX;
        final float deltaY = HALF_HEIGHT - mouseY;

        float undoMCScaling = 1;
        switch (scaledResolution.getScaleFactor()) {
            case 1:
                undoMCScaling = 1f / scaledResolution.getScaleFactor();
                break;
            case 2:
                if (mc.displayHeight < 720) {
                    undoMCScaling = 1f / scaledResolution.getScaleFactor() * 1.5f;
                }
                break;
        }
        float radius = 74 * undoMCScaling;
        if (Math.sqrt(deltaX * deltaX + deltaY * deltaY) > radius) {
            final float radians = (float) Math.atan2(deltaY, deltaX);
            final float degree = Math.round(radians * (180 / Math.PI));

            hoveredSlot = (int) ((degree - 180) / -60) - 1;
            if (hoveredSlot == -1)
                hoveredSlot = 5;
        }


        GL11.glTranslatef(HALF_WIDTH, HALF_HEIGHT, 0);

//        if(scaledResolution.getScaleFactor() == 1) {
//            float undoMCScaling = 1f / scaledResolution.getScaleFactor();
//            GL11.glScalef(undoMCScaling, undoMCScaling, 0);
//        }
        GL11.glScalef(guiAnimationScale, guiAnimationScale, 0);


        GL11.glScalef(undoMCScaling, undoMCScaling, 0);
        float scale = 1.4f;
        GL11.glScalef(scale, scale, 0);
        //  new Color(0x8f8a86,0.5f).glColor();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        for (int i = 0; i < 6; i++) {
            Form form = wheelSlot[i].form;
            GL11.glPushMatrix();
//            GL11.glTranslatef(0, , 0);


            GL11.glRotatef(i * -60, 0, 0, 1);
            if (i == hoveredSlot) {
                GL11.glScalef(1.1f, 1.1f, 0);
                GL11.glColor4f(173f / 255, 216f / 255, 230f / 255, 0.9f);
            } else {
                GL11.glColor4f(1, 1, 1, 0.7f);
            }
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
            WheelSegment.variant = 1;
            wheelSlot[i].draw();
            if (form != null)
                drawCenteredString(fontRendererObj, form.menuName, 0, 0, 0xFFFFFFFF);

            GL11.glPopMatrix();
        }

        GL11.glPopMatrix();
        String text = mouseX + "," + mouseY + ", " + hoveredSlot;
        drawCenteredString(fontRendererObj, text, mouseX, mouseY, 0xFFFFFFFF);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPushMatrix();
        GL11.glTranslatef(HALF_WIDTH, HALF_HEIGHT, 0);
        GL11.glScalef(animationScaleFactor, animationScaleFactor, animationScaleFactor);
        GL11.glTranslatef(-HALF_WIDTH, -HALF_HEIGHT, 0);
        renderPlayer(mouseX, mouseY);
        GL11.glPopMatrix();

    }

    @Override
    protected void drawBackground() {
        //  super.drawBackground();

        int xPosGradient = 3;
        int yPosGradient = 6;
        drawGradientRect(xPosGradient, yPosGradient, 100 + xPosGradient, 236 + yPosGradient, 0xc0101010, 0xd0101010);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

    }

    public void drawDefaultBackground() {
        //     super.drawDefaultBackground();
    }


    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
        if (keyCode == 1)
            close();

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

            if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
                rotation -= Mouse.getDX() * 0.75f;
            }
        }


        EntityLivingBase entity = mc.thePlayer;
        DBCData data = DBCData.getClient();

        int l = this.width / 2;
        int i1 = this.height / 2 + 60;

        boolean changeForm = hoveredSlot != -1;
        int oldForm = data.addonFormID;
        data.addonFormID = -1;
        if (changeForm) {
            data.addonFormID = wheelSlot[hoveredSlot].formID;
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


    }

    @Override
    public void save() {
    }

    /**
     * Called when the mouse is clicked.
     */
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
