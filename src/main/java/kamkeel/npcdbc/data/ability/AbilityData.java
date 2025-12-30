package kamkeel.npcdbc.data.ability;

import kamkeel.npcdbc.controllers.AbilityController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.entity.IPlayer;

import java.util.function.Consumer;

public class AbilityData {
    private final Ability parent;
    public Ability.Type type = Ability.Type.Active;

    public Consumer<DBCPlayerEvent.AbilityEvent.Activate> onActivateConsumer = null;

    public Consumer< DBCPlayerEvent.AbilityEvent.Toggle> onToggleConsumer = null;

    public AbilityData(Ability parent) {
        this.parent = parent;
    }

    public Ability.Type getType() {
        return type;
    }

    public void setType(Ability.Type type) {
        this.type = type;
    }

    public void onActivate(Consumer<DBCPlayerEvent.AbilityEvent.Activate> function) {
        onActivateConsumer = function;
    }

    public void onToggle(Consumer< DBCPlayerEvent.AbilityEvent.Toggle> function) {
        onToggleConsumer = function;
    }

    public void onActivate(EntityPlayer player) {
        IPlayer iPlayer = NoppesUtilServer.getIPlayer(player);
        DBCData data = DBCData.getData(player);
        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);

        if (type == Ability.Type.Active) {
            DBCPlayerEvent.AbilityEvent.Activate event = new DBCPlayerEvent.AbilityEvent.Activate(iPlayer, parent);

            if (event.isCanceled()) {
                return;
            }

            if (event.getKiCost() > -1 && data.Ki < event.getKiCost()) {
                return;
            }

            if (event.getCooldown() > -1 && info.hasCooldown(parent.id)) {
                return;
            }

            if (onActivateConsumer != null)
                onActivateConsumer.accept(event);


            AbilityScript script = getScriptHandler();
            if (script == null)
                return;


            script.callScript(AbilityScript.ScriptType.OnAbilityActivate, event);

            DBCEventHooks.onAbilityActivateEvent(event);
        }
    }

    public void onToggle(EntityPlayer player) {
        IPlayer iPlayer = NoppesUtilServer.getIPlayer(player);
        DBCData data = DBCData.getData(player);
        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);

        if (type == Ability.Type.Toggle) {
            DBCPlayerEvent.AbilityEvent.Toggle event = new DBCPlayerEvent.AbilityEvent.Toggle(iPlayer, parent);

            if (event.isCanceled()) {
                return;
            }

            if (event.getKiCost() > -1 && data.Ki < event.getKiCost()) {
                return;
            }

            if (event.getCooldown() > -1 && info.hasCooldown(parent.id)) {
                return;
            }

            if (parent.cooldown > -1) {
                info.addCooldown(parent.id, event.getCooldown());
            }

            if (onToggleConsumer != null)
                onToggleConsumer.accept(event);


            AbilityScript script = getScriptHandler();
            if (script == null)
                return;


            script.callScript(AbilityScript.ScriptType.OnAbilityToggle, event);

            DBCEventHooks.onAbilityToggleEvent(event);
        }
    }

    public AbilityScript getScriptHandler() {
        return AbilityController.getInstance().abilityScriptHandlers.get(parent.id);
    }

    public void setScriptHandler(AbilityScript handler) {
        AbilityController.getInstance().abilityScriptHandlers.put(parent.id, handler);
    }

    public NBTTagCompound writeToNBT(boolean saveScripts) {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setInteger("type", type.ordinal());

        if (saveScripts) {
            NBTTagCompound scriptData = new NBTTagCompound();
            AbilityScript handler = getScriptHandler();
            if (handler != null)
                handler.writeToNBT(scriptData);
            compound.setTag("ScriptData", scriptData);
        }

        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        type = Ability.Type.values()[compound.getInteger("type")];

        if (compound.hasKey("ScriptData", Constants.NBT.TAG_COMPOUND)) {
            AbilityScript handler = new AbilityScript();
            handler.readFromNBT(compound.getCompoundTag("ScriptData"));
            setScriptHandler(handler);
        }
    }
}
