package kamkeel.npcdbc.data.statuseffect.custom;

import kamkeel.npcdbc.api.effect.ICustomEffect;
import kamkeel.npcdbc.api.event.IDBCEvent;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import noppes.npcs.api.entity.IPlayer;

import static kamkeel.npcdbc.scripted.DBCPlayerEvent.EffectEvent.ExpirationType;

import java.util.function.Consumer;

public class CustomEffect extends StatusEffect implements ICustomEffect {

    /**
     * Experimental script stuff.
     */
    public Consumer<IDBCEvent.EffectEvent.Added> onAddedConsumer;
    public Consumer<IDBCEvent.EffectEvent.Ticked> onTickConsumer;
    public Consumer<DBCPlayerEvent.EffectEvent.Removed> onRemovedConsumer;
    public String menuName = "§aNEW EFFECT";
    public int width = 16, height = 16;


    public CustomEffect(int id) {
        this();
        this.id = id;
    }

    public CustomEffect() {
        super(true);
    }

    public CustomEffect(int id, String name) {
        this(id);
        this.name = name;
    }

    @Override
    public void setMenuName(String name) {
        if (name != null && !name.isEmpty())
            this.menuName = name.replaceAll("&", "§");
    }

    @Override
    public String getMenuName() {
        return menuName;
    }

    @Override
    public void setName(String name) {
        if (name != null && !name.isEmpty())
            this.name = name;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public int getEveryXTick() {
        return everyXTick;
    }

    @Override
    public void setEveryXTick(int everyXTick) {
        if (everyXTick < 10)
            everyXTick = 10;
        int remainder = everyXTick % 10;
        if (remainder >= 5)
            everyXTick += 10 - remainder;
        else
            everyXTick -= remainder;
        this.everyXTick = everyXTick;
    }

    @Override
    public int getIconX() {
        return iconX;
    }

    @Override
    public void setIconX(int iconX) {
        this.iconX = iconX;
    }

    @Override
    public int getIconY() {
        return iconY;
    }

    @Override
    public void setIconY(int iconY) {
        this.iconY = iconY;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public boolean isLossOnDeath() {
        return lossOnDeath;
    }

    @Override
    public void setLossOnDeath(boolean lossOnDeath) {
        this.lossOnDeath = lossOnDeath;
    }

    @Override
    public ICustomEffect save() {
        return StatusEffectController.getInstance().saveEffect(this);
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }


    public void onAdded(Consumer<IDBCEvent.EffectEvent.Added> function) {
        onAddedConsumer = function;
    }

    public void onTick(Consumer<IDBCEvent.EffectEvent.Ticked> function) {
        onTickConsumer = function;
    }

    public void onRemoved(Consumer<DBCPlayerEvent.EffectEvent.Removed> function) {
        onRemovedConsumer = function;
    }

    public void onAdded(EntityPlayer player, PlayerEffect playerEffect) {
        IPlayer iPlayer = PlayerDataUtil.getIPlayer(player);

        DBCPlayerEvent.EffectEvent.Added event = new DBCPlayerEvent.EffectEvent.Added(iPlayer, playerEffect);
        if (onAddedConsumer != null) onAddedConsumer.accept(event);
        EffectScriptHandler script = getScriptHandler();
        if (script == null) {
            return;
        }

        script.callScript(EffectScriptHandler.ScriptType.OnAdd, event);
    }

    public void onTick(EntityPlayer player, PlayerEffect playerEffect) {
        IPlayer iPlayer = PlayerDataUtil.getIPlayer(player);
        DBCPlayerEvent.EffectEvent.Ticked event = new DBCPlayerEvent.EffectEvent.Ticked(iPlayer, playerEffect);

        if (onTickConsumer != null) {
            onTickConsumer.accept(event);
        }

        EffectScriptHandler script = getScriptHandler();
        if (script == null) {
            return;
        }

        script.callScript(EffectScriptHandler.ScriptType.OnTick, event);
    }

    public void onRemoved(EntityPlayer player, PlayerEffect playerEffect, ExpirationType type) {
        IPlayer iPlayer = PlayerDataUtil.getIPlayer(player);

        DBCPlayerEvent.EffectEvent.Removed event = new DBCPlayerEvent.EffectEvent.Removed(iPlayer, playerEffect, type);

        if (onRemovedConsumer != null) {
            onRemovedConsumer.accept(event);
        }

        EffectScriptHandler script = getScriptHandler();
        if (script == null) {
            return;
        }

        script.callScript(EffectScriptHandler.ScriptType.OnRemove, event);
    }

    public NBTTagCompound writeToNBT(boolean saveScripts) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("ID", id);
        compound.setString("name", name);
        compound.setString("menuName", menuName);
        compound.setInteger("length", length);
        compound.setInteger("everyXTick", everyXTick);
        compound.setInteger("iconX", iconX);
        compound.setInteger("iconY", iconY);
        compound.setInteger("iconWidth", width);
        compound.setInteger("iconHeight", height);
        compound.setString("icon", icon);
        compound.setBoolean("lossOnDeath", lossOnDeath);

//        if (saveScripts && scriptContainer != null) {
        if (saveScripts) {
            NBTTagCompound scriptData = new NBTTagCompound();
            EffectScriptHandler handler = getScriptHandler();
            if (handler != null)
                handler.writeToNBT(scriptData);
            compound.setTag("ScriptData", scriptData);
        }

        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("ID"))
            id = compound.getInteger("ID");
        else
            id = StatusEffectController.Instance.getUnusedId();
        name = compound.getString("name");

        if (compound.hasKey("menuName", Constants.NBT.TAG_STRING))
            menuName = compound.getString("menuName");
        else
            menuName = name;

        length = compound.getInteger("length");
        everyXTick = compound.getInteger("everyXTick");
        iconX = compound.getInteger("iconX");
        iconY = compound.getInteger("iconY");

        if (compound.hasKey("iconWidth", Constants.NBT.TAG_INT))
            width = compound.getInteger("iconWidth");
        else
            width = 16;

        if (compound.hasKey("iconHeight", Constants.NBT.TAG_INT))
            height = compound.getInteger("iconHeight");
        else
            height = 16;

        icon = compound.getString("icon");
        lossOnDeath = compound.getBoolean("lossOnDeath");

        if (compound.hasKey("ScriptData", Constants.NBT.TAG_COMPOUND)) {
            EffectScriptHandler handler = new EffectScriptHandler();
            handler.readFromNBT(compound.getCompoundTag("ScriptData"));
            setScriptHandler(handler);
        }

    }

    public CustomEffect cloneEffect() {
        CustomEffect newEffect = new CustomEffect();
        newEffect.readFromNBT(this.writeToNBT(true));
        newEffect.id = -1;
        return newEffect;
    }

    public EffectScriptHandler getScriptHandler() {
        return StatusEffectController.getInstance().customEffectScriptHandlers.get(this.id);
    }
    public void setScriptHandler(EffectScriptHandler handler) {
        StatusEffectController.getInstance().customEffectScriptHandlers.put(this.id, handler);
    }

    public EffectScriptHandler getOrCreateScriptHandler() {
        EffectScriptHandler data = getScriptHandler();
        if (data == null)
            setScriptHandler(data =new EffectScriptHandler());
        return data;
    }
}
