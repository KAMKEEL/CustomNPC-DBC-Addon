package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.api.outline.IOutlineHandler;
import kamkeel.npcdbc.constants.DBCSyncType;
import kamkeel.npcdbc.api.outline.IOutline;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.get.DBCInfoSync;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import noppes.npcs.constants.EnumPacketClient;
import noppes.npcs.util.NBTJsonUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class OutlineController implements IOutlineHandler {
    public static OutlineController Instance = new OutlineController();
    public HashMap<Integer, Outline> customOutlinesSync = new HashMap();
    public HashMap<Integer, Outline> customOutlines;
    private HashMap<Integer, String> bootOrder;
    private int lastUsedID = 0;

    public OutlineController() {
        Instance = this;
        customOutlines = new HashMap<>();
        bootOrder = new HashMap<>();
    }

    public void load() {
        customOutlines = new HashMap<>();
        bootOrder = new HashMap<>();
        LogWriter.info("Loading custom outlines...");
        readCustomOutlineMap();
        loadOutlines();
        LogWriter.info("Done loading custom outlines.");
    }

    @Override
    public IOutline createOutline(String name) {
        if (hasName(name))
            return get(name);
        else {
            Outline outline = new Outline();
            outline.name = name;
            if (outline.id == -1) {
                outline.id = getUnusedId();
            }
            int setID = outline.id;
            while (bootOrder.containsKey(setID) || customOutlines.containsKey(setID)) {
                if (bootOrder.containsKey(setID))
                    if (bootOrder.get(setID).equals(outline.name))
                        break;

                setID++;
            }
            customOutlines.put(outline.id, outline);
            outline.save();
            return outline;
        }
    }

    @Override
    public IOutline saveOutline(IOutline customOutline) {
        if (customOutline.getID() < 0) {
            customOutline.setID(getUnusedId());
            while (hasName(customOutline.getName()))
                customOutline.setName(customOutline.getName() + "_");
        } else {
            Outline existing = customOutlines.get(customOutline.getID());
            if (existing != null && !existing.name.equals(customOutline.getName()))
                while (hasName(customOutline.getName()))
                    customOutline.setName(customOutline.getName() + "_");
        }

        customOutlines.remove(customOutline.getID());
        customOutlines.put(customOutline.getID(), (Outline) customOutline);

        saveOutlineLoadMap();

        File dir = this.getDir();
        if (!dir.exists())
            dir.mkdirs();

        File file = new File(dir, customOutline.getName() + ".json_new");
        File file2 = new File(dir, customOutline.getName() + ".json");

        try {
            NBTTagCompound nbtTagCompound = ((Outline) customOutline).writeToNBT();
            NBTJsonUtil.SaveFile(file, nbtTagCompound);
            if (file2.exists())
                file2.delete();
            file.renameTo(file2);
            PacketHandler.Instance.sendToAll(new DBCInfoSync(DBCSyncType.OUTLINE, EnumPacketClient.SYNC_UPDATE, nbtTagCompound, -1).generatePacket());
        } catch (Exception e) {
            LogWriter.except(e);
        }
        return customOutlines.get(customOutline.getID());
    }

    @Override
    public void deleteOutlineFile(String name) {
        File dir = this.getDir();
        if (!dir.exists())
            dir.mkdirs();
        File file2 = new File(dir, name + ".json");
        if (file2.exists())
            file2.delete();
    }

    private void loadOutlines() {
        customOutlines.clear();

        File dir = getDir();
        if (!dir.exists()) {
            dir.mkdir();
        } else {
            for (File file : dir.listFiles()) {
                if (!file.isFile() || !file.getName().endsWith(".json"))
                    continue;
                try {
                    Outline outline = new Outline();
                    outline.readFromNBT(NBTJsonUtil.LoadFile(file));
                    outline.name = file.getName().substring(0, file.getName().length() - 5);

                    if (outline.id == -1) {
                        outline.id = getUnusedId();
                    }

                    int originalID = outline.id;
                    int setID = outline.id;
                    while (bootOrder.containsKey(setID) || customOutlines.containsKey(setID)) {
                        if (bootOrder.containsKey(setID))
                            if (bootOrder.get(setID).equals(outline.name))
                                break;

                        setID++;
                    }

                    outline.id = setID;
                    if (originalID != setID) {
                        LogWriter.info("Found Custom Outline ID Mismatch: " + outline.name + ", New ID: " + setID);
                        outline.save();
                    }

                    customOutlines.put(outline.id, outline);
                } catch (Exception e) {
                    LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
                }
            }
        }
        saveOutlineLoadMap();
    }

    @Override
    public boolean hasName(String newName) {
        if (newName.trim().isEmpty())
            return true;
        for (Outline outline : customOutlines.values())
            if (outline.name.equals(newName))
                return true;
        return false;
    }


    @Override
    public IOutline get(String name) {
        return getOutlineFromName(name);
    }

    @Override
    public IOutline get(int id) {
        return this.customOutlines.get(id);
    }

    @Override
    public boolean has(String name) {
        return getOutlineFromName(name) != null;
    }

    @Override
    public boolean has(int id) {
        return get(id) != null;
    }

    @Override
    public void delete(int id) {
        if (!this.customOutlines.containsKey(id))
            return;

        Outline foundOutline = this.customOutlines.remove(id);
        if (foundOutline != null && foundOutline.name != null) {
            File dir = this.getDir();
            for (File file : dir.listFiles()) {
                if (!file.isFile() || !file.getName().endsWith(".json"))
                    continue;
                if (file.getName().equals(foundOutline.name + ".json")) {
                    file.delete();
                    PacketHandler.Instance.sendToAll(new DBCInfoSync(DBCSyncType.OUTLINE, EnumPacketClient.SYNC_REMOVE, new NBTTagCompound(), foundOutline.getID()).generatePacket());
                    break;
                }
            }
            saveOutlineLoadMap();
        }
    }

    @Override
    public void delete(String name) {
        Outline delete = (Outline) getOutlineFromName(name);
        if (delete != null) {
            Outline foundOutline = this.customOutlines.remove(delete.getID());
            if (foundOutline != null && foundOutline.name != null) {
                File dir = this.getDir();
                for (File file : dir.listFiles()) {
                    if (!file.isFile() || !file.getName().endsWith(".json"))
                        continue;
                    if (file.getName().equals(foundOutline.name + ".json")) {
                        file.delete();
                        PacketHandler.Instance.sendToAll(new DBCInfoSync(DBCSyncType.OUTLINE, EnumPacketClient.SYNC_REMOVE, new NBTTagCompound(), foundOutline.getID()).generatePacket());
                        break;
                    }
                }
                saveOutlineLoadMap();
            }
        }
    }

    private File getDir() {
        return new File(CustomNpcs.getWorldSaveDirectory(), "customoutlines");
    }

    public int getUnusedId() {
        for (int catid : customOutlines.keySet()) {
            if (catid > lastUsedID)
                lastUsedID = catid;
        }
        lastUsedID++;
        return lastUsedID;
    }

    @Override
    public String[] getNames() {
        String[] names = new String[customOutlines.size()];
        int i = 0;
        for (Outline outline : customOutlines.values()) {
            names[i] = outline.name.toLowerCase();
            i++;
        }
        return names;
    }

    @Override
    public IOutline[] getOutlines() {
        ArrayList<IOutline> outlines = new ArrayList<>(this.customOutlines.values());
        return outlines.toArray(new IOutline[0]);
    }

    @Override
    public IOutline getOutlineFromName(String outlineName) {
        for (Map.Entry<Integer, Outline> entryOutline : customOutlines.entrySet()) {
            if (entryOutline.getValue().name.equalsIgnoreCase(outlineName)) {
                return entryOutline.getValue();
            }
        }
        return null;
    }

    public File getMapDir() {
        File dir = CustomNpcs.getWorldSaveDirectory();
        if (!dir.exists())
            dir.mkdir();
        return dir;
    }
    ////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////

    public void readCustomOutlineMap() {
        bootOrder.clear();

        try {
            File file = new File(getMapDir(), "customoutlines.dat");
            if (file.exists()) {
                loadCustomOutlineMap(file);
            }
        } catch (Exception e) {
            try {
                File file = new File(getMapDir(), "customoutlines.dat_old");
                if (file.exists()) {
                    loadCustomOutlineMap(file);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void readCustomOutlineMap(DataInputStream stream) throws IOException {
        NBTTagCompound nbtCompound = CompressedStreamTools.read(stream);
        this.readMapNBT(nbtCompound);
    }

    private void loadCustomOutlineMap(File file) throws IOException {
        DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
        readCustomOutlineMap(var1);
        var1.close();
    }

    public void readMapNBT(NBTTagCompound compound) {
        lastUsedID = compound.getInteger("lastID");
        NBTTagList list = compound.getTagList("CustomOutlines", 10);
        if (list != null) {
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
                String outlineName = nbttagcompound.getString("Name");
                Integer key = nbttagcompound.getInteger("ID");
                bootOrder.put(key, outlineName);
            }
        }
    }

    public NBTTagCompound writeMapNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList outlineList = new NBTTagList();
        for (Integer key : customOutlines.keySet()) {
            Outline outline = customOutlines.get(key);
            if (!outline.getName().isEmpty()) {
                NBTTagCompound outlineCompound = new NBTTagCompound();
                outlineCompound.setString("Name", outline.getName());
                outlineCompound.setInteger("ID", key);

                outlineList.appendTag(outlineCompound);
            }
        }
        nbt.setTag("CustomOutlines", outlineList);
        nbt.setInteger("lastID", lastUsedID);
        return nbt;
    }

    public void saveOutlineLoadMap() {
        try {
            File saveDir = getMapDir();
            File file = new File(saveDir, "customoutlines.dat_new");
            File file1 = new File(saveDir, "customoutlines.dat_old");
            File file2 = new File(saveDir, "customoutlines.dat");
            CompressedStreamTools.writeCompressed(this.writeMapNBT(), new FileOutputStream(file));
            if (file1.exists()) {
                file1.delete();
            }
            file2.renameTo(file1);
            if (file2.exists()) {
                file2.delete();
            }
            file.renameTo(file2);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            LogWriter.except(e);
        }
    }

    public static OutlineController getInstance() {
        return Instance;
    }


}
