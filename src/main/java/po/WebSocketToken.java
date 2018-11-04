package po;

import javax.websocket.Session;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/*
websocket connection
* */
public class WebSocketToken {

  //
  private String id;

  private String topic;

  private CopyOnWriteArrayList messages;

  private boolean isNesAll;

  private String sessinId;

  public String getId() {
    return id;
  }

  public CopyOnWriteArrayList getMessages() {
    return messages;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setMessages(CopyOnWriteArrayList messages) {
    this.messages = messages;
  }

  public boolean isNesAll() {
    return isNesAll;
  }

  public void setNesAll(boolean nesAll) {
    isNesAll = nesAll;
  }

  public String getSessinId() {
    return sessinId;
  }

  public void setSessinId(String sessinId) {
    this.sessinId = sessinId;
  }
}
