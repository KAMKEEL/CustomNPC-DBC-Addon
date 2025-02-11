package kamkeel.npcdbc.data.statuseffect;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import noppes.npcs.controllers.data.CustomEffect;

public class StatusEffect extends CustomEffect {
    public StatusEffect() {
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
    }

    public int getWidth() {
        return 16;
    }

    /**
     * DO NOT OVERRIDE
     */
    public int getHeight() {
        return 16;
    }

    public String getIcon() {
        return this.icon;
    }
}
