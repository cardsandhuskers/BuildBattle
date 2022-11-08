package io.github.cardsandhuskers.buildbattle.handlers;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class VoteCounter {
    private HashMap<Player, Integer> playerVoteMap;
    private int leadingTheme;

    public VoteCounter() {
        playerVoteMap = new HashMap<>();

    }

    public int getWinningTheme() {
        return leadingTheme;
    }

    public void addPlayerVote(Player p, int voteIndex) {
        playerVoteMap.put(p, voteIndex);
    }


    public ArrayList<Integer> countVotes() {

        int votes1 = 0, votes2 = 0, votes3 = 0, votes4 = 0, votes5 = 0;

        for(Player p:playerVoteMap.keySet()) {
            int vote = playerVoteMap.get(p);
            switch (vote) {
                case 1:
                    votes1++;
                    break;
                case 2:
                    votes2++;
                    break;
                case 3:
                    votes3++;
                    break;
                case 4:
                    votes4++;
                    break;
                case 5:
                    votes5++;
                    break;
                default:
                    break;
            }
        }
        ArrayList<Integer> votesList = new ArrayList<>();
        votesList.add(votes1);
        votesList.add(votes2);
        votesList.add(votes3);
        votesList.add(votes4);
        votesList.add(votes5);

        int max = Collections.max(votesList);
        int maxLocation = votesList.indexOf(max);
        leadingTheme = maxLocation;

        return votesList;
    }




}
