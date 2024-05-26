package kamkeel.npcdbc.data.dbcdata;

import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.i.ExtendedPlayer;
import JinRyuu.JRMCore.server.config.dbc.JGConfigRaces;
import JinRyuu.JRMCore.server.config.dbc.JGConfigUltraInstinct;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.Effects;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.statuseffect.PlayerEffect;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.util.ValueUtil;

import java.util.*;

import static JinRyuu.JRMCore.JRMCoreH.getMajinAbsorptionValueS;
import static JinRyuu.JRMCore.JRMCoreH.nbt;

public class DBCDataStats {
    private final DBCData data;

    public DBCDataStats(DBCData dbcData) {
        this.data = dbcData;
    }

    public HashMap<Integer, PlayerEffect> getPlayerEffects() {
        return data.currentEffects;
    }

    public void setCurrentEffects(HashMap<Integer, PlayerEffect> setVals) {
        NBTTagCompound raw = data.getRawCompound();
        data.currentEffects = updateEffects(setVals);
        saveEffectsNBT(raw);
    }

    public HashMap<Integer, PlayerEffect> updateEffects(HashMap<Integer, PlayerEffect> setVals) {
        HashMap<Integer, PlayerEffect> createdMap = new HashMap<>();
        for (PlayerEffect playerEffect : setVals.values()) {
            PlayerEffect newEffect;
            if (data.currentEffects.containsKey(playerEffect.id)) {
                newEffect = data.currentEffects.get(playerEffect.id);
                newEffect.duration = playerEffect.duration;
                newEffect.level = playerEffect.level;
                createdMap.put(playerEffect.id, newEffect);
            } else {
                createdMap.put(playerEffect.id, new PlayerEffect(playerEffect.id, playerEffect.duration, playerEffect.level));
            }
        }
        return createdMap;
    }

    public void saveEffectsNBT(NBTTagCompound nbt) {
        NBTTagList nbttaglist = new NBTTagList();

        // Copy the collection to avoid ConcurrentModificationException
        List<PlayerEffect> effectsCopy = new ArrayList<>(data.currentEffects.values());
        for (PlayerEffect playerEffect : effectsCopy) {
            nbttaglist.appendTag(playerEffect.writeEffectData(new NBTTagCompound()));
        }

        nbt.setTag("addonActiveEffects", nbttaglist);
    }

