package kamkeel.npcdbc.data.effects;

/**
 * This is knowledge base class for
 * mapping DBC Effects to their valid ids.
 * Hard coded to decrypt their resource IDs
 * to Status ID as well as if their timers
 * are recorded.
 */
public class DBCEffect {
    public String name;
    public String langName;
    public int id;
    public int resourceID;
    boolean infinite;

    /**
     * Constructs a DBC Effect reference
     *
     * @param name       Name of the status effect
     * @param langName   langfile name
     * @param id         Gameplay ID of the status effect
     * @param resourceID Texture ID
     * @param infinite   is the status effect timed or not
     */
    public DBCEffect(String name, String langName, int id, int resourceID, boolean infinite) {
        this.name = name;
        this.langName = langName;
        this.id = id;
        this.resourceID = resourceID;
        this.infinite = infinite;
    }
}
