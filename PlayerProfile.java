import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.nullicorn.nedit.NBTReader;
import me.nullicorn.nedit.type.NBTCompound;
import me.nullicorn.nedit.type.NBTList;

public class PlayerProfile {

    final int BOOTS_INDEX = 0;
    final int LEGGINGS_INDEX = 1;
    final int CHESTPLATE_INDEX = 2;
    final int HELMET_INDEX = 3;
    final int NECKLACE_INDEX = 4;
    final int CLOAK_INDEX = 5;
    final int BELT_INDEX = 6;
    final int GAUNTLET_INDEX = 7;
    final int WEAPON_INDEX = 8;

    boolean godPotionEnabled = false;
    boolean useDungeonStats = false;
    boolean refreshAll = false;

    int fairySouls = 0;
    int skyBlockLevel = 0;
    int bestiaryLevel = 0;
    double magicalPower = 0;
    int weaponDamage = 0;
    int mageDamage = 0;


    int goldCollection = 0;              //TODO: user input
    long bankBalance = 100000000 * 10;        //TODO: user input
    int enabledPetAbilities [] = {1,1,1,1};      // 1 is enabled, 2 is not

    double enderSlayerBonus = 0;
    double impalingBonus = 0;
    double dragonBonus = 0;
    double smiteBonus = 0;
    double baneBonus = 0;
    double cubismBonus = 0;

    Long selectedMobHealth = 0L;
    double globalStatBoost = 0.0;
    double magicalMultiiplier = 0;
    double petBaseAdditive = 0;
    double weaponAdditive = 0;
    double weaponMultiplier = 1;
    double armorAdditive = 0;
    double armorMultiplier = 1;
    double petFirstHit = 0;
    double finalDamage = 0;
    double mobAdditiveBoost = 0;
    double mobMultiBoost = 1;
    double weaponAbilityDamage = 0;
    double playerAbilityDamage = 0;
    double tempDamage = 0;
    double tempStrength = 0;
    double tempcritDamage = 0;
    double tempSpeed = 0;
    double tempDefense = 0;
    double tempHealth = 0;
    double tempIntelligence = 0;
    double strengthOnHit = 0;
    double petMultiplier = 1;
    double critEffectiveness = 1;
    double abilityBoost = 1;
    double speedCap = 400;
    double dungeonModifier = 0.0;
    double finalDamageMulti = 1.0;
    double petFinalDamageMulti = 0.0;

    int mainProfileIndex = 0;

    String UUID = "";
    String selectedPowerStone = "None";
    String selectedMob = "None";
    String petName = "None";
    String petItem = "None";
    Rarity petRarity = Rarity.COMMON;

    // magic weapons that do not get base multiplier boost 
    String excludedMagicWeapons [] = {"aspect of the dragons","golem sword"}; 

    ArrayList<String> armorReforges = new ArrayList<>(Arrays.asList(""));
    ArrayList<String> equipmentReforges = new ArrayList<>(Arrays.asList(""));
    ArrayList<String> bowReforges = new ArrayList<>(Arrays.asList(""));
    ArrayList<String> swordReforges = new ArrayList<>(Arrays.asList(""));
    ArrayList<String> swordEnchants = new ArrayList<>(Arrays.asList());
    ArrayList<String> bowEnchants = new ArrayList<>(Arrays.asList());

    ArrayList<InventoryItem> playerGear;
    ArrayList<String> playerTalismans;

    /*
     * these two list are used in conjunction to keep track of stats added by chosen reforge power
     * They will maintain equal size and each index will correlate
     * This is to easily remove or update them to the global stats
     */
    ArrayList<String> powerStoneStats = new ArrayList<>();
    ArrayList<Double> powerStoneValues = new ArrayList<>();

    Map<String,Double> statTotals;
    Map<String,Double> staticStats = createBaseStats();
    Map<String,Double> petStats = createBaseStats();

    /**
     * List of stats gained from skills and skyblock level
     */
    Map<String,Double> miscStats = createBaseStats();
    Map<String,Double> globalStatModifiers = createGlobalModifers();

    Map<String,Integer> skillLevels = new HashMap<>();

    int petLevel = 1;
    int petTier = 1;

    JSONObject playerApi;
    JSONObject playerProfile;
    JSONObject hypixelValue;
    JSONObject skillsApiInfo;
    JSONObject hypixelEnchants;

    Map<String, JSONObject> allItems;
    Map<String, JSONObject> accessoryItems;

    enum Rarity {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC, DIVINE, SPECIAL, VERY ,VERY_SPECIAL{
            @Override
            public Rarity next(){       //have very special return itself in case there's a call for it's next rarity
                return values()[ordinal()];
            }
        };

        public Rarity next() {
            return values()[ordinal() + 1];
        }
    }

    PlayerProfile(){
        initInventorySlots();
        playerTalismans = new ArrayList<>();
        allItems = new LinkedHashMap<String, JSONObject>();

        staticStats = createProfileStats();

        // set default profile stat values
        statTotals = createProfileStats();
        statTotals.put("HEALTH", 100.0);
        statTotals.put("WALK_SPEED", 100.0);
        statTotals.put("INTELLIGENCE", 0.0);           // TODO: temp val for defuse kit
        statTotals.put("CRITICAL_CHANCE", 30.0);
        statTotals.put("CRITICAL_DAMAGE", 50.0);

        // add skill levels to Map
        skillLevels.put("FARMING", 0);
        skillLevels.put("MINING", 0);
        skillLevels.put("COMBAT", 0);
        skillLevels.put("FORAGING", 0);
        skillLevels.put("FISHING", 0);
        skillLevels.put("ENCHANTING", 0);
        skillLevels.put("ALCHEMY", 0);
        skillLevels.put("CARPENTRY", 0);
        skillLevels.put("TAMING", 0);
        skillLevels.put("CATACOMBS", 0);
    }

    /**
     * @return Map with default values for each tracked stat.
     */
    public Map<String,Double> createBaseStats(){
        Map<String,Double> newStats = new LinkedHashMap<>();
        newStats.put("HEALTH", 0.0);
        newStats.put("DEFENSE", 0.0);                
        newStats.put("INTELLIGENCE", 0.0);           
        newStats.put("DAMAGE", 0.0);
        newStats.put("STRENGTH", 0.0);               
        newStats.put("CRITICAL_CHANCE", 0.0);
        newStats.put("CRITICAL_DAMAGE", 0.0);
        newStats.put("ATTACK_SPEED", 0.0);
        newStats.put("FEROCITY", 0.0);
        newStats.put("WALK_SPEED", 0.0);
        newStats.put("MAGIC_FIND", 0.0);
        newStats.put("TRUE_DEFENSE", 0.0);
        newStats.put("ABILITY_DAMAGE_PERCENT", 0.0);
        return newStats;
    }

    /** Create a linked hash map for main profile stats with specific ordering.
     * @return Map with default values for profile stat order.
     */
    public Map<String,Double> createProfileStats(){
        Map<String,Double> newStats = new LinkedHashMap<>();
        newStats.put("HEALTH", 0.0);
        newStats.put("DEFENSE", 0.0);   
        newStats.put("WALK_SPEED", 0.0);
        newStats.put("DAMAGE", 0.0);
        newStats.put("STRENGTH", 0.0);               
        newStats.put("INTELLIGENCE", 0.0);           
        newStats.put("CRITICAL_CHANCE", 0.0);
        newStats.put("CRITICAL_DAMAGE", 0.0);
        newStats.put("ATTACK_SPEED", 0.0);
        newStats.put("ABILITY_DAMAGE_PERCENT", 0.0);
        newStats.put("MAGIC_FIND", 0.0);
        newStats.put("TRUE_DEFENSE", 0.0);
        newStats.put("FEROCITY", 0.0);
        newStats.put("MAGICAL_POWER", 0.0);
        return newStats;
    }

    public void setSkyblockLevel(int level){
        skyBlockLevel = level;
    }

    public int getSkyBlockLevel(){
        return skyBlockLevel;
    }

    public Map<String,Double> createGlobalModifers(){
        Map<String,Double> temp = createBaseStats();
        Map<String,Double> globalMods = new HashMap<>();

        for (Entry<String,Double> stat : temp.entrySet()){
            if (!stat.getKey().equals("DAMAGE")) 
                globalMods.put(stat.getKey(), 1.0);
        }
        return globalMods;
    }

    public void addToAllGlobalModifers(double amount){
        for (Entry<String,Double> statModifer : globalStatModifiers.entrySet()){
            globalStatModifiers.compute(statModifer.getKey(), (key, val) -> val + amount);
        }
    }

    public void addToGlobalModifer(String stat, double amount){
        globalStatModifiers.computeIfPresent(stat, (key, val) -> val + amount);
    }

    public void resetGlobalStatModifer(){
        speedCap = 400;

        // remove all stat modifers
        for (Entry<String,Double> stat : globalStatModifiers.entrySet()){
            statTotals.put(stat.getKey(), statTotals.get(stat.getKey()) / stat.getValue());
        }
        
        // set each to 1
        for (Entry<String,Double> statModifer : globalStatModifiers.entrySet()){
            globalStatModifiers.put(statModifer.getKey(), 1.0);
        }

    }

    public void addStatMultipliers(){
        for (Entry<String,Double> stat : globalStatModifiers.entrySet()){
            statTotals.put(stat.getKey(), statTotals.get(stat.getKey()) * stat.getValue());
        }
    }

    public Integer getSkillLevel(String skill){
        return skillLevels.get(skill.toUpperCase());
    }

    public void setSkillLevel(String skill, Integer level){
        skillLevels.put(skill.toUpperCase(), level);
    }

    public void addItemList(Map<String, JSONObject> allItems, Map<String, JSONObject> accessoryItems ){
        this.allItems = allItems;
        this.accessoryItems = accessoryItems;
    }

    public void setSkillsApiMilestones(JSONObject skillsApiInfo){
        this.skillsApiInfo = skillsApiInfo;
    }

    /**
     * Set available reforge and enchant pools from Hypixel JSON
     * @param hypixelValue Json object of reforges and enchants.
     */
    public void setCustomValues(JSONObject hypixelValue){
        this.hypixelValue = hypixelValue;
        JSONArray armorReforgeList = hypixelValue.getJSONObject("Reforge").getJSONObject("armor").names();
        JSONArray equipmentReforgeList = hypixelValue.getJSONObject("Reforge").getJSONObject("equipment").names();
        JSONArray swordReforgeList = hypixelValue.getJSONObject("Reforge").getJSONObject("sword").names();
        JSONArray bowReforgeList = hypixelValue.getJSONObject("Reforge").getJSONObject("bow").names();
        JSONArray bowEnchantList = hypixelValue.getJSONObject("Enchantments").getJSONObject("bow").names();
        JSONArray swordEnchantList = hypixelValue.getJSONObject("Enchantments").getJSONObject("sword").names();
        hypixelEnchants = hypixelValue.getJSONObject("Enchantments");

        for (int i = 0; i < armorReforgeList.length(); i++){
            armorReforges.add(armorReforgeList.get(i).toString());
        }
        for (int i = 0; i < equipmentReforgeList.length(); i++){
            equipmentReforges.add(equipmentReforgeList.get(i).toString());
        }
        for (int i = 0; i < swordReforgeList.length(); i++){
            swordReforges.add(swordReforgeList.get(i).toString());
        }
        for (int i = 0; i < bowReforgeList.length(); i++){
            bowReforges.add(bowReforgeList.get(i).toString());
        }
        for (int i = 0; i < bowEnchantList.length(); i++){
            bowEnchants.add(bowEnchantList.get(i).toString());
        }
        for (int i = 0; i < swordEnchantList.length(); i++){
            swordEnchants.add(swordEnchantList.get(i).toString());
        }


        // TODO: remove reforges exclusive to a certain gear type to treat armorReforges as a list of all compatible reforges
        armorReforges.remove("loving");
        armorReforges.remove("ridiculous");

        Collections.sort(armorReforges);
        Collections.sort(equipmentReforges);
        Collections.sort(swordReforges);
        Collections.sort(bowReforges);
        Collections.sort(bowEnchants);
        Collections.sort(swordEnchants);

        // add reference to enchants in tooltip for dynamic levels
        playerGear.get(0).toolTip.setEnchantReference(hypixelEnchants, "boots");
        playerGear.get(1).toolTip.setEnchantReference(hypixelEnchants, "leggings");
        playerGear.get(2).toolTip.setEnchantReference(hypixelEnchants, "chestplate");
        playerGear.get(3).toolTip.setEnchantReference(hypixelEnchants, "helmet");
        playerGear.get(8).toolTip.setEnchantReference(hypixelEnchants, "sword");

        setItemPools();
    }

    public void setPlayerApi(JSONObject playerApi, String UUID){
        this.playerApi = playerApi;
        this.UUID = UUID;
    }

    public void useDungeonStats(boolean status){
        resetGlobalStatModifer();
        useDungeonStats = status;
        refreshAll = true;
        refreshGear();
    }

    void initInventorySlots(){
        playerGear = new ArrayList<>();
        for (int gearSlots = 1; gearSlots <= 9; ++gearSlots){
            playerGear.add(new InventoryItem());
        }
    }

