package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.config.ConfigCapsules;
import kamkeel.npcdbc.constants.EnumHealthCapsules;
import kamkeel.npcdbc.constants.EnumKiCapsules;
import kamkeel.npcdbc.constants.EnumStaminaCapsules;

import java.util.HashMap;
import java.util.UUID;

public class CapsuleController {

    public static HashMap<UUID, Long> lastUsedKiCapsule = new HashMap<>();
    public static HashMap<UUID, Long> lastUsedHealthCapsule = new HashMap<>();
    public static HashMap<UUID, Long> lastUsedStaminaCapsule = new HashMap<>();

    public static HashMap<UUID, HashMap<Integer, Long>> lastAlternateKiCapsule = new HashMap<>();
    public static HashMap<UUID, HashMap<Integer, Long>> lastAlternateHealthCapsule = new HashMap<>();
    public static HashMap<UUID, HashMap<Integer, Long>> lastAlternateStaminaCapsule = new HashMap<>();

    public static void setKiCapsule(UUID playerUUID, int type){
        if(ConfigCapsules.EnableCapsuleCooldowns){
            if (type < 0 || type > EnumKiCapsules.count())
                type = 0;

            EnumKiCapsules kiCapsules = EnumKiCapsules.values()[type];
            long freedomTime = System.currentTimeMillis() + (kiCapsules.getCooldown() * 1000L);

            if(ConfigCapsules.KiCapsuleCooldownType == 0){
                CapsuleController.lastUsedKiCapsule.put(playerUUID, freedomTime);
            }
            else {
                if (!CapsuleController.lastAlternateKiCapsule.containsKey(playerUUID))
                    CapsuleController.lastAlternateKiCapsule.put(playerUUID, new HashMap<>());

                HashMap<Integer, Long> tierTime = CapsuleController.lastAlternateKiCapsule.get(playerUUID);
                tierTime.put(type, freedomTime);
                CapsuleController.lastAlternateKiCapsule.put(playerUUID, tierTime);
            }
        }
    }

    public static boolean canUseKiCapsule(UUID playerUUID, int type){
        if(ConfigCapsules.EnableCapsuleCooldowns){
            if (type < 0 || type > EnumKiCapsules.count())
                type = 0;

            long currentTime = System.currentTimeMillis();
            long freedomTime = 0;

            if(ConfigCapsules.KiCapsuleCooldownType == 0){
                if (CapsuleController.lastUsedKiCapsule.containsKey(playerUUID))
                    freedomTime = CapsuleController.lastUsedKiCapsule.get(playerUUID);

                return currentTime > freedomTime;
            }
            else {
                if (!CapsuleController.lastAlternateKiCapsule.containsKey(playerUUID))
                    CapsuleController.lastAlternateKiCapsule.put(playerUUID, new HashMap<>());

                HashMap<Integer, Long> tierTime = CapsuleController.lastAlternateKiCapsule.get(playerUUID);
                if (tierTime.containsKey(type))
                    freedomTime = tierTime.get(type);

                return currentTime > freedomTime;
            }
        }
        return true;
    }


    public static void setHealthCapsule(UUID playerUUID, int type){
        if(ConfigCapsules.EnableCapsuleCooldowns){
            if (type < 0 || type > EnumHealthCapsules.count())
                type = 0;

            EnumHealthCapsules kiCapsules = EnumHealthCapsules.values()[type];
            long freedomTime = System.currentTimeMillis() + (kiCapsules.getCooldown() * 1000L);

            if(ConfigCapsules.HealthCapsuleCooldownType == 0){
                CapsuleController.lastUsedHealthCapsule.put(playerUUID, freedomTime);
            }
            else {
                if (!CapsuleController.lastAlternateHealthCapsule.containsKey(playerUUID))
                    CapsuleController.lastAlternateHealthCapsule.put(playerUUID, new HashMap<>());

                HashMap<Integer, Long> tierTime = CapsuleController.lastAlternateHealthCapsule.get(playerUUID);
                tierTime.put(type, freedomTime);
                CapsuleController.lastAlternateHealthCapsule.put(playerUUID, tierTime);
            }
        }
    }

    public static boolean canUseHealthCapsule(UUID playerUUID, int type){
        if(ConfigCapsules.EnableCapsuleCooldowns){
            if (type < 0 || type > EnumHealthCapsules.count())
                type = 0;

            long currentTime = System.currentTimeMillis();
            long freedomTime = 0;

            if(ConfigCapsules.HealthCapsuleCooldownType == 0){
                if (CapsuleController.lastUsedHealthCapsule.containsKey(playerUUID))
                    freedomTime = CapsuleController.lastUsedHealthCapsule.get(playerUUID);

                return currentTime > freedomTime;
            }
            else {
                if (!CapsuleController.lastAlternateHealthCapsule.containsKey(playerUUID))
                    CapsuleController.lastAlternateHealthCapsule.put(playerUUID, new HashMap<>());

                HashMap<Integer, Long> tierTime = CapsuleController.lastAlternateHealthCapsule.get(playerUUID);
                if (tierTime.containsKey(type))
                    freedomTime = tierTime.get(type);

                return currentTime > freedomTime;
            }
        }
        return true;
    }

    public static void setStaminaCapsule(UUID playerUUID, int type){
        if(ConfigCapsules.EnableCapsuleCooldowns){
            if (type < 0 || type > EnumStaminaCapsules.count())
                type = 0;

            EnumStaminaCapsules kiCapsules = EnumStaminaCapsules.values()[type];
            long freedomTime = System.currentTimeMillis() + (kiCapsules.getCooldown() * 1000L);

            if(ConfigCapsules.StaminaCapsuleCooldownType == 0){
                CapsuleController.lastUsedStaminaCapsule.put(playerUUID, freedomTime);
            }
            else {
                if (!CapsuleController.lastAlternateStaminaCapsule.containsKey(playerUUID))
                    CapsuleController.lastAlternateStaminaCapsule.put(playerUUID, new HashMap<>());

                HashMap<Integer, Long> tierTime = CapsuleController.lastAlternateStaminaCapsule.get(playerUUID);
                tierTime.put(type, freedomTime);
                CapsuleController.lastAlternateStaminaCapsule.put(playerUUID, tierTime);
            }
        }
    }

    public static boolean canUseStaminaCapsule(UUID playerUUID, int type){
        if(ConfigCapsules.EnableCapsuleCooldowns){
            if (type < 0 || type > EnumStaminaCapsules.count())
                type = 0;

            long currentTime = System.currentTimeMillis();
            long freedomTime = 0;

            if(ConfigCapsules.StaminaCapsuleCooldownType == 0){
                if (CapsuleController.lastUsedStaminaCapsule.containsKey(playerUUID))
                    freedomTime = CapsuleController.lastUsedStaminaCapsule.get(playerUUID);

                return currentTime > freedomTime;
            }
            else {
                if (!CapsuleController.lastAlternateStaminaCapsule.containsKey(playerUUID))
                    CapsuleController.lastAlternateStaminaCapsule.put(playerUUID, new HashMap<>());

                HashMap<Integer, Long> tierTime = CapsuleController.lastAlternateStaminaCapsule.get(playerUUID);
                if (tierTime.containsKey(type))
                    freedomTime = tierTime.get(type);

                return currentTime > freedomTime;
            }
        }
        return true;
    }
}
