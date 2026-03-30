package kamkeel.npcdbc.controllers.sync.handlers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.sync.DBCSyncHandler;
import kamkeel.npcdbc.data.form.Form;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.HashMap;

/**
 * Sync handler for custom forms. Extracted from
 * {@code DBCSyncController}'s Form-related sync logic.
 *
 * <p>Covers RELOAD, UPDATE, and REMOVE actions for
 * {@link kamkeel.npcdbc.constants.DBCSyncType#FORM}.</p>
 */
public class FormSyncHandler implements DBCSyncHandler {

    @Override
    public NBTTagCompound serializeAll() {
        NBTTagList list = new NBTTagList();
        for (Form customForm : FormController.getInstance().customForms.values()) {
            list.appendTag(customForm.writeToNBT());
        }
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("Data", list);
        return compound;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleReload(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("Data", 10);
        FormController fc = FormController.getInstance();
        for (int i = 0; i < list.tagCount(); i++) {
            Form form = new Form();
            form.readFromNBT(list.getCompoundTagAt(i));
            fc.customFormsSync.put(form.id, form);
        }

        fc.customForms = fc.customFormsSync;
        fc.customFormsSync = new HashMap<>();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleUpdate(NBTTagCompound compound) {
        Form form = new Form();
        form.readFromNBT(compound);
        FormController.getInstance().customForms.put(form.id, form);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleRemove(int id) {
        FormController.Instance.customForms.remove(id);
    }

    @Override
    public boolean supportsUpdate() {
        return true;
    }

    @Override
    public boolean supportsRemove() {
        return true;
    }
}
