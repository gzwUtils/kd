package com.gzw.kd.common.utils;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gzw
 * @description： pdf generate
 * @since：2022/11/5 16:55
 */
@Slf4j
@SuppressWarnings({"unused"})
public class PdfUtils {


    /**
     * 根据PDF模版生成PDF文件
     * @param templateFilePath PDF模版文件路径
     * @param data 表单数据
     * @param imageData 图片数据 VALUE为图片文件路径
     * @param formFlattening false：生成后的PDF文件表单域仍然可编辑 true：生成后的PDF文件表单域不可编辑
     * @param pdfFilePath 生成PDF的文件路径
     */
    public static void createPdf(String templateFilePath, HashMap<String,String> data, HashMap<String,String> imageData,
                                  boolean formFlattening, String pdfFilePath) throws Exception {
        PdfReader reader = null;
        ByteArrayOutputStream bos = null;
        PdfStamper pdfStamper;
        FileOutputStream fos = null;
        try {
            // 读取PDF模版文件
            reader = new PdfReader(templateFilePath);
            // 输出流
            bos = new ByteArrayOutputStream();
            // 构建PDF对象
            pdfStamper = new PdfStamper(reader, bos);

            // 获取表单数据
            AcroFields form = pdfStamper.getAcroFields();

            // 使用中文字体 使用 AcroFields填充值的不需要在程序中设置字体，在模板文件中设置字体为中文字体 Adobe 宋体 std L
            BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            form.addSubstitutionFont(bfChinese);

            // 表单赋值
            for (String key : data.keySet()) {
                form.setField(key, data.get(key));
                // 也可以指定字体
                form.setFieldProperty(key, "textfont", bfChinese, null);
            }

            // 添加图片
            if (null != imageData && imageData.size() > 0) {
                for (String key : imageData.keySet()) {
                    int pageNo = form.getFieldPositions(key).get(0).page;
                    Rectangle signRect = form.getFieldPositions(key).get(0).position;
                    float x = signRect.getLeft();
                    float y = signRect.getBottom();
                    // 读图片
                    Image image = Image.getInstance(imageData.get(key));
                    // 获取操作的页面
                    PdfContentByte under = pdfStamper.getOverContent(pageNo);
                    // 根据域的大小缩放图片
                    image.scaleToFit(signRect.getWidth(), signRect.getHeight());
                    // 添加图片
                    image.setAbsolutePosition(x, y);
                    under.addImage(image);
                }
            }

            // 如果为false那么生成的PDF文件还能编辑，一定要设为true
            pdfStamper.setFormFlattening(formFlattening);
            pdfStamper.close();

            // 保存文件
            fos = new FileOutputStream(pdfFilePath);
            fos.write(bos.toByteArray());
            fos.flush();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (null != bos) {
                try {
                    bos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (null != reader) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     *  PDF文件流合成
     * @param streamOfPdfFiles  PDF文件流
     * @param outputStream out
     * @param paginate paginate
     * @return stream
     */
    public static OutputStream concatPdfs(List<InputStream> streamOfPdfFiles, OutputStream outputStream, boolean paginate) {

        Document document = new Document();
        try {
            List<PdfReader> readers = new ArrayList<>();
            int totalPages = 0;

            for (InputStream pdf : streamOfPdfFiles) {
                PdfReader pdfReader = new PdfReader(pdf);
                readers.add(pdfReader);
                totalPages += pdfReader.getNumberOfPages();
            }
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();
            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
                    BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            PdfContentByte cb = writer.getDirectContent();

            PdfImportedPage page;
            int currentPageNumber = 0;
            int pageOfCurrentReaderPdf = 0;

            for (PdfReader pdfReader : readers) {
                //pdf页数处理
                while (pageOfCurrentReaderPdf < pdfReader.getNumberOfPages()) {
                    document.newPage();
                    pageOfCurrentReaderPdf++;
                    currentPageNumber++;
                    page = writer.getImportedPage(pdfReader,
                            pageOfCurrentReaderPdf);
                    cb.addTemplate(page, 0, 0);

                    if (paginate) {
                        cb.beginText();
                        cb.setFontAndSize(bf, 9);
                        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, ""
                                        + currentPageNumber + " of " + totalPages, 520,
                                5, 0);
                        cb.endText();
                    }
                }
                pageOfCurrentReaderPdf = 0;
            }
            outputStream.flush();
            document.close();
            outputStream.close();
        } catch (Exception e) {
            log.error("merge pdf error  ", e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception ioe) {
                log.error("out close error  ", ioe);
            }
        }
        return outputStream;
    }

    /**
     *
     * @param filePath 文件路径
     * @param savePath 保存路径
     * @param password 密码
     * @return 加密
     */
    public static boolean pdfEncrypt(String filePath, String savePath, String password) {
        try {
            return pdfEncrypt(new FileInputStream(filePath), savePath, password);
        } catch (FileNotFoundException e) {
            log.error("file not found error ", e);
            return false;
        }
    }


    /**
     *
     * @param inputStream 输入流
     * @param savePath 保存路径
     * @param password 密码
     * @return 加密
     */
    public static boolean pdfEncrypt(InputStream inputStream, String savePath, String password) {
        PdfReader reader = null;
        PdfStamper stamper = null;
        try {
            reader = new PdfReader(inputStream);
            stamper = new PdfStamper(reader, Files.newOutputStream(Paths.get(savePath)));

            /*
              ALLOW_PRINTING	文档允许打印
              ALLOW_DEGRADED_PRINTING	允许用户打印文档，但不提供allow_printing质量（128位加密）
              ALLOW_MODIFY_CONTENTS	允许用户修改内容，例如 更改页面内容，或插入或删除页
              ALLOW_ASSEMBLY	允许用户插入、删除和旋转页面和添加书签。页面的内容不能更改，除非也授予allow_modify_contents权限，（128位加密）
              ALLOW_COPY	允许用户复制或以其他方式从文档中提取文本和图形，包括使用辅助技术。例如屏幕阅读器或其他可访问设备
              ALLOW_SCREENREADERS	允许用户提取文本和图形以供易访问性设备使用，（128位加密）
              ALLOW_MODIFY_ANNOTATIONS	允许用户添加或修改文本注释和交互式表单字段
              ALLOW_FILL_IN	允许用户填写表单字段，（128位加密）
             */
            //访问者密码，拥有者密码(权限密码让pdf文件无法被修改)，访问者权限，加密方式。
            stamper.setEncryption(password.getBytes(), password.getBytes(), PdfWriter.ALLOW_COPY | PdfWriter.ALLOW_DEGRADED_PRINTING, PdfWriter.STANDARD_ENCRYPTION_128);
        } catch (Exception e) {
            log.error("pdf encrypt exception ", e);
            return false;
        } finally {
            try {
                if (stamper != null) {
                    stamper.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                log.error("io close error ", e);
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error("io close error ", e);
            }
        }
        return true;
    }

}
