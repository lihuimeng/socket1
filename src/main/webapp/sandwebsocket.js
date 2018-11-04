/*
//  Copyright (c) 2018 杉德支付网络服务发展有限公司
//
//  websocket 插件
//
//  v0.1    2018-10-26  朱伟丽
//
//------

*/
var ws = (function(){
    var constant = {
        POLLTIME: 1000 * 5,
        TIMEOUT: 1000 * 10

    };
    var client = function(_uri,clientId,_onmessssageArrive){
        this.uri = _uri;
        this.onmessssageArrive = _onmessssageArrive;
        this.clientId = clientId;
        this.websocket=null;

    };
    client.prototype = {

        doconnect:function(){

            this.websocket = new WebSocket(this.uri);
            this.websocket.alive = false;
            this.websocket.onmessssageArrive = this.onmessssageArrive;
            this.websocket.onmessage= function(event) {
                var msg = event.data;
                var msgJSON = JSON.parse(msg);
                //消息类型为响应心跳
                if (msgJSON.msgType=='heartBeatResp') {
                    this.alive = true;
                }else{
                    //执行用户接受消息处理业务
                    this.onmessssageArrive.call(this,event);
                }
            };
            this.websocket.onclose = function(){
                this.alive = false;
            };
            this.websocket.onerror = function(){
                this.alive = false;;
            }


        },
        doclose:function () {
            this.websocket.close();
            this.websocket.alive = false;
        },
        sendMsg:function(_msg){
                if(this.websocket.readyState == this.websocket.OPEN){
                    if(typeof(_msg)!='object'){
                        throw new Error("消息类型必须为JSON", [typeof newOnMessageArrived, "onMessageArrived"]);
                    }
                    _msg.clientId = this.clientId;
                    this.websocket.send(JSON.stringify(_msg));
                }else/* if(websocket.readyState==websocket.CONNECTING)*/{
                    setTimeout(function(cll){
                        cll.sendMsg(_msg);
                    },constant.TIMEOUT,this)
                }
        }
    }


    var ClientImpl= function(_uri,clientId,onmessssageArriv){
         this.cl = new client(_uri,clientId,onmessssageArriv);
         this.cl.doconnect();
         this.heart = new this.heartbeat(this.cl);
         this.heart.start();
    }
  ClientImpl.prototype={

      sendMsg: function(msg){
          this.cl.sendMsg(msg);
      },
      closeWebSocket: function () {
          this.cl.doclose();
          this.heart.stop();
      },
      heartbeat: function(wsclient){
          this.clidd=wsclient,
          this.pollfun=null,
          this.start=function(){
              this.pollfun = setTimeout(function(cli,clim){

                  var msg = {isNessAll:true};
                  msg.msgType="heartBeat";
                  cli.sendMsg(msg);
                  setTimeout(function(cline,climl){
                      if(cline.websocket.alive != true){
                          //断开重新连接
                          cline.doclose();
                          climl.stop();
                          cline.doconnect();
                      }
                      climl.start();
                  },constant.TIMEOUT,cli,clim)

              },constant.POLLTIME,this.clidd,this);
          },
          this.stop=function(){
              clearTimeout(this.pollfun);
          }
      },

  }

    return {
        client: ClientImpl
    }
})(window);






