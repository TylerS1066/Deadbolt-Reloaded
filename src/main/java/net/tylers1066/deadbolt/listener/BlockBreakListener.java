package net.tylers1066.deadbolt.listener;

import net.tylers1066.deadbolt.DeadboltReloaded;
import net.tylers1066.deadbolt.db.Deadbolt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        Player p = e.getPlayer();
        Deadbolt db = new Deadbolt(b);

        if (!db.isProtected() || db.isOwner(p))
            return;

        if (p.hasPermission("deadbolt.admin.break")) {
            DeadboltReloaded.getInstance().getLogger().info(ChatColor.RED + "(Admin) " + ChatColor.RESET + p.getDisplayName() + ChatColor.RED + " broke a block owned by " + db.getOwner());
            for (Player other : Bukkit.getOnlinePlayers()) {
                if(other.hasPermission("deadbolt.broadcast.break"))
                    other.sendMessage(ChatColor.RED + "(Admin) " + ChatColor.RESET + p.getDisplayName() + ChatColor.RED + " broke a block owned by " + db.getOwner());
            }
            return;
        }

        p.sendMessage("You don't own this block");
        e.setCancelled(true);
    }
}
