package kamkeel.npcdbc.data;

import JinRyuu.JRMCore.i.ExtendedPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import static JinRyuu.JRMCore.JRMCoreH.DBC;
import static JinRyuu.JRMCore.JRMCoreH.setInt;

public class DBCDamageCalc {

    public Entity entity;
    public float damage = -1;
    public float stamina = -1;
    public float ki = -1;
    public boolean willKo = false;

    public DBCDamageCalc() {}

    public DBCDamageCalc(float damage, float stamina, float ki) {
        this.damage = damage;
        this.stamina = stamina;
        this.ki = ki;
    }

    public DBCDamageCalc(float damage) {
        this.damage = damage;
        this.stamina = 0;
        this.ki = 0;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getStamina() {
        return stamina;
    }

    public void setStaminaReduction(float stamina) {
        this.stamina = stamina;
    }

    public float getKi() {
        return ki;
    }

    public void setKiReduciton(int ki) {
        this.ki = ki;
    }

    public void processExtras(){
        if (entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
//            if(stamina > 0)
//                setInt(stamina, player, "jrmcStamina");
//
//            if(ki > 0)
//                setInt(ki, player, "jrmcEnrgy");

            if (DBC()) {
                ItemStack stackbody = ExtendedPlayer.get(player).inventory.getStackInSlot(1);
                ItemStack stackhead = ExtendedPlayer.get(player).inventory.getStackInSlot(2);
                if (stackbody != null) {
                    stackbody.damageItem(1, player);
                }

                if (stackhead != null) {
                    stackhead.damageItem(1, player);
                }
            }
        }
    }
}
