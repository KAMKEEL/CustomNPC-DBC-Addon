package kamkeel.npcdbc.mixin.impl.npc.client;

import kamkeel.addon.client.DBCClient;
import kamkeel.npcdbc.client.ParticleFormHandler;
import kamkeel.npcdbc.client.gui.global.customforms.GuiNPCManageCustomForms;
import kamkeel.npcdbc.client.gui.SubGuiDBCProperties;
import kamkeel.npcdbc.client.gui.inventory.GuiDBC;
import kamkeel.npcdbc.mixin.INPCDisplay;
import net.minecraft.entity.EntityLivingBase;
import noppes.npcs.client.gui.mainmenu.GuiNpcStats;
import noppes.npcs.client.gui.player.inventory.GuiCNPCInventory;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DBCClient.class)
public class MixinDBCClient {

    @Shadow(remap = false) public boolean supportEnabled;

    /**
     * @author Kam
     * @reason Show DBC Settings Button
     */
    @Overwrite(remap = false)
    public void showDBCStatButtons(GuiNpcStats stats, EntityLivingBase entity){
        if(!supportEnabled)
            return;

        stats.addButton(new GuiNpcButton(300, stats.guiLeft + 217, stats.guiTop+54, 56, 20, "selectServer.edit"));
        stats.addLabel(new GuiNpcLabel(300,"stats.dbcsettings", stats.guiLeft + 140, stats.guiTop+54+5));
    }

    /**
     * @author Kam
     * @reason Perform DBC Settings Button
     */
    @Overwrite(remap = false)
    public void showDBCStatActionPerformed(GuiNpcStats stats, GuiNpcButton btn) {
        if(!supportEnabled)
            return;

        if(btn.id == 300){
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
        if(display.hasDBCData())
            ParticleFormHandler.spawnParticle(npcInterface, display.getDBCDisplay().getFormAuraTypes());
    }


    /**
     * @author Kam
     * @reason Manages Custom Forms
     */
    @Overwrite(remap = false)
    public GuiNPCInterface2 manageCustomForms(EntityNPCInterface npcInterface){ return new GuiNPCManageCustomForms(npcInterface); }


    /**
     * @author Kam
     * @reason Inventory GUI for DBC
     */
    @Overwrite(remap = false)
    public GuiCNPCInventory inventoryGUI() {
        return new GuiDBC();
    }
}
