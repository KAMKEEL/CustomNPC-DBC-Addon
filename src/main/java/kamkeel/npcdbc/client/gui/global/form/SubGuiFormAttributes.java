package kamkeel.npcdbc.client.gui.global.form;

import kamkeel.npcdbc.client.gui.global.form.attributes.SubGuiAddFormAttribute;
import kamkeel.npcdbc.client.gui.global.form.attributes.SubGuiEditFormAttribute;
import kamkeel.npcdbc.client.gui.global.form.attributes.SubGuiEditFormMagic;
import kamkeel.npcdbc.client.gui.global.form.attributes.SubGuiSelectMagicTag;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormAttributes;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.controllers.MagicController;

import java.util.*;

public class SubGuiFormAttributes extends SubGuiInterface implements ICustomScrollListener, ISubGuiListener {
    private static final int SCROLL_WIDTH        = 170;
    private static final int HEADER_HEIGHT       = 18;
    private static final int BUTTON_ROW_HEIGHT   = 24;

    public final Form           form;
    private final FormAttributes attributes;
    private final GuiNpcFormMenu menu;

    private GuiCustomScroll scrollAttrs, scrollMagic;
    private GuiNpcButton    btnApplyAttr, btnRemoveAttr, btnEditAttr;
    private GuiNpcButton    btnApplyMagic, btnRemoveMagic, btnEditMagic;

    public SubGuiFormAttributes(GuiNPCManageForms parent, Form form) {
        this.form       = form;
        this.attributes = form.customAttributes;
        this.menu       = new GuiNpcFormMenu(parent, this, -7, form);
        setBackground("menubg.png");
        xSize = 360;  ySize = 216;
    }

    @Override
    public void initGui() {
        super.initGui();
        guiTop += 7;
        menu.initGui(guiLeft, guiTop, xSize);

        // ─── Headers ───────────────────────────────────────
        addLabel(new GuiNpcLabel(100, "Attributes",       guiLeft +  4, guiTop + 6, CustomNpcResourceListener.DefaultTextColor));
        addLabel(new GuiNpcLabel(101, "Magic Attributes", guiLeft +186, guiTop + 6, CustomNpcResourceListener.DefaultTextColor));

        // leave 18px for header, and 22px at bottom for buttons
        int scrollHeight = ySize - HEADER_HEIGHT - 28;

        // ─── Non‑magic scroll ─────────────────────────────
        if (scrollAttrs == null) {
            scrollAttrs = new GuiCustomScroll(this, 0);
        }
        scrollAttrs.setSize(SCROLL_WIDTH, scrollHeight);
        scrollAttrs.guiLeft = guiLeft + 4;
        scrollAttrs.guiTop  = guiTop  + HEADER_HEIGHT;
        scrollAttrs.setUnsortedList(new ArrayList<>(attributes.getAllAttributes().keySet()));
        addScroll(scrollAttrs);

        // ─── Magic scroll ─────────────────────────────────
        if (scrollMagic == null) {
            scrollMagic = new GuiCustomScroll(this, 1);
        }
        scrollMagic.setSize(SCROLL_WIDTH, scrollHeight);
        scrollMagic.guiLeft = guiLeft + 186;
        scrollMagic.guiTop  = guiTop  + HEADER_HEIGHT;
        List<String> magList = new ArrayList<>();
        for (Map.Entry<String, Map<Integer, Float>> e : attributes.getAllMagic().entrySet()) {
            String tag = e.getKey();
            for (Integer mid : e.getValue().keySet()) {
                magList.add(tag + ": " +
                    MagicController.getInstance().getMagic(mid).getDisplayName());
            }
        }
        scrollMagic.setUnsortedList(magList);
        addScroll(scrollMagic);

        // ─── Buttons under each scroll ────────────────────
        int baseY = guiTop + ySize - 26;
        // Attributes
        addButton(btnApplyAttr  = new GuiNpcButton(10, guiLeft +   4, baseY, 50, 20, "Apply"));
        addButton(btnRemoveAttr = new GuiNpcButton(12, guiLeft +  60, baseY, 50, 20, "Remove"));
        addButton(btnEditAttr   = new GuiNpcButton(14, guiLeft + 116, baseY, 50, 20, "Edit"));
        btnRemoveAttr.enabled = btnEditAttr.enabled = false;

        // Magic
        addButton(btnApplyMagic  = new GuiNpcButton(11, guiLeft + 186, baseY, 50, 20, "Apply"));
        addButton(btnRemoveMagic = new GuiNpcButton(13, guiLeft + 242, baseY, 50, 20, "Remove"));
        addButton(btnEditMagic   = new GuiNpcButton(15, guiLeft + 298, baseY, 50, 20, "Edit"));
        btnRemoveMagic.enabled = btnEditMagic.enabled = false;
    }

    @Override
    public void customScrollClicked(int idx, int mx, int my, GuiCustomScroll scroll) {
        boolean isAttr = scroll == scrollAttrs;
        String sel = scroll.getSelected();
        if (isAttr) {
            btnRemoveAttr.enabled = sel != null;
            btnEditAttr.enabled   = sel != null;
        } else {
            btnRemoveMagic.enabled = sel != null;
            btnEditMagic.enabled   = sel != null;
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        switch(button.id) {
            case 10: // Apply Attr
                setSubGui(new SubGuiAddFormAttribute(this));
                break;

            case 12: { // Remove Attr
                String key = scrollAttrs.getSelected();
                if (key!=null) {
                    attributes.removeAttribute(key);
                    initGui();
                }
                scrollAttrs.selected = -1;
                break;
            }

            case 14: { // Edit Attr
                String key = scrollAttrs.getSelected();
                if (key!=null) {
                    setSubGui(new SubGuiEditFormAttribute(this, key));
                }
                break;
            }

            case 11: // Apply Magic
                setSubGui(new SubGuiSelectMagicTag(this));
                break;

            case 13: { // Remove Magic
                String sel = scrollMagic.getSelected();
                if (sel!=null) {
                    String[] parts = sel.split(":");
                    String tag  = parts[0].trim();
                    String name = parts[1].trim();
                    int id = MagicController.getInstance().magics.entrySet().stream()
                        .filter(e->e.getValue().getDisplayName().equals(name))
                        .map(Map.Entry::getKey)
                        .findFirst().orElse(-1);
                    if (id!=-1) attributes.removeMagicAttribute(tag, id);
                    initGui();
                }
                scrollMagic.selected = -1;
                break;
            }

            case 15: { // Edit Magic
                String sel = scrollMagic.getSelected();
                if (sel!=null) {
                    String[] parts = sel.split(":");
                    String tag  = parts[0].trim();
                    String name = parts[1].trim();
                    int id = MagicController.getInstance().magics.entrySet().stream()
                        .filter(e->e.getValue().getDisplayName().equals(name))
                        .map(Map.Entry::getKey)
                        .findFirst().orElse(-1);
                    if (id!=-1) {
                        setSubGui(new SubGuiEditFormMagic(this, tag, id));
                    }
                }
                break;
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        menu.mouseClicked(mouseX, mouseY, mouseButton);
    }


    @Override
    public void subGuiClosed(SubGuiInterface subgui) {
        initGui();
    }

    @Override
    public void drawScreen(int mx, int my, float pt) {
        super.drawScreen(mx,my,pt);
        menu.drawElements(fontRendererObj, mx, my, mc, pt);
    }

    // utility to compute remaining height for scroll
    private int fifty() { return 18 + 20 + 4; }

    public void save() {
        menu.save();
    }
}
