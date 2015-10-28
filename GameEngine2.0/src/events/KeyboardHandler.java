package events;

import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCharModsCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFW;

import windowing.Window;

public class KeyboardHandler implements
GLFWKeyCallback.SAM,
GLFWCharCallback.SAM,
GLFWCharModsCallback.SAM{
	
	private KeyEvent keyboard;
	private Window window;
	
	public KeyboardHandler(KeyEvent eventHandle, Window curWindow){
		window = curWindow;
		keyboard = eventHandle;
	}
	
	@Override
	public void invoke(long windowHandle, int codepoint, int mods) {
		keyboard.charInputMods(window, codepoint, ModKey.getMods(mods));
	}

	@Override
	public void invoke(long windowHandle, int codepoint) {
		keyboard.charInput(window, codepoint);
	}

	@Override
	public void invoke(long windowHandle, int key, int scancode, int action, int mods) {
		if(action == GLFW.GLFW_PRESS){
			keyboard.keyPress(window, Key.getKey(key), false, ModKey.getMods(mods));
		}else if(action == GLFW.GLFW_REPEAT){
			keyboard.keyPress(window, Key.getKey(key), true, ModKey.getMods(mods));
		}else{
			keyboard.keyRelease(window, Key.getKey(key), ModKey.getMods(mods));
		}
	}

}
