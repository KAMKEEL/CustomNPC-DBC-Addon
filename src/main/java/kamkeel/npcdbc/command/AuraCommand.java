package kamkeel.npcdbc.command;

import kamkeel.command.CommandKamkeelBase;
import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.aura.Aura;
import kamkeel.npcdbc.scripted.DBCAPI;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

import java.util.List;

public class AuraCommand extends CommandKamkeelBase {

    @Override
    public String getCommandName() {
        return "aura";
    }

    @Override
    public String getDescription() {
        return "Custom Aura operations";
    }

    @SubCommand(desc = "give an aura to a player by name", usage = "<player> <aura_name>")
    public void give(ICommandSender sender, String args[]) throws CommandException {
        String playername=args[0];
        String name = "";
        for (int i = 1; i < args.length; i++)
            name += args[i] + (i != args.length - 1 ? " " : "");
        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }
        Aura aura = (Aura) DBCAPI.Instance().getAura(name);

        for(PlayerData playerdata : data){
            PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(playerdata);
            if (!info.hasAuraUnlocked(aura.id)) {
                info.addAura(aura);
                info.updateClient();
                sendResult(sender, String.format("%s §agiven to §7'§b%s§7'", aura.getName(), playerdata.playername));
                if (sender != playerdata.player)
                    sendResult(playerdata.player, String.format("§Aura §7%s §aadded.", aura.getName()));

            } else
                sendResult(sender, String.format("§7'§b%s§7' §ealready has §7%s §eunlocked!", playerdata.playername, aura.getName()));
            return;
        }
    }

    @SubCommand(desc = "give an aura to a player by numerical ID", usage = "<player> <aura_ID>")
    public void giveid(ICommandSender sender, String args[]) throws CommandException {
        String playername=args[0];
        int id = Integer.parseInt(args[1]);

        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }

        Aura aura = (Aura) DBCAPI.Instance().getAuraHandler().get(id);
        if (aura == null) {
            sendError(sender, "Unknown aura: " + id);
            return;
        }

        for(PlayerData playerdata : data){
            PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(playerdata);
            if (!info.hasAuraUnlocked(aura.id)) {
                info.addAura(aura);
                info.updateClient();
                sendResult(sender, String.format("%s §agiven to §7'§b%s§7'", aura.getName(), playerdata.playername));
                if (sender != playerdata.player)
                    sendResult(playerdata.player, String.format("§Aura §7%s §aadded.", aura.getName()));

            } else
                sendResult(sender, String.format("§7'§b%s§7' §ealready has §7%s §eunlocked!", playerdata.playername, aura.getName()));
            return;
        }
    }

    @SubCommand(desc = "remove an aura from a player by name", usage = "<player> <aura_name>")
    public void remove(ICommandSender sender, String args[]) throws CommandException{
        String playername=args[0];
        String name = "";
        for (int i = 1; i < args.length; i++)
            name += args[i] + (i != args.length - 1 ? " " : "");

        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }

        Aura aura = (Aura) DBCAPI.Instance().getAura(name);
        if (aura == null) {
            sendError(sender, "Unknown aura: " + name);
            return;
        }

        for(PlayerData playerdata : data){
            PlayerDBCInfo playerDBCInfo = PlayerDataUtil.getDBCInfo(playerdata);
            if(playerDBCInfo.hasAura(aura)){
                playerDBCInfo.removeAura(aura);
                if (playerDBCInfo.selectedAura == aura.id)
                    playerDBCInfo.selectedAura = -1;
                if (playerDBCInfo.currentAura == aura.id)
                    playerDBCInfo.currentAura = -1;
                playerDBCInfo.updateClient();
                sendResult(sender, String.format("%s §cremoved from §7'§b%s§7'", aura.getName(), playerdata.playername));
                if(sender != playerdata.player)
                    sendResult(playerdata.player, String.format("§cForm §7%s §cremoved.", aura.getName()));

            } else {
                sendResult(sender, String.format("%s §enot found on §7'§b%s§7'", aura.getName(), playerdata.playername));
            }
            return;
        }
    }

    @SubCommand(desc = "remove an aura from a player by numerical ID", usage = "<player> <aura_ID>")
    public void removeid(ICommandSender sender, String args[]) throws CommandException{
        String playername=args[0];
        int id = Integer.parseInt(args[1]);

        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }

        Aura aura = (Aura) DBCAPI.Instance().getAuraHandler().get(id);
        if (aura == null) {
            sendError(sender, "Unknown aura: " + id);
            return;
        }

        for(PlayerData playerdata : data){
            PlayerDBCInfo playerDBCInfo = PlayerDataUtil.getDBCInfo(playerdata);
            if(playerDBCInfo.hasAura(aura)){
                playerDBCInfo.removeAura(aura);
                if (playerDBCInfo.selectedAura == aura.id)
                    playerDBCInfo.selectedAura = -1;
                if (playerDBCInfo.currentAura == aura.id)
                    playerDBCInfo.currentAura = -1;
                playerDBCInfo.updateClient();
                sendResult(sender, String.format("%s §cremoved from §7'§b%s§7'", aura.getName(), playerdata.playername));
                if(sender != playerdata.player)
                    sendResult(playerdata.player, String.format("§cForm §7%s §cremoved.", aura.getName()));

            } else {
                sendResult(sender, String.format("%s §enot found on §7'§b%s§7'", aura.getName(), playerdata.playername));
            }
            return;
        }
    }

    @SubCommand(
        desc = "clears all auras from a player",
        usage = "<player>"
    )
    public void clear(ICommandSender sender, String args[]) throws CommandException{
        String playername=args[0];
        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }
        for(PlayerData playerdata : data){
            PlayerDBCInfo playerDBCInfo = PlayerDataUtil.getDBCInfo(playerdata);
            playerDBCInfo.unlockedAuras.clear();
            playerDBCInfo.currentAura = -1;
            playerDBCInfo.selectedAura = -1;
            playerDBCInfo.updateClient();
            sendResult(sender, String.format("Removed all auras from §7'§b%s§7'", playerdata.playername));
            if(sender != playerdata.player)
                sendResult(playerdata.player, String.format("All custom auras removed."));
            return;
        }
    }

    @SubCommand(
        desc = "List all auras on a player",
        usage = "<player>"
    )
    public void info(ICommandSender sender, String args[]) throws CommandException {
        String playername=args[0];

        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }
        for(PlayerData playerdata : data){
            sendResult(sender, "--------------------");
            PlayerDBCInfo playerDBCInfo = PlayerDataUtil.getDBCInfo(playerdata);
            if(playerDBCInfo.unlockedAuras.isEmpty()){
                sendResult(sender, String.format("No Auras found for §7'§b%s§7'", playerdata.playername));
            }
            else {
                for(int formID : playerDBCInfo.unlockedAuras){
                    IAura aura = AuraController.getInstance().get(formID);
                    if (aura != null) {
                        sendResult(sender, String.format("%s", aura.getName()));
                    }
                }
            }
            sendResult(sender, "--------------------");
            return;
        }
    }

    @SubCommand(desc = "Lists all existing auras")
    public void infoall(ICommandSender sender, String args[]) throws CommandException {
        sendResult(sender, "--------------------");
        for (Aura aura : AuraController.getInstance().customAuras.values())
            sendResult(sender, String.format("\u00a7b%s \u00a77(ID: %d)", aura.getName(), aura.id));
        sendResult(sender, "--------------------");

    }

    @SubCommand(desc = "Reloads all auras")
    public void reload(ICommandSender sender, String[] args) {
        AuraController.Instance.load();
        sendResult(sender, "Auras reloaded!");
    }
}
