/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api
 */

export interface IDBCAddon extends IDBCPlayer {
    /**
     * @return Refer to {@link IDBCAddon#getAllAttributes()} and {@link IDBCAddon#getFullAttribute(int)}
     */
    getAllFullAttributes(): number[];
    /**
     * @return The mind a player is currently using for their skills
     */
    getUsedMind(): number;
    /**
     * @return The mind the player has currently available.
     */
    getAvailableMind(): number;
    /**
     * Set a players lock on state!
     *
     * @param lockOnTarget Reference to new target Entity or null to remove lock on.
     */
    setLockOnTarget(lockOnTarget: IEntityLivingBase): void;
    /**
     * This will only work if the player has the ki fist skill
     *
     * @param on Should ki fist be enabled
     */
    setKiFistOn(on: boolean): void;
    /**
     * This will only work if the player has the ki protection skill
     *
     * @param on Should ki protection be enabled
     */
    setKiProtectionOn(on: boolean): void;
    /**
     * This will only work if the player has Ki Fist &amp; Ki Infuse skills <br>
     * <br>
     * 0 - No ki weapons <br>
     * 1 - Ki Blade <br>
     * 2 - Ki Scythe <br>
     *
     * @param type ki weapon type
     */
    setKiWeaponType(type: number): void;
    /**
     * @return True if ki fist is enabled
     */
    kiFistOn(): boolean;
    /**
     * @return True if ki protection is enabled
     */
    kiProtectionOn(): boolean;
    /**
     * Checks what ki weapon the player has enabled <br>
     * <br>
     * 0 - No ki weapons <br>
     * 1 - Ki Blade <br>
     * 2 - Ki Scythe <br>
     *
     * @return The ki weapon type
     */
    getKiWeaponType(): number;
    /**
     * Checks if player has turbo on.
     *
     * @return True or false
     */
    isTurboOn(): boolean;
    /**
     * Sets the turbo state for the player
     *
     * @param on true turns turbo on, false turns it off.
     */
    setTurboState(on: boolean): void;
    /**
     * @return Player's max body
     */
    getMaxBody(): number;
    /**
     * @return Player's max hp (body)
     */
    getMaxHP(): number;
    /**
     * @return Current body as a percentage of max hp (body)
     */
    getBodyPercentage(): number;
    /**
     * @return Player's max ki
     */
    getMaxKi(): number;
    /**
     * @return Player's max stamina
     */
    getMaxStamina(): number;
    /**
     * @return an array of all player attributes
     */
    getAllAttributes(): number[];
    /**
     * @param attri               adds attri to player stats
     * @param multiplyAddedAttris if true, adds first then multiplies by multivalue
     * @param multiValue          value to multiply with
     */
    modifyAllAttributes(attri: number[], multiplyAddedAttris: boolean, multiValue: number): void;
    /**
     * @param Num           adds all attributes by Num
     * @param setStatsToNum sets all attributes by Num
     */
    modifyAllAttributes(Num: number, setStatsToNum: boolean): void;
    /**
     * If not setStats, then it will ADD submitted[0] to stats[0]. Submitted must be length of 6
     *
     * @param submitted Adds all attributes by array of attributes respectively i.e atr 0 gets added to index 0 of a
     * @param setStats  sets all attributes to submitted array
     */
    modifyAllAttributes(submitted: number[], setStats: boolean): void;
    /**
     * 0 for strength, 1 dex, 2 constitution, 3 willpower, 4 mind, 5 spirit
     *
     * @param statid statID to multiply
     * @param multi  value to multi by
     */
    multiplyAttribute(statid: number, multi: number): void;
    /**
     * @param multi multiplies all attributes by multi
     */
    multiplyAllAttributes(multi: number): void;
    /**
     * A "Full" stat is a stat that has all form multipliers calculated. i.e if base Strength is 10,000, returns 10,000.
     * if SSJ form multi is 20x and is SSJ, returns 200,000. LSSJ returns 350,000, LSSJ Kaioken x40 returns 1,000,000 and so on
     * 0 for strength, 1 dex, 2 constitution, 3 willpower, 4 mind, 5 spirit
     *
     * @param statid ID of attribute
     * @return attribute value
     */
    getFullAttribute(statid: number): number;
    /**
     * @return Player's race name
     */
    getRaceName(): string;
    /**
     * @return Name of form player is currently in
     */
    getCurrentDBCFormName(): string;
    /**
     * @param formName name of form to change mastery of
     * @param amount   sets the current mastery value to amount
     * @param add      adds the amount to current mastery, instead of setting it to it
     */
    changeDBCMastery(formName: string, amount: number, add: boolean): void;
    /**
     * @param formName name of form
     * @return Form mastery value
     */
    getDBCMasteryValue(formName: string): number;
    /**
     * @return Entire form mastery NBT string, aka data32
     */
    getAllDBCMasteries(): string;
    /**
     * @return True if player is fused and spectator
     */
    isDBCFusionSpectator(): boolean;
    /**
     * @return True if player is charging a ki attack
     */
    isChargingKi(): boolean;
    /**
     * @param skillname Acceptable skill names:
     *                  <code>"Fusion", "Jump", "Dash", "Fly", "Endurance", <br>
     *                  "PotentialUnlock", "KiSense", "Meditation", "Kaioken", "GodForm", <br>
     *                  "OldKaiUnlock", "KiProtection", "KiFist", "KiBoost", "DefensePenetration", <br>
     *                  "KiInfuse", "UltraInstinct", "InstantTransmission", "GodOfDestruction"</code>
     * @return skill level from 1 to 10. Or 0 if the player doesn't have that skill
     */
    getSkillLevel(skillname: string): number;
    /**
     * @param attribute 0 for Melee Dmg, 1 for Defense, 3 for Ki Power
     * @return Player's stat, NOT attributes i.e Melee Dmg, not STR
     */
    getMaxStat(attribute: number): number;
    /**
     * @param attribute check getMaxStat
     * @return Player's stat as a percentage of MaxStat through power release i.e if MaxStat is 1000 and release is 10, returns 100
     */
    getCurrentStat(attribute: number): number;
    /**
     * @return if Form player is in is 10x base, returns 10
     */
    getCurrentFormMultiplier(): number;
    getMajinAbsorptionRace(): number;
    setMajinAbsorptionRace(race: number): void;
    getMajinAbsorptionPower(): number;
    setMajinAbsorptionPower(power: number): void;
    /**
     * @return True if player is transformed into white MUI
     */
    isMUI(): boolean;
    /**
     * @return True if player is currently KO
     */
    isKO(): boolean;
    /**
     * @return True if either MUI or UI Omen
     */
    isUI(): boolean;
    /**
     * @return True if Mystic
     */
    isMystic(): boolean;
    /**
     * @return True if in Kaioken
     */
    isKaioken(): boolean;
    /**
     * @return True if in God of Destruction
     */
    isGOD(): boolean;
    /**
     * @return True if in Legendary
     */
    isLegendary(): boolean;
    /**
     * @return True if Divine
     */
    isDivine(): boolean;
    /**
     * @return True if player is using Majin
     */
    isMajin(): boolean;
    /**
     * If the player has a given form, it forces them to transform to that form.
     *
     * @param formID Form ID of the custom form you want to force the player into
     */
    setCustomForm(formID: number): void;
    /**
     * If the player has a given form, it forces them to transform to that form.
     *
     * @param form Form object of the custom form you want to force the player into
     */
    setCustomForm(form: import('./form/IForm').IForm): void;
    /**
     * If the player has a given form or <code>ignoreUnlockCheck</code> is true, it forces them to transform to that form.
     *
     * @param formID            Form ID of the custom form you want to force the player into
     * @param ignoreUnlockCheck Should this ignore checking if a player has a form unlocked
     */
    setCustomForm(formID: number, ignoreUnlockCheck: boolean): void;
    /**
     * If the player has a given form or <code>ignoreUnlockCheck</code> is true, it forces them to transform to that form.
     *
     * @param form              Form object of the custom form you want to force the player into
     * @param ignoreUnlockCheck Should this ignore checking if a player has a form unlocked
     */
    setCustomForm(form: import('./form/IForm').IForm, ignoreUnlockCheck: boolean): void;
    setFlight(flightOn: boolean): void;
    isFlying(): boolean;
    setAllowFlight(allowFlight: boolean): void;
    setFlightSpeedRelease(release: number): void;
    setBaseFlightSpeed(speed: number): void;
    setDynamicFlightSpeed(speed: number): void;
    setFlightGravity(isEffected: boolean): void;
    setFlightDefaults(): void;
    setSprintSpeed(speed: number): void;
    hasCustomForm(formName: string): boolean;
    hasCustomForm(formID: number): boolean;
    getCustomForms(): import('./form/IForm').IForm[];
    /**
     * Gives a player a custom form by the given name.
     *
     * @param formName Name of the form to give the player.
     */
    giveCustomForm(formName: string): void;
    /**
     * Gives a player a custom form.
     *
     * @param form Form object to give the player.
     */
    giveCustomForm(form: import('./form/IForm').IForm): void;
    /**
     * Removes the given form by name.
     * <br><br>
     * Removing mastery is dependent on configs
     *
     * @param formName Name of the form to remove.
     */
    removeCustomForm(formName: string): void;
    /**
     * Removes the given form.
     * <br><br>
     * Removing mastery is dependent on configs
     *
     * @param form Form object to remove.
     */
    removeCustomForm(form: import('./form/IForm').IForm): void;
    /**
     * Removes the given form by name.
     *
     * @param formName       Name of the form to remove.
     * @param removesMastery does removing this form remove mastery
     */
    removeCustomForm(formName: string, removesMastery: boolean): void;
    /**
     * Removes the given form.
     *
     * @param form           Form object to remove
     * @param removesMastery does removing this form remove mastery
     */
    removeCustomForm(form: import('./form/IForm').IForm, removesMastery: boolean): void;
    getSelectedForm(): import('./form/IForm').IForm;
    /**
     * Selects a custom form for the player.
     * <br>
     * This does not force a transformation. <br>
     * It only selects the form they will transform into whenever they choose to transform.
     *
     * @param form Form object.
     */
    setSelectedForm(form: import('./form/IForm').IForm): void;
    /**
     * Selects a custom form for the player.
     * <br>
     * This does not force a transformation. <br>
     * It only selects the form they will transform into whenever they choose to transform.
     *
     * @param formID Form ID.
     */
    setSelectedForm(formID: number): void;
    /**
     * Clears form selection.
     */
    removeSelectedForm(): void;
    /**
     * Sets the selected custom aura <b>if the player has it unlocked.</b>
     *
     * @param auraName Name of the aura to set.
     */
    setAuraSelection(auraName: string): void;
    /**
     * Sets the selected custom aura <b>if the player has it unlocked.</b>
     *
     * @param aura Aura object to set.
     */
    setAuraSelection(aura: import('./aura/IAura').IAura): void;
    /**
     * Sets the selected custom aura <b>if the player has it unlocked.</b>
     *
     * @param auraID ID of the aura to set.
     */
    setAuraSelection(auraID: number): void;
    /**
     * Unlocks the given aura for the player.
     *
     * @param auraName Name of the aura to unlock.
     */
    giveAura(auraName: string): void;
    hasAura(auraName: string): boolean;
    hasAura(auraId: number): boolean;
    /**
     * Unlocks the given aura for the player.
     *
     * @param aura Aura object to unlock for the player.
     */
    giveAura(aura: import('./aura/IAura').IAura): void;
    /**
     * Unlocks the given aura for the player.
     *
     * @param auraID ID of the aura to unlock for the player.
     */
    giveAura(auraID: number): void;
    /**
     * Removes an aura from the player.
     *
     * @param auraName Name of the aura to remove.
     */
    removeAura(auraName: string): void;
    /**
     * Remove an aura from the player.
     *
     * @param aura Aura object to remove.
     */
    removeAura(aura: import('./aura/IAura').IAura): void;
    /**
     * Remove an aura from the player.
     *
     * @param auraID ID of the aura to remove.
     */
    removeAura(auraID: number): void;
    /**
     * Sets the custom aura for the player <br>
     * <h2>THE PLAYER DOESN'T NEED TO HAVE THIS AURA UNLOCKED</h2>
     *
     * @param auraName ID of the aura.
     */
    setAura(auraName: string): void;
    /**
     * Sets the custom aura for the player <br>
     * <h2>THE PLAYER DOESN'T NEED TO HAVE THIS AURA UNLOCKED</h2>
     *
     * @param aura Aura object
     */
    setAura(aura: import('./aura/IAura').IAura): void;
    /**
     * Sets the custom aura for the player <br>
     * <h2>THE PLAYER DOESN'T NEED TO HAVE THIS AURA UNLOCKED</h2>
     *
     * @param auraID ID of the aura.
     */
    setAura(auraID: number): void;
    /**
     * Removes a custom aura selection and current aura running for the player.
     */
    removeCurrentAura(): void;
    /**
     * Removes a custom aura selection for the player.
     */
    removeAuraSelection(): void;
    getSelectedDBCForm(): number;
    setSelectedDBCForm(formID: number): void;
    removeSelectedDBCForm(): void;
    /**
     * @return True if player is in any CNPC+ custom form
     */
    isInCustomForm(): boolean;
    /**
     * @param form Form object.
     * @return True if the player is in a given form.
     */
    isInCustomForm(form: import('./form/IForm').IForm): boolean;
    /**
     * @param formID formID
     * @return True if player is in formID
     */
    isInCustomForm(formID: number): boolean;
    setCustomForm(formName: string, ignoreUnlockCheck: boolean): void;
    setCustomForm(formName: string): void;
    /**
     * Sets the mastery of a form if the player has it unlocked, otherwise it won't change anything.
     *
     * @param formID ID of the form to change the mastery of.
     * @param value  New mastery value.
     */
    setCustomMastery(formID: number, value: number): void;
    /**
     * Sets the mastery of a form if the player has it unlocked, otherwise it won't change anything.
     *
     * @param form  The form you want to change the mastery of.
     * @param value New mastery value.
     */
    setCustomMastery(form: import('./form/IForm').IForm, value: number): void;
    /**
     * Sets the mastery of a form.
     *
     * @param formID            ID of the form to change the mastery of.
     * @param value             New mastery value.
     * @param ignoreUnlockCheck If set to true, it will adjust the form mastery even if the player doesn't have the form unlocked.
     */
    setCustomMastery(formID: number, value: number, ignoreUnlockCheck: boolean): void;
    /**
     * Sets the mastery of a form.
     *
     * @param form              The form you want to change the mastery of.
     * @param value             New mastery value.
     * @param ignoreUnlockCheck If set to true, it will adjust the form mastery even if the player doesn't have the form unlocked.
     */
    setCustomMastery(form: import('./form/IForm').IForm, value: number, ignoreUnlockCheck: boolean): void;
    /**
     * Modifies the mastery of a form ONLY if the player has the form unlocked.
     *
     * @param formID ID of the form to change the mastery of.
     * @param value  Amount of mastery to add.
     */
    addCustomMastery(formID: number, value: number): void;
    /**
     * Modifies the mastery of a form ONLY if the player has the form unlocked.
     *
     * @param form  The form you want to change the mastery of.
     * @param value Amount of mastery to add.
     */
    addCustomMastery(form: import('./form/IForm').IForm, value: number): void;
    /**
     * Modifies the mastery of a form.
     *
     * @param formID            ID of the form to change the mastery of.
     * @param value             Amount of mastery to add.
     * @param ignoreUnlockCheck If set to true, it will adjust the form mastery even if the player doesn't have the form unlocked.
     */
    addCustomMastery(formID: number, value: number, ignoreUnlockCheck: boolean): void;
    /**
     * Modifies the mastery of a form.
     *
     * @param form              The form you want to change the mastery of.
     * @param value             Amount of mastery to add.
     * @param ignoreUnlockCheck If set to true, it will adjust the form mastery even if the player doesn't have the form unlocked.
     */
    addCustomMastery(form: import('./form/IForm').IForm, value: number, ignoreUnlockCheck: boolean): void;
    /**
     * Check the form mastery. If the player is fused, it sums masteries of both players.
     *
     * @param formID Form ID to check.
     * @return Mastery of the given form.
     */
    getCustomMastery(formID: number): number;
    /**
     * Check the form mastery. If the player is fused, it sums masteries of both players.
     *
     * @param form Form to check.
     * @return Mastery of the given form.
     */
    getCustomMastery(form: import('./form/IForm').IForm): number;
    /**
     * Check the form mastery.
     *
     * @param formID      Form ID to check.
     * @param checkFusion Should it perform a fusion check? If the player is fused, it sums masteries of both players.
     * @return Mastery of the given form.
     */
    getCustomMastery(formID: number, checkFusion: boolean): number;
    /**
     * Check the form mastery.
     *
     * @param form        Form to check.
     * @param checkFusion Should it perform a fusion check? If the player is fused, it sums masteries of both players.
     * @return Mastery of the given form.
     */
    getCustomMastery(form: import('./form/IForm').IForm, checkFusion: boolean): number;
    removeCustomMastery(formID: number): void;
    removeCustomMastery(form: import('./form/IForm').IForm): void;
    getCurrentForm(): import('./form/IForm').IForm;
    isInAura(): boolean;
    isInAura(aura: import('./aura/IAura').IAura): boolean;
    isInAura(auraName: string): boolean;
    isInAura(auraID: number): boolean;
    removeOutline(): void;
    setOutline(outline: import('./outline/IOutline').IOutline): void;
    setOutline(outlineName: string): void;
    setOutline(outlineID: number): void;
    getOutline(): import('./outline/IOutline').IOutline;
    /**
     * @return IPlayer object of the player you are fused with
     */
    IPlayer<?> getFusionPartner();
    
