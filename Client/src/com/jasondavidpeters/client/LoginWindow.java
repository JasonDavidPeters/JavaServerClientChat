package com.jasondavidpeters.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class LoginWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField usernameField;
    private JTextField ipaddressField;
    private JTextField portField;

    public LoginWindow() {
//	this..
	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception e) {
	    e.printStackTrace();
	}
	setPreferredSize(new Dimension(250, 450));

	JPanel panel = new JPanel();
	getContentPane().add(panel, BorderLayout.CENTER);
	GridBagLayout gbl_panel = new GridBagLayout();

	gbl_panel.columnWidths = new int[] { 21, 199, 0, 0 };
	gbl_panel.rowHeights = new int[] { 55, 40, 13, 57, 13, 48, 0, 0 };
	gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
	gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
	panel.setLayout(gbl_panel);

	JLabel usernameLabel = new JLabel("Enter username");
	GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
	gbc_lblNewLabel.anchor = GridBagConstraints.SOUTH;
	gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
	gbc_lblNewLabel.gridx = 1;
	gbc_lblNewLabel.gridy = 0;
	panel.add(usernameLabel, gbc_lblNewLabel);

	usernameField = new JTextField("jason");
	GridBagConstraints gbc_textField = new GridBagConstraints();
	gbc_textField.fill = GridBagConstraints.HORIZONTAL;
	gbc_textField.insets = new Insets(0, 0, 5, 5);
	gbc_textField.gridx = 1;
	gbc_textField.gridy = 1;
	panel.add(usernameField, gbc_textField);
//    	usernameField.setColumns(1);

	JLabel ipLabel = new JLabel("Enter ip address");
	GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
	gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
	gbc_lblNewLabel_1.gridx = 1;
	gbc_lblNewLabel_1.gridy = 2;
	panel.add(ipLabel, gbc_lblNewLabel_1);

	ipaddressField = new JTextField("localhost");
	GridBagConstraints gbc_textField_1 = new GridBagConstraints();
	gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
	gbc_textField_1.insets = new Insets(0, 0, 5, 5);
	gbc_textField_1.gridx = 1;
	gbc_textField_1.gridy = 3;
	panel.add(ipaddressField, gbc_textField_1);
//    	textField_1.setColumns(1);

	JLabel portLabel = new JLabel("Enter port number");
	GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
	gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
	gbc_lblNewLabel_2.gridx = 1;
	gbc_lblNewLabel_2.gridy = 4;
	panel.add(portLabel, gbc_lblNewLabel_2);

	portField = new JTextField("2020");
	GridBagConstraints gbc_textField_2 = new GridBagConstraints();
	gbc_textField_2.insets = new Insets(0, 0, 5, 5);
	gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
	gbc_textField_2.gridx = 1;
	gbc_textField_2.gridy = 5;
	panel.add(portField, gbc_textField_2);
//    	textField_2.setColumns(10);
	JButton connectButton = new JButton("Connect");
	GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
	gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
	gbc_btnNewButton.gridx = 1;
	gbc_btnNewButton.gridy = 6;
	panel.add(connectButton, gbc_btnNewButton);

	/*
	 * TODO: Press connect, enter a connection to the server
	 * 
	 */
	connectButton.addActionListener(e -> {
	    setVisible(false);
	    try {
		new ClientWindow(usernameField.getText(), InetAddress.getByName(ipaddressField.getText()),
			Integer.parseInt(portField.getText()));
	    } catch (NumberFormatException e1) {
		e1.printStackTrace();
	    } catch (UnknownHostException e1) {
		e1.printStackTrace();
	    }
	});

	getContentPane().add(panel);

	pack();

	setLocationRelativeTo(null);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	setVisible(true);

    }

}
