package kamkeel.npcdbc.api.client.overlay;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.api.form.IForm;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;

@SideOnly(Side.CLIENT)
public interface IOverlayContext {

    boolean typeDisabled(IOverlay.Type type);

    int gender();
    boolean female();
    float age();
    float inverseAge();
    int pregnant();
    String genderDir();
    int eyeType();
    boolean eyebrows();
    int furType();
    boolean hairType(String type);
    int color(String type);
    Color color(IOverlay.ColorType type);
    Color color(IOverlay.ColorType type, IOverlay overlay);
    boolean furGT();
    boolean furDaima();
    boolean furSavior();
    String furDir();
    boolean hasFur();
    boolean pupils();
    boolean berserk();

    boolean isNPC();
    IPlayer getPlayer();
    ICustomNpc getNPC();
    IEntity getEntity();

    IOverlay getOverlay();

    IForm form();
}
