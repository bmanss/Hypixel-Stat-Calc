import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputListener;

import net.miginfocom.swing.MigLayout;
import org.json.*;

public class MainWindow extends JFrame implements ActionListener,ItemListener{
    DecimalFormat decimalFormatter = new DecimalFormat( "#.##" );
    
    Font baseFont = new Font("Arial",Font.PLAIN,17);
    Font baseFontBold = new Font("Arial",Font.BOLD,17);

    PlayerProfile mainProfile = null;
    PlayerProfile customProfile = new PlayerProfile();
    PlayerProfile currentProfile = null;

    ToolTipListener toolTipListener = new ToolTipListener();

    boolean validDependencies = false;

    double totalHealth = 100.0;
    double totalStrength = 0.0;
    double totalDefense = 0.0;
    double totalCritChance = 0.0;
    double totalCritDamage = 0.0;
    double totalDamage = 0.0;
    double totalIntelligence = 0.0;
    double totalSpeed = 0.0;
    double totalAttackSpeed = 0.0;
    double totalFerocity = 0.0;
    double totalMagicFind = 0.0;
    double totalTrueDefense = 0.0;

    int mobHealth = 0;

    JLabel healthLabel = new JLabel("Health:");
    JLabel strengthLabel = new JLabel("Strength:");
    JLabel defenseLabel = new JLabel("Defense:");
    JLabel intelligenceLabel = new JLabel("Intelligence:");
    JLabel critChanceLabel = new JLabel("Crit Chance:");
    JLabel critDamageLabel = new JLabel("Crit Damage:");
    JLabel SpeedLabel = new JLabel("Speed:");
    JLabel attackSpeedLabel = new JLabel("Attack Speed:");
    JLabel ferocityLabel = new JLabel("Ferocity:");
    JLabel damageLabel = new JLabel("Damage:");
    JLabel magicFindLabel = new JLabel("Magic Find:");
    JLabel trueDefenseLabel = new JLabel("True Defense:");
    JLabel magicalPowerLabel = new JLabel("Magical Power:");
    JLabel abilityDamageLabel = new JLabel("Ability Damage: ");
    JLabel manaLabel = new JLabel("Mana: ");

    JLabel firstStrikeDamageLabel = new JLabel("First Hit: ");
    JLabel abilityHitLabel = new JLabel("Ability Hit: ");

    JTextField mobHealthInput = new JTextField();
    JTextField healthInput = new JTextField();
    JTextField strengthInput = new JTextField();

    JCheckBox ability1 = new JCheckBox("Ability 1");
    JCheckBox ability2 = new JCheckBox("Ability 2");
    JCheckBox ability3 = new JCheckBox("Ability 3");
    JCheckBox ability4 = new JCheckBox("Ability 4");

    ArrayList <String> helmetOption = new ArrayList<>(Arrays.asList(""));
    ArrayList <String> chestOption = new ArrayList<>(Arrays.asList(""));
    ArrayList <String> legOptions = new ArrayList<>(Arrays.asList(""));
    ArrayList <String> bootOptions = new ArrayList<>(Arrays.asList(""));
    ArrayList <String> weaponOption = new ArrayList<>(Arrays.asList(""));
    
    ArrayList <String> necklaceOption = new ArrayList<>(Arrays.asList(""));
    ArrayList <String> cloakOption = new ArrayList<>(Arrays.asList(""));
    ArrayList <String> beltOption = new ArrayList<>(Arrays.asList(""));
    ArrayList <String> gauntletOption = new ArrayList<>(Arrays.asList(""));

    JComboBox<String> helmetBox = new JComboBox<>();
    JComboBox<String> chestplateBox = new JComboBox<>();
    JComboBox<String> leggingsBox = new JComboBox<>();
    JComboBox<String> bootsBox = new JComboBox<>();
    JComboBox<String> weaponBox = new JComboBox<>();
    JComboBox<String> reforgePowerBox = new JComboBox<>();

    JComboBox<String> necklaceBox = new JComboBox<>();
    JComboBox<String> cloakBox = new JComboBox<>();
    JComboBox<String> beltBox = new JComboBox<>();
    JComboBox<String> gauntletBox = new JComboBox<>();
    JComboBox<String> petsBox = new JComboBox<>();
    JComboBox<String> petsItemBox = new JComboBox<>();

    JSpinner petLevel = new JSpinner();
    JSpinner petTier = new JSpinner();

    String mobTypes [] = new String []{"None", "Blaze", "Creeper", "Dragon", "Enderman", "Lava Sea Creature", "Magma Cube" , "Mythological", "Pigmen", "Sea Creature",
                                                                   "Skeleton", "Slimes", "Spider", "Undead", "Wither" ,"Wolf", "Zombie"};
    JComboBox<String> mobTypesBox = new JComboBox<>(mobTypes);

    JCheckBox godPotion_CB = new JCheckBox("God Potion");
    JCheckBox dungeonStats_CB = new JCheckBox("Dungeon Stats");

    Map<String,JSONObject> allItems = new LinkedHashMap<String, JSONObject>();
    Map<String,JSONObject> accessoryItems = new LinkedHashMap<String, JSONObject>();

    String UUID = "323ab7bbe1974fde9c60fc9ed4b51e8b"; // me
    //String UUID = "f0ae77503b2347df86e7f053a3b39e86"; //ak1004nk
    String API_KEY = "1ae8df22-ce2f-492c-830d-0a529676bce6";

    final String mojangProfileAPI = "https://api.mojang.com/users/profiles/minecraft/";
    String itemListUrl = "https://api.hypixel.net/resources/skyblock/items";
    String skyblockPlayerAPI = "https://api.hypixel.net/skyblock/profiles?key=" + API_KEY + "&uuid=" + UUID;
    String skillsApi = "https://api.hypixel.net/resources/skyblock/skills";
    String apiResponseCode = "";

    JSONObject skillsApiInfo = null;
    JSONObject playerApi = null;
    JSONObject hypixelCustomValues = null;
    JSONObject hypixelItems = null;
    

