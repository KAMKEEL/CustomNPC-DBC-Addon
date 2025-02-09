package kamkeel.npcdbc.api.effect;

public interface ICustomEffect extends IStatusEffect {

    void setMenuName(String name);

    String getMenuName();

    void setName(String name);

    String getIcon();

    void setIcon(String icon);

    /**
     * @return On which tick does the effect actually perform its {@link kamkeel.npcdbc.api.event.IDBCEvent.EffectEvent.Ticked} event
     */
    int getEveryXTick();

    /**
     * Sets on which tick the effect performs its {@link kamkeel.npcdbc.api.event.IDBCEvent.EffectEvent.Ticked} event <br>
     * <br>
     * In case a value lower than 10 is supplied, tick time is set to 10. <br>
     * In case a value that is not a multiple of 10 is supplied, the value is rounded to the closest 10.
     *
     * @param everyXTick number of ticks.
     */
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