    /**
     * Fires a Ki Attack in the Head Direction of the NPC
     *
     * @param type          Type of Ki Attack [0 - 8] "Wave", "Blast", "Disk", "Laser", "Spiral", "BigBlast", "Barrage", "Shield", "Explosion"
     * @param speed         Speed of Ki Attack [0 - 100]
     * @param damage        Damage for Ki Attack
     * @param hasEffect     True for Explosion
     * @param color         Color of Ki Attack [0 - 30] -&gt; <br>
     *                      0: "AlignmentBased", "white", "blue", "purple", "red", "black", "green", "yellow", "orange", "pink", "magenta", <br>
     *                      11: "lightPink", "cyan", "darkCyan", "lightCyan", "darkGray", "gray", "darkBlue", "lightBlue", "darkPurple", "lightPurple", <br>
     *                      21: "darkRed", "lightRed", "darkGreen", "lime", "darkYellow", "lightYellow", "gold", "lightOrange", "darkBrown", "lightBrown"
     * @param density       Density of Ki Attack &gt; 0
     * @param hasSound      Play Impact Sound of Ki Attack
     * @param chargePercent Charge Percentage of Ki Attack [0 - 100]
     */
    fireKiAttack(type: number, speed: number, damage: number, hasEffect: boolean, color: number, density: number, hasSound: boolean, chargePercent: number): void;
    /**
     * Fires an IKiAttack with its internal params
     *
     * @param kiAttack ki attack to shoot
     */
    fireKiAttack(kiAttack: import('./IKiAttack').IKiAttack): void;
    /**
     * @return True if player is releasing ki
     */
    isReleasing(): boolean;
    /**
     * @return True if player is releasing ki and has meditation skill
     */
    isMeditating(): boolean;
    /**
     * @return True if player is using majin super regen
     */
    isSuperRegen(): boolean;
    /**
     * @return True if player is dodging/swooping
     */
    isSwooping(): boolean;
    isInMedicalLiquid(): boolean;
    /**
     *
     * @param slot, integer of the ki attack slot to look in. Accepts values 1-4
     * @return Ki Attack data
     */
    getAttackFromSlot(slot: number): import('./IKiAttack').IKiAttack;
    /**
     * Sets dbcPlayer to a Koed state
     * @param KoTime integer for player Ko time, 1=5 sec
     */
    setKo(KoTime: number): void;
}

