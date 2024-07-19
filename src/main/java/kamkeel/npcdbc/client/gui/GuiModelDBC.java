package kamkeel.npcdbc.client.gui;

import kamkeel.npcdbc.client.model.part.hair.DBCHair;
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
    private final String[] arrRace = new String[]{"gui.no","display.human", "display.saiyan", "display.halfsaiyan", "display.namekian", "display.arcosian", "display.majin"};
    private final String[] arrHorns = new String[]{"gui.no", "display.part.antenna", "display.part.firstformspikes", "display.part.secondformspikes", "display.part.thirdformbighead", "display.part.ultimatespikes"};
    private final String[] arrHair = new String[]{"display.base", "display.ssj", "display.ssj2", "display.ssj3", "display.ssj4", "display.oozaru", "display.raditz"};
    private final String[] arrRaceEars = new String[]{"gui.no", "display.part.arcoEars"};
    private final String[] arrBody = new String[]{"gui.no","display.part.backSpike"};
    private final String[] arrArm = new String[]{"gui.no","display.part.armSpikes",  "display.part.shoulder"};
    private DBCDisplay display;
    private int tab = 1;
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
            int index = getHairType();
            addButton(new GuiButtonBiDirectional(105, guiLeft + 47, y += 22, 100, 20, arrHair, index));
            getButton(104).packedFGColour = display.hairColor != 0 ? display.hairColor : 1;
            getButton(105).enabled = display.hairCode.length() == 786 || display.hairCode.length() == 784 || display.hairCode.length() == 392;

            y += 22;
            ModelPartData dbcHorn = playerdata.getPartData("dbcHorn");
            addButton(new GuiButtonBiDirectional(2, guiLeft + 47, y += 22, 100, 20, arrHorns, dbcHorn == null ? 0 : dbcHorn.type));
            addLabel(new GuiNpcLabel(2, "part.horns", guiLeft, y + 5, 0xFFFFFF));
            if (dbcHorn != null && !display.useSkin) {
                addButton(new GuiNpcButton(12, guiLeft + 151, y, 35, 20, dbcHorn.getColor()));
                getButton(12).packedFGColour = dbcHorn.color != 0 ? dbcHorn.color : 1;
            }

            ModelPartData dbcEars = playerdata.getPartData("dbcEars");
            addButton(new GuiButtonBiDirectional(3, guiLeft + 47, y += 22, 100, 20, arrRaceEars, dbcEars == null ? 0 : dbcEars.type));
            addLabel(new GuiNpcLabel(3, "part.ears", guiLeft, y + 5, 0xFFFFFF));
            if (dbcEars != null && !display.useSkin) {
                addButton(new GuiNpcButton(13, guiLeft + 151, y, 35, 20, dbcEars.getColor()));
                getButton(13).packedFGColour = dbcEars.color != 0 ? dbcEars.color : 1;
            }

            ModelPartData dbcBody = playerdata.getPartData("dbcBody");
            addButton(new GuiButtonBiDirectional(4, guiLeft + 47, y += 22, 100, 20, arrBody, dbcBody == null ? 0 : dbcBody.type));
            addLabel(new GuiNpcLabel(4, "model.body", guiLeft, y + 5, 0xFFFFFF));
            if (dbcBody != null && !display.useSkin) {
                addButton(new GuiNpcButton(14, guiLeft + 151, y, 35, 20, dbcBody.getColor()));
                getButton(14).packedFGColour = dbcBody.color != 0 ? dbcBody.color : 1;
            }

            ModelPartData dbcArms = playerdata.getPartData("dbcArms");
            addButton(new GuiButtonBiDirectional(5, guiLeft + 47, y += 22, 100, 20, arrArm, dbcArms == null ? 0 : dbcArms.type));
            addLabel(new GuiNpcLabel(5, "model.arms", guiLeft, y + 5, 0xFFFFFF));
            if (dbcArms != null && !display.useSkin) {
                addButton(new GuiNpcButton(15, guiLeft + 151, y, 35, 20, dbcArms.getColor()));
                getButton(15).packedFGColour = dbcArms.color != 0 ? dbcArms.color : 1;
            }
        } else {
            //  addButton(new GuiNpcButton(1, guiLeft + 64, y += 22, 60, 20, arrRace, display.race+1));
            addButton(new GuiButtonBiDirectional(1, guiLeft + 46, y += 22, 94, 20, arrRace, display.race + 1));
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
                        getButton(305).packedFGColour = display.eyeColor != 0 ? display.eyeColor : 1;

                        addButton(new GuiNpcButton(320, guiLeft + 68, y, 76, 20, "display.setDefault"));

                        addButton(new GuiNpcButton(300, guiLeft + 20, y+=22, 42, 20, getColor(display.bodyCM)));
                        addLabel(new GuiNpcLabel(300, "CM", guiLeft, y + 5, 0xFFFFFF));
                        getButton(300).packedFGColour = display.bodyCM != 0 ? display.bodyCM : 1;

                        addButton(new GuiNpcButton(301, guiLeft + 101, y, 42, 20, getColor(display.bodyC1)));
                        addLabel(new GuiNpcLabel(301, "C1", guiLeft + 80, y + 5, 0xFFFFFF));
                        getButton(301).packedFGColour = display.bodyC1 != 0 ? display.bodyC1 : 1;

                        addButton(new GuiNpcButton(302, guiLeft + 20, y+=22, 42, 20, getColor(display.bodyC2)));
                        addLabel(new GuiNpcLabel(302, "C2", guiLeft, y + 5, 0xFFFFFF));
                        getButton(302).packedFGColour = display.bodyC2 != 0 ? display.bodyC2 : 1;

                        addButton(new GuiNpcButton(303, guiLeft + 101, y, 42, 20, getColor(display.bodyC3)));
                        addLabel(new GuiNpcLabel(303, "C3", guiLeft + 80, y + 5, 0xFFFFFF));
                        getButton(303).packedFGColour = display.bodyC3 != 0 ? display.bodyC3 : 1;

                        if(display.race == DBCRace.SAIYAN || display.race == DBCRace.HALFSAIYAN){
                            addButton(new GuiNpcButton(311, guiLeft + 20, y+=22, 42, 20, getColor(display.furColor)));
                            addLabel(new GuiNpcLabel(311, "display.fur", guiLeft, y + 5, 0xFFFFFF));
                            getButton(311).packedFGColour = display.furColor != 0 ? display.furColor : 1;

                        }

                    } else {
                        if(display.race == DBCRace.ARCOSIAN){
                            addButton(new GuiButtonBiDirectional(201, guiLeft + 35, y += 22, 78, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7"}, display.arcoState));
                            addLabel(new GuiNpcLabel(201, "display.state", guiLeft, y + 5, 0xFFFFFF));
                        }

                        String[] mouths = new String[]{"0", "1", "2", "3", "4"};
                        if(display.race == DBCRace.ARCOSIAN)
                            mouths = new String[]{"0", "1", "2", "3", "4", "5"};

                        String[] eyes = new String[]{"0", "1", "2", "3", "4", "5"};
                        if(display.race == DBCRace.ARCOSIAN)
                            eyes = new String[]{"0", "1"};
                        if(display.race == DBCRace.NAMEKIAN)
                            eyes = new String[]{"0", "1", "2"};

                        addButton(new GuiButtonBiDirectional(203, guiLeft + 35, y += 22, 78, 20, eyes, display.eyeType));
                        addLabel(new GuiNpcLabel(203, "display.eye", guiLeft, y + 5, 0xFFFFFF));


                        addButton(new GuiButtonBiDirectional(204, guiLeft + 35, y += 22, 78, 20, mouths, display.mouthType));
                        addLabel(new GuiNpcLabel(204, "display.mouth", guiLeft, y + 5, 0xFFFFFF));

                        addButton(new GuiButtonBiDirectional(202, guiLeft + 35, y += 22, 78, 20, new String[]{"0", "1", "2", "3", "4"}, display.noseType));
                        addLabel(new GuiNpcLabel(202, "display.nose", guiLeft, y + 5, 0xFFFFFF));

                        if(display.race == DBCRace.ARCOSIAN || display.race == DBCRace.NAMEKIAN){
                            addButton(new GuiButtonBiDirectional(200, guiLeft + 35, y += 22, 78, 20, new String[]{"0", "1", "2"}, display.bodyType));
                            addLabel(new GuiNpcLabel(200, "model.body", guiLeft, y + 5, 0xFFFFFF));
                            if (display.race == DBCRace.ARCOSIAN)
                                addButton(new GuiNpcButton(206, guiLeft + 2, y += 22, 90, 20, new String[]{"display.maskOff", "display.maskOn"}, display.hasArcoMask ? 1 : 0));

                        }

                        if(display.race == DBCRace.SAIYAN || display.race == DBCRace.HALFSAIYAN){
                            addButton(new GuiButtonBiDirectional(208, this.guiLeft + 35, y += 22, 78, 20, new String[]{"display.normalTail", "display.wrappedTail", "display.noTail"}, display.tailState));
                            addLabel(new GuiNpcLabel(208, "display.tail", guiLeft, y + 5, 0xFFFFFF));

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
            if(!display.enabled)
                playerdata.removePart("tail");
             else if (DBCRace.isSaiyan(display.race) || display.race == DBCRace.ARCOSIAN)
                verifyRaceTail();

            initGui();
        }
        if(button.id == 1){
            int value = button.getValue();
            display.race = (byte) (value - 1);
            display.useSkin = display.race != -1;
            verifyRaceTail();
            initGui();
        }
        if(button.id == 101){
            String newDNSHair = getClipboardContents();
            if (isStringNumber(newDNSHair) && (newDNSHair.length() == 786 || newDNSHair.length() == 784 || newDNSHair.length() == 392)) {
                display.hairCode = dnsHairG1toG2(newDNSHair);
                initGui();
            } else if (newDNSHair.equalsIgnoreCase("bald")) {
                display.hairCode = "bald";
                initGui();
            }
        }
        if (button.id == 102) {
            setClipboardContents(display.hairCode);
        }
        if(button.id == 103){
            display.hairColor = 0x0;
            if (display.race < 3)
                display.hairCode = DBCHair.GOKU_HAIR;
            else if (display.race < 5)
                display.hairCode = "";
            else if (display.race == DBCRace.MAJIN) {
                display.hairCode = DBCHair.MAJIN_HAIR;
                display.hairColor = display.bodyCM;
            }

            display.hairType = "base";
            initGui();
        }
        if(button.id == 104){
            this.mc.displayGuiScreen(new GuiDBCDisplayColor(this, playerdata, display, npc, 4));
        }
        if (button.id == 105) {
            display.hairType = getHairString(button.getValue());
            initGui();
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
        if (button.id == 208) {
            int value = button.getValue();
            ModelPartData data = this.playerdata.getOrCreatePart("tail");
            if (value == 2)
                this.playerdata.removePart("tail");
            else
                data.type = (byte) (8);
            display.tailState = data.pattern = (byte) value;
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
            if (display.race == DBCRace.SAIYAN || display.race == DBCRace.HALFSAIYAN)
                tail.pattern = display.tailState < 2 ? display.tailState : 0;
            else if (display.race == DBCRace.ARCOSIAN)
                tail.pattern = 2;

        } else {
            playerdata.removePart("tail");
        }
    }

    private int getHairType() {
        int index = 0;
        //  "base", "ssj", "ssj2", "ssj3", "ssj4", "oozaru"
        if (!display.hairType.isEmpty()) {
            if (display.hairType.toLowerCase().contains("base"))
                index = 0;
            else if (display.hairType.equalsIgnoreCase("ssj"))
                index = 1;
            else if (display.hairType.toLowerCase().contains("ssj2"))
                index = 2;
            else if (display.hairType.toLowerCase().contains("ssj3"))
                index = 3;
            else if (display.hairType.toLowerCase().contains("ssj4"))
                index = 4;
            else if (display.hairType.toLowerCase().contains("oozaru"))
                index = 5;
            else if (display.hairType.toLowerCase().contains("raditz"))
                index = 6;
        }
        return index;
    }

    private String getHairString(int i) {
        switch (i) {
            case 0:
                return "base";
            case 1:
                return "ssj";
            case 2:
                return "ssj2";
            case 3:
                return "ssj3";
            case 4:
                return "ssj4";
            case 5:
                return "oozaru";
            case 6:
                return "raditz";
            default:
                return "";
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
