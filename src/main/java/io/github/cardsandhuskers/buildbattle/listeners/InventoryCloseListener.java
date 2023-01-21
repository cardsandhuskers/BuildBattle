package io.github.cardsandhuskers.buildbattle.listeners;

import io.github.cardsandhuskers.buildbattle.handlers.VotingInventoryHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryCloseListener implements Listener {
    VotingInventoryHandler votingInventoryHandler;
    public InventoryCloseListener(VotingInventoryHandler votingInventoryHandler) {
        this.votingInventoryHandler = votingInventoryHandler;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if(ChatColor.stripColor(e.getView().getTitle()).equalsIgnoreCase("Vote for the Theme!")) {
            //System.out.println("CLOSE");
            votingInventoryHandler.closeMenu((Player) e.getPlayer());
        }
    }
}
