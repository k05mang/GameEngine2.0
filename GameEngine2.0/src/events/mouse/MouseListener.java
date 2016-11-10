package events.mouse;
import events.keyboard.ModKey;
import windowing.Window;

public interface MouseListener {
	/**
	 * Function callback when a mouse button is pressed
	 * 
	 * @param window Window that called this callback function
	 * @param button GLFW button code
	 * @param isRepeat Indicates whether the button is being held and is a repeat value
	 * @param mods Array containing the mod keys that were pressed
	 */
	public void onMousePress(Window window, MouseButton button, boolean isRepeat, ModKey[] mods);
	
	/**
	 * Function callback for when a mouse button is released
	 * 
	 * @param window Window that called this callback function
	 * @param button GLFW button code
	 * @param mods Array containing the mod keys that were pressed
	 */
	public void onMouseRelease(Window window, MouseButton button, ModKey[] mods);
	
	/**
	 * Function callback for when the mouse or touch pad is scrolled
	 * 
	 * @param window Window that called this callback function
	 * @param xoffset The scroll offset aWindow the x-axis
	 * @param yoffset The scroll offset aWindow the y-axis
	 */
	public void onMouseScroll(Window window, double xoffset, double yoffset);
	
	/**
	 * Function callback for when the mouse enters or leaves the window area
	 * 
	 * @param window Window that called this callback function
	 * @param entered Indicates whether the mouse entered the window or exited it
	 */
	public void onMouseEnter(Window window, boolean entered);
	
	/**
	 * Function callback for when the mouse moves
	 * 
	 * @param window Window that called this callback function
	 * @param xpos New x position of the mouse cursor
	 * @param ypos New y position of the mouse cursor, y values increase as the mouse moves down the screen
	 */
	public void onMouseMove(Window window, double xpos, double ypos);
}
