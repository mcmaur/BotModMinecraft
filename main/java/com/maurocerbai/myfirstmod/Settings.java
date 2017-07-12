package com.maurocerbai.myfirstmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;

public class Settings {
	public static final String MODID = "examplemod";
	public static final String VERSION = "1.0";

	static Minecraft minecraft = Minecraft.getMinecraft();
	static World world = minecraft.world;
	
	static final String COMMANDS_NEAREST_ORE = "!no";

}
