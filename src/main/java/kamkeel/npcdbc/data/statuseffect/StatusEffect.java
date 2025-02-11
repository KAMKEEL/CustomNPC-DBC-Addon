package kamkeel.npcdbc.data.statuseffect;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import noppes.npcs.controllers.data.CustomEffect;
import scala.tools.nsc.doc.model.Public;

public class StatusEffect extends CustomEffect {

    public String langName;

    public StatusEffect() {
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
