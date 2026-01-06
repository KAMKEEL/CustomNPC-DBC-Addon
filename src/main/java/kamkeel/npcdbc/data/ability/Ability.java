package kamkeel.npcdbc.data.ability;

import kamkeel.npcdbc.controllers.AbilityController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
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
import noppes.npcs.scripted.event.AnimationEvent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static noppes.npcs.NoppesStringUtils.translate;

public class Ability {
    public int id = -1;
    public String name = "";
    public String menuName = "NEW ABILITY";
    public String description = "";
    public int cooldown = -1;
    public int kiCost = -1;

    public String icon = "";
    public int iconX = 0;
    public int iconY = 0;
    public int width = 16;
    public int height = 16;
    public float scale = 1;
    public Animation animation = null;

    public Type type = Type.Active;

    public Ability() {
    }

    public Ability(int id) {
        this.id = id;
    }

    public Ability(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getKiCost() {
        return kiCost;
    }

    public void setKiCost(int kiCost) {
        this.kiCost = kiCost;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getIconX() {
        return iconX;
    }

    public void setIconX(int iconX) {
        this.iconX = iconX;
    }

    public int getIconY() {
        return iconY;
    }

    public void setIconY(int iconY) {
        this.iconY = iconY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        if (AnimationController.Instance.has(animation.getName()))
            this.animation = animation;
    }

    public Ability save() {
        return AbilityController.Instance.saveAbility(this);
    }

    public Ability cloneAbility() {
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

        if (event.isCanceled()) {
            return false;
        }

        if (event.getCooldown() > -1 && abilityData.hasCooldown(id)) {
            Utility.sendMessage(player, translate("§c", "npcdbc.abilityCooldown", ": ", abilityData.abilityTimers.get(id) + ""));
            return false;
        }

        if (event.getKiCost() > -1 && data.Ki < event.getKiCost()) {
            Utility.sendMessage(player, translate("§c", "npcdbc.abilityNoKi"));
            return false;
        }

        if (!canFireEvent(player))
            return false;

        if (this.type == Type.Animated) {
            setupAnimations();

            if (this.animation != null) {
                AnimationData animData = (AnimationData) iPlayer.getAnimationData();

                animData.setEnabled(true);
                animData.setAnimation(this.animation);
                animData.updateClient();
            }
        }

        DBCEventHooks.onAbilityUsed(event);

        AbilityScript script = getScriptHandler();
        if (script != null) {
            script.callScript(getScriptType(), event);
        }

        if (event.getKiCost() > -1) {
            data.Ki -= event.getKiCost();
        }

        if (event.getCooldown() > -1) {
            abilityData.addCooldown(id, event.getCooldown());
        }

        info.updateClient();
        return true;
    }

    private DBCPlayerEvent.AbilityEvent getEventByType(IPlayer player) {
        if (type == Type.Active)
            return new DBCPlayerEvent.AbilityEvent.Activate(player, this);
        else if (type == Type.Toggle)
            return new DBCPlayerEvent.AbilityEvent.Toggle(player, this);
        else if (type == Type.Animated)
            return new DBCPlayerEvent.AbilityEvent.Animated(player, this);
        return null;
    }

    private AbilityScript.ScriptType getScriptType() {
        return AbilityScript.ScriptType.values()[type.ordinal()];
    }

    // Extra checking for AddonAbilities
    protected boolean canFireEvent(EntityPlayer player) {
        return true;
    }

    protected void setupAnimations() {

    }

    public void onStart(Consumer<IAnimation> onStart) {
        if (animation != null)
            animation.onStart(onStart);
    }

    public void onFrame(BiConsumer<Integer, IAnimation> onFrame) {
        if (animation != null)
            animation.onFrame(onFrame);
    }

    public void onEnd(Consumer<IAnimation> onEnd) {
        if (animation != null)
            animation.onEnd(onEnd);
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
        compound.setString("description", description);

        compound.setInteger("cooldown", cooldown);
        compound.setInteger("kiCost", kiCost);

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
        description = compound.getString("description");

        cooldown = compound.getInteger("cooldown");
        kiCost = compound.getInteger("kiCost");

        type = Type.values()[compound.getInteger("type")];

        if (compound.hasKey("ScriptData", Constants.NBT.TAG_COMPOUND)) {
            AbilityScript handler = new AbilityScript();
            handler.readFromNBT(compound.getCompoundTag("ScriptData"));
            setScriptHandler(handler);
        }
    }

    public enum Type {
        Active,
        Toggle,
        Animated
    }
}
