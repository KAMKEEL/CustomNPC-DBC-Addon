package kamkeel.npcdbc.data.overlay;

import net.minecraft.nbt.NBTTagCompound;

/**

 */
public interface JaninoScriptable<T> {

    boolean hasScript();

    JaninoScript<T> getScript();

    JaninoScript<T> createScript();

    void deleteScript();

    static <T> JaninoScript<T> readFromNBT(NBTTagCompound compound, JaninoScript<T> script) {
        if (compound.hasKey("Script"))
            script.readFromNBT(compound.getCompoundTag("Script"));
        return script;
    }

    static <T> NBTTagCompound writeToNBT(NBTTagCompound compound, JaninoScript<T> script) {
        if (script != null)
            compound.setTag("Script", script.writeToNBT(new NBTTagCompound()));

        return compound;
    }
}
