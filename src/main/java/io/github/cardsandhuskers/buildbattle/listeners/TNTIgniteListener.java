package io.github.cardsandhuskers.buildbattle.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class TNTIgniteListener implements Listener {

    @EventHandler
    public void onBlockIgnite(ExplosionPrimeEvent e) {
        e.setCancelled(true);
        //System.out.println("TNT LIT");

    }
}
