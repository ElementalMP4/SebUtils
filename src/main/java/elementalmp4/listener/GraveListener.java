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
import java.util.*;

@SebUtilsListener
public class GraveListener implements Listener {

    private static final String GRAVE_OWNER_META = "grave_owner";
    private static final String GRAVE_LABEL_META = "grave_label";
    private static final String INVENTORY_CONTENTS_META = "grave_inventory_contents";
    private static final String INVENTORY_ARMOR_META = "grave_armor_contents";
    private static final String INVENTORY_OFFHAND_META = "grave_offhand_contents";
    private static final String INVENTORY_EXTRA_META = "grave_extra_contents";
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
        deathBlock.setMetadata(INVENTORY_OFFHAND_META, new FixedMetadataValue(SebUtils.getPlugin(), itemStackArrayToBase64(player.getInventory().getItemInOffHand())));
        deathBlock.setMetadata(INVENTORY_ARMOR_META, new FixedMetadataValue(SebUtils.getPlugin(), itemStackArrayToBase64(player.getInventory().getArmorContents())));
        deathBlock.setMetadata(INVENTORY_EXTRA_META, new FixedMetadataValue(SebUtils.getPlugin(), itemStackArrayToBase64(player.getInventory().getExtraContents())));

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
        if (clickedBlock != null && clickedBlock.getType() == Material.DEEPSLATE_BRICK_WALL) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                String playerName = player.getName();
                if (clickedBlock.hasMetadata(GRAVE_OWNER_META)) {
                    if (clickedBlock.getMetadata(GRAVE_OWNER_META).get(0).asString().equals(playerName)) {

                        ItemStack[] inventory = itemStackArrayFromBase64(clickedBlock.getMetadata(INVENTORY_CONTENTS_META).get(0).asString());
                        ItemStack[] offhand = itemStackArrayFromBase64(clickedBlock.getMetadata(INVENTORY_OFFHAND_META).get(0).asString());
                        ItemStack[] armor = itemStackArrayFromBase64(clickedBlock.getMetadata(INVENTORY_ARMOR_META).get(0).asString());
                        ItemStack[] extra = itemStackArrayFromBase64(clickedBlock.getMetadata(INVENTORY_EXTRA_META).get(0).asString());

                        player.getInventory().setContents(inventory);
                        player.getInventory().setItemInOffHand(offhand[0]);
                        player.getInventory().setArmorContents(armor);
                        player.getInventory().setExtraContents(extra);

                        player.playSound(player.getLocation(), Sound.ITEM_BUNDLE_DROP_CONTENTS, 10, 1);

                        clickedBlock.setType(Material.AIR);
                        String armorStandId = clickedBlock.getMetadata(GRAVE_LABEL_META).get(0).asString();
                        ArmorStand armorStand = (ArmorStand) Bukkit.getEntity(UUID.fromString(armorStandId));
                        if (armorStand != null) {
                            armorStand.remove();
                        }

                        String graveId = clickedBlock.getMetadata(GRAVE_ID_META).get(0).asString();
                        GraveService.removeGrave(graveId);
                    } else {
                        player.sendMessage(ChatColor.RED + "This isn't your grave!");
                    }
                }
            }
        }
    }

    //https://gist.github.com/graywolf336/8153678
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
