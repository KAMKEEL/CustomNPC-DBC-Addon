package kamkeel.npcdbc.client.gui.hud.abilityHotbar;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.data.ability.Ability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class AbilityHotbarSlot {
    private static final int SELECT_TIME = 200;
    public static final ResourceLocation resource = new ResourceLocation(CustomNpcPlusDBC.ID + ":textures/gui/ability_hotbar.png");

    public Ability ability;
    public HUDAbilityHotbar parent;
    public int index;

    public boolean isSelected = false;
    public long startSelectTime = 0;
    public long stopSelectTime = 0;
    public float selectScale = 0;

    private static final int SLOT_SIZE = 64;
    private static final int SLOT_GAP  = 24;
    private static final int SLOT_X_OFFSET = 10;

    public AbilityHotbarSlot(HUDAbilityHotbar parent, Ability abilityToCopy, int index) {
        this.parent = parent;
        this.ability = abilityToCopy;
        this.index = index;
    }

    public double easeInSine(float x) {
        return x < 0.5
            ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2
            : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2;
    }

    public void draw(Minecraft mc) {
        ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

        int screenW = sr.getScaledWidth();
        int screenH = sr.getScaledHeight();

        int totalHeight = 6 * SLOT_SIZE + (6 - 1) * SLOT_GAP;

        int baseY = (screenH - totalHeight) / 2;

        int x = SLOT_X_OFFSET;
        int y = baseY + index * (SLOT_SIZE + SLOT_GAP);

        float scale = 1.0f + 0.2f * getSegmentScale();

        GL11.glPushMatrix();

        GL11.glTranslatef(x + SLOT_SIZE / 2f, y + SLOT_SIZE / 2f, 0);
        GL11.glScalef(scale, scale, 1);
        GL11.glTranslatef(-SLOT_SIZE / 2f, -SLOT_SIZE / 2f, 0);

        mc.getTextureManager().bindTexture(resource);

        Gui.func_146110_a(
            0, 0,
            0, 0,
            SLOT_SIZE, SLOT_SIZE,
            256, 256
        );

        GL11.glPopMatrix();
    }

    public void setSelectedState(boolean newSelectState) {

        if (!isSelected && newSelectState) {
            startSelectTime = (long) (Minecraft.getSystemTime() - (Math.max(selectScale - 1, 0)) * SELECT_TIME);
        }
        if (isSelected && !newSelectState) {
            stopSelectTime = (long) (Minecraft.getSystemTime() - (1 - selectScale) * SELECT_TIME);
        }
        isSelected = newSelectState;
    }

    public float getSegmentScale() {
        float updateTime;
        if (isSelected) {
            updateTime = (float) (Minecraft.getSystemTime() - startSelectTime) / SELECT_TIME;
            updateTime = Math.min(updateTime, 1);
            selectScale = (float) easeInSine(updateTime);
        } else {
            updateTime = (float) (Minecraft.getSystemTime() - stopSelectTime) / SELECT_TIME;
            updateTime = Math.min(updateTime, 1);
            selectScale = (float) easeInSine(1 - updateTime);
        }

        selectScale = Math.min(1, Math.max(selectScale, 0));

        return selectScale;
    }
}
