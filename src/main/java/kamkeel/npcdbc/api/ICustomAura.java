package kamkeel.npcdbc.api;

public interface ICustomAura {
    /**
     * @return legal types: SSJ, SSGod, SSB ,SSBEvo , SSRose, SSRoseEvo, UI, GodOfDestruction, God
     */
    String getType();

    /**
     * @return legal types: SSJ, SSGod, SSB ,SSBEvo , SSRose, SSRoseEvo, UI, GodOfDestruction, God
     */
    void setType(String auraType);

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

    /**
     * @param colorType colorType Legal types: color1, color2, color3
     * @param hasType   whether aura should have colorType or not
     */
    void setHasColor(String colorType, boolean hasType);

    boolean getHasColor(String colortype);

    void setHasLightning(boolean hasLightning);

    boolean hasLightning();

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

    float getAlpha();

    void setAlpha(float alpha);

}
