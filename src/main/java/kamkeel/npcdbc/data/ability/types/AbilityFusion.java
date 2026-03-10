package kamkeel.npcdbc.data.ability.types;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import kamkeel.npcdbc.constants.DBCSkills;
import kamkeel.npcdbc.controllers.FusionHandler;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.network.NetworkUtility;
import kamkeel.npcdbc.util.DBCSettingsUtil;
import kamkeel.npcdbc.util.Utility;
import kamkeel.npcs.controllers.data.ability.Ability;
import kamkeel.npcs.controllers.data.ability.enums.*;
import kamkeel.npcs.controllers.data.telegraph.TelegraphType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.gui.builder.FieldDef;
import noppes.npcs.controllers.AnimationController;
import noppes.npcs.controllers.data.Animation;
import noppes.npcs.controllers.data.PlayerAbilityData;
import noppes.npcs.controllers.data.PlayerData;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AbilityFusion extends Ability {

    private String leftFuseeAnimationName = "Fusion_Left";
    private String rightFuseeAnimationName = "Fusion_Right";
    private int leftFuseeAnimationId = -1;
    private int rightFuseeAnimationId = -1;

    private int fusionDanceTicks = 92;
    private int fusionRequestTicks = 80;

    private String fusionMessage = "§6FUUUU-SION! §bHA!";

    private float range = 8;
    private int timeLimit = 20;

    private UUID fuseeUUID = null;
    private UUID playerUUID = null;
    private int fusionStartTick = -1;
    private boolean isFusing = false;

    public AbilityFusion() {
        this.typeId = "ability.npcdbc.fusion_dance";
        this.name = "Fusion Dance";
        this.minRange = 0.0f;
        this.cooldownTicks = 0;
        this.lockMovement = LockMode.WINDUP;
        this.windUpTicks = 60;
        this.telegraphType = TelegraphType.NONE;
        this.showTelegraph = false;
        this.allowedBy = UserType.PLAYER_ONLY;
        this.rotationMode = RotationMode.LOCKED;
        this.rotationPhase = LockMode.ACTIVE;
    }

    @Override
    public boolean hasDamage() {
        return false;
    }

    @Override
    public boolean isTargetingModeLocked() {
        return true;
    }

    @Override
    public boolean allowBurst() {
        return false;
    }

    @Override
    public TargetingMode[] getAllowedTargetingModes() {
        return new TargetingMode[]{TargetingMode.SELF};
    }

    @Override
    public void onExecute(EntityLivingBase caster, EntityLivingBase target) {

    }

    @Override
    public void onWindUpTick(EntityLivingBase caster, EntityLivingBase target, int tick) {
        if (!(caster instanceof EntityPlayer)) {
            cleanup();
            signalCompletion();
            return;
        }

        EntityPlayer player = (EntityPlayer) caster;

        if (tick == 1) {
            if (!checkAllConditions(player, null, false)) {
                cleanup();
                signalCompletion();
                return;
            }

            playerUUID = Utility.getUUID(player);
        }


        if (fuseeUUID != null) return;

        List<EntityPlayer> nearbyPlayers = player.worldObj.getEntitiesWithinAABB(EntityPlayer.class, player.boundingBox.expand(range, range, range));

        for (EntityPlayer nearbyPlayer : nearbyPlayers) {
            if (nearbyPlayer == player)
                continue;

            if (checkAllConditions(player, nearbyPlayer, true) && isFuseeReadyToFuse(nearbyPlayer)) {
                fuseeUUID = Utility.getUUID(nearbyPlayer);
                break;
            }
        }
    }

    @Override
    public void onActiveTick(EntityLivingBase caster, EntityLivingBase target, int tick) {
        if (!(caster instanceof EntityPlayer) || fuseeUUID == null) {
            cleanup();
            signalCompletion();
            return;
        }

        EntityPlayer player = (EntityPlayer) caster;
        EntityPlayer fusee = (EntityPlayer) Utility.getFromUUID(fuseeUUID, caster.worldObj);

        if (fusee == null) {
            cleanup();
            signalCompletion();
            return;
        }

        if (isFusing) {
            if (tick >= fusionDanceTicks + fusionStartTick) {
                DBCData.fusePlayers(player, fusee, timeLimit);
                NetworkUtility.sendServerMessage(player, fusionMessage);
                NetworkUtility.sendServerMessage(fusee, fusionMessage);
                cleanup();
                signalCompletion();
            }

            return;
        }

        // Checks if the fusee already requested the caster
        UUID fuseeTarget = FusionHandler.fusionDance.get(fuseeUUID);
        boolean fuseeRequestedUs = playerUUID.equals(fuseeTarget);

        if (fuseeRequestedUs) {
            // Mutual request confirmed, start fusion
            FusionHandler.fusionDance.remove(playerUUID);
            FusionHandler.fusionDance.remove(fuseeUUID);

            prepareForFusion(player, fusee);
            fusionStartTick = tick;
            isFusing = true;
        } else {
            // Register fusion request from player to fusee
            FusionHandler.fusionDance.put(playerUUID, fuseeUUID);

            // Timeout: if no response, cancel
            if (tick >= fusionRequestTicks) {
                FusionHandler.fusionDance.remove(playerUUID);
                NetworkUtility.sendServerMessage(player, "§c", "npcdbc.fusionTimeout");
                cleanup();
                signalCompletion();
            }
        }
    }

    @Override
    public void cleanup() {
        FusionHandler.fusionDance.remove(playerUUID);
        FusionHandler.fusionDance.remove(fuseeUUID);

        fusionStartTick = -1;
        isFusing = false;
    }

    private boolean checkAllConditions(EntityPlayer sender, EntityPlayer fusee, boolean checkFusee) {
        EntityPlayer player = checkFusee ? fusee : sender;

        if (isFused(player)) {
            NetworkUtility.sendServerMessage(sender, "§c", player.getCommandSenderName(), " ", "npcdbc.alreadyFused");
            return false;
        }

        if (!hasFusionSkill(player)) {
            NetworkUtility.sendServerMessage(sender, "§c", player.getCommandSenderName(), " ", "npcdbc.fusionNoSkill");
            return false;
        }

        if (!hasFusionEnabled(player)) {
            NetworkUtility.sendServerMessage(sender, "§c", player.getCommandSenderName(), " ", "npcdbc.fusionSkillFusion");
            return false;
        }

        if (hasNoFuse(player)) {
            NetworkUtility.sendServerMessage(sender, "§c", player.getCommandSenderName(), " ", "npcdbc.noFuse");
            return false;
        }

        return true;
    }

    private boolean isFuseeReadyToFuse(EntityPlayer fusee) {
        PlayerAbilityData data = PlayerData.get(fusee).abilityData;
        if (!data.isExecutingAbility()) return false;
        Ability currentAbility = data.getCurrentAbility();
        if (!(currentAbility instanceof AbilityFusion)) return false;

        AbilityPhase phase = currentAbility.getPhase();
        return phase == AbilityPhase.WINDUP;
    }

    private boolean hasNoFuse(EntityPlayer player) {
        DBCData data = DBCData.get(player);
        String fusionString = data.getRawCompound().getString("jrmcFuzion");
        if (fusionString == null)
            return false;

        fusionString = fusionString.replace(" ", "");
        if (fusionString.isEmpty() || fusionString.contains(","))
            return false;

        try {
            return Integer.parseInt(fusionString) > 0;
        } catch (Exception ignored) {
            return true;
        }
    }

    private boolean hasFusionSkill(EntityPlayer player) {
        DBCData data = DBCData.get(player);
        return data.hasSkill(DBCSkills.Fusion.getId());
    }

    private boolean hasFusionEnabled(EntityPlayer player) {
        return DBCSettingsUtil.isFusion(player);
    }

    private boolean isFused(EntityPlayer player) {
        DBCData data = DBCData.get(player);
        return data.stats.isFused();
    }

    private void prepareForFusion(EntityPlayer player, EntityPlayer fusee) {
        double yaw = Math.toRadians(player.rotationYaw);

        double leftX = Math.cos(yaw);
        double leftZ = -Math.sin(yaw);

        double targetX = player.posX;
        double targetZ = player.posZ;

        for (int dist = 1; dist <= 2; dist++) {
            double tx = player.posX + leftX * dist;
            double tz = player.posZ + leftZ * dist;

            if (player.worldObj.isAirBlock((int) tx, (int) player.posY, (int) tz)) {
                targetX = tx;
                targetZ = tz;
                break;
            }
        }

        fusee.setPositionAndUpdate(targetX, player.posY, targetZ);
        player.setRotationYawHead(player.rotationYaw);
        fusee.setRotationYawHead(player.rotationYaw);

        playFusionAnimation(player, true);
        playFusionAnimation(fusee, false);
    }

    private void playFusionAnimation(EntityPlayer player, boolean right) {
        if (AnimationController.Instance == null) return;

        String name = right ? rightFuseeAnimationName : leftFuseeAnimationName;
        int id = right ? rightFuseeAnimationId : leftFuseeAnimationId;
        Animation animation = null;

        if (AnimationController.Instance.get(id) != null) {
            animation = (Animation) AnimationController.Instance.get(id);
        } else if (AnimationController.Instance.get(name, true) != null) {
            animation = (Animation) AnimationController.Instance.get(name, true);
        }

        if (animation == null) return;

        setAnimationData(player, animation);
    }

    protected void setAnimationData(EntityPlayer player, Animation animation) {
        PlayerData playerData = PlayerData.get(player);
        playerData.animationData.setEnabled(true);
        playerData.animationData.setAnimation(animation);
        playerData.animationData.updateClient();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getAbilityDefinitions(List<FieldDef> defs) {
        defs.addAll(Arrays.asList(
            FieldDef.row(
                FieldDef.intField("ability.timeLimit", this::getTimeLimit, this::setTimeLimit),
                FieldDef.floatField("gui.range", this::getRange, this::setRange)
            ),
            FieldDef.row(
                FieldDef.intField("ability.danceTicks", this::getFusionDanceTicks, this::setFusionDanceTicks),
                FieldDef.intField("ability.requestTicks", this::getFusionRequestTicks, this::setFusionRequestTicks)
            ),
            FieldDef.stringField("ability.fusionMessage", this::getFusionMessage, this::setFusionMessage)
        ));

        FieldDef.modifyVisibility(defs, "ability.activeAnimation", () -> false);

        FieldDef.insertAfter(defs, "ability.windUpAnimation", FieldDef.animSubGui("ability.leftFusionAnimation",
            this::getLeftFuseeAnimationId, this::setLeftFuseeAnimationId,
            this::getLeftFuseeAnimationName, this::setLeftFuseeAnimationName
        ));

        FieldDef.insertAfter(defs, "ability.leftFusionAnimation", FieldDef.animSubGui("ability.rightFusionAnimation",
            this::getRightFuseeAnimationId, this::setRightFuseeAnimationId,
            this::getRightFuseeAnimationName, this::setRightFuseeAnimationName
        ));
    }

    @Override
    public void writeTypeNBT(NBTTagCompound nbt) {
        nbt.setInteger("fusionDanceTicks", fusionDanceTicks);
        nbt.setInteger("fusionRequestTicks", fusionRequestTicks);
        nbt.setFloat("range", range);
        nbt.setInteger("timeLimit", timeLimit);
        nbt.setString("fusionMessage", fusionMessage);
        nbt.setInteger("leftFuseeAnimationId", leftFuseeAnimationId);
        nbt.setString("leftFuseeAnimationName", leftFuseeAnimationName);
        nbt.setInteger("rightFuseeAnimationId", rightFuseeAnimationId);
        nbt.setString("rightFuseeAnimationName", rightFuseeAnimationName);
    }

    @Override
    public void readTypeNBT(NBTTagCompound nbt) {
        fusionDanceTicks = nbt.getInteger("fusionDanceTicks");
        fusionRequestTicks = nbt.getInteger("fusionRequestTicks");
        range = nbt.getFloat("range");
        timeLimit = nbt.getInteger("timeLimit");
        fusionMessage = nbt.getString("fusionMessage");
        leftFuseeAnimationId = nbt.getInteger("leftFuseeAnimationId");
        leftFuseeAnimationName = nbt.getString("leftFuseeAnimationName");
        rightFuseeAnimationId = nbt.getInteger("rightFuseeAnimationId");
        rightFuseeAnimationName = nbt.getString("rightFuseeAnimationName");
    }

    public int getFusionDanceTicks() {
        return fusionDanceTicks;
    }

    public void setFusionDanceTicks(int fusionDanceTicks) {
        this.fusionDanceTicks = fusionDanceTicks;
    }

    public int getFusionRequestTicks() {
        return fusionRequestTicks;
    }

    public void setFusionRequestTicks(int fusionRequestTicks) {
        this.fusionRequestTicks = fusionRequestTicks;
    }

    public String getFusionMessage() {
        return fusionMessage;
    }

    public void setFusionMessage(String fusionMessage) {
        this.fusionMessage = fusionMessage;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = Math.max(1, timeLimit);
    }

    public int getLeftFuseeAnimationId() {
        return leftFuseeAnimationId;
    }

    public void setLeftFuseeAnimationId(int leftFuseeAnimationId) {
        this.leftFuseeAnimationId = leftFuseeAnimationId;
    }

    public String getLeftFuseeAnimationName() {
        return leftFuseeAnimationName;
    }

    public void setLeftFuseeAnimationName(String leftFuseeAnimationName) {
        this.leftFuseeAnimationName = leftFuseeAnimationName;
    }

    public int getRightFuseeAnimationId() {
        return rightFuseeAnimationId;
    }

    public void setRightFuseeAnimationId(int rightFuseeAnimationId) {
        this.rightFuseeAnimationId = rightFuseeAnimationId;
    }

    public String getRightFuseeAnimationName() {
        return rightFuseeAnimationName;
    }

    public void setRightFuseeAnimationName(String rightFuseeAnimationName) {
        this.rightFuseeAnimationName = rightFuseeAnimationName;
    }
}
