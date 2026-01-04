package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.constants.DBCAnimations;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.LogWriter;
import noppes.npcs.api.handler.data.IAnimation;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.controllers.data.Animation;
import noppes.npcs.util.NBTJsonUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBCAnimationController {
    public HashMap<Integer, Animation> animations = new HashMap<>();
    public static DBCAnimationController Instance = new DBCAnimationController();

    public DBCAnimationController() {
        Instance = this;
    }

    public static DBCAnimationController getInstance() {
        return Instance;
    }

    public void load() {
        animations = new HashMap<>();
        loadAnimations();
    }

    private void loadAnimations() {
        try {
            for (DBCAnimations anim : DBCAnimations.values()) {
                ResourceLocation location = new ResourceLocation(CustomNpcPlusDBC.ID, "animations/" + anim.getFileName() + ".json");
                File file = getFile(anim.getFileName() + ".json");

                if (file == null) {
                    LogWriter.info("DBCAnimationController: Animation " + anim.getFileName() + " not found.");
                    continue;
                }

                Animation animation = new Animation();
                animation.readFromNBT(NBTJsonUtil.LoadFile(file));

                animations.put(anim.ordinal(), animation);
            }
        } catch (Exception e) {
            LogWriter.except(e);
        }
    }

    public boolean has(String name) {
        return getAnimationFromName(name) != null;
    }

    public boolean has(int id) {
        return get(id) != null;
    }

    public IAnimation get(String name) {
        return getAnimationFromName(name);
    }

    public IAnimation get(int id) {
        return this.animations.get(id);
    }

    public IAnimation[] getAnimations() {
        ArrayList<IAnimation> animations = new ArrayList<>(this.animations.values());
        return animations.toArray(new IAnimation[0]);
    }

    public Animation getAnimationFromName(String animation) {
        for (Map.Entry<Integer, Animation> entryAnimation : AnimationController.getInstance().animations.entrySet()) {
            if (entryAnimation.getValue().name.equalsIgnoreCase(animation)) {
                return entryAnimation.getValue();
            }
        }
        return null;
    }

    public String[] getNames() {
        String[] names = new String[animations.size()];
        int i = 0;
        for (Animation animation : animations.values()) {
            names[i] = animation.name.toLowerCase();
            i++;
        }
        return names;
    }

    // maybe i'll want to use this in the future
//    public File getFile(ResourceLocation location) {
//        try (InputStream stream = Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream()) {
//
//            File file = new File("temp");
//
//            FileUtils.copyInputStreamToFile(stream, file);
//
//            return file;
//        } catch (Exception i) {
//            LogWriter.except(i);
//        }
//        return null;
//    }

    public File getFile(String fileName) {
        try (InputStream stream = CustomNpcPlusDBC.class.getResourceAsStream("/internal/data/animations/" + fileName)) {

            File file = new File("temp");

            FileUtils.copyInputStreamToFile(stream, file);

            return file;
        } catch (Exception i) {
            LogWriter.except(i);
        }
        return null;
    }
}
