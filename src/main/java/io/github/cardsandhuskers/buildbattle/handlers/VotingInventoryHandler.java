package io.github.cardsandhuskers.buildbattle.handlers;

import io.github.cardsandhuskers.buildbattle.BuildBattle;
import io.github.cardsandhuskers.buildbattle.objects.VotingMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

import static io.github.cardsandhuskers.buildbattle.BuildBattle.theme;

public class VotingInventoryHandler {
    private ArrayList<VotingMenu> votingMenuList = new ArrayList<>();
    private ArrayList<VotingMenu> openMenus = new ArrayList<>();
    private VoteCounter voteCounter;
    private BuildBattle plugin;
    private ArrayList<String> themeList = new ArrayList<>();
    public VotingInventoryHandler(VoteCounter voteCounter, BuildBattle plugin) {
        this.plugin = plugin;
        this.voteCounter = voteCounter;
        getThemes();
    }

    /**
     * gets the voting menu Object corresponding to the specified player
     * If one does not exist, it is created
     * @param p
     * @return VotingMenu
     */
    public VotingMenu getMenu(Player p) {
        //loop through and find the matching one
        for(VotingMenu v:votingMenuList) {
            if(v.getOwner() != null && v.getOwner().equals(p)) {
                openMenus.add(v);
                return v;
            }
        }
        //if it doesn't exist make a new one
        VotingMenu v = new VotingMenu(p, voteCounter, themeList);
        votingMenuList.add(v);
        openMenus.add(v);
        return v;
    }

    /**
     * Sets the vote of the specified player
     * @param p
     * @param vote
     */
    public void setVote(Player p, int vote) {
        for(VotingMenu v:votingMenuList) {
            if(v.getOwner() != null && v.getOwner().getPlayer().equals(p)) {
                v.setVote(vote);
            }
        }
    }

    /**
     * updates all open menus
     */
    public void updateMenus() {
        //System.out.println(openMenus);
        for(VotingMenu v:openMenus) {
            v.openInventory();
        }
    }

    /**
     * removes the menu from the list of open menus when a player closes their voting menu
     * @param p
     */
    public void closeMenu(Player p) {
        for(VotingMenu m:openMenus) {
            if(m.getOwner().equals(p)) {
                openMenus.remove(m);
            }
        }
    }

    /**
     * Assigns the winning theme
     */
    public void assignTheme() {

        int index = voteCounter.getWinningTheme();
        theme = themeList.get(index);
        Bukkit.broadcastMessage("The theme is " + ChatColor.GREEN + "" + ChatColor.BOLD + theme);
        for(Player p:Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 2F);
        }
    }

    /**
     * Gets 5 themes from the list in config and adds them to the themeList
     */
    private void getThemes() {
        ArrayList<String> tempList = new ArrayList<>();

        for(String theme:plugin.getConfig().getStringList("Themes")) {
            tempList.add(theme);
        }
        if(tempList.size() <= 5) {
            for(String s:tempList) {
                themeList.add(s);
            }
        } else {
            Random random = new Random();
            for(int i = 0; i < 5; i++) {
                int item = random.nextInt(tempList.size());
                themeList.add(tempList.get(item));
                tempList.remove(item);
            }
        }

    }
}
