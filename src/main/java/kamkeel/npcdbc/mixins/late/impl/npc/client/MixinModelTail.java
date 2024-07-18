package kamkeel.npcdbc.mixins.late.impl.npc.client;


import kamkeel.npcdbc.client.utils.Color;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.client.model.part.ModelTail;
import noppes.npcs.client.model.part.tails.ModelMonkeyTail;
import noppes.npcs.client.model.util.ModelScaleRenderer;
import noppes.npcs.entity.EntityCustomNpc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

            int tailColor = 0;
            if (DBCRace.isSaiyan(display.race)) {
                if (monkey.monkey_large.isHidden)
                    ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:gui/allw.png"));

                tailColor = display.bodyC1;


                boolean hasFur = display.hasFur;
                boolean isSSJ4 = display.hairType.equals("ssj4"), isOozaru = display.hairType.equals("oozaru");
                int furColor = display.furColor;

                Form form = display.getForm();
                if (form != null) {
                    if (form.display.hasColor("hair"))
                        tailColor = form.display.hairColor;
                    if (form.display.hasColor("fur"))
                        furColor = form.display.furColor;

                    hasFur = form.display.hasBodyFur;
                    if (form.display.hairType.equals("ssj4"))
                        isSSJ4 = true;
                    else if (form.display.hairType.equals("oozaru"))
                        isOozaru = true;
                }
                if (isSSJ4 || hasFur || isOozaru)
                    tailColor = furColor;


            } else if (display.race == DBCRace.ARCOSIAN) {
                if (!monkey.monkey_large.isHidden)
                    ClientProxy.bindTexture(new ResourceLocation("jinryuudragonbc:cc/arc/m/3B00.png"));
                if (display.arcoState < 4 || display.arcoState == 6)
                    tailColor = display.bodyC3;
                else
                    tailColor = display.bodyCM;

                Form form = display.getForm();
                if (form != null) {
                    if ((form.display.bodyType.contains("first") || form.display.bodyType.contains("second") || form.display.bodyType.contains("third"))) {
                        if (form.display.hasColor("bodyc3"))
                            tailColor = form.display.bodyC3;
                    } else if (form.display.hasColor("bodycm"))
                        tailColor = form.display.bodyCM;

                }

            }

            new Color(tailColor, base.alpha).glColor();
        }
    }
}
