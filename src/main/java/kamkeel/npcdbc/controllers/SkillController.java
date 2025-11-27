package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.api.skill.ICustomSkill;
import kamkeel.npcdbc.api.skill.ISkillHandler;
import kamkeel.npcdbc.constants.DBCSyncType;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.skill.CustomSkill;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.get.DBCInfoSyncPacket;
import kamkeel.npcs.network.enums.EnumSyncAction;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import noppes.npcs.util.NBTJsonUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class SkillController implements ISkillHandler {
    public static SkillController Instance = new SkillController();
    public HashMap<Integer, CustomSkill> customSkills;
    public HashMap<Integer, CustomSkill> customSkillsSync = new HashMap<>();
    private HashMap<Integer, String> bootOrder;
    private int lastUsedID = 0;

    public SkillController() {
        Instance = this;
        customSkills = new HashMap<>();
        bootOrder = new HashMap<>();
    }

    public void load() {
        customSkills = new HashMap<>();
        bootOrder = new HashMap<>();
        lastUsedID = 0;
        LogWriter.info("Loading custom skills...");
        readCustomSkillMap();
        loadSkills();
        LogWriter.info("Done loading custom skills.");

//        CustomSkill skill = new CustomSkill(1, "test", 10, null, null);
//        delete(1);
//        skill.save();
    }

    public void delete(int id) {
        if (!this.customSkills.containsKey(id))
            return;

        CustomSkill skill = this.customSkills.remove(id);
        if (skill != null && skill.stringLiteralId != null) {
            File dir = this.getDir();
            for (File file : dir.listFiles()) {
                if (!file.isFile() || !file.getName().endsWith(".json"))
                    continue;
                if (file.getName().equals(skill.stringLiteralId + ".json")) {
                    file.delete();
                    DBCPacketHandler.Instance.sendToAll(new DBCInfoSyncPacket(DBCSyncType.SKILL, EnumSyncAction.REMOVE, skill.getId(), new NBTTagCompound()));
                    break;
                }
            }
            saveSkillLoadMap();
        }
    }

    @Override
    public void delete(ICustomSkill skill) {
        if (skill != null)
            delete(skill.getId());
    }

    @Override
    public ICustomSkill getSkill(int id) {
        return this.customSkills.get(id);
    }

    @Override
    public ICustomSkill getSkill(String stringLiteralId) {
        for (Map.Entry<Integer, CustomSkill> entrySkill : SkillController.Instance.customSkills.entrySet()) {
            if (entrySkill.getValue().stringLiteralId.equalsIgnoreCase(stringLiteralId)) {
                return entrySkill.getValue();
            }
        }
        return null;
    }

    @Override
    public ICustomSkill createSkill(String stringLiteralId) {
        return createSkill(stringLiteralId, 1);
    }

    @Override
    public ICustomSkill createSkill(String stringLiteralId, int maxLevel) {
        return createSkill(stringLiteralId, maxLevel, null, null);
    }

    @Override
    public ICustomSkill createSkill(String stringLiteralId, int maxLevel, int[] tpCosts, int[] mindCosts) {
        if (hasName(stringLiteralId))
            return getSkill(stringLiteralId);

        CustomSkill skill = new CustomSkill(getUnusedId(), stringLiteralId, maxLevel, tpCosts, mindCosts);
        int setID = skill.id;
        while (bootOrder.containsKey(setID) || customSkills.containsKey(setID)) {
            if (bootOrder.containsKey(setID))
                if (bootOrder.get(setID).equals(skill.stringLiteralId))
                    break;

            setID++;
        }
        skill.id = setID;
        customSkills.put(skill.id, skill);
        skill.save();
        return skill;
    }

    //////////////////////////////////////////
    //////////////////////////////////////////
    //
    //
    public int getUnusedId() {
        for (int catid : customSkills.keySet()) {
            if (catid > lastUsedID)
                lastUsedID = catid;
        }
        lastUsedID++;
        return lastUsedID;
    }

    private void loadSkills() {
        customSkills.clear();

        File dir = getDir();
        if (!dir.exists()) {
            dir.mkdir();
        } else {
            for (File file : dir.listFiles()) {
                if (!file.isFile() || !file.getName().endsWith(".json"))
                    continue;
                try {
                    CustomSkill skill = new CustomSkill();
                    skill.readFromNBT(NBTJsonUtil.LoadFile(file));
                    skill.stringLiteralId = file.getName().substring(0, file.getName().length() - 5);

                    if (skill.id == -1) {
                        skill.id = getUnusedId();
                    }

                    int originalID = skill.id;
                    int setID = skill.id;
                    while (bootOrder.containsKey(setID) || customSkills.containsKey(setID)) {
                        if (bootOrder.containsKey(setID))
                            if (bootOrder.get(setID).equals(skill.stringLiteralId))
                                break;

                        setID++;
                    }

                    skill.id = setID;
                    if (originalID != setID) {
                        LogWriter.info("Found Custom Skill ID Mismatch: " + skill.stringLiteralId + ", New ID: " + setID);
                        this.saveSkill(skill);
                    }

                    customSkills.put(skill.id, skill);
                } catch (Exception e) {
                    LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
                }
            }
        }
        saveSkillLoadMap();
    }

    public boolean hasName(String newName) {
        if (newName.trim().isEmpty())
            return true;
        for (CustomSkill skill : customSkills.values())
            if (skill.stringLiteralId.equals(newName))
                return true;
        return false;
    }

    public ICustomSkill saveSkill(ICustomSkill iskill) {
        CustomSkill skill = (CustomSkill) iskill;
        if (skill.getId() < 0) {
            skill.id = getUnusedId();
            while (hasName(skill.stringLiteralId))
                skill.stringLiteralId += "_";
        } else {
            CustomSkill existing = customSkills.get(skill.getId());
            if (existing != null && !existing.stringLiteralId.equals(skill.stringLiteralId))
                while (hasName(skill.stringLiteralId))
                    skill.stringLiteralId += "_";
        }

        customSkills.put(skill.id, skill);

        saveSkillLoadMap();

        File dir = this.getDir();
        if (!dir.exists())
            dir.mkdirs();

        File file = new File(dir, skill.stringLiteralId + ".json_new");
        File file2 = new File(dir, skill.stringLiteralId + ".json");

        try {
            NBTTagCompound nbtTagCompound = skill.writeToNBT();
            NBTJsonUtil.SaveFile(file, nbtTagCompound);
            if (file2.exists())
                file2.delete();
            file.renameTo(file2);
            DBCPacketHandler.Instance.sendToAll(new DBCInfoSyncPacket(DBCSyncType.SKILL, EnumSyncAction.UPDATE, -1, nbtTagCompound));
        } catch (Exception e) {
            LogWriter.except(e);
        }
        return customSkills.get(skill.getId());
    }

    private void readCustomSkillMap() {
        bootOrder.clear();
        try {
            File file = new File(getMapDir(), "customskills.dat");
            if (file.exists()) {
                loadCustomSkillMap(file);
            }
        } catch (Exception e) {
            try {
                File file = new File(getMapDir(), "customskills.dat_old");
                if (file.exists()) {
                    loadCustomSkillMap(file);
                }
            } catch (Exception ignored) {}
        }
    }

    private void loadCustomSkillMap(File file) throws IOException {
        DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
        readCustomSkillMap(var1);
        var1.close();
    }

    public void readCustomSkillMap(DataInputStream stream) throws IOException {
        NBTTagCompound nbtCompound = CompressedStreamTools.read(stream);
        this.readMapNBT(nbtCompound);
    }

    public void readMapNBT(NBTTagCompound compound) {
        lastUsedID = compound.getInteger("lastID");
        NBTTagList list = compound.getTagList("CustomSkills", 10);
        if (list != null) {
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound nbttagcompound = list.getCompoundTagAt(i);
                String formName = nbttagcompound.getString("Name");
                Integer key = nbttagcompound.getInteger("ID");
                bootOrder.put(key, formName);
            }
        }
    }

    public void deleteSkillFile(String name) {
        File dir = this.getDir();
        if (!dir.exists())
            dir.mkdirs();
        File file2 = new File(dir, name + ".json");
        if (file2.exists())
            file2.delete();
    }

    public NBTTagCompound writeMapNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList skillList = new NBTTagList();
        for (Integer key : customSkills.keySet()) {
            CustomSkill customSkill = customSkills.get(key);
            if (!customSkill.getStringId().isEmpty()) {
                NBTTagCompound formCompound = new NBTTagCompound();
                formCompound.setString("Name", customSkill.getStringId());
                formCompound.setInteger("ID", key);

                skillList.appendTag(formCompound);
            }
        }
        nbt.setTag("CustomSkills", skillList);
        nbt.setInteger("lastID", lastUsedID);
        return nbt;
    }

    public void saveSkillLoadMap() {
        try {
            File saveDir = getMapDir();
            File file = new File(saveDir, "customskills.dat_new");
            File file1 = new File(saveDir, "customskills.dat_old");
            File file2 = new File(saveDir, "customskills.dat");
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

    public File getMapDir() {
        File dir = CustomNpcs.getWorldSaveDirectory();
        if (!dir.exists())
            dir.mkdir();
        return dir;
    }
    private File getDir() {
        return new File(CustomNpcs.getWorldSaveDirectory(), "customskills");
    }

}
