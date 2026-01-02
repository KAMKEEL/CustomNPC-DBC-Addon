package kamkeel.npcdbc.client.gui.hud.abilityWheel.icon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.ability.AddonAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import noppes.npcs.client.ClientCacheHandler;
import noppes.npcs.client.renderer.ImageData;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class AbilityIcon extends Gui {
    public final Ability ability;
    public final boolean isDBC;

    public int width, height, iconX, iconY;
    public float scale;
    public String iconTexture;


    public AbilityIcon(Ability abilityToCopy) {
        this.ability = abilityToCopy;
        this.isDBC = this.ability instanceof AddonAbility;

        this.scale = ability.getScale();
        this.width = ability.getWidth();
        this.height = ability.getHeight();
        this.iconX = ability.getIconX();
        this.iconY = ability.getIconY();
        this.iconTexture = ability.getIcon();
    }

    public void draw() {
        TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
        ImageData imageData = ClientCacheHandler.getImageData(this.iconTexture);

        if (!imageData.imageLoaded())
            return;

        GL11.glPushMatrix();

        renderEngine.bindTexture(imageData.getLocation());

        float s = Math.max(0.1f, Math.min(3.5f, scale));

        GL11.glScalef(s, s, s);

        Tessellator t = getTessellator(imageData);
        t.draw();

        GL11.glPopMatrix();
    }

    private Tessellator getTessellator(ImageData imageData) {
        float hw = width / 2f;
        float hh = height / 2f;

        float texW = imageData.getTotalWidth();
        float texH = imageData.getTotalHeight();

        float u1 = iconX / texW;
        float v1 = iconY / texH;
        float u2 = (iconX + width) / texW;
        float v2 = (iconY + height) / texH;

        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(-hw,  hh, zLevel, u1, v2);
        t.addVertexWithUV( hw,  hh, zLevel, u2, v2);
        t.addVertexWithUV( hw, -hh, zLevel, u2, v1);
        t.addVertexWithUV(-hw, -hh, zLevel, u1, v1);
        return t;
    }
}
