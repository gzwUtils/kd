package com.gzw.kd.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @description： 图片上传
 * @since：2024/2/24 23:52
 */
@Accessors(chain = true)
@Data
public class WxMediaUploadResult {

    private String type;
    private String mediaId;
    private long createdAt;
    private String url;
}
