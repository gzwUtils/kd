package com.gzw.kd.scheduletask;
import com.gzw.kd.common.entity.User;
import com.gzw.kd.common.enums.UserStatusEnum;
import com.gzw.kd.service.UserService;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

/**
 * @author 高志伟
 */
@Slf4j
@Component
public class LearnJob implements BaseJob {


    @Resource
    UserService userService;





    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("quartz LearnJob execute start --------------------------");
        List<User> users = userService.getAllStopUsers();
        users.forEach(p->{
            try {
                userService.updateStatusById(String.valueOf(p.getId()), UserStatusEnum.START.getStatus());
            } catch (Exception e) {
                log.error("定时更新用户状态失败............id {}",p.getId(),e);
            }
        });
        log.info("quartz LearnJob execute end --------------------------");
    }
}
