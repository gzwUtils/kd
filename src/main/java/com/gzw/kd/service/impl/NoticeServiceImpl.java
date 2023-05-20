package com.gzw.kd.service.impl;


import com.gzw.kd.common.entity.Notice;
import com.gzw.kd.mapper.NoticeMapper;
import com.gzw.kd.service.NoticeService;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author gzw
 * @description： gpt
 * @since：2023/4/4 16:19
 */
@Slf4j
@Service
public class NoticeServiceImpl implements NoticeService {

    @Resource
    private NoticeMapper noticeMapper;


    @Override
    public Integer add(Notice notice) {
        return noticeMapper.add(notice);
    }

    @Override
    public String getContext() {
        return noticeMapper.getContext();
    }
}
