package kamkeel.npcdbc.constants;

public enum DBCAnimations {
    NAMEKREGEN("Namek_Regen"),
    FUSIONLEFT("Fusion_Left"),
    FUSIONRIGHT("Fusion_Right");

    public final String fileName;

    DBCAnimations(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
