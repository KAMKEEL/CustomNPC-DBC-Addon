package kamkeel.npcdbc.client.gui.global.auras;

import kamkeel.npcdbc.client.sound.AuraSound;
import kamkeel.npcdbc.client.sound.SoundHandler;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.data.SoundSource;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.aura.AuraDisplay;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.get.aura.DBCGetAura;
import kamkeel.npcdbc.network.packets.player.aura.DBCRequestAura;
import kamkeel.npcdbc.network.packets.request.aura.DBCRemoveAura;
import kamkeel.npcdbc.network.packets.request.aura.DBCSaveAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.select.GuiSoundSelection;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import static kamkeel.npcdbc.client.ClientEventHandler.spawnAura;
import static kamkeel.npcdbc.client.ClientEventHandler.spawnKaiokenAura;

public class GuiNPCManageAuras extends GuiNPCInterface2 implements ICustomScrollListener, IScrollData, IGuiData, ISubGuiListener, GuiYesNoCallback, ITextfieldListener {
    public GuiCustomScroll scrollAuras;
    public HashMap<String, Integer> data = new HashMap<>();
    public AuraSound auraSound, secondarySound, kaiokenSound, kettleSound, secondaryKettleSound;
    boolean setNormalSound = true;
    public Aura aura = new Aura();
    private String search = "";
    private String originalName = "";
    public String selected = null;

    public AuraDisplay display;
    public DBCDisplay visualDisplay;
    private boolean renderAura;
    private int revampedAura;
    public static int auraTicks = 1;
    private float zoomed = 50.0F, rotation;

    public GuiNPCManageAuras(EntityNPCInterface npc) {
        super(npc);
        this.npc = DBCDisplay.setupGUINPC((EntityCustomNpc) npc);
        this.npc.display.name = "aura man";
        this.npc.height = 1.62f;
        this.npc.width = 0.43f;
        visualDisplay = ((INPCDisplay) this.npc.display).getDBCDisplay();

        visualDisplay.auraID = -1;
        visualDisplay.formID = -1;
        visualDisplay.outlineID = -1;

        this.display = aura.display;

        DBCPacketHandler.Instance.sendToServer(new DBCRequestAura(-1, false));
    }

    public void initGui() {
        super.initGui();
        addButton(new GuiNpcButton(0, guiLeft + 368, guiTop + 8, 45, 20, "gui.add"));

        addButton(new GuiNpcButton(1, guiLeft + 368, guiTop + 32, 45, 20, "gui.remove"));
        getButton(1).enabled = aura != null && aura.id != -1;

        addButton(new GuiNpcButton(2, guiLeft + 368, guiTop + 56, 45, 20, "gui.clone"));
        getButton(2).enabled = aura != null && aura.id != -1;

        addButton(new GuiNpcButton(3, guiLeft + 368, guiTop + 80, 45, 20, "gui.edit"));
        getButton(3).enabled = aura != null && aura.id != -1;

        if (scrollAuras == null) {
            scrollAuras = new GuiCustomScroll(this, 0, 0);
            scrollAuras.setSize(143, 185);
        }
        scrollAuras.guiLeft = guiLeft + 220;
        scrollAuras.guiTop = guiTop + 4;
        addScroll(scrollAuras);
        scrollAuras.setList(getSearchList());

        addTextField(new GuiNpcTextField(55, this, fontRendererObj, guiLeft + 220, guiTop + 4 + 3 + 185, 143, 20, search));
        if (aura != null && aura.id != -1) {
            addLabel(new GuiNpcLabel(10, "ID", guiLeft + 368, guiTop + 4 + 3 + 185));
            addLabel(new GuiNpcLabel(11, aura.id + "", guiLeft + 368, guiTop + 4 + 3 + 195));
            //
            //            int y = guiTop + 3;
            //
            //            addTextField(new GuiNpcTextField(13, this, this.fontRendererObj, guiLeft + 36, y, 180, 20, aura.name));
            //            addLabel(new GuiNpcLabel(13, "gui.name", guiLeft + 4, y + 5));
            //
            //            y += 23;
            //            addTextField(new GuiNpcTextField(14, this, guiLeft + 70, y, 146, 20, aura.menuName.replaceAll("§", "&")));
            //            getTextField(14).setMaxStringLength(20);
            //            addLabel(new GuiNpcLabel(14, "general.menuName", guiLeft + 4, y + 5));

            //            y += 40;
            //            addLabel(new GuiNpcLabel(30, "general.auraSound", guiLeft + 5, y + 5));
            //            y += 16;
            //            addTextField(new GuiNpcTextField(30, this, fontRendererObj, guiLeft + 5, y, 155, 20, aura.display.auraSound));
            //            addButton(new GuiNpcButton(30, guiLeft + 161, y, 40, 20, "gui.select"));
            //            addButton(new GuiNpcButton(301, guiLeft + 201, y, 16, 20, "X"));
            //            getButton(301).enabled = !aura.display.auraSound.equals("jinryuudragonbc:DBC.aura");
            //
            //            y += 23;
            //            addLabel(new GuiNpcLabel(31, "general.kaiokenSound", guiLeft + 5, y + 5));
            //            y += 16;
            //            addTextField(new GuiNpcTextField(31, this, fontRendererObj, guiLeft + 5, y, 155, 20, aura.display.kaiokenSound));
            //            addButton(new GuiNpcButton(31, guiLeft + 161, y, 40, 20, "gui.select"));
            //            addButton(new GuiNpcButton(311, guiLeft + 201, y, 16, 20, "X"));
            //            getButton(311).enabled = !aura.display.kaiokenSound.isEmpty();

            //            y += 59;
            //            addButton(new GuiNpcButton(1500, guiLeft + 7, y, 202, 20, "display.displaySettings"));
        }
    }

