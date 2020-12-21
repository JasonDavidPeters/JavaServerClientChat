package com.jasondavidpeters.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

public class ClientWindow extends JFrame implements KeyListener {

    private static final long serialVersionUID = 1L;
    private JTextField userInput;
    private JTextArea chatBox;
    private JScrollPane scrollPane;

    private String username;
    private Thread listenThread;
    private Thread disconnectionCheck;
    private Socket socket;
    private InetAddress ip;
    private int port;
    private boolean connected;
    private boolean running;

    public ClientWindow(String username, InetAddress ip, int port) {
	this.username = username;
	this.ip = ip;
	this.port = port;
	System.out.println("attempting to connect to: " + ip + " " + port);
	connected = openConnection(socket, ip, port);
	if (!connected)
	    System.exit(0);

	running = true;
	/*
	 * TODO: check connection before continuing
	 */
	setPreferredSize(new Dimension(800, 650));
	pack();
	setLocationRelativeTo(null);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setResizable(false);

	JPanel panel = new JPanel();
	getContentPane().add(panel, BorderLayout.NORTH);
	GridBagLayout gbl_panel = new GridBagLayout();
	gbl_panel.columnWidths = new int[] { 0, 0 };
	gbl_panel.rowHeights = new int[] { 576, 49, 35, 0 };
	gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
	gbl_panel.rowWeights = new double[] { 1.0, 0.0, 0.0, Double.MIN_VALUE };
	panel.setLayout(gbl_panel);

	chatBox = new JTextArea();
	scrollPane = new JScrollPane(chatBox, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
		ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	chatBox.setWrapStyleWord(true);
	GridBagConstraints gbc_textPane = new GridBagConstraints();
	gbc_textPane.insets = new Insets(0, 0, 5, 0);
	gbc_textPane.fill = GridBagConstraints.BOTH;
	gbc_textPane.gridx = 0;
	gbc_textPane.gridy = 0;
	scrollPane.setViewportView(chatBox);
	panel.add(scrollPane, gbc_textPane);
	chatBox.setEditable(false);

	userInput = new JTextField();
	GridBagConstraints gbc_textField = new GridBagConstraints();
	gbc_textField.insets = new Insets(0, 0, 5, 0);
	gbc_textField.fill = GridBagConstraints.HORIZONTAL;
	gbc_textField.gridx = 0;
	gbc_textField.gridy = 1;
	panel.add(userInput, gbc_textField);
	setVisible(true);

	userInput.addKeyListener(this);

	disconnect();
	listen();
    }

    private boolean openConnection(Socket socket, InetAddress ip, int port) {
	try {
	    socket = new Socket(ip, port);
	    this.socket = socket;
	    String message = "h>>" + username + "<<h";
	    write(message.getBytes(),false);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	if (socket.isConnected())
	    return true;
	return false;
    }

    private void disconnect() {
	disconnectionCheck = new Thread() {
	    @Override
	    public void run() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
			String message = "d>>disconnection<<d";
			write(message.getBytes(), false);
			try {
			    socket.getInputStream().skip(1024);
			    socket.getInputStream().close();
			    running = false;
			} catch (IOException e) {
			    e.printStackTrace();
			}

		    }
		});

	    }
	};
	disconnectionCheck.start();
    }

    private void write(byte[] data, boolean flush) {
	/*
	 * TODO: send data through socket
	 */
	try {
	    OutputStream out = socket.getOutputStream();
	    if (flush)
		out.flush();
	    out.write(data);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private void listen() {
	listenThread = new Thread("Listen") {
	    public void run() {
		while (running) {
		    byte[] data = new byte[400];
		    try {
			if (socket.isClosed()) {
			    return;
			}
			InputStream is = socket.getInputStream();
			if (is.read(data) > 0) {
			    String message = "";
			    for (int i = 0; i < data.length; i++) {
				message += (char) data[i];
			    }
			    chatBox.append(message);
			    chatBox.setCaretPosition(chatBox.getDocument().getLength());
			}

		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}
		/*
		 * TODO: if the socket has data, read it here and append it to the chatbox
		 */
	    }
	};
	listenThread.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
	/*
	 * If the user presses enter then we append the data into the chat window
	 */
	if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	    if (userInput.getText().equalsIgnoreCase("/users")) {
		String message = userInput.getText();
		message = "c>>" + message + "<<c";
		write(message.getBytes(),false);
		userInput.setText("");
		return;
	    }
	    String message = username + ": " + userInput.getText();
	    message = "m>>" + message + "<<m";
	    write(message.getBytes(), false);
	    userInput.setText("");
	}
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

}
