package kamkeel.npcdbc.command;

import JinRyuu.JRMCore.JRMCoreH;
import kamkeel.npcdbc.api.form.IForm;
import kamkeel.npcdbc.controllers.FormController;
import kamkeel.npcdbc.controllers.TransformController;
import kamkeel.npcdbc.data.PlayerDBCInfo;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.form.Form;
import kamkeel.npcdbc.scripted.DBCAPI;
import kamkeel.npcdbc.util.PlayerDataUtil;
import kamkeel.npcs.command.CommandKamkeelBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

import java.util.Collection;
import java.util.List;

import static kamkeel.npcs.util.ColorUtil.sendError;
import static kamkeel.npcs.util.ColorUtil.sendResult;

public class FormCommand extends CommandKamkeelBase {

    @Override
    public String getCommandName() {
        return "form";
    }

    @Override
    public String getDescription() {
        return "Form operations";
    }

    @SubCommand(desc = "Gives a form to a player by name", usage = "<player> <form_name>")
    public void give(ICommandSender sender, String args[]) throws CommandException {
        FormCommand hi = this;
        String playername = args[0];
        String name = "";
        for (int i = 1; i < args.length; i++)
            name += args[i] + (i != args.length - 1 ? " " : "");


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
            if (!info.hasFormUnlocked(form.id)) {
                if (form.raceEligible(playerdata.player)) {
                    info.addForm(form);
                    info.updateClient();
                    sendResult(sender, String.format("%s §agiven to §7'§b%s§7'", form.getName(), playerdata.playername));
                    if (sender != playerdata.player)
                        sendResult(playerdata.player, String.format("§aForm §7%s §aadded.", form.getName()));
                } else {
                    int playerRace = DBCData.get(playerdata.player).Race;
                    sendResult(sender, String.format("§b%s's §crace §b(%s)§c is not eligible for §7%s", playerdata.playername, JRMCoreH.Races[playerRace], form.getName()));
                }

            } else
                sendResult(sender, String.format("§7'§b%s§7' §ealready has §7%s §eunlocked!", playerdata.playername, form.getName()));


            return;
        }
    }

    @SubCommand(desc = "Gives a form to a player by numerical ID", usage = "<player> <form_ID>")
    public void giveid(ICommandSender sender, String args[]) throws CommandException {
        FormCommand hi = this;
        String playername = args[0];
        int id = Integer.parseInt(args[1]);

        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }

        Form form = (Form) DBCAPI.Instance().getFormHandler().get(id);
        if (form == null) {
            sendError(sender, "Unknown form: " + id);
            return;
        }

        for (PlayerData playerdata : data) {
            PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(playerdata);
            if (!info.hasFormUnlocked(form.id)) {
                if (form.raceEligible(playerdata.player)) {
                    info.addForm(form);
                    info.updateClient();
                    sendResult(sender, String.format("%s §agiven to §7'§b%s§7'", form.getName(), playerdata.playername));
                    if (sender != playerdata.player)
                        sendResult(playerdata.player, String.format("§aForm §7%s §aadded.", form.getName()));
                } else {
                    int playerRace = DBCData.get(playerdata.player).Race;
                    sendResult(sender, String.format("§b%s's §crace §b(%s)§c is not eligible for §7%s", playerdata.playername, JRMCoreH.Races[playerRace], form.getName()));
                }

            } else
                sendResult(sender, String.format("§7'§b%s§7' §ealready has §7%s §eunlocked!", playerdata.playername, form.getName()));


            return;
        }
    }

    @SubCommand(desc = "Removes a form from a player by name", usage = "<player> <form_name>"
    )
    public void remove(ICommandSender sender, String args[]) throws CommandException {
        String playername = args[0];
        String name = "";
        for (int i = 1; i < args.length; i++)
            name += args[i] + (i != args.length - 1 ? " " : "");


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
            if (info.hasForm(form)) {
                if (info.selectedForm == form.id)
                    info.selectedForm = -1;
                if (info.currentForm == form.id) {
                    TransformController.handleFormDescend(playerdata.player, -10, -1);
                    info.currentForm = -1;
                }
                info.removeForm(form);
                info.updateClient();
                sendResult(sender, String.format("%s §cremoved from §7'§b%s§7'", form.getName(), playerdata.playername));
                if (sender != playerdata.player)
                    sendResult(playerdata.player, String.format("§c Form §7%s §cremoved.", form.getName()));
            } else {
                sendResult(sender, String.format("%s §enot found on §7'§b%s§7'", form.getName(), playerdata.playername, form.id));
            }
            return;
        }
    }

    @SubCommand(desc = "Removes a form from a player by numerical ID", usage = "<player> <form_ID>"
    )
    public void removeid(ICommandSender sender, String args[]) throws CommandException {
        String playername = args[0];
        int id = Integer.parseInt(args[1]);

        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }

        Form form = (Form) DBCAPI.Instance().getFormHandler().get(id);
        if (form == null) {
            sendError(sender, "Unknown form: " + id);
            return;
        }


        for (PlayerData playerdata : data) {
            PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(playerdata);
            if (info.hasForm(form)) {
                if (info.selectedForm == form.id)
                    info.selectedForm = -1;
                if (info.currentForm == form.id) {
                    TransformController.handleFormDescend(playerdata.player, -10, -1);
                    info.currentForm = -1;
                }
                info.removeForm(form);
                info.updateClient();
                sendResult(sender, String.format("%s §cremoved from §7'§b%s§7'", form.getName(), playerdata.playername));
                if (sender != playerdata.player)
                    sendResult(playerdata.player, String.format("§c Form §7%s §cremoved.", form.getName()));
            } else {
                sendResult(sender, String.format("%s §enot found on §7'§b%s§7'", form.getName(), playerdata.playername, form.id));
            }
            return;
        }
    }

    @SubCommand(desc = "Clears all forms from a player",
        usage = "<player>"
    )
    public void clear(ICommandSender sender, String args[]) throws CommandException {
        String playername = args[0];
        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }
        for (PlayerData playerdata : data) {
            PlayerDBCInfo info = PlayerDataUtil.getDBCInfo(playerdata);
            info.clearAllForms();
            info.updateClient();
            sendResult(sender, String.format("Removed all forms from '\u00A7b%s\u00A77'", playerdata.playername));
            if (sender != playerdata.player)
                sendResult(playerdata.player, String.format("All custom forms removed."));
            return;
        }
    }

    @SubCommand(
        desc = "List all forms on a player",
        usage = "<player>"
    )
    public void info(ICommandSender sender, String args[]) throws CommandException {
        String playername = args[0];

        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }
        for (PlayerData playerdata : data) {
            sendResult(sender, "--------------------");
            PlayerDBCInfo playerDBCInfo = PlayerDataUtil.getDBCInfo(playerdata);
            if (playerDBCInfo.unlockedForms.isEmpty()) {
                sendResult(sender, String.format("No Forms found for Player '\u00A7b%s\u00A77'", playerdata.playername));
            } else {
                for (int formID : playerDBCInfo.unlockedForms) {
                    IForm form = FormController.getInstance().get(formID);
                    if (form != null) {
                        sendResult(sender, String.format("%s", form.getName()));
                    }
                }
            }
            sendResult(sender, "--------------------");
            return;
        }
    }

    @SubCommand(desc = "Lists all existing forms")
    public void infoall(ICommandSender sender, String args[]) throws CommandException {
        sendResult(sender, "--------------------");
        for (Form form : FormController.getInstance().customForms.values())
            sendResult(sender, String.format("\u00a7b%s \u00a77(ID: %d)", form.getName(), form.id));
        sendResult(sender, "--------------------");

    }

    @SubCommand(desc = "Reloads all forms")
    public void reload(ICommandSender sender, String[] args) {
        FormController.Instance.load();
        sendResult(sender, "Forms reloaded!");
    }

    @SubCommand(
        desc = "Find form id number by its name",
        usage = "<formName>"
    )
    public void id(ICommandSender sender, String args[]) throws CommandException {
        if (args.length == 0) {
            sendError(sender, "Please provide a name for the form");
            return;
        }
        String formName = String.join(" ", args).toLowerCase();
        Collection<Form> forms = FormController.getInstance().customForms.values();
        int count = 0;
        for (Form form : forms) {
            if (form.getName().toLowerCase().contains(formName)) {
                sendResult(sender, String.format("Form \u00A7e%d\u00A77 - \u00A7c'%s'", form.id, form.getName()));
                count++;
            }
        }
        if (count == 0) {
            sendResult(sender, String.format("No Form found with name: \u00A7c'%s'", formName));
        }
    }

}
