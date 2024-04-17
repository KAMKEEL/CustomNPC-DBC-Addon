package kamkeel.npcdbc.mixin.impl.npc;

import kamkeel.npcdbc.scripted.ScriptDBCAddon;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.scripted.entity.ScriptDBCPlayer;
import noppes.npcs.scripted.entity.ScriptLivingBase;
import noppes.npcs.scripted.entity.ScriptPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.Set;

@Mixin(value = ScriptPlayer.class, remap = false)
public class MixinScriptPlayer<T extends EntityPlayerMP>  extends ScriptLivingBase<T> {

    @Shadow public T player;

    public MixinScriptPlayer(T entity) {
        super(entity);
    }

    /**
     * @author Kam
     * @reason Make getDBCPlayer Return DBC Addon
     */
    @Overwrite(remap = false)
    public ScriptDBCPlayer<T> getDBCPlayer() {
        Set keySet = player.getEntityData().getCompoundTag("PlayerPersisted").func_150296_c();
        Iterator iterator = keySet.iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();
            if(s.contains("jrmc"))
                return new ScriptDBCAddon<>(this.player);
        }
        return null;
    }
}
