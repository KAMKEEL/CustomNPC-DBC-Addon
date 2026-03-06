package kamkeel.npcdbc.client.gui.global.auras;

import noppes.npcs.client.gui.util.GuiDirectoryCategorized;
import net.minecraft.client.gui.GuiScreen;
import kamkeel.npcdbc.client.sound.AuraSound;
import kamkeel.npcdbc.client.sound.SoundHandler;
import kamkeel.npcdbc.constants.DBCSyncType;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.data.SoundSource;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.aura.AuraDisplay;
import noppes.npcs.controllers.data.Category;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.get.aura.DBCGetAura;
import kamkeel.npcdbc.network.packets.request.aura.DBCRemoveAura;
import kamkeel.npcdbc.network.packets.request.aura.DBCSaveAura;
import kamkeel.npcdbc.network.packets.request.category.DBCCategoryMoveItem;
import kamkeel.npcdbc.network.packets.request.category.DBCCategoryRemove;
import kamkeel.npcdbc.network.packets.request.category.DBCCategorySave;
import kamkeel.npcdbc.network.packets.request.category.DBCRequestCategories;
import kamkeel.npcdbc.network.packets.request.category.DBCRequestCategoryItems;
import net.minecraft.client.Minecraft;
import noppes.npcs.client.NoppesUtil;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.HashMap;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import static kamkeel.npcdbc.client.ClientEventHandler.spawnAura;
import static kamkeel.npcdbc.client.ClientEventHandler.spawnKaiokenAura;

public class GuiAuraDirectory extends GuiDirectoryCategorized implements IAuraManagerGui {
    public Aura aura = new Aura();
    public AuraDisplay display;
    public DBCDisplay visualDisplay;
    public EntityNPCInterface npc;
    private EntityNPCInterface originalNpc;

    public AuraSound auraSound, secondarySound, kaiokenSound, kettleSound, secondaryKettleSound;
    private boolean renderAura;
    private int revampedAura;
    public static int auraTicks = 1;

    public GuiAuraDirectory(EntityNPCInterface npc) {
        super();
        this.originalNpc = npc;
        this.npc = DBCDisplay.setupGUINPC((EntityCustomNpc) npc);
        this.npc.display.name = "aura man";
        this.npc.height = 1.62f;
        this.npc.width = 0.43f;
        visualDisplay = ((INPCDisplay) this.npc.display).getDBCDisplay();
        visualDisplay.auraID = -1;
        visualDisplay.formID = -1;
        visualDisplay.outlineID = -1;
        display = aura.display;
        zoomed = 50;
    }

    @Override
    protected String getTitle() { return "Auras"; }

    @Override
    protected void requestCategoryList() {
        DBCPacketHandler.Instance.sendToServer(new DBCRequestCategories(DBCSyncType.AURA));
    }

    @Override
    protected void requestItemsInCategory(int catId) {
        DBCPacketHandler.Instance.sendToServer(new DBCRequestCategoryItems(DBCSyncType.AURA, catId));
    }

    @Override
    protected void requestItemData(int itemId) {
        DBCPacketHandler.Instance.sendToServer(new DBCGetAura(itemId));
    }

    @Override
    protected void onSaveCategory(Category cat) {
        DBCPacketHandler.Instance.sendToServer(new DBCCategorySave(DBCSyncType.AURA, cat.writeNBT(new NBTTagCompound())));
    }

    @Override
    protected void onRemoveCategory(int catId) {
        DBCPacketHandler.Instance.sendToServer(new DBCCategoryRemove(DBCSyncType.AURA, catId));
    }

    @Override
    protected void onAddItem(int catId) {
        String name = "New";
        while (itemData.containsKey(name)) name += "_";
        Aura newAura = new Aura();
        newAura.name = name;
        DBCPacketHandler.Instance.sendToServer(new DBCSaveAura(newAura.writeToNBT(), name));
    }

    @Override
    protected void onRemoveItem(int itemId) {
        DBCPacketHandler.Instance.sendToServer(new DBCRemoveAura(itemId));
        aura = new Aura();
        visualDisplay.auraOn = false;
        visualDisplay.auraID = -1;
        visualDisplay.outlineID = -1;
        stopAllSounds();
    }

    @Override
    protected void onEditItem() {
        if (aura != null && aura.id >= 0) {
            Minecraft.getMinecraft().displayGuiScreen(new SubGuiAuraDisplay(this));
        }
    }

    @Override
    protected void onCloneItem() {
        if (aura != null && aura.id >= 0) {
            Aura clone = (Aura) aura.clone();
            while (itemData.containsKey(clone.name)) clone.name += "_";
            DBCPacketHandler.Instance.sendToServer(new DBCSaveAura(clone.writeToNBT(), clone.name));
        }
    }

