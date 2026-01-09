package kamkeel.npcdbc.client.gui.component;

import kamkeel.npcdbc.client.gui.global.form.SubGuiFormDisplay;
import kamkeel.npcdbc.data.form.FacePartData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormDisplay;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.client.gui.util.SubGuiInterface;

public class SubGuiFormFaceParts extends SubGuiInterface {
    public SubGuiFormDisplay parent;
    public Form form;
    public FormDisplay display;
    public FacePartData faceData;
    public int faceType = 0;

    public SubGuiFormFaceParts(SubGuiFormDisplay parent) {
        this.npc = parent.npc;
        this.parent = parent;
        this.form = parent.form;
        this.display = parent.display;
        this.faceData = parent.display.faceData;

        this.xSize = 176;
        this.ySize = 222;
        setBackground("smallbg.png");

        drawNpc = true;
        xOffsetNpc = 230;
        yOffsetNpc = 200;
        xOffsetButton = -43;
        yOffsetButton = 5;
        yMouseRange = 200;
        defaultZoom = zoom = 2.8f;
        maxZoom = 4;
        drawRenderButtons = true;
    }

    @Override
    public void initGui() {
        super.initGui();
        int y = guiTop + 27;
        String[] labelNames = new String[]{"Eyebrows", "Eye White", "Eye Left", "Eye Right", "Nose", "Mouth"};

        addButton(new GuiNpcButton(6, guiLeft + xSize + 3, guiTop, 20, 20, "X"));

        addLabel(new GuiNpcLabel(7, "Face Type", guiLeft + 8, y + 5));
        addButton(new GuiNpcButton(7, guiLeft + xSize - 50 - 8, y, 50, 20, new String[]{"1", "2", "3", "4", "5", "6", "All"}, faceType));
        y += 25;

        for (int i = 0; i < 6; i++) {
            int partId = FacePartData.Part.values()[i].ordinal();
            boolean isBerserk = (
                partId == FacePartData.Part.LeftEye.ordinal() ||
                partId == FacePartData.Part.RightEye.ordinal()
            ) && display.isBerserk;
            boolean isAllRemoved = faceData.disabled(6, partId);
            boolean isRemoved = faceData.disabled(faceType, partId);

            addLabel(new GuiNpcLabel(i, labelNames[i], guiLeft + 8, y + 5));
            addButton(new GuiNpcButton(i, guiLeft + xSize - 50 - 8, y, 50, 20, new String[]{"Enabled", "Disabled"}, isRemoved || isAllRemoved || isBerserk ? 1 : 0));
            getButton(i).setEnabled(!isBerserk && !(isAllRemoved && faceType != 6));

            y += 25;
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;

        if (button.id == 6) {
            close();
            return;
        }

        if (button.id == 7) {
            faceType = (faceType + 1) % 7;
            initGui();
            return;
        }

        faceData.toggle(button.id, faceType);
    }
}
