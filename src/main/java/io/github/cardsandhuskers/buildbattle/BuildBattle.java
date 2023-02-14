package io.github.cardsandhuskers.buildbattle;

import io.github.cardsandhuskers.buildbattle.commands.SetArenaPosCommand;
import io.github.cardsandhuskers.buildbattle.commands.SetLobbyCommand;
import io.github.cardsandhuskers.buildbattle.commands.SetWorldSpawnCommand;
import io.github.cardsandhuskers.buildbattle.commands.StartGameCommand;
import io.github.cardsandhuskers.buildbattle.objects.Placeholder;
import io.github.cardsandhuskers.teams.Teams;
import io.github.cardsandhuskers.teams.handlers.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class BuildBattle extends JavaPlugin {
    public static TeamHandler handler;
    public static int timeVar = 0;
    public static String timerStatus = "Game Starting";
    public static String theme = "Voting...";
    public static double multiplier = 1;
    public static boolean gameRunning = false;
    @Override
    public void onEnable() {

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            /*
             * We register the EventListener here, when PlaceholderAPI is installed.
             * Since all events are in the main class (this class), we simply use "this"
             */
            new Placeholder(this).register();

        } else {
            /*
             * We inform about the fact that PlaceholderAPI isn't installed and then
             * disable this plugin to prevent issues.
             */
            System.out.println("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        // Plugin startup logic
        getCommand("setBuildBattleArena").setExecutor(new SetArenaPosCommand(this));
        getCommand("startBuildBattle").setExecutor(new StartGameCommand(this));
        getCommand("setBuildBattleSpawn").setExecutor(new SetWorldSpawnCommand(this));
        getCommand("setLobby").setExecutor(new SetLobbyCommand(this));

        handler = Teams.handler;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