    @Override
    protected void onItemReceived(NBTTagCompound compound) {
        int oldID = aura != null ? aura.id : -1;
        aura = new Aura();
        aura.readFromNBT(compound);
        setPrevItemName(aura.name);

        if (aura.id != -1) {
            AuraController.getInstance().customAuras.replace(aura.id, aura);
            display = aura.display;
            visualDisplay.auraID = aura.id;
            visualDisplay.outlineID = display.outlineID;
            visualDisplay.auraOn = true;

            if (aura.id != oldID) {
                stopAllSounds();
                playSound(true);
            }
        }
    }

    @Override
    protected boolean hasSelectedItem() {
        return aura != null && aura.id >= 0;
    }

    @Override
    protected int getSelectedItemId() {
        return aura != null ? aura.id : -1;
    }

    @Override
    protected void sendMovePacket(int itemId, int destCatId) {
        DBCPacketHandler.Instance.sendToServer(new DBCCategoryMoveItem(DBCSyncType.AURA, itemId, destCatId));
    }

    @Override
    protected void drawItemPreview(int centerX, int centerY, int mouseX, int mouseY, float partialTicks) {
        if (aura.id == -1) return;

        int ticks = Minecraft.getMinecraft().thePlayer.ticksExisted;
        if (auraTicks != ticks) renderAura = true;

        SubGuiAuraDisplay.useGUIAura = true;
        if (ticks % 5 == 0 && renderAura && visualDisplay.auraID != -1) {
            EntityAura enhancedAura = visualDisplay.auraEntity;
            boolean kaioken = visualDisplay.isKaioken;
            if (revampedAura == 1) {
                if (enhancedAura == null) {
                    enhancedAura = new EntityAura(npc, aura).load(true).spawn();
                    enhancedAura.isInKaioken = kaioken;
                    enhancedAura.isGUIAura = true;
                } else if (ticks % 10 == 0) {
                    enhancedAura.load(true);
                    if (kaioken) {
                        EntityAura kaiokenAura = enhancedAura.children.get("Kaioken");
                        if (kaiokenAura != null) kaiokenAura.loadKaioken();
                    }
                }
            } else {
                if (enhancedAura != null) enhancedAura.despawn();
                if (kaioken && display.kaiokenOverrides) {
                    spawnKaiokenAura(aura, visualDisplay);
                } else {
                    spawnAura(npc, aura);
                    if (aura.hasSecondaryAura()) spawnAura(npc, aura.getSecondaryAur());
                    if (kaioken) spawnKaiokenAura(aura, visualDisplay);
                }
            }
            auraTicks = ticks;
            renderAura = false;
        }

        GL11.glColor4f(1, 1, 1, 1);
        EntityLivingBase entity = this.npc;

        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        float scale = Math.min(previewW, previewH) / 200f;
        float renderZoom = zoomed * scale;

        GL11.glTranslatef(centerX, centerY, 60F);
        GL11.glScalef(-renderZoom, renderZoom, renderZoom);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f7 = entity.rotationYawHead;
        float f5 = (float) centerX - mouseX;
        float f6 = (float) (centerY - 50) - mouseY;

        GL11.glRotatef(135F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-(float) Math.atan(f6 / 800F) * 20F, 1.0F, 0.0F, 0.0F);
        entity.prevRenderYawOffset = entity.renderYawOffset = rotation;
        entity.prevRotationYaw = entity.rotationYaw = (float) Math.atan(f5 / 80F) * 40F + rotation;
        entity.rotationPitch = entity.prevRotationPitch = -(float) Math.atan(f6 / 80F) * 10F;
        entity.prevRotationYawHead = entity.rotationYawHead = entity.rotationYaw;
        GL11.glTranslatef(0.0F, 0.1f + entity.yOffset, 1F);
        RenderManager.instance.playerViewY = 180F;

        try {
            RenderManager.instance.renderEntityWithPosYaw(entity, 0.0, 0.0, 0.0, 0.0F, partialTicks);
        } catch (Exception ignored) {
        }

        SubGuiAuraDisplay.useGUIAura = false;
        entity.prevRenderYawOffset = entity.renderYawOffset = f2;
        entity.prevRotationYaw = entity.rotationYaw = f3;
        entity.rotationPitch = entity.prevRotationPitch = f4;
        entity.prevRotationYawHead = entity.rotationYawHead = f7;

        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPopMatrix();
    }

    @Override
    protected GuiScreen getWindowedVariant() {
        return new GuiNPCManageAuras(originalNpc);
    }

    // ========== IAuraManagerGui ==========
    @Override
    public HashMap<String, Integer> getAuraData() { return itemData; }

