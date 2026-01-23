package kamkeel.npcdbc.client.gui.global.form.attributes;

import kamkeel.npcdbc.client.gui.global.form.SubGuiFormAttributes;
import kamkeel.npcdbc.data.form.FormAttributes;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.client.gui.util.SubGuiInterface;

public class SubGuiEditFormAttribute extends SubGuiInterface implements ITextfieldListener {
    private final SubGuiFormAttributes parent;
    private final String key;
    private final FormAttributes attrs;
    private GuiNpcTextField tfValue;

    public SubGuiEditFormAttribute(SubGuiFormAttributes parent, String key) {
        this.parent = parent;
        this.key = key;
        this.attrs = parent.form.customAttributes;
        // window size & styling
        setBackground("menubg.png");
        this.xSize = 200;
        this.ySize = 80;
        this.closeOnEsc = true;
    }

    @Override
    public void initGui() {
        super.initGui();
        int midX = guiLeft + xSize / 2;

        // title label
        this.addLabel(new GuiNpcLabel(0, "Edit “" + key + "”", guiLeft + 10, guiTop + 8, 0xFFFFFF));

        // current value
        float cur = attrs.getAllAttributes().getOrDefault(key, 0f);
        tfValue = new GuiNpcTextField(1, this, guiLeft + 10, guiTop + 28, xSize - 20, 20, Float.toString(cur));
        tfValue.setMaxStringLength(16);
        tfValue.floatsOnly = true;
        this.addTextField(tfValue);

        // Done button
        int bw = 50, bh = 20;
        this.addButton(new GuiNpcButton(0, midX - bw / 2, guiTop + ySize - bh - 6, bw, bh, "Done"));
    }

    @Override
    public void actionPerformed(GuiButton btn) {
        if (btn.id == 0) {
            // parse & save
            try {
                float v = tfValue.getFloat();
                attrs.setAttribute(key, v);
            } catch (NumberFormatException e) {
                // ignore invalid input
            }
            // refresh parent and close
            parent.initGui();
            this.close();
        }
    }

    @Override
    public void unFocused(GuiNpcTextField textField) {
        // no‐op; we only save on “Done”
    }
}
