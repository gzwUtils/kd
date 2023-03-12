package com.gzw.kd.vo.input;

import com.gzw.kd.common.entity.Menu;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author 高志伟
 */

@Accessors(chain = true)
@Data
public class RequestVo {

    private List<Menu> menuList;
}
