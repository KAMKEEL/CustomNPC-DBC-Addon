package kamkeel.npcdbc.data;

import kamkeel.npcdbc.api.ICustomAura;

public class CustomAura implements ICustomAura {
    public String type, texture1, texture2, texture3;
    public int color1, color2, color3, alpha = 255, lightningColor, lightningAlpha = 255, speed;
    public boolean hasLightning, hasSpeed, hasAlpha, hasColor1, hasColor2, hasColor3, hasTexture1, hasTexture2, hasTexture3;

    @Override
    public String getType() {
        return null;
    }

    @Override
    public void setType(String auraType) {

    }

    @Override
    public void setColor(String colorType, int color) {

    }

    @Override
    public int getColor(String colorType) {
        return 0;
    }

    @Override
    public void setHasColor(String colorType, boolean hasType) {

    }

    @Override
    public boolean getHasColor(String colortype) {
        return false;
    }

    @Override
    public void setHasLightning(boolean hasLightning) {

    }

    @Override
    public boolean hasLightning() {
        return false;
    }

    @Override
    public int getLightningColor() {
        return 0;
    }

    @Override
    public void setLightningColor(int color) {

    }

    @Override
    public int getLightningAlpha() {
        return 0;
    }

    @Override
    public void setLightningAlpha(int alpha) {

    }

    ;

    @Override
    public int getSpeed() {
        return 0;
    }

    @Override
    public void setSpeed(int speed) {

    }

    @Override
    public String getTexture(String textureType) {
        return null;
    }

    @Override
    public void setTexture(String textureType, String textureLocation) {

    }

    @Override
    public float getAlpha() {
        return 0;
    }

    @Override
    public void setAlpha(float alpha) {

    }
}
