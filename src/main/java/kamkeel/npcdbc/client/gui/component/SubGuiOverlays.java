package kamkeel.npcdbc.client.gui.component;

import kamkeel.npcdbc.client.gui.global.form.SubGuiFormDisplay;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormOverlay;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.SubGuiColorSelector;
import noppes.npcs.client.gui.select.GuiTextureSelection;
import noppes.npcs.client.gui.util.*;
import noppes.npcs.controllers.data.SkinOverlay;
import noppes.npcs.entity.EntityNPCInterface;

public class SubGuiOverlays extends SubGuiInterface implements ISubGuiListener, ITextfieldListener  {
    public SubGuiFormDisplay parent;
    public Form form;
    public FormOverlay overlay;
    public int mode;
    public int[] selectedFaces = new int[]{0, 0, 0};
    public int lastColorClicked = 0;
    public int lastTextureClicked = 0;

    public SubGuiOverlays(SubGuiFormDisplay parent, int mode) {
        this.parent = parent;
        this.form = parent.form;
        this.overlay = form.overlays;
        this.mode = mode;
        this.npc = parent.npc;

        setBackground("menubg.png");
        xSize = 300;
    }

    @Override
    public void initGui() {
        super.initGui();

        int y;

        if (mode == 0) {
            y = guiTop + 26;
            for (int i = 0; i < overlay.getBodies().length; i++) {
                int id = i + 1;
                addLabel(new GuiNpcLabel(id, "Body Overlay " + id + ":", guiLeft + 8, y + 5));
                addTextField(new GuiNpcTextField(id, this, guiLeft + 90, y, 150, 20, "")); // id 1, 2 and 3
                getTextField(id).setText(overlay.getBodyTexture(i));
                GuiNpcButton button;

                button = new GuiNpcButton(id, guiLeft + 243, y, 50, 20, "form.select");
                addButton(button);

                y += 23;

                addLabel(new GuiNpcLabel(Integer.parseInt(id + "0"), "Color:", guiLeft + 58, y + 5));
                button = new GuiNpcButton(Integer.parseInt(id + "0"), guiLeft + 90, y, 50, 20, new String[]{
                    "display.custom", "display.body", "display.eye", "display.hair", "display.fur"}, overlay.getBodyColorType(i));
                addButton(button); // id 10, 20 and 30

                button = new GuiNpcButton(Integer.parseInt(id + "5"), guiLeft + 5, y, 50, 20, new String[]
                        {"Disabled", "Enabled"}, overlay.getBody(i).isEnabled() ? 1 : 0);
                addButton(button);

                if (overlay.getBodyColorType(i) == FormOverlay.ColorType.Custom.getId()) {
                    button = new GuiNpcButton(Integer.parseInt(id + "1"), guiLeft + 145, y, 50, 20, getColor(overlay.getBodyColor(i)));
                    button.packedFGColour = overlay.getBodyColor(i);
                    addButton(button); // id 11, 21 and 31
                }

                y += 35;
            }
        } else if (mode == 1) {
            y = guiTop + 5;
            for (int i = 0; i < overlay.getFaces().length; i++) {
                int id = i + 1;
                addLabel(new GuiNpcLabel(id, "Face Overlay " + id + ":", guiLeft + 8, y + 5));
                addTextField(new GuiNpcTextField(id, this, guiLeft + 90, y, 150, 20, "")); // id 1, 2 and 3
                if (overlay.isFaceMatchingPlayer(i)) {
                    getTextField(id).setText(overlay.getFaceTexture(i, selectedFaces[i]));
                } else {
                    getTextField(id).setText(overlay.getFaceTexture(i));
                }

                GuiNpcButton button;
                button = new GuiNpcButton(id, guiLeft + 243, y, 50, 20, "form.select");
                addButton(button);

                y += 23;

                addLabel(new GuiNpcLabel(Integer.parseInt(id + "1"), "Color:", guiLeft + 58, y + 5));
                button = new GuiNpcButton(Integer.parseInt(id + "0"), guiLeft + 90, y, 50, 20, new String[]{
                    "display.custom", "display.body", "display.eye", "display.hair", "display.fur"}, overlay.getFaceColorType(i));
                addButton(button); // id 10, 20 and 30

                addLabel(new GuiNpcLabel(Integer.parseInt(id + "2"), "Disable Face:", guiLeft + 143, y + 5));
                button = new GuiNpcButtonYesNo(Integer.parseInt(id + "2"), guiLeft + 210, y, 50, 20, overlay.isFaceDisabled(i));
                addButton(button); // id 12, 22 and 32

                button = new GuiNpcButton(Integer.parseInt(id + "5"), guiLeft + 5, y, 50, 20, new String[]
                        {"Disabled", "Enabled"}, overlay.getFace(i).isEnabled() ? 1 : 0);
                addButton(button);

                y += 23;

                if (overlay.getFaceColorType(i) == FormOverlay.ColorType.Custom.getId()) {
                    button = new GuiNpcButton(Integer.parseInt(id + "1"), guiLeft + 90, y, 50, 20, getColor(overlay.getFaceColor(i)));
                    button.packedFGColour = overlay.getFaceColor(i);
                    addButton(button); // id 11, 21 and 31
                }

                addLabel(new GuiNpcLabel(Integer.parseInt(id + "3"), "Match Face:", guiLeft + 143, y + 5));
                button = new GuiNpcButtonYesNo(Integer.parseInt(id + "3"), guiLeft + 210, y, 50, 20, overlay.isFaceMatchingPlayer(i));
                addButton(button); // id 13, 23 and 33

                if (overlay.isFaceMatchingPlayer(i)) {
                    button = new GuiNpcButton(Integer.parseInt(id + "4"), guiLeft + 263, y, 20, 20, new String[]{
                        "1", "2", "3", "4", "5", "6"}, selectedFaces[i]); // id 14, 24 and 34
                    addButton(button);
                }

                y += 25;
            }
        }


        addButton(new GuiNpcButton(4, guiLeft + 303, guiTop, 20, 20, "X"));
    }

