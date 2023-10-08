package com.kob.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import lombok.Getter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread{
    final private Integer rows;
    final private Integer cols;
    final private Integer inner_walls_count;
    @Getter
    final private int[][] g;
    final private static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    @Getter
    final private Player playerA, playerB;
    private Integer nextStepA = null;
    private Integer nextStepB = null;
    private ReentrantLock lock = new ReentrantLock();
    private String status = "playing";  // playing -> finished
    private String loser = ""; //all: 平局, "A": A输, "B": B输
    private final static String addBotUrl = "http://127.0.0.1:3002/bot/add/";
    public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Bot botA, Integer idB, Bot botB){
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "", botCodeB = "";
        if (botA != null){
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if (botB != null){
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }
        playerA = new Player(idA, botIdA, botCodeA, rows - 2, 1, new ArrayList<>());
        playerB = new Player(idB, botIdB, botCodeB, 1, cols - 2, new ArrayList<>());
    }

    public void setNextStepA(Integer nextStepA){
        lock.lock();
        try{
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB){
        lock.lock();
        try{
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
    }

    public boolean check_connectivity(int sx, int sy, int tx, int ty){
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;

        for (int i = 0; i < 4; i ++){
            int x = sx + dx[i], y = sy + dy[i];
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0){
                if (check_connectivity(x, y, tx, ty)){
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }
        g[sx][sy] = 0;
        return false;
    }
    private boolean draw(){
        for (int i =  0; i < this.rows; i ++) {
            for (int j = 0; j < this.cols; j ++){
                g[i][j] = 0;
            }
        }
        //创建墙
        for (int r = 0; r < this.rows; r ++){
            g[r][0] = g[r][this.cols - 1] = 1;
        }
        for (int c = 0; c < this.cols; c ++){
            g[0][c] = g[this.rows - 1][c] = 1;
        }
        //创建随机障碍物
        Random random = new Random();
        for (int i = 0; i < this.inner_walls_count / 2; i ++){
            for (int j = 0; j < 1000; j ++){
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);

                if (g[r][c] == 1|| g[this.rows - 1 - r][this.cols - 1 - c] == 1){
                    continue;
                }
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2){
                    continue;
                }
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }
        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap(){
        for (int i = 0; i < 1000; i ++) {
            if (draw()){
                break;
            }
        }
    }

    private String getInput(Player player){ //将当前局面信息编码成字符串
        //地图+自己起始sx+自己起始sy+自己路径步骤+对方起始sx+对方起始sy+对方路径步骤
        Player me, you;
        if (playerA.getId().equals(player.getId())){
            me = playerA;
            you = playerB;
        } else{
            me = playerB;
            you = playerA;
        }
        return getMapString() + "#" +
                me.getSx() + "#" +
                me.getSy() + "#(" +
                me.getStepsString() + ")#" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getStepsString() + ")#";
    }
    private void sendBotCode(Player player){
        if (player.getBotId().equals(-1)) return;  //人操作

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", player.getId().toString());
        data.add("bot_code", player.getBotCode());
        data.add("input", getInput(player));

        WebSocketServer.restTemplate.postForObject(addBotUrl, data, String.class);

    }

    //等待两名玩家下一步操作
    private boolean nextStep(){
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        sendBotCode(playerA);
        sendBotCode(playerB);

        for (int i = 0; i < 50; i ++){
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
                throw new RuntimeException(e);
            }
        }
        return false;
    }
    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB){
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);
        if (g[cell.x][cell.y] == 1) return false;

        for (int i = 0; i < n - 1; i ++) {
            if (cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y){
                return false;
            }
        }

        for (int i = 0; i < n - 1; i ++) {
            if (cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y){
                return false;
            }
        }
        return true;
    }
    private void judge() {  //判断两名玩家下一步是否合法
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = check_valid(cellsA, cellsB);
        boolean validB = check_valid(cellsB, cellsA);
        if (!validA || !validB){
            status = "finished";

            if (!validA && ! validB){
                loser = "all";
            } else if (!validA){
                loser = "A";
            } else {
                loser = "B";
            }
        }

    }
    private void sendAllMessage(String message) {
        if (WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if (WebSocketServer.users.get(playerB.getId()) != null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }
    private void sendMove() {   //传递移动信息
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            nextStepA = nextStepB = null;
            sendAllMessage(resp.toString());
        } finally {
            lock.unlock();
        }

    }
    private String getMapString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < rows; i ++){
            for (int j = 0; j < cols; j ++) {
                res.append(g[i][j]);
            }
        }
        return res.toString();
    }

    private void saveToDatabase(){
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
//        System.out.println(record);
        WebSocketServer.recordMapper.insert(record);
    }
    private void sendResult() { //向两名client公布结果
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        sendAllMessage(resp.toString());
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i ++){
            if (nextStep()){    //是否读取两条蛇下一步操作
                judge();
                if (status.equals("playing")) {
                    sendMove();
                } else {
                    saveToDatabase();
                    sendResult();
                    break;
                }
            }else{
                status = "finished";
                lock.lock();
                try {
                    if (nextStepA == null && nextStepB == null){
                        loser = "all";
                    }else if(nextStepA != null){
                        loser = "B";
                    }else{
                        loser = "A";
                    }
                } finally {
                    lock.unlock();
                }
                saveToDatabase();
                sendResult();
                break;

            }
        }
    }


}
