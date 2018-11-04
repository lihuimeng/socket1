package cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import config.ConfigAnalyzer;
import po.WebSocketToken;

import java.util.Map;

public class WebSocketSessionCache {
    private static final RedisCacheClient redisCacheClient;

    static{
          JSONObject redisConfig = ConfigAnalyzer.redisConfig;
           redisCacheClient = RedisCacheClient.getRedisCacheClient(redisConfig);
     }
   public  WebSocketSessionCache(){

    }

    public WebSocketToken getWebSocketSession(String topic,String connectId){
        WebSocketToken webSession = new WebSocketToken();
        Map<String,String> map = redisCacheClient.hgetAll(topic);
        String value = map.get(connectId);
        JSONObject tokenJSON = JSON.parseObject(value);


        return webSession;
    }

    public String setWebSocketSessions(String topic,Map<String,String> map){
       return  redisCacheClient.hset(topic,map);
    }


    public Map<String,String> getSeesions(String topic){
        WebSocketToken webSession = new WebSocketToken();
        Map<String,String> map = redisCacheClient.hgetAll(topic);
        return map;
    }



}
