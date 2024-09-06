package kamkeel.npcdbc.controllers;

import kamkeel.npcdbc.api.effect.IBonusHandler;
import kamkeel.npcdbc.api.effect.IPlayerBonus;
import kamkeel.npcdbc.data.PlayerBonus;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.api.entity.IPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BonusController implements IBonusHandler {

    public static BonusController Instance = new BonusController();
    public HashMap<UUID, Map<String, PlayerBonus>> playerBonus = new HashMap<>();

    public static BonusController getInstance() {
        return Instance;
    }

    public void load() {
        playerBonus.clear();
    }

    public float[] getCurrentBonuses(EntityPlayer player) {
        Map<String, PlayerBonus> currentBonus = new HashMap<>();
        UUID uuid = Utility.getUUID(player);
        if (playerBonus.containsKey(uuid))
            currentBonus = playerBonus.get(Utility.getUUID(player));

        float[] bonuses = new float[5];
        for(PlayerBonus playerBonus : currentBonus.values()){
            bonuses[0] += playerBonus.strength;
            bonuses[1] += playerBonus.dexterity;
            bonuses[2] += playerBonus.willpower;
            bonuses[3] += playerBonus.constituion;
            bonuses[4] += playerBonus.spirit;
        }
        return bonuses;
    }

    public Map<String, PlayerBonus> getPlayerBonus(EntityPlayer player) {
        return playerBonus.get(Utility.getUUID(player));
    }

    public void giveBonus(EntityPlayer player, PlayerBonus bon) {
        if(bon == null)
            return;

        Map<String, PlayerBonus> currentBonus = new HashMap<>();
        UUID uuid = Utility.getUUID(player);
        if (playerBonus.containsKey(uuid))
            currentBonus = playerBonus.get(Utility.getUUID(player));
        else
            playerBonus.put(uuid, currentBonus);

        currentBonus.put(bon.name, bon);
    }

    public void removeEffect(EntityPlayer player, String bonusName) {
        if(bonusName == null)
            return;
        Map<String, PlayerBonus> current;
        UUID uuid = Utility.getUUID(player);
        if (playerBonus.containsKey(uuid))
            current = playerBonus.get(Utility.getUUID(player));
        else
            playerBonus.put(uuid, current = new HashMap<>());
        current.remove(bonusName);
    }

    public boolean hasBonus(EntityPlayer player, String id) {
        UUID uuid = Utility.getUUID(player);
        if (playerBonus.containsKey(uuid))
            return playerBonus.get(uuid).containsKey(id);
        else
            return false;
    }

    public void applyBonus(EntityPlayer player, PlayerBonus bonus) {
        if(player == null || bonus == null)
            return;
        Map<String, PlayerBonus> currentBonus;
        UUID uuid = Utility.getUUID(player);
        if (playerBonus.containsKey(uuid))
            currentBonus = playerBonus.get(Utility.getUUID(player));
        else
            playerBonus.put(uuid, currentBonus = new HashMap<>());
        currentBonus.put(bonus.name, bonus);
    }

    public void applyBonus(EntityPlayer player, String name, byte type, float str, float dex, float wil) {
        if(player == null || name.isEmpty())
            return;

        Map<String, PlayerBonus> currentbonus;
        UUID uuid = Utility.getUUID(player);
        if (playerBonus.containsKey(uuid))
            currentbonus = playerBonus.get(Utility.getUUID(player));
        else
            playerBonus.put(uuid, currentbonus = new HashMap<>());

        PlayerBonus bonus = new PlayerBonus(name,  type, str, dex, wil);
        currentbonus.put(name, bonus);
    }

    public void removeBonus(EntityPlayer player, PlayerBonus bonus) {
        if(player == null || bonus == null)
            return;
        Map<String, PlayerBonus> current;
        UUID uuid = Utility.getUUID(player);
        if (playerBonus.containsKey(uuid))
            current = playerBonus.get(Utility.getUUID(player));
        else
            playerBonus.put(uuid, current = new HashMap<>());
        current.remove(bonus.name);

    }

    public void removeBonus(EntityPlayer player, String name) {
        if(player == null || name.isEmpty())
            return;
        Map<String, PlayerBonus> current;
        UUID uuid = Utility.getUUID(player);
        if (playerBonus.containsKey(uuid))
            current = playerBonus.get(Utility.getUUID(player));
        else
            playerBonus.put(uuid, current = new HashMap<>());
        current.remove(name);
    }

    @Override
    public void clearBonuses(IPlayer player) {
        if(player == null || player.getMCEntity() == null)
            return;
        clearBonuses(player.getMCEntity());
    }

    public void clearBonuses(Entity player) {
        Map<String, PlayerBonus> effects = playerBonus.get(player.getUniqueID());
        if(effects != null){
            effects.clear();
        }
    }

    @Override
    public IPlayerBonus createBonus(String name, float str, float dex, float wil) {
        return new PlayerBonus(name, (byte) 0, str, dex, wil);
    }

    @Override
    public IPlayerBonus createBonus(String name, float str, float dex, float wil, float con, float spi) {
        return new PlayerBonus(name, (byte) 1, str, dex, wil, con, spi);
    }

    @Override
    public boolean hasBonus(IPlayer player, String name) {
        if(player == null || player.getMCEntity() == null)
            return false;
        return hasBonus((EntityPlayer) player.getMCEntity(), name);
    }

    @Override
    public boolean hasBonus(IPlayer player, IPlayerBonus bonus) {
        return hasBonus(player, bonus.getName());
    }

    @Override
    public void applyBonus(IPlayer player, String name, float str, float dex, float wil) {
        if(player == null || player.getMCEntity() == null)
            return;
        applyBonus((EntityPlayer) player.getMCEntity(), name, (byte) 0, str, dex, wil);
    }

    @Override
    public void applyBonus(IPlayer player, IPlayerBonus bonus) {
        if(bonus == null)
            return;
        if(player == null || player.getMCEntity() == null)
            return;
        applyBonus((EntityPlayer) player.getMCEntity(), (PlayerBonus) bonus);
    }

    @Override
    public void removeBonus(IPlayer player, String name) {
        if(player == null || player.getMCEntity() == null)
            return;
        removeBonus((EntityPlayer) player.getMCEntity(), name);
    }

    @Override
    public void removeBonus(IPlayer player, IPlayerBonus bonus) {
        if(player == null || player.getMCEntity() == null)
            return;
        removeBonus((EntityPlayer) player.getMCEntity(), bonus.getName());
    }
}
