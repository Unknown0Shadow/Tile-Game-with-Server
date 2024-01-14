package toolbox;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;

/**
 * The class that handles all the maths for this program. Mostly used for calculating projection matrices.
 */
public class Maths {

	/**
	 * Creates the transformation matrix. Used to transform and render objects' shape and position on the screen.
	 * @param translation translation matrix
	 * @param rx rotation around x axis (never used)
	 * @param ry rotation around y axis (never used)
	 * @param rz rotation around z axis (never used)
	 * @param scale 
	 * @return transformation matrix
	 */
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx,
			float ry, float rz, float scale){
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0,0,1), matrix, matrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		return matrix;
	}
	/**
	 * Creates the matrix which will be used to calculate the rendering from the camera's point of view.
	 * @param camera
	 * @return matrix
	 */
	public static Matrix4f createViewMatrix(Camera camera){
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Vector3f cameraPos = camera.getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}
	/**
	 * Gives the sign of x.
	 * @param x
	 * @return 1 if x is positive, -1 otherwise
	 */
	public static short signOf(float x){
		if(x < 0){
			return -1;
		}
		return 1;
	}
	/**
	 * Calculates the distance between two points given as floats.
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return float distance
	 */
	public static float distance(float x1, float y1, float x2, float y2){
		return (float) Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2));
	}
	/**
	 * Calculates the distance between two points given as Vector2f.
	 * @param v1 first point
	 * @param v2 second point
	 * @return float distance
	 */
	public static float distancef(Vector2f v1, Vector2f v2){
		return (float) Math.sqrt(Math.pow(v1.x-v2.x, 2)+Math.pow(v1.y-v2.y, 2));
	}
}
