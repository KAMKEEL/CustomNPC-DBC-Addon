package kamkeel.npcdbc.api.form;

/**
 * This interface is heavily based on controlling Forms interacting with other forms
 * from DBC Vanilla forms and Custom Forms
 */
public interface IFormStackable {

    /**
     * @param DBCForm Non-racial DBC Forms, 20 Kaioken, 21 Mystic, 22 UI, 23 GoD
     */
    void setFormMulti(int DBCForm, float multi);

    /**
     * @param DBCForm Non-racial DBC Forms, 20 Kaioken, 21 Mystic, 22 UI, 23 GoD
     * @return DBCForm's multi
     */
    float getFormMulti(int DBCForm);

    /**
     * @param DBCNonRacialFormID Non-racial DBC Forms, 20 Kaioken, 21 Mystic, 22 UI, 23 GoD
     * @return True if can stack DBCNonRacialFormID on Custom Form
     */
    boolean isFormStackable(int DBCNonRacialFormID);

    boolean isVanillaStackable();

    /**
     * @param vanillaStackable True makes this custom form stackable onto Vanilla DBC racial forms. i.e if you go into DBC SSBlue then go into a custom form with default presets, you will maintain the
     *                         appearance and aura of DBC SSBlue. However, custom forms always have stat calculation priority. If vanilla DBC SSBlue has multi of 100x, and CF has 10x, SSBlue CF will only have the 10x of CF multi.
     */
    void setVanillaStackable(boolean vanillaStackable);

    /**
     * @param DBCNonRacialFormID Non-racial DBC Forms, 20 Kaioken, 21 Mystic, 22 UI, 23 GoD
     */
    void allowStackForm(int DBCNonRacialFormID, boolean stackForm);


    void setState2Factor(int dbcForm, float factor);

    /**
     * @param dbcForm Legal args: 20 Kaioken, 21 UltraInstinct
     * @return how higher a stackable form's multi gets as you go up in state2
     * i.e. If KK form multi is 10x and State2Factor is 1x, very first KK form multi will be 10x, second KK form multi is 13.3x, final KK form will be 20x
     * if State2Factor is 2x, first is 10x, second is 16.6, final KK form will be 30 and so on.
     * This value scales off as a factor of the form's multiplier
     */
    float getState2Factor(int dbcForm);

    IFormStackable save();
}
