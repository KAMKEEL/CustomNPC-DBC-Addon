package kamkeel.npcdbc.data.race.registry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.race.BioAndroidRaceRenderer;
import kamkeel.npcdbc.client.race.IRaceRenderer;
import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.form.FormRace;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.builder.RaceBuilder;
import kamkeel.npcs.util.Register;
import noppes.npcs.LogWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RaceRegistry extends Register<Race> {
    public static final RaceRegistry INSTANCE = RaceRegistry.create("npcdbc", "DBC Addon");

    protected RaceRegistry(String namespace) {
        super("race", namespace);
    }

    @SideOnly(Side.CLIENT)
    private final Map<String, IRaceRenderer> renderers = new HashMap<>();

    public static RaceRegistry create(String namespace, String displayName) {
        if (!REGISTERED_NAMESPACES.containsKey("race"))
            REGISTERED_NAMESPACES.put("race", new ArrayList<>());

        if (REGISTERED_NAMESPACES.get("race").contains(namespace)) {
            LogWriter.error("REGISTER ABILITIES: Namespace " + namespace + " already registered!");
        }

        REGISTERED_NAMESPACES.get("race").add(namespace);
        NAMESPACE_DISPLAY_NAMES.put(namespace, displayName);

        return new RaceRegistry(namespace);
    }

    @Override
    public Race register(String factoryName, Supplier<Race> factory) {
        if (factory.get() == null) return null;

        return super.register(factoryName, factory);
    }

    public Race register(RaceBuilder builder) {
        Race race = builder.registry(this).build();
        if (race == null) return null;

        return super.register(race.getName(), builder::build);
    }

    public void register() {
        for (Map.Entry<String, Supplier<Race>> entry : entries.entrySet()) {
            Race race = entry.getValue().get();
            RaceController.Instance.register(race);

            if (race.formTree != null) {
                race.formTree.register(FormRace.of(race.id));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerRenderer(String key, IRaceRenderer renderer) {
        renderers.put(key, renderer);
    }

    @SideOnly(Side.CLIENT)
    public IRaceRenderer getRenderer(String key) {
        return key == null ? null : renderers.get(key);
    }

    @SideOnly(Side.CLIENT)
    public IRaceRenderer getRenderer(Race race) {
        return getRenderer(race.display.rendererKey);
    }

    @SideOnly(Side.CLIENT)
    public static void registerClient(){
        INSTANCE.registerRenderer("npcdbc:bio_android", new BioAndroidRaceRenderer());
    }
}
