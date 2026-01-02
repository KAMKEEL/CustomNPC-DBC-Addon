package kamkeel.npcdbc.client.gui.hud.abilityHotbar;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.data.ability.Ability;
import net.minecraft.util.ResourceLocation;

public class AbilityHotbarSlot {
    public static final ResourceLocation resource = new ResourceLocation(CustomNpcPlusDBC.ID + ":textures/gui/ability_hotbar.png");

    public HUDAbilityHotbar parent;

    public AbilityHotbarSlot(HUDAbilityHotbar parent, Ability abilityToCopy) {
        this.parent = parent;
    }
}
