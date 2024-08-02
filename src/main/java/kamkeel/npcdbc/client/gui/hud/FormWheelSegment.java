package kamkeel.npcdbc.client.gui.hud;

import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.FormWheelData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.form.DBCSaveFormWheel;
import kamkeel.npcdbc.network.packets.form.DBCSelectForm;
import net.minecraft.client.Minecraft;

class FormWheelSegment extends WheelSegment {
    public HUDFormWheel parent;
    public Form form;
    public int formID;
    public FormWheelData data = new FormWheelData();

    FormWheelSegment(HUDFormWheel parent, int index) {
        this(parent, 0, 0, index);

    }

    FormWheelSegment(HUDFormWheel parent, int posX, int posY, int index) {
        super(index);
        this.parent = parent;
        this.posX = posX;
        this.posY = posY;
        this.index = data.slot = index;
    }

    public void selectForm() {
        PacketHandler.Instance.sendToServer(new DBCSelectForm(data.formID).generatePacket());
    }

    public void setForm(int formID, boolean isDBC, boolean updateServer) {
        data.formID = formID;
        data.isDBC = isDBC;
        form = !data.isDBC ? (Form) FormController.getInstance().get(data.formID) : null;
        if (updateServer)
            PacketHandler.Instance.sendToServer(new DBCSaveFormWheel(index, data).generatePacket());
    }

    public void setForm(FormWheelData data, boolean updateServer) {
        this.data = data;
        form = !data.isDBC ? (Form) FormController.getInstance().get(data.formID) : null;
        if (updateServer)
            PacketHandler.Instance.sendToServer(new DBCSaveFormWheel(index, data).generatePacket());
    }

    public void removeForm() {
        data.reset();
        form = null;
        PacketHandler.Instance.sendToServer(new DBCSaveFormWheel(index, data).generatePacket());
        if (parent.hoveredSlot == index)
            parent.selectSlot(-1);

        parent.timeClosedSubGui = Minecraft.getSystemTime();

    }


}
