package kamkeel.npcdbc.client.gui.global.form.attributes;

import kamkeel.npcdbc.client.gui.global.form.SubGuiFormAttributes;
import kamkeel.npcdbc.data.form.FormAttributes;
import noppes.npcs.controllers.MagicController;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.*;

public class SubGuiEditFormMagic extends SubGuiInterface implements ITextfieldListener {
    private final SubGuiFormAttributes parent;
    private final String               tag;
    private final int                  magicId;
    private final FormAttributes       attrs;
    private GuiNpcTextField            tfValue;

    public SubGuiEditFormMagic(SubGuiFormAttributes parent, String tag, int magicId) {
        this.parent  = parent;
        this.tag     = tag;
        this.magicId = magicId;
        this.attrs   = parent.form.customAttributes;
        setBackground("menubg.png");
        this.xSize = 200;
        this.ySize = 80;
        this.closeOnEsc = true;
    }

    @Override
    public void initGui() {
        super.initGui();
        int midX = guiLeft + xSize/2;

        // title: magic name
        String name = MagicController.getInstance().getMagic(magicId).getDisplayName();
        this.addLabel(new GuiNpcLabel(0, "Edit "+ name, guiLeft + 10, guiTop + 8, 0xFFFFFF));

        // current value
        float cur = attrs.getMagicMap(tag).getOrDefault(magicId, 0f);
        tfValue = new GuiNpcTextField(1, this, guiLeft + 10, guiTop + 28, xSize - 20, 20, Float.toString(cur));
        tfValue.setMaxStringLength(16);
        tfValue.floatsOnly = true;
        this.addTextField(tfValue);

        // Done
        int bw = 50, bh = 20;
        this.addButton(new GuiNpcButton(0, midX - bw/2, guiTop + ySize - bh - 6, bw, bh, "Done"));
    }

    @Override
    public void actionPerformed(GuiButton btn) {
        if (btn.id == 0) {
            try {
                float v = tfValue.getFloat();
                // re‐apply under same tag & id
                attrs.applyMagicAttribute(tag, magicId, v);
            } catch (NumberFormatException e) {
                // ignore
            }
            parent.initGui();
            this.close();
        }
    }

    @Override
    public void unFocused(GuiNpcTextField textField) {
        // no‐op
    }
}
