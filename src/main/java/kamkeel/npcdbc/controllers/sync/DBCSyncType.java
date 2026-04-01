package kamkeel.npcdbc.controllers.sync;

import kamkeel.npcdbc.controllers.sync.handlers.*;
import kamkeel.npcs.controllers.sync.SyncRegistry;
import kamkeel.npcs.network.enums.SyncType;

import static kamkeel.npcs.network.enums.SyncType.register;

public class DBCSyncType {

    public static final SyncType FORM = register("npcdbc:form");
    public static final SyncType AURA = register("npcdbc:aura");
    public static final SyncType OUTLINE = register("npcdbc:outline");
    public static final SyncType SKILL = register("npcdbc:skill");
    public static final SyncType RACE = register("npcdbc:race");

    public static void load() {
        SyncRegistry.clear();

        SyncRegistry.register(FORM, new FormSyncHandler());
        SyncRegistry.register(AURA, new AuraSyncHandler());
        SyncRegistry.register(OUTLINE, new OutlineSyncHandler());
        SyncRegistry.register(SKILL, new SkillSyncHandler());
        SyncRegistry.register(RACE, new RaceSyncHandler());
    }
}