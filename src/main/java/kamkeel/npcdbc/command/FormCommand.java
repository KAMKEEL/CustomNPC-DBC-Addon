package kamkeel.npcdbc.command;

import kamkeel.command.CommandKamkeelBase;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

import java.util.List;

public class FormCommand extends CommandKamkeelBase {

	@Override
	public String getCommandName() {
		return "form";
	}

	@Override
	public String getDescription() {
		return "Form operations";
	}

    @SubCommand(
            desc = "give a form to a player",
            usage = "<player> <num>"
    )
    public void give(ICommandSender sender, String args[]) throws CommandException{
        String playername=args[0];
        int formID;
        try {
            formID = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            sendError(sender, "Form num must be an integer: " + args[1]);
            return;
        }
        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }

        Form form = FormController.getInstance().customForms.get(formID);
        if (form == null) {
            sendError(sender, "Unknown form: " + formID);
            return;
        }


        for(PlayerData playerdata : data){
            PlayerDBCInfo playerDBCInfo = PlayerDataUtil.getDBCInfo(playerdata);
            playerDBCInfo.addForm(form);
            playerdata.save();
            sendResult(sender, String.format("%s\u00A7e added to Player '\u00A7b%s\u00A77'", form.getName(), playerdata.playername, formID));
            if(sender != playerdata.player)
                sendResult(playerdata.player, String.format("\u00A77Form &s\u00A7e added.", form.getName()));
            return;
        }
    }

    @SubCommand(
            desc = "remove a form from a player",
            usage = "<player> <num>"
    )
    public void remove(ICommandSender sender, String args[]) throws CommandException{
        String playername=args[0];
        int formID;
        try {
            formID = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            sendError(sender, "Form num must be an integer: " + args[1]);
            return;
        }
        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }

        Form form = FormController.getInstance().customForms.get(formID);
        if (form == null) {
            sendError(sender, "Unknown form: " + formID);
            return;
        }


        for(PlayerData playerdata : data){
            PlayerDBCInfo playerDBCInfo = PlayerDataUtil.getDBCInfo(playerdata);
            if(playerDBCInfo.hasForm(form)){
                playerDBCInfo.removeForm(form);
                if(playerDBCInfo.selectedForm == formID)
                    playerDBCInfo.selectedForm = -1;
                if(playerDBCInfo.currentForm == formID)
                    playerDBCInfo.currentForm = -1;
                playerdata.save();
                sendResult(sender, String.format("%s\u00A7e removed to Player '\u00A7b%s\u00A77'", form.getName(), playerdata.playername, formID));
                if(sender != playerdata.player)
                    sendResult(playerdata.player, String.format("\u00A77Form &s\u00A7e removed.", form.getName()));
            } else {
                sendResult(sender, String.format("%s\u00A7e not found on Player '\u00A7b%s\u00A77'", form.getName(), playerdata.playername, formID));
            }
            return;
        }
    }

    @SubCommand(
            desc = "clears all forms from a player",
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
            playerDBCInfo.unlockedForms.clear();
            playerDBCInfo.currentForm = -1;
            playerDBCInfo.selectedForm = -1;
            playerdata.save();
            sendResult(sender, String.format("Forms cleared from Player '\u00A7b%s\u00A77'", playerdata.playername));
            if(sender != playerdata.player)
                sendResult(playerdata.player, String.format("All custom forms removed."));
            return;
        }
    }

    @SubCommand(
            desc = "List all forms on a player",
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
            if(playerDBCInfo.unlockedForms.isEmpty()){
                sendResult(sender, String.format("No Forms found for Player '\u00A7b%s\u00A77'", playerdata.playername));
            }
            else {
                for(int formID : playerDBCInfo.unlockedForms){
                    IForm form = FormController.getInstance().get(formID);
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
