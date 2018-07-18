package neoapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
 
// todo: 1) add all possible exceptions and appropriate messages
// 2) add logger?
// 3) add javadoc?
// 4) exceptions + throw
 
public class NEOApp {     
    
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
    // Create a URL string to query NEOs using NASA API where
    // start_date and end_date are the same and equal to the today's date.
    private static String createURLString(LocalDate date) {
        String formattedDate = date.format(formatter);        
        return String.format("https://api.nasa.gov/neo/rest/v1/feed?start_date=%s&end_date=%s&api_key=DEMO_KEY",
                             formattedDate, formattedDate);
    }
     
    // This function can be used to test the query for debugging purpose.
    // It reads the GET response and prints the raw JSON data line by line.
    private static void printURLResponce(URL url) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String inputLine;

            System.out.println("/***** URL Content *****\n");
            while((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }  
    }
    
   /**
    *
    * Retrieve a list of Asteroids close to the Earth as of today.
    * Use the NASA API:
    * GET https://api.nasa.gov/neo/rest/v1/feed?start_date=START_DATE&end_date=END_DATE&api_key=API_KEY
    * in order to retrieve the data. Then search for the Asteroid which has
    * biggest size and one that is closest to the Earth in order to
    * print their details.
     * @param args
    */
    public static void main(String[] args) { 
        try {
            LocalDate  today = java.time.LocalDate.now();            
            URL        url = new URL(createURLString(today));             
            JsonObject jsonObject = null;
            
            // Print the raw responce.
            printURLResponce(url);

            // Read response from stream to json object.
            try (JsonReader jsonReader = Json.createReader(new InputStreamReader(url.openStream()))) {
                jsonObject = jsonReader.readObject();
            } catch (JsonException e) {
                throw e;
            } 
            
            // Read the number of Ateroids close to the earth today
            int elementCount = jsonObject.getInt(Constants.elementCountStr, 0);
            System.out.println(String.format("\nNumber of NEOs close to the Earth on %s is %d\n", 
                               today.format(formatter), elementCount));
            
            if (elementCount < 1) {
                return;
            }
            
            JsonArray asteroidsJsonArray = jsonObject.getJsonObject(
                    Constants.nearEarthObjectsStr).getJsonArray(today.format(formatter));
            
            if (asteroidsJsonArray.size() < 1) {
                throw new AsteroidParsingException(String.format(
                    "Unknown error, read 0 Asteroid objects, while expected %d\n",
                    elementCount));
            }
 
            // Find the biggest and the closest NEOs.
            Asteroid biggestNEO = new Asteroid(asteroidsJsonArray.getJsonObject(0));
            Asteroid closestNEO = biggestNEO;
            Asteroid currAsteroid = null;

            ComparatorBySize sizeComparator = new ComparatorBySize();
            ComparatorByDistance distanceComparator = new ComparatorByDistance();
                    
            for (int i = 1; i < asteroidsJsonArray.size(); i++) {
                try {
                    currAsteroid = new Asteroid(asteroidsJsonArray.getJsonObject(i));
                } catch (AsteroidParsingException e) {
                    System.out.println(String.format(
                            "%s. JSON object: %s", e.getMessage(), 
                            asteroidsJsonArray.getJsonObject(i).toString()));
                    continue;
                }
                
                if (sizeComparator.compare(biggestNEO, currAsteroid) < 0) {
                    biggestNEO = currAsteroid;
                }
                
                if (distanceComparator.compare(closestNEO, currAsteroid) < 0) {
                    closestNEO = currAsteroid;
                }
            }                    

            System.out.println(String.format(
                    "\n*** The biggest Asteroid details *** %s", biggestNEO));                
            System.out.println(String.format(
                    "\n*** The closest to the Earth Asteroid details *** %s", closestNEO));  
            
        } catch(JsonException e) {
            System.out.println(String.format("Error during reading JSON responce. Reason: ", e.getMessage()));
            e.printStackTrace(System.err); 
        } catch(AsteroidParsingException e) {
            System.out.println(String.format("Error during parsing JSON object. Reason: ", e.getMessage()));
            e.printStackTrace(System.err); 
        } catch(RuntimeException e) {
            System.out.println(e.getMessage());
            e.printStackTrace(System.err);
        } catch(Exception e) {
            System.out.println(String.format("The execution failed due to the following reason: ", e.getMessage()));
            e.printStackTrace(System.err);
        } 
    }
}