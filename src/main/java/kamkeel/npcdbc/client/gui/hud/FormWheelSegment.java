package kamkeel.npcdbc.client.gui.hud;

import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.form.DBCSaveFormWheel;
import kamkeel.npcdbc.network.packets.form.DBCSelectForm;

class FormWheelSegment extends WheelSegment {

    public HUDFormWheel parent;
    public Form form;
    public int formID;

    FormWheelSegment(HUDFormWheel parent, int index) {
        this(parent, 0, 0, index);

    }

    FormWheelSegment(HUDFormWheel parent, int posX, int posY, int index) {
        super(index);
        this.parent = parent;
        this.posX = posX;
        this.posY = posY;
        this.index = index;
    }


    public void selectForm() {
        if (form != null)
            PacketHandler.Instance.sendToServer(new DBCSelectForm(form.id).generatePacket());

    }

    public void reset() {
        form = null;
        formID = -1;
        PacketHandler.Instance.sendToServer(new DBCSaveFormWheel(index, -1).generatePacket());
    }

}
