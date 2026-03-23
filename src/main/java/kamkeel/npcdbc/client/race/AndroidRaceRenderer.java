package kamkeel.npcdbc.client.race;

import JinRyuu.JBRA.ModelBipedDBC;
import JinRyuu.JBRA.RenderPlayerJBRA;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.LogWriter;

import java.io.IOException;

/**
 * Combined model-part owner and renderer for the Android custom race.
 * <p>
 * Model parts are lazily initialised on first render because DBC's
 * {@link ModelBipedDBC} is not available at static-init time.
 * Once built, parts are cached for the lifetime of the renderer.
 */
@SideOnly(Side.CLIENT)
public class AndroidRaceRenderer implements IRaceRenderer {

    // ── Android-specific model parts ──
    private ModelRenderer bioheadsemi;
    private ModelRenderer biowings;
    private ModelRenderer biotail;
    private ModelRenderer biotail2;
    private ModelRenderer biotailmax;
    private ModelRenderer biotailmax2;

    private boolean partsInitialized;
    private ModelBipedDBC cachedModel;
    private Boolean assetsAvailable;
    private boolean loggedMissingAssets;

    private String textureDir() {
        return ConfigDBCClient.EnableHDTextures
            ? CustomNpcPlusDBC.ID + ":textures/hd/android/"
            : CustomNpcPlusDBC.ID + ":textures/sd/android/";
    }

    // ── IRaceRenderer ──

    @Override
    public boolean render(RaceRenderContext ctx) {
        if (!hasRequiredAssets()) {
            if (!loggedMissingAssets) {
                LogWriter.info("Skipping Android custom race renderer: required Android textures are not present in assets/" + CustomNpcPlusDBC.ID + ".");
                loggedMissingAssets = true;
            }
            return false;
        }

        ensurePartsInitialized(ctx.model);

        DBCData data = ctx.dbcData;
        int state = data.State;

        switch (state) {
            case 1:
                renderSemiPerfect(ctx);
                break;
            case 2:
                renderPerfect(ctx);
                break;
            case 3:
                renderPerfectMax(ctx, data);
                break;
            case 4:
                renderUncontrolledMax(ctx, data);
                break;
            default:
                renderBase(ctx);
                break;
        }
        return true;
    }

    // ── Part initialisation ──
    // Parts are attached to the model's body hierarchy so they inherit
    // vanilla pose/animation transforms automatically.

    private void ensurePartsInitialized(ModelBipedDBC model) {
        if (partsInitialized && cachedModel == model) return;

        bioheadsemi  = createPart(model, 0, 0, -4, -8, -4, 8, 8, 8, 0.6F);
        biowings     = createPart(model, 0, 0, -6, 0, 2, 12, 12, 1, 0.0F);
        biotail      = createPart(model, 0, 0, -1, 0, 0, 2, 10, 2, 0.0F);
        biotail2     = createPart(model, 0, 0, -1, 0, 0, 2, 10, 2, 0.0F);
        biotailmax   = createPart(model, 0, 0, -1, 0, 0, 2, 12, 2, 0.0F);
        biotailmax2  = createPart(model, 0, 0, -1, 0, 0, 2, 12, 2, 0.0F);

        cachedModel = model;
        partsInitialized = true;
    }

    private ModelRenderer createPart(ModelBipedDBC model,
                                      int texOffX, int texOffY,
                                      float offX, float offY, float offZ,
                                      int sizeX, int sizeY, int sizeZ,
                                      float inflate) {
        ModelRenderer part = new ModelRenderer(model, texOffX, texOffY);
        part.addBox(offX, offY, offZ, sizeX, sizeY, sizeZ, inflate);
        part.setRotationPoint(0, 0, 0);
        return part;
    }

    private boolean hasRequiredAssets() {
        if (assetsAvailable != null) {
            return assetsAvailable;
        }

        String[] required = {
            "bio3.png",
            "bio1.png",
            "bio2.png",
            "bioeyesbase.png",
            "bioeyeleft.png",
            "bioeyeright.png"
        };

        for (String name : required) {
            ResourceLocation location = new ResourceLocation(textureDir() + name);
            try {
                Minecraft.getMinecraft().getResourceManager().getResource(location);
            } catch (IOException ignored) {
                assetsAvailable = false;
                return false;
            }
        }

        assetsAvailable = true;
        return true;
    }

    private void syncToHead(ModelRenderer part, ModelBipedDBC model) {
        if (model.bipedHead == null) {
            return;
        }
        part.rotationPointX = model.bipedHead.rotationPointX;
        part.rotationPointY = model.bipedHead.rotationPointY;
        part.rotationPointZ = model.bipedHead.rotationPointZ;
        part.rotateAngleX = model.bipedHead.rotateAngleX;
        part.rotateAngleY = model.bipedHead.rotateAngleY;
        part.rotateAngleZ = model.bipedHead.rotateAngleZ;
    }

    private void syncToBody(ModelRenderer part, ModelBipedDBC model) {
        if (model.bipedBody == null) {
            return;
        }
        part.rotationPointX = model.bipedBody.rotationPointX;
        part.rotationPointY = model.bipedBody.rotationPointY;
        part.rotationPointZ = model.bipedBody.rotationPointZ;
        part.rotateAngleX = model.bipedBody.rotateAngleX;
        part.rotateAngleY = model.bipedBody.rotateAngleY;
        part.rotateAngleZ = model.bipedBody.rotateAngleZ;
    }

    // ── Rendering helpers ──

    private void bindAndColor(RaceRenderContext ctx, String texture, int color) {
        ctx.bindTexture(new ResourceLocation(textureDir() + texture));
        RenderPlayerJBRA.glColor3f(color);
    }

    private void renderPart(ModelRenderer part) {
        part.render(0.0625F);
    }

