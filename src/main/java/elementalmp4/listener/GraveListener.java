package main.java.elementalmp4.listener;

import main.java.elementalmp4.GlobalConfig;
import main.java.elementalmp4.SebUtils;
import main.java.elementalmp4.annotation.SebUtilsListener;
import main.java.elementalmp4.service.GlobalConfigService;
import main.java.elementalmp4.service.GraveService;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@SebUtilsListener
public class GraveListener implements Listener {

    private static final String GRAVE_OWNER_META = "grave_owner";
    private static final String GRAVE_LABEL_META = "grave_label";
    private static final String INVENTORY_CONTENTS_META = "grave_inventory_contents";
    private static final String GRAVE_ID_META = "grave_id";

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (GlobalConfigService.getAsBoolean(GlobalConfig.GRAVES_ENABLED)) {
            createGrave(event);
        }
    }

    private void createGrave(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.getDrops().clear();

        Block deathBlock = player.getLocation().getBlock();
        deathBlock.setType(Material.DEEPSLATE_BRICK_WALL);
        deathBlock.setMetadata(GRAVE_OWNER_META, new FixedMetadataValue(SebUtils.getPlugin(), player.getName()));

        Location labelLocation = deathBlock.getLocation().add(0.5, 1.5, 0.5);
        ArmorStand armorStand = deathBlock.getWorld().spawn(labelLocation, ArmorStand.class);
        armorStand.setCustomName(player.getName() + "'s Grave");
        armorStand.setCustomNameVisible(true);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setArms(false);
        armorStand.setBasePlate(false);
        armorStand.setSmall(true);
        armorStand.setHeadPose(new EulerAngle(Math.toRadians(90), 0, 0));

        deathBlock.setMetadata(GRAVE_LABEL_META, new FixedMetadataValue(SebUtils.getPlugin(), armorStand.getUniqueId().toString()));
        deathBlock.setMetadata(INVENTORY_CONTENTS_META, new FixedMetadataValue(SebUtils.getPlugin(), itemStackArrayToBase64(player.getInventory().getContents())));

        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);

        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();
        String world = player.getWorld().getName();
        String id = GraveService.createGrave(player.getName(), x, y, z, world);

        deathBlock.setMetadata(GRAVE_ID_META, new FixedMetadataValue(SebUtils.getPlugin(), id));

        TextComponent message = new TextComponent();
        message.addExtra(ChatColor.RED + "You died!");
        TextComponent teleportComponent = new TextComponent("" + ChatColor.YELLOW + ChatColor.BOLD + " [TELEPORT]");
        teleportComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpgrave " + id));
        message.addExtra(teleportComponent);
        player.spigot().sendMessage(message);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();

        // See if the interaction is a click on a deepslate brick wall first, so we know the interaction is on a potential grave
        if (clickedBlock == null || clickedBlock.getType() != Material.DEEPSLATE_BRICK_WALL) {
            return;
        }

        // Look for our custom metadata, so we know the interaction is on an actual grave
        if (!clickedBlock.hasMetadata(GRAVE_OWNER_META)) {
            return;
        }

        // Then see if it was a right click, so we know if it was a retrieval attempt
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // Check the player's name against the metadata
        String playerName = player.getName();
        if (!clickedBlock.getMetadata(GRAVE_OWNER_META).get(0).asString().equals(playerName)) {
            player.sendMessage(ChatColor.RED + "This isn't your grave!");
            return;
        }

        // Restore the inventory
        ItemStack[] inventory = itemStackArrayFromBase64(clickedBlock.getMetadata(INVENTORY_CONTENTS_META).get(0).asString());
        if (isEmpty(player.getInventory().getContents())) {
            player.getInventory().setContents(inventory);
        } else {
            for (ItemStack stack : inventory) {
                if (stack != null && stack.getType() != Material.AIR) {
                    player.getWorld().dropItemNaturally(player.getLocation(), stack);
                }
            }
        }

        player.playSound(player.getLocation(), Sound.ITEM_BUNDLE_DROP_CONTENTS, 10, 1);
        clickedBlock.setType(Material.AIR);
        String armorStandId = clickedBlock.getMetadata(GRAVE_LABEL_META).get(0).asString();
        ArmorStand armorStand = (ArmorStand) Bukkit.getEntity(UUID.fromString(armorStandId));
        if (armorStand != null) {
            armorStand.remove();
        }

        String graveId = clickedBlock.getMetadata(GRAVE_ID_META).get(0).asString();
        GraveService.removeGrave(graveId);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (block.getType() == Material.DEEPSLATE_BRICK_WALL && block.hasMetadata(GRAVE_OWNER_META)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can't destroy graves!");
        }
    }

    private boolean isEmpty(ItemStack[] items) {
        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                return false;
            }
        }
        return true;
    }

    public static String itemStackArrayToBase64(ItemStack... items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Unable to save item stacks", e);
        }
    }

    public static ItemStack[] itemStackArrayFromBase64(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException("Unable to decode class type", e);
        }
    }
}
