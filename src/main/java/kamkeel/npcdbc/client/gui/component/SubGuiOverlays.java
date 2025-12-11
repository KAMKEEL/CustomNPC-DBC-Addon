package kamkeel.npcdbc.client.gui.component;

import kamkeel.npcdbc.client.gui.global.form.SubGuiFormDisplay;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.overlay.OverlayChain;
import kamkeel.npcdbc.data.overlay.Overlay.Face;
import kamkeel.npcdbc.data.overlay.Overlay;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import noppes.npcs.client.gui.SubGuiColorSelector;
import noppes.npcs.client.gui.select.GuiTextureSelection;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.ArrayList;
import java.util.Arrays;

import static kamkeel.npcdbc.data.overlay.Overlay.Type.*;

public class SubGuiOverlays extends SubGuiInterface implements ISubGuiListener, ITextfieldListener {
    public SubGuiFormDisplay parent;
    public Form form;
    public static OverlayChain overlays;
    public static ArrayList<Integer> selectedFaces = new ArrayList<>();
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

        if (overlays.enabled) {
            for (int i = 0; i < overlays.overlays.size(); i++) {
                selectedFaces.add(0);
            }
        } else {
            selectedFaces.add(0);
        }

        xSize = 300;

        drawNpc = true;
        xOffsetNpc = 300;
        yOffsetNpc = 200;
        xOffsetButton = -43;
        yOffsetButton = 10;
        yMouseRange = 200;
        defaultZoom = zoom = 2.9f;
        maxZoom = 4;
        drawRenderButtons = true;
    }

    @Override
    public void initGui() {
        super.initGui();
        int y = 5;

        int width = 300;
        int height = ySize - (y - guiTop) - 2;
        if (window == null) {
            window = new GuiScrollWindow(this, guiLeft, guiTop, width, height, 0);
        } else {
            window.initGui();
            window.xPos = parent.guiLeft;
            window.yPos = parent.guiTop;
            window.clipWidth = width - 20;
            window.clipHeight = parent.ySize;
            window.maxScrollY = 0;

        }

        addScrollableGui(3, window);
        addButton(new GuiNpcButton(2, guiLeft + 230, guiTop - 10, 20, 20, "X"));

        y = 10;

        for (int i = 0; i < overlays.overlays.size(); i++) {
            Overlay currentOverlay = get(i);
            Face faceOverlay = null;
            GuiNpcButton button;
            GuiNpcTextField textField;
            boolean isFace = currentOverlay.getType() == Face;

            if (isFace) {
                faceOverlay = (Face) currentOverlay;
            }

            window.addLabel(new GuiNpcLabel(id(1, i), "Overlay " + (i + 1) + ":", 5, y + 5, 0xffffff));
            textField = new GuiNpcTextField(id(1, i), this, 65, y, 150, 20, ""); // id 1
            window.addTextField(textField);

            if (isFace && faceOverlay.isMatchingPlayerFace()) {
                window.getTextField(id(1, i)).setText(faceOverlay.getTexture(selectedFaces.get(i)));
            } else {
                window.getTextField(id(1, i)).setText(currentOverlay.getTexture());
            }


            button = new GuiNpcButton(id(1, i), 217, y, 50, 20, "form.select");
            window.addButton(button);  // id 1

            y += 23;

            button = new GuiNpcButton(id(5, i), 5, y, 50, 20, new String[]
                {"Disabled", "Enabled"}, currentOverlay.isEnabled() ? 1 : 0);
            window.addButton(button); // id 5

            if (currentOverlay.isEnabled()) {
                window.addLabel(new GuiNpcLabel(id(2, i), "Color:", 58, y + 5, 0xffffff));

                String[] names = Arrays.stream(Overlay.ColorType.values()).map(Enum::name).toArray(String[]::new);
                button = new GuiNpcButton(id(2, i), 90, y, 50, 20, names, currentOverlay.getColorType());
                window.addButton(button); // id 2

                window.addLabel(new GuiNpcLabel(id(4, i), "Glow:", 143, y + 5, 0xffffff));
                button = new GuiNpcButtonYesNo(id(4, i), 170, y, 50, 20, currentOverlay.isGlow());
                window.addButton(button); // id 4
            }

            y += 23;

            if (currentOverlay.isEnabled()) {
                if (currentOverlay.getColorType() == Overlay.ColorType.Custom.ordinal()) {
                    button = new GuiNpcButton(id(3, i), 90, y, 50, 20, getColor(currentOverlay.getColor()));
                    button.packedFGColour = currentOverlay.getColor();
                    window.addButton(button); // id 3
                }

                if (isFace) {
                    window.addLabel(new GuiNpcLabel(id(6, i), "Match Face:", 143, y + 5, 0xffffff));
                    button = new GuiNpcButtonYesNo(id(6, i), 205, y, 50, 20, faceOverlay.isMatchingPlayerFace());
                    window.addButton(button); // id 6

                    if (faceOverlay.isMatchingPlayerFace()) {
                        button = new GuiNpcButton(id(7, i), 263, y, 20, 20, new String[]{
                            "1", "2", "3", "4", "5", "6"}, selectedFaces.get(i)); // id 7
                        window.addButton(button);
                    }
                }


                String[] names = Arrays.stream(Overlay.Type.values()).map(Enum::name).toArray(String[]::new);
                button = new GuiNpcButton(id(8, i), 5, y, 50, 20, names, currentOverlay.getType().ordinal());
                window.addButton(button); // id 8

                int tempY = currentOverlay.getColorType() == Overlay.ColorType.Custom.ordinal() ? y + 23 : y;

                window.addLabel(new GuiNpcLabel(id(9, i), "Alpha:", 58, tempY + 5, 0xffffff));
                textField = new GuiNpcTextField(id(9, i), this, 90, tempY, 30, 20, currentOverlay.getAlpha() + "");
                textField.setFloatsOnly();
                textField.setMinMaxDefaultFloat(0.0f, 1.0f, 1.0f);
                window.addTextField(textField); // id 1
            }

            y += 23;

            button = new GuiNpcButton(id(10, i), 5, y, 50, 20, "Delete");
            window.addButton(button); // id 9
            y += 35;
        }

        if (overlays.overlays.size() >= 10) {
            addLabel(new GuiNpcLabel(1, "WARNING: Too many overlays may cause lag or file deletion!",parent.guiLeft+5, guiTop - 26, 0xFF0000));
        }

        window.addButton(new GuiNpcButton(1, 8, y, 20, 20, "+"));
        y += 23; //Don't forget the extra 3 pixels after the +
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

    private void rebuildSelectedFaces() {
        selectedFaces.clear();

        if (overlays.enabled) {
            for (int i = 0; i < overlays.overlays.size(); i++) {
                if (overlays.overlays.get(i).getType() != Face)
                    continue;

                selectedFaces.add(0);
            }
        } else {
            selectedFaces.add(0);
        }
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        super.actionPerformed(guibutton);
        GuiNpcButton button = (GuiNpcButton) guibutton;
        int id = button.id;
        int buttonType = extractButtonId(id);
        overlayID = extractId(id);

        if (id == 1) {
            overlays.add(ALL);
            rebuildSelectedFaces();
            initGui();
        }

        if (buttonType == 10) {
            Overlay foundOverlay = get(overlayID);
            overlays.deleteOverlay(overlayID);

            if (foundOverlay.getType() == Face)
                selectedFaces.remove(overlayID);

            rebuildSelectedFaces();
            initGui();
        }

        if (id == 2) {
            close();
            return;
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

        if (buttonType == 6) {
            if (get(overlayID).getType() == Face) {
                ((Face) get(overlayID)).setMatchPlayerFace(button.getValue() == 1);
                initGui();
            }
        }

        if (buttonType == 7) {
            if (get(overlayID).getType() == Face) {
                selectedFaces.set(overlayID, (selectedFaces.get(overlayID) + 1) % 6);
                GuiTextField field = window.getTextField(id(1, overlayID));
                String texture = ((Face) get(overlayID)).getTexture(selectedFaces.get(overlayID));
                field.setText(texture);
                initGui();
            }
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
    public void subGuiClosed(SubGuiInterface subGuiInterface) {
        if (subGuiInterface instanceof SubGuiColorSelector) {
            int color = ((SubGuiColorSelector) subGuiInterface).color;
            get(overlayID).color(color);
            initGui();
        }
    }

    private Overlay get(int id) {
        return overlays.get(id);
    }

    private void setTexture(String texture, boolean enable) {
        Overlay ov = get(overlayID);
        int clickedTex = lastTextureClicked;

        if (ov.getType() == Face) {
            //TODO why -1 if not matching? the math max/min in the method still sets it to 0. also if -1, face.setTexture only sets faceTextures, not the main texture, is that fine?
            ((Face) ov).setTexture(texture, ((Face) ov).isMatchingPlayerFace() ? selectedFaces.get(clickedTex) : -1);
            ov.texture(texture);
        } else {
            ov.texture(texture);
        }

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

        if (fieldType == 1) {
            if (ov.getType() == Face) {
                ((Face) ov).setTexture(text, ((Face) ov).isMatchingPlayerFace() ? selectedFaces.get(overlayID) : -1);
            } else {
                ov.texture(text);
            }
        }

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
