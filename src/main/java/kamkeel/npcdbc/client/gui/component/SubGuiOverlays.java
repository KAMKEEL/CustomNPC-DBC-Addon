package kamkeel.npcdbc.client.gui.component;

import kamkeel.npcdbc.client.gui.global.form.SubGuiFormDisplay;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormOverlay;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.client.gui.SubGuiColorSelector;
import noppes.npcs.client.gui.select.GuiTextureSelection;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.entity.EntityNPCInterface;

import java.util.ArrayList;

public class SubGuiOverlays extends SubGuiModelInterface implements ISubGuiListener, ITextfieldListener {
    public SubGuiFormDisplay parent;
    public Form form;
    public static FormOverlay overlay;
    public static int mode;
    public static ArrayList<Integer> selectedFaces = new ArrayList<>();
    public int lastColorClicked = 0;
    public static int lastTextureClicked = 0;
    public static int overlayID;
    private static final int SHIFT = 1000;

    private GuiScrollWindow window;

    public SubGuiOverlays(SubGuiFormDisplay parent, int mode) {
        super(parent.npc);
        this.parent = parent;
        this.form = parent.form;
        overlay = form.display.overlays;
        SubGuiOverlays.mode = mode;
        this.npc = parent.npc;

        if (overlay.hasFaceOverlays) {
            for (int i = 0; i < overlay.faceOverlays.size(); i++) {
                selectedFaces.add(0);
            }
        } else {
            selectedFaces.add(0);
        }

        xSize = 300;

        //setBackground("menubg.png");
        drawXButton = false;
        xOffsetNpc = 110;
        yOffsetNpc = 30;
        xMouseRange = 50;
        yMouseRange=200;
        zoomed=85;
        maxZoom=150;
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
        addButton(new GuiNpcButton(2, guiLeft + 220, guiTop - 10, 20, 20, "X"));

        y = 10;

        if (mode == 0) {
            for (int i = 0; i < overlay.getBodies().size(); i++) {
                // maxScroll += 23 * i - (5 * i);

                window.addLabel(new GuiNpcLabel(id(1, i), "Overlay " + (i + 1) + ":", 5, y + 5, 0xffffff));
                window.addTextField(new GuiNpcTextField(id(1, i), this, 65, y, 150, 20, "")); // id 1
                window.getTextField(id(1, i)).setText(overlay.getBodyTexture(i));
                GuiNpcButton button;

                button = new GuiNpcButton(id(1, i), 217, y, 50, 20, "form.select");
                window.addButton(button);  // id 1

                y += 23;

                button = new GuiNpcButton(id(5, i), 5, y, 50, 20, new String[]
                    {"Disabled", "Enabled"}, overlay.getBody(i).isEnabled() ? 1 : 0);
                window.addButton(button); // id 5

                if (overlay.getBody(i).isEnabled()) {
                    window.addLabel(new GuiNpcLabel(id(2, i), "Color:", 58, y + 5, 0xffffff));
                    button = new GuiNpcButton(id(2, i), 90, y, 50, 20, new String[]{
                        "display.custom", "display.body", "display.eye", "display.hair", "display.fur"}, overlay.getBodyColorType(i));
                    window.addButton(button); // id 2

                    window.addLabel(new GuiNpcLabel(id(4, i), "Glow:", 143, y + 5, 0xffffff));
                    button = new GuiNpcButtonYesNo(id(4, i), 170, y, 50, 20, overlay.doesBodyGlow(i));
                    window.addButton(button); // id 4
                }

                y += 23;

                if (overlay.getBody(i).isEnabled()) {
                    if (overlay.getBodyColorType(i) == FormOverlay.ColorType.Custom.ordinal()) {
                        button = new GuiNpcButton(id(3, i), 90, y, 50, 20, getColor(overlay.getBodyColor(i)));
                        button.packedFGColour = overlay.getBodyColor(i);
                        window.addButton(button); // id 3
                    }
                }

                button = new GuiNpcButton(id(8, i), 5, y, 50, 20, "Delete");
                window.addButton(button); // id 8
                y += 35;
            }
        } else if (mode == 1) {
            for (int i = 0; i < overlay.getFaces().size(); i++) {

                window.addLabel(new GuiNpcLabel(id(1, i), "Face Overlay " + (i + 1) + ":", 5, y + 5, 0xffffff));
                window.addTextField(new GuiNpcTextField(id(1, i), this, 90, y, 150, 20, "")); // id 1
                if (overlay.isFaceMatchingPlayer(i)) {
                    window.getTextField(id(1, i)).setText(overlay.getFaceTexture(i, selectedFaces.get(i)));
                } else {
                    window.getTextField(id(1, i)).setText(overlay.getFaceTexture(i));
                }

                GuiNpcButton button;
                button = new GuiNpcButton(id(1, i), 243, y, 50, 20, "form.select");
                window.addButton(button); // id 1

                y += 23;

                button = new GuiNpcButton(id(5, i), 5, y, 50, 20, new String[]
                    {"Disabled", "Enabled"}, overlay.getFace(i).isEnabled() ? 1 : 0);
                window.addButton(button); // id 5

                if (overlay.getFace(i).isEnabled()) {
                    window.addLabel(new GuiNpcLabel(id(2, i), "Color:", 58, y + 5, 0xffffff));
                    button = new GuiNpcButton(id(2, i), 90, y, 50, 20, new String[]{
                        "display.custom", "display.body", "display.eye", "display.hair", "display.fur"}, overlay.getFaceColorType(i));
                    window.addButton(button); // id 2

                    window.addLabel(new GuiNpcLabel(id(4, i), "Glow:", 143, y + 5, 0xffffff));
                    button = new GuiNpcButtonYesNo(id(4, i), 170, y, 50, 20, overlay.doesFaceGlow(i));
                    window.addButton(button); // 4
                }

                y += 23;

                button = new GuiNpcButton(id(8, i), 5, y, 50, 20, "Delete");
                window.addButton(button); // id 8

                if (overlay.getFace(i).isEnabled()) {
                    if (overlay.getFaceColorType(i) == FormOverlay.ColorType.Custom.ordinal()) {
                        button = new GuiNpcButton(id(3, i), 90, y, 50, 20, getColor(overlay.getFaceColor(i)));
                        button.packedFGColour = overlay.getFaceColor(i);
                        window.addButton(button); // id 3
                    }

                    window.addLabel(new GuiNpcLabel(id(6, i), "Match Face:", 143, y + 5, 0xffffff));
                    button = new GuiNpcButtonYesNo(id(6, i), 205, y, 50, 20, overlay.isFaceMatchingPlayer(i));
                    window.addButton(button); // id 6

                    if (overlay.isFaceMatchingPlayer(i)) {
                        button = new GuiNpcButton(id(7, i), 263, y, 20, 20, new String[]{
                            "1", "2", "3", "4", "5", "6"}, selectedFaces.get(i)); // id 7
                        window.addButton(button);
                    }
                }

                y += 25;
            }
        }

        if ((mode == 0 ? overlay.bodyOverlays : overlay.faceOverlays).size() >= 10) {
            addLabel(new GuiNpcLabel(1, "WARNING: Too many overlays may cause lag or file deletion", guiLeft, guiTop - 26, 0xFF0000));
        }

        window.addButton(new GuiNpcButton(1, 8, y, 20, 20, "+"));
        y+=23; //Don't forget the extra 3 pixels after the +
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

        if (overlay.hasFaceOverlays) {
            for (int i = 0; i < overlay.faceOverlays.size(); i++) {
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
            if (mode == 0) {
                overlay.addBodyOverlay();
            } else if (mode == 1) {
                overlay.addFaceOverlay();
            }


            rebuildSelectedFaces();
            initGui();
        }

        if (buttonType == 8) {

            if (mode == 0) {
                overlay.deleteBodyOverlay(overlayID);
            } else if (mode == 1) {
                overlay.deleteFaceOverlay(overlayID);
                selectedFaces.remove(overlayID);
            }

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

        if (mode == 0) {
            if (buttonType == 1) {
                setSubGui(new GuiOverlaySelection(npc, window.getTextField(id).getText()));
            }

            if (buttonType == 2) {
                overlay.setBodyColorType(overlayID, (overlay.getBodyColorType(overlayID) + 1) % FormOverlay.ColorType.values().length);
                initGui();
            }

            if (buttonType == 3) {
                lastColorClicked = overlayID;
                setSubGui(new SubGuiColorSelector(overlay.getBodyColor(overlayID)));
            }

            if (buttonType == 4) {
                overlay.setFaceGlow(overlayID, button.getValue() == 1);
            }

            if (buttonType == 5) {
                overlay.getBody(overlayID).setEnabled(button.getValue() == 1);
                initGui();
            }
        } else if (mode == 1) {
            if (buttonType == 1) {
                setSubGui(new GuiOverlaySelection(npc, window.getTextField(id).getText()));
            }

            if (buttonType == 2) {
                overlay.setFaceColorType(overlayID, (overlay.getFaceColorType(overlayID) + 1) % FormOverlay.ColorType.values().length);
                initGui();
            }

            if (buttonType == 3) {
                lastColorClicked = overlayID;
                setSubGui(new SubGuiColorSelector(overlay.getFaceColor(overlayID)));
            }

            if (buttonType == 4) {
                overlay.setFaceGlow(overlayID, button.getValue() == 1);
            }

            if (buttonType == 5) {
                overlay.getFace(overlayID).setEnabled(button.getValue() == 1);
                initGui();
            }

            if (buttonType == 6) {
                overlay.setMatchPlayerFace(overlayID, button.getValue() == 1);
                initGui();
            }

            if (buttonType == 7) {
                selectedFaces.set(overlayID, (selectedFaces.get(overlayID) + 1) % 6);
                GuiTextField field = window.getTextField(id(1, overlayID));
                String texture = overlay.getFaceTexture(overlayID, selectedFaces.get(overlayID));
                field.setText(texture);
            }
        }
    }

    @Override
    public void subGuiClosed(SubGuiInterface subGuiInterface) {
        if (subGuiInterface instanceof SubGuiColorSelector) {
            int color = ((SubGuiColorSelector) subGuiInterface).color;

            if (mode == 0) {
                overlay.setBodyColor(overlayID, color);
            } else if (mode == 1) {
                overlay.setFaceColor(overlayID, color);
            }

            initGui();
        } else if (subGuiInterface instanceof GuiOverlaySelection) {
            GuiOverlaySelection gts = (GuiOverlaySelection) subGuiInterface;
            if (gts.selectedResource != null) {
                setOverlay(gts.selectedResource, false);
                initGui();
            }
        }
    }

    private static void setOverlay(ResourceLocation resource, boolean enable) {
        boolean isFace = mode == 1; //else is body

        int clickedTex = lastTextureClicked;
        if (isFace) {
            overlay.setFaceTexture(clickedTex, resource.toString(), overlay.isFaceMatchingPlayer(clickedTex) ? selectedFaces.get(clickedTex) : -1);
            if (enable)
                overlay.getFace(clickedTex).setEnabled(true);
        } else {
            overlay.setBodyTexture(clickedTex, resource.toString());
            if (enable)
                overlay.getBody(clickedTex).setEnabled(true);
        }
    }

    @Override
    public void unFocused(GuiNpcTextField textField) {
        int id = textField.id;
        overlayID = extractId(id);
        String text = textField.getText();

        if (mode == 0) {
            overlay.setBodyTexture(overlayID, text);
        } else if (mode == 1) {
            overlay.setFaceTexture(overlayID, text, overlay.isFaceMatchingPlayer(overlayID) ? selectedFaces.get(overlayID) : -1);
        }
    }

    private static class GuiOverlaySelection extends GuiTextureSelection {
        public GuiOverlaySelection(EntityNPCInterface npc, String texture) {
            super(npc, texture);
        }

        @Override
        protected void actionPerformed(GuiButton guibutton) {
            super.actionPerformed(guibutton);
            if (guibutton.id == 2) {
                //  this.npc.display.skinOverlayData.overlayList.put(0, new SkinOverlay(this.selectedResource.toString()));
            }

            this.npc.textureLocation = null;
            this.close();
            this.parent.initGui();
        }

        public void customScrollClicked(int i, int j, int k, GuiCustomScroll scroll) {
            super.customScrollClicked(i, j, k, scroll);

            if (scroll.id == 1) {
                // this.npc.display.skinOverlayData.overlayList.put(0, new SkinOverlay(this.selectedResource.toString()));
                setOverlay(selectedResource, true);
            }
        }
    }
}
