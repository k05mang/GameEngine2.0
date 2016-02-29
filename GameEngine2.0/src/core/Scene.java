package core;
import static org.lwjgl.opengl.GL11.GL_INVALID_ENUM;
import static org.lwjgl.opengl.GL11.GL_INVALID_OPERATION;
import static org.lwjgl.opengl.GL11.GL_INVALID_VALUE;
import static org.lwjgl.opengl.GL11.GL_OUT_OF_MEMORY;
import static org.lwjgl.opengl.GL11.GL_STACK_OVERFLOW;
import static org.lwjgl.opengl.GL11.GL_STACK_UNDERFLOW;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL30.GL_INVALID_FRAMEBUFFER_OPERATION;
import windowing.Window;

public interface Scene {
	
	/**
	 * Function called by the Window during the main render loop. The Scene subclass 
	 * should perform any rendering in this function.
	 * 
	 * @param window Calling window object
	 */
	public void computeScene(Window window);
	
	/**
	 * Function called when the context is being deleted. This method is meant to allow the 
	 * subclass to clean up external resources used during the run time of the application.
	 * This would be things such as GPU assets that can be deleted with calls to various 
	 * functions.
	 */
	public void cleanup();
	
	/**
	 * Function that can be used to get OpenGL error states that will be printed to the terminal
	 */
	public static void printError(){
		int error = glGetError();
		System.out.println(error);
		switch(error){
			case GL_INVALID_ENUM:
				System.out.println("GL_INVALID_ENUM");
				break;
			case GL_INVALID_VALUE:
				System.out.println("GL_INVALID_VALUE");
				break;
			case GL_INVALID_OPERATION:
				System.out.println("GL_INVALID_OPERATION");
				break;
			case GL_INVALID_FRAMEBUFFER_OPERATION:
				System.out.println("GL_INVALID_FRAMEBUFFER_OPERATION");
				break;
			case GL_OUT_OF_MEMORY:
				System.out.println("GL_OUT_OF_MEMORY");
				break;
			case GL_STACK_UNDERFLOW:
				System.out.println("GL_STACK_UNDERFLOW");
				break;
			case GL_STACK_OVERFLOW:
				System.out.println("GL_STACK_OVERFLOW");
				break;
			default:
				System.out.println("No error");
				break;
		}
	}
}
