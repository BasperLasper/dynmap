package org.dynmap.bukkit.helper.v26_2;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.dynmap.DynmapChunk;
import org.dynmap.bukkit.helper.BukkitMaterial;
import org.dynmap.bukkit.helper.BukkitVersionHelper;
import org.dynmap.bukkit.helper.BukkitWorld;
import org.dynmap.renderer.DynmapBlockState;
import org.dynmap.utils.MapChunkCache;
import org.dynmap.utils.Polygon;

/**
 * Bukkit API-only fallback for the requested 26.2 target.
 *
 * Spigot does not currently publish 1.26.2 BuildTools metadata or Maven
 * artifacts, so this helper intentionally avoids nonexistent v1_26_R1
 * CraftBukkit/NMS classes. Replace this with a mapped helper once upstream
 * 1.26.2 artifacts exist.
 */
public class BukkitVersionHelperSpigot26_2 extends BukkitVersionHelper {
    private static final int SYNTHETIC_STATE_COUNT = 128;
    static final Map<Material, DynmapBlockState> materialToState = new EnumMap<Material, DynmapBlockState>(Material.class);

    @Override
    public boolean isUnsafeAsync() {
        return false;
    }

    @Override
    protected boolean isBiomeBaseListNeeded() {
        return false;
    }

    @Override
    public Object[] getBiomeBaseList() {
        return Biome.values();
    }

    @Override
    public float getBiomeBaseTemperature(Object bb) {
        return 0.5F;
    }

    @Override
    public float getBiomeBaseHumidity(Object bb) {
        return 0.5F;
    }

    @Override
    public String getBiomeBaseIDString(Object bb) {
        return (bb == null) ? null : bb.toString().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getBiomeBaseResourceLocsation(Object bb) {
        String id = getBiomeBaseIDString(bb);
        return (id == null || id.indexOf(':') >= 0) ? id : "minecraft:" + id;
    }

    @Override
    public int getBiomeBaseID(Object bb) {
        return (bb instanceof Biome) ? ((Biome) bb).ordinal() : -1;
    }

    @Override
    public Object getUnloadQueue(World world) {
        return null;
    }

    @Override
    public boolean isInUnloadQueue(Object unloadqueue, int x, int z) {
        return false;
    }

    @Override
    public Object[] getBiomeBaseFromSnapshot(ChunkSnapshot css) {
        Object[] biomes = new Object[256];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                biomes[(z << 4) | x] = css.getBiome(x, z);
            }
        }
        return biomes;
    }

    @Override
    public long getInhabitedTicks(Chunk c) {
        return 0;
    }

    @Override
    public Map<?, ?> getTileEntitiesForChunk(Chunk c) {
        return Collections.emptyMap();
    }

    @Override
    public int getTileEntityX(Object te) {
        return (te instanceof BlockState) ? ((BlockState) te).getX() : 0;
    }

    @Override
    public int getTileEntityY(Object te) {
        return (te instanceof BlockState) ? ((BlockState) te).getY() : 0;
    }

    @Override
    public int getTileEntityZ(Object te) {
        return (te instanceof BlockState) ? ((BlockState) te).getZ() : 0;
    }

    @Override
    public Object readTileEntityNBT(Object te, World world) {
        return null;
    }

    @Override
    public Object getFieldValue(Object nbt, String field) {
        return null;
    }

    @Override
    public void unloadChunkNoSave(World w, Chunk c, int cx, int cz) {
        w.unloadChunk(cx, cz, false, false);
    }

    @Override
    public String[] getBlockNames() {
        Material[] values = Material.values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].name().toLowerCase(Locale.ROOT);
        }
        return names;
    }

    @Override
    public String[] getBiomeNames() {
        Biome[] values = Biome.values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].name().toLowerCase(Locale.ROOT);
        }
        return names;
    }

    @Override
    public Player[] getOnlinePlayers() {
        return Bukkit.getServer().getOnlinePlayers().toArray(new Player[0]);
    }

    @Override
    public double getHealth(Player p) {
        return p.getHealth();
    }

    @Override
    public Polygon getWorldBorder(World world) {
        WorldBorder wb = world.getWorldBorder();
        if (wb == null) {
            return null;
        }
        Location c = wb.getCenter();
        double size = wb.getSize();
        if (size <= 1 || size >= 1E7) {
            return null;
        }
        size = size / 2;
        Polygon p = new Polygon();
        p.addVertex(c.getX() - size, c.getZ() - size);
        p.addVertex(c.getX() + size, c.getZ() - size);
        p.addVertex(c.getX() + size, c.getZ() + size);
        p.addVertex(c.getX() - size, c.getZ() + size);
        return p;
    }

    @Override
    public BukkitMaterial[] getMaterialList() {
        Material[] values = Material.values();
        BukkitMaterial[] materials = new BukkitMaterial[values.length];
        for (int i = 0; i < values.length; i++) {
            materials[i] = new BukkitMaterial(values[i].name(), values[i].isSolid(), false);
        }
        return materials;
    }

    @Override
    public void initializeBlockStates() {
        materialToState.clear();
        DynmapBlockState.Builder bld = new DynmapBlockState.Builder();
        for (Material material : Material.values()) {
            String blockName = material.name().toLowerCase(Locale.ROOT);
            DynmapBlockState base = null;
            for (int state = 0; state < SYNTHETIC_STATE_COUNT; state++) {
                bld.setBaseState(base)
                    .setStateIndex(state)
                    .setBlockName(blockName)
                    .setStateName("meta=" + state)
                    .setMaterial(material.name())
                    .setLegacyBlockID(material.ordinal() + (1 << 20))
                    .setAttenuatesLight(material.isSolid() ? 15 : 0);
                if (material == Material.AIR) {
                    bld.setAir();
                }
                if (material.isSolid()) {
                    bld.setSolid();
                }
                DynmapBlockState dbs = bld.build();
                if (base == null) {
                    base = dbs;
                    materialToState.put(material, dbs);
                }
            }
        }
    }

    @Override
    public MapChunkCache getChunkCache(BukkitWorld dw, List<DynmapChunk> chunks) {
        MapChunkCache26_2 c = new MapChunkCache26_2();
        c.setChunks(dw, chunks);
        return c;
    }

    @Override
    public String getStateStringByCombinedId(int blkid, int meta) {
        return "meta=" + meta;
    }

    @Override
    public boolean useGenericCache() {
        return false;
    }
}
