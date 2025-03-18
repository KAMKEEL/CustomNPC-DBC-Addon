package kamkeel.npcdbc.mixins.late.impl.npc.client;

import kamkeel.npcdbc.client.CNPCAnimationHelper;
import kamkeel.npcdbc.client.ParticleFormHandler;
import kamkeel.npcdbc.client.gui.SubGuiDBCProperties;
import kamkeel.npcdbc.client.gui.global.auras.GuiNPCManageAuras;
import kamkeel.npcdbc.client.gui.global.form.GuiNPCManageForms;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcs.addon.client.DBCClient;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.gui.mainmenu.GuiNpcStats;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DBCClient.class)
public class MixinDBCClient {

    @Shadow(remap = false)
    public boolean supportEnabled;

    /**
     * @author Kam
     * @reason Show DBC Settings Button
     */
    @Overwrite(remap = false)
    public void showDBCStatButtons(GuiNpcStats stats, EntityLivingBase entity) {
        if (!supportEnabled)
            return;

        stats.addButton(new GuiNpcButton(300, stats.guiLeft + 217, stats.guiTop + 54, 56, 20, "selectServer.edit"));
        stats.addLabel(new GuiNpcLabel(300, "stats.dbcsettings", stats.guiLeft + 140, stats.guiTop + 54 + 5));
    }

    /**
     * @author Kam
     * @reason Perform DBC Settings Button
     */
    @Overwrite(remap = false)
    public void showDBCStatActionPerformed(GuiNpcStats stats, GuiNpcButton btn) {
        if (!supportEnabled)
            return;

        if (btn.id == 300) {
            stats.setSubGui(new SubGuiDBCProperties(stats.npc));
        }
    }


    /**
     * @author Kam
     * @reason Perform DBC Aura Rendering
     */
    @Overwrite(remap = false)
    public void renderDBCAuras(EntityNPCInterface npcInterface) {
        INPCDisplay display = (INPCDisplay) npcInterface.display;
        if (display.hasDBCData())
            ParticleFormHandler.spawnAura2D(display.getDBCDisplay());
    }


    /**
     * @author Kam
     * @reason Manages Custom Forms
     */
    @Overwrite(remap = false)
    public GuiNPCInterface2 manageCustomForms(EntityNPCInterface npcInterface) {
        return new GuiNPCManageForms(npcInterface);
    }


    /**
     * @author Kam
     * @reason Manages Custom Auras
     */
    @Overwrite(remap = false)
    public GuiNPCInterface2 manageCustomAuras(EntityNPCInterface npcInterface) {
        return new GuiNPCManageAuras(npcInterface);
    }

    /**
     * @author Kam
     * @reason Manages Animation Mixins
     */
    @Overwrite(remap = false)
    public void applyRenderModel(ModelRenderer renderer) {
        CNPCAnimationHelper.applyValues(renderer);
    }

    /**
     * @author Kam
     * @reason Manages First Animation Mixins
     */
    @Overwrite(remap = false)
    public boolean firstPersonAnimation(float partialRenderTick, EntityPlayer player, ModelBiped model, RenderBlocks renderBlocksIr, ResourceLocation resItemGlint) {
        return CNPCAnimationHelper.mixin_renderFirstPersonAnimation(partialRenderTick, player, model, renderBlocksIr, resItemGlint);
    }
}
