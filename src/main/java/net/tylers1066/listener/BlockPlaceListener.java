package net.tylers1066.listener;

import net.tylers1066.db.Deadbolt;
import net.tylers1066.util.EnhancedSign;
import net.tylers1066.util.Util;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        Block b = e.getBlockPlaced();
        Player p = e.getPlayer();

        // Disallow placing signs on curent deadbolt signs
        if(Util.isWallSign(e.getBlockAgainst().getType())) {
            Sign sign = (new EnhancedSign(e.getBlockAgainst())).getSign();
            if(sign != null && Util.isValidHeader(sign)) {
                e.setCancelled(true);
                return;
            }
        }


        Deadbolt db = new Deadbolt(b);

        if(!db.isProtected() || db.isOwner(p))
            return;


    }
}
