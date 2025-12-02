package kamkeel.npcdbc.controllers;

import JinRyuu.JRMCore.JRMCoreH;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.CustomNpcPlusDBC;
import kamkeel.npcdbc.config.ConfigDBCGameplay;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.enums.EnumNBTType;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.SoundSource;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.form.FormCustomStackable;
import kamkeel.npcdbc.data.form.FormStack;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.network.packets.player.DBCSetValPacket;
import kamkeel.npcdbc.network.packets.player.PlaySound;
import kamkeel.npcdbc.network.packets.player.TransformPacket;
import kamkeel.npcdbc.scripted.DBCEventHooks;
import kamkeel.npcdbc.scripted.DBCPlayerEvent;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import kamkeel.npcs.controllers.AttributeController;
import kamkeel.npcs.controllers.data.attribute.tracker.PlayerAttributeTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.LogWriter;
import noppes.npcs.config.ConfigMain;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

import static JinRyuu.JRMCore.JRMCoreH.isInBaseForm;
import static JinRyuu.JRMCore.JRMCoreH.rc_arc;

public class TransformController {

    public static int time, releaseTime;
    public static boolean ascending, cantTransform, transformed;
    public static float rage, rageValue;
    public static DBCData dbcData;
    public static Form transformedInto;

    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    // Client  side handling

    @SideOnly(Side.CLIENT)
    public static void Ascend(Form form) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        Form currentForm = DBCData.getForm(player);
        if (cantTransform || (rage > 0 && transformed) || currentForm != null && currentForm.getID() == form.id)
            return;
        dbcData = DBCData.get(Minecraft.getMinecraft().thePlayer);
        if (dbcData == null || JRMCoreH.curRelease <= 0 || JRMCoreH.curEnergy <= 0)
            return;

        float formLevel = PlayerDataUtil.getClientDBCInfo().getFormLevel(form.id);
        time++;
        releaseTime++;
        TransformController.setAscending(true);
        rageValue = getRageMeterIncrementation(form, formLevel);
        rage += rageValue;
        JRMCoreH.TransSaiCurRg = (byte) rage;
        DBCPacketHandler.Instance.sendToServer(new DBCSetValPacket(CustomNpcPlusDBC.proxy.getClientPlayer(), EnumNBTType.INT, "jrmcSaiRg", (int) rage));

