package com.ch.webchatspring.entity;

import lombok.Data;

@Data
public class UserInfo {
    private Integer uid;//与数据表中的字段名相同
    private String name;
    private int login_status;
}

