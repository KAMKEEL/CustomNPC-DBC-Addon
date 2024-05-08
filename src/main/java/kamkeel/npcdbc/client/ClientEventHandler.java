package kamkeel.npcdbc.client;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.constants.enums.EnumPlayerAuraTypes;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.mixin.IEntityAura;
import kamkeel.npcdbc.mixin.INPCDisplay;
import kamkeel.npcdbc.network.PacketHandler;
import kamkeel.npcdbc.network.packets.PlaySound;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.living.LivingEvent;
import noppes.npcs.entity.EntityCustomNpc;

import static noppes.npcs.NoppesStringUtils.translate;


public class ClientEventHandler {

    @SubscribeEvent
    public void onSkill(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.SERVER || event.player == null)
            return;

        if (event.phase == TickEvent.Phase.START) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.currentScreen == null && KeyHandler.AscendKey.getIsKeyPressed()) {
                performAscend();
            } else {
                TransformController.decrementRage();
            }
        }
    }

    private void performAscend() {
        PlayerDBCInfo formData = PlayerDataUtil.getClientDBCInfo();
        if (formData != null && formData.hasSelectedForm()) {
            Form form = formData.getSelectedForm();
            DBCData dbcData = DBCData.getClient();
            if (dbcData.stats.isFusionSpectator())
                return;
            float healthReq = (form.mastery.healthRequirement >= 100f || form.mastery.healthRequirement <= 0f) ?
                    150 : form.mastery.healthRequirement * form.mastery.calculateMulti("healthRequirement", formData.getFormLevel(form.id));
            if (dbcData.stats.getCurrentBodyPercentage() > healthReq)
                return;
            if (form.mastery.hasHeat() && dbcData.Pain > 0)
                return;

            if (formData.isInCustomForm()) {
                form = formData.getCurrentForm();
                if (form.hasChild() && formData.hasFormUnlocked(form.getChildID())) {
                    IForm child = form.getChild();
                    if (verifyFormTransform((Form) child))
                        TransformController.Ascend((Form) child);
                }
            } else if (verifyFormTransform(formData.getSelectedForm()))
                TransformController.Ascend(formData.getSelectedForm());
        }
    }

    private boolean verifyFormTransform(Form form) {
        if (form == null)
            return false;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null)
            return false;

        PlayerDBCInfo formData = PlayerDataUtil.getClientDBCInfo();
        if (formData == null)
            return false;

        boolean allowBypass = form.mastery.canInstantTransform(formData.getFormLevel(form.id)) && ClientCache.allowTransformBypass;
        if (allowBypass)
            return true;

        DBCData dbcData = DBCData.getClient();
        if (form.requiredForm.containsKey((int) dbcData.Race)) {
            return form.requiredForm.get((int) dbcData.Race) == dbcData.State;
        } else {
            if (form.parentID != -1 && form.isFromParentOnly()) {
                return form.parentID == formData.currentForm;
            }
        }
        return true;
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == null) {
            PlayerDBCInfo formData = PlayerDataUtil.getClientDBCInfo();
            if (formData != null) {
                if (KeyHandler.AscendKey.isPressed()) {
                    if (formData.selectedForm == -1)
                        Utility.sendMessage(mc.thePlayer, translate("§c", "npcdbc.noFormSelected"));
                    else if (formData.isInCustomForm()) {
                        if (TransformController.rage > 0 && TransformController.transformed) {
                            Utility.sendMessage(mc.thePlayer, translate("§c", "npcdbc.cooldown"));
                            return;
                        }
                        Form form = formData.getCurrentForm();
                        if (form.hasChild() && !formData.hasFormUnlocked(form.getChildID()))
                            Utility.sendMessage(mc.thePlayer, translate("§c", "npcdbc.nextUnlocked"));
                    } else {
                        Form form = formData.getSelectedForm();
                        if (form != null) {
                            DBCData dbcData = DBCData.getClient();
                            if (dbcData.stats.isFusionSpectator()) {
                                Utility.sendMessage(mc.thePlayer, translate("§c", "npcdbc.spectator"));
                                return;
                            }
                            float healthReq = (form.mastery.healthRequirement >= 100f || form.mastery.healthRequirement <= 0f) ?
                                    150 : form.mastery.healthRequirement * form.mastery.calculateMulti("healthRequirement", formData.getFormLevel(form.id));

                            if (dbcData.stats.getCurrentBodyPercentage() > healthReq) {
                                Utility.sendMessage(mc.thePlayer, "§c" + StatCollector.translateToLocalFormatted("npcdbc.healthRequirement", healthReq));
                                return;
                            }

                            if (form.mastery.hasHeat() && DBCData.getClient().Pain > 0) {
                                Utility.sendMessage(mc.thePlayer, translate("§c", "npcdbc.pain"));
                                return;
                            }

                            boolean allowBypass = form.mastery.canInstantTransform(formData.getFormLevel(form.id)) && ClientCache.allowTransformBypass;
                            if (allowBypass)
                                return;

                            if (form.requiredForm.containsKey((int) dbcData.Race)) {
                                if (form.requiredForm.get((int) dbcData.Race) != dbcData.State) {
                                    Utility.sendMessage(mc.thePlayer, translate("§c", "npcdbc.wrongDBC"));
                                    return;
                                }
                            } else {
                                // Must be in Parent Form to Transform
                                if (form.parentID != -1 && form.isFromParentOnly()) {
                                    if (form.parentID != formData.currentForm) {
                                        Utility.sendMessage(mc.thePlayer, translate("§c", "npcdbc.transformFromParent"));
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    @SubscribeEvent
    public void logoutEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        ClientCache.clientDataCache.clear();
    }

    @SubscribeEvent
    public void entityAura(LivingEvent.LivingUpdateEvent event) {
        if (event.entity instanceof EntityCustomNpc && !Utility.isServer(event.entity)) {
            EntityCustomNpc npc = (EntityCustomNpc) event.entity;
            DBCDisplay display = ((INPCDisplay) npc.display).getDBCDisplay();
            if (!display.enabled)
                return;
            Aura aura = (Aura) AuraController.getInstance().get(display.auraID);
            if (aura == null)
                return;
            if (npc.ticksExisted % 5 == 0 && display.auraOn) {
                EntityAura2 aur = new EntityAura2(npc.worldObj, npc.getEntityId() + "", aura.display.color1, 0, 0, 100, false);
                boolean kk = aura.display.kaiokenOn;
                if (aura.display.hasColor("color1"))
                    aur.setCol(aura.display.color1);
                if (aura.display.hasColor("color2"))
                    aur.setColL2(aura.display.color2);
                if (aura.display.hasColor("color3"))
                    aur.setColL3(aura.display.color3);

                if (aura.display.hasSpeed())
                    aur.setSpd((int) aura.display.speed);
                else
                    aur.setSpd(40);

                if (aura.display.hasAlpha("aura"))
                    aur.setAlp((float) aura.display.alpha / 255);
                else
                    aur.setAlp(0.4f);

                if (aura.display.hasSize())
                    ((IEntityAura) aur).setSize(aura.display.size);

                ((IEntityAura) aur).setHasLightning(aura.display.hasLightning);
                ((IEntityAura) aur).setLightningColor(aura.display.lightningColor);

                if (aura.display.hasAlpha("lightning"))
                    ((IEntityAura) aur).setLightningAlpha(aura.display.lightningAlpha);
                else
                    ((IEntityAura) aur).setLightningAlpha(255);

                String sound = "jinryuudragonbc:DBC.aura";

                int mimicColor = EnumPlayerAuraTypes.getManualAuraColor(aura.display.type);
                if (mimicColor != -1)
                    aur.setCol(mimicColor);

                if (aura.display.type == EnumPlayerAuraTypes.SaiyanGod) {
                    aur.setAlp(0.2F);
                    aur.setTex("aurai");
                    aur.setTexL2("aurai2");
                    aur.setColL2(16747301);
                    sound = "jinryuudragonbc:1610.aurag";
                } else if (aura.display.type == EnumPlayerAuraTypes.SaiyanBlue) {
                    aur.setSpd(40);
                    aur.setAlp(0.5F);
                    aur.setTex("aurag");
                    aur.setColL3(15727354);
                    aur.setTexL3("auragb");

                } else if (aura.display.type == EnumPlayerAuraTypes.SaiyanBlueEvo) {
                    aur.setSpd(40);
                    aur.setAlp(0.5F);
                    aur.setTex("aurag");
                    aur.setColL3(12310271);
                    aur.setTexL3("auragb");
                } else if (aura.display.type == EnumPlayerAuraTypes.SaiyanRose) {
                    aur.setSpd(30);
                    aur.setAlp(0.2F);
                    aur.setTex("aurai");
                    aur.setTexL2("aurai2");
                    aur.setColL2(7872713);
                } else if (aura.display.type == EnumPlayerAuraTypes.SaiyanRoseEvo) {
                    aur.setSpd(30);
                    aur.setAlp(0.2F);
                    aur.setTex("aurai");
                    aur.setTexL2("aurai2");
                    aur.setColL2(8592109);
                } else if (aura.display.type == EnumPlayerAuraTypes.UI) {
                    aur.setSpd(100);
                    aur.setAlp(0.15F);
                    aur.setTex("auras");
                    aur.setCol(15790320);
                    aur.setColL3(4746495);
                    aur.setTexL3("auragb");
                    sound = "jinryuudragonbc:DBC5.aura_ui";
                } else if (aura.display.type == EnumPlayerAuraTypes.GoD) {
                    aur.setSpd(30);
                    aur.setAlp(0.2F);
                    aur.setTex("aurag");
                    aur.setTexL3("auragb");
                    aur.setColL2(12464847);
                    sound = "jinryuudragonbc:DBC5.aura_destroyer";
                } else if (aura.display.type == EnumPlayerAuraTypes.UltimateArco) {
                    aur.setAlp(0.5F);
                    aur.setTex("aurau");
                    aur.setTexL2("aurau2");
                    aur.setColL2(16776724);
                }

                if (EnumPlayerAuraTypes.isBlue(aura.display.type)) {
                    if (kk)
                        sound = "jinryuudragonbc:1610.aurabk";
                    else
                        sound = "jinryuudragonbc:1610.aurab";
                }
                if (aura.display.hasSound()) {
                    sound = aura.display.auraSound;
                    int time = (int) (aura.display.soundTime * 20);
                    if (npc.ticksExisted % time == 0)
                        PacketHandler.Instance.sendToServer(new PlaySound(sound, 15).generatePacket());

                } else {
                    float time = 20 * 2.5f;
                    if (EnumPlayerAuraTypes.isBlue(aura.display.type) && kk)
                        time = 20 * 3;

                    if (npc.ticksExisted % time == 0)
                        PacketHandler.Instance.sendToServer(new PlaySound(sound, 15).generatePacket());
                }
                aur.worldObj.spawnEntityInWorld(aur);

            }

        }
    }

}
