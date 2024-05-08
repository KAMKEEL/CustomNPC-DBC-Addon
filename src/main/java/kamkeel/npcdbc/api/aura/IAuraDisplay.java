package kamkeel.npcdbc.api.aura;

public interface IAuraDisplay {


    void toggleKaioken(boolean toggle);

    boolean isKaiokenToggled();

    /**
     * @return legal types: ssgod, godofdestruction, ui, ssb, ssrose, ssroseevo, ssbkk, shinka
     */
    String getType();

    /**
     * @return legal types: ssgod, godofdestruction, ui, ssb, ssrose, ssroseevo, ssbkk, shinka
     */
    void setType(String auraType);

    boolean hasColor(String type);

    /**
     * @param colorType Legal types: color1, color2, color3
     * @param color     hexadecimal color to set type to
     */
    void setColor(String colorType, int color);

    /**
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

    boolean hasSize();


    float getSize();

    /**
     * @param size Value to set aura size to. Min 0.05, Max 10
     */
    void setSize(float size);


    boolean hasSpeed();

    float getSpeed();

    /**
     * @param speed How fast aura should go through its lifecycle in ticks. DBC Default is 40 ticks. The lower, the faster
     */
    void setSpeed(float speed);

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


    IAuraDisplay save();


}
