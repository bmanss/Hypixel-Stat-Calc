# Hypixel Stat Calculator

## Overview
This is an application that's currently being worked on for Hypixel Skyblock that's not affiliated with Hypixel themselves.  The application itself is made entirely in Java
along with a JSON file that contains all necessary data and values not provided by their API.  **There is currently no build version available.**
The main function of the program is to allow users to load their profile and preview their stats with various gear.

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
    - Skyblock Level (not feasible with API)

### 2. Player Profile Modification
- Add additional stats manually
- change power stone
- enable god potion buff
- (Planned) change skill levels
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
