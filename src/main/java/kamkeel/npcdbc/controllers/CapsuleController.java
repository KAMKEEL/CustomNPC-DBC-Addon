package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.config.ConfigCapsules;
import kamkeel.npcdbc.constants.Capsule;
import kamkeel.npcdbc.constants.enums.EnumHealthCapsules;
import kamkeel.npcdbc.constants.enums.EnumKiCapsules;
import kamkeel.npcdbc.constants.enums.EnumMiscCapsules;
import kamkeel.npcdbc.constants.enums.EnumStaminaCapsules;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

public class CapsuleController {

    public static CapsuleController Instance = new CapsuleController();

    public HashMap<UUID, HashMap<Integer, Long>> miscCapsuleCooldown = new HashMap<>();

    // For Capsule Type as a Whole [Cooldown Type 0]
    public HashMap<UUID, Long> lastUsedKiCapsule = new HashMap<>();
    public HashMap<UUID, Long> lastUsedHealthCapsule = new HashMap<>();
    public HashMap<UUID, Long> lastUsedStaminaCapsule = new HashMap<>();

    // For Individual Capsule [Cooldown Type 1]
    public HashMap<UUID, HashMap<Integer, Long>> lastAlternateKiCapsule = new HashMap<>();
    public HashMap<UUID, HashMap<Integer, Long>> lastAlternateHealthCapsule = new HashMap<>();
    public HashMap<UUID, HashMap<Integer, Long>> lastAlternateStaminaCapsule = new HashMap<>();

    public HashMap<Integer, HashMap<Integer, Integer>> capsuleStrength = new HashMap<>();
    public HashMap<Integer, HashMap<Integer, Integer>> capsuleCooldowns = new HashMap<>();

    public static void setMiscCapsule(UUID playerUUID, int type){
        if(ConfigCapsules.EnableCapsuleCooldowns){
            if (type < 0 || type > EnumMiscCapsules.count())
                type = 0;

            EnumMiscCapsules miscCapsules = EnumMiscCapsules.values()[type];
            long freedomTime = System.currentTimeMillis() + (miscCapsules.getCooldown() * 1000L);

            if (!CapsuleController.Instance.miscCapsuleCooldown.containsKey(playerUUID))
                CapsuleController.Instance.miscCapsuleCooldown.put(playerUUID, new HashMap<>());

            HashMap<Integer, Long> tierTime = CapsuleController.Instance.miscCapsuleCooldown.get(playerUUID);
            tierTime.put(type, freedomTime);
            CapsuleController.Instance.miscCapsuleCooldown.put(playerUUID, tierTime);
        }
    }

    public static long canUseMiscCapsule(UUID playerUUID, int type){
        if(ConfigCapsules.EnableCapsuleCooldowns){
            if (type < 0 || type > EnumMiscCapsules.count())
                type = 0;

            long currentTime = System.currentTimeMillis();
            long freedomTime = 0;

            if (!CapsuleController.Instance.miscCapsuleCooldown.containsKey(playerUUID))
                CapsuleController.Instance.miscCapsuleCooldown.put(playerUUID, new HashMap<>());

            HashMap<Integer, Long> tierTime = CapsuleController.Instance.miscCapsuleCooldown.get(playerUUID);
            if (tierTime.containsKey(type))
                freedomTime = tierTime.get(type);

            // Calculate remaining time in milliseconds
            long remainingTimeMillis = freedomTime - currentTime;

            // Return remaining time in seconds
            return (remainingTimeMillis + 999) / 1000;
        }
        return 0;
    }

    public static void setKiCapsule(UUID playerUUID, int type){
        if(ConfigCapsules.EnableCapsuleCooldowns){
            if (type < 0 || type > EnumKiCapsules.count())
                type = 0;

            EnumKiCapsules kiCapsules = EnumKiCapsules.values()[type];
            long freedomTime = System.currentTimeMillis() + (kiCapsules.getCooldown() * 1000L);

            if(ConfigCapsules.KiCapsuleCooldownType == 0){
                CapsuleController.Instance.lastUsedKiCapsule.put(playerUUID, freedomTime);
            }
            else {
                if (!CapsuleController.Instance.lastAlternateKiCapsule.containsKey(playerUUID))
                    CapsuleController.Instance.lastAlternateKiCapsule.put(playerUUID, new HashMap<>());

                HashMap<Integer, Long> tierTime = CapsuleController.Instance.lastAlternateKiCapsule.get(playerUUID);
                tierTime.put(type, freedomTime);
                CapsuleController.Instance.lastAlternateKiCapsule.put(playerUUID, tierTime);
            }
        }
    }

