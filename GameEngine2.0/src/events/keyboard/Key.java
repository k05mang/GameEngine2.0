package events.keyboard;
import static org.lwjgl.glfw.GLFW.*;

public enum Key {
	
	_0(GLFW_KEY_0),
	_1(GLFW_KEY_1),
	_2(GLFW_KEY_2),
	_3(GLFW_KEY_3),
	_4(GLFW_KEY_4),
	_5(GLFW_KEY_5),
	_6(GLFW_KEY_6),
	_7(GLFW_KEY_7),
	_8(GLFW_KEY_8),
	_9(GLFW_KEY_9),
	A(GLFW_KEY_A),
	APOSTROPHE(GLFW_KEY_APOSTROPHE),
	B(GLFW_KEY_B),
	BACKSLASH(GLFW_KEY_BACKSLASH),
	BACKSPACE(GLFW_KEY_BACKSPACE),
	C(GLFW_KEY_C),
	CAPS_LOCK(GLFW_KEY_CAPS_LOCK),
	COMMA(GLFW_KEY_COMMA),
	D(GLFW_KEY_D),
	DELETE(GLFW_KEY_DELETE),
	DOWN(GLFW_KEY_DOWN),
	E(GLFW_KEY_E),
	END(GLFW_KEY_END),
	ENTER(GLFW_KEY_ENTER),
	EQUAL(GLFW_KEY_EQUAL),
	ESCAPE(GLFW_KEY_ESCAPE),
	F(GLFW_KEY_F),
	F1(GLFW_KEY_F1),
	F10(GLFW_KEY_F10),
	F11(GLFW_KEY_F11),
	F12(GLFW_KEY_F12),
	F13(GLFW_KEY_F13),
	F14(GLFW_KEY_F14),
	F15(GLFW_KEY_F15),
	F16(GLFW_KEY_F16),
	F17(GLFW_KEY_F17),
	F18(GLFW_KEY_F18),
	F19(GLFW_KEY_F19),
	F2(GLFW_KEY_F2),
	F20(GLFW_KEY_F20),
	F21(GLFW_KEY_F21),
	F22(GLFW_KEY_F22),
	F23(GLFW_KEY_F23),
	F24(GLFW_KEY_F24),
	F25(GLFW_KEY_F25),
	F3(GLFW_KEY_F3),
	F4(GLFW_KEY_F4),
	F5(GLFW_KEY_F5),
	F6(GLFW_KEY_F6),
	F7(GLFW_KEY_F7),
	F8(GLFW_KEY_F8),
	F9(GLFW_KEY_F9),
	G(GLFW_KEY_G),
	GRAVE_ACCENT(GLFW_KEY_GRAVE_ACCENT),
	H(GLFW_KEY_H),
	HOME(GLFW_KEY_HOME),
	I(GLFW_KEY_I),
	INSERT(GLFW_KEY_INSERT),
	J(GLFW_KEY_J),
	K(GLFW_KEY_K),
	KP_0(GLFW_KEY_KP_0),
	KP_1(GLFW_KEY_KP_1),
	KP_2(GLFW_KEY_KP_2),
	KP_3(GLFW_KEY_KP_3),
	KP_4(GLFW_KEY_KP_4),
	KP_5(GLFW_KEY_KP_5),
	KP_6(GLFW_KEY_KP_6),
	KP_7(GLFW_KEY_KP_7),
	KP_8(GLFW_KEY_KP_8),
	KP_9(GLFW_KEY_KP_9),
	KP_ADD(GLFW_KEY_KP_ADD),
	KP_DECIMAL(GLFW_KEY_KP_DECIMAL),
	KP_DIVIDE(GLFW_KEY_KP_DIVIDE),
	KP_ENTER(GLFW_KEY_KP_ENTER),
	KP_EQUAL(GLFW_KEY_KP_EQUAL),
	KP_MULTIPLY(GLFW_KEY_KP_MULTIPLY),
	KP_SUBTRACT(GLFW_KEY_KP_SUBTRACT),
	L(GLFW_KEY_L),
	LAST(GLFW_KEY_LAST),
	LEFT(GLFW_KEY_LEFT),
	LEFT_ALT(GLFW_KEY_LEFT_ALT),
	LEFT_BRACKET(GLFW_KEY_LEFT_BRACKET),
	LEFT_CONTROL(GLFW_KEY_LEFT_CONTROL),
	LEFT_SHIFT(GLFW_KEY_LEFT_SHIFT),
	LEFT_SUPER(GLFW_KEY_LEFT_SUPER),
	M(GLFW_KEY_M),
	MENU(GLFW_KEY_MENU),
	MINUS(GLFW_KEY_MINUS),
	N(GLFW_KEY_N),
	NUM_LOCK(GLFW_KEY_NUM_LOCK),
	O(GLFW_KEY_O),
	P(GLFW_KEY_P),
	PAGE_DOWN(GLFW_KEY_PAGE_DOWN),
	PAGE_UP(GLFW_KEY_PAGE_UP),
	PAUSE(GLFW_KEY_PAUSE),
	PERIOD(GLFW_KEY_PERIOD),
	PRINT_SCREEN(GLFW_KEY_PRINT_SCREEN),
	Q(GLFW_KEY_Q),
	R(GLFW_KEY_R),
	RIGHT(GLFW_KEY_RIGHT),
	RIGHT_ALT(GLFW_KEY_RIGHT_ALT),
	RIGHT_BRACKET(GLFW_KEY_RIGHT_BRACKET),
	RIGHT_CONTROL(GLFW_KEY_RIGHT_CONTROL),
	RIGHT_SHIFT(GLFW_KEY_RIGHT_SHIFT),
	RIGHT_SUPER(GLFW_KEY_RIGHT_SUPER),
	S(GLFW_KEY_S),
	SCROLL_LOCK(GLFW_KEY_SCROLL_LOCK),
	SEMICOLON(GLFW_KEY_SEMICOLON),
	SLASH(GLFW_KEY_SLASH),
	SPACE(GLFW_KEY_SPACE),
	T(GLFW_KEY_T),
	TAB(GLFW_KEY_TAB),
	U(GLFW_KEY_U),
	UNKNOWN(GLFW_KEY_UNKNOWN),
	UP(GLFW_KEY_UP),
	V(GLFW_KEY_V),
	W(GLFW_KEY_W),
	WORLD_1(GLFW_KEY_WORLD_1),
	WORLD_2(GLFW_KEY_WORLD_2),
	X(GLFW_KEY_X),
	Y(GLFW_KEY_Y),
	Z(GLFW_KEY_Z);

