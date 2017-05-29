package fonts;

import java.util.ArrayList;

import mesh.curve.BezierCurve;
import mesh.curve.BezierMesh;
import mesh.curve.BezierPath;

public class Glyph {

	public ArrayList<BezierPath> contours;
	public ArrayList<BezierMesh> renderables;
	private ArrayList<Glyph> subGlyphs;
	
	/**
	 * Constructs the storage for a glyph object based on the given number of contours or curves {@code numContours}
	 * 
	 * @param numContours Number of contours or curves this glyph uses in its rendering
	 */
	public Glyph(int numContours) {
		contours = new ArrayList<BezierPath>(numContours);
		renderables = new ArrayList<BezierMesh>(numContours);
		subGlyphs = new ArrayList<Glyph>();
	}

	/**
	 * Adds the given path contour to this glyph to be used in its rendering
	 * 
	 * @param contour Contour formed in parsing the glyph, to be used in rendering the shape of the glyph
	 */
	public void add(BezierPath contour){
		contours.add(contour);
		int count = 0;
		for(BezierCurve curve : contour.getCurves()){
			renderables.add(new BezierMesh(curve));
//			if(count == contour.getCurves().size()-1){
//				renderables.get(renderables.size()-1).setMaterial("path");
//			}else if(count == contour.getCurves().size()-2){
//				renderables.get(renderables.size()-1).setMaterial("sec");
//			}
			count++;
		}
	}
	
	public void add(Glyph glyph){
		subGlyphs.add(glyph);
	}
}
