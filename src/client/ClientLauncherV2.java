package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.IllegalFormatConversionException;
import java.util.concurrent.TimeUnit;

import org.lwjgl.util.vector.Vector2f;

import AI.Node;
import entities.Camera;
import entities.Enemy;
import entities.Entity;
import entities.Player;
import entities.Trigger;
import renderEngine.DisplayManager;
import toolbox.Maths;
//00 - server info // 01 - game info // 02 - test connection
/**
 * Creates a connection with the server and, if the server allows it, starts the game.<br>
 * <br>
 * Variables:<br>
 * PORT - integer, port number<br>
 * HOST - String, the ip address<br>
 * dos - DataOutputStream, the output channel of the client<br>
 * dis- DataInputStream, the input channel of the client<br>
 * socket - Socket, the connection with the server<br>
 * hasServer - boolean, true if the server is up, false otherwise<br>
 * connect - boolean, true if the client is trying to connect, false otherwise<br>
 * camera - Camera, the point of view of the player<br>
 * player - Player, the player object
 */
public class ClientLauncherV2 {
	private static int PORT;
	private static String HOST;
	private static DataOutputStream dos;
	private static DataInputStream dis;
	private static Socket socket = null;
	private static boolean hasServer = false;
	private static boolean connect = true;
	private static Camera camera;
	private static Player player;
	
	/**
	 * Sets up the port and the host ip.
	 * @param args arg1 = IP, arg 2 = PORT. The arguments must be these 2, or nothing.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		if(args.length==2){
			try{
				HOST = args[0]; PORT = Integer.parseInt(args[1]);
				if(PORT > 65536) throw new Exception();
			}catch(IllegalFormatConversionException e){
				e.printStackTrace();
				System.out.printf("Could not convert %s to integer. Using default port 3333.\n", args[0]);
				PORT = 3333; HOST = "localhost";
			}catch(Exception e){
				System.out.println("General error. Using default port 3333");
				e.printStackTrace();
				PORT = 3333; HOST = "localhost";
			}
		}else{ PORT = 3333; HOST = "localhost"; }
		int iteration = 1;
		while(connect){
			try {
				socket = new Socket(HOST, PORT);
				System.out.println("Connecting to the server...");
			} catch (Exception noServer) {
				System.out.printf("There is no active server at port %d. Waiting...\n", PORT);
				while(true){
					try {
						TimeUnit.MILLISECONDS.sleep(500);
						socket = new Socket(HOST, PORT);
						System.out.println("\nConnecting to the server...");
						break;
					} catch (InterruptedException e) {
					} catch (SocketException noServer2){
						System.out.print(".");
						if(iteration++ %40 == 0){ System.out.println(); }
					}
				}
			}
			
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			hasServer = true;
			iteration = 1;
			String str = "";
			while(hasServer){
				TimeUnit.MILLISECONDS.sleep(500);
				if(dis.available() > 0){
					str = dis.readUTF();
					if(str.startsWith("00")){
						System.out.println(str.split("00")[1]);
						break;
					}
				}
				checkConnection();
				System.out.print(".");
				if(iteration++ %40 == 0){ System.out.println(); }
			}
			if(hasServer){
				System.out.println("Waiting for the game to start");
				iteration = 1;
				int variation = 0;
				while(hasServer){
					dos.writeUTF("01gamestatus");
					dos.flush();
					TimeUnit.MILLISECONDS.sleep(500);
					if(dis.available() > 0){
						str = dis.readUTF();
						if(str.startsWith("01true")){
							variation = Integer.parseInt(str.split("true")[1]);
							break;
						}
					}
					checkConnection();
					System.out.print(".");
					if(iteration++ %40 == 0){ System.out.println(); }
					TimeUnit.MILLISECONDS.sleep(500);
				}
				if(hasServer){
					DisplayManager.CreateDisplay(800, 600, "Java RPG Project");
					DisplayManager.toggleVsync();
					RepositoryV2.init();
					camera = new Camera(0.05f);
					player = RepositoryV2.createPlayer(new Vector2f(0,0), 0.8f, camera);
					RepositoryV2.setPlayer(player);
					player.setTextureIndex(variation%8);
					RepositoryV2.restart();
					
					while(hasServer){
						if(DisplayManager.isCloseRequested()){
							connect = false;
							break;
						}
						camera.move();
						for(Entity character:RepositoryV2.getNPCs()){
							character.move(RepositoryV2.getTerrain());
							if(character.getClass() == Enemy.class){
								if(Maths.distancef(player.getPosition(), character.getPosition()) <
										((Enemy)character).getDetectRange()){
									((Enemy)character).setTarget(player.getPosition());
								}
								if(((Enemy)character).getPath()!= null){
									for(Node node:((Enemy)character).getPath()){
										RepositoryV2.addEffect(13, new Vector2f(node.x, node.y), 0, 0, 0, 1);
									}
								}
							}
							collisionCheck(character);
						}
						if(player.noTarget){
							player.target = false;
							for(Entity npc:RepositoryV2.getNPCs()){
								if(npc != player){
									((Enemy)npc).abortTarget();
								}
							}
							player.noTarget = false;
						}
						if(player.target){
							for(Entity npc:RepositoryV2.getNPCs()){
								if(npc != player){
									((Enemy)npc).setTarget(player.getPosition());
								}
							}
							player.target = false;
						}
						RepositoryV2.getRenderer().render(camera, RepositoryV2.getEntities());
						RepositoryV2.clearEffects();
						DisplayManager.updateDisplay();
						try{
							checkConnection();
						}catch(Exception e){hasServer = false;break;}
					}
					System.out.println("Closed!");
					
					RepositoryV2.getRenderer().cleanUp();
					RepositoryV2.getLoader().cleanUp();
					DisplayManager.closeDisplay();
				}
			}
			if(connect){
				System.out.println("Lost connection to the server");
			}else{System.out.println("Game closed by user");}
			TimeUnit.SECONDS.sleep(5);
		}
	}
	/**
	 * Checks collision with the triggers.
	 * @param character
	 */
	private static void collisionCheck(Entity character){
		if(character == player){
			for(Trigger trigger: RepositoryV2.getTriggers()){
				if(player.getBox().intersects(trigger.getBox())){
					RepositoryV2.activateTrigger(trigger.getActionID());
				}
			}
		}
		
	}
	/**
	 * Checks the connection with the server.
	 */
	private static void checkConnection(){
		try{
			dos.writeUTF("");
			dos.flush();
		}catch(Exception e){
			hasServer = false;
			System.out.println("Error!");
		}
	}
}