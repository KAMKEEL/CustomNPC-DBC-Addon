package kamkeel.npcdbc.client.gui.component;

import kamkeel.npcdbc.client.gui.global.form.SubGuiFormDisplay;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.overlay.Overlay;
import kamkeel.npcdbc.data.overlay.OverlayChain;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.util.StatCollector;
import noppes.npcs.client.gui.SubGuiColorSelector;
import noppes.npcs.client.gui.select.GuiTextureSelection;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static kamkeel.npcdbc.data.overlay.Overlay.Type.ALL;


public class SubGuiOverlays extends SubGuiInterface implements ISubGuiListener, ITextfieldListener, GuiYesNoCallback {
    public SubGuiFormDisplay parent;
    public Form form;
    public static OverlayChain overlays;
    public int lastColorClicked = 0;
    public static int lastTextureClicked = 0;
    public static int overlayID;
    private static final int SHIFT = 1000;

    private GuiScrollWindow window;

    public SubGuiOverlays(SubGuiFormDisplay parent) {
        this.parent = parent;
        this.form = parent.form;
        overlays = form.display.overlays;
        this.npc = parent.npc;


        xSize = 420;
        ySize = 216;

        drawNpc = true;
        xOffsetNpc = 360;
        yOffsetNpc = 189;
        xOffsetButton = -43;
        yOffsetButton = 10;
        yMouseRange = 200;
        defaultZoom = zoom = 2.6f;
        maxZoom = 4;
        drawRenderButtons = true;

        setBackground("menubg.png");
    }

    @Override
    public void initGui() {
        super.initGui();
        int y = 5;
        guiTop += 7;

        int width = 300;
        int height = ySize - (y - guiTop) - 2;
        if (window == null) {
            window = new GuiScrollWindow(this, guiLeft, guiTop, width, height, 0);
        } else {
            window.initGui();
            window.xPos = guiLeft + 4;//parent.guiLeft;
            window.yPos = guiTop + 4;//= parent.guiTop;
            window.clipWidth = width + 5;
            window.clipHeight = ySize - 8;//parent.ySize;
            window.maxScrollY = 0;

        }

        addScrollableGui(3, window);
        addTopButton(new GuiMenuTopButton(0, guiLeft  + width + 98, guiTop - 17, "X"));

        y = 5;

        int buttonOneRowOneX = 32;
        for (int i = 0; i < overlays.overlays.size(); i++) {
            Overlay currentOverlay = get(i);
            GuiNpcButton button;
            GuiNpcTextField textField;


            window.addLabel(new GuiNpcLabel(id(1, i), "Overlay " + (i + 1) + ":", 5, y + 5, 0xffffff));
            button = new GuiNpcButton(id(5, i), 240, y, 50, 20, new String[]{"Disabled", "Enabled"},
                    currentOverlay.isEnabled() ? 1 : 0);
            window.addButton(button); // id 5

            y += 23;
            textField = new GuiNpcTextField(id(1, i), this, 5, y, 228, 20, ""); // id 1
            window.addTextField(textField);
                window.getTextField(id(1, i)).setText(currentOverlay.getTexture());


            button = new GuiNpcButton(id(1, i), 240, y, 50, 20, "form.select");
            window.addButton(button);  // id 1

            if (currentOverlay.isEnabled()) {
                button = new GuiNpcButton(id(11, i), 240, y += 23, 50, 20, "Scripts");
                window.addButton(button);  // id 1
            }

            button = new GuiNpcButton(id(10, i), 240, y += 23, 50, 20, "Delete");
            window.addButton(button); // id 9
            y -= 23;

            if (currentOverlay.isEnabled()) {
                int x = 5;
                window.addLabel(new GuiNpcLabel(id(8, i), "Type:", x, y + 5, 0xffffff));
                String[] names = Arrays.stream(Overlay.Type.values()).map(Enum::name).toArray(String[]::new);
                button = new GuiNpcButton(id(8, i), x += buttonOneRowOneX, y, 50, 20, names,
                        currentOverlay.getType().ordinal());
                window.addButton(button); // id 8

                window.addLabel(new GuiNpcLabel(id(4, i), "Glow:", x += 53, y + 5, 0xffffff));
                button = new GuiNpcButtonYesNo(id(4, i), x += buttonOneRowOneX, y, 50, 20, currentOverlay.isGlow());
                window.addButton(button); // id 4


                y += 23;
                x = 5;
                window.addLabel(new GuiNpcLabel(id(2, i), "Color:", x, y + 5, 0xffffff));

                names = Arrays.stream(Overlay.ColorType.values()).map(Enum::name).toArray(String[]::new);
                button = new GuiNpcButton(id(2, i), x += 32, y, 50, 20, names, currentOverlay.getColorType());
                window.addButton(button); // id 2

                if (currentOverlay.getColorType() == Overlay.ColorType.Custom.ordinal()) {
                    button = new GuiNpcButton(id(3, i), x += 53, y, 50, 20, getColor(currentOverlay.getColor()));
                    button.packedFGColour = currentOverlay.getColor();
                    window.addButton(button); // id 3
                }

                int tempY = currentOverlay.getColorType() == Overlay.ColorType.Custom.ordinal() ? y : y;

                window.addLabel(new GuiNpcLabel(id(9, i), "Alpha:", x += 53, tempY + 5, 0xffffff));
                textField = new GuiNpcTextField(id(9, i), this, x += 34, tempY, 47, 20, currentOverlay.getAlpha() + "");
                textField.setFloatsOnly();
                textField.setMinMaxDefaultFloat(0.0f, 1.0f, 1.0f);
                window.addTextField(textField); // id 1
            } else {
                y += 23;
            }


            y += 37;
        }

        if (overlays.overlays.size() >= 10) {
            addLabel(new GuiNpcLabel(1, "WARNING: Too many overlays may cause lag or file deletion!",parent.guiLeft+5, guiTop - 26, 0xFF0000));
        }

        window.addButton(new GuiNpcButton(1, 240, y, 50, 20, "Add"));
        y += 45; //Don't forget the extra 3 pixels after the +

        int thisY = y > window.clipHeight ? y += 23 : window.clipHeight;
        window.addLabel(new GuiNpcLabel(1171, "Disable Other Overlays", 5, thisY - 25, 0xFFFFFF));
        window.addButton(new GuiNpcButton(4, 155, thisY - 30, 100, 20, "Select Types"));

        /**
         * y is the TOTAL height of all elements, even outside the scroll window height
         */
        if (y > window.clipHeight) //120 - 100
            window.maxScrollY = y - window.clipHeight;
    }

