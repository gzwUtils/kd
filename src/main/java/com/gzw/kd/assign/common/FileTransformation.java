/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.gzw.kd.assign.common;
import com.gzw.kd.common.exception.GlobalException;
import org.apache.commons.codec.binary.Base64;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import static com.gzw.kd.common.enums.ResultCodeEnum.UNKNOWN_ERROR;

@SuppressWarnings("all")
public class FileTransformation {

    /**
     * 传入本地文件路径转二进制byte
     * @param srcFilePath 本地文件路径
     * @return
     * @throws GlobalException
     */
    public static byte[] fileToBytes(String srcFilePath) throws GlobalException {
        return getBytes(srcFilePath);
    }

    /**
     * 图片转base64
     *
     * @param filePath 本地文件路径
     * @return
     * @throws GlobalException
     */
    public static String fileToBase64(String filePath) throws GlobalException {
        byte[] bytes;
        String base64 = null;
        bytes = fileToBytes(filePath);
        base64 = Base64.encodeBase64String(bytes);
        base64 = base64.replaceAll("\r\n", "");
        return base64;
    }

    /***
     * 计算文件内容的Content-MD5
     * @param filePath 文件路径
     * @return
     */
    public static String getFileContentMD5(String filePath) throws GlobalException {
        // 获取文件MD5的二进制数组（128位）
        byte[] bytes = getFileMD5Bytes128(filePath);
        // 对文件MD5的二进制数组进行base64编码
        return new String(Base64.encodeBase64String(bytes));
    }

    /**
     * 下载文件
     *
     * @param httpUrl 网络文件地址url
     * @return
     */
    public static boolean downLoadFileByUrl(String httpUrl, String dir) throws GlobalException {
        InputStream fis = null;
        FileOutputStream fileOutputStream = null;
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.connect();
            fis = httpConn.getInputStream();
            fileOutputStream = new FileOutputStream(new File(dir));
            byte[] md5Bytes = null;

            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                fileOutputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw new GlobalException("获取文件流异常",UNKNOWN_ERROR.getCode());
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                throw new GlobalException("关闭文件流异常",UNKNOWN_ERROR.getCode());
            }
        }
        return true;
    }


    /**
     * 网络文件转二进制MD5数组并获取文件大小
     *
     * @param fileUrl 网络文件地址url
     * @return
     */
    public static Map fileUrlToBytes(String fileUrl) throws GlobalException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.connect();
            InputStream fis = httpConn.getInputStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            outStream.close();
            map.put("fileSize", fis.available());
            byte[] md5Bytes = null;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                md5.update(buffer, 0, length);
                outStream.write(buffer, 0, length);
            }
            md5Bytes = md5.digest();
            byte[] fileData = outStream.toByteArray();
            map.put("fileData", fileData);
            outStream.close();
            fis.close();
            map.put("md5Bytes", md5Bytes);
        } catch (IOException e) {
            throw new GlobalException("获取文件流异常",UNKNOWN_ERROR.getCode());
        } catch (NoSuchAlgorithmException e) {
            throw new GlobalException("文件计算异常",UNKNOWN_ERROR.getCode());
        }
        return map;
    }

    /***
     * 获取文件MD5的二进制数组（128位）
     * @param filePath
     * @return
     * @throws GlobalException
     */
    public static byte[] getFileMD5Bytes128(String filePath) throws GlobalException {
        FileInputStream fis = null;
        byte[] md5Bytes = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                md5.update(buffer, 0, length);
            }
            md5Bytes = md5.digest();
            fis.close();
        } catch (FileNotFoundException e) {
            throw new GlobalException("文件找不到",UNKNOWN_ERROR.getCode());
        } catch (NoSuchAlgorithmException e) {
            throw new GlobalException("不支持此算法",UNKNOWN_ERROR.getCode());
        } catch (IOException e) {
            throw new GlobalException("输入流或输出流异常",UNKNOWN_ERROR.getCode());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new GlobalException("关闭文件输入流失败",UNKNOWN_ERROR.getCode());
                }
            }
        }
        return md5Bytes;
    }

    /**
     * @param path
     * @return
     * @throws GlobalException
     * @description 根据文件路径，获取文件base64
     */
    public static String getBase64Str(String path) throws GlobalException {
        InputStream is = null;
        try {
            is = new FileInputStream(new File(path));
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            return Base64.encodeBase64String(bytes);
        } catch (Exception e) {
            throw new GlobalException("获取文件输入流失败",UNKNOWN_ERROR.getCode());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new GlobalException("关闭文件输入流失败",UNKNOWN_ERROR.getCode());
                }
            }
        }
    }

    /**
     * @param path 文件路径
     * @return
     * @description 获取文件名称
     */
    public static String getFileName(String path) {
        return new File(path).getName();
    }

    /**
     * @param filePath {@link String} 文件地址
     * @return
     * @throws GlobalException
     * @description 获取文件字节流
     */
    public static byte[] getBytes(String filePath) throws GlobalException {
        File file = new File(filePath);
        FileInputStream fis = null;
        byte[] buffer = null;
        try {
            fis = new FileInputStream(file);
            buffer = new byte[(int) file.length()];
            fis.read(buffer);
        } catch (Exception e) {
            throw new GlobalException("获取文件字节流失败",UNKNOWN_ERROR.getCode());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new GlobalException("关闭文件字节流失败",UNKNOWN_ERROR.getCode());
                }
            }
        }
        return buffer;
    }
}
