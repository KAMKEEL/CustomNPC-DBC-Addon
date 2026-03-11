package kamkeel.npcdbc.client;

import JinRyuu.DragonBC.common.DBCClient;
import JinRyuu.DragonBC.common.DBCConfig;
import JinRyuu.DragonBC.common.DBCKiTech;
import JinRyuu.JRMCore.JRMCoreCliTicH;
import JinRyuu.JRMCore.JRMCoreClient;
import JinRyuu.JRMCore.JRMCoreConfig;
import JinRyuu.JRMCore.JRMCoreH;
import JinRyuu.JRMCore.JRMCoreKeyHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.constants.enums.EnumNBTType;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.network.DBCPacketHandler;
import kamkeel.npcdbc.network.packets.player.DBCSetValPacket;
import net.minecraft.block.Block;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import noppes.npcs.client.ClientAbilityState;

import java.util.UUID;

/**
 * Enhanced movement system that reimplements DBC's DashKi and FloatKi
 * with additive acceleration instead of velocity assignment.
 * This preserves knockback, explosions, and other external forces
 * during turbo sprint and flight.
 */
@SideOnly(Side.CLIENT)
public class DBCMoveHelper {
    private static final UUID SPRINT_MODIFIER_UUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");

    /**
     * Returns horizontal friction coefficient matching vanilla's moveEntityWithHeading.
     */
    private static float getFriction(EntityPlayer player) {
        if (player.isInWater()) return 0.8F;
        if (player.handleLavaMovement()) return 0.5F;
        if (!player.onGround) return 0.91F;

        int bx = MathHelper.floor_double(player.posX);
        int by = MathHelper.floor_double(player.boundingBox.minY) - 1;
        int bz = MathHelper.floor_double(player.posZ);
        Block block = player.worldObj.getBlock(bx, by, bz);
        return block.slipperiness * 0.91F;
    }

    /**
     * Additive replacement for DBC's mv().
     * Same direction calculation, but uses += instead of =.
     * Steady-state speed matches DBC's original value.
     */
    private static void move(float strafe, float forward, EntityPlayer player, float speed) {
        float f3 = strafe * strafe + forward * forward;
        if (f3 >= 1.0E-4F) {
            f3 = MathHelper.sqrt_float(f3);
            if (f3 < 1.0F) f3 = 1.0F;

            f3 = speed / f3;
            float sinYaw = MathHelper.sin(player.rotationYaw * (float) Math.PI / 180.0F);
            float cosYaw = MathHelper.cos(player.rotationYaw * (float) Math.PI / 180.0F);
            strafe *= f3;
            forward *= f3;

            double targetX = (double) ((strafe * cosYaw - forward * sinYaw) * 0.25F);
            double targetZ = (double) ((forward * cosYaw + strafe * sinYaw) * 0.25F);

            float friction = getFriction(player);
            player.motionX += targetX * (1.0F - friction);
            player.motionZ += targetZ * (1.0F - friction);
        }
    }

    /**
     * Computes speed modifier from the player's movement speed attribute.
     * Excludes sprint modifier. Returns multiplier relative to base speed.
     */
    private static float getSpeedModifier(EntityPlayer player) {
        if (!ClientCache.allowSpeedModifierTurboFlight) return 1.0f;

        IAttributeInstance attr = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        double totalSpeed = attr.getAttributeValue();

        AttributeModifier sprintMod = attr.getModifier(SPRINT_MODIFIER_UUID);
        if (sprintMod != null) {
            totalSpeed /= (1.0 + sprintMod.getAmount());
        }

        double baseSpeed = attr.getBaseValue();
        if (baseSpeed <= 0) return 1.0f;

        float ratio = (float) (totalSpeed / baseSpeed);
        return 1.0f + (ratio - 1.0f) * ClientCache.speedModifierTurboFlight;
    }

