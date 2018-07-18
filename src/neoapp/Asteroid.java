/**
 *
 * @author Yulia Moldavsky
 */
package neoapp;

import java.util.Comparator;
import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 *
 * This class compares Asteroids by their size.
 */
class ComparatorBySize implements Comparator<Asteroid>
{
    @Override
    public int compare(Asteroid a, Asteroid b) {
        return Double.compare(a.getEstimatedDiameterMax(), b.getEstimatedDiameterMax());
    }
}
 
/**
 *
 * This class compares Asteroids by their distance to Earth.
 */
class ComparatorByDistance implements Comparator<Asteroid>
{
    @Override
    public int compare(Asteroid a, Asteroid b) {
        return Double.compare(a.getDistanceToEarth(), b.getDistanceToEarth());
    }
}

/**
 *
 * This class specifies constants used to parse JSON 
 * representation of the Asteroid.
 */
final class Constants {
    static final String elementCountStr          = "element_count";
    static final String estimatedDiameterStr     = "estimated_diameter";
    static final String kilometersStr            = "kilometers"; 
    static final String estimatedDiameterMinStr  = "estimated_diameter_min";
    static final String estimatedDiameterMaxStr  = "estimated_diameter_max";
    static final String closeApproachDataStr     = "close_approach_data";
    static final String missDistanceStr          = "miss_distance";
    static final String nearEarthObjectsStr      = "near_earth_objects";
    static final String astronomical             = "astronomical";
}

/**
 *
 * This class represents an exception thrown in case JSON object
 * cannot be deserialized or parsed.
 */
final class AsteroidParsingException extends Exception {
    public AsteroidParsingException(String message) {
        super(message);
    }
    
    public AsteroidParsingException(String message,
                                    Throwable cause) {
        super(message, cause);
    }
}
        
/**
 *
 * This class represents a single Asteroid object.
 * Not all fields are serialized, except those that are required
 * to allow sort operations to be performed on Asteroid objects.
 */
public class Asteroid { 
    
    private double          estimatedDiameterMax = 0;
    private double          distanceToEarth = 0; 
    transient JsonObject    asteroidJsonObject = null; // the JSON raw data
 
    /**
     *
     * @param asteroidJsonObject
     * @throws AsteroidParsingException
     */
    public Asteroid (JsonObject asteroidJsonObject) throws AsteroidParsingException {
        parseEstimatedDiameter(asteroidJsonObject);
        parseDistanceToEarth(asteroidJsonObject);
        this.asteroidJsonObject = asteroidJsonObject;
    }   
    
    private void parseEstimatedDiameter(JsonObject obj) throws AsteroidParsingException {
        try {
            this.estimatedDiameterMax = obj.getJsonObject(
                Constants.estimatedDiameterStr).getJsonObject(
                Constants.kilometersStr).getJsonNumber(
                Constants.estimatedDiameterMaxStr).doubleValue(); 
        } catch(ClassCastException e) {
            throw new AsteroidParsingException(String.format(
                "Error during deserializing %s field.", 
                Constants.estimatedDiameterStr), e);
        }        
        //System.out.println("NEO size is: " + this.estimatedDiameterMax + "\n");   
    }

    private void parseDistanceToEarth (JsonObject obj) throws AsteroidParsingException {
        try {
            JsonArray distanceArray = obj.getJsonArray(Constants.closeApproachDataStr);
        
            if (distanceArray.size() >= 1) {
                String distance = distanceArray.getJsonObject(0).getJsonObject(
                    Constants.missDistanceStr).getString(Constants.astronomical);
                this.distanceToEarth = Double.parseDouble(distance);
            }
        } catch(ClassCastException | NumberFormatException e) {
            throw new AsteroidParsingException(String.format(
                "Error during deserializing %s field under the %s date.", 
                Constants.missDistanceStr, Constants.closeApproachDataStr), e);
        }
        //System.out.println("NEO distance is: " + this.distanceToEarth + "\n");   
    }
    
    public double getEstimatedDiameterMax() {
        return estimatedDiameterMax;
    }
    
    public double getDistanceToEarth() {
        return distanceToEarth;
    }

    @Override
    public String toString() {
        return PrettyJsonFormatter.getInstance().format(asteroidJsonObject);
    }    
}
    
