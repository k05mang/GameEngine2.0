package fonts;

import java.util.ArrayList;

import mesh.curve.BezierMesh;
import mesh.curve.BezierCurve;

public class Glyph {

	public ArrayList<BezierCurve> curves;
	public ArrayList<BezierMesh> renderables;
	private ArrayList<Glyph> subGlyphs;
	
	/**
	 * Constructs the storage for a glyph object based on the given number of contours or curves {@code numContours}
	 * 
	 * @param numContours Number of contours or curves this glyph uses in its rendering
	 */
	public Glyph(int numContours) {
		curves = new ArrayList<BezierCurve>(numContours);
		renderables = new ArrayList<BezierMesh>(numContours);
		subGlyphs = new ArrayList<Glyph>();
	}

	/**
	 * Adds the given curve to this glyph to be used in its rendering
	 * 
	 * @param contour Contour formed in parsing the glyph, to be used in rendering the shape of the glyph
	 */
	public void add(BezierCurve contour){
		curves.add(contour);
		renderables.add(new BezierMesh(contour));
	}
	
	public void add(Glyph glyph){
		subGlyphs.add(glyph);
	}
}
