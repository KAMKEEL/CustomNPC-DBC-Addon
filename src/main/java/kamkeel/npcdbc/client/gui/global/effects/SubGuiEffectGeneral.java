package kamkeel.npcdbc.client.gui.global.effects;

import kamkeel.npcdbc.data.statuseffect.custom.CustomEffect;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiMenuTopButton;
import noppes.npcs.client.gui.util.SubGuiInterface;

import java.util.ArrayList;
import java.util.List;

public class SubGuiEffectGeneral extends SubGuiInterface {
    private final GuiNPCManageEffects parent;
    public CustomEffect effect;

    private List<GuiMenuTopButton> topButtons = new ArrayList<>();

    public SubGuiEffectGeneral(GuiNPCManageEffects parent, CustomEffect effect) {
        this.effect = effect;
        this.parent = parent;
        this.closeOnEsc = true;

        setBackground("menubg.png");
        xSize = 360;
        ySize = 216;
    }

    @Override
    public void initGui() {
        super.initGui();
        GuiMenuTopButton close = new GuiMenuTopButton(-5, guiLeft + xSize - 22, guiTop - 10, "X");

        GuiMenuTopButton general = new GuiMenuTopButton(-1, guiLeft + 4, guiTop - 10, "display.general");
        GuiMenuTopButton scripts = new GuiMenuTopButton(-2, general.xPosition + general.getWidth(), guiTop - 10, "script.scripts");

        close.active = false;
        general.active = true;
        scripts.active = false;

        addTopButton(close);
        addTopButton(general);
        addTopButton(scripts);

        guiTop += 7;
        int y = guiTop + 3;
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        int id = guibutton.id;

        if (id == -5) {
            close();
            return;
        }
        if (id == -2) {
            GuiEffectScript scriptGUI = new GuiEffectScript(parent, effect);
            scriptGUI.setWorldAndResolution(mc, width, height);
            scriptGUI.initGui();
            mc.currentScreen = scriptGUI;
        }
    }
}
