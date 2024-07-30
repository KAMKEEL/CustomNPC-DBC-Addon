package kamkeel.npcdbc.client.gui.hud;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.gui.component.SubGuiSelectForm;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.form.DBCRequestFormWheel;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.NBTTags;
import noppes.npcs.client.gui.util.*;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HUDFormWheel extends GuiNPCInterface implements IGuiData, ISubGuiListener {

    public static float BLUR_INTENSITY = 0;
    public static float MAX_BLUR = 3;
    List<Gui> guiList = new ArrayList<>();
    ResourceLocation resourceLocation = new ResourceLocation(CustomNpcPlusDBC.ID + ":/textures/gui/hud/formwheel/GuiWheel.png");
    public FormWheelSegment[] wheelSlot = new FormWheelSegment[6];

    float guiAnimationScale = 0;
    long timeOpened;

    public int hoveredSlot;

    public HUDFormWheel() {
        for (int i = 0; i < 6; i++)
            wheelSlot[i] = new FormWheelSegment(this, i);
        setBackground("menubg.png");
        PacketHandler.Instance.sendToServer(new DBCRequestFormWheel().generatePacket());
    }

    @Override
    public void initGui() {
        // Prevents replaying the open animation on screen resize
        if (timeOpened == 0)
            timeOpened = Minecraft.getSystemTime();

        int y = guiTop + 5;
        int guiX = guiTop + 4;

        PlayerDBCInfo data = PlayerDataUtil.getClientDBCInfo();

        Form form = wheelSlot[0].form;
        addLabel(new GuiNpcLabel(0, "Slot 1", guiX + 2, y + 5));
        getLabel(0).color = 0xffffff;
        addButton(new GuiNpcButton(0, guiX, y += 15, 80, 20, "general.noForm"));
        if (form != null)
            getButton(0).setDisplayText(form.getName());
        addButton(new GuiNpcButton(100, guiX + 80, y, 16, 20, "X"));
        getButton(100).enabled = form != null;

        y += 22;
        form = wheelSlot[1].form;
        addLabel(new GuiNpcLabel(1, "Slot 2", guiX + 2, y + 5));
        getLabel(1).color = 0xffffff;
        addButton(new GuiNpcButton(1, guiX, y += 15, 80, 20, "general.noForm"));
        if (form != null)
            getButton(1).setDisplayText(form.getName());
        addButton(new GuiNpcButton(11, guiX + 80, y, 16, 20, "X"));
        getButton(11).enabled = form != null;

        y += 22;
        form = wheelSlot[2].form;
        addLabel(new GuiNpcLabel(2, "Slot 3", guiX + 2, y + 5));
        getLabel(2).color = 0xffffff;
        addButton(new GuiNpcButton(2, guiX, y += 15, 80, 20, "general.noForm"));
        if (form != null)
            getButton(2).setDisplayText(form.getName());
        addButton(new GuiNpcButton(22, guiX + 80, y, 16, 20, "X"));
        getButton(22).enabled = form != null;

        y += 22;
        form = wheelSlot[3].form;
        addLabel(new GuiNpcLabel(3, "Slot 4", guiX + 2, y + 5));
        getLabel(3).color = 0xffffff;
        addButton(new GuiNpcButton(3, guiX, y += 15, 80, 20, "general.noForm"));
        if (form != null)
            getButton(3).setDisplayText(form.getName());
        addButton(new GuiNpcButton(33, guiX + 80, y, 16, 20, "X"));
        getButton(33).enabled = form != null;

        y += 22;
        form = wheelSlot[4].form;
        addLabel(new GuiNpcLabel(4, "Slot 5", guiX + 2, y + 5));
        getLabel(4).color = 0xffffff;
        addButton(new GuiNpcButton(4, guiX, y += 15, 80, 20, "general.noForm"));
        if (form != null)
            getButton(4).setDisplayText(form.getName());
        addButton(new GuiNpcButton(44, guiX + 80, y, 16, 20, "X"));
        getButton(44).enabled = form != null;

        y += 22;
        form = wheelSlot[5].form;
        addLabel(new GuiNpcLabel(5, "Slot 6", guiX + 2, y + 5));
        getLabel(5).color = 0xffffff;
        addButton(new GuiNpcButton(5, guiX, y += 15, 80, 20, "general.noForm"));
        if (form != null)
            getButton(5).setDisplayText(form.getName());
        addButton(new GuiNpcButton(55, guiX + 80, y, 16, 20, "X"));
        getButton(55).enabled = form != null;


        BLUR_INTENSITY = 0;
    }

    public void buttonEvent(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (button.id <= 6) {
            this.setSubGui(new SubGuiSelectForm(button.id,true));
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
//        if(!Keyboard.isKeyDown(KeyHandler.FormWheelKey.getKeyCode())){
//           close();
//        }
        float updateTime = (float) (Minecraft.getSystemTime() - timeOpened) / 250;
        float temp = Math.min(updateTime, 1);
        guiAnimationScale = (guiAnimationScale + 0.25f * (temp - guiAnimationScale));
        BLUR_INTENSITY = guiAnimationScale * MAX_BLUR;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

        super.drawScreen(mouseX, mouseY, partialTicks);
        if (hasSubGui())
            return;
        ;
        int gradientColor = ((int) (255 * 0.2f * guiAnimationScale) << 24);
        this.drawGradientRect(0, 0, this.width, this.height, gradientColor, gradientColor);
        int index = -1;
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

            index = (int) ((degree - 180) / -60) - 1;
            if (index == -1)
                index = 5;
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
            if (i == index) {
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
            wheelSlot[i].draw();
            if (form != null)
                drawCenteredString(fontRendererObj, form.menuName, 0, 0, 0xFFFFFFFF);

            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
        String text = mouseX + "," + mouseY;
        drawCenteredString(fontRendererObj, text, mouseX, mouseY, 0xFFFFFFFF);
        GL11.glDisable(GL11.GL_BLEND);
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
    public void setGuiData(NBTTagCompound compound) {
        Map<Integer, Integer> playerWheel = NBTTags.getIntegerIntegerMap(compound.getTagList("FormWheel", 10));
        for (int i = 0; i < 6; i++) {
            int formID = playerWheel.get(i);
            wheelSlot[i].form = (Form) FormController.getInstance().get(formID);
        }

        initGui();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
        if (keyCode == 1)
            close();

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
