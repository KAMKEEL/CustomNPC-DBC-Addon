package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.api.ability.IAbility;
import kamkeel.npcdbc.constants.DBCSyncType;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.ability.Ability;
import kamkeel.npcdbc.data.ability.AbilityData;
import kamkeel.npcdbc.data.ability.AbilityScript;
import kamkeel.npcdbc.data.ability.AddonAbility;
import kamkeel.npcdbc.data.ability.types.*;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.get.DBCInfoSyncPacket;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcs.network.enums.EnumSyncAction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import noppes.npcs.util.NBTJsonUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class AbilityController {
    public static AbilityController Instance = new AbilityController();
    public HashMap<Integer, Ability> abilitiesSync = new HashMap();
    public HashMap<Integer, Ability> abilities;
    public HashMap<Integer, AddonAbility> addonAbilities;
    public HashMap<Integer, AbilityScript> abilityScriptHandlers = new HashMap<>();
    private HashMap<Integer, String> bootOrder;
    private int lastUsedID = 0;

    public AbilityController() {
        Instance = this;
        abilities = new HashMap<>();
        addonAbilities = new HashMap<>();
        bootOrder = new HashMap<>();
    }

    public void load() {
        abilities = new HashMap<>();
        addonAbilities = new HashMap<>();
        bootOrder = new HashMap<>();
        lastUsedID = 0;
        loadAddonAbilities();
        LogWriter.info("Loading abilities...");
        readAbilityMap();
        loadAbilities();
        LogWriter.info("Done loading abilities.");
    }

    public IAbility createAbility(String name) {
        if (hasName(name))
            return get(name);
        else {
            Ability ability = new Ability();
            ability.name = name;
            if (ability.id == -1) {
                ability.id = getUnusedId();
            }
            int setID = ability.id;
            while (bootOrder.containsKey(setID) || abilities.containsKey(setID)) {
                if (bootOrder.containsKey(setID))
                    if (bootOrder.get(setID).equals(ability.name))
                        break;

                setID++;
            }
            abilities.put(ability.id, ability);
            ability.save();
            return ability;
        }
    }

    private void loadAddonAbilities() {
        addonAbility(new DashAbilities.Swoop());
        addonAbility(new DashAbilities.ZVanish());
        addonAbility(new DashAbilities.Afterimage());
        addonAbility(new Fusion());
        addonAbility(new KiFist());
        addonAbility(new KiProtection());
        addonAbility(new FriendlyFist());
        addonAbility(new KiWeapon());
        addonAbility(new NamekRegen());
        addonAbility(new MultiuseTest());
    }

    private void addonAbility(AddonAbility ability) {
        addonAbilities.put(ability.id, ability);
    }

    private void loadAbilities() {
        abilities.clear();

        File dir = getDir();
        if (!dir.exists()) {
            dir.mkdir();
        } else {
            for (File file : dir.listFiles()) {
                if (!file.isFile() || !file.getName().endsWith(".json"))
                    continue;
                try {
                    Ability ability = new Ability();
                    ability.readFromNBT(NBTJsonUtil.LoadFile(file));
                    ability.name = file.getName().substring(0, file.getName().length() - 5);

                    if (ability.id == -1) {
                        ability.id = getUnusedId();
                    }

                    int originalID = ability.id;
                    int setID = ability.id;
                    while (bootOrder.containsKey(setID) || abilities.containsKey(setID)) {
                        if (bootOrder.containsKey(setID))
                            if (bootOrder.get(setID).equals(ability.name))
                                break;

                        setID++;
                    }

                    ability.id = setID;
                    if (originalID != setID) {
                        LogWriter.info("Found Ability ID Mismatch: " + ability.name + ", New ID: " + setID);
                        ability.save();
                    }

                    abilities.put(ability.id, ability);
                } catch (Exception e) {
                    LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
                }
            }
        }
        saveAbilityLoadMap();
    }

    private File getDir() {
        return new File(CustomNpcs.getWorldSaveDirectory(), "abilities");
    }

    public int getUnusedId() {
        for (int catid : abilities.keySet()) {
            if (catid > lastUsedID)
                lastUsedID = catid;
        }
        lastUsedID++;
        return lastUsedID;
    }

    public IAbility saveAbility(IAbility ability) {
        if (ability.getID() < 0) {
            ability.setID(getUnusedId());
            while (hasName(ability.getName()))
                ability.setName(ability.getName() + "_");
        } else {
            Ability existing = abilities.get(ability.getID());
            if (existing != null && !existing.name.equals(ability.getName()))
                while (hasName(ability.getName()))
                    ability.setName(ability.getName() + "_");
        }

        abilities.remove(ability.getID());
        abilities.put(ability.getID(), (Ability) ability);

        saveAbilityLoadMap();

        // Save Ability File
        File dir = this.getDir();
        if (!dir.exists())
            dir.mkdirs();

        File file = new File(dir, ability.getName() + ".json_new");
        File file2 = new File(dir, ability.getName() + ".json");

        try {
            NBTTagCompound nbtTagCompound = ((Ability) ability).writeToNBT(true);
            NBTJsonUtil.SaveFile(file, nbtTagCompound);
            if (file2.exists())
                file2.delete();
            file.renameTo(file2);
            // sync type ABILITY
            DBCPacketHandler.Instance.sendToAll(new DBCInfoSyncPacket(DBCSyncType.ABILITY, EnumSyncAction.UPDATE, -1, nbtTagCompound));
        } catch (Exception e) {
            LogWriter.except(e);
        }
        return abilities.get(ability.getID());
    }

    public boolean hasName(String newName) {
        if (newName.trim().isEmpty())
            return true;
        for (Ability ability : abilities.values())
            if (ability.name.equals(newName))
                return true;
        return false;
    }

    public void delete(String name) {
        Ability delete = getAbilityFromName(name, false);
        if (delete != null) {
            Ability foundAbility = this.abilities.remove(delete.getID());
            abilityScriptHandlers.remove(delete.getID());
            if (foundAbility != null && foundAbility.name != null) {
                File dir = this.getDir();
                for (File file : dir.listFiles()) {
                    if (!file.isFile() || !file.getName().endsWith(".json"))
                        continue;
                    if (file.getName().equals(foundAbility.name + ".json")) {
                        file.delete();
                        // sync type ABILITY
                        DBCPacketHandler.Instance.sendToAll(new DBCInfoSyncPacket(DBCSyncType.ABILITY, EnumSyncAction.REMOVE, foundAbility.getID(), new NBTTagCompound()));
                        break;
                    }
                }
                saveAbilityLoadMap();
            }
        }
    }

    public void delete(int id) {
        if (!this.abilities.containsKey(id))
            return;

        Ability foundAbility = this.abilities.remove(id);
        if (foundAbility != null && foundAbility.name != null) {
            File dir = this.getDir();
            for (File file : dir.listFiles()) {
                if (!file.isFile() || !file.getName().endsWith(".json"))
                    continue;
                if (file.getName().equals(foundAbility.name + ".json")) {
                    file.delete();
                    // sync type ABILITY
                    DBCPacketHandler.Instance.sendToAll(new DBCInfoSyncPacket(DBCSyncType.ABILITY, EnumSyncAction.REMOVE, foundAbility.getID(), new NBTTagCompound()));
                    break;
                }
            }
            saveAbilityLoadMap();
        }
    }

    public boolean has(String name) {
        return getAbilityFromName(name, false) != null;
    }

    public boolean has(int id) {
        return get(id, false) != null;
    }

    public boolean has(String name, boolean dbcAbility) {
        return getAbilityFromName(name, dbcAbility) != null;
    }

    public boolean has(int id, boolean dbcAbility) {
        return get(id, dbcAbility) != null;
    }

    public IAbility get(int id) {
        return get(id, false);
    }

    public IAbility get(String name) {
        return getAbilityFromName(name, false);
    }

    public IAbility get(int id, boolean dbcAbility) {
        if (id == -1)
            return null;
        return getAbilityMap(dbcAbility).get(id);
    }

    public IAbility get(String name, boolean dbcAbility) {
        return getAbilityFromName(name, dbcAbility);
    }

    public IAbility[] getAbilities() {
        ArrayList<Ability> abilities = new ArrayList<>(this.abilities.values());
        return abilities.toArray(new IAbility[0]);
    }

    public AddonAbility[] getDBCAbilities() {
        ArrayList<AddonAbility> addonAbilities = new ArrayList<>(this.addonAbilities.values());
        return addonAbilities.toArray(new AddonAbility[0]);
    }

    public Ability getAbilityFromName(String abilityName, boolean dbcAbility) {
        for (Map.Entry<Integer, ? extends Ability> ability : getAbilityMap(dbcAbility).entrySet()) {
            if (ability.getValue().name.equalsIgnoreCase(abilityName)) {
                return ability.getValue();
            }
        }
        return null;
    }

    public String[] getNames() {
        String[] names = new String[abilities.size()];
        int i = 0;
        for (Ability ability : abilities.values()) {
            names[i] = ability.name.toLowerCase();
            i++;
        }
        return names;
    }

    private Map<Integer, ? extends Ability> getAbilityMap(boolean dbcAbility) {
        return dbcAbility ? addonAbilities : abilities;
    }

    public File getMapDir() {
        File dir = CustomNpcs.getWorldSaveDirectory();
        if (!dir.exists())
            dir.mkdir();
        return dir;
    }

    public void readAbilityMap() {
        bootOrder.clear();

        try {
            File file = new File(getMapDir(), "abilities.dat");
            if (file.exists()) {
                loadAbilityMap(file);
            }
        } catch (Exception e) {
            try {
                File file = new File(getMapDir(), "abilities.dat_old");
                if (file.exists()) {
                    loadAbilityMap(file);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public NBTTagCompound writeMapNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList abilityList = new NBTTagList();
        for (Integer key : abilities.keySet()) {
            Ability ability = abilities.get(key);
            if (!ability.getName().isEmpty()) {
                NBTTagCompound abilityCompound = new NBTTagCompound();
                abilityCompound.setString("name", ability.getName());
                abilityCompound.setInteger("ID", key);

                abilityList.appendTag(abilityCompound);
            }
        }
        nbt.setTag("Abilities", abilityList);
        nbt.setInteger("lastID", lastUsedID);
        return nbt;
    }

    public void readMapNBT(NBTTagCompound compound) {
        lastUsedID = compound.getInteger("lastID");
        NBTTagList list = compound.getTagList("Abilities", 10);
        if (list != null) {
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
                String abilityName = nbttagcompound.getString("name");
                Integer key = nbttagcompound.getInteger("ID");
                bootOrder.put(key, abilityName);
            }
        }
    }

    private void loadAbilityMap(File file) throws IOException {
        DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
        readAbilityMap(var1);
        var1.close();
    }

    public void readAbilityMap(DataInputStream stream) throws IOException {
        NBTTagCompound nbtCompound = CompressedStreamTools.read(stream);
        this.readMapNBT(nbtCompound);
    }

    public void saveAbilityLoadMap() {
        try {
            File saveDir = getMapDir();
            File file = new File(saveDir, "abilities.dat_new");
            File file1 = new File(saveDir, "abilities.dat_old");
            File file2 = new File(saveDir, "abilities.dat");
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

    public static AbilityController getInstance() {
        return Instance;
    }

    public void deleteAbilityFile(String name) {
        File dir = this.getDir();
        if (!dir.exists())
            dir.mkdirs();
        File file2 = new File(dir, name + ".json");
        if (file2.exists())
            file2.delete();
    }
}
