package kamkeel.npcdbc.data.effects;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import noppes.npcs.controllers.data.CustomEffect;

public class AddonEffect extends CustomEffect {

    public String langName;

    public AddonEffect() {
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
    }

    public int getWidth() {
        return 16;
    }
    public int getHeight() {
        return 16;
    }

    public String getIcon() {
        return this.icon;
    }

    public String getLangName(){ return this.langName; }
}
