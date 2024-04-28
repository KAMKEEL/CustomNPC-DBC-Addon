package kamkeel.npcdbc.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

public class ByteBufUtils extends cpw.mods.fml.common.network.ByteBufUtils {

    public static void writeIntArray(ByteBuf buffer, int[] arr) {
        buffer.writeInt(arr.length);
        for (int i : arr) {
            buffer.writeInt(i);
        }
    }

    public static int[] readIntArray(ByteBuf buffer) {
        int length = buffer.readInt();
        int[] arr = new int[length];
        for (int i = 0; i < length; i++) {
            arr[i] = buffer.readInt();
        }
        return arr;
    }

    public static void writeNBT(ByteBuf buffer, NBTTagCompound compound) throws IOException {
        byte[] bytes = CompressedStreamTools.compress(compound);
        buffer.writeShort((short)bytes.length);
        buffer.writeBytes(bytes);
    }

    public static NBTTagCompound readNBT(ByteBuf buffer) throws IOException {
        byte[] bytes = new byte[buffer.readShort()];
        buffer.readBytes(bytes);
        return CompressedStreamTools.func_152457_a(bytes, new NBTSizeTracker(2097152L));
    }
}
