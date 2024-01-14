package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.IllegalFormatConversionException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;

// 00 - server info // 01 - game info // 02 - test connection
/**
 * Creates a server and manages it.<br>
 * <br>
 * Variables:<br>
 * clients - HashMap of integer and ClientV2, maps clients to their ID's<br>
 * PORT - integer, the port number<br>
 * ID - integer, the ID counter of the clients<br>
 * server - ServerSocket, the socket of the server<br>
 * frame - JFrame, the window<br>
 * reports - JTextArea, the place where the program reports to the user what happens<br>
 * portLabel - JLabel, displays text. When the server is running, it also displays the port number<br>
 * commandField - JTextField, the field where the user can write commands<br>
 * portField - JFormattedTextField, the field where the user can enter the port number<br>
 * startButton, stopButton - JButton, buttons to start and stop the server<br>
 * table - JTable, the table which contains the list of clients. It's 3rd and 4th columns can be clicked to activate their respective actions<br>
 * scrollPane, scrollTable - JScrollPane, adds scroll bar to the table and textarea respectively<br>
 * model - DefaultTableModel, sets the columns for the table, and contains the data in form of rows<br>
 * seeker - Thread, continuously seeks socket connections<br>
 * gameStatus - Thread, continuously checks if any client wants to know if the server allowed the game to start, and responds back with true if so, or false otherwise<br>
 * connectionStatus - Thread, checks the connection of the clients, and drops them from the table if they are no longer responding<br>
 * gameUp - boolean, tells if the game is allowed to start, true if so, false otherwise<br>
 * running - boolean, tells if the server is running, true if so, false otherwise
 */
public class ServerV2 {
	private static Map<Integer, ClientV2> clients = new HashMap<Integer, ClientV2>();
	private static int PORT;
	private static int ID = 0;
	private static ServerSocket server;
	
	private static JFrame frame;
	private static JTextArea reports;
	private static JLabel portLabel;
	private static JTextField commandField;
	private static JFormattedTextField portField;
	private static JButton startButton;
	private static JButton stopButton;
	private static JScrollPane scrollPane;
	private static JTable table;
	private static JScrollPane scrollTable;
	private static DefaultTableModel model;
	
	private static Thread seeker, gameStatus;
	private static Thread connectionStatus;
	private static boolean gameUp = false;
	private static boolean running = false;
	
