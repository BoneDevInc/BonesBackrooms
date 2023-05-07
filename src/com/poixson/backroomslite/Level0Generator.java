package com.poixson.backroomslite;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Lightable;


import org.bukkit.block.data.Rotatable;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import com.poixson.utils.FastNoiseLiteD;
import com.poixson.utils.FastNoiseLiteD.CellularDistanceFunction;
import com.poixson.utils.FastNoiseLiteD.CellularReturnType;
import com.poixson.utils.FastNoiseLiteD.FractalType;
import com.poixson.utils.FastNoiseLiteD.NoiseType;
import com.poixson.utils.FastNoiseLiteD.RotationType3D;


public class Level0Generator extends ChunkGenerator {

	public static final double THRESH_WALL_L = 0.38;
	public static final double THRESH_WALL_H = 0.5;

	public static final Material LOBBY_WALL     = Material.SAND;
	public static final Material LOBBY_SUBFLOOR = Material.OAK_PLANKS;
	public static final Material LOBBY_CARPET   = Material.STRIPPED_BIRCH_WOOD;

	protected final int level_y = 80;
	protected final int level_h = 4;

	// noise
	protected final FastNoiseLiteD noiseLobbyWalls;



	public Level0Generator() {
		// lobby walls
		this.noiseLobbyWalls = new FastNoiseLiteD();
		this.noiseLobbyWalls.setFrequency(0.050); //0.022
		this.noiseLobbyWalls.setFractalOctaves(2);
		this.noiseLobbyWalls.setFractalGain(0.1);
		this.noiseLobbyWalls.setFractalLacunarity(0.4);
		this.noiseLobbyWalls.setNoiseType(NoiseType.Cellular);
		this.noiseLobbyWalls.setFractalType(FractalType.PingPong);
		this.noiseLobbyWalls.setFractalPingPongStrength(2.28);
		this.noiseLobbyWalls.setCellularDistanceFunction(CellularDistanceFunction.Manhattan);
		this.noiseLobbyWalls.setCellularReturnType(CellularReturnType.Distance);
		this.noiseLobbyWalls.setRotationType3D(RotationType3D.ImproveXYPlanes);
	}



	@Override
	public void generateSurface(final WorldInfo worldInfo, final Random random,
			final int chunkX, final int chunkZ, final ChunkData chunk) {
		double valueWall;
		boolean isWall;
		int xx, zz;
		final int wh = this.level_h + 4;
		final int cy = this.level_y + this.level_h + 2;
		for (int z=0; z<16; z++) {
			zz = (chunkZ * 16) + z;
			for (int x=0; x<16; x++) {
				xx = (chunkX * 16) + x;
				valueWall = this.noiseLobbyWalls.getNoiseRot(xx, zz, 0.25);
				isWall = (valueWall > THRESH_WALL_L && valueWall < THRESH_WALL_H);
				chunk.setBlock(x, this.level_y, z, Material.BEDROCK);
				chunk.setBlock(x, cy+2,         z, Material.BEDROCK);
				if (isWall) {
					for (int iy=1; iy<wh; iy++) {
						if (iy==2) {chunk.setBlock(x,this.level_y+iy,z,Material.CUT_SANDSTONE);} else {
							chunk.setBlock(x, this.level_y+iy, z, LOBBY_WALL);
						}
					}
				} else {
					chunk.setBlock(x, this.level_y+1, z, LOBBY_CARPET);
					final int  modX7 = (xx < 0 ? 1-xx : xx) % 7;
					final int  modZ7 = (zz < 0 ? 0-zz : zz) % 7;
					if (modZ7 == 0 && modX7 < 2) {
						// ceiling lights
						chunk.setBlock(x, cy, z, Material.OCHRE_FROGLIGHT);
					} else {
						// ceiling
						chunk.setBlock(x, cy, z, Material.STRIPPED_BIRCH_LOG);
						chunk.setBlock(x, cy+1, z, Material.STONE);
					}
				}
			}
		}
	}



	@Override
	public Location getFixedSpawnLocation(final World world, final Random random) {
		final int y = this.level_y + 2;
		return world.getBlockAt(0, y, 0).getLocation();
	}



}
