package main.java.elementalmp4.listener;

import main.java.elementalmp4.service.PlotService;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class PlotListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Location blockLocation = e.getBlockPlaced().getLocation();
        Optional<String> blockOwner = PlotService.blockIsOwned(e.getPlayer().getName(), blockLocation.getBlockX(), blockLocation.getBlockZ(), blockLocation.getWorld().getName());
        if (blockOwner.isPresent()) {
            e.setCancelled(true);
            e.getPlayer().getWorld().spawnParticle(Particle.SMOKE_LARGE, e.getBlock().getLocation(), 3);
            e.getPlayer().sendMessage(ChatColor.RED + "Only " + ChatColor.GOLD + blockOwner.get() + ChatColor.RED + " can build here!");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Location blockLocation = e.getBlock().getLocation();
        Optional<String> blockOwner = PlotService.blockIsOwned(e.getPlayer().getName(), blockLocation.getBlockX(), blockLocation.getBlockZ(), blockLocation.getWorld().getName());
        if (blockOwner.isPresent()) {
            e.setCancelled(true);
            e.getPlayer().getWorld().spawnParticle(Particle.SMOKE_LARGE, e.getBlock().getLocation(), 3);
            e.getPlayer().sendMessage(ChatColor.RED + "Only " + ChatColor.GOLD + blockOwner.get() + ChatColor.RED + " can build here!");
        }
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) {
            return;
        }
        Location blockLocation = e.getClickedBlock().getLocation();
        Optional<String> blockOwner = PlotService.blockIsOwned(e.getPlayer().getName(), blockLocation.getBlockX(), blockLocation.getBlockZ(), blockLocation.getWorld().getName());
        if (blockOwner.isPresent()) {
            e.setCancelled(true);
            e.getPlayer().getWorld().spawnParticle(Particle.SMOKE_LARGE, e.getClickedBlock().getLocation(), 3);
            e.getPlayer().sendMessage(ChatColor.RED + "Only " + ChatColor.GOLD + blockOwner.get() + ChatColor.RED + " can interact with that!");
        }

        if (!e.getPlayer().hasPermission("sebutils.plots")) return;
        if (e.getAction().toString().contains("RIGHT_CLICK")) {
            ItemStack item = e.getItem();
            if (item != null && item.getType() == Material.IRON_NUGGET) {
                if (e.getClickedBlock() != null && e.getClickedBlock().getType() != Material.AIR) {
                    PlotService.handleIronNuggetClick(e);
                }
            }
        }
    }

}
