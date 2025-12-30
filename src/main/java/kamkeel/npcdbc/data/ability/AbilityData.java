package kamkeel.npcdbc.data.ability;

import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.scripted.event.player.PlayerEvent;

import java.util.function.Consumer;

public class AbilityData {
    Ability parent;
    Ability.Type type = Ability.Type.Active;

    public Consumer<Boolean> onActivateConsumer = null;

    public Consumer<Boolean> onToggleConsumer = null;

    public AbilityData(Ability parent) {
        this.parent = parent;
    }

    public Ability.Type getType() {
        return type;
    }

    public void setType(Ability.Type type) {
        this.type = type;
    }

    public void onActivate(Consumer<Boolean> function) {
        onActivateConsumer = function;
    }

    public void onToggle(Consumer<Boolean> function) {
        onToggleConsumer = function;
    }

    public void onActivate(EntityPlayer player) {
        IPlayer iPlayer = NoppesUtilServer.getIPlayer(player);
        DBCData data = DBCData.getData(player);

        if (type == Ability.Type.Active) {
            DBCPlayerEvent.AbilityEvent.Activate event = new DBCPlayerEvent.AbilityEvent.Activate(iPlayer, parent);
            boolean canUse = parent.kiCost > -1 && data.Ki >= parent.kiCost;

            if (onActivateConsumer != null && canUse && !event.isCanceled()) {
                onActivateConsumer.accept(true);
                DBCEventHooks.onAbilityActivateEvent(event);

                if (parent.cooldown > -1) {

                    // insert here cooldown
                }
            }
        }
    }

    public void onToggle(EntityPlayer player) {
        IPlayer iPlayer = NoppesUtilServer.getIPlayer(player);
        DBCData data = DBCData.getData(player);

        if (type == Ability.Type.Toggle) {
            DBCPlayerEvent.AbilityEvent.Toggle event = new DBCPlayerEvent.AbilityEvent.Toggle(iPlayer, parent);
            boolean canUse = parent.kiCost > -1 && data.Ki >= parent.kiCost;

            if (onToggleConsumer != null && canUse && !event.isCanceled()) {
                onToggleConsumer.accept(true);
                DBCEventHooks.onAbilityToggleEvent(event);

                if (parent.cooldown > -1) {

                    // insert here cooldown
                }
            }
        }
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();

        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {

    }
}
