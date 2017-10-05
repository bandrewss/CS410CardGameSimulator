import javax.swing.*;

public class GUI extends JFrame {
	
	private JTextArea display;
	private JButton[] cardButtons = new JButton[17];
	private JScrollPane jScrollPane1;
	
	Client client;
	
	
	
	public GUI(String name, Client c) {
		client = c;
		
		
		jScrollPane1 = new javax.swing.JScrollPane();
		display = new javax.swing.JTextArea();
		display.setEditable(false);

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jScrollPane1 = new JScrollPane();
		display = new JTextArea();

		for (int i = 0; i < cardButtons.length; ++i) {
			cardButtons[i] = new JButton();
			String cardName =String.format("%d", i);
			cardButtons[i].setText(cardName);
		}
		for (int i = 0; i < cardButtons.length; ++i) {
			 
			cardButtons[i].addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	            	jButtonActionPerformed(evt);
	            }
	        });
		}

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		display.setColumns(20);
		display.setRows(5);
		jScrollPane1.setViewportView(display);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(layout.createSequentialGroup().addGap(57, 57, 57).addComponent(
												jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 277,
												javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGroup(layout
												.createSequentialGroup().addContainerGap().addComponent(cardButtons[0])
												.addGap(18, 18, 18)
												.addComponent(cardButtons[1])
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
												.addComponent(cardButtons[2]))
										.addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
												.addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout
														.createSequentialGroup().addComponent(cardButtons[6])
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(cardButtons[7])
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(cardButtons[8]))
												.addGroup(javax.swing.GroupLayout.Alignment.LEADING,
														layout.createSequentialGroup()
																.addComponent(cardButtons[3])
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(cardButtons[4]).addGap(18, 18, 18)
																.addComponent(cardButtons[5]))))
										.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
												layout.createSequentialGroup().addContainerGap().addGroup(layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(layout.createSequentialGroup()
																.addComponent(cardButtons[12])
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(cardButtons[13])
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(cardButtons[14]))
														.addGroup(layout.createSequentialGroup()
																.addComponent(cardButtons[9])
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(cardButtons[10])
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																.addComponent(cardButtons[11])))))
								.addGroup(
										layout.createSequentialGroup().addGap(62, 62, 62).addComponent(cardButtons[15])
												.addGap(30, 30, 30).addComponent(cardButtons[16])))
						.addContainerGap(66, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addContainerGap()
				.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
				.addGap(18, 18, 18)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(cardButtons[0]).addComponent(cardButtons[1]).addComponent(cardButtons[2]))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(cardButtons[3]).addComponent(cardButtons[4]).addComponent(cardButtons[5]))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(cardButtons[6]).addComponent(cardButtons[7]).addComponent(cardButtons[8]))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(cardButtons[9]).addComponent(cardButtons[10]).addComponent(cardButtons[11]))
				.addGap(18, 18, 18)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(cardButtons[12]).addComponent(cardButtons[13]).addComponent(cardButtons[14]))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(cardButtons[15]).addComponent(cardButtons[16]))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		pack();
		setSize(400, 500);
		setTitle(name);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setAlwaysOnTop(true);
	}
	
	public void setButtonN(int n, String s) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				cardButtons[n].setText(s);
			}
		});
	}
	/*
	public void createListener(int i) {
		cardButtons[i].addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	jButtonActionPerformed(evt, i);
            }
        });
	}
	*/
	public boolean jButtonActionPerformed(java.awt.event.ActionEvent evt) {
		String message = evt.getActionCommand();
		appendToDisplay(message);
		
		client.playCard(message);
		
		return true;   
    	//hand.playCard(i);
    	//cardButtons[i].setVisible(false);
    }  
	
	/*public void createEvent(int n) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JButton singleButton=cardButtons[n];
				singleButton.addActionListener(new java.awt.event.ActionListener() {
		            public void actionPerformed(java.awt.event.ActionEvent evt) {
		            	singleButton(evt);
		            }
		        });
			}
		});
	}

	*/
	/*
	 * Adds given string to dummy gui
	 */
	public void appendToDisplay(String s) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				display.append(String.format("%s\n", s));
			}
		});
	}
}
