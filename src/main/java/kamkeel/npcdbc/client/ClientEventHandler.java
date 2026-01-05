package kamkeel.npcdbc.client;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import JinRyuu.DragonBC.common.Npcs.EntityAuraRing;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreKeyHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.client.gui.global.auras.SubGuiAuraDisplay;
import kamkeel.npcdbc.client.gui.hud.abilityWheel.HUDAbilityWheel;
import kamkeel.npcdbc.client.gui.hud.formWheel.HUDFormWheel;
import kamkeel.npcdbc.client.sound.AuraSound;
import kamkeel.npcdbc.client.sound.SoundHandler;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.DBCRace;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes2D;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes3D;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.IAuraData;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.SoundSource;
import kamkeel.npcdbc.data.ability.AbilityData;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.mixins.late.IEntityAura;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.AbilityUsePacket;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.living.LivingEvent;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

import static noppes.npcs.NoppesStringUtils.translate;

public class ClientEventHandler {

    public static int ticks;

    @SubscribeEvent
    public void onSkill(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.SERVER || event.player == null)
            return;
        if (event.phase == TickEvent.Phase.START) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.currentScreen == null) {
                if (JRMCoreKeyHandler.KiAscend.getIsKeyPressed()) {
                    performAscend();
                } else {
                    TransformController.decrementRage();
                }
            }
        }
    }

    private void performAscend() {
        PlayerDBCInfo formData = PlayerDataUtil.getClientDBCInfo();
        Form selectedForm = formData.getSelectedForm();
        Form currentForm = formData.getCurrentForm();


        if (formData != null && selectedForm != null) {
            DBCData dbcData = DBCData.getClient();
            if (dbcData.stats.isFusionSpectator())
                return;
            float healthReq = (selectedForm.mastery.healthRequirement >= 100f || selectedForm.mastery.healthRequirement <= 0f) ? 150 : selectedForm.mastery.healthRequirement * selectedForm.mastery.calculateMulti("healthRequirement", formData.getFormLevel(selectedForm.id));
            if (dbcData.stats.getCurrentBodyPercentage() > healthReq)
                return;
            if (selectedForm.mastery.hasHeat() && dbcData.Pain > 0)
                return;


            if (currentForm != null && currentForm.isChildOf(selectedForm)) {
                Form child = (Form) currentForm.getChild();
                if (child != null && formData.hasFormUnlocked(child.id) && verifyFormTransform(child))
                    TransformController.Ascend(child);
            } else if (verifyFormTransform(selectedForm))
                TransformController.Ascend(selectedForm);
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

        DBCData dbcData = DBCData.getClient();

        if (!form.raceEligible(dbcData.Race))
            return false;
        if ((form.display.hairType.equals("ssj4") || form.display.hairType.equals("oozaru")) && DBCRace.isSaiyan(dbcData.Race) && !dbcData.hasTail())
            return false;

        if (!form.stackable.kaiokenStackable && dbcData.isForm(DBCForm.Kaioken))
            return false;
        if (!form.stackable.uiStackable && dbcData.isForm(DBCForm.UltraInstinct))
            return false;
        if (!form.stackable.mysticStackable && dbcData.isForm(DBCForm.Mystic))
            return false;
        if (!form.stackable.godStackable && dbcData.isForm(DBCForm.GodOfDestruction))
            return false;


        boolean allowBypass = form.mastery.canInstantTransform(formData.getFormLevel(form.id)) && ClientCache.allowTransformBypass;

        if (form.requiredForm.containsKey((int) dbcData.Race)) {
            return allowBypass || form.requiredForm.get((int) dbcData.Race) == dbcData.State;
        } else {
            if (form.hasParent() && form.isFromParentOnly()) {
                return allowBypass || form.parentID == formData.currentForm;
            }
        }
        return true;
    }

    @SubscribeEvent
    public void onMousePress(InputEvent.MouseInputEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != null)
            return;

        if (KeyHandler.FormWheelKey.isPressed()) {
            if (PlayerDataUtil.getClientDBCInfo() != null)
                mc.displayGuiScreen(new HUDFormWheel());
        }

        if (KeyHandler.AbilityWheelKey.isPressed() && !JRMCoreKeyHandler.Fn.isPressed()) {
            if (PlayerDataUtil.getClientDBCInfo() != null)
                mc.displayGuiScreen(new HUDAbilityWheel());
        }
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent e) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == null) {
            PlayerDBCInfo formData = PlayerDataUtil.getClientDBCInfo();
            if (formData != null) {
                if (KeyHandler.FormWheelKey.isPressed()) {
                    mc.displayGuiScreen(new HUDFormWheel());
                    return;
                }

                if (KeyHandler.AbilityWheelKey.isPressed()) {
                    mc.displayGuiScreen(new HUDAbilityWheel());
                    return;
                }

                if (KeyHandler.AbilityCastKey.isPressed()) {
                    PlayerDBCInfo info = PlayerDataUtil.getClientDBCInfo();
                    AbilityData dbc = info.dbcAbilityData;
                    AbilityData custom = info.customAbilityData;

                    if (custom.hasSelectedAbility() && custom.hasAbilityUnlocked(custom.selectedAbility)) {
                        DBCPacketHandler.Instance.sendToServer(new AbilityUsePacket(Minecraft.getMinecraft().thePlayer, custom.selectedAbility, false));
                        return;
                    }

                    if (dbc.hasSelectedAbility() && dbc.hasAbilityUnlocked(dbc.selectedAbility)){
                        DBCPacketHandler.Instance.sendToServer(new AbilityUsePacket(Minecraft.getMinecraft().thePlayer, dbc.selectedAbility, true));
                        return;
                    }

                    return;
                }

                if (JRMCoreKeyHandler.KiAscend.isPressed()) {
                    DBCData dbcData = DBCData.getClient();
                    Form form = formData.getCurrentForm();

                    //   if (formData.selectedForm == -1)
                    //       Utility.sendMessage(mc.thePlayer, translate("§c", "npcdbc.noFormSelected"));
                    if (formData.isInCustomForm()) {
                        if (TransformController.rage > 0 && TransformController.transformed) {
                            Utility.sendMessage(mc.thePlayer, translate("§c", "npcdbc.cooldown"));
                            return;
                        }

                        if (form != null && form.hasChild()) {
                            form = (Form) form.getChild();
                            if (!formData.hasFormUnlocked(form.getID())) {
                                Utility.sendMessage(mc.thePlayer, translate("§c", "npcdbc.nextUnlocked"));
                                return;
                            }

                            if ((form.display.hairType.equals("ssj4") || form.display.hairType.equals("oozaru")) && DBCRace.isSaiyan(dbcData.Race) && !dbcData.hasTail()) {
                                Utility.sendMessage(mc.thePlayer, translate("§c", "npcdbc.noTail"));
                                return;
                            }
                        }
                    } else {
                        form = formData.getSelectedForm();
                        if (form != null) {

                            if (!form.raceEligible(dbcData.Race)) {
                                Utility.sendMessage(mc.thePlayer, translate("§c", "npcdbc.raceIneligible"));
                                return;
                            }

                            boolean allowBypass = form.mastery.canInstantTransform(formData.getFormLevel(form.id)) && ClientCache.allowTransformBypass;
                            if (form.requiredForm.containsKey((int) dbcData.Race)) {
                                if (!allowBypass && form.requiredForm.get((int) dbcData.Race) != dbcData.State) {
                                    Utility.sendMessage(mc.thePlayer, translate("§c", "npcdbc.wrongDBC"));
                                    return;
                                }
                            } else {
                                // Must be in Parent Form to Transform
                                if (form.parentID != -1 && form.isFromParentOnly() && !allowBypass) {
                                    if (form.parentID != formData.currentForm) {
                                        Utility.sendMessage(mc.thePlayer, translate("§c", "npcdbc.transformFromParent"));
                                        return;
                                    }
                                }
                            }
                            if ((form.display.hairType.equals("ssj4") || form.display.hairType.equals("oozaru")) && DBCRace.isSaiyan(dbcData.Race) && !dbcData.hasTail()) {
                                Utility.sendMessage(mc.thePlayer, translate("§c", "npcdbc.noTail"));
                                return;
                            }

                            if (dbcData.stats.isFusionSpectator()) {
                                Utility.sendMessage(mc.thePlayer, translate("§c", "npcdbc.spectator"));
                                return;
                            }
                            float healthReq = (form.mastery.healthRequirement >= 100f || form.mastery.healthRequirement <= 0f) ? 150 : form.mastery.healthRequirement * form.mastery.calculateMulti("healthRequirement", formData.getFormLevel(form.id));

                            if (dbcData.stats.getCurrentBodyPercentage() > healthReq) {
                                Utility.sendMessage(mc.thePlayer, "§c" + StatCollector.translateToLocalFormatted("npcdbc.healthRequirement", healthReq, "§c"));
                                return;
                            }

                            if (form.mastery.hasHeat() && DBCData.getClient().Pain > 0) {
                                Utility.sendMessage(mc.thePlayer, translate("§c", "npcdbc.pain"));
                                return;
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
    public void handleSounds(TickEvent.ClientTickEvent event) {
        if (event.side == Side.CLIENT && event.phase == TickEvent.Phase.END) {
            if (ticks % 5 == 0) {
                SoundHandler.verifySounds();
            }

            ticks++;
        }
    }

    // TODO figure out why this shit is NOT WORKING
    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entity instanceof EntityPlayer && !Utility.isServer(event.entity)) {
            EntityPlayer player = (EntityPlayer) event.entity;

            PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(player);

            if (info != null && info.dbcAbilityData.canAnimateAbility()) {
                player.motionX = player.motionY = player.motionZ = 0;
            }
        }
    }

    @SubscribeEvent
    public void entityAura(LivingEvent.LivingUpdateEvent event) {
        if ((event.entity instanceof EntityCustomNpc || event.entity instanceof EntityPlayer) && !Utility.isServer(event.entity)) {
            if (event.entity.ticksExisted % 5 == 0) {
                Aura aura = null;
                boolean isPlayer = event.entity instanceof EntityPlayer;
                boolean isNPC = event.entity instanceof EntityNPCInterface;
                DBCData dbcData = null;
                DBCDisplay display = null;
                boolean isInKaioken = false, isSpectator = false;
                IAuraData data = null;

                boolean vanillaAura = false;
                if (isNPC) {
                    EntityCustomNpc npc = (EntityCustomNpc) event.entity;
                    display = ((INPCDisplay) npc.display).getDBCDisplay();
                    data = display;
                    data.setActiveAuraColor(-1);
                    if (!display.enabled)
                        return;
                    aura = display.getToggledAura();

                    if (aura == null && display.isAuraOn()) {
                        aura = new Aura().display.setOverrideDBCAura(true);
                        vanillaAura = true;
                    } else if (aura == null)
                        return;
                    isInKaioken = display.isKaioken;
                } else if (isPlayer) {
                    dbcData = DBCData.get((EntityPlayer) event.entity);
                    data = dbcData;
                    aura = dbcData.getToggledAura();
                    isSpectator = dbcData.stats.isFusionSpectator();
                    data.setActiveAuraColor(-1);

                    if (aura == null || isSpectator)
                        return;

                    isInKaioken = dbcData.isForm(DBCForm.Kaioken) && aura.display.hasKaiokenAura;
                }
                EntityAura enhancedAura = isPlayer ? dbcData.auraEntity : display.auraEntity;
                if (ConfigDBCClient.RevampAura) {
                    if (enhancedAura == null && !isSpectator)
                        if (vanillaAura)
                            new EntityAura(event.entity, aura).setIsVanilla(true).load(true).spawn();
                        else
                            new EntityAura(event.entity, aura).load(true).spawn();
                } else {
                    if (enhancedAura != null)
                        enhancedAura.despawn();

                    if (isInKaioken && aura.display.kaiokenOverrides) {
                        spawnKaiokenAura(aura, dbcData);
                    } else {
                        EntityAura2 aur = spawnAura(event.entity, aura);
                        data.setActiveAuraColor(aur.getCol());

                        if (aura.hasSecondaryAura())
                            spawnAura(event.entity, aura.getSecondaryAur());
                        if (isInKaioken)
                            spawnKaiokenAura(aura, dbcData);
                    }
                }
            }
        }
    }

    public static EntityAura2 spawnAura(Entity entity, Aura aura) {

        boolean isPlayer = entity instanceof EntityPlayer;
        boolean isNPC = entity instanceof EntityNPCInterface;
        DBCData dbcData = null;
        DBCDisplay display = null;

        String auraOwner = isPlayer ? entity.getCommandSenderName() : Utility.getEntityID(entity);

        if (isPlayer)
            dbcData = DBCData.get((EntityPlayer) entity);
        else
            display = ((INPCDisplay) ((EntityCustomNpc) entity).display).getDBCDisplay();

        boolean rotate90 = isPlayer && (dbcData.containsSE(7));
        IAuraData data = isPlayer ? dbcData : display;

        int release = SubGuiAuraDisplay.useGUIAura ? 5 : data.getRelease();
        EntityAura2 aur = new EntityAura2(entity.worldObj, auraOwner, 0, data.getState(), data.getState2(), release, rotate90);
        ((IEntityAura) aur).setAuraData(data);
        aur.setAlp(0.2F);


        if (SubGuiAuraDisplay.useGUIAura) {
            ((IEntityAura) aur).setGUIAura(true);
        }

        if (isNPC)
            ((IEntityAura) aur).setSize((float) ((EntityNPCInterface) entity).display.modelSize / 5);

        if (aura.display.hasSize())
            ((IEntityAura) aur).setSize(((IEntityAura) aur).getSize() * aura.display.size);

        ((IEntityAura) aur).setHasLightning(aura.display.hasLightning);
        ((IEntityAura) aur).setLightningColor(aura.display.lightningColor);
        ((IEntityAura) aur).setLightningAlpha(aura.display.lightningAlpha);
        ((IEntityAura) aur).setLightningSpeed(aura.display.lightningSpeed);
        ((IEntityAura) aur).setLightningIntensity(aura.display.lightningIntensity);


        int formColor = isPlayer ? dbcData.AuraColor > 0 ? dbcData.AuraColor : JRMCoreH.Algnmnt_rc(dbcData.Alignment) : 11075583; //alignment color
        int mimicColor = EnumAuraTypes3D.getManualAuraColor(aura.display.type, false);
        if (mimicColor != -1)
            formColor = mimicColor;
        boolean has2D = aura.display.type2D != EnumAuraTypes2D.None;

        if (aura.display.type == EnumAuraTypes3D.SaiyanGod) {
            aur.setAlp(0.2F);
            aur.setTex("aurai");
            aur.setTexL2("aurai2");
            aur.setColL2(16747301);
            if (has2D)
                aur.setBol6(0);
        } else if (aura.display.type == EnumAuraTypes3D.SaiyanBlue) {
            aur.setSpd(40);
            aur.setAlp(0.5F);
            aur.setTex("aurag");
            aur.setColL3(15727354);
            aur.setTexL3("auragb");
            if (has2D)
                aur.setBol6(1);
        } else if (aura.display.type == EnumAuraTypes3D.SaiyanBlueEvo) {
            aur.setSpd(40);
            aur.setAlp(0.5F);
            aur.setTex("aurag");
            aur.setColL3(12310271);
            aur.setTexL3("auragb");
            if (has2D)
                aur.setBol6(2);
        } else if (aura.display.type == EnumAuraTypes3D.SaiyanRose) {
            aur.setSpd(30);
            aur.setAlp(0.2F);
            aur.setTex("aurai");
            aur.setTexL2("aurai2");
            aur.setColL2(7872713);
            if (has2D)
                aur.setBol6(3);
        } else if (aura.display.type == EnumAuraTypes3D.SaiyanRoseEvo) {
            aur.setSpd(30);
            aur.setAlp(0.2F);
            aur.setTex("aurai");
            aur.setTexL2("aurai2");
            aur.setColL2(8592109);
            if (has2D)
                aur.setBol6(5);
        } else if (aura.display.type == EnumAuraTypes3D.UI) {
            aur.setSpd(100);
            aur.setAlp(0.15F);
            aur.setTex("auras");
            aur.setCol(15790320);
            aur.setColL3(4746495);
            aur.setTexL3("auragb");
            if (has2D) {
                aur.setBol4(true);
                aur.setBol4a(true);
            }
        } else if (aura.display.type == EnumAuraTypes3D.GoD) {
            aur.setSpd(100);
            aur.setAlp(0.2F);
            aur.setTex("aurag");
            aur.setTexL3("auragb");
            aur.setColL3(12464847);
            if (has2D)
                aur.setBol6(6);
        } else if (aura.display.type == EnumAuraTypes3D.UltimateArco) {
            aur.setAlp(0.5F);
            aur.setTex("aurau");
            aur.setTexL2("aurau2");
            aur.setColL2(16776724);
            if (has2D)
                aur.setBol6(4);
        }

        if (aura.display.type2D == EnumAuraTypes2D.None) {
            aur.setBol6(-10);
        } else if (aura.display.type2D == EnumAuraTypes2D.SaiyanGod) {
            aur.setBol6(0);
        } else if (aura.display.type2D == EnumAuraTypes2D.SaiyanBlue) {
            aur.setBol6(1);
        } else if (aura.display.type2D == EnumAuraTypes2D.SaiyanBlueEvo) {
            aur.setBol6(2);
        } else if (aura.display.type2D == EnumAuraTypes2D.SaiyanRose) {
            aur.setBol6(3);
        } else if (aura.display.type2D == EnumAuraTypes2D.SaiyanRoseEvo) {
            aur.setBol6(5);
        } else if (aura.display.type2D == EnumAuraTypes2D.UI) {
            aur.setBol4(true);
        } else if (aura.display.type2D == EnumAuraTypes2D.MasteredUI) {
            aur.setBol4(true);
            aur.setBol4a(true);
        } else if (aura.display.type2D == EnumAuraTypes2D.GoD) {
            aur.setBol6(6);
        } else if (aura.display.type2D == EnumAuraTypes2D.UltimateArco) {
            aur.setBol6(4);
        } else if (aura.display.type2D != EnumAuraTypes2D.Default && aura.display.type2D != EnumAuraTypes2D.Base) {
            aur.setBol6(-2);
        }

        ((IEntityAura) aur).setType2D(aura.display.type2D);

        if (aura.display.type == EnumAuraTypes3D.None)
            aur.setRendPass(-1);
        //////////////////////////////////////////////////////
        //////////////////////////////////////////////////////
        //Forms & Aura Ring
        Form form = null;
        boolean isTransforming = false;
        boolean isCharging = false;

        if (isNPC) {
            display = ((INPCDisplay) ((EntityNPCInterface) entity).display).getDBCDisplay();
            form = display.getForm();
            isTransforming = display.isTransforming;
            isCharging = isTransforming;
        } else if (isPlayer) {
            form = dbcData.getForm();
            isTransforming = dbcData.isTransforming();
            isCharging = dbcData.containsSE(4) || isTransforming;
        }

        //////////////////////////////////////////////////////
        //////////////////////////////////////////////////////


        if (isPlayer && aura.display.copyDBCSuperformColors && dbcData.State > 0)//vanilla DBC form colors
            formColor = dbcData.getDBCColor();

        if (aura.display.hasColor("color1")) //IAura color
            formColor = aura.display.color1;

        if (form != null && form.display.hasColor("aura")) //IForm color
            formColor = form.display.auraColor;

        aur.setCol(formColor);

        if (aura.display.hasColor("color2"))
            aur.setColL2(aura.display.color2);
        if (aura.display.hasColor("color3"))
            aur.setColL3(aura.display.color3);


        if (aura.display.hasAlpha("aura")) {
            aur.setAlp((float) aura.display.alpha / (255));
        }
        //        if (SubGuiAuraDisplay.useGUIAura)
        //            aur.setAlp(aur.getAlp() / 10);


        if (aura.display.hasSpeed())
            aur.setSpd(aura.display.speed);

        //Kettle Mode stuff
        if (aura.display.kettleModeEnabled) {
            if (aura.display.kettleModeCharging && !SubGuiAuraDisplay.useGUIAura)
                aur.kettleMode = (byte) (isCharging ? 2 : 0);
            else
                aur.kettleMode = 2;
        }

        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        // This block indefinitely loops through aura sound as long as aura is enabled
        // regardless of the sound.ogg duration. The second the sound ends, it insta-replays

        AuraSound.play(entity, aura, data);

        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////

        if (isTransforming)
            spawnAuraRing(entity, aur.getCol());

        ((IEntityAura) aur).setEntity(entity);
        aur.worldObj.spawnEntityInWorld(aur);

        return aur;
    }

    public static EntityAura2 spawnKaiokenAura(Aura aura, IAuraData data) {
        if (data == null)
            return null;

        boolean override = aura.display.kaiokenOverrides;
        //  boolean rotate90 = data.containsSE(7) ? true : false;

        Entity entity = data.getEntity();
        boolean isPlayer = entity instanceof EntityPlayer;
        String auraOwner = isPlayer ? entity.getCommandSenderName() : Utility.getEntityID(entity);

        int release = SubGuiAuraDisplay.useGUIAura ? 5 : data.getRelease();
        EntityAura2 kaiokenAura = new EntityAura2(entity.worldObj, entity.getCommandSenderName(), 16646144, 2.0F + data.getState(), data.getState2() * 1.5f, release, false);
        ((IEntityAura) kaiokenAura).setAuraData(data);
        ((IEntityAura) kaiokenAura).setIsKaioken(true);

        if (SubGuiAuraDisplay.useGUIAura) {
            ((IEntityAura) kaiokenAura).setGUIAura(true);
        }

        if (override) {
            kaiokenAura.setAlp(0.2F);
            kaiokenAura.setSpd(20);
        } else {
            kaiokenAura.setAlp(0.4F);
            kaiokenAura.setSpd(40);
            kaiokenAura.setTex("aurak");
            kaiokenAura.setInner(false);
            kaiokenAura.setRendPass(0);
        }

        if (aura.display.hasAlpha("kaioken"))
            kaiokenAura.setAlp((float) aura.display.kaiokenAlpha / 255);

        if (aura.display.hasColor("kaioken"))
            kaiokenAura.setCol(aura.display.kaiokenColor);

        if (aura.display.hasSpeed())
            kaiokenAura.setSpd(aura.display.speed);

        ((IEntityAura) kaiokenAura).setSize(aura.display.size * aura.display.kaiokenSize);
        ((IEntityAura) kaiokenAura).setHasLightning(aura.display.hasLightning);
        ((IEntityAura) kaiokenAura).setLightningColor(aura.display.lightningColor);
        ((IEntityAura) kaiokenAura).setLightningAlpha(aura.display.lightningAlpha);
        ((IEntityAura) kaiokenAura).setLightningSpeed(aura.display.lightningSpeed);
        ((IEntityAura) kaiokenAura).setLightningIntensity(aura.display.lightningIntensity);

        String kkSound = aura.display.getFinalKKSound();
        if (kkSound != null && !SoundHandler.isPlayingSound(entity, kkSound)) {
            AuraSound kaiokenSound = new AuraSound(aura, new SoundSource(kkSound, entity));

            kaiokenSound.isKaiokenSound = true;
            kaiokenSound.setRepeat(true).play(false);
        }

        if (data.isTransforming() && override)
            spawnAuraRing(entity, kaiokenAura.getCol());

        ((IEntityAura) kaiokenAura).setEntity(entity);
        entity.worldObj.spawnEntityInWorld(kaiokenAura);
        return kaiokenAura;
    }

    public static EntityAuraRing spawnAuraRing(Entity entity, int color) {
        if (entity.ticksExisted % 20 != 0)
            return null;

        boolean isPlayer = entity instanceof EntityPlayer;
        EntityAuraRing ring = new EntityAuraRing(entity.worldObj, isPlayer ? entity.getCommandSenderName() : Utility.getEntityID(entity), color, 0, 0, 0);

        entity.worldObj.spawnEntityInWorld(ring);
        return ring;
    }
}