	public final int value;
	
	private Key(int type){
		value = type;
	}
	
	protected static Key getKey(int type){
		switch(type){
			case GLFW_KEY_0:
				return _0;
			case GLFW_KEY_1:
				return _1;
			case GLFW_KEY_2:
				return _2;
			case GLFW_KEY_3:
				return _3;
			case GLFW_KEY_4:
				return _4;
			case GLFW_KEY_5:
				return _5;
			case GLFW_KEY_6:
				return _6;
			case GLFW_KEY_7:
				return _7;
			case GLFW_KEY_8:
				return _8;
			case GLFW_KEY_9:
				return _9;
			case GLFW_KEY_A:
				return A;
			case GLFW_KEY_APOSTROPHE:
				return APOSTROPHE;
			case GLFW_KEY_B:
				return B;
			case GLFW_KEY_BACKSLASH:
				return BACKSLASH;
			case GLFW_KEY_BACKSPACE:
				return BACKSPACE;
			case GLFW_KEY_C:
				return C;
			case GLFW_KEY_CAPS_LOCK:
				return CAPS_LOCK;
			case GLFW_KEY_COMMA:
				return COMMA;
			case GLFW_KEY_D:
				return D;
			case GLFW_KEY_DELETE:
				return DELETE;
			case GLFW_KEY_DOWN:
				return DOWN;
			case GLFW_KEY_E:
				return E;
			case GLFW_KEY_END:
				return END;
			case GLFW_KEY_ENTER:
				return ENTER;
			case GLFW_KEY_EQUAL:
				return EQUAL;
			case GLFW_KEY_ESCAPE:
				return ESCAPE;
			case GLFW_KEY_F:
				return F;
			case GLFW_KEY_F1:
				return F1;
			case GLFW_KEY_F10:
				return F10;
			case GLFW_KEY_F11:
				return F11;
			case GLFW_KEY_F12:
				return F12;
			case GLFW_KEY_F13:
				return F13;
			case GLFW_KEY_F14:
				return F14;
			case GLFW_KEY_F15:
				return F15;
			case GLFW_KEY_F16:
				return F16;
			case GLFW_KEY_F17:
				return F17;
			case GLFW_KEY_F18:
				return F18;
			case GLFW_KEY_F19:
				return F19;
			case GLFW_KEY_F2:
				return F2;
			case GLFW_KEY_F20:
				return F20;
			case GLFW_KEY_F21:
				return F21;
			case GLFW_KEY_F22:
				return F22;
			case GLFW_KEY_F23:
				return F23;
			case GLFW_KEY_F24:
				return F24;
			case GLFW_KEY_F25:
				return F25;
			case GLFW_KEY_F3:
				return F3;
			case GLFW_KEY_F4:
				return F4;
			case GLFW_KEY_F5:
				return F5;
			case GLFW_KEY_F6:
				return F6;
			case GLFW_KEY_F7:
				return F7;
			case GLFW_KEY_F8:
				return F8;
			case GLFW_KEY_F9:
				return F9;
			case GLFW_KEY_G:
				return G;
			case GLFW_KEY_GRAVE_ACCENT:
				return GRAVE_ACCENT;
			case GLFW_KEY_H:
				return H;
			case GLFW_KEY_HOME:
				return HOME;
			case GLFW_KEY_I:
				return I;
			case GLFW_KEY_INSERT:
				return INSERT;
			case GLFW_KEY_J:
				return J;
			case GLFW_KEY_K:
				return K;
			case GLFW_KEY_KP_0:
				return KP_0;
			case GLFW_KEY_KP_1:
				return KP_1;
			case GLFW_KEY_KP_2:
				return KP_2;
			case GLFW_KEY_KP_3:
				return KP_3;
			case GLFW_KEY_KP_4:
				return KP_4;
			case GLFW_KEY_KP_5:
				return KP_5;
			case GLFW_KEY_KP_6:
				return KP_6;
			case GLFW_KEY_KP_7:
				return KP_7;
			case GLFW_KEY_KP_8:
				return KP_8;
			case GLFW_KEY_KP_9:
				return KP_9;
			case GLFW_KEY_KP_ADD:
				return KP_ADD;
			case GLFW_KEY_KP_DECIMAL:
				return KP_DECIMAL;
			case GLFW_KEY_KP_DIVIDE:
				return KP_DIVIDE;
			case GLFW_KEY_KP_ENTER:
				return KP_ENTER;
			case GLFW_KEY_KP_EQUAL:
				return KP_EQUAL;
			case GLFW_KEY_KP_MULTIPLY:
				return KP_MULTIPLY;
			case GLFW_KEY_KP_SUBTRACT:
				return KP_SUBTRACT;
			case GLFW_KEY_L:
				return L;
			case GLFW_KEY_LAST:
				return LAST;
			case GLFW_KEY_LEFT:
				return LEFT;
			case GLFW_KEY_LEFT_ALT:
				return LEFT_ALT;
			case GLFW_KEY_LEFT_BRACKET:
				return LEFT_BRACKET;
			case GLFW_KEY_LEFT_CONTROL:
				return LEFT_CONTROL;
			case GLFW_KEY_LEFT_SHIFT:
				return LEFT_SHIFT;
			case GLFW_KEY_LEFT_SUPER:
				return LEFT_SUPER;
			case GLFW_KEY_M:
				return M;
//			case GLFW_KEY_MENU:
//				return MENU;
			case GLFW_KEY_MINUS:
				return MINUS;
			case GLFW_KEY_N:
				return N;
			case GLFW_KEY_NUM_LOCK:
				return NUM_LOCK;
			case GLFW_KEY_O:
				return O;
			case GLFW_KEY_P:
				return P;
			case GLFW_KEY_PAGE_DOWN:
				return PAGE_DOWN;
			case GLFW_KEY_PAGE_UP:
				return PAGE_UP;
			case GLFW_KEY_PAUSE:
				return PAUSE;
			case GLFW_KEY_PERIOD:
				return PERIOD;
			case GLFW_KEY_PRINT_SCREEN:
				return PRINT_SCREEN;
			case GLFW_KEY_Q:
				return Q;
			case GLFW_KEY_R:
				return R;
			case GLFW_KEY_RIGHT:
				return RIGHT;
			case GLFW_KEY_RIGHT_ALT:
				return RIGHT_ALT;
			case GLFW_KEY_RIGHT_BRACKET:
				return RIGHT_BRACKET;
			case GLFW_KEY_RIGHT_CONTROL:
				return RIGHT_CONTROL;
			case GLFW_KEY_RIGHT_SHIFT:
				return RIGHT_SHIFT;
			case GLFW_KEY_RIGHT_SUPER:
				return RIGHT_SUPER;
			case GLFW_KEY_S:
				return S;
			case GLFW_KEY_SCROLL_LOCK:
				return SCROLL_LOCK;
			case GLFW_KEY_SEMICOLON:
				return SEMICOLON;
			case GLFW_KEY_SLASH:
				return SLASH;
			case GLFW_KEY_SPACE:
				return SPACE;
			case GLFW_KEY_T:
				return T;
			case GLFW_KEY_TAB:
				return TAB;
			case GLFW_KEY_U:
				return U;
			case GLFW_KEY_UNKNOWN:
				return UNKNOWN;
			case GLFW_KEY_UP:
				return UP;
			case GLFW_KEY_V:
				return V;
			case GLFW_KEY_W:
				return W;
			case GLFW_KEY_WORLD_1:
				return WORLD_1;
			case GLFW_KEY_WORLD_2:
				return WORLD_2;
			case GLFW_KEY_X:
				return X;
			case GLFW_KEY_Y:
				return Y;
			case GLFW_KEY_Z:
				return Z;
		}
		return null;
	}
}
