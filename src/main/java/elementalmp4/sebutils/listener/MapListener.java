package main.java.elementalmp4.sebutils.listener;

import main.java.elementalmp4.sebutils.annotation.SebUtilsListener;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.modules.MapModule;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import java.nio.file.Path;

import static main.java.elementalmp4.sebutils.SebUtils.getModuleManager;
import static main.java.elementalmp4.sebutils.SebUtils.getPlugin;

@SebUtilsListener
public class MapListener implements Listener {

    private static boolean mapDisabled() {
        return !GlobalConfigService.getAsBoolean(GlobalConfig.MAP_ENABLED);
    }

    private static boolean blockIsNotVisible(Block block) {
        World world = block.getWorld();
        int x = block.getX();
        int z = block.getZ();

        int highestY = world.getHighestBlockYAt(x, z);
        return block.getY() < highestY;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (mapDisabled()) return;

        Chunk c = event.getChunk();
        Path tileDir = getPlugin().getDataFolder()
                .toPath()
                .resolve("tiles");

        if (!MapModule.tileExists(tileDir, c.getX(), c.getZ())) {
            getModuleManager().get(MapModule.class).markForRender(c);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (mapDisabled()) return;

        Block block = event.getBlockPlaced();
        if (blockIsNotVisible(block)) return;

        getModuleManager().get(MapModule.class).markForRender(block.getChunk());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (mapDisabled()) return;

        Block block = event.getBlock();
        if (blockIsNotVisible(block)) return;

        getModuleManager().get(MapModule.class).markForRender(block.getChunk());
    }
}

