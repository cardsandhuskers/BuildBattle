package io.github.cardsandhuskers.buildbattle.objects;

import io.github.cardsandhuskers.buildbattle.BuildBattle;
import io.github.cardsandhuskers.buildbattle.handlers.BuildVotingHandler;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import static io.github.cardsandhuskers.buildbattle.BuildBattle.*;

public class Placeholder extends PlaceholderExpansion {
    private final BuildBattle plugin;

    public Placeholder(BuildBattle plugin) {
        this.plugin = plugin;
    }
    @Override
    public String getIdentifier() {
        return "Buildbattle";
    }
    @Override
    public String getAuthor() {
        return "cardsandhuskers";
    }
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    @Override
    public boolean persist() {
        return true;
    }



    @Override
    public String onRequest(OfflinePlayer p, String s) {
        if(s.equals("theme")) {
            return "" + theme;
        }
        if(s.equals("timer")) {
            int mins = timeVar / 60;
            String seconds = String.format("%02d", timeVar - (mins * 60));
            return mins + ":" + seconds;
        }
        if(s.equals("timerStage")) {
            return timerStatus;
        }
        if(s.equals("round")) {
            if(BuildVotingHandler.counter == 0) {
                return "-";
            }
            return BuildVotingHandler.counter + "";
        }




        return null;
    }

}
