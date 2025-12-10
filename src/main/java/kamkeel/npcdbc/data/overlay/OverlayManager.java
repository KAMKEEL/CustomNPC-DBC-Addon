package kamkeel.npcdbc.data.overlay;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;

public class OverlayManager {

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

    public void readFromNBT(NBTTagCompound c) {

    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound c = new NBTTagCompound();


        return c;
    }
}
