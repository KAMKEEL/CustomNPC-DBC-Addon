package kamkeel.npcdbc.client.gui;

import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixin.INPCDisplay;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.model.GuiModelColor;
import noppes.npcs.client.gui.util.GuiModelInterface;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.data.ModelPartData;

public class GuiModelDBC extends GuiModelInterface {

	private final GuiScreen parent;
    private static final String[] arrRace = new String[]{"gui.no","Human", "Saiyan", "HalfSaiyan", "Namekian", "Arcosian", "Majin"};
    private static final String[] arrArcoHorns = new String[]{"gui.no","Spike","SpikeTwo","Long","Ultimate"};
    private static final String[] arrRaceEars = new String[]{"gui.no","Arco"};
    private DBCDisplay display;

    public GuiModelDBC(GuiScreen parent, EntityCustomNpc npc){
		super(npc);
		this.parent = parent;
		this.xOffset = 90;
        this.display = ((INPCDisplay) npc.display).getDBCDisplay();
	}

    @Override
    public void initGui() {
    	super.initGui();
		int y = guiTop + 20;
        addLabel(new GuiNpcLabel(1, "Race", guiLeft, y + 5, 0xFFFFFF));
        addButton(new GuiNpcButton(1, guiLeft + 40, y, 60, 20, arrRace, display.race+1));

        ModelPartData arcoHorn = playerdata.getPartData("arcoHorn");
    	addButton(new GuiNpcButton(2, guiLeft + 40, y+=22, 60, 20, arrArcoHorns, arcoHorn == null ? 0 : arcoHorn.type));
		addLabel(new GuiNpcLabel(2, "Arco", guiLeft, y + 5, 0xFFFFFF));
		if(arcoHorn != null){
            addButton(new GuiNpcButton(12, guiLeft + 101, y, 40, 20, arcoHorn.getColor()));
            getButton(12).enabled = display.race > -1;
        }

        ModelPartData dbcEars = playerdata.getPartData("dbcEars");
        addButton(new GuiNpcButton(3, guiLeft + 40, y += 22, 60, 20, arrRaceEars, dbcEars == null ? 0 : dbcEars.type));
        addLabel(new GuiNpcLabel(3, "Ears", guiLeft, y + 5, 0xFFFFFF));
        if(dbcEars != null){
            addButton(new GuiNpcButton(13, guiLeft + 122, y, 40, 20, dbcEars.getColor()));
            getButton(13).enabled = display.race > -1;
        }
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);
        GuiNpcButton button = (GuiNpcButton) btn;
        if(button.id == 1){
            int value = button.getValue();
            display.race = value - 1;
            if(display.race > -1){
                int bodyCM = display.bodyCM == -1 ? getDefaultColor("bodyCM", display.race) : display.bodyCM;
                int bodyC1 = display.bodyC1 == -1 ? getDefaultColor("bodyC1", display.race) : display.bodyC1;
                int bodyC2 = display.bodyC2 == -1 ? getDefaultColor("bodyC2", display.race) : display.bodyC2;
                int bodyC3 = display.bodyC3 == -1 ? getDefaultColor("bodyC3", display.race) : display.bodyC3;



            }
            initGui();
        }
        if(button.id == 2){
            int value = button.getValue();
            if(value == 0)
                playerdata.removePart("arcoHorn");
            else{
                ModelPartData data = playerdata.getOrCreatePart("arcoHorn");
                if(button.getValue() > 0)
                    data.setTexture("tail/monkey1", value);
            }
            initGui();
        }
        if(button.id == 12){
            this.mc.displayGuiScreen(new GuiModelColor(this, playerdata.getPartData("arcoHorn"), npc));
        }


        if(button.id == 3){
            int value = button.getValue();
            if(value == 0)
                playerdata.removePart("dbcEars");
            else{
                ModelPartData data = playerdata.getOrCreatePart("dbcEars");
                if(button.getValue() > 0)
                    data.setTexture("tail/monkey1", value);
            }
            initGui();
        }
        if(button.id == 13){
            this.mc.displayGuiScreen(new GuiModelColor(this, playerdata.getPartData("dbcEars"), npc));
        }
    }

    @Override
    public void close(){
        this.mc.displayGuiScreen(parent);
    }

    public int getDefaultColor(String type, int race) {
        if (race < 3) {
            if (type.equalsIgnoreCase("bodycm"))
                return 16297621;
        } else if (race == 3) {
            if (type.equalsIgnoreCase("bodycm"))
                return 5095183;
            else if (type.equalsIgnoreCase("bodyc1"))
                return 13796998;
            else if (type.equalsIgnoreCase("bodyc2"))
                return 12854822;
        } else if (race == 4) {
            if (type.equalsIgnoreCase("bodycm"))
                return 15460342;
            else if (type.equalsIgnoreCase("bodyc1"))
                return 16111595;
            else if (type.equalsIgnoreCase("bodyc2"))
                return 8533141;
            else if (type.equalsIgnoreCase("bodyc3"))
                return 16550015;
        } else if (race == 5)
            if (type.equalsIgnoreCase("bodycm"))
                return 16757199;
        return 0;
    }
}
