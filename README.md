# Hypixel Stat Calculator

## Overview
This is an application that's currently being worked on for Hypixel Skyblock that's not affiliated with Hypixel themselves.  The application itself is made entirely in Java
along with a JSON file that contains all necessary data and values not provided by their API.  There is currently no build version available.
The main function of the program is to allow users to load their profile and preview their stats with various gear.

## Features
### 1. Profile Loading
- Users can Enter their profile name or UUID along with a valid API Key to load in their player stats and current equipped gear. Only the weapon in their first inventory slot will be loaded.
- #### What Will be Loaded:
- Skill levels and their bonuses
- Slayer Bonuses
- Melody progress (for intelligence bonus)
- Bestiary progress (for health bonus)
- All equipped gear along with enchants,books,stars,reforge,recombobulated
- Talisman Bag for magical power (with duplicate detection) along with any stat values or enrichment values
- #### What Will NOT be Loaded:
- Pet score (for magic find)
- Community upgrades (because its very limited in API)
- Essence shop bonuses