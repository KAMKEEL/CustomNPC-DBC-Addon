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

@SideOnly(Side.CLIENT)
public class AbilityIcon extends Gui {
    private static final ResourceLocation FALLBACK_TEXTURE = new ResourceLocation("npcdbc", "textures/gui/ability_icons.png");
    private static final int FALLBACK_TEXTURE_X = 472;
    private static final int FALLBACK_TEXTURE_Y = 53;
    private static final int FALLBACK_TEXTURE_WIDTH = 32;
    private static final int FALLBACK_TEXTURE_HEIGHT = 32;
    private static final float FALLBACK_TEXTURE_SCALE = 2.0f;

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

    public void draw() {
        draw(0);
    }

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
            GL11.glScalef(FALLBACK_TEXTURE_SCALE, FALLBACK_TEXTURE_SCALE, 1);
            renderEngine.bindTexture(FALLBACK_TEXTURE);
            t = getFallbackTessellator();
        } else {
            GL11.glScalef(iconData.getScale(), iconData.getScale(), 1);
            renderEngine.bindTexture(imageData.getLocation());
            t = getTessellator(imageData, state);
        }

        t.draw();
        GL11.glPopMatrix();
    }

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
        float hw = FALLBACK_TEXTURE_WIDTH / 2f;
        float hh = FALLBACK_TEXTURE_HEIGHT / 2f;

        float texW = 512f;
        float texH = 512f;

        float u1 = FALLBACK_TEXTURE_X / texW;
        float v1 = FALLBACK_TEXTURE_Y / texH;
        float u2 = (FALLBACK_TEXTURE_X + FALLBACK_TEXTURE_WIDTH) / texW;
        float v2 = (FALLBACK_TEXTURE_Y + FALLBACK_TEXTURE_HEIGHT) / texH;

        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(-hw, hh, zLevel, u1, v2);
        t.addVertexWithUV(hw, hh, zLevel, u2, v2);
        t.addVertexWithUV(hw, -hh, zLevel, u2, v1);
        t.addVertexWithUV(-hw, -hh, zLevel, u1, v1);
        return t;
    }

    public boolean hasTexture() {
        return iconData.hasTexture();
    }
}
