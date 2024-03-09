package com.gzw.kd.common.entity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gzw
 * @description：
 * @since：2024/2/24 23:25
 */
@Accessors(chain = true)
@Data
public class WxNewsInfo {

    /**标题 必填*/
    private String title;
    /**图文消息的封面图片素材id*/
    private String thumbMediaId;
    /***/
    private String thumbUrl;
    /**是否在正文显示封面。平台已不支持此功能，因此默认为0，即不展示*/
    private int showCoverPic;
    /**作者 */
    private String author;
    /*** 图文消息的摘要*/
    private String digest;
    /*** 图文消息的具体内容，支持HTML标签，必须少于2万字符，小于1M*/
    private String content;
    /*** 临时链接*/
    private String url;
    /*** 图文消息的原文地址*/
    private String contentSourceUrl;
    /*** Uint32 是否打开评论，0不打开(默认)，1打开*/
    private int needOpenComment;
    /*** Uint32 是否粉丝才可评论，0所有人可评论(默认)，1粉丝才可评论*/
    private int onlyFansCanComment;
}
