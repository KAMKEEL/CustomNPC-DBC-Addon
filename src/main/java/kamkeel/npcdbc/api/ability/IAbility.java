package kamkeel.npcdbc.api.ability;

import noppes.npcs.controllers.data.Animation;

public interface IAbility {
    int getID();

    void setID(int id);

    String getName();

    void setName(String name);

    String getMenuName();

    void setMenuName(String menuName);

    int getKiCost();

    void setKiCost(int kiCost);

    int getCooldown();

    void setCooldown(int cooldown);

    int getMaxUses();

    void setMaxUses(int maxUses);

    String getIcon();

    void setIcon(String icon);

    int getIconX();

    void setIconX(int iconX);

    int getIconY();

    void setIconY(int iconY);

    int getWidth();

    void setWidth(int width);

    int getHeight();

    void setHeight(int height);

    float getScale();

    void setScale(float scale);

    Animation getAnimation();

    void setAnimation(Animation animation);

    IAbility save();

    IAbility cloneAbility();
}
