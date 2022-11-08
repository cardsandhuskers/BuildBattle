package io.github.cardsandhuskers.buildbattle.listeners;

import io.github.cardsandhuskers.buildbattle.BuildBattle;
import io.github.cardsandhuskers.buildbattle.objects.Arena;
import io.github.cardsandhuskers.teams.objects.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;

import static io.github.cardsandhuskers.buildbattle.BuildBattle.handler;

public class BlockBreakListener implements Listener {
    private ArrayList<Arena> arenaList;

    public BlockBreakListener(ArrayList<Arena> arenaList) {
        this.arenaList = arenaList;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if( BuildBattle.gameRunning == true) {
            Player p = e.getPlayer();
            Team t = handler.getPlayerTeam(p);
            Location l = e.getBlock().getLocation();
            boolean cancel = true;
            for (Arena a : arenaList) {
                if (a.getTeam().equals(t)) {
                    if (a.isValidLocation(l)) {
                        cancel = false;
                    }
                }
            }
            if (cancel) {
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(true);
        }
    }
}
