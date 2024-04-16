package kamkeel.npcdbc.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.player.inventory.GuiCNPCInventory;
import noppes.npcs.client.gui.util.GuiButtonNextPage;
import noppes.npcs.client.gui.util.IGuiData;
import noppes.npcs.constants.EnumPlayerPacket;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.PlayerFactionData;
import org.lwjgl.opengl.GL11;
import tconstruct.client.tabs.AbstractTab;

import java.util.ArrayList;

public class GuiDBC extends GuiCNPCInventory implements IGuiData {

	private final ResourceLocation resource = new ResourceLocation("customnpcs","textures/gui/standardbg.png");

	public GuiDBC() {
		super();
		xSize = 280;
		ySize = 180;
        this.drawDefaultBackground = false;
        title = "";
        NoppesUtilPlayer.sendData(EnumPlayerPacket.FactionsGet);
	}

	@Override
    public void initGui()
    {
		super.initGui();
    }

	@Override
    public void drawScreen(int i, int j, float f)
    {
    	drawDefaultBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(resource);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, 252, 195);
		drawTexturedModalRect(guiLeft + 252, guiTop, 188, 0, 67, 195);
        renderScreen();

        super.drawScreen(i, j, f);

    }

	private void renderScreen(){
        int size = 10;
    }

    @Override
	protected void actionPerformed(GuiButton guibutton){
        if(guibutton instanceof AbstractTab)
            return;

        if (guibutton.id >= 100 && guibutton.id <= 105) {
            super.actionPerformed(guibutton);
            return;
        }
    }

    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {}

    @Override
    public void keyTyped(char c, int i)
    {
        if (i == 1 || isInventoryKey(i))
        {
            close();
        }
    }
	@Override
	public void save() {}

	@Override
	public void setGuiData(NBTTagCompound compound) {}

}
