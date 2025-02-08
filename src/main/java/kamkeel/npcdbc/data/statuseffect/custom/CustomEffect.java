package kamkeel.npcdbc.data.statuseffect.custom;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.api.effect.ICustomEffect;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.controllers.ScriptContainer;

import java.util.function.BiConsumer;

public class CustomEffect extends StatusEffect implements ICustomEffect {

    /**
     * Experimental script stuff.
     */
    public BiConsumer<IPlayer, PlayerEffect> onAddedConsumer, onTickConsumer, onRemovedConsumer;
    public String menuName = "§aNEW EFFECT";


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
        this.menuName = menuName;
    }

    @Override
    public String getMenuName() {
        return menuName;
    }

    @Override
    public void setName(String name) {
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


    public void onAdded(BiConsumer<IPlayer, PlayerEffect> function) {
        onAddedConsumer = function;
    }

    public void onTick(BiConsumer<IPlayer, PlayerEffect> function) {
        onTickConsumer = function;
    }

    public void onRemoved(BiConsumer<IPlayer, PlayerEffect> function) {
        onRemovedConsumer = function;
    }

    public void onAdded(EntityPlayer player, PlayerEffect playerEffect) {
        IPlayer iPlayer = PlayerDataUtil.getIPlayer(player);
        if (onAddedConsumer != null)
            onAddedConsumer.accept(iPlayer, playerEffect);

        EffectScriptHandler script = getScriptHandler();
        if (script == null)
            return;

        DBCPlayerEvent.EffectEvent.Added event = new DBCPlayerEvent.EffectEvent.Added(iPlayer, playerEffect);
        script.callScript(EffectScriptHandler.ScriptType.OnAdd, event);
    }

    public void onTick(EntityPlayer player, PlayerEffect playerEffect) {
        IPlayer iPlayer = PlayerDataUtil.getIPlayer(player);
        if (onTickConsumer != null)
            onTickConsumer.accept(iPlayer, playerEffect);

        EffectScriptHandler script = getScriptHandler();
        if (script == null)
            return;

        DBCPlayerEvent.EffectEvent.Ticked event = new DBCPlayerEvent.EffectEvent.Ticked(iPlayer, playerEffect);
        script.callScript(EffectScriptHandler.ScriptType.OnTick, event);
    }

    public void onRemoved(EntityPlayer player, PlayerEffect playerEffect) {
        IPlayer iPlayer = PlayerDataUtil.getIPlayer(player);
        if (onRemovedConsumer != null)
            onRemovedConsumer.accept(iPlayer, playerEffect);

        EffectScriptHandler script = getScriptHandler();
        if (script == null)
            return;

        DBCPlayerEvent.EffectEvent.Removed event = new DBCPlayerEvent.EffectEvent.Removed(iPlayer, playerEffect);
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
        icon = compound.getString("icon");
        lossOnDeath = compound.getBoolean("lossOnDeath");

        if (compound.hasKey("ScriptData", Constants.NBT.TAG_COMPOUND)) {
            EffectScriptHandler handler = new EffectScriptHandler(this);
            handler.readFromNBT(compound.getCompoundTag("ScriptData"));
            setScriptHandler(handler);
        }

    }

    public CustomEffect clone() {
        CustomEffect newEffect = new CustomEffect();
        newEffect.readFromNBT(this.writeToNBT(true));
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
            setScriptHandler(data =new EffectScriptHandler(this));
        return data;
    }
}
