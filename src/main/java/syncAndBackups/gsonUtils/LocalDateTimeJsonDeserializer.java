package syncAndBackups.gsonUtils;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;



/**
 * A JsonDeserializer for LocalDateTime
 * @author xsala
 *
 */
public class LocalDateTimeJsonDeserializer implements JsonDeserializer<LocalDateTime> {

	
	@Override //recieves a JsonElement and returns LocalDateTime.
	public LocalDateTime deserialize(JsonElement jse, Type srcType, JsonDeserializationContext context)throws JsonParseException {
		return LocalDateTime.parse(jse.getAsString(), DateTimeFormatter.ofPattern("dd-MM-yyyy;HH:mm:ss"));
	}


}