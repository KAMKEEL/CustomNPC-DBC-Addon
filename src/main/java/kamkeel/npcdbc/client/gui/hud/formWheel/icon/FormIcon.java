package kamkeel.npcdbc.client.gui.hud.formWheel.icon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FormIcon {
    public FormIconData bodyType;
    public FormIconData hairType;
    public FormIconData auraType;

    public int width, height;

    FormIcon(){

    }

    public void draw(){

    }

    public FormIcon setHairType(FormIconData object) {
        this.hairType = object;

        return this;
    }
    public FormIcon setAuraType(FormIconData object) {
        this.auraType = object;

        return this;
    }
    public FormIcon setBodyType(FormIconData object) {
        this.bodyType = object;

        return this;
    }

}
