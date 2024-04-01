package main.java.elementalmp4.listener;

import main.java.elementalmp4.GlobalConfig;
import main.java.elementalmp4.annotation.SebUtilsListener;
import main.java.elementalmp4.service.GlobalConfigService;
import main.java.elementalmp4.service.PermitService;
import main.java.elementalmp4.service.PlotService;
import main.java.elementalmp4.utils.Plot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@SebUtilsListener
public class PlotListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Location blockLocation = e.getBlockPlaced().getLocation();
        Optional<Plot> blockOwner = PlotService.blockIsOwnedBySomeoneElse(e.getPlayer().getName(), blockLocation.getBlockX(), blockLocation.getBlockZ(), blockLocation.getWorld().getName());
        if (blockOwner.isPresent()) {
            if (!PermitService.userHasPermit(blockOwner.get().getId(), e.getPlayer().getName())) {
                e.setCancelled(true);
                e.getPlayer().getWorld().spawnParticle(Particle.SMOKE_LARGE, e.getBlock().getLocation(), 3);
                e.getPlayer().sendMessage(ChatColor.RED + "Only " + ChatColor.GOLD + blockOwner.get().getOwner() + ChatColor.RED + " can build here!");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Location blockLocation = e.getBlock().getLocation();
        Optional<Plot> blockOwner = PlotService.blockIsOwnedBySomeoneElse(e.getPlayer().getName(), blockLocation.getBlockX(), blockLocation.getBlockZ(), blockLocation.getWorld().getName());
        if (blockOwner.isPresent()) {
            if (!PermitService.userHasPermit(blockOwner.get().getId(), e.getPlayer().getName())) {
                e.setCancelled(true);
                e.getPlayer().getWorld().spawnParticle(Particle.SMOKE_LARGE, e.getBlock().getLocation(), 3);
                e.getPlayer().sendMessage(ChatColor.RED + "Only " + ChatColor.GOLD + blockOwner.get().getOwner() + ChatColor.RED + " can build here!");
            }
        }
    }

    @EventHandler
    public void onPlayerAttacked(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() == EntityType.PLAYER) {
            Location blockLocation = e.getEntity().getLocation();
            Optional<Plot> blockOwner = PlotService.blockIsOwnedBySomeoneElse(e.getDamager().getName(), blockLocation.getBlockX(), blockLocation.getBlockZ(), blockLocation.getWorld().getName());
            if (blockOwner.isPresent()) {
                boolean cancelEvent = true;

                if (PermitService.userHasPermit(blockOwner.get().getId(), e.getDamager().getName())) {
                    cancelEvent = false;
                }

                if (e.getDamager().hasPermission("sebutils.admin") && GlobalConfigService.getAsBoolean(GlobalConfig.ADMIN_PLOT_OVERRIDE)) {
                    cancelEvent = false;
                }

                if (cancelEvent) {
                    e.setCancelled(true);
                    e.getDamager().getWorld().spawnParticle(Particle.SMOKE_LARGE, e.getEntity().getLocation(), 3);
                    e.getDamager().sendMessage(ChatColor.RED + "Only " + ChatColor.GOLD + blockOwner.get().getOwner() + ChatColor.RED + " can interact with that!");
                }
            }
        }
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) {
            return;
        }
        Location blockLocation = e.getClickedBlock().getLocation();
        Optional<Plot> blockOwner = PlotService.blockIsOwnedBySomeoneElse(e.getPlayer().getName(), blockLocation.getBlockX(), blockLocation.getBlockZ(), blockLocation.getWorld().getName());
        if (blockOwner.isPresent()) {
            boolean cancelEvent = true;

            if (PermitService.userHasPermit(blockOwner.get().getId(), e.getPlayer().getName())) {
                cancelEvent = false;
            }

            if (e.getPlayer().hasPermission("sebutils.admin") && GlobalConfigService.getAsBoolean(GlobalConfig.ADMIN_PLOT_OVERRIDE)) {
                cancelEvent = false;
            }

            if (cancelEvent) {
                e.setCancelled(true);
                e.getPlayer().getWorld().spawnParticle(Particle.SMOKE_LARGE, e.getClickedBlock().getLocation(), 3);
                e.getPlayer().sendMessage(ChatColor.RED + "Only " + ChatColor.GOLD + blockOwner.get().getOwner() + ChatColor.RED + " can interact with that!");
                return;
            }
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