    public void initCustomProfile(){
        calcDamage(0L);
    }

    public void parsePlayerProfile() throws JSONException, IOException{

        setProfileIndex();
        playerProfile = playerApi.getJSONArray("profiles").getJSONObject(mainProfileIndex).getJSONObject("members").getJSONObject(UUID);

        fairySouls = playerProfile.getInt("fairy_souls_collected") - playerProfile.getInt("fairy_souls");
        int farmingXP = playerProfile.getInt("experience_skill_farming");
        int miningXP = playerProfile.getInt("experience_skill_mining");
        int combatXP = playerProfile.getInt("experience_skill_combat");
        int foragingXP = playerProfile.getInt("experience_skill_foraging");
        int fishingXP = playerProfile.getInt("experience_skill_fishing");
        int enchantingXP = playerProfile.getInt("experience_skill_enchanting");
        int alchemyXP = playerProfile.getInt("experience_skill_alchemy");
        int carpentryXP = playerProfile.getInt("experience_skill_carpentry");
        int tamingXP = playerProfile.getInt("experience_skill_taming");
        int catacombsXP = playerProfile.getJSONObject("dungeons").getJSONObject("dungeon_types").getJSONObject("catacombs").getInt("experience");

        skillLevels.put("FARMING", findSkillLevel("FARMING", farmingXP));
        skillLevels.put("MINING", findSkillLevel("MINING", miningXP));
        skillLevels.put("COMBAT", findSkillLevel("COMBAT", combatXP));
        skillLevels.put("FORAGING", findSkillLevel("FORAGING", foragingXP));
        skillLevels.put("FISHING", findSkillLevel("FISHING", fishingXP));
        skillLevels.put("ENCHANTING", findSkillLevel("ENCHANTING", enchantingXP));
        skillLevels.put("ALCHEMY", findSkillLevel("ALCHEMY", alchemyXP));
        skillLevels.put("CARPENTRY", findSkillLevel("CARPENTRY", carpentryXP));
        skillLevels.put("TAMING", findSkillLevel("TAMING", tamingXP));
        skillLevels.put("CATACOMBS",  findSkillLevel("Catacombs", catacombsXP));
        
        getDungeonModifier();

        bestiaryLevel = getBestiaryLevel();

        // calculate all skill bonuses from skills and skyblock level and store them in miscStats
        getSkillStatsBoost();
        skyBlockLevelBoost();

        // add all calculated bonuses to totals
        addToTotal(miscStats);

        getSlayerBonus();
        parseTalismanBag();
        applyTuningValues();
        addPowerStoneStats();
        getMelodyIntelligence();

        NBTCompound invArmor = NBTReader.readBase64(
                    playerApi.getJSONArray("profiles").
                    getJSONObject(mainProfileIndex).
                    getJSONObject("members").
                    getJSONObject(UUID).
                    getJSONObject("inv_armor").
                    getString("data"));

        NBTCompound invEquipment = NBTReader.readBase64(
                    playerApi.getJSONArray("profiles").
                    getJSONObject(mainProfileIndex).
                    getJSONObject("members").
                    getJSONObject(UUID).
                    getJSONObject("equippment_contents").
                    getString("data"));

        NBTCompound inventory = NBTReader.readBase64(
                    playerApi.getJSONArray("profiles").
                    getJSONObject(mainProfileIndex).
                    getJSONObject("members").
                    getJSONObject(UUID).
                    getJSONObject("inv_contents").
                    getString("data"));

        JSONObject collections = playerApi.getJSONArray("profiles").
                    getJSONObject(mainProfileIndex).
                    getJSONObject("members").
                    getJSONObject(UUID).
                    getJSONObject("collection");

        // try to set gold collection for golden dragon pet ability
        if (collections.has("GOLD_INGOT"))     
            goldCollection = collections.getInt("GOLD_INGOT");
        
        NBTCompound currentInventory = invArmor;

        // load equipped gear
        int playerGearIndex = 0;
        for (int i = 0 ; i < 2; ++i){
            for (int apiIndex = 0; apiIndex < 4; ++apiIndex){
                if (currentInventory.getList("i").getCompound(apiIndex).size() != 0)
                    setArmorFromApi(playerGear.get(playerGearIndex), currentInventory.getList("i").getCompound(apiIndex));
                ++playerGearIndex;
            }

            // get equipment slots
            currentInventory = invEquipment;
        }

        // setItemStats() will make sure its an allowable "weapon"
        if (inventory.getList("i").getCompound(0).size() != 0)
            setArmorFromApi(playerGear.get(playerGearIndex), inventory.getList("i").getCompound(0));

        setActivePet();
        getPetStats();
        addStatMultipliers();
        calcDamage(selectedMobHealth);
    }

    /**
     * Set Initial pools for gear slots.
     */
    public void setItemPools(){
        // TODO: get special reforges in somehow (list is already sorted)
        // weapon is not set because it depends on the selected item
        JSONObject globalArmorEnchants = hypixelEnchants.getJSONObject("armor");
        JSONObject categoryEnchants = null;
        ArrayList<String> enchantPool;
        for (int gearIndex = 0; gearIndex < 8; ++gearIndex){

            // gear here is equipment with no enchants so just add reforges 
            if (gearIndex > 3){
                playerGear.get(gearIndex).setReforgePool(equipmentReforges, "equipment");
                continue;
            }

            enchantPool = new ArrayList<>();

            // add global enchants
            for (String enchant : globalArmorEnchants.keySet()){
                enchantPool.add(enchant);
            }
            switch (gearIndex){
                case BOOTS_INDEX : 
                    categoryEnchants = hypixelEnchants.getJSONObject("boots");
                    break;
                case LEGGINGS_INDEX : 
                    categoryEnchants = hypixelEnchants.getJSONObject("leggings");
                    break;
                case CHESTPLATE_INDEX : 
                    categoryEnchants = hypixelEnchants.getJSONObject("chestplate");
                    break;
                case HELMET_INDEX : 
                    categoryEnchants = hypixelEnchants.getJSONObject("helmet");
                    break;
            }

            // add category specific enchants
            for (String enchant : categoryEnchants.keySet()){
                enchantPool.add(enchant);
            }

            Collections.sort(enchantPool);
            playerGear.get(gearIndex).setEnchantPool(enchantPool);
            playerGear.get(gearIndex).setReforgePool(armorReforges, "armor");
        }
            ArrayList<String> emptyReforge = new ArrayList<>(Arrays.asList(""));
            playerGear.get(8).setReforgePool(emptyReforge, "sword");
    }

    void setWeaponModifierPool(InventoryItem inventoryItem, String reforge){
        if (inventoryItem.getName().equals(""))
            return;

        JSONObject itemReference = allItems.get(inventoryItem.getName());
        String itemCategory = "";                                // get reforge in case the user changed it 
        if (itemReference.has("category")){
            if (inventoryItem.getToolTip().isDisabled())
                inventoryItem.getToolTip().enableModifiers();

            itemCategory = itemReference.getString("category"); 
            if (itemCategory.equals("SWORD") || itemCategory.equals("FISHING_WEAPON")){
                inventoryItem.setEnchantPool(swordEnchants);
                inventoryItem.setReforgePool(swordReforges, "sword");
            }
            else if (itemCategory.equals("BOW")){
                inventoryItem.setEnchantPool(bowEnchants);
                inventoryItem.setReforgePool(bowReforges, "bow");
            }
            inventoryItem.setReforge(reforge);
        }

        // disable tooltip if there is no category 
        else {
            inventoryItem.getToolTip().disableModifiers();
            inventoryItem.setEnchantPool(null);
            inventoryItem.setReforgePool(null, "");
        }
    }
    /**
     * Reads in necessary information on a player's armor piece from the inventory API.
     * @param inventoryItem
     * @param armorSlot
     */
    void setArmorFromApi(InventoryItem inventoryItem, NBTCompound armorSlot){
        JSONObject itemReference = null;
        String itemCategory = "";
        String reforge = "";
        NBTCompound extraAttributes = armorSlot.getCompound("tag.ExtraAttributes");
        NBTCompound enchantments = extraAttributes.getCompound("enchantments");
        
        // get item's name along with any reforge that gets removed later
        String itemName = armorSlot.getCompound("tag.display").
                                    getString("Name").replaceAll("??[a-zA-Z0-9]|[^a-zA-Z0-9 ']", "").toLowerCase().trim();

        // set reforge and remove it from the item's name if it exist
        if (extraAttributes.containsKey("modifier")){
            itemName = itemName.replace(extraAttributes.getString("modifier") + " ", "").toLowerCase();
            reforge = extraAttributes.getString("modifier");
        }          
        
        // after item's name has been parsed make sure its in the available item list if not return from function
        if (!allItems.containsKey(itemName)) return;

        // set item reference from list of available items
        itemReference = allItems.get(itemName);  

        // set item's name
        inventoryItem.setName(itemName);

        // check if dungeon tiered item and set the item's quality
        if (extraAttributes.containsKey("item_tier"))
            inventoryItem.setDungeonTier(extraAttributes.getInt("item_tier", 0),extraAttributes.getInt("baseStatBoostPercentage", 0) / 100.0 + 1);
        
        // add hot potato books
        if (extraAttributes.containsKey("hot_potato_count"))
            inventoryItem.setPotatoBooks(extraAttributes.getInt("hot_potato_count", 0));

        // set stars if applicable 
        if (extraAttributes.containsKey("upgrade_level"))
            inventoryItem.setStars(extraAttributes.getInt("upgrade_level", 0));
        else if (extraAttributes.containsKey("dungeon_item_level"))
            inventoryItem.setStars(extraAttributes.getInt("dungeon_item_level", 0));

        // set item's category and its category for which reforges to pool from
        if (itemReference.has("category")){
            itemCategory = itemReference.getString("category");
            inventoryItem.setCategory(itemCategory);
        }


        // add weapon enchants/reforges to list of selectables on tooltip
        setWeaponModifierPool(inventoryItem,reforge);

        // store enchantments on the item if they're in the master JSON of hypixel values
        if (enchantments != null){
        for (Entry<String, Object> newEnchant: enchantments.entrySet()){
            String enchantName = newEnchant.getKey().toString().toLowerCase();
                if (inventoryItem.getEnchantPool() != null && inventoryItem.getEnchantPool().contains(enchantName))
                    inventoryItem.addEnchant(enchantName, newEnchant.getValue().toString());
            }
        }

        // check for the item's rarity if one is not specified default to common
        if (itemReference.has("tier"))
            inventoryItem.setRarity(itemReference.get("tier").toString());
        else {
            inventoryItem.setRarity("COMMON");
        }

        // set recombob
        if (extraAttributes.containsKey("rarity_upgrades"))
            inventoryItem.setRecombobulated(true);
        
        // if item is a dungeon item
        if (itemReference.has("dungeon_item"))
            inventoryItem.setTieredItem();

        // apply values from stored attributes and item reference 
        applyArmorStats(inventoryItem, itemReference);

        // read the stats from the item into global stats
        readItemStats(inventoryItem);
    }

    ArrayList<String> jArrayToList(JSONArray jArray){
        ArrayList<String> newList = new ArrayList<>();
        for (int i = 0; i < jArray.length(); i++){
            newList.add(jArray.get(i).toString());
        }
        return newList;
    }
    
