import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	private JPanel centerPanel_input;
	private JPanel centerPanel_output;
	private JPanel botPanel;
	private JTextArea fieldInput;
	private JTextArea fieldOuput;
	private JTextArea fieldInput2;
	private JTextArea fieldOuput2;
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
		
		String[] dropDownOptions= {"Dilemma", "Incident"};
		JComboBox dropdown = new JComboBox(dropDownOptions);
		dropdown.setSelectedIndex(0);
		dropdown.addActionListener(this);
		
		JLabel labelDropdown = new JLabel("Clone Type:", JLabel.CENTER);
		JLabel label_input = new JLabel("Input");
		label_input.setForeground(Color.white);
		label_input.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel label_output = new JLabel("Output");
		label_output.setForeground(Color.white);
		label_output.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel grabBagInput= new JLabel();
		grabBagInput.setLayout(new GridBagLayout());
		fieldInput= new JTextArea(20,20);
		fieldOuput= new JTextArea("EventKeys");
		fieldInput2= new JTextArea("ToCopy");
		fieldOuput2= new JTextArea("OutputedLines");
		JScrollPane scroll1 = new JScrollPane(fieldInput);
		JScrollPane scroll2 = new JScrollPane(fieldOuput);
		JScrollPane scroll3 = new JScrollPane(fieldInput2);
		JScrollPane scroll4 = new JScrollPane(fieldOuput2);
		scroll1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll1.setBounds(fieldInput.getBounds());
		scroll1.getViewport().setBackground(Color.white);
		//scroll1.getViewport().add(fieldInput);
		

		topPanel.add(labelDropdown);
		topPanel.add(dropdown);
		
		centerPanel.setLayout(new GridLayout(2,1, 5, 10) );
		centerPanel_input.setLayout(new BorderLayout() );
		centerPanel_output.setLayout(new BorderLayout() );
		centerPanel_input.add(label_input, BorderLayout.PAGE_START);
		centerPanel_input.add(grabBagInput, BorderLayout.CENTER);
		GridBagConstraints c = new GridBagConstraints();   
	    c.anchor = GridBagConstraints.WEST;
	    c.insets = new Insets(1,20,1,30);
	    c.fill=GridBagConstraints.HORIZONTAL;
	    c.weightx = 1.0;
	    c.weighty = 1.0;
		//////grabBagInput.add(fieldInput, c);
		grabBagInput.add(scroll1, c);
		c.insets = new Insets(1,30,1,20);
		c.anchor = GridBagConstraints.EAST;
		grabBagInput.add(fieldInput2, c);
		grabBagInput.add(scroll2, c);
		centerPanel_output.add(label_output, BorderLayout.PAGE_START);
		centerPanel_output.add(fieldOuput, BorderLayout.CENTER);
		centerPanel_output.add(fieldOuput2, BorderLayout.CENTER);
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
		//fieldInput2.setHorizontalAlignment(JTextField.RIGHT);
		//fieldInput2.setPreferredSize(new Dimension(centerPanel_input.getWidth()/2, centerPanel_input.getHeight()*5));
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		// TODO Auto-generated method stub
		
	}
	
	
}
