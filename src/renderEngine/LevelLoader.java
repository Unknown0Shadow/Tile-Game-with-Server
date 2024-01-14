package renderEngine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import entities.Entity;
/**
 * Reads and writes data into files.
 */
public class LevelLoader {
	/**
	 * Stores serialised data into a text file. (obsolete)
	 * @param levelName the name of the file
	 * @param tiles the list of tiles
	 * @param npcs the list of npc's
	 * @param shadows the list of shadows
	 * @param lights the list of lights
	 */
	public static void saveLevel(String levelName, List<Entity> tiles,
			List<Entity> npcs, List<Entity> shadows, List<Entity> lights){
		ArrayList<Object> data = new ArrayList<Object>(); // store all data in one massive array
		data.add(tiles);
		data.add(npcs);
		data.add(shadows);
		data.add(lights);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream("levels/"+levelName+".dat");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(data);
			objectOutputStream.close();
			fileOutputStream.close();
			System.out.println("Level data successfully saved in "+levelName+".dat!");
		} catch (IOException e) {
			System.out.println("An error occured while trying to save data to "+levelName+".dat!");
			e.printStackTrace();
		}
	}
	/**
	 * Stores serialised data into a text file.
	 * @param levelName the name of the file
	 * @param tiles the matrix containing the tile texture indices
	 */
	public static void saveLevel(String levelName, List<int[][]> tiles){
		try{
			FileOutputStream fileOutputStream = new FileOutputStream("levels/"+levelName+".dat");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(tiles);
			objectOutputStream.close();
			fileOutputStream.close();
			System.out.println("Level data successfully saved in "+levelName+".dat!");
		} catch (IOException e) {
			System.out.println("An error occured while trying to save data to "+levelName+".dat!");
			e.printStackTrace();
		}
	}
	/**
	 * Loads serialised data from a file.
	 * @param levelName the name of the file
	 * @return a list of matrices, consisting of terrain, shadows, lights
	 */
	public static List<int[][]> loadLevel2(String levelName){
		List<int[][]> ints = new ArrayList<>();
		try{
			FileInputStream fileInputStream = new FileInputStream("levels/" + levelName+".dat");
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			ints = (ArrayList<int[][]>)objectInputStream.readObject();
			objectInputStream.close();
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFound! Could not find file levels/"+levelName+".dat!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("An error occured while trying to load data from levels/"+levelName+".dat!");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFound! An error occured while trying to deserialize data from levels/"+levelName+".dat!");
			e.printStackTrace();
		}
		return ints;
	}
	/**
	 * Loads serialised data from a file. (obsolete)
	 * @param levelName the name of the file
	 * @return an ArrayList containing the lists of entities
	 */
	public static ArrayList<Object> loadLevel(String levelName){
		ArrayList<Object> data = new ArrayList<Object>();
		try {
			FileInputStream fileInputStream = new FileInputStream("levels/"+levelName+".dat");
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			data = (ArrayList<Object>) objectInputStream.readObject();
			objectInputStream.close();
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFound! Could not find file levels/"+levelName+".dat!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("An error occured while trying to load data from levels/"+levelName+".dat!");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFound! An error occured while trying to deserialize data from levels/"+levelName+".dat!");
			e.printStackTrace();
		}
		return data;
	}
}
