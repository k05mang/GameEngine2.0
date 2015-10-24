package events;
import org.lwjgl.glfw.*;
import core.Window;

public class EventHandler implements 
GLFWMouseButtonCallback.SAM,
GLFWScrollCallback.SAM,
GLFWCursorPosCallback.SAM,
GLFWCursorEnterCallback.SAM,
GLFWFramebufferSizeCallback.SAM
{
	private KeyEvent keyboard;
	private MouseEvent mouse;
	private WindowEvent windowEvent;
	private Window window;
	
	public EventHandler(Window window){
		this.window = window;
		keyboard = null;
		mouse = null;
		windowEvent = null;
	}

	@Override
	public void invoke(long window) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invoke(long window, int codepoint, int mods) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invoke(long window, int entered) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invoke(long window, double xoffset, double yoffset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invoke(long window, int button, int action, int mods) {
		// TODO Auto-generated method stub
		
	}

}
