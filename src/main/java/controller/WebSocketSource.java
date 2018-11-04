package controller;

import cache.WebSocketSessionCache;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.socket.WebSocketSession;
import po.WebSocketToken;

import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

//websocket 服务基类
public class WebSocketSource  {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     protected static int onlineCount = 0;
     protected static ConcurrentHashMap webSocketMap = new ConcurrentHashMap();
     protected static ConcurrentHashMap sessionMap = new ConcurrentHashMap();
     protected WebSocketSessionCache webSocketSessionCache = new WebSocketSessionCache();

    public void sendMsg(Session session, JSONObject message){
        try {
            session.getBasicRemote().sendText(message.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("消息发送失败");
        }

    }

    public void sendMsgs(Session session, WebSocketToken webSession){
            CopyOnWriteArrayList<String> messages = webSession.getMessages();
            if (messages != null && !messages.isEmpty()) {
                for (String msg : messages) {
                        JSONObject msgJ = new JSONObject();
                        msgJ.put("msgType", "sss");
                        msgJ.put("message", msg);
                        sendMsg(session,msgJ);
                        messages.remove(msg);
                }
                webSession.setMessages(messages);
            }
    }
    public void close(){
        subOnlineCount();           //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    public void error(){

    }

    public void open(){
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketSource.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketSource.onlineCount--;
    }
}
