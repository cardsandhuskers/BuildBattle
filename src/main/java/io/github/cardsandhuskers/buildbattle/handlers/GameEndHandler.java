package io.github.cardsandhuskers.buildbattle.handlers;

import io.github.cardsandhuskers.buildbattle.BuildBattle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class GameEndHandler {
    BuildBattle plugin;

    public GameEndHandler(BuildBattle plugin) {
        this.plugin = plugin;
    }

    public void endGame() {
        HandlerList.unregisterAll(plugin);
        Location lobby = plugin.getConfig().getLocation("Lobby");
        for(Player p: Bukkit.getOnlinePlayers()) {
            p.teleport(lobby);
        }
        for(Player p:Bukkit.getOnlinePlayers()) {
            if(p.isOp()) {
                p.performCommand("startRound");
                break;
            }
        }
    }
}
