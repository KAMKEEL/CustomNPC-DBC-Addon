package kamkeel.npcdbc.command;

import kamkeel.command.CommandKamkeelBase;
import kamkeel.npcdbc.controllers.StatusEffectController;
import kamkeel.npcdbc.data.statuseffect.StatusEffect;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

import java.util.*;

public class EffectCommand extends CommandKamkeelBase {

    @Override
    public String getDescription() {
        return "Custom Effect operations";
    }

    @Override
    public String getCommandName() {
        return "effect";
    }

    // info all
    @SubCommand(desc = "Lists all effects")
    public void infoAll(ICommandSender sender, String[] args) throws CommandException {
        Set<Map.Entry<Integer, StatusEffect>> effects = StatusEffectController.getInstance().standardEffects.entrySet();
        if (effects.isEmpty()) {
            sendError(sender, "No effects found.");
            return;
        }

        sendResult(sender, "--------------------");

        for (Map.Entry<Integer, StatusEffect> entry : effects) {
            sendResult(sender, String.format("\u00a7b%2$s \u00a77(ID: %1$s)", entry.getKey(), entry.getValue().getName()));
        }

        sendResult(sender, "--------------------");
    }

    @SubCommand(desc = "Gives a effect to a player", usage = "<player> <time> <effectName>")
    public void give(ICommandSender sender, String[] args) throws CommandException {
        String playername = args[0];
        int time = Integer.parseInt(args[1]);
        String name = "";
        for (int i = 2; i < args.length; i++) {
            name += args[i] + (i != args.length - 1 ? " " : "");
        }

        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }

        StatusEffect statusEffect = StatusEffectController.getInstance().getFromName(name);
        if (statusEffect == null) {
            sendError(sender, "Unknown effect: " + name);
            return;
        }

        for (PlayerData playerdata : data) {
            StatusEffectController.getInstance().applyEffect(playerdata.player, statusEffect.getId(), time);
            sendResult(sender, String.format("%s §agiven to §7'§b%s§7'", statusEffect.getName(), playerdata.playername));
            if (sender != playerdata.player) {
                sendResult(playerdata.player, String.format("§Effect §7%s §aadded.", statusEffect.getName()));
            }
        }
    }


    @SubCommand(desc = "Removes a effect from a player", usage = "<player> <effectName>")
    public void remove(ICommandSender sender, String[] args) throws CommandException {
        String playername = args[0];
        String name = "";
        for (int i = 1; i < args.length; i++) {
            name += args[i] + (i != args.length - 1 ? " " : "");
        }

        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }

        StatusEffect statusEffect = StatusEffectController.getInstance().getFromName(name);
        if (statusEffect == null) {
            sendError(sender, "Unknown effect: " + name);
            return;
        }

        for (PlayerData playerData : data) {
            StatusEffectController.getInstance().removeEffect(playerData.player, statusEffect.getId());
            sendResult(sender, String.format("Effect %s removed from %s", statusEffect.getName(), playerData.playername));
        }
    }

    @SubCommand(desc = "Gives a effect to a player", usage = "<player> <time> <effectId>")
    public void giveId(ICommandSender sender, String[] args) throws CommandException {
        String playername = args[0];
        int time = Integer.parseInt(args[1]);

        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }

        for (PlayerData playerdata : data) {
            int statusEffectId = Integer.parseInt(args[2]);
            StatusEffectController.getInstance().applyEffect(playerdata.player, statusEffectId, time);
            sendResult(sender, String.format("Effect %d given to %s", statusEffectId, playerdata.playername));
        }
    }

    @SubCommand(desc = "Removes a effect from a player", usage = "<player> <effectId>")
    public void removeId(ICommandSender sender, String[] args) throws CommandException {
        String playername = args[0];
        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }

        for (PlayerData playerData : data) {
            int statusEffectId = Integer.parseInt(args[1]);
            StatusEffectController.getInstance().removeEffect(playerData.player, statusEffectId);
            sendResult(sender, String.format("Effect %d removed from %s", statusEffectId, playerData.playername));
        }
    }
}