    public void playSound(boolean allowRepeats) {
        String soundLoc = display.getFinalSound(display.type);
        String kaiokenLoc = visualDisplay.isKaioken ? display.getKaiokenSound() : null;
        String kettleLoc = display.kettleModeEnabled ? "jinryuudragonbc:DBC5.majin_cattle" : null;

        Aura secondary = aura.getSecondaryAur();
        String secondLoc = secondary != null ? secondary.display.getFinalSound(secondary.display.type) : null;
        String secondaryKettleLoc = secondary != null && secondary.display.kettleModeEnabled ? "jinryuudragonbc:DBC5.majin_cattle" : null;

        if (soundLoc != null && (allowRepeats ? true : !SoundHandler.isPlayingSound(npc, soundLoc))) {
            auraSound = new AuraSound(aura, new SoundSource(soundLoc, npc));
            auraSound.isGUIAura = true;
            auraSound.setRepeat(true).play(false);
        }

        if (kaiokenLoc != null && (allowRepeats ? true : !SoundHandler.isPlayingSound(npc, kaiokenLoc))) {
            kaiokenSound = new AuraSound(aura, new SoundSource(kaiokenLoc, npc));
            kaiokenSound.isGUIAura = true;
            kaiokenSound.setRepeat(true).play(false);
        }
        if (kettleLoc != null && (allowRepeats ? true : !SoundHandler.isPlayingSound(npc, kettleLoc))) {
            kettleSound = new AuraSound(aura, new SoundSource(kettleLoc, npc));
            kettleSound.isGUIAura = true;
            kettleSound.soundSource.fadeFactor *= 10;
            kettleSound.setRepeat(true).play(false);
        }

        if (secondLoc != null && (allowRepeats ? true : !SoundHandler.isPlayingSound(npc, secondLoc))) {
            secondarySound = new AuraSound(aura, new SoundSource(secondLoc, npc));
            secondarySound.isGUIAura = true;
            secondarySound.setRepeat(true).play(false);
        }
        if (secondaryKettleLoc != null && (allowRepeats ? true : !SoundHandler.isPlayingSound(npc, secondaryKettleLoc))) {
            secondaryKettleSound = new AuraSound(aura, new SoundSource(secondaryKettleLoc, npc));
            secondaryKettleSound.isGUIAura = true;
            secondaryKettleSound.soundSource.fadeFactor *= 10;
            secondaryKettleSound.setRepeat(true).play(false);
        }
    }

