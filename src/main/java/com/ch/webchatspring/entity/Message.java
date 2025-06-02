package com.ch.webchatspring.entity;
import java.sql.Timestamp;
public class Message {
    int chat_id;
    int user_send_uid;
    int user_receiver_uid;
    Timestamp send_time;
    String message;
}
