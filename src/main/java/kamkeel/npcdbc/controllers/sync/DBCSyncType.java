package kamkeel.npcdbc.controllers.sync;

import kamkeel.npcdbc.controllers.sync.handlers.*;
import kamkeel.npcs.controllers.sync.SyncRegistry;
import kamkeel.npcs.network.enums.SyncType;

public class DBCSyncType {

    public static final SyncType PLAYER_DATA = register("playerdata");
    public static final SyncType FORM = register("form");
    public static final SyncType AURA = register("aura");
    public static final SyncType OUTLINE = register("outline");
    public static final SyncType SKILL = register("skill");
    public static final SyncType RACE = register("race");
    public static final SyncType OVERLAY_MODEL = register("overlay_model");

    public static void register() {
        SyncRegistry.register(PLAYER_DATA, new DBCInfoSyncHandler());
        SyncRegistry.register(FORM, new FormSyncHandler());
        SyncRegistry.register(AURA, new AuraSyncHandler());
        SyncRegistry.register(OUTLINE, new OutlineSyncHandler());
        SyncRegistry.register(SKILL, new SkillSyncHandler());
        SyncRegistry.register(RACE, new RaceSyncHandler());
        SyncRegistry.register(OVERLAY_MODEL, new OverlayModelSyncHandler());
    }
    
    private static SyncType register(String name) {
       return SyncType.register("npcdbc:" + name);
    }
}