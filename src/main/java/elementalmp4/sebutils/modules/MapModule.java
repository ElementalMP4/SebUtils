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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static main.java.elementalmp4.sebutils.SebUtils.getPlugin;
import static main.java.elementalmp4.sebutils.SebUtils.getPluginLogger;

public class MapModule extends AbstractModule implements Listener {

    private static final int CHUNKS_PER_RUN = 30;
    private static final int MAX_ZOOM = 4;

    private final Set<ChunkPosition> renderBacklog =
            ConcurrentHashMap.newKeySet();

    private final Map<String, World> worlds =
            new ConcurrentHashMap<>();

    private BukkitTask renderTask;
    private BukkitTask scanTask;

    @Override
    protected void onStart() {
        Plugin plugin = getPlugin();

        for (World world : Bukkit.getWorlds()) {
            worlds.put(world.getName(), world);
            try {
                Files.createDirectories(tileDirFor(world.getName(), 0));
            } catch (IOException e) {
                getPluginLogger().severe(
                        "Failed to create tile dir for " + world.getName()
                );
            }
        }

        renderTask = Bukkit.getScheduler().runTaskTimer(
                plugin, this::processRenderingBacklog, 20L, 20L
        );

        scanTask = Bukkit.getScheduler().runTaskTimer(
                plugin, this::scanForUnrenderedChunks, 200L, 1200L
        );

        getPluginLogger().info("Map module enabled");
    }

    @Override
    protected void onStop() {
        if (renderTask != null) renderTask.cancel();
        if (scanTask != null) scanTask.cancel();
        renderBacklog.clear();
        getPluginLogger().info("Map module disabled");
    }

    public void markForRender(Chunk chunk) {
        renderBacklog.add(
                new ChunkPosition(
                        chunk.getWorld().getName(),
                        chunk.getX(),
                        chunk.getZ(),
                        0
                )
        );
    }

    private void processRenderingBacklog() {
        Iterator<ChunkPosition> it = renderBacklog.iterator();
        int processed = 0;

        while (processed++ < CHUNKS_PER_RUN && it.hasNext()) {
            ChunkPosition pos = it.next();
            it.remove();

            World world = worlds.get(pos.world());
            if (world == null) continue;

            if (pos.zoom() == 0) {
                if (!world.isChunkLoaded(pos.x(), pos.z())) continue;

                int[] pixels = renderChunkColors(world, pos.x(), pos.z());
                writeBaseTileAsync(pos, pixels);
                enqueueParentTiles(pos);
            } else {
                writeZoomTileAsync(pos);
            }
        }

        getPluginLogger().info("Rendered %d chunks - %d remaining".formatted(processed, renderBacklog.size()));
    }

    private void enqueueParentTiles(ChunkPosition child) {
        int x = child.x();
        int z = child.z();

        for (int zoom = 1; zoom <= MAX_ZOOM; zoom++) {
            x >>= 1;
            z >>= 1;
            renderBacklog.add(new ChunkPosition(child.world(), x, z, zoom));
        }
    }

