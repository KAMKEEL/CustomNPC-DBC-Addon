package kamkeel.npcdbc.client;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;

public class DBCRenderEvent extends RenderPlayerEvent {

    public DBCRenderEvent(EntityPlayer player, RenderPlayer renderer, float partialRenderTick) {
        super(player, renderer, partialRenderTick);
    }

    @Cancelable
    public static class Pre extends DBCRenderEvent {
        public Pre(EntityPlayer player, RenderPlayer renderer, float tick) {
            super(player, renderer, tick);
        }
    }

    public static class Post extends DBCRenderEvent {
        public Post(EntityPlayer player, RenderPlayer renderer, float tick) {
            super(player, renderer, tick);
        }
    }


}