    public void stopSound(AuraSound sound, boolean immediate) {
        if (sound != null) {
            if (immediate)
                sound.stop(false);
            else {
                sound.soundSource.fadeOut = true;
                sound.soundSource.fadeFactor = 0.075f;
            }
            sound = null;
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;
        if (button.id == 0) {
            save();
            String name = "New";
            while (data.containsKey(name))
                name += "_";
            Aura aura = new Aura(-1, name);
            DBCPacketHandler.Instance.sendToServer(new DBCSaveAura(aura.writeToNBT(), ""));
        } else if (button.id == 1) {
            if (data.containsKey(scrollAuras.getSelected())) {
                GuiYesNo guiyesno = new GuiYesNo(this, scrollAuras.getSelected(), StatCollector.translateToLocal("gui.delete"), 1);
                displayGuiScreen(guiyesno);
            }
        } else if (button.id == 2) {
            Aura aura = (Aura) this.aura.clone();
            while (data.containsKey(aura.name))
                aura.name += "_";
            DBCPacketHandler.Instance.sendToServer(new DBCSaveAura(aura.writeToNBT(), ""));
        }

        if (aura == null)
            return;

        if (button.id == 30) {
            setNormalSound = true;
            setSubGui(new GuiSoundSelection((getTextField(30).getText())));
        } else if (button.id == 301) {
            aura.display.auraSound = "jinryuudragonbc:DBC.aura";
            initGui();
        } else if (button.id == 31) {
            setNormalSound = false;
            setSubGui(new GuiSoundSelection((getTextField(31).getText())));
        } else if (button.id == 311) {
            aura.display.kaiokenSound = "";
            initGui();
        } else if (button.id == 3) {
            Minecraft.getMinecraft().displayGuiScreen(new SubGuiAuraDisplay(this));
        }
    }

    @Override
    public void setGuiData(NBTTagCompound compound) {
        int oldID = aura != null ? aura.id : -1;

        this.aura = new Aura();
        aura.readFromNBT(compound);
        setSelected(aura.name);

        if (aura.id != -1) {
            AuraController.getInstance().customAuras.replace(aura.id, aura);
            display = aura.display;
            visualDisplay.auraID = aura.id;
            visualDisplay.outlineID = display.outlineID;
            visualDisplay.auraOn = true;


            boolean sameID = aura.id == oldID;
            if (!sameID) {
                stopSound(auraSound, false);
                stopSound(kaiokenSound, false);
                stopSound(secondarySound, false);
                stopSound(kettleSound, false);
                stopSound(secondaryKettleSound, false);

                playSound(true);
            }
        }
        initGui();
    }

    public boolean isMouseOverRenderer(int x, int y) {
        return x >= guiLeft + 10 && x <= guiLeft + 10 + 200 && y >= guiTop + 6 && y <= guiTop + 6 + 204;
    }

    @Override
    public void drawScreen(int i, int j, float f) {
        if (isMouseOverRenderer(i, j)) {
            zoomed += Mouse.getDWheel() * 0.035f;
            if (zoomed > 100)
                zoomed = 100;
            if (zoomed < 10)
                zoomed = 10;
        }
        if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
            rotation -= Mouse.getDX() * 0.75f;
        }
        super.drawScreen(i, j, f);

        if (hasSubGui())
            return;

        int ticks = Minecraft.getMinecraft().thePlayer.ticksExisted;
        if (auraTicks != ticks) {
            renderAura = true;
        }

        SubGuiAuraDisplay.useGUIAura = true;
        if (ticks % 5 == 0 && renderAura && visualDisplay.auraID != -1) {
            EntityAura enhancedAura = visualDisplay.auraEntity;
            boolean kaioken = visualDisplay.isKaioken;
            if (revampedAura == 1) {
                if (enhancedAura == null) {
                    enhancedAura = new EntityAura(npc, aura).load(true).spawn();
                    enhancedAura.isInKaioken = kaioken;
                    enhancedAura.isGUIAura = true;
                } else {
                    if (ticks % 10 == 0) {
                        enhancedAura.load(true);
                        if (kaioken) {
                            EntityAura kaiokenAura = enhancedAura.children.get("Kaioken");
                            if (kaiokenAura != null)
                                kaiokenAura.loadKaioken();
                        }
                    }
                }
            } else {
                if (enhancedAura != null)
                    enhancedAura.despawn();

                if (kaioken && display.kaiokenOverrides) {
                    spawnKaiokenAura(aura, visualDisplay);
                } else {
                    spawnAura(npc, aura);
                    if (aura.hasSecondaryAura())
                        spawnAura(npc, aura.getSecondaryAur());
                    if (kaioken)
                        spawnKaiokenAura(aura, visualDisplay);
                }
            }
            auraTicks = ticks;
            renderAura = false;
        }

        GL11.glColor4f(1, 1, 1, 1);
        EntityLivingBase entity = this.npc;

        int l = guiLeft + 110;
        int i1 = guiTop + 187;

        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef(l, i1, 60F);

        GL11.glScalef(-zoomed, zoomed, zoomed);
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f7 = entity.rotationYawHead;
        float f5 = (float) (l) - i;
        float f6 = (float) (i1 - 50) - j;
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


        // Render Entity
        try {
            RenderManager.instance.renderEntityWithPosYaw(entity, 0.0, 0.0, 0.0, 0.0F, f);
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
    public void drawBackground() {
        super.drawBackground();
        int xPosGradient = guiLeft + 10;
        int yPosGradient = guiTop + 6;
        drawGradientRect(xPosGradient, yPosGradient, 200 + xPosGradient, 204 + yPosGradient, 0xc0101010, 0xd0101010);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        renderScreen();
    }

    private void renderScreen() {
    }

    @Override
    public void keyTyped(char c, int i) {
        super.keyTyped(c, i);

        if (getTextField(55) != null) {
            if (getTextField(55).isFocused()) {
                if (search.equals(getTextField(55).getText()))
                    return;
                search = getTextField(55).getText().toLowerCase();
                scrollAuras.resetScroll();
                scrollAuras.setList(getSearchList());
            }
        }
    }

    private List<String> getSearchList() {
        if (search.isEmpty()) {
            return new ArrayList<String>(this.data.keySet());
        }
        List<String> list = new ArrayList<String>();
        for (String name : this.data.keySet()) {
            if (name.toLowerCase().contains(search))
                list.add(name);
        }
        return list;
    }

    @Override
    public void setData(Vector<String> list, HashMap<String, Integer> data) {
        String name = scrollAuras.getSelected();
        this.data = data;
        scrollAuras.setList(getSearchList());

        if (name != null)
            scrollAuras.setSelected(name);
    }

    @Override
    public void setSelected(String selected) {
        this.selected = selected;
        scrollAuras.setSelected(selected);
        originalName = scrollAuras.getSelected();
    }

    @Override
    public void customScrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll.id == 0) {
            save();
            selected = scrollAuras.getSelected();
            originalName = scrollAuras.getSelected();
            if (selected != null && !selected.isEmpty()) {
                DBCPacketHandler.Instance.sendToServer(new DBCGetAura(data.get(selected)));
            }
        }
    }

