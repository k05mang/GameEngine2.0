package core;
import windowing.Window;

public interface Scene {
	public void computeScene(Window window);
	
	public void cleanup();
	
	public void launch();
}
