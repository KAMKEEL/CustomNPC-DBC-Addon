package kamkeel.npcdbc.api.form;

public interface IFormKaiokenStackables {

    /**
     * @return true if this form is using global kaioken configs (the ones specified in <code>jinryuujrmcore.cfg</code>)
     */
    boolean isUsingGlobalKaiokenMultis();

    /**
     * @param useGlobal True to rely on global configs, false to rely on the forms internal configs.
     */
    void toggleGlobalKaiokenMulti(boolean useGlobal);

    /**
     * @param state2 0 - Lowest kaioken state (Ex. Kaioken x2), 5 - highest kaioken state
     * @param multi Kaioken multiplier for this form
     */
    void setKaioStateMulti(int state2, float multi);

    float getKaioStateMulti(int state2);

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
     * @param isOn
     */
    void toggleAdvanedDrain(boolean isOn);
    /**
     * @return True if using advanced configs.
     */
    boolean usingAdvancedDrain();

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

}
