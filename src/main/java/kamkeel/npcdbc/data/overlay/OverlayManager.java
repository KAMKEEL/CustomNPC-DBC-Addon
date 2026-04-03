package kamkeel.npcdbc.data.overlay;

import kamkeel.npcdbc.data.race.serial.DataCompound;
import kamkeel.npcdbc.data.race.serial.DataSerializable;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public class OverlayManager implements DataSerializable {

    public final ArrayList<OverlayChain> chains = new ArrayList<>();
    public boolean enabled = true;


    public OverlayManager() {
    }

    public OverlayChain add(OverlayChain chain) {
        this.chains.add(chain);
        return chain;
    }

    public OverlayChain get(int id) {
        if (id < this.chains.size())
            return this.chains.get(id);
        return null;
    }

    public OverlayChain remove(int id) {
        if (id >= this.chains.size())
            return null;

        return this.chains.remove(id);
    }

    public boolean remove(OverlayChain chain) {
        return this.chains.remove(chain);
    }

    public List<OverlayChain> getChains() {
        return this.chains;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        DataCompound c = DataCompound.ofNbt(nbt);
        this.deserialize(c);
    }

    public NBTTagCompound writeToNBT() {
        DataCompound c = DataCompound.create();
        return this.serialize(c).toNbt();
    }

    @Override
    public DataCompound serialize(DataCompound data) {
        data.putBoolean("enabled", enabled);

        DataCompound rendering = data.child();
        for (int i = 0; i < chains.size(); i++)
            rendering.put("chain" + i, chains.get(i));
        data.put("overlayChains", rendering);

        return data;
    }

    @Override
    public void deserialize(DataCompound data) {
        enabled = data.getBoolean("enabled", enabled);
        DataCompound rendering = data.get("overlayChains");

        int i = 0;
        while (rendering.has("chain" + i)) {
            DataCompound overlayChain = rendering.get("chain" + i);
            OverlayChain chain = new OverlayChain();

            chain.deserialize(overlayChain);
            chains.add(chain);
            i++;
        }
    }
}
