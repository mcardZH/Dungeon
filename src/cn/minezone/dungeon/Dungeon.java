package cn.minezone.dungeon;

import cn.minezone.dungeon.api.DungeonAPI;
import cn.minezone.dungeon.commands.DungeonAdmin;
import cn.minezone.dungeon.commands.DungeonManage;
import cn.minezone.dungeon.listener.MobKilledListener;
import cn.minezone.dungeon.listener.PlayerDeathListener;
import cn.minezone.dungeon.listener.PlayerQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Dungeon extends JavaPlugin {
    private String[] ad = {
            ChatColor.RED + "┏━━━━━━━━━━━━━━━━━━━━━━┓",
            ChatColor.RED + "┃     地牢冒险 1.0     ┃",
            ChatColor.RED + "┣━━━━━━━━━━━━━━━━━━━━━━┫",
            ChatColor.RED + "┃ 作者：mcard          ┃",
            ChatColor.RED + "┃ 接插件定制           ┃",
            ChatColor.RED + "┃ QQ:1459974942        ┃",
            ChatColor.RED + "┗━━━━━━━━━━━━━━━━━━━━━━┛",
    };

    private static File dataFolder;
    private YamlConfiguration config;
    private static DungeonAPI dungeonAPI;

    @Override
    public void onEnable() {

        Bukkit.getPluginCommand("dungeon").setExecutor(new DungeonManage());
        Bukkit.getPluginCommand("dungeonadmin").setExecutor(new DungeonAdmin());

        dataFolder = getDataFolder();

        saveResource("config.yml", false);
        saveResource("played.yml", true);

        config = YamlConfiguration.loadConfiguration(new File(dataFolder, "config.yml"));

        Bukkit.getPluginManager().registerEvents(new MobKilledListener(getDungeonAPI()), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(getDungeonAPI()), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(getDungeonAPI()), this);

        File f = new File(getDataFolder(), "playing.yml");
        try {
            f.delete();
        } catch (Exception e) {

        }
        new DungeonReLimit(config).runTaskTimer(this, 0, 58 * 60 * 20);

        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI"); //用服务端获取PAPI插件
        if (plugin != null) { //如果是null，意味着插件没有安装，因为服务器获取不到PAPI
            new PAPIManage(this, getDungeonAPI()).hook();
        }

        Bukkit.getConsoleSender().sendMessage(ad);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ad);
    }

    /**
     * 取回插件API
     *
     * @return 插件API
     */
    public static DungeonAPI getDungeonAPI() {
        if (dungeonAPI == null) {
            dungeonAPI = new DungeonAPI(YamlConfiguration.loadConfiguration(new File(dataFolder, "config.yml")), dataFolder);
        }
        return dungeonAPI;
    }
}
