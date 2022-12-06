package syncAndBackups.gsonUtils;

import java.io.File;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class FileDeserializer implements JsonDeserializer<File> {

	@Override
	public File deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		return new File(json.getAsString());
	}
	

}
