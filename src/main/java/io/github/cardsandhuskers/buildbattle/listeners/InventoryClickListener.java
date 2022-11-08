package io.github.cardsandhuskers.buildbattle.listeners;

import io.github.cardsandhuskers.buildbattle.BuildBattle;
import io.github.cardsandhuskers.buildbattle.handlers.VoteCounter;
import io.github.cardsandhuskers.buildbattle.handlers.VotingInventoryHandler;
import io.github.cardsandhuskers.buildbattle.objects.Arena;
import io.github.cardsandhuskers.teams.objects.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;

import static io.github.cardsandhuskers.buildbattle.BuildBattle.handler;

public class InventoryClickListener implements Listener {
    private VotingInventoryHandler votingInventoryHandler;
    private VoteCounter voteCounter;
    ArrayList<Arena> arenaList;
    public InventoryClickListener(VotingInventoryHandler votingInventoryHandler, VoteCounter voteCounter, ArrayList<Arena> arenaList) {
        this.votingInventoryHandler = votingInventoryHandler;
        this.voteCounter = voteCounter;
        this.arenaList = arenaList;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getCurrentItem() != null) {
            if(ChatColor.stripColor(e.getView().getTitle()).equalsIgnoreCase("Vote for the Theme!") && e.getCurrentItem() != null) {
                e.setCancelled(true);
                int slot = e.getSlot();
                if((slot) %9 == 0 && e.getCurrentItem().getType() == Material.TERRACOTTA) {
                    //System.out.println("SETTING VOTE");
                    votingInventoryHandler.setVote((Player) e.getWhoClicked(), slot/9 + 1);
                    voteCounter.addPlayerVote((Player) e.getWhoClicked(), slot/9+1);
                    votingInventoryHandler.updateMenus();
                }
            } else if(BuildBattle.timerStatus.equalsIgnoreCase("build time")) {
                if(ChatColor.stripColor(e.getView().getTitle()).equalsIgnoreCase("Click a Block to set the Floor")) {
                    Material mat = e.getCurrentItem().getType();
                    Player p = (Player) e.getWhoClicked();
                    Team t = handler.getPlayerTeam(p);

                    if(t != null) {
                        for(Arena a:arenaList) {
                            if(a.getTeam().equals(t)) {
                                if(isValidFloor(mat) != null) {
                                    a.setFloor(isValidFloor(mat));
                                }
                                else {
                                    p.sendMessage(ChatColor.RED + "Invalid Floor type");
                                }
                            }
                        }
                    }
                    e.setCancelled(true);
                    p.closeInventory();
                }
            } else if (BuildBattle.timerStatus.equalsIgnoreCase("vote")) {
                e.setCancelled(true);
            }
            if (e.getCurrentItem().getType() == Material.NETHER_STAR) {

                e.setCancelled(true);
            }
        }

    }
    public Material isValidFloor(Material mat) {
        switch (mat) {
            case WATER_BUCKET:
                return Material.WATER;
            case LAVA_BUCKET:
                return Material.LAVA;
            //all of the things that return null
            case IRON_AXE:
            case IRON_INGOT:
                return null;
            default:
                return mat;
        }
    }
}
