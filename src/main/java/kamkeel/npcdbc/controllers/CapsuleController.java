package kamkeel.npcdbc.controllers;

import java.util.HashMap;
import java.util.UUID;

public class CapsuleController {

    public static HashMap<UUID, Long> lastUsedKiCapsule = new HashMap<>();
    public static HashMap<UUID, Long> lastUsedHealthCapsule = new HashMap<>();
    public static HashMap<UUID, Long> lastUsedStaminaCapsule = new HashMap<>();

}
