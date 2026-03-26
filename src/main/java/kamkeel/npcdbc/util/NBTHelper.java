package kamkeel.npcdbc.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class NBTHelper {

    /**
     * @param map   Map of objects you would like to save
     * @param toNBT Lambda / callback used to write the object into NBT
     * @param <T>   Type of the map's values
     * @return Taglist of compounds with this format: <br>
     * {Slot: {@link Integer}, Content: {@link NBTTagCompound}}
     */
    public static <T> NBTTagList nbtIntegerObjectMap(Map<Integer, T> map, Function<T, NBTTagCompound> toNBT) {
        return nbtIntegerObjectMap(map, toNBT, (ignored, ignored2) -> true);
    }

    /**
     * @param map           Map of objects you would like to save
     * @param toNBT         Lambda / callback used to write the object into NBT
     * @param keepCondition Callback that checks if (currentID, newObject) should be kept in the newly created map or not <br>
     *                      For further info refer to {@link KeepConditionCallback}
     * @param <T>           Type of the map's values
     * @return Taglist of compounds with this format: <br>
     * {Slot: {@link Integer}, Content: {@link NBTTagCompound}}
     */
    public static <T> NBTTagList nbtIntegerObjectMap(Map<Integer, T> map, Function<T, NBTTagCompound> toNBT, KeepConditionCallback<Integer, T> keepCondition) {
        NBTTagList nbttaglist = new NBTTagList();
        if (map != null) {
            for (Map.Entry<Integer, T> entry : map.entrySet()) {
                T value = entry.getValue();
                if (value == null)
                    continue;
                if (!keepCondition.check(entry.getKey(), value))
                    continue;
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setInteger("Slot", entry.getKey());
                nbttagcompound.setTag("Content", toNBT.apply(value));
                nbttaglist.appendTag(nbttagcompound);
            }

        }
        return nbttaglist;
    }

    /**
     * @param list          tag list containing compounds with this format: <br>
     *                      {Slot: {@link Integer}, Content: {@link NBTTagCompound}}
     * @param fromNBT       Lambda / Callback used to read the object from a map from NBT.
     * @param keepCondition Callback that checks if (currentID, newObject) should be kept in the newly created map or not <br>
     *                      For further info refer to {@link KeepConditionCallback}
     * @param <T>           Type of the object you'd like to read from NBT
     * @return HashMap of the newly created objects
     */
    public static <T> HashMap<Integer, T> javaIntegerObjectMap(NBTTagList list, Function<NBTTagCompound, T> fromNBT, KeepConditionCallback<Integer, T> keepCondition) {
        HashMap<Integer, T> map = new HashMap<>();

        if (list != null && list.func_150303_d() == Constants.NBT.TAG_COMPOUND) {
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound compound = list.getCompoundTagAt(i);
                int slot = compound.getInteger("Slot");
                NBTTagCompound contentCompound = compound.getCompoundTag("Content");
                T newColor = fromNBT.apply(contentCompound);
                if (keepCondition.check(slot, newColor))
                    map.put(slot, newColor);
            }
        }

        return map;
    }

    /**
     * @param list    tag list containing compounds with this format: <br>
     *                {Slot: {@link Integer}, Content: {@link NBTTagCompound}}
     * @param fromNBT Lambda / Callback used to read the object from a map from NBT.
     * @param <T>     Type of the object you'd like to read from NBT
     * @return HashMap of the newly created objects
     */
    public static <T> HashMap<Integer, T> javaIntegerObjectMap(NBTTagList list, Function<NBTTagCompound, T> fromNBT) {
        return javaIntegerObjectMap(list, fromNBT, (ignore1, ignore2) -> true);
    }

    /**
     * Callback used to determine if an object should be kept or not while reading the list
     *
     * @param <Param1> First parameter type of the function
     * @param <Param2> Second parameter type of the function
     */
    @FunctionalInterface
    public interface KeepConditionCallback<Param1, Param2> {
        boolean check(Param1 param1, Param2 param2);
    }

    // ──────────────────────────────────────────────────────────────────
    //  String-keyed write helpers
    // ──────────────────────────────────────────────────────────────────

    /**
     * Serialises a {@code HashSet<String>} into an {@link NBTTagList} of {@code TAG_STRING} entries.
     */
    public static NBTTagList nbtStringSet(HashSet<String> set) {
        NBTTagList list = new NBTTagList();
        if (set != null) {
            for (String s : set) {
                if (s != null)
                    list.appendTag(new NBTTagString(s));
            }
        }
        return list;
    }

    /**
     * Serialises a {@code Map<String, Float>} into an {@link NBTTagList} of {@code TAG_COMPOUND}
     * entries, each with a {@code "key"} string and a {@code "level"} float.
     */
    public static NBTTagList nbtStringFloatMap(Map<String, Float> map) {
        NBTTagList list = new NBTTagList();
        if (map != null) {
            for (Map.Entry<String, Float> entry : map.entrySet()) {
                if (entry.getKey() == null) continue;
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("key", entry.getKey());
                tag.setFloat("level", entry.getValue());
                list.appendTag(tag);
            }
        }
        return list;
    }

    /**
     * Serialises a {@code Map<String, Integer>} into an {@link NBTTagList} of {@code TAG_COMPOUND}
     * entries, each with a {@code "key"} string and a {@code "value"} int.
     */
    public static NBTTagList nbtStringIntMap(Map<String, Integer> map) {
        NBTTagList list = new NBTTagList();
        if (map != null) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getKey() == null) continue;
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("key", entry.getKey());
                tag.setInteger("value", entry.getValue());
                list.appendTag(tag);
            }
        }
        return list;
    }

    /**
     * Serialises a {@code Map<String, V>} into an {@link NBTTagList} of {@code TAG_COMPOUND}
     * entries, each with a {@code "key"} string and a {@code "Content"} sub-compound produced by
     * {@code toNBT}.
     */
    public static <V> NBTTagList nbtStringObjectMap(Map<String, V> map, Function<V, NBTTagCompound> toNBT) {
        return nbtStringObjectMap(map, toNBT, (k, v) -> true);
    }

    /**
     * Serialises a {@code Map<String, V>} with an optional keep-condition filter.
     */
    public static <V> NBTTagList nbtStringObjectMap(Map<String, V> map, Function<V, NBTTagCompound> toNBT, BiPredicate<String, V> keepCondition) {
        NBTTagList list = new NBTTagList();
        if (map != null) {
            for (Map.Entry<String, V> entry : map.entrySet()) {
                String key = entry.getKey();
                V value = entry.getValue();
                if (key == null || value == null) continue;
                if (!keepCondition.test(key, value)) continue;
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("key", key);
                tag.setTag("Content", toNBT.apply(value));
                list.appendTag(tag);
            }
        }
        return list;
    }

    // ──────────────────────────────────────────────────────────────────
    //  String-keyed read helpers
    // ──────────────────────────────────────────────────────────────────

    /**
     * Reads a {@code HashSet<String>} from an {@link NBTTagList} of {@code TAG_STRING} entries.
     */
    public static HashSet<String> getStringSet(NBTTagList list) {
        HashSet<String> set = new HashSet<>();
        if (list != null && list.func_150303_d() == Constants.NBT.TAG_STRING) {
            for (int i = 0; i < list.tagCount(); i++) {
                String s = list.getStringTagAt(i);
                if (s != null && !s.isEmpty())
                    set.add(s);
            }
        }
        return set;
    }

    /**
     * Reads a {@code HashMap<String, Float>} from an {@link NBTTagList} of {@code TAG_COMPOUND}
     * entries that each have a {@code "key"} string and a {@code "level"} float.
     */
    public static HashMap<String, Float> getStringFloatMap(NBTTagList list) {
        HashMap<String, Float> map = new HashMap<>();
        if (list != null && list.func_150303_d() == Constants.NBT.TAG_COMPOUND) {
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                if (tag.hasKey("key")) {
                    String key = tag.getString("key");
                    float level = tag.getFloat("level");
                    if (!key.isEmpty())
                        map.put(key, level);
                }
            }
        }
        return map;
    }

    /**
     * Reads a {@code HashMap<String, Integer>} from an {@link NBTTagList} of {@code TAG_COMPOUND}
     * entries that each have a {@code "key"} string and a {@code "value"} int.
     */
    public static HashMap<String, Integer> getStringIntMap(NBTTagList list) {
        HashMap<String, Integer> map = new HashMap<>();
        if (list != null && list.func_150303_d() == Constants.NBT.TAG_COMPOUND) {
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                if (tag.hasKey("key")) {
                    String key = tag.getString("key");
                    int value = tag.getInteger("value");
                    if (!key.isEmpty())
                        map.put(key, value);
                }
            }
        }
        return map;
    }

    /**
     * Reads a {@code HashMap<String, V>} from an {@link NBTTagList} of {@code TAG_COMPOUND}
     * entries that each have a {@code "key"} string and a {@code "Content"} sub-compound.
     *
     * @param fromNBT       converts the {@code "Content"} compound into a value
     * @param keepCondition optional filter; return {@code true} to keep the entry
     */
    public static <V> HashMap<String, V> getStringObjectMap(NBTTagList list, Function<NBTTagCompound, V> fromNBT, BiPredicate<String, V> keepCondition) {
        HashMap<String, V> map = new HashMap<>();
        if (list != null && list.func_150303_d() == Constants.NBT.TAG_COMPOUND) {
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                if (tag.hasKey("key")) {
                    String key = tag.getString("key");
                    NBTTagCompound content = tag.getCompoundTag("Content");
                    V value = fromNBT.apply(content);
                    if (!key.isEmpty() && keepCondition.test(key, value))
                        map.put(key, value);
                }
            }
        }
        return map;
    }

    /**
     * Convenience overload without keep-condition.
     */
    public static <V> HashMap<String, V> getStringObjectMap(NBTTagList list, Function<NBTTagCompound, V> fromNBT) {
        return getStringObjectMap(list, fromNBT, (k, v) -> true);
    }

}
