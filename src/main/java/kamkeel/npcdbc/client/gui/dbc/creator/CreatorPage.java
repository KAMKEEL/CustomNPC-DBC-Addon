package kamkeel.npcdbc.client.gui.dbc.creator;

import net.minecraft.client.gui.GuiButton;

import java.util.List;

/**
 * Abstract base for wizard pages within {@link CharacterCreationGui}.
 * <p>
 * Pages own their controls and rendering content but do NOT own navigation.
 * Page switching is exclusively managed by the parent wizard container.
 * <p>
 * All mutable creator state lives in {@link CreatorSession}; pages read and
 * write through it. Vanilla static sync is done through {@link VanillaCreatorBridge}.
 */
public abstract class CreatorPage {

    protected final CharacterCreationGui parent;
    protected final CreatorSession session;
    protected final VanillaCreatorBridge bridge;

    public CreatorPage(CharacterCreationGui parent, CreatorSession session, VanillaCreatorBridge bridge) {
        this.parent = parent;
        this.session = session;
        this.bridge = bridge;
    }

    /**
     * Adds page-specific buttons and labels to the parent's lists.
     * Called during {@link CharacterCreationGui#initGui()}.
     *
     * @param buttonList the parent's mutable button list
     * @param guiLeft    left edge of the GUI background
     * @param guiTop     top edge of the GUI background
     */
    @SuppressWarnings("rawtypes")
    public abstract void initPage(List buttonList, int guiLeft, int guiTop);

    /**
     * Draws page-specific content. Called from the parent's drawScreen.
     */
    public abstract void drawPage(int mouseX, int mouseY, float partialTicks);

    /**
     * Handles a button click. Return {@code true} if the page consumed the event.
     */
    public abstract boolean actionPerformed(GuiButton button);

    /** Called when this page becomes the active page. */
    public void onPageEnter() {}

    /** Called when this page is being left (navigating away). */
    public void onPageLeave() {}

    /**
     * Optional key input handler. Return {@code true} if the key was consumed.
     */
    public boolean keyTyped(char typedChar, int keyCode) {
        return false;
    }
}
