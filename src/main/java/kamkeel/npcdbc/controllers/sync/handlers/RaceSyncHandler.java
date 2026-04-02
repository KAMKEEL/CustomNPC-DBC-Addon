package kamkeel.npcdbc.controllers.sync.handlers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.controllers.RaceController;
import kamkeel.npcdbc.data.race.Race;
import kamkeel.npcs.controllers.sync.SyncHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaceSyncHandler implements SyncHandler {

    @Override
    public NBTTagCompound serializeAll() {
        NBTTagList list = new NBTTagList();
        for (Race race : RaceController.getInstance().getRaceOrder()) {
            list.appendTag(race.writeToNBT());
        }
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("Data", list);
        return compound;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleReload(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("Data", 10);
        List<Race> newOrder = new ArrayList<>();
        Map<String, Race> newRaces = new HashMap<>();

        for (int i = 0; i < list.tagCount(); i++) {
            Race race = new Race();
            race.readFromNBT(list.getCompoundTagAt(i));
            newRaces.put(race.getName(), race);
            newOrder.add(race);
        }

        RaceController.getInstance().setRaceData(newRaces, newOrder);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleUpdate(NBTTagCompound compound, String key) {
        Race race = new Race();
        race.readFromNBT(compound);
        RaceController.getInstance().put(race);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientHandleRemove(String key) {
        RaceController.getInstance().remove(key);
    }

    @Override
    public boolean supportsUpdate() {
        return true;
    }

    @Override
    public boolean supportsRemove() {
        return true;
    }
}
