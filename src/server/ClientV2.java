package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
/**
 * Holds information about the client and facilitates client-server communication<br>
 * <br>
 * Variables:<br>
 * socket - Socket, the socket connection between server and this client<br>
 * ID - integer, the identity number of the client<br>
 * dos - DataOutputStream, the channel which sends data to the client<br>
 * dis - DataInputStream, the channel which receives data from the client<br>
 * accepted - boolean, determines whether or not the client has been authorised by the server to run the game<br>
 * clientVariant - integer, if there are more clients, this number will increase, and it will be used to choose a different skin for the client's player
 */
public class ClientV2 implements Runnable {
	private Socket socket;
	private int ID;
	private DataOutputStream dos;
	private DataInputStream dis;
	private boolean accepted = false;
	private static int clientVariant = 0;
	
	public ClientV2(Socket socket, int ID) throws Exception{
		this.socket = socket;
		this.ID = ID;
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
	}
	
	public Socket getSocket(){
		return socket;
	}
	public int getID(){
		return ID;
	}
	public DataOutputStream getDOS(){
		return dos;
	}
	public DataInputStream getDIS(){
		return dis;
	}
	public boolean isAccepted(){
		return accepted;
	}
	/**
	 * Closes the socket and it's channels.
	 * @throws Exception
	 */
	public void close() throws Exception{
		socket.close();
		dos.close();
		dis.close();
	}
	/**
	 * runs the ClientV2 thread. This allows each client to run independently of one another.
	 */
	@Override
	public void run(){
		try {
			dos.writeUTF("00Connected! Welcome to the server!");
			dos.flush();
		} catch (IOException e) { e.printStackTrace();}
		accepted = true;
	}
	
	public static int getVariant(){
		return clientVariant;
	}
	public static void incrementVariant(){
		clientVariant++;
	}
}
