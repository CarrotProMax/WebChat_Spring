package com.ch.webchatspring.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Chat {
    private int uid;
    private int operateSymbol;
    private String sendName;
    private String receiveName;
    private String message;
    private Timestamp time;
//我这里的思路是这个是用来接受前端信息的，然后这个类是给前端用的，当前端发起了发送信息的请求在数据库中进行存储，然后趁着心跳检测来查询后端有没有接受到信息(虽然效率很低下，但是目前只能这么办)
    //心跳检测在发送到后端之前需要遍历是否是在登录状态否则拒绝该次心跳
}
