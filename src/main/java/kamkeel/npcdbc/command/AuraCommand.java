package kamkeel.npcdbc.command;

import kamkeel.command.CommandKamkeelBase;
import kamkeel.npcdbc.api.aura.IAura;
import kamkeel.npcdbc.controllers.AuraController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.aura.Aura;
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

    @SubCommand(
        desc = "give an aura to a player",
        usage = "<player> <num>"
    )
    public void give(ICommandSender sender, String args[]) throws CommandException {
        String playername=args[0];
        int auraID;
        try {
            auraID = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            sendError(sender, "Overlay num must be an integer: " + args[1]);
            return;
        }
        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }

        Aura aura = AuraController.getInstance().customAuras.get(auraID);
        if (aura == null) {
            sendError(sender, "Unknown aura: " + auraID);
            return;
        }



        for(PlayerData playerdata : data){
            PlayerDBCInfo playerDBCInfo = PlayerDataUtil.getDBCInfo(playerdata);
            playerDBCInfo.addAura(aura);
            playerdata.save();
            sendResult(sender, String.format("%s\u00A7e added to Player '\u00A7b%s\u00A77'", aura.getName(), playerdata.playername, auraID));
            if(sender != playerdata.player)
                sendResult(playerdata.player, String.format("\u00A77Aura &s\u00A7e added.", aura.getName()));
            return;
        }
    }

    @SubCommand(
        desc = "remove an aura from a player",
        usage = "<player> <num>"
    )
    public void remove(ICommandSender sender, String args[]) throws CommandException{
        String playername=args[0];
        int auraID;
        try {
            auraID = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            sendError(sender, "Aura num must be an integer: " + args[1]);
            return;
        }
        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }

        Aura aura = AuraController.getInstance().customAuras.get(auraID);
        if (aura == null) {
            sendError(sender, "Unknown aura: " + auraID);
            return;
        }


        for(PlayerData playerdata : data){
            PlayerDBCInfo playerDBCInfo = PlayerDataUtil.getDBCInfo(playerdata);
            if(playerDBCInfo.hasAura(aura)){
                playerDBCInfo.removeAura(aura);
                if(playerDBCInfo.selectedAura == auraID)
                    playerDBCInfo.selectedAura = -1;
                if(playerDBCInfo.currentAura == auraID)
                    playerDBCInfo.currentAura = -1;
                playerdata.save();
                sendResult(sender, String.format("%s\u00A7e removed to Player '\u00A7b%s\u00A77'", aura.getName(), playerdata.playername, auraID));
                if(sender != playerdata.player)
                    sendResult(playerdata.player, String.format("\u00A77Aura &s\u00A7e removed.", aura.getName()));
            } else {
                sendResult(sender, String.format("%s\u00A7e not found on Player '\u00A7b%s\u00A77'", aura.getName(), playerdata.playername, auraID));
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
            playerdata.save();
            sendResult(sender, String.format("Custom auras cleared from Player '\u00A7b%s\u00A77'", playerdata.playername));
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
                sendResult(sender, String.format("No Auras found for Player '\u00A7b%s\u00A77'", playerdata.playername));
            }
            else {
                for(int formID : playerDBCInfo.unlockedAuras){
                    IAura form = AuraController.getInstance().get(formID);
                    if(form != null){
                        sendResult(sender, String.format("%s", form.getMenuName()));
                    }
                }
            }
            sendResult(sender, "--------------------");
            return;
        }
    }
}
