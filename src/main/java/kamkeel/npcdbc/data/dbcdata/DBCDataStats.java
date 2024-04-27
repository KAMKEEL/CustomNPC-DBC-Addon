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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import noppes.npcs.util.ValueUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static JinRyuu.JRMCore.JRMCoreH.getMajinAbsorptionValueS;
import static JinRyuu.JRMCore.JRMCoreH.nbt;

public class DBCDataStats {
    DBCData d;

    public DBCDataStats(DBCData dbcData) {
        this.d = dbcData;
    }

    public HashMap<Integer, PlayerEffect> getPlayerEffects() {
        return d.currentEffects;
    }

    public void setCurrentEffects(HashMap<Integer, PlayerEffect> setVals) {
        NBTTagCompound raw = d.getRawCompound();
        d.currentEffects = updateEffects(setVals);
        saveEffectsNBT(raw);
    }

    public HashMap<Integer, PlayerEffect> updateEffects(HashMap<Integer, PlayerEffect> setVals) {
        HashMap<Integer, PlayerEffect> createdMap = new HashMap<>();
        for (PlayerEffect playerEffect : setVals.values()) {
            PlayerEffect newEffect;
            if (d.currentEffects.containsKey(playerEffect.id)) {
                newEffect = d.currentEffects.get(playerEffect.id);
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
        Iterator iterator = d.currentEffects.values().iterator();
        while (iterator.hasNext()) {
            PlayerEffect playerEffect = (PlayerEffect) iterator.next();
            nbttaglist.appendTag(playerEffect.writeEffectData(new NBTTagCompound()));
        }
        nbt.setTag("addonActiveEffects", nbttaglist);
    }

    public void decrementActiveEffects() {
        HashMap<Integer, PlayerEffect> currentEffects = StatusEffectController.Instance.playerEffects.get(Utility.getUUID(d.player));
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
                if (parent != null)
                    parent.runout(d.player, currentEffect);
                iterator.remove();
            } else
                currentEffect.duration--;
        }
        setCurrentEffects(currentEffects);
    }

    public int[] getAllAttributes() {
        return new int[]{d.STR, d.DEX, d.CON, d.WIL, d.MND, d.SPI};
    }

    public int getFullAttribute(int attri) {
        boolean majin = JRMCoreH.StusEfcts(12, d.StatusEffects);
        boolean fusion = (JRMCoreH.StusEfcts(10, d.StatusEffects) || JRMCoreH.StusEfcts(11, d.StatusEffects));
        boolean legendary = JRMCoreH.StusEfcts(14, d.StatusEffects);
        boolean kaioken = JRMCoreH.StusEfcts(5, d.StatusEffects);
        boolean mystic = JRMCoreH.StusEfcts(13, d.StatusEffects);
        boolean ui = JRMCoreH.StusEfcts(19, d.StatusEffects);
        boolean GoD = JRMCoreH.StusEfcts(20, d.StatusEffects);

        return JRMCoreH.getPlayerAttribute(d.player, getAllAttributes(), attri, d.State, d.State2, d.Race, d.RacialSkills, (int) d.Release, d.ArcReserve, legendary, majin, kaioken, mystic, ui, GoD, d.Powertype, d.Skills.split(","), fusion, d.MajinAbsorptionData);

    }

    public int[] getAllFullAttributes() {
        boolean majin = JRMCoreH.StusEfcts(12, d.StatusEffects);
        boolean fusion = (JRMCoreH.StusEfcts(10, d.StatusEffects) || JRMCoreH.StusEfcts(11, d.StatusEffects));
        boolean legendary = JRMCoreH.StusEfcts(14, d.StatusEffects);
        boolean kaioken = JRMCoreH.StusEfcts(5, d.StatusEffects);
        boolean mystic = JRMCoreH.StusEfcts(13, d.StatusEffects);
        boolean ui = JRMCoreH.StusEfcts(19, d.StatusEffects);
        boolean GoD = JRMCoreH.StusEfcts(20, d.StatusEffects);
        int[] a = new int[6];
        for (int i = 0; i <= 5; i++)
            a[i] = JRMCoreH.getPlayerAttribute(d.player, getAllAttributes(), i, d.State, d.State2, d.Race, d.RacialSkills, d.Release, d.ArcReserve, legendary, majin, kaioken, mystic, ui, GoD, d.Powertype, d.Skills.split(","), fusion, d.MajinAbsorptionData);

        return a;
    }

