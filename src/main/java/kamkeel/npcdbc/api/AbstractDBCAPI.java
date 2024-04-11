package kamkeel.npcdbc.api;

import cpw.mods.fml.common.Loader;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

public abstract class AbstractDBCAPI {
    private static AbstractDBCAPI instance = null;

    public static boolean IsAvailable() {
        return Loader.isModLoaded("jinryuujrmcore");
    }

    public static AbstractDBCAPI Instance() {
        if (instance != null) {
            return instance;
        } else if (!IsAvailable()) {
            return null;
        } else {
            try {
                Class<?> c = Class.forName("kamkeel.npcdbc.scripted.DBCAPI");
                instance = (AbstractDBCAPI) c.getMethod("Instance").invoke(null);
            } catch (Exception ignored) {

            }
            return instance;
        }
    }

    public abstract IDBCStats getDBCData(ICustomNpc<EntityNPCInterface> npc);

    public abstract IKiAttack createKiAttack();

    public abstract IKiAttack createKiAttack(byte type, byte speed, int damage, boolean hasEffect, byte color, byte density, boolean hasSound, byte chargePercent);

    public abstract void fireKiAttack(ICustomNpc<EntityNPCInterface> npc, byte type, byte speed, int dam1, boolean hasEffect, byte color, byte density, boolean hasSound, byte chrg);

    public abstract void fireKiAttack(ICustomNpc<EntityNPCInterface> npc, IKiAttack kiAttack);
}
