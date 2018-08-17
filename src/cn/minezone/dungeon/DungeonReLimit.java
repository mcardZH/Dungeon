package cn.minezone.dungeon;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class DungeonReLimit extends BukkitRunnable {

    private YamlConfiguration config;

    public DungeonReLimit(YamlConfiguration config) {
        this.config = config;
    }

    @Override
    public void run() {
        Date date = new Date();
        YamlConfiguration played = YamlConfiguration.loadConfiguration(new File(Bukkit.getPluginManager().getPlugin("Dungeon").getDataFolder(), "played.yml"));
        config = YamlConfiguration.loadConfiguration(new File(Bukkit.getPluginManager().getPlugin("Dungeon").getDataFolder(), "config.yml"));
        for (Player p : Bukkit.getOnlinePlayers()) {
            for (String per : config.getConfigurationSection("re-limit").getKeys(true)) {
                if (p.hasPermission(per)) {
                    if (config.getIntegerList("re-limit." + per).contains(date.getHours())) {
                        System.out.print("重置" + p.getName() + "的挑战次数");
                        played.set(p.getName(), null);
                    }
                }
            }
        }
        try {
            played.save(new File(Bukkit.getPluginManager().getPlugin("Dungeon").getDataFolder(), "played.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
