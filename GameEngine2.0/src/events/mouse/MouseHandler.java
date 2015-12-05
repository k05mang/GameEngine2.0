package events.mouse;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorEnterCallback;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import events.keyboard.ModKey;
import windowing.Window;
import static org.lwjgl.opengl.GL11.GL_TRUE;

public class MouseHandler implements 
GLFWMouseButtonCallback.SAM,
GLFWCursorPosCallback.SAM,
GLFWCursorEnterCallback.SAM{
	
	private MouseEvent mouse;
	private Window window;
	public final ScrollHandler scroll;
	
	public MouseHandler(MouseEvent eventHandle, Window curWindow){
		scroll = new ScrollHandler();
		mouse = eventHandle;
		window = curWindow;
	}

	@Override
	public void invoke(long windowHandle, int entered) {
		mouse.mouseEnter(window, entered == GL_TRUE ? true : false);
	}

	@Override
	public void invoke(long windowHandle, double xpos, double ypos) {
		window.cursorX = xpos;
		window.cursorY = ypos;
		mouse.mouseMove(window, xpos, ypos);
	}

	@Override
	public void invoke(long windowHandle, int button, int action, int mods) {
		if(action == GLFW.GLFW_PRESS){
			mouse.mousePress(window, MouseButton.getButton(button), false, ModKey.getMods(mods));
		}else if(action == GLFW.GLFW_REPEAT){
			mouse.mousePress(window,  MouseButton.getButton(button), true, ModKey.getMods(mods));
		}else{
			mouse.mouseRelease(window,  MouseButton.getButton(button), ModKey.getMods(mods));
		}
	}
	
	public class ScrollHandler implements 
	GLFWScrollCallback.SAM{

		@Override
		public void invoke(long windowHandle, double xoffset, double yoffset) {
			mouse.mouseScroll(window, xoffset, yoffset);
		}
		
	}
}
