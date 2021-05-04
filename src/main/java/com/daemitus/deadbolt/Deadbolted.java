package com.daemitus.deadbolt;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.TrapDoor;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Deadbolted {

    private Set<Block> blocks = new HashSet<>();
    private Set<Block> traversed = new HashSet<>();
    private String owner = null;
    private Set<String> users = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    public static DeadboltPlugin plugin;

    public Deadbolted(Block block) {
        search(block);
    }

    private void search(Block block) {

        switch (block.getType()) {
            case AIR:
                break;
            case OAK_WALL_SIGN:
            case BIRCH_WALL_SIGN:
            case ACACIA_WALL_SIGN:
            case DARK_OAK_WALL_SIGN:
            case JUNGLE_WALL_SIGN:
            case SPRUCE_WALL_SIGN:
                BlockState state = block.getState();
                org.bukkit.block.Sign signState = (Sign) state;
                if (Deadbolt.getLanguage().isValidWallSign(signState)) {
                    search(Util.getSignAttached(signState));
                }
                break;
            case OAK_DOOR:
            case IRON_DOOR:
            case SPRUCE_DOOR:
            case BIRCH_DOOR:
            case JUNGLE_DOOR:
            case ACACIA_DOOR:
            case DARK_OAK_DOOR:
                searchDoor(block, true, true);
                break;
            case OAK_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case ACACIA_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case SPRUCE_FENCE_GATE:
                searchFenceGate(block, true, true);
                break;
            case OAK_TRAPDOOR:
            case SPRUCE_TRAPDOOR:
            case BIRCH_TRAPDOOR:
            case JUNGLE_TRAPDOOR:
            case ACACIA_TRAPDOOR:
            case DARK_OAK_TRAPDOOR:
            case IRON_TRAPDOOR:
                searchTrapDoor(block, true, Deadbolt.getConfig().vertical_trapdoors);
                break;
            case DISPENSER:
                searchSimpleBlock(block, Deadbolt.getConfig().group_dispensers, Deadbolt.getConfig().group_dispensers);
                break;
            case BREWING_STAND:
                searchSimpleBlock(block, Deadbolt.getConfig().group_brewing_stands, Deadbolt.getConfig().group_brewing_stands);
                break;
            case ENCHANTING_TABLE:
                searchSimpleBlock(block, Deadbolt.getConfig().group_enchantment_tables, Deadbolt.getConfig().group_enchantment_tables);
                break;
            case CAULDRON:
                searchSimpleBlock(block, Deadbolt.getConfig().group_cauldrons, Deadbolt.getConfig().group_cauldrons);
                break;
            case FURNACE:
            case BLAST_FURNACE:
            case SMOKER:
                searchFurnace(block, Deadbolt.getConfig().group_furnaces, Deadbolt.getConfig().group_furnaces);
                break;
            case BARREL:
                searchSimpleBlock(block, Deadbolt.getConfig().group_barrels, Deadbolt.getConfig().group_barrels);
                break;
            case TRAPPED_CHEST:
            case CHEST:
                searchChest(block, true, false);
                break;
            default:
                for (BlockFace bf : Deadbolt.getConfig().CARDINAL_FACES) {
                    Block adjacent = block.getRelative(bf);
                    if (adjacent.getBlockData() instanceof TrapDoor) {
                        Block hinge = adjacent.getRelative(((TrapDoor) adjacent.getBlockData()).getFacing().getOppositeFace());
                        if (hinge.equals(block)) {
                            search(adjacent);
                        }
                    }
                }
                Block adjacentUp = block.getRelative(BlockFace.UP);
                switch (adjacentUp.getType()) {
                    // adjacentUp.getState().getData() instanceof Door no longer works for new doors
                    case OAK_DOOR:
                    case IRON_DOOR:
                    case SPRUCE_DOOR:
                    case BIRCH_DOOR:
                    case JUNGLE_DOOR:
                    case ACACIA_DOOR:
                    case DARK_OAK_DOOR:
                        search(adjacentUp);
                        break;
                }
                Block adjacentDown = block.getRelative(BlockFace.DOWN);
                switch (adjacentDown.getType()) {
                    // adjacentUp.getState().getData() instanceof Door no longer works for new doors
                    case OAK_DOOR:
                    case IRON_DOOR:
                    case SPRUCE_DOOR:
                    case BIRCH_DOOR:
                    case JUNGLE_DOOR:
                    case ACACIA_DOOR:
                    case DARK_OAK_DOOR:
                        search(adjacentDown);
                        break;
                }
        }
    }

    private void searchDoor(Block block, boolean horizontal, boolean vertical) {
        if (!add(block)) {
            return;
        }
        for (BlockFace bf : Deadbolt.getConfig().CARDINAL_FACES) {
            Block adjacent = block.getRelative(bf);
            if (horizontal && adjacent.getType().equals(block.getType())) {
                searchDoor(adjacent, true, vertical);
            } else if (Tag.WALL_SIGNS.isTagged(adjacent.getType())) {
                parseSignAttached(adjacent, block);
            }
        }
        if (vertical) {
            Block adjacentUp = block.getRelative(BlockFace.UP);
            if (adjacentUp.getType().equals(block.getType())) {
                searchDoor(adjacentUp, horizontal, true);
            } else {
                parseNearbySigns(adjacentUp);
            }
            //Get the base block, regardless of type
            Block adjacentDown = block.getRelative(BlockFace.DOWN);
            if (adjacentDown.getType().equals(block.getType())) {
                searchDoor(adjacentDown, horizontal, true);
            } else {
                parseNearbySigns(adjacentDown);
                add(adjacentDown);
            }
        }
    }

    private void searchFenceGate(Block block, boolean horizontal, boolean vertical) {
        if (!add(block)) {
            return;
        }
        for (BlockFace bf : Deadbolt.getConfig().CARDINAL_FACES) {
            Block adjacent = block.getRelative(bf);
            if (horizontal && adjacent.getBlockData() instanceof Gate) {
                searchFenceGate(adjacent, true, vertical);
            } else if (Tag.WALL_SIGNS.isTagged(adjacent.getType())) {
                parseSignAttached(adjacent, block);
            } else {
                parseNearbySigns(adjacent);
            }
        }
        if (vertical) {
            for (BlockFace bf : Deadbolt.getConfig().VERTICAL_FACES) {
                Block adjacent = block.getRelative(bf);
                if(adjacent.getBlockData() instanceof Gate) {
                    searchFenceGate(adjacent, horizontal, true);
                }
            }
        }
    }

    private void searchTrapDoor(Block block, boolean horizontal, boolean vertical) {
        if (!add(block)) {
            return;
        }
        Block hinge = block.getRelative(((TrapDoor) block.getBlockData()).getFacing().getOppositeFace());
        parseNearbySigns(hinge);
        add(hinge);
        for (BlockFace bf : Deadbolt.getConfig().CARDINAL_FACES) {
            Block adjacent = block.getRelative(bf);
            if (horizontal && adjacent.getState().getBlockData() instanceof TrapDoor) {
                searchTrapDoor(adjacent, true, vertical);
            } else if (Tag.WALL_SIGNS.isTagged(adjacent.getType())) {
                parseSignAttached(adjacent, block);
            }
        }
        if (vertical) {
            for (BlockFace bf : Deadbolt.getConfig().VERTICAL_FACES) {
                Block adjacent = block.getRelative(bf);
                if (adjacent.getBlockData() instanceof TrapDoor) {
                    searchTrapDoor(adjacent, horizontal, true);
                } else if (Tag.WALL_SIGNS.isTagged(adjacent.getType())) {
                    BlockState state = adjacent.getState();
                    WallSign signData = (WallSign) state.getBlockData();
                    Block attached = adjacent.getRelative(signData.getFacing().getOppositeFace());
                    if (parseSign((Sign) state)) {
                        add(adjacent, attached);
                    }
                }
            }
        }
    }

    private void searchSimpleBlock(Block block, boolean horizontal, boolean vertical) {
        if (!add(block)) {
            return;
        }
        for (BlockFace bf : Deadbolt.getConfig().CARDINAL_FACES) {
            Block adjacent = block.getRelative(bf);
            if (horizontal && adjacent.getType().equals(block.getType())) {
                searchSimpleBlock(adjacent, true, vertical);
            } else if (Tag.WALL_SIGNS.isTagged(adjacent.getType())) {
                parseSignAttached(adjacent, block);
            }
        }
        if (vertical) {
            for (BlockFace bf : Deadbolt.getConfig().VERTICAL_FACES) {
                Block adjacent = block.getRelative(bf);
                if (adjacent.getType().equals(block.getType())) {
                    searchSimpleBlock(adjacent, horizontal, true);
                }
            }
        }
    }

    private void searchFurnace(Block block, boolean horizontal, boolean vertical) {
        if (!add(block)) {
            return;
        }
        for (BlockFace bf : Deadbolt.getConfig().CARDINAL_FACES) {
            Block adjacent = block.getRelative(bf);
            if (horizontal && adjacent.getState() instanceof Furnace) {
                searchFurnace(adjacent, true, vertical);
            } else if (Tag.WALL_SIGNS.isTagged(adjacent.getType())) {
                parseSignAttached(adjacent, block);
            }
        }
        if (vertical) {
            for (BlockFace bf : Deadbolt.getConfig().VERTICAL_FACES) {
                Block adjacent = block.getRelative(bf);
                if (adjacent.getState() instanceof Furnace) {
                    searchFurnace(adjacent, horizontal, true);
                }
            }
        }
    }

    private void searchChest(Block block, boolean horizontal, boolean vertical) {
        if (!add(block)) {
            return;
        }
        for (BlockFace bf : Deadbolt.getConfig().CARDINAL_FACES) {
            Block adjacent = block.getRelative(bf);
            if (horizontal && adjacent.getState() instanceof Chest) {
                searchChest(adjacent, true, vertical);
            } else if (Tag.WALL_SIGNS.isTagged(adjacent.getType())) {
                parseSignAttached(adjacent, block);
            }
        }
        if (vertical) {
            for (BlockFace bf : Deadbolt.getConfig().VERTICAL_FACES) {
                Block adjacent = block.getRelative(bf);
                if (adjacent.getState() instanceof Chest) {
                    searchChest(adjacent, horizontal, true);
                }
            }
        }
    }

    private void parseNearbySigns(Block block) {
        for (BlockFace bf : Deadbolt.getConfig().CARDINAL_FACES) {
            Block adjacent = block.getRelative(bf);
            if (Tag.WALL_SIGNS.isTagged(adjacent.getType())) {
                parseSignAttached(adjacent, block);
            }
        }
    }

    private void parseSignAttached(Block signBlock, Block attached) {
        Sign sign = (Sign) signBlock.getState();
        WallSign direction = (WallSign) sign.getBlockData();
        if (signBlock.getRelative(direction.getFacing().getOppositeFace()).equals(attached)) {
            if (parseSign(sign)) {
                add(attached, signBlock);
            }
        }
    }

    private boolean parseSign(Sign sign) {
        String ident = Util.getLine(sign, 0);
        if (Deadbolt.getLanguage().isPrivate(ident)) {
            String line1 = Util.getLine(sign, 1);
            owner = line1.isEmpty() ? owner : line1;
            users.add(Util.getLine(sign, 2));
            users.add(Util.getLine(sign, 3));
            return true;
        } else if (Deadbolt.getLanguage().isMoreUsers(ident)) {
            users.add(Util.getLine(sign, 1));
            users.add(Util.getLine(sign, 2));
            users.add(Util.getLine(sign, 3));
            return true;
        }
        return false;
    }

    public boolean isProtected() {
        return owner != null && !owner.isEmpty();
    }

    public boolean isOwner(Player player) {
        return isProtected() && Util.signNameEqualsPlayerName(owner, player.getName());
    }

    public boolean isUser(Player player) {
        if (isOwner(player) || isEveryone()) {
            return true;
        } else {
            for (String user : users) {
                if (Util.signNameEqualsPlayerName(user, player.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isEveryone() {
        for (String line : users) {
            if (Deadbolt.getLanguage().isEveryone(line)) {
                return true;
            }
        }
        return false;
    }

    public int getTimer() {
        for (String line : users) {
            int timer = Deadbolt.getLanguage().getTimer(line);
            if (timer != -1) {
                return timer;
            }
        }
        return -1;
    }

    private boolean add(Block... block) {
        boolean success = true;
        for (Block b : block) {
            success &= blocks.add(b) && traversed.add(b);
        }
        return success;
    }

    public String getOwner() {
        return owner;
    }

    public Set<String> getUsers() {
        return this.users;
    }

    public Set<Block> getBlocks() {
        return blocks;
    }

    public void toggleDoors(Block block) {
        Set<Block> clickedDoor = new HashSet<>();
        Material type = block.getType();
        boolean open = !((Openable) block.getBlockData()).isOpen();
        for(Block b : blocks) {
            if(b.getType().equals(type)) {
                Openable doorpart = (Openable) b.getBlockData();
                doorpart.setOpen(open);
                b.setBlockData(doorpart, false);
                clickedDoor.add(b);
            }
        }
        if (!isNaturalOpen(block) && Deadbolt.getConfig().silent_door_sounds) {
            block.getWorld().playSound(block.getLocation(),
                    open ? Sound.BLOCK_IRON_DOOR_OPEN : Sound.BLOCK_IRON_DOOR_CLOSE,
                    SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        if (Deadbolt.getConfig().deny_timed_doors) {
            return;
        }
        int delay = getTimer();
        if (delay == -1) {
            if (Deadbolt.getConfig().forced_timed_doors) {
                delay = Deadbolt.getConfig().forced_timed_doors_delay;
            } else {
                return;
            }
        }
        boolean runonce = true;
        for(Block b : clickedDoor) {
            if(ToggleDoorTask.timedBlocks.add(b)) {
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new ToggleDoorTask(b,
                                (runonce && Deadbolt.getConfig().timed_door_sounds && (isNaturalOpen(b) || Deadbolt.getConfig().silent_door_sounds)),
                                !open),
                        delay * 20);
                runonce = false;
            } else {
                ToggleDoorTask.timedBlocks.remove(b);
            }
        }
    }

    private boolean isNaturalOpen(Block block) {
        switch (block.getType()) {
            case IRON_DOOR:
            case IRON_TRAPDOOR:
                return false;
            default:
                return true;
        }
    }

    /**
     * The purpose of this is to let protections auto-expire if the owner did
     * not play for the last X days.
     *
     * @param playerToInform
     * @return
     */
    public boolean isAutoExpired(Player playerToInform) {
        // Are we even supposed to use the auto-expire feature?
        // Is the feature perhaps disabled in the configuration?
        if (Deadbolt.getConfig().auto_expire_days <= 0) {
            return false;
        }

        // Fetch the owner string
        String signPlayerName = this.getOwner();

        // That must be a valid player name
        if (!PlayerNameUtil.isValidPlayerName(signPlayerName)) {
            return false;
        }

        // What time is it?
        long now = System.currentTimeMillis();

        // This is an unwanted necessity due to sign lines being one char to short.
        // More than one player name could cover for the auto expire.
        // Find all those valid owners.
        Set<String> allValidOwnerNames = PlayerNameUtil.interpretPlayerNameFromSign(signPlayerName);

        // At least one of them needs to have been online recently
        boolean hasExpired = true;
        long daysTillExpire = 0;
        String nameThatCovered = null;
        for (String validOwnerName : allValidOwnerNames) {
            long lastPlayed = PlayerNameUtil.getLastPlayed(validOwnerName);
            long millisSinceLastPlayed = now - lastPlayed;
            long daysSinceLastPlayed = (long) Math.floor(millisSinceLastPlayed / (1000 * 60 * 60 * 24));
            daysTillExpire = Deadbolt.getConfig().auto_expire_days - daysSinceLastPlayed;
            //log(validOwnerName, "lastPlayed", lastPlayed, "millisSinceLastPlayed", millisSinceLastPlayed, "daysSinceLastPlayed", daysSinceLastPlayed, "daysTillExpire", daysTillExpire);
            if (daysTillExpire > 0) {
                nameThatCovered = validOwnerName;
                //log("This name covered for it!", nameThatCovered);
                hasExpired = false;
                break;
            }
        }

        if (hasExpired) {
            if (playerToInform != null && !playerToInform.getName().equalsIgnoreCase(nameThatCovered)) {
                Deadbolt.getConfig().sendMessage(playerToInform, ChatColor.RED, Deadbolt.getLanguage().msg_auto_expire_expired);
            }
        } else {
            if (playerToInform != null && !playerToInform.getName().equalsIgnoreCase(nameThatCovered)) {
                Deadbolt.getConfig().sendMessage(playerToInform, ChatColor.YELLOW, Deadbolt.getLanguage().msg_auto_expire_owner_x_days, nameThatCovered, String.valueOf(daysTillExpire));
            }
        }

        return hasExpired;
    }

    public boolean isAutoExpired() {
        return this.isAutoExpired(null);
    }
}
