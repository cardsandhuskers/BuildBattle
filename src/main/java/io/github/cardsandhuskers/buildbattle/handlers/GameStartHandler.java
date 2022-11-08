package io.github.cardsandhuskers.buildbattle.handlers;

import io.github.cardsandhuskers.buildbattle.BuildBattle;
import io.github.cardsandhuskers.buildbattle.listeners.InventoryClickListener;
import io.github.cardsandhuskers.buildbattle.listeners.ItemClickListener;
import io.github.cardsandhuskers.buildbattle.listeners.PlayerMoveListener;
import io.github.cardsandhuskers.buildbattle.objects.Arena;
import io.github.cardsandhuskers.buildbattle.objects.Countdown;
import io.github.cardsandhuskers.teams.objects.Team;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
    private PlayerPointsAPI ppAPI;
    private VotingInventoryHandler votingInventoryHandler;
    private HashMap<Player, Integer> buildVoteMap = new HashMap<>();
    public GameStartHandler(BuildBattle plugin, ArrayList<Arena> arenaList, PlayerPointsAPI ppAPI) {
        this.ppAPI = ppAPI;
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
                20,
                //Timer Start
                () -> {
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
                    }
                    votingInventoryHandler.assignTheme();
                },

                //Each Second
                (t) -> {
                    BuildBattle.timeVar = t.getSecondsLeft();

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
                30,
                //Timer Start
                () -> {
                    BuildBattle.timeVar = 10;
                    BuildBattle.timerStatus = "Build Time";

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
                    BuildVotingHandler buildVotingHandler = new BuildVotingHandler(plugin, arenaList, buildVoteMap, ppAPI);
                    buildVotingHandler.startVoting();

                },

                //Each Second
                (t) -> {
                    BuildBattle.timeVar = t.getSecondsLeft();

                }
        );

        // Start scheduling, don't use the "run" method unless you want to skip a second
        timer.scheduleTimer();
    }




}
