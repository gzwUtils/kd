package com.gzw.kd.common.utils;

import static com.gzw.kd.common.Constants.*;
import com.gzw.kd.common.R;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.exception.GlobalException;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 文件上传
 * @author 高志伟
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class FileUploadUtil {

    @Value("${bio.uploadPath}")
    private  String basedir;


    public R upload(@RequestParam("file") MultipartFile file) {

        if(file.isEmpty()){
            return R.error().message("请上传文件");
        }
        String projectPath = System.getProperty("user.dir");
        String originalFilename = file.getOriginalFilename();
        log.info("file upload name {} {} ",originalFilename, LocalDateTime.now());
        if(StringUtils.isBlank(originalFilename)){
            return R.error().message("文件名称不能为空");
        }
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if(!FILE_SUFFIX.contains(suffix)){
            return R.error().message("文件格式错误");
        }

        String path = projectPath+basedir + "/"+ LocalDateTime.now().format(DATE_FORMAT);
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(dir,LocalDateTime.now().format(DATE_TIME_FORMAT_HM)+"_"+originalFilename));
            return R.ok();
        } catch (Exception e) {
            log.error("upload file  error {}", e.getMessage(), e);
            throw new GlobalException(ResultCodeEnum.UNKNOWN_ERROR);
        }
    }
}
