package kamkeel.npcdbc.client;

import JinRyuu.DragonBC.common.Npcs.EntityAura2;
import JinRyuu.DragonBC.common.Npcs.EntityAuraRing;
import JinRyuu.JRMCore.JRMCoreH;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.client.shader.PostProcessing;
import kamkeel.npcdbc.client.sound.AuraSound;
import kamkeel.npcdbc.client.sound.SoundHandler;
import kamkeel.npcdbc.config.ConfigDBCClient;
import kamkeel.npcdbc.constants.DBCForm;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes2D;
import kamkeel.npcdbc.constants.enums.EnumAuraTypes3D;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.SoundSource;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.data.npc.DBCDisplay;
import kamkeel.npcdbc.entity.EntityAura;
import kamkeel.npcdbc.mixins.late.IEntityAura;
import kamkeel.npcdbc.mixins.late.INPCDisplay;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.event.entity.living.LivingEvent;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import org.lwjgl.input.Keyboard;

import static noppes.npcs.NoppesStringUtils.translate;


public class ClientEventHandler {

    private int soundTicker = -1;

    @SubscribeEvent
    public void onSkill(TickEvent.PlayerTickEvent event) {
        if (event.side == Side.SERVER || event.player == null)
            return;
        if (event.phase == TickEvent.Phase.START) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.currentScreen == null) {
                if (KeyHandler.AscendKey.getIsKeyPressed()) {
                    performAscend();
                } else {
                    TransformController.decrementRage();
                }
            }
//            if (Mouse.isButtonDown(3) || Mouse.isButtonDown(2)) {
//                Minecraft.getMinecraft().refreshResources();
//                ShaderHelper.loadShaders(true);
//            }
            if (Keyboard.isKeyDown(Keyboard.KEY_F10)) {
                for (int i : PostProcessing.bloomTextures) {
                    if (i > 0)
                        PostProcessing.saveTextureToPNG(i);
                }
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
            float healthReq = (form.mastery.healthRequirement >= 100f || form.mastery.healthRequirement <= 0f) ? 150 : form.mastery.healthRequirement * form.mastery.calculateMulti("healthRequirement", formData.getFormLevel(form.id));
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
        if (!form.stackable.kaiokenStackable && dbcData.isForm(DBCForm.Kaioken))
            return false;
        if (!form.stackable.uiStackable && dbcData.isForm(DBCForm.UltraInstinct))
            return false;
        if (!form.stackable.mysticStackable && dbcData.isForm(DBCForm.Mystic))
            return false;
        if (!form.stackable.godStackable && dbcData.isForm(DBCForm.GodOfDestruction))
            return false;

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
                            float healthReq = (form.mastery.healthRequirement >= 100f || form.mastery.healthRequirement <= 0f) ? 150 : form.mastery.healthRequirement * form.mastery.calculateMulti("healthRequirement", formData.getFormLevel(form.id));

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
    public void handleSounds(TickEvent.ClientTickEvent event) {
        if (event.side == Side.CLIENT) {
            if (soundTicker % 5 == 0) {
                SoundHandler.verifySounds();
                soundTicker = 0;
            }

            soundTicker++;
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

                boolean vanillaAura = false;
                if (isNPC) {
                    EntityCustomNpc npc = (EntityCustomNpc) event.entity;
                    display = ((INPCDisplay) npc.display).getDBCDisplay();
                    if (!display.enabled)
                        return;
                    aura = display.getToggledAura();

                    if (aura == null && display.isAuraOn()){
                        aura = new Aura().display.setOverrideDBCAura(true);
                        vanillaAura = true;
                    }
                    else if(aura == null)
                        return;
                } else if (isPlayer) {
                    dbcData = DBCData.get((EntityPlayer) event.entity);
                    aura = dbcData.getToggledAura();
                    isSpectator = dbcData.stats.isFusionSpectator();

                    if (aura == null || isSpectator)
                        return;

                    isInKaioken = dbcData.isForm(DBCForm.Kaioken) && aura.display.hasKaiokenAura;
                }
                EntityAura enhancedAura = isPlayer ? dbcData.auraEntity : display.auraEntity;
                if (ConfigDBCClient.RevampAura) {
                    if (enhancedAura == null && !isSpectator)
                        if(vanillaAura)
                            new EntityAura(event.entity, aura).setIsVanilla(true).load(true).spawn();
                        else
                            new EntityAura(event.entity, aura).load(true).spawn();
                } else {
                    if (enhancedAura != null)
                        enhancedAura.despawn();

                    if (isInKaioken && aura.display.kaiokenOverrides) {
                        spawnKaiokenAura(aura, dbcData);
                    } else {
                        spawnAura(event.entity, aura);
                        if (aura.hasSecondaryAura())
                            spawnAura(event.entity, aura.getSecondaryAur());
                        if (isInKaioken)
                            spawnKaiokenAura(aura, dbcData);
                    }
                }


            }
        }
    }

