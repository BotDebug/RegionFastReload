package me.botdebug;

import net.minecraft.server.v1_12_R1.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RegionFastReload extends JavaPlugin {
    public static Plugin instance;

    public RegionFastReload() {
        instance = this;
    }

    public static Location stringToLocation(String stringLocation) {
        if (stringLocation == null) {
            return null;
        }
        String[] locStringSplit = stringLocation.split(";");
        World world = Bukkit.getWorld(locStringSplit[0]);
        if (world == null) {
            return null;
        }
        try {
            return new Location(world, Double.parseDouble(locStringSplit[1]), Double.parseDouble(locStringSplit[2]), Double.parseDouble(locStringSplit[3]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static List<Location> getAllLocation(Location a, Location b) {
        int maxX = Math.max(a.getBlockX(), b.getBlockX());
        int maxy = Math.max(a.getBlockY(), b.getBlockY());
        int maxZ = Math.max(a.getBlockZ(), b.getBlockZ());
        int minX = Math.min(a.getBlockX(), b.getBlockX());
        int minY = Math.min(a.getBlockY(), b.getBlockY());
        int minZ = Math.min(a.getBlockZ(), b.getBlockZ());
        List<Location> list = new ArrayList<>();
        for (int t1 = minX; t1 <= maxX; ++t1) {
            for (int t2 = minY; t2 <= maxy; ++t2) {
                for (int t3 = minZ; t3 <= maxZ; ++t3) {
                    Location bb = new Location(a.getWorld(), t1, t2, t3);
                    list.add(bb);
                }
            }
        }
        return list;
    }

    public static List<Block> getAllBlock(Location a, Location b) {
        List<Block> c = new ArrayList<>();
        getAllLocation(a, b).forEach(s -> c.add(s.getBlock()));
        return c;
    }

    public static void removeBlockInNativeWorld(net.minecraft.server.v1_12_R1.World nmsWorld, int x, int y, int z) {
        nmsWorld.setTypeAndData(new BlockPosition(x, y, z), net.minecraft.server.v1_12_R1.Blocks.AIR.getBlockData(), 2);
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        getCommand("regionfastreload").setExecutor(new ManualCleanup());
        this.getServer().getScheduler().runTaskTimer(this, () -> {
            if (!this.getConfig().getBoolean("enable")) {
                return;
            }
            for (String string : this.getConfig().getStringList("clear-time")) {
                LocalTime curTime = LocalTime.now();
                String[] stringArray = string.split(":");
                int timerHour = Integer.parseInt(stringArray[0]);
                int timerMinute = Integer.parseInt(stringArray[1]);
                LocalTime countDown = LocalTime.of(timerHour, timerMinute).minusMinutes(1);
                if (countDown.getHour() == curTime.getHour() && countDown.getMinute() == curTime.getMinute()) {
                    for (Player player : this.getServer().getOnlinePlayers()) {
                        player.sendTitle("", this.getConfig().getString("tip-message"), 10, 70, 20);
                    }
                    continue;
                }
                if (curTime.getHour() != timerHour || curTime.getMinute() != timerMinute) continue;
                this.getServer().getConsoleSender().sendMessage("§a[§e" + this.getName() + "§a]§r starts cleaning...");
                List<Block> blocks = getAllBlock(stringToLocation(this.getConfig().getString("select-a")), stringToLocation(this.getConfig().getString("select-b")));
                net.minecraft.server.v1_12_R1.World nmsWorld = ((CraftWorld)(Bukkit.getWorld(RegionFastReload.instance.getConfig().getString("select-a").split(";")[0]))).getHandle();
                blocks.stream()
                        .filter(block -> block.getType() != Material.BEDROCK)
                        .forEach(block -> RegionFastReload.removeBlockInNativeWorld(nmsWorld, block.getX(), block.getY(), block.getZ()));
            }
        }, 0L, 1200L);
        this.getServer().getConsoleSender().sendMessage("§a[§e" + this.getName() + "§a]§r enabled!");
    }
}