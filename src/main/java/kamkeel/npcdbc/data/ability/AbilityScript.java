package kamkeel.npcdbc.data.ability;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import io.netty.buffer.ByteBuf;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcs.util.ByteBufUtils;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.config.ConfigScript;
import noppes.npcs.constants.EnumScriptType;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.INpcScriptHandler;

import java.io.IOException;
import java.util.*;

public class AbilityScript implements INpcScriptHandler {
    public ScriptContainer container;
    public String scriptLanguage = "ECMAScript";
    public boolean enabled = false;

    public AbilityScript() {
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setString("ScriptLanguage", this.scriptLanguage);
        compound.setBoolean("ScriptEnabled", this.enabled);
        if (this.container != null) {
            compound.setTag("ScriptContent", this.container.writeToNBT(new NBTTagCompound()));
        }

        return compound;
    }

    public AbilityScript readFromNBT(NBTTagCompound compound) {
        this.scriptLanguage = compound.getString("ScriptLanguage");
        this.enabled = compound.getBoolean("ScriptEnabled");
        if (compound.hasKey("ScriptContent", 10)) {
            this.container = new ScriptContainer(this);
            this.container.readFromNBT(compound.getCompoundTag("ScriptContent"));
        }

        return this;
    }

    public void callScript(AbilityScript.ScriptType type, DBCPlayerEvent.AbilityEvent event) {
        this.callScript((String)type.function, (Event)event);
    }

    public boolean isEnabled() {
        return this.enabled && ScriptController.HasStart && this.container != null && ConfigScript.ScriptingEnabled;
    }

    public void callScript(EnumScriptType type, Event event) {
        this.callScript(type.function, event);
    }

    public void callScript(String s, Event event) {
        if (this.isEnabled()) {
            this.container.run(s, event);
        }
    }

    public boolean isClient() {
        return FMLCommonHandler.instance().getEffectiveSide().isClient();
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean b) {
        this.enabled = b;
    }

    public String getLanguage() {
        return this.scriptLanguage;
    }

    public void setLanguage(String s) {
        this.scriptLanguage = s;
    }

    public void setScripts(List<ScriptContainer> list) {
        if (list != null && !list.isEmpty()) {
            this.container = (ScriptContainer)list.get(0);
        } else {
            this.container = null;
        }
    }

    public List<ScriptContainer> getScripts() {
        return (List<ScriptContainer>)(this.container == null ? new ArrayList() : Collections.singletonList(this.container));
    }

    public String noticeString() {
        return "";
    }

    public Map<Long, String> getConsoleText() {
        TreeMap<Long, String> map = new TreeMap();
        int tab = 0;

        for(ScriptContainer script : this.getScripts()) {
            ++tab;

            for(Map.Entry<Long, String> longStringEntry : script.console.entrySet()) {
                map.put(longStringEntry.getKey(), " tab " + tab + ":\n" + (String)longStringEntry.getValue());
            }
        }

        return map;
    }

    public void clearConsole() {
        for(ScriptContainer script : this.getScripts()) {
            script.console.clear();
        }
    }

    public void saveScript(ByteBuf buffer) throws IOException {
        int tab = buffer.readInt();
        int totalScripts = buffer.readInt();
        if (totalScripts == 0) {
            this.container = null;
        }

        if (tab == 0) {
            NBTTagCompound tabCompound = ByteBufUtils.readNBT(buffer);
            ScriptContainer script = new ScriptContainer(this);
            script.readFromNBT(tabCompound);
            this.container = script;
        } else {
            NBTTagCompound compound = ByteBufUtils.readNBT(buffer);
            this.setLanguage(compound.getString("ScriptLanguage"));
            if (!ScriptController.Instance.languages.containsKey(this.getLanguage())) {
                if (!ScriptController.Instance.languages.isEmpty()) {
                    this.setLanguage((String)ScriptController.Instance.languages.keySet().toArray()[0]);
                } else {
                    this.setLanguage("ECMAScript");
                }
            }

            this.setEnabled(compound.getBoolean("ScriptEnabled"));
        }

    }

    public static enum ScriptType {
        OnAbilityActivate("onAbilityActivate"),
        OnAbilityToggle("onAbilityToggle"),
        OnAbilityAnimation("onAbilityAnimation");

        public final String function;

        private ScriptType(String functionName) {
            this.function = functionName;
        }
    }
}
