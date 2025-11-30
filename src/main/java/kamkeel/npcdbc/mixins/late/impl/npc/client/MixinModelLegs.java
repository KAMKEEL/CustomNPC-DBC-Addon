package kamkeel.npcdbc.mixins.late.impl.npc.client;

import kamkeel.npcdbc.client.ClientProxy;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import net.minecraft.client.model.ModelBase;
import noppes.npcs.client.model.ModelMPM;
import noppes.npcs.client.model.part.ModelLegs;
import noppes.npcs.client.model.util.ModelScaleRenderer;
import noppes.npcs.entity.EntityCustomNpc;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ModelLegs.class)
public abstract class MixinModelLegs extends ModelScaleRenderer {
    @Shadow
    private ModelMPM base;

    private static boolean isFemale;

    public MixinModelLegs(ModelBase par1ModelBase) {
        super(par1ModelBase);
    }

    @Redirect(method="render", at=@At(value= "INVOKE", target="Lnoppes/npcs/client/model/util/ModelScaleRenderer;render(F)V", ordinal = 0))
    private void injectMatrixRightLeg(ModelScaleRenderer instance, float v) {
        EntityCustomNpc npc = ClientProxy.currentlyDrawnNPC;
        if (npc == null)
            return;
        DBCDisplay display = ((INPCDisplay) npc.display).getDBCDisplay();
        isFemale = display.isFemaleInternal();

        GL11.glPushMatrix();
        if(isFemale) {
            GL11.glScalef(0.85F, 1.0F, 0.775F);
            if (this.base.isSneak) {
                GL11.glTranslatef(-0.015F, 0, -0.0F);
            } else {
                GL11.glTranslatef(-0.015F, 0, -0.015F);
            }
        }
        instance.render(v);
        GL11.glPopMatrix();
    }
    @Redirect(method="render", at=@At(value= "INVOKE", target="Lnoppes/npcs/client/model/util/ModelScaleRenderer;render(F)V", ordinal = 1))
    private void injectMatrixLeftLeg(ModelScaleRenderer instance, float v) {

        GL11.glPushMatrix();
        if (isFemale) {
            GL11.glScalef(0.85F, 1.0F, 0.775F);
            if (this.base.isSneak) {
                GL11.glTranslatef(0.015F, 0, -0.0F);
            } else {
                GL11.glTranslatef(0.015F, 0, -0.015F);
            }
        }
        instance.render(v);
        GL11.glPopMatrix();
        isFemale = false;
    }
}