    public int getExtraOutput(int att, int release) {
        int extraoutput = 0;

        if (att == 0) {
            int maxki = getMaxStat(5);
            extraoutput = (int) (JRMCoreH.SklLvl(12, d.Skills.split(",")) * 0.0025 * maxki * release * 0.01);
        } else if (att == 1) {
            int maxki = getMaxStat(5);
            extraoutput = (int) (JRMCoreH.SklLvl(11, d.Skills.split(",")) * 0.005 * maxki * release * 0.01);
        } else if (att == 5)
            extraoutput = getMaxStat(5) - JRMCoreH.stat(d.player, att, d.Powertype, att, d.SPI, d.Race, d.Class, JRMCoreH.SklLvl_KiBs(d.Skills.split(","), 1));

        return extraoutput;
    }

    public int getMaxStat(int attributeID) { // gets max player stat, 0 dmg 1 def only, rest are
        int attribute = 0;

        if (attributeID == 0 || attributeID == 1 || attributeID == 4)
            attribute = getFullAttribute(attributeID);
        else
            attribute = getAllAttributes()[attributeID];

        float f = attributeID == 5 ? JRMCoreH.SklLvl_KiBs(d.Skills.split(","), 1) : 0f;
        int stat = JRMCoreH.stat(d.player, attributeID, d.Powertype, attributeID, attribute, d.Race, d.Class, f);

        if (attributeID == 0)
            stat += getExtraOutput(attributeID, 100);
        else if (attributeID == 1)
            stat += getExtraOutput(attributeID, 100);

        return stat;
    }

    public int getCurrentStat(int attribute) { // gets stat at current release
        return (int) (getMaxStat(attribute) * d.Release * 0.01D * JRMCoreH.weightPerc(0, d.player));
    }

    public double getCurrentMulti() {
        return getFullAttribute(0) / d.STR;

    }

    public int getMaxBody() {
        return JRMCoreH.stat(d.player, 2, d.Powertype, 2, d.CON, d.Race, d.Class, 0);
    }

    public int getMaxStamina() {
        return JRMCoreH.stat(d.player, 2, d.Powertype, 3, d.CON, d.Race, d.Class, 0);
    }

    public int getMaxKi() {
        return JRMCoreH.stat(d.player, 5, d.Powertype, 5, d.CON, d.Race, d.Class, 0);
    }

    public float getCurrentBodyPercentage() {
        return (d.Body * 100) / (float) getMaxBody();

    }

    // Negative Values will Drain instead
    public void restoreKiPercent(float percToRestore) {
        int maxKi = getMaxKi();
        int toAdd = (int) (maxKi * (percToRestore / 100));

        d.Ki = ValueUtil.clamp(d.Ki + toAdd, 0, maxKi);
        d.getRawCompound().setInteger("jrmcEnrgy", d.Ki);
    }

    public void restoreHealthPercent(float percToRestore) {
        int maxBody = getMaxBody();
        int toAdd = (int) (maxBody * (percToRestore / 100));

        d.Body = ValueUtil.clamp(d.Body + toAdd, 0, maxBody);
        d.getRawCompound().setInteger("jrmcBdy", d.Body);
    }

    public void restoreStaminaPercent(float percToRestore) {
        int maxSta = getMaxStamina();
        int toAdd = (int) (maxSta * (percToRestore / 100));


        d.Stamina = ValueUtil.clamp(d.Stamina + toAdd, 0, maxSta);
        d.getRawCompound().setInteger("jrmcStamina", d.Stamina);
    }

