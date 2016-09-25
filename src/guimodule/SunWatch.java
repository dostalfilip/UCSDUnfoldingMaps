package guimodule;

import processing.core.PApplet;
import processing.core.PImage;


public class SunWatch extends PApplet {
	private String URL = "http://www.marriott.com/Images/MiniStores/Header_Images/Destinations/en/San_Diego_Hotels_city_view.jpg";
	private PImage backgrounding;
	
	
	public void setup() {
		// TODO Auto-generated method stub
		size(700, 300);
		backgrounding = loadImage(URL, "jpg");
		backgrounding.resize(0, height);	
	}
	
	public void draw() {
		// TODO Auto-generated method stub
		 int time_hour = hour();
		 int time_minutes = minute();
		 int[] rgb = vratrgb(time_hour*60 + time_minutes);

		
		background(0);
		image(backgrounding, 0, 0);
		fill(255,209,0);
		fill(rgb[0],rgb[1],rgb[2]);
		ellipse(6*width/7,height/5,height/4,height/5);
		
		
		fill(255,209,0);
		text(rgb[0], 100, 150);
		text(rgb[1], 100, 160);
		text(rgb[2], 100, 170);
		

		text((time_hour*60 + time_minutes), 100, 190);
	}
	
	public int[] vratrgb (int minuty) {
		int[] rgb = new int[3];
		
		float poc = minuty / 10;
		
		float c = poc/144 ;
		
		rgb[0]= (int)(c*255);
		rgb[1]= (int)(c*255);
		rgb[2]= 0;
		
		
		return rgb;
	}

}


// pomocny program 

