import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class GUI implements ActionListener{

	private JFrame mainFrame;
	private JPanel mainPanel;
	private JPanel topPanel;
	private JPanel centerPanel;
	private JPanel botPanel;

	//Input
	//NEW 
	JLabel inLabelEventKey ;
	JLabel inLabelStartingIndex ;
	JLabel inLabelChar1;
	JLabel inLabelChar2;
	JLabel inLabelChar3;
	JLabel inLabelFaction1;
	JLabel inLabelFaction2;
	JLabel inLabelFaction3;
	JLabel inLabelRegion1;
	JLabel inLabelRegion2;
	JLabel inLabelRegion3;
	
	JTextField inEventKey;
	JTextField inStartingIndex;
	JTextField inChar1;
	JTextField inChar2;
	JTextField inChar3;
	JTextField inFaction1;
	JTextField inFaction2;
	JTextField inFaction3;
	JTextField inRegion1;
	JTextField inRegion2;
	JTextField inRegion3;
	//CLONE
	ComboBoxRenderer renderer;
	JLabel modeLabelDropdown;
	JLabel modeChoiceLabelDropdown ;
	JComboBox<String> modeDropdown;
	JComboBox<String> modeChoiceDropdown;
	JComboBox<String> targetChoice;
	JTextField lineChoice;
	private JPanel centerPanel_input;
	private JTextArea fieldInput;
	private JTextArea fieldInput2;
	JLabel inlabel_left;
	JLabel inlabel_right;
	JScrollPane scroll1 ;
	JScrollPane scroll2 ;

	//Output
	private JPanel centerPanel_output;
	private JTextArea fieldOutput;
	private JTextArea fieldOutput2;
	JLabel outlabel_left;
	JLabel outlabel_right;
	JScrollPane scroll3 ;
	JScrollPane scroll4 ;

	//finals
	final String[] MODEOPTIONS= {"NEW", "CLONE"};
	final String[] DROPDOWNOPTIONS= {"Dilemma", "Incident", "Text", "Other"};
	final String[] TARGETOPTIONS= {"Target_Region_1", "Target_Region_2", "Target_Character_1", "Target_Character_2", "Target_Faction_1","Target_Faction_2"};
	final String[] TEXTOPTIONS= {"Dilemma", "Incident"};
	final Color[] VALID = {Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK};
	final Color[] INVALID = {Color.BLACK, Color.BLACK, Color.RED, Color.BLACK};

	//Vars
	private int modeChoice=0;
	private String lastKnownInput_1="";
	private String lastKnownInput_2="";
	private String lastKnownInputText_1="";
	private String lastKnownInputText_2="";
	private ArrayList<JTextField> boxes;

	public GUI ()
	{
		mainFrame = new JFrame("Grid Layout");
		mainPanel= new JPanel();
		topPanel= new JPanel();
		centerPanel= new JPanel();
		centerPanel_input= new JPanel();
		centerPanel_output= new JPanel();
		botPanel= new JPanel();
		boxes= new ArrayList<JTextField>();
		JButton buttonGenerate= new JButton("Generate");
		buttonGenerate.addActionListener(this); // Calls actionPerformed() implemented by ActionListener

		//labels for drops downs
		modeLabelDropdown = new JLabel("Mode:", JLabel.CENTER);
		modeChoiceLabelDropdown = new JLabel(" Type:", JLabel.CENTER);
		modeDropdown = new JComboBox<String>(MODEOPTIONS);
		modeDropdown.setSelectedIndex(0);
		modeDropdown.addActionListener(this);
		modeChoiceDropdown = new JComboBox<String>(DROPDOWNOPTIONS);
		modeChoiceDropdown.setSelectedIndex(0);
		modeChoiceDropdown.addActionListener(this);

		//Custom Coloring on DropDown
		renderer = new ComboBoxRenderer(modeChoiceDropdown);
		renderer.setColors(VALID);
		renderer.setStrings(DROPDOWNOPTIONS);
		modeChoiceDropdown.setRenderer(renderer);

		//TopPanel
		topPanel.add(modeLabelDropdown);
		topPanel.add(modeDropdown);
		topPanel.add(modeChoiceLabelDropdown);
		topPanel.add(modeChoiceDropdown);
		//Center Main 
		centerPanel.setLayout(new GridLayout(2,1, 5, 10) );

		fieldOutput= new JTextArea(2,2);
		fieldOutput2= new JTextArea(2,2);
		scroll3 = new JScrollPane(fieldOutput);
		scroll4 = new JScrollPane(fieldOutput2);
		SetScrollBar(scroll3, fieldOutput);
		SetScrollBar(scroll4, fieldOutput2);
		//Output Creation
		JLabel label_output = new JLabel("Output");
		label_output.setForeground(Color.white);
		label_output.setHorizontalAlignment(SwingConstants.CENTER);
		outlabel_left = new JLabel("New Keys");
		outlabel_left.setForeground(Color.white);
		outlabel_left.setHorizontalAlignment(SwingConstants.CENTER);
		outlabel_right = new JLabel("New Lines");
		outlabel_right.setForeground(Color.white);
		outlabel_right.setHorizontalAlignment(SwingConstants.CENTER);
		//Output
		centerPanel_output.setLayout(new BorderLayout() );
		JLabel grabBagInput2= new JLabel();
		grabBagInput2.setLayout(new GridBagLayout());
		centerPanel_output.add(label_output, BorderLayout.PAGE_START);
		centerPanel_output.add(grabBagInput2, BorderLayout.CENTER);
		SetGrabBagColumn( grabBagInput2,  outlabel_left, 0, 0,  new Insets(1,1,1,1));
		SetGrabBagColumn( grabBagInput2,  outlabel_right, 0, 1, new Insets(1,1,1,1));
		SetGrabBagColumn( grabBagInput2,  scroll3, 1, 0, new Insets(1,10,1,10));
		SetGrabBagColumn( grabBagInput2,  scroll4, 1, 1, new Insets(1,10,1,10));
		//topPanel.setSize(500, 100);

		SetUpCloned();
		SetUpNew();

		centerPanel.add(centerPanel_input);
		centerPanel.add(centerPanel_output);
		centerPanel_input.setBackground(Color.DARK_GRAY);
		centerPanel_output.setBackground(Color.DARK_GRAY);
		botPanel.add(buttonGenerate);

		//topPanel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
		//topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));

		mainPanel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
		//mainPanel.setLayout(new GridLayout(3,1, 5, 10) );
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		mainPanel.add(topPanel, BorderLayout.PAGE_START);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		mainPanel.add(botPanel, BorderLayout.PAGE_END);

		//mainFrame.add(topPanel,  BorderLayout.CENTER);
		mainFrame.add(mainPanel,  BorderLayout.CENTER);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setTitle("TK Creator    by Havie");
		mainFrame.pack();
		mainFrame.setVisible(true);
		mainFrame.setSize(1280, 720);

		//have to do here once window is created 
		SizeTextBoxes();

	}
	private void ReDrawWindow()
	{
		Dimension size= mainFrame.getSize();
		mainFrame.pack();
		mainFrame.setVisible(true);
		mainFrame.setSize(size);
		centerPanel_input.repaint();
		SizeTextBoxes();
	}
	private void SetUpCloned()
	{
		ClearCenter();
		targetChoice = new JComboBox<String>(TARGETOPTIONS);
		targetChoice.setSelectedIndex(0);
		targetChoice.addActionListener(this);
		lineChoice= new JTextField("Optional key addon");
		
		//Output Changes 
		outlabel_left.setText("New Keys");
		outlabel_right.setText("New Lines");
		
		//Input Creation
		JLabel label_input = new JLabel("Input");
		label_input.setForeground(Color.white);
		label_input.setHorizontalAlignment(SwingConstants.CENTER);
		inlabel_left = new JLabel("New Values");
		inlabel_left.setForeground(Color.white);
		inlabel_left.setHorizontalAlignment(SwingConstants.CENTER);
		inlabel_right = new JLabel("Lines To Clone");
		inlabel_right.setForeground(Color.white);
		inlabel_right.setHorizontalAlignment(SwingConstants.CENTER);


		fieldInput= new JTextArea(2,2);
		fieldInput2= new JTextArea(2,2);
		scroll1 = new JScrollPane(fieldInput);
		scroll2 = new JScrollPane(fieldInput2);
		SetScrollBar(scroll1, fieldInput);
		SetScrollBar(scroll2, fieldInput2);
		SetScrollBar(scroll3, fieldOutput);
		SetScrollBar(scroll4, fieldOutput2);

		//Input
		centerPanel_input.setLayout(new BorderLayout() );
		centerPanel_input.add(label_input, BorderLayout.PAGE_START);
		JLabel grabBagInput= new JLabel();
		grabBagInput.setLayout(new GridBagLayout());
		centerPanel_input.add(grabBagInput, BorderLayout.CENTER);
		SetGrabBagColumn( grabBagInput,  inlabel_left, 0, 0,  new Insets(1,1,1,1));
		SetGrabBagColumn( grabBagInput,  inlabel_right, 0, 1, new Insets(1,1,1,1));
		SetGrabBagColumn( grabBagInput,  scroll1, 1, 0, new Insets(1,10,1,10));
		SetGrabBagColumn( grabBagInput,  scroll2, 1, 1, new Insets(1,10,1,10));
		SetGrabBagColumn( grabBagInput,  targetChoice, 2, 0, new Insets(1,10,1,10));
		SetGrabBagColumn( grabBagInput,  lineChoice, 2, 1, new Insets(1,10,1,10));

		ReDrawWindow();
	}
	private void SetUpNew()
	{
		ClearCenter();
		//Input Creation
		JLabel label_input = new JLabel("Input");
		label_input.setForeground(Color.white);
		label_input.setHorizontalAlignment(SwingConstants.CENTER);
		inlabel_left = new JLabel("Conditions");
		inlabel_left.setForeground(Color.white);
		inlabel_left.setHorizontalAlignment(SwingConstants.CENTER);
		inlabel_right = new JLabel("Keys");
		inlabel_right.setForeground(Color.white);
		inlabel_right.setHorizontalAlignment(SwingConstants.CENTER);
		
		//Output Changes
		outlabel_left.setText("New Options");
		outlabel_right.setText("New Payloads");
		
		inLabelEventKey = new JLabel("Event Key");
		inLabelStartingIndex = new JLabel("Starting Index");
		inLabelChar1 = new JLabel("Character 1");
		inLabelChar2  = new JLabel("Character 2");
		inLabelChar3 = new JLabel("Character 3");
		inLabelFaction1 = new JLabel("Faction 1");
		inLabelFaction2 = new JLabel("Faction 2");
		inLabelFaction3 = new JLabel("Faction 3");
		inLabelRegion1 = new JLabel("Region 1");
		inLabelRegion2 = new JLabel("Region 2");
		inLabelRegion3 = new JLabel("Region 3");
		inLabelEventKey.setForeground(Color.GREEN);
		inLabelEventKey.setHorizontalAlignment(SwingConstants.CENTER);
		inLabelStartingIndex.setForeground(Color.GREEN);
		inLabelStartingIndex.setHorizontalAlignment(SwingConstants.CENTER);
		inLabelChar1.setForeground(Color.white);
		inLabelChar1.setHorizontalAlignment(SwingConstants.CENTER);
		inLabelChar2.setForeground(Color.white);
		inLabelChar2.setHorizontalAlignment(SwingConstants.CENTER);
		inLabelChar3.setForeground(Color.white);
		inLabelChar3.setHorizontalAlignment(SwingConstants.CENTER);
		inLabelFaction1.setForeground(Color.GREEN);
		inLabelFaction1.setHorizontalAlignment(SwingConstants.CENTER);
		inLabelFaction2.setForeground(Color.white);
		inLabelFaction2.setHorizontalAlignment(SwingConstants.CENTER);
		inLabelFaction3.setForeground(Color.white);
		inLabelFaction3.setHorizontalAlignment(SwingConstants.CENTER);
		inLabelRegion1.setForeground(Color.white);
		inLabelRegion1.setHorizontalAlignment(SwingConstants.CENTER);
		inLabelRegion2.setForeground(Color.white);
		inLabelRegion2.setHorizontalAlignment(SwingConstants.CENTER);
		inLabelRegion3.setForeground(Color.white);
		inLabelRegion3.setHorizontalAlignment(SwingConstants.CENTER);
		
		inEventKey =new JTextField("key_name");
		//inEventKey.setPreferredSize(new Dimension(1000,1000));
		inStartingIndex=new JTextField("0");
		inChar1= new JTextField("unused",300);
		inChar2= new JTextField("unused",300);
		inChar3= new JTextField("unused",300);
		inFaction1= new JTextField("default",300);
		inFaction2= new JTextField("unused",300);
		inFaction3= new JTextField("unused",300);
		inRegion1= new JTextField("unused",300);
		inRegion2= new JTextField("unused",300);
		inRegion3= new JTextField("unused",300);
		boxes.add(inEventKey);
		boxes.add(inStartingIndex);
		boxes.add(inChar1);
		boxes.add(inChar2);
		boxes.add(inChar3);
		boxes.add(inFaction1);
		boxes.add(inFaction2);
		boxes.add(inFaction3);
		boxes.add(inRegion1);
		boxes.add(inRegion2);
		boxes.add(inRegion3);

		//Input
		centerPanel_input.setLayout(new BorderLayout() );
		centerPanel_input.add(label_input, BorderLayout.PAGE_START);
		JLabel grabBagInput= new JLabel();
		grabBagInput.setLayout(new GridBagLayout());
		centerPanel_input.add(grabBagInput, BorderLayout.CENTER);
		int i=0;
		SetGrabBagColumn( grabBagInput,  inlabel_left, i, 0,  new Insets(1,1,1,1), GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inlabel_right, i, 1, new Insets(1,1,1,1),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inLabelEventKey, ++i, 0, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inEventKey, i, 1, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inLabelStartingIndex, ++i, 0, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inStartingIndex, i, 1, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inLabelChar1, ++i, 0, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inChar1, i, 1, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inLabelChar2, ++i, 0, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inChar2, i, 1, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inLabelChar3, ++i, 0, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inChar3, i, 1, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inLabelFaction1, ++i, 0, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inFaction1, i, 1, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inLabelFaction2, ++i, 0, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inFaction2, i, 1, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inLabelFaction3, ++i, 0, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inFaction3, i, 1, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inLabelRegion1, ++i, 0, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inRegion1, i, 1, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inLabelRegion2, ++i, 0, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inRegion2, i, 1, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inLabelRegion3, ++i, 0, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		SetGrabBagColumn( grabBagInput,  inRegion3, i, 1, new Insets(1,10,1,10),GridBagConstraints.RELATIVE);
		ReDrawWindow();
	}
	private void ClearCenter()
	{
		centerPanel_input.removeAll();
		centerPanel_input.repaint();
	}
	private void SizeTextBoxes()
	{
		//NB: using centerPanel_input width seems to be ignored everywhere, as I can set it to the width and its still much smaller
		scroll1.setPreferredSize(new Dimension(centerPanel_input.getWidth()/2, centerPanel_input.getHeight()*9));
		scroll2.setPreferredSize(new Dimension(centerPanel_input.getWidth()/2, centerPanel_input.getHeight()*9));
		scroll3.setPreferredSize(new Dimension(centerPanel_input.getWidth()/2, centerPanel_input.getHeight()*9));
		scroll4.setPreferredSize(new Dimension(centerPanel_input.getWidth()/2, centerPanel_input.getHeight()*9));
		for(JTextField jf: boxes)
		{
			if(jf!=null)
				{
				jf.setMinimumSize(new Dimension(centerPanel.getWidth(),20));
				jf.setPreferredSize(new Dimension(300,45)); //GridBag Lay out ignores this
				}
		}
	}
	/**
	 * A Little trick to change to color of drop down menu items
	 * @param valid
	 */
	private void setDropdownRender(boolean valid)
	{	
		if (valid)
			renderer.setColors(VALID);
		else 
			renderer.setColors(INVALID);
	}
	@Override
	public void actionPerformed(ActionEvent a) {
		//System.out.println("Action Event="+a.getActionCommand());
		//System.out.println("Action Event Source="+ a.getSource().toString());
		if (a.getActionCommand().equals("Generate"))
			Generate();
		else if (a.getActionCommand().equals("comboBoxChanged"))
			SwitchLogic(a.getSource());
		else
			Driver.print("Did not generate..action::"+a.getActionCommand());
	}
	private void Generate()
	{
		//Driver.print("Trying to Generate:");
		Parser p= new Parser();
		//Driver.print("fieldInput1="+ fieldInput.getText());
		//Driver.print("fieldINput2="+ fieldInput2.getText());
		String OptionalText=lineChoice.getText();
		if (OptionalText.equals("Optional key addon")) // should probably make final
			OptionalText="";
		if( modeDropdown.getSelectedIndex()==1) //CLONED
		{
			if(modeChoice==0 || modeChoice==1)
				p.OutputClonedEventLinesRaw(fieldInput2.getText(), fieldInput.getText(), Parser.eTargetType(targetChoice.getSelectedIndex()),modeChoiceDropdown.getSelectedItem().toString(), OptionalText, fieldOutput, fieldOutput2 );	
			else if (modeChoice==2)
			{
				if(targetChoice.getSelectedIndex()==0)
				{
					int choices=2;
					if(!lineChoice.getText().equals("Enter # of Choices (dilemma only)"))
					{
						try{choices=Integer.parseInt(lineChoice.getText());}
						catch(NumberFormatException e){choices=2;}
					}
					p.OutputDilemmaTXT(fieldInput.getText().split("\n"),choices, fieldOutput, fieldOutput2 );
				}
				else
				{
					p.OutputIncidentTXT(fieldInput.getText().split("\n"),fieldOutput, fieldOutput2 );
				}
	
			}
			else
				Driver.print("not sure");
		}
		else // New ==0 (written reversed order at development time)
		{
			/*
			 * JTextField inChar1;
				JTextField inChar2;
				JTextField inChar3;
				JTextField inFaction1;
				JTextField inFaction2;
				JTextField inFaction3;
				JTextField inRegion1;
				JTextField inRegion2;
				JTextField inRegion3;
			 */
			
			//Build an Array out of the types
			ArrayList<String> targetCharacters= new ArrayList<String>();
			ArrayList<String> targetFactions= new ArrayList<String>();
			ArrayList<String> targetRegions= new ArrayList<String>();
			
			AddToList(inChar1.getText(), targetCharacters,1);
			AddToList(inChar2.getText(), targetCharacters,1);
			AddToList(inChar3.getText(), targetCharacters,1);
			
			AddToList(inFaction1.getText(), targetFactions,2);
			AddToList(inFaction2.getText(), targetFactions,2);
			AddToList(inFaction3.getText(), targetFactions,2);
			
			AddToList(inRegion1.getText(), targetRegions,3);
			AddToList(inRegion2.getText(), targetRegions,3);
			AddToList(inRegion3.getText(), targetRegions,3);
			
			int index=0;
			try{index=Integer.parseInt(inStartingIndex.getText());}
			catch(Exception e){index=0;}
			
			p.OutputNewEventOptionLines( index, inEventKey.getText(), targetCharacters,targetFactions,targetRegions, fieldOutput, fieldOutput2);
			
		}

	}
	private void AddToList(String key, ArrayList<String> list, int type)
	{
		if(CheckFormatting(key, type))
			list.add(key);
	}
	private boolean CheckFormatting(String s, int type)
	{
		switch(type)
		{
		case 1:
			return s.contains("template_historical");
		case 2:
			return s.contains("main_faction") || s.equals("default");
		case 3:
			return s.contains("3k_main_");
		}
		return false;
	}
	private void SetScrollBar(JScrollPane scrollBar, JTextArea textBox)
	{
		scrollBar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollBar.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollBar.setBounds(textBox.getBounds());
	}
	private void SwitchLogic(Object o)
	{
		if (o instanceof JComboBox)
		{
			@SuppressWarnings("unchecked")
			JComboBox<String> box= (JComboBox<String>)o;

			if(box==modeDropdown) //Changed NEW VS CLONE
			{
				switch(box.getSelectedIndex())
				{
				case 0: //NEW 
					SetUpNew();
					setDropdownRender(true);
					SwitchLogic(modeChoiceDropdown);
					break;
				case 1: //CLONE
					SetUpCloned();
					setDropdownRender(false);
					SwitchLogic(modeChoiceDropdown);
					break;
				}
			}
			if(box==targetChoice) //CHANGED Cloned Event Target
			{
				if (modeChoiceDropdown.getSelectedIndex()==2) //TEXT
				{
					switch(box.getSelectedIndex())
					{
					case 0: //dilemma 
						Driver.print("IS THIS HAPPEING? (1)");
						outlabel_left.setText("Titles/Descriptions");
						outlabel_right.setText("Choice Labels");
						break;
					case 1: //incident
						Driver.print("IS THIS HAPPEING? (2)");
						outlabel_left.setText("Titles");
						outlabel_right.setText("Descriptions");
						break;
					}
				}
				else
				{
					outlabel_left.setText("New Keys");
					outlabel_right.setText("New Lines");
				}
			}
			else if(box==modeChoiceDropdown) //Changed TYPE
			{
				// Cant do  DROPDOWNOPTIONS[0] because objects in a final array aren't const
				switch(box.getSelectedItem().toString())
				{
				case "Dilemma":
					ChangeDisplay(0);
					break;
				case "Incident":
					ChangeDisplay(1);
					break;
				case "Text":
					ChangeDisplay(2);
					break;
				case "Other":
					ChangeDisplay(3);
					break;
				default:
					break;
				}
			}
		}
	}

	private void ChangeDisplay(int modeChange)
	{	
		int lastMode=modeChoice;
		modeChoice=modeChange;
		switch(modeChoice)
		{
		case 0:
			//since i cant seem to have 1 hidden Jtext area behind the other, we will just save the info 
			if(lastMode==2 ) //TEXT
			{
				if (modeDropdown.getSelectedIndex() == 0) //NEW
					{
						SetUpNew();
					}
				else if (modeDropdown.getSelectedIndex() == 1) //CLONED
				{
					setDropdownRender(false);
					lastKnownInputText_1=fieldInput.getText();
					lastKnownInputText_2=fieldInput2.getText();
					fieldInput.setText(lastKnownInput_1);
					fieldInput2.setText(lastKnownInput_2);
					fieldInput2.setVisible(true);
					inlabel_left.setText("New Values");
					inlabel_right.setText("Lines To Clone");
					Driver.print("CHANGED TO New Keys");
					outlabel_left.setText("New Keys");
					outlabel_right.setText("New Lines");
					targetChoice.removeAllItems();
					lineChoice.setText("Optional key addon");
					fieldOutput.setText("");
					fieldOutput2.setText("");
					Driver.print("(1)DID THIS TRIGGER ACTION?");
					for(String s : TARGETOPTIONS)
						targetChoice.addItem(s);
					

				}

			}
			break;
		case 1:
			if(lastMode==2)
			{
				if (modeDropdown.getSelectedIndex() == 0) //NEW
					SetUpNew();
				else if (modeDropdown.getSelectedIndex() == 1) //CLONED
				{
					setDropdownRender(false);
					lastKnownInputText_1=fieldInput.getText();
					lastKnownInputText_2=fieldInput2.getText();
					fieldInput.setText(lastKnownInput_1);
					fieldInput2.setText(lastKnownInput_2);
					fieldInput2.setVisible(true);
					inlabel_left.setText("New Values");
					inlabel_right.setText("Lines To Clone");
					outlabel_left.setText("New Keys");
					outlabel_right.setText("New Lines");
					targetChoice.removeAllItems();
					fieldOutput.setText("");
					fieldOutput2.setText("");
					lineChoice.setText("Optional key addon");
					Driver.print("(2)DID THIS TRIGGER ACTION?");
					for(String s : TARGETOPTIONS)
						targetChoice.addItem(s);

				}
			}
			break;
		case 2:

			if (modeDropdown.getSelectedIndex() == 0) //NEW
			{
				SetUpCloned();
				setDropdownRender(true);
			}
			else
				setDropdownRender(false);


			lastKnownInput_1=fieldInput.getText();
			lastKnownInput_2=fieldInput2.getText();
			fieldInput.setText(lastKnownInputText_1);
			fieldInput2.setText(lastKnownInputText_2);
			fieldInput2.setVisible(false);
			inlabel_left.setText("Event Names");
			inlabel_right.setText("Unused");
			Driver.print(" CHANGED TO Titles/Descriptions");
			outlabel_left.setText("Titles/Descriptions");
			outlabel_right.setText("Choice Labels");
			targetChoice.removeAllItems();
			fieldOutput.setText("");
			fieldOutput2.setText("");
			lineChoice.setText("Enter # of Choices (dilemma only)");
			for(String s : TEXTOPTIONS)
				targetChoice.addItem(s);


			break;
		case 3:
			/*fieldInput.setVisible(false);
			fieldInput2.setVisible(false);
			scroll1.setVisible(false);
			scroll2.setVisible(false);
			scroll1_text.setVisible(true);
			scroll2_text.setVisible(true);
			fieldInput_text.setVisible(true);
			fieldInput2_text.setVisible(true);*/
			break;
		default:
			break;

		}

	}
	private void SetGrabBagColumn(JLabel pane, Component component, int row, int col, Insets inset)
	{
		SetGrabBagColumn( pane,  component, row,  col,  inset, 1);
	}
	private void SetGrabBagColumn(JLabel pane, Component component, int row, int col, Insets inset, float weight)
	{
		GridBagConstraints c = new GridBagConstraints();   
		//c.anchor = GridBagConstraints.WEST;
		c.insets = inset;
		c.fill= GridBagConstraints.HORIZONTAL;
		c.gridx=col;
		c.gridy=row;
		c.weightx = weight;
		//c.anchor= GridBagConstraints.CENTER;
		//c.weighty = 1.0;
		//////grabBagInput.add(fieldInput, c);
		pane.add(component, c);
	}


}
