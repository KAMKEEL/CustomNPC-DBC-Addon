package kamkeel.npcdbc.mixins.late.impl.npc.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import kamkeel.npcdbc.client.gui.GuiModelDBC;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.model.GuiCreationScreen;
import noppes.npcs.client.gui.util.GuiModelInterface;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.entity.EntityCustomNpc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiCreationScreen.class, remap = false)
public abstract class MixinGuiCreationScreen extends GuiModelInterface {

    public MixinGuiCreationScreen(EntityCustomNpc npc) {
        super(npc);
    }

    @Inject(method = "showPlayerButtons", at = @At("TAIL"))
    private void addDBCModelButton(CallbackInfo ci) {
        this.addButton(new GuiNpcButton(500, this.guiLeft + 310, this.guiTop + 62, 80, 20, "DBC Model"));
    }

    @Inject(method = "actionPerformed", at = @At("TAIL"), remap = true)
    private void doButtonJob(GuiButton btn, CallbackInfo ci){
        if(btn.id == 500){
            this.mc.displayGuiScreen(new GuiModelDBC(this, npc));
        }
    }

}
