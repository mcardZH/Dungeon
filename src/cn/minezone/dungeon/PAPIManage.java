package cn.minezone.dungeon;

import cn.minezone.dungeon.api.DungeonAPI;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PAPIManage extends EZPlaceholderHook {

    private DungeonAPI api;

    public PAPIManage(Plugin plugin, DungeonAPI api) {
        super(plugin, "dungeon");
        this.api = api;
    }

    @Override
    public String onPlaceholderRequest(Player player, String string) {
        if (string.equals("left_time")) {
            return api.getDungeonLastTime(api.getPlayerPlaying(player.getName())) + "";
        }
        if (string.startsWith("mob_killed_")) {
            String t = string.replace("mob_killed_", "");
            return api.getKillMobNum(api.getPlayerPlaying(player.getName()), t) + "";
        }
        if (string.startsWith("dungeon_playing_")) {
            String t = string.replace("dungeon_playing_", "");
            return api.isDungeonPlaying(t) + "";
        }
        if (string.startsWith("left_times_")) {
            String t = string.replace("left_times_", "");
            return api.getPlayerMaxPlayed(player, t) - api.getPlayerPlayed(player.getName(), t) + "";
        }
        if (string.startsWith("max_times_")) {
            String t = string.replace("max_times_", "");
            return api.getPlayerMaxPlayed(player, t) + "";
        }
        return null;
    }
}
