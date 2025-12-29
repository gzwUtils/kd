package com.gzw.kd.controller;


import com.gzw.kd.common.R;
import com.gzw.kd.service.MessageRecordService;
import com.gzw.kd.vo.input.MessageQueryInput;
import com.gzw.kd.vo.output.ReadMessageOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {


    private final MessageRecordService messageService;

    /**
     * 获取消息列表
     */
    @PostMapping("/list")
    public R getMessages(@RequestBody MessageQueryInput queryDTO) {
        return R.ok().data("list",messageService.getMessages(queryDTO));
    }

    /**
     * 获取消息详情
     */
    @GetMapping("/detail")
    public R getMessageDetail(
            @RequestParam Long id) {
        return R.ok().data("detail",messageService.getMessageById(id));
    }

    /**
     * 标记消息为已读
     */
    @PostMapping("/read")
    public R markAsRead(@Valid @RequestBody ReadMessageOutput readDTO) {
        messageService.markAsRead(readDTO.getId());
        return R.ok();
    }

    /**
     * 标记所有消息为已读
     */
    @PostMapping("/read-all")
    public R markAllAsRead() {
        messageService.markAllAsRead();
        return R.ok();
    }

    /**
     * 删除单条消息
     */
    @DeleteMapping("/delete")
    public R deleteMessage(
            @RequestParam Long id) {
        messageService.deleteMessage(id);
        return R.ok();
    }

    /**
     * 清空所有消息
     */
    @DeleteMapping("/clear-all")
    public R clearAllMessages() {
        messageService.clearAllMessages();
        return R.ok();
    }

    /**
     * 获取消息统计
     */
    @GetMapping("/count")
    public R getMessageCount() {
        return R.ok().data("count",messageService.getMessageCount());
    }
}
