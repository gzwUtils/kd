package com.gzw.kd.common.utils;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * io流工具包
 *
 * @author gaozhiwei
 */
@SuppressWarnings("all")
public class IOUtils {

    /**
     * 将inputStream流写入相对位置, 需要注意的是参数流不会在方法内关闭, 使用者有责任对参数流进行关闭
     *
     * @param inputStream io
     * @param file_name   文件名
     * @return 目标文件对象
     * @throws IOException
     */
    public static synchronized File copyByIo(InputStream inputStream, String fileName) throws IOException {
        BufferedInputStream bufferedInputStream = null;
        File file;
        FileOutputStream out = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(inputStream);
            file = new File(fileName);
            out = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(out);
            byte[] b = new byte[1024];
            int temp;
            while ((temp = bufferedInputStream.read(b)) != -1) {
                bufferedOutputStream.write(b, 0, temp);
            }
        } finally {
            close(bufferedOutputStream, out, bufferedInputStream);
        }
        return file;
    }

    /**
     * 关流
     *
     * @param closeables 需要关闭的对象
     * @throws IOException io
     */
    public static void close(Closeable... closeables) throws IOException {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                closeable.close();
            }
        }
    }

    /**
     * 使用nio方式对本地文件进行copy
     * 速度较快
     * <p>输入流不会被关闭,调用者有责任将其关闭</p>
     *
     * @param in     文件io流
     * @param target 目标文件路径
     * @return 目标文件对象
     * @throws IOException io exception
     */
    public static synchronized File copyByNio(FileInputStream in, String target) throws IOException {
        File target_file = new File(target);
        File parentFile = target_file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (!target_file.exists()) {
            target_file.createNewFile();
        }

        FileOutputStream out = new FileOutputStream(target_file);

        FileChannel inChannel = in.getChannel();
        FileChannel outChannel = out.getChannel();

        outChannel.transferFrom(inChannel, 0, inChannel.size());

        close(outChannel, inChannel, out);

        return target_file;
    }

    /**
     * 使用nio方式对本地文件进行copy
     * 速度较快
     *
     * @param source 本地文件路径
     * @param target 目标文件路径
     * @return 目标文件对象
     * @throws IOException io exception
     */
    public static synchronized File copyByNio(String source, String target) throws IOException {
        File source_file = new File(source);
        File target_file = new File(target);
        if (!source_file.exists()) {
            throw new IOException("文件不存在");
        }
        if (!target_file.exists()) {
            target_file.createNewFile();
        }

        FileInputStream in = new FileInputStream(source_file);
        FileOutputStream out = new FileOutputStream(target_file);

        FileChannel inChannel = in.getChannel();
        FileChannel outChannel = out.getChannel();

        outChannel.transferFrom(inChannel, 0, inChannel.size());

        close(outChannel, inChannel, out, in);

        return target_file;
    }


    /**
     * 使用nio以及文件映射方式对本地文件进行copy
     *
     * @param source 本地文件路径
     * @param target 目标文件路径
     * @return 目标文件对象
     * @throws IOException io exception
     */
    public static synchronized File copyByNioAndMapper(String source, String target) throws IOException {
        File source_file = new File(source);
        File target_file = new File(target);
        if (!source_file.exists()) {
            throw new IOException("文件不存在");
        }
        if (!target_file.exists()) {
            target_file.createNewFile();
        }

        FileInputStream in = new FileInputStream(source_file);
        FileOutputStream out = new FileOutputStream(target_file);

        FileChannel inChannel = in.getChannel();
        FileChannel outChannel = out.getChannel();

        MappedByteBuffer mbb = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        outChannel.write(mbb);
        close(outChannel, out, inChannel, in);

        return target_file;
    }

    /**
     * 本地copy流
     *
     * @param source
     * @param target
     * @return
     * @throws IOException
     */
    public static synchronized File copyByIo(String source, String target) throws IOException {
        InputStream inputStream = null;
        BufferedInputStream bufferedInputStream = null;
        File file;
        FileOutputStream out = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            inputStream = new FileInputStream(source);
            bufferedInputStream = new BufferedInputStream(inputStream);
            file = new File(target);
            out = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(out);
            byte[] b = new byte[1024];
            int temp;
            while ((temp = bufferedInputStream.read(b)) != -1) {
                bufferedOutputStream.write(b, 0, temp);
            }
        } finally {
            close(bufferedOutputStream, out, bufferedInputStream, inputStream);
        }
        return file;
    }

}
