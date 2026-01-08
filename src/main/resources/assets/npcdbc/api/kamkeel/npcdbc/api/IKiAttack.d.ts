/**
 * Generated from Java file for CustomNPC+ Minecraft Mod 1.7.10
 * Package: kamkeel.npcdbc.api
 */

export interface IKiAttack {
    /**
     * @return Type of Ki Attack [0 - 8] "Wave", "Blast", "Disk", "Laser", "Spiral", "BigBlast", "Barrage", "Shield", "Explosion"
     **/
    getType(): number;
    /**
     * @param type Type of Ki Attack [0 - 8] "Wave", "Blast", "Disk", "Laser", "Spiral", "BigBlast", "Barrage", "Shield", "Explosion"
     **/
    setType(type: number): void;
    /**
     * @return Speed of Ki Attack
     **/
    getSpeed(): number;
    /**
     * @param speed Speed of Ki Attack [0 - 100]
     **/
    setSpeed(speed: number): void;
    getDamage(): number;
    setDamage(damage: number): void;
    hasEffect(): boolean;
    setHasEffect(hasEffect: boolean): void;
    /**
     * @return Color of Ki Attack [0 - 30] <br>
     * 0: "AlignmentBased", "white", "blue", "purple", "red", "black", "green", "yellow", "orange", "pink", "magenta", <br>
     * 11: "lightPink", "cyan", "darkCyan", "lightCyan", "darkGray", "gray", "darkBlue", "lightBlue", "darkPurple", "lightPurple", <br>
     * 21: "darkRed", "lightRed", "darkGreen", "lime", "darkYellow", "lightYellow", "gold", "lightOrange", "darkBrown", "lightBrown"
     **/
    getColor(): number;
    /**
     * @param color Color of Ki Attack [0 - 30] <br>
     *              0: "AlignmentBased", "white", "blue", "purple", "red", "black", "green", "yellow", "orange", "pink", "magenta", <br>
     *              11: "lightPink", "cyan", "darkCyan", "lightCyan", "darkGray", "gray", "darkBlue", "lightBlue", "darkPurple", "lightPurple", <br>
     *              21: "darkRed", "lightRed", "darkGreen", "lime", "darkYellow", "lightYellow", "gold", "lightOrange", "darkBrown", "lightBrown"
     **/
    setColor(color: number): void;
    getDensity(): number;
    setDensity(density: number): void;
    hasSound(): boolean;
    setHasSound(hasSound: boolean): void;
    getChargePercent(): number;
    setChargePercent(chargePercent: number): void;
    /**
     * If the NPC's current form has Destroyer configs enabled, <br>
     * this ki attack will be granted Destroyer effects even if <br>
     * `isDestroyerAttack` is false
     *
     * @return True if it should respect those configs.
     */
    respectFormDestoryerConfig(): boolean;
    /**
     * If the NPC's current form has Destroyer configs enabled, <br>
     * this ki attack will be granted Destroyer effects even if <br>
     * `isDestroyerAttack` is false
     *
     * @param respectFormConfig True if it should respect those configs.
     */
    setRespectFormDestroyerConfig(respectFormConfig: boolean): void;
    /**
     * @return Checks if the ki attack is forced to be a Destroyer attack
     */
    isDestroyerAttack(): boolean;
    /**
     * Forcefully set the ki attack to be a Destroyer type
     *
     * @param isDestroyer true or false
     */
    setDestroyerAttack(isDestroyer: boolean): void;
}

