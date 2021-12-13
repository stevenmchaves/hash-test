package helpers;

import com.google.gson.Gson;

public final class JsonUtils {
  private static final Gson gson = new Gson();

  private JsonUtils(){}

  public static boolean isJSONValid(String jsonInString) {
      try {
          gson.fromJson(jsonInString, Object.class);
          return true;
      } catch(com.google.gson.JsonSyntaxException ex) { 
          return false;
      }
  }
}
