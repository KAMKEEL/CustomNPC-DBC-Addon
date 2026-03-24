package kamkeel.npcdbc.data.ability.effect;

import kamkeel.npcs.controllers.data.ability.enums.UserType;
import kamkeel.npcs.controllers.data.ability.type.AbilityEffect;
import kamkeel.npcs.controllers.data.telegraph.TelegraphType;

import java.util.function.Consumer;

public class DBCEffectAbility extends AbilityEffect {

    public DBCEffectAbility(String key, Consumer<AbilityEffect> configurator) {
        configurator.accept(this);

        this.allowedBy = UserType.PLAYER_ONLY;
        this.showTelegraph = false;
        this.telegraphType = TelegraphType.NONE;

        configureAsBuiltIn("npcdbc:" + key);
    }
}
