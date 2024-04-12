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

    public boolean kaiokenStackable = true, uiStackable = true;
    public float kaiokenMulti = 1.0f, UIMulti = 1.0f;

    public int auraColor = 1;

    public CustomForm(String name) {
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
    public void setAttributeMulti(int id, float multi) {
        switch (id) {
            case 0:
                strengthMulti = multi;
                break;
            case 1:
                dexMulti = multi;
                break;
            case 3:
                willMulti = multi;
                break;

        }
    }

    @Override
    public float getAttributeMulti(int id) {
        switch (id) {
            case 0:
                return strengthMulti;
            case 1:
                return dexMulti;
            case 3:
                return willMulti;

        }
        return 1.0f;
    }


    @Override
    public boolean isKaiokenStackable() {
        return kaiokenStackable;
    }


    @Override
    public boolean isUIStackable() {
        return uiStackable;
    }

    @Override
    public void stackKaioken(boolean stackKaioken) {
        kaiokenStackable = stackKaioken;
    }

    @Override
    public void stackUI(boolean stackUI) {
        uiStackable = stackUI;
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
    public float getUIMulti() {
        return UIMulti;
    }

    @Override
    public void setUIMulti(float UIMulti) {
        this.UIMulti = UIMulti;
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
