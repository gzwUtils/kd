package com.gzw.kd.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.websocket.Session;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OnlineUser {


    private String uid;
    private String name;
    private Session session;
}
