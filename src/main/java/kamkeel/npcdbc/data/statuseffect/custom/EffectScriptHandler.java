package kamkeel.npcdbc.data.statuseffect.custom;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.controllers.data.IScriptHandler;

import java.util.*;

public class EffectScriptHandler implements IScriptHandler {
    public ScriptContainer container;
    public String scriptLanguage = "ECMAScript";
    public boolean enabled = false;
    public boolean hasInited = false;
    private CustomEffect effect;

    public EffectScriptHandler(CustomEffect effect) {
        this.effect = effect;
    }

    @Override
    public void callScript(String s, Event event) {
        if (container != null)
            container.run(s, event);
    }

    @Override
    public boolean isClient() {
        return FMLCommonHandler.instance().getEffectiveSide().isClient();
    }

    @Override
    public boolean getEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean b) {
        this.enabled = b;
    }

    @Override
    public String getLanguage() {
        return this.scriptLanguage;
    }

    @Override
    public void setLanguage(String s) {
        this.scriptLanguage = s;
    }

    @Override
    public void setScripts(List<ScriptContainer> list) {
        if (list == null || list.isEmpty()) {
            container = null;
            return;
        }
        container = list.get(0);
    }

    @Override
    public List<ScriptContainer> getScripts() {
        if (container == null)
            return new ArrayList<>();
        return Collections.singletonList(container);
    }

    @Override
    public String noticeString() {
        return "";
    }

    @Override
    public Map<Long, String> getConsoleText() {
        TreeMap<Long, String> map = new TreeMap();
        int tab = 0;
        Iterator var3 = this.getScripts().iterator();

        while(var3.hasNext()) {
            ScriptContainer script = (ScriptContainer)var3.next();
            ++tab;
            Iterator var5 = script.console.entrySet().iterator();

            while(var5.hasNext()) {
                Map.Entry<Long, String> longStringEntry = (Map.Entry)var5.next();
                map.put(longStringEntry.getKey(), " tab " + tab + ":\n" + (String)longStringEntry.getValue());
            }
        }

        return map;
    }

    @Override
    public void clearConsole() {
        Iterator var1 = this.getScripts().iterator();

        while(var1.hasNext()) {
            ScriptContainer script = (ScriptContainer)var1.next();
            script.console.clear();
        }
    }
}
