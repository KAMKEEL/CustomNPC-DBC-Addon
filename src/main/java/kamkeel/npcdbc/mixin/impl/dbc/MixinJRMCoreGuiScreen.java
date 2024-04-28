package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.JRMCoreGuiButtons00;
import JinRyuu.JRMCore.JRMCoreGuiScreen;
import cpw.mods.fml.common.FMLCommonHandler;
import kamkeel.npcdbc.client.gui.dbc.StatSheetGui;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.mixin.IDBCGuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static kamkeel.npcdbc.client.gui.dbc.StatSheetGui.overrideBaseDBC;

@Mixin(value = JRMCoreGuiScreen.class, remap = false)

public class MixinJRMCoreGuiScreen extends GuiScreen implements IDBCGuiScreen {

    @Shadow
    protected static List<Object[]> detailList;


    @Shadow
    public int guiID;
    @Unique
    private int newGuiID;
    @Unique
    private boolean ignoreInit = false;

    @Inject(method = "updateScreen", at=@At("HEAD"), remap = true)
    private void onUpdateScreen(CallbackInfo ci){
        if(this.guiID == 10 && overrideBaseDBC && DBCData.getClient().Powertype == 1)
            FMLCommonHandler.instance().showGuiScreen(new StatSheetGui());
    }

    @Inject(method = "initGui", at=@At("RETURN"), remap = true)
    private void onInitGui(CallbackInfo ci){
        if(ignoreInit)
            this.guiID = newGuiID;
    }


    @Inject(method = "drawScreen", at=@At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"), remap = true)
    private void onDrawScreen(CallbackInfo ci){
        if(this.guiID != 10)
            return;
        String s = "Switch to "+(overrideBaseDBC ? "Normal" : "§aEnhanced") +" GUI";
        int i = this.fontRendererObj.getStringWidth(s)+10;
        this.buttonList.add(new JRMCoreGuiButtons00(303030303, (this.width -i)/2, (this.height-159)/2 - 30, i + 8, 20, s, 0));
    }

    @Inject(method="actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V", at=@At("HEAD"), remap = true)
    public void onActionPerformed(GuiButton button, CallbackInfo ci){
        if(button.id == 303030303){
            overrideBaseDBC = true;
        }
    }

    @Unique
    public void setGuiIDPostInit(int id) {
        this.newGuiID = id;
        this.ignoreInit = true;
    }
}
