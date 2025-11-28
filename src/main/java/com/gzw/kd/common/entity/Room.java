package com.gzw.kd.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Data
public class Room {

    private String roomId;
    private String roomName;
    private Set<String> members = ConcurrentHashMap.newKeySet();
}
