package com.gzw.kd.assign.common;

import com.gzw.kd.common.exception.GlobalException;
import lombok.Getter;

import java.io.File;

import static com.gzw.kd.common.enums.ResultCodeEnum.FILE_NOT_EXIST;


/**
 * @author 高志伟
 */
@Getter
@SuppressWarnings("all")
public class EsignFileBean {

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小
     */
    private int fileSize;

    /**
     * 文件内容MD5
     */
    private String fileContentMD5;
    /**
     * 文件地址
     */
    private String filePath;


    public EsignFileBean(String filePath) throws GlobalException {
        this.filePath = filePath;
        this.fileContentMD5 = FileTransformation.getFileContentMD5(filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            throw new GlobalException(FILE_NOT_EXIST);
        }
        this.fileName = file.getName();
        this.fileSize = (int) file.length();
    }

    /**
     * 传入本地文件地址获取二进制数据
     *
     * @return
     * @throws GlobalException
     */
    public byte[] getFileBytes() throws GlobalException {
        return FileTransformation.fileToBytes(filePath);
    }
}
