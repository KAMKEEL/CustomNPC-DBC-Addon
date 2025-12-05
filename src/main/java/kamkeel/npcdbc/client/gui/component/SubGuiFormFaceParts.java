package kamkeel.npcdbc.client.gui.component;

import kamkeel.npcdbc.client.gui.global.form.SubGuiFormDisplay;
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
    public FormDisplay.FaceData faceData;

    public SubGuiFormFaceParts(SubGuiFormDisplay parent) {
        super();
        this.parent = parent;
        this.form = parent.form;
        this.display = parent.display;
        this.faceData = parent.display.faceData;

        this.xSize = 176;
        this.ySize = 222;
        setBackground("smallbg.png");
    }

    @Override
    public void initGui() {
        super.initGui();
        int y = guiTop + 13;

        addButton(new GuiNpcButton(6, guiLeft + xSize + 3, guiTop, 20, 20, "X"));

        addLabel(new GuiNpcLabel(0, "Eyebrows", guiLeft + 8, y + 5));
        addButton(new GuiNpcButton(0, guiLeft + xSize - 50 - 8, y, 50, 20, new String[]{"Enabled", "Disabled"}, faceData.hasEyebrows() ? 1 : 0));
        y += 35;
        addLabel(new GuiNpcLabel(1, "Eye White", guiLeft + 8, y + 5));
        addButton(new GuiNpcButton(1, guiLeft + xSize - 50 - 8, y, 50, 20, new String[]{"Enabled", "Disabled"}, faceData.hasWhite() ? 1 : 0));
        y += 35;
        addLabel(new GuiNpcLabel(2, "Eye Left", guiLeft + 8, y + 5));
        addButton(new GuiNpcButton(2, guiLeft + xSize - 50 - 8, y, 50, 20, new String[]{"Enabled", "Disabled"}, faceData.hasLeftEye() || display.isBerserk ? 1 : 0));
        getButton(2).setEnabled(!display.isBerserk);
        y += 35;
        addLabel(new GuiNpcLabel(3, "Eye Right", guiLeft + 8, y + 5));
        addButton(new GuiNpcButton(3, guiLeft + xSize - 50 - 8, y, 50, 20, new String[]{"Enabled", "Disabled"}, faceData.hasRightEye() || display.isBerserk ? 1 : 0));
        getButton(3).setEnabled(!display.isBerserk);
        y += 35;
        addLabel(new GuiNpcLabel(4, "Nose", guiLeft + 8, y + 5));
        addButton(new GuiNpcButton(4, guiLeft + xSize - 50 - 8, y, 50, 20, new String[]{"Enabled", "Disabled"}, faceData.hasNose() ? 1 : 0));
        y += 35;
        addLabel(new GuiNpcLabel(5, "Mouth", guiLeft + 8, y + 5));
        addButton(new GuiNpcButton(5, guiLeft + xSize - 50 - 8, y, 50, 20, new String[]{"Enabled", "Disabled"}, faceData.hasMouth() ? 1 : 0));
        y += 35;
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton) guibutton;

        if (button.id == 6) {
            close();
            return;
        }

        faceData.toggleFacePart(button.id);
    }
}
