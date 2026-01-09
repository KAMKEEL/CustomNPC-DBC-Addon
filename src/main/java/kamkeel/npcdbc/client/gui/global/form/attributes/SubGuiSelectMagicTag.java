package kamkeel.npcdbc.client.gui.global.form.attributes;

import kamkeel.npcdbc.client.gui.global.form.SubGuiFormAttributes;
import kamkeel.npcdbc.data.form.FormAttributes;
import kamkeel.npcs.CustomAttributes;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiCustomScroll;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.ICustomScrollListener;
import noppes.npcs.client.gui.util.SubGuiInterface;
import noppes.npcs.controllers.MagicController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SubGuiSelectMagicTag extends SubGuiInterface implements ICustomScrollListener {
    public final SubGuiFormAttributes parent;
    private final FormAttributes        attrs;
    private GuiCustomScroll              scroll;
    private String                       selectedTag;

    private static final List<String> TAGS = Arrays.asList(
        CustomAttributes.MAGIC_DAMAGE_KEY,
        CustomAttributes.MAGIC_BOOST_KEY,
        CustomAttributes.MAGIC_DEFENSE_KEY,
        CustomAttributes.MAGIC_RESISTANCE_KEY
    );

    public SubGuiSelectMagicTag(SubGuiFormAttributes parent) {
        this.parent = parent;
        this.attrs  = parent.form.customAttributes;
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
        // only tags not yet present at all, or that still have some magic left to add
        scroll.setUnsortedList(
            TAGS.stream()
                .filter(tag-> {
                    // how many already present under this tag?
                    int used = attrs.getMagicMap(tag).size();
                    // how many total magics exist?
                    int total = MagicController
                        .getInstance().magics.size();
                    return used < total;
                })
                .collect(Collectors.toList())
        );
        addScroll(scroll);
        addButton(new GuiNpcButton(1, guiLeft+10, guiTop+ySize-40, 80,20,"gui.select"));
        addButton(new GuiNpcButton(2, guiLeft+100, guiTop+ySize-40,80,20,"gui.cancel"));
    }

    @Override
    public void customScrollClicked(int i, int mx, int my, GuiCustomScroll s){
        selectedTag = s.getSelected();
    }

    @Override
    public void actionPerformed(GuiButton btn){
        if (btn.id==1 && selectedTag!=null){
            setSubGui(new SubGuiSelectMagicEntry(this, selectedTag));
        }
        if (btn.id==2){
            close();
        }
    }
}
