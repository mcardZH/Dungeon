package cn.minezone.dungeon.listener;

import cn.minezone.dungeon.api.DungeonAPI;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MobKilledListener implements Listener {

    private DungeonAPI api;

    public MobKilledListener(DungeonAPI api) {
        this.api = api;
    }

    @EventHandler
    public void onKilled(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) {
            return;
        }
        LivingEntity le;
        try {
            le = (LivingEntity) e.getEntity();
        } catch (Exception e1) {
            return;
        }
        if (le.getHealth() <= e.getFinalDamage()) {
            //death
            if (e.getEntity().getName() == null) {
                return;
            }
            if (!e.getDamager().getType().equals(EntityType.PLAYER)) {
                return;
            }
            Player p = (Player) e.getDamager();
            int size = 0;

            for (String mob : api.getMobList(api.getPlayerPlaying(p.getName()))) {
                if (mob.equals(e.getEntity().getName())) {
                    api.addKillMob(api.getPlayerPlaying(p.getName()), mob, 1);
                    break;
                }
            }
            for (String mob : api.getMobList(api.getPlayerPlaying(p.getName()))) {
                if (api.getKillMobNum(api.getPlayerPlaying(p.getName()), mob) >= api.getKillMobMax(api.getPlayerPlaying(p.getName()), mob)) {
                    size++;
                }
            }
            if (size == api.getMobList(api.getPlayerPlaying(p.getName())).size() && size != 0) {
                api.endDungeon(api.getPlayerPlaying(p.getName()), true);
            }
        }
    }


}
