package kamkeel.npcdbc.scripted;

import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.entity.EntityEnergyAtt;
import kamkeel.npcdbc.api.AbstractDBCAPI;
import kamkeel.npcdbc.api.IDBCStats;
import kamkeel.npcdbc.api.IKiAttack;
import kamkeel.npcdbc.data.KiAttack;
import kamkeel.npcdbc.mixin.INPCStats;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

public class DBCAPI extends AbstractDBCAPI {
    private static AbstractDBCAPI Instance;

    private DBCAPI() {
    }

    public static AbstractDBCAPI Instance() {
        if (DBCAPI.Instance == null) {
            Instance = new DBCAPI();
        }
        return Instance;
    }

    @Override
    public IDBCStats getDBCData(ICustomNpc<EntityNPCInterface> npc) {
        if (npc.getMCEntity() instanceof EntityNPCInterface)
            return ((INPCStats) npc.getMCEntity().stats).getDBCStats();
        return null;
    }

    @Override
    public IKiAttack createKiAttack() {
        return new KiAttack();
    }

    @Override
    public IKiAttack createKiAttack(byte type, byte speed, int damage, boolean hasEffect, byte color, byte density, boolean hasSound, byte chargePercent) {
        return new KiAttack(type, speed, damage, hasEffect, color, density, hasSound, chargePercent);
    }

    @Override
    public void fireKiAttack(ICustomNpc<EntityNPCInterface> npc, byte type, byte speed, int damage, boolean hasEffect, byte color, byte density, boolean hasSound, byte chargePercent) {
        if (npc == null)
            return;

        if (npc.getMCEntity() == null)
            return;

        EntityEnergyAtt entityEnergyAtt = null;
        try {
            if (JRMCoreConfig.dat5695[type]) {
                type = ValueUtil.clamp(type, (byte) 0, (byte) 8);
                speed = ValueUtil.clamp(speed, (byte) 0, (byte) 8);
                if (damage < 0) {
                    damage = 0;
                }
                byte effect = hasEffect ? (byte) 1 : (byte) 0;
                color = ValueUtil.clamp(color, (byte) 0, (byte) (JRMCoreH.techCol.length - 1));
                if (density < 0) {
                    density = 0;
                }
                byte playSound = hasSound ? (byte) 1 : (byte) 0;
                chargePercent = ValueUtil.clamp(chargePercent, (byte) 0, (byte) 100);
                byte[] sts = JRMCoreH.techDBCstatsDefault;

                EntityNPCInterface trueNpc = npc.getMCEntity();
                npc.getMCEntity().worldObj.playSoundAtEntity(trueNpc, "jinryuudragonbc:DBC2.basicbeam_fire", 0.5F, 1.0F);
                entityEnergyAtt = new EntityEnergyAtt(trueNpc, type, speed, 50, effect, color, density, (byte) 0, (byte) 0, playSound, chargePercent, damage, 0, sts, (byte) 0);
                trueNpc.worldObj.spawnEntityInWorld(entityEnergyAtt);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Override
    public void fireKiAttack(ICustomNpc<EntityNPCInterface> npc, IKiAttack kiAttack) {
        if (npc == null || npc.getMCEntity() == null || kiAttack == null)
            return;

        EntityEnergyAtt entityEnergyAtt = null;
        try {
            byte type = kiAttack.getType();
            byte speed = kiAttack.getSpeed();
            int damage = kiAttack.getDamage();
            boolean hasEffect = kiAttack.isHasEffect();
            byte color = kiAttack.getColor();
            byte density = kiAttack.getDensity();
            boolean hasSound = kiAttack.isHasSound();
            byte chargePercent = kiAttack.getChargePercent();

            if (JRMCoreConfig.dat5695[type]) {
                // Clamping and Verification
                type = ValueUtil.clamp(type, (byte) 0, (byte) 8);
                speed = ValueUtil.clamp(speed, (byte) 0, (byte) 8);
                if (damage < 0) {
                    damage = 0;
                }
                byte effect = hasEffect ? (byte) 1 : (byte) 0;
                color = ValueUtil.clamp(color, (byte) 0, (byte) (JRMCoreH.techCol.length - 1));
                if (density < 0) {
                    density = 0;
                }
                byte playSound = hasSound ? (byte) 1 : (byte) 0;
                chargePercent = ValueUtil.clamp(chargePercent, (byte) 0, (byte) 100);
                byte[] sts = JRMCoreH.techDBCstatsDefault;

                EntityNPCInterface trueNpc = npc.getMCEntity();
                npc.getMCEntity().worldObj.playSoundAtEntity(trueNpc, "jinryuudragonbc:DBC2.basicbeam_fire", 0.5F, 1.0F);
                entityEnergyAtt = new EntityEnergyAtt(trueNpc, type, speed, 50, effect, color, density, (byte) 0, (byte) 0, playSound, chargePercent, damage, 0, sts, (byte) 0);
                trueNpc.worldObj.spawnEntityInWorld(entityEnergyAtt);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
    }
}

