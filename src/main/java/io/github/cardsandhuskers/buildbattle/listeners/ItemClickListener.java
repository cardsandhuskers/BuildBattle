package io.github.cardsandhuskers.buildbattle.listeners;

import io.github.cardsandhuskers.buildbattle.BuildBattle;
import io.github.cardsandhuskers.buildbattle.handlers.VoteCounter;
import io.github.cardsandhuskers.buildbattle.handlers.VotingInventoryHandler;
import io.github.cardsandhuskers.buildbattle.objects.Arena;
import io.github.cardsandhuskers.buildbattle.objects.VotingMenu;
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
    private HashMap<Player, Integer> buildVoteMap;
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

            } else if (BuildBattle.timerStatus.equalsIgnoreCase("vote")) {
                Material mat = e.getMaterial();
                int val = 0;
                Player p = e.getPlayer();
                Arena arena = getCurrentArena(p);
                if(handler.getPlayerTeam(p).equals(arena.getTeam())) {
                    p.sendMessage(ChatColor.RED + "You cannot Vote on your own Build!");
                } else {
                    switch(mat) {
                        case RED_TERRACOTTA:
                            val = 1;
                            p.sendMessage("You voted " + ChatColor.DARK_RED + "" + ChatColor.BOLD + "Terrible");
                            break;
                        case PINK_TERRACOTTA:
                            val = 2;
                            p.sendMessage("You voted " + ChatColor.RED + "" + ChatColor.BOLD + "Bad");
                            break;
                        case LIME_TERRACOTTA:
                            val = 3;
                            p.sendMessage("You voted " + ChatColor.GREEN + "" + ChatColor.BOLD + "Good");
                            break;
                        case GREEN_TERRACOTTA:
                            val = 4;
                            p.sendMessage("You voted " + ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Great");
                            break;
                        case LIGHT_BLUE_TERRACOTTA:
                            val = 5;
                            p.sendMessage("You voted " + ChatColor.AQUA + "" + ChatColor.BOLD + "Amazing");
                            break;
                    }
                    buildVoteMap.put(e.getPlayer(), val);
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
}
