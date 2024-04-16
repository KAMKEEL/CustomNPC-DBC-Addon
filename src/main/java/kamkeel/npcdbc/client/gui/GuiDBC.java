package kamkeel.npcdbc.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.client.gui.player.inventory.GuiCNPCInventory;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.client.gui.util.IGuiData;
import noppes.npcs.constants.EnumPlayerPacket;
import org.lwjgl.opengl.GL11;
import tconstruct.client.tabs.AbstractTab;

public class GuiDBC extends GuiCNPCInventory implements IGuiData, ICustomScrollListener {
    private final ResourceLocation resource = new ResourceLocation("customnpcs", "textures/gui/standardbg.png");
    FormSelectionScroll formSelectionScroll;

    public GuiDBC() {
        super();
        xSize = 280;
        ySize = 180;
        this.drawDefaultBackground = false;
        title = "";
        NoppesUtilPlayer.sendData(EnumPlayerPacket.FactionsGet);
    }

    @Override
    public void initGui() {
        super.initGui();
        formSelectionScroll = new FormSelectionScroll(this, 1); //this handles everything inside of it


    }

    @Override
    public void drawScreen(int i, int j, float f) {
        drawDefaultBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(resource);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, 252, 195);
        drawTexturedModalRect(guiLeft + 252, guiTop, 188, 0, 67, 195);
        renderScreen();

        super.drawScreen(i, j, f);

    }

    private void renderScreen() {
        int size = 10;
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton instanceof AbstractTab)
            return;

        if (guibutton.id >= 100 && guibutton.id <= 105) {
            super.actionPerformed(guibutton);
            return;
        }

        formSelectionScroll.actionPerformed((GuiNpcButton) guibutton);


    }

    @Override
    public void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        formSelectionScroll.mouseClicked(i, j, k);
    }


    @Override
    public void keyTyped(char c, int i) {
        if (i == 1 || isInventoryKey(i))
            close();
        super.keyTyped(c, i);
        formSelectionScroll.keyTyped(c, i);

    }


    @Override
    public void setGuiData(NBTTagCompound compound) {
    }

    @Override
    public void customScrollClicked(int i, int j, int k, GuiCustomScroll guiCustomScroll) {
        if (guiCustomScroll.id == formSelectionScroll.id)
            formSelectionScroll.customScrollClicked(i, j, k);
    }

    @Override
    public void customScrollDoubleClicked(String selection, GuiCustomScroll scroll) {
    }
}
