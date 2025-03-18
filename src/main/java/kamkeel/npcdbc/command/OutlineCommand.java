package kamkeel.npcdbc.command;

import kamkeel.npcdbc.controllers.OutlineController;
import kamkeel.npcdbc.data.dbcdata.DBCData;
import kamkeel.npcdbc.data.outline.Outline;
import kamkeel.npcdbc.scripted.DBCAPI;
import kamkeel.npcs.command.CommandKamkeelBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerData;

import java.util.Collection;
import java.util.List;

import static kamkeel.npcs.util.ColorUtil.sendError;
import static kamkeel.npcs.util.ColorUtil.sendResult;

public class OutlineCommand extends CommandKamkeelBase {

    @Override
    public String getCommandName() {
        return "outline";
    }

    @Override
    public String getDescription() {
        return "Outline operations";
    }

    @SubCommand(desc = "Sets a player's active outline by name", usage = "<player> <outline_name>")
    public void set(ICommandSender sender, String args[]) throws CommandException {
        String playername = args[0];
        String name = "";
        for (int i = 1; i < args.length; i++)
            name += args[i] + (i != args.length - 1 ? " " : "");


        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }

        Outline outline = (Outline) DBCAPI.Instance().getOutline(name);
        if (outline == null) {
            sendError(sender, "Unknown outline: " + name);
            return;
        }


        for (PlayerData playerdata : data) {
            EntityPlayer player = playerdata.player;
            DBCData dbc = DBCData.get(player);
            if (dbc.outlineID != outline.id) {
                dbc.setOutline(outline);
                sendResult(sender, String.format("%s §ais now active to §7'§b%s§7'", outline.getName(), playerdata.playername));
                if (sender != playerdata.player)
                    sendResult(playerdata.player, String.format("§Outline §7%s §ais now active.", outline.getName()));

            } else
                sendResult(sender, String.format("§7'§b%s§7' §ealready has §7%s §eactive!", playerdata.playername, outline.getName()));
            return;
        }
    }

    @SubCommand(desc = "Sets a player's active outline by numerical id", usage = "<player> <outline_ID>")
    public void setid(ICommandSender sender, String args[]) throws CommandException {
        String playername = args[0];
        int id = Integer.parseInt(args[1]);


        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }

        Outline outline = (Outline) OutlineController.getInstance().get(id);
        if (outline == null) {
            sendError(sender, "Unknown outline ID: " + id);
            return;
        }


        for (PlayerData playerdata : data) {
            EntityPlayer player = playerdata.player;
            DBCData dbc = DBCData.get(player);
            if (dbc.outlineID != outline.id) {
                dbc.setOutline(outline);
                sendResult(sender, String.format("%s §ais now active to §7'§b%s§7'", outline.getName(), playerdata.playername));
                if (sender != playerdata.player)
                    sendResult(playerdata.player, String.format("§Outline §7%s §ais now active.", outline.getName()));

            } else
                sendResult(sender, String.format("§7'§b%s§7' §ealready has §7%s §eactive!", playerdata.playername, outline.getName()));
            return;
        }
    }

    @SubCommand(desc = "Removes a player's active outline", usage = "<player>")
    public void remove(ICommandSender sender, String args[]) throws CommandException {
        String playername = args[0];


        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }

        for (PlayerData playerdata : data) {
            EntityPlayer player = playerdata.player;
            DBCData dbc = DBCData.get(player);
            if (dbc.outlineID != -1) {
                Outline outline = dbc.getOutline();
                dbc.setOutline(null);
                sendResult(sender, String.format("%s §cremoved from §7'§b%s§7'", outline.getName(), playerdata.playername));
                if (sender != playerdata.player)
                    sendResult(playerdata.player, String.format("§cOutline §7%s §cremoved.", outline.getName()));

            } else {
                sendResult(sender, String.format("§eNo outline active for §7'§b%s§7'", playerdata.playername));
            }
            return;
        }
    }


    @SubCommand(desc = "Lists player's active outline", usage = "<player>")
    public void info(ICommandSender sender, String args[]) throws CommandException {
        String playername = args[0];

        List<PlayerData> data = PlayerDataController.Instance.getPlayersData(sender, playername);
        if (data.isEmpty()) {
            sendError(sender, "Unknown player: " + playername);
            return;
        }
        for (PlayerData playerdata : data) {
            EntityPlayer player = playerdata.player;
            DBCData dbc = DBCData.get(player);
            if (dbc.outlineID == -1)
                sendResult(sender, String.format("No Outline active for §7'§b%s§7'", playerdata.playername));
            else {
                Outline outline = (Outline) OutlineController.Instance.get(dbc.outlineID);
                sendResult(sender, "--------------------");
                sendResult(sender, String.format("%s", outline.getName()));
                sendResult(sender, "--------------------");
            }

        }

    }

    @SubCommand(desc = "Lists all existing outlines")
    public void infoall(ICommandSender sender, String args[]) throws CommandException {
        sendResult(sender, "--------------------");
        for (Outline outline : OutlineController.getInstance().customOutlines.values())
            sendResult(sender, String.format("%s", outline.getName()));
        sendResult(sender, "--------------------");

    }

    @SubCommand(desc = "Reloads all outlines")
    public void reload(ICommandSender sender, String[] args) {
        OutlineController.Instance.load();
        sendResult(sender, "Outlines reloaded!");
    }

    @SubCommand(
        desc = "Find outline id number by its name",
        usage = "<outlineName>"
    )
    public void id(ICommandSender sender, String args[]) throws CommandException {
        if (args.length == 0) {
            sendError(sender, "Please provide a name for the outline");
            return;
        }
        String outlineName = String.join(" ", args).toLowerCase();
        Collection<Outline> outlines = OutlineController.getInstance().customOutlines.values();
        int count = 0;
        for (Outline outline : outlines) {
            if (outline.getName().toLowerCase().contains(outlineName)) {
                sendResult(sender, String.format("Outline \u00A7e%d\u00A77 - \u00A7c'%s'", outline.id, outline.getName()));
                count++;
            }
        }
        if (count == 0) {
            sendResult(sender, String.format("No Outline found with name: \u00A7c'%s'", outlineName));
        }
    }
}
