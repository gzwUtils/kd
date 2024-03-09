package com.gzw.kd.service;

import com.gzw.kd.common.entity.WxMediaUploadResult;
import com.gzw.kd.common.entity.WxNewsInfo;
import java.io.File;
import java.util.List;

/**
 * @author gzw
 * @description： 公众号推送
 * @since：2024/2/24 23:18
 */
public interface WechatService {

    /**
     * 推送图文消息
     * @return result
     */
    boolean sendMessageToAll();

    /**
     * 推送模版消息
     * @return res
     */
    boolean sendTemplate();

    /**
     * 上传图片
     * @param mediaType 媒体类型
     * @param var2 文件
     * @return res
     */
    WxMediaUploadResult uploadMedia(String mediaType, File var2);


    /**
     * 上传图文素材
     * @param var1 context
     * @return json
     */
    String addNewsMedia(List<WxNewsInfo> var1);
}
