package kamkeel.npcdbc.data.effects;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import noppes.npcs.api.handler.data.ICustomEffect;
import noppes.npcs.controllers.data.CustomEffect;

public class AddonEffect extends CustomEffect {

    public String langName;

    public AddonEffect() {
        icon = CustomNpcPlusDBC.ID + ":textures/gui/icons.png";
        index = 1;
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

    public String getLangName() {
        return this.langName;
    }

    public ICustomEffect save() {
        return this;
    }
}