    void parseTalismanBag() throws JSONException, IOException{
        JSONArray tieredList = hypixelValue.getJSONObject("Accessories").getJSONArray("tiered");
        JSONArray dynamicList = hypixelValue.getJSONObject("Accessories").getJSONArray("dynamic");
        JSONObject talismanSets = hypixelValue.getJSONObject("Accessories").getJSONObject("sets");
    
        ArrayList<String> tieredTalismanKeys = jArrayToList(tieredList);
        ArrayList<String> dynamicTalisman = jArrayToList(dynamicList);

        // this array is reserved for talisman that share the same keyword for a tiered talisman set but are not a part of a tiered set 
        ArrayList<String> blackListedTalisman = new ArrayList<>(Arrays.asList("WOLF_PAW"));
        Map<String,String> loreStatsToRead = new HashMap<>();

        Map <String, Rarity> heldTalisman = new LinkedHashMap<>();
        
         // read talisman bag contents
         NBTCompound talismanBag = NBTReader.readBase64(
            playerApi.getJSONArray("profiles").
            getJSONObject(mainProfileIndex).
            getJSONObject("members").
            getJSONObject(UUID).
            getJSONObject("talisman_bag").
            getString("data"));

        NBTList talismanArray = talismanBag.getList("i");
        NBTCompound extraAttributes;

        String talismanLore;
        String currentTalismanID;
        JSONObject talismanReference;

        // add all currently held talisman to a Map to be filtered for dupes
        for (int talismanArrayIndex = 0; talismanArrayIndex < talismanArray.size(); ++talismanArrayIndex){
            if (talismanArray.getCompound(talismanArrayIndex).size() == 0)   // get next talisman if current index is empty
                continue;

            talismanLore = talismanArray.getCompound(talismanArrayIndex).getList("tag.display.Lore").toString();
            extraAttributes = talismanArray.getCompound(talismanArrayIndex).getCompound("tag.ExtraAttributes");
            currentTalismanID = extraAttributes.getString("id");
            talismanReference = accessoryItems.get(currentTalismanID);

            if (currentTalismanID.contains("HAT_CRAB"))
                currentTalismanID = "PARTY_HAT_CRAB";

            // defualt to common if reference item does not specify
            Rarity talismanRarity = Rarity.COMMON;

             // if an item reference exist check for a rarity and any stats
             if (talismanReference != null){

                // if there is an item Reference take the value of its "tier" and convert it to Rarity
                if (talismanReference.has("tier") && !dynamicTalisman.contains(currentTalismanID))
                    talismanRarity = Rarity.valueOf(accessoryItems.get(currentTalismanID).get("tier").toString());
                
                // if not read the rarity from the lore
                else {
                    // TODO: sometimes the new years bag crashes don't know why/
                    try {
                        talismanRarity = Rarity.valueOf(getRarityFromLore(talismanLore));
                    } catch (Exception e) {
                    }
                }
            }
            /*
             * else get the Rarity from the item's lore and add the item to the list of all talisman
             * 
             * Originally added for PARTY_CRAB_HAT_ANIMATED but I just added an extra check for that case since it's an exception to the overall scheme
             * as it does not exist in the api item list but the CRAB_HAT does and they're virtually the same
             * Left in for just in casies :)
             */
            else {
                talismanRarity = Rarity.valueOf(getRarityFromLore(talismanLore));    //if item reference does not exist get rarity from lore
                JSONObject newTalisman = new JSONObject();
                newTalisman.put("tier", talismanRarity.name());
                accessoryItems.put(currentTalismanID, newTalisman);
                loreStatsToRead.put(currentTalismanID, talismanLore);
            } 

            // check for recombob and get the next rarity
            if (extraAttributes.containsKey("rarity_upgrades"))
                talismanRarity = talismanRarity.next();

            // skip this talisman and continue parsing if it already exist and is a greater rarity version (meaning recombob)
            if (heldTalisman.containsKey(currentTalismanID) && heldTalisman.get(currentTalismanID).ordinal() >= talismanRarity.ordinal())
                continue;

             // if talisman has enrichment add it to list of talisman that need lore stats read 
             if (extraAttributes.containsKey("talisman_enrichment"))
                loreStatsToRead.put(currentTalismanID, talismanLore);
             
            // check if accessory has variable stats that needs to be read from lore
            for (String itemID : dynamicTalisman){
                if (currentTalismanID.contains(itemID)){
                    loreStatsToRead.put(currentTalismanID, talismanLore);
                }
            }
            heldTalisman.put(currentTalismanID, talismanRarity);
        }

        // filter dupes/tiered talisman out (will take highest base rarity talisman out of the tiered ones)
        for (String accessory : tieredTalismanKeys){
            ArrayList<String> dupes = (ArrayList<String>) heldTalisman.keySet().stream()
                                                                      .filter(acc->acc.contains(accessory) && !(blackListedTalisman.contains(acc)) && acc.lastIndexOf("_") == accessory.lastIndexOf("_"))
                                                                      .collect(Collectors.toList());

            // if dupes are detected remove lowest rarity ones. Prioritizes first in acc bag                    
            if (dupes.size() > 1){
                String highestTalisman = dupes.get(0);
                while (dupes.size() > 1){
                    for (int index = 1; index < dupes.size(); ++index){
                        if (heldTalisman.get(dupes.get(index)).ordinal() < heldTalisman.get(highestTalisman).ordinal()){
                            heldTalisman.remove(dupes.get(index));
                            dupes.remove(dupes.get(index));
                        }

                        else {
                            String temp = highestTalisman;
                            heldTalisman.remove(highestTalisman);
                            highestTalisman = dupes.get(index);
                            dupes.remove(temp);
                        }
                    }
                }
            }
        }

        // check for dupes of Talisman sets
        for (String set : talismanSets.keySet()){
            JSONObject currentSet = talismanSets.getJSONObject(set);
            JSONArray talismanInSet = currentSet.names();
            ArrayList<String> dupes = new ArrayList<>();

            // find and add set dupes
            for (int talismanIndex = 0; talismanIndex < talismanInSet.length(); talismanIndex++){
                if (heldTalisman.containsKey(talismanInSet.get(talismanIndex))){
                    dupes.add((String) talismanInSet.get(talismanIndex));
                }
            }
            // filter out lower rarity
            if (dupes.size() > 1){
                String highestTalisman = dupes.get(0);
                while (dupes.size() > 1){
                    for (int index = 1; index < dupes.size(); ++index){
                        if (heldTalisman.get(dupes.get(index)).ordinal() < heldTalisman.get(highestTalisman).ordinal()){
                            heldTalisman.remove(dupes.get(index));
                            dupes.remove(dupes.get(index));
                        }
                        else{
                            String temp = highestTalisman;
                            heldTalisman.remove(highestTalisman);
                            highestTalisman = dupes.get(index);
                            dupes.remove(temp);
                        }
                    }
                }
            }
        }

        // remove dupes from map of talisman that need lorestats read
        for (String talisman : loreStatsToRead.keySet()){
            if (!heldTalisman.containsKey(talisman)){
                loreStatsToRead.remove(talisman);
            }
        }

        // check all remanining talisman for base stats and read those in
        for (Entry<String,Rarity> talisman : heldTalisman.entrySet()){
            JSONObject reference = accessoryItems.get(talisman.getKey());
            if (reference.has("stats") && !loreStatsToRead.containsKey(talisman.getKey())){
                JSONObject talismanStats = reference.getJSONObject("stats");
                for (String stat : talismanStats.keySet()){
                    Double statValue = talismanStats.getDouble(stat);
                    addToStats(statTotals,stat, statValue);
                }
            }
        }

        // read all lore stats
        for (Entry<String,String> lore : loreStatsToRead.entrySet()){
            readLoreStats(lore.getValue(), lore.getKey());
        }

         for (Entry<String, Rarity> filteredTalismans : heldTalisman.entrySet()){
            switch(filteredTalismans.getValue().name()){
                case "COMMON": 
                    magicalPower += 3;
                    break;
                case "UNCOMMON": 
                    magicalPower += 5;
                    break;
                case "RARE":
                    magicalPower += 8;
                    break;
                case "EPIC": 
                    magicalPower += 12;
                    break;
                case "LEGENDARY": 
                    magicalPower += 16;
                    break;
                case "MYTHIC": 
                    magicalPower += 22;
                    break;
                case "SPECIAL": 
                    magicalPower += 3;
                    break;
                case "VERY": 
                    magicalPower += 5;
                    break;
                default:
                    break;
            }
         }
         
        addToStats(statTotals,"MAGICAL_POWER", magicalPower);
        magicalMultiiplier = getMagicalMultiplier(magicalPower);
    }
    
    /**
     * implements a regex replace of the string to easily index the rarity location
     */
    String getRarityFromLore(String lore){
        String loreLine = lore.replaceAll(".*???[a-z](?=[A-Z])", "");
        return loreLine.substring(0, loreLine.indexOf(" "));
    } 

    /**
     * implements a regex replace of the string to filter the contents to easily find stats via substring index
     */
    void readLoreStats(String lore, String talismanID){
        String filteredLore = lore.replaceAll("??[a-zA-z0-9]|\\([^()]*\\)|[^a-zA-Z0-9 +.]", "").replaceAll("\s+", " ");
        String statName = "";
        Double statValue = 0.0;
        String statsSplit [] = filteredLore.split("(?<=\\+[.0-9]*)\s|(?<=\\.)\s");
        // manually read the bonus from the new years cake bag since it does not conform to regualr stat conventions in talismen
        if (talismanID.equals("NEW_YEAR_CAKE_BAG")){
            try {
                statValue = Double.parseDouble(filteredLore.substring(filteredLore.lastIndexOf("+"),filteredLore.lastIndexOf("H") - 1));
                addToStats(statTotals,"HEALTH", statValue);
                return;
            } catch (Exception e) {
                return;
            }
        }
        // read each line in the split string looking for a stat and try to add it (if the line contains a "+" denoting a possible value)
        for (String newStat : statsSplit){
                if (newStat.contains("+")){
                    if (newStat.contains("Speed"))
                        newStat = newStat.replace("Speed", "WALK_SPEED");
                    statName = newStat.substring(0, newStat.indexOf("+") - 1).replaceAll(" ", "_").toUpperCase();
                    statValue = Double.parseDouble(newStat.substring(newStat.indexOf("+") + 1, newStat.length()));
                    addToStats(statTotals,statName, statValue);
                }
            
        }
    }

    /**
     * Applys the numerical values associated with an item's attributes along with the values in the item reference.
     */
    //TODO: do not show reforges etc. for fishing rods 
    void applyArmorStats(InventoryItem equippedItem, JSONObject itemReference){
        String itemRarity = "";
        JSONObject reforgeValues = hypixelValue.getJSONObject("Reforge");
        int potatoBooks = equippedItem.getPotatoBooks();
        double statModifer = 1.0;
        double starMultiplier = 1.0 + (0.02 * equippedItem.getStars());

        /**
         * if not using dungeon stats multiply base stats by their star count modifier
         * else multiple them by 1 because dungeon stats do not benefit from base value increase from stars
         * an item's dunegeon stats are calculated after all values of an item including enchants are totaled
         */
        if (!useDungeonStats)
            statModifer = starMultiplier;

        if (equippedItem.getReforgeCategory() != null)
            reforgeValues = reforgeValues.getJSONObject(equippedItem.getReforgeCategory());
        else {
            reforgeValues = null;
        }

        if (itemReference.has("tier"))
            equippedItem.setRarity(itemReference.get("tier").toString());
        else {
            equippedItem.setRarity("COMMON");
        }
        itemRarity = equippedItem.getRarity();

        if (itemReference.has("ability_damage_scaling"))
            equippedItem.addStat("ABILITY_DAMAGE_SCALING", itemReference.getDouble("ability_damage_scaling"));

        // set item material, currently only to check if its gold for a pet ability
        if (itemReference.has("material"))
            equippedItem.setMaterial(itemReference.getString("material"));

        // make sure item has a reforge and its in the hypixel values json
        if(reforgeValues != null && !equippedItem.getReforge().equals("") && reforgeValues.has(equippedItem.getReforge())){
            reforgeValues = reforgeValues.getJSONObject(equippedItem.getReforge());

            // if rarity does not exists i.e. it is special/very special default to mythic rarity
            if (reforgeValues.has(itemRarity)){
                reforgeValues = reforgeValues.getJSONObject(itemRarity);
            }
            else {
                reforgeValues = reforgeValues.getJSONObject("MYTHIC");
            }
            
            // add reforge stats
            for (String reforgeStat : reforgeValues.keySet()){     
                equippedItem.addStat(reforgeStat, reforgeValues.getDouble(reforgeStat));
            }

            if (potatoBooks != 0){
                if (equippedItem.getReforgeCategory().equals("armor")){
                    equippedItem.addStat("HEALTH", 4 * potatoBooks);
                    equippedItem.addStat("DEFENSE", 2 * potatoBooks);
                }
                else{
                    equippedItem.addStat("DAMAGE", 2 * potatoBooks);
                    equippedItem.addStat("STRENGTH", 2 * potatoBooks);
                }
            } 
        }

        // check items for special dynamic attributes that have an effect on its stats
        //TODO: have this work with the special input field UI element
        if (equippedItem.getName().equals("hyperion")){
            equippedItem.addStat("WEAPON_ABILITY_DAMAGE",10000);
            equippedItem.addStat("ABILITY_DAMAGE_SCALING", 0.3);
        }

        /**
         * if dungeon add enchants 
         * then add base stats without stars multi
         * then multiply by dungeon boost
         */

        // add item base stats for tiered dungeon items
        if (itemReference.has("tiered_stats")){
            JSONObject tieredStats = itemReference.getJSONObject("tiered_stats");
            for (String stat : tieredStats.keySet()){
                JSONArray statValue = tieredStats.getJSONArray(stat);
                if (stat.equals("WEAPON_ABILITY_DAMAGE")){
                    equippedItem.addStat(stat, Math.ceil(statValue.getDouble(0) * equippedItem.getQualityBoost()) * statModifer);
                    continue;
                }
                int tieredValue = statValue.getInt((equippedItem.getStatTier() - 1) <  0 ? 0 : equippedItem.getStatTier() - 1);
                equippedItem.addStat(stat, Math.ceil(tieredValue * equippedItem.getQualityBoost()) * statModifer);
            }
        }

        // add item base stats for all other items (some reference stats are lower case for whatever reason -_- )
        else {
            JSONObject referenceStats = null;
            if (itemReference.has("stats")){
                referenceStats = itemReference.getJSONObject("stats");
                for (String stat : equippedItem.getStats().keySet()){
                    if (referenceStats.has(stat))
                        equippedItem.addStat(stat, itemReference.getJSONObject("stats").getDouble(stat) * statModifer);
                    else if (referenceStats.has(stat.toLowerCase()))
                        equippedItem.addStat(stat, itemReference.getJSONObject("stats").getDouble(stat.toLowerCase()) * statModifer);
                }
            }
            // check for special cases here like Daedalus axe which gets its damage from taming level
            else {
                if (equippedItem.getName().equals("daedalus axe")){
                    equippedItem.addStat("DAMAGE", 4 * skillLevels.get("TAMING"));
                }
            }
        }

        // check for special cases
        if (equippedItem.getReforge().equals("ancient"))
            equippedItem.addStat("CRITICAL_DAMAGE", skillLevels.get("CATACOMBS"));

        if (equippedItem.getReforge().equals("withered"))
            equippedItem.addStat("STRENGTH", skillLevels.get("CATACOMBS"));

        if (equippedItem.getReforge().equals("loving"))
            abilityBoost = 1.05;

        if (!equippedItem.getEnchantments().isEmpty()){
            addItemEnchants(equippedItem);
        }

        // if dungeon multiply by dungeon boost
        if (useDungeonStats){
            int starBoost = equippedItem.getStars();
            double dungeonModifier = 0.0;
            if (starBoost > 5)
                starBoost = ((starBoost - 5) * 5) + 50;
            else {
                starBoost = starBoost * 10;
            }
            dungeonModifier = getDungeonModifier() + starBoost;

            for (Entry<String, Double> stat: equippedItem.getStats().entrySet()){
                // crit chance/ATK speed/ferocity/ability damage appears to only be affected by stars (up to 5)
                if (stat.getKey().equals("CRITICAL_CHANCE") || stat.getKey().equals("ATTACK_SPEED") ||
                    stat.getKey().equals("FEROCITY")  || stat.getKey().equals("ABILITY_DAMAGE_PERCENT")){

                    // if stars are > 5 add max modifier of 50% to crit chance otherwise add appropriate value depending on stars
                    if (equippedItem.getStars() > 5)
                        equippedItem.addStat(stat.getKey(), stat.getValue() * .50);
                    else {
                        equippedItem.addStat(stat.getKey(), (equippedItem.getStars() / 10.0) * stat.getValue());
                    }
                }
                else{
                    equippedItem.addStat(stat.getKey(), stat.getValue() * (dungeonModifier / 100));
                }

                // check if the item is a golden/diamond helmet and multiply the final stat by 2x
                if (equippedItem.getName().matches("(golden|diamond).+(head)")){
                    equippedItem.addStat(stat.getKey(), stat.getValue());
                }
            }
        }
    }

