package kamkeel.npcdbc.data.overlay;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.data.form.FacePartData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import noppes.npcs.controllers.data.EffectScript;

import java.util.*;
import java.util.function.Function;

public class OverlayChain {

    public final ArrayList<Overlay> overlays = new ArrayList<>();

    public String name = "";
    public boolean enabled = true;

    public Set<FacePartData.Part> disabledParts = new HashSet<>();

    public Function<OverlayContext, Boolean> condition;

    public OverlayScript scriptHandler = new OverlayScript();

    public OverlayChain() {
    }

    public OverlayChain(String name) {
        this.name = name;
    }

    public OverlayChain disable(FacePartData.Part... parts) {
        for (FacePartData.Part part : parts)
            disabledParts.add(part);

        return this;
    }

    public static OverlayChain create(String name) {
        return new OverlayChain(name);
    }

    public OverlayChain condition(Function<OverlayContext, Boolean> condition) {
        this.condition = condition;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public boolean checkCondition(OverlayContext ctx) {
        return condition.apply(ctx);
    }


    public Overlay add(Overlay.Type type) {
        Overlay o = type.create().chain(this);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(Overlay.Type type, String texture) {
        Overlay o = type.create().chain(this).texture(texture);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(Overlay.Type type, Overlay.TextureFunction texture) {
        Overlay o = type.create().chain(this).texture(texture);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(Overlay.Type type, Overlay.ColorType colorType) {
        Overlay o = type.create().chain(this).colorType(colorType);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(Overlay.Type type, int color) {
        Overlay o = type.create().chain(this).colorType(Overlay.ColorType.Custom).color(color);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(Overlay.Type type, Overlay.ColorType colorType, String texture) {
        Overlay o = type.create().chain(this).texture(texture).colorType(colorType);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(Overlay.Type type, Overlay.ColorType colorType, Overlay.TextureFunction texture) {
        Overlay o = type.create().chain(this).texture(texture).colorType(colorType);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(Overlay.Type type, int color, String texture) {
        Overlay o = type.create().chain(this).texture(texture).colorType(Overlay.ColorType.Custom).color(color);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(Overlay.Type type, int color, Overlay.TextureFunction texture) {
        Overlay o = type.create().chain(this).texture(texture).colorType(Overlay.ColorType.Custom).color(color);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(Overlay.Type type, Overlay.ColorType colorType, boolean glow, String texture) {
        Overlay o = type.create().chain(this).texture(texture).colorType(colorType).glow(glow);
        this.overlays.add(o);
        return o.color(0xffffff);
    }

    public Overlay add(Overlay.Type type, Overlay.ColorType colorType, boolean glow, Overlay.TextureFunction texture) {
        Overlay o = type.create().chain(this).texture(texture).colorType(colorType).glow(glow);
        this.overlays.add(o);
        return o.color(0xffffff);
    }

    public Overlay add(Overlay.Type type, int color, boolean glow, String texture) {
        Overlay o = type.create().chain(this).texture(texture).colorType(Overlay.ColorType.Custom).color(color).glow(glow);
        this.overlays.add(o);
        return o.color(0xffffff);
    }

    public Overlay add(Overlay.Type type, int color, boolean glow, Overlay.TextureFunction texture) {
        Overlay o = type.create().chain(this).texture(texture).colorType(Overlay.ColorType.Custom).color(color).glow(glow);
        this.overlays.add(o);
        return o.color(0xffffff);
    }

    public Overlay get(int id) {
        if (id < this.overlays.size())
            return this.overlays.get(id);
        return null;
    }

    public Overlay deleteOverlay(int id) {
        if (id >= this.overlays.size())
            return null;

        return this.overlays.remove(id);
    }

    public void replaceOverlay(Overlay oldOverlay, Overlay newOverlay) {
        int index = overlays.indexOf(oldOverlay);
        if (index != -1)
            overlays.set(index, newOverlay);
    }

    public List<Overlay> getOverlays() {
        return this.overlays;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public OverlayChain enable(boolean enable) {
        this.enabled = enable;
        return this;
    }

    public void readFromNBT(NBTTagCompound compound) {
        enabled = compound.getBoolean("hasOverlays");

        NBTTagCompound rendering = compound.getCompoundTag("overlayData");

        int i = 0;
        while (rendering.hasKey("overlay" + i)) {
            NBTTagCompound overlayCompound = rendering.getCompoundTag("overlay" + i);

            int type = overlayCompound.hasKey("type", Constants.NBT.TAG_INT) ? overlayCompound.getInteger("type") : 0;
            Overlay overlay = Overlay.Type.create(type);

            if (overlay != null) {
                overlay.readFromNBT(overlayCompound);
                overlays.add(i, overlay);
            }
            i++;
        }

        if (compound.hasKey("disabledParts")) {
            disabledParts.clear();
            FacePartData.Part[] values = FacePartData.Part.values();
            for (byte ordinal : compound.getByteArray("disabledParts")) {
                if (ordinal >= 0 && ordinal < values.length)
                    disabledParts.add(values[ordinal]);
            }
        }

        if (compound.hasKey("ScriptData", Constants.NBT.TAG_COMPOUND)) {
            scriptHandler.readFromNBT(compound.getCompoundTag("ScriptData"));
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("hasOverlays", enabled);

        NBTTagCompound rendering = new NBTTagCompound();

        for (int i = 0; i < overlays.size(); i++) {
            rendering.setTag("overlay" + i, overlays.get(i).writeToNBT());
        }

        if (!disabledParts.isEmpty()) {
            byte[] arr = new byte[disabledParts.size()];
            int i = 0;
            for (FacePartData.Part t : disabledParts)
                arr[i++] = (byte) t.ordinal();
            compound.setByteArray("disabledParts", arr);
        }

        NBTTagCompound scriptData = new NBTTagCompound();
        if (scriptHandler != null)
            scriptHandler.writeToNBT(scriptData);
        compound.setTag("ScriptData", scriptData);

        compound.setTag("overlayData", rendering);
        return compound;
    }
}
