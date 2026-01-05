package kamkeel.npcdbc.data.ability;

import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.packets.player.ability.DBCAnimateAbility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import noppes.npcs.controllers.data.AnimationData;
import noppes.npcs.scripted.event.AnimationEvent;

public class AddonAbility extends Ability {
    public String langName;
    public String langDescription;
    public int skillId = -1;

    public AddonAbility() {
        super();
        icon = CustomNpcPlusDBC.ID + ":textures/gui/ability_icons.png";
    }

    @Override
    public int getWidth() {
        return 48;
    }

    @Override
    public int getHeight() {
        return 48;
    }

    @Override
    public float getScale() {
        return 1.5f;
    }

    public String getLangName() {
        return this.langName;
    }

    public String getLangDescription() {
        return langDescription;
    }

    public int getSkillId() {
        return skillId;
    }

    public Ability save() {
        return this;
    }

    public boolean handleAnimations(AbilityData data, AnimationEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) ((AnimationData) event.getAnimationData()).getMCEntity();

        if (event instanceof AnimationEvent.Started) {
            DBCPacketHandler.Instance.sendToPlayer(new DBCAnimateAbility(this.id, true), player);
            NetworkUtility.sendServerMessage(player, "I wanna kms");
        }

        if (event instanceof AnimationEvent.Ended) {
            DBCPacketHandler.Instance.sendToPlayer(new DBCAnimateAbility(this.id, false), player);
        }

        return onAnimationEvent(data, event);
    }

    public boolean onAnimationEvent(AbilityData data, AnimationEvent event) {
        return false;
    }
}