    void addItemEnchants(InventoryItem equippedItem){
        JSONObject HypixelEnchantValues = hypixelValue.getJSONObject("Enchantments").getJSONObject(equippedItem.getReforgeCategory());
        JSONObject itemSpecificEnchants = null;
        double enchantValue = 0.0;

        // make sure category is supported 
        if (hypixelValue.getJSONObject("Enchantments").has(equippedItem.getCategory().toLowerCase()))
            itemSpecificEnchants = hypixelValue.getJSONObject("Enchantments").getJSONObject(equippedItem.getCategory().toLowerCase());
        
        for (Map.Entry<String,String> enchant : equippedItem.getEnchantments().entrySet()){
            JSONObject valueHolder = null;
            if (HypixelEnchantValues.has(enchant.getKey()))
                valueHolder = HypixelEnchantValues;
            else {
                valueHolder = itemSpecificEnchants;
            }
            enchantValue = valueHolder.getJSONObject(enchant.getKey()).getDouble(enchant.getValue());
            switch(enchant.getKey()){
                case "growth" : 
                    equippedItem.addStat("HEALTH", enchantValue);
                    break;
                case "protection" : 
                    equippedItem.addStat("DEFENSE", enchantValue);
                    break;
                case "critical" : 
                    equippedItem.addStat("CRITICAL_DAMAGE", enchantValue);
                    break;
                //TODO: UI element to show this
                case "overload" : 
                    equippedItem.addStat("CRITICAL_DAMAGE", Double.parseDouble(enchant.getValue()));
                    equippedItem.addStat("CRITICAL_CHANCE",  Double.parseDouble(enchant.getValue()));
                    break;
            }
        }
    }
    

    /*
     * Simple function that takes the stat value to the corresponding stat via a switch statment for each possible stat
     */


    /**
     * Add value to a stat in particular group.
     * @param stats Collection of stats and their values.
     * @param statName Name of stat.
     * @param statValue Value of stat.
     */
    public void addToStats(Map<String,Double> stats, String statName ,Double statValue){
        stats.computeIfPresent(statName, (key, val) -> val + statValue);
    }

    /**
     * Add Group of stats to total values.
     * @param stats Collection of stats and their values.
     */
    void addToTotal(Map<String,Double> stats){
        for (Entry<String,Double> stat : stats.entrySet()){
            statTotals.computeIfPresent(stat.getKey(), (key, val) -> val + stat.getValue());
        }
    }

    /**
     * Apply values stored in the object to the global stats
     */
    void readItemStats(InventoryItem inventoryItem){
        Map<String,Double> itemStats = inventoryItem.getStats();
        for (Map.Entry<String,Double> currentStat : itemStats.entrySet()){
            addToStats(statTotals,currentStat.getKey(), currentStat.getValue());
        }
    }

    int getDungeonModifier(){
        JSONArray levelStatBoost = hypixelValue.getJSONObject("Catacombs").getJSONArray("StatModifier");
        return levelStatBoost.getInt(skillLevels.get("CATACOMBS"));
    }

    void getSlayerBonus(){
        JSONObject slayers = playerProfile.getJSONObject("slayer_bosses");
        int slayerLevel = 0;
        String allSlayerTypes [] = {"zombie","spider","wolf","enderman","blaze"};
        for (int slayerIndex = 0; slayerIndex < allSlayerTypes.length; ++slayerIndex ){
            String slayerType = allSlayerTypes[slayerIndex];
            switch (slayerType){
                case "zombie":
                    slayerLevel = slayers.getJSONObject(slayerType).getJSONObject("claimed_levels").length();
                    switch(slayerLevel){
                        case 9: 
                            addToStats(statTotals,"HEALTH", 6.0); 
                        case 8:
                            addToStats(statTotals,"HEALTH", 5.0); 
                        case 7:
                            addToStats(statTotals,"HEALTH", 5.0); 
                        case 6:
                            addToStats(statTotals,"HEALTH", 4.0); 
                        case 5:
                            addToStats(statTotals,"HEALTH", 4.0);
                        case 4:
                            addToStats(statTotals,"HEALTH", 3.0); 
                        case 3:
                            addToStats(statTotals,"HEALTH", 3.0);
                        case 2: 
                            addToStats(statTotals,"HEALTH", 2.0); 
                        case 1:
                            addToStats(statTotals,"HEALTH", 2.0); 
                        default:
                            break;  
                    }
                    break;
                case "spider":
                    slayerLevel = slayers.getJSONObject(slayerType).getJSONObject("claimed_levels").length();
                    switch(slayerLevel){
                        case 9: 
                            addToStats(statTotals,"CRITICAL_DAMAGE", 3.0);
                        case 8:
                            addToStats(statTotals,"CRITICAL_DAMAGE", 3.0);
                        case 7:
                            addToStats(statTotals,"CRITICAL_CHANCE", 1.0);
                        case 6:
                            addToStats(statTotals,"CRITICAL_DAMAGE", 2.0);
                        case 5:
                            addToStats(statTotals,"CRITICAL_DAMAGE", 2.0);
                        case 4:
                            addToStats(statTotals,"CRITICAL_DAMAGE", 1.0);
                        case 3:
                            addToStats(statTotals,"CRITICAL_DAMAGE", 1.0); 
                        case 2: 
                            addToStats(statTotals,"CRITICAL_DAMAGE", 1.0); 
                        case 1:
                            addToStats(statTotals,"CRITICAL_DAMAGE", 1.0); 
                        default:
                            break;  
                    }
                    break;
                case "wolf":
                    slayerLevel = slayers.getJSONObject(slayerType).getJSONObject("claimed_levels").length();
                    switch(slayerLevel){
                        case 9: 
                            addToStats(statTotals,"HEALTH", 5.0);
                        case 8:
                            addToStats(statTotals,"WALK_SPEED", 1.0);
                        case 7:
                            addToStats(statTotals,"CRITICAL_DAMAGE", 2.0);
                        case 6:
                            addToStats(statTotals,"HEALTH", 3.0); 
                        case 5:
                            addToStats(statTotals,"CRITICAL_DAMAGE", 1.0);
                        case 4:
                            addToStats(statTotals,"HEALTH", 2.0);
                        case 3:
                            addToStats(statTotals,"WALK_SPEED", 1.0);
                        case 2: 
                            addToStats(statTotals,"HEALTH", 2.0);
                        case 1:
                            addToStats(statTotals,"WALK_SPEED", 1.0);
                        default:
                            break;  
                    }
                    break;
                case "enderman":
                    slayerLevel = slayers.getJSONObject(slayerType).getJSONObject("claimed_levels").length();
                    switch(slayerLevel){
                        case 9: 
                            addToStats(statTotals,"HEALTH", 5.0);
                        case 8:
                            addToStats(statTotals,"INTELLIGENCE", 4.0);
                        case 7:
                            addToStats(statTotals,"HEALTH", 4.0); 
                        case 6:
                            addToStats(statTotals,"INTELLIGENCE", 3.0);
                        case 5:
                            addToStats(statTotals,"HEALTH", 3.0);
                        case 4:
                            addToStats(statTotals,"INTELLIGENCE", 2.0); 
                        case 3:
                            addToStats(statTotals,"HEALTH", 2.0);
                        case 2: 
                            addToStats(statTotals,"INTELLIGENCE", 2.0);
                        case 1:
                            addToStats(statTotals,"HEALTH", 1.0);
                        default:
                            break;  
                    }
                    break;
                case "blaze":
                    slayerLevel = slayers.getJSONObject(slayerType).getJSONObject("claimed_levels").length();
                    switch(slayerLevel){
                        case 9: 
                            addToStats(statTotals,"HEALTH", 7.0);
                        case 8:
                            addToStats(statTotals,"TRUE_DEFENSE", 2.0);
                        case 7:
                            addToStats(statTotals,"HEALTH", 6.0); 
                        case 6:
                            addToStats(statTotals,"STRENGTH", 2.0);
                        case 5:
                            addToStats(statTotals,"HEALTH", 5.0); 
                        case 4:
                            addToStats(statTotals,"TRUE_DEFENSE", 1.0);
                        case 3:
                            addToStats(statTotals,"HEALTH", 4.0); 
                        case 2: 
                            addToStats(statTotals,"STRENGTH", 1.0);
                        case 1:
                            addToStats(statTotals,"HEALTH", 3.0);
                        default:
                            break;  
                    }
                    break;
            }
        }
    }

    int findSkillLevel(String skill, int xp){
        int finalLevel = 0;
        JSONArray skillMilestones;

        if (skill.equals("Catacombs")){
            skillMilestones = hypixelValue.getJSONObject("Catacombs").getJSONArray("XpTable");
            for (int levelIndex = 0; levelIndex < skillMilestones.length(); ++levelIndex){
                if (xp < skillMilestones.getInt(levelIndex))
                    break;
                ++finalLevel;
            }
        }
        else{
            skillMilestones = skillsApiInfo.getJSONObject("skills").getJSONObject(skill).getJSONArray("levels");
            for (int levelIndex = 0; levelIndex < skillMilestones.length(); ++levelIndex){
                if (xp < skillMilestones.getJSONObject(levelIndex).getInt("totalExpRequired"))
                    break;
                ++finalLevel;
            }
        }

        skillLevels.put(skill.toUpperCase(), finalLevel);
        return finalLevel;
    }

    void skyBlockLevelBoost(){
        int strength = skyBlockLevel / 5;
        int health = (skyBlockLevel / 10) * 5;
        addToStats(miscStats,"HEALTH", (double) health);  // extra 5 health for 10 level milestone
        addToStats(miscStats,"STRENGTH", (double) strength);      // add 1 strength per 5 level milestone
        addToStats(miscStats,"HEALTH", skyBlockLevel * 5.0 );         // 5 health per level
    }

    void getSkillStatsBoost(){
        double strengthBonus = 0;
        double defenseBonus = 0;
        double intelligenceBonus = 0;
        double healthBonus = 0;
        if (skillLevels.get("MINING") < 15)
            defenseBonus = skillLevels.get("MINING");
        else{
            defenseBonus += ((skillLevels.get("MINING") - 14) * 2) + 14;
        }
        if (skillLevels.get("FORAGING") < 15)
            strengthBonus = skillLevels.get("FORAGING");
        else{
            strengthBonus = ((skillLevels.get("FORAGING") - 14) * 2) + 14;
        }
        if (skillLevels.get("ENCHANTING") < 15)
            intelligenceBonus = skillLevels.get("ENCHANTING");
        else{
            intelligenceBonus = ((skillLevels.get("ENCHANTING") - 14) * 2) + 14;
        }
        if (skillLevels.get("ALCHEMY") < 15)
            intelligenceBonus += skillLevels.get("ENCHANTING");
        else{
            intelligenceBonus += ((skillLevels.get("ALCHEMY") - 14) * 2) + 14;
        }
        for (int levelCap = 1; levelCap <= skillLevels.get("FARMING"); ++levelCap){
            healthBonus += 2;
            if (levelCap >= 15)
                healthBonus += 1;
            if (levelCap >= 20)
                healthBonus += 1;
            if (levelCap >= 26)
                healthBonus += 1;
        }
        for (int levelCap = 1; levelCap <= skillLevels.get("FISHING"); ++levelCap){
            healthBonus += 2;
            if (levelCap >= 15)
                healthBonus += 1;
            if (levelCap >= 20)
                healthBonus += 1;
            if (levelCap >= 26)
                healthBonus += 1;
        }

        playerAbilityDamage = skillLevels.get("ENCHANTING") * 0.5;
        addToStats(miscStats,"ABILITY_DAMAGE_PERCENT", playerAbilityDamage);
        addToStats(miscStats,"HEALTH", (skillLevels.get("CATACOMBS") * 2) + skillLevels.get("CARPENTRY") + (bestiaryLevel * 2) + healthBonus);
        addToStats(miscStats,"INTELLIGENCE",intelligenceBonus);
        addToStats(miscStats,"STRENGTH", strengthBonus);
        addToStats(miscStats,"DEFENSE", defenseBonus);
        addToStats(miscStats,"CRITICAL_CHANCE", skillLevels.get("COMBAT") * 0.5);
    }

