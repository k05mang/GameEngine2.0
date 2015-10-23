package events;

public interface MouseEvent {
	public void mousePress(long window, int button, boolean isRepeat, int mods);
	
	public void mouseRelease(long window, int button, int mods);
	
	public void mouseScroll(long window, double xoffset, double yoffset);
	
	public void mouseEnter(long window, boolean entered);
	
	public void mouseMove(long window, double xpos, double ypos);
}
