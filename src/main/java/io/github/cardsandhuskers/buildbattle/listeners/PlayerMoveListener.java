package io.github.cardsandhuskers.buildbattle.listeners;

import io.github.cardsandhuskers.buildbattle.objects.Arena;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;

public class PlayerMoveListener implements Listener {
    private ArrayList<Arena> arenaList;

    public PlayerMoveListener(ArrayList<Arena> arenaList) {
        this.arenaList = arenaList;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Location l = e.getTo();
        Player p = e.getPlayer();
        //get arena player is in and check y
        for(Arena a: arenaList) {
            if(l.getX() >= a.getCoordinate("lower", 'x') - 4 && l.getX() <= a.getCoordinate("higher", 'x') + 4) {
                if(l.getZ() >= a.getCoordinate("lower", 'z') - 4 && l.getZ() <= a.getCoordinate("higher", 'z') + 4) {
                    if(l.getY() >= a.getCoordinate("upper", 'y')) {
                        a.teleportPlayerToSpawn(p);
                    }
                }
            }
        }


    }
}
