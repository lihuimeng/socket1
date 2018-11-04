package config;
import com.alibaba.fastjson.JSONObject;
import org.ho.yaml.Yaml;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ConfigAnalyzer {

     public static JSONObject redisConfig;

      static  {
          InputStream frameworkConfig = ConfigAnalyzer.class.getClassLoader().getResourceAsStream("framework.yaml");
          JSONObject framewordConfigs = null;
          try {
              framewordConfigs = Yaml.loadType(frameworkConfig, JSONObject.class);
          } catch (FileNotFoundException e) {
              e.printStackTrace();
          }
          redisConfig = framewordConfigs.getJSONObject("redisClient");
       }



}
