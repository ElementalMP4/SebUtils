package main.java.elementalmp4.sebutils.modules;

import io.javalin.http.Context;
import main.java.elementalmp4.sebutils.entity.ChunkPosition;
import main.java.elementalmp4.sebutils.utils.BlockColorMap;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static main.java.elementalmp4.sebutils.SebUtils.getPlugin;
import static main.java.elementalmp4.sebutils.SebUtils.getPluginLogger;

public class MapModule extends AbstractModule implements Listener {

    private static final int CHUNKS_PER_RUN = 30;
    private final Set<ChunkPosition> renderBacklog = ConcurrentHashMap.newKeySet();

    private BukkitTask renderTask;
    private BukkitTask scanTask;

    private World world;
    private Path tileDir;

    @Override
    protected void onStart() {
        Plugin plugin = getPlugin();

        this.world = Bukkit.getWorlds().get(0);
        this.tileDir = plugin.getDataFolder()
                .toPath()
                .resolve("tiles")
                .resolve(world.getName());

        try {
            Files.createDirectories(tileDir);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to create map tile directory");
            return;
        }

        this.renderTask = Bukkit.getScheduler().runTaskTimer(
                plugin,
                this::processRenderingBacklog,
                20L,
                20L
        );

        this.scanTask = Bukkit.getScheduler().runTaskTimer(
                plugin,
                this::scanForUnrenderedChunks,
                20L * 10,
                20L * 60
        );

        plugin.getLogger().info("Map module enabled");
    }

    @Override
    protected void onStop() {
        if (renderTask != null) {
            renderTask.cancel();
        }
        if (scanTask != null) {
            scanTask.cancel();
        }
        renderBacklog.clear();
        getPlugin().getLogger().info("Map module disabled");
    }

    public void markForRender(Chunk chunk) {
        renderBacklog.add(new ChunkPosition(chunk.getX(), chunk.getZ()));
    }

    private void processRenderingBacklog() {
        if (!renderBacklog.isEmpty()) {
            getPluginLogger().info("%d chunks waiting to be rendered".formatted(renderBacklog.size()));
        }
        Iterator<ChunkPosition> it = renderBacklog.iterator();

        int chunk = 0;
        while (chunk++ < CHUNKS_PER_RUN && it.hasNext()) {
            ChunkPosition pos = it.next();
            it.remove();

            if (!world.isChunkLoaded(pos.x(), pos.z())) {
                continue;
            }

            int[] pixels = renderChunkColors(world, pos.x(), pos.z());
            writeTileAsync(
                    getPlugin(),
                    tileDir,
                    pos.x(),
                    pos.z(),
                    pixels
            );
        }
    }

    private void scanForUnrenderedChunks() {
        for (Chunk chunk : world.getLoadedChunks()) {
            int x = chunk.getX();
            int z = chunk.getZ();

            if (!tileExists(x, z)) {
                renderBacklog.add(new ChunkPosition(x, z));
            }
        }
    }

    private boolean tileExists(int chunkX, int chunkZ) {
        return tileExists(tileDir, chunkX, chunkZ);
    }

    public static boolean tileExists(Path tileDir, int chunkX, int chunkZ) {
        return Files.exists(
                tileDir.resolve(chunkX + "_" + chunkZ + ".png")
        );
    }


    private int[] renderChunkColors(World world, int chunkX, int chunkZ) {
        int[] pixels = new int[16 * 16];
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = world.getMaxHeight() - 1; y >= world.getMinHeight(); y--) {
                    Block block = world.getBlockAt(baseX + x, y, baseZ + z);
                    if (!block.isEmpty()) {
                        pixels[z * 16 + x] =
                                BlockColorMap.get(block.getType());
                        break;
                    }
                }
            }
        }
        return pixels;
    }

    private void writeTileAsync(
            Plugin plugin,
            Path dir,
            int chunkX,
            int chunkZ,
            int[] pixels
    ) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                BufferedImage img = new BufferedImage(
                        16, 16, BufferedImage.TYPE_INT_RGB
                );
                img.setRGB(0, 0, 16, 16, pixels, 0, 16);

                BufferedImage scaled = new BufferedImage(
                        128, 128, BufferedImage.TYPE_INT_RGB
                );
                Graphics2D g = scaled.createGraphics();
                g.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
                );
                g.drawImage(img, 0, 0, 128, 128, null);
                g.dispose();

                ImageIO.write(
                        scaled,
                        "PNG",
                        dir.resolve(chunkX + "_" + chunkZ + ".png").toFile()
                );
            } catch (IOException e) {
                plugin.getLogger().severe(
                        "Failed to write map tile: " + e.getMessage()
                );
            }
        });
    }

    public void serveTile(Context ctx) {
        String worldName = ctx.pathParam("world");
        String tileName  = ctx.pathParam("tile");

        if (!tileName.endsWith(".png")) {
            ctx.status(400);
            return;
        }

        Path tilePath = getPlugin().getDataFolder()
                .toPath()
                .resolve("tiles")
                .resolve(worldName)
                .resolve(tileName);

        if (!Files.exists(tilePath)) {
            ctx.status(404);
            return;
        }

        try {
            ctx.header("Cache-Control", "max-age=600");
            ctx.header("Content-Type", "image/png");
            ctx.result(Files.newInputStream(tilePath));
        } catch (IOException e) {
            getPlugin().getLogger().warning(
                    "Failed to serve tile " + tilePath + ": " + e.getMessage()
            );
            ctx.status(500);
        }
    }

}
