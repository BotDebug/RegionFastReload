package me.botdebug;

import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.Blocks;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalTime;

public class RegionFastReload extends JavaPlugin {
    public static Plugin instance;
    public static BlockPosition posA;
    public static BlockPosition posB;


    public RegionFastReload() {
        instance = this;
        posA = stringToBlockPos(this.getConfig().getString("select-a"));
        posB = stringToBlockPos(this.getConfig().getString("select-b"));
    }

    public static BlockPosition stringToBlockPos(String stringPos) {
        if (stringPos == null) {
            return null;
        }
        String[] posStringSplit = stringPos.split(";");
        try {
            return new BlockPosition(Double.parseDouble(posStringSplit[1]), Double.parseDouble(posStringSplit[2]), Double.parseDouble(posStringSplit[3]));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void resetRegion(net.minecraft.server.v1_12_R1.World nmsWorld, BlockPosition a, BlockPosition b) {
        int maxX = Math.max(a.getX(), b.getX());
        int maxy = Math.max(a.getY(), b.getY());
        int maxZ = Math.max(a.getZ(), b.getZ());
        int minX = Math.min(a.getX(), b.getX());
        int minY = Math.min(a.getY(), b.getY());
        int minZ = Math.min(a.getZ(), b.getZ());
        for (int t1 = minX; t1 <= maxX; ++t1) {
            for (int t2 = minY; t2 <= maxy; ++t2) {
                for (int t3 = minZ; t3 <= maxZ; ++t3) {
                    BlockPosition bp = new BlockPosition(t1, t2, t3);
                    if (nmsWorld.getType(bp) != Blocks.BEDROCK.getBlockData()) {
                        removeBlockInNativeWorld(nmsWorld, bp);
                    }
                }
            }
        }
    }

    public static void removeBlockInNativeWorld(net.minecraft.server.v1_12_R1.World nmsWorld, BlockPosition bp) {
        nmsWorld.setTypeAndData(bp, net.minecraft.server.v1_12_R1.Blocks.AIR.getBlockData(), 2);
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
                net.minecraft.server.v1_12_R1.World nmsWorld = ((CraftWorld) (Bukkit.getWorld(this.getConfig().getString("select-a").split(";")[0]))).getHandle();
                resetRegion(nmsWorld, posA, posB);
                this.getServer().getConsoleSender().sendMessage("§a[§e" + this.getName() + "§a]§r finished cleaning...");
            }
        }, 0L, 1200L);
        this.getServer().getConsoleSender().sendMessage("§a[§e" + this.getName() + "§a]§r enabled!");
    }
}