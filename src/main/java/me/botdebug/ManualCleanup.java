package me.botdebug;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import static me.botdebug.RegionFastReload.*;

public class ManualCleanup implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) return false;
        Bukkit.getServer().getConsoleSender().sendMessage("§a[§e" + instance.getName() + "§a]§r starts cleaning...");
        sender.sendMessage("§a[§e" + instance.getName() + "§a]§r starts cleaning...");
        net.minecraft.server.v1_12_R1.World nmsWorld = ((CraftWorld) (Bukkit.getWorld(instance.getConfig().getString("select-a").split(";")[0]))).getHandle();
        resetRegion(nmsWorld, posA, posB);
        Bukkit.getServer().getConsoleSender().sendMessage("§a[§e" + instance.getName() + "§a]§r finished cleaning...");
        sender.sendMessage("§a[§e" + instance.getName() + "§a]§r finished cleaning...");
        return true;
    }
}
