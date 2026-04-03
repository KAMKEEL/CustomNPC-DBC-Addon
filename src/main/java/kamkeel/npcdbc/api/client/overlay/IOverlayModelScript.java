package kamkeel.npcdbc.api.client.overlay;

import kamkeel.npcdbc.client.race.IOverlayModel;
import kamkeel.npcdbc.data.overlay.OverlayContext;
import noppes.npcs.janino.annotations.ParamName;

/**
 * Janino script contract for scripted overlay models.
 * <p>
 * The {@link Functions} abstract class implements {@link IOverlayModel}
 * directly so user-authored Janino scripts satisfy the overlay-model
 * rendering contract by simply overriding the inherited methods.
 */
public interface IOverlayModelScript {

    abstract class Functions implements IOverlayModel {

        @Override
        public void initialize(@ParamName("ctx") OverlayContext ctx) {
        }

        @Override
        public void render(@ParamName("ctx") OverlayContext ctx) {
        }

        @Override
        public boolean appliesTo(@ParamName("ctx") OverlayContext ctx) {
            return true;
        }

        @Override
        public boolean rendersInFirstPerson(@ParamName("ctx") OverlayContext ctx) {
            return false;
        }
    }
}
