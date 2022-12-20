import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.*;
import javax.swing.text.*;

import org.json.JSONObject;

import net.miginfocom.swing.MigLayout;

public class ItemTooltipPanel extends JLayeredPane implements ActionListener, ItemListener{
    StyleContext style = StyleContext.getDefaultStyleContext();
    AttributeSet attr = style.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(22, 60, 156));

    JCheckBox recombob = new JCheckBox("Recombobulated");
    JLabel Label_Stars = new JLabel("Stars: ");
    JLabel Label_Reforge = new JLabel("Reforge: ");
    JLabel Label_Enchants = new JLabel("Enchants: ");
    JLabel Label_Books = new JLabel("Books: ");
    JComboBox<Integer> starsCount =  new JComboBox<>(new Integer[]{0,1,2,3,4,5,6,7,8,9,10});
    JComboBox<Integer> bookCount =  new JComboBox<>(new Integer[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15});

    Font baseFont = new Font("Arial",Font.BOLD,15);

    String enchantCategory = "";

    JTextPane enchantDisplay = new JTextPane();

    JButton button_Add = new JButton("Add");
    JButton button_Clear = new JButton("Clear");
    JButton button_Remove = new JButton("Remove");

    JComboBox<String> enchantsList = new JComboBox<>();
    JComboBox<String> enchantLevel= new JComboBox<>(new String [] {"1"});
    JComboBox<String> reforgeList = new JComboBox<>();

    Map <String, String> addedEnchants = new HashMap<>();

    JSONObject allEnchantLevels;

    boolean isDisabled = false;
    boolean test = false;
    ItemTooltipPanel(){
        setSize(300, 300);
        setOpaque(true);
        setVisible(false);
        setBackground(new Color(99,96,96));
        enchantDisplay.setText("");
        enchantDisplay.setEditable(false);
        
        setLayout(new MigLayout());
        recombob.setOpaque(false);
        recombob.setFocusable(false);
        reforgeList.setPreferredSize(new Dimension(125,25));
        starsCount.setPreferredSize(new Dimension(50,25));
        bookCount.setPreferredSize(new Dimension(50,25));
        enchantsList.setPreferredSize(new Dimension(125,25));
        enchantLevel.setPreferredSize(new Dimension(50,25));

        enchantDisplay.setBackground(new Color(99,96,96));
        enchantDisplay.setFont(baseFont);

        button_Add.addActionListener(this);
        button_Clear.addActionListener(this);
        button_Remove.addActionListener(this);

        enchantsList.addItemListener(this);
        
        loadComponents();
    }

    public void loadComponents(){
        add(recombob,"wrap");
        add(Label_Reforge,"split");
        add(reforgeList,"gap 14px, wrap");
        if (test){
            add(new JComboBox<String>(),"wrap");
        }
        add(Label_Stars,"split");
        add(starsCount, "gap 29px , wrap");
        add(Label_Books,"split");
        add(bookCount, "gap 24px , wrap");
        add(Label_Enchants, "split 3");
        add(enchantsList);
        add(enchantLevel,"wrap");
        add(button_Add, "split 3");
        add(button_Remove);
        add(button_Clear,"wrap");
        add(enchantDisplay);
        test = true;    
       
    }

    public void disableModifiers(){
        isDisabled = true;
        enchantsList.setEnabled(false);
        reforgeList.setEnabled(false);
        enchantLevel.setEnabled(false);
        bookCount.setEnabled(false);
        starsCount.setEnabled(false);
        recombob.setEnabled(false);
        button_Add.setEnabled(false);
        button_Clear.setEnabled(false);
        button_Remove.setEnabled(false);

    }
    public void enableModifiers(){
        isDisabled = false;
        enchantsList.setEnabled(true);
        reforgeList.setEnabled(true);
        enchantLevel.setEnabled(true);
        bookCount.setEnabled(true);
        starsCount.setEnabled(true);
        recombob.setEnabled(true);
        button_Add.setEnabled(true);
        button_Clear.setEnabled(true);
        button_Remove.setEnabled(true);
    }

    public boolean isDisabled(){
        return isDisabled;
    }

    public void reloadComponents(){
        removeAll();
        loadComponents();
        revalidate();
    }

    public void setEnchantReference(JSONObject allEnchantLevels, String enchantCategory){
        this.enchantCategory = enchantCategory;
        this.allEnchantLevels = allEnchantLevels;
    }

    public void setStars(int stars){
        starsCount.setSelectedIndex(stars); 
        this.revalidate();
        this.repaint();
    }

    public void setBooks(int books){
        bookCount.setSelectedIndex(books); 
        
    }

    public void setRecombobulated(boolean recombobulated){
        recombob.setSelected(recombobulated);
        
    }

    public void setSelectedReforge(String selectedReforge){
        reforgeList.getModel().setSelectedItem(selectedReforge);
        
    }

    public void setReforgeList(String [] reforgePool){
        if (reforgePool != null)
            reforgeList.setModel(new DefaultComboBoxModel<>(reforgePool));
        else {
            reforgeList.setModel(new DefaultComboBoxModel<>(new String [] {""}));
        }
        
    }

    public void setEnchantList(String [] enchantPool){
        if (enchantPool != null){
            enchantsList.setModel(new DefaultComboBoxModel<>(enchantPool));
            if (!enchantCategory.equals(""))
                updateEnchantLevel();
        }
        else {
            enchantsList.setModel(new DefaultComboBoxModel<>(new String [] {""}));
            enchantLevel.setModel(new DefaultComboBoxModel<>(new String [] {""}));
        }

    }

    public void resetEnchantDisplay(){
        enchantDisplay.setText("");
        addedEnchants = new HashMap<>();
    }

    public void addSelectedEnchant(String enchant, String level){
        if (addedEnchants.containsKey("ultimate_one_for_all")){
            return;
        }
        else if (enchant.equals("ultimate_one_for_all")){
            addedEnchants.clear();
            addedEnchants.put(enchant, level);
            updateEnchantDisplay();
            return;
        }

        // make sure sharpness, bane of arthropods and smite cannot be on the same weapon
        if (enchant.equals("sharpness") && (addedEnchants.containsKey("bane_of_arthropods") || addedEnchants.containsKey("smite")))
            return;
        if ((enchant.equals("bane_of_arthropods") || enchant.equals("smite")) && addedEnchants.containsKey("sharpness"))
            return;
        if ((enchant.equals("bane_of_arthropods") && addedEnchants.containsKey("smite")) ||
            enchant.equals("smite") && addedEnchants.containsKey("bane_of_arthropods"))
            return;
         
        addedEnchants.put(enchant, level);
        updateEnchantDisplay();
    }

    void updateEnchantDisplay(){
        enchantDisplay.setEditable(true);
        int wrapCount = 0; 
        enchantDisplay.setCharacterAttributes(attr, false);
        enchantDisplay.setText("");
        for (Entry<String, String> nextEnchant : addedEnchants.entrySet()){
            enchantDisplay.replaceSelection(nextEnchant.getKey() + " " + nextEnchant.getValue() + "  ");
            ++wrapCount;
            if (wrapCount >= 2){
                enchantDisplay.replaceSelection("\n");
                wrapCount = 0;
            }
        }
        enchantDisplay.setEditable(false);
    }

    public String getReforge(){
        return reforgeList.getSelectedItem().toString();
    }

    public int getStars(){
        return (int) starsCount.getSelectedItem();
    }

    public boolean getRecombob(){
        return recombob.isSelected();
    }

    public Map<String, String> getEnchants(){
        return addedEnchants;
    } 

    public int getBooks(){
        return (int) bookCount.getSelectedItem();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button_Add){
            if (enchantsList.getSelectedItem() != null){
                addSelectedEnchant(enchantsList.getSelectedItem().toString(), enchantLevel.getSelectedItem().toString());
            }
        }
        else if (e.getSource() == button_Clear){
            addedEnchants.clear();
            updateEnchantDisplay();
        }
        else if (e.getSource() == button_Remove){
            if (enchantsList.getSelectedItem() != null){
                addedEnchants.remove(enchantsList.getSelectedItem().toString());
                updateEnchantDisplay();
            }
        }
    }

    public void updateEnchantLevel(){
        JSONObject enchantDirectory = allEnchantLevels.getJSONObject("armor");
        if (!enchantDirectory.has(enchantsList.getSelectedItem().toString()))
            enchantDirectory = allEnchantLevels.getJSONObject(enchantCategory.toLowerCase());

        String newLevels [] = new String[enchantDirectory.getJSONObject(enchantsList.getSelectedItem().toString()).length()];
        for (int index = 0; index < newLevels.length; ++index){
            newLevels[index] = String.valueOf(index + 1);
        }
        enchantLevel.setModel(new DefaultComboBoxModel<>(newLevels));
    }

    public void itemStateChanged(ItemEvent e) {
        updateEnchantLevel();
    }
}
