package kamkeel.npcdbc.api.form;

/**
 * This interface is heavily based on how DBC calculates its form masteries. Please check any race's form_mastery.cfg config to
 * get a better understanding on how this interface functions
 */
public interface IFormMastery {
    /**
     * @param type 3 Legal: "flat", "perlevel", "max"
     * @return value of specified type
     */

    /**
     * @param type  4 Legal: "attribute","kidrain","strain","healthrequirement"
     * @param type1 3 Legal: "flat", "perlevel", "minormax"
     * @return value of specified type
     */
    float getMulti(String type, String type1);

    /**
     * @param type  4 Legal: "attribute","kidrain","strain","healthrequirement"
     * @param type1 3 Legal: "flat", "perlevel", "minormax"
     * @param value amount to change type1 of type by
     */
    void setMulti(String type, String type1, float value);

    /**
     * @param eventType 4 Legal: "update", "attack", "damaged", "fireki"
     * @param type      4 Legal: "gain", "flat", "permind", "max"
     * @return the value of specified type within eventType
     */
    float getGain(String eventType, String type);

    /**
     * @param eventType 4 Legal: "update", "attack", "damaged", "fireki"
     * @param type      4 Legal: "gain", "flat", "permind", "max"
     * @param value     amount to change type by within eventType
     */
    void setGain(String eventType, String type, float value);

    float getMaxLevel();

    void setMaxLevel(float value);

    float getInstantTransformationUnlockLevel();

    void setInstantTransformationUnlockLevel(float value);

    boolean hasInstantTransformationUnlockLevel();

    /**
     * Saves CustomForm with the New Form Mastery Modifications
     *
     * @return IFormMastery self object
     */
    IFormMastery save();
}
