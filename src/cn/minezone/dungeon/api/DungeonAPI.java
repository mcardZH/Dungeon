package cn.minezone.dungeon.api;

import cn.minezone.dungeon.DungeonRunnable;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DungeonAPI {

    private YamlConfiguration config;
    private File dataFolder;
    private HashMap<String, Integer> taskID = new HashMap<>();

    public DungeonAPI(YamlConfiguration config, File dataFolder) {
        this.config = config;
        this.dataFolder = dataFolder;
    }

    /**
     * 开启地牢冒险
     *
     * @param p    玩家
     * @param name 地牢名称
     */
    public void startDungeon(Player p, String name) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Dungeon");
        plugin.saveResource("played.yml", false);
        plugin.saveResource("playing.yml", false);

        YamlConfiguration played = YamlConfiguration.loadConfiguration(new File(dataFolder, "played.yml"));
        YamlConfiguration playing = YamlConfiguration.loadConfiguration(new File(dataFolder, "playing.yml"));

        String[] loc = config.getString("dungeons." + name + ".location").split(" ", 3);

        Location location = new Location(Bukkit.getWorld(config.getString("world")), Long.parseLong(loc[0]), Long.parseLong(loc[1]), Long.parseLong(loc[2]));

        p.teleport(location);
        played.set(p.getName() + "." + name, played.getInt(p.getName() + "." + name, 0) + 1);//增加玩家记录的挑战次数
        playing.set(name + ".running", true);//保存初始配置
        playing.set(name + ".time", System.currentTimeMillis());
        playing.set(name + ".player", p.getName());
        try {
            played.save(new File(dataFolder, "played.yml"));
            playing.save(new File(dataFolder, "playing.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        taskID.put(name, new DungeonRunnable(this, p, name, playing).runTaskTimer(plugin, 0, 20).getTaskId());

        boolean isOP = p.isOp();
        p.setOp(true);
        for (String cmd : config.getStringList("dungeons." + name + ".start-commands")) {//执行初始化指令
            cmd = cmd.replace("{playerName}", p.getName());
            Bukkit.dispatchCommand(p, PlaceholderAPI.setPlaceholders(p, cmd));
        }
        p.setOp(isOP);
    }

    /**
     * 结束地牢冒险
     *
     * @param name 地牢名称
     */
    public void endDungeon(String name, boolean runCommand) {


        YamlConfiguration playing = YamlConfiguration.loadConfiguration(new File(dataFolder, "playing.yml"));
        Player p = Bukkit.getPlayer(playing.getString(name + ".player"));
        playing.set(name + ".running", false);
        playing.set(name + ".time", 0);
        playing.set(name + ".player", "");
        playing.set(name + ".killed", null);
        try {
            playing.save(new File(dataFolder, "playing.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bukkit.getScheduler().cancelTask(taskID.get(name));

        taskID.remove(name);

        String[] loc = config.getString("spawn-loc").split(" ", 4);
        Location location = new Location(Bukkit.getWorld(loc[0]), Long.parseLong(loc[1]), Long.parseLong(loc[2]), Long.parseLong(loc[3]));
        p.teleport(location);

        if (runCommand) {
            boolean isOP = p.isOp();
            p.setOp(true);
            for (String cmd : config.getStringList("dungeons." + name + ".end-commands")) {//执行结束指令
                cmd = cmd.replace("{playerName}", p.getName());
                Bukkit.dispatchCommand(p, PlaceholderAPI.setPlaceholders(p, cmd));
            }
            p.setOp(isOP);
        }

    }

    public boolean isDungeonPlaying(String name) {
        YamlConfiguration playing = YamlConfiguration.loadConfiguration(new File(dataFolder, "playing.yml"));
        return playing.getBoolean(name + ".running");
    }


    public int getDungeonLastTime(String name) {
        YamlConfiguration playing = YamlConfiguration.loadConfiguration(new File(dataFolder, "playing.yml"));
        return (int) (config.getInt("dungeons." + name + ".limit-time") - (System.currentTimeMillis() - playing.getLong(name + ".time")) / 1000);
    }

    public boolean isDungeonExist(String name) {
        return config.getString("dungeons." + name + ".location") != null ? true : false;
    }

    public int getKillMobNum(String name, String mob) {
        YamlConfiguration playing = YamlConfiguration.loadConfiguration(new File(dataFolder, "playing.yml"));
        return playing.getInt(name + ".killed." + mob);
    }

    public int getKillMobMax(String name, String mob) {
        return config.getInt("dungeons." + name + ".mobs." + mob);
    }

    public List<String> getMobList(String name) {
        List<String> t = new ArrayList<>();
        try {
            for (String temp : config.getConfigurationSection("dungeons." + name + ".mobs").getKeys(false)) {
                t.add(temp);
            }
        } catch (Exception e) {

        }

        return t;
    }

    public void addKillMob(String name, String mob, int i) {
        YamlConfiguration playing = YamlConfiguration.loadConfiguration(new File(dataFolder, "playing.yml"));
        playing.set(name + ".killed." + mob, playing.getInt(name + ".killed." + mob) + i);
        try {
            playing.save(new File(dataFolder, "playing.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlayerPlaying(String player) {
        YamlConfiguration playing = YamlConfiguration.loadConfiguration(new File(dataFolder, "playing.yml"));
        for (String name : playing.getKeys(false)) {
            if (playing.getString(name + ".player").equals(player)) {
                return true;
            }
        }
        return false;
    }

    public String getPlayerPlaying(String player) {
        YamlConfiguration playing = YamlConfiguration.loadConfiguration(new File(dataFolder, "playing.yml"));
        for (String name : playing.getKeys(false)) {
            if (playing.getString(name + ".player").equals(player)) {
                return name;
            }
        }
        return null;
    }


    public void setPlayerPlayed(String player, String name, int times) {
        YamlConfiguration played = YamlConfiguration.loadConfiguration(new File(dataFolder, "played.yml"));
        played.set(player + "." + name, times);
        try {
            played.save(new File(dataFolder, "played.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPlayerPlayed(String player, String name) {
        YamlConfiguration played = YamlConfiguration.loadConfiguration(new File(dataFolder, "played.yml"));
        return played.getInt(player + "." + name);
    }

    public int getPlayerMaxPlayed(Player player, String name) {
        int max = 0;
        for (String permission : config.getConfigurationSection("dungeons." + name + ".limit").getKeys(true)) {
            if (player.hasPermission(permission)) {
                if (config.getInt("dungeons." + name + ".limit." + permission, 0) > max) {
                    max = config.getInt("dungeons." + name + ".limit." + permission);
                }
            }
        }
        return max;
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(new File(dataFolder, "config.yml"));
    }
}