    public static long canUseKiCapsule(UUID playerUUID, int type){
        if(ConfigCapsules.EnableCapsuleCooldowns){
            if (type < 0 || type > EnumKiCapsules.count())
                type = 0;

            long currentTime = System.currentTimeMillis();
            long freedomTime = 0;

            if(ConfigCapsules.KiCapsuleCooldownType == 0){
                if (CapsuleController.Instance.lastUsedKiCapsule.containsKey(playerUUID))
                    freedomTime = CapsuleController.Instance.lastUsedKiCapsule.get(playerUUID);

                // Calculate remaining time in milliseconds
                long remainingTimeMillis = freedomTime - currentTime;

                // Return remaining time in seconds
                return (remainingTimeMillis + 999) / 1000;
            }
            else {
                if (!CapsuleController.Instance.lastAlternateKiCapsule.containsKey(playerUUID))
                    CapsuleController.Instance.lastAlternateKiCapsule.put(playerUUID, new HashMap<>());

                HashMap<Integer, Long> tierTime = CapsuleController.Instance.lastAlternateKiCapsule.get(playerUUID);
                if (tierTime.containsKey(type))
                    freedomTime = tierTime.get(type);

                // Calculate remaining time in milliseconds
                long remainingTimeMillis = freedomTime - currentTime;

                // Return remaining time in seconds
                return (remainingTimeMillis + 999) / 1000;
            }
        }
        return 0;
    }


    public static void setHealthCapsule(UUID playerUUID, int type){
        if(ConfigCapsules.EnableCapsuleCooldowns){
            if (type < 0 || type > EnumHealthCapsules.count())
                type = 0;

            EnumHealthCapsules kiCapsules = EnumHealthCapsules.values()[type];
            long freedomTime = System.currentTimeMillis() + (kiCapsules.getCooldown() * 1000L);

            if(ConfigCapsules.HealthCapsuleCooldownType == 0){
                CapsuleController.Instance.lastUsedHealthCapsule.put(playerUUID, freedomTime);
            }
            else {
                if (!CapsuleController.Instance.lastAlternateHealthCapsule.containsKey(playerUUID))
                    CapsuleController.Instance.lastAlternateHealthCapsule.put(playerUUID, new HashMap<>());

                HashMap<Integer, Long> tierTime = CapsuleController.Instance.lastAlternateHealthCapsule.get(playerUUID);
                tierTime.put(type, freedomTime);
                CapsuleController.Instance.lastAlternateHealthCapsule.put(playerUUID, tierTime);
            }
        }
    }

    public static long canUseHealthCapsule(UUID playerUUID, int type){
        if(ConfigCapsules.EnableCapsuleCooldowns){
            if (type < 0 || type > EnumHealthCapsules.count())
                type = 0;

            long currentTime = System.currentTimeMillis();
            long freedomTime = 0;

            if(ConfigCapsules.HealthCapsuleCooldownType == 0){
                if (CapsuleController.Instance.lastUsedHealthCapsule.containsKey(playerUUID))
                    freedomTime = CapsuleController.Instance.lastUsedHealthCapsule.get(playerUUID);

                // Calculate remaining time in milliseconds
                long remainingTimeMillis = freedomTime - currentTime;

                // Return remaining time in seconds
                return (remainingTimeMillis + 999) / 1000;
            }
            else {
                if (!CapsuleController.Instance.lastAlternateHealthCapsule.containsKey(playerUUID))
                    CapsuleController.Instance.lastAlternateHealthCapsule.put(playerUUID, new HashMap<>());

                HashMap<Integer, Long> tierTime = CapsuleController.Instance.lastAlternateHealthCapsule.get(playerUUID);
                if (tierTime.containsKey(type))
                    freedomTime = tierTime.get(type);

                // Calculate remaining time in milliseconds
                long remainingTimeMillis = freedomTime - currentTime;

                // Return remaining time in seconds
                return (remainingTimeMillis + 999) / 1000;
            }
        }
        return 0;
    }

    public static void setStaminaCapsule(UUID playerUUID, int type){
        if(ConfigCapsules.EnableCapsuleCooldowns){
            if (type < 0 || type > EnumStaminaCapsules.count())
                type = 0;

            EnumStaminaCapsules kiCapsules = EnumStaminaCapsules.values()[type];
            long freedomTime = System.currentTimeMillis() + (kiCapsules.getCooldown() * 1000L);

            if(ConfigCapsules.StaminaCapsuleCooldownType == 0){
                CapsuleController.Instance.lastUsedStaminaCapsule.put(playerUUID, freedomTime);
            }
            else {
                if (!CapsuleController.Instance.lastAlternateStaminaCapsule.containsKey(playerUUID))
                    CapsuleController.Instance.lastAlternateStaminaCapsule.put(playerUUID, new HashMap<>());

                HashMap<Integer, Long> tierTime = CapsuleController.Instance.lastAlternateStaminaCapsule.get(playerUUID);
                tierTime.put(type, freedomTime);
                CapsuleController.Instance.lastAlternateStaminaCapsule.put(playerUUID, tierTime);
            }
        }
    }

