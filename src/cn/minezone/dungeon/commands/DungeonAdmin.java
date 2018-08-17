package cn.minezone.dungeon.commands;

import cn.minezone.dungeon.Dungeon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DungeonAdmin implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.isOp()) {
            return true;
        }
        Dungeon.getDungeonAPI().setPlayerPlayed(args[1], args[2], Dungeon.getDungeonAPI().getPlayerPlayed(args[1], args[2]) + Integer.parseInt(args[3]));

        return true;
    }
}
