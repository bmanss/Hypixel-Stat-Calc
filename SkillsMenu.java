import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

public class SkillsMenu extends JFrame {
    final static int FARMING_MAX = 60;
    final static int MINING_MAX = 60;
    final static int COMBAT_MAX = 60;
    final static int FORAGING_MAX = 50;
    final static int FISHING_MAX = 50;
    final static int ALCHEMY_MAX = 50;
    final static int ENCHANTING_MAX = 60;
    final static int TAMING_MAX = 60;
    final static int CATACOMBS_MAX = 50;
    final static int CARPENTRY_MAX = 50;

    PlayerProfile currentProfile = null;
    MainWindow mainWindow = null;
    JSpinner farmingInput = new JSpinner();
    JSpinner miningInput = new JSpinner();
    JSpinner combatInput = new JSpinner();
    JSpinner foragingInput = new JSpinner();
    JSpinner fishingInput = new JSpinner();
    JSpinner enchantingInput = new JSpinner();
    JSpinner alchemyInput = new JSpinner();
    JSpinner tamingInput = new JSpinner();
    JSpinner carpentryInput = new JSpinner();
    JSpinner catacombsInput = new JSpinner();
    JSpinner skyBlocklevel = new JSpinner();
    Font baseFontBold = new Font("Arial",Font.BOLD,17);
    JLabel title = new JLabel("Skills");
    public JButton save = new JButton("Save");
    public JButton close = new JButton("Close");

    SkillsMenu(Dimension size, MainWindow mainwindow) {
        this.setPreferredSize(size);
        this.setUndecorated(true);
        this.setVisible(false);

        save.setMinimumSize(new Dimension(75,35));
        close.setMinimumSize(new Dimension(75,35));

        save.setFocusPainted(false);
        close.setFocusPainted(false);

        // save button will level for each skill to current value of input field
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentProfile.setSkyblockLevel((int)skyBlocklevel.getValue());
                currentProfile.setSkillLevel("FARMING", (int)farmingInput.getValue());
                currentProfile.setSkillLevel("MINING", (int)miningInput.getValue());
                currentProfile.setSkillLevel("COMBAT", (int)combatInput.getValue());
                currentProfile.setSkillLevel("FORAGING", (int)foragingInput.getValue());
                currentProfile.setSkillLevel("FISHING", (int)fishingInput.getValue());
                currentProfile.setSkillLevel("ENCHANTING", (int)enchantingInput.getValue());
                currentProfile.setSkillLevel("ALCHEMY", (int)alchemyInput.getValue());
                currentProfile.setSkillLevel("TAMING", (int)tamingInput.getValue());
                currentProfile.setSkillLevel("CARPENTRY", (int)carpentryInput.getValue());
                currentProfile.setSkillLevel("CATACOMBS", (int)catacombsInput.getValue());
                currentProfile.refreshGear();
                mainwindow.displayStats(currentProfile);
            }
        });

        // setPreferredSize(new Dimension(500,750));
        getContentPane().setBackground(Color.lightGray);
        
        // set models with Maximum value for that skill
        skyBlocklevel.setModel(new SpinnerNumberModel(0, 0, 500, 1));
        farmingInput.setModel(new SpinnerNumberModel(0, 0, FARMING_MAX, 1));
        miningInput.setModel(new SpinnerNumberModel(0, 0, MINING_MAX, 1));
        combatInput.setModel(new SpinnerNumberModel(0, 0, COMBAT_MAX, 1));
        foragingInput.setModel(new SpinnerNumberModel(0, 0, FORAGING_MAX, 1));
        fishingInput.setModel(new SpinnerNumberModel(0, 0, FISHING_MAX, 1));
        enchantingInput.setModel(new SpinnerNumberModel(0, 0, ENCHANTING_MAX, 1));
        alchemyInput.setModel(new SpinnerNumberModel(0, 0, ALCHEMY_MAX, 1));
        tamingInput.setModel(new SpinnerNumberModel(0, 0, TAMING_MAX, 1));
        carpentryInput.setModel(new SpinnerNumberModel(0, 0, CARPENTRY_MAX, 1));
        catacombsInput.setModel(new SpinnerNumberModel(0, 0, CATACOMBS_MAX, 1));

        // add components
        LC constraint = new LC();
        constraint.setFillX(true);
        setLayout(new MigLayout(constraint));
        add(title,"wrap, align center, span 2");
        add(new JLabel("SkyBlock Level: "));
        add(skyBlocklevel,"wrap");
        add(new JLabel("Farming:"));
        add(farmingInput, "wrap");
        add(new JLabel("Mining:"));
        add(miningInput, "wrap");
        add(new JLabel("Combat:"));
        add(combatInput, "wrap");
        add(new JLabel("Foraging:"));
        add(foragingInput, "wrap");
        add(new JLabel("Fishing:"));
        add(fishingInput, "wrap");
        add(new JLabel("Enchanting:"));
        add(enchantingInput, "wrap");
        add(new JLabel("Alchemy:"));
        add(alchemyInput, "wrap");
        add(new JLabel("Taming:"));
        add(tamingInput, "wrap");
        add(new JLabel("Carpentry:"));
        add(carpentryInput, "wrap");
        add(new JLabel("Catacombs:"));
        add(catacombsInput, "wrap");
        add(new JLabel(),"wrap");
        add(close);
        add(save);
        
        //set size for all input fields
        for (Component component : getContentPane().getComponents()){
            component.setFont(baseFontBold);
            if (component instanceof JSpinner){
                JSpinner spinner = (JSpinner) component;
                spinner.getEditor().setPreferredSize(new Dimension(60,25));
                spinner.getEditor().setMinimumSize(new Dimension(60,25));
            }
        }

        title.setFont(new Font("Arial",Font.BOLD,22));
        pack();
    }

    public void setProfile(PlayerProfile currentProfile){
         this.currentProfile = currentProfile;
         setSkillLevels();
    }

    void setSkillLevels(){
        skyBlocklevel.setValue((currentProfile.getSkyBlockLevel()));
        farmingInput.setValue(currentProfile.getSkillLevel("FARMING"));
        miningInput.setValue(currentProfile.getSkillLevel("MINING"));
        combatInput.setValue(currentProfile.getSkillLevel("COMBAT"));
        foragingInput.setValue(currentProfile.getSkillLevel("FORAGING"));
        fishingInput.setValue(currentProfile.getSkillLevel("FISHING"));
        enchantingInput.setValue(currentProfile.getSkillLevel("ENCHANTING"));
        alchemyInput.setValue(currentProfile.getSkillLevel("ALCHEMY"));
        tamingInput.setValue(currentProfile.getSkillLevel("TAMING"));
        carpentryInput.setValue(currentProfile.getSkillLevel("CARPENTRY"));
        catacombsInput.setValue(currentProfile.getSkillLevel("CATACOMBS"));
    }
}