    // Fairy Souls no longer contribute to any bonus, was converted to skyblock exp
    /**
     * void getFairySoulsBonus(){
        Double healthBonus = 0.0;
        Double strengthBonus = 0.0;
        Double defenseBonus = 0.0;
        Double speedBonus = 0.0;
        double healthIncrement = 3.0;
        for (int incrementCap = 1; incrementCap <= fairySouls/5; ++incrementCap){
            healthBonus += (int) healthIncrement;
            if (incrementCap % 5 == 0){
                strengthBonus += 2;
                defenseBonus += 2;
            }
            else{
                ++strengthBonus;
                ++defenseBonus;
            }
            if (incrementCap % 10 == 0)
                ++speedBonus;
            if (incrementCap <= 41)
                healthIncrement += 0.5;
        }

        addToStats(statTotals,"HEALTH", healthBonus);
        addToStats(statTotals,"STRENGTH", strengthBonus);
        addToStats(statTotals,"DEFENSE", defenseBonus);
        addToStats(statTotals,"WALK_SPEED", speedBonus);
    }
    */

    int getBestiaryLevel(){
        JSONObject playerBestiaryKills = playerApi.getJSONArray("profiles").getJSONObject(mainProfileIndex).
                                                    getJSONObject("members").getJSONObject(UUID).getJSONObject("bestiary");

        int genericMilestones [] = {10,25,100,250,500,1000,2500,5000,10000,25000,50000,100000};
        int bossMilestones [] = {2,5,10,20,30,40,50,75,100,150,200,300};
        int currentMilestones [] = genericMilestones;
        int totalTiers = 0;
        String mobTypes [] = {"island","generic","boss"};

        for (int mobTypeIndex = 0; mobTypeIndex < mobTypes.length; ++mobTypeIndex){
            if (mobTypes[mobTypeIndex].equals("boss"))
                currentMilestones = bossMilestones;
            JSONArray mobList = hypixelValue.getJSONObject("Bestiary").getJSONArray(mobTypes[mobTypeIndex]);
            for (int mobIndex = 0; mobIndex < mobList.length(); ++mobIndex){
                int familyKillCount;
                String bestiaryTarget = "kills_family_" + mobList.getString(mobIndex);
                if (playerBestiaryKills.has(bestiaryTarget)){
                    familyKillCount = playerBestiaryKills.getInt(bestiaryTarget);

                    // check if respective mob type and the mob's kill count is greater than the max tier before the required kills increases at a constant rate
                    if (mobTypes[mobTypeIndex].equals("island") && familyKillCount >= 500){    
                        totalTiers += 5;
                        continue;
                    }
                    else if (mobTypes[mobTypeIndex].equals("generic") && familyKillCount > 100000){
                        totalTiers += ((familyKillCount - 100000) / 100000) + 12;
                        continue;
                    }
                    else if (mobTypes[mobTypeIndex].equals("boss") && familyKillCount > 300){
                        totalTiers += ((familyKillCount - 300) / 100) + 12;
                        continue;
                    }
                    for (int milestoneIndex = 0; milestoneIndex < currentMilestones.length; ++milestoneIndex){
                        if (familyKillCount >= currentMilestones[milestoneIndex]){
                            ++totalTiers;
                        }
                    }
                }
            }
        }
        return totalTiers / 10;
    }

    /*
     * adds totals benefits from potion to stat totals
     */
    void setGodPotionStats(boolean status){
        godPotionEnabled = status;
        int modifier = 1;
        if (!status)
            modifier = -1;

        resetGlobalStatModifer();

        addToStats(statTotals,"HEALTH", 100.0 * modifier);
        addToStats(statTotals,"STRENGTH", 98.75 * modifier);
        addToStats(statTotals,"DEFENSE", 66.0 * modifier);
        addToStats(statTotals,"WALK_SPEED", 228.0 * modifier);
        addToStats(statTotals,"INTELLIGENCE", 100.0 * modifier);
        addToStats(statTotals,"CRITICAL_CHANCE", 25.0 * modifier);
        addToStats(statTotals,"CRITICAL_DAMAGE", 80.0 * modifier);
        addToStats(statTotals,"MAGIC_FIND", 88.0 * modifier);
        addToStats(statTotals,"FEROCITY", 2.0 * modifier);
        addToStats(statTotals,"TRUE_DEFENSE", 20.0 * modifier);

        refreshGear();
    }

    void setProfileIndex(){
        JSONArray profileList = playerApi.getJSONArray("profiles");
        for (int profileIndex = 0; profileIndex < profileList.length(); ++profileIndex){
            JSONObject currentProfile = profileList.getJSONObject(profileIndex);
            if (!currentProfile.has("game_mode")){
                mainProfileIndex = profileIndex;
            }
        }
    }

    //TODO: multiple tuning slots (maybe)
    /**
     * Add tuning values to profile totals and sets the accessory power
     */
    void applyTuningValues(){
        JSONObject accessoryBag = playerProfile.getJSONObject("accessory_bag_storage");
        JSONObject tuningSlot0 = accessoryBag.getJSONObject("tuning").getJSONObject("slot_0");

        if (accessoryBag.has("selected_power"))
            selectedPowerStone = accessoryBag.getString("selected_power");
        for (String tuningStat : tuningSlot0.keySet()){
            Double tuningValue = 0.0;
            switch(tuningStat){
                case "critical_chance":
                    tuningValue = tuningSlot0.getDouble(tuningStat) * 0.2;
                    break;
                case "health":
                    tuningValue = tuningSlot0.getDouble(tuningStat) * 5;
                    break;
                case "intelligence":
                    tuningValue = tuningSlot0.getDouble(tuningStat) * 2;
                    break;
                case "attack_speed":
                    tuningValue = tuningSlot0.getDouble(tuningStat) * 0.3;
                    break;
                case "walk_speed":
                    tuningValue = tuningSlot0.getDouble(tuningStat) * 1.5;
                    break;
                default:
                    tuningValue = tuningSlot0.getDouble(tuningStat);
                    break;
            }
            addToStats(statTotals,tuningStat.toUpperCase(), tuningValue);
        }
    }

    // returns the magical multiplier required for power stones calculations 
    double getMagicalMultiplier(Double magicalPower){
        return 29.97 * Math.pow(Math.log(0.0019 * magicalPower + 1) , 1.2);
    }

    void addPowerStoneStats(){
        if (selectedPowerStone.equals("None"))
            return;
        JSONObject baseValues = hypixelValue.getJSONObject("PowerStone").getJSONObject(selectedPowerStone).getJSONObject("base");
        JSONObject uniqueValues = hypixelValue.getJSONObject("PowerStone").getJSONObject(selectedPowerStone).getJSONObject("unique");
        magicalMultiiplier = getMagicalMultiplier(statTotals.get("MAGICAL_POWER"));
        for (String stat : baseValues.keySet()){
            powerStoneStats.add(stat);
            powerStoneValues.add(baseValues.getDouble(stat) * magicalMultiiplier);
            addToStats(statTotals,stat, baseValues.getDouble(stat) * magicalMultiiplier);
        }
        for (String stat : uniqueValues.keySet()){
            powerStoneStats.add(stat);
            powerStoneValues.add(uniqueValues.getDouble(stat));
            addToStats(statTotals,stat, uniqueValues.getDouble(stat));
        }
    }

    public void removePowerStoneStats(){
        for (int index = 0; index < powerStoneStats.size(); ++index){
            String stat = powerStoneStats.get(index);
            Double value = powerStoneValues.get(index);
            statTotals.computeIfPresent(stat, (key, val) -> val - value);
        }
        powerStoneStats.clear();
        powerStoneValues.clear();
    }

    void getMelodyIntelligence(){
        int harpRewards [] = {1,1,1,2,2,2,3,3,3,4,4,1,1};
        JSONObject harpQuest = null;
        int songCompletions = 0;
        Double intelligenceBonus = 0.0;
        if (playerProfile.has("harp_quest"))
            harpQuest = playerProfile.getJSONObject("harp_quest");
        else {
            return;
        }

        for (String song : harpQuest.keySet()){
            if (song.contains("best_completion"))
                ++songCompletions;
        }
        for (int index = 0; index < songCompletions && index < harpRewards.length; ++index){
            intelligenceBonus += harpRewards[index];
        }
        addToStats(statTotals,"INTELLIGENCE", intelligenceBonus);
    }

    /**
     * @param stat
     * @return Global stat if stat exist
     */
    public double getStat(String stat){
        if (stat.equals("WALK_SPEED") && statTotals.get("WALK_SPEED") > speedCap)
            return speedCap;
        return statTotals.get(stat);
    }

    /**
     * @param stat Name of stat to fetch.
     * @return Static stat if stat exist.
     */
    public double getStaticStat(String stat){
        return staticStats.get(stat);
    }

    /**
     * 
     * @return
     * Map containg all stats stored on the profile
     */
    public Map<String,Double> getAllStats(){
        return statTotals;
    }

