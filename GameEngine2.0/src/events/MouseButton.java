package events;
import static org.lwjgl.glfw.GLFW.*;

public enum MouseButton {
	_1(GLFW_MOUSE_BUTTON_1),
	_2(GLFW_MOUSE_BUTTON_2),
	_3(GLFW_MOUSE_BUTTON_3),
	_4(GLFW_MOUSE_BUTTON_4),
	_5(GLFW_MOUSE_BUTTON_5),
	_6(GLFW_MOUSE_BUTTON_6),
	_7(GLFW_MOUSE_BUTTON_7),
	_8(GLFW_MOUSE_BUTTON_8),
	LAST(GLFW_MOUSE_BUTTON_LAST),
	LEFT(GLFW_MOUSE_BUTTON_LEFT),
	MIDDLE(GLFW_MOUSE_BUTTON_MIDDLE),
	RIGHT(GLFW_MOUSE_BUTTON_RIGHT);
	
	public final int value;
	
	private MouseButton(int type){
		value = type;
	}
	
	protected static MouseButton getButton(int type){
		switch(type){
//			case GLFW_MOUSE_BUTTON_1:
//				return _1;
//			case GLFW_MOUSE_BUTTON_2:
//				return _2;
//			case GLFW_MOUSE_BUTTON_3:
//				return _3;
			case GLFW_MOUSE_BUTTON_4:
				return _4;
			case GLFW_MOUSE_BUTTON_5:
				return _5;
			case GLFW_MOUSE_BUTTON_6:
				return _6;
			case GLFW_MOUSE_BUTTON_7:
				return _7;
//			case GLFW_MOUSE_BUTTON_8:
//				return _8;
			case GLFW_MOUSE_BUTTON_LAST:
				return LAST;
			case GLFW_MOUSE_BUTTON_LEFT:
				return LEFT;
			case GLFW_MOUSE_BUTTON_MIDDLE:
				return MIDDLE;
			case GLFW_MOUSE_BUTTON_RIGHT:
				return RIGHT;

		}
		return null;
	}

}
