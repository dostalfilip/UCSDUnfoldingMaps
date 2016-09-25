package module4;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

/** Implements a visual marker for ocean earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 *
 */
public class OceanQuakeMarker extends EarthquakeMarker {
	
	public OceanQuakeMarker(PointFeature quake) {
		super(quake);
		
		// setting field in earthquake marker
		isOnLand = false;
	}
	

	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		// Drawing a centered square for Ocean earthquakes
		// DO NOT set the fill color.  That will be set in the EarthquakeMarker
		// class to indicate the depth of the earthquake.
		// Simply draw a centered square.
		
		// HINT: Notice the radius variable in the EarthquakeMarker class
		// and how it is set in the EarthquakeMarker constructor
		
		// TODO: Implement this method

		//pg.rect(x, y, 10, 10);
		
		
		
		
		if(getMagnitude()<4){
			pg.rect(x-5, y-5, 10, 10);
		}
		if(getMagnitude()>4 && getMagnitude()<5){
			pg.rect(x-7.5f, y-7.5f, 15, 15);
		}
		if(getMagnitude()>5){
			pg.rect(x-10, y-10, 20, 20);
		}
		
	}
	


	

}
