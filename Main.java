
import java.io.IOException;
import java.util.Map;
import org.json.JSONObject;


public class Main{

    private static double strength = 0;
    private static double critDamage = 0;
    private static double baseDamage = 0;
    private static double baseMultiplier = 0;
    private static int combatLevel = 0;
    private static InventoryItem helmet;

    private static JSONObject enchantList;

    public static void main(String[] args) throws IOException {  
        baseDamage = 35;
        strength = 237;
        critDamage = 166;
        baseMultiplier = 180.0 / 100.0;
        new MainWindow();
        
    }


    public static double Final(){
        return (5 + baseDamage) * (1 + (strength / 100)) * (1 + (critDamage / 100)) * (1 + baseMultiplier);
    }

    public static double calcBaseMultiplier(){
        double baseMultiplier = 0;
        baseMultiplier += combatMultiplier();

        for (Map.Entry<String,String> entry : helmet.getEnchantments().entrySet()){
            baseMultiplier += enchantList.getJSONObject(entry.getKey()).getDouble(entry.getValue());
        }
        return baseMultiplier;
    }

    public static int combatMultiplier(){
        if (combatLevel > 50 )
            return 200 + combatLevel % 10;
        return (combatLevel * 4);
    }   
}