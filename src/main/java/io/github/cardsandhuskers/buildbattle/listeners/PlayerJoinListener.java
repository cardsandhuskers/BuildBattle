package io.github.cardsandhuskers.buildbattle.listeners;

import io.github.cardsandhuskers.buildbattle.BuildBattle;
import io.github.cardsandhuskers.buildbattle.handlers.BuildVotingHandler;
import io.github.cardsandhuskers.buildbattle.objects.Arena;
import io.github.cardsandhuskers.teams.objects.Team;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

import static io.github.cardsandhuskers.buildbattle.BuildBattle.handler;

public class PlayerJoinListener implements Listener {
    BuildBattle plugin;
    ArrayList<Arena> arenaList;


    public PlayerJoinListener(BuildBattle plugin, ArrayList<Arena> arenaList) {

        this.plugin = plugin;
        this.arenaList = arenaList;

    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if(BuildBattle.timerStatus.equalsIgnoreCase("game starting")) {
            p.teleport(plugin.getConfig().getLocation("WorldSpawn"));
            if(handler.getPlayerTeam(p) == null) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, ()-> {
                    p.setGameMode(GameMode.SPECTATOR);
                }, 20L);
            }
        } else if(BuildBattle.timerStatus.equalsIgnoreCase("vote")) {
            if(handler.getPlayerTeam(p) != null) {
                for(Player player:handler.getPlayerTeam(p).getOnlinePlayers()) {
                    if(!player.equals(p)) {
                        p.teleport(player.getLocation());
                    }
                }
            } else {
                p.teleport(plugin.getConfig().getLocation("WorldSpawn"));
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, ()-> {
                    p.setGameMode(GameMode.SPECTATOR);
                }, 20L);
            }
        } else {
            if(handler.getPlayerTeam(p) != null) {
                Team t = handler.getPlayerTeam(p);
                for(Arena a:arenaList) {
                    if(a.getTeam().equals(t)) {
                        a.teleportPlayerToSpawn(p);
                    }
                }
            } else {
                p.teleport(plugin.getConfig().getLocation("WorldSpawn"));
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, ()-> {
                    p.setGameMode(GameMode.SPECTATOR);
                }, 20L);
            }
        }




        //Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
        //    p.setGameMode(GameMode.CREATIVE);
        //},20L);
    }
}
