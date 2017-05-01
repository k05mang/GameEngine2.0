package fonts;

import java.util.ArrayList;

import mesh.curve.BezierCurve;
import mesh.curve.BezierPath;

public class Glyph {

	private ArrayList<BezierPath> curves;
	private ArrayList<BezierCurve> renderables;
	private ArrayList<Glyph> subGlyphs;
	
	/**
	 * Constructs the storage for a glyph object based on the given number of contours or curves {@code numContours}
	 * 
	 * @param numContours Number of contours or curves this glyph uses in its rendering
	 */
	public Glyph(int numContours) {
		curves = new ArrayList<BezierPath>(numContours);
		renderables = new ArrayList<BezierCurve>(numContours);
		subGlyphs = new ArrayList<Glyph>();
	}

	/**
	 * Adds the given curve to this glyph to be used in its rendering
	 * 
	 * @param contour Contour formed in parsing the glyph, to be used in rendering the shape of the glyph
	 */
	public void add(BezierPath contour){
		curves.add(contour);
		renderables.add(new BezierCurve(contour));
	}
	
	public void add(Glyph glyph){
		subGlyphs.add(glyph);
	}
}