    //Negative value will add instead
    public void restoreUIHeat(float percToRestore) {
        if (!d.isForm(DBCForm.UltraInstinct))
            return;

        int maxHeat = JGConfigUltraInstinct.CONFIG_UI_HEAT_DURATION[d.State2];
        int toAdd = (int) (maxHeat * (percToRestore / 100));

        d.Heat = ValueUtil.clamp(d.Heat - toAdd, 0, maxHeat);
        d.getRawCompound().setInteger("jrmcEf8slc", d.Heat);
    }

    public void setArcReserve(int reserve) {
        if (d.Race != 4)
            return;
        nbt(d.player).setInteger("jrmcArcRsrv", reserve);
    }

    public void restoreArcPP(int percToRestoreFromMax) {
        if (d.Race != 4)
            return;

        int maxReserve = JRMCoreConfig.ArcosianPPMax[getMaxSkillX()];
        int toAdd = maxReserve * (percToRestoreFromMax / 100);

        // Arc Reserve can be less than 0, so Add from 0.
        int reserve = Math.max(nbt(d.player).getInteger("jrmcArcRsrv"), 0);
        reserve = ValueUtil.clamp(reserve + toAdd, 0, maxReserve);
        setArcReserve(reserve);
    }

    public void setAbsorption(int amount) {
        if (d.Race != DBCRace.MAJIN)
            return;

        nbt(d.player).setString("jrmcMajinAbsorptionData", amount + ",0,0+0");
    }

    public void restoreAbsorption(int percToRestoreFromMax) {

        if (d.Race != DBCRace.MAJIN)
            return;
        int maxAbsorption = JGConfigRaces.CONFIG_MAJIN_ABSORPTON_MAX_LEVEL;
        int toAdd = (int) (maxAbsorption * (percToRestoreFromMax / 100f));

        int currentAbsorption = getMajinAbsorptionValueS(nbt(d.player).getString("jrmcMajinAbsorptionData"));
        setAbsorption(ValueUtil.clamp(toAdd + currentAbsorption, 0, maxAbsorption));
    }

    public int getMaxSkillX() {
        String racial = nbt(d.player).getString("jrmcSSltX");
        if (racial == null || racial.isEmpty() || racial.contains("pty"))
            return 0;
        return Integer.parseInt(racial.substring(2));
    }

    public int getJRMCPlayerID() {
        for (int pl = 0; pl < JRMCoreH.plyrs.length; pl++)
            if (JRMCoreH.plyrs[pl].equals(d.player.getCommandSenderName()))
                return pl;
        return 0;
    }


    public String getJRMCData(int id) {
        for (int pl = 0; pl < JRMCoreH.plyrs.length; pl++) {
            if (JRMCoreH.plyrs[pl].equals(d.player.getCommandSenderName())) {
                return JRMCoreH.data(id)[pl];
            }
        }
        return "";

    }

    public boolean isChargingKiAttack() {
        ExtendedPlayer jrmcExtendedPlayer = ExtendedPlayer.get(d.player);

        //Abusing JRMCore's animation system to see if a player is charging a ki attack.
        boolean kiAnimationTypeSelected = jrmcExtendedPlayer.getAnimKiShoot() != 0;
        boolean shouldAttemptAnimation = jrmcExtendedPlayer.getAnimKiShootOn() != 0;

        return kiAnimationTypeSelected && shouldAttemptAnimation;
    }

    public void applyNamekianRegen() {
        if (d.player == null)
            return;

        if (getCurrentBodyPercentage() < ConfigDBCGameplay.NamekianRegenMin) {
            if (!StatusEffectController.getInstance().hasEffect(d.player, Effects.NAMEK_REGEN)) {
                StatusEffectController.getInstance().applyEffect(d.player, new PlayerEffect(Effects.NAMEK_REGEN, -100, (byte) 1));
            }
        }
    }
}