    public String getColor(int input) {
        String str;
        for (str = Integer.toHexString(input); str.length() < 6; str = "0" + str) {
        }
        return str;
    }

    private int id(int button, int index) {
        return button * SHIFT + index;
    }

    private int extractButtonId(int id) {
        return id / SHIFT;
    }

    private int extractId(int id) {
        return id % SHIFT;
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        super.actionPerformed(guibutton);
        GuiNpcButton button = (GuiNpcButton) guibutton;
        int id = button.id;
        int buttonType = extractButtonId(id);
        overlayID = extractId(id);

        if (id == 0) {
            close();
            return;
        }

        if (id == 1) {
            overlays.add(ALL);
            initGui();
        }

        if (id == 4) {
            List<String> all = Arrays.stream(Overlay.Type.values()).map(Enum::name).collect(Collectors.toList());
            List<String> selected = form.display.disabledOverlayTypes.stream().map(Enum::name).collect(Collectors.toList());
            setSubGui(new SubGuiSelectList(all, selected, "All Types", "Selected Types"));
            initGui();
        }

        if (buttonType == 10) {
            Overlay foundOverlay = get(overlayID);
            if (foundOverlay.texture.isEmpty() || !foundOverlay.hasScript()) {
                deleteOverlay();
                initGui();
            } else {
                GuiYesNo guiyesno = new GuiYesNo(this, StatCollector.translateToLocal("gui.paste"),
                        StatCollector.translateToLocal("gui.sure"), 1);
                this.displayGuiScreen(guiyesno);
            }


        }
        
        if (buttonType == 1) {//buttons 1,2,3 are identical to clicked indices
            lastTextureClicked = overlayID;
        }

        if (buttonType == 1) {
            setSubGui(new GuiOverlaySelection(npc, window.getTextField(id).getText()));
        }

        if (buttonType == 2) {
            get(overlayID).colorType((get(overlayID).getColorType() + 1) % Overlay.ColorType.values().length);
            initGui();
        }

        if (buttonType == 3) {
            lastColorClicked = overlayID;
            setSubGui(new SubGuiColorSelector(get(overlayID).getColor()));
        }

        if (buttonType == 4) {
            get(overlayID).glow(button.getValue() == 1);
        }

        if (buttonType == 5) {
            get(overlayID).enabled(button.getValue() == 1);
            initGui();
        }

        if (buttonType == 8) {
            /*
                TODO fix this issue, right now it's crashing

                FIXED
             */

            Overlay old = get(overlayID);
            if (old != null)
                overlays.replaceOverlay(old, old.asType(button.getValue()));

            /*
            This bugs out with calling initGui() right after
            as method double fires (Eye -> Body -> Eye).
            Must be init in next tick.
             */
            update();

        }

        //Script
        if (buttonType == 11) 
            GuiJaninoScript.create(this.parent.parent, get(overlayID).createScript(), width, height);
        
    }

