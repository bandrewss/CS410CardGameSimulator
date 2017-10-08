import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.*;

public class GUI extends JFrame {
	
	private JTextArea display;
	private JButton[] cardButtons;
	private JScrollPane jScrollPane1;
	private JFrame frame;
	private JPanel gridPanel;
	private GridLayout grid;
	
	private Client client;
	
	
	
	public GUI(String name, Client c) {
		client = c;
		
		frame = new JFrame();
		
		gridPanel = new JPanel();
		grid = new GridLayout(6, 3);
		gridPanel.setLayout(grid);
		
		cardButtons = new JButton[17];
		
		for (int i = 0; i < cardButtons.length; ++i) {
			cardButtons[i] = new JButton();
			String cardName =String.format("%d", i);
			cardButtons[i].setText(cardName);
			gridPanel.add(cardButtons[i]);
			cardButtons[i].setVisible(false);
			
			cardButtons[i].addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	            	jButtonActionPerformed(evt);
	            }
	        });
		}
		
		jScrollPane1 = new JScrollPane();
		display = new JTextArea();
		display.setEditable(false);
		display.setColumns(25);
		display.setRows(10);
		jScrollPane1.setViewportView(display);

		frame.add(jScrollPane1, BorderLayout.NORTH);
		frame.add(gridPanel, BorderLayout.CENTER);
		frame.setSize(400, 500);
		frame.setTitle(name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
	}
	
	/*
	 * Sets the window title.
	 */
	public void setTitle(String s) {
		frame.setTitle(s);
	}
	
	/*
	 * Sets label of button of index n to string s.
	 * Parameters: int index, string label
	 */
	private void setButton(int n, String s) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				cardButtons[n].setText(s);
				cardButtons[n].setVisible(true);
			}
		});
	}
	
	public void setEndGameButtons() {
		cardButtons[0].setText("Rematch");
		cardButtons[0].setVisible(true);
		
		cardButtons[1].setText("Exit");
		cardButtons[1].setVisible(true);
	}
	
	/*
	 * Sets all the button labels to the cards in the given hand.
	 * Parameters: a hand full of cards.
	 */
	public void setButtons(Hand hand) {
		for(int i = 0; i < hand.getHandSize(); ++i) {
			setButton(i, hand.getCardByIndex(i).toString());
		}
	}
	
	/*
	 * Removes the button whos label matches the given String.
	 * Parameter: a string representation of a card.
	 */
	public void removeCardButton(String card) {
		for(int i = 0; i < cardButtons.length; ++i) {
			if(cardButtons[i].getText().equals(card)) { 
				cardButtons[i].setVisible(false);
				break;
			}
		}
			
	}
	
	/*
	 * Sets all the JButtons to invisible.
	 */
	public void removeAllButtons() { 
		for(JButton button:cardButtons) {
			button.setVisible(false);
		}
	}
	
	public boolean jButtonActionPerformed(java.awt.event.ActionEvent evt) {
		String message = evt.getActionCommand();
		
		if(message.equals("Rematch")) {
			appendToDisplay("Requested a rematch");
			client.requestRematch();
			removeAllButtons();
		}
		else if(message.equals("Exit")) {
			client.requestExit();
		}
		else {
			client.playCard(message);
		}
		return true;   
    }  
		

	/*
	 * Adds given string to console window.
	 * Parameter: the string to add.
	 */
	public void appendToDisplay(String s) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				display.append(String.format("%s\n", s));
			}
		});
	}
}
