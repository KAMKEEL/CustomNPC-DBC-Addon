package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCEffects;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.BonusController;
import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;

public class FruitOfMight extends StatusEffect {
    public static Aura fruitOfMightAura = null;
    public float kiToDrain;
    public PlayerBonus fruitOfMightBonus;

    public FruitOfMight() {
        name = "FruitOfMight";
        id = Effects.FRUIT_OF_MIGHT;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/statuseffects.png";
        iconX = 64;
        iconY = 0;
        fruitOfMightBonus = new PlayerBonus(name, (byte) 0, (float) ConfigDBCEffects.FOM_Strength, (float) ConfigDBCEffects.FOM_Dex, (float) ConfigDBCEffects.FOM_Will);
        kiToDrain = (float) ConfigDBCEffects.FOM_KiDrain;

        if (fruitOfMightAura == null) {
            fruitOfMightAura = new Aura();
            fruitOfMightAura.id = -10;
            fruitOfMightAura.display.setColor("color1", 0x0); //black
            fruitOfMightAura.display.setColor("color3", 0xb329ba); //purple
            fruitOfMightAura.display.hasLightning = true;
            fruitOfMightAura.display.lightningColor = 0xb329ba; //purple
        }
    }

    @Override
    public void init(EntityPlayer player, PlayerEffect playerEffect){
        BonusController.getInstance().applyBonus(player, fruitOfMightBonus);
        PlayerDBCInfo c = PlayerDataUtil.getDBCInfo(player);
        c.currentAura = fruitOfMightAura.id;
        c.updateClient();
    }

    @Override
    public void process(EntityPlayer player, PlayerEffect playerEffect) {
        DBCData dbcData = DBCData.get(player);
        dbcData.stats.restoreKiPercent(kiToDrain);
        if (dbcData.Ki <= 0)
            playerEffect.kill();
    }

    @Override
    public void runout(EntityPlayer player, PlayerEffect playerEffect) {
        PlayerDBCInfo c = PlayerDataUtil.getDBCInfo(player);
        if (c.currentAura == fruitOfMightAura.id) {
            c.currentAura = -1;
            c.updateClient();
        }
        BonusController.getInstance().removeBonus(player, fruitOfMightBonus);
    }
}
