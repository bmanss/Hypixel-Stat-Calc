# Hypixel Stat Calculator

## Overview
This is an application that's currently being worked on for Hypixel Skyblock that's not affiliated with Hypixel themselves.  The application itself is made entirely in Java
along with a JSON file that contains all necessary data and values not provided by their API.
The main function of the program is to allow users to load their profile and preview their stats with various gear.

## How To Use
- There are two required files, Hypixel-Stat-Calc.jar and HypixelValues.json. These files need to be in the same directory for the application to work.
HypixelValues.json holds all necessary values not provided by Hypixel's Api. Modifying this file improperly can cause the application to break.
- Within the application a valid Hypixel APi key needs to be set along with a valid profile name or UUID to load a player's Hypixel profile. These input fields can 
be accessed via the settings menu at the top of the program.
- Another option is to select Custom Profile which does not require a profile name of API key and will create a default state.
- After choosing to load a profile or create a custom one you are free select and change gear and stats. 
- To set or change Skills/skyblock level select the profile button at the top to access the menu.

## Current Features
### 1. Profile Loading
- Users can Enter their profile name or UUID along with a valid API Key to load in their player stats and current equipped gear. Only the weapon in their first inventory slot will be loaded.
- #### What Will be Loaded:
    - Skill levels and their bonuses
    - Slayer Bonuses
    - Melody progress (for intelligence bonus)
    - Bestiary progress (for health bonus)
    - All equipped gear along with enchants,books,stars,reforge,recombobulated
    - Talisman Bag for magical power (with duplicate detection) along with any stat values or enrichment values
    - Current pet and pet level
- #### What Will NOT be Loaded:
    - Pet score (for magic find)
    - Community upgrades (because its very limited in API)
    - Essence shop bonuses
    - Gear attributes i.e. Mana pool, speed shards
    - GemStones
    - Skyblock Level (not feasible with API)

### 2. Player Profile Modification
- Add additional stats manually either via base stats or static stats
- Base stats are like normal stats that benefit from modifiers
- Static stats are additional stats added on to base stats unaffected by modifiers, bestiary strength bonus, for example. 
- change power stone
- enable god potion buff
- Change skill levels
### 3. Gear Modification
- Change items
- Enchant gear
- Star gear
- Reforge gear
- Recombobulate gear
- Add hot potato/fuming books

### 4. Damage Preview
- Ability to set which mob is being damaged
- Set mob health
- Only the first critical hit damage is displayed
- If an item has ability damage that is displayed

### 5. Dungeon Stats
- Option to enable dungeon stats on armor 
- Any armor with stars can display dungeon stats
- Golden/Diamond heads will display bonuses from their respective floor
- Class abilities are not currently tracked, so additional melee damage,intelligence,etc. will not be calculated in