	/**
	 * Sets the default port number and starts the application.
	 * @param args can be the port number or nothing
	 */
	public static void main(String[] args){
		if(args.length==1){
			try{
				PORT = Integer.parseInt(args[0]);
				if(PORT > 65536) throw new Exception();
			}catch(IllegalFormatConversionException e){
				e.printStackTrace();
				System.out.printf("Could not convert %s to integer. Using default port 3333.\n", args[0]);
				PORT = 3333;
			}catch(Exception e){
				System.out.println("General error. Using default port 3333");
				e.printStackTrace();
				PORT = 3333;
			}
		}else{ PORT = 3333; }
		
		createPanel();
	}
	/**
	 * Creates the window.
	 */
	private static void createPanel(){
		//////////////// FRAME
		frame = new JFrame();
		frame.setTitle("Server Window");
		frame.setSize(700, 400);
		frame.setLayout(null);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//////////////// RIGHT SIDE
		portLabel = new JLabel("Port Number:");
		NumberFormat format = NumberFormat.getInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(0);
		formatter.setMaximum(65536);
		formatter.setAllowsInvalid(false);
		portField = new JFormattedTextField(formatter);
		portField.setValue(PORT);
		startButton = new JButton("Start Server");
		stopButton = new JButton("Shut Down");
		
		startButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(!running){
					PORT = (int)portField.getValue();
					try {
						runServer();
						running = true;
						createConnectionChecker();
						portLabel.setText(String.format("Port Number: %d", PORT));
					} catch (Exception e1) {
						e1.printStackTrace();
						report("Error", String.format("Could not start server at port %d.", PORT));
					}
				}else{report("Error", "Trying to run server. Server already running!");}
			}
		});
		stopButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(running){
					closeServer();
					report("Server", "Server shutting down.");
					running = false;
					portLabel.setText("Port Number:");
				}else{report("Error", "Trying to shut down the server. Server is already down!");}
			}
		});
		
		///////////////// COMMAND LINE
		commandField = new JTextField();
		commandField.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(running){
					command(commandField.getText());
				}else{report("Error", "Could not run command, server not running!");}
				commandField.setText("");
			}
		});
		
		///////////////// REPORT AREA
		reports = new JTextArea();
		scrollPane = new JScrollPane(reports,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		reports.setEditable(false);
		reports.setLineWrap(true);
		
		/////////////////// TABLE
		String[] columns = {"ID", "IP", "Socket", "Kick"};
		model = new DefaultTableModel(null, columns);
		table = new JTable(model){
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row, int column){
				return false;
			}
		};
		scrollTable = new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		DefaultTableCellRenderer centreRenderer = new DefaultTableCellRenderer();
		centreRenderer.setHorizontalAlignment(JLabel.CENTER);
		table.getColumnModel().getColumn(2).setCellRenderer(centreRenderer);
		table.getColumnModel().getColumn(3).setCellRenderer(centreRenderer);
		table.getTableHeader().setReorderingAllowed(false);
		
		table.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(e.getButton() == MouseEvent.BUTTON1){
					int row = table.getSelectedRow();
					int column = table.getSelectedColumn();
					int parsed_ID = Integer.parseInt((String)table.getValueAt(row, 0));
					if(column == 2 && table.getValueAt(row, column).equals("Accept")){
						table.setValueAt("Online", row, column);
						report("Server", String.format("Client Nr %d connected from %s", parsed_ID, table.getValueAt(row, 1)));
						Thread thread = new Thread(clients.get(parsed_ID));
						thread.start();
					}else if(column == 3){
						model.removeRow(row);
						if(clients.containsKey(parsed_ID)){
							closeClient(parsed_ID);
						}
					}
				}
			}
		});
		
		////////////////// LAYOUT
		scrollTable.setBounds(10,10,520,100);
		scrollPane.setBounds(10, 130, 520, 200);
		commandField.setBounds(10, 340, 520, 20);
		portLabel.setBounds(550, 20, 110, 20);
		portField.setBounds(550, 50, 110, 20);
		startButton.setBounds(550, 80, 110, 20);
		stopButton.setBounds(550, 110, 110, 20);
		frame.add(scrollTable);
		frame.add(scrollPane);
		frame.add(commandField);
		frame.add(portLabel);
		frame.add(portField);
		frame.add(startButton);
		frame.add(stopButton);
		frame.setVisible(true);
	}
	/**
	 * Creates the connection checker thread.
	 */
	private static void createConnectionChecker(){
		connectionStatus = new Thread(){
			public void run(){
				while(running){
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					for(ClientV2 client: clients.values()){
						try{
							client.getDOS().writeUTF("02TestConnection");
							client.getDOS().flush();
						}catch(Exception e){
							int row = getRowNumber(client.getID());
							if(row != -1){model.removeRow(row);}
							report("Server",String.format("Client %d disconnected from the server.", client.getID()));
							closeClient(client.getID());
						}
					}
				}
			}
		};
		connectionStatus.start();
	}
	/**
	 * Starts the server and its relevant threads.
	 * @throws Exception
	 */
	private static void runServer() throws Exception{
		server = new ServerSocket(PORT);
		String[] client_info = {"",""};
		seeker = new Thread(){
			public void run(){
				report("Server", "Waiting for clients...");
				while(true){
					try {
						TimeUnit.SECONDS.sleep(1);
						Socket socket = server.accept();
						clients.put(ID, new ClientV2(socket, ID));
						client_info[0] = Integer.toString(ID);
						client_info[1] = socket.getLocalAddress().getHostName();
						insertTable(client_info[0], client_info[1]);
						ID++;
					} catch (InterruptedException e) {} catch (Exception e) {}
				}
			}
		};
		gameStatus = new Thread(){
			public void run(){
				while(true){
					try{
						TimeUnit.SECONDS.sleep(1);
						for(ClientV2 client:clients.values()){
							if(client.getDIS().available() > 0){
								String str = client.getDIS().readUTF();
								if(str.equals("01gamestatus")){
									client.getDOS().writeUTF("01"+requestGameStatus()+Integer.toString(ClientV2.getVariant()));
									client.getDOS().flush();
									if(requestGameStatus()){
										ClientV2.incrementVariant();
									}
								}
							}
						}
					} catch (InterruptedException e) {} catch (Exception e) {}
				}
			}
		};
		report("Server", "Server is now up.");
		gameStatus.start();
		seeker.start();
	}
	/**
	 * Stops the server.
	 */
	private static void closeServer(){
		for(int client_ID : clients.keySet()){
			closeClient(client_ID);
		}
		model.setRowCount(0);		
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		gameUp = false;
	}
	/**
	 * Closes the connection with this client.
	 * @param clientID the identity number of the client
	 */
	private static void closeClient(int clientID){
		try{
			clients.get(clientID).close();
		}catch(SocketException se){
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			clients.remove(clientID);
		}
	}
	/**
	 * Writes a message on the server window.
	 * @param author the source of the message.
	 * @param message
	 */
	public static void report(String author, String message){
		reports.append(String.format("%s: %s\n",author, message));
	}
	/**
	 * Inserts a new row into the table.
	 * @param a table ID
	 * @param b client IP
	 */
	private static void insertTable(String a, String b){
		String[] data = {a, b, "Accept", "Kick"};
		model.addRow(data);
	}
	/**
	 * Performs commands written in the command text field.
	 * @param cmd command
	 */
	private static void command(String cmd){
		String[] cmd_line = cmd.split(" ");
		if(cmd_line[0].toLowerCase().equals("start")){
			if(!gameUp){
				gameUp = true;
				report("Game", "Game started!");
			}else{report("Error","Game already up! If you wish to restart the game, use the <restart> command instead");}
		}
		if(cmd_line[0].toLowerCase().equals("kick")){
			if(cmd_line.length > 0){
				try{
					int kick_id = Integer.parseInt(cmd_line[1]);
					if(clients.containsKey(kick_id)){
						model.removeRow(getRowNumber(kick_id));
						closeClient(kick_id);
					}else{report("Server",String.format("No client with ID %d", kick_id));}
				}catch(IllegalFormatConversionException e){}
				catch(Exception ee){ee.printStackTrace();}
			}
		}
		if(cmd_line[0].toLowerCase().equals("help")){
			report("Help", "Available commands:\n\t<help> - shows available commands\n\t<kick n> - kicks the client with id n\n\t<start> - starts the game");
		}
	}
	/**
	 * Returns the status of the game.
	 * @return True if the game is up. False otherwise
	 */
	public static boolean requestGameStatus(){
		return gameUp;
	}
	/**
	 * Returns the number of the row in the table that belongs to this client.
	 * @param client_ID
	 * @return table row number
	 */
	private static int getRowNumber(int client_ID){
		for(int i = 0; i < model.getRowCount(); i++){
			if(Integer.parseInt((String)model.getValueAt(i, 0)) == client_ID){
				return i;
			}
		}
		return -1;
	}
}