    public static void spawnAura(Entity entity, Aura aura) {
        boolean isPlayer = entity instanceof EntityPlayer;
        boolean isNPC = entity instanceof EntityNPCInterface;
        DBCData dbcData = null;
        DBCDisplay display = null;

        String auraOwner = isPlayer ? entity.getCommandSenderName() : Utility.getEntityID(entity);

        if (isPlayer)
            dbcData = DBCData.get((EntityPlayer) entity);

        boolean rotate90 = isPlayer && (dbcData.containsSE(7));
        EntityAura2 aur = new EntityAura2(entity.worldObj, auraOwner, 0, isPlayer ? dbcData.State : 0, isPlayer ? dbcData.State2 : 0, isPlayer ? dbcData.Release : 100, rotate90);
        aur.setAlp(0.2F);

        if (isNPC)
            ((IEntityAura) aur).setSize((float) ((EntityNPCInterface) entity).display.modelSize / 5);

        if (aura.display.hasSize())
            ((IEntityAura) aur).setSize(aura.display.size);

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
            aur.setColL2(12464847);
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
        }

        ((IEntityAura) aur).setType2D(aura.display.type2D);

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


        if (isPlayer && dbcData.State > 0)//vanilla DBC form colors
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


        if (aura.display.hasAlpha("aura"))
            aur.setAlp((float) aura.display.alpha / 255);

        if (aura.display.hasSpeed())
            aur.setSpd(aura.display.speed);

        //Kettle Mode stuff
        if (aura.display.kettleModeCharging)
            aur.kettleMode = isCharging ? aura.display.kettleModeType : 0;
        if (aura.display.kettleModeAura)
            aur.kettleMode = aura.display.kettleModeType;

        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        // This block indefinitely loops through aura sound as long as aura is enabled
        // regardless of the sound.ogg duration. The second the sound ends, it insta-replays

        AuraSound.play(entity, aura);

        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////

        if (isTransforming)
            spawnAuraRing(entity, aur.getCol());

        aur.worldObj.spawnEntityInWorld(aur);
    }

    public static EntityAura2 spawnKaiokenAura(Aura aura, DBCData dbcData) {
        if (dbcData == null)
            return null;

        boolean override = aura.display.kaiokenOverrides;
        boolean rotate90 = dbcData.containsSE(7) ? true : false;

        EntityAura2 kaiokenAura = new EntityAura2(dbcData.player.worldObj, dbcData.player.getCommandSenderName(), 16646144, 2.0F + dbcData.State, dbcData.State2 * 1.5f, dbcData.Release, rotate90);
        ((IEntityAura) kaiokenAura).setIsKaioken(true);
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


        String kkSound = aura.display.getFinalKKSound();
        if (kkSound != null && !SoundHandler.isPlayingSound(dbcData.player, kkSound)) {
            AuraSound kaiokenSound = new AuraSound(aura, new SoundSource(kkSound, dbcData.player));

            kaiokenSound.isKaiokenSound = true;
            kaiokenSound.setRepeat(true).play(false);
        }

        if (dbcData.isTransforming() && override)
            spawnAuraRing(dbcData.player, kaiokenAura.getCol());

        dbcData.player.worldObj.spawnEntityInWorld(kaiokenAura);
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
