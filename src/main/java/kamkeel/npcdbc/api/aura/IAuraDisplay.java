package kamkeel.npcdbc.api.aura;

public interface IAuraDisplay {


    /**
     * @return legal types: SSJ, SSGod, SSB ,SSBEvo , SSRose, SSRoseEvo, UI, GodOfDestruction
     */
    String getType();

    /**
     * @return legal types: SSJ, SSGod, SSB ,SSBEvo , SSRose, SSRoseEvo, UI, GodOfDestruction
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

    int getAlpha();

    void setAlpha(String type, int alpha);

    /**
     * @param hasLightning True if aura is to have SSJ2 lightning effects
     */
    void hasLightning(boolean hasLightning);

    boolean getHasLightning();

    boolean hasSize();


    float getSize();

    void setSize(float size);


    boolean hasSpeed();

    float getSpeed();


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
