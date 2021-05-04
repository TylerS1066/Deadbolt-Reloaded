package com.daemitus.deadbolt.events;

import com.daemitus.deadbolt.Deadbolt;
import com.daemitus.deadbolt.DeadboltPlugin;
import com.daemitus.deadbolt.Deadbolted;
import com.daemitus.deadbolt.Perm;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {

    private final DeadboltPlugin plugin = Deadbolt.getPlugin();

    public BlockListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Deadbolted db = Deadbolt.get(block);

        if (db.isProtected() && !db.isAutoExpired() && !db.isOwner(player)) {
            if (player.hasPermission(Perm.admin_break)) {
                Deadbolt.getConfig().sendBroadcast(Perm.admin_broadcast_break, ChatColor.RED, Deadbolt.getLanguage().msg_admin_break, player.getName(), db.getOwner());
            } else {
                Deadbolt.getConfig().sendMessage(player, ChatColor.RED, Deadbolt.getLanguage().msg_deny_block_break);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        Player player = event.getPlayer();
        Block against = event.getBlockAgainst();

        if (against.getBlockData() instanceof WallSign && Deadbolt.getLanguage().isValidWallSign((Sign) against.getState())) {
            event.setCancelled(true);
            return;
        }
        if (event.getBlock().getType() == Material.HOPPER) {
            Deadbolted d = Deadbolt.get(event.getBlock().getRelative(BlockFace.UP));
            if (d.isProtected() && !d.isOwner(player)) {
                Deadbolt.getConfig().sendMessage(player, ChatColor.RED, Deadbolt.getLanguage().msg_hopper);
                event.setCancelled(true);
            }
            return;
        }
        Deadbolted db = Deadbolt.get(block);
        switch (block.getType()) {
            case BARREL:
            case CHEST:
            case FURNACE:
            case BLAST_FURNACE:
            case SMOKER:
            case CAULDRON:
            case DISPENSER:
            case BREWING_STAND:
            case ENCHANTING_TABLE:
            case ANVIL:
            case ENDER_CHEST:
            case BEACON:
            case DROPPER:
            case TRAPPED_CHEST:
            case HOPPER:
                if (db.isProtected() && !db.isOwner(player)) {
                    event.setCancelled(true);
                    Deadbolt.getConfig().sendMessage(player, ChatColor.RED, Deadbolt.getLanguage().msg_deny_container_expansion);
                }
                return;
            case IRON_DOOR:
            case OAK_DOOR:
            case SPRUCE_DOOR:
            case BIRCH_DOOR:
            case JUNGLE_DOOR:
            case ACACIA_DOOR:
            case DARK_OAK_DOOR:
                if (db.isProtected() && !db.isOwner(player)) {
                    Deadbolt.getConfig().sendMessage(player, ChatColor.RED, Deadbolt.getLanguage().msg_deny_door_expansion);
                    Block upBlock = block.getRelative(BlockFace.UP);
                    block.setType(Material.STONE);
                    block.setType(Material.AIR);
                    upBlock.setType(Material.STONE);
                    upBlock.setType(Material.AIR);
                    event.setCancelled(true);
                }
                return;
            case OAK_TRAPDOOR:
            case SPRUCE_TRAPDOOR:
            case BIRCH_TRAPDOOR:
            case JUNGLE_TRAPDOOR:
            case ACACIA_TRAPDOOR:
            case DARK_OAK_TRAPDOOR:
            case IRON_TRAPDOOR:
                if (db.isProtected() && !db.isOwner(player)) {
                    Deadbolt.getConfig().sendMessage(player, ChatColor.RED, Deadbolt.getLanguage().msg_deny_trapdoor_expansion);
                    event.setCancelled(true);
                }
                return;
            case OAK_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case ACACIA_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
                if (db.isProtected() && !db.isOwner(player)) {
                    Deadbolt.getConfig().sendMessage(player, ChatColor.RED, Deadbolt.getLanguage().msg_deny_fencegate_expansion);
                    event.setCancelled(true);
                }
                return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        Block block = event.getBlock();
        Deadbolted db = Deadbolt.get(block);
        if (db.isProtected()) {
            event.setCancelled(true);
        }
    }

    private String getPermission(Material type) {
        switch (type) {
            case BARREL:
                return Perm.user_create_barrel;
            case CHEST:
                return Perm.user_create_chest;
            case FURNACE:
            case BLAST_FURNACE:
            case SMOKER:
                return Perm.user_create_furnace;
            case CAULDRON:
                return Perm.user_create_cauldron;
            case DISPENSER:
                return Perm.user_create_dispenser;
            case BREWING_STAND:
                return Perm.user_create_brewery;
            case ENCHANTING_TABLE:
                return Perm.user_create_enchant;
            case OAK_DOOR:
            case IRON_DOOR:
            case SPRUCE_DOOR:
            case BIRCH_DOOR:
            case JUNGLE_DOOR:
            case ACACIA_DOOR:
            case DARK_OAK_DOOR:
                return Perm.user_create_door;
            case OAK_TRAPDOOR:
            case SPRUCE_TRAPDOOR:
            case BIRCH_TRAPDOOR:
            case JUNGLE_TRAPDOOR:
            case ACACIA_TRAPDOOR:
            case DARK_OAK_TRAPDOOR:
            case IRON_TRAPDOOR:
                return Perm.user_create_trapdoor;
            case OAK_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case ACACIA_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
                return Perm.user_create_fencegate;
            case ANVIL:
                return Perm.user_create_anvil;
            case ENDER_CHEST:
                return Perm.user_create_ender;
            case BEACON:
                return Perm.user_create_beacon;
            case HOPPER:
                return Perm.user_create_hopper;
            case DROPPER:
                return Perm.user_create_dropper;
            case TRAPPED_CHEST:
                return Perm.user_create_trapped_chest;
            default:
                return null;
        }
    }
}
