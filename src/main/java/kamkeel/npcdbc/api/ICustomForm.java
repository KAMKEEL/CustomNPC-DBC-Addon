package kamkeel.npcdbc.api;

public interface ICustomForm {

    String getName();

    void setName(String name);

    int getRace();

    void setRace(int race);

    float getAllMulti();

    void setAllMulti(float allMulti);

    float getStrengthMulti();

    void setStrengthMulti(float strengthMulti);

    float getDexMulti();

    void setDexMulti(float dexMulti);

    float getWillMulti();

    void setWillMulti(float willMulti);

    float getConMulti();

    void setConMulti(float conMulti);

    float getMindMulti();

    void setMindMulti(float mindMulti);

    float getSpiritMulti();

    void setSpiritMulti(float spiritMulti);

    boolean isCanKaioken();

    void setCanKaioken(boolean canKaioken);

    float getKaiokenMulti();

    void setKaiokenMulti(float kaiokenMulti);

    int getAuraColor();

    void setAuraColor(int auraColor);
}
