package kamkeel.npcdbc.command;

import kamkeel.command.CommandKamkeelBase;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.scripted.DBCAPI;
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

    @SubCommand(desc = "Give a player form mastery", usage = "<player> <amount> <formname>")
    public void give(ICommandSender sender, String[] args) throws CommandException {
        String playername = args[0];
        float amount;

        String name = "";
        for (int i = 2; i < args.length; i++)
            name += args[i] + (i != args.length - 1 ? " " : "");


        try {
            amount = Float.parseFloat(args[1]);
        } catch (NumberFormatException ex) {
            sendError(sender, "Mastery amount must be a float: " + args[1]);
            return;
        }

        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }

        Form form = (Form) DBCAPI.Instance().getForm(name);
        if (form == null) {
            sendError(sender, "Unknown form: " + name);
            return;
        }

        for (PlayerData playerdata : data) {
            PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(playerdata);

            if (!info.hasForm(form)) {
                sendResult(sender, String.format("\u00A7ePlayer '\u00A7b%s\u00A7e' doesn't have '\u00A7b%s\u00A77' unlocked.", playerdata.playername, form.getName()));
                return;
            }

            info.addFormLevel(form.id, amount);
            info.updateClient();
            sendResult(sender, String.format("\u00A7b%s's\u00A7e mastery of \u00A77'%s'\u00A7e was adjusted by \u00A77%s §d(%s)", playerdata.playername, form.getName(), amount, info.getFormLevel(form.id)));

            return;
        }
    }

    @SubCommand(desc = "Set a player form mastery", usage = "<player> <amount> <formname>")
    public void set(ICommandSender sender, String[] args) throws CommandException {
        String playername = args[0];
        float amount;

        String name = "";
        for (int i = 2; i < args.length; i++)
            name += args[i] + (i != args.length - 1 ? " " : "");


        try {
            amount = Float.parseFloat(args[1]);
        } catch (NumberFormatException ex) {
            sendError(sender, "Mastery amount must be a float: " + args[1]);
            return;
        }

        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }

        Form form = (Form) DBCAPI.Instance().getForm(name);
        if (form == null) {
            sendError(sender, "Unknown form: " + name);
            return;
        }

        for (PlayerData playerdata : data) {
            PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(playerdata);

            if (!info.hasForm(form)) {
                sendResult(sender, String.format("\u00A7ePlayer '\u00A7b%s\u00A7e' doesn't have '\u00A7b%s\u00A77' unlocked.", playerdata.playername, form.getName()));
                return;
            }

            info.setFormLevel(form.id, amount);
            info.updateClient();
            sendResult(sender, String.format("\u00A7b%s's\u00A7e mastery of \u00A77'%s'\u00A7e was set to §d%s", playerdata.playername, form.getName(), info.getFormLevel(form.id)));

            return;
        }
    }

    @SubCommand(desc = "Get info about a players form mastery", usage = "<player>")
    public void info(ICommandSender sender, String[] args) throws CommandException {
        String playername = args[0];

        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }

        for (PlayerData playerdata : data) {
            PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(playerdata);

            sendResult(sender, "--------------------");
            sendResult(sender, String.format("§b%s's §emastery:", playerdata.playername));
            for (int id : info.unlockedForms) {
                Form form = info.getForm(id);
                if (form == null)
                    continue;

                float formLevel = info.getFormLevel(form.id);
                float formMaxLevel = form.getMastery().getMaxLevel();
                String formatted = new DecimalFormat("#.##").format(formLevel / formMaxLevel * 100);

                sendResult(sender, String.format("§7%s  §b%s/%s", form.getName(), formLevel, formMaxLevel) + "%s", " §d(" + formatted + "%)");
            }
            sendResult(sender, "--------------------");
            return;
        }
    }
}
