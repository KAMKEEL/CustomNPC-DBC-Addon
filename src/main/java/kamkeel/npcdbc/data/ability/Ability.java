package kamkeel.npcdbc.data.ability;

import kamkeel.npcdbc.api.ability.IAbility;
import kamkeel.npcdbc.controllers.AbilityController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.AbilityAnimatePacket;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.handler.data.IAnimation;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.controllers.data.Animation;
import noppes.npcs.controllers.data.AnimationData;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static noppes.npcs.NoppesStringUtils.translate;

public class Ability implements IAbility {
    public int id = -1;
    public String name = "";
    public String menuName = "NEW ABILITY";
    public int cooldown = 0;
    public int kiCost = 0;
    public boolean multiUse = false;
    public int maxUses = 1;

    public String icon = "";
    public int iconX = 0;
    public int iconY = 0;
    public int width = 16;
    public int height = 16;
    public float scale = 1;

    public Animation animation = null;

    protected boolean cooldownCancelled = false;

    public Type type = Type.Cast;

    public Ability() {
    }

    public Ability(int id) {
        this.id = id;
    }

    public Ability(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void setID(int id) {
        if (id > 0 || id == -1)
            this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getMenuName() {
        return menuName;
    }

    @Override
    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    @Override
    public int getKiCost() {
        return kiCost;
    }

    @Override
    public void setKiCost(int kiCost) {
        this.kiCost = kiCost;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public boolean isMultiUse() {
        return multiUse;
    }

    public void setMultiUse(boolean multiUse) {
        this.multiUse = multiUse;
    }

    @Override
    public int getMaxUses() {
        return maxUses;
    }

    @Override
    public void setMaxUses(int maxUses) {
        this.maxUses = maxUses;
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
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public float getScale() {
        return scale;
    }

    @Override
    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public Animation getAnimation() {
        return animation;
    }

    @Override
    public void setAnimation(Animation animation) {
        if (AnimationController.Instance.has(animation.getName()))
            setAnimation(animation.getID());
    }

    @Override
    public void setAnimation(int id) {
        if (AnimationController.Instance.animations.containsKey(id)) {
            Animation anim = new Animation();
            anim.readFromNBT(((Animation) AnimationController.Instance.get(id)).writeToNBT());
            this.animation = anim;
        }
    }

    @Override
    public IAbility save() {
        return AbilityController.Instance.saveAbility(this);
    }

    @Override
    public IAbility cloneAbility() {
        Ability newAbility = new Ability();
        newAbility.readFromNBT(this.writeToNBT(true));
        newAbility.id = -1;
        return newAbility;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean onUse(EntityPlayer player) {
        IPlayer iPlayer = NoppesUtilServer.getIPlayer(player);
        DBCData data = DBCData.getData(player);
        PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);
        AbilityData abilityData = this instanceof AddonAbility ? info.dbcAbilityData : info.customAbilityData;

        DBCPlayerEvent.AbilityEvent event = getEventByType(iPlayer);

        if (event == null)
            return false;

        if (event.getCooldown() > -1 && abilityData.hasCooldown(id)) {
            Utility.sendMessage(player, translate("§c", "npcdbc.abilityCooldown", ": ", abilityData.abilityTimers.get(id) + "", "s"));
            return false;
        }

        if (event.getKiCost() > -1 && data.Ki < event.getKiCost()) {
            Utility.sendMessage(player, translate("§c", "npcdbc.abilityNoKi"));
            return false;
        }

        if (abilityData.animatingAbility != -1)
            return false;

        if (!canFireEvent(player, event))
            return false;

        fireEvent(player, event);

        DBCEventHooks.onAbilityUsed(event);

        AbilityScript script = getScriptHandler();
        if (script != null) {
            AbilityScript.ScriptType scriptType = type == Type.Cast ?
                AbilityScript.ScriptType.AbilityCast : type == Type.Toggle ?
                AbilityScript.ScriptType.AbilityToggle :
                AbilityScript.ScriptType.AbilityAnimationStart;


            script.callScript(scriptType, event);
        }

        if (this.type == Type.Animated) {
            setupAnimation((DBCPlayerEvent.AbilityEvent.Animated) event);
            finishAnimationSetup((DBCPlayerEvent.AbilityEvent.Animated) event);

            if (this.animation != null) {
                AnimationData animData = (AnimationData) iPlayer.getAnimationData();

                animData.setEnabled(true);
                animData.setAnimation(this.animation);
                animData.updateClient();
            }
        }

        afterEvent(player, event);

        if (event.getKiCost() > 0) {
            data.Ki -= event.getKiCost();
        }

        boolean maxAmountReached = abilityData.abilityCounter.get(id) != null && abilityData.abilityCounter.get(id) == maxUses;
        boolean shouldActivateCooldown =
            event.getCooldown() > 0 &&
            !cooldownCancelled &&
            (type != Type.Toggle && (!multiUse || maxAmountReached));

        if (shouldActivateCooldown) {
            abilityData.addCooldown(id, event.getCooldown());
        }

        cooldownCancelled = false;
        info.updateClient();
        return true;
    }

    private DBCPlayerEvent.AbilityEvent getEventByType(IPlayer player) {
        if (type == Type.Cast)
            return new DBCPlayerEvent.AbilityEvent.Casted(player, this);
        else if (type == Type.Toggle)
            return new DBCPlayerEvent.AbilityEvent.Toggled(player, this);
        else if (type == Type.Animated)
            return new DBCPlayerEvent.AbilityEvent.Animated(player, this);
        return null;
    }

    // Extra checking for AddonAbilities
    protected boolean canFireEvent(EntityPlayer player, DBCPlayerEvent.AbilityEvent event) {
        return true;
    }

    // Setup for AddonAbilities
    protected void setupAnimation(DBCPlayerEvent.AbilityEvent.Animated event) {
        this.animation = (Animation) event.getAnimation();
    }

    private void finishAnimationSetup(DBCPlayerEvent.AbilityEvent.Animated event) {
        if (animation == null)
            return;

        AbilityScript script = getScriptHandler();
        Consumer<IAnimation> originalStart = animation.onAnimationStart;
        Consumer<IAnimation> originalEnd = animation.onAnimationEnd;
        BiConsumer<Integer, IAnimation> originalFrame = animation.onAnimationFrame;
        BiConsumer<Long, IAnimation> originalTick = animation.onAnimationTick;

        onAnimationStart((a) -> {
            if (originalStart != null)
                originalStart.accept(a);

            DBCPacketHandler.Instance.sendToServer(new AbilityAnimatePacket(this.id, this instanceof AddonAbility, true));
        });

        onAnimationEnd((a) -> {
            if (originalEnd != null)
                originalEnd.accept(a);

            if (script != null)
                script.callScript(AbilityScript.ScriptType.AbilityAnimationEnd, event);


            DBCPacketHandler.Instance.sendToServer(new AbilityAnimatePacket(this.id, this instanceof AddonAbility, false));
        });

        onAnimationFrame((f, a) -> {
            if (originalFrame != null)
                originalFrame.accept(f, a);

            if (script != null)
                script.callScript(AbilityScript.ScriptType.AbilityAnimationFrame, event);

        });

        onAnimationTick((t, a) -> {
            if (originalTick != null)
                originalTick.accept(t, a);

            if (script != null)
                script.callScript(AbilityScript.ScriptType.AbilityAnimationTick, event);

        });
    }

    // Event for AddonAbilities
    protected void fireEvent(EntityPlayer player, DBCPlayerEvent.AbilityEvent event) {

    }

    // Event for AddonAbilities
    protected void afterEvent(EntityPlayer player, DBCPlayerEvent.AbilityEvent event) {

    }

    protected void onAnimationStart(Consumer<IAnimation> onStart) {
        if (animation != null)
            animation.onStart(onStart);
    }

    protected void onAnimationFrame(BiConsumer<Integer, IAnimation> onFrame) {
        if (animation != null)
            animation.onFrame(onFrame);
    }

    protected void onAnimationEnd(Consumer<IAnimation> onEnd) {
        if (animation != null)
            animation.onEnd(onEnd);
    }

    protected void onAnimationTick(BiConsumer<Long, IAnimation> onTick) {
        if (animation != null)
            animation.onTick(onTick);
    }

    public AbilityScript getScriptHandler() {
        return AbilityController.getInstance().abilityScriptHandlers.get(id);
    }

    public void setScriptHandler(AbilityScript handler) {
        AbilityController.getInstance().abilityScriptHandlers.put(id, handler);
    }

    public AbilityScript getOrCreateScriptHandler() {
        AbilityScript data = getScriptHandler();
        if (data == null)
            setScriptHandler(data = new AbilityScript());
        return data;
    }

    public static String getAbilityColorCode(Ability a) {
        if (a != null && a.getMenuName().contains("§")) {
            String s = a.getMenuName();
            int i = s.indexOf("§");
            return s.substring(i, 2);
        }
        return "";
    }

    public static String getColoredName(Ability a) {
        if (a == null)
            return "";
        return getAbilityColorCode(a) + a.getName();
    }

    public NBTTagCompound writeToNBT(boolean saveScripts) {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setInteger("ID", id);

        compound.setString("name", name);
        compound.setString("menuName", menuName);

        compound.setInteger("cooldown", cooldown);
        compound.setInteger("kiCost", kiCost);
        compound.setInteger("maxUses", maxUses);

        compound.setInteger("type", type.ordinal());

        compound.setString("icon", icon);

        compound.setInteger("iconX", iconX);
        compound.setInteger("iconY", iconY);

        compound.setInteger("width", width);
        compound.setInteger("height", height);

        compound.setFloat("scale", scale);

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
        id = compound.getInteger("ID");

        name = compound.getString("name");
        menuName = compound.getString("menuName");

        cooldown = compound.getInteger("cooldown");
        kiCost = compound.getInteger("kiCost");
        maxUses = compound.getInteger("maxUses");

        type = Type.values()[compound.getInteger("type")];

        icon = compound.getString("icon");

        iconX = compound.getInteger("iconX");
        iconY = compound.getInteger("iconY");

        width = compound.getInteger("width");
        height = compound.getInteger("height");

        scale = compound.getInteger("scale");

        if (compound.hasKey("ScriptData", Constants.NBT.TAG_COMPOUND)) {
            AbilityScript handler = new AbilityScript();
            handler.readFromNBT(compound.getCompoundTag("ScriptData"));
            setScriptHandler(handler);
        }
    }

    public enum Type {
        Cast,
        Toggle,
        Animated
    }
}
