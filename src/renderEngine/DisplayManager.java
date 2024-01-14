package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
/**
 * Manages the display window and all the graphic processes.<br>
 * <br>
 * Variables:<br>
 * display_width, display_height - integer, the width and height of the window<br>
 * display_title - String, the title of the window<br>
 * display_fps_cap - integer, the maximum amount of frames per second<br>
 * vsync - boolean, whether or not to use vsync<br>
 * lastFrameTime - long, the time when the last frame was processed<br>
 * delta - float, the difference of time between the lastFrameTime and currentFrameTime, divided by 1000
 */
public class DisplayManager {
	private static int display_width;
	private static int display_height;
	private static String display_title;
	private static int display_fps_cap = 60;
	private static boolean vsync;
	
	private static long lastFrameTime;
	private static float delta;
	/**
	 * Creates the window.
	 * @param width
	 * @param height
	 * @param title
	 */
	public static void CreateDisplay(int width, int height, String title){
		display_width = width;
		display_height = height;
		display_title = title;
		
		ContextAttribs attribs = new ContextAttribs(3,2) // 3.2 version of opengl
		.withForwardCompatible(true)
		.withProfileCore(true);
		
		try {
			Display.setDisplayMode(new DisplayMode(display_width, display_height));
			Display.create(new PixelFormat(), attribs);
			Display.setTitle(display_title);
			initGL();
		} catch (LWJGLException e) {
			System.out.println("Unable to setup mode "+display_width+"x"+display_height + e);
		}
		lastFrameTime = getCurrentTime();
	}
	/**
	 * Initialises OpenGL.
	 */
	private static void initGL(){
		GL11.glDisable(GL11.GL_DEPTH_TEST);	// disables 3d info processing
		GL11.glClearColor(0, 0, 0, 0);	// makes the screen black
		GL11.glViewport(0, 0, display_width, display_height);
	}
	/**
	 * Updates the view. Redraws.
	 */
	public static void updateDisplay(){
		Display.sync(display_fps_cap);
		Display.update();
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
	}
	/**
	 * Closes the window.
	 */
	public static void closeDisplay(){
		Display.destroy();
	}
	
	// getters
	public static boolean isVsyncEnabled(){
		return vsync;
	}
	public static boolean isCloseRequested(){
		return Display.isCloseRequested();
	}
	public static int getWidth(){
		return display_width;
	}
	public static int getHeight(){
		return display_height;
	}
	public static String getTitle(){
		return display_title;
	}
	public static int getFpsCap(){
		return display_fps_cap;
	}
	// setters
	public static void setFpsCap(int fps_cap){
		display_fps_cap = fps_cap;
	}
	/**
	 * Toogles vsync on and off based on vsync boolean variable.
	 */
	public static void toggleVsync(){
		vsync = !vsync;
		System.out.printf("Vsync toggled to %s\n", vsync);
		Display.setVSyncEnabled(vsync);	// prevents visual glitches due to monitor refresh rate
	}
	
	private static long getCurrentTime(){
		return Sys.getTime()*1000/Sys.getTimerResolution();
	}
	
	public static float getFrameTimeSeconds(){
		return delta;
	}
}