package windowing;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.glfw.*;

import events.*;

/**
 * TODO expand to multi monitor support
 * @author Kevin Mango
 *
 */
public class Window {
	private long window;
	private KeyboardHandler keyboard;
	private MouseHandler mouse;
	private WindowHandler windowHandler;
	private int width, height;
	
	public Window(int width, int height){
		window = 0L;
		keyboard = null;
		mouse = null;
		windowHandler = null;
		this.width = width;
		this.height = height;
	}
	
	public void init(){
		
	}
	
	public void launch(/*something here*/){
		
	}
	
	/**
	 * Sets the keyboard input callback, for this window, to the given event handling class
	 * 
	 * @param callback Event handling class that implements the functions called when certain events fire from the window keyboard input
	 */
	public void setKeyboardCallback(KeyEvent callback){
		keyboard = new KeyboardHandler(callback, this);//create the handler for calling the right methods on the events
		//bind the callbacks to the window
		glfwSetCharCallback(window, GLFWCharCallback.create(keyboard));
		glfwSetCharModsCallback(window, GLFWCharModsCallback.create(keyboard));
		glfwSetKeyCallback(window, GLFWKeyCallback.create(keyboard));
	}
	
	/**
	 * Sets the mouse input callback, for this window, to the given event handling class
	 * 
	 * @param callback Event handling class that implements the functions called when certain events fire from the window mouse input
	 */
	public void setMouseCallback(MouseEvent callback){
		mouse = new MouseHandler(callback, this);//create the handler for calling the right methods on the events
		//bind the callbacks to the window
		glfwSetMouseButtonCallback(window, GLFWMouseButtonCallback.create(mouse));
		glfwSetCursorPosCallback(window, GLFWCursorPosCallback.create(mouse));
		glfwSetCursorEnterCallback(window, GLFWCursorEnterCallback.create(mouse));
		glfwSetScrollCallback(window, GLFWScrollCallback.create(mouse.scroll));
	}

	/**
	 * Sets the window event callback, for this window, to the given event handling class
	 * 
	 * @param callback Event handling class that implements the functions called when certain events fire from the window
	 */
	public void setWindowCallback(WindowEvent callback){
		windowHandler = new WindowHandler(callback, this);//create the handler for calling the right methods on the events
		//bind the callbacks to the window
		glfwSetWindowCloseCallback(window, GLFWWindowCloseCallback.create(windowHandler.close));
		glfwSetWindowIconifyCallback(window, GLFWWindowIconifyCallback.create(windowHandler.iconify));
		glfwSetWindowRefreshCallback(window, GLFWWindowRefreshCallback.create(windowHandler.refresh));
		glfwSetWindowPosCallback(window, GLFWWindowPosCallback.create(windowHandler.pos));
		glfwSetWindowSizeCallback(window, GLFWWindowSizeCallback.create(windowHandler.resize));
		glfwSetWindowFocusCallback(window, GLFWWindowFocusCallback.create(windowHandler.focus));
		glfwSetFramebufferSizeCallback(window, GLFWFramebufferSizeCallback.create(windowHandler.framebuffer));
	}
	
	/**
	 * Shows this window if it is hidden
	 */
	public void show(){
		glfwShowWindow(window);
	}
	
	/**
	 * Hides this window if it is shown
	 */
	public void hide(){
		glfwHideWindow(window);
	}
	
	/**
	 * Restores this window from an iconified state
	 */
	public void restore(){
		glfwRestoreWindow(window);
	}
	
	/**
	 * Iconify's this window
	 */
	public void iconify(){
		glfwIconifyWindow(window);
	}
	
	/**
	 * Sets a hint, for this window to be created with, to the given value
	 * 
	 * @param hint Hint enum indicating what parameter to set
	 * @param value Value to set the given hint to
	 */
	public void setHint(WindowHint hint, int value){
		glfwWindowHint(hint.type, value);
	}

	/**
	 * Sets a hint, for this window to be created with, to the given value
	 * 
	 * @param hint Hint enum indicating what parameter to set
	 * @param value Value to set the given hint to
	 */
	public void setHint(WindowHint hint, boolean value){
		glfwWindowHint(hint.type, value ? GL_TRUE : GL_FALSE);
	}

	/**
	 * Sets a hint, for this window to be created with, to the given value
	 * 
	 * @param hint Hint enum indicating what parameter to set
	 * @param value Value to set the given hint to
	 */
	public void setHint(WindowHint hint, HintConstant value){
		glfwWindowHint(hint.type, value.type);
	}
	
	/**
	 * Sets the title of this window to the given title
	 * 
	 * @param title Value to set the title to
	 */
	public void setTitle(String title){
		glfwSetWindowTitle(window, title);
	}
	
	/**
	 * Sets the sizing limits of this window to the given values, this indicates that the window, if resizable,
	 * cannot exceed the minimum and maximum sizes given
	 * 
	 * @param minWidth Minimum width to for this window
	 * @param minHeight Minimum height to for this window
	 * @param maxWidth Maximum width to for this window
	 * @param maxHeight Maximum height to for this window
	 */
	public void setSizeLimit(int minWidth, int minHeight, int maxWidth, int maxHeight){
		glfwSetWindowSizeLimits(window, minWidth, minHeight, maxWidth, maxHeight);
	}
	
	/**
	 * Sets the size of this window to the given width and height, assuming they do not exceed previously set limitations on window size
	 * 
	 * @param width Width to set this window to
	 * @param height Height to set this window to
	 */
	public void setSize(int width, int height){
		this.width = width;
		this.height = height;
		glfwSetWindowSize(window, width, height);
	}
	
	/**
	 * Tells this window it should close and release all it's resources
	 */
	public void close(){
		glfwSetWindowShouldClose(window, GL_TRUE);
	}
	
	/**
	 * Sets the position of this window to the given values
	 * 
	 * @param xpos X position, in screen coordinates, to set this window to
	 * @param ypos Y position, in screen coordinates, to set this window to
	 */
	public void setPos(int xpos, int ypos){
		glfwSetWindowPos(window, xpos, ypos);
	}
	
//	public void center(){
//		
//	}
	
	/**
	 * Sets the aspect ratio for this window. 
	 * <p>
	 * When the window is resized it will maintain the given aspect ratio and restrict the resizing of the window to maintain the ratio.
	 * If the denominator is 0 then 1 will be passed to the underlying system to prevent division by zero errors
	 * </p>
	 * 
	 * @param numerator Numerator of the aspect ratio
	 * @param denominator Denominator of the aspect ratio
	 */
	public void setAspectRatio(int numerator, int denominator){
		glfwSetWindowAspectRatio(window, numerator, denominator == 0 ? 1 : denominator);
	}
	 
	/**
	 * Sets the gamma of the primary monitor of the system to the given gamma exponent
	 * 
	 * @param gammaExp Gamma value to set the primary monitor to
	 */
	public void setGamma(float gammaExp){
		//TODO make this select a monitor to set the gamma of
		glfwSetGamma(glfwGetPrimaryMonitor(), gammaExp);
	}
	
	/**
	 * Sets the value of the clipboard
	 * 
	 * @param value Value to set the clipboard to
	 */
	public void setClipboard(String value){
		glfwSetClipboardString(window, value);
	}
	 /**
	  * Gets the clipboard string
	  * 
	  * @return String contained in the clipboard
	  */
	public String getClipboard(){
		return glfwGetClipboardString(window);
	}
}
