# BuildBattle
Build Battle plugin for minecraft 1.19

Each team will build in a separate arena
Players will then vote on each of the builds. The builds are anonymous so you do not know who built what
Players have a limited number of each vote type to prevent spamming a bad vote

# Commands: 

**/setBuildBattleArena [ArenaNumber] [Position 1 or 2]**
- Sets the corner of an arena (buildable area, so you should set at the corner of the floor)
- It doesn't matter which position is higher or lower on any axis,
- the lower corner should be inside the floor block, as it uses the lowest block as the floor
- Arena numbers should be contiguous (don't skip numbers)

**/setBuildBattleSpawn**
- Sets the world spawn for build battle to the player's location (the game lobby spot)

**/setLobby**
- Sets the main lobby that players will be returned to after the game ends

**/startBuildBattle [multiplier]**
- Starts build battle with the specified multiplier
