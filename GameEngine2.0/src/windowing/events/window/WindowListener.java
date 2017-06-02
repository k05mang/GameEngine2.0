package windowing.events.window;
import windowing.Window;

public interface WindowListener {

	/**
	 * Function callback for when the window gets resized
	 * 
	 * @param window Window that called this callback function
	 * @param width Width of the new window size
	 * @param height Height of the new window size
	 */
	public void onWindowResize(Window window, int width, int height);
	
	/**
	 * Function callback for when the windows framebuffer gets resized
	 * 
	 * @param window Window that called this callback function
	 * @param width Width of the new Framebuffer size
	 * @param height Height of the new Framebuffer size
	 */
	public void onFrameBufferResize(Window window, int width, int height);
	
	/**
	 * Function callback for when a request to close the window has been made
	 * 
	 * @param window Window that called this callback function
	 */
	public void onWindowClose(Window window);
	
	/**
	 * Function callback for when the window needs to be refreshed after another window has blocked it
	 * 
	 * @param window Window that called this callback function
	 */
	public void onWindowRefresh(Window window);
	
	/**
	 * Function callback for when the window is iconified
	 * 
	 * @param window Window that called this callback function
	 */
	public void onWindowIconify(Window window);
	
	/**
	 * Function callback for when the window is restored from an iconified state
	 * 
	 * @param window Window that called this callback function
	 */
	public void onWindowRestore(Window window);
	
	/**
	 * Function callback for when the window is focused or defocused
	 * 
	 * @param window Window that called this callback function
	 * @param isFocused Indicates whether the window is being focused or defocused
	 */
	public void onWindowFocus(Window window, boolean isFocused);
	
	/**
	 * Function callback for when the windows position changes, and the window is moved
	 * 
	 * @param window Window that called this callback function
	 * @param xpos X position that the window was moved to
	 * @param ypos Y position that the window was moved to
	 */
	public void onWindowPosChange(Window window, int xpos, int ypos);
}