    @Override
    public GuiCustomScroll getAuraScroll() { return itemScroll; }

    @Override
    public EntityNPCInterface getAuraNPC() { return npc; }

    @Override
    public Aura getAura() { return aura; }

    @Override
    public AuraDisplay getAuraDisplay() { return display; }

    @Override
    public DBCDisplay getAuraVisualDisplay() { return visualDisplay; }

    @Override
    public String getAuraSelected() { return prevItemName; }

    @Override
    public void setAuraSelected(String selected) { this.prevItemName = selected; }

    @Override
    public AuraSound getAuraSound() { return auraSound; }

    @Override
    public AuraSound getKaiokenSound() { return kaiokenSound; }

    @Override
    public AuraSound getKettleSound() { return kettleSound; }

    @Override
    public AuraSound getSecondarySound() { return secondarySound; }

    @Override
    public AuraSound getSecondaryKettleSound() { return secondaryKettleSound; }

    @Override
    public void closeSubGui(Object obj) {
        saveCurrentItem();
        if (selectedCatId >= 0) requestItemsInCategory(selectedCatId);
        // Restart sounds in case sound settings were changed in SubGui
        stopAllSounds();
        NoppesUtil.openGUI(player, this);
        if (aura != null && aura.id >= 0) {
            playSound(true);
        }
    }

    @Override
    protected void saveCurrentItem() {
        if (aura != null && aura.id >= 0 && prevItemName != null && !prevItemName.isEmpty()) {
            DBCPacketHandler.Instance.sendToServer(new DBCSaveAura(aura.writeToNBT(), prevItemName));
            prevItemName = aura.name;
        }
    }

    // ========== Sound ==========
    public void playSound(boolean allowRepeats) {
        String soundLoc = display.getFinalSound(display.type);
        String kaiokenLoc = visualDisplay.isKaioken ? display.getKaiokenSound() : null;
        String kettleLoc = display.kettleModeEnabled ? "jinryuudragonbc:DBC5.majin_cattle" : null;

        Aura secondary = aura.getSecondaryAur();
        String secondLoc = secondary != null ? secondary.display.getFinalSound(secondary.display.type) : null;
        String secondaryKettleLoc = secondary != null && secondary.display.kettleModeEnabled ? "jinryuudragonbc:DBC5.majin_cattle" : null;

        if (soundLoc != null && (allowRepeats || !SoundHandler.isPlayingSound(npc, soundLoc))) {
            auraSound = new AuraSound(aura, new SoundSource(soundLoc, npc));
            auraSound.isGUIAura = true;
            auraSound.setRepeat(true).play(false);
        }
        if (kaiokenLoc != null && (allowRepeats || !SoundHandler.isPlayingSound(npc, kaiokenLoc))) {
            kaiokenSound = new AuraSound(aura, new SoundSource(kaiokenLoc, npc));
            kaiokenSound.isGUIAura = true;
            kaiokenSound.setRepeat(true).play(false);
        }
        if (kettleLoc != null && (allowRepeats || !SoundHandler.isPlayingSound(npc, kettleLoc))) {
            kettleSound = new AuraSound(aura, new SoundSource(kettleLoc, npc));
            kettleSound.isGUIAura = true;
            kettleSound.soundSource.fadeFactor *= 10;
            kettleSound.setRepeat(true).play(false);
        }
        if (secondLoc != null && (allowRepeats || !SoundHandler.isPlayingSound(npc, secondLoc))) {
            secondarySound = new AuraSound(aura, new SoundSource(secondLoc, npc));
            secondarySound.isGUIAura = true;
            secondarySound.setRepeat(true).play(false);
        }
        if (secondaryKettleLoc != null && (allowRepeats || !SoundHandler.isPlayingSound(npc, secondaryKettleLoc))) {
            secondaryKettleSound = new AuraSound(aura, new SoundSource(secondaryKettleLoc, npc));
            secondaryKettleSound.isGUIAura = true;
            secondaryKettleSound.soundSource.fadeFactor *= 10;
            secondaryKettleSound.setRepeat(true).play(false);
        }
    }

    public void stopSound(AuraSound sound, boolean immediate) {
        if (sound != null) {
            if (immediate) sound.stop(false);
            else {
                sound.soundSource.fadeOut = true;
                sound.soundSource.fadeFactor = 0.075f;
            }
        }
    }

    public void stopAllSounds() {
        stopSound(auraSound, false);
        stopSound(kaiokenSound, false);
        stopSound(secondarySound, false);
        stopSound(kettleSound, false);
        stopSound(secondaryKettleSound, false);
    }

    @Override
    public void onGuiClosed() {
        stopAllSounds();
        super.onGuiClosed();
    }

}
