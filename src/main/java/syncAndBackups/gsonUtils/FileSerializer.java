package syncAndBackups.gsonUtils;

import java.io.File;
import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class FileSerializer implements JsonSerializer<File> {

	@Override
	public JsonElement serialize(File src, Type typeOfSrc, JsonSerializationContext context) {
		
		return new JsonPrimitive(src.toString());
	}

}


