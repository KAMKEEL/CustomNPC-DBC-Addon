package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class Zenkai extends StatusEffect {
    public PlayerBonus saiyanZenkai;
    public PlayerBonus halfSaiyanZenkai;
    public Zenkai() {
        name = "Zenkai";
        id = Effects.ZENKAI;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/statuseffects.png";
        iconX = 160;
        iconY = 0;

        saiyanZenkai = new PlayerBonus("Saiyan" + name, (byte) 0, (float) ConfigDBCEffects.Saiyan_Strength, (float) ConfigDBCEffects.Saiyan_Dex, (float) ConfigDBCEffects.Saiyan_Will);
        halfSaiyanZenkai = new PlayerBonus("HalfSaiyan" + name, (byte) 0, (float) ConfigDBCEffects.HalfSaiyan_Strength, (float) ConfigDBCEffects.HalfSaiyan_Dex, (float) ConfigDBCEffects.HalfSaiyan_Will);
    }

    @Override
    public void init(EntityPlayer player, PlayerEffect playerEffect){
        BonusController.getInstance().applyBonus(player, saiyanZenkai);
    }

    @Override
    public void runout(EntityPlayer player, PlayerEffect playerEffect) {
        BonusController.getInstance().removeBonus(player, saiyanZenkai);
    }
}
