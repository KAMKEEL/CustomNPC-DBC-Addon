package kamkeel.npcdbc.controllers;

import jdk.nashorn.internal.runtime.regexp.joni.Config;
import kamkeel.npcdbc.config.ConfigCapsules;
import kamkeel.npcdbc.constants.Capsule;
import kamkeel.npcdbc.constants.enums.*;
import net.minecraft.util.MathHelper;

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
    public HashMap<UUID, Long> lastUsedRegenCapsule = new HashMap<>();

    // For Individual Capsule [Cooldown Type 1]
    public HashMap<UUID, HashMap<Integer, Long>> lastIndividualKiCapsule = new HashMap<>();
    public HashMap<UUID, HashMap<Integer, Long>> lastIndividualHealthCapsule = new HashMap<>();
    public HashMap<UUID, HashMap<Integer, Long>> lastIndividualStaminaCapsule = new HashMap<>();
    public HashMap<UUID, HashMap<Integer, Long>> lastIndividualRegenCapsule = new HashMap<>();

    // For Regen Capsule Types [Cooldown Type 2]
    public HashMap<UUID, HashMap<Integer, Long>> lastRegenTypeCapsule = new HashMap<>();

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
                if (!CapsuleController.Instance.lastIndividualKiCapsule.containsKey(playerUUID))
                    CapsuleController.Instance.lastIndividualKiCapsule.put(playerUUID, new HashMap<>());

                HashMap<Integer, Long> tierTime = CapsuleController.Instance.lastIndividualKiCapsule.get(playerUUID);
                tierTime.put(type, freedomTime);
                CapsuleController.Instance.lastIndividualKiCapsule.put(playerUUID, tierTime);
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
                if (!CapsuleController.Instance.lastIndividualKiCapsule.containsKey(playerUUID))
                    CapsuleController.Instance.lastIndividualKiCapsule.put(playerUUID, new HashMap<>());

                HashMap<Integer, Long> tierTime = CapsuleController.Instance.lastIndividualKiCapsule.get(playerUUID);
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
                if (!CapsuleController.Instance.lastIndividualHealthCapsule.containsKey(playerUUID))
                    CapsuleController.Instance.lastIndividualHealthCapsule.put(playerUUID, new HashMap<>());

                HashMap<Integer, Long> tierTime = CapsuleController.Instance.lastIndividualHealthCapsule.get(playerUUID);
                tierTime.put(type, freedomTime);
                CapsuleController.Instance.lastIndividualHealthCapsule.put(playerUUID, tierTime);
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
                if (!CapsuleController.Instance.lastIndividualHealthCapsule.containsKey(playerUUID))
                    CapsuleController.Instance.lastIndividualHealthCapsule.put(playerUUID, new HashMap<>());

                HashMap<Integer, Long> tierTime = CapsuleController.Instance.lastIndividualHealthCapsule.get(playerUUID);
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
                if (!CapsuleController.Instance.lastIndividualStaminaCapsule.containsKey(playerUUID))
                    CapsuleController.Instance.lastIndividualStaminaCapsule.put(playerUUID, new HashMap<>());

                HashMap<Integer, Long> tierTime = CapsuleController.Instance.lastIndividualStaminaCapsule.get(playerUUID);
                tierTime.put(type, freedomTime);
                CapsuleController.Instance.lastIndividualStaminaCapsule.put(playerUUID, tierTime);
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
                if (!CapsuleController.Instance.lastIndividualStaminaCapsule.containsKey(playerUUID))
                    CapsuleController.Instance.lastIndividualStaminaCapsule.put(playerUUID, new HashMap<>());

                HashMap<Integer, Long> tierTime = CapsuleController.Instance.lastIndividualStaminaCapsule.get(playerUUID);
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

    public static void setRegenCapsule(UUID playerUUID, int type){
        if(!ConfigCapsules.EnableCapsuleCooldowns)
            return;

        if(type < 0 || type >= EnumRegenCapsules.count())
            type = 0;

        EnumRegenCapsules regenCapsules = EnumRegenCapsules.values()[type];
        long freedomTime = System.currentTimeMillis() + (regenCapsules.getCooldown()*1000L);

        if(ConfigCapsules.RegenCapsuleCooldownType == 0){
            CapsuleController.Instance.lastUsedRegenCapsule.put(playerUUID, freedomTime);
        }else if(ConfigCapsules.RegenCapsuleCooldownType == 1){
            if (!CapsuleController.Instance.lastIndividualRegenCapsule.containsKey(playerUUID))
                CapsuleController.Instance.lastIndividualRegenCapsule.put(playerUUID, new HashMap<>());

            HashMap<Integer, Long> tierTime = CapsuleController.Instance.lastIndividualRegenCapsule.get(playerUUID);
            tierTime.put(type, freedomTime);
            CapsuleController.Instance.lastIndividualRegenCapsule.put(playerUUID, tierTime);
        }else if(ConfigCapsules.RegenCapsuleCooldownType == 2){
            if (!CapsuleController.Instance.lastRegenTypeCapsule.containsKey(playerUUID))
                CapsuleController.Instance.lastRegenTypeCapsule.put(playerUUID, new HashMap<>());

            HashMap<Integer, Long> typeTime = CapsuleController.Instance.lastRegenTypeCapsule.get(playerUUID);
            typeTime.put(regenCapsules.getStatusEffectId(), freedomTime);
            CapsuleController.Instance.lastRegenTypeCapsule.put(playerUUID, typeTime);
        }

    }

    public static long canUseRegenCapsule(UUID playerUUID, int type) {
        if(!ConfigCapsules.EnableCapsuleCooldowns)
            return 0;

        if(type < 0 || type >= EnumRegenCapsules.count())
            type = 0;

        EnumRegenCapsules regenCapsules = EnumRegenCapsules.values()[type];
        long currentTime = System.currentTimeMillis();
        long freedomTime = 0;

        if(ConfigCapsules.RegenCapsuleCooldownType == 0){

            if (CapsuleController.Instance.lastUsedRegenCapsule.containsKey(playerUUID))
                freedomTime = CapsuleController.Instance.lastUsedRegenCapsule.get(playerUUID);

        }else if(ConfigCapsules.RegenCapsuleCooldownType == 1){
            if (!CapsuleController.Instance.lastIndividualRegenCapsule.containsKey(playerUUID))
                CapsuleController.Instance.lastIndividualRegenCapsule.put(playerUUID, new HashMap<>());

            HashMap<Integer, Long> tierTime = CapsuleController.Instance.lastIndividualRegenCapsule.get(playerUUID);
            if (tierTime.containsKey(type))
                freedomTime = tierTime.get(type);
        }else if(ConfigCapsules.RegenCapsuleCooldownType == 2){
            if (!CapsuleController.Instance.lastRegenTypeCapsule.containsKey(playerUUID))
                CapsuleController.Instance.lastRegenTypeCapsule.put(playerUUID, new HashMap<>());

            HashMap<Integer, Long> typeTime = CapsuleController.Instance.lastRegenTypeCapsule.get(playerUUID);
            if (typeTime.containsKey(regenCapsules.getStatusEffectId()))
                freedomTime = typeTime.get(regenCapsules.getStatusEffectId());
        }

        // Calculate remaining time in milliseconds
        long remainingTimeMillis = freedomTime - currentTime;

        // Return remaining time in seconds
        return (remainingTimeMillis + 999) / 1000;
    }

    public void load(){
        // Clean
        lastUsedKiCapsule.clear();
        lastIndividualKiCapsule.clear();
        lastUsedHealthCapsule.clear();
        lastIndividualHealthCapsule.clear();
        lastUsedStaminaCapsule.clear();
        lastIndividualStaminaCapsule.clear();
        lastIndividualRegenCapsule.clear();
        miscCapsuleCooldown.clear();
        capsuleStrength.clear();
        capsuleCooldowns.clear();

        capsuleStrength.put(Capsule.HP, new HashMap<>());
        capsuleStrength.put(Capsule.KI, new HashMap<>());
        capsuleStrength.put(Capsule.STAMINA, new HashMap<>());
        capsuleStrength.put(Capsule.REGEN, new HashMap<>());

        capsuleCooldowns.put(Capsule.MISC, new HashMap<>());
        capsuleCooldowns.put(Capsule.HP, new HashMap<>());
        capsuleCooldowns.put(Capsule.KI, new HashMap<>());
        capsuleCooldowns.put(Capsule.STAMINA, new HashMap<>());
        capsuleCooldowns.put(Capsule.REGEN, new HashMap<>());

        HashMap<Integer, Integer> miscCooldown = capsuleCooldowns.get(Capsule.MISC);
        HashMap<Integer, Integer> hpCooldown = capsuleCooldowns.get(Capsule.HP);
        HashMap<Integer, Integer> kiCooldown = capsuleCooldowns.get(Capsule.KI);
        HashMap<Integer, Integer> staminaCooldown = capsuleCooldowns.get(Capsule.STAMINA);
        HashMap<Integer, Integer> regenCooldown = capsuleCooldowns.get(Capsule.REGEN);

        HashMap<Integer, Integer> hpStrength = capsuleStrength.get(Capsule.HP);
        HashMap<Integer, Integer> kiStrength = capsuleStrength.get(Capsule.KI);
        HashMap<Integer, Integer> staminaStrength = capsuleStrength.get(Capsule.STAMINA);
        HashMap<Integer, Integer> regenStrength = capsuleCooldowns.get(Capsule.REGEN);


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
        for(EnumRegenCapsules regenCapsules : EnumRegenCapsules.values()){
            regenCooldown.put(regenCapsules.getMeta(), regenCapsules.getCooldown());
            regenStrength.put(regenCapsules.getMeta(), regenCapsules.getStrength());
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