    public static long canUseStaminaCapsule(UUID playerUUID, int type){
        if(ConfigCapsules.EnableCapsuleCooldowns){
            if (type < 0 || type > EnumStaminaCapsules.count())
                type = 0;

            long currentTime = System.currentTimeMillis();
            long freedomTime = 0;

            if(ConfigCapsules.StaminaCapsuleCooldownType == 0){
                if (CapsuleController.Instance.lastUsedStaminaCapsule.containsKey(playerUUID))
                    freedomTime = CapsuleController.Instance.lastUsedStaminaCapsule.get(playerUUID);

                // Calculate remaining time in milliseconds
                long remainingTimeMillis = freedomTime - currentTime;

                // Return remaining time in seconds
                return (remainingTimeMillis + 999) / 1000;
            }
            else {
                if (!CapsuleController.Instance.lastAlternateStaminaCapsule.containsKey(playerUUID))
                    CapsuleController.Instance.lastAlternateStaminaCapsule.put(playerUUID, new HashMap<>());

                HashMap<Integer, Long> tierTime = CapsuleController.Instance.lastAlternateStaminaCapsule.get(playerUUID);
                if (tierTime.containsKey(type))
                    freedomTime = tierTime.get(type);

                // Calculate remaining time in milliseconds
                long remainingTimeMillis = freedomTime - currentTime;

                // Return remaining time in seconds
                return (remainingTimeMillis + 999) / 1000;
            }
        }
        return 0;
    }

    public void load(){
        // Clean
        lastUsedKiCapsule.clear();
        lastAlternateKiCapsule.clear();
        lastUsedHealthCapsule.clear();
        lastAlternateHealthCapsule.clear();
        lastUsedStaminaCapsule.clear();
        lastAlternateStaminaCapsule.clear();
        miscCapsuleCooldown.clear();
        capsuleStrength.clear();
        capsuleCooldowns.clear();

        capsuleStrength.put(Capsule.HP, new HashMap<>());
        capsuleStrength.put(Capsule.KI, new HashMap<>());
        capsuleStrength.put(Capsule.STAMINA, new HashMap<>());

        capsuleCooldowns.put(Capsule.MISC, new HashMap<>());
        capsuleCooldowns.put(Capsule.HP, new HashMap<>());
        capsuleCooldowns.put(Capsule.KI, new HashMap<>());
        capsuleCooldowns.put(Capsule.STAMINA, new HashMap<>());

        HashMap<Integer, Integer> miscCooldown = capsuleCooldowns.get(Capsule.MISC);
        HashMap<Integer, Integer> hpCooldown = capsuleCooldowns.get(Capsule.HP);
        HashMap<Integer, Integer> kiCooldown = capsuleCooldowns.get(Capsule.KI);
        HashMap<Integer, Integer> staminaCooldown = capsuleCooldowns.get(Capsule.STAMINA);
        HashMap<Integer, Integer> hpStrength = capsuleStrength.get(Capsule.HP);
        HashMap<Integer, Integer> kiStrength = capsuleStrength.get(Capsule.KI);
        HashMap<Integer, Integer> staminaStrength = capsuleStrength.get(Capsule.STAMINA);

        for(EnumMiscCapsules miscCapsules : EnumMiscCapsules.values()){
            miscCooldown.put(miscCapsules.getMeta(), miscCapsules.getCooldown());
        }
        for(EnumHealthCapsules healthCapsules : EnumHealthCapsules.values()){
            hpCooldown.put(healthCapsules.getMeta(), healthCapsules.getCooldown());
            hpStrength.put(healthCapsules.getMeta(), healthCapsules.getStrength());
        }
        for(EnumKiCapsules kiCapsule : EnumKiCapsules.values()){
            kiCooldown.put(kiCapsule.getMeta(), kiCapsule.getCooldown());
            kiStrength.put(kiCapsule.getMeta(), kiCapsule.getStrength());
        }
        for(EnumStaminaCapsules staminaCapsule : EnumStaminaCapsules.values()){
            staminaCooldown.put(staminaCapsule.getMeta(), staminaCapsule.getCooldown());
            staminaStrength.put(staminaCapsule.getMeta(), staminaCapsule.getStrength());
        }
    }

    public static byte[] serializeHashMap(HashMap<Integer, HashMap<Integer, Integer>> map) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(map);
        objectOutputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }

    @SuppressWarnings("unchecked")
    public static HashMap<Integer, HashMap<Integer, Integer>> deserializeHashMap(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return (HashMap<Integer, HashMap<Integer, Integer>>) objectInputStream.readObject();
    }

    public static CapsuleController getInstance() {
        return Instance;
    }
}