    private void renderHeadPart(ModelBipedDBC model, ModelRenderer part) {
        syncToHead(part, model);
        renderPart(part);
    }

    private void renderBodyPart(ModelBipedDBC model, ModelRenderer part) {
        syncToBody(part, model);
        renderPart(part);
    }

    private int auraAdjustedWhite(RaceRenderContext ctx) {
        return 0xFFFFFF;
    }

    // ── State renderers ──

    private void renderBase(RaceRenderContext ctx) {
        String dir = textureDir();
        ModelBipedDBC model = ctx.model;

        // Body layers
        bindAndColor(ctx, "bio3.png", ctx.bodyCM);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "bio1.png", ctx.bodyC1);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "bio2.png", ctx.bodyC2);
        model.renderBody(0.0625F);

        // Tail passes
        int white = auraAdjustedWhite(ctx);
        bindAndColor(ctx, "biotail2.png", white);
        renderBodyPart(model, biotail2);

        bindAndColor(ctx, "biotail.png", white);
        renderBodyPart(model, biotail);

        // Wings
        bindAndColor(ctx, "biowings.png", white);
        renderBodyPart(model, biowings);

        // Eyes
        renderEyes(ctx, "bioeyesbase.png", "bioeyeleft.png", "bioeyeright.png");
    }

    private void renderSemiPerfect(RaceRenderContext ctx) {
        ModelBipedDBC model = ctx.model;
        int white = auraAdjustedWhite(ctx);

        // Body layers
        bindAndColor(ctx, "bio3S.png", ctx.bodyCM);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "bio1S.png", ctx.bodyC1);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "bio2S.png", ctx.bodyC2);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "bio4S.png", white);
        model.renderBody(0.0625F);

        // Head crest
        bindAndColor(ctx, "bioskinsemi.png", white);
        renderHeadPart(model, bioheadsemi);

        // Tail passes
        bindAndColor(ctx, "biotail2.png", white);
        renderBodyPart(model, biotail2);

        bindAndColor(ctx, "biotailS.png", white);
        renderBodyPart(model, biotail);

        // Eyes
        renderEyes(ctx, "bioeyesbaseS.png", "bioeyeleftS.png", "bioeyerightS.png");
    }

    private void renderPerfect(RaceRenderContext ctx) {
        ModelBipedDBC model = ctx.model;
        int white = auraAdjustedWhite(ctx);

        // Body layers
        bindAndColor(ctx, "bio2P.png", ctx.bodyCM);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "bio1P.png", white);
        model.renderBody(0.0625F);

        // Head crest
        bindAndColor(ctx, "bioskin.png", white);
        renderHeadPart(model, bioheadsemi);

        // Wings
        bindAndColor(ctx, "biowingsP.png", white);
        renderBodyPart(model, biowings);

        // Eyes
        renderEyes(ctx, "bioeyesbaseS.png", "bioeyeleftS.png", "bioeyerightS.png");
    }

    private void renderPerfectMax(RaceRenderContext ctx, DBCData data) {
        ModelBipedDBC model = ctx.model;
        int white = auraAdjustedWhite(ctx);
        int mainColor = ctx.bodyCM;

        // Body layers
        bindAndColor(ctx, "bio2M.png", mainColor);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "bio1M.png", white);
        model.renderBody(0.0625F);

        // Max tail
        bindAndColor(ctx, "biotailmax2.png", white);
        renderBodyPart(model, biotailmax2);

        // Head
        bindAndColor(ctx, "bioheadM.png", white);
        renderHeadPart(model, bioheadsemi);

        // Tail
        bindAndColor(ctx, "biotailmax.png", white);
        renderBodyPart(model, biotailmax);

        // Wings
        bindAndColor(ctx, "biowingsP.png", white);
        renderBodyPart(model, biowings);

        // Eyes
        renderEyes(ctx, "bioeyesbaseS.png", "bioeyeleftS.png", "bioeyerightS.png");
    }

    private void renderUncontrolledMax(RaceRenderContext ctx, DBCData data) {
        ModelBipedDBC model = ctx.model;
        int white = auraAdjustedWhite(ctx);
        int mainColor = ctx.bodyCM;

        // Base body first
        bindAndColor(ctx, "bio3.png", mainColor);
        model.renderBody(0.0625F);

        // Uncontrolled overlays
        bindAndColor(ctx, "bio2UM.png", white);
        model.renderBody(0.0625F);

        bindAndColor(ctx, "bio1UM.png", white);
        model.renderBody(0.0625F);

        // Head
        bindAndColor(ctx, "bioheadUM.png", white);
        renderHeadPart(model, bioheadsemi);

        // Tail
        bindAndColor(ctx, "biotailUM.png", white);
        renderBodyPart(model, biotailmax);

        // Max tail overlay
        bindAndColor(ctx, "biotailmax2.png", white);
        renderBodyPart(model, biotailmax2);

        // Wings
        bindAndColor(ctx, "biowingsP.png", white);
        renderBodyPart(model, biowings);

        // Eyes
        renderEyes(ctx, "bioeyesbaseUM.png", "bioeyeleftUM.png", "bioeyerightUM.png");
    }

    // ── Shared eye render helper ──

    private void renderEyes(RaceRenderContext ctx,
                            String baseTexture, String leftTexture, String rightTexture) {
        ModelBipedDBC model = ctx.model;

        bindAndColor(ctx, baseTexture, 0xFFFFFF);
        model.renderHairs(0.0625F, "EYEBASE");

        bindAndColor(ctx, leftTexture, 0xFFFFFF);
        model.renderHairs(0.0625F, "EYELEFT");

        bindAndColor(ctx, rightTexture, 0xFFFFFF);
        model.renderHairs(0.0625F, "EYERIGHT");
    }
}
