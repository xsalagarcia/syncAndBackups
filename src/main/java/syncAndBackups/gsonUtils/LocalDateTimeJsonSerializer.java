package syncAndBackups.gsonUtils;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * A JsonSerializer for LocalDateTime
 * @author xsala
 *
 */
public class LocalDateTimeJsonSerializer implements JsonSerializer<LocalDateTime>{

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy;HH:mm:ss");
	
	@Override //recieves LocalDateTime and returns a String primitive.
	public JsonElement serialize(LocalDateTime ldt, Type srcType, JsonSerializationContext context) {
		return new JsonPrimitive(formatter.format(ldt));
	}
	
}