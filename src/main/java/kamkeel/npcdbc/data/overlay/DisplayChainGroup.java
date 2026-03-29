package kamkeel.npcdbc.data.overlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DisplayChainGroup {

    private final List<DisplayChain> chains = new ArrayList<>();

    private DisplayChainGroup() {}

    private DisplayChainGroup(DisplayChain... chains) {
        for (DisplayChain c : chains)
            this.chains.add(c);
    }
    
    public static DisplayChainGroup of(DisplayChain... chains) {
        return new DisplayChainGroup(chains);
    }

    public void add(DisplayChain chain) {
        chains.add(chain);
    }

    public List<DisplayChain> getChains() {
        return Collections.unmodifiableList(chains);
    }

    public DisplayLayer getLayer(String slotId) {
        String key = slotId.toLowerCase();
        for (int i = chains.size() - 1; i >= 0; i--) {
            DisplayLayer found = chains.get(i).getLayer(key);
            if (found != null) return found;
        }
        return null;
    }

    public boolean isEmpty() {
        return chains.isEmpty();
    }

    public int size() {
        return chains.size();
    }
}
