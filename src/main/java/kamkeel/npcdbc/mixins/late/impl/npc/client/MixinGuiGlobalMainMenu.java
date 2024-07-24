package kamkeel.npcdbc.mixins.late.impl.npc.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import kamkeel.npcdbc.client.gui.global.outline.GuiNPCManageOutlines;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.mainmenu.GuiNPCGlobalMainMenu;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.entity.EntityCustomNpc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiNPCGlobalMainMenu.class, remap = false)
public abstract class MixinGuiGlobalMainMenu extends GuiNPCInterface2 {


    public MixinGuiGlobalMainMenu(EntityCustomNpc npc) {
        super(npc);
    }

    @Inject(method = "initGui", at = @At("TAIL"))
    public void addDBCModelButton(CallbackInfo ci, @Local(name = "y") LocalIntRef Y) {
        int y = Y.get();
        // this.addButton(new GuiNpcButton(200, guiLeft + 210, y + 132, 99, 20, "global.customforms"));
        //   this.addButton(new GuiNpcButton(201, guiLeft + 210 + 99 + 3, y + 132, 99, 20, "global.customauras"));
        this.addButton(new GuiNpcButton(2001, guiLeft + 210 + 99 + 3, y + 132, 99, 20, "global.customoutlines"));


    }

    @Inject(method = "actionPerformed", at = @At("TAIL"), remap = true)
    protected void doButtonJob(GuiButton guibutton, CallbackInfo ci) {
        int id = guibutton.id;

        if (id == 2001) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiNPCManageOutlines(npc));
        }


    }

    @Shadow
    public void initGui() {
    }
}
