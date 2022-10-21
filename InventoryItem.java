import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class InventoryItem {
    private Rarity itemRarity;
    private String itemName = "";
    private String material = "";       //mainly used to check if material is gold for golden dragon ability
    private String reforge = "";
    private String category = "";
    private String reforgeCategory = "";
    private int starLevel = 0;
    private int potatoBooks = 0;
    private int statTier = 0;
    private double itemQuality = 0.0;
    private boolean hasDungeonTiers = false;
    private boolean dungeonItem = false;
    private boolean recombobulated = false;
    private boolean hasChanged = false;
    private Map<String,Double> itemStats;
    private Map<String,String> enchantments;

    private ArrayList<String> enchantPool;

    ItemTooltipPanel toolTip = new ItemTooltipPanel();

    private enum Rarity {
        COMMON, UNCOMMON, RARE, EPIC, LEGENDARY, MYTHIC, DIVINE, SPECIAL, VERY_SPECIAL;

        public Rarity next() {
            return values()[ordinal() + 1];
        }
    }

    InventoryItem(){
        enchantments = new HashMap<>();
        itemStats = new HashMap<>();

        itemStats.put("DAMAGE", 0.0);
        itemStats.put("STRENGTH", 0.0);
        itemStats.put("CRITICAL_DAMAGE", 0.0);
        itemStats.put("CRITICAL_CHANCE", 0.0);
        itemStats.put("HEALTH", 0.0);
        itemStats.put("DEFENSE", 0.0);
        itemStats.put("INTELLIGENCE", 0.0);
        itemStats.put("SPEED", 0.0);
        itemStats.put("ATTACK_SPEED", 0.0);
        itemStats.put("FEROCITY", 0.0);
        itemStats.put("TRUE_DEFENSE", 0.0);
        itemStats.put("ABILITY_DAMAGE_PERCENT", 0.0);
    }

    public void setReforgePool(ArrayList<String> pool, String category){
        reforgeCategory = category;
        toolTip.setReforgeList(pool.toArray(new String[pool.size()]));
    }

    public void setMaterial(String material){
        this.material = material;
    }

    public void setEnchantPool(ArrayList<String> pool){
        enchantPool = pool;
        toolTip.setEnchantList(pool.toArray(new String[pool.size()]));
    }

    public void setCategory(String category){
        this.category = category;
    }

    public void setRarity(String newRarity){
        itemRarity = Rarity.valueOf(newRarity);
        if (recombobulated)
            itemRarity = itemRarity.next();
    }

    public void setName(String name){
        itemName = name;
    }

    public void setDungeonTier(int tier, Double qualityBoost){
        statTier = tier;
        itemQuality = qualityBoost;
        hasDungeonTiers = true;
    }

    public void setReforge(String reforge){
        toolTip.setSelectedReforge(reforge);
        this.reforge = reforge;
    }

    public void setRecombobulated(){
        recombobulated = true;
        toolTip.setRecombobulated(recombobulated);
    }

    public void setDungeonItem(){
        dungeonItem = true;
    }

    public void setStars(int stars){
        starLevel = stars;
        toolTip.setStars(stars);
    }

    public void setPotatoBooks(int books){
        potatoBooks = books;
        toolTip.setBooks(books);
    }

    public void setStat(String stat, double value){
        itemStats.computeIfPresent(stat, (key, val) -> val + value);
    }

    public void addEnchant(String enchantment, String level){
        enchantments.put(enchantment, level);
        toolTip.addSelectedEnchant(enchantment, level);
    }

    public String getCategory(){
        return category;
    }

    public ArrayList<String> getEnchantPool(){
        return enchantPool;
    }

    public String getReforgeCategory(){
        return reforgeCategory;
    }

    public String getRarity(){
        return itemRarity.name();
    }

    public double getQualityBoost(){
        return itemQuality;
    }

    public String getReforge(){
        return reforge;
    }

    public String getMaterial(){
        return material;
    }

    public int getPotatoBooks(){
        return potatoBooks;
    }

    public int getStatTier(){
        return statTier;
    }

    public String getName(){
        return itemName;
    }

    public boolean hasDungeonTiers(){
        return hasDungeonTiers;
    }

    public boolean getDungeonStatus(){
        return dungeonItem;
    }

    public int getStars(){
        return starLevel;
    }

    public Map<String,Double> getStats(){
        return itemStats;
    }

    public Map<String,String> getEnchantments(){
        return enchantments;
    }

    public ItemTooltipPanel getToolTip(){
        return toolTip;
    }

    public void setChanged(){
        hasChanged = true;
    }

    public void refreshModifiers(){
        if (reforge != toolTip.getReforge())
            hasChanged = true;
        reforge = toolTip.getReforge();

        if (starLevel != toolTip.getStars())
            hasChanged = true;
        starLevel = toolTip.getStars();

        if (potatoBooks != toolTip.getBooks())
            hasChanged = true;
        potatoBooks = toolTip.getBooks();

        if (recombobulated != toolTip.getRecombob())
            hasChanged = true;
        recombobulated = toolTip.getRecombob();

        for (Entry<String, String> newEnchant : enchantments.entrySet()){
            if (!toolTip.getEnchants().containsKey(newEnchant.getKey())){
                hasChanged = true;
            }
        }

        for (Entry<String, String> newEnchant : toolTip.getEnchants().entrySet()){
            if ((!enchantments.containsKey(newEnchant.getKey())) || enchantments.get(newEnchant.getKey()) != newEnchant.getValue())
                hasChanged = true;
        }

        // clear enchants and add the ones from the tooltip
        enchantments.clear();
        for (Entry<String, String> newEnchant : toolTip.getEnchants().entrySet()){
            enchantments.put(newEnchant.getKey(), newEnchant.getValue());
        }
    }

    public void resetStats(){
        hasChanged = false;
        for (Entry<String, Double> stat : itemStats.entrySet()){
            itemStats.put(stat.getKey(), 0.0);
        }
    }

    public boolean hasChanged(){
        return hasChanged;
    }
}
