package com.daemitus.deadbolt;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;

public class ToggleDoorTask implements Runnable {

    public static Set<Block> timedBlocks = new HashSet<>();
    private final Block block;
    private final boolean sound;
    private final boolean open;

    public ToggleDoorTask(Block block, boolean sound, boolean open) {
        this.block = block;
        this.sound = sound;
        this.open = open;
    }

    public void run() {
        if (timedBlocks.remove(block)) {
            Openable door = (Openable) block.getBlockData();
            door.setOpen(open);
            block.setBlockData(door, false);
            if (sound) {
                block.getWorld().playSound(block.getLocation(),
                        open ? Sound.BLOCK_IRON_DOOR_OPEN : Sound.BLOCK_IRON_DOOR_CLOSE,
                        SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    public static void cleanup() {
        Iterator<Block> iter = timedBlocks.iterator();
        while (iter.hasNext()) {
            Block next = iter.next();
            Openable door = (Openable) next.getBlockData();
            door.setOpen(false);
            next.setBlockData(door, false);
            iter.remove();
        }
    }
}
