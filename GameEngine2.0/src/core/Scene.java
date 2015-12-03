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
	public void computeScene(Window window);
	
	public void cleanup();
	
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
	
//	public void launch();
}
