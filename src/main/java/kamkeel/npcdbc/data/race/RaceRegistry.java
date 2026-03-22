package kamkeel.npcdbc.data.race;

import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcs.util.Register;
import noppes.npcs.LogWriter;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

public class RaceRegistry extends Register<Race> {
    protected RaceRegistry(String namespace) {
        super("race", namespace);
    }

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

    public void register() {
        for (Map.Entry<String, Supplier<Race>> entry : entries.entrySet()) {
            RaceController.Instance.register(entry.getValue().get());
        }
    }
}