    public void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        if (k == 0) {
            for (GuiMenuTopButton button : getMenu().topButtons) {
                if (button.mousePressed(mc, i, j)) {
                    stopSound(auraSound, false);
                    stopSound(kaiokenSound, false);
                    stopSound(secondarySound, false);
                    stopSound(kettleSound, false);
                    stopSound(secondaryKettleSound, false);
                }
            }
        }
    }

    @Override
    public void customScrollDoubleClicked(String selection, GuiCustomScroll scroll) {
        ICustomScrollListener.super.customScrollDoubleClicked(selection, scroll);
    }

    @Override
    public void save() {
        if (this.selected != null && this.data.containsKey(this.selected) && this.aura != null) {
            DBCPacketHandler.Instance.sendToServer(new DBCSaveAura(aura.writeToNBT(), originalName));
        }
    }

    public void close() {
        stopSound(auraSound, false);
        stopSound(kaiokenSound, false);
        stopSound(secondarySound, false);
        stopSound(kettleSound, false);
        stopSound(secondaryKettleSound, false);
        super.close();
    }

    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        if (subgui instanceof GuiSoundSelection) {
            GuiSoundSelection gss = (GuiSoundSelection) subgui;
            if (gss.selectedResource != null) {
                if (setNormalSound) {
                    getTextField(30).setText(gss.selectedResource.toString());
                    unFocused(getTextField(30));
                } else {
                    getTextField(31).setText(gss.selectedResource.toString());
                    unFocused(getTextField(31));
                }
                initGui();
            }
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        NoppesUtil.openGUI(player, this);
        if (!result)
            return;
        if (id == 1) {
            if (data.containsKey(scrollAuras.getSelected())) {
                DBCPacketHandler.Instance.sendToServer(new DBCRemoveAura(data.get(scrollAuras.getSelected())));
                scrollAuras.clear();
                aura = new Aura();

                visualDisplay.auraOn = false;
                visualDisplay.auraID = -1;
                visualDisplay.outlineID = -1;
                stopSound(auraSound, false);
                stopSound(kaiokenSound, false);
                stopSound(secondarySound, false);
                stopSound(kettleSound, false);
                stopSound(secondaryKettleSound, false);
                initGui();
            }
        }
    }

    @Override
    public void unFocused(GuiNpcTextField guiNpcTextField) {
        if (aura == null || aura.id == -1)
            return;
        if (guiNpcTextField.id == 13) {
            String name = guiNpcTextField.getText();
            if (!name.isEmpty() && !this.data.containsKey(name)) {
                String old = this.aura.name;
                this.data.remove(this.aura.name);
                this.aura.name = name;
                this.data.put(this.aura.name, this.aura.id);
                this.selected = name;
                this.scrollAuras.replace(old, this.aura.name);
            } else
                guiNpcTextField.setText(aura.name);
        }
        if (guiNpcTextField.id == 14) {
            String menuName = guiNpcTextField.getText();
            if (!menuName.isEmpty()) {
                aura.menuName = menuName.replaceAll("&", "§");
            }
        }
        if (guiNpcTextField.id == 30) {
            aura.display.auraSound = guiNpcTextField.getText();
            getButton(301).enabled = !aura.display.auraSound.equals("jinryuudragonbc:DBC.aura");
        }
        if (guiNpcTextField.id == 31) {
            aura.display.kaiokenSound = guiNpcTextField.getText();
            getButton(311).enabled = !aura.display.kaiokenSound.isEmpty();
        }
    }
}
