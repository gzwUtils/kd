package com.gzw.kd.common.utils;

import static com.gzw.kd.common.Constants.*;
import com.gzw.kd.common.R;
import com.gzw.kd.common.entity.WaterMarkContent;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.exception.GlobalException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.time.LocalDateTime;

/**
 * 文件上传
 *
 * @author 高志伟
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class FileUploadUtil {

    @Value("${bio.uploadPath}")
    private String basedir;


    private static final float OPACITY = 0.8f;


    public R upload(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return R.error().message("请上传文件");
        }
        String projectPath = System.getProperty("user.dir");
        String originalFilename = file.getOriginalFilename();
        log.info("file upload name {} {} ", originalFilename, LocalDateTime.now());
        if (StringUtils.isBlank(originalFilename)) {
            return R.error().message("文件名称不能为空");
        }
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!FILE_SUFFIX.contains(suffix)) {
            return R.error().message("文件格式错误");
        }

        String path = projectPath + basedir + "/" + LocalDateTime.now().format(DATE_FORMAT);
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            File out = new File(dir, LocalDateTime.now().format(DATE_TIME_FORMAT_HM) + "_" + originalFilename);
            file.transferTo(out);
            waterMark(null,suffix,out.getPath(),OPACITY);
            return R.ok();
        } catch (Exception e) {
            log.error("upload file  error {}", e.getMessage(), e);
            throw new GlobalException(ResultCodeEnum.UNKNOWN_ERROR);
        }
    }


    public String encrypt(MultipartFile file, String password) throws IOException {
        String path = System.getProperty("user.dir") + basedir + "/" + LocalDateTime.now().format(DATE_FORMAT);
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".") + 1);
        File out = new File(dir, LocalDateTime.now().format(DATE_TIME_FORMAT_HM) + "_" + file.getOriginalFilename());
        switch (suffix) {
            case "pdf":
                PdfUtils.pdfEncrypt(file.getInputStream(), out.getPath(), password);
                break;
            default:
                break;
        }
        return out.getPath();
    }


    public void waterMarkToPdf(WaterMarkContent content, String path) {
        try {
            WaterMarkUtil.addWaterMark(path, path,content);
        } catch (Exception e) {
            log.error("pdf水印添加失败 path:{}", path, e);
        }
    }

    public void waterMarkToImg(WaterMarkContent content, String path, float opacity) {
        BufferedImage image = WaterMarkUtil.createWatermarkImage(content);
        try {
            Objects.requireNonNull(image);
            WaterMarkUtil.setWaterMarkToImg(path, image, opacity);
        } catch (Exception e) {
            log.error("img 水印添加失败 path:{}", path, e);
        }
    }


    public void waterMark(WaterMarkContent content, String suffix, String path, float opacity) {
        switch (suffix) {
            case ".pdf":
                waterMarkToPdf(content, path);
                break;
            case ".jpg":
                waterMarkToImg(content, path, opacity);
                break;
            default:
                log.warn("suffix {} no support  waterMark ", suffix);
                break;
        }

    }
}
