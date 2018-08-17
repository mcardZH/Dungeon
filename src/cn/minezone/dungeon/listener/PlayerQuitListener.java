package cn.minezone.dungeon.listener;

import cn.minezone.dungeon.api.DungeonAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private DungeonAPI api;

    public PlayerQuitListener(DungeonAPI api) {
        this.api = api;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (api.isPlayerPlaying(e.getPlayer().getName())) {
            api.endDungeon(api.getPlayerPlaying(e.getPlayer().getName()), false);
        }
    }
}
