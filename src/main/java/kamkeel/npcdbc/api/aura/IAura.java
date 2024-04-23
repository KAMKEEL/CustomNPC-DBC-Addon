package kamkeel.npcdbc.api.aura;

public interface IAura {

    String getName();

    void setName(String name);

    String getMenuName();

    void setMenuName(String name);

    int getID();

    /**
     * Do not use this unless you know what you are changing. Dangerous to change.
     */
    void setID(int newID);

    IAura save();


}
