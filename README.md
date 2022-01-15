# wSkywars
A Skywars game with kill effects, win effects, and customisable cages. Supports solo and teams.

## Important ##
Make sure that you have a "defaultcage.schem" file inside the skywarsschematics/cages/ folder.
This will be the default cage. It is also very important to have WorldEdit installed on your server.

### **How to set it up**
**Hub**\
Setting it up is very straightforward. The first thing you have to do is set a hub spawn point. 
You can do that by executing the [/sw sethub] command.

**Maps**\
The next thing you want to do is create an arena. 
Create an empty world, then execute the [/sw createskywarsworld (worldname)] command. 
Next, create the actual arena. That is command , [/sw createarena (name)]. 
Have a schematic file (example.schem) in the “skywarsschematics” folder, 
with the name you are naming the arena. If that is the case, the schematic will 
be pasted, and the map now exists.

**Spawn Locations**\
The next step is creating spawn locations for every player.
You don’t need to worry about the placements of the cages.
You can add a spawn location to the map by executing the
[/sw addspawn (arenaName)]. 

**Chest Location & Types**\
There are two types of chests, MID and ISLAND. 
Loot for both chest types can be configured inside the “chestitems.yml” file. 
This system allows you to have more overpowered loot in the middle islands. 
You can add a chest spawn location to the arena with the following command 
[/sw addchest (arena) (MID/ISLAND)].  

**Cages**\
It is not required to implement extra cages. 
However, it would be cool. All you have to do is add a section to the “cages.yml” file,
following the example. The last thing to do is create a cage design and create a schematic out 
of it, and put it inside the “skywarsschematics/cages/“ folder. 

### **Cosmetics**
**Kill Effects and Win Effects**\
Several Kill and Win Effects are provided within this plugin. 
You can configure them and change their price inside the “killeffects.yml” 
and the “wineffects.yml” files. 

### **Storage**
All stats and cosmetic data are stored in SQLite. 
I may add additional storage options in the future.

### **Admin Commands**
**These commands require the "skywars.admin" permission**\
``/sw createarena <arena>`` - Create a skywars arena\
``/sw addchest <arena> <MID/ISLAND> `` - Add a type of chest to the arena\
``/sw sethub`` - Set the hub spawn location\
``/sw createskywarsworld <name>`` - Create an empty void world\
``/sw addspawn <arena>`` - Add a player spawn location to an arena\
``/sw worldtp <world>`` - Teleport you to a specific skywars world (void world)

### **Commands**
**These commands require no permission**\
``/sw join`` - Join a game of skywars\
``/sw leave`` - Leave a game of skywars



