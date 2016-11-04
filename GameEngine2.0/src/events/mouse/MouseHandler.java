package events.mouse;

import static org.lwjgl.opengl.GL11.GL_TRUE;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import windowing.Window;
import events.keyboard.ModKey;

public class MouseHandler implements 
GLFWMouseButtonCallback.SAM,
GLFWCursorPosCallback.SAM,
GLFWCursorEnterCallback.SAM{
	
	private ArrayList<MouseListener> mouseListeners;
	private Window window;
	public final ScrollHandler scroll;
	
	/**
	 * Constructs a MouseHandler responsible for handling input events related to the mouse of the {@code curWindow}
	 * window context.
	 * 
	 * @param curWindow Current context to pass mouse input events to the listeners
	 */
	public MouseHandler(Window curWindow){
		scroll = new ScrollHandler();
		mouseListeners = new ArrayList<MouseListener>();
		window = curWindow;
	}

	/**
	 * Adds a MouseListener object to this mouse handler. The listener will receive any events fired by the 
	 * window related to the mouse.
	 * 
	 * @param listener MouseListener object to attach to this handler
	 */
	public void addListener(MouseListener listener){
		mouseListeners.add(listener);
	}

	/**
	 * Removes a MouseListener from this mouse handler. The listener will be completely removed from this handler.
	 * The listener will no longer receive events fired by the window context this handler is associated with.
	 * 
	 * @param listener MouseListener to remove from this handler
	 */
	public void removeListener(MouseListener listener){
		mouseListeners.remove(listener);
	}

	@Override
	public void invoke(long windowHandle, int entered) {
		//iterate over all the active listeners
		for(MouseListener listener : mouseListeners){
			listener.mouseEnter(window, entered == GL_TRUE);
		}
	}

	@Override
	public void invoke(long windowHandle, double xpos, double ypos) {
		window.cursorX = xpos;
		window.cursorY = ypos;
		//iterate over all the active listeners
		for(MouseListener listener : mouseListeners){
			listener.mouseMove(window, xpos, ypos);
		}
	}

	@Override
	public void invoke(long windowHandle, int button, int action, int mods) {
		//iterate over all the active listeners
		for(MouseListener listener : mouseListeners){
			if(action == GLFW.GLFW_PRESS){
				listener.mousePress(window, MouseButton.getButton(button), false, ModKey.getMods(mods));
			}else if(action == GLFW.GLFW_REPEAT){
				listener.mousePress(window,  MouseButton.getButton(button), true, ModKey.getMods(mods));
			}else{
				listener.mouseRelease(window,  MouseButton.getButton(button), ModKey.getMods(mods));
			}
		}
	}
	
	public class ScrollHandler implements 
	GLFWScrollCallback.SAM{

		@Override
		public void invoke(long windowHandle, double xoffset, double yoffset) {
			//iterate over all the active listeners
			for(MouseListener listener : mouseListeners){
				listener.mouseScroll(window, xoffset, yoffset);
			}
		}
		
	}
}
