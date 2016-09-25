package module3;

//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Filip Dostál
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap_fd extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	//private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.atom";
// http://earthquake.usgs.gov/earthquakes/feed/v1.0/atom.php RSS zdroj je mozno zmenit zdroj dat
	
	public void setup() {
		size(1175, 875, OPENGL);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 950, 800, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 950, 800, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "2.5_week.atom";
			
		}
		
		//map.zoomAndPanTo(10, new Location(49.59f, 17.25f));
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    // These print statements show you (1) all of the relevant properties 
	    // in the features, and (2) how to get one property and use it
	    
	    for (int j = 0; j < earthquakes.size(); j++) {
			PointFeature i = earthquakes.get(j);
			//if (earthquakes.size() > 0) {
				//PointFeature f = earthquakes.get(0);
				System.out.println(i.getProperties());
				Object magObj = i.getProperty("magnitude");
				float mag = Float.parseFloat(magObj.toString());
				// PointFeatures also have a getLocation method
				
				SimplePointMarker pozice = createMarker(i);
				
				if(mag < 3.0){
					int grey = color(150, 150, 150);
					pozice.setColor(grey);
					pozice.setRadius((float) 10.0);					
				}
				
				if(mag >= 3.0 && mag < 4.0){
					int green = color(10, 255, 10);
					pozice.setColor(green);
					pozice.setRadius((float) 12.5);
					
				}
				if(mag >= 4.0 && mag < 4.5){
					int yellow = color(255, 255, 10);
					pozice.setColor(yellow);
					pozice.setRadius((float) 15.0);
				}
				
				if(mag >= 4.5 && mag < 5.0){
					int orange = color(255, 208, 10);
					pozice.setColor(orange);
					pozice.setRadius((float) 17.5);
				}

				if(mag >= 5.0){	
					int red = color(204, 0, 0);
					pozice.setColor(red);
					pozice.setRadius((float) 20.0);
				}
				
				
				map.addMarker(pozice);
		}
	    
	    // Here is an example of how to use Processing's color method to generate 
	    // an int that represents the color yellow.  
	   // int yellow = color(255, 255, 0);
	    
	    //TODO: Add code here as appropriate

	}
		


	// A suggested helper method that takes in an earthquake feature and 
	// returns a SimplePointMarker for that earthquake
	// TODO: Implement this method and call it from setUp, if it helps
	private SimplePointMarker createMarker(PointFeature feature)
	{
		// finish implementing and use this method, if it helps.
		return new SimplePointMarker(feature.getLocation());
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}


	// helper method to draw key in GUI
	// TODO: Implement this method to draw the key
	private void addKey() 
	{	
		
		
		fill(10);
		textSize(20);	
		fill(255,255,255);
		text("Key Legend", 40, 30); //90
		text("Zemìtøesení vìtší než 2.5 Magnitude za posldních 7 dní", 200, 30);
		
		
		rect(25,50,150,800,10);
		
		
		fill(150, 150, 150);
		ellipse( 40, 80-5, 10, 10);
		textSize(12);
		fill(10);
		text("Magnitude < 3", 60, 80);
		
		fill(10, 255, 10);
		ellipse( 40, (float) (110-6.5), 13, 13);
		textSize(12);
		fill(10);
		text("Magnitude 3-4 ", 60, 110);
		
		fill(255, 255, 10);
		ellipse( 40, 140-8, 16, 16);
		textSize(12);
		fill(10);
		text("Magnitude 4-4.5 ", 60, 140);
		
		fill(255, 208, 10);
		ellipse( 40, (float) (170-9.5), 18, 18);
		textSize(12);
		fill(10);
		text("Magnitude 4.5-5 ", 60, 170);
		
		fill(204, 0, 0);
		ellipse( 40, 200-10, 20, 20);
		textSize(12);
		fill(10);
		text("Magnitude < 5 ", 60, 200);
		
		// Remember you can use Processing's graphics methods here
		
		
	
	}
}
