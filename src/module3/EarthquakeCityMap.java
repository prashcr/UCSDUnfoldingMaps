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

/**
 * EarthquakeCityMap An application with an interactive map displaying
 * earthquake data. Author: UC San Diego Intermediate Software Development MOOC
 * team
 * 
 * @author Your name here Date: July 17, 2015
 */
public class EarthquakeCityMap extends PApplet {

    // You can ignore this. It's to keep eclipse from generating a warning.
    private static final long serialVersionUID = 1L;
    
    // IF YOU ARE WORKING OFFLINE, change the value of this variable to true
    private static final boolean offline = false;
    
    // Less than this threshold is a light earthquake
    public static final float THRESHOLD_MODERATE = 5;
    
    // Less than this threshold is a minor earthquake
    public static final float THRESHOLD_LIGHT = 4;
    
    private final int red = color(233, 63, 51);
    private final int yellow = color(253, 247, 49);
    private final int blue = color(42, 79, 252);

    /**
     * This is where to find the local tiles, for working without an Internet
     * connection
     */
    public static String mbTilesString = "blankLight-1-3.mbtiles";

    // The map
    private UnfoldingMap map;
    
    // List of USGS feeds http://earthquake.usgs.gov/earthquakes/feed/v1.0/atom.php
    // Past 7 days feed with magnitude 2.5+ Earthquakes
    private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

    public void setup() {
        size(950, 600, OPENGL);

        if (offline) {
            map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
            earthquakesURL = "2.5_week.atom"; // Same feed, saved Aug 7, 2015,
                                              // for working offline
        } else {
            map = new UnfoldingMap(this, 200, 50, 700, 500, new Google.GoogleMapProvider());
            // IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
            // earthquakesURL = "2.5_week.atom";
        }

        map.zoomToLevel(2);
        MapUtils.createDefaultEventDispatcher(this, map);

        // The List you will populate with new SimplePointMarkers
        List<Marker> markers = new ArrayList<Marker>();

        // Use provided parser to collect properties for each earthquake
        // PointFeatures have a getLocation method
        List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);

        // These print statements show you (1) all of the relevant properties
        // in the features, and (2) how to get one property and use it
        if (earthquakes.size() > 0) {
            PointFeature f = earthquakes.get(0);
            System.out.println(f.getProperties());
            Object magObj = f.getProperty("magnitude");
            float mag = Float.parseFloat(magObj.toString());
            // PointFeatures also have a getLocation method
        }
        
        // Create markers for every earthquake
        for (PointFeature earthquake : earthquakes) {
            markers.add(createMarker(earthquake));
        }
        
        map.addMarkers(markers);
    }

    // A suggested helper method that takes in an earthquake feature and
    // returns a SimplePointMarker for that earthquake
    private SimplePointMarker createMarker(PointFeature feature) {
        
        SimplePointMarker m = new SimplePointMarker(feature.getLocation());
        Object magObj = feature.getProperty("magnitude");
        float mag = Float.parseFloat(magObj.toString());
        
        if (mag >= 5.0) {
            m.setColor(red);
            m.setRadius(16);
        }
        else if (mag < 4.0) {
            m.setColor(blue);
            m.setRadius(5);
        }
        else {
            m.setColor(yellow);
        }
        
        return m;
    }

    public void draw() {
        background(10);
        map.draw();
        addKey();
    }

    // Helper method to draw key in GUI
    private void addKey() {
        // Remember that Processing's graphics methods can be used here
        // Rectangle that contains the key
        fill(color(232, 255, 255));
        rect(25, 50, 150, 250);
        
        fill(color(10, 10, 10));
        textSize(16);
        text("Earthquake Key", 40, 80);
        textSize(12);
        text("(In Richter scale)", 45, 100);
        
        textSize(15);
        text(">=5.0", 82, 145);
        text(">=4.0", 82, 205);
        text("<4.0", 82, 265);
        
        fill(red);
        ellipse(60, 140, 27, 27);
        
        fill(yellow);
        ellipse(60, 200, 20, 20);
        
        fill(blue);
        ellipse(60, 260, 13, 13);
    }
}
