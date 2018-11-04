

import cache.WebSocketSessionCache;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import controller.WebSocketSource;
import org.springframework.beans.factory.annotation.Autowired;
import po.WebSocketToken;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint("/websocket")
public class WebSocketTest  extends WebSocketSource{

    /**
     * 连接建立成功调用的方法
     * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session){
       open();
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(){
            close();
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);
        JSONObject msgJSON = (JSONObject) JSONObject.parse(message);
        Object type = msgJSON.get("msgType");
        if(type!=null&&type.equals("product")){
            registTopic(msgJSON);
        }else if(type!=null&&type.equals("heartBeat")) {
            heartBeatResp(msgJSON,session);
        }else{
            subTopic(msgJSON,session);
        }
    }

    /*
    心跳回复
    * */
    private void heartBeatResp(JSONObject msgJSON,Session session){
        try {
            String topic = msgJSON.getString("topic");
            //是否需要topic下全部消息
            boolean isNessAll = msgJSON.getBoolean("isNessAll");
            String connectionId = msgJSON.getString("id");
            String clientId = msgJSON.getString("clientId");
            //记录客户端连接信息
            sessionMap.put(clientId,session);
            //服务端相应
            JSONObject msg = new JSONObject();
            msg.put("msgType","heartBeatResp");
            System.out.println("服务器响应："+msg.toJSONString());
            session.getBasicRemote().sendText(msg.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
     生产者推送消息
    * */
    private void registTopic(JSONObject msgJSON){
        String topic = msgJSON.getString("topic");
        String message = msgJSON.getString("message");
        sendMessage(message,topic );
    }
    /*
    消费者订阅消息
    * */
    private void subTopic(JSONObject msgJSON,Session session){
        String topic = msgJSON.getString("topic");
        //是否需要topic下全部消息
        boolean isNessAll = msgJSON.getBoolean("isNessAll");
        String connectionId = msgJSON.getString("clientId");
        //ConcurrentHashMap<Integer, WebSocketToken> map = (ConcurrentHashMap<Integer, WebSocketToken>) webSocketMap.get(topic);
        Map<String,String> map = webSocketSessionCache.getSeesions(topic);
        if(map==null){
            map= new ConcurrentHashMap<String, String>();
            webSocketMap.put(topic,map);
        }
        WebSocketToken webSession = null;
        if(map.containsKey(connectionId)){
            String value = map.get(connectionId);
            webSession= JSON.parseObject(value,WebSocketToken.class);
        }else{
            webSession = new WebSocketToken();
        }
        webSession.setId(connectionId);
        webSession.setNesAll(isNessAll);
        webSession.setTopic(topic);
        webSession.setSessinId(session.getId());
        if(!map.containsKey(connectionId)){
            map.put(connectionId,JSON.toJSONString(webSession));
        }
        //给重新连接上的客户端补发订阅topic下的消息
        if(isNessAll){
                if (session.isOpen()) {
                    CopyOnWriteArrayList<String> messages = webSession.getMessages();
                        if (messages != null && !messages.isEmpty()) {
                            for (String msg : messages) {
                                JSONObject msgJ = new JSONObject();
                                msgJ.put("msgType","sss");
                                msgJ.put("message",msg);
                                sendMsg(session,msgJ);
                                messages.remove(msg);
                            }
                            webSession.setMessages(messages);
                            map.put(connectionId,JSON.toJSONString(webSession));
                        }
                }
        }


        //更新缓存
        webSocketSessionCache.setWebSocketSessions(topic,map);

        JSONObject msg = new JSONObject();
        msg.put("msgType","sss");
        System.out.println("服务器响应："+msg.toJSONString());
        try {
            session.getBasicRemote().sendText(msg.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        System.out.println("发生错误");
        error.printStackTrace();
    }

    public  void sendMessage(String message,String topic)   {
       // ConcurrentHashMap<Integer, WebSocketToken> map = (ConcurrentHashMap<Integer, WebSocketToken>) webSocketMap.get(topic);
        Map<String,String> map = webSocketSessionCache.getSeesions(topic);
            if(map!=null&&!map.isEmpty()){
                for (Map.Entry entry: map.entrySet()) {
                Object value = entry.getValue();
                WebSocketToken webSession = JSON.parseObject(value.toString(),WebSocketToken.class);
                Session session = (Session) sessionMap.get(webSession.getId());
                JSONObject msgJ = new JSONObject();
                msgJ.put("msgType","sss");
                msgJ.put("message",message);
                if (session!=null&&session.isOpen()) {
                    if (webSession.isNesAll()) {
                        CopyOnWriteArrayList<String> messages = webSession.getMessages();
                        if (messages != null && !messages.isEmpty()) {
                            for (String msg : messages) {
                                JSONObject mJSON = new JSONObject();
                                msgJ.put("msgType","sss");
                                msgJ.put("message",msg);
                                sendMsg(session,mJSON);
                                messages.remove(msg);
                            }
                            webSession.setMessages(messages);
                        }
                        sendMsg(session,msgJ);
                    } else {
                        sendMsg(session,msgJ);
                    }
                } else {
                    if (webSession.isNesAll()) {
                        CopyOnWriteArrayList messages = webSession.getMessages();
                        if (messages == null) {
                            messages = new CopyOnWriteArrayList();
                        }
                        messages.add(message);
                        webSession.setMessages(messages);
                        map.put((String)entry.getKey(),JSON.toJSONString(webSession));
                    } else {
                        map.remove(entry);
                    }
                }
            }
         }else{
                // 游离topic message暂存(功能暂不开发)

            }
            //更新缓存
        webSocketSessionCache.setWebSocketSessions(topic,map);
    }


}