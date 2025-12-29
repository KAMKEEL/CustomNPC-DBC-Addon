package kamkeel.npcdbc.client.utils;

import JinRyuu.JRMCore.JRMCoreH;
import cpw.mods.fml.common.FMLCommonHandler;
import kamkeel.npcdbc.api.ISimpleDBCData;
import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.api.outline.IOutline;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.scripted.DBCAPI;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.block.Block;

public class SimplifiedDBCData implements ISimpleDBCData {
    public final DBCData dbcData;
    public SimplifiedDBCData(DBCData dbcData) {
        this.dbcData = dbcData;
    }

    @Override
    public int getRace() {
        return dbcData.getRace();
    }

    @Override
    public byte getRelease() {
        return dbcData.Release;
    }

    @Override
    public int getBody() {
        return dbcData.Body;
    }

    @Override
    public int getHP() {
        return getBody();
    }

    @Override
    public int getStamina() {
        return dbcData.Stamina;
    }

    @Override
    public int getKi() {
        return dbcData.Ki;
    }

    @Override
    public byte getForm() {
        return dbcData.State;
    }

    @Override
    public byte getForm2() {
        return dbcData.State2;
    }

    @Override
    public int getPowerPoints() {
        return dbcData.ArcReserve;
    }

    @Override
    public String getJRMCSE() {
        return dbcData.StatusEffects;
    }

    @Override
    public byte getDBCClass() {
        return dbcData.Class;
    }

    @Override
    public int getPowerType() {
        return dbcData.Powertype;
    }

    @Override
    public String getFusionString() {
        return dbcData.Fusion;
    }

    @Override
    public boolean isTurboOn() {
        return dbcData.containsSE(3);
    }

    @Override
    public int getMaxHP() {
        return getMaxBody();
    }

    @Override
    public int getMaxBody() {
        return dbcData.stats.getMaxBody();
    }

    @Override
    public float getBodyPercentage() {
        return dbcData.stats.getCurrentBodyPercentage();
    }

    @Override
    public int getMaxKi() {
        return dbcData.stats.getMaxKi();
    }

    @Override
    public int getMaxStamina() {
        return dbcData.stats.getMaxStamina();
    }

    @Override
    public int[] getAllAttributes() {
        return dbcData.stats.getAllAttributes();
    }

    @Override
    public int getFullAttribute(int id) {
        return dbcData.stats.getFullAttribute(id);
    }

    @Override
    public int[] getAllFullAttributes() {
        return dbcData.stats.getAllFullAttributes();
    }

    @Override
    public String getRaceName() {
        if (this.getRace() >= 0 && this.getRace() <= 5) {
            return JRMCoreH.Races[this.getRace()];
        }
        return null;
    }

    @Override
    public String getCurrentDBCFormName() {
        int race = this.getRace();
        int form = this.getForm();
        return DBCAPI.Instance().getFormName(race, form);
    }

    @Override
    public boolean isChargingKiAttack() {
        return dbcData.stats.isChargingKiAttack();
    }

    @Override
    public int getMaxStat(int id) {
        return dbcData.stats.getMaxStat(id);
    }

    @Override
    public int getCurrentStat(int id) {
        return dbcData.stats.getCurrentStat(id);
    }

    @Override
    public int getMajinAbsorptionRace() {
        if (getRace() != 5)
            return 0;
        String s = dbcData.MajinAbsorptionData;
        String[] data = s.split(",");
        String value = data.length >= 3 ? data[1] : "0";
        return Integer.parseInt(value);
    }

    @Override
    public int getMajinAbsorptionPower() {
        if (getRace() != 5)
            return 0;
        String s = dbcData.MajinAbsorptionData;
        return JRMCoreH.getMajinAbsorptionValueS(s);
    }

    @Override
    public boolean isKO() {
        return dbcData.isKO;
    }

    @Override
    public boolean isUI() {
        return dbcData.isForm(DBCForm.UltraInstinct);
    }

    @Override
    public boolean isMUI() {
        return dbcData.isForm(DBCForm.MasteredUltraInstinct);
    }

    @Override
    public boolean isMystic() {
        return dbcData.isForm(DBCForm.Mystic);
    }

    @Override
    public boolean isGOD() {
        return dbcData.isForm(DBCForm.GodOfDestruction);
    }

    @Override
    public boolean isLegendary() {
        return dbcData.isForm(DBCForm.Legendary);
    }

    @Override
    public boolean isDivine() {
        return dbcData.isForm(DBCForm.Divine);
    }

    @Override
    public boolean isMajin() {
        return dbcData.isForm(DBCForm.Majin);
    }

    @Override
    public boolean isKaioken() {
        return dbcData.isForm(DBCForm.Kaioken);
    }

    @Override
    public boolean isFlying() {
        return dbcData.isFlying;
    }

    @Override
    public boolean isInCustomForm() {
        return dbcData.addonFormID != -1;
    }

    @Override
    public boolean isInCustomForm(int id) {
        return dbcData.addonFormID == id;
    }

    @Override
    public boolean isInCustomForm(IForm form) {
        return isInCustomForm(form.getID());
    }

    @Override
    public IForm getCurrentForm() {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(dbcData.player);
            if (info != null) {
                Form current = info.getCurrentForm();
                if (current != null) {
                    return current;
                }
            }
        }

        return dbcData.getForm();
    }

    @Override
    public IAura getAura() {
        return dbcData.getAura();
    }

    @Override
    public boolean isInAura() {
        return getAura() != null;
    }

    @Override
    public boolean isInAura(IAura aura) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer())
            return PlayerDataUtil.getDBCInfo(dbcData.player).isInCustomAura();
        return getAura() == aura;
    }

    @Override
    public boolean isInAura(String auraName) {
        return isInAura(AuraController.getInstance().get(auraName));
    }

    @Override
    public boolean isInAura(int auraID) {
        return auraID == dbcData.auraID;
    }

    @Override
    public IOutline getOutline() {
        return dbcData.getOutline();
    }

    @Override
    public boolean isReleasing() {
        return (dbcData.StatusEffects.contains(JRMCoreH.StusEfcts[4]));
    }

    @Override
    public boolean isMeditating() {
        if (dbcData.Skills.contains("MD")) {
            return (dbcData.StatusEffects.contains(JRMCoreH.StusEfcts[4]));
        } else return false;
    }

    @Override
    public boolean isSuperRegen() {
        if (dbcData.stats.getCurrentBodyPercentage() < 100f && dbcData.getRace() == DBCRace.MAJIN && Integer.parseInt(dbcData.RacialSkills.replace("TR", "")) > 0)
            return isReleasing();
        return false;
    }

    @Override
    public boolean isSwooping() {
        return (dbcData.StatusEffects.contains(JRMCoreH.StusEfcts[7]));
    }

    @Override
    public boolean isInMedicalLiquid() {
        Block block = dbcData.player.worldObj.getBlock(
            (int) Math.floor(dbcData.player.posX),
            (int) Math.floor(dbcData.player.posY),
            (int) Math.floor(dbcData.player.posZ)
        );
        return (block == Block.getBlockFromName("jinryuudragonblockc:tile.BlockHealingPods"));
    }
}