    public String getColor(int input) {
        String str;
        for (str = Integer.toHexString(input); str.length() < 6; str = "0" + str) {
        }
        return str;
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) {
        super.actionPerformed(guibutton);
        GuiNpcButton button = (GuiNpcButton) guibutton;

        if (button.id == 4) {
            close();
            return;
        }

        if (mode == 0) {
            if (button.id == 1) {
                lastTextureClicked = 0;
                setSubGui(new GuiOverlaySelection(npc, getTextField(1).getText()));
            }
            if (button.id == 2) {
                lastTextureClicked = 1;
                setSubGui(new GuiOverlaySelection(npc, getTextField(2).getText()));
            }
            if (button.id == 3) {
                lastTextureClicked = 2;
                setSubGui(new GuiOverlaySelection(npc, getTextField(3).getText()));
            }

            if (button.id == 10) {
                overlay.setBodyColorType(0, (overlay.getBodyColorType(0) + 1) % FormOverlay.ColorType.values().length);
                initGui();
            }
            if (button.id == 20) {
                overlay.setBodyColorType(1, (overlay.getBodyColorType(1) + 1) % FormOverlay.ColorType.values().length);
                initGui();
            }
            if (button.id == 30) {
                overlay.setBodyColorType(2, (overlay.getBodyColorType(2) + 1) % FormOverlay.ColorType.values().length);
                initGui();
            }

            if (button.id == 11) {
                lastColorClicked = 0;
                setSubGui(new SubGuiColorSelector(overlay.getBodyColor(0)));
            }
            if (button.id == 21) {
                lastColorClicked = 1;
                setSubGui(new SubGuiColorSelector(overlay.getBodyColor(1)));
            }
            if (button.id == 31) {
                lastColorClicked = 2;
                setSubGui(new SubGuiColorSelector(overlay.getBodyColor(2)));
            }

            if (button.id == 15) {
                overlay.getBody(0).setEnabled(button.getValue() == 1);
            }
            if (button.id == 25) {
                overlay.getBody(1).setEnabled(button.getValue() == 1);
            }
            if (button.id == 35) {
                overlay.getBody(2).setEnabled(button.getValue() == 1);
            }
        } else if (mode == 1) {
            if (button.id == 1) {
                lastTextureClicked = 3;
                setSubGui(new GuiOverlaySelection(npc, getTextField(1).getText()));
            }
            if (button.id == 2) {
                lastTextureClicked = 4;
                setSubGui(new GuiOverlaySelection(npc, getTextField(2).getText()));
            }
            if (button.id == 3) {
                lastTextureClicked = 5;
                setSubGui(new GuiOverlaySelection(npc, getTextField(3).getText()));
            }

            if (button.id == 10) {
                overlay.setFaceColorType(0, (overlay.getFaceColorType(0) + 1) % FormOverlay.ColorType.values().length);
                initGui();
            }
            if (button.id == 20) {
                overlay.setFaceColorType(1, (overlay.getFaceColorType(1) + 1) % FormOverlay.ColorType.values().length);
                initGui();
            }
            if (button.id == 30) {
                overlay.setFaceColorType(2, (overlay.getFaceColorType(2) + 1) % FormOverlay.ColorType.values().length);
                initGui();
            }

            if (button.id == 11) {
                lastColorClicked = 3;
                setSubGui(new SubGuiColorSelector(overlay.getFaceColor(0)));
            }
            if (button.id == 21) {
                lastColorClicked = 4;
                setSubGui(new SubGuiColorSelector(overlay.getFaceColor(1)));
            }
            if (button.id == 31) {
                lastColorClicked = 5;
                setSubGui(new SubGuiColorSelector(overlay.getFaceColor(2)));
            }

            if (button.id == 12) {
                overlay.setDisableFace(0, button.getValue() == 1);
            }
            if (button.id == 22) {
                overlay.setDisableFace(1, button.getValue() == 1);
            }
            if (button.id == 32) {
                overlay.setDisableFace(2, button.getValue() == 1);
            }

            if (button.id == 13) {
                overlay.setMatchPlayerFace(0, button.getValue() == 1);
                initGui();
            }
            if (button.id == 23) {
                overlay.setMatchPlayerFace(1, button.getValue() == 1);
                initGui();
            }
            if (button.id == 33) {
                overlay.setMatchPlayerFace(2, button.getValue() == 1);
                initGui();
            }

            if (button.id == 14) {
                selectedFaces[0] = (selectedFaces[0] + 1) % 6;
                getTextField(1).setText(overlay.getFaceTexture(0, selectedFaces[0]));
            }
            if (button.id == 24) {
                selectedFaces[1] = (selectedFaces[1] + 1) % 6;
                getTextField(2).setText(overlay.getFaceTexture(1, selectedFaces[1]));
            }
            if (button.id == 34) {
                selectedFaces[2] = (selectedFaces[2] + 1) % 6;
                getTextField(3).setText(overlay.getFaceTexture(2, selectedFaces[2]));
            }

            if (button.id == 15) {
                overlay.getFace(0).setEnabled(button.getValue() == 1);
            }
            if (button.id == 25) {
                overlay.getFace(1).setEnabled(button.getValue() == 1);
            }
            if (button.id == 35) {
                overlay.getFace(2).setEnabled(button.getValue() == 1);
            }
        }
    }

