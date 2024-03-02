package io.github.cardsandhuskers.buildbattle.handlers;

import io.github.cardsandhuskers.buildbattle.BuildBattle;
import io.github.cardsandhuskers.buildbattle.listeners.ItemClickListener;
import io.github.cardsandhuskers.buildbattle.objects.Arena;
import io.github.cardsandhuskers.buildbattle.objects.Countdown;
import io.github.cardsandhuskers.teams.objects.Team;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static io.github.cardsandhuskers.buildbattle.BuildBattle.handler;
import static io.github.cardsandhuskers.buildbattle.BuildBattle.multiplier;

public class BuildVotingHandler {
    private BuildBattle plugin;
    private ArrayList<Arena> arenaList;
    private HashMap<UUID, ItemClickListener.Vote> buildVoteMap;
    private HashMap<UUID, ArrayList<Integer>> itemNumbersMap;

    public static int counter = 0;

    public BuildVotingHandler(BuildBattle plugin, ArrayList<Arena> arenaList, HashMap buildVoteMap) {
        this.buildVoteMap = buildVoteMap;
        this.plugin = plugin;
        this.arenaList = arenaList;
        counter = 1;
        itemNumbersMap = new HashMap<>();

        //initialize quantities for the votes, ordered from lowest to highest 'quality'
        ArrayList<Integer> initial = new ArrayList<>();
        initial.add(2);
        initial.add(3);
        initial.add(4);
        initial.add(3);
        initial.add(2);

        //put every participating player in a map with the array
        for(Team team: handler.getTeams()) {
            for(Player p:team.getOnlinePlayers()) {
                itemNumbersMap.put(p.getUniqueId(), (ArrayList<Integer>) initial.clone());
            }
        }
        giveVotingItems();
    }

    /**
     * Initializes voting
     */
    public void startVoting() {
        //System.out.println("COUNTER " + counter);
        //if all arenas have been iterated over, end the game
        if(counter > arenaList.size()) {
            teleportToWinner();

        } else {
            Arena a = arenaList.get(counter - 1);
            a.teleportAllToSpawn();
            buildVotingTimer();
            Bukkit.broadcastMessage(ChatColor.GREEN + "Voting Round " + ChatColor.AQUA + (counter));
            buildVoteMap.clear();
            for(Team t: handler.getTeams()) {
                for(Player p:t.getOnlinePlayers()) {
                    p.setGameMode(GameMode.ADVENTURE);
                    p.setAllowFlight(true);
                    p.setFlying(true);
                }
            }
        }

    }

    /**
     * Counts votes and updates relevant things
     */
    private void countVotes() {
        //System.out.println("Round counter: " + counter);
        //if(counter < 1) counter = 1;
        Team t = arenaList.get(counter - 1).getTeam();
        if(t == null) return;
        //if player is on the same team as the arena, cancel vote (this may be redundant) tt
        for(Player p: t.getOnlinePlayers()) {
            if(buildVoteMap.get(p.getUniqueId()) != null) {
                buildVoteMap.remove(p.getUniqueId());
            }
        }

        //total number of voters
        int numVoters = 0;
        //sum of points to give team
        double points = 0;

        //get all the people who have voted and remove voting item from them
        for(UUID u: buildVoteMap.keySet()) {
            Player p = Bukkit.getPlayer(u);
            //if they're not in their own arena (I think this is redundant)
            //if(p != null /*&& handler.getPlayerTeam(p) != null && !handler.getPlayerTeam(p).equals(t)*/) {
            numVoters ++;

            ItemClickListener.Vote vote = buildVoteMap.get(u);

            ArrayList<Integer> playerItems = itemNumbersMap.get(u);

            int voteIndex = 2;
            switch(vote) {
                case TERRIBLE:
                    voteIndex = 0;
                    points += plugin.getConfig().getInt("terrible");
                break;
                case BAD:
                    voteIndex = 1;
                    points += plugin.getConfig().getInt("bad");
                break;
                case GOOD:
                    voteIndex = 2;
                    points += plugin.getConfig().getInt("good");
                break;
                case GREAT:
                    voteIndex = 3;
                    points += plugin.getConfig().getInt("great");
                break;
                case AMAZING:
                    voteIndex = 4;
                    points += plugin.getConfig().getInt("amazing");
                break;
            }

            //error handling
            if(playerItems != null) {
                //removes item corresponding to their vote from their inventory
                playerItems.set(voteIndex, playerItems.get(voteIndex) - 1);
            }
        }

        //count players on teams to get total number of participating players
        int numPlayers = 0;

        for(Team team: handler.getTeams()) {
            if(!(team.equals(t))) {
                for(Player p:team.getOnlinePlayers()) {
                    numPlayers++;
                }
            }
        }

        //System.out.println("NUM PLAYERS: " + numPlayers + " TOTAL: " + total + "") ;
        //calculate total points, first make sure everyone has voted, and if they haven't, give them medium votes
        if(numVoters < numPlayers) {
            points += (numPlayers - numVoters) * plugin.getConfig().getInt("good");
        }
        //System.out.println("NUM PLAYERS: " + numPlayers + " TOTAL: " + total + "") ;

        //add in multiplier and divide total points by num players on the team
        points = (points * multiplier);
        double playerPoints = points/t.getSize();
        //for each player on the given team, give them points
        for(Player p:t.getOnlinePlayers()) {
            t.addTempPoints(p, playerPoints);
        }

        giveVotingItems();

    }


