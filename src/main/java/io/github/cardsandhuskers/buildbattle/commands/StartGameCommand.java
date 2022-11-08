package io.github.cardsandhuskers.buildbattle.commands;

import io.github.cardsandhuskers.buildbattle.BuildBattle;
import io.github.cardsandhuskers.buildbattle.handlers.BuildVotingHandler;
import io.github.cardsandhuskers.buildbattle.handlers.GameStartHandler;
import io.github.cardsandhuskers.buildbattle.handlers.VotingInventoryHandler;
import io.github.cardsandhuskers.buildbattle.listeners.BlockBreakListener;
import io.github.cardsandhuskers.buildbattle.listeners.BlockPlaceListener;
import io.github.cardsandhuskers.buildbattle.listeners.PlayerJoinListener;
import io.github.cardsandhuskers.buildbattle.objects.Arena;
import io.github.cardsandhuskers.buildbattle.objects.Countdown;
import io.github.cardsandhuskers.teams.objects.Team;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static io.github.cardsandhuskers.buildbattle.BuildBattle.handler;
import static io.github.cardsandhuskers.buildbattle.BuildBattle.multiplier;
import static org.bukkit.Bukkit.getServer;

public class StartGameCommand implements CommandExecutor {
    private ArrayList<Arena> arenaList;
    private BuildBattle plugin;
    private GameStartHandler gameStartHandler;
    private PlayerPointsAPI ppAPI;

    public StartGameCommand(BuildBattle plugin, PlayerPointsAPI ppAPI) {
        this.ppAPI =ppAPI;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //initialize and assign arenas, then go to gameStartHandler and do everything else
        int counter = 1;
        ArrayList<Team> teamList = handler.getTeams();

        if(args.length > 0) {
            try {
                multiplier = Double.parseDouble(args[0]);
            } catch (Exception e) {
                if(sender instanceof Player p) {
                    p.sendMessage(ChatColor.RED + "ERROR: Argument must be double");
                }
            }

        }
        arenaList = new ArrayList<>();
        while(plugin.getConfig().getLocation("Arenas.Arena" + counter + "." + 1) != null && plugin.getConfig().getLocation("Arenas.Arena" + counter + "." + 2) != null) {
            Location pos1 = plugin.getConfig().getLocation("Arenas.Arena" + counter + "." + 1);
            Location pos2 = plugin.getConfig().getLocation("Arenas.Arena" + counter + "." + 2);
            if(teamList.size() >= counter) {
                Arena a = new Arena(pos1, pos2, teamList.get(counter - 1), plugin);
                arenaList.add(a);
                a.clearArena();
                a.setFloor(Material.WHITE_CONCRETE);
            }
            counter++;
        }
        if(counter >= handler.getNumTeams() + 1 && handler.getNumTeams() > 0) {
            pregameTimer();
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + "ERROR: Not Enough Arenas or No Teams");
        }
        return true;
    }

    public void pregameTimer() {
        Countdown timer = new Countdown((JavaPlugin)plugin,
                5,
                //Timer Start
                () -> {
                    BuildBattle.timeVar = 10;
                    BuildBattle.timerStatus = "Game Starting";
                    BuildVotingHandler.counter = 0;

                    Location l = plugin.getConfig().getLocation("WorldSpawn");
                    if(l != null) {
                        for(Player p:Bukkit.getOnlinePlayers()) {
                            p.teleport(l);
                            if(handler.getPlayerTeam(p) == null) {
                                p.setGameMode(GameMode.SPECTATOR);
                            }
                        }
                    }

                    BuildBattle.gameRunning = true;
                    getServer().getPluginManager().registerEvents(new BlockBreakListener(arenaList), plugin);
                    getServer().getPluginManager().registerEvents(new BlockPlaceListener(arenaList), plugin);
                    getServer().getPluginManager().registerEvents(new PlayerJoinListener(plugin, arenaList), plugin);


                },

                //Timer End
                () -> {
                    BuildBattle.timeVar = 0;

                    gameStartHandler = new GameStartHandler(plugin, arenaList, ppAPI);
                    gameStartHandler.startGame();
                },

                //Each Second
                (t) -> {
                    BuildBattle.timeVar = t.getSecondsLeft();
                    Bukkit.broadcastMessage(t.getSecondsLeft() + "");

                }
        );

        // Start scheduling, don't use the "run" method unless you want to skip a second
        timer.scheduleTimer();
    }
}
