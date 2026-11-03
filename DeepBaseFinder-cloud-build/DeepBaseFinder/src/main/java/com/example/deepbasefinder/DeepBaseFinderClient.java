package com.example.deepbasefinder;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Util;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.block.Blocks;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DeepBaseFinderClient implements ClientModInitializer {
    private static final int HORIZONTAL_RADIUS = 24;
    private static final int MAX_RESULTS = 40;
    private static final int SCAN_INTERVAL_TICKS = 100;
    private static KeyBinding scanKey;
    private static long lastScan = 0;

    @Override
    public void onInitializeClient() {
        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("deep_base_finder", "general"));
        scanKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.deep_base_finder.scan", GLFW.GLFW_KEY_O, category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (scanKey.wasPressed()) scan(client);
        });
    }

    private static void scan(MinecraftClient client) {
        if (client.player == null || client.world == null) return;
        long now = Util.getMeasuringTimeMs();
        if (now - lastScan < 1500) return;
        lastScan = now;

        BlockPos origin = client.player.getBlockPos();
        int minY = client.world.getBottomY();
        int maxY = client.world.getTopY();
        int minX = origin.getX() - HORIZONTAL_RADIUS;
        int maxX = origin.getX() + HORIZONTAL_RADIUS;
        int minZ = origin.getZ() - HORIZONTAL_RADIUS;
        int maxZ = origin.getZ() + HORIZONTAL_RADIUS;

        List<BlockPos> found = new ArrayList<>();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (!client.world.isChunkLoaded(x >> 4, z >> 4)) continue;
                for (int y = minY; y < maxY; y++) {
                    pos.set(x, y, z);
                    BlockState state = client.world.getBlockState(pos);
                    Block block = state.getBlock();
                    if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST ||
                            block == Blocks.BARREL || block instanceof ShulkerBoxBlock) {
                        found.add(pos.toImmutable());
                    }
                }
            }
        }

        found.sort(Comparator.comparingDouble(p -> p.getSquaredDistance(origin)));
        client.player.sendMessage(Text.literal("§6[Deep Base Finder] §fScan complete: §e" + found.size()
                + "§f storage block(s) found in loaded chunks, Y " + minY + " to " + (maxY - 1) + "."), false);

        int shown = Math.min(found.size(), MAX_RESULTS);
        for (int i = 0; i < shown; i++) {
            BlockPos p = found.get(i);
            String id = Registries.BLOCK.getId(client.world.getBlockState(p).getBlock()).toString();
            client.player.sendMessage(Text.literal("§b" + id + " §7at §f" + p.getX() + " " + p.getY() + " " + p.getZ()), false);
        }
        if (found.size() > shown) {
            client.player.sendMessage(Text.literal("§7Showing nearest " + shown + " of " + found.size() + " results."), false);
        }
        if (found.isEmpty()) {
            client.player.sendMessage(Text.literal("§7No visible storage blocks found. Try exploring so more chunks are loaded."), false);
        }
    }
}
