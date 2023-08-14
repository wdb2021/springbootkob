package com.kob.backend.consumer;


import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")   // 不要以'/'结尾
public class WebSocketServer {
    final private static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    final private static CopyOnWriteArraySet<User> matchpool = new CopyOnWriteArraySet<>();

    private Session session = null;
    private User user;
     private static UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        //initial connect
        this.session = session;
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);
        users.put(userId, this);

        if (this.user != null) {
            users.put(userId, this);
        } else {
            this.session.close();
        }

        System.out.println(users);
        System.out.println("Connected!");
    }

    @OnClose
    public void onClose() {
        //close connect
        if (this.user != null) {
            users.remove(this.user.getId());
            matchpool.remove(this.user);
        }

        System.out.println("Disconnected!");
    }

    private void startMatching() {
        System.out.println("start matching 1");
        matchpool.add(this.user);

        while(matchpool.size() >= 2) {
            System.out.println(matchpool.size());
            Iterator<User> it = matchpool.iterator();
            User a = it.next(), b=it.next();
            matchpool.remove(a);
            matchpool.remove(b);

            JSONObject respA = new JSONObject();
            JSONObject respB = new JSONObject();
            respA.put("event","start-matching");
            respA.put("opponent_username", b.getUsername());
            respA.put("opponent_avatar", b.getAvatar());
            users.get(a.getId()).sendMessage(respA.toJSONString());

            respB.put("event","start-matching");
            respB.put("opponent_username", a.getUsername());
            respB.put("opponent_avatar", a.getAvatar());
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }
        System.out.println("start matching");
    }

    private void stopMatching() {
        System.out.println("Stop matching");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        //receive msg from client
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if ("start-matching".equals(event)) {
            startMatching();
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        } else if ("test".equals(event)) {
            System.out.println("start-testing in backend");
            JSONObject resp = new JSONObject();
            resp.put("event", "test");
            sendMessage(resp.toJSONString());
        }
        Date datetime = new Date();
        System.out.println("Received msg!" + datetime);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();

        System.out.println("error!");
    }

    public void sendMessage(String message) {
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
