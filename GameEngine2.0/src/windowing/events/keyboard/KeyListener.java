package windowing.events.keyboard;
import windowing.Window;

public interface KeyListener {
	/**
	 * Function callback for when a key is pressed or being repeated
	 * 
	 * @param window Window that called this callback function
	 * @param key GLFW keycode
	 * @param isRepeat Indicates whether the key is being held and repeated
	 * @param mods Array containing the mod keys that were pressed
	 */
	public void onKeyPress(Window window, Key key, boolean isRepeat, ModKey[] mods);
	
	/**
	 * Function callback for when a key is released
	 * 
	 * @param window Window that called this callback function
	 * @param key GLFW keycode
	 * @param mods Array containing the mod keys that were pressed
	 */
	public void onKeyRelease(Window window, Key key, ModKey[] mods);
	
	/**
	 * Function callback for key inputs where the keyCodes are the unicode representation of the key
	 * instead of the key code constant
	 * 
	 * @param window Window that called this callback function
	 * @param key Key pressed as a unicode character value with modifiers applied
	 */
	public void onCharInput(Window window, char key);
	
	/**
	 * Function callback for key inputs where the keyCodes are the unicode representation of the key
	 * instead of the key code constant, this function also includes the modifiers
	 * 
	 * @param window Window that called this callback function
	 * @param key Key pressed as a unicode character value with modifiers applied
	 * @param mods Array containing the mod keys that were pressed
	 */
	public void onCharInputMods(Window window, char key, ModKey[] mods);
}
