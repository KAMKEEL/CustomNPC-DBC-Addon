package kamkeel.npcdbc.client.race;

import JinRyuu.JBRA.ModelBipedDBC;
import JinRyuu.JBRA.RenderPlayerJBRA;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.race.Race;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RaceRenderContext {
    public final Entity entity;
    public final double x;
    public final double y;
    public final double z;
    public final float yaw;
    public final float partialTicks;

    public final RenderPlayerJBRA renderer;
    public final ModelBipedDBC model;
    public final DBCData dbcData;
    public final Race race;

    public int bodyCM;
    public int bodyC1;
    public int bodyC2;
    public int bodyC3;

    public int eyeC1;
    public int eyeC2;
    public int skinType;
    public int state;
    public int bodyType;

    public boolean isFirstPersonArm;
    public int armAnimationId = -1;

    public RaceRenderContext(Entity entity, double x, double y, double z,
                             float yaw, float partialTicks,
                             RenderPlayerJBRA renderer, ModelBipedDBC model,
                             DBCData dbcData, Race race) {
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.partialTicks = partialTicks;
        this.renderer = renderer;
        this.model = model;
        this.dbcData = dbcData;
        this.race = race;
    }
    
    public void bindTexture(ResourceLocation loc) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
    }
}
