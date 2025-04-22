package kamkeel.npcdbc.client.gui.global.form.attributes;

import kamkeel.npcdbc.client.gui.global.form.SubGuiFormAttributes;
import kamkeel.npcdbc.data.form.FormAttributes;
import kamkeel.npcs.controllers.AttributeController;
import kamkeel.npcs.controllers.data.attribute.AttributeDefinition;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.*;

import java.util.List;
import java.util.stream.Collectors;

public class SubGuiAddFormAttribute extends SubGuiInterface implements ICustomScrollListener {
    private final SubGuiFormAttributes parent;
    private final FormAttributes         attrs;
    private GuiCustomScroll               scroll;
    private List<AttributeDefinition>     choices;
    private String                        selected;

    public SubGuiAddFormAttribute(SubGuiFormAttributes parent) {
        this.parent = parent;
        this.attrs  = parent.form.customAttributes;
        setBackground("menubg.png");
        xSize = 200;  ySize = 200;
        // build list of definitions not yet applied
        this.choices = AttributeController
            .getAllAttributes()
            .stream()
            // skip anything already applied…
            .filter(d -> !attrs.getAllAttributes().containsKey(d.getKey()))
            // …and skip any “magic” attributes
            .filter(d -> !d.getKey().toLowerCase().contains("magic"))
            .collect(Collectors.toList());
        this.closeOnEsc = true;
    }

    @Override
    public void initGui() {
        super.initGui();
        guiTop += 10;
        if (scroll==null) {
            scroll = new GuiCustomScroll(this,0);
            scroll.setSize(180, ySize-60);
        }
        scroll.guiLeft = guiLeft+10;
        scroll.guiTop  = guiTop+10;
        scroll.setUnsortedList(
            choices.stream().map(AttributeDefinition::getDisplayName).collect(Collectors.toList())
        );
        addScroll(scroll);
        addButton(new GuiNpcButton(1, guiLeft+10, guiTop+ySize-40, 80, 20, "gui.add"));
        addButton(new GuiNpcButton(2, guiLeft+100, guiTop+ySize-40, 80, 20, "gui.cancel"));
    }

    @Override
    public void customScrollClicked(int i, int mx, int my, GuiCustomScroll scroll) {
        selected = scroll.getSelected();
    }

    @Override
    public void actionPerformed(GuiButton btn) {
        if (btn.id==1 && selected!=null) {
            // lookup definition by displayName
            AttributeDefinition def = choices.stream()
                .filter(d->d.getDisplayName().equals(selected))
                .findFirst().get();
            attrs.setAttribute(def.getKey(), 1.0f);
            parent.initGui();
            close();
        }
        if (btn.id==2) {
            close();
        }
    }
}
