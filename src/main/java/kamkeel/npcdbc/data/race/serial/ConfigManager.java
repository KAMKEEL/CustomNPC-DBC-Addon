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
import java.util.function.Supplier;

public class ConfigManager<T extends DataSerializable> {

    private String subdirectory;
    private final Function<T, String> nameExtractor;

    private Supplier<String> directory;

    public ConfigManager(Supplier<String> directory, String subdirectory, Function<T, String> fileNameExtractor) {
        this.directory = directory;
        this.subdirectory = subdirectory;
        this.nameExtractor = fileNameExtractor;
    }
    
    public ConfigManager(String subdirectory, Function<T, String> fileNameExtractor) {
        this(null, subdirectory, fileNameExtractor);
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

    public void deleteConfig(String name) {
        File file = getConfigFile(name);
        if (file.exists() && file.delete())
            LogWriter.info("[" + subdirectory + "] Deleted config: " + name);
    }

    private File getConfigFile(String name) {
        return new File(getConfigDirectory(), subdirectory + "/" + name + ".yml");
    }
    private File getConfigFile(T target) {
        return getConfigFile(nameExtractor.apply(target));
    }

    private File getConfigDirectory() {
        return directory != null ? new File(directory.get()) : CustomNpcs.getWorldSaveDirectory();
    }
}
