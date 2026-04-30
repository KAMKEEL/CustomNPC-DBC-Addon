package kamkeel.npcdbc.data.race.races.android;

import kamkeel.npcdbc.items.ModItems;
import kamkeel.npcdbc.util.EnumRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.Collection;

public final class AndroidPartType extends EnumRegistry {

    // ──────────────────── Built-in parts ────────────────────
    public static final AndroidPartType RED_RIBBON_CORE = register(AndroidParts.RED_RIBBON_CORE);
    public static final AndroidPartType KI_RING = register(AndroidParts.KI_RING);

    // ──────────────────── Instance fields ────────────────────
    private final AndroidPartData part;

    private AndroidPartType(AndroidPartData part) {
        this.part = part;
    }

    // ──────────────────── Registration ────────────────────
    public static AndroidPartType register(AndroidPartData part) {
        return new AndroidPartType(part).register();
    }

    // ──────────────────── Public API ────────────────────

    public String getId() {
        return part.getId();
    }

    public String getName() {
        return part.getName();
    }

    public String getUnlocalizedName() {
        return part.getUnlocalizedName();
    }

    public String getNamespace() {
        return part.getNamespace();
    }

    public String getTextureDir() {
        return part.getTextureDir();
    }

    public String resolveTextureDir() {
        return part.resolveTextureDir();
    }

    public AndroidPartData getData() {
        return part;
    }

    public AndroidPartSlot getSlot() {
        return part.getSlot();
    }

    public boolean fitsSlot(AndroidPartSlot slot) {
        return part.fitsSlot(slot);
    }

    public void onEquip(EntityPlayer player) {
        part.onEquip(player);
    }

    public void onUnequip(EntityPlayer player) {
        part.onUnequip(player);
    }

    public void onTick(EntityPlayer player) {
        part.onTick(player);
    }

    public ItemStack toItemStack() {
        return new ItemStack(ModItems.AndroidParts, 1, this.ordinal());
    }

    // ──────────────────── Convenience Methods ────────────────────

    public static AndroidPartType byId(String id) { return EnumRegistry.byId(AndroidPartType.class, id); }
    public static AndroidPartType byOrdinal(int ordinal) { return EnumRegistry.byOrdinal(AndroidPartType.class, ordinal); }
    public static Collection<AndroidPartType> values() { return EnumRegistry.values(AndroidPartType.class); }
    public static int count() { return EnumRegistry.count(AndroidPartType.class); }
}