    /**
     * Enhanced DashKi — reimplements DBC's DashKi with additive acceleration.
     * Dodges keep hard-assignment (intentional for instant response).
     * Sprint uses additive move() instead of mv().
     */
    public static void DashKi(boolean sprint) {
        EntityPlayer var4 = DBCClient.mc.thePlayer;
        World var3 = DBCClient.mc.theWorld;

        // Speed calculation (identical to DBC)
        int n = JRMCoreH.SklLvl(2, (byte) 1);
        int st = JRMCoreH.StusEfctsMe(13)
            ? (JRMCoreH.rc_sai(JRMCoreH.Race)
                ? JRMCoreH.mstc_sai(JRMCoreH.SklLvlX(1, JRMCoreH.PlyrSkillX) - 1)
                : (JRMCoreH.rc_arc(JRMCoreH.Race) ? JRMCoreH.mstc_arc() : (JRMCoreH.rc_humNam(JRMCoreH.Race) ? JRMCoreH.mstc_humnam() : 1)))
            : JRMCoreH.State;
        float inc = JRMCoreH.statInc(JRMCoreH.Pwrtyp, 7, 100, JRMCoreH.Race, JRMCoreH.Class, 0.0F) * 0.01F;
        float add = JRMCoreH.spdFrm(JRMCoreH.PlyrAttrbts(null)[1], n, (float) JRMCoreH.curRelease, DBCKiTech.turbo, false, st, JRMCoreH.State2, inc);
        int cost = (int) (1.0F + add);

        int[] PlyrAttrbts = JRMCoreH.PlyrAttrbts();
        byte pwr = JRMCoreH.Pwrtyp;
        byte rce = JRMCoreH.Race;
        byte cls = JRMCoreH.Class;
        int maxStam = JRMCoreH.stat(var4, 2, pwr, 3, PlyrAttrbts[2], rce, cls, 0.0F);
        int ce = JRMCoreH.curStamina;
        int cst = (int) ((float) maxStam * 0.2F - (float) n * 0.01F);

        // Dodge section (identical to DBC, hard-assign intentional)
        boolean left = JRMCoreClient.mc.gameSettings.keyBindLeft.isPressed();
        boolean right = JRMCoreClient.mc.gameSettings.keyBindRight.isPressed();
        boolean back = JRMCoreClient.mc.gameSettings.keyBindBack.isPressed();
        boolean dodge = !JRMCoreH.PlyrSettingsB(2);
        long ctm = System.currentTimeMillis() / 1000L;

        if (DBCKiTech.dodge_per == 0
            && DBCKiTech.dodge_recently != ctm
            && DBCKiTech.isPressed(JRMCoreKeyHandler.Fn)
            && !DBCKiTech.isPressed(JRMCoreClient.mc.gameSettings.keyBindUseItem)
            && !DBCKiTech.isPressed(JRMCoreKeyHandler.KiCharge)
            && !DBCKiTech.isPressed(JRMCoreKeyHandler.KiAscend)) {
            DBCKiTech.dodge_per = !left && !right && !back ? 0 : 1;
        } else if (DBCKiTech.dodge_recently == ctm) {
            DBCKiTech.dodge_per = 0;
        }

        if (DBCKiTech.dodge_per > 0 && DBCKiTech.dodge_recently != ctm && ce > cst && n > 0 && dodge && JRMCoreH.curRelease > 0) {
            int y = JRMCoreClient.mc.thePlayer.onGround ? 2 : 1;
            if (left) {
                DBCKiTech.dodge_recently = ctm;
                double e = Math.cos((double) (JRMCoreClient.mc.thePlayer.rotationYaw - 90.0F) * Math.PI / 180.0) * 1.5 * (double) y;
                double r = Math.sin((double) (JRMCoreClient.mc.thePlayer.rotationYaw - 90.0F) * Math.PI / 180.0) * -1.5 * (double) y;
                JRMCoreClient.mc.thePlayer.motionZ = e;
                JRMCoreClient.mc.thePlayer.motionX = r;
                DBCKiTech.triForce(4, 0, 0);
            }
            if (right) {
                DBCKiTech.dodge_recently = ctm;
                double e = Math.cos((double) (JRMCoreClient.mc.thePlayer.rotationYaw + 90.0F) * Math.PI / 180.0) * 1.5 * (double) y;
                double r = Math.sin((double) (JRMCoreClient.mc.thePlayer.rotationYaw + 90.0F) * Math.PI / 180.0) * -1.5 * (double) y;
                JRMCoreClient.mc.thePlayer.motionZ = e;
                JRMCoreClient.mc.thePlayer.motionX = r;
                DBCKiTech.triForce(4, 0, 1);
            }
            if (back) {
                DBCKiTech.dodge_recently = ctm;
                double e = Math.cos((double) JRMCoreClient.mc.thePlayer.rotationYaw * Math.PI / 180.0) * -1.0 * (double) y;
                double r = Math.sin((double) JRMCoreClient.mc.thePlayer.rotationYaw * Math.PI / 180.0) * 1.0 * (double) y;
                JRMCoreClient.mc.thePlayer.motionZ = e;
                JRMCoreClient.mc.thePlayer.motionX = r;
                DBCKiTech.triForce(4, 0, 3);
            }
            DBCKiTech.dodge_per = 0;
            KeyBinding.setKeyBindState(JRMCoreKeyHandler.Fn.getKeyCode(), false);
        }

        // Sprint section (enhanced with additive acceleration)
        boolean able = true;
        if (JRMCoreH.curEnergy < cost || DBCKiTech.dodge_forwHold) {
            able = false;
        }

        if (able && sprint && !DBCKiTech.floating && JRMCoreH.curRelease > 0) {
            DBCKiTech.dash++;
            if (DBCKiTech.dash >= 20) {
                JRMCoreH.Cost(cost);
                DBCKiTech.dash = 0;
            }

            if (var3.getBlock((int) var4.posX, (int) var4.posY - 2, (int) var4.posZ) != Blocks.ice) {
                float par1 = var4.moveStrafing;
                float par2 = var4.moveForward;
                if (DBCKiTech.isPressed(DBCClient.mc.gameSettings.keyBindForward)
                    || DBCKiTech.isPressed(DBCClient.mc.gameSettings.keyBindBack)
                    || DBCKiTech.isPressed(DBCClient.mc.gameSettings.keyBindLeft)
                    || DBCKiTech.isPressed(DBCClient.mc.gameSettings.keyBindRight)) {
                    // ENHANCED: Additive acceleration + addon speed multipliers
                    float moveSpeed = add * DBCData.getClient().getSprintSpeed() * getSpeedModifier(var4);
                    move(par1, par2, var4, moveSpeed);
                    DBCKiTech.floatMultAdded = true;
                } else {
                    // ENHANCED: No keys — let friction decelerate naturally
                    DBCKiTech.floatMultAdded = false;
                }
            }
        }
    }

