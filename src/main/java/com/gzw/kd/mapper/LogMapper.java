package com.gzw.kd.mapper;



import com.gzw.kd.common.entity.Log;
import com.gzw.kd.vo.output.LogExportOutput;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("all")
@Repository
@Mapper
public interface LogMapper {

    //导出日志信息
    List<LogExportOutput> getLogInfo(LocalDateTime time);

    /**
     * 获取操作记录
     * @param name
     * @return
     */
    List<Log> getAllOperation(String name);

}
