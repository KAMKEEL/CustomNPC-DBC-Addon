package kamkeel.npcdbc.mixins.late.impl.npc.client;


import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.ClientConstants;
import kamkeel.npcdbc.client.render.RenderEventHandler;
import kamkeel.npcdbc.api.Color;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.client.model.part.ModelTail;
import noppes.npcs.client.model.part.tails.ModelMonkeyTail;
import noppes.npcs.client.model.util.ModelScaleRenderer;
import noppes.npcs.entity.EntityCustomNpc;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static kamkeel.npcdbc.client.render.RenderEventHandler.disableStencilWriting;
import static org.lwjgl.opengl.GL11.glTranslatef;

@Mixin(value = ModelTail.class, remap = false)
public abstract class MixinModelTail extends ModelScaleRenderer {

    @Shadow
    private ModelMonkeyTail monkey;
    @Shadow
    private EntityCustomNpc entity;
    @Shadow
    private ModelMPM base;

    @Shadow
    private int color;

    @Shadow
    public ModelRenderer tail;

    public MixinModelTail(ModelBase par1ModelBase) {
        super(par1ModelBase);
    }

    public MixinModelTail(ModelBase par1ModelBase, int par2, int par3) {
        super(par1ModelBase, par2, par3);
    }


    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/model/util/ModelScaleRenderer;render(F)V", shift = At.Shift.BEFORE, remap = true), remap = true)
    private void colorCorrectionTail2(float par1, CallbackInfo ci) {
        if (!this.isHidden && !monkey.isHidden) {
            DBCDisplay display = ((INPCDisplay) entity.display).getDBCDisplay();
            if (display == null || !display.enabled)
                return;

            if (!ClientConstants.renderingOutline && display.outlineID != -1)
                RenderEventHandler.enableStencilWriting((entity.getEntityId() + RenderEventHandler.TAIL_STENCIL_ID) % 256);

            if (ClientConstants.renderingOutline) {
                if (!monkey.monkey_wrapped.isHidden)
                    glTranslatef(0, 0.0375f, 0);
                else {
                    if (!monkey.monkey.isHidden)
                        glTranslatef(0, 0.075f, 0);
                    else if (!monkey.monkey_large.isHidden)
                        glTranslatef(0, 0.075f, 0);
                    GL11.glScaled(0.945, 0.945, 0.945);
                }
                disableStencilWriting((entity.getEntityId() + RenderEventHandler.TAIL_STENCIL_ID) % 256, false);
            }

            int tailColor = 0;
            if (DBCRace.isSaiyan(display.race)) {
                if (monkey.monkey_large.isHidden)
                    ClientProxy.bindTexture(new ResourceLocation((ConfigDBCClient.EnableHDTextures ? CustomNpcPlusDBC.ID + ":textures/hd/base/" : "jinryuudragonbc:gui/") + "allw.png"));

                tailColor = display.bodyC1;


                boolean hasFur = display.hasFur;
                boolean isSSJ4 = display.hairType.equals("ssj4"), isOozaru = display.hairType.equals("oozaru");
                int furColor = display.furColor;

                Form form = display.getForm();
                if (form != null) {
                    FormDisplay d = form.display;
                    FormDisplay.BodyColor customClr = display.formColor;
                    if (customClr.hasAnyColor(d, "hair"))
                        tailColor = customClr.getProperColor(d, "hair");
                    if (customClr.hasAnyColor(d, "fur"))
                        furColor = customClr.getProperColor(d, "fur");

                    hasFur = form.display.hasBodyFur;

                    if (form.display.hairType.equals("ssj4"))
                        isSSJ4 = true;
                    else if (form.display.hairType.equals("oozaru"))
                        isOozaru = true;
                }

                if (isSSJ4 && furColor == -1)
                    furColor = 0xDA152C;
                if (isOozaru && furColor == -1)
                    furColor = 6498048;

                if (isSSJ4 || hasFur || isOozaru)
                    tailColor = furColor;


            } else if (display.race == DBCRace.ARCOSIAN) {
                if (!monkey.monkey_large.isHidden && display.arcoState < 4)
                    ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/3B00.png"));

                int arcoState = display.getArco();
                if (arcoState < 4 || arcoState == 6)
                    tailColor = display.bodyC3;
                else
                    tailColor = display.bodyCM;

                Form form = display.getForm();
                if (form != null) {
                    FormDisplay d = form.display;
                    FormDisplay.BodyColor customClr = display.formColor;

                    if ((form.display.bodyType.contains("first") || form.display.bodyType.contains("second") || form.display.bodyType.contains("third"))) {
                        if (customClr.hasAnyColor(d, "bodyc3"))
                            tailColor = customClr.getProperColor(d, "bodyc3");
                    } else if (customClr.hasAnyColor(d, "bodycm"))
                        tailColor = customClr.getProperColor(d, "bodycm");

                }

            }

            new Color(tailColor, base.alpha).glColor();
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/model/util/ModelScaleRenderer;render(F)V", shift = At.Shift.AFTER, remap = true), remap = true)
    private void after(float par1, CallbackInfo ci) {
        DBCDisplay display = ((INPCDisplay) entity.display).getDBCDisplay();

        if (!ClientConstants.renderingOutline && display.outlineID != -1)
            RenderEventHandler.enableStencilWriting((entity.getEntityId() + RenderEventHandler.TAIL_STENCIL_ID) % 256);

        if (ClientConstants.renderingOutline)
            disableStencilWriting((entity.getEntityId()) % 256, false);

    }
}
