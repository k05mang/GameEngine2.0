package events;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.glfw.GLFWWindowIconifyCallback;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowRefreshCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;

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
	private WindowEvent eventHandle;
	
	/**
	 * Creates a handler for window events using the given WindowEvent object to call function callbacks
	 * when window events are fired
	 * 
	 * @param handler WindowEvent object to call functions when certain events fire
	 */
	public WindowHandler(WindowEvent handler, Window window){
		iconify = new IconifyHandler();
		resize = new ResizeHandler();
		refresh = new RefreshHandler();
		focus = new FocusHandler();
		close = new CloseHandler();
		pos = new PosHandler();
		framebuffer = new FramebufferSizeHandler();
		
		eventHandle = handler;
		this.window = window;
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
			if(iconified == GL_TRUE){
				eventHandle.windowIconify(window);
			}else{
				eventHandle.windowRestore(window);
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
			eventHandle.windowResize(window, width, height);
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
			eventHandle.windowRefresh(window);
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
			eventHandle.windowFocus(window, (focused == GL_TRUE ? true : false));
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
			eventHandle.windowClose(window);
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
			eventHandle.windowPosChange(window, xpos, ypos);
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
			eventHandle.frameBufferResize(window,  width,  height);
		}
		
	}

}
