package kamkeel.npcdbc.data.overlay;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.api.client.overlay.IOverlay;
import kamkeel.npcdbc.api.client.overlay.IOverlayChain;
import kamkeel.npcdbc.data.form.FacePartData;
import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class OverlayChain implements IOverlayChain, DataSerializable {

    public final ArrayList<Overlay> overlays = new ArrayList<>();

    public String name = "";
    public boolean enabled = true;

    public Set<FacePartData.Part> disabledParts = new HashSet<>();

    public Function<OverlayContext, Boolean> condition;


    public OverlayChain() {
    }

    public OverlayChain(String name) {
        this.name = name;
    }


    @Override
    public OverlayChain disable(FacePartData.Part... parts) {
        for (FacePartData.Part part : parts)
            disabledParts.add(part);

        return this;
    }

    public static OverlayChain create(String name) {
        return new OverlayChain(name);
    }

    @Override
    public OverlayChain condition(Function<OverlayContext, Boolean> condition) {
        this.condition = condition;
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean checkCondition(OverlayContext ctx) {
        return condition.apply(ctx);
    }

    public void add(Overlay... overlays) {
        for (Overlay overlay : overlays) {
            overlay.chain(this);
            this.overlays.add(overlay);
        }
    }

    public Overlay add(IOverlay.Type type) {
        Overlay o = ((Overlay) type.create()).chain(this);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(IOverlay.Type type, String texture) {
        Overlay o = ((Overlay) type.create()).chain(this).texture(texture);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(IOverlay.Type type, IOverlay.TextureFunction texture) {
        Overlay o = ((Overlay) type.create()).chain(this).texture(texture);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(IOverlay.Type type, IOverlay.ColorType colorType) {
        Overlay o = ((Overlay) type.create()).chain(this).colorType(colorType);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(IOverlay.Type type, int color) {
        Overlay o = ((Overlay) type.create()).chain(this).colorType(IOverlay.ColorType.Custom).color(color);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(IOverlay.Type type, IOverlay.ColorType colorType, String texture) {
        Overlay o = ((Overlay) type.create()).chain(this).texture(texture).colorType(colorType);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(IOverlay.Type type, IOverlay.ColorType colorType, IOverlay.TextureFunction texture) {
        Overlay o = ((Overlay) type.create()).chain(this).texture(texture).colorType(colorType);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(IOverlay.Type type, int color, String texture) {
        Overlay o = ((Overlay) type.create()).chain(this).texture(texture).colorType(IOverlay.ColorType.Custom).color(color);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(IOverlay.Type type, int color, IOverlay.TextureFunction texture) {
        Overlay o = ((Overlay) type.create()).chain(this).texture(texture).colorType(IOverlay.ColorType.Custom).color(color);
        this.overlays.add(o);
        return o;
    }

    public Overlay add(IOverlay.Type type, IOverlay.ColorType colorType, boolean glow, String texture) {
        Overlay o = ((Overlay) type.create()).chain(this).texture(texture).colorType(colorType).glow(glow);
        this.overlays.add(o);
        return o.color(0xffffff);
    }

    public Overlay add(IOverlay.Type type, IOverlay.ColorType colorType, boolean glow, IOverlay.TextureFunction texture) {
        Overlay o = ((Overlay) type.create()).chain(this).texture(texture).colorType(colorType).glow(glow);
        this.overlays.add(o);
        return o.color(0xffffff);
    }

    public Overlay add(IOverlay.Type type, int color, boolean glow, String texture) {
        Overlay o = ((Overlay) type.create()).chain(this).texture(texture).colorType(IOverlay.ColorType.Custom).color(color).glow(glow);
        this.overlays.add(o);
        return o.color(0xffffff);
    }

    public Overlay add(IOverlay.Type type, int color, boolean glow, IOverlay.TextureFunction texture) {
        Overlay o = ((Overlay) type.create()).chain(this).texture(texture).colorType(IOverlay.ColorType.Custom).color(color).glow(glow);
        this.overlays.add(o);
        return o.color(0xffffff);
    }

    public Overlay get(int id) {
        if (id < this.overlays.size())
            return this.overlays.get(id);
        return null;
    }

    public Overlay getOverlay(String key) {
        if (key == null) return null;
        String lower = key.toLowerCase();
        for (Overlay o : overlays) {
            if (o.key != null && o.key.toLowerCase().equals(lower))
                return o;
        }
        return null;
    }

    public boolean hasOverlay(String key) {
        return getOverlay(key) != null;
    }

    public Overlay deleteOverlay(int id) {
        if (id >= this.overlays.size())
            return null;

        return this.overlays.remove(id);
    }

    public void replaceOverlay(IOverlay oldOverlay, IOverlay newOverlay) {
        if (!(oldOverlay instanceof Overlay) || !(newOverlay instanceof Overlay))
            return;

        int index = overlays.indexOf(oldOverlay);
        if (index != -1)
            overlays.set(index, (Overlay) newOverlay);
    }
    
    public List<Overlay> getOverlays() {
        return this.overlays;
    }

    // ── IOverlayChain implementation ──

    public String getName() {
        return this.name;
    }

    public IOverlayChain setEnabled(boolean enable) {
        this.enabled = enable;
        return this;
    }

    public IOverlay getOverlay(int index) {
        return get(index);
    }
    
    public int size() {
        return overlays.size();
    }

    public IOverlay addOverlay(IOverlay.Type type) {
        return add(type);
    }

    public IOverlay removeOverlay(int index) {
        return deleteOverlay(index);
    }

    public boolean replaceOverlay(int index, IOverlay newOverlay) {
        if (index < 0 || index >= overlays.size() || !(newOverlay instanceof Overlay))
            return false;
        overlays.set(index, (Overlay) newOverlay);
        return true;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public OverlayChain enable(boolean enable) {
        this.enabled = enable;
        return this;
    }

    @Override
    public DataCompound serialize(DataCompound data) {
        data.putBoolean("hasOverlays", enabled);

        DataCompound rendering = data.child();
        for (int i = 0; i < overlays.size(); i++)
            rendering.put("overlay" + i, overlays.get(i));
        data.put("overlayData", rendering);


        if (!disabledParts.isEmpty()) {
            int[] arr = new int[disabledParts.size()];
            int i = 0;
            for (FacePartData.Part t : disabledParts)
                arr[i++] = t.ordinal();
            data.putIntArray("disabledParts", arr);
        }
        return data;
    }

    @Override
    public void deserialize(DataCompound data) {
        enabled = data.getBoolean("hasOverlays", enabled);
        overlays.clear();
        DataCompound rendering = data.get("overlayData");
        
        int i = 0;
        while (rendering.has("overlay" + i)) {
            DataCompound overlayData = rendering.get("overlay" + i);
            int type = overlayData.has("type") ? overlayData.getInt("type", 0) : 0;
            Overlay overlay = (Overlay) IOverlay.Type.create(type);

            if (overlay != null) {
                overlay.deserialize(overlayData);
                overlays.add(overlay);
            }
            i++;
        }

        if (data.has("disabledParts")) {
            disabledParts.clear();
            FacePartData.Part[] values = FacePartData.Part.values();
            for (int ordinal :  data.getIntArray("disabledParts", new int[0])) {
                if (ordinal >= 0 && ordinal < values.length)
                    disabledParts.add(values[ordinal]);
            }
        }
    }
}