    private int[] renderChunkColors(World world, int chunkX, int chunkZ) {
        int[] pixels = new int[16 * 16];
        int baseX = chunkX << 4;
        int baseZ = chunkZ << 4;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = world.getMaxHeight() - 1;
                     y >= world.getMinHeight();
                     y--) {

                    Block block = world.getBlockAt(baseX + x, y, baseZ + z);

                    if (!block.isEmpty()) {
                        pixels[z * 16 + x] = BlockColorMap.get(block.getType());
                        break;
                    }
                }
            }
        }
        return pixels;
    }

    private void writeBaseTileAsync(ChunkPosition pos, int[] pixels) {
        Bukkit.getScheduler().runTaskAsynchronously(
                getPlugin(), () -> {
                    try {
                        Path dir = tileDirFor(pos.world(), 0);
                        Files.createDirectories(dir);

                        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
                        img.setRGB(0, 0, 16, 16, pixels, 0, 16);

                        BufferedImage scaled = scaleToTileSize(img);
                        File outputFile = dir.resolve(getTileName(pos.x(), pos.z())).toFile();
                        ImageIO.write(scaled, "PNG", outputFile);
                    } catch (IOException e) {
                        getPluginLogger().warning(
                                "Failed to write base tile: " + e.getMessage()
                        );
                    }
                }
        );
    }

    private void writeZoomTileAsync(ChunkPosition pos) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try {
                BufferedImage img = renderZoomTile(pos.world(), pos.zoom(), pos.x(), pos.z());
                Path dir = tileDirFor(pos.world(), pos.zoom());
                Files.createDirectories(dir);
                File imageFile = dir.resolve(pos.x() + "_" + pos.z() + ".png").toFile();
                ImageIO.write(img, "PNG", imageFile);
            } catch (IOException e) {
                getPluginLogger().warning(
                        "Failed to write zoom tile: " + e.getMessage()
                );
            }
        });
    }

    private static String getChildTileName(int x, int dx, int z, int dz) {
        return getTileName(((x << 1) + dx), ((z << 1) + dz));
    }

    private static String getTileName(int x, int z) {
        return "%d_%d.png".formatted(x, z);
    }

    private BufferedImage renderZoomTile(String world, int zoom, int x, int z) throws IOException {
        BufferedImage aggregateTileImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        Graphics2D aggregateTileGraphics = aggregateTileImage.createGraphics();
        Path childDir = tileDirFor(world, zoom - 1);

        for (int dx = 0; dx < 2; dx++) {
            for (int dz = 0; dz < 2; dz++) {

                Path childTile = childDir.resolve(getChildTileName(x, dx, z, dz));
                if (!Files.exists(childTile)) continue;

                BufferedImage chunk = ImageIO.read(childTile.toFile());
                aggregateTileGraphics.drawImage(chunk, dx * 64, dz * 64, 64, 64, null);
            }
        }

        aggregateTileGraphics.dispose();
        return aggregateTileImage;
    }

    private BufferedImage scaleToTileSize(BufferedImage src) {
        BufferedImage out = new BufferedImage(
                128, 128, BufferedImage.TYPE_INT_RGB
        );
        Graphics2D scale = out.createGraphics();
        scale.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );
        scale.drawImage(src, 0, 0, 128, 128, null);
        scale.dispose();
        return out;
    }

    private Path tileDirFor(String world, int zoom) {
        return getPlugin().getDataFolder()
                .toPath()
                .resolve("tiles")
                .resolve(world)
                .resolve("z" + zoom);
    }

    private void scanForUnrenderedChunks() {
        int tilesToBeRendered = 0;
        for (World world : worlds.values()) {
            for (Chunk chunk : world.getLoadedChunks()) {
                Path tile = tileDirFor(world.getName(), 0).resolve(getTileName(chunk.getX(), chunk.getZ()));

                if (!Files.exists(tile)) {
                    markForRender(chunk);
                    tilesToBeRendered++;
                }
            }
        }
        getPluginLogger().info("Queued %d tiles for rendering".formatted(tilesToBeRendered));
    }

    public static boolean tileExists(Path tileDir, int chunkX, int chunkZ) {
        return Files.exists(
                tileDir.resolve("z0")
                        .resolve(getTileName(chunkX, chunkZ))
        );
    }

    public void serveTile(Context ctx) {
        Path tilePath = getPlugin().getDataFolder()
                .toPath()
                .resolve("tiles")
                .resolve(ctx.pathParam("world"))
                .resolve("z" + ctx.pathParam("zoom"))
                .resolve(ctx.pathParam("tile"));

        if (!Files.exists(tilePath)) {
            ctx.status(404);
            return;
        }

        try {
            ctx.header("Cache-Control", "public, max-age=3600");
            ctx.header("Content-Type", "image/png");
            ctx.result(Files.newInputStream(tilePath));
        } catch (IOException e) {
            getPluginLogger().warning("Failed to serve tile " + tilePath + ": " + e.getMessage());
            ctx.status(500);
        }
    }
}
