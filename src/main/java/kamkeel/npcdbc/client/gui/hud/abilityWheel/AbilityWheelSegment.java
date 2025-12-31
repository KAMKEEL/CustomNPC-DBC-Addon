package kamkeel.npcdbc.client.gui.hud.abilityWheel;

import kamkeel.npcdbc.client.gui.hud.WheelSegment;
import kamkeel.npcdbc.client.gui.hud.formWheel.icon.FormIcon;
import kamkeel.npcdbc.data.FormWheelData;
import kamkeel.npcdbc.data.form.Form;
import net.minecraft.client.gui.FontRenderer;

public class AbilityWheelSegment extends WheelSegment {
    public HUDAbilityWheel parent;
    public Form form;
    public FormWheelData data = new FormWheelData();
    private FormIcon icon = null;

    AbilityWheelSegment(HUDAbilityWheel parent, int index) {
        this(parent, 0, 0, index);

    }

    AbilityWheelSegment(HUDAbilityWheel parent, int posX, int posY, int index) {
        super(index);
        this.parent = parent;
        this.posX = posX;
        this.posY = posY;
        this.index = data.slot = index;
    }

    @Override
    protected void drawWheelItem(FontRenderer fontRenderer) {

    }
}
