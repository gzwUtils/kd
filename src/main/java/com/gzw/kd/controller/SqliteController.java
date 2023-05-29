package com.gzw.kd.controller;

import com.gzw.kd.common.R;
import com.gzw.kd.service.SqliteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gzw
 * @description： sqlite
 * @since：2023/5/18 09:39
 */
@Slf4j
@SuppressWarnings("all")
@Api(tags = "sqlite")
@RequestMapping("/sqlite")
@RestController
public class SqliteController {

    @Resource
    private SqliteService sqliteService;

    @ApiOperation("sqlite插入")
    @RequestMapping(value = "/insert", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R insert(@RequestParam("sql") @ApiParam("insertSql") String sql) {
        StopWatch watch = new StopWatch();
        watch.start();
        sqliteService.insert(sql);
        watch.stop();
        log.warn("insert sql 耗时:{} 毫秒",watch.getLastTaskTimeMillis());
        return R.ok();
    }

    @ApiOperation("sqlite查询")
    @RequestMapping(value = "/select", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R select(@RequestParam("sql") @ApiParam("selectSql") String sql) {
        StopWatch watch = new StopWatch();
        watch.start();
        List<Map<String, Object>> list = sqliteService.select(sql);
        watch.stop();
        log.warn("select sql 耗时:{} 毫秒",watch.getLastTaskTimeMillis());
        return R.ok().data("list",list);
    }


    @ApiOperation("sqlite建表")
    @RequestMapping(value = "/createSql", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R createSql(@RequestParam("sql") @ApiParam("createSql") String sql) {
        sqliteService.createSql(sql);
        return R.ok();
    }


    @ApiOperation("sqlite批量插入")
    @RequestMapping(value = "/batchInsert", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public R batchInsert(@RequestParam("sql") @ApiParam("insertSql") String sql) {
        StopWatch watch = new StopWatch();
        watch.start();
        StringBuilder stringBuilder =new StringBuilder(sql);
        for(int i=0;i<200000;i++){
            stringBuilder.append("("+i+","+"'妞妞'"+","+"'灰灰'"+"),");
            if(i%10000==0){
               String  batchSql = stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
                sqliteService.insert(batchSql);
                stringBuilder = new StringBuilder(sql);
            }
        }
        watch.stop();
        log.warn(" batch insert sql 耗时:{} 毫秒",watch.getLastTaskTimeMillis());
        return R.ok();
    }


}
