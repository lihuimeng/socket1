package cache;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RedisCacheClient {


    private static RedisCacheClient redisCacheClient = null;

    private static ShardedJedisPool jedisPool = null;


    public static  final  RedisCacheClient getRedisCacheClient(JSONObject redisConfig){
            if(redisCacheClient ==null){
                redisCacheClient = new RedisCacheClient();
                initPool(redisConfig);
            }
            return redisCacheClient;
    }

    public static void initPool(JSONObject configObj){
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(configObj.getInteger("poolMaxTotal"));
        config.setMaxIdle(configObj.getInteger("poolMaxIdle"));
        config.setMaxWaitMillis(configObj.getInteger("poolMaxWaitMillis"));
        config.setTestOnBorrow(configObj.getBoolean("poolTestOnBorrow"));
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        JedisShardInfo jedisShardInfo = new JedisShardInfo(configObj.getString("uri"));
        shards.add(jedisShardInfo);
        jedisPool = new ShardedJedisPool(config, shards);
    }

    public ShardedJedis getJedis(){
        return  jedisPool.getResource();
    }

   public void  restoreJedis(ShardedJedis jedis){
       jedis.close();
   }

    public String hget(String key,String field ){
        ShardedJedis jedis = getJedis();
        String value = null;
        try{
            value = jedis.hget(key, field);
        }catch(Exception e){
            System.out.println("获取redis数据异常");
        }finally {
            restoreJedis(jedis);
        }
       return value;
    }

    public Map<String,String> hgetAll(String key){
        ShardedJedis jedis = getJedis();
        Map<String,String> value = null;
        try{
            value = jedis.hgetAll(key);
        }catch(Exception e){
            System.out.println("获取redis数据异常");
        }finally {
            restoreJedis(jedis);
        }
        return value;
    }

    public String get(String key){
        return null;
    }

    public void set(String key ,String value){

    }

   public String hset(String key,Map map){
       ShardedJedis jedis = getJedis();
       String result = null;
       try{
           result = jedis.hmset(key,map);
       }catch(Exception e){
           e.printStackTrace();
       }finally {
           restoreJedis(jedis);
       }
       return result;
   }
}
