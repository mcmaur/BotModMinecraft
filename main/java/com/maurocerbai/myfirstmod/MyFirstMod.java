package com.maurocerbai.myfirstmod;

import java.awt.AWTException;
import java.awt.Robot;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = Settings.MODID, version = Settings.VERSION)
public class MyFirstMod {

	private int SCANLIMIT = 10;
	LinkedList<BlockWithPosition> tobescanned;

	@Instance
	public static MyFirstMod instance = new MyFirstMod();

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		// read your config file, create Blocks, Items, etc. and register them
		// with the GameRegistry.
		JConsole.getInstance().appendINFO("Pre Init mod");
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		// we can build up data structures, add Crafting Recipes and register
		// new handler.
		JConsole.getInstance().appendINFO("Init mod");
		tobescanned = new LinkedList<BlockWithPosition>();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		// Its used to communicate with other mods and adjust your setup based
		// on this
		JConsole.getInstance().appendINFO("Post Init mod");
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(instance);
		JConsole.getInstance().appendINFO("Load mod");
	}

	@EventHandler
	public void onServerStarted(FMLServerStartedEvent event) {
		JConsole.getInstance().appendINFO("Opened world");
	}

	double playerPreviousPosX = 0, playerPreviousPosY = 0,
			playerPreviousPosZ = 0;

	@SubscribeEvent
	public void onLivingUpdateEvent(LivingUpdateEvent event) {

		if (event.getEntity() instanceof EntityPlayerMP) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			// EntityPlayer player = Settings.world.getPlayerPosition();

			// if player has moved print his new position coord
			if (playerPreviousPosX != player.posX
					|| playerPreviousPosY != player.posY
					|| playerPreviousPosZ != player.posZ) {
				DecimalFormat df = new DecimalFormat();
				df.setRoundingMode(RoundingMode.DOWN);
				JConsole.getInstance().appendINFO(
						"player moved to [x=" + df.format(player.posX) + ", y="
								+ df.format(player.posY) + ", z="
								+ df.format(player.posZ) + "]");
			}

			// Update previous position value with current
			playerPreviousPosX = player.posX;
			playerPreviousPosY = player.posY;
			playerPreviousPosZ = player.posZ;
		}
	}

	private boolean isPreciousMaterial(BlockWithPosition considering) {
		int i = Block.getIdFromBlock(considering.block);
		/*
		 * 14-Gold Ore 15-Iron Ore 16-Coal Ore 21-Lapis Lazuli Ore 56-Diamond
		 * Ore 73- Redstone Ore 74-Glowing Redstone Ore 129-Emerald Ore
		 * 153-Nether Quartz Ore
		 */
		if (i == 14 || i == 15 || i == 16 || i == 21 || i == 56 || i == 73
				|| i == 74 || i == 129 || i == 153)
			return true;
		else
			return false;
	}

	@SubscribeEvent
	public void onServerChatEvent(ServerChatEvent event) {
		String msg = event.getMessage();
		JConsole.getInstance().appendINFO("Chat event: " + msg);
		if (msg.equals(Settings.COMMANDS_NEAREST_ORE)) {
			JConsole.getInstance().appendERR("" + findNearestOre());
		}

	}

	public BlockPos findNearestOre() {
		BlockPos targetPosition = null;
		
		// players
		List<EntityPlayer> a = Settings.world.playerEntities;
		JConsole.getInstance().appendERR("" + a.size());

		EntityPlayer player = Settings.world.playerEntities.get(0);
		// block under player feet
		BlockPos underfeet = new BlockPos(player.posX, player.posY - 1,
				player.posZ);
		BlockWithPosition blockunderfeet = new BlockWithPosition(Settings.world
				.getBlockState(underfeet).getBlock(), underfeet);
		JConsole.getInstance().appendINFO(
				"Block under player: " + blockunderfeet);
		tobescanned.add(blockunderfeet);

		int countwhileiteration = 0;
		while (countwhileiteration < SCANLIMIT && !tobescanned.isEmpty()) {
			countwhileiteration++;
			BlockWithPosition considering = tobescanned.removeFirst();
			JConsole.getInstance().appendERR("Scanning: " + considering);

			if (isPreciousMaterial(considering)) {
				// if the material is ore then the position will be saved
				JConsole.getInstance().appendWARN(
						"Materiale prezioso: " + considering);
				targetPosition = considering.pos;
				break;
			} else {
				// add the block nearby to the list of item to be scanned
				BlockPos right = considering.pos.add(0, 0, 1);
				tobescanned.add(new BlockWithPosition(Settings.world
						.getBlockState(right).getBlock(), right));

				BlockPos left = considering.pos.add(0, 0, -1);
				tobescanned.add(new BlockWithPosition(Settings.world
						.getBlockState(left).getBlock(), left));

				BlockPos front = considering.pos.add(1, 0, 0);
				tobescanned.add(new BlockWithPosition(Settings.world
						.getBlockState(front).getBlock(), front));

				BlockPos back = considering.pos.add(-1, 0, 1);
				tobescanned.add(new BlockWithPosition(Settings.world
						.getBlockState(back).getBlock(), back));

				BlockPos up = considering.pos.add(0, 1, 0);
				tobescanned.add(new BlockWithPosition(Settings.world
						.getBlockState(up).getBlock(), up));

				BlockPos down = considering.pos.add(0, -1, 0);
				tobescanned.add(new BlockWithPosition(Settings.world
						.getBlockState(down).getBlock(), down));
			}
		}
		return targetPosition;
	}

	public void emulateKey() {
		// emulate keyboard to move player
		try {
			Robot robot = new Robot();

			// Simulate a mouse click
			// robot.mousePress(InputEvent.BUTTON1_MASK);
			// robot.mouseRelease(InputEvent.BUTTON1_MASK);

			// Simulate a key press
			// robot.keyPress(KeyEvent.VK_W);
			// robot.keyRelease(KeyEvent.VK_W);

		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	private class BlockWithPosition {
		Block block;
		BlockPos pos;

		public BlockWithPosition(Block block, BlockPos pos) {
			super();
			this.block = block;
			this.pos = pos;
		}

		@Override
		public String toString() {
			return "BlockWithPosition [block=" + block + ", pos=" + pos + "]";
		}
	}

}