package windowing;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.util.HashMap;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import core.Scene;
import core.managers.SceneManager;
import windowing.events.keyboard.KeyListener;
import windowing.events.keyboard.KeyboardHandler;
import windowing.events.mouse.MouseHandler;
import windowing.events.mouse.MouseListener;
import windowing.events.window.WindowHandler;
import windowing.events.window.WindowListener;

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
	public int width, height, xpos, ypos, fbWidth, fbHeight;
	public double cursorX, cursorY, prevX, prevY;
	private HashMap<WindowHint, Integer> windowHints;
	//keyboard callabcks
	private GLFWKeyCallback key;
	private GLFWCharCallback charCall;
	private GLFWCharModsCallback charMods;
	//mouse callbacks
	private GLFWMouseButtonCallback mButton;
	private GLFWCursorPosCallback cPos;
	private GLFWCursorEnterCallback cEnter;
	private GLFWScrollCallback scroll;
	//window callbacks
	private GLFWWindowCloseCallback close;
	private GLFWWindowIconifyCallback icon;
	private GLFWWindowRefreshCallback refresh;
	private GLFWWindowPosCallback wPos;
	private GLFWWindowSizeCallback wSize;
	private GLFWWindowFocusCallback focus;
	private GLFWFramebufferSizeCallback fbSize;
	
	public Window(int width, int height){
		window = -1L;
		this.width = width;
		this.height = height;
		xpos = ypos = fbWidth = fbHeight = 0;
		cursorX = cursorY = prevX = prevY = 0.0;
		windowHints = new HashMap<WindowHint, Integer>();
	}
	
	/**
	 * Initializes the windowing and opengl libraries and creates the window
	 * 
	 * @param title Title of the window
	 * @param isFullscreen Indicates whether the window should start out as fullscreen or windowed mode
	 */
	public void init(String title, boolean isFullscreen){
		// Initialize GLFW
        if ( glfwInit() != GL_TRUE ){
        	throw new IllegalStateException("Unable to initialize GLFW");
        }
        
        //set the window hints before creating the window
        if(!windowHints.isEmpty()){
        	for(WindowHint hint : windowHints.keySet()){
        		glfwWindowHint(hint.type, windowHints.get(hint));
        	}
        }
        
        // Create the window
        //TODO make this so it specifies what monitor to make fullscreen for this window when adding support for multi-monitor
        
        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        width = isFullscreen ? vidmode.width() : width;
        height = isFullscreen ? vidmode.height() : height;
        window = glfwCreateWindow(width, height, title, isFullscreen ? glfwGetPrimaryMonitor() : NULL, NULL);
        //check to make sure it was created
        if ( window == NULL ){
        	throw new RuntimeException("Failed to create the GLFW window");
        }
        
        //if is not fullscreen center the window
        if(!isFullscreen){
            // Center the window
            glfwSetWindowPos(
                window,
                (vidmode.width() - width) / 2,
                (vidmode.height() - height) / 2
            );
        }
 
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(0);
        
        // Make the window visible
        show();
        
        //create OpenGL context
        GL.createCapabilities();
        
        //create the event handlers
  		//----------------------Event handler for keyboard events----------------------
  		keyboard = new KeyboardHandler(this);//create the handler for calling the right methods on the events
  		//cache the callbacks to prevent garbage collection
  		key = GLFWKeyCallback.create(keyboard);
  		charCall = GLFWCharCallback.create(keyboard);
  		charMods = GLFWCharModsCallback.create(keyboard);
  		//bind the callbacks to the window
  		glfwSetCharCallback(window, charCall);
  		glfwSetCharModsCallback(window, charMods);
  		glfwSetKeyCallback(window, key);
  		//----------------------Event handler for mouse events----------------------
  		mouse = new MouseHandler(this);//create the handler for calling the right methods on the events
  		//cache the callbacks to prevent garbage collection
  		mButton = GLFWMouseButtonCallback.create(mouse);
  		cPos = GLFWCursorPosCallback.create(mouse);
  		cEnter = GLFWCursorEnterCallback.create(mouse);
  		scroll = GLFWScrollCallback.create(mouse.scroll);
  		//bind the callbacks to the window
  		glfwSetMouseButtonCallback(window, mButton);
  		glfwSetCursorPosCallback(window, cPos);
  		glfwSetCursorEnterCallback(window, cEnter);
  		glfwSetScrollCallback(window, scroll);
  		//----------------------Event handler for window events----------------------
  		windowHandler = new WindowHandler(this);//create the handler for calling the right methods on the events
  		//cache the callbacks to prevent garbage collection
  		close = GLFWWindowCloseCallback.create(windowHandler.close);
  		icon = GLFWWindowIconifyCallback.create(windowHandler.iconify);
  		refresh = GLFWWindowRefreshCallback.create(windowHandler.refresh);
  		wPos = GLFWWindowPosCallback.create(windowHandler.pos);
  		wSize = GLFWWindowSizeCallback.create(windowHandler.resize);
  		focus = GLFWWindowFocusCallback.create(windowHandler.focus);
  		fbSize = GLFWFramebufferSizeCallback.create(windowHandler.framebuffer);
  		//bind the callbacks to the window
  		glfwSetWindowCloseCallback(window, close);
  		glfwSetWindowIconifyCallback(window, icon);
  		glfwSetWindowRefreshCallback(window, refresh);
  		glfwSetWindowPosCallback(window, wPos);
  		glfwSetWindowSizeCallback(window, wSize);
  		glfwSetWindowFocusCallback(window, focus);
  		glfwSetFramebufferSizeCallback(window, fbSize);
	}
	
	/**
	 * Starts the main loop for the system to run
	 * 
	 * @param control Scene object that will utilize the window and render, perform logic, and handle input events
	 */
	public void launch(Scene control){
		//while the user has not attempted to close the window keep looping
		while ( glfwWindowShouldClose(window) == GL_FALSE ) {
            control.computeScene(this);
//            glfwSwapInterval(0);
 
            glfwSwapBuffers(window); // swap the color buffers
 
            // Poll for window events
            glfwPollEvents();
        }
		control.cleanup();
		terminate();
	}
	
	/**
	 * Destroys this window and its resources
	 */
	public void destroy(){
		glfwDestroyWindow(window);
	}
	
	/**
	 * Terminates the entire underlying windowing system
	 */
	public static void terminate(){
		glfwTerminate();
	}
	
	/**
	 * Adds a keyboard input callback, for this window, to the given event handling class
	 * 
	 * @param callback Event handling class that implements the functions called when certain events fire from the window keyboard input
	 */
	public void addKeyboardListener(KeyListener callback){
		keyboard.addListener(callback);
	}
	
	/**
	 * Removes the given KeyListener from the this Windows keyboard event handler.
	 * 
	 * @param callback Callback to remove
	 */
	public void removeKeyboardListener(KeyListener callback){
		keyboard.removeListener(callback);
	}
	
	/**
	 * Adds a mouse input callback, for this window, to the given event handling class
	 * 
	 * @param callback Event handling class that implements the functions called when certain events fire from the window mouse input
	 */
	public void addMouseListener(MouseListener callback){
		mouse.addListener(callback);
	}

	/**
	 * Removes the given MouseListener from the this Windows mouse event handler.
	 * 
	 * @param callback Callback to remove
	 */
	public void removeMouseListener(MouseListener callback){
		mouse.removeListener(callback);
	}

	/**
	 * Adds a window event callback, for this window, to the given event handling class
	 * 
	 * @param callback Event handling class that implements the functions called when certain events fire from the window
	 */
	public void addWindowListener(WindowListener callback){
		windowHandler.addListener(callback);
	}

	/**
	 * Removes the given WindowListener from the this Windows window event handler.
	 * 
	 * @param callback Callback to remove
	 */
	public void removeWindowListener(WindowListener callback){
		windowHandler.removeListener(callback);
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
	 * Sets a hint for this window to be created with the given value
	 * 
	 * @param hint Hint enum indicating what parameter to set
	 * @param value Value to set the given hint to
	 */
	public void setHint(WindowHint hint, int value){
		windowHints.put(hint, value);
	}

	/**
	 * Sets a hint for this window to be created with the given value
	 * 
	 * @param hint Hint enum indicating what parameter to set
	 * @param value Value to set the given hint to
	 */
	public void setHint(WindowHint hint, boolean value){
		windowHints.put(hint, value ? GL_TRUE : GL_FALSE);
	}

	/**
	 * Sets a hint for this window to be created with the given value
	 * 
	 * @param hint Hint enum indicating what parameter to set
	 * @param value Value to set the given hint to
	 */
	public void setHint(WindowHint hint, HintConstant value){
		windowHints.put(hint, value.type);
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
		Callbacks.glfwReleaseCallbacks(window);//release the callbacks
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
	
	/**
	 * Gets the width of this window
	 * 
	 * @return Width of this window in pixels
	 */
	public int getWidth(){
		return width;
	}
	
	/**
	 * Gets the height of this window
	 * 
	 * @return Height of this window in pixels
	 */
	public int getHeight(){
		return height;
	}
	
	/**
	 * Gets the x position of this window relative to the monitor
	 * 
	 * @return X position of this window relative to the current monitor
	 */
	public int getXPosition(){
		return xpos;
	}

	/**
	 * Gets the y position of this window relative to the monitor
	 * 
	 * @return Y position of this window relative to the current monitor
	 */
	public int getYPosition(){
		return ypos;
	}
	
	/**
	 * Hides the cursor when the mouse enters the window
	 */
	public void hideCursor(){
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
	}
	
	/**
	 * Displays the cursor when the mouse is inside the window
	 */
	public void showCursor(){
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}
	
	/**
	 * Locks the cursor and hides it, cursor inputs will still be read and passed to the respective callback
	 * however the cursor will never leave the window and will remain hidden. This effectively allows the cursor
	 * to move in an "infinite" space, which is useful for mouse based camera movements.
	 */
	public void lockCursor(){
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}
	
	/**
	 * Unlocks the cursor and returns it to normal function
	 */
	public void unlockCursor(){
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}
	
	/**
	 * Determines whether the cursor has been locked
	 * 
	 * @return True if the cursor is in a locked state
	 */
	public boolean isCursorLocked(){
		return glfwGetInputMode(window, GLFW_CURSOR) == GLFW_CURSOR_DISABLED;
	}
	
	/**
	 * Determines whether the cursor is hidden
	 * 
	 * @return True if the cursor is hidden
	 */
	public boolean isCursorHidden(){
		return glfwGetInputMode(window, GLFW_CURSOR) == GLFW_CURSOR_HIDDEN || isCursorLocked();
	}
	
	/**
	 * Gets the width of the framebuffer for this window
	 * 
	 * @return Width of the framebuffer for this window
	 */
	public int getFramebufferWidth(){
		return fbWidth;
	}
	
	/**
	 * Gets the height of the framebuffer for this window
	 * 
	 * @return Height of the framebuffer for this window
	 */
	public int getFramebufferHeight(){
		return fbHeight;
	}
	
	/**
	 * Gets the current x position of the cursor
	 * 
	 * @return Double precision x position of the cursor
	 */
	public double getCursorX(){
		return cursorX;
	}

	/**
	 * Gets the current y position of the cursor
	 * 
	 * @return Double precision y position of the cursor
	 */
	public double getCursorY(){
		return cursorY;
	}
	
	/**
	 * Gets the previous x position of the cursor
	 * 
	 * @return Double precision x position of the previous cursor value
	 */
	public double getPrevCursorX(){
		return prevX;
	}

	/**
	 * Gets the previous y position of the cursor
	 * 
	 * @return Double precision y position of the previous cursor value
	 */
	public double getPrevCursorY(){
		return prevY;
	}
}
