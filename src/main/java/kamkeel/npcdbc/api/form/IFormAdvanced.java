package kamkeel.npcdbc.api.form;

public interface IFormAdvanced {
    
    IAdvancedFormStat getStat(int id);

    void setStatEnabled(int id, boolean enabled);

    boolean isStatEnabled(int id);

    void setStatBonus(int id, int bonus);

    int getStatBonus(int id);

    void setStatMulti(int id, float multiplier);

    float getStatMulti(int id);

    IFormAdvanced save();
}
