package kamkeel.npcdbc.client.gui;

import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixin.INPCDisplay;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.model.GuiModelColor;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.data.ModelPartData;

public class GuiModelDBC extends GuiModelInterface {

	private final GuiScreen parent;
    private final String[] arrRace = new String[]{"gui.no","Human", "Saiyan", "HalfSaiyan", "Namekian", "Arcosian", "Majin"};
    private final String[] arrArcoHorns = new String[]{"gui.no","Spike","SpikeTwo","Long","Ultimate"};
    private final String[] arrRaceEars = new String[]{"gui.no","Arco"};
    private final String[] arrBody = new String[]{"gui.no","BSpike"};
    private final String[] arrArm = new String[]{"gui.no","ArmSpike",  "Shoulder"};
    private DBCDisplay display;

    public GuiModelDBC(GuiScreen parent, EntityCustomNpc npc){
		super(npc);
		this.parent = parent;
		this.xOffset = 110;
        this.display = ((INPCDisplay) npc.display).getDBCDisplay();
	}

    @Override
    public void initGui() {
    	super.initGui();
		int y = guiTop + 20;
        addButton(new GuiNpcButton(600, guiLeft + 40, y - 30, 60, 20, "Reload"));

        addLabel(new GuiNpcLabel(1, "Race", guiLeft, y + 5, 0xFFFFFF));
        addButton(new GuiNpcButton(1, guiLeft + 40, y, 60, 20, arrRace, display.race+1));

        ModelPartData arcoHorn = playerdata.getPartData("dbcHorn");
    	addButton(new GuiNpcButton(2, guiLeft + 40, y+=22, 60, 20, arrArcoHorns, arcoHorn == null ? 0 : arcoHorn.type));
		addLabel(new GuiNpcLabel(2, "Arco", guiLeft, y + 5, 0xFFFFFF));
		if(arcoHorn != null){
            addButton(new GuiNpcButton(12, guiLeft + 101, y, 40, 20, arcoHorn.getColor()));
            getButton(12).enabled = !display.useSkin;
        }

        ModelPartData dbcEars = playerdata.getPartData("dbcEars");
        addButton(new GuiNpcButton(3, guiLeft + 40, y += 22, 60, 20, arrRaceEars, dbcEars == null ? 0 : dbcEars.type));
        addLabel(new GuiNpcLabel(3, "Ears", guiLeft, y + 5, 0xFFFFFF));
        if(dbcEars != null){
            addButton(new GuiNpcButton(13, guiLeft + 101, y, 40, 20, dbcEars.getColor()));
            getButton(13).enabled = !display.useSkin;
        }

        ModelPartData dbcBody = playerdata.getPartData("dbcBody");
        addButton(new GuiNpcButton(4, guiLeft + 40, y += 22, 60, 20, arrBody, dbcBody == null ? 0 : dbcBody.type));
        addLabel(new GuiNpcLabel(4, "Body", guiLeft, y + 5, 0xFFFFFF));
        if(dbcBody != null){
            addButton(new GuiNpcButton(14, guiLeft + 101, y, 40, 20, dbcBody.getColor()));
            getButton(14).enabled = !display.useSkin;
        }

        ModelPartData dbcArms = playerdata.getPartData("dbcArms");
        addButton(new GuiNpcButton(5, guiLeft + 40, y += 22, 60, 20, arrArm, dbcArms == null ? 0 : dbcArms.type));
        addLabel(new GuiNpcLabel(5, "Arms", guiLeft, y + 5, 0xFFFFFF));
        if(dbcArms != null){
            addButton(new GuiNpcButton(15, guiLeft + 101, y, 40, 20, dbcArms.getColor()));
            getButton(15).enabled = !display.useSkin;
        }

        if(display.race > -1){
            addButton(new GuiNpcButtonYesNo(304, guiLeft + 40, y+=22, 60, 20, display.useSkin));
            addLabel(new GuiNpcLabel(304, "Skin", guiLeft, y + 5, 0xFFFFFF));
            if(display.useSkin) {
                if(display.race == DBCRace.ARCOSIAN || display.race == DBCRace.NAMEKIAN){
                    addButton(new GuiButtonBiDirectional(200,guiLeft + 40, y+=22, 52, 20, new String[]{"0", "1", "2"}, display.bodyType));
                    addLabel(new GuiNpcLabel(200, "Type", guiLeft, y + 5, 0xFFFFFF));

                    if(display.race == DBCRace.ARCOSIAN){
                        addButton(new GuiButtonBiDirectional(201,guiLeft + 145, y, 52, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7"}, display.arcoState));
                        addLabel(new GuiNpcLabel(201, "State", guiLeft + 105, y + 5, 0xFFFFFF));
                    }
                }
                addButton(new GuiNpcButton(300, guiLeft + 20, y+=22, 42, 20, getColor(display.bodyCM)));
                addLabel(new GuiNpcLabel(300, "CM", guiLeft, y + 5, 0xFFFFFF));
                getButton(300).packedFGColour = display.bodyCM;

                addButton(new GuiNpcButton(301, guiLeft + 101, y, 42, 20, getColor(display.bodyC1)));
                addLabel(new GuiNpcLabel(301, "C1", guiLeft + 80, y + 5, 0xFFFFFF));
                getButton(301).packedFGColour = display.bodyC1;

                addButton(new GuiNpcButton(302, guiLeft + 20, y+=22, 42, 20, getColor(display.bodyC2)));
                addLabel(new GuiNpcLabel(302, "C2", guiLeft, y + 5, 0xFFFFFF));
                getButton(302).packedFGColour = display.bodyC2;

                addButton(new GuiNpcButton(303, guiLeft + 101, y, 42, 20, getColor(display.bodyC3)));
                addLabel(new GuiNpcLabel(303, "C3", guiLeft + 80, y + 5, 0xFFFFFF));
                getButton(303).packedFGColour = display.bodyC3;
            }
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
                display.arcoState = 0;
                display.bodyType = 0;
            }
            if(display.race == -1){
                display.useSkin = false;
                display.arcoState = 0;
                display.bodyType = 0;
            }
            initGui();
        }
        if(button.id == 2){
            int value = button.getValue();
            if(value == 0)
                playerdata.removePart("dbcHorn");
            else{
                ModelPartData data = playerdata.getOrCreatePart("dbcHorn");
                if(button.getValue() > 0)
                    data.setTexture("tail/monkey1", value);
            }
            initGui();
        }
        if(button.id == 12){
            this.mc.displayGuiScreen(new GuiModelColor(this, playerdata.getPartData("dbcHorn"), npc));
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

        if(button.id == 4){
            int value = button.getValue();
            if(value == 0)
                playerdata.removePart("dbcBody");
            else{
                ModelPartData data = playerdata.getOrCreatePart("dbcBody");
                if(button.getValue() > 0)
                    data.setTexture("tail/monkey1", value);
            }
            initGui();
        }
        if(button.id == 14){
            this.mc.displayGuiScreen(new GuiModelColor(this, playerdata.getPartData("dbcBody"), npc));
        }

        if(button.id == 5){
            int value = button.getValue();
            if(value == 0)
                playerdata.removePart("dbcArms");
            else{
                ModelPartData data = playerdata.getOrCreatePart("dbcArms");
                if(button.getValue() > 0)
                    data.setTexture("tail/monkey1", value);
            }
            initGui();
        }
        if(button.id == 15){
            this.mc.displayGuiScreen(new GuiModelColor(this, playerdata.getPartData("dbcArms"), npc));
        }

        if(button.id == 300){
            this.mc.displayGuiScreen(new GuiDBCModelColor(this, display, npc, 0));
        }
        if(button.id == 301){
            this.mc.displayGuiScreen(new GuiDBCModelColor(this, display, npc, 1));
        }
        if(button.id == 302){
            this.mc.displayGuiScreen(new GuiDBCModelColor(this, display, npc, 2));
        }
        if(button.id == 303){
            this.mc.displayGuiScreen(new GuiDBCModelColor(this, display, npc, 3));
        }
        if(button.id == 304){
            display.useSkin = button.getValue() == 1;
            initGui();
        }
        if(button.id == 200){
            display.bodyType = button.getValue();
        }
        if(button.id == 201){
            display.arcoState = button.getValue();
        }
        if(button.id == 600){
            initGui();
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

    public String getColor(int input) {
        String str;
        for(str = Integer.toHexString(input); str.length() < 6; str = "0" + str) {}
        return str;
    }
}
