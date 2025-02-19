package kamkeel.npcdbc.mixins.late.impl.npc.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.client.gui.inventory.GuiDBC;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.gui.player.inventory.GuiCNPCInventory;
import noppes.npcs.client.gui.util.GuiMenuSideButton;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.entity.EntityCustomNpc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiCNPCInventory.class)
public abstract class MixinGuiCNPCInventory extends GuiNPCInterface {

    @Unique
    private static final ResourceLocation MENU_ICONS = new ResourceLocation(CustomNpcPlusDBC.ID + ":textures/gui/icons.png");

    @Shadow
    public static int activeTab;

    public MixinGuiCNPCInventory(EntityCustomNpc npc) {
        super(npc);
    }


    @Inject(method = "initGui", at = @At(value = "FIELD", target = "Lnoppes/npcs/config/ConfigMain;EnableProfiles:Z", shift = At.Shift.BEFORE, remap = false))
    public void addDBCInventory(CallbackInfo ci,  @Local(name = "y") LocalIntRef y) {
        int yVal = y.get();

        yVal += 21;
        GuiMenuSideButton dbcButton = new GuiMenuSideButton(-200, this.guiLeft + this.xSize + 37, this.guiTop + yVal, 22, 22, "");
        dbcButton.rightSided = true;
        dbcButton.active = activeTab == -200;
        dbcButton.renderIconPosX = 18;
        dbcButton.renderIconPosY = 48;
        dbcButton.renderResource = MENU_ICONS;
        this.addButton(dbcButton);
        y.set(yVal);

    }

    @Inject(method = "actionPerformed", at = @At("TAIL"))
    protected void doButtonJob(GuiButton guibutton, CallbackInfo ci) {
        int id = guibutton.id;
        if (guibutton.id == -200 && activeTab != -200) {
            activeTab = -200;
            this.mc.displayGuiScreen(new GuiDBC());
        }
    }
}
