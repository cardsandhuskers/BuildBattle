package io.github.cardsandhuskers.buildbattle.handlers;

import io.github.cardsandhuskers.buildbattle.BuildBattle;
import io.github.cardsandhuskers.teams.objects.Team;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import static io.github.cardsandhuskers.teams.Teams.handler;
import static io.github.cardsandhuskers.teams.Teams.ppAPI;

public class GameEndHandler {
    BuildBattle plugin;

    public GameEndHandler(BuildBattle plugin) {
        this.plugin = plugin;
    }

    public void endGame() {
        HandlerList.unregisterAll(plugin);

        for(Team t:handler.getTeams()) {
            for(OfflinePlayer p:t.getPlayers()) {
                int points = (int) t.getPlayerTempPointsValue(p);
                ppAPI.give(p.getUniqueId(), points);
            }
        }
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
