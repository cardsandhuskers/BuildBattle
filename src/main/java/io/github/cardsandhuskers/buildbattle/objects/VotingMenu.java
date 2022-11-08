package io.github.cardsandhuskers.buildbattle.objects;

import io.github.cardsandhuskers.buildbattle.handlers.VoteCounter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class VotingMenu {
    private final OfflinePlayer owner;
    private int vote = 0;
    private VoteCounter voteCounter;
    private ArrayList<String> themeList;

    /**
     * This is an object created for each player
     * @param p
     * @param voteCounter
     * @param themeList
     */
    public VotingMenu(Player p, VoteCounter voteCounter, ArrayList<String> themeList) {
        this.themeList = themeList;
        this.voteCounter = voteCounter;
        owner = p;
    }
    public Player getOwner() {
        return owner.getPlayer();
    }


    /**
     * Builds and opens the inventory for the player that owns it
     */
    public void openInventory() {
        Player p = owner.getPlayer();
        Inventory inventory = Bukkit.createInventory(p, 54, ChatColor.RED + "Vote for the Theme!");

        //places the vote blocks that you can click on
        int index = 0;
        for(String theme:themeList) {
            ItemStack stack = new ItemStack(Material.TERRACOTTA, 1);
            ItemMeta stackMeta = stack.getItemMeta();
            stackMeta.setDisplayName(theme);
            stackMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            if(vote == index/9 + 1) {
                stackMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
            }
            stack.setItemMeta(stackMeta);
            inventory.setItem(index, stack);

            index+=9;
        }

        ArrayList<Integer> votesCount = voteCounter.countVotes();
        //get total votes
        int total = 0;
        for(int i:votesCount) {
            total += i;
        }
        int row = 0;
        //1/6 since there are 6 blocks per row
        double valPerSquare = .16667;
        //make sure there are votes
        if(total != 0) {

            for(int i:votesCount) {
                int numSquares = 0;
                if(i != 0) {
                    double percent = (double)i/(double)total;
                    numSquares = (int) (Math.round(percent/valPerSquare));
                }
                ItemStack redGlass = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
                ItemStack whiteGlass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
                for(int j = 1; j <= numSquares; j++) {
                    inventory.setItem(row * 9 + j, redGlass);
                }
                for(int j = numSquares + 1; j <= 6; j++) {
                    inventory.setItem(row * 9 + j, whiteGlass);
                }

                row++;
            }
        } else {
            //if there are no votes set everything to white glass
            ItemStack whiteGlass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
            for(row = 0; row < 5; row++) {
                for(int j = 1; j <= 6; j++) {
                    inventory.setItem(row * 9 + j, whiteGlass);
                }
            }
        }

        p.openInventory(inventory);
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

}
