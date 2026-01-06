package kamkeel.npcdbc.constants;

import kamkeel.npcdbc.controllers.DBCAnimationController;
import noppes.npcs.LogWriter;
import noppes.npcs.controllers.data.Animation;

public enum DBCAnimations {
    NAMEK_REGEN("Namek_Regen"),
    FUSION_LEFT("Fusion_Left"),
    FUSION_RIGHT("Fusion_Right");

    public final String fileName;
    private Animation animation;

    DBCAnimations(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public Animation get() {
        if (animation == null)
            animation = (Animation) DBCAnimationController.getInstance().get(this.fileName);
        return animation;
    }
}
