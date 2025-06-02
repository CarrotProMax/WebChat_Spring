package com.ch.webchatspring.entity;


import lombok.Data;

@Data
public class MapUserTable {
    private Integer m_uid;
    private String m_uname;
    private String m_usex;
    @Override
    public String toString() {
        return "User [uid=" + m_uid +",uname=" + m_uname + ",usex=" + m_usex +"]";
    }
}
