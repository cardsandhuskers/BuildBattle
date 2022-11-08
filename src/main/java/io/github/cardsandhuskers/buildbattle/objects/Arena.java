package io.github.cardsandhuskers.buildbattle.objects;

import io.github.cardsandhuskers.buildbattle.BuildBattle;
import io.github.cardsandhuskers.teams.objects.Team;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class Arena {
    private String lowerX, lowerY, lowerZ, higherX, higherY, higherZ;
    private Team arenaTeam;
    private BuildBattle plugin;
    private Location pos1, pos2, spawn;




    public Arena(Location pos1, Location pos2, Team team, BuildBattle plugin) {
        arenaTeam = team;
        this.plugin = plugin;
        this.pos1 = pos1;
        this.pos2 = pos2;

        setLowerCoords();

        spawn = new Location(pos1.getWorld(), (pos1.getX() + pos2.getX())/2, getCoordinate("lower", 'y') + 1, (pos1.getZ() + pos2.getZ())/2);
    }

    /**
     * Gets the team associated with this arena
     * @return team
     */
    public Team getTeam() {
        return arenaTeam;
    }

    /**
     * teleports all players on the specified arena's team to the arena
     */
    public void teleportTeamToSpawn() {
        for(Player p: arenaTeam.getOnlinePlayers()) {
            p.teleport(getSafeLocation());
        }
    }

    /**
     * teleports specified player to arena
     * @param p
     */
    public void teleportPlayerToSpawn(Player p) {
        p.teleport(getSafeLocation());
    }

    /**
     * Teleports all online players to arena
     */
    public void teleportAllToSpawn() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.teleport(getSafeLocation());
        }
    }
    /**
     * Sets the floor of the arena
     * @param mat
     */
    public void setFloor(Material mat) {
        for(int x = getCoordinate("lower", 'x'); x <= getCoordinate("higher", 'x'); x++) {
            for(int z = getCoordinate("lower", 'z'); z <= getCoordinate("higher", 'z'); z++) {
                Location l = new Location(pos1.getWorld(), x, pos1.getY(), z);
                Block b = l.getBlock();
                b.setType(mat);
            }
        }
    }

    /**
     * Sets all the blocks above the arena floor to air
     */
    public void clearArena() {
        int counter = 1;
        for(int x = getCoordinate("lower", 'x'); x <= getCoordinate("higher", 'x'); x++) {
            int finalX = x;
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                for(int y = getCoordinate("lower", 'y') + 1; y <= getCoordinate("higher", 'y'); y++) {
                    for(int z = getCoordinate("lower", 'z'); z <= getCoordinate("higher", 'z'); z++) {
                        Location l = new Location(pos1.getWorld(), finalX, y, z);
                        Block b = l.getBlock();
                        b.setType(Material.AIR);
                        //System.out.println(b.getY());
                    }
                }
            }, 10L * counter);
            counter++;
        }
    }

    /**
     * Checks if the location of a block place or break is valid
     * @param l
     * @return boolean if location is valid
     */
    public boolean isValidLocation(Location l) {
        if(l.getX() >= getCoordinate("lower", 'x') && l.getX() <= getCoordinate("higher", 'x')) {
            if(l.getY() >= getCoordinate("lower", 'y') && l.getY() <= getCoordinate("higher", 'y')) {
                if(l.getZ() >= getCoordinate("lower", 'z') && l.getZ() <= getCoordinate("higher", 'z')) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the coordinate matching the part of the arena corner
     * @param pos lower or higher
     * @param axis x, y or z
     * @return int coordinate matching parameters
     */
    public int getCoordinate(String pos, char axis) {
        switch(axis) {
            case 'x':
                if(pos.equalsIgnoreCase("lower")) {
                    if (lowerX.equalsIgnoreCase("pos1")) {
                        return pos1.getBlockX();
                    } else {
                        return pos2.getBlockX();
                    }
                } else {
                    if (higherX.equalsIgnoreCase("pos1")) {
                        return pos1.getBlockX();
                    } else {
                        return pos2.getBlockX();
                    }
                }
            case 'y':
                if(pos.equalsIgnoreCase("lower")) {
                    if (lowerY.equalsIgnoreCase("pos1")) {
                        return pos1.getBlockY();
                    } else {
                        return pos2.getBlockY();
                    }
                } else {
                    if (higherY.equalsIgnoreCase("pos1")) {
                        return pos1.getBlockY();
                    } else {
                        return pos2.getBlockY();
                    }
                }
            case 'z':
                if(pos.equalsIgnoreCase("lower")) {
                    if (lowerZ.equalsIgnoreCase("pos1")) {
                        return pos1.getBlockZ();
                    } else {
                        return pos2.getBlockZ();
                    }
                } else {
                    if (higherZ.equalsIgnoreCase("pos1")) {
                        return pos1.getBlockZ();
                    } else {
                        return pos2.getBlockZ();
                    }
                }
            default: return 0;
        }
    }

    public void setFireworks() {
        Location l1 = new Location(spawn.getWorld(), spawn.getX() + 10, spawn.getY() + 5, spawn.getZ() + 10);
        Location l2 = new Location(spawn.getWorld(), spawn.getX() + 10, spawn.getY() + 5, spawn.getZ() - 10);
        Location l3 = new Location(spawn.getWorld(), spawn.getX() -10, spawn.getY() + 5, spawn.getZ() + 10);
        Location l4 = new Location(spawn.getWorld(), spawn.getX() - 10, spawn.getY() + 5, spawn.getZ() - 10);

        for(int i = 0; i < 5; i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Firework firework1 = (Firework) l1.getWorld().spawnEntity(l1, EntityType.FIREWORK);
                FireworkMeta fireworkMeta = firework1.getFireworkMeta();
                fireworkMeta.addEffect(FireworkEffect.builder().withColor(arenaTeam.translateColor()).flicker(true).build());
                firework1.setFireworkMeta(fireworkMeta);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    firework1.detonate();
                }, 30L);


                Firework firework2 = (Firework) l2.getWorld().spawnEntity(l2, EntityType.FIREWORK);
                firework2.setFireworkMeta(fireworkMeta);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    firework2.detonate();
                }, 30L);

                Firework firework3 = (Firework) l3.getWorld().spawnEntity(l3, EntityType.FIREWORK);
                firework3.setFireworkMeta(fireworkMeta);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    firework3.detonate();
                }, 30L);

                Firework firework4 = (Firework) l4.getWorld().spawnEntity(l4, EntityType.FIREWORK);
                firework4.setFireworkMeta(fireworkMeta);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    firework4.detonate();
                }, 30L);


            }, 20L * i);
        }
    }

    /**
     * Returns a block in the arena that has plenty of space around it to teleport the player to
     * @return Location to teleport player to
     */
    private Location getSafeLocation() {
        if(hasRoom(spawn)) {
            return spawn;
        } else {

            //++ corner
            Location l = new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ());
            for(int y = spawn.getBlockY(); y < getCoordinate("higher", 'y') - 4; y++) {
                l.setY(y);
                for(int x = spawn.getBlockX(); x < getCoordinate("higher", 'x'); x++) {
                    l.setX(x);
                    for(int z = spawn.getBlockZ(); z < getCoordinate("higher", 'z'); z++) {
                        l.setZ(z);
                        if(hasRoom(l)) {
                            return l;
                        }
                    }
                }
            }


            //-- corner
            l = new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ());
            for(int y = spawn.getBlockY(); y < getCoordinate("higher", 'y') - 4; y++) {
                l.setY(y);
                for(int x = spawn.getBlockX(); x > getCoordinate("lower", 'x'); x--) {
                    l.setX(x);
                    for(int z = spawn.getBlockZ(); z > getCoordinate("lower", 'z'); z--) {
                        l.setZ(z);
                        if(hasRoom(l)) {
                            return l;
                        }
                    }
                }
            }

            //+- corner
            l = new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ());
            for(int y = spawn.getBlockY(); y < getCoordinate("higher", 'y') - 4; y++) {
                l.setY(y);
                for(int x = spawn.getBlockX(); x < getCoordinate("higher", 'x'); x++) {
                    l.setX(x);
                    for(int z = spawn.getBlockZ(); z > getCoordinate("lower", 'z'); z--) {
                        l.setZ(z);
                        if(hasRoom(l)) {
                            return l;
                        }
                    }
                }
            }

            //-+ corner
            l = new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ());
            for(int y = spawn.getBlockY(); y < getCoordinate("higher", 'y') - 4; y++) {
                l.setY(y);
                for(int x = spawn.getBlockX(); x > getCoordinate("lower", 'x'); x--) {
                    l.setX(x);
                    for(int z = spawn.getBlockZ(); z < getCoordinate("higher", 'z'); z++) {
                        l.setZ(z);
                        if(hasRoom(l)) {
                            return l;
                        }
                    }
                }
            }
        }
        return spawn;
    }


    /**
     * Sets the lower and upper coordinate positions of the arena
     */
    private void setLowerCoords() {
        if (pos1.getX() > pos2.getX()) {
            higherX = "pos1";
            lowerX = "pos2";
        } else {
            higherX = "pos2";
            lowerX = "pos1";
        }
        if (pos1.getY() > pos2.getY()) {
            higherY = "pos1";
            lowerY = "pos2";
        } else {
            higherY = "pos2";
            lowerY = "pos1";
        }
        if (pos1.getZ() > pos2.getZ()) {
            higherZ = "pos1";
            lowerZ = "pos2";
        } else {
            higherZ = "pos2";
            lowerZ = "pos1";
        }
    }

    /**
     *
     * @param location
     * @return whether location has room
     */
    private boolean hasRoom(Location location) {
        //checks if a 7x7x10 is empty
        for(int x = location.getBlockX() - 2; x <= location.getBlockX() + 3; x++) {
            for(int z = location.getBlockZ() - 2; z <= location.getBlockZ() + 3; z++) {
                for(int y = location.getBlockY(); y<= location.getBlockY() + 10; y++) {
                    Location l = new Location(location.getWorld(), x, y, z);
                    if(l.getBlock().getType() != Material.AIR) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

}
