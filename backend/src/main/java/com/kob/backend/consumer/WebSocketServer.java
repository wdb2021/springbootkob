package com.kob.backend.consumer;


import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import jdk.nashorn.internal.scripts.JO;
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
    final public static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    final private static CopyOnWriteArraySet<User> matchpool = new CopyOnWriteArraySet<>();

    private Session session = null;
    private User user;
    private static UserMapper userMapper;
    public static RecordMapper recordMapper;
    private Game game = null;

    @Autowired
    public void  setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }

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
        matchpool.add(this.user);

        while(matchpool.size() >= 2) {
            System.out.println(matchpool.size());
            Iterator<User> it = matchpool.iterator();
            User a = it.next(), b=it.next();
            matchpool.remove(a);
            matchpool.remove(b);

            Game game = new Game(17, 18, 40, a.getId(), b.getId());
            game.createMap();

            users.get(a.getId()).game = game;
            users.get(b.getId()).game = game;
            game.start();    //每局游戏单独开一个线程

            JSONObject respGame = new JSONObject();
            respGame.put("a_id", game.getPlayerA().getId());
            respGame.put("a_sx", game.getPlayerA().getSx());
            respGame.put("a_sy", game.getPlayerB().getSy());
            respGame.put("b_id", game.getPlayerB().getId());
            respGame.put("b_sx", game.getPlayerB().getSx());
            respGame.put("b_sy", game.getPlayerB().getSy());
            respGame.put("map", game.getG());

            JSONObject respA = new JSONObject();
            JSONObject respB = new JSONObject();
            respA.put("event","start-matching");
            respA.put("opponent_username", b.getUsername());
            respA.put("opponent_avatar", b.getAvatar());
            respA.put("game", respGame);
            users.get(a.getId()).sendMessage(respA.toJSONString());

            respB.put("event","start-matching");
            respB.put("opponent_username", a.getUsername());
            respB.put("opponent_avatar", a.getAvatar());
            respB.put("game", respGame);
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }
        System.out.println("start matching");
    }

    private void stopMatching() {
        System.out.println("Stop matching");
    }


    private void move(int direction) {
        if (game.getPlayerA().getId().equals(user.getId())) {
            game.setNextStepA(direction);
        } else if (game.getPlayerB().getId().equals(user.getId())) {
            game.setNextStepB(direction);
        }
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
        } else if ("move".equals(event)){
            move(data.getInteger("direction"));
            System.out.println(data);
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
