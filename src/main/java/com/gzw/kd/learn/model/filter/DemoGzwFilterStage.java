package com.gzw.kd.learn.model.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gzw.kd.common.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @since 2020/9/25
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class DemoGzwFilterStage {

    User user;

    Integer userId;

    List<Map<String, Object>> stageHandlerResult = new ArrayList<>();

    public DemoGzwFilterStage(User user, Integer userId) {
        this.user = user;
        this.userId = userId;
    }
}