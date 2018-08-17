package cn.minezone.dungeon.commands;

import cn.minezone.dungeon.Dungeon;
import cn.minezone.dungeon.api.DungeonAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class DungeonManage implements CommandExecutor {

    private YamlConfiguration config;

    public DungeonManage() {
        config = YamlConfiguration.loadConfiguration(new File(Bukkit.getPluginManager().getPlugin("Dungeon").getDataFolder(), "config.yml"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] a) {

        if (a.length == 0 || a[0].equalsIgnoreCase("help")) {
            for (String help : config.getStringList("language.help")) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', help));
            }
            return true;
        }

        DungeonAPI api = Dungeon.getDungeonAPI();
        if (a.length == 2 && a[0].equalsIgnoreCase("join")) {
            if (!api.isDungeonExist(a[1])) {
                showTip(sender, "unknown-dungeon");
                return true;
            }
            if (api.isDungeonPlaying(a[1])) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        config.getString("language.playing").replace("{time}", api.getDungeonLastTime(a[1]) + "")));
                return true;
            }
            if (!(sender instanceof Player)) {
                showTip(sender, "no-player-sender");
                return true;
            }
            if (api.getPlayerMaxPlayed((Player) sender, a[1]) <= api.getPlayerPlayed(sender.getName(), a[1])) {
                showTip(sender, "play-too-much");
                return true;
            }
            if (api.isPlayerPlaying(sender.getName())) {
                showTip(sender, "playing");
            }
            api.startDungeon((Player) sender, a[1]);
            return true;
        }
        if (a.length == 1 && a[0].equalsIgnoreCase("leave")) {
            if (!(sender instanceof Player)) {
                showTip(sender, "no-player-sender");
                return true;
            }

            if (api.isPlayerPlaying(sender.getName())) {
                api.endDungeon(api.getPlayerPlaying(sender.getName()), false);
                return true;
            } else {
                showTip(sender, "no-playing");
                return true;
            }
        }
        if (a.length == 1 && a[0].equalsIgnoreCase("reload") && sender.isOp()) {
            api.reloadConfig();
            config = YamlConfiguration.loadConfiguration(new File(Bukkit.getPluginManager().getPlugin("Dungeon").getDataFolder(), "config.yml"));
            sender.sendMessage(ChatColor.GREEN + "reload success");
            return true;
        }

        return false;
    }

    private void showTip(CommandSender s, String key) {
        s.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("language." + key)));
    }
}
