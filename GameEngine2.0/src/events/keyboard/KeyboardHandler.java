package events.keyboard;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWCharModsCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import windowing.Window;

public class KeyboardHandler implements
GLFWKeyCallback.SAM,
GLFWCharCallback.SAM,
GLFWCharModsCallback.SAM{
	
	private ArrayList<KeyListener> keyListeners;
	private Window window;
	
	/**
	 * Constructs a KeyboardHandler responsible for handling input events related to the keyboard of the {@code curWindow}
	 * window context.
	 * 
	 * @param curWindow Current context to pass keyboard input events to the listeners
	 */
	public KeyboardHandler(Window curWindow){
		window = curWindow;
		keyListeners = new ArrayList<KeyListener>();
	}
	
	/**
	 * Adds a KeyListener object to this keyboard handler. The listener will receive any events fired by the 
	 * window related to the keyboard.
	 * 
	 * @param listener KeyListener object to attach to this handler
	 */
	public void addListener(KeyListener listener){
		keyListeners.add(listener);
	}
	
	/**
	 * Removes a KeyListener from this keyboard handler. The listener will be completely removed from this handler.
	 * The listener will no longer receive events fired by the window context this handler is associated with.
	 * 
	 * @param listener KeyListener to remove from this handler
	 */
	public void removeListener(KeyListener listener){
		keyListeners.remove(listener);
	}
	
	@Override
	public void invoke(long windowHandle, int codepoint, int mods) {
		//iterate over all the active listeners
		for(KeyListener listener : keyListeners){
			//this invoke method handles character input modifiers
			listener.onCharInputMods(window, Character.toChars(codepoint)[0], ModKey.getMods(mods));
		}
	}

	@Override
	public void invoke(long windowHandle, int codepoint) {
		//iterate over all the active listeners
		for(KeyListener listener : keyListeners){
			//this invoke handles character input
			listener.onCharInput(window, Character.toChars(codepoint)[0]);
		}
	}

	@Override
	public void invoke(long windowHandle, int key, int scancode, int action, int mods) {
		//iterate over all the active listeners
		for(KeyListener listener : keyListeners){
			//this invoke handles key presses and releases, using the action value we can determine
			//which function to call in the listener
			if(action == GLFW.GLFW_PRESS){
				listener.onKeyPress(window, Key.getKey(key), false, ModKey.getMods(mods));
			}else if(action == GLFW.GLFW_REPEAT){
				listener.onKeyPress(window, Key.getKey(key), true, ModKey.getMods(mods));
			}else{
				listener.onKeyRelease(window, Key.getKey(key), ModKey.getMods(mods));
			}
		}
	}

}
