package org.dynmap.bukkit.helper.v26_2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.plugin.Plugin;
import org.dynmap.Log;
import org.dynmap.bukkit.helper.AbstractMapChunkCache.Snapshot;
import org.dynmap.bukkit.helper.MapChunkCacheClassic;
import org.dynmap.renderer.DynmapBlockState;

/**
 * Paper 26.2 blocks chunk loads from async render threads.
 */
public class MapChunkCache26_2 extends MapChunkCacheClassic {
    public static class WrappedSnapshot26_2 implements Snapshot {
        private final ChunkSnapshot ss;
        private final Method getBlockType;

        public WrappedSnapshot26_2(ChunkSnapshot ss) {
            this.ss = ss;
            this.getBlockType = getSnapshotBlockTypeMethod(ss);
        }

        @Override
        public DynmapBlockState getBlockType(int x, int y, int z) {
            Material material = getMaterial(x & 0xF, y, z & 0xF);
            DynmapBlockState state = BukkitVersionHelperSpigot26_2.materialToState.get(material);
            return (state != null) ? state : DynmapBlockState.AIR;
        }

        private Material getMaterial(int x, int y, int z) {
            if (getBlockType != null) {
                try {
                    return (Material) getBlockType.invoke(ss, x, y, z);
                } catch (IllegalAccessException | InvocationTargetException | ClassCastException ignored) {
                }
            }
            return Material.AIR;
        }

        @Override
        public int getBlockSkyLight(int x, int y, int z) {
            return ss.getBlockSkyLight(x & 0xF, y, z & 0xF);
        }

        @Override
        public int getBlockEmittedLight(int x, int y, int z) {
            return ss.getBlockEmittedLight(x & 0xF, y, z & 0xF);
        }

        @Override
        public int getHighestBlockYAt(int x, int z) {
            return ss.getHighestBlockYAt(x, z);
        }

        @Override
        public Biome getBiome(int x, int z) {
            return ss.getBiome(x & 0xF, z & 0xF);
        }

        @Override
        public boolean isSectionEmpty(int sy) {
            return ss.isSectionEmpty(sy);
        }

        @Override
        public Object[] getBiomeBaseFromSnapshot() {
            return BukkitVersionHelperSpigot26_2.helper.getBiomeBaseFromSnapshot(ss);
        }
    }

    private static Method getSnapshotBlockTypeMethod(ChunkSnapshot ss) {
        try {
            return ss.getClass().getMethod("getBlockType", int.class, int.class, int.class);
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    @Override
    public Snapshot wrapChunkSnapshot(ChunkSnapshot css) {
        return new WrappedSnapshot26_2(css);
    }

    @Override
    public int loadChunks(final int max_to_load) {
        if (Bukkit.isPrimaryThread()) {
            return super.loadChunks(max_to_load);
        }
        Plugin plugin = Bukkit.getPluginManager().getPlugin("dynmap");
        if ((plugin == null) || !plugin.isEnabled()) {
            return 0;
        }
        Future<Integer> future = Bukkit.getScheduler().callSyncMethod(plugin, () -> super.loadChunks(max_to_load));
        try {
            return future.get();
        } catch (InterruptedException ix) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException ex) {
            Log.warning("Error loading chunks on server thread for Paper 26.2", ex);
        }
        return 0;
    }
}
