package kamkeel.npcdbc.client.gui.global.effects;

import kamkeel.npcdbc.data.statuseffect.custom.CustomEffect;
import kamkeel.npcdbc.data.statuseffect.custom.EffectScriptHandler;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.effect.DBCRequestEffectScript;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.gui.script.GuiScriptInterface;

public class GuiEffectScript extends GuiScriptInterface {
    public final GuiNPCManageEffects parent;
    public final CustomEffect effect;
    private final EffectScriptHandler script;

    public GuiEffectScript(GuiNPCManageEffects parent, CustomEffect effect) {
        this.parent = parent;
        this.effect = effect;
        this.script = new EffectScriptHandler(effect);
        this.handler = this.script;

        PacketHandler.Instance.sendToServer(new DBCRequestEffectScript().generatePacket());
    }

    public void setGuiData(NBTTagCompound compound) {

    }
}
