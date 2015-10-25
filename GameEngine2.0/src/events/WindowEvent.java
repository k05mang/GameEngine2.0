package events;
import windowing.Window;

public interface WindowEvent {

	/**
	 * Function callback for when the window gets resized
	 * 
	 * @param window Window that called this callback function
	 * @param width Width of the new window size
	 * @param height Height of the new window size
	 */
	public void windowResize(Window window, int width, int height);
	
	/**
	 * Function callback for when the windows framebuffer gets resized
	 * 
	 * @param window Window that called this callback function
	 * @param width Width of the new Framebuffer size
	 * @param height Height of the new Framebuffer size
	 */
	public void frameBufferResize(Window window, int width, int height);
	
	/**
	 * Function callback for when a request to close the window has been made
	 * 
	 * @param window Window that called this callback function
	 */
	public void windowClose(Window window);
	
	/**
	 * Function callback for when the window needs to be refreshed after another window has blocked it
	 * 
	 * @param window Window that called this callback function
	 */
	public void windowRefresh(Window window);
	
	/**
	 * Function callback for when the window is iconified
	 * 
	 * @param window Window that called this callback function
	 */
	public void windowIconify(Window window);
	
	/**
	 * Function callback for when the window is restored from an iconified state
	 * 
	 * @param window Window that called this callback function
	 */
	public void windowRestore(Window window);
	
	/**
	 * Function callback for when the window is focused or defocused
	 * 
	 * @param window Window that called this callback function
	 * @param isFocused Indicates whether the window is being focused or defocused
	 */
	public void windowFocus(Window window, boolean isFocused);
	
	public void windowPosChange(Window window, int xpos, int ypos);
}
