package kamkeel.npcdbc.data.ability;

import kamkeel.npcdbc.controllers.AbilityController;
import kamkeel.npcdbc.data.AbilityWheelData;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.NBTTags;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class AbilityData {
    boolean isDBC = false;
    public int selectedAbility = -1;
    public HashSet<Integer> unlockedAbilities = new HashSet<>();
    public HashMap<Integer, Integer> abilityTimers = new HashMap<>();
    public HashMap<Integer, Integer> toggledAbilities = new HashMap<>();
    public AbilityWheelData[] abilityWheel = new AbilityWheelData[6];

    public AbilityData(boolean isDBC) {
        this.isDBC = isDBC;
    }

    public AbilityData(){
    }

    public boolean isDBC() {
        return isDBC;
    }

    public void setDBC(boolean DBC) {
        isDBC = DBC;
    }

    public void addAbility(Ability ability) {
        if (ability == null)
            return;

        unlockedAbilities.add(ability.id);
    }

    public void addAbility(int id) {
        if (!getAbilityMap().containsKey(id))
            return;

        unlockedAbilities.add(id);
    }

    public void addAbilityWheel(int wheelSlot, AbilityWheelData data) {
        if (wheelSlot > 5)
            return;
        abilityWheel[wheelSlot].readFromNBT(data.writeToNBT(new NBTTagCompound()));
    }

    public boolean hasAbilityUnlocked(int id) {
        return unlockedAbilities.contains(id);
    }

    public boolean removeAbility(Ability ability) {
        if (ability == null)
            return false;

        return unlockedAbilities.remove(ability.id);
    }

    public boolean removeAbility(int id) {
        return unlockedAbilities.remove(id);
    }

    public void removeAbilityWheel(int wheelSlot) {
        if (wheelSlot <= 5 && wheelSlot >= 0)
            abilityWheel[wheelSlot].reset();
    }

    public Ability getUnlockedAbility(int id) {
        if (unlockedAbilities.contains(id))
            return getAbilityMap().get(id);

        return null;
    }

    public boolean hasSelectedAbility() {
        return selectedAbility > -1 && getSelectedAbility() != null;
    }

    public boolean hasSelectedAbility(int id) {
        return selectedAbility == id;
    }

    public boolean hasSelectedAbility(Ability ability) {
        if (ability == null)
            return false;
        return selectedAbility == ability.id;
    }

    public boolean hasAbility(int id) {
        return unlockedAbilities.contains(id);
    }

    public boolean hasAbility(Ability ability) {
        if (ability == null)
            return false;
        return unlockedAbilities.contains(ability.id);
    }

    public Ability getSelectedAbility() {
        return getAbilityMap().get(selectedAbility);
    }

    public void setSelectedAbility(int id) {
        if (unlockedAbilities.contains(id) && getAbilityMap().containsKey(id))
            selectedAbility = id;
    }

    public void setSelectedAbility(Ability ability) {
        if (ability != null && unlockedAbilities.contains(ability.id) && getAbilityMap().containsKey(ability.id))
            selectedAbility = ability.id;
    }

    public void clearAllAbilities() {
        resetAbilityData(true);
    }

    public void resetAbilityData(boolean removeAbilities) {
        selectedAbility = -1;
        if (removeAbilities)
            unlockedAbilities.clear();

        for (AbilityWheelData abilityWheelData : abilityWheel) abilityWheelData.reset();
    }

    public void addCooldown(int abilityId, int timeInTicks) {
        if (!abilityTimers.containsKey(abilityId))
            abilityTimers.put(abilityId, timeInTicks);
        abilityTimers.replace(abilityId, timeInTicks);
    }

    public void decrementCooldown(int abilityId) {
        if (abilityTimers.containsKey(abilityId)) {
            int currentTime = abilityTimers.get(abilityId);
            if (currentTime > 0)
                abilityTimers.replace(abilityId, currentTime - 1);
            else if (currentTime == 0) {
                abilityTimers.remove(abilityId);
            }
        }
    }

    public int getCooldown(int abiltyId) {
        if (abilityTimers.containsKey(abiltyId))
            return abilityTimers.get(abiltyId);
        return -1;

    }

    public boolean hasCooldown(int abilityId) {
        if (abilityTimers.containsKey(abilityId))
            return abilityTimers.get(abilityId) > -1;
        return false;
    }

    private Map<Integer, ? extends Ability> getAbilityMap() {
        if (isDBC)
            return AbilityController.getInstance().addonAbilities;

        return AbilityController.getInstance().abilities;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound abilities = new NBTTagCompound();

        abilities.setInteger("SelectedAbility", selectedAbility);
        abilities.setTag("UnlockedAbilities", NBTTags.nbtIntegerSet(unlockedAbilities));
        abilities.setTag("AbilityCooldowns", NBTTags.nbtIntegerIntegerMap(abilityTimers));

        compound.setTag(isDBC ? "DBCAbilityData" : "CustomAbilityData", abilities);
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        NBTTagCompound abilities = compound.getCompoundTag(isDBC ? "DBCAbilityData" : "CustomAbilityData");

        selectedAbility = abilities.getInteger("SelectedAbility");
        unlockedAbilities = NBTTags.getIntegerSet(abilities.getTagList("UnlockedAbilities", 10));
        abilityTimers = NBTTags.getIntegerIntegerMap(abilities.getTagList("AbilityCooldowns", 10));
    }
}
