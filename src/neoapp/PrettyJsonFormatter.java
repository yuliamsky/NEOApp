/**
 *
 * @author Yulia Moldavsky
 */
package neoapp;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.JsonException;
import javax.json.stream.JsonGenerator;

/**
 *
 * The purpose of this class is to create a singleton object
 * that can convert JSON data to the pretty printed string.
 */
public class PrettyJsonFormatter { 
    private static final PrettyJsonFormatter instance = new PrettyJsonFormatter();    
    JsonWriterFactory   factory;
    
    private PrettyJsonFormatter() { // throws ???
        Map<String, Boolean> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        this.factory = Json.createWriterFactory(config);
    }

    public static PrettyJsonFormatter getInstance() {
        return instance;
    }
   
    public String format(JsonObject obj) {
        if (obj == null) {
            return "";
        }
        
        StringWriter        stringWriter = new StringWriter();  
        
        try (JsonWriter jsonWriter = factory.createWriter(stringWriter)) {
            jsonWriter.writeObject(obj);
        } catch (JsonException | IllegalStateException e) {
            System.out.println(String.format(
                    "\nCan't convert JSON object %s to pretty string.", obj.toString()));
            return "";
        }
        return stringWriter.toString();
    }    
}
