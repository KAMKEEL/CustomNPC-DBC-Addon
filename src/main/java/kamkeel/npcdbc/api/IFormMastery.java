package kamkeel.npcdbc.api;

/**
 * This interface is heavily based on how DBC calculates its form masteries. Please check any race's form_mastery.cfg config to
 * get a better understanding on how this interface functions
 */
public interface IFormMastery {
    public IFormMastery save();

    /**
     * @param type  3 Legal: "flat", "perlevel", "max"
     * @param value amount to change type by
     */
    void setAttributeMulti(String type, float value);

    /**
     * @param eventType 4 Legal: "update", "attack", "damaged", "fireki"
     * @param type      4 Legal: "gain", "flat", "permind", "max"
     * @param value     amount to change type by within eventType
     */
    void setGain(String eventType, String type, float value);

    /**
     * @param type 3 Legal: "flat", "perlevel", "max"
     * @return value of specified type
     */
    float getAttributeMulti(String type);

    /**
     * @param eventType 4 Legal: "update", "attack", "damaged", "fireki"
     * @param type      4 Legal: "gain", "flat", "permind", "max"
     * @return the value of specified type within eventType
     */
    float getGain(String eventType, String type);

    /**
     * @param type 3 Legal: "flat", "perlevel", "MinOrMax"
     * @return value of specified type
     */
    float getKiDrainMulti(String type);

    /**
     * @param type  3 Legal: "flat", "perlevel", "MinOrMax"
     * @param value amount to change type by
     */
    void setKiDrainMulti(String type, float value);

    /**
     * @param type  3 Legal: "flat", "perlevel", "MinOrMax"
     * @param value amount to change type by
     */
    void setHealthRequirementMulti(String type, float value);

    /**
     * @param type 3 Legal: "flat", "perlevel", "MinOrMax"
     * @return value of specified type
     */
    float getHealthRequirementMulti(String type);

    /**
     * @param type  3 Legal: "flat", "perlevel", "MinOrMax"
     * @param value amount to change type by
     */
    void setStrainMulti(String type, float value);

    /**
     * @param type 3 Legal: "flat", "perlevel", "MinOrMax"
     * @return value of specified type
     */
    float getStrainMulti(String type);
}
