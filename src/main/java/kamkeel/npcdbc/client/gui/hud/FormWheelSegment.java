package kamkeel.npcdbc.client.gui.hud;

import kamkeel.npcdbc.client.utils.Color;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.form.DBCSaveFormWheel;
import kamkeel.npcdbc.network.packets.form.DBCSelectForm;
import net.minecraft.client.Minecraft;

class FormWheelSegment extends WheelSegment {

    public HUDFormWheel parent;
    public Form form;
    public int formID;

    public static Color HOVERED = new Color(0xADD8E6, 0.7f);
    public static Color NOT_HOVERED = new Color(0xFFFFFF, 0.35f);

    private boolean isHovered = false;
    private long startHoverTime = 0;
    private long stopHoverTime = 0;
    private float hoverScale = 0;
    private Color currentColor;

    FormWheelSegment(HUDFormWheel parent, int index) {
        this(parent, 0, 0, index);

    }

    FormWheelSegment(HUDFormWheel parent, int posX, int posY, int index) {
        super(index);
        this.parent = parent;
        this.posX = posX;
        this.posY = posY;
        this.index = index;

        this.currentColor = NOT_HOVERED;
    }

    @Override
    public void draw() {
        currentColor = Color.lerpRGBA(NOT_HOVERED, HOVERED, hoverScale);
        currentColor.glColor();
        super.draw();
    }


    public void selectForm() {
        PacketHandler.Instance.sendToServer(new DBCSelectForm(formID).generatePacket());

    }

    public void setForm(int formID, boolean updateServer) {
        this.formID = formID;
        form = (Form) FormController.getInstance().get(formID);
        if (updateServer)
            PacketHandler.Instance.sendToServer(new DBCSaveFormWheel(index, formID).generatePacket());
    }

    public void removeForm() {
        formID = -1;
        form = null;
        PacketHandler.Instance.sendToServer(new DBCSaveFormWheel(index, -1).generatePacket());
    }

    public void setHoveredState(boolean newHoverState) {

        if (!isHovered && newHoverState) {
            startHoverTime = Minecraft.getSystemTime();
        }
        if (isHovered && !newHoverState) {
            stopHoverTime = Minecraft.getSystemTime();
        }
        isHovered = newHoverState;
    }

    public float getSegmentScale() {
        float updateTime;
        if (isHovered) {
            updateTime = (float) (Minecraft.getSystemTime() - startHoverTime) / 100;
            updateTime = Math.min(updateTime, 1);
            hoverScale = (hoverScale + 0.25f * (updateTime - hoverScale));
        } else {
            updateTime = (float) (Minecraft.getSystemTime() - stopHoverTime) / 100;
            updateTime = -Math.min(updateTime, 1);
            hoverScale = (hoverScale + 0.25f * (1 + updateTime - hoverScale));
        }

        hoverScale = Math.min(1, Math.max(hoverScale, 0));

        return hoverScale;
    }

}
