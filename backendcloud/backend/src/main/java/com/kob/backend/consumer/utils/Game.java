package com.kob.backend.consumer.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import org.springframework.security.core.parameters.P;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread {
    final private Integer cols, rows;
    final private Integer inner_walls_count;
    final int[][] g;
    final private static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    final private Player playerA, playerB;
    private Integer nextStepA=null, nextStepB=null;
    private ReentrantLock lock = new ReentrantLock();
    private String status = "playing";   //playing -> finished
    private String loser = "";       //all: dogfall, A:lose, B:lose

    public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Integer idB) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        playerA = new Player(idA, rows - 2, 1, new ArrayList<>());
        playerB = new Player(idB, 1, cols - 2, new ArrayList<>());
    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public String getStatus() {return status;}

    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
    }


    public int[][] getG() {
        return g;
    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;

        for (int i=0; i<4; i++) {
            int x = sx + dx[i], y = sy + dy[i];
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
                if (check_connectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }
        g[sx][sy]=0;
        return false;
    }

    private boolean draw() {  //draw the map
        for (int i=0; i<this.rows; i++) {
            for (int j=0; j<this.cols; j++) {
                g[i][j]=0;
            }
        }

        for (int r=0; r<this.rows; r++) {
            g[r][0]=g[r][this.cols-1]=1;
        }
        for (int c=0; c<this.cols; c++) {
            g[0][c]=g[this.rows-1][c]=1;
        }

        Random random = new Random();
        for (int i=0; i<this.inner_walls_count/2; i++) {
            for (int j=0; j<1000; j++) {
                int r=random.nextInt(this.rows);
                int c=random.nextInt(this.cols);

                if (g[r][c]==1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1)
                    continue;
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) {
                    continue;
                }

                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }

        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap() {
        for (int i=0; i<1000; i++) {
            if (draw())
                break;
        }
    }


    private boolean nextStep() {  //等待两个玩家下一步操作
        try {  //前端每秒最多走5格，后端需要间隔200ms才能继续读操作来自前端
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (int i=0; i<1000; i++) {
            try {
                Thread.sleep(100);
                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null) {
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                //throw new RuntimeException(e);
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB) {
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);
        if (g[cell.x][cell.y] == 1) return false;

        for (int i=0; i<n-1; i++) {
            if (cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y)
                return false;
        }

        for (int i=0; i<n-1; i++) {
            if (cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y)
                return false;

        }
        return true;
    }

    private void judge() {  // 判断操作是否合法
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = check_valid(cellsA, cellsB);
        boolean validB = check_valid(cellsB, cellsA);
        if (!validA || !validB) {
            status = "finished";

            if (!validA && !validB) loser = "all";
            else if (!validA) loser = "A";
            else loser = "B";
        }
    }

    private void sendMove() {  // msg to client
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB = null;
        } finally {
            lock.unlock();
        }
    }

    private void sendAllMessage(String message) {
        if (WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if (WebSocketServer.users.get(playerB.getId()) != null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    private void sendResult() {  // result_to_client
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    private String getMapString() {
        StringBuilder res = new StringBuilder();
        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }

    private void updateUserRating(Player player, Integer rating) {
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user);
    }

    private void saveToDatabase() {
        Integer ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
        Integer ratingB = WebSocketServer.userMapper.selectById(playerB.getId()).getRating();

        if ("A".equals(loser)) {
            ratingA -= 20;
            ratingB += 40;
        } else if ("B".equals(loser)) {
            ratingA += 40;
            ratingB -= 20;
        }

        updateUserRating(playerA, ratingA);
        updateUserRating(playerB, ratingB);

        Record record = new Record(
            null,
            playerA.getId(),
            playerA.getSx(),
            playerA.getSy(),
            playerB.getId(),
            playerB.getSx(),
            playerB.getSy(),
            playerA.getStepsString(),
            playerB.getStepsString(),
            getMapString(),
            loser,
            new Date()
        );

        WebSocketServer.recordMapper.insert(record);
    }

    @Override
    public void run() {
        for (int i = 0; i<1000; i++) {
            if (nextStep()) {    //是否获取下一步操作
                System.out.println(status);
                judge();
                if (status.equals("playing")) {
                    sendMove();
                } else {
                    sendResult();
                    System.out.println(status+ " else ");
                    break;
                }
            } else {
                status = "playing";
                lock.lock();
                try {
                    if (nextStepA == null && nextStepB == null) {
                        loser = "all";
                    } else if (nextStepA == null) {
                        loser = "A";
                    } else {
                        loser = "B";
                    }
                } finally {
                    lock.unlock();
                }
                System.out.println(status+ " last ");
                sendResult();
                break;
            }
        }

    }
}
