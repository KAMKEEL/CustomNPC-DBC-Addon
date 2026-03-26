package kamkeel.npcdbc;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.race.BioAndroidRaceRenderer;
import kamkeel.npcdbc.client.race.IRaceRenderer;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcs.util.Register;

import java.util.HashMap;
import java.util.Map;

public class AddonRegistries {

    public static final Races RACES = Races.create("npcdbc", "DBC Addon");
    public static final Forms FORMS = Forms.create("npcdbc", "DBC Addon");

    @SideOnly(Side.CLIENT)
    public static void registerClient() {
        Races.registerRenderer("npcdbc:bio_android", new BioAndroidRaceRenderer());
    }

    public static class Races extends Register<Race> {

        @SideOnly(Side.CLIENT)
        private static final Map<String, IRaceRenderer> renderers = new HashMap<>();

        private Races(String namespace) {
            super("race", namespace);
        }

        public static Races create(String namespace, String displayName) {
            Register.registerNamespace("race", namespace, displayName);
            return new Races(namespace);
        }

        public Race register(Race race) {
            RaceController.Instance.register(race);
            return super.register(race.getName(), () -> race);
        }

        @SideOnly(Side.CLIENT)
        public static void registerRenderer(String key, IRaceRenderer renderer) {
            renderers.put(key, renderer);
        }

        @SideOnly(Side.CLIENT)
        public static IRaceRenderer removeRenderer(String key) {
            return renderers.remove(key);
        }

        @SideOnly(Side.CLIENT)
        public static IRaceRenderer getRenderer(String key) {
            return key == null ? null : renderers.get(key);
        }

        @SideOnly(Side.CLIENT)
        public static IRaceRenderer getRenderer(Race race) {
            return getRenderer(race.display.rendererKey);
        }
    }

    public static class Forms extends Register<Form> {

        private Forms(String namespace) {
            super("form", namespace);
        }

        public static Forms create(String namespace, String displayName) {
            Register.registerNamespace("form", namespace, displayName);
            return new Forms(namespace);
        }

        public Form register(Form form) {
            FormController.Instance.registerBuiltIn(form);
            return super.register(form.getName(), () -> form);
        }
    }
}

