package kamkeel.npcdbc.mixin.impl.dbc;

import JinRyuu.JRMCore.JRMCoreGuiScreen;
import cpw.mods.fml.common.FMLCommonHandler;
import kamkeel.npcdbc.client.gui.dbc.StatSheetGui;
import kamkeel.npcdbc.data.DBCData;
import kamkeel.npcdbc.mixin.IDBCGuiScreen;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

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
        if(this.guiID == 10 && StatSheetGui.overrideBaseDBC && DBCData.getClient().Powertype == 1)
            FMLCommonHandler.instance().showGuiScreen(new StatSheetGui());
    }

    @Inject(method = "initGui", at=@At("RETURN"), remap = true)
    private void onInitGui(CallbackInfo ci){
        if(ignoreInit)
            this.guiID = newGuiID;
    }

    @Unique
    public void setGuiIDPostInit(int id) {
        this.newGuiID = id;
        this.ignoreInit = true;
    }
}
