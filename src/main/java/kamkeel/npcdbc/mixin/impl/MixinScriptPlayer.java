package kamkeel.npcdbc.mixin.impl;

import kamkeel.addon.client.DBCClient;
import kamkeel.npcdbc.client.ParticleFormHandler;
import kamkeel.npcdbc.client.gui.SubGuiDBCProperties;
import kamkeel.npcdbc.mixin.INPCDisplay;
import kamkeel.npcdbc.scripted.ScriptDBCAddon;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.client.gui.mainmenu.GuiNpcStats;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.scripted.entity.ScriptDBCPlayer;
import noppes.npcs.scripted.entity.ScriptLivingBase;
import noppes.npcs.scripted.entity.ScriptPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.Set;

@Mixin(ScriptPlayer.class)
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
