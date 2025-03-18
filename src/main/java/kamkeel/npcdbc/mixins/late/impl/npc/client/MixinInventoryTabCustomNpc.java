package kamkeel.npcdbc.mixins.late.impl.npc.client;

import kamkeel.npcdbc.client.gui.inventory.GuiDBC;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import noppes.npcs.client.gui.player.inventory.GuiCNPCInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tconstruct.client.tabs.AbstractTab;
import tconstruct.client.tabs.InventoryTabCustomNpc;

@Mixin(value = InventoryTabCustomNpc.class)
public abstract class MixinInventoryTabCustomNpc extends AbstractTab {

    public MixinInventoryTabCustomNpc(int id, int posX, int posY, ItemStack renderStack) {
        super(id, posX, posY, renderStack);
    }

    @Inject(method = "tabHelper", at = @At(value = "TAIL"), remap = false)
    private static void addTabHelperFunctionality(CallbackInfo ci) {
        int tab = GuiCNPCInventory.activeTab;
        if (tab == -200) {
            Minecraft mc = Minecraft.getMinecraft();
            mc.displayGuiScreen(new GuiDBC());
        }
    }
}
