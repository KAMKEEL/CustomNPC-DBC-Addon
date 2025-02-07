package kamkeel.npcdbc.mixins.late.impl.npc.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import kamkeel.npcdbc.client.gui.global.auras.GuiNPCManageAuras;
import kamkeel.npcdbc.client.gui.global.effects.GuiNPCManageEffects;
import kamkeel.npcdbc.client.gui.global.form.GuiNPCManageForms;
import kamkeel.npcdbc.client.gui.global.outline.GuiNPCManageOutlines;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.launchwrapper.Launch;
import noppes.npcs.client.gui.mainmenu.GuiNPCGlobalMainMenu;
import noppes.npcs.client.gui.player.inventory.GuiCNPCInventory;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.client.gui.util.GuiNpcButton;
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
    private GuiNpcSquareButton aurasButton;

    @Unique
    private GuiNpcSquareButton outlinesButton;

    @Inject(method = "initGui", at = @At(value = "INVOKE", target = "Lnoppes/npcs/client/gui/mainmenu/GuiNPCGlobalMainMenu;layoutButtons()V", shift = At.Shift.BEFORE))
    public void addDBCModelButton(CallbackInfo ci) {

        this.registerButton(this.formsButton = new GuiNpcSquareButton(200, 0, 0, 20, "global.customforms", -13421773));
        this.formsButton.setIconPos(24, 24, 48, 50).setIconTexture(GuiCNPCInventory.specialIcons);

        this.registerButton(this.aurasButton = new GuiNpcSquareButton(201, 0, 0, 20, "global.customauras", -13421773));
        this.aurasButton.setIconPos(24, 24, 72, 50).setIconTexture(GuiCNPCInventory.specialIcons);

        this.registerButton(this.outlinesButton = new GuiNpcSquareButton(202, 0, 0, 20, "global.customoutlines", -13421773));
        this.outlinesButton.setIconPos(24, 24, 96, 50).setIconTexture(GuiCNPCInventory.specialIcons);

        if((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment")) {
            this.addButton(new GuiNpcButton(203, guiLeft + 210 + 99 + 3, y + 154, 99, 20, "Effects"));
        }
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
        else if (id == 203) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiNPCManageEffects(npc));
        }
    }

    @Shadow
    public void initGui() {
    }
}
