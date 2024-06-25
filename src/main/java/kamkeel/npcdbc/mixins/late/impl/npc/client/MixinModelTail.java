package kamkeel.npcdbc.mixins.late.impl.npc.client;


import kamkeel.npcdbc.constants.DBCScriptType;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import net.minecraft.client.model.ModelBase;
import noppes.npcs.client.model.part.ModelTail;
import noppes.npcs.client.model.part.tails.ModelMonkeyTail;
import noppes.npcs.client.model.util.ModelScaleRenderer;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
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
    private int color;

    public MixinModelTail(ModelBase par1ModelBase) {
        super(par1ModelBase);
    }

    public MixinModelTail(ModelBase par1ModelBase, int par2, int par3) {
        super(par1ModelBase, par2, par3);
    }

    @Inject(method = "initData", at = @At(value = "TAIL"))
    private void colorCorrectionTail(EntityCustomNpc data, CallbackInfo ci) {
        if(this.isHidden && !monkey.isHidden){
            DBCDisplay display = ((INPCDisplay) data.display).getDBCDisplay();
            if(display != null && display.enabled){
                color = display.getColor("fur");
                if(color == -1){
                    color = display.getColor("hair");
                }
            }
        }
    }
}
