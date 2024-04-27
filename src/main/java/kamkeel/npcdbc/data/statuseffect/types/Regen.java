package kamkeel.npcdbc.data.statuseffect.types;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.entity.player.EntityPlayer;

public class Regen extends StatusEffect {
    public float percentToRegen;
    public String type;

    public Regen(int timer) {
        super(timer);
        name = "Regeneration";
        id = Effects.REGEN;
        icon = CustomNpcPlusDBC.ID + ":textures/gui/statuseffects.png";
        iconX = 0;
        iconY = 0;
    }

    public Regen(int timer, String type, float percentageToRegen, int onceEveryXTicks) {
        this(timer);
        this.percentToRegen = percentageToRegen;
        this.everyXTick = onceEveryXTicks;
        this.type = type;
    }

    @Override
    public void process(EntityPlayer player) {
        DBCData dbcData = DBCData.get(player);
        switch (type.toLowerCase()) {
            case "ki":
                dbcData.restoreKiPercent(percentToRegen);
                break;
            case "stamina":
                dbcData.restoreStaminaPercent(percentToRegen);
                break;
            case "body":
                dbcData.restoreHealthPercent(percentToRegen);
                break;
        }
    }


}
