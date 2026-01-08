/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api.form
 */

export interface IFormKaiokenStackables {
    setKaioDrain(drain: number): void;
    getKaioDrain(): number;
    /**
     * <code>false</code> - disables advanced configs. <br>
     * <code>true</code> - enables advanced configs. <br> <br>
     * <p>
     * Normal configs multiply the current form's kaioken drain
     * (For example, if base form normally drains 20 HP per tick, and the <code>globalMulti</code> is 5, the drain in now 20x5 = 100)<br><br>
     * <p>
     * Advanced configs replace the current DBC forms kaioken balance values, <code>globalMulti</code> is simply just a scalar value.
     *
     * @param isOn true or false
     */
    setMultiplyingCurrentFormDrain(isOn: boolean): void;
    /**
     * @return True if using advanced configs.
     */
    isMultiplyingCurrentFormDrain(): boolean;
    /**
     * @param state2   0 - Lowest kaioken state (Ex. Kaioken x2), 5 - highest kaioken state
     * @param strained if true, edit the drain value while strained
     * @param value    new state specific multi.
     */
    setKaioState2Balance(state2: number, strained: boolean, value: number): void;
    /**
     * @param state2   0 - Lowest kaioken state (Ex. Kaioken x2), 5 - highest kaioken state
     * @param strained if true, gets the drain value while strained
     * @return State specific drain multi
     */
    getKaioState2Balance(state2: number, strained: boolean): number;
    /**
     * @param state2 0 - Lowest kaioken state (Ex. Kaioken x2), 5 - highest kaioken state
     * @return The current attribute multiplier for that state.
     */
    getKaiokenAttributeMulti(state2: number): number;
    /**
     * <code>state2Multi * mutliScalar</code> is the final attribute multiplier.
     *
     * @return multiScalar
     */
    getKaiokenMultiScalar(): number;
    /**
     * @param state2 0 - Lowest kaioken state (Ex. Kaioken x2), 5 - highest kaioken state
     * @param multi  Multi for this current state
     */
    setKaiokenAttributeMulti(state2: number, multi: number): void;
    /**
     * <code>state2Multi * mutliScalar</code> is the final drain.
     *
     * @param scalar How much to scale the multi
     */
    setKaiokenMultiScalar(scalar: number): void;
    /**
     * @return If this is using normal Kaioken multis (defined in <code>jinryuujrmcore.cfg</code>)
     */
    isUsingGlobalAttributeMultis(): boolean;
    /**
     * @param isUsing If this form should use global Kaioken multis (defined in <code>jinryuujrmcore.cfg</code>)
     */
    setUsingGlobalAttributeMultis(isUsing: boolean): void;
}

