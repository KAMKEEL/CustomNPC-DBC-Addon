package kamkeel.npcdbc.api.form;

public interface IFormKaiokenStackables {

    void setKaioDrain(float drain);

    float getKaioDrain();

    /**
     * <code>false</code> - disables advanced configs. <br>
     * <code>true</code> - enables advanced configs. <br> <br>
     *
     * Normal configs multiply the current form's kaioken drain
     * (For example, if base form normally drains 20 HP per tick, and the <code>globalMulti</code> is 5, the drain in now 20x5 = 100)<br><br>
     *
     * Advanced configs replace the current DBC forms kaioken balance values, <code>globalMulti</code> is simply just a scalar value.
     * @param isOn true or false
     */
    void setMultiplyingCurrentFormDrain(boolean isOn);
    /**
     * @return True if using advanced configs.
     */
    boolean isMultiplyingCurrentFormDrain();

    /**
     *
     * @param state2 0 - Lowest kaioken state (Ex. Kaioken x2), 5 - highest kaioken state
     * @param strained if true, edit the drain value while strained
     * @param value new state specific multi.
     */
    void setKaioState2Balance(int state2, boolean strained, float value);

    /**
     * @param state2 0 - Lowest kaioken state (Ex. Kaioken x2), 5 - highest kaioken state
     * @param strained if true, gets the drain value while strained
     * @return State specific drain multi
     */
    float getKaioState2Balance(int state2, boolean strained);

    /**
     * @param state2  0 - Lowest kaioken state (Ex. Kaioken x2), 5 - highest kaioken state
     * @return The current attribute multiplier for that state.
     */
    float getKaiokenAttributeMulti(int state2);

    /**
     * <code>state2Multi * mutliScalar</code> is the final attribute multiplier.
     * @return multiScalar
     */
    float getKaiokenMultiScalar();

    /**
     * @param state2 0 - Lowest kaioken state (Ex. Kaioken x2), 5 - highest kaioken state
     * @param multi Multi for this current state
     */
    void setKaiokenAttributeMulti(int state2, float multi);

    /**
     * <code>state2Multi * mutliScalar</code> is the final drain.
     * @param scalar How much to scale the multi
     */
    void setKaiokenMultiScalar(float scalar);

    /**
     * @return If this is using normal Kaioken multis (defined in <code>jinryuujrmcore.cfg</code>)
     */
    boolean isUsingGlobalAttributeMultis();

    /**
     *
     * @param isUsing If this form should use global Kaioken multis (defined in <code>jinryuujrmcore.cfg</code>)
     */
    void setUsingGlobalAttributeMultis(boolean isUsing);


}