    @Override
    public void close() {
        super.close();
        //if (form != null);
            //form.save();
    }

    @Override
    public void subGuiClosed(SubGuiInterface subGuiInterface) {
        if (subGuiInterface instanceof SubGuiColorSelector) {
            int color = ((SubGuiColorSelector) subGuiInterface).color;
            if (lastColorClicked == 0) {
                overlay.setBodyColor(0, color);
            } else if (lastColorClicked == 1) {
                overlay.setBodyColor(1, color);
            } else if (lastColorClicked == 2) {
                overlay.setBodyColor(2, color);
            } else if (lastColorClicked == 3) {
                overlay.setFaceColor(0, color);
            } else if (lastColorClicked == 4) {
                overlay.setFaceColor(1, color);
            } else if (lastColorClicked == 5) {
                overlay.setFaceColor(2, color);
            }

            initGui();
        } else if (subGuiInterface instanceof GuiOverlaySelection) {
            GuiOverlaySelection gts = (GuiOverlaySelection) subGuiInterface;
            if (gts.selectedResource != null) {
                if (lastTextureClicked == 0) {
                    overlay.setBodyTexture(0, gts.selectedResource.toString());
                } else if (lastTextureClicked == 1) {
                    overlay.setBodyTexture(1, gts.selectedResource.toString());
                } else if (lastTextureClicked == 2) {
                    overlay.setBodyTexture(2, gts.selectedResource.toString());
                } else if (lastTextureClicked == 3) {
                    if (overlay.isFaceMatchingPlayer(0)) {
                        overlay.setFaceTexture(0, gts.selectedResource.toString(), selectedFaces[0]);
                    } else {
                        overlay.setFaceTexture(0, gts.selectedResource.toString());
                    }
                } else if (lastTextureClicked == 4) {
                    if (overlay.isFaceMatchingPlayer(1)) {
                        overlay.setFaceTexture(1, gts.selectedResource.toString(), selectedFaces[1]);
                    } else {
                        overlay.setFaceTexture(1, gts.selectedResource.toString());
                    }
                } else if (lastTextureClicked == 5) {
                    if (overlay.isFaceMatchingPlayer(2)) {
                        overlay.setFaceTexture(2, gts.selectedResource.toString(), selectedFaces[2]);
                    } else {
                        overlay.setFaceTexture(2, gts.selectedResource.toString());
                    }
                }
                initGui();
            }
        }
    }

    @Override
    public void unFocused(GuiNpcTextField textField) {
        if (mode == 0) {
            String text = textField.getText();
            if (textField.id == 1) {
                overlay.setBodyTexture(0, text);
            }

            if (textField.id == 2) {
                overlay.setBodyTexture(1, text);
            }

            if (textField.id == 3) {
                overlay.setBodyTexture(2, text);
            }
        } else if (mode == 1) {
            String text = textField.getText();

            if (textField.id == 1) {
                if (overlay.isFaceMatchingPlayer(0)) {
                    overlay.setFaceTexture(0, text, selectedFaces[0]);
                } else {
                    overlay.setFaceTexture(0, text);
                }
            }

            if (textField.id == 2) {
                if (overlay.isFaceMatchingPlayer(1)) {
                    overlay.setFaceTexture(1, text, selectedFaces[1]);
                } else {
                    overlay.setFaceTexture(1, text);
                }
            }

            if (textField.id == 3) {
                if (overlay.isFaceMatchingPlayer(2)) {
                    overlay.setFaceTexture(2, text, selectedFaces[2]);
                } else {
                    overlay.setFaceTexture(2, text);
                }
            }
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
                this.npc.display.skinOverlayData.overlayList.put(0, new SkinOverlay(this.selectedResource.toString()));
            }

            this.npc.textureLocation = null;
            this.close();
            this.parent.initGui();
        }
    }
}
