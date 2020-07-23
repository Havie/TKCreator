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
	JComboBox<String> dropdown;
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
	final String[] DROPDOWNOPTIONS= {"Dilemma", "Incident", "Text", "Other"};
	final String[] TARGETOPTIONS= {"Target_Region_1", "Target_Region_2", "Target_Character_1", "Target_Character_2", "Target_Faction_1","Target_Faction_2"};
	final String[] TEXTOPTIONS= {"Dilemma", "Incident"};
	
	//Vars
	private int mode=0;
	private String lastKnownInput_1="";
	private String lastKnownInput_2="";
	private String lastKnownInputText_1="";
	private String lastKnownInputText_2="";
	public GUI ()
	{
		mainFrame = new JFrame("Grid Layout");
		mainPanel= new JPanel();
		topPanel= new JPanel();
		centerPanel= new JPanel();
		centerPanel_input= new JPanel();
		centerPanel_output= new JPanel();
		botPanel= new JPanel();
		
		JButton buttonGenerate= new JButton("Generate");
		buttonGenerate.addActionListener(this); // Calls actionPerformed() implemented by ActionListener
		
		
		dropdown = new JComboBox<String>(DROPDOWNOPTIONS);
		dropdown.setSelectedIndex(0);
		dropdown.addActionListener(this);
		targetChoice = new JComboBox<String>(TARGETOPTIONS);
		targetChoice.setSelectedIndex(0);
		targetChoice.addActionListener(this);
		lineChoice= new JTextField("Optional key addon");
		
		JLabel labelDropdown = new JLabel("Clone Type:", JLabel.CENTER);
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

		fieldInput= new JTextArea(2,2);
		fieldOutput= new JTextArea(2,2);
		fieldInput2= new JTextArea(2,2);
		fieldOutput2= new JTextArea(2,2);
		scroll1 = new JScrollPane(fieldInput);
		scroll2 = new JScrollPane(fieldInput2);
		scroll3 = new JScrollPane(fieldOutput);
		scroll4 = new JScrollPane(fieldOutput2);
		SetScrollBar(scroll1, fieldInput);
		SetScrollBar(scroll2, fieldInput2);
		SetScrollBar(scroll3, fieldOutput);
		SetScrollBar(scroll4, fieldOutput2);

		//TopPanel
		topPanel.add(labelDropdown);
		topPanel.add(dropdown);
		//Center Main 
		centerPanel.setLayout(new GridLayout(2,1, 5, 10) );
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
		mainFrame.setSize(800, 600);
		
		System.out.println("the width of CenterPanel_input="+centerPanel_input.getWidth());
		System.out.println("the Height of CenterPanel_input="+centerPanel_input.getHeight());
		//fieldInput.setHorizontalAlignment(JTextField.LEFT);
		scroll1.setPreferredSize(new Dimension(centerPanel_input.getWidth()/2, centerPanel_input.getHeight()*5));
		scroll2.setPreferredSize(new Dimension(centerPanel_input.getWidth()/2, centerPanel_input.getHeight()*5));
		scroll3.setPreferredSize(new Dimension(centerPanel_input.getWidth()/2, centerPanel_input.getHeight()*5));
		scroll4.setPreferredSize(new Dimension(centerPanel_input.getWidth()/2, centerPanel_input.getHeight()*5));
		
		//fieldInput2.setHorizontalAlignment(JTextField.RIGHT);
		//fieldInput2.setPreferredSize(new Dimension(centerPanel_input.getWidth()/2, centerPanel_input.getHeight()*5));
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		//System.out.println("Action Event="+a.getActionCommand());
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
		if(mode==0 || mode==1)
			p.OutputClonedEventLinesRaw(fieldInput2.getText(), fieldInput.getText(), Parser.eTargetType(targetChoice.getSelectedIndex()),dropdown.getSelectedItem().toString(), OptionalText, fieldOutput, fieldOutput2 );	
		else if (mode==2)
		{
			if(targetChoice.getSelectedIndex()==0)
			{
				int choices=2;
				if(!lineChoice.getText().equals("Enter # of Choices (dilemma only)"))
					choices=Integer.parseInt(lineChoice.getText());
				p.OutputDilemmaTXT(fieldInput.getText().split("\n"),choices, fieldOutput, fieldOutput2 );
			}
			else
			{
				p.OutputIncidentTXT(fieldInput.getText().split("\n"));
			}
			
		}
		else
			Driver.print("not sure");
	
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
			if(box==targetChoice)
				return;
			
			// {"Dilemma", "Incident", "Text", "Other"};
			// Cant do  DROPDOWNOPTIONS[0] because objects in a final array arent const
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
	private void ChangeDisplay(int modeChange)
	{	int lastMode=mode;
		mode=modeChange;
		switch(mode)
		{
		case 0:
			//since i cant seem to have 1 hidden Jtext area behind the other, we will just save the info 
			if(lastMode==2)
			{
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
				lineChoice.setText("Optional key addon");
				fieldOutput.setText("");
				for(String s : TARGETOPTIONS)
					targetChoice.addItem(s);
			}
			break;
		case 1:
			if(lastMode==2)
			{
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
				lineChoice.setText("Optional key addon");
				for(String s : TARGETOPTIONS)
					targetChoice.addItem(s);
			}
			break;
		case 2:
			if(lastMode==0 || lastMode==1)
			{
				lastKnownInput_1=fieldInput.getText();
				lastKnownInput_2=fieldInput2.getText();
				fieldInput.setText(lastKnownInputText_1);
				fieldInput2.setText(lastKnownInputText_2);
				fieldInput2.setVisible(false);
				inlabel_left.setText("Event Names");
				inlabel_right.setText("Unused");
				outlabel_left.setText("Titles/Descriptions");
				outlabel_right.setText("Choice Labels");
				targetChoice.removeAllItems();
				fieldOutput.setText("");
				lineChoice.setText("Enter # of Choices (dilemma only)");
				for(String s : TEXTOPTIONS)
					targetChoice.addItem(s);
			}
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
	private void SetGrabBagColumn(JLabel grabBagInput, Component scrollBar, int row, int col, Insets inset)
	{
		GridBagConstraints c = new GridBagConstraints();   
	    //c.anchor = GridBagConstraints.WEST;
	    c.insets = inset;
	    c.fill=GridBagConstraints.HORIZONTAL;
	    c.gridx=col;
	    c.gridy=row;
	    c.weightx = 1.0;
	    //c.weighty = 1.0;
		//////grabBagInput.add(fieldInput, c);
		grabBagInput.add(scrollBar, c);
	}
	
	
}
