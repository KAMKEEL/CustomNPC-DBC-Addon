package kamkeel.npcdbc.data.race.serial;

import org.yaml.snakeyaml.Yaml;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

public class ConfigManager<T extends DataSerializable> {

    private final String subdirectory;
    private final Function<T, String> nameExtractor;

    public ConfigManager(String subdirectory, Function<T, String> fileNameExtractor) {
        this.subdirectory  = subdirectory;
        this.nameExtractor = fileNameExtractor;
    }

    public void loadOrCreate(T target) {
        File file = getConfigFile(target);
        if (file.exists()) {
            load(target, file);
        } else {
            exportTemplate(target, file);
        }
    }

    private void load(T target, File file) {
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            Yaml yaml = new Yaml();
            Map<String, Object> map = yaml.load(reader);
            target.deserialize(DataCompound.ofYaml(map));
            LogWriter.info("[" + subdirectory + "] Loaded config: " + nameExtractor.apply(target));
        } catch (Exception e) {
            LogWriter.error("[" + subdirectory + "] Failed to load config for " + nameExtractor.apply(target) + " — " + e.getMessage());
        } finally {
            if (reader != null) try { reader.close(); } catch (IOException ignored) {}
        }
    }

    private void exportTemplate(T target, File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        FileWriter writer = null;
        try {
            DataCompound data = DataCompound.create(true);
            target.serialize(data);
            writer = new FileWriter(file);
            writer.write(data.toYamlString());
            LogWriter.info("[" + subdirectory + "] Exported template config: " + nameExtractor.apply(target));
        } catch (Exception e) {
            LogWriter.error("[" + subdirectory + "] Failed to export template for " + nameExtractor.apply(target) + " — " + e.getMessage());
        } finally {
            if (writer != null) try { writer.close(); } catch (IOException ignored) {}
        }
    }

    private File getConfigFile(T target) {
        return new File(CustomNpcs.getWorldSaveDirectory(), subdirectory + "/" + nameExtractor.apply(target) + ".yml");
    }
}
