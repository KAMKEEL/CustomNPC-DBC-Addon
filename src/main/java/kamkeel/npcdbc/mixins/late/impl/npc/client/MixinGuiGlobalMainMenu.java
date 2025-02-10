package kamkeel.npcdbc.mixins.late.impl.npc.client;

import kamkeel.npcdbc.client.gui.global.auras.GuiNPCManageAuras;
import kamkeel.npcdbc.client.gui.global.form.GuiNPCManageForms;
import kamkeel.npcdbc.client.gui.global.outline.GuiNPCManageOutlines;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.mainmenu.GuiNPCGlobalMainMenu;
import noppes.npcs.client.gui.player.inventory.GuiCNPCInventory;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.client.gui.util.GuiNpcSquareButton;
import noppes.npcs.entity.EntityCustomNpc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiNPCGlobalMainMenu.class)
public abstract class MixinGuiGlobalMainMenu extends GuiNPCInterface2 {


    public MixinGuiGlobalMainMenu(EntityCustomNpc npc) {
        super(npc);
    }

    @Shadow
    public void registerButton(GuiNpcSquareButton button){}


    @Unique
    private GuiNpcSquareButton formsButton;

    @Unique
    private GuiNpcSquareButton aurasButton;

    @Unique
    private GuiNpcSquareButton outlinesButton;

    @Unique
    private GuiNpcSquareButton customEffectsButton;

    @Inject(method = "initGui", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/gui/mainmenu/GuiNPCGlobalMainMenu;layoutButtons()V", shift = At.Shift.BEFORE))
    public void addDBCModelButton(CallbackInfo ci) {

        this.registerButton(this.formsButton = new GuiNpcSquareButton(200, 0, 0, 20, "global.customforms", -13421773));
        this.formsButton.setIconPos(24, 23, 24, 56).setIconTexture(GuiCNPCInventory.specialIcons);

        this.registerButton(this.aurasButton = new GuiNpcSquareButton(201, 0, 0, 20, "global.customauras", -13421773));
        this.aurasButton.setIconPos(24, 23, 48, 56).setIconTexture(GuiCNPCInventory.specialIcons);

        this.registerButton(this.outlinesButton = new GuiNpcSquareButton(202, 0, 0, 20, "global.customoutlines", -13421773));
        this.outlinesButton.setIconPos(24, 23, 72, 56).setIconTexture(GuiCNPCInventory.specialIcons);
    }

    @Inject(method = "actionPerformed", at = @At("TAIL"))
    protected void doButtonJob(GuiButton guibutton, CallbackInfo ci) {
        int id = guibutton.id;

        if (id == 200) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiNPCManageForms(npc));
        } else if (id == 201) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiNPCManageAuras(npc));
        } else if (id == 202) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiNPCManageOutlines(npc));
        }
    }

    @Shadow
    public void initGui() {
    }
}
