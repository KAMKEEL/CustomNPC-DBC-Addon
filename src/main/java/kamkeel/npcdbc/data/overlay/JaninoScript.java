package kamkeel.npcdbc.data.overlay;

import cpw.mods.fml.common.FMLCommonHandler;
import io.github.somehussar.janinoloader.api.script.IScriptBodyBuilder;
import io.github.somehussar.janinoloader.api.script.IScriptClassBody;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NBTTags;
import noppes.npcs.config.ConfigScript;
import noppes.npcs.controllers.ScriptController;
import org.codehaus.commons.compiler.Sandbox;

import java.security.Permissions;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class JaninoScript<T> {
    public boolean enabled = false;

    public String script = "";
    public List<String> externalScripts = new ArrayList<>();
    public String fullScript = "";
    public boolean evaluated;

    private String language = "Java";
    public TreeMap<Long, String> console = new TreeMap();

    public final Class<T> type;
    protected Sandbox sandbox = new Sandbox(new Permissions());
    public final IScriptBodyBuilder<T> builder;
    protected IScriptClassBody<T> scriptBody;

    protected JaninoScript(Class<T> type) {
        this.type = type;
        this.builder = IScriptBodyBuilder
            .getBuilder(type, CustomNpcPlusDBC.getClientCompiler());
        configure(builder);

        this.scriptBody = builder.build();
    }

    /**
     * Safe hook — called AFTER builder creation, BEFORE build()
     */
    protected void configure(IScriptBodyBuilder<T> builder) {
    }


    protected T getUnsafe() {
        return scriptBody.get();
    }

    public <R> R call(Function<T, R> fn) {
        return sandbox.confine((PrivilegedAction<R>) () -> {
            T t = scriptBody.get();
            return t != null ? fn.apply(t) : null;
        });
    }

    public void run(Consumer<T> fn) {
        sandbox.confine((PrivilegedAction<Void>) () -> {
            T t = scriptBody.get();
            if (t != null)
                fn.accept(t);
            return null;
        });
    }

    public void setScript(String script) {
        if (!this.script.equals(script))
            this.evaluated = false;

        this.script = script;
        reloadScript();
    }

    public void reloadScript() {
        try {
            scriptBody.setScript(getFullCode());
        } catch (Exception e) {
        }
    }

    private String getFullCode() {
        if (!isEnabled())
            return fullScript = "";

        if (!this.evaluated) {
            // build includes first depending on config setting
            StringBuilder sb = new StringBuilder();
            this.appendExternalScripts(sb);

            // then your per‐hook main script
            if (this.script != null && !this.script.isEmpty())
                sb.append(this.script).append("\n");

            this.fullScript = sb.toString();
        }
        return this.fullScript;
    }

    private void appendExternalScripts(StringBuilder sb) {
        for (String scriptName : externalScripts) {
            String code = ScriptController.Instance.scripts.get(scriptName);
            if (code != null && !code.isEmpty())
                sb.append(code).append("\n");
        }
    }


    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("enabled", enabled);
        compound.setString("script", script);
        compound.setTag("externalScripts", NBTTags.nbtStringList(this.externalScripts));
        compound.setTag("console", NBTTags.NBTLongStringMap(this.console));
        return compound;
    }

    public JaninoScript readFromNBT(NBTTagCompound compound) {
        this.enabled = compound.getBoolean("enabled");
        setScript(compound.getString("script"));
        this.externalScripts = NBTTags.getStringList(compound.getTagList("externalScripts", 10));
        this.console = NBTTags.GetLongStringMap(compound.getTagList("console", 10));

        return this;
    }

    public boolean isEnabled() {
        return this.enabled && ScriptController.HasStart && ConfigScript.ScriptingEnabled;
    }

    public boolean isClient() {
        return FMLCommonHandler.instance()
            .getEffectiveSide()
            .isClient();
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean b) {
        this.enabled = b;
        reloadScript();
    }

    public String getLanguage() {
        return this.language;
    }

    public void appendConsole(String message) {
        if (message != null && !message.isEmpty()) {
            long time = System.currentTimeMillis();
            if (this.console.containsKey(time)) {
                message = (String) this.console.get(time) + "\n" + message;
            }

            this.console.put(time, message);

            while (this.console.size() > 40) {
                this.console.remove(this.console.firstKey());
            }
        }
    }

    public Map<Long, String> getConsoleText() {
        TreeMap<Long, String> map = new TreeMap();
        int tab = 0;

        Iterator var5 = console.entrySet()
            .iterator();

        while (var5.hasNext()) {
            Map.Entry<Long, String> longStringEntry = (Map.Entry) var5.next();
            map.put(longStringEntry.getKey(), " tab " + tab + ":\n" + (String) longStringEntry.getValue());
        }


        return map;
    }

    public void clearConsole() {
        console.clear();
    }
}


