package kamkeel.npcdbc.data;

import kamkeel.npcdbc.api.ICustomForm;
import kamkeel.npcdbc.constants.DBCRace;

public class CustomForm implements ICustomForm {

    public String name;
    public int race = DBCRace.HUMAN;
    public float allMulti = 1.0f;

    public float strengthMulti = 1.0f;
    public float dexMulti = 1.0f;
    public float willMulti = 1.0f;
    public float conMulti = 1.0f;
    public float mindMulti = 1.0f;
    public float spiritMulti = 1.0f;

    public boolean canKaioken = true;
    public float kaiokenMulti = 1.0f;

    public int auraColor = 1;

    public CustomForm(String name){
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getRace() {
        return race;
    }

    @Override
    public void setRace(int race) {
        this.race = race;
    }

    @Override
    public float getAllMulti() {
        return allMulti;
    }

    @Override
    public void setAllMulti(float allMulti) {
        this.allMulti = allMulti;
    }

    @Override
    public float getStrengthMulti() {
        return strengthMulti;
    }

    @Override
    public void setStrengthMulti(float strengthMulti) {
        this.strengthMulti = strengthMulti;
    }

    @Override
    public float getDexMulti() {
        return dexMulti;
    }

    @Override
    public void setDexMulti(float dexMulti) {
        this.dexMulti = dexMulti;
    }

    @Override
    public float getWillMulti() {
        return willMulti;
    }

    @Override
    public void setWillMulti(float willMulti) {
        this.willMulti = willMulti;
    }

    @Override
    public float getConMulti() {
        return conMulti;
    }

    @Override
    public void setConMulti(float conMulti) {
        this.conMulti = conMulti;
    }

    @Override
    public float getMindMulti() {
        return mindMulti;
    }

    @Override
    public void setMindMulti(float mindMulti) {
        this.mindMulti = mindMulti;
    }

    @Override
    public float getSpiritMulti() {
        return spiritMulti;
    }

    @Override
    public void setSpiritMulti(float spiritMulti) {
        this.spiritMulti = spiritMulti;
    }

    @Override
    public boolean isCanKaioken() {
        return canKaioken;
    }

    @Override
    public void setCanKaioken(boolean canKaioken) {
        this.canKaioken = canKaioken;
    }

    @Override
    public float getKaiokenMulti() {
        return kaiokenMulti;
    }

    @Override
    public void setKaiokenMulti(float kaiokenMulti) {
        this.kaiokenMulti = kaiokenMulti;
    }

    @Override
    public int getAuraColor() {
        return auraColor;
    }

    @Override
    public void setAuraColor(int auraColor) {
        this.auraColor = auraColor;
    }
}
