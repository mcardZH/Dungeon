package cn.minezone.dungeon;

import cn.minezone.dungeon.api.DungeonAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DungeonRunnable extends BukkitRunnable {

    private DungeonAPI api;
    private Player p;
    private String dungeon;

    public DungeonRunnable(DungeonAPI api, Player p, String dungeon, YamlConfiguration playing) {
        this.api = api;
        this.p = p;
        this.dungeon = dungeon;
    }

    @Override
    public void run() {
        if (p.isOnline() && api.isDungeonPlaying(dungeon)) {
            String bar = "";
            for (String mob : api.getMobList(dungeon)) {
                bar += ChatColor.RESET + "" + ChatColor.GREEN + mob + ChatColor.RESET + ":" + api.getKillMobNum(dungeon, mob) + "/" + api.getKillMobMax(dungeon, mob) + " ";
            }
            if (p == null) {
                System.out.print("p null");
            }
            if (api == null) {
                System.out.print("api null");
            }
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(bar + ChatColor.GREEN + "剩余时间:" + api.getDungeonLastTime(dungeon) + "s"));
        }
        if (api.getDungeonLastTime(dungeon) <= 0) {
            System.out.print("end");
            api.endDungeon(dungeon, false);
        }

    }

    private void sendActionbar(Player player, String message) {
        try {
            Constructor<?> constructor = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("IChatBaseComponent"), byte.class);

            Object icbc = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + message + "\"}");
            Object packet = constructor.newInstance(icbc, (byte) 2);
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);

            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    private Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

}
