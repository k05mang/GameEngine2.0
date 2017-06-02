package windowing.events.window;

import static org.lwjgl.opengl.GL11.GL_TRUE;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowIconifyCallback;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowRefreshCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import windowing.Window;

public class WindowHandler{

	public final IconifyHandler iconify;
	public final ResizeHandler resize;
	public final RefreshHandler refresh;
	public final FocusHandler focus;
	public final CloseHandler close;
	public final PosHandler pos;
	public final FramebufferSizeHandler framebuffer;
	
	private Window window;
	private ArrayList<WindowListener> windowListeners;
	
	/**
	 * Constructs a WindowHandler responsible for handling input events related to the window of the {@code curWindow}
	 * window context.
	 * 
	 * @param curWindow Current context to pass window change events to the listeners
	 */
	public WindowHandler(Window window){
		iconify = new IconifyHandler();
		resize = new ResizeHandler();
		refresh = new RefreshHandler();
		focus = new FocusHandler();
		close = new CloseHandler();
		pos = new PosHandler();
		framebuffer = new FramebufferSizeHandler();
		
		windowListeners = new ArrayList<WindowListener>();
		this.window = window;
	}

	/**
	 * Adds a WindowListener object to this window handler. The listener will receive any events fired by the 
	 * window related to the window.
	 * 
	 * @param listener WindowListener object to attach to this handler
	 */
	public void addListener(WindowListener listener){
		windowListeners.add(listener);
	}

	/**
	 * Removes a WindowListener from this window handler. The listener will be completely removed from this handler.
	 * The listener will no longer receive events fired by the window context this handler is associated with.
	 * 
	 * @param listener WindowListener to remove from this handler
	 */
	public void removeListener(WindowListener listener){
		windowListeners.remove(listener);
	}
	
	/**
	 * Class for handling the iconify event fired by a glfw window
	 * 
	 * @author Kevin Mango
	 *
	 */
	public class IconifyHandler implements 
	GLFWWindowIconifyCallback.SAM{
		
		@Override
		public void invoke(long windowHandle, int iconified) {
			//iterate over all the active listeners
			for(WindowListener listener : windowListeners){
				if(iconified == GL_TRUE){
					listener.onWindowIconify(window);
				}else{
					listener.onWindowRestore(window);
				}
			}
		}
		
	}
	
	/**
	 * Class for handling resize events fired by a glfw window
	 * 
	 * @author Kevin Mango
	 *
	 */
	public class ResizeHandler implements 
	GLFWWindowSizeCallback.SAM{
		
		@Override
		public void invoke(long windowHandle, int width, int height) {
			window.width = width;
			window.height = height;
			//iterate over all the active listeners
			for(WindowListener listener : windowListeners){
				listener.onWindowResize(window, width, height);
			}
		}
		
	}
	
	/**
	 * Class for handling refresh events fired by a glfw window
	 * 
	 * @author Kevin Mango
	 *
	 */
	public class RefreshHandler implements 
	GLFWWindowRefreshCallback.SAM{
		
		@Override
		public void invoke(long windowHandle) {
			//iterate over all the active listeners
			for(WindowListener listener : windowListeners){
				listener.onWindowRefresh(window);
			}
		}
		
	}

	/**
	 * Class for handling focus events fired by a glfw window
	 * 
	 * @author Kevin Mango
	 *
	 */
	public class FocusHandler implements 
	GLFWWindowFocusCallback.SAM{
		
		@Override
		public void invoke(long windowHandle, int focused) {
			//iterate over all the active listeners
			for(WindowListener listener : windowListeners){
				listener.onWindowFocus(window, (focused == GL_TRUE ? true : false));
			}
		}
		
	}

	/**
	 * Class for handling close window events fired by a glfw window
	 * 
	 * @author Kevin Mango
	 *
	 */
	public class CloseHandler implements 
	GLFWWindowCloseCallback.SAM{
		
		@Override
		public void invoke(long windowHandle) {
			//iterate over all the active listeners
			for(WindowListener listener : windowListeners){
				listener.onWindowClose(window);
			}
		}
		
	}

	/**
	 * Class for handling window position change events fired by a glfw window
	 * 
	 * @author Kevin Mango
	 *
	 */
	public class PosHandler implements 
	GLFWWindowPosCallback.SAM{
		
		@Override
		public void invoke(long windowHandle, int xpos, int ypos) {
			window.xpos = xpos;
			window.ypos = ypos;
			//iterate over all the active listeners
			for(WindowListener listener : windowListeners){
				listener.onWindowPosChange(window, xpos, ypos);
			}
		}
		
	}

	/**
	 * Class for handling framebuffer size change events fired by a glfw window
	 * 
	 * @author Kevin Mango
	 *
	 */
	public class FramebufferSizeHandler implements 
	GLFWFramebufferSizeCallback.SAM{

		@Override
		public void invoke(long windowHandle, int width, int height) {
			window.fbWidth = width;
			window.fbHeight = height;
			//iterate over all the active listeners
			for(WindowListener listener : windowListeners){
				listener.onFrameBufferResize(window,  width,  height);
			}
		}
		
	}

}
