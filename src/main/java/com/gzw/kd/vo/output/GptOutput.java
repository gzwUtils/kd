package com.gzw.kd.vo.output;

import com.gzw.kd.common.entity.GptChoice;
import java.util.List;
import lombok.Data;

/**
 * @author gzw
 * @description：
 * @since：2023/4/4 16:07
 */

@Data
public class GptOutput {

    private String id;

    private String object;

    private String created;

    private String model;

    private List<GptChoice> choices;
}
