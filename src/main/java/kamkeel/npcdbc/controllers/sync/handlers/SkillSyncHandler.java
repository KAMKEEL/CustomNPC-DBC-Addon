package kamkeel.npcdbc.controllers.sync.handlers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.controllers.SkillController;
import kamkeel.npcdbc.data.skill.CustomSkill;
import kamkeel.npcdbc.util.NBTHelper;
import kamkeel.npcs.controllers.sync.SyncHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;

public class SkillSyncHandler implements SyncHandler {

    @Override
    public NBTTagCompound serializeAll() {
        NBTTagList list = NBTHelper.nbtIntegerObjectMap(SkillController.Instance.customSkills, s -> s.writeToNBT());
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("Data", list);
        return compound;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleReload(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("Data", 10);
        SkillController.Instance.customSkillsSync = NBTHelper.javaIntegerObjectMap(list, t -> {
            CustomSkill skill = new CustomSkill();
            skill.readFromNBT(t);
            return skill;
        });
        SkillController.Instance.customSkills = SkillController.Instance.customSkillsSync;
        SkillController.Instance.customSkillsSync = new HashMap<>();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleUpdate(NBTTagCompound compound) {
        CustomSkill skill = new CustomSkill();
        skill.readFromNBT(compound);
        SkillController.Instance.customSkills.put(skill.id, skill);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleRemove(int id) {
        SkillController.Instance.customSkills.remove(id);
    }

    @Override public boolean supportsUpdate() { return true; }
    @Override public boolean supportsRemove() { return true; }
}
