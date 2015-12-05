package events.keyboard;
import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;

public enum ModKey {
	ALT(GLFW_MOD_ALT),
	CTRL(GLFW_MOD_CONTROL),
	SHIFT(GLFW_MOD_SHIFT),
	SUPER(GLFW_MOD_SUPER);
	
	public final int value;
	
	private ModKey(int type){
		value = type;
	}
	
	public static ModKey[] getMods(int type){
		ArrayList<ModKey> mods = new ArrayList<ModKey>();
		/*since the value of the mod variable created from the callback methods is a single int
		with the different types bitwise OR together this means that the bit related to that particular 
		key will be set to 1, by an AND operation we should get 0 if the value isn't in the int passed
		or non zero if it was set
		*/
		if((GLFW_MOD_ALT & type) != 0){
			mods.add(ALT);
		}
		
		if((GLFW_MOD_CONTROL & type) != 0){
			mods.add(CTRL);
		}
		
		if((GLFW_MOD_SHIFT & type) != 0){
			mods.add(SHIFT);
		}
		
		if((GLFW_MOD_SUPER & type) != 0){
			mods.add(SUPER);
		}
		return mods.toArray(new ModKey[]{});
	}
}
