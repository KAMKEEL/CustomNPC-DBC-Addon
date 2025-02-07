package kamkeel.npcdbc.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NBTHelper {

    public static <T> NBTTagList nbtIntegerObjectMap(Map<Integer, T> map, Function<T, NBTTagCompound> toNBT) {
        NBTTagList nbttaglist = new NBTTagList();
        if (map != null) {

            for (int slot : map.keySet()) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setInteger("Slot", slot);
                nbttagcompound.setTag("Content", toNBT.apply(map.get(slot)));
                nbttaglist.appendTag(nbttagcompound);
            }

        }
        return nbttaglist;
    }

    public static <T> HashMap<Integer, T> javaIntegerObjectMap(NBTTagList list, Function<NBTTagCompound, T> fromNBT, KeepConditionCallback<Integer, T> keepCondition) {
        HashMap<Integer, T> map = new HashMap<>();

        if (list != null && list.func_150303_d() == Constants.NBT.TAG_COMPOUND) {
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound compound = list.getCompoundTagAt(i);
                int slot = compound.getInteger("Slot");
                NBTTagCompound contentCompound = compound.getCompoundTag("Content");
                map.put(slot, fromNBT.apply(contentCompound));
            }
        }

        return map;
    }

    public static <T> HashMap<Integer, T> javaIntegerObjectMap(NBTTagList list, Function<NBTTagCompound, T> fromNBT) {
        return javaIntegerObjectMap(list, fromNBT, (ignore1, ignore2) -> true);
    }

    @FunctionalInterface
    public interface KeepConditionCallback<Param1, Param2> {
        boolean check(Param1 param1, Param2 param2);
    }

}
