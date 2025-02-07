package kamkeel.npcdbc.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NBTHelper {

    /**
     *
     * @param map Map of objects you would like to save
     * @param toNBT Lambda / callback used to write the object into NBT
     * @return Taglist created from the provided hashmap
     * @param <T> Type of the map's values
     */
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

    /**
     *
     * @param list tag list created using other functions from this map
     * @param fromNBT Lambda / Callback used to read the object from a map from NBT.
     * @param keepCondition Callback that checks if (currentID, newObject) should be kept in the newly created map or not <br>
     *                      For further info refer to {@link KeepConditionCallback}
     * @return HashMap of the newly created objects
     * @param <T> Type of the object you'd like to read from NBT
     */
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

    /**
     *
     * @param list tag list created using other functions from this map
     * @param fromNBT Lambda / Callback used to read the object from a map from NBT.
     * @return HashMap of the newly created objects
     * @param <T> Type of the object you'd like to read from NBT
     */
    public static <T> HashMap<Integer, T> javaIntegerObjectMap(NBTTagList list, Function<NBTTagCompound, T> fromNBT) {
        return javaIntegerObjectMap(list, fromNBT, (ignore1, ignore2) -> true);
    }

    /**
     * Callback used to determine if an object should be kept or not while reading the list
     * @param <Param1> First parameter type of the function
     * @param <Param2> Second parameter type of the function
     */
    @FunctionalInterface
    public interface KeepConditionCallback<Param1, Param2> {
        boolean check(Param1 param1, Param2 param2);
    }

}
