package kamkeel.npcdbc.client.gui;

import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.model.GuiModelColor;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.data.ModelPartData;

import java.awt.*;
import java.awt.datatransfer.*;

import static JinRyuu.JRMCore.JRMCoreH.dnsHairG1toG2;

public class GuiModelDBC extends GuiModelInterface implements ClipboardOwner {

	private final GuiScreen parent;
    private final String[] arrRace = new String[]{"gui.no","Human", "Saiyan", "HalfSaiyan", "Namekian", "Arcosian", "Majin"};
    private final String[] arrHorns = new String[]{"gui.no","display.part.spike","display.part.spike2","display.part.long","display.part.ultimate","display.part.antenna"};
    private final String[] arrRaceEars = new String[]{"gui.no","Arco"};
    private final String[] arrBody = new String[]{"gui.no","display.part.backSpike"};
    private final String[] arrArm = new String[]{"gui.no","display.part.armSpikes",  "display.part.shoulder"};
    private DBCDisplay display;
    private int tab = 0;
    private int raceTab = 0;

    public GuiModelDBC(GuiScreen parent, EntityCustomNpc npc){
		super(npc);
		this.parent = parent;
		this.xOffset = 110;
        this.display = ((INPCDisplay) npc.display).getDBCDisplay();
	}

    @Override
    public void initGui() {
    	super.initGui();
		int y = guiTop + 5;
        addButton(new GuiNpcButtonYesNo(0, guiLeft + 64, y, 60, 20, display.enabled));
        addLabel(new GuiNpcLabel(0, "gui.enabled", guiLeft, y + 5, 0xFFFFFF));
        if(!display.enabled)
            return;

        addButton(new GuiNpcButton(50, guiLeft, y += 22, 60, 20, "display.parts"));
        getButton(50).enabled = tab != 0;
        addButton(new GuiNpcButton(51, guiLeft + 64, y, 60, 20, "display.race"));
        getButton(51).enabled = tab != 1;

        if(tab == 0){
            addButton(new GuiNpcButton(101, guiLeft + 40, y+=22, 60, 20, "gui.paste"));
            addButton(new GuiNpcButton(102, guiLeft + 101, y, 50, 20, "gui.copy"));
            addLabel(new GuiNpcLabel(100, "display.hair", guiLeft, y + 5, 0xFFFFFF));
            addButton(new GuiNpcButton(104, guiLeft + 101, y+=22, 50, 20, getColor(display.hairColor)));
            addButton(new GuiNpcButton(103, guiLeft + 40, y, 60, 20, "gui.clear"));
            getButton(104).packedFGColour = display.hairColor;

            ModelPartData dbcHorn = playerdata.getPartData("dbcHorn");
            addButton(new GuiNpcButton(2, guiLeft + 40, y+=22, 60, 20, arrHorns, dbcHorn == null ? 0 : dbcHorn.type));
            addLabel(new GuiNpcLabel(2, "part.horns", guiLeft, y + 5, 0xFFFFFF));
            if(dbcHorn != null){
                addButton(new GuiNpcButton(12, guiLeft + 101, y, 50, 20, dbcHorn.getColor()));
                getButton(12).enabled = !display.useSkin;
                getButton(12).packedFGColour = dbcHorn.color;
            }

            ModelPartData dbcEars = playerdata.getPartData("dbcEars");
            addButton(new GuiNpcButton(3, guiLeft + 40, y += 22, 60, 20, arrRaceEars, dbcEars == null ? 0 : dbcEars.type));
            addLabel(new GuiNpcLabel(3, "part.ears", guiLeft, y + 5, 0xFFFFFF));
            if(dbcEars != null){
                addButton(new GuiNpcButton(13, guiLeft + 101, y, 50, 20, dbcEars.getColor()));
                getButton(13).enabled = !display.useSkin;
                getButton(13).packedFGColour = dbcEars.color;
            }

            ModelPartData dbcBody = playerdata.getPartData("dbcBody");
            addButton(new GuiNpcButton(4, guiLeft + 40, y += 22, 60, 20, arrBody, dbcBody == null ? 0 : dbcBody.type));
            addLabel(new GuiNpcLabel(4, "model.body", guiLeft, y + 5, 0xFFFFFF));
            if(dbcBody != null){
                addButton(new GuiNpcButton(14, guiLeft + 101, y, 50, 20, dbcBody.getColor()));
                getButton(14).enabled = !display.useSkin;
                getButton(14).packedFGColour = dbcBody.color;
            }

            ModelPartData dbcArms = playerdata.getPartData("dbcArms");
            addButton(new GuiNpcButton(5, guiLeft + 40, y += 22, 60, 20, arrArm, dbcArms == null ? 0 : dbcArms.type));
            addLabel(new GuiNpcLabel(5, "model.arms", guiLeft, y + 5, 0xFFFFFF));
            if(dbcArms != null){
                addButton(new GuiNpcButton(15, guiLeft + 101, y, 50, 20, dbcArms.getColor()));
                getButton(15).enabled = !display.useSkin;
                getButton(15).packedFGColour = dbcArms.color;
            }
        } else {
            addButton(new GuiNpcButton(1, guiLeft + 64, y += 22, 60, 20, arrRace, display.race+1));
            addLabel(new GuiNpcLabel(1, "display.race", guiLeft, y + 5, 0xFFFFFF));
            if(display.race > -1){
                addButton(new GuiNpcButtonYesNo(304, guiLeft + 64, y+=22, 60, 20, display.useSkin));
                addLabel(new GuiNpcLabel(304, "display.skinOverride", guiLeft, y + 5, 0xFFFFFF));
                if(display.useSkin) {
                    addButton(new GuiNpcButton(52, guiLeft, y += 22, 60, 20, "display.color"));
                    getButton(52).enabled = raceTab != 0;
                    addButton(new GuiNpcButton(53, guiLeft + 64, y, 60, 20, "display.modify"));
                    getButton(53).enabled = raceTab != 1;

                    if(raceTab == 0){
                        addButton(new GuiNpcButton(305, guiLeft + 20, y+=22, 42, 20, getColor(display.eyeColor)));
                        addLabel(new GuiNpcLabel(305, "display.eye", guiLeft, y + 5, 0xFFFFFF));
                        getButton(305).packedFGColour = display.eyeColor;

                        addButton(new GuiNpcButton(320, guiLeft + 68, y, 76, 20, "display.setDefault"));

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

                        if(display.race == DBCRace.SAIYAN || display.race == DBCRace.HALFSAIYAN){
                            addButton(new GuiNpcButton(311, guiLeft + 20, y+=22, 42, 20, getColor(display.furColor)));
                            addLabel(new GuiNpcLabel(311, "FUR", guiLeft, y + 5, 0xFFFFFF));
                            getButton(311).packedFGColour = display.furColor;
                        }

                    } else {
                        if(display.race == DBCRace.ARCOSIAN){
                            addButton(new GuiButtonBiDirectional(201,guiLeft + 35, y+=22, 52, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7"}, display.arcoState));
                            addLabel(new GuiNpcLabel(201, "display.state", guiLeft, y + 5, 0xFFFFFF));
                            addButton(new GuiNpcButton(206, guiLeft + 94, y, 70, 20, new String[]{"display.maskOff", "display.maskOn"}, display.hasArcoMask ? 1 : 0));
                        }

                        String[] mouths = new String[]{"0", "1", "2", "3", "4"};
                        if(display.race == DBCRace.ARCOSIAN)
                            mouths = new String[]{"0", "1", "2", "3", "4", "5"};

                        String[] eyes = new String[]{"0", "1", "2", "3", "4", "5"};
                        if(display.race == DBCRace.ARCOSIAN)
                            eyes = new String[]{"0", "1"};
                        if(display.race == DBCRace.NAMEKIAN)
                            eyes = new String[]{"0", "1", "2"};

                        addButton(new GuiButtonBiDirectional(203,guiLeft + 35, y+=22, 52, 20, eyes, display.eyeType));
                        addLabel(new GuiNpcLabel(203, "display.eye", guiLeft, y + 5, 0xFFFFFF));


                        addButton(new GuiButtonBiDirectional(204,guiLeft + 35, y+=22, 52, 20, mouths, display.mouthType));
                        addLabel(new GuiNpcLabel(204, "display.mouth", guiLeft, y + 5, 0xFFFFFF));

                        addButton(new GuiButtonBiDirectional(202,guiLeft + 35, y+=22, 52, 20, new String[]{"0", "1", "2", "3", "4"}, display.noseType));
                        addLabel(new GuiNpcLabel(202, "display.nose", guiLeft, y + 5, 0xFFFFFF));

                        if(display.race == DBCRace.ARCOSIAN || display.race == DBCRace.NAMEKIAN){
                            addButton(new GuiButtonBiDirectional(200,guiLeft + 35, y+=22, 52, 20, new String[]{"0", "1", "2"}, display.bodyType));
                            addLabel(new GuiNpcLabel(200, "model.body", guiLeft, y + 5, 0xFFFFFF));
                        }

                        if(display.race == DBCRace.SAIYAN || display.race == DBCRace.HALFSAIYAN){
                            addButton(new GuiNpcButton(207, guiLeft + 2, y+=22, 90, 20, new String[]{"display.furOff", "display.furOn"}, display.hasFur ? 1 : 0));
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);
        GuiNpcButton button = (GuiNpcButton) btn;
        if(button.id == 50){
            tab = 0;
            initGui();
        }
        if(button.id == 51){
            tab = 1;
            initGui();
        }
        if(button.id == 52){
            raceTab = 0;
            initGui();
        }
        if(button.id == 53){
            raceTab = 1;
            initGui();
        }
        if(button.id == 0){
            display.enabled = button.getValue() == 1;
            if(!display.enabled){
                playerdata.removePart("dbcHorn");
                playerdata.removePart("dbcEars");
                playerdata.removePart("dbcBody");
                playerdata.removePart("dbcArms");
                playerdata.removePart("tail");
                display.hairCode = "";
                display.useSkin = false;
                display.arcoState = 0;
                display.bodyType = 0;
                display.noseType = 0;
                display.mouthType = 0;
                display.eyeType = 0;
            }
            initGui();
        }
        if(button.id == 1){
            int value = button.getValue();
            display.race = (byte) (value - 1);
            if(display.race == -1){
                display.useSkin = false;
            }
            display.arcoState = 0;
            display.bodyType = 0;
            display.noseType = 0;
            display.mouthType = 0;
            display.eyeType = 0;
            verifyRaceTail();
            initGui();
        }
        if(button.id == 101){
            String newDNSHair = getClipboardContents();
            if (isStringNumber(newDNSHair) && (newDNSHair.length() == 786 || newDNSHair.length() == 784 || newDNSHair.length() == 392)) {
                display.hairCode = dnsHairG1toG2(newDNSHair);
                initGui();
            }
        }
        if (button.id == 102) {
            setClipboardContents(display.hairCode);
        }
        if(button.id == 103){
            display.hairCode = "";
            display.hairColor = 0x0;
            initGui();
        }
        if(button.id == 104){
            this.mc.displayGuiScreen(new GuiDBCDisplayColor(this, playerdata, display, npc, 4));
        }
        if(button.id == 305){
            this.mc.displayGuiScreen(new GuiDBCDisplayColor(this, playerdata, display, npc, 5));
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
                if(button.getValue() > 0){
                    data.setTexture("tail/monkey1", value);
                    if(button.getValue() == 2)
                        data.texture = "npcdbc:textures/parts/shoulder.png";
                }

            }
            initGui();
        }
        if(button.id == 15){
            this.mc.displayGuiScreen(new GuiModelColor(this, playerdata.getPartData("dbcArms"), npc));
        }
        if(button.id == 300){
            this.mc.displayGuiScreen(new GuiDBCDisplayColor(this, playerdata, display, npc, 0));
        }
        if(button.id == 301){
            this.mc.displayGuiScreen(new GuiDBCDisplayColor(this, playerdata, display, npc, 1));
        }
        if(button.id == 302){
            this.mc.displayGuiScreen(new GuiDBCDisplayColor(this, playerdata, display, npc, 2));
        }
        if(button.id == 303){
            this.mc.displayGuiScreen(new GuiDBCDisplayColor(this, playerdata, display, npc, 3));
        }
        if(button.id == 311){
            this.mc.displayGuiScreen(new GuiDBCDisplayColor(this, playerdata, display, npc, 6));
        }
        if(button.id == 320){
            display.setDefaultColors();
            verifyRaceTail();
            initGui();
        }
        if(button.id == 304){
            // Set NPC to Steve Model
            display.useSkin = button.getValue() == 1;
            if(display.useSkin)
                npc.display.modelType = 0;
            verifyRaceTail();
            initGui();
        }
        if(button.id == 200){
            display.bodyType = button.getValue();
        }
        if(button.id == 201){
            display.arcoState = button.getValue();
        }
        if(button.id == 202){
            display.noseType = button.getValue();
        }
        if(button.id == 203){
            display.eyeType = button.getValue();
        }
        if(button.id == 204){
            display.mouthType = button.getValue();
        }
        if(button.id == 206){
            display.hasArcoMask = button.getValue() == 1;
        }
        if(button.id == 207){
            display.hasFur = button.getValue() == 1;
        }
    }

    @Override
    public void close(){
        this.mc.displayGuiScreen(parent);
    }

    public void setClipboardContents(String aString) {
        StringSelection stringSelection = new StringSelection(aString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, this);
    }

    public String getClipboardContents() {
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents((Object)null);
        boolean hasTransferableText = contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                result = (String)contents.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception ignored) {}
        }
        return result;
    }

    public String getColor(int input) {
        String str;
        for(str = Integer.toHexString(input); str.length() < 6; str = "0" + str) {}
        return str;
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {}

    public void verifyRaceTail(){
        if(display.useSkin && (display.race == DBCRace.SAIYAN || display.race == DBCRace.HALFSAIYAN  || display.race == DBCRace.ARCOSIAN)){
            ModelPartData tail = playerdata.getOrCreatePart("tail");
            tail.setTexture("tail/monkey1", 8);
            if(display.race == DBCRace.SAIYAN || display.race == DBCRace.HALFSAIYAN){
                tail.pattern = 0;
                tail.color = display.hairColor;
                if(display.furColor != -1){
                    tail.color = display.furColor;
                }
            }
            if(display.race == DBCRace.ARCOSIAN){
                tail.pattern = 2;
                tail.color = display.bodyC3;
            }
        } else {
            playerdata.removePart("tail");
        }
    }

    public static boolean isStringNumber(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isDigit(c))
                return false;
        }
        return true;
    }
}
