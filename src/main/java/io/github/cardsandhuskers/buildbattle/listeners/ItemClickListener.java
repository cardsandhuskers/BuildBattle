package io.github.cardsandhuskers.buildbattle.listeners;

import io.github.cardsandhuskers.buildbattle.BuildBattle;
import io.github.cardsandhuskers.buildbattle.handlers.VoteCounter;
import io.github.cardsandhuskers.buildbattle.handlers.VotingInventoryHandler;
import io.github.cardsandhuskers.buildbattle.objects.Arena;
import io.github.cardsandhuskers.buildbattle.objects.VotingMenu;
import io.github.cardsandhuskers.teams.objects.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;

import static io.github.cardsandhuskers.buildbattle.BuildBattle.handler;

public class ItemClickListener implements Listener {

    private VoteCounter voteCounter;
    private VotingInventoryHandler votingInventoryHandler;
    private HashMap<Player, Vote> buildVoteMap;
    private ArrayList<Arena> arenaList;

    public ItemClickListener(VoteCounter voteCounter, VotingInventoryHandler votingInventoryHandler, HashMap buildVoteMap, ArrayList<Arena> arenaList) {
        this.voteCounter = voteCounter;
        this.votingInventoryHandler = votingInventoryHandler;
        this.buildVoteMap = buildVoteMap;
        this.arenaList = arenaList;
    }

    @EventHandler
    public void onInventoryClick(PlayerInteractEvent e) {
        if(e.getItem() != null && handler.getPlayerTeam(e.getPlayer()) != null) {

            if(e.getItem().getType() == Material.ENDER_PEARL) {
                e.setCancelled(true);
            }

            if (BuildBattle.timerStatus.equalsIgnoreCase("theme voting")) {
                if(e.getMaterial() == Material.NETHER_STAR) {
                    VotingMenu m = votingInventoryHandler.getMenu(e.getPlayer());
                    m.openInventory();
                }
            } else if (BuildBattle.timerStatus.equalsIgnoreCase("build time")) {
                if(e.getMaterial() == Material.NETHER_STAR) {
                    Inventory inventory = Bukkit.createInventory(e.getPlayer(), 9, ChatColor.RED + "Click a Block to set the Floor");
                    e.getPlayer().openInventory(inventory);
                }
                if(e.getMaterial() == Material.LAVA_BUCKET || e.getMaterial() == Material.WATER_BUCKET) {
                    Team t = handler.getPlayerTeam(e.getPlayer());
                    boolean cancel = true;
                    for(Arena a :arenaList) {
                        if(a.getTeam().equals(t)) {
                            if(a.isValidLocation(e.getClickedBlock().getLocation())) {
                                cancel = false;
                            }
                        }
                    }
                    if(cancel) {
                        e.setCancelled(true);
                    }
                }

            } else if (BuildBattle.timerStatus.equalsIgnoreCase("vote")) {
                Material mat = e.getMaterial();
                Vote vote = Vote.GOOD;
                Player p = e.getPlayer();
                Arena arena = getCurrentArena(p);
                //seems occasional null arenas arise, this should at least stop that from causing error, but things still may not work right
                if(arena != null && handler.getPlayerTeam(p).equals(arena.getTeam())) {
                    p.sendMessage(ChatColor.RED + "You cannot Vote on your own Build!");
                } else {
                    switch(mat) {
                        case RED_TERRACOTTA:
                            vote = Vote.TERRIBLE;
                            p.sendMessage("You voted " + ChatColor.DARK_RED + "" + ChatColor.BOLD + "Terrible");
                            break;
                        case PINK_TERRACOTTA:
                            vote = Vote.BAD;
                            p.sendMessage("You voted " + ChatColor.RED + "" + ChatColor.BOLD + "Bad");
                            break;
                        case LIME_TERRACOTTA:
                            vote = Vote.GOOD;
                            p.sendMessage("You voted " + ChatColor.GREEN + "" + ChatColor.BOLD + "Good");
                            break;
                        case GREEN_TERRACOTTA:
                            vote = Vote.GREAT;
                            p.sendMessage("You voted " + ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Great");
                            break;
                        case LIGHT_BLUE_TERRACOTTA:
                            vote = Vote.AMAZING;
                            p.sendMessage("You voted " + ChatColor.AQUA + "" + ChatColor.BOLD + "Amazing");
                            break;
                    }
                    buildVoteMap.put(e.getPlayer(), vote);
                }
            }
            if(e.getMaterial() == Material.FLINT_AND_STEEL) {
                if(e.getClickedBlock().getType() == Material.TNT) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(ChatColor.RED + "NO, Absolutely not");
                }
            }
        }
    }

    /**
     * Gets the arena a player is currently standing in
     * @param p
     * @return Arena player is in
     */
    private Arena getCurrentArena(Player p) {
        Location l = p.getLocation();
        double x = l.getX();
        double y = l.getY();
        double z = l.getZ();
        //check if the player's coordinates are within the bounds of each arena
        for(Arena a:arenaList) {
            if(x > a.getCoordinate("lower", 'x') && x < a.getCoordinate("higher", 'x')) {
                if(y > a.getCoordinate("lower", 'y') && y < a.getCoordinate("higher", 'y')) {
                    if(z > a.getCoordinate("lower", 'z') && z < a.getCoordinate("higher", 'z')) {
                        return a;
                    }
                }
            }
        }
        return null;
    }
    public static enum Vote {
        TERRIBLE,
        BAD,
        GOOD,
        GREAT,
        AMAZING
    }
}