    JButton JBloadProfile = new JButton("load Profile");
    JButton JBcustomProfile = new JButton("Custom Profile");
    JButton JBrefreshProfile = new JButton("Refresh Profile");
    JButton JBaddBaseStats = new JButton("Add Base Stats");
    JButton JBaddStaticStats = new JButton("Add Static Stats");

    GridBagConstraints gridContraints = new GridBagConstraints();

    JPanel buttonPanel = new JPanel();
    JPanel armorListPanel = new JPanel();
    JPanel statDisplayPanel = new JPanel();
    JPanel bordercontainer = new JPanel();
    JPanel damagePanel = new JPanel();
    JLayeredPane armorDisplayBase = new JLayeredPane();

    Container glassPanel;

    JLabel profileName = new JLabel("Profile: ");
    String namePlaceholer = "BigOofinator";
    Map<Component,String> manualValues = new LinkedHashMap<>();
    Map<Component,Integer> modifiersComponents = new HashMap<>(); 
    Map<JComboBox<String>,Integer> itemBoxComponents = new HashMap<>(); 
    Map<JCheckBox,Integer> abilityComponents = new HashMap<>(); 

    PopupFactory popFactory;
    Popup itemPopup;
    boolean popupIsVisible = false;
    JFrame mainWindow;
    ItemTooltipPanel overlay = new ItemTooltipPanel();
    
    // create Menu Bar
    JMenuBar menuBar = new JMenuBar();

    // create settings tab for menu bar and available options for settings
    JMenu settingsMenu = new JMenu("Settings");
    JMenuItem setProfileMI = new JMenuItem("Profile Name");
    JMenuItem apiKeyMI = new JMenuItem("Api Key");

    // create profile tab for menu bar and available options for profile
    JMenu profileMenu = new JMenu("Profile");
    JMenuItem skillsMI = new JMenuItem("Edit Skills");

