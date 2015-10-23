package events;

public interface KeyEvent {
	public void keyPress(long window, int key, boolean isRepeat, int mods);
	
	public void keyRelease(long window, int key, int mods);
}
