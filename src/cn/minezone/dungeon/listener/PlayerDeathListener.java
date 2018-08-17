package cn.minezone.dungeon.listener;

import cn.minezone.dungeon.api.DungeonAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private DungeonAPI api;

    public PlayerDeathListener(DungeonAPI api) {
        this.api = api;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (api.isPlayerPlaying(e.getEntity().getName())) {
            api.endDungeon(api.getPlayerPlaying(e.getEntity().getName()), false);
        }
    }

}