    public void decrementActiveEffects() {
        HashMap<Integer, PlayerEffect> currentEffects = StatusEffectController.Instance.playerEffects.get(Utility.getUUID(data.player));
        Iterator<Map.Entry<Integer, PlayerEffect>> iterator = currentEffects.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, PlayerEffect> entry = iterator.next();
            PlayerEffect currentEffect = entry.getValue();
            if (currentEffect == null) {
                iterator.remove();
                continue;
            }

            if (currentEffect.duration == -100)
                continue;
            else if (currentEffect.duration <= 0) {
                StatusEffect parent = StatusEffectController.Instance.get(currentEffect.id);
                if (parent != null){
                    parent.runout(data.player, currentEffect);
                    parent.kill(data.player, currentEffect);
                }
                iterator.remove();
            } else
                currentEffect.duration--;
        }
        setCurrentEffects(currentEffects);
    }

    public int[] getAllAttributes() {
        return new int[]{data.STR, data.DEX, data.CON, data.WIL, data.MND, data.SPI};
    }

    public int getFullAttribute(int attri) {
        boolean majin = JRMCoreH.StusEfcts(12, data.StatusEffects);
        boolean fusion = (JRMCoreH.StusEfcts(10, data.StatusEffects) || JRMCoreH.StusEfcts(11, data.StatusEffects));
        boolean legendary = JRMCoreH.StusEfcts(14, data.StatusEffects);
        boolean kaioken = JRMCoreH.StusEfcts(5, data.StatusEffects);
        boolean mystic = JRMCoreH.StusEfcts(13, data.StatusEffects);
        boolean ui = JRMCoreH.StusEfcts(19, data.StatusEffects);
        boolean GoD = JRMCoreH.StusEfcts(20, data.StatusEffects);

        return JRMCoreH.getPlayerAttribute(data.player, getAllAttributes(), attri, data.State, data.State2, data.Race, data.RacialSkills, (int) data.Release, data.ArcReserve, legendary, majin, kaioken, mystic, ui, GoD, data.Powertype, data.Skills.split(","), fusion, data.MajinAbsorptionData);

    }

    public int[] getAllFullAttributes() {
        boolean majin = JRMCoreH.StusEfcts(12, data.StatusEffects);
        boolean fusion = (JRMCoreH.StusEfcts(10, data.StatusEffects) || JRMCoreH.StusEfcts(11, data.StatusEffects));
        boolean legendary = JRMCoreH.StusEfcts(14, data.StatusEffects);
        boolean kaioken = JRMCoreH.StusEfcts(5, data.StatusEffects);
        boolean mystic = JRMCoreH.StusEfcts(13, data.StatusEffects);
        boolean ui = JRMCoreH.StusEfcts(19, data.StatusEffects);
        boolean GoD = JRMCoreH.StusEfcts(20, data.StatusEffects);
        int[] a = new int[6];
        for (int i = 0; i <= 5; i++)
            a[i] = JRMCoreH.getPlayerAttribute(data.player, getAllAttributes(), i, data.State, data.State2, data.Race, data.RacialSkills, data.Release, data.ArcReserve, legendary, majin, kaioken, mystic, ui, GoD, data.Powertype, data.Skills.split(","), fusion, data.MajinAbsorptionData);

        return a;
    }

    public int getExtraOutput(int att, int release) {
        int extraoutput = 0;

        if (att == 0) {
            int maxki = getMaxStat(5);
            extraoutput = (int) (JRMCoreH.SklLvl(12, data.Skills.split(",")) * 0.0025 * maxki * release * 0.01);
        } else if (att == 1) {
            int maxki = getMaxStat(5);
            extraoutput = (int) (JRMCoreH.SklLvl(11, data.Skills.split(",")) * 0.005 * maxki * release * 0.01);
        } else if (att == 5)
            extraoutput = getMaxStat(5) - JRMCoreH.stat(data.player, att, data.Powertype, att, data.SPI, data.Race, data.Class, JRMCoreH.SklLvl_KiBs(data.Skills.split(","), 1));

        return extraoutput;
    }

    public int getMaxStat(int attributeID) { // gets max player stat, 0 dmg 1 def only, rest are
        int attribute = 0;

        if (attributeID == 0 || attributeID == 1 || attributeID == 4)
            attribute = getFullAttribute(attributeID);
        else
            attribute = getAllAttributes()[attributeID];

        float f = attributeID == 5 ? JRMCoreH.SklLvl_KiBs(data.Skills.split(","), 1) : 0f;
        int stat = JRMCoreH.stat(data.player, attributeID, data.Powertype, attributeID, attribute, data.Race, data.Class, f);

        if (attributeID == 0)
            stat += getExtraOutput(attributeID, 100);
        else if (attributeID == 1)
            stat += getExtraOutput(attributeID, 100);

        return stat;
    }


    public int getCurrentStat(int attribute) { // gets stat at current release
        return (int) (getMaxStat(attribute) * data.Release * 0.01D * JRMCoreH.weightPerc(0, data.player));
    }

    public double getCurrentMulti() {
        return (double) getFullAttribute(0) / data.STR;

    }

    public int getMaxBody() {
        return JRMCoreH.stat(data.player, 2, data.Powertype, 2, data.CON, data.Race, data.Class, 0);
    }

    public int getMaxStamina() {
        return JRMCoreH.stat(data.player, 2, data.Powertype, 3, data.CON, data.Race, data.Class, 0);
    }

    public int getMaxKi() {
        return JRMCoreH.stat(data.player, 5, data.Powertype, 5, data.SPI, data.Race, data.Class, JRMCoreH.SklLvl_KiBs(data.Skills.split(","), 1));
    }

    public int getMaxFusionBody() {
        int con = data.CON;
        if(isFused()){
            EntityPlayer spectator = getSpectatorEntity();
            if(spectator != null){
                DBCData specData = DBCData.get(spectator);
                con = Math.min(data.CON, specData.CON) * 2;
            }
        }
        return JRMCoreH.stat(data.player, 2, data.Powertype, 2, con, data.Race, data.Class, 0);
    }

    public int getMaxFusionStamina() {
        int con = data.CON;
        if(isFused()){
            EntityPlayer spectator = getSpectatorEntity();
            if(spectator != null){
                DBCData specData = DBCData.get(spectator);
                con = Math.min(data.CON, specData.CON) * 2;
            }
        }
        return JRMCoreH.stat(data.player, 2, data.Powertype, 3, con, data.Race, data.Class, 0);
    }

    public int getMaxFusionKi() {
        int spi = data.SPI;
        if(isFused()){
            EntityPlayer spectator = getSpectatorEntity();
            if(spectator != null){
                DBCData specData = DBCData.get(spectator);
                spi = Math.min(data.SPI, specData.SPI) * 2;
            }
        }
        return JRMCoreH.stat(data.player, 5, data.Powertype, 5, spi, data.Race, data.Class, JRMCoreH.SklLvl_KiBs(data.player, 1));
    }

    public float getCurrentBodyPercentage() {
        if(isFused())
            return (data.Body * 100) / (float) getMaxFusionBody();

        return (data.Body * 100) / (float) getMaxBody();
    }

    // Negative Values will Drain instead
    public void restoreKiPercent(float percToRestore) {
        int maxKi = isFused() ? getMaxFusionKi() : getMaxKi();
        int toAdd = (int) (maxKi * (percToRestore / 100));

        data.Ki = ValueUtil.clamp(data.Ki + toAdd, 0, maxKi);
        data.getRawCompound().setInteger("jrmcEnrgy", data.Ki);
    }

    public void restoreKiFlat(int amountToRestore) {
        int maxKi = isFused() ? getMaxFusionKi() : getMaxKi();
        data.Ki = ValueUtil.clamp(data.Ki+amountToRestore, 0, maxKi);
        data.getRawCompound().setInteger("jrmcEnrgy", data.Ki);
    }

    public void restoreHealthPercent(float percToRestore) {
        int maxBody = isFused() ? getMaxFusionBody() : getMaxBody();
        int toAdd = (int) (maxBody * (percToRestore / 100));

        data.Body = ValueUtil.clamp(data.Body + toAdd, 0, maxBody);
        data.getRawCompound().setInteger("jrmcBdy", data.Body);
    }

    public void restoreStaminaPercent(float percToRestore) {
        int maxSta = isFused() ? getMaxFusionStamina() : getMaxStamina();
        int toAdd = (int) (maxSta * (percToRestore / 100));


        data.Stamina = ValueUtil.clamp(data.Stamina + toAdd, 0, maxSta);
        data.getRawCompound().setInteger("jrmcStamina", data.Stamina);
    }

    //Negative value will add instead
    public void restoreUIHeat(float percToRestore) {
        if (!data.isForm(DBCForm.UltraInstinct))
            return;

        int maxHeat = JGConfigUltraInstinct.CONFIG_UI_HEAT_DURATION[data.State2];
        int toAdd = (int) (maxHeat * (percToRestore / 100));

        data.Heat = ValueUtil.clamp(data.Heat - toAdd, 0, maxHeat);
        data.getRawCompound().setInteger("jrmcEf8slc", data.Heat);
    }

    public void setArcReserve(int reserve) {
        if (data.Race != 4)
            return;
        nbt(data.player).setInteger("jrmcArcRsrv", reserve);
    }

    public void restoreArcPP(int percToRestoreFromMax) {
        if (data.Race != 4)
            return;

        int maxReserve = JRMCoreConfig.ArcosianPPMax[getMaxSkillX()];
        int toAdd = maxReserve * (percToRestoreFromMax / 100);

        // Arc Reserve can be less than 0, so Add from 0.
        int reserve = Math.max(nbt(data.player).getInteger("jrmcArcRsrv"), 0);
        reserve = ValueUtil.clamp(reserve + toAdd, 0, maxReserve);
        setArcReserve(reserve);
    }

    public void setAbsorption(int amount) {
        if (data.Race != DBCRace.MAJIN)
            return;

        nbt(data.player).setString("jrmcMajinAbsorptionData", amount + ",0,0+0");
    }

    public void restoreAbsorption(int percToRestoreFromMax) {

        if (data.Race != DBCRace.MAJIN)
            return;
        int maxAbsorption = JGConfigRaces.CONFIG_MAJIN_ABSORPTON_MAX_LEVEL;
        int toAdd = (int) (maxAbsorption * (percToRestoreFromMax / 100f));

        int currentAbsorption = getMajinAbsorptionValueS(nbt(data.player).getString("jrmcMajinAbsorptionData"));
        setAbsorption(ValueUtil.clamp(toAdd + currentAbsorption, 0, maxAbsorption));
    }

    public int getMaxSkillX() {
        String racial = nbt(data.player).getString("jrmcSSltX");
        if (racial == null || racial.isEmpty() || racial.contains("pty"))
            return 0;
        return Integer.parseInt(racial.substring(2));
    }

    public int getJRMCPlayerID() {
        for (int pl = 0; pl < JRMCoreH.plyrs.length; pl++)
            if (JRMCoreH.plyrs[pl].equals(data.player.getCommandSenderName()))
                return pl;
        return 0;
    }


    public String getJRMCData(int id) {
        for (int pl = 0; pl < JRMCoreH.plyrs.length; pl++) {
            if (JRMCoreH.plyrs[pl].equals(data.player.getCommandSenderName())) {
                return JRMCoreH.data(id)[pl];
            }
        }
        return "";

    }

    public boolean isFused() {
        if (data.Fusion.contains(",")) {
            String[] fusionMembers = data.Fusion.split(",");
            return fusionMembers.length == 3;
        }
        return false;
    }

    public boolean isFusionSpectator() {
        if (data.Fusion.contains(",")) {
            String[] fusionMembers = data.Fusion.split(",");
            if (fusionMembers.length == 3)
                return fusionMembers[1].equalsIgnoreCase(data.player.getCommandSenderName());

        }
        return false;
    }

    public String getSpectatorName() {
        if (data.Fusion.contains(",")) {
            String[] fusionMembers = data.Fusion.split(",");
            if (fusionMembers.length == 3)
                return fusionMembers[1];

        }
        return "";
    }

    public EntityPlayer getSpectatorEntity(){
        if(isFused() && !isFusionSpectator()){
            String spectator = getSpectatorName();
            if(!spectator.isEmpty()){
                EntityPlayer specEntity = null;
                if(data.player.worldObj.isRemote){
                    specEntity = data.player.worldObj.getPlayerEntityByName(spectator);
                } else {
                    specEntity = NoppesUtilServer.getPlayerByName(spectator);
                }
                return specEntity;
            }
        }
        return null;
    }

    public boolean isChargingKiAttack() {
        ExtendedPlayer jrmcExtendedPlayer = ExtendedPlayer.get(data.player);

        //Abusing JRMCore's animation system to see if a player is charging a ki attack.
        boolean kiAnimationTypeSelected = jrmcExtendedPlayer.getAnimKiShoot() != 0;
        boolean shouldAttemptAnimation = jrmcExtendedPlayer.getAnimKiShootOn() != 0;

        return kiAnimationTypeSelected && shouldAttemptAnimation;
    }

    public void applyNamekianRegen() {
        if (data.player == null)
            return;

        if (getCurrentBodyPercentage() < ConfigDBCGameplay.NamekianRegenMin) {
            if (!StatusEffectController.getInstance().hasEffect(data.player, Effects.NAMEK_REGEN)) {
                StatusEffectController.getInstance().applyEffect(data.player, Effects.NAMEK_REGEN, -100);
            }
        }
    }

    public byte getPotentialUnlockLevel() {
        return (byte) JRMCoreH.SklLvl(5, data.Skills.split(","));
    }

}
