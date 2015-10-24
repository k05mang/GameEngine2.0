package events;

import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCharModsCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

public class KeyboardHandler implements
GLFWKeyCallback.SAM,
GLFWCharCallback.SAM,
GLFWCharModsCallback.SAM{
	
	private KeyEvent keyboard;
	
	@Override
	public void invoke(long window, int codepoint, int mods) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invoke(long window, int codepoint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		// TODO Auto-generated method stub
		
	}

}
