package com.gzw.kd.common.utils;

import cn.hutool.core.util.StrUtil;
import static com.gzw.kd.common.Constants.*;
import com.gzw.kd.common.entity.Operator;
import com.gzw.kd.common.entity.WaterMarkContent;
import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.exception.GlobalException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gzw
 * @description： 水印
 * @since：2023/6/28 11:37
 */
@Slf4j
@SuppressWarnings({"unused"})
public class WaterMarkUtil {

    /**
     * 水印透明度
     */

    private static final float ALPHA = 0.3f;

    /**
     * 水印横向位置
     */
    private static final int POSITION_WIDTH = 50;

    /**
     * 水印纵向位置
     */
    private static final int POSITION_HEIGHT = 100;

    /**
     * 水印字体
     */
    private static final Font FONT = new Font("宋体", Font.BOLD, 60);

    /**
     * 水印文字颜色
     */
    private static final Color COLOR = Color.red;

    /**
     * 给图片添加水印文字
     *
     * @param text       水印文字
     * @param srcImgPath 源图片路径
     * @param targetPath 目标图片路径
     */
    public static void markImage(String text, String srcImgPath, String targetPath) {
        markImage(text, srcImgPath, targetPath, null);
    }

    /**
     * 给图片添加水印文字、可设置水印文字的旋转角度
     *
     * @param text       水印文字
     * @param srcImgPath 源图片路径
     * @param targetPath 目标图片路径
     * @param degree     水印旋转
     */
    public static void markImage(String text, String srcImgPath, String targetPath, Integer degree) {

        OutputStream os = null;
        try {
            // 0、图片类型
            String type = srcImgPath.substring(srcImgPath.indexOf(".") + 1);

            // 1、源图片
            Image srcImg = ImageIO.read(new File(srcImgPath));

            int imgWidth = srcImg.getWidth(null);
            int imgHeight = srcImg.getHeight(null);

            BufferedImage buffImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);

            // 2、得到画笔对象
            Graphics2D g = buffImg.createGraphics();
            // 3、设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(srcImg.getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH), 0, 0, null);
            // 4、设置水印旋转
            if (null != degree) {
                g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);
            }
            // 5、设置水印文字颜色
            g.setColor(COLOR);
            // 6、设置水印文字Font
            g.setFont(FONT);
            // 7、设置水印文字透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, ALPHA));
            // 8、设置水印的坐标
            int x = imgWidth - 2 * getWatermarkLength(text, g);
            int y = imgHeight - 2 * getWatermarkLength(text, g);
            g.drawString(text, x, y);
            g.dispose();
            os = Files.newOutputStream(Paths.get(targetPath));
            ImageIO.write(buffImg, type.toUpperCase(), os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != os) {
                    os.close();
                }
            } catch (Exception e) {
                log.error("水印添加异常 error {}", e.getMessage(), e);
            }
        }
    }


    public static int getWatermarkLength(String waterMarkContent, Graphics2D g) {
        return g.getFontMetrics(g.getFont()).charsWidth(waterMarkContent.toCharArray(), 0, waterMarkContent.length());
    }

    /**
     * 创建图片水印
     *
     * @param watermark 水印
     * @return 图片
     */
    public static BufferedImage createWatermarkImage(WaterMarkContent watermark) {
        watermark = getMarkContent(watermark);
        if (watermark == null) {
            return null;
        }
        String[] textArray = watermark.getText().split(",");
        Font font = new Font("microsoft-yahei", Font.PLAIN, 20);
        int width = 300;
        int height = 100;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 背景透明 开始
        Graphics2D g = image.createGraphics();
        image = g.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        g.dispose();
        // 背景透明 结束
        g = image.createGraphics();
        // 设定画笔颜色
        g.setColor(new Color(Integer.parseInt(watermark.getColor().substring(1), 16)));
        // 设置画笔字体
        g.setFont(font);
        // 设定倾斜度
        g.shear(0.1, -0.26);

        // 设置字体平滑
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int y = 50;
        for (String s : textArray) {
            // 画出字符串
            g.drawString(s, 0, y);
            y = y + font.getSize();
        }
        // 画出字符串
        g.drawString(DATE_TIME_FORMAT_S.format(LocalDateTime.now()), 0, y);
        // 释放画笔
        g.dispose();
        return image;
    }

    private static WaterMarkContent getMarkContent(WaterMarkContent watermark) {
        if (watermark == null) {
            Operator operator = (Operator) ContextUtil.getHttpRequest().getSession().getAttribute(LOGIN_USER_SESSION_KEY);
            watermark = new WaterMarkContent();
            watermark.setEnable(true);
            watermark.setText(operator.getAccount());
            watermark.setColor("#C5CBCF");
            watermark.setDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        if (!watermark.isEnable()) {
            return null;
        }
        return watermark;
    }


    /**
     * 给PDF文件添加文字水印
     * @param path input
     * @param outPath out
     * @param content mark
     */
    public static void addWaterMark(String path, String outPath, WaterMarkContent content) {
        try {
            content = getMarkContent(content);
            if (content == null) {
                return;
            }
            PdfReader reader = new PdfReader(path);
            FileOutputStream out = new FileOutputStream(outPath);
            // 输出的PDF文件内容
            PdfStamper pdfStamper = new PdfStamper(reader, out);
            BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", true);
            PdfGState pdfGstate = new PdfGState();
            pdfGstate.setFillOpacity(0.2f);
            pdfGstate.setStrokeOpacity(0.4f);
            int totalPage = reader.getNumberOfPages() + 1;
            for (int i = 1; i < totalPage; i++) {
                // 内容下层
                PdfContentByte pdfContentByte = pdfStamper.getUnderContent(i);
                pdfContentByte.beginText();
                // 字体添加透明度
                pdfContentByte.setGState(pdfGstate);
                // 添加字体大小等
                pdfContentByte.setFontAndSize(baseFont, 20);
                //添加字体颜色
                pdfContentByte.setColorFill(new BaseColor(Integer.parseInt(content.getColor().substring(1), 16)));
                // 添加范围
                pdfContentByte.setTextMatrix(70, 200);
                //  具体位置 内容   多少行  多少列  旋转多少度 共360度 一页几排 一排几个
                for (int a = INT_ZERO; a < INT_THREE; a++) {
                    for (int j = INT_ZERO; j < INT_THREE; j++) {
                        // 横向  宽
                        int x = INT_70 + INT_170 * j;
                        // 纵向  高
                        int y = INT_70 + INT_200 * a;
                        // 45 是水印旋转的角度
                        pdfContentByte.showTextAligned(Element.ALIGN_BOTTOM, content.getText(), x, y, 45);
                    }
                }
                pdfContentByte.endText();
            }
            pdfStamper.close();
            reader.close();
        } catch (Exception e) {
            log.error("pdf 添加水印异常", e);
            throw new GlobalException(ResultCodeEnum.UNKNOWN_ERROR);
        }
    }

    /**
     * pdf加水印图片
     *
     * @param filePath 文件路径
     * @param bfi      bfi
     * @throws IOException       ioexception
     * @throws DocumentException 文件异常
     */
    public static void setWaterMarkToPdf(String filePath, BufferedImage bfi) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(filePath);
        //保存路径
        String downloadPath = filePath.substring(0, filePath.lastIndexOf("/") + 1) + StrUtil.subAfter(filePath, "/", true);
        FileOutputStream outputStream = new FileOutputStream(downloadPath);
        PdfStamper stamper = new PdfStamper(reader,outputStream);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(bfi, "png", out);
        //将图片放入pdf中
        com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(out.toByteArray());
        //获取pdf页数
        int num = reader.getNumberOfPages();
        com.itextpdf.text.Rectangle pageSize;
        float width = 0f;
        float height = 0f;
        int x = 0;
        for (int i = 1; i < num; i++) {
            //得到页面大小
            x++;
            if (x == 1) {
                pageSize = reader.getPageSize(i);
                width = pageSize.getWidth();
                height = pageSize.getHeight();
            }
            //水印图片设置在内容之上，之下用getUnderContent
            PdfContentByte pdfContentByte = stamper.getUnderContent(i);
            pdfContentByte.saveState();
            //设置图片的位置，参数Image.UNDERLYING是作为文字的背景显示。
            image.setAlignment(com.itextpdf.text.Image.UNDEFINED);
            //设置图片的绝对位置
            image.setAbsolutePosition((width - image.getWidth()) / 2, (height - image.getHeight()) / 2);
            pdfContentByte.addImage(image);
            pdfContentByte.endText();
            pdfContentByte.restoreState();
        }
        stamper.close();
        outputStream.close();
        reader.close();
    }




    /**
     * 图片加水印
     *
     * @param filePath 文件路径
     * @param bfi      水印
     */
    public static void setWaterMarkToImg(String filePath, BufferedImage bfi, float opacity) throws IOException {
        //获取画布
        BufferedImage read = ImageIO.read(new File(filePath));
        Graphics2D graphics = read.createGraphics();

        //缩放水印图片
        int width = bfi.getWidth();
        int height = bfi.getHeight();
        //设置比例
        float f = getScale(width, height, 0.5f);
        //获取缩放后的宽度
        int w = (int) (width * f);
        int h = (int) (height * f);
        //缩放图片
        Image image = bfi.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        //设置透明度，0->1，逐渐不透明
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        //添加水印并设置在图片的右下角
        graphics.drawImage(image, read.getWidth() - w, read.getHeight() - h, null);
        //释放资源
        graphics.dispose();

        //保存图片
        String downloadPath = filePath.substring(0, filePath.lastIndexOf("/") + 1) + StrUtil.subAfter(filePath, "/", true);
        ImageIO.write(read, filePath.substring(filePath.lastIndexOf(".") + 1), new File(downloadPath));
    }


    /**
     * 获得图片比例
     *
     * @param width  宽度
     * @param height 高度
     * @param f      f
     * @return float
     */
    public static float getScale(int width, int height, float f) {
        if (width > INT_3000) {
            f = 0.06f;
        }
        if (width > INT_1000 && width < INT_3000) {
            f = 0.1f;
        }
        if (width > INT_300 && width < INT_1000) {
            f = 0.3f;
        }
        return f;
    }
}