    /**
     * Timer called for each round of voting that takes place
     */
    private void buildVotingTimer() {
        Countdown timer = new Countdown((JavaPlugin) plugin,
                //should be 25
                plugin.getConfig().getInt("BuildVotingTime"),
                //Timer Start
                () -> {
                    BuildBattle.timeVar = 20;
                    BuildBattle.timerStatus = "Vote";
                },

                //Timer End
                () -> {
                    BuildBattle.timeVar = 0;
                    countVotes();
                    counter++;
                    //start next round
                    startVoting();

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
     * At the end of the game, teleport all players to winning plot and then start countdown
     * displays results and then ends the game when done
     */
    private void teleportToWinner() {
        ArrayList<Team> teamList = handler.getTempPointsSortedList();
        Team winner = handler.getTempPointsSortedList().get(0);
        Arena winnerArena = arenaList.get(0);
        for(Arena a:arenaList) {
            if(a.getTeam().equals(winner)) {
                winnerArena = a;
            }
        }

        Arena finalWinnerArena = winnerArena;
        Countdown timer = new Countdown((JavaPlugin) plugin,
            plugin.getConfig().getInt("GameEndTime"),
            //Timer Start
            () -> {
                BuildBattle.timeVar = 15;
                BuildBattle.timerStatus = "Returning to Lobby";
                finalWinnerArena.teleportAllToSpawn();

            },

            //Timer End
            () -> {
                BuildBattle.timeVar = 0;
                GameEndHandler gameEndHandler = new GameEndHandler(plugin);
                gameEndHandler.endGame();
            },

            //Each Second
            (t) -> {
                BuildBattle.timeVar = t.getSecondsLeft();

                if(t.getSecondsLeft() == t.getTotalSeconds() - 1) {
                    //broadcast winner
                    Bukkit.broadcastMessage(ChatColor.DARK_BLUE + "------------------------------");
                    Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Winner:");
                    Bukkit.broadcastMessage(winner.color + winner.getTeamName());
                    Bukkit.broadcastMessage(ChatColor.DARK_BLUE + "------------------------------");

                    //put up title of winning build
                    for(Player p:Bukkit.getOnlinePlayers()) {
                        p.sendTitle("Winner:", winner.color + winner.getTeamName(), 5, 30, 5);
                    }
                    //set off some fireworks
                    finalWinnerArena.setFireworks();


                }
                if(t.getSecondsLeft() == t.getTotalSeconds() - 5) {
                    //broadcast everyone else's scores for the game
                    Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Results:");
                    int counter = 1;
                    for(Team team:teamList) {
                        Bukkit.broadcastMessage(counter + ". " + team.color + ChatColor.BOLD +  team.getTeamName() + ChatColor.RESET + " Points: " + (int)team.getTempPoints());
                        counter++;
                    }
                }
            }
        );
        // Start scheduling, don't use the "run" method unless you want to skip a second
        timer.scheduleTimer();
    }

    /**
     * Gives the voting items based on what votes have been cast by the player
     * Called at the beginning of each round
     */
    private void giveVotingItems() {
        //build itemstacks
        ItemStack terrible = new ItemStack(Material.RED_TERRACOTTA, 2);
        ItemMeta terribleMeta = terrible.getItemMeta();
        terribleMeta.setDisplayName("Terrible");
        terrible.setItemMeta(terribleMeta);

        ItemStack bad = new ItemStack(Material.PINK_TERRACOTTA, 3);
        ItemMeta badMeta = terrible.getItemMeta();
        badMeta.setDisplayName("Bad");
        bad.setItemMeta(badMeta);

        ItemStack good = new ItemStack(Material.LIME_TERRACOTTA, 4);
        ItemMeta goodMeta = good.getItemMeta();
        goodMeta.setDisplayName("Good");
        good.setItemMeta(goodMeta);

        ItemStack great = new ItemStack(Material.GREEN_TERRACOTTA, 3);
        ItemMeta greatMeta = great.getItemMeta();
        greatMeta.setDisplayName("Great");
        great.setItemMeta(greatMeta);

        ItemStack amazing = new ItemStack(Material.LIGHT_BLUE_TERRACOTTA, 2);
        ItemMeta amazingMeta = amazing.getItemMeta();
        amazingMeta.setDisplayName("Amazing");
        amazing.setItemMeta(amazingMeta);

        //set itemstack quantities for each player
        for(Team t:handler.getTeams()) {
            for(Player p:t.getOnlinePlayers()) {
                ArrayList<Integer> tempList = itemNumbersMap.get(p);
                if(tempList != null) {
                    Inventory i = p.getInventory();

                    terrible.setAmount(tempList.get(0));
                    i.setItem(2, terrible);

                    bad.setAmount(tempList.get(1));
                    i.setItem(3, bad);

                    good.setAmount(tempList.get(2));
                    i.setItem(4, good);

                    great.setAmount(tempList.get(3));
                    i.setItem(5, great);

                    amazing.setAmount(tempList.get(4));
                    i.setItem(6, amazing);
                }
            }
        }
    }
}
