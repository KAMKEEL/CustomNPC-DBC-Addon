package kamkeel.npcdbc;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.client.race.IOverlayModel;
import kamkeel.npcdbc.client.race.IRaceRenderer;
import kamkeel.npcdbc.client.race.bio.BioAndroidCrestModel;
import kamkeel.npcdbc.client.race.bio.BioAndroidMaxTailModel;
import kamkeel.npcdbc.client.race.bio.BioAndroidTailModel;
import kamkeel.npcdbc.client.race.bio.BioAndroidWingsModel;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcdbc.data.race.races.bioandroid.BioAndroidLayers;
import kamkeel.npcs.util.Register;

import java.util.HashMap;
import java.util.Map;

public class AddonRegistries {

    public static final Races RACES = Races.create(CustomNpcPlusDBC.ID, "DBC Addon");
    public static final Forms FORMS = Forms.create(CustomNpcPlusDBC.ID, "DBC Addon");

    @SideOnly(Side.CLIENT)
    public static void registerClient() {
        OverlayModels.register(BioAndroidLayers.CREST_MODEL, new BioAndroidCrestModel());
        OverlayModels.register(BioAndroidLayers.WINGS_MODEL, new BioAndroidWingsModel());
        OverlayModels.register(BioAndroidLayers.TAIL_MODEL, new BioAndroidTailModel());
        OverlayModels.register(BioAndroidLayers.TAIL_MAX_MODEL, new BioAndroidMaxTailModel());
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

        @SideOnly(Side.CLIENT)
        public static void registerModelComponent(String key, IOverlayModel component) {
            OverlayModels.register(key, component);
        }

        @SideOnly(Side.CLIENT)
        public static IOverlayModel getModelComponent(String key) {
            return OverlayModels.get(key);
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

    public static class OverlayModels {
        private static final Map<String, IOverlayModel> models = new HashMap<>();
        
        public static void register(String key, IOverlayModel model) {
            models.put(key, model);
        }

        public static IOverlayModel get(String key) {
            return key == null ? null : models.get(key);
        }

        public static IOverlayModel remove(String key) {
            return models.remove(key);
        }
    }
}

