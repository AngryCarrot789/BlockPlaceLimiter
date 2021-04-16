# BlockPlaceLimiter
A plugin that tracks user placed/broken blocks within all worlds, for limiting the placement of specific blocks

A plugin for limiting the number of blocks players can place. Also tracks where they are. not finished yet cus this is quite a big plugin lol

every player will have their own config file. when the plugin loads, it will start loading config files. 
but if the config file has no limited blocks placed, then it will unload it to decrease RAM usage.

however, if a player joins, it will load their config even if they have nothing placed. and if they leave, 
it does the same check to see if their config should be unloaded, and if so it unloads it

this has the ability to track ALL block placements, but block breaking/placing goes through the LimitManager first, which dismisses any un-limited block.
it will only then go through the ServerBlockTracker if the block is limited. there, a bunch of stuff goes on to track blocks, linking them to players

the trackers also dont require players to be online, it simply uses the player name. this means, the trackers work for both players online and offfline. 

i still have loads of testing to do tho... because theres quite a lot going on so thers lots to go wrong. 

but atm, placing/breaking blocks as the same player while online works. not sure about other players blocks but that should work too... not sure about offline players too but it should work
