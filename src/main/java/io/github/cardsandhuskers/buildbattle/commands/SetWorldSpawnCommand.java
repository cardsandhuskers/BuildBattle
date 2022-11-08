package io.github.cardsandhuskers.buildbattle.commands;

import io.github.cardsandhuskers.buildbattle.BuildBattle;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetWorldSpawnCommand implements CommandExecutor {
    private BuildBattle plugin;

    public SetWorldSpawnCommand(BuildBattle plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player p) {
            if(p.isOp()) {
                Location location = p.getLocation();

                plugin.getConfig().set("WorldSpawn", location);
                plugin.saveConfig();
                p.sendMessage("Location set at:\nWorld: " + location.getWorld() + "\nX: " + location.getX() + " Y: " + location.getY() + " Z: " + location.getZ());
            }
        } else {
            System.out.println("ERROR: cannot run from console.");
        }





        return true;
    }
}
