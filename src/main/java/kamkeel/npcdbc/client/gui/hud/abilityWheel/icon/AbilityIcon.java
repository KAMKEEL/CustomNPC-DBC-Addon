package kamkeel.npcdbc.client.gui.hud.abilityWheel.icon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.data.ability.AbilityIconData;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.ChainedAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.ClientCacheHandler;
import noppes.npcs.client.renderer.ImageData;
import org.lwjgl.opengl.GL11;

/**
 * Renders ability icons in the Ability Wheel and Hotbar.
 * Uses AbilityIconData stored in the ability's customData.
 */
@SideOnly(Side.CLIENT)
public class AbilityIcon extends Gui {
    private static final ResourceLocation FALLBACK_TEXTURE = new ResourceLocation("customnpcs", "textures/marks/question.png");

    private final AbilityIconData iconData;

    public int width;
    public int height;

    public AbilityIcon(Ability ability) {
        this.iconData = AbilityIconData.fromAbility(ability);
        this.width = iconData.getWidth();
        this.height = iconData.getHeight();
    }

    public AbilityIcon(ChainedAbility chain) {
        this.iconData = AbilityIconData.fromChainedAbility(chain);
        this.width = iconData.getWidth();
        this.height = iconData.getHeight();
    }

    /**
     * Draw the icon centered at the current position.
     */
    public void draw() {
        draw(0);
    }

    /**
     * Draw the icon for a specific toggle state.
     * Uses per-state UV overrides if available, otherwise falls back to default.
     * @param state 0 = default/off, 1+ = active toggle state
     */
    public void draw(int state) {
        TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;

        ImageData imageData = null;
        if (iconData.hasTexture()) {
            imageData = ClientCacheHandler.getImageData(iconData.getTexture());
        }

        GL11.glPushMatrix();
        GL11.glColor4f(1, 1, 1, 1);

        Tessellator t;

        if (imageData == null || !imageData.imageLoaded()) {
            // Fallback: render question mark
            GL11.glScalef(2, 2, 1);
            GL11.glTranslatef(0, -3, 1);

            renderEngine.bindTexture(FALLBACK_TEXTURE);
            t = getFallbackTessellator();
        } else {
            // Render custom icon with state-aware UV
            GL11.glScalef(iconData.getScale(), iconData.getScale(), 1);
            renderEngine.bindTexture(imageData.getLocation());
            t = getTessellator(imageData, state);
        }

        t.draw();
        GL11.glPopMatrix();
    }

    /**
     * Draw the icon at a specific position (top-left corner).
     */
    public void drawAt(int x, int y) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x + width / 2f, y + height / 2f, 0);
        draw();
        GL11.glPopMatrix();
    }

    private Tessellator getTessellator(ImageData imageData, int state) {
        float hw = width / 2f;
        float hh = height / 2f;

        float texW = imageData.getTotalWidth();
        float texH = imageData.getTotalHeight();

        int ix = iconData.getIconXForState(state);
        int iy = iconData.getIconYForState(state);

        float u1 = ix / texW;
        float v1 = iy / texH;
        float u2 = (ix + width) / texW;
        float v2 = (iy + height) / texH;

        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(-hw, hh, zLevel, u1, v2);
        t.addVertexWithUV(hw, hh, zLevel, u2, v2);
        t.addVertexWithUV(hw, -hh, zLevel, u2, v1);
        t.addVertexWithUV(-hw, -hh, zLevel, u1, v1);
        return t;
    }

    private Tessellator getFallbackTessellator() {
        float hw = width / 2f;
        float hh = height / 2f;

        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(-hw, hh, zLevel, 0, 1);
        t.addVertexWithUV(hw, hh, zLevel, 1, 1);
        t.addVertexWithUV(hw, -hh, zLevel, 1, 0);
        t.addVertexWithUV(-hw, -hh, zLevel, 0, 0);
        return t;
    }

    /**
     * Check if this icon has a valid texture.
     */
    public boolean hasTexture() {
        return iconData.hasTexture();
    }
}
