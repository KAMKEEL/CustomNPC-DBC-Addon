package kamkeel.npcdbc.api.form;

public interface IAdvancedFormStat {


    boolean isEnabled();

    void setEnabled(boolean enabled);

    int getBonus();

    void setBonus(int bonus);

    float getMultiplier();

    void setMultiplier(float multiplier);
}
