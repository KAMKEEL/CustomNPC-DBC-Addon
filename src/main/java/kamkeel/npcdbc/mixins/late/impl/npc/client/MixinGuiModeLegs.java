package kamkeel.npcdbc.mixins.late.impl.npc.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.model.GuiModelLegs;
import noppes.npcs.client.gui.util.GuiModelInterface;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.data.ModelPartData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiModelLegs.class, remap = false)
public abstract class MixinGuiModeLegs extends GuiModelInterface {


    public MixinGuiModeLegs(EntityCustomNpc npc) {
        super(npc);
    }

    @Inject(method = "initGui", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/gui/model/GuiModelLegs;addButton(Lnoppes/npcs/client/gui/util/GuiNpcButton;)V", ordinal = 6, shift = At.Shift.AFTER, remap = false), remap = true)
    public void addDBCModelButton(CallbackInfo ci, @Local(name = "y") LocalIntRef y, @Local(name = "tail") LocalRef<ModelPartData> tail1) {
        ModelPartData tail = tail1.get();
        if (tail != null && tail.type == 8) {
            this.addButton(new GuiNpcButton(122, this.guiLeft + 163, y.get(), 20, 20, "X"));
            getButton(122).enabled = !tail.getColor().equals("632700");
        }
    }

    @Inject(method = "actionPerformed", at = @At("TAIL"), remap = true)
    protected void doButtonJob(GuiButton btn, CallbackInfo ci) {
        if (btn.id == 122) {
            ModelPartData data = this.playerdata.getPartData("tail");
            data.color = 0x632700;
        }
        initGui();
    }

    @Shadow
    public void initGui() {
    }
}
