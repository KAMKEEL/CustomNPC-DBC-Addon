package kamkeel.npcdbc.client.gui.component;

import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormOverlay;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.SubGuiColorSelector;
import noppes.npcs.client.gui.util.*;

public class SubGuiOverlays extends SubGuiInterface implements ISubGuiListener, ITextfieldListener  {
    public Form form;
    public FormOverlay overlay;
    public int mode;
    public int[] selectedFaces = new int[]{0, 0};
    public int lastColorClicked = 0;

    public SubGuiOverlays(Form form, int mode) {
        this.form = form;
        this.overlay = form.overlays;
        this.mode = mode;

        setBackground("menubg.png");
        xSize = 300;
    }

    @Override
    public void initGui() {
        super.initGui();

        int y = guiTop + 5;

        if (mode == 0) {
            for (int i = 0; i < 2; i++) {
                int id = i + 1;
                addLabel(new GuiNpcLabel(id, "Body Overlay " + id + ":", guiLeft + 8, y + 5));
                addTextField(new GuiNpcTextField(id, this, guiLeft + 90, y, 200, 20, "")); // id 1 and 2
                getTextField(id).setText(overlay.getBodyTexture(i));
                GuiNpcButton button;

                y += 23;

                addLabel(new GuiNpcLabel(Integer.parseInt(id + "0"), "Color:", guiLeft + 58, y + 5));
                button = new GuiNpcButton(id, guiLeft + 90, y, 50, 20, new String[]{
                    "display.custom", "display.body", "display.eye", "display.hair", "display.fur"}, overlay.getBodyColorType(i));
                addButton(button); // id 1 and 2

                if (overlay.getBodyColorType(i) == FormOverlay.ColorType.Custom.getId()) {
                    button = new GuiNpcButton(Integer.parseInt(id + "0"), guiLeft + 145, y, 50, 20, getColor(overlay.getBodyColor(i)));
                    button.packedFGColour = overlay.getBodyColor(i);
                    addButton(button); // id 10 and 20
                }

                y += 46;
            }
        } else if (mode == 1) {
            for (int i = 0; i < 2; i++) {
                int id = i + 1;
                addLabel(new GuiNpcLabel(id, "Face Overlay " + id + ":", guiLeft + 8, y + 5));
                addTextField(new GuiNpcTextField(id, this, guiLeft + 90, y, 200, 20, "")); // id 1 and 2
                if (overlay.isFaceMatchingPlayer(i)) {
                    getTextField(id).setText(overlay.getFaceTexture(i, selectedFaces[i]));
                } else {
                    getTextField(id).setText(overlay.getFaceTexture(i));
                }

                GuiNpcButton button;

                y += 23;

                addLabel(new GuiNpcLabel(Integer.parseInt(id + "0"), "Color:", guiLeft + 58, y + 5));
                button = new GuiNpcButton(id, guiLeft + 90, y, 50, 20, new String[]{
                    "display.custom", "display.body", "display.eye", "display.hair", "display.fur"}, overlay.getFaceColorType(i));
                addButton(button); // id 1 and 2

                addLabel(new GuiNpcLabel(Integer.parseInt(id + "1"), "Disable Face:", guiLeft + 143, y + 5));
                button = new GuiNpcButtonYesNo(Integer.parseInt(id + "1"), guiLeft + 210, y, 50, 20, overlay.isFaceDisabled(i));
                addButton(button); // id 11 and 21

                y += 23;

                if (overlay.getFaceColorType(i) == FormOverlay.ColorType.Custom.getId()) {
                    button = new GuiNpcButton(Integer.parseInt(id + "0"), guiLeft + 90, y, 50, 20, getColor(overlay.getFaceColor(i)));
                    button.packedFGColour = overlay.getFaceColor(i);
                    addButton(button); // id 10 and 20
                }

                addLabel(new GuiNpcLabel(Integer.parseInt(id + "2"), "Match Face:", guiLeft + 143, y + 5));
                button = new GuiNpcButtonYesNo(Integer.parseInt(id + "2"), guiLeft + 210, y, 50, 20, overlay.isFaceMatchingPlayer(i));
                addButton(button); // id 12 and 22

                if (overlay.isFaceMatchingPlayer(i)) {
                    button = new GuiNpcButton(Integer.parseInt(id + "3"), guiLeft + 263, y, 20, 20, new String[]{
                        "1", "2", "3", "4", "5", "6"}, selectedFaces[i]); // id 13 and 23
                    addButton(button);
                }

                y += 46;
            }
        }


        addButton(new GuiNpcButton(3, guiLeft + 243, guiTop + 190, 50, 20, "gui.close"));
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

        if (button.id == 3) {
            close();
            return;
        }

        if (mode == 0) {
            if (button.id == 1) {
                overlay.setBodyColorType(0, (overlay.getBodyColorType(0) + 1) % FormOverlay.ColorType.values().length);
                initGui();
            }
            if (button.id == 2) {
                overlay.setBodyColorType(1, (overlay.getBodyColorType(1) + 1) % FormOverlay.ColorType.values().length);
                initGui();
            }

            if (button.id == 10) {
                lastColorClicked = 0;
                setSubGui(new SubGuiColorSelector(overlay.getBodyColor(0)));
            }
            if (button.id == 20) {
                lastColorClicked = 1;
                setSubGui(new SubGuiColorSelector(overlay.getBodyColor(1)));
            }
        } else if (mode == 1) {
            if (button.id == 1) {
                overlay.setFaceColorType(0, (overlay.getFaceColorType(0) + 1) % (FormOverlay.ColorType.values().length - 1));
                initGui();
            }
            if (button.id == 2) {
                overlay.setFaceColorType(1, (overlay.getFaceColorType(1) + 1) % (FormOverlay.ColorType.values().length - 1));
                initGui();
            }

            if (button.id == 10) {
                lastColorClicked = 2;
                setSubGui(new SubGuiColorSelector(overlay.getFaceColor(0)));
            }
            if (button.id == 20) {
                lastColorClicked = 3;
                setSubGui(new SubGuiColorSelector(overlay.getFaceColor(1)));
            }

            if (button.id == 11) {
                overlay.setDisableFace(0, button.getValue() == 1);
            }
            if (button.id == 21) {
                overlay.setDisableFace(1, button.getValue() == 1);
            }

            if (button.id == 12) {
                overlay.setMatchPlayerFace(0, button.getValue() == 1);
                initGui();
            }
            if (button.id == 22) {
                overlay.setMatchPlayerFace(1, button.getValue() == 1);
                initGui();
            }

            if (button.id == 13) {
                selectedFaces[0] = (selectedFaces[0] + 1) % 6;
                getTextField(1).setText(overlay.getFaceTexture(0, selectedFaces[0]));
            }
            if (button.id == 23) {
                selectedFaces[1] = (selectedFaces[1] + 1) % 6;
                getTextField(2).setText(overlay.getFaceTexture(1, selectedFaces[1]));
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
                overlay.setFaceColor(0, color);
            } else if (lastColorClicked == 3) {
                overlay.setFaceColor(1, color);
            }

            initGui();
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
        }
    }
}
