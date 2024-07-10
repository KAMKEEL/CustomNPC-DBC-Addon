package kamkeel.npcdbc.command;

import kamkeel.command.CommandKamkeelBase;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.util.PlayerDataUtil;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

import java.text.DecimalFormat;
import java.util.List;

public class FormMasteryCommand extends CommandKamkeelBase {
    @Override
    public String getDescription() {
        return "Allows changing a player's form mastery for custom forms";
    }

    @Override
    public String getCommandName() {
        return "formmastery";
    }

    @SubCommand(
        desc = "Give a player form mastery",
        usage = "<player> <formID> <amount>"
    )
    public void give(ICommandSender sender, String[] args) throws CommandException {
        String playername=args[0];
        int formID;
        float amount;

        try {
            formID = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            sendError(sender, "Form num must be an integer: " + args[1]);
            return;
        }

        try{
            amount = Float.parseFloat(args[2]);
        } catch (NumberFormatException ex) {
            sendError(sender, "Mastery amount must be an float: " + args[2]);
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

            if(!playerDBCInfo.hasForm(form)){
                sendResult(sender, String.format("\u00A7ePlayer '\u00A7b%s\u00A7e' doesn't have '\u00A7b%s\u00A77' unlocked.", playerdata.playername, form.getName()));
                return;
            }

            playerDBCInfo.addFormLevel(formID, amount);
            playerdata.save();
            sendResult(sender, String.format("\u00A7b%s's\u00A7e mastery of \u00A77'%s'\u00A7e was adjusted by \u00A77%s", playerdata.playername, form.getName(), amount));

            return;
        }
    }

    @SubCommand(
        desc = "Set a player form mastery",
        usage = "<player> <formID> <amount>"
    )
    public void set(ICommandSender sender, String[] args) throws CommandException {
        String playername=args[0];
        int formID;
        float amount;

        try {
            formID = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex) {
            sendError(sender, "Form num must be an integer: " + args[1]);
            return;
        }

        try{
            amount = Float.parseFloat(args[2]);
        } catch (NumberFormatException ex) {
            sendError(sender, "Mastery amount must be an float: " + args[2]);
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

            if(!playerDBCInfo.hasForm(form)){
                sendResult(sender, String.format("\u00A7ePlayer '\u00A7b%s\u00A7e' doesn't have '\u00A7b%s\u00A77' unlocked.", playerdata.playername, form.getName()));
                return;
            }

            playerDBCInfo.setFormLevel(formID, amount);
            playerdata.save();
            sendResult(sender, String.format("\u00A7b%s's\u00A7e mastery of \u00A77'%s'\u00A7e was set to \u00A77%s", playerdata.playername, form.getName(), amount));

            return;
        }
    }

    @SubCommand(
        desc = "Get info about a players form mastery",
        usage = "<player> <formID>"
    )
    public void info(ICommandSender sender, String[] args) throws CommandException {
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

        for(PlayerData playerdata : data) {
            PlayerDBCInfo playerDBCInfo = PlayerDataUtil.getDBCInfo(playerdata);
            if(!playerDBCInfo.hasForm(form)){
                sendResult(sender, String.format("\u00A7ePlayer '\u00A7b%s\u00A7e' doesn't have '\u00A7b%s\u00A77' unlocked.", playerdata.playername, form.getName()));
                return;
            }

            float formLevel = playerDBCInfo.getFormLevel(formID);
            float formMaxLevel = form.getMastery().getMaxLevel();

            String formatted = new DecimalFormat("#.##").format(formLevel/formMaxLevel*100);



            sendResult(sender, String.format("\u00A7b%s's\u00A7e mastery of \u00A77'%s'\u00A7e is \u00A77%s/%s ", playername, form.getName(), formLevel, formMaxLevel)+"%s", "\u00A7b("+formatted+"%)");
        }
    }
}
