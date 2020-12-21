import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {

    private Thread serverThread;
    private Thread clientThread;
    private Thread readThread;
    private Thread sendThread;

    private ServerSocket serverSocket;
    private InetAddress ip;
    private int port;
    private boolean running;

    List<Client> clients = new ArrayList<Client>();

    public Server() {
	try {
	    ip = InetAddress.getByName("127.0.0.1");
	    port = 2020;
	} catch (UnknownHostException e) {
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) {
	Server server = new Server();
	server.start();
    }

    public synchronized void start() {
	running = true;
	serverThread = new Thread(this);
	serverThread.start();

    }

    public synchronized void stop() {

    }

    public void run() {
	System.out.println("starting server on: " + ip + " " + port);
	try {
	    serverSocket = new ServerSocket();
	    serverSocket.bind(new InetSocketAddress(ip, port));

	} catch (IOException e) {
	    e.printStackTrace();
	}
	loadIncomingClients();

    }

    private boolean clientConnected() {
	/*
	 * TODO: check if the client is still connected
	 */
	return false;
    }

    private void listenForData() {
	byte[] data = new byte[1024];
	for (Client client : clients) {
	    readThread = new Thread() {
		public void run() {
		    while (running) {
			try {
			    InputStream is = client.socket.getInputStream();

			    if (is.read(data) > 0) { // cannot read if the client disconnects
				String message = "";
				for (int i = 0; i < data.length; i++) {
				    message += (char) data[i];
				}
				if (message.split(">>|<<")[1].equalsIgnoreCase("disconnection")) { // receiving disconnection  coded message
				    System.out.println("client disconnect");
				    String m = client.username + " has disconnected.\n";
				    client.socket.close();
				    clients.remove(client);
				    sendToAll(m.getBytes());
				    return;
				} else if (message.startsWith("m>>")) { // receiving user message
				    message = message.split("m>>|<<m")[1] + "\n";
				    sendToAll(message.getBytes());
				} else if (message.startsWith("c>>")) { // receiving user command
				    String command = message.split("c>>|<<c")[1];
				    switch (command) {
				    case "/users":
					System.out.println("command /users");
					String messageBack = "";
					for (int i = 0; i < clients.size(); i++)
					    messageBack+=clients.get(i).username + ((i == clients.size()-1) ? ".\n" : ", ");
					send(client.socket,messageBack.getBytes());
				    	break;
				    }
				}
			    } else {
				return;
			    }
//			}
			} catch (IOException e) {
			    e.printStackTrace();
			}
		    }
		}
	    };
	}
	readThread.start();
    }

    private void send(Socket s, byte[] data) {
	sendThread = new Thread("Send") {
	    public void run() {
		try {
		    OutputStream out = s.getOutputStream();
		    out.write(data);
		} catch (IOException e) {
		    e.printStackTrace();
		}

	    }
	};
	sendThread.start();
    }

    private void sendToAll(byte[] data) {
	for (Client c : clients) {
	    send(c.socket, data);
	}
    }

    private void loadIncomingClients() {
	clientThread = new Thread() {
	    public void run() {
		while (running) {
		    try {
			Socket client = serverSocket.accept();
			if (client.isConnected()) {
			    byte[] handShake = new byte[300];
			    client.getInputStream().read(handShake);
			    String message = "";
			    for (int i = 0; i < handShake.length; i++) {
				message += (char) handShake[i];
			    }
			    String username = message.split("h>>|<<h")[1];
			    clients.add(new Client(username, client.getInetAddress(), client.getPort(), client));
			    listenForData();
			    System.out.println("Client Connected " + username + " connected.");
			}
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}
	    }
	};
	clientThread.start();
    }

}

class Client {
    public InetAddress ip;
    public int port;
    public Socket socket;
    public String username;

    public Client(String username, InetAddress ip, int port, Socket socket) {
	this.username = username;
	this.socket = socket;
	this.ip = ip;
	this.port = port;
    }
}
