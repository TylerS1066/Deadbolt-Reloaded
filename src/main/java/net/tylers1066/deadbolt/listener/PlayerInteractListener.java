package net.tylers1066.deadbolt.listener;

import net.tylers1066.deadbolt.DeadboltReloaded;
import net.tylers1066.deadbolt.db.Deadbolt;
import net.tylers1066.deadbolt.selection.Selection;
import net.tylers1066.deadbolt.selection.SelectionManager;
import net.tylers1066.deadbolt.util.EnhancedSign;
import net.tylers1066.deadbolt.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (!handle(e.getPlayer(), e.getClickedBlock()))
            return;

        e.setUseInteractedBlock(Event.Result.DENY);
        e.setUseItemInHand(Event.Result.DENY);
    }

    private boolean handle(Player p, Block b) {
        Material type = b.getType();
        if (Util.isWallSign(type))
            return handleSign(p, b);
        else if(Util.isDoor(type) || Util.isTrapdoor(type) || Util.isGate(type))
            return handleOpenable(p, b);
        else if(Util.isChest(type) || Util.isFurnace(type) || Util.isDispenser(type)
                || Util.isDropper(type) || Util.isOtherContainer(type))
            return handleContainer(p, b);
        return false;
    }

    private boolean handleSign(Player p, Block b) {
        Deadbolt db = new Deadbolt(b);

        if (!db.isProtected())
            return false;

        if (!db.isOwner(p)) {
            if(p.hasPermission("deadbolt.admin.commands")) {
                SelectionManager.add(p, new Selection(new EnhancedSign(b), db));
                p.sendMessage(ChatColor.RED + "(Admin) Warning, selected a sign owned by " + db.getOwner());
                return true;
            }
            p.sendMessage("You don't own this sign");
            return false; // Deny if not owner and not admin
        }

        SelectionManager.add(p, new Selection(new EnhancedSign(b), db));
        p.sendMessage("Sign selected, use /deadbolt <line number> <text>");
        return true;
    }

    private boolean handleOpenable(Player p, Block b) {
        Deadbolt db = new Deadbolt(b);

        if (!db.isProtected())
            return false;

        if (db.isMember(p)) {
            db.toggle();
            return true;
        }
        if (p.hasPermission("deadbolt.admin.bypass")) {
            db.toggle();
            DeadboltReloaded.getInstance().getLogger().info(ChatColor.RED + "(Admin) " + ChatColor.RESET + p.getDisplayName() + ChatColor.RED + " bypassed a block owned by " + db.getOwner());
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.hasPermission("deadbolt.broadcast.bypass"))
                    other.sendMessage(ChatColor.RED + "(Admin) " + ChatColor.RESET + p.getDisplayName() + ChatColor.RED + " bypassed a block owned by " + db.getOwner());
            }
            p.sendMessage(ChatColor.RED + "(Admin) Warning, this door is owned by " + db.getOwner() + ", make sure to shut it");
            return true;
        }
        p.sendMessage("Access denied");
        return true;
    }

    private boolean handleContainer(Player p, Block b) {
        Deadbolt db = new Deadbolt(b);

        if (!db.isProtected())
            return false;

        if (db.isMember(p))
            return false;

        if (!p.hasPermission("deadbolt.admin.snoop")) {
            // Deny if not member or not admin
            p.sendMessage("Access denied");
            return true;
        }

        DeadboltReloaded.getInstance().getLogger().info(ChatColor.RED + "(Admin) " + ChatColor.RESET + p.getDisplayName() + ChatColor.RED + " opened a container owned by " + db.getOwner());
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other.hasPermission("deadbolt.broadcast.snoop"))
                other.sendMessage(ChatColor.RED + "(Admin) " + ChatColor.RESET + p.getDisplayName() + ChatColor.RED + " opened a container owned by " + db.getOwner());
        }
        return false;
    }
}
