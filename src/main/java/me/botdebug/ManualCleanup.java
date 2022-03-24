package me.botdebug;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

import java.util.List;

import static me.botdebug.RegionFastReload.getAllBlock;
import static me.botdebug.RegionFastReload.stringToLocation;

public class ManualCleanup implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) return false;
        Bukkit.getServer().getConsoleSender().sendMessage("§a[§e" + "RegionFastReload" + "§a]§r starts cleaning...");
        List<Block> blocks = getAllBlock(stringToLocation(RegionFastReload.instance.getConfig().getString("select-a")), stringToLocation(RegionFastReload.instance.getConfig().getString("select-b")));
        net.minecraft.server.v1_12_R1.World nmsWorld = ((CraftWorld)(Bukkit.getWorld(RegionFastReload.instance.getConfig().getString("select-a").split(";")[0]))).getHandle();
        blocks.stream()
                .filter(block -> block.getType() != Material.BEDROCK)
                .forEach(block -> RegionFastReload.removeBlockInNativeWorld(nmsWorld, block.getX(), block.getY(), block.getZ()));
        return true;
    }
}
