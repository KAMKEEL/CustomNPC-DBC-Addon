package kamkeel.npcdbc.api.aura;

import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.outline.IOutline;

public interface IAuraDisplay {

    boolean getKettleModeAura();

    /**
     * @param set True if kettle mode is always on regardless of charging/kettleModeCharging (turn kettleModeType on)
     */
    void setKettleModeAura(boolean set);

    boolean getKettleModeCharging();

    /**
     * @param set True if kettle mode is only when charging ki/transforming; (turn kettleModeType on)
     */
    void setKettleModeCharging(boolean set);

    byte getKettleModeType();

    /**
     * @param type Legal: 0 for off, 1 for only white particles without aura, 2 for both aura and particles
     */
    void setKettleModeType(byte type);

    boolean getOverrideDBCAura();

    Aura setOverrideDBCAura(boolean override);

    void toggleKaioken(boolean toggle);


    float getKaiokenSize();

    void setKaiokenSize(float size);

    String getKaiokenSound();

    /**
     * @param soundDirectory "customnpcs:auras.kaioken_aura"
     *                       Set to "nosound" to disable kaioken aura sound,
     *                       Default is "", which plays "jinryuudragonbc:1610.aurabk".
     */
    void setKaiokenSound(String soundDirectory);

    boolean hasSound();

    String getAuraSound();

    /**
     * @param soundDirectory i.e "customnpcs:auras.majin_aura"
     */
    void setAuraSound(String soundDirectory);


    boolean isKaiokenToggled();

    /**
     * @return legal types: ssgod, godofdestruction, ui, ssb, ssrose, ssroseevo, ssbkk, shinka
     */
    String getType();

    /**
     * @return legal types: ssgod, godofdestruction, ui, ssb, ssrose, ssroseevo, ssbkk, shinka
     */
    void setType(String auraType);

    String getType2D();

    /**
     * @return legal types: ssgod, godofdestruction, ui, mui, ssb, ssrose, ssroseevo, ssbkk, shinka
     */
    void setType2D(String type2D);

    boolean hasColor(String type);

    /**
     * -
     * Individual IAura Color Precedence:
     * 1. IForm's IAura object color followed by IForm's auraColor (Highest)
     * 2. Individual IAura Color
     * 3. IF overrideDBCForm & in DBC Form -> IAura uses DBC Form Colors [SSJ (yellow), Legendary (green), Divine (purple)]
     * 4. Individual IAura's Type Colors [GoD, UI, SSGod]
     * 5. Base Aura Color (Lowest)
     *
     * @param colorType Legal types: color1, color2, color3
     * @param color     hexadecimal color to set type to
     */
    void setColor(String colorType, int color);

    /**
     * -
     * Individual IAura Color Precedence:
     * 1. Form's Aura
     * 2. Individual IAura Color
     * 3. IF overrideDBCForm & in DBC Form > Use DBC Form Colors [SSJ, Shinka, Rose]
     * 4. Individual IAura's Type Colors [GoD, UI, SSGod]
     * 5. Base Aura Color
     *
     * @param colorType Legal types: color1, color2, color3
     * @return Decimal color of type
     */
    int getColor(String colorType);


    boolean hasAlpha(String type);

    int getAlpha(String type);

    /**
     * @param type  Legal types: aura, lightning
     * @param alpha value of alpha/transparency to set type to
     */
    void setAlpha(String type, int alpha);

    /**
     * @param hasLightning True if aura is to have SSJ2 lightning effects
     */
    void hasLightning(boolean hasLightning);

    boolean getHasLightning();

    int getLightningSpeed();

    void setLightningSpeed(int lightningSpeed);

    int getLightningIntensity();

    void setLightningIntensity(int lightningIntensity);

    boolean hasSize();


    float getSize();

    /**
     * @param size Value to set aura size to. Min 0.05, Max 10
     */
    void setSize(float size);


    boolean hasSpeed();

    int getSpeed();

    /**
     * @param speed How fast aura should go through its lifecycle in ticks. DBC Default is 40 ticks. The lower, the faster
     */
    void setSpeed(int speed);



    /**
     * @param textureType Legal types: texture1, texture2, texture3
     * @return resource location of texture
     */
    String getTexture(String textureType);

    /**
     * @param textureType     Legal types: texture1, texture2, texture3
     * @param textureLocation resource location of texture
     */
    void setTexture(String textureType, String textureLocation);


    void setOutline(int id);

    void setOutline(IOutline outline);

    IAuraDisplay save();


}