    Border border = new LineBorder(Color.black, 1, false);
    JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);

    Component miscWeaponValue = null;
    JLabel miscDescription = null;
    boolean shouldAddMisc = false;

    ActionListener closeSkillsMenu;

    Dimension skillMenuSize = new Dimension(350,500);
    SkillsMenu skillMenu = new SkillsMenu(skillMenuSize,this);

    MainWindow(){

        // setup glass panel
        this.getRootPane().setGlassPane(new JComponent() {
            public void paintComponent(Graphics g){
                g.setColor(new Color(0,0,0,150));
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
        });
        glassPanel = (Container) this.getGlassPane();
        glassPanel.setVisible(false);

        this.setTitle("Hypixel Stat Calculator");
        mainWindow = this;
        this.addMouseListener(toolTipListener);
        this.setLayout(new GridBagLayout());
        this.setPreferredSize(new Dimension(1200,900));
        this.pack();
        initComponents();
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(true);
    }
    //add(Box.createRigidArea(new Dimension(0, 50)));       for space between buttons
    //add(Box.createVerticalGlue());                        for huge spaces

    public void initComponents(){ 
        // set up menu bar components
        sep.setBackground(Color.lightGray);
        menuBar.setPreferredSize(new Dimension(30,30));
        settingsMenu.setPreferredSize(new Dimension(75,30));
        setProfileMI.setPreferredSize(new Dimension(100,30));
        apiKeyMI.setPreferredSize(new Dimension(100,30));
        skillsMI.setPreferredSize(new Dimension(75,30));

        menuBar.setBackground(new Color(218,221,227));
        setProfileMI.setBackground(new Color(218,221,227));
        apiKeyMI.setBackground(new Color(218,221,227));
        skillsMI.setBackground(new Color(218,221,227));

        // set up button even handler for skills menu
        closeSkillsMenu = new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                mainWindow.setVisible(true);
                skillMenu.setVisible(false);
                glassPanel.setVisible(false);
                mainWindow.setEnabled(true);
            }
        };
        skillMenu.close.addActionListener(closeSkillsMenu);

        // create action listener for setting profile name or UUID
        setProfileMI.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String profileIdentifier = JOptionPane.showInputDialog(mainWindow, "Enter Profile Name or UUID: ", "Set Profile" ,JOptionPane.PLAIN_MESSAGE);
                if (profileIdentifier != null){
                    if (profileIdentifier.length() <= 13 ){
                            try {
                                profileIdentifier = fetchUUID(profileIdentifier);
                            } catch (JSONException e1) {
                                System.out.println("hu");
                            }
                    }
                    UUID = profileIdentifier;
                    skyblockPlayerAPI = "https://api.hypixel.net/skyblock/profiles?key=" + API_KEY + "&uuid=" + UUID;
                }
            }
        });
        // create action listener for setting API key
        apiKeyMI.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String key = JOptionPane.showInputDialog(mainWindow, "Enter API Key: ", "Set API Key" ,JOptionPane.PLAIN_MESSAGE);
                if (key != null){
                    API_KEY = key;
                    skyblockPlayerAPI = "https://api.hypixel.net/skyblock/profiles?key=" + API_KEY + "&uuid=" + UUID;
                }
            }
            
        });


        skillsMI.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (currentProfile != null){
                    int x = buttonPanel.getLocationOnScreen().x + mainWindow.getWidth() / 2;
                    int y = mainWindow.getLocationOnScreen().y + mainWindow.getHeight() / 2;
                    Dimension size = new Dimension(350,500);
                    int xOffset = (int) size.getWidth() / 4;
                    int yOffset = (int) size.getHeight() / 2;
                    skillMenu.setProfile(currentProfile);
                    skillMenu.setLocation(x - xOffset - 50, y -yOffset);
                    skillMenu.setVisible(true);
                    mainWindow.setEnabled(false);
                    glassPanel.setVisible(true);
                }
            }
        });

        settingsMenu.setBorderPainted(false);
        profileMenu.setBorderPainted(false);
        apiKeyMI.setBorderPainted(false);
        skillsMI.setBorderPainted(false);

        settingsMenu.add(setProfileMI);
        settingsMenu.add(sep);
        settingsMenu.add(apiKeyMI);
        profileMenu.add(skillsMI);
        menuBar.setBorder(border);
        menuBar.add(settingsMenu);
        menuBar.add(profileMenu);
        setJMenuBar(menuBar);

        godPotion_CB.setEnabled(false);
        dungeonStats_CB.setEnabled(false);
        JBrefreshProfile.setEnabled(false);
        JBrefreshProfile.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (currentProfile != null){
                    if (petsBox.getSelectedItem().toString().equals("None")){
                        currentProfile.setPet(petsBox.getSelectedItem().toString(), "None" ,1,1);
                        petsItemBox.setSelectedItem("None");
                        petLevel.setValue(1);
                        petTier.setValue(1);
                    }
                    else {
                        currentProfile.setPet(petsBox.getSelectedItem().toString(), petsItemBox.getSelectedItem().toString() ,(int) petLevel.getValue(),(int) petTier.getValue());
                    }
                    currentProfile.setMobHealth(getMobHealth());
                    currentProfile.refreshGear();
                    displayStats(currentProfile);
                    //currentProfile.printItems();
                }
            }
        });

        JBaddBaseStats.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                addBaseManualValues();
                currentProfile.refreshGear();
                displayStats(currentProfile);
            }
        });
        JBaddStaticStats.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                addStaticManualValues();
                currentProfile.refreshGear();
                displayStats(currentProfile);
            }
        });

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        armorListPanel.setLayout(new MigLayout());
        armorDisplayBase.setLayout(null);
        armorDisplayBase.setOpaque(true);

        armorListPanel.setBounds(this.getX(), this.getY(), this.getWidth() / 3 , this.getHeight());
        statDisplayPanel.setBounds(this.getX() + 400, this.getY(), this.getWidth() / 2 , this.getHeight());

        overlay.setBounds(mainWindow.getX(), this.getY(), overlay.getWidth() , overlay.getHeight());

        // add tooltip overlay, gear selection, and stat values 
        armorDisplayBase.add(overlay, JLayeredPane.PALETTE_LAYER);
        armorDisplayBase.add(armorListPanel, JLayeredPane.DEFAULT_LAYER);
        armorDisplayBase.add(statDisplayPanel, JLayeredPane.DEFAULT_LAYER);

        damagePanel.setLayout(new MigLayout("", "","[]20[]"));

        bordercontainer.setPreferredSize(new Dimension(400,500));
        bordercontainer.setMinimumSize(new Dimension(400,500));
        bordercontainer.setLayout(new BorderLayout());
        bordercontainer.add(armorDisplayBase,BorderLayout.CENTER);
        
        mobTypesBox.addItemListener(this);

        itemBoxComponents.put(helmetBox, 3);
        itemBoxComponents.put(chestplateBox, 2);
        itemBoxComponents.put(leggingsBox, 1);
        itemBoxComponents.put(bootsBox, 0);
        itemBoxComponents.put(necklaceBox, 4);
        itemBoxComponents.put(cloakBox, 5);
        itemBoxComponents.put(beltBox, 6);
        itemBoxComponents.put(gauntletBox, 7);
        itemBoxComponents.put(weaponBox, 8);
        ability1.setSelected(true);
        ability2.setSelected(true);
        ability3.setSelected(true);
        ability4.setSelected(true);

        helmetBox.setMaximumSize(new Dimension(200,25));
        helmetBox.setPreferredSize(new Dimension(200,25));

        chestplateBox.setMaximumSize(new Dimension(200,25));
        chestplateBox.setPreferredSize(new Dimension(200,25));

        leggingsBox.setMaximumSize(new Dimension(200,25));
        leggingsBox.setPreferredSize(new Dimension(200,25));

        bootsBox.setMaximumSize(new Dimension(200,25));
        bootsBox.setPreferredSize(new Dimension(200,25));

        necklaceBox.setMaximumSize(new Dimension(200,25));
        necklaceBox.setPreferredSize(new Dimension(200,25));

        cloakBox.setMaximumSize(new Dimension(200,25));
        cloakBox.setPreferredSize(new Dimension(200,25));

        beltBox.setMaximumSize(new Dimension(200,25));
        beltBox.setPreferredSize(new Dimension(200,25));

        gauntletBox.setMaximumSize(new Dimension(200,25));
        gauntletBox.setPreferredSize(new Dimension(200,25));

        weaponBox.setMaximumSize(new Dimension(200,25));
        weaponBox.setPreferredSize(new Dimension(200,25));

        reforgePowerBox.setMaximumSize(new Dimension(200,25));
        reforgePowerBox.setPreferredSize(new Dimension(200,25));

        petsBox.setMaximumSize(new Dimension(200,25));
        petsBox.setPreferredSize(new Dimension(200,25));

        petsItemBox.setMaximumSize(new Dimension(200,25));
        petsItemBox.setPreferredSize(new Dimension(200,25));
       
        petLevel.setPreferredSize(new Dimension(50,25));
        petLevel.setModel(new SpinnerNumberModel(1, 1, 100, 1));

        petTier.setPreferredSize(new Dimension(50,25));
        petTier.setModel(new SpinnerNumberModel(1, 1, 6, 1));

        loadGearComponents();

        // // add item listener to each gear selection box and checkbox
        for (Component component : armorListPanel.getComponents()){
            if (component instanceof JComboBox){
                @SuppressWarnings("unchecked")
                JComboBox<String> temp = (JComboBox<String>) component;
                temp.addItemListener(this);
            }
            else if (component instanceof JCheckBox){
                JCheckBox temp = (JCheckBox) component;
                temp.addItemListener(this);
            }
        };

        buttonPanel.setBackground(Color.gray);
        armorListPanel.setBackground(new Color(177,177,177));
        armorDisplayBase.setBackground(new Color(177,177,177));

        buttonPanel.setPreferredSize(new Dimension(200,500));
        armorListPanel.setPreferredSize(new Dimension(250,500));
        armorDisplayBase.setPreferredSize(new Dimension(250,500));
        statDisplayPanel.setPreferredSize(new Dimension(10,500));

        buttonPanel.setMinimumSize(new Dimension(200,500));
        armorListPanel.setMinimumSize(new Dimension(200,500));
        armorDisplayBase.setMinimumSize(new Dimension(200,500));
        statDisplayPanel.setMinimumSize(new Dimension(200,500));

        buttonPanel.add(Box.createRigidArea(new Dimension(0, 50))); 

        JBloadProfile.setFocusPainted(false);
        JBloadProfile.setMaximumSize(new Dimension(130,50));
        JBloadProfile.setAlignmentX(Component.CENTER_ALIGNMENT);
        JBloadProfile.addActionListener(this);

        JBcustomProfile.setFocusPainted(false);
        JBcustomProfile.setMaximumSize(new Dimension(130,50));
        JBcustomProfile.setAlignmentX(Component.CENTER_ALIGNMENT);
        JBcustomProfile.addActionListener(this);

        JBrefreshProfile.setFocusPainted(false);
        JBrefreshProfile.setMaximumSize(new Dimension(130,50));
        JBrefreshProfile.setAlignmentX(Component.CENTER_ALIGNMENT);

        JBaddBaseStats.setFocusPainted(false);
        JBaddBaseStats.setPreferredSize(new Dimension(130,35));

        JBaddStaticStats.setFocusPainted(false);
        JBaddStaticStats.setPreferredSize(new Dimension(130,35));

        statDisplayPanel.setBackground(new Color(177,177,177));
        statDisplayPanel.setLayout(new MigLayout());
        
        //add UI element for entering manual values
        createMaunalStatEntry();

        // add buttons for accepting those values
        statDisplayPanel.add(JBaddBaseStats, "cell 0 14");
        statDisplayPanel.add(JBaddStaticStats,"cell 0 14");

        healthLabel.setForeground(new Color(203,13,13));
        defenseLabel.setForeground(Color.green);
        intelligenceLabel.setForeground(new Color(43,227,223));
        damageLabel.setForeground(Color.BLACK);
        strengthLabel.setForeground(new Color(203,13,13));
        critChanceLabel.setForeground(Color.blue);
        critDamageLabel.setForeground(Color.blue);
        ferocityLabel.setForeground(new Color(203,13,13));
        SpeedLabel.setForeground(Color.white);
        magicFindLabel.setForeground(new Color(43,227,223));
        trueDefenseLabel.setForeground(Color.white);
        abilityDamageLabel.setForeground(new Color(203,13,13));
        attackSpeedLabel.setForeground(Color.yellow);
        magicalPowerLabel.setForeground(Color.orange);

        statDisplayPanel.add(healthLabel, "cell 0 0");
        statDisplayPanel.add(defenseLabel,"cell 0 1");
        statDisplayPanel.add(SpeedLabel,"cell 0 2");
        statDisplayPanel.add(strengthLabel,"cell 0 4");
        statDisplayPanel.add(intelligenceLabel,"cell 0 5");
        statDisplayPanel.add(critChanceLabel,"cell 0 6");
        statDisplayPanel.add(critDamageLabel,"cell 0 7");
        statDisplayPanel.add(attackSpeedLabel,"cell 0 8");
        statDisplayPanel.add(abilityDamageLabel,"cell 0 9");
        statDisplayPanel.add(magicFindLabel,"cell 0 10");
        statDisplayPanel.add(trueDefenseLabel,"cell 0 11");
        statDisplayPanel.add(ferocityLabel,"cell 0 12");
        statDisplayPanel.add(damageLabel,"cell 0 3");
        statDisplayPanel.add(magicalPowerLabel,"cell 0 13");
        mobHealthInput.setPreferredSize(new Dimension(150,30));

        damagePanel.setBackground(Color.gray);
        damagePanel.add(new JLabel("Mob Type:"), "split 2");
        damagePanel.add(mobTypesBox);
        damagePanel.add(new JLabel("Health: "), "split 2");
        damagePanel.add(mobHealthInput,"wrap");
        damagePanel.add(firstStrikeDamageLabel,"wrap");
        damagePanel.add(abilityHitLabel);
        statDisplayPanel.add(damagePanel, "cell 0 15");

        changefont(statDisplayPanel, baseFontBold);
        changefont(profileName, baseFontBold);


        gridContraints.weightx = 0;
        gridContraints.weighty = 1;
        gridContraints.fill = GridBagConstraints.BOTH;
        add(buttonPanel,gridContraints);
        gridContraints.weightx = 1;
        add(bordercontainer,gridContraints);
        gridContraints.weightx = 1;
        //add(statDisplayPanel,gridContraints);
        validate();

        damagePanel.setPreferredSize(new Dimension(damagePanel.getX() + (statDisplayPanel.getWidth() - 150),200));
        profileName.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(profileName);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 30))); 
        buttonPanel.add(JBloadProfile);
        buttonPanel.add(JBcustomProfile);
        buttonPanel.add(JBrefreshProfile);
    }

    public void loadGearComponents(){
        
        armorListPanel.add(new JLabel("Armor: "),"wrap");
        armorListPanel.add(helmetBox);
        armorListPanel.add(new JLabel("Modifiers"),"Wrap");
        armorListPanel.add(chestplateBox);
        armorListPanel.add(new JLabel("Modifiers"),"Wrap");
        armorListPanel.add(leggingsBox);
        armorListPanel.add(new JLabel("Modifiers"),"Wrap");
        armorListPanel.add(bootsBox);
        armorListPanel.add(new JLabel("Modifiers"),"Wrap");
        armorListPanel.add(new JLabel("Equipment: "),"wrap");
        armorListPanel.add(necklaceBox);
        armorListPanel.add(new JLabel("Modifiers"),"Wrap");
        armorListPanel.add(cloakBox);
        armorListPanel.add(new JLabel("Modifiers"),"Wrap");
        armorListPanel.add(beltBox);
        armorListPanel.add(new JLabel("Modifiers"),"Wrap");
        armorListPanel.add(gauntletBox);
        armorListPanel.add(new JLabel("Modifiers"),"Wrap");
        armorListPanel.add(new JLabel("Weapon: "),"wrap");
        armorListPanel.add(weaponBox);
        armorListPanel.add(new JLabel("Modifiers"),"Wrap");

        if (shouldAddMisc){
            armorListPanel.add(miscDescription,"wrap");
            armorListPanel.add(miscWeaponValue,"wrap");
        }

        armorListPanel.add(new JLabel("PowerStone: "),"wrap");
        armorListPanel.add(reforgePowerBox,"wrap");
        armorListPanel.add(new JLabel("Pet: "));
        armorListPanel.add(new JLabel(" Level:      "),"split 2");
        armorListPanel.add(new JLabel("Tier: "),"wrap");
        armorListPanel.add(petsBox);
        armorListPanel.add(petLevel, "split 2");
        armorListPanel.add(petTier,"wrap");
        armorListPanel.add(petsItemBox,"wrap");
        armorListPanel.add(ability1, "split 2");
        armorListPanel.add(ability2,"wrap");
        armorListPanel.add(ability3,"split 2");
        armorListPanel.add(ability4,"wrap");
        armorListPanel.add(godPotion_CB,"wrap");
        armorListPanel.add(dungeonStats_CB);

        int armorIndex = 3;
        int equipIndex = 0;

        // add mouse listeners and effects to each modifier label
        for (Component component : armorListPanel.getComponents()){
            if (component instanceof JLabel && ((JLabel) component).getText().equals("Modifiers")){
                JLabel lb = (JLabel) component;
                if (armorIndex >= 0){
                    modifiersComponents.put(lb, armorIndex);
                    --armorIndex;
                }
                else{
                    modifiersComponents.put(lb, equipIndex);
                }
                lb.setPreferredSize(new Dimension(100,30));
                lb.setHorizontalAlignment(SwingConstants.CENTER);
                lb.addMouseListener(toolTipListener);  
                lb.setOpaque(true);
                lb.setBackground(Color.gray);
                ++equipIndex;
            }
        }
    }

    public void reloadArmorPanel(){
        modifiersComponents.clear();
        armorListPanel.removeAll();
        loadGearComponents();
        revalidate();
        repaint();
    }

    public void addNewWeaponValue(Component fieldType,String description, String [] options){
        miscDescription = new JLabel(description);
        if (fieldType instanceof JComboBox){
            miscWeaponValue = new JComboBox<String>(options);
        }
        else {
            miscWeaponValue = new JTextField();
        }
        miscWeaponValue.setMaximumSize(new Dimension(200,25));
        miscWeaponValue.setPreferredSize(new Dimension(200,25));
        ((JComponent) miscWeaponValue).setBorder(javax.swing.BorderFactory.createEmptyBorder());
        shouldAddMisc = true;
    }

    public void resetAbilityStatus(){
        ability1.setSelected(true);
        ability2.setSelected(true);
        ability3.setSelected(true);
        ability4.setSelected(true);
    }

    public void createMaunalStatEntry(){
        Map<String,Double> stats = customProfile.getAllStats();
        int columnIndex = 0;
        for (Entry<String,Double> statList : stats.entrySet()){
            String column = "cell 0 " + Integer.toString(columnIndex) + "";
            JSpinner spinner = new JSpinner();
            spinner.setModel(new SpinnerNumberModel(0.0, -10000.0, 10000.0, 0.1));
            spinner.getEditor().setPreferredSize(new Dimension(50,25)); 
            statDisplayPanel.add(spinner,column);
            manualValues.put(spinner, statList.getKey());
            ++columnIndex;
        }
    }
    public void addBaseManualValues(){
        for (Entry<Component,String> modifierValues : manualValues.entrySet()){
            JSpinner spinner = (JSpinner) modifierValues.getKey();
            Double enteredValue = (double) spinner.getValue();
            currentProfile.addToStats(currentProfile.statTotals,modifierValues.getValue(), enteredValue);
            spinner.setValue(0.0);
        }
    }

    public void addStaticManualValues(){
        for (Entry<Component,String> modifierValues : manualValues.entrySet()){
            JSpinner spinner = (JSpinner) modifierValues.getKey();
            Double enteredValue = (double) spinner.getValue();
            currentProfile.addToStats(currentProfile.staticStats,modifierValues.getValue(), enteredValue);
            spinner.setValue(0.0);
        }
    }

    public void changefont(Component component, Font font){
        component.setFont(font);
        if (component instanceof Container){
            for (Component child : ((Container) component).getComponents()){
                changefont (child, font);
            }
        }
    }

    public void loadHypixelValues() throws IOException{
        String jarPath = getClass()
          .getProtectionDomain()
          .getCodeSource()
          .getLocation()
          .getPath();
        
        jarPath = jarPath.replace("%20", " ");
        File enchantsFile = new File(jarPath.toString().substring(0, jarPath.lastIndexOf("/") + 1) + "HypixelValues.json");
        BufferedReader fileReader;
        StringBuilder readerContents = new StringBuilder();

        // read in json file with static values associated with specific items or enchants
        fileReader = new BufferedReader(new FileReader(enchantsFile));
        String readerLine;
        while( (readerLine = fileReader.readLine()) != null) {
            readerContents.append(readerLine);
        }
        fileReader.close();
        hypixelCustomValues = new JSONObject(readerContents.toString());
    } 

    public JSONObject readFromApi(String APIurl, Boolean isGzip) throws JSONException, IOException{
        URL url = new URL(APIurl);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        InputStream apiContentsStream = null;
        urlConnection.setRequestMethod("GET");
        if (isGzip){
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            apiResponseCode = urlConnection.getResponseMessage();
            apiContentsStream = new GZIPInputStream(urlConnection.getInputStream());
        }
        else {
            apiContentsStream = urlConnection.getInputStream();
        }
        
        InputStreamReader contentsReader= new InputStreamReader(apiContentsStream);
        BufferedReader ContentsBuffer = new BufferedReader(contentsReader);

        urlConnection.connect();
        try {
            return new JSONObject(ContentsBuffer.readLine());
        } catch (Exception e) {
            apiResponseCode = urlConnection.getResponseMessage();
            return null;
        }
    }

    /**
     * Attempt to fetch UUID from Mojang's API.
     * @param profileName Player's ingame name.
     * @return Valid UUID or NULL if profile name is not valid,
     */
    public String fetchUUID(String profileName){
        JSONObject apiResponse = null;
        namePlaceholer = profileName;
        // if profile name is not valid return null 
        try {
            apiResponse = readFromApi(mojangProfileAPI + profileName,false);
            return apiResponse.getString("id");
        } catch (Exception e) {
            return null;
        }
    }

    public void sortItemList(){
        JSONArray itemList = hypixelItems.getJSONArray("items");
        for (int i = 0; i < itemList.length(); i++){            
            if (itemList.getJSONObject(i).has("category")){
                String category = (String) itemList.getJSONObject(i).get("category");
                String referenceName = itemList.getJSONObject(i).getString("name").toLowerCase();
                String referenceID= (String) itemList.getJSONObject(i).get("id");
                JSONObject refrenceItem = itemList.getJSONObject(i);
                switch(category){
                    case "HELMET":
                        helmetOption.add(referenceName);
                        break;
                    case "CHESTPLATE":
                        chestOption.add(referenceName);
                        break;
                    case "LEGGINGS":
                        legOptions.add(referenceName);
                        break;
                    case "BOOTS":
                        bootOptions.add(referenceName);
                        break;
                    case "SWORD":
                        weaponOption.add(referenceName);
                        break;
                    case "BOW":
                        weaponOption.add(referenceName);
                        break;
                    case "FISHING_WEAPON":
                        weaponOption.add(referenceName);
                        break;
                    case "NECKLACE":
                        necklaceOption.add(referenceName);
                        break;
                    case "CLOAK":
                        cloakOption.add(referenceName);
                        break;
                    case "BELT":
                        beltOption.add(referenceName);
                        break;
                    case "GLOVES":
                        gauntletOption.add(referenceName);
                        break;
                    case "BRACELET":
                        gauntletOption.add(referenceName);
                        break;
                }

                if (category.equals("ACCESSORY"))
                    accessoryItems.put(referenceID, refrenceItem);

                // only add it to allitems list if it the category is not one of these
                else if (!category.equals("COSMETIC") && !category.equals("REFORGE_STONE") && !category.equals("PET_ITEM") &&
                         !category.equals("SHEARS") && !category.equals("DUNGEON_PASS") && !category.equals("ARROW") && 
                         !category.equals("TRAVEL_SCROLL") && !category.equals("DEPLOYABLE") && !category.equals("ARROW_POISON") &&
                         !category.equals("BAIT")){
                            
                    allItems.put(referenceName, refrenceItem);
                }
            }
            // TODO: fix items like white gift box showing in weapon slot
            else {
                String referenceName = itemList.getJSONObject(i).getString("name").toLowerCase();
                JSONObject refrenceItem = itemList.getJSONObject(i);
                if (refrenceItem.has("stats")){
                    allItems.put(referenceName, refrenceItem);
                    weaponOption.add(referenceName);
                }
            }
        }
        
        Collections.sort(helmetOption);
        Collections.sort(chestOption);
        Collections.sort(legOptions);
        Collections.sort(bootOptions);
        Collections.sort(weaponOption);

        Collections.sort(necklaceOption);
        Collections.sort(cloakOption);
        Collections.sort(beltOption);
        Collections.sort(gauntletOption);
        
        helmetBox.setModel(new DefaultComboBoxModel<String>(helmetOption.toArray(new String[helmetOption.size()])));
        chestplateBox.setModel(new DefaultComboBoxModel<String>(chestOption.toArray(new String[chestOption.size()])));
        leggingsBox.setModel(new DefaultComboBoxModel<String>(legOptions.toArray(new String[legOptions.size()])));
        bootsBox.setModel(new DefaultComboBoxModel<String>(bootOptions.toArray(new String[bootOptions.size()])));
        weaponBox.setModel(new DefaultComboBoxModel<String>(weaponOption.toArray(new String[weaponOption.size()])));

        necklaceBox.setModel(new DefaultComboBoxModel<String>(necklaceOption.toArray(new String[necklaceOption.size()])));
        cloakBox.setModel(new DefaultComboBoxModel<String>(cloakOption.toArray(new String[cloakOption.size()])));
        beltBox.setModel(new DefaultComboBoxModel<String>(beltOption.toArray(new String[beltOption.size()])));
        gauntletBox.setModel(new DefaultComboBoxModel<String>(gauntletOption.toArray(new String[gauntletOption.size()])));

    }

    public void displayStats(PlayerProfile profile){
        // jerry boost only visual
        // double jerryBoost = 1.1;

        healthLabel.setText("Health: " + decimalFormatter.format(profile.getStat("HEALTH")) + " + (" + profile.getStaticStat("HEALTH") + ")");
        strengthLabel.setText("Strength: " + decimalFormatter.format(profile.getStat("STRENGTH")) + " + (" + profile.getStaticStat("STRENGTH") + ")");
        defenseLabel.setText("Defense: " + decimalFormatter.format(profile.getStat("DEFENSE")) + " + (" + profile.getStaticStat("DEFENSE"));
        intelligenceLabel.setText("Intelligence: " + decimalFormatter.format(profile.getStat("INTELLIGENCE")) + " + (" + profile.getStaticStat("INTELLIGENCE") + ")"); 
        critChanceLabel.setText("Crit Chance: " + decimalFormatter.format(profile.getStat("CRITICAL_CHANCE")) + " + (" + profile.getStaticStat("CRITICAL_CHANCE") + ")");
        critDamageLabel.setText("Crit Damage: " + decimalFormatter.format(profile.getStat("CRITICAL_DAMAGE")) + " + (" + profile.getStaticStat("CRITICAL_DAMAGE") + ")");
        SpeedLabel.setText("Speed: " + decimalFormatter.format(profile.getStat("WALK_SPEED")) + " + (" + profile.getStaticStat("WALK_SPEED") + ")");
        attackSpeedLabel.setText("Attack Speed: " + decimalFormatter.format(profile.getStat("ATTACK_SPEED")) + " + (" + profile.getStaticStat("ATTACK_SPEED") + ")");
        ferocityLabel.setText("Ferocity: " + decimalFormatter.format(profile.getStat("FEROCITY")) + " + (" + profile.getStaticStat("FEROCITY") + ")");
        damageLabel.setText("Damage: " + decimalFormatter.format(profile.getStat("DAMAGE")) + " + (" + profile.getStaticStat("DAMAGE") + ")");
        magicFindLabel.setText("Magic Find: " + decimalFormatter.format(profile.getStat("MAGIC_FIND")) + " + (" + profile.getStaticStat("MAGIC_FIND") + ")");
        trueDefenseLabel.setText("True Defense: " + decimalFormatter.format(profile.getStat("TRUE_DEFENSE")) + " + (" + profile.getStaticStat("TRUE_DEFENSE") + ")");
        magicalPowerLabel.setText("Magical Power: " + (int) profile.getStat("MAGICAL_POWER"));
        abilityDamageLabel.setText("Ability Damage: " + decimalFormatter.format(profile.getStat("ABILITY_DAMAGE_PERCENT")) + " + (" + profile.getStaticStat("ABILITY_DAMAGE_PERCENT") + ")");
        //manaLabel.setText("Mana: " + decimalFormatter.format(profile.getStat("INTELLIGENCE") + 100)); 

        // TODO: maybe add dialog popup for wrong health format
        currentProfile.setMobHealth(getMobHealth());
        firstStrikeDamageLabel.setText("First Hit: " + currentProfile.getWeaponDamage());
        abilityHitLabel.setText("Ability Hit: " + currentProfile.getMageDamage());
        
    }

    public Long getMobHealth(){
        Long health = 0L;
        try {
            health = Long.parseLong(mobHealthInput.getText());
        } catch (NumberFormatException e) {
            health = 0L;
            mobHealthInput.setText("0");
        }
        return health;
    }

    public void setActiveItems(){
        for (Entry<JComboBox<String>, Integer> listComponent : itemBoxComponents.entrySet()){
            JComboBox<String> currentBox = listComponent.getKey();
            String itemName = currentProfile.getItem(listComponent.getValue()).getName();
            currentBox.getModel().setSelectedItem(itemName);
        }
        ArrayList<String> allStones = new ArrayList<>(Arrays.asList("None"));
        for (String stone : hypixelCustomValues.getJSONObject("PowerStone").keySet()){
            allStones.add(stone);
        }
        Collections.sort(allStones);
        reforgePowerBox.setModel(new DefaultComboBoxModel<String>(allStones.toArray(new String [allStones.size()])));
        reforgePowerBox.getModel().setSelectedItem(currentProfile.getPowerStone());
        petsBox.setSelectedItem(currentProfile.getActivePet());
        petsItemBox.setSelectedItem(currentProfile.getActivePetItem());
        petLevel.setValue(currentProfile.getPetLevel());
        petTier.setValue(currentProfile.getPetRarity());
    }

    public void createPetOptions(){
        ArrayList<String> petNames = new ArrayList<>();
        ArrayList<String> petItems = new ArrayList<>();

        JSONObject petList = hypixelCustomValues.getJSONObject("Pets");
        JSONObject petItemList = hypixelCustomValues.getJSONObject("Pet items");

        for (Object pet : petList.names()){
            petNames.add(pet.toString());
        }
        for (Object petItem : petItemList.names()){
            petItems.add(petItem.toString());
        }
        Collections.sort(petNames);
        Collections.sort(petItems);
        petNames.add(0,"None");
        petItems.add(0, "None");
        petsItemBox.setModel(new DefaultComboBoxModel<>(petItems.toArray(new String [petItems.size()])));
        petsBox.setModel(new DefaultComboBoxModel<>(petNames.toArray(new String [petNames.size()])));
    }


    public void loadDefaultState() throws JSONException, IOException{

        // try and load dependencies
        if (!validDependencies){
            hypixelItems = readFromApi(itemListUrl,true);
            sortItemList();
            loadHypixelValues();
        }

        createPetOptions();
        resetAbilityStatus();
        mobTypesBox.setSelectedItem("None");
        petsBox.setSelectedItem(" ");
        petLevel.setValue(1);
        petTier.setValue(1);
        if (!godPotion_CB.isEnabled()){
            godPotion_CB.setEnabled(true);
            dungeonStats_CB.setEnabled(true);
        }
            
        JBrefreshProfile.setEnabled(true);
        godPotion_CB.setSelected(false);
        dungeonStats_CB.setSelected(false);
        validDependencies = true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {  

        // try to load item list and hypixel master JSON dependencies
        try {
            loadDefaultState();
        } catch (JSONException | IOException e2) {
            if (!apiResponseCode.equals("OK"))
                JOptionPane.showMessageDialog(mainWindow, "Unable to retrieve Items from API.", "Error", JOptionPane.ERROR_MESSAGE);
            else {
                JOptionPane.showMessageDialog(mainWindow, "Unable to read HypixelValues.json.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        if (e.getSource() == JBloadProfile && validDependencies){
            // if player profile or api key is invalid display proper message and return from action call
            try {
                playerApi = readFromApi(skyblockPlayerAPI,true);
                skillsApiInfo = readFromApi(skillsApi,true);
            } catch (Exception e3) {
                if (apiResponseCode.equals("Forbidden"))
                    JOptionPane.showMessageDialog(mainWindow, "Invalid API Key.", "Error", JOptionPane.ERROR_MESSAGE);
                else if (apiResponseCode.equals("Unprocessable Entity"))
                    JOptionPane.showMessageDialog(mainWindow, "Invalid Profile.", "Error", JOptionPane.ERROR_MESSAGE);
                else {
                    JOptionPane.showMessageDialog(mainWindow, "Unable to load profile.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                return;
            }

            mainProfile = new PlayerProfile();
            currentProfile = mainProfile;
            mainProfile.addItemList(allItems,accessoryItems);
            mainProfile.setPlayerApi(playerApi,UUID);
            mainProfile.setCustomValues(hypixelCustomValues);
            mainProfile.setSkillsApiMilestones(skillsApiInfo);
            try {
                mainProfile.parsePlayerProfile();
                setActiveItems();
                displayStats(currentProfile);
                profileName.setText("Profile: " + namePlaceholer);
            } catch (JSONException | IOException e1) {
                e1.printStackTrace();
                godPotion_CB.setEnabled(false);
                dungeonStats_CB.setEnabled(false);
                JBrefreshProfile.setEnabled(false);
                JOptionPane.showMessageDialog(mainWindow, "Unable to process profile. Unexpected Error.\n" + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        else if(e.getSource() == JBcustomProfile && validDependencies){
            customProfile = new PlayerProfile();
            resetAbilityStatus();
            godPotion_CB.setEnabled(true);
            dungeonStats_CB.setEnabled(true);
            godPotion_CB.setSelected(false);
            dungeonStats_CB.setSelected(false);
            JBrefreshProfile.setEnabled(true);
            currentProfile = customProfile;
            customProfile.addItemList(allItems,accessoryItems);
            customProfile.setCustomValues(hypixelCustomValues);
            setActiveItems();
            currentProfile.initCustomProfile();
            displayStats(customProfile);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == ability1){
            currentProfile.setPetAbilityStatus(0, e.getStateChange());
        }
        if (e.getSource() == ability2){
            currentProfile.setPetAbilityStatus(1, e.getStateChange());
        }
        if (e.getSource() == ability3){
            currentProfile.setPetAbilityStatus(2, e.getStateChange());
        }
        if (e.getSource() == ability4){
            currentProfile.setPetAbilityStatus(3, e.getStateChange());
        }

        if (e.getStateChange() == ItemEvent.SELECTED && e.getSource() == mobTypesBox){
            currentProfile.setSelectedMob(mobTypesBox.getSelectedItem().toString());
        }

        if (e.getItem() == godPotion_CB && e.getStateChange() == ItemEvent.SELECTED && currentProfile !=null){
            currentProfile.setGodPotionStats(true);
            displayStats(currentProfile);
        }
        else if (e.getItem() == godPotion_CB && currentProfile !=null){
            currentProfile.setGodPotionStats(false);
            displayStats(currentProfile);
        }

        if (e.getItem() == dungeonStats_CB && e.getStateChange() == ItemEvent.SELECTED && currentProfile !=null){
            currentProfile.useDungeonStats(true);
            displayStats(currentProfile);
        }
        else if (e.getItem() == dungeonStats_CB && currentProfile !=null){
            currentProfile.useDungeonStats(false);
            displayStats(currentProfile);
        }

        if (e.getStateChange() == ItemEvent.SELECTED && e.getSource() == reforgePowerBox){
            currentProfile.setPowerStone(e.getItem().toString());
        }
        // change item name and reset modifiers *stats are updated on refresh
        else if (e.getStateChange() == ItemEvent.SELECTED &&  itemBoxComponents.containsKey(e.getSource())){
            int itemIndex = itemBoxComponents.get(e.getSource());
            InventoryItem gearPiece = currentProfile.getItem(itemIndex);
            if (gearPiece.getName() != e.getItem().toString()){
                currentProfile.changeItemName(itemIndex, e.getItem().toString());
                currentProfile.setWeaponModifierPool(gearPiece,"");
                gearPiece.resetModifiers(gearPiece.getEnchantPool(), gearPiece.getReforgePool());

                // check for weapons that have special parameter and add UI element for it
                if (itemIndex == currentProfile.WEAPON_INDEX){
                    InventoryItem weapon = currentProfile.getItem(currentProfile.WEAPON_INDEX);
                    if (weapon.getName().equals("emerald blade")){
                        addNewWeaponValue(new JTextArea(), "Purse Amount:", null);
                        reloadArmorPanel();
                    }
                    else if (weapon.getName().equals("hyperion")){
                        addNewWeaponValue(new JComboBox<String>(), "Ability:", new String [] {"Implosion"});
                        reloadArmorPanel();
                    }
                    else if (weapon.getName().equals("midas staff")){
                        addNewWeaponValue(new JTextArea(), "Purchase Amount:", null);
                        reloadArmorPanel();
                    }
                    else if(shouldAddMisc){
                        shouldAddMisc = false;
                        reloadArmorPanel();
                    }

                }
            }

        }
    }

    public class ToolTipListener implements MouseInputListener{
        
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
        }

        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {
        }

        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {
        }

        @Override
        public void mouseEntered(java.awt.event.MouseEvent e) {
            if (e.getComponent() instanceof JLabel && currentProfile != null){
                Point p = e.getComponent().getLocation();
                if (overlay.isVisible()){
                    overlay.setVisible(false);
                }
                armorDisplayBase.remove(overlay);
                overlay = currentProfile.getItem(modifiersComponents.get(e.getComponent())).getToolTip();
                overlay.addMouseListener(toolTipListener);
                armorDisplayBase.add(overlay, JLayeredPane.PALETTE_LAYER);
                overlay.setBounds(p.x + 100 ,p.y, overlay.getWidth(), overlay.getHeight());
                overlay.setVisible(true);
            }

            //TODO: mouse out of window hide overlay
            if (e.getComponent() == mainWindow && overlay.isVisible()){
                overlay.setVisible(false);
            }
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent e) {
        }

        @Override
        public void mouseDragged(java.awt.event.MouseEvent e) {
        }

        @Override
        public void mouseMoved(java.awt.event.MouseEvent e) {
        }
        
    }
}