package kamkeel.npcdbc.client.gui.component;

import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormOverlay;
import noppes.npcs.client.gui.GuiNpcTextureOverlays;
import noppes.npcs.controllers.data.SkinOverlay;
import noppes.npcs.entity.EntityNPCInterface;

public class GuiSelectOverlay extends GuiNpcTextureOverlays {
    Form form;
    FormOverlay overlay;
    int mode;
    int index;
    int faceType = 0;

    public GuiSelectOverlay(int mode, int index, Form form, EntityNPCInterface npc, SubGuiOverlays parent) {
        super(npc, parent);
        this.form = form;
        this.overlay = form.overlays;
        this.mode = mode;
        this.index = index;
    }

    public GuiSelectOverlay(int mode, int index, int faceType, Form form, EntityNPCInterface npc, SubGuiOverlays parent) {
        this(mode, index, form, npc, parent);
        this.faceType = faceType;
    }

    @Override
    public void elementClicked() {
        if (dataTextures.contains(slot.selected) && slot.selected != null) {
            if (mode == 0) {
                overlay.setBodyTexture(index, assets.getAsset(slot.selected));
                ((SubGuiOverlays) parent).getTextField(index + 1).setText(overlay.getBodyTexture(index));
            } else if (mode == 1) {
                if (overlay.isFaceMatchingPlayer(index)) {
                    overlay.setFaceTexture(index, assets.getAsset(slot.selected), faceType);
                    ((SubGuiOverlays) parent).getTextField(index + 1).setText(overlay.getFaceTexture(index, faceType));
                } else {
                    overlay.setFaceTexture(index, assets.getAsset(slot.selected));
                    ((SubGuiOverlays) parent).getTextField(index + 1).setText(overlay.getFaceTexture(index));
                }
            }

            npc.display.skinOverlayData.overlayList.put(0, new SkinOverlay(assets.getAsset(slot.selected)));
        }
    }

    @Override
    public void close() {
        npc.display.skinOverlayData.clear();
    }
}
