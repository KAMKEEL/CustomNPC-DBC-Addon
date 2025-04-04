package kamkeel.npcdbc.data;

import JinRyuu.JRMCore.i.ExtendedPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import static JinRyuu.JRMCore.JRMCoreH.DBC;
import static JinRyuu.JRMCore.JRMCoreH.setInt;

public class DBCDamageCalc {

    public Entity entity;
    public int damage = -1;
    public int stamina = -1;
    public int ki = -1;

    public DBCDamageCalc() {}

    public DBCDamageCalc(int damage, int stamina, int ki) {
        this.damage = damage;
        this.stamina = stamina;
        this.ki = ki;
    }

    public DBCDamageCalc(int damage) {
        this.damage = damage;
        this.stamina = 0;
        this.ki = 0;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public int getKi() {
        return ki;
    }

    public void setKi(int ki) {
        this.ki = ki;
    }

    public void processExtras(){
        if (entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if(stamina != -1)
                setInt(stamina, player, "jrmcStamina");

            if(ki != -1)
                setInt(ki, player, "jrmcEnrgy");

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
