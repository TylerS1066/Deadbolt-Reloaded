package com.daemitus.deadbolt;

import com.md_5.config.AnnotatedConfig;
import com.md_5.config.ConfigComment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class Config extends AnnotatedConfig {

    //------------------------------------------------------------------------//
    @ConfigComment("File to load for messages")
    public String language = "english.yml";
    @ConfigComment("Protections will auto-expire if the player is offline for X days. 0 means never expire.")
    public int auto_expire_days = 0;
    @ConfigComment("Allows trapdoors to link with each other vertically")
    public boolean vertical_trapdoors = true;
    @ConfigComment("Allows furnaces to act like chests, one sign for all connected blocks. Includes smokers and blast furnaces.")
    public boolean group_furnaces = true;
    @ConfigComment("Allows dispensers to act like chests, one sign for all connected blocks")
    public boolean group_dispensers = true;
    @ConfigComment("Allows cauldrons to act like chests, one sign for all connected blocks")
    public boolean group_cauldrons = true;
    @ConfigComment("Allows enchantment tables to act like chests, one sign for all connected blocks")
    public boolean group_enchantment_tables = true;
    @ConfigComment("Allows brewing stands to act like chests, one sign for all connected blocks")
    public boolean group_brewing_stands = true;
    @ConfigComment("Allows barrels to act like chests, one sign for all connected blocks")
    public boolean group_barrels = true;
    @ConfigComment("Allows right click placement of signs automatically on the target")
    public boolean deny_quick_signs = false;
    @ConfigComment("List of blockIDs that can be quick sign protected")
    public List<String> quick_signs_blockids = Arrays.asList(
            Material.CHEST.name(),
            Material.TRAPPED_CHEST.name()
    );
    @ConfigComment("Clear sign selection after using /deadbolt <line> <text>")
    public boolean clear_sign_selection = false;
    @ConfigComment("Denies things such as snowmen opening doors")
    public boolean deny_entity_interact = true;
    @ConfigComment("Denies explosions from breaking protected blocks")
    public boolean deny_explosions = true;
    @ConfigComment("Denies pistons from breaking protected blocks")
    public boolean deny_pistons = true;
    @ConfigComment("Denies redstone from toggling protected blocks")
    public boolean deny_redstone = true;
    @ConfigComment("Denies Hopper Minecart from interacting with protected blocks")
    public boolean deny_hoppercart = true;
    @ConfigComment("List of blockIDs protected by redstone unless overrode by [everyone]")
    public List<String> redstone_protected_blockids = Arrays.asList(
            // Doors
            Material.OAK_DOOR.name(),
            Material.IRON_DOOR.name(),
            Material.SPRUCE_DOOR.name(),
            Material.BIRCH_DOOR.name(),
            Material.JUNGLE_DOOR.name(),
            Material.ACACIA_DOOR.name(),
            Material.DARK_OAK_DOOR.name(),
            // trap doors
            Material.OAK_TRAPDOOR.name(),
            Material.SPRUCE_TRAPDOOR.name(),
            Material.BIRCH_TRAPDOOR.name(),
            Material.JUNGLE_TRAPDOOR.name(),
            Material.ACACIA_TRAPDOOR.name(),
            Material.DARK_OAK_TRAPDOOR.name(),
            Material.IRON_TRAPDOOR.name()
    );
    @ConfigComment("Denies function of the [timer: x] tag on signs")
    public boolean deny_timed_doors = false;
    @ConfigComment("Forces timed doors on every protected (trap)door")
    public boolean forced_timed_doors = false;
    @ConfigComment("Default delay used with forced timed doors")
    public int forced_timed_doors_delay = 3;
    @ConfigComment("Enables sound effects on timed doors")
    public boolean timed_door_sounds = true;
    @ConfigComment("Gives traditional wood door sounds to silent ones (iron doors)")
    public boolean silent_door_sounds = true;
    @ConfigComment({"Standard multiplayer color scheme",
        "0 Black          6 Gold           c Red",
        "1 Dark Blue      7 Gray           d Pink",
        "2 Dark Green     8 Dark Gray      e Yellow",
        "3 Teal           9 Blue           f White",
        "4 Dark Red       a Bright Green",
        "5 Purple         b Aqua"})
    public String default_colors_private_line_1 = "0";
    public String default_colors_private_line_2 = "0";
    public String default_colors_private_line_3 = "0";
    public String default_colors_private_line_4 = "0";
    public String default_colors_moreusers_line_1 = "0";
    public String default_colors_moreusers_line_2 = "0";
    public String default_colors_moreusers_line_3 = "0";
    public String default_colors_moreusers_line_4 = "0";
    //------------------------------------------------------------------------//
    private final transient String TAG = "Deadbolt: ";
    //------------------------------------------------------------------------//
    public transient Map<Player, Block> selectedSign = new HashMap<>();
    //------------------------------------------------------------------------//
    public final transient Set<BlockFace> CARDINAL_FACES = EnumSet.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);
    public final transient Set<BlockFace> VERTICAL_FACES = EnumSet.of(BlockFace.UP, BlockFace.DOWN);
    //------------------------------------------------------------------------//
    // TODO: This has nothing to do with configuration. Should be placed somewhere else 

    public void sendMessage(CommandSender sender, ChatColor color, String message, String... args) {
        sender.sendMessage(color + TAG + String.format(message, (Object[]) args));
    }

    // TODO: This has nothing to do with configuration. Should be placed somewhere else
    public void sendBroadcast(String permission, ChatColor color, String message, String... args) {
        Bukkit.getServer().broadcast(color + TAG + String.format(message, (Object[]) args), permission);
    }
}