    public InventoryItem getItem(int itemIndex){
        try {
            return playerGear.get(itemIndex);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
    /**
     * Removes the InventoryItem's stats from the global total.
     */
    public void removeItemStats(InventoryItem item){
        if (item.getReforge().equals("loving"))
            abilityBoost = 1;
        for (Entry<String, Double> stat : item.getStats().entrySet()){
            statTotals.computeIfPresent(stat.getKey(), (key, val) -> val - stat.getValue());
        }
    }

    /**
     * Changes name of a gear piece at the index
     */
    public void changeItemName(Integer armorIndex, String itemName){
        playerGear.get(armorIndex).setName(itemName);
        playerGear.get(armorIndex).setChanged();
    }

    /**
     *  Changes item's modifiers to those in the tooltip and checks for any changes from the previous values.
     *  If a change is detected the values will be recalibrated into the global stats.
     *  If an item has a blank name  ("") the values will be stripped from the item and the global stats.
     */
    public void refreshGear(){
        resetGlobalStatModifer();
        if (selectedPowerStone.equals("None"))
            removePowerStoneStats();
        else {
            removePowerStoneStats();
            addPowerStoneStats();
        }
        resetMiscStats();
        for (InventoryItem playerItem : playerGear){
            // only refresh modifiers if item is not empty
            if (!playerItem.getName().equals(""))
                    playerItem.refreshModifiers();
            if (playerItem.hasChanged() || refreshAll){
                if (playerItem.getName().equals("")){
                    removeItemStats(playerItem);
                    playerItem.resetStats();
                    continue;
                }
                removeItemStats(playerItem);
                playerItem.resetStats();
                applyArmorStats(playerItem, allItems.get(playerItem.getName()));
                readItemStats(playerItem);
            }
        }
        getPetStats();
        addStatMultipliers();
        calcDamage(selectedMobHealth);
        refreshAll = false;
    }

    public void setPowerStone(String powerStone){
        selectedPowerStone = powerStone;
    }
    
    public String getPowerStone(){
        return selectedPowerStone;
    }

    public void setPet(String petName, String petItem ,int petLevel, int petTier){
        this.petItem = petItem;
        this.petName = petName;
        this.petLevel = petLevel;
        this.petTier = petTier;
    }

    public void printItems(){
        for (InventoryItem s : playerGear){
            System.out.println("----------------------");
            System.out.println(s.getName() + ": " + s.getReforge() );
            for (Entry<String, Double> ss : s.getStats().entrySet()){
                System.out.println(ss.getKey() + ": " + ss.getValue());
            }
            System.out.println("\nEnchants");
            for (Entry<String,String> enchant : s.getEnchantments().entrySet()){
                System.out.println(enchant.getKey() + ": " + enchant.getValue());
            }
        }
    }

    public String toTitleCase(String input){
        input = input.trim();
        if (!input.contains(" "))
            return input.substring(0, 1).toUpperCase() + input.substring(1,input.length()).toLowerCase();
        return input.substring(0, 1).toUpperCase() + input.substring(1,input.indexOf(" ") + 1).toLowerCase() +
                toTitleCase(input.substring(input.indexOf(" ") + 1, input.length()));
    }

    public void setActivePet(){
        // should not fail all profiles have a pets array
        JSONArray playerPets = playerApi.getJSONArray("profiles").
                                        getJSONObject(mainProfileIndex).
                                        getJSONObject("members").
                                        getJSONObject(UUID).
                                        getJSONArray("pets");
                                        
        String activePet = "None";
        double petXp = 0.0;
        // find active pet 
        for (int petIndex = 0; petIndex < playerPets.length(); petIndex++){
            JSONObject currentPet = playerPets.getJSONObject(petIndex);
            if (currentPet.getBoolean("active")){
                String pet = currentPet.getString("type");
                String activeItem = currentPet.get("heldItem").toString();
                petXp = currentPet.getDouble("exp");
                // API pet item gets parsed int "Pet Item ".... so that part is replaced with empty string
                petItem = toTitleCase(activeItem.replace("_", " ")).replace("Pet Item ", "");
                activePet = pet.charAt(0) + pet.substring(1, pet.length()).toLowerCase();
                petRarity = Rarity.valueOf(currentPet.getString("tier"));
                break;
            }
        }
        petName = toTitleCase(activePet.replace("_", " "));
        if (!petName.equals("None")){
            petLevel = calcPetLevel(petXp);
            petTier = petRarity.ordinal() + 1;
        }

    }

    // TODO: golden dragon
    public int calcPetLevel(Double petXp){
        Rarity rarity = petRarity;
        if (rarity.toString().equals("MYTHIC")){
            rarity = Rarity.LEGENDARY;
        }

        JSONArray cumulativeLevels = hypixelValue.getJSONObject("Pet XpTable").getJSONArray(rarity.toString());
        int level = 0;
        for (int xpMilestone = 0; xpMilestone < cumulativeLevels.length(); xpMilestone++){
            if (petXp >= cumulativeLevels.getInt(xpMilestone))
                level++;
            else {
                break;
            }
        }
        return level;
    }

    public int getPetLevel(){
        return petLevel;
    }
    public int getPetRarity(){
        return petRarity.ordinal() + 1;
    }

    public String getActivePet(){
        return petName;
    }
    public String getActivePetItem(){
        return petItem;
    }

    /**
     * Remove a collection of stat values from the total values.
     * @param stats Collection of stats and values.
     */
    public void removeStats(Map<String,Double> stats){
        for (Entry<String, Double> stat : stats.entrySet()){
            addToStats(statTotals,stat.getKey(), -stat.getValue());
        }
    }

    public void getPetStats(){

        // remove pet stats from global if present
        removeStats(petStats);

        // reset pet stats
        petStats = createBaseStats();
        mobAdditiveBoost = 0;
        mobMultiBoost = 1;
        finalDamageMulti -= petFinalDamageMulti;
        petFinalDamageMulti = 0;

        petBaseAdditive = 0;
        petFirstHit = 0;

        // if no pet is selected return
        if (petName.equals("None")){
            addGearEffects();
            return;
        }

        // path into necessary pet values (all pet JSONS should have them)
        JSONObject petValues = hypixelValue.getJSONObject("Pets").getJSONObject(petName);
        JSONObject baseStats = petValues.getJSONObject("baseStats");
        JSONObject leveledStats = petValues.getJSONObject("statsPerLevel");
        JSONObject petItems =  hypixelValue.getJSONObject("Pet items");
        JSONArray abilities = petValues.getJSONArray("abilities");
        int abilityOrder [] = new int [abilities.length()];

        // loop through abilities and order any abilities that should be last to the end of the order
        for (int abilityNum = 0, position = 0; abilityNum < abilityOrder.length; ++abilityNum){
            String abilityAction = abilities.getJSONObject(abilityNum).getString("action"); 
            if (abilityAction.equals("statForStat") || abilityAction.equals("allStats")){
                abilityOrder[abilityOrder.length - 1] = abilityNum;
            }
            else {
                abilityOrder[position] = abilityNum;
                ++position;
            }
        }
        // add base stats
        for (String stat : baseStats.keySet()){
            petStats.put(stat, baseStats.getDouble(stat));
        }

        // add stats that increase with the pet's level
        for (String stat : leveledStats.keySet()){
            addPetStat(stat, leveledStats.getDouble(stat) * petLevel);
        }

        // add petitem effects if supported. Item name can be something unsupported due to API parsing. If unsupported set it to None
        if (!petItems.has(petItem)){
            petItem = "None";
        }
        else if (!petItem.equals("None")){
            JSONObject petItemEffects = petItems.getJSONObject(petItem);
            String itemEffect = petItemEffects.getString("effect");

            // process items with additive effect
            if (itemEffect.equals("additive")){
                JSONObject itemStats = petItemEffects.getJSONObject("stats");
                for (String stat : itemStats.keySet()){
                    addPetStat(stat, itemStats.getDouble(stat));
                }
            }
            // process items with multiplicative effect
            else if (itemEffect.equals("multiplicative")){
                JSONArray itemStats = petItemEffects.getJSONArray("stats");
                Double amount = petItemEffects.getDouble("amount");
                if (itemStats.get(0).equals("all")){
                    for (Entry<String,Double> stat : petStats.entrySet()){
                        addPetStat(stat.getKey(), stat.getValue() * amount);
                    }
                }
                else {
                    for (var stat : itemStats){
                        addPetStat(stat.toString(), petStats.get(stat) * amount);
                    }
                }
            }
        }
        // get armor and weapon effects after all base pet stats are set
        addGearEffects();

        for (int i = 0 ; i < abilities.length(); ++i){
            if (enabledPetAbilities[abilityOrder[i]] == 2)
                continue;

            Double statPerLevel = 0.0;
            Double perLevelValue = 0.0;
            Double statValue = 0.0;
            JSONArray perLevelAmount;
            JSONArray statTiers;
            JSONObject currentAbility = abilities.getJSONObject(abilityOrder[i]); 
            JSONObject stats = null;
            String abilityAction = currentAbility.getString("action");

            switch (abilityAction) {
                case "addStatPerLevel" : 
                    stats = currentAbility.getJSONObject("stats");
                    for (String stat : stats.keySet()){
                        if (stat.equals("DAMAGE"))
                            continue;
                        statTiers = stats.getJSONArray(stat);
                        statPerLevel = statTiers.getDouble(getTierToUse(statTiers.length()));
                        statValue = statPerLevel * petLevel;
                        addPetStat(stat, statValue);
                    }
                    break;
                case "baseMultiplierPerLevel" :     //TODO: some might be post multi 
                    String type = currentAbility.getString("type");
                    perLevelAmount = currentAbility.getJSONArray("amountPerLevel");
                    //This works on tiger
                    if (type.equals("additive"))
                        petBaseAdditive = perLevelAmount.getDouble(getTierToUse(perLevelAmount.length())) * petLevel;
                    else {
                        petMultiplier = 1 + ((perLevelAmount.getDouble(getTierToUse(perLevelAmount.length())) / 100) * petLevel);
                    }

                    break;
                case "firstHitBuff" : 
                    Map<String,String> weaponEnchants = playerGear.get(WEAPON_INDEX).getEnchantments();
                    perLevelAmount = currentAbility.getJSONArray("amountPerLevel");
                    if (weaponEnchants.containsKey("first_strike") || weaponEnchants.containsKey("triple_strike")){
                        petFirstHit = perLevelAmount.getDouble(getTierToUse(perLevelAmount.length())) * petLevel;
                    }
                    break;
                case "allStats" : 
                    perLevelAmount = currentAbility.getJSONArray("amountPerLevel");
                    perLevelValue = perLevelAmount.getDouble(getTierToUse(perLevelAmount.length())) / 100.0;
                    addToAllGlobalModifers(perLevelValue * petLevel);
                    break;
                case "buffArmor" :
                    String armorKeyword = currentAbility.getString("armorKeyword");
                    perLevelAmount = currentAbility.getJSONArray("amountPerLevel");
                    double percentageIncrease = (perLevelAmount.getDouble(getTierToUse(perLevelAmount.length())) * petLevel) / 100;
                    for (int armorIndex = 0; armorIndex < 4; ++armorIndex){
                        InventoryItem armorPiece = playerGear.get(armorIndex);
                        if (armorPiece.getName().contains(armorKeyword)){
                            for (Entry<String,Double> armorStat : armorPiece.getStats().entrySet()){
                                addPetStat(armorStat.getKey(), (armorStat.getValue() * percentageIncrease));
                            }
                        }
                    }
                    break;
                case "buffWeapon" :
                    String weapon = currentAbility.getString("weapon");
                    stats = currentAbility.getJSONObject("stats");
                    if (playerGear.get(WEAPON_INDEX).getName().equals(weapon)){
                        for (String stat : stats.keySet()){
                            statTiers = stats.getJSONArray(stat);
                            statPerLevel = statTiers.getDouble(getTierToUse(statTiers.length()));
                            addPetStat(stat, statPerLevel * petLevel);
                        }
                    }
                    break;
                case "doubleBooks" :
                    int bookCount = 0;
                    for (int armorIndex = 0; armorIndex < 4; ++armorIndex){
                        bookCount = playerGear.get(armorIndex).getPotatoBooks();
                        addPetStat("HEALTH", (4 * bookCount));
                        addPetStat("DEFENSE", (2 * bookCount));
                    }
                    bookCount = playerGear.get(WEAPON_INDEX).getPotatoBooks();
                    addPetStat("DAMAGE", (2 * bookCount));
                    addPetStat("STRENGTH", (2 * bookCount));
                    break;
                case "buffMobType" :        // pets add multiplicative boost
                    String mobType = currentAbility.getString("mobType");
                    String effect = currentAbility.getString("effect");
                    perLevelAmount = currentAbility.getJSONArray("amountPerLevel");
                    statPerLevel = (perLevelAmount.getDouble(getTierToUse(perLevelAmount.length()) / 100));
                    if (selectedMob.equals(mobType) || (mobType.equals("EndMobs") && (selectedMob.equals("Dragon") || selectedMob.equals("Enderman")))){
                        mobMultiBoost += statPerLevel * petLevel;
                    }
                    break;
                case "buffEnder" : 
                    mobType = currentAbility.getString("mobType");
                    perLevelAmount = currentAbility.getJSONArray("amountPerLevel");
                    statPerLevel = (perLevelAmount.getDouble(getTierToUse(perLevelAmount.length())));
                    if ((mobType.equals("EndMobs") && (selectedMob.equals("Dragon") || selectedMob.equals("Enderman")))){
                        mobAdditiveBoost += (statPerLevel * petLevel) + 100;
                    }
                    break;
                case "goldsPower" :
                    perLevelAmount = currentAbility.getJSONArray("amountPerLevel");
                    if (playerGear.get(WEAPON_INDEX).getMaterial().contains("GOLD")){
                        statValue = ((perLevelAmount.getDouble(getTierToUse(perLevelAmount.length()))) * petLevel) + 50;
                        addPetStat("STRENGTH", statValue * (1 + globalStatBoost));
                    }
                    break;
                case "shiningScales" :
                    int digitCount = 0;
                    for (int goldCount = goldCollection; goldCount >= 1; goldCount /= 10){
                        ++digitCount;
                    }
                    addPetStat("STRENGTH", (10 * digitCount));
                    addPetStat("MAGIC_FIND", (2 * digitCount));
                    break;
                case "dragonsGreed" :
                    //double strength = (petStats.get("STRENGTH") + statTotals.get("STRENGTH")) * globalStatModifiers.get("STRENGTH"); 
                    int magicFind = (int) ((petStats.get("MAGIC_FIND") + statTotals.get("MAGIC_FIND")) * globalStatModifiers.get("MAGIC_FIND")) / 5;     
                    perLevelAmount = currentAbility.getJSONArray("amountPerLevel");
                    statPerLevel = perLevelAmount.getDouble(getTierToUse(perLevelAmount.length()));
                    statValue = ((statPerLevel * petLevel) + 0.25) * magicFind;
                    addToGlobalModifer("STRENGTH", statValue);
                    break;
                case "legendaryTreasure" :
                    //TODO: adds percentage to damage calc not damage stat 
                    addPetStat("DAMAGE", 0.1263 * (bankBalance / 1000000));
                    break;
                case "IncreaseSpeedCap" :
                    //TODO: adds percentage to damage calc not damage stat 
                    perLevelAmount = currentAbility.getJSONArray("amountPerLevel");
                    statPerLevel = perLevelAmount.getDouble(getTierToUse(perLevelAmount.length()));
                    statValue = statPerLevel * petLevel;
                    addPetStat("WALK_SPEED", statValue);
                    speedCap = speedCap + statValue;
                    break;
                case "petAbility" :
                    //TODO: need to implement --- probably wont do
                    break;
                /*
                 * Blue Whale 'additive'
                 * Griffin 'multiplicative'
                 */
                case "statByPercentage" :
                    String receiveStat = currentAbility.getString("stat");
                    effect = currentAbility.getString("effect");
                    perLevelAmount = currentAbility.getJSONArray("amountPerLevel");
                    statPerLevel = (perLevelAmount.getDouble(getTierToUse(perLevelAmount.length())) / 100);
                    statValue = statPerLevel * petLevel;
                    // add base amount if pet has it 
                    if (currentAbility.has("baseAmount")){
                        statValue += currentAbility.getDouble("baseAmount") / 100;
                    }
                    // For multiplicative 
                    if (effect.equals("multiplicative")){
                        addToGlobalModifer(receiveStat, statValue * globalStatModifiers.get(receiveStat));
                    }
                    // For additive
                    else {
                        addToGlobalModifer(receiveStat, statValue);
                    }
                    break;
                case "rockDefense" :
                    perLevelAmount = currentAbility.getJSONArray("amountPerLevel");
                    statPerLevel = (perLevelAmount.getDouble(getTierToUse(perLevelAmount.length())) / 100);
                    statValue = statPerLevel * petLevel;
                    Double rockDefense = (statTotals.get("DEFENSE") + petStats.get("DEFENSE")) * globalStatModifiers.get("DEFENSE") * statValue;
                    addPetStat("DEFENSE", rockDefense);
                    break;
                    //TODO: figure out where put put buff, needs to be a final multiplier
                case "buffFinalDamage" :
                    perLevelAmount = currentAbility.getJSONArray("amountPerLevel");
                    statPerLevel = (perLevelAmount.getDouble(getTierToUse(perLevelAmount.length())) / 100);
                    petFinalDamageMulti = statPerLevel * petLevel;
                    finalDamageMulti += petFinalDamageMulti;
                    break;
                case "buffBowDamage" :
                    //TODO: need to implement   --- need to work on post multiplier damage first 
                    break;
                case "buffArrowDamage" :
                    //TODO: need to implement --- maybe not incredibly negligible 
                    break;
                case "buffZombieSets" :
                    perLevelAmount = currentAbility.getJSONArray("amountPerLevel");
                    statPerLevel = perLevelAmount.getDouble(getTierToUse(perLevelAmount.length())) / 100;
                    String acceptedArmor[] = {"revenant", "zombie", "heart", "reaper", "rotten", "skeleton grunt", "skeleton soldier", "skeleton master", "skeleton lord", "skeletor"};
                    for (InventoryItem gearPiece: playerGear){

                        //reaper mask gets 2x the percentage value for some reason
                        if (gearPiece.getName().endsWith("reaper mask")){
                            for (Entry<String,Double> stat : gearPiece.getStats().entrySet()){
                                addPetStat(stat.getKey(), (stat.getValue()) * (statPerLevel * petLevel) * 2);
                            }
                            continue;
                        }
                        for (String keyword : acceptedArmor){
                            if (gearPiece.getName().contains(keyword)){
                                for (Entry<String,Double> stat : gearPiece.getStats().entrySet()){
                                    addPetStat(stat.getKey(), (stat.getValue()) * (statPerLevel * petLevel));
                                }
                            }
                        }
                    }
                    break;
                case "statForStat" :
                    receiveStat = currentAbility.getString("receiveStat");
                    String dependentStat = currentAbility.getString("dependentStat");
                    double dependentAmount = 0.0;
                    double dependentTotal = 0.0;
                    JSONArray dependentValues = null;
                    perLevelAmount = currentAbility.getJSONArray("amountPerLevel");

                    // check if dependent Amount is a String meaning it should be handled as a percentage
                    if (currentAbility.get("dependentAmount") instanceof String){
                        statPerLevel = perLevelAmount.getDouble(getTierToUse(perLevelAmount.length())) / 100;
                        dependentTotal = (petStats.get(dependentStat) + statTotals.get(dependentStat)) * globalStatModifiers.get(dependentStat);
                        statValue = dependentTotal * (statPerLevel * petLevel);
                        addPetStat(receiveStat, statValue);
                    }
                    else {
                        statPerLevel = perLevelAmount.getDouble(getTierToUse(perLevelAmount.length()));
                        dependentValues = currentAbility.getJSONArray("dependentAmount");
                        dependentAmount = dependentValues.getDouble(getTierToUse(dependentValues.length()));
                        dependentTotal = (petStats.get(dependentStat) + statTotals.get(dependentStat)) * globalStatModifiers.get(dependentStat);
                        addPetStat(receiveStat, (dependentTotal / dependentAmount) * (statPerLevel * petLevel));
                    }

                    break;
                /*
                 * Mithril golem 'multiplicative' 
                 */
                case "buffCombatStats" :    // health, defense, crit damage/chance, strength, damage
                    perLevelAmount = currentAbility.getJSONArray("amountPerLevel");
                    statPerLevel = perLevelAmount.getDouble(getTierToUse(perLevelAmount.length())) / 100;
                    addToGlobalModifer("HEALTH", (statPerLevel * petLevel) * globalStatModifiers.get("HEALTH"));
                    addToGlobalModifer("DEFENSE", (statPerLevel * petLevel)* globalStatModifiers.get("DEFENSE"));
                    addToGlobalModifer("STRENGTH", (statPerLevel * petLevel)* globalStatModifiers.get("STRENGTH"));
                    addToGlobalModifer("CRITICAL_DAMAGE", (statPerLevel * petLevel)* globalStatModifiers.get("CRITICAL_DAMAGE"));
                    addToGlobalModifer("CRITICAL_CHANCE", (statPerLevel * petLevel)* globalStatModifiers.get("CRITICAL_CHANCE"));
                    break;
                    
                // currently only made to support ammonite because its the only pet that does this
                case "statBasedOnSkill" :           
                    stats = currentAbility.getJSONObject("skills");
                    for (String stat : stats.keySet()){
                        
                        if (stat.equals("mining")){
                            perLevelAmount = stats.getJSONArray("mining");
                            statPerLevel = perLevelAmount.getDouble(getTierToUse(perLevelAmount.length()));
                            addPetStat("WALK_SPEED", ((statPerLevel * petLevel) * skillLevels.get("MINING")) * (1 + globalStatBoost));
                            addPetStat("DEFENSE", ((statPerLevel * petLevel) * skillLevels.get("MINING")) * (1 + globalStatBoost));
                        }
                        else {
                            perLevelAmount = stats.getJSONArray("fishing");
                            statPerLevel = perLevelAmount.getDouble(getTierToUse(perLevelAmount.length()));
                            addPetStat("WALK_SPEED", ((statPerLevel * petLevel) * skillLevels.get("FISHING")) * (1 + globalStatBoost));
                            addPetStat("DEFENSE", ((statPerLevel * petLevel) * skillLevels.get("FISHING")) * (1 + globalStatBoost));
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        //add stats to global
        addToTotal(petStats);

    }

    public void setSelectedMob(String mobType){
        selectedMob = mobType;
    }
    public void setMobHealth(Long mobHealth){
        selectedMobHealth = mobHealth;
    }

    /**
     * Enables or disables an ability. Only called when ability checkbox states changes
     * @param abilityNumber
     * @param status 1 for enabled 2 for disabled
     */
    public void setPetAbilityStatus(int abilityNumber, int status){
        enabledPetAbilities[abilityNumber] = status;
    }

    /**
     * 
     * @param statValuesLength Length of tiered stat values JSONArray
     * @return Index to the value to use, the pet tier if available or the highest tier
     */
    int getTierToUse(int statValuesLength){
        if (statValuesLength >= petTier)
            return petTier - 1;
        else {
            return statValuesLength - 1;
        }
    }

    /**
     * Removes and regathers all misc stats due to a change in skill/skyblock level
     */
    public void resetMiscStats(){
        removeStats(miscStats);
        miscStats = createBaseStats();
        getSkillStatsBoost();
        skyBlockLevelBoost();
        addToTotal(miscStats);
    }

    

    void addPetStat(String stat, double value){
        if (petStats.containsKey(stat))
                petStats.compute(stat, (key, val) -> val + value);
            else {
                petStats.put(stat, value);
            }
    }

    /**
     * Find all additional armor/weapon effects.
     */
    public void addGearEffects(){
        setTempStats(false);     // remove temp stats 
        getArmorEffects();              // gather armor effects, add any temp stats
        getWeaponEffects();             // gather weapon effects, add any temp stats
        setTempStats(true);     // add temp stats
    }

    public void calcDamage(Long mobHealth){
        double baseMultiplier = calcBaseMultiplier(mobHealth, false);
        double magicBaseMultiplier = calcBaseMultiplier(mobHealth, true);
        Double abilityDamage = 1 + ((statTotals.get("ABILITY_DAMAGE_PERCENT") + staticStats.get("ABILITY_DAMAGE_PERCENT") )/ 100);
        Double postMultiplier = (mobMultiBoost * weaponMultiplier * armorMultiplier * petMultiplier); 
        Double strength = statTotals.get("STRENGTH") + staticStats.get("STRENGTH") + strengthOnHit;
        Double critDamage = statTotals.get("CRITICAL_DAMAGE") + staticStats.get("CRITICAL_DAMAGE");
        Double damage = statTotals.get("DAMAGE") + staticStats.get("DAMAGE");
        Double intelligence = statTotals.get("INTELLIGENCE") + staticStats.get("INTELLIGENCE");

        Double weaponAbilityDamage = playerGear.get(WEAPON_INDEX).getStats().get("WEAPON_ABILITY_DAMAGE");
        Double abilityDamagePcercent = playerGear.get(WEAPON_INDEX).getStats().get("ABILITY_DAMAGE_SCALING");

        // if weapon does not have ability damage default to 1
        if (abilityDamagePcercent == 0){
            abilityDamagePcercent = 1.0;
        }
        weaponDamage = (int) ((5 + damage) * (1 + (strength/ 100.0)) * (1 + ((critDamage * critEffectiveness) / 100.0)) * (1 + (baseMultiplier / 100.0)) * (postMultiplier)  * finalDamageMulti);
        mageDamage =  (int) ((weaponAbilityDamage * abilityBoost) * (1 + ((intelligence / 100.0) * abilityDamagePcercent)) * (1 + (magicBaseMultiplier / 100.0)) * abilityDamage * postMultiplier  * finalDamageMulti);
    }   

    public int getWeaponDamage (){
        return weaponDamage;
    }
    public int getMageDamage (){
        return mageDamage;
    }

    public void getArmorEffects(){
        InventoryItem helmet = playerGear.get(HELMET_INDEX);
        InventoryItem chestplate = playerGear.get(CHESTPLATE_INDEX);
        InventoryItem leggings = playerGear.get(LEGGINGS_INDEX);
        InventoryItem boots = playerGear.get(BOOTS_INDEX);

        InventoryItem necklace = playerGear.get(NECKLACE_INDEX);
        InventoryItem cloak = playerGear.get(CLOAK_INDEX);
        InventoryItem belt = playerGear.get(BELT_INDEX);
        InventoryItem gauntlet = playerGear.get(GAUNTLET_INDEX);

        armorMultiplier = 1;
        armorAdditive = 0;
        critEffectiveness = 1;

        for (int armorIndex = 0; armorIndex < 4; ++armorIndex){

            // check reforges for renowned and adds 1% to globalStatBoost
            if (playerGear.get(armorIndex).getReforge().equals("renowned")){
                addToAllGlobalModifers(0.01);
            }

            // add max ultimate wisdom amount to temp stat
            else if (playerGear.get(armorIndex).getEnchantments().containsKey("ultimate_wisdom")){
                String wisdomLevel = playerGear.get(armorIndex).getEnchantments().get("ultimate_wisdom");
                tempIntelligence += hypixelEnchants.getJSONObject("armor").getJSONObject("ultimate_wisdom").getDouble(wisdomLevel);
            }
        }

    
        // All individual piece bonuses
        switch (helmet.getName()){
            case "lantern helmet" : 
                tempHealth += 4 * skillLevels.get("FARMING");
                tempDefense += 2 * skillLevels.get("FARMING");
                break;
            case "taurus helmet" : 
                if (selectedMob.equals("Lava Sea Creature"))
                    armorAdditive += 10;
                break;
            case "magma lord helmet" : 
                if (selectedMob.equals("Lava Sea Creature"))
                    armorAdditive += 30;
                break;
            case "thunder helmet" : 
                if (selectedMob.equals("Lava Sea Creature"))
                    armorAdditive += 20;
                break;
            case "crown of greed" : 
                armorMultiplier += 0.25;
                break;
        }
        switch (chestplate.getName()){
            case "flaming chestplate" : 
                if (selectedMob.equals("Lava Sea Creature"))
                    armorAdditive += 10;
                break;
            case "magma lord chestplate" : 
                if (selectedMob.equals("Lava Sea Creature"))
                    armorAdditive += 30;
                break;
            case "thunder chestplate" : 
                if (selectedMob.equals("Lava Sea Creature"))
                    armorAdditive += 20;
                break;
        }
        switch (leggings.getName()){
            case "moogma leggings" : 
                if (selectedMob.equals("Lava Sea Creature"))
                    armorAdditive += 10;
                break;
            case "magma lord leggings" : 
                if (selectedMob.equals("Lava Sea Creature"))
                    armorAdditive += 30;
                break;
            case "thunder leggings" : 
                if (selectedMob.equals("Lava Sea Creature"))
                    armorAdditive += 20;
                break;
        }
        switch (boots.getName()){
            case "rancher's boots" : 
                tempSpeed += 4 * skillLevels.get("FARMING");
                tempDefense += 2 * skillLevels.get("FARMING");
                break;
            case "farmer boots" : 
                tempSpeed += 4 * skillLevels.get("FARMING");
                tempDefense += 2 * skillLevels.get("FARMING");
                break;
            case "magma lord boots" : 
                if (selectedMob.equals("Lava Sea Creature"))
                    armorAdditive += 30;
                break;
            case "thunder boots" : 
                if (selectedMob.equals("Lava Sea Creature"))
                    armorAdditive += 20;
                break;
        }
        switch (necklace.getName()){
            case "thunderbolt necklace" : 
                if (selectedMob.equals("Lava Sea Creature"))
                    armorAdditive += 10;
                break;
        }
        switch (cloak.getName()){
            case "dragonfade cloak" : 
                if (selectedMob.equals("Dragon"))
                    petMultiplier += 0.01;
                break;
        }
        // placeholder
        switch (belt.getName()){
        }
        switch (gauntlet.getName()){
            case "magma lord gauntlet" : 
                if (selectedMob.equals("Lava Sea Creature"))
                    armorAdditive += 10;
                break;
            case "dragonfuse glove" : 
                if (playerGear.get(WEAPON_INDEX).getName().equals("aspect of the dragon")){
                    tempStrength += 50;
                    tempDamage += 35;
                }
                break;
            case "demonslayer gauntlet" : 
                if (selectedMob.equals("Blaze"))
                    armorAdditive += 10;
                break;
        }

        /*
         * all set bonuses and effects below
         */

        // mastif
        if (checkArmorSet("mastiff",4)){
            tempHealth = (statTotals.get("CRITICAL_DAMAGE") * globalStatModifiers.get("CRITICAL_DAMAGE") + tempHealth )* 50;
            critEffectiveness -= 0.5;
        }
        // reaper slayer set
        else if (checkArmorSet("reaper", 3)){
            if (selectedMob.equals("Zombie"))
                armorAdditive += 100;
        }
        // superior dragon set
        else if (checkArmorSet("superior dragon", 4)){
            addToAllGlobalModifers(0.05);
        }
        // strong dragon
        else if (checkArmorSet("strong dragon", 4)){
            if (playerGear.get(WEAPON_INDEX).getName().equals("aspect of the end"));
                tempDamage += 75;
        }
        // lapis armor
        else if (checkArmorSet("lapis armor", 4)){
            tempHealth += 60;
        }
        // crimson isle rampart
        else if (checkArmorSet("rampart", 4)){
            tempHealth += 50;
            tempStrength += 20;
            tempcritDamage += 15;
        }
        // tuxedos
        else if (checkArmorSet("cheap tuxedo", 3)){
            armorAdditive += 50;
        }
        else if (checkArmorSet("fancy tuxedo", 3)){
            armorAdditive += 100;
        }
        else if (checkArmorSet("elegant tuxedo", 3)){
            armorAdditive += 150;
        }
        


    }

    public Boolean checkArmorSet(String keyword, int requiredAmount){
        for (int armorIndex = 0; armorIndex < 4 && armorIndex < requiredAmount; ++armorIndex){
            String armorName = playerGear.get(armorIndex).getName();
            if (!armorName.contains(keyword))
                return false;
        }
        return true;
    }

    public void setTempStats(Boolean status){
        int modifier = 1;
        if (!status) modifier = -1;

        addToStats(statTotals,"HEALTH", tempHealth * modifier);
        addToStats(statTotals,"STRENGTH", tempStrength * modifier);
        addToStats(statTotals,"CRITICAL_DAMAGE", tempcritDamage * modifier);
        addToStats(statTotals,"DAMAGE", tempDamage * modifier);
        addToStats(statTotals,"DEFENSE", tempDefense * modifier);
        addToStats(statTotals,"WALK_SPEED", tempSpeed * modifier);
        addToStats(statTotals,"INTELLIGENCE", tempIntelligence * modifier);

        if (!status){
            tempSpeed = 0.0;
            tempHealth = 0.0;
            tempStrength = 0.0;
            tempcritDamage = 0.0;
            tempcritDamage = 0.0;
            tempDefense = 0.0;
            tempIntelligence = 0.0;
            strengthOnHit = 0;
        }
    }

    public void getWeaponEffects(){
        weaponAdditive = 0;
        weaponMultiplier = 1;

        switch(playerGear.get(WEAPON_INDEX).getName()){
            case "undead sword" : 
                if (selectedMob.equals("Zombie") || selectedMob.equals("Wither") || selectedMob.equals("Skeleton") || selectedMob.equals("Pigmen"))
                    weaponMultiplier += 1;
                break;
            case "spider sword" : 
                if (selectedMob.equals("Spider"))
                    weaponMultiplier += 1;
                break;
            case "scorpion foil" : 
                if (selectedMob.equals("Spider"))
                    weaponMultiplier += 2.5;
                break;
            case "end sword" : 
                if (selectedMob.equals("Enderman") || selectedMob.equals("Dragon"))
                    weaponMultiplier += 1;
                break;
            case "voidwalker katana" : 
                if (selectedMob.equals("Enderman"))
                    weaponMultiplier += 1;
                break;
            case "voidedge katana" : 
                if (selectedMob.equals("Enderman"))
                    weaponMultiplier += 1.5;
                break;
            case "vorpal katana" : 
                if (selectedMob.equals("Enderman"))
                    weaponMultiplier += 2;
                break;
            case "atomsplit katana" : 
                if (selectedMob.equals("Enderman"))
                    weaponMultiplier += 2.5;
                break;
            case "revenant falchion" : 
                if (selectedMob.equals("Zombie"))
                    weaponMultiplier += 1.5;
                break;
            case "reaper falchion" : 
                if (selectedMob.equals("Zombie"))
                    weaponMultiplier += 2;
                break;
            case "axe of the shredded" : 
                if (selectedMob.equals("Zombie"))
                    weaponMultiplier += 2.5;
                break;
            case "firedust dagger" : 
                if (selectedMob.equals("Blaze"))
                    weaponMultiplier = 1.2;
                if (selectedMob.equals("Pigmen"))
                    weaponMultiplier = 1.1;
                break;
            case "twilight dagger" : 
                if (selectedMob.equals("Blaze"))
                    weaponMultiplier = 1.5;
                if (selectedMob.equals("Skeleton"))
                    weaponMultiplier = 1.2;
                break;
            case "kindlebane dagger" : 
                if (selectedMob.equals("Blaze"))
                    weaponMultiplier = 1.5;
                if (selectedMob.equals("Pigmen"))
                    weaponMultiplier = 1.2;
                break;
            case "mawdredge dagger" : 
                if (selectedMob.equals("Blaze"))
                    weaponMultiplier = 2.5;
                if (selectedMob.equals("Skeleton"))
                    weaponMultiplier = 1.5;
                break;
            case "pyrochaos dagger" : 
                if (selectedMob.equals("Blaze"))
                    weaponMultiplier = 2.0;
                if (selectedMob.equals("Pigmen"))
                    weaponMultiplier = 1.5;
                break;
            case "deathripper dagger" : 
                if (selectedMob.equals("Blaze"))
                    weaponMultiplier = 3.5;
                if (selectedMob.equals("Skeleton"))
                    weaponMultiplier = 2.0;
                break;
            case "shaman sword" : 
                tempDamage = (statTotals.get("HEALTH") * globalStatModifiers.get("HEALTH") + tempHealth) / 50;
                break;
            case "pooch sword" : 
                tempDamage = (statTotals.get("HEALTH") * globalStatModifiers.get("HEALTH") + tempHealth) / 50;
                if (selectedMob.equals("Wolf")){
                    strengthOnHit = 161;
                }
                break;
            case "sword of revelations" : 
                if (selectedMob.equals("Mythological"))
                    weaponMultiplier += 2.0;
                break;
            case "daedalus axe" : 
                if (selectedMob.equals("Mythological"))
                    weaponMultiplier += 2.0;
                break;
        }
    }
    /**
     * 
     * @param mobHealth
     * @return Total base multiplier across all area including enchants pet/armor/weapon bonuses
     */
     double calcBaseMultiplier(Long mobHealth, boolean isMagic){

        // check if weapon is in list of weapons with abilities unaffacted by base multipliers stuff 
        if (isMagic){
            for (String weapon : excludedMagicWeapons){
                if (playerGear.get(WEAPON_INDEX).getName().equals(weapon))
                    return 0.0;
            }
        }
        int combatMultiplier = 0;
        double baseMultiplier = petBaseAdditive + weaponAdditive + armorAdditive + mobAdditiveBoost;
        enderSlayerBonus = 0;
        impalingBonus = 0;
        dragonBonus = 0;
        smiteBonus = 0;
        baneBonus = 0;
        cubismBonus = 0;

        // add combat multiplier
        if (skillLevels.get("COMBAT") > 50 )
            combatMultiplier = 200 + skillLevels.get("COMBAT") % 10;
        else {
            combatMultiplier = skillLevels.get("COMBAT") * 4;
        }
        baseMultiplier += combatMultiplier;

        // check for archery potion base addition
        if (godPotionEnabled && playerGear.get(WEAPON_INDEX).getCategory().equals("BOW"))
            baseMultiplier += 80;

        // check weapon enchantments for multipliers
        if (!playerGear.get(WEAPON_INDEX).getName().equals("") && !playerGear.get(WEAPON_INDEX).getEnchantments().isEmpty()){
            String enchantType = playerGear.get(WEAPON_INDEX).getReforgeCategory();
            JSONObject enchantPath = hypixelValue.getJSONObject("Enchantments").getJSONObject(enchantType);
            // loop through enchants
            for (Entry<String,String> enchant : playerGear.get(WEAPON_INDEX).getEnchantments().entrySet()){
                if (!enchantPath.has(enchant.getKey()))
                    continue;
                double maxPercentage = 0;
                String enchantName = enchant.getKey();
                String enchantLevel = enchant.getValue();
                double enchantValue = enchantPath.getJSONObject(enchantName).getDouble(enchantLevel);
                switch(enchantName){
                    case "sharpness" : 
                        if (!isMagic)
                            baseMultiplier += enchantValue;
                        break;
                    case "first_strike" : 
                        if (!isMagic)
                            baseMultiplier += enchantValue;
                        break;
                    case "triple_strike" : 
                        if (!isMagic)
                            baseMultiplier += enchantValue;
                        break; 
                    case "power" : 
                        baseMultiplier += enchantValue;
                        break; 
                    case "ultimate_one_for_all" : 
                        baseMultiplier += enchantValue;
                        break; 
                    case "smite" : 
                        if (selectedMob.equals("Zombie") || selectedMob.equals("Skeleton") || selectedMob.equals("Pigmen") || selectedMob.equals("Withers"))
                            baseMultiplier += enchantValue;
                        break;
                    case "ender_slayer" : 
                        if (selectedMob.equals("Enderman"))
                            baseMultiplier += enchantValue;
                        break;
                    case "bane_of_arthropods" : 
                        if (selectedMob.equals("Spider"))
                            baseMultiplier += enchantValue;
                        break;
                    case "dragon_hunter" : 
                        if (selectedMob.equals("Dragon"))
                            baseMultiplier += enchantValue;
                        break;
                    case "cubism" : 
                        if (selectedMob.equals("Creeper") || selectedMob.equals("Magma Cube") || selectedMob.equals("Slimes"))
                            baseMultiplier += enchantValue;
                        break;
                    case "impaling" : 
                        if (selectedMob.equals("Sea Creature") || selectedMob.equals("Lava Sea Creature"))
                            baseMultiplier += enchantValue;
                        break;
                    case "giant_killer" : 
                        double enchantPercentage = (mobHealth - statTotals.get("HEALTH")) / statTotals.get("HEALTH") * enchantValue;
                        switch (enchantLevel) {
                            case "1" :
                                maxPercentage = 5;
                                break;
                            case "2" :
                                maxPercentage = 10;
                                break;
                            case "3" :
                                maxPercentage = 15;
                                break;
                            case "4" :
                                maxPercentage = 20;
                                break;
                            case "5" :
                                maxPercentage = 30;
                                break;
                            case "6" :
                                maxPercentage = 45;
                                break;
                            case "7" :
                                maxPercentage = 65;
                                break;
                        }
                        if (enchantPercentage > maxPercentage)
                            enchantPercentage = maxPercentage;
                        else if (enchantPercentage < 0)
                            enchantPercentage = 0;

                        baseMultiplier += enchantPercentage;
                        break;
                    case "prosecute" : 
                        // TODO: since I do not currently track current health this works otherwise i need to (currentH / maxH) 0.1 * enchantValue * 1000;
                        enchantPercentage = 0.1 * enchantValue * 1000;
                        baseMultiplier += enchantPercentage;
                        break;
                }
            }
        }

        /**
         * check for other base multiplier addition that need to be done after all stats have been calculated
         * Only done on isMagic = false since baseMultiplier is calculated twice for melee damage and magic damage before being reset 
         */
        if (!isMagic){
            String helmet = playerGear.get(HELMET_INDEX).getName();
        // check for warden helmet
            if (helmet.equals("warden helmet")){
                speedCap = speedCap / 2;
                if (statTotals.get("WALK_SPEED") >= speedCap){
                    baseMultiplier += (speedCap/25*20) + 1;
                }

                else {
                    baseMultiplier += (statTotals.get("WALK_SPEED")/25*20)/2 + 1;
                }
            }
        }

        return baseMultiplier;
    }

}