    public void confirmClicked(boolean flag, int i) {
        if (flag) {
            if (i == 1)
                deleteOverlay();
        }
        this.displayGuiScreen(this);
    }

    public void deleteOverlay() {
        Overlay o = get(overlayID);
        overlays.deleteOverlay(overlayID);

    }

    private boolean updateButtons;

    public void update() { //basically initGui just in the next tick
        updateButtons = true;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTick) {
        if (updateButtons) {
            updateButtons = false;
            initGui();
        }
        super.drawScreen(mouseX, mouseY, partialTick);
    }

    @Override
    public void subGuiClosed(SubGuiInterface sub) {
        if (sub instanceof SubGuiColorSelector) {
            int color = ((SubGuiColorSelector) sub).color;
            get(overlayID).color(color);
            initGui();
        } else if (sub instanceof SubGuiSelectList) {
            List<String> selected = ((SubGuiSelectList) sub).selected;
            form.display.disabledOverlayTypes = selected.stream().map(Overlay.Type::valueOf).collect(Collectors.toSet());
        }
    }

    private Overlay get(int id) {
        return overlays.get(id);
    }

    private void setTexture(String texture, boolean enable) {
        Overlay ov = get(overlayID);
        ov.texture(texture);
        if (enable)
            ov.enabled(true);
    }

    @Override
    public void unFocused(GuiNpcTextField textField) {
        int id = textField.id;
        int fieldType = extractButtonId(id);
        overlayID = extractId(id);
        Overlay ov = get(overlayID);
        String text = textField.getText();

        if (fieldType == 1) 
                ov.texture(text);

        if (fieldType == 9) {
            try {
                ov.alpha(Float.parseFloat(text));
            } catch (NumberFormatException ignored) {
                textField.setText("1");
            }
        }
    }

    private class GuiOverlaySelection extends GuiTextureSelection {
        public GuiOverlaySelection(EntityNPCInterface npc, String texture) {
            super(npc, texture);
            if (selectedResource == null)
                setLocation("npcdbc", "textures/");
            originalTexture = get(overlayID).texture;

            yOffsetNpc += 15;
            defaultZoom = zoom = 2.25f;
            setNPCSkin = false;
            drawDefaultBackground = true;
        }

        private String originalTexture;

        @Override
        protected void actionPerformed(GuiButton guibutton) {
            super.actionPerformed(guibutton);

            if (guibutton.id == 2 && selectedResource != null)
                /*
                Setting original to selected means tex change is confirmed (not just viewing it)
                 */
                originalTexture = selectedResource.toString();

            this.close();
            this.parent.initGui();
        }

        public void customScrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
            super.customScrollClicked(i, j, k, scroll);
            if (scroll.id == 1)
                setTexture(selectedResource.toString(), true);
        }

        public void customScrollDoubleClicked(String selection, GuiCustomScroll scroll) {
            if (scroll.id == 1) {
                originalTexture = selectedResource.toString();
                this.close();
                this.parent.initGui();
            } else
                super.customScrollDoubleClicked(selection, scroll);
        }

        public void close() {
            setTexture(originalTexture, false);
            super.close();
        }
    }
}