    /**
     * Enhanced FloatKi — reimplements DBC's FloatKi with additive acceleration.
     * Swoops keep hard-assignment (intentional for instant response).
     * Regular flight uses additive move() instead of mv().
     */
    public static void FloatKi(KeyBinding kiFlight, KeyBinding keyBindJump, KeyBinding keyBindSneak) {
        // ADDON: Flight disable
        if (!DBCData.getClient().flightEnabled)
            return;

        // ADDON: Ability movement suppression
        if (ClientAbilityState.hasAbilityMovement && ClientAbilityState.activePhase)
            return;

        EntityPlayer p = DBCClient.mc.thePlayer;

        // Speed calculation (identical to DBC)
        int n = JRMCoreH.SklLvl(3, (byte) 1);
        int st = JRMCoreH.StusEfctsMe(13)
            ? (JRMCoreH.rc_sai(JRMCoreH.Race)
                ? JRMCoreH.mstc_sai(JRMCoreH.SklLvlX(1, JRMCoreH.PlyrSkillX) - 1)
                : (JRMCoreH.rc_arc(JRMCoreH.Race) ? JRMCoreH.mstc_arc() : (JRMCoreH.rc_humNam(JRMCoreH.Race) ? JRMCoreH.mstc_humnam() : 1)))
            : JRMCoreH.State;
        float inc = JRMCoreH.statInc(JRMCoreH.Pwrtyp, 11, 100, JRMCoreH.Race, JRMCoreH.Class, 0.0F) * 0.01F;
        float add = JRMCoreH.spdFrm(JRMCoreH.PlyrAttrbts(null)[4], n, (float) JRMCoreH.curRelease, DBCKiTech.turbo, true, st, JRMCoreH.State2, inc);

        boolean pressingRightClick = DBCKiTech.isPressed(JRMCoreClient.mc.gameSettings.keyBindUseItem);
        boolean pressingForward = DBCKiTech.isPressed(JRMCoreClient.mc.gameSettings.keyBindForward);
        boolean pressingLeft = DBCKiTech.isPressed(JRMCoreClient.mc.gameSettings.keyBindLeft);
        boolean pressingBack = DBCKiTech.isPressed(JRMCoreClient.mc.gameSettings.keyBindBack);
        boolean pressingRight = DBCKiTech.isPressed(JRMCoreClient.mc.gameSettings.keyBindRight);
        boolean isAnyDirectionKeyPressing = !JRMCoreH.StusEfctsMe(4) && !pressingRightClick && (pressingForward || pressingLeft || pressingBack || pressingRight);
        boolean forw = isAnyDirectionKeyPressing && DBCKiTech.isPressed(JRMCoreKeyHandler.Fn);

        // Swoop dodge_forwSwTm tracking (identical to DBC)
        if (DBCKiTech.dodge_forwSwTm == 0
            && DBCKiTech.isPressed(JRMCoreKeyHandler.Fn)
            && !DBCKiTech.isPressed(JRMCoreClient.mc.gameSettings.keyBindUseItem)
            && !DBCKiTech.isPressed(JRMCoreKeyHandler.KiCharge)
            && !DBCKiTech.isPressed(JRMCoreKeyHandler.KiAscend)) {
            DBCKiTech.dodge_forwSwTm = forw ? 1 : 0;
        }

        if (DBCKiTech.dodge_forwSwTm > 0 && isAnyDirectionKeyPressing && !JRMCoreClient.mc.thePlayer.onGround) {
            DBCKiTech.dodge_forwHold = true;
            DBCKiTech.dodge_forwSwTm++;
        } else {
            DBCKiTech.dodge_forwHold = false;
            DBCKiTech.dodge_forwSwTm = 0;
        }

        int[] PlyrAttrbts = JRMCoreH.PlyrAttrbts();
        byte pwr = JRMCoreH.Pwrtyp;
        byte rce = JRMCoreH.Race;
        byte cls = JRMCoreH.Class;
        int maxEnergy = JRMCoreH.stat(p, 5, pwr, 5, PlyrAttrbts[5], rce, cls, 0.0F);
        int ce2 = JRMCoreH.curEnergy;
        int cst2 = (int) ((float) maxEnergy * 0.1F - (float) n * 0.005F);
        int maxStam = JRMCoreH.stat(p, 2, pwr, 3, PlyrAttrbts[2], rce, cls, 0.0F);
        int ce = JRMCoreH.curStamina;
        int cst = (int) ((float) maxStam * (0.2F - (float) n * 0.005F));
        boolean dodge = !JRMCoreH.PlyrSettingsB(2);

        // Swoop section (identical to DBC, hard-assign intentional)
        if (ce > cst && n > 0 && dodge && ce2 > cst2 && !p.isRiding()) {
            if (DBCKiTech.dodge_forwHold && !p.onGround) {
                if (!DBCKiTechAccess.getDodgeForwDashSTE()) {
                    DBCKiTechAccess.setDodgeForwDashSTE(true);
                }
                if (DBCKiTech.floating) {
                    DBCKiTech.floating = false;
                    syncIsFlying();
                }

                float s = add * 1.5F;
                float w = JRMCoreH.weightPerc(1);
                s *= w;
                float yaw = p.rotationYaw;
                float pitch = p.rotationPitch;
                if (pressingLeft) {
                    yaw -= 90.0F / (!pressingForward && !pressingBack ? 1.0F : 2.0F) * (pressingBack ? -1.0F : 1.0F);
                    pitch = 0.0F;
                } else if (pressingRight) {
                    yaw += 90.0F / (!pressingForward && !pressingBack ? 1.0F : 2.0F) * (pressingBack ? -1.0F : 1.0F);
                    pitch = 0.0F;
                }
                if (pressingForward) {
                    pitch = p.rotationPitch;
                } else if (pressingBack) {
                    yaw -= 180.0F;
                    pitch = p.rotationPitch * -1.0F;
                }

                double motionX = (double) (-MathHelper.sin(yaw / 180.0F * (float) Math.PI) * MathHelper.cos(pitch / 180.0F * (float) Math.PI));
                double motionZ = (double) (MathHelper.cos(yaw / 180.0F * (float) Math.PI) * MathHelper.cos(pitch / 180.0F * (float) Math.PI));
                double motionY = (double) (-MathHelper.sin(pitch / 180.0F * (float) Math.PI));
                DBCKiTech.setThrowableHeading(p, motionX, motionY, motionZ, s * (float) JRMCoreConfig.Flngspd, 0.0F);

                if (DBCKiTechAccess.getSec() <= 0.0F || DBCKiTech.dodge_forwSwTm == 2) {
                    DBCKiTechAccess.setSec(10.0F);
                    DBCKiTech.triForce(4, 0, DBCKiTech.dodge_forwSwTm == 2 ? 5 : 4);
                    DBCKiTech.chargePart(false);
                }
                if (DBCKiTech.dodge_forwSwTm == 2) {
                    KeyBinding.setKeyBindState(JRMCoreKeyHandler.Fn.getKeyCode(), false);
                }
            }

            if (DBCKiTechAccess.getDodgeForwDashSTE() && !DBCKiTech.dodge_forwHold) {
                DBCKiTechAccess.setDodgeForwDashSTE(false);
                DBCKiTech.floating = true;
                syncIsFlying();
                DBCKiTech.dodge_forwSwTm = 0;
                KeyBinding.setKeyBindState(JRMCoreKeyHandler.Fn.getKeyCode(), false);
            }
        } else if (DBCKiTech.dodge_forwHold && DBCKiTechAccess.getDodgeForwDashSTE()) {
            DBCKiTechAccess.setDodgeForwDashSTE(false);
            DBCKiTech.floating = true;
            syncIsFlying();
            DBCKiTech.dodge_forwSwTm = 0;
            KeyBinding.setKeyBindState(JRMCoreKeyHandler.Fn.getKeyCode(), false);
        }

        // Sec/Sent tracking (identical to DBC)
        if (DBCKiTechAccess.getSec() > 0.0F) {
            DBCKiTechAccess.setSec(DBCKiTechAccess.getSec() - 1);
        }

        boolean isSwooping = JRMCoreH.StusEfctsMe(7);
        float sent = DBCKiTechAccess.getSent();
        if (isSwooping && !DBCKiTechAccess.getDodgeForwDashSTE() && sent <= 0.0F) {
            DBCKiTechAccess.setSent(JRMCoreCliTicH.counterValue * 0.5F);
            JRMCoreH.Skll((byte) 5, (byte) 1, (byte) 7);
        } else if (!isSwooping && DBCKiTechAccess.getDodgeForwDashSTE() && sent <= 0.0F) {
            DBCKiTechAccess.setSent(JRMCoreCliTicH.counterValue * 0.5F);
            JRMCoreH.Skll((byte) 5, (byte) 0, (byte) 7);
        } else if (sent > 0.0F && sent <= 1.0F && !isSwooping && !DBCKiTechAccess.getDodgeForwDashSTE()) {
            DBCKiTechAccess.setSent(0.0F);
        } else if (sent > 0.0F && sent <= 1.0F && isSwooping && DBCKiTechAccess.getDodgeForwDashSTE()) {
            DBCKiTechAccess.setSent(0.0F);
        }

        if (DBCKiTechAccess.getSent() > 0.0F) {
            DBCKiTechAccess.setSent(DBCKiTechAccess.getSent() - 1);
        }

        // Flight ability/cost check
        int cost = (int) (1.0F + add);
        boolean able = true;
        boolean b = n == 0;
        if (DBCConfig.flyAnyLvl) b = false;
        if (JRMCoreH.curEnergy < cost || b) able = false;

        float speedModifier = getSpeedModifier(p);

        if (DBCConfig.oldFly) {
            // Old fly mode
            if (DBCKiTech.isPressed(kiFlight) || DBCKiTech.isPressed(JRMCoreKeyHandler.Fn) && DBCKiTech.isPressed(kiFlight) && able) {
                DBCKiTech.floatTime++;
                if (DBCKiTech.floatTime >= 20) {
                    JRMCoreH.Cost(cost);
                    DBCKiTech.floatTime = 0;
                }

                if (DBCKiTech.isPressed(JRMCoreKeyHandler.Fn)) {
                    DBCClient.mc.thePlayer.motionY /= 15.15;
                } else {
                    DBCClient.mc.thePlayer.motionY = (double) (0.6F * add * (float) JRMCoreConfig.Flngspd);
                }

                float par1 = p.moveStrafing;
                float par2 = p.moveForward;
                if (DBCKiTech.isPressed(DBCClient.mc.gameSettings.keyBindForward)
                    || DBCKiTech.isPressed(DBCClient.mc.gameSettings.keyBindBack)
                    || DBCKiTech.isPressed(DBCClient.mc.gameSettings.keyBindLeft)
                    || DBCKiTech.isPressed(DBCClient.mc.gameSettings.keyBindRight)) {
                    // ENHANCED: Additive + addon base flight speed
                    float moveSpeed = add * DBCData.getClient().getBaseFlightSpeed() * DBCData.getClient().flightSpeedRelease / 100f * speedModifier;
                    move(par1, par2, p, moveSpeed);
                    DBCKiTech.floatMultAdded = true;
                } else {
                    // ENHANCED: No keys — friction decelerates naturally
                    DBCKiTech.floatMultAdded = false;
                }
            } else if (p.motionY > 0.0 && !p.onGround) {
                // ENHANCED: Decay upward momentum when flight key released
                DBCClient.mc.thePlayer.motionY *= ClientCache.flightVerticalDamping;
            }
        } else {
            // New fly mode
            if (DBCKiTech.isPressed(kiFlight)) {
                KeyBinding.setKeyBindState(kiFlight.getKeyCode(), false);
                if (able) {
                    if (!DBCKiTech.floating) {
                        if (p.onGround) {
                            DBCClient.mc.thePlayer.motionY = 0.5;
                            DBCKiTechAccess.setJumpToFly(true);
                        }
                    } else {
                        DBCKiTech.floatMultAdded = false;
                    }
                    DBCKiTech.floating = !DBCKiTech.floating;
                    syncIsFlying();
                }
            }

            if (DBCKiTech.floating && able && !p.onGround) {
                DBCKiTech.floatTime++;
                if (DBCKiTech.floatTime >= 20) {
                    JRMCoreH.Cost(cost);
                    DBCKiTech.floatTime = 0;
                }

                // Y-axis motion
                if (DBCKiTech.isPressed(keyBindJump)) {
                    DBCKiTech.jumpRel = true;
                    DBCClient.mc.thePlayer.motionY = (double) (0.25F * add * (float) JRMCoreConfig.Flngspd);
                } else if (DBCKiTech.isPressed(keyBindSneak)) {
                    if (DBCClient.mc.thePlayer.motionY > (double) (-(0.25F * add))) {
                        DBCClient.mc.thePlayer.motionY = (double) (-(0.25F * add) * (float) JRMCoreConfig.Flngspd);
                    }
                } else if (p.motionY > 0.0) {
                    // ENHANCED: Decay upward momentum when not pressing jump
                    DBCClient.mc.thePlayer.motionY *= ClientCache.flightVerticalDamping;
                } else if (p.motionY < 0.0) {
                    // ADDON: Flight gravity inline
                    boolean gravityOn = DBCData.getClient().flightGravity && JRMCoreConfig.PlayerFlyingDragDownOn;
                    DBCClient.mc.thePlayer.motionY = DBCClient.mc.thePlayer.motionY / (!JRMCoreH.isShtng && gravityOn ? 15.15 : 150.15);
                }

                float par1 = p.moveStrafing;
                float par2 = p.moveForward;
                if (DBCKiTech.isPressed(DBCClient.mc.gameSettings.keyBindForward)
                    || DBCKiTech.isPressed(DBCClient.mc.gameSettings.keyBindBack)
                    || DBCKiTech.isPressed(DBCClient.mc.gameSettings.keyBindLeft)
                    || DBCKiTech.isPressed(DBCClient.mc.gameSettings.keyBindRight)) {

                    // Swordsoul dynamic flight
                    if (JRMCoreH.StusEfctsMe(9)
                        && !JRMCoreH.StusEfctsMe(4)
                        && !DBCKiTech.isPressed(DBCClient.mc.gameSettings.keyBindBack)
                        && !DBCKiTech.isPressed(DBCClient.mc.gameSettings.keyBindLeft)
                        && !DBCKiTech.isPressed(DBCClient.mc.gameSettings.keyBindRight)) {
                        float wei = JRMCoreH.weightPerc(1);
                        float var53 = add * wei;
                        double motionXx = (double) (-MathHelper.sin(p.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(p.rotationPitch / 180.0F * (float) Math.PI));
                        double motionZx = (double) (MathHelper.cos(p.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(p.rotationPitch / 180.0F * (float) Math.PI));
                        double motionYx = (double) (-MathHelper.sin(p.rotationPitch / 180.0F * (float) Math.PI));
                        // ADDON: Dynamic flight speed multiplier
                        float dynamicSpeed = var53 * (float) JRMCoreConfig.Flngspd * DBCData.getClient().getDynamicFlightSpeed() * DBCData.getClient().flightSpeedRelease / 100f * speedModifier;
                        DBCKiTech.setThrowableHeading(p, motionXx, motionYx, motionZx, dynamicSpeed, 0.0F);
                    } else {
                        // ENHANCED: Additive + addon base flight speed
                        float moveSpeed = add * (float) JRMCoreConfig.Flngspd * DBCData.getClient().getBaseFlightSpeed() * DBCData.getClient().flightSpeedRelease / 100f * speedModifier;
                        move(par1, par2, p, moveSpeed);
                    }
                    DBCKiTech.floatMultAdded = true;
                } else {
                    // ENHANCED: No keys — friction decelerates naturally
                    DBCKiTech.floatMultAdded = false;
                }
            }

            // Ground landing
            if (DBCKiTech.floating && p.onGround) {
                if (DBCKiTechAccess.getJumpToFly()) {
                    DBCKiTechAccess.setJumpToFly(false);
                } else {
                    DBCKiTech.floating = false;
                    DBCKiTech.floatMultAdded = false;
                    syncIsFlying();
                }
            }
        }
    }

    /**
     * Syncs the isFlying state to the server when floating changes.
     */
    private static void syncIsFlying() {
        if (DBCData.getClient().isFlying != DBCKiTech.floating) {
            DBCData.getClient().isFlying = DBCKiTech.floating;
            DBCPacketHandler.Instance.sendToServer(new DBCSetValPacket(DBCData.getClient().player, EnumNBTType.BOOLEAN, "DBCisFlying", DBCKiTech.floating));
        }
    }
}
