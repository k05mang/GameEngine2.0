package events;
import windowing.Window;

public interface MouseEvent {
	/**
	 * Function callback when a mouse button is pressed
	 * 
	 * @param window Window that called this callback function
	 * @param button GLFW button code
	 * @param isRepeat Indicates whether the button is being held and is a repeat value
	 * @param mods Bitfield of modifier keys that were held with this mouse press
	 */
	public void mousePress(Window window, int button, boolean isRepeat, int mods);
	
	/**
	 * Function callback for when a mouse button is released
	 * 
	 * @param window Window that called this callback function
	 * @param button GLFW button code
	 * @param mods Bitfield of modifier keys that were held with this mouse press
	 */
	public void mouseRelease(Window window, int button, int mods);
	
	/**
	 * Function callback for when the mouse or touch pad is scrolled
	 * 
	 * @param window Window that called this callback function
	 * @param xoffset The scroll offset aWindow the x-axis
	 * @param yoffset The scroll offset aWindow the y-axis
	 */
	public void mouseScroll(Window window, double xoffset, double yoffset);
	
	/**
	 * Function callback for when the mouse enters or leaves the window area
	 * 
	 * @param window Window that called this callback function
	 * @param entered Indicates whether the mouse entered the window or exited it
	 */
	public void mouseEnter(Window window, boolean entered);
	
	/**
	 * Function callback for when the mouse moves
	 * 
	 * @param window Window that called this callback function
	 * @param xpos New x position of the mouse cursor
	 * @param ypos New y position of the mouse cursor
	 */
	public void mouseMove(Window window, double xpos, double ypos);
}