        if (time >= 6) { //increments rage meter and drain ki cost every 6 ticks
            time = 0;
            int cost = JRMCoreH.maxEnergy / 50;
            if (JRMCoreH.curEnergy < cost)
                return;
            JRMCoreH.Cost(cost);
        }
        //        if (player.ticksExisted % 5 == 0) { //increments rage meter and drain ki cost every 6 ticks
        //            float toDrain = form.mastery.kiDrain * form.mastery.calculateMulti("kiDrain", formLevel);
        //
        //            dbcData.stats.restoreKiPercent(-toDrain / form.mastery.kiDrainTimer * 10);
        //            PacketHandler.Instance.sendToServer(new DBCSetValPacket(CustomNpcPlusDBC.proxy.getClientPlayer(), EnumNBTType.INT, "jrmcEnrgy", (int) dbcData.Ki));
        //
        //
        //        }
        if (JRMCoreH.curRelease < 50 && releaseTime >= 10) { //if release is less than 50%, increment it until it is so
            float en = 100.0F / JRMCoreH.maxEnergy * JRMCoreH.curEnergy;
            float re = JRMCoreH.curRelease;
            en = en > 75.0F ? 75.0F : en;
            if (re >= 50.0F ? (re - 50.0F < (en - 50.0F) * 2.0F) : (re < ((en <= 10.0F) ? (en * 5.0F) : 50.0F)))
                JRMCoreH.Rls((byte) 1);
            releaseTime = 0;
        }
        if (rage >= 100) { //transform when rage meter reaches 100 (max)
            int selectedId = PlayerDataUtil.getClientDBCInfo() != null ?
                PlayerDataUtil.getClientDBCInfo().selectedForm :
                -1;

            int definitiveId;
            int stackedFrom;

            if (form.customStackable.customStackable && currentForm != null && FormController.Instance.has(selectedId)) {
                Form stackForm = getStackForm(currentForm, (Form) FormController.Instance.get(selectedId));

                if (stackForm != null) {
                    definitiveId = stackForm.getID();
                    stackedFrom = currentForm.getID();
                } else {
                    definitiveId = form.getID();
                    stackedFrom = -1;
                }
            } else {
                definitiveId = form.getID();
                stackedFrom = -1;
            }

            DBCPacketHandler.Instance.sendToServer(new TransformPacket(definitiveId, true, stackedFrom));
            resetTimers();
            cantTransform = true;
            transformed = true;
            transformedInto = form;
        }
    }

    @SideOnly(Side.CLIENT)
    public static void decrementRage() {
        dbcData = DBCData.get(Minecraft.getMinecraft().thePlayer);
        if (dbcData == null || rage == 0)
            return;

        if (rage > 0) {
            if (rage > 100)
                rage = 100;
            if (rage - (rageValue) > 0)
                rage -= (rageValue);
            else {
                rage = 0;
                transformedInto = null;
            }
            if (rage <= 50 && JRMCoreH.StusEfctsMe(1))
                JRMCoreH.Skll((byte) 5, (byte) 1, (byte) 1);
        }
        if (rage == 0 && (JRMCoreH.StusEfctsMe(1) || cantTransform || transformed || ascending))
            setAscending(false);

        JRMCoreH.TransSaiCurRg = (byte) rage;
        DBCPacketHandler.Instance.sendToServer(new DBCSetValPacket(CustomNpcPlusDBC.proxy.getClientPlayer(), EnumNBTType.INT, "jrmcSaiRg", (int) rage));
    }

    @SideOnly(Side.CLIENT)
    public static void setAscending(boolean bo) {
        ascending = bo;
        if (time == 1 || !bo)
            JRMCoreH.Skll((byte) 5, bo ? (byte) 0 : 1, (byte) 1);
        if (!bo) {
            cantTransform = false;
            transformed = false;
            transformedInto = null;
        }
    }

    @SideOnly(Side.CLIENT)
    public static void resetTimers() {
        time = 0;
        releaseTime = 0;
    }

    public static float getRageMeterIncrementation(Form form, float formLevel) {
        float maxLevel = form.getMastery().getMaxLevel();
        float ratio = formLevel / maxLevel;

        if (form.getMastery().hasInstantTransformationUnlockLevel())
            if (form.mastery.canInstantTransform(formLevel))
                return 15;

        if (Utility.percentBetween(formLevel, maxLevel, 0, 5))
            return 1;
        else if (Utility.percentBetween(formLevel, maxLevel, 5, 101))
            return 15 * ratio;
        return 0;
    }

    private static Form getStackForm(Form current, Form selected) {
        FormCustomStackable stackable = current.customStackable;
        int selectedId = selected.id;

        for (FormStack stack : stackable.formStacks.values()) {
            if (stack.fromForm.id == selectedId) {
                return stack.toForm;
            }
        }

        return null;
    }

    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    // NPC transformation handling

    public static void npcAscend(EntityNPCInterface npc, Form form) {
        DBCDisplay display = ((INPCDisplay) npc.display).getDBCDisplay();

        if (form.id == display.formID || !display.isTransforming)
            return;

        if (display.rageValue <= 0)
            display.rageValue = getRageMeterIncrementation(form, display.getFormLevel(form.id));

        display.rage += display.rageValue;

        if (display.rage >= 100) {
            display.formID = form.id;
            PlaySound.play(npc, form.getAscendSound());
        }
        npc.updateClient();
    }

    public static void npcDecrementRage(EntityNPCInterface npc, DBCDisplay display) {
        if (display.rage <= 0)
            return;


        display.rage = (int) ValueUtil.clamp(display.rage - display.rageValue, 0, 100);

        if (display.rage == 0) {
            display.rageValue = 0;
            display.selectedForm = -1;
        } else if (display.rage <= 50 && display.isTransforming) {
            display.isTransforming = false;
        }
        npc.updateClient();
    }

    public static void npcDescend(EntityNPCInterface npc, int id) {
        DBCDisplay display = ((INPCDisplay) npc.display).getDBCDisplay();
        Form current = display.getForm();
        if (current == null)
            return;

        if (FormController.Instance.has(id))
            display.formID = id;
        else
            display.formID = -1;

        display.selectedForm = -1;
        display.isTransforming = false;
        PlaySound.play(npc, current.getDescendSound());
        npc.updateClient();
    }


    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    // Server side handling

    public static void handleFormAscend(EntityPlayer player, int formID, int stackedFrom) {
        Form form = (Form) FormController.getInstance().get(formID);
        if (form == null)
            return;

        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(player);

        if (!formData.hasForm(form))
            return;

        if (formData.currentForm != formID) {
            DBCData dbcData = DBCData.get(player);


            boolean allowBypass = form.mastery.canInstantTransform(formData.getFormLevel(form.id)) && ConfigDBCGameplay.InstantTransform;
            if (!allowBypass) {
                // Check for in Required DBC Form before Transforming
                if (form.requiredForm.containsKey((int) dbcData.Race)) {
                    if (form.requiredForm.get((int) dbcData.Race) != dbcData.State)
                        return;
                } else {
                    // Must be in Parent Form to Transform
                    if (form.isFromParentOnly() && form.parentID != -1 && form.parentID != formData.currentForm)
                        return;
                }
            }

            int prevID = formData.currentForm != 1 ? formData.currentForm : dbcData.State;
            if (DBCEventHooks.onFormChangeEvent(new DBCPlayerEvent.FormChangeEvent(PlayerDataUtil.getIPlayer(player), formData.currentForm != 1, prevID, true, formID)))
                return;

            PlaySound.play(new SoundSource(form.getAscendSound(), player));
            if (!isInBaseForm(dbcData.Race, dbcData.State)) {
                if (!form.stackable.vanillaStackable) {
                    if (rc_arc(dbcData.Race) && dbcData.State >= 4)
                        dbcData.State = 4;
                    else
                        dbcData.State = 0;
                }
            }
            if (!form.stackable.godStackable && dbcData.isForm(DBCForm.GodOfDestruction))
                dbcData.setForm(DBCForm.GodOfDestruction, false);

            if (!form.stackable.mysticStackable && dbcData.isForm(DBCForm.Mystic))
                dbcData.setForm(DBCForm.Mystic, false);

            if (!form.stackable.uiStackable && dbcData.isForm(DBCForm.UltraInstinct))
                dbcData.setForm(DBCForm.UltraInstinct, false);

            if (!form.stackable.kaiokenStackable && dbcData.isForm(DBCForm.Kaioken))
                dbcData.setForm(DBCForm.Kaioken, false);

            if (stackedFrom == -1) {
                formData.lastFormBeforeStack = -1;
            }

            if (stackedFrom != -1) {
                formData.lastFormBeforeStack = stackedFrom;
            }

            formData.currentForm = formID;

            if (formData.getForm(formID) != null && formData.getForm(formID).hasTimer())
                formData.addTimer(formID, formData.getForm(formID).getTimer());

            formData.updateClient();
            LogWriter.info(form.getMenuName());
            NetworkUtility.sendInfoMessage(player, "§a", "npcdbc.transform", "§r ", form.getMenuName());
            dbcData.saveNBTData(true);

            if(ConfigMain.AttributesEnabled){
                PlayerAttributeTracker tracker = AttributeController.getTracker(player);
                tracker.recalcAttributes(player);
            }
        }
    }

    public static void handleFormDescend(EntityPlayer player, int formID, int stackedFrom) {
        PlayerDBCInfo formData = PlayerDataUtil.getDBCInfo(player);
        if (formData.isInCustomForm()) {
            Form form = formData.getCurrentForm();
            DBCData dbcData = DBCData.get(player);

            Form parent = (Form) form.getParent();
            boolean intoParent = parent != null && formData.hasFormUnlocked(form.getParentID());

            if (!intoParent && !form.stackable.vanillaStackable) {
                if (JRMCoreH.rSai(dbcData.Race) && (dbcData.State == 7 || dbcData.State == 8)) {
                    dbcData.State = 0;
                }
            }

            int prevID = formData.currentForm != 1 ? formData.currentForm : dbcData.State;
            if (DBCEventHooks.onFormChangeEvent(new DBCPlayerEvent.FormChangeEvent(PlayerDataUtil.getIPlayer(player), formData.currentForm != 1, prevID, true, intoParent ? form.getParentID() : -1)))
                return;

            PlaySound.play(new SoundSource(form.getDescendSound(), player));
            if (form.mastery.hasHeat() && dbcData.addonCurrentHeat > 0) {
                float heatRatio = dbcData.addonCurrentHeat / form.mastery.maxHeat;
                dbcData.Pain = (int) (form.mastery.painTime * 60 / 5 * form.mastery.calculateMulti("pain", formData.getCurrentLevel()) * heatRatio);
                dbcData.addonCurrentHeat = 0;
            }
            if (formID == -10) {
                formData.currentForm = -1;
            } else if (form.requiredForm.containsKey((int) dbcData.Race)) {
                formData.currentForm = -1;
                NetworkUtility.sendInfoMessage(player, "§c", "npcdbc.descend", "§r ", form.getMenuName());
                dbcData.State = form.requiredForm.get((int) dbcData.Race);
            } else {
                int realStackedFrom = stackedFrom != -1 ? stackedFrom : formData.lastFormBeforeStack;

                if (realStackedFrom != -1) {
                    Form previousForm = (Form) FormController.Instance.get(realStackedFrom);
                    NetworkUtility.sendInfoMessage(player, "§c", "npcdbc.descend", "§r ", previousForm.getMenuName());
                    formData.currentForm = realStackedFrom;

                    formData.lastFormBeforeStack = -1;
                } else if (intoParent) {
                    NetworkUtility.sendInfoMessage(player, "§c", "npcdbc.descend", "§r ", form.getParent().getMenuName());
                    formData.currentForm = form.getParentID();
                } else if (formData.getTimer(form.id) == 0) {
                    NetworkUtility.sendInfoMessage(player, "§c", "npcdbc.timeExpired");
                } else {
                    NetworkUtility.sendInfoMessage(player, "§c", "npcdbc.descendFrom", "§r ", form.getMenuName());
                    formData.currentForm = -1;
                }
            }

            formData.updateClient();
            JRMCoreH.setByte(0, player, "jrmcSaiRg");
            dbcData.saveNBTData(true);

            if(ConfigMain.AttributesEnabled){
                PlayerAttributeTracker tracker = AttributeController.getTracker(player);
                tracker.recalcAttributes(player);
            }
        }
    }
}
