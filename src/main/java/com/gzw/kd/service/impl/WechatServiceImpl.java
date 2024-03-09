package com.gzw.kd.service.impl;

import com.gzw.kd.common.entity.WxMediaUploadResult;
import com.gzw.kd.common.entity.WxNewsInfo;
import com.gzw.kd.common.exception.GlobalException;
import com.gzw.kd.service.WechatService;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

/**
 * @author gzw
 * @description： 推送
 * @since：2024/2/24 23:20
 */
@Slf4j
@Service
public class WechatServiceImpl implements WechatService {


    @Override
    public boolean sendMessageToAll() {
        boolean flag = false;
        try {
            List<WxNewsInfo> wxNewsInfoList = new ArrayList<>();
            String resourcePath = ResourceUtils.getURL("classpath:").getPath() + "static/";

            // 1.上传封面图片
            WxNewsInfo wxNewsInfo = new WxNewsInfo();
            try {
                File file = new File(resourcePath + "tt.jpg");
                WxMediaUploadResult result = uploadMedia("image", file);
                if (StringUtils.isNotBlank(result.getMediaId())) {
                    wxNewsInfo.setThumbMediaId(result.getMediaId());
                }
            } catch (GlobalException e) {
                log.info("----------上传封面发生错误:{}----------" + e.getMessage());
            }
            // 2.封装其他信息
            wxNewsInfo.setAuthor("gzw");
            wxNewsInfo.setTitle("kd");
            wxNewsInfo.setContentSourceUrl("https://juejin.cn/post/7117185295480520712");
            wxNewsInfo.setShowCoverPic(0);
            wxNewsInfo.setNeedOpenComment(1);
            wxNewsInfo.setOnlyFansCanComment(1);
            wxNewsInfo.setContent("https://juejin.cn/post/7117185295480520712");
            wxNewsInfoList.add(wxNewsInfo);

            // 3.上传群发图文素材
            String mediaId =addNewsMedia(wxNewsInfoList);
            log.info("----------上传图文素材的mediaId:{}----------" + mediaId);
            flag = true;
        } catch (Exception e) {
            log.error("群发消息失败........{}",e.getMessage());
        }
        return flag;
    }

    @Override
    public boolean sendTemplate() {
        return false;
    }

    @Override
    public WxMediaUploadResult uploadMedia(String mediaType, File var2) {
        return null;
    }

    @Override
    public String addNewsMedia(List<WxNewsInfo> var1) {
        return null;
    }
}
