package module6;

import java.util.*;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;
import processing.core.PConstants;


/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet implements Comparable<EarthquakeMarker>  {
	
	// We will use member variables, instead of local variables, to store the data
	// that the setUp and draw methods will need to access (as well as other methods)
	// You will use many of these variables, but the only one you should need to add
	// code to modify is countryQuakes, where you will store the number of earthquakes
	// per country.
	
	// You can ignore this.  It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFILINE, change the value of this variable to true
	private static final boolean offline = false;
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	

	//feed with magnitude 2.5+ Earthquakes
	//private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_day.atom";
	
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_month.atom";
	//private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_month.atom";
	
	//private String earthquakesURL = "quiz2.atom"; // for test purpose
	
	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	
	// The map
	private UnfoldingMap map;
	
	// Markers for each city
	private List<Marker> cityMarkers;
	// Markers for each earthquake
	private List<Marker> quakeMarkers;

	// A List of country markers
	private List<Marker> countryMarkers;
	//private List<Marker> countryMarkers2;
	//pridano ˙pudeji
	private List<EarthquakeMarker> quakeMarkerList;
	private int pocetzemetreseni = -1;
	private String zeme = "Unselected";
	private float topshake = 0.f; // nejsilnejsi zemetreseni
	
	// NEW IN MODULE 5
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	public void setup() {		
		// (1) Initializing canvas and map tiles
		size(900, 700, OPENGL);
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 650, 600, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 650, 600, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// FOR TESTING: Set earthquakesURL to be one of the testing files by uncommenting
		// one of the lines below.  This will work whether you are online or offline
		//earthquakesURL = "test1.atom";
		//earthquakesURL = "test2.atom";
		
		// Uncomment this line to take the quiz
		//earthquakesURL = "quiz2.atom";
		
		
		// (2) Reading in earthquake data and geometric properties
	    //     STEP 1: load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		//map.addMarkers(countryMarkers);
		//System.out.println(countryMarkers.get(0).getId());
		//countryMarkers2 = new ArrayList<Marker>();
	//	for(Feature country : countries) {
		//	countryMarkers2.add(new CountryMarker((PointFeature) country));
		//	}
		
		//     STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
	    
		//     STEP 3: read in earthquake RSS feed
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    quakeMarkerList = new ArrayList<EarthquakeMarker>();	//compare Magnitude Array
	    quakeMarkers = new ArrayList<Marker>(); 				//marker on screen
	    
	    
	    for(PointFeature feature : earthquakes) {
		  //check if LandQuake
	    	
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		    quakeMarkerList.add(new LandQuakeMarker(feature)); // viscotoje
		  }
		  // OceanQuakes
		  else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		    //quakeMarkerList.add(new OceanQuakeMarker(feature)); // viscotoje
		  }
	    }
	    
	    Collections.sort(quakeMarkerList); // ono to funguje hur·······
	  
	    //System.out.println(quakeMarkerList.size()); // ohhh yeahhh  that preatty amazing DEBUG


	    

	    // could be used for debugging
	    System.out.println("\nTop 5 Earthquake");
	    printQuakesTop3();

	    System.out.println("\nCountry Earthquake");
	    printQuakes();
	 		
	    // (3) Add markers to map
	    //     NOTE: Country markers are not added to the map.  They are used
	    //           for their geometric properties
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	    
	    shadeCountries();
	    
	}  // End setup
	
	
	public void draw() {
		background(0);
		map.draw();
		addKey();
		
	}
	
	
	// TODO: Add the method:
	//   private void sortAndPrint(int numToPrint)
	// and then call that method from setUp
	
	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(quakeMarkers);
		selectMarkerIfHover(cityMarkers);
		//loop();
		
		
	}

	
	// If there is a marker selected 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		// Abort if there's already a marker selected
		if (lastSelected != null) {
			return;
		}
		
		for (Marker m : markers) 
		{
			CommonMarker marker = (CommonMarker)m;
			if (marker.isInside(map,  mouseX, mouseY)) {
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	
	/** The event handler for mouse clicks
	 * It will display an earthquake and its threat circle of cities
	 * Or if a city is clicked, it will display all the earthquakes 
	 * where the city is in the threat circle
	 */
	@Override
	public void mouseClicked()
	{
		if (lastClicked != null) {
			unhideMarkers();
			lastClicked = null;
		}
		else if (lastClicked == null) 
		{
			checkEarthquakesForClick();
			if (lastClicked == null) {
				checkCitiesForClick();
				if (lastClicked == null) {
					checkCountryForClick();
				}
			}
		}
	}
	
	
	private void checkCountryForClick()
	{
		if (lastClicked != null) return;
		// Loop over the earthquake markers to see if one of them is selected
		lastClicked = (CommonMarker) cityMarkers.get(0);
		
		for (Marker marker : countryMarkers) {
			if (marker.isInside(map, mouseX, mouseY)) {
				
				List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
				float buffer =0.0f;
				
				//nejprve najdem top pocet zemetreseni
				
					int pocet = 0;
					for (PointFeature feature : earthquakes){
						if(isInCountry(feature,marker)){
							//feature.getStringProperty()
							pocet++;	
							
							java.util.HashMap<String, Object> properties = feature.getProperties();
							float magnitude = Float.parseFloat(properties.get("magnitude").toString());
							if(magnitude> buffer){
								buffer = magnitude;}
							
					}
						
						topshake = buffer;
						pocetzemetreseni = pocet;
						zeme = "Country: " +(String) marker.getProperty("name");
						
						
					}
				
			}
		}
				
			
	}
	
	
	
	
	
	
	// Helper method that will check if a city marker was clicked on
	// and respond appropriately
	private void checkCitiesForClick()
	{
		if (lastClicked != null) return;
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker marker : cityMarkers) {
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = (CommonMarker)marker;
				// Hide all the other earthquakes and hide
				for (Marker mhide : cityMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
				for (Marker mhide : quakeMarkers) {
					EarthquakeMarker quakeMarker = (EarthquakeMarker)mhide;
					if (quakeMarker.getDistanceTo(marker.getLocation()) 
							> quakeMarker.threatCircle()) {
						quakeMarker.setHidden(true);
					}
				}
				return;
			}
		}		
	}
	
	// Helper method that will check if an earthquake marker was clicked on
	// and respond appropriately
	private void checkEarthquakesForClick()
	{
		if (lastClicked != null) return;
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker m : quakeMarkers) {
			EarthquakeMarker marker = (EarthquakeMarker)m;
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = marker;
				// Hide all the other earthquakes and hide
				for (Marker mhide : quakeMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
				for (Marker mhide : cityMarkers) {
					if (mhide.getDistanceTo(marker.getLocation()) 
							> marker.threatCircle()) {
						mhide.setHidden(true);
					}
				}
				return;
			}
		}
	}
	
	// loop over and unhide all markers
	private void unhideMarkers() {
		for(Marker marker : quakeMarkers) {
			marker.setHidden(false);
		}
			
		for(Marker marker : cityMarkers) {
			marker.setHidden(false);
		}
	}
	
	// helper method to draw key in GUI
	private void addKey() {	
		// Remember you can use Processing's graphics methods here
		fill(10);
		textSize(20);	
		fill(255,255,255);
		text("Legend", 30, 30); //90
		text("Data source: " + "...v1.0/summary/4.5_month.atom", 200, 30);
		
		
		fill(255, 250, 240);
		
		int xbase = 25;
		int ybase = 50;
		
		rect(xbase, ybase, 150, 250, 7);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(16);
		text("Earthquake Key", 35, ybase+25);
		line(25, ybase+40, 175, ybase+40);
		
		textSize(12);
		fill(150, 30, 30);
		int tri_xbase = xbase + 35;
		int tri_ybase = ybase + 50;
		triangle(tri_xbase, tri_ybase-CityMarker.TRI_SIZE, tri_xbase-CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE, tri_xbase+CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE);

		fill(0, 0, 0);
		textAlign(LEFT, CENTER);
		text("City Marker", tri_xbase + 15, tri_ybase);
		
		text("Land Quake", xbase+50, ybase+70);
		text("Ocean Quake", xbase+50, ybase+90);
		text("Size ~ Magnitude", xbase+25, ybase+110);
		
		fill(255, 255, 255);
		ellipse(xbase+35, 
				ybase+70, 
				10, 
				10);
		rect(xbase+35-5, ybase+90-5, 10, 10);
		
		fill(color(255, 255, 0));
		ellipse(xbase+35, ybase+140, 12, 12);
		fill(color(0, 0, 255));
		ellipse(xbase+35, ybase+160, 12, 12);
		fill(color(255, 0, 0));
		ellipse(xbase+35, ybase+180, 12, 12);
		
		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("Shallow", xbase+50, ybase+140);
		text("Intermediate", xbase+50, ybase+160);
		text("Deep", xbase+50, ybase+180);

		text("Past hour", xbase+50, ybase+200);
		
		fill(255, 255, 255);
		int centerx = xbase+35;
		int centery = ybase+200;
		ellipse(centerx, centery, 12, 12);

		strokeWeight(2);
		line(centerx-8, centery-8, centerx+8, centery+8);
		line(centerx-8, centery+8, centerx+8, centery-8);
		
		
		// informace o zemetreseni v konkretni zemi
		
		fill(255, 250, 240);	
		rect(25, 320, 150, 100, 7);
		
		fill(0);
		textSize(16);
		text("Country Details", 35,  335);
		line(25, 350, 175, 350);
		
		textSize(12);
		text(zeme, 35,  360);
		text("Earthquake count: "+pocetzemetreseni, 35,  380);
		
		text("TOP Earthquake: " + topshake, 35,  400);
		

	}

	
	
	// Checks whether this quake occurred on land.  If it did, it sets the 
	// "country" property of its PointFeature to the country where it occurred
	// and returns true.  Notice that the helper method isInCountry will
	// set this "country" property already.  Otherwise it returns false.
	private boolean isLand(PointFeature earthquake) {
		
		// IMPLEMENT THIS: loop over all countries to check if location is in any of them
		// If it is, add 1 to the entry in countryQuakes corresponding to this country.
		for (Marker country : countryMarkers) {
			if (isInCountry(earthquake, country)) {
				return true;
			}
		}
		
		// not inside any country
		return false;
	}
	
	// prints countries with number of earthquakes
	// You will want to loop through the country markers or country features
	// (either will work) and then for each country, loop through
	// the quakes to count how many occurred in that country.
	// Recall that the country markers have a "name" property, 
	// And LandQuakeMarkers have a "country" property set.
	private void printQuakes() {
		int totalWaterQuakes = quakeMarkers.size();
		int totalLandQuakes =totalWaterQuakes;
		for (Marker country : countryMarkers) {
			String countryName = country.getStringProperty("name");
			int numQuakes = 0;
			for (Marker marker : quakeMarkers)
			{
				EarthquakeMarker eqMarker = (EarthquakeMarker)marker;
				if (eqMarker.isOnLand()) {
					if (countryName.equals(eqMarker.getStringProperty("country"))) {
						numQuakes++;
					}
				}
			}
			if (numQuakes > 0) {
				totalWaterQuakes -= numQuakes;
				System.out.println(countryName + ": " + numQuakes);
				}	
		}
		totalLandQuakes -= totalWaterQuakes;
		System.out.println("\nLAND  QUAKES: " + totalLandQuakes);
		System.out.println("OCEAN QUAKES: " + totalWaterQuakes);
		System.out.println("TOTAL QUAKES: " + quakeMarkers.size());
	}
	
	
	//mine pomocna metoda vypise zemetreseni
	
	private void printQuakesTop3() {

		float[] top3 = new float[3];
		//float buffer;
		int i = 0;
		float top = 0;
		
			for (EarthquakeMarker marker : quakeMarkerList)
			{
				if(marker.getMagnitude()> top3[2]){
					if(marker.getMagnitude()> top3[1]){
						if(marker.getMagnitude()> top3[0]){
							top3[2] = top3[1];
							top3[1] = top3[0];
							top3[0] = (float) marker.getMagnitude();	
						}
						else {	top3[2] = top3[1];
								top3[1] = (float) marker.getMagnitude();
						}
					}
					else{top3[2] = (float) marker.getMagnitude();}
				}
				
				if(marker.getMagnitude()> top){
					top = marker.getMagnitude();
				}
				
				if(i<5){
				System.out.println( i+1 + ". Value of magnitude and place: " + marker.getTitle() ); //+ ((EarthquakeMarker) marker).getMagnitude()
				}
				i++; // omezuje vypis radku
			}
			for(float tops : top3){
				//System.out.println("Hey man the best 3 magnitudes are: "+ tops);
			}
			
			//System.out.println("Hodnota nejvyööÌ magnitudy je: " + top); uncommet the top 3 magnitude  bcs already show TOP magnitude
	}
	
	
	
	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake feature if 
	// it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// getting location of feature
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {
				
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}
			
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}
	
	private void shadeCountries() {
		//colorMode(RGB, 255, 100, 100, 100);
		List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
		int top =0;
		
		//nejprve najdem top pocet zemetreseni
		for (Marker country : countryMarkers) {	
			int pocet = 0;
			for (PointFeature feature : earthquakes){
				if(isInCountry(feature,country)){
					pocet++;
				}
			}
			if(pocet>top){top = pocet;}
		}
		
		// pote zacneme vybarvovat
		for (Marker country : countryMarkers) {	
			int pocet = 0;
			for (PointFeature feature : earthquakes){
				if(isInCountry(feature,country)){
					map.addMarker(country);
					pocet++;	

				}
			}
			if(pocet<top/4){
				country.setColor(color(94, 255, 0, 80));
			}
			if(pocet>=top/4 && pocet<2*top/4){
				country.setColor(color(229, 255, 0, 80));
			}
			if(pocet>=2*top/4 && pocet<3*top/4){
				country.setColor(color(255, 110, 0, 80));
			}
			if(pocet>=3*top/4){
				country.setColor(color(255, 18, 0, 80));
			}
		}
	}

	@Override
	public int compareTo(EarthquakeMarker arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
