package io.github.cardsandhuskers.buildbattle.handlers;

import io.github.cardsandhuskers.buildbattle.BuildBattle;
import io.github.cardsandhuskers.buildbattle.listeners.InventoryClickListener;
import io.github.cardsandhuskers.buildbattle.listeners.ItemClickListener;
import io.github.cardsandhuskers.buildbattle.listeners.PlayerMoveListener;
import io.github.cardsandhuskers.buildbattle.objects.Arena;
import io.github.cardsandhuskers.buildbattle.objects.Countdown;
import io.github.cardsandhuskers.teams.objects.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

import static io.github.cardsandhuskers.buildbattle.BuildBattle.handler;
import static org.bukkit.Bukkit.getServer;

public class GameStartHandler {
    private BuildBattle plugin;
    private ArrayList<Arena> arenaList;
    private VoteCounter voteCounter;
    private VotingInventoryHandler votingInventoryHandler;
    private HashMap<Player, ItemClickListener.Vote> buildVoteMap = new HashMap<>();
    public GameStartHandler(BuildBattle plugin, ArrayList<Arena> arenaList) {
        this.plugin = plugin;
        this.arenaList = arenaList;
    }

    /**
     * Starts the game, called after timer in the startgamecommand method
     */
    public void startGame() {

        voteCounter = new VoteCounter();
        votingInventoryHandler = new VotingInventoryHandler(voteCounter, plugin);

        for(Arena a:arenaList) {
            a.teleportTeamToSpawn();
        }

        getServer().getPluginManager().registerEvents(new PlayerMoveListener(arenaList), plugin);
        getServer().getPluginManager().registerEvents(new ItemClickListener(voteCounter, votingInventoryHandler, buildVoteMap, arenaList), plugin);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(votingInventoryHandler, voteCounter, arenaList), plugin);

        themeVotingTimer();

    }

    /**
     * Theme voting timer,
     * called by the startGame method
     */
    private void themeVotingTimer() {
        Countdown timer = new Countdown((JavaPlugin)plugin,
                //should be 40 seconds
                plugin.getConfig().getInt("ThemeVotingTime"),
                //Timer Start
                () -> {
                    for(Team t: handler.getTeams()) {
                        t.resetTempPoints();
                    }
                    BuildBattle.timeVar = 20;
                    BuildBattle.timerStatus = "Theme Voting";
                    for(Player p:Bukkit.getOnlinePlayers()) {
                        Inventory inv = p.getInventory();
                        inv.clear();
                    }
                    ItemStack votingItem = new ItemStack(Material.NETHER_STAR, 1);
                    ItemMeta votingMeta = votingItem.getItemMeta();
                    votingMeta.setDisplayName("Vote");
                    votingItem.setItemMeta(votingMeta);
                    for(Team t:handler.getTeams()) {
                        for(Player p:t.getOnlinePlayers()) {
                            Inventory inv = p.getInventory();
                            inv.setItem(8, votingItem);
                        }
                    }
                },

                //Timer End
                () -> {
                    BuildBattle.timeVar = 0;
                    buildTimer();
                    for(Player p: Bukkit.getOnlinePlayers()) {
                        p.closeInventory();
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                    }
                    votingInventoryHandler.assignTheme();
                },

                //Each Second
                (t) -> {
                    BuildBattle.timeVar = t.getSecondsLeft();
                    if(t.getSecondsLeft() <= 5) {
                        for(Player p:Bukkit.getOnlinePlayers()) {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                        }
                        Bukkit.broadcastMessage(ChatColor.AQUA + "Voting time ends in " + ChatColor.YELLOW + t.getSecondsLeft() + ChatColor.AQUA + " seconds!");
                    }
                }
        );

        // Start scheduling, don't use the "run" method unless you want to skip a second
        timer.scheduleTimer();
    }

    /**
     * Timer for the build time, real time should probably be 600 seconds (10 minutes), but maybe less, this needs to be researched a bit
     * Called by themeVotingTimer method
     */
    private void buildTimer() {
        Countdown timer = new Countdown((JavaPlugin) plugin,
                //should be 600 seconds
                plugin.getConfig().getInt("BuildTime"),
                //Timer Start
                () -> {
                    BuildBattle.timeVar = 10;
                    BuildBattle.timerStatus = "Build Time";
                    Bukkit.broadcastMessage(ChatColor.GRAY + "Use the Nether Star to Change the Floor block!");
                    //give every player a change floor nether star
                    ItemStack floorItem = new ItemStack(Material.NETHER_STAR, 1);
                    ItemMeta floorMeta = floorItem.getItemMeta();
                    floorMeta.setDisplayName("Change Floor");
                    floorItem.setItemMeta(floorMeta);

                    for(Team t:handler.getTeams()) {
                        for(Player p:t.getOnlinePlayers()) {
                            Inventory inv = p.getInventory();
                            inv.setItem(8, floorItem);
                        }
                    }
                },

                //Timer End
                () -> {
                    BuildBattle.timeVar = 0;
                    BuildBattle.gameRunning = false;
                    for(Player p:Bukkit.getOnlinePlayers()) {
                        Inventory inv = p.getInventory();
                        inv.clear();
                    }
                    BuildVotingHandler buildVotingHandler = new BuildVotingHandler(plugin, arenaList, buildVoteMap);
                    buildVotingHandler.startVoting();

                },

                //Each Second
                (t) -> {
                    BuildBattle.timeVar = t.getSecondsLeft();
                    if(t.getSecondsLeft() == 20) {
                        for(Player p:Bukkit.getOnlinePlayers()) {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        }
                        Bukkit.broadcastMessage(ChatColor.AQUA + "Build time ends in " + ChatColor.YELLOW + t.getSecondsLeft() + ChatColor.AQUA + " seconds!");
                    }
                    if(t.getSecondsLeft() == 10) {
                        for(Player p:Bukkit.getOnlinePlayers()) {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        }
                        Bukkit.broadcastMessage(ChatColor.AQUA + "Build time ends in " + ChatColor.YELLOW + t.getSecondsLeft() + ChatColor.AQUA + " seconds!");
                    }
                    if(t.getSecondsLeft() <= 5) {
                        for(Player p:Bukkit.getOnlinePlayers()) {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        }
                        Bukkit.broadcastMessage(ChatColor.AQUA + "Build time ends in " + ChatColor.YELLOW + t.getSecondsLeft() + ChatColor.AQUA + " seconds!");
                    }
                }
        );

        // Start scheduling, don't use the "run" method unless you want to skip a second
        timer.scheduleTimer();
    }




}
