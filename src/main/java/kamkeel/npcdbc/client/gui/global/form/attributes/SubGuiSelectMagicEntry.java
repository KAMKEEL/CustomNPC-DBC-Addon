package kamkeel.npcdbc.client.gui.global.form.attributes;

import kamkeel.npcdbc.data.form.FormAttributes;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.client.gui.util.SubGuiInterface;
import noppes.npcs.controllers.MagicController;
import noppes.npcs.controllers.data.Magic;

import java.util.List;
import java.util.stream.Collectors;

public class SubGuiSelectMagicEntry extends SubGuiInterface implements ICustomScrollListener {
    private final SubGuiSelectMagicTag parent;
    private final FormAttributes       attrs;
    private final String               tag;
    private GuiCustomScroll             scroll;
    private String                      selectedName;

    public SubGuiSelectMagicEntry(SubGuiSelectMagicTag parent, String tag){
        this.parent = parent;
        this.attrs  = parent.parent.form.customAttributes;
        this.tag    = tag;
        setBackground("menubg.png");
        xSize = 200; ySize = 200;
        closeOnEsc = true;
    }

    @Override
    public void initGui(){
        super.initGui();
        guiTop += 10;
        if (scroll==null){
            scroll = new GuiCustomScroll(this,0);
            scroll.setSize(180,ySize-60);
        }
        scroll.guiLeft = guiLeft+10;
        scroll.guiTop  = guiTop+10;

        // list all magics not yet applied under this tag
        List<Magic> all = MagicController.getInstance().magics.values().stream().collect(Collectors.toList());
        List<Integer> usedIds = attrs.getMagicMap(tag).keySet().stream().collect(Collectors.toList());
        List<String> names = all.stream()
            .filter(m->!usedIds.contains(m.getId()))
            .map(Magic::getDisplayName)
            .collect(Collectors.toList());

        scroll.setUnsortedList(names);
        addScroll(scroll);
        addButton(new GuiNpcButton(1, guiLeft+10, guiTop+ySize-40,80,20,"gui.add"));
        addButton(new GuiNpcButton(2, guiLeft+100, guiTop+ySize-40,80,20,"gui.cancel"));
    }

    @Override
    public void customScrollClicked(int i, int mx, int my, GuiCustomScroll s){
        selectedName = s.getSelected();
    }

    @Override
    public void actionPerformed(GuiButton btn){
        if (btn.id==1 && selectedName!=null){
            // lookup Magic by name
            Magic m = MagicController.getInstance().magics.values().stream()
                .filter(x->x.getDisplayName().equals(selectedName))
                .findFirst().get();
            attrs.applyMagicAttribute(tag, m.getId(), 1.0f);
            parent.parent.initGui();
            close();
            parent.close();
        }
        if (btn.id==2){
            close();
            parent.close();
        }
    }
}
