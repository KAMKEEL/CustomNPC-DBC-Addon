package kamkeel.npcdbc.api.effect;

public interface ICustomEffect extends IStatusEffect {

    void setMenuName(String name);

    String getMenuName();

    void setName(String name);

    String getIcon();

    void setIcon(String icon);

    int getEveryXTick();

    void setEveryXTick(int everyXTick);

    int getIconX();

    void setIconX(int iconX);

    int getIconY();

    void setIconY(int iconY);

    int getWidth();

    void setWidth(int width);

    int getHeight();

    void setHeight(int height);

    boolean isLossOnDeath();

    void setLossOnDeath(boolean lossOnDeath);

    ICustomEffect save();

    void setID(int id);
}


