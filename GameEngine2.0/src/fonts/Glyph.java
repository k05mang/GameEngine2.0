package fonts;

import java.util.ArrayList;
import mesh.curve.BezierPath;

public class Glyph {

	private ArrayList<BezierPath> curves;
	private ArrayList<Glyph> subGlyphs;
	
	public Glyph() {
		curves = new ArrayList<BezierPath>();
		subGlyphs = new ArrayList<Glyph>();
	}

	public void add(BezierPath curve){
		curves.add(curve);
	}
	
	public void add(Glyph glyph){
		subGlyphs.add(glyph);
	}
}
