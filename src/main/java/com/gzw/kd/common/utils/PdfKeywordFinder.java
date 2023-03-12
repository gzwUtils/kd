package com.gzw.kd.common.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.itextpdf.awt.geom.Rectangle2D.Float;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.*;

/**
 * @author gaozhiwei
 */

@SuppressWarnings("all")
public class PdfKeywordFinder {


    /**
     * @description: 查找插入签名图片的最终位置，因为是插入签名图片，所以之前的关键字应只会出现一次
     * 这里直接使用了第一次查找到关键字的位置，并返回该关键字之后的坐标位置
     * @return: float[0]:页码，float[1]:最后一个字的x坐标，float[2]:最后一个字的y坐标
     */
    public static float[] getAddImagePositionXy(String pdfName, String keyword) throws IOException {
        float[] temp = new float[3];
        List<float[]> positions = PdfKeywordFinder.findKeywordPositions(pdfName, keyword);
        temp[0] = positions.get(0)[0];
        temp[1] = positions.get(0)[1] + (keyword.length() * positions.get(0)[3]);
        temp[2] = positions.get(0)[2] - positions.get(0)[3];
        return temp;
    }

    /**
     * findKeywordPositions
     *  返回查找到关键字的首个文字的左上角坐标值
     *
     * @param pdfName pdf name
     * @param keyword  keyword
     * @return List<float [ ]> : float[0]:pageNum float[1]:x float[2]:y
     * @throws IOException
     */
    public static List<float[]> findKeywordPositions(String pdfName, String keyword) throws IOException {
        File pdfFile = new File(pdfName);
        byte[] pdfData = new byte[(int) pdfFile.length()];
        FileInputStream inputStream = new FileInputStream(pdfFile);
        inputStream.read(pdfData);
        inputStream.close();
        List<float[]> result = new ArrayList<>();
        List<PdfPageContentPositions> pdfPageContentPositions = getPdfContentPositionsList(pdfData);
        for (PdfPageContentPositions pdfPageContentPosition : pdfPageContentPositions) {
            List<float[]> charPositions = findPositions(keyword, pdfPageContentPosition);
            if (charPositions.size() < 1) {
                continue;
            }
            result.addAll(charPositions);
        }
        return result;
    }

    private static List<PdfPageContentPositions> getPdfContentPositionsList(byte[] pdfData) throws IOException {
        PdfReader reader = new PdfReader(pdfData);

        List<PdfPageContentPositions> result = new ArrayList<>();
        int pages = reader.getNumberOfPages();
        for (int pageNum = 1; pageNum <= pages; pageNum++) {
            float width = reader.getPageSize(pageNum).getWidth();
            float height = reader.getPageSize(pageNum).getHeight();

            PdfRenderListener pdfRenderListener = new PdfRenderListener(pageNum, width, height);
            PdfContentStreamProcessor processor = new PdfContentStreamProcessor(pdfRenderListener);
            PdfDictionary pageDic = reader.getPageN(pageNum);
            PdfDictionary resourcesDic = pageDic.getAsDict(PdfName.RESOURCES);
            try {
                processor.processContent(ContentByteUtils.getContentBytesForPage(reader, pageNum), resourcesDic);
            } catch (IOException e) {
                reader.close();
                throw e;
            }
            String content = pdfRenderListener.getContent();
            List<CharPosition> charPositions = pdfRenderListener.getcharPositions();

            List<float[]> positionsList = new ArrayList<>();
            for (CharPosition charPosition : charPositions) {
                float[] positions = new float[]{charPosition.getPageNum(), charPosition.getX(), charPosition.getY(), charPosition.getCharWidth()};
                positionsList.add(positions);
            }

            PdfPageContentPositions pdfPageContentPositions = new PdfPageContentPositions();
            pdfPageContentPositions.setContent(content);
            pdfPageContentPositions.setPostions(positionsList);

            result.add(pdfPageContentPositions);
        }
        reader.close();
        return result;
    }


    private static List<float[]> findPositions(String keyword, PdfPageContentPositions pdfPageContentPositions) {

        List<float[]> result = new ArrayList<>();

        String content = pdfPageContentPositions.getContent();
        List<float[]> charPositions = pdfPageContentPositions.getPositions();

        for (int pos = 0; pos < content.length(); ) {
            int positionIndex = content.indexOf(keyword, pos);
            if (positionIndex == -1) {
                break;
            }
            float[] postions = charPositions.get(positionIndex);
            result.add(postions);
            pos = positionIndex + 1;
        }
        return result;
    }

    private static class PdfPageContentPositions {
        private String content;
        private List<float[]> positions;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<float[]> getPositions() {
            return positions;
        }

        public void setPostions(List<float[]> positions) {
            this.positions = positions;
        }
    }

    private static class PdfRenderListener implements RenderListener {
        private final int pageNum;
        private final float pageWidth;
        private final float pageHeight;
        private final StringBuilder contentBuilder = new StringBuilder();
        private final List<CharPosition> charPositions = new ArrayList<>();

        public PdfRenderListener(int pageNum, float pageWidth, float pageHeight) {
            this.pageNum = pageNum;
            this.pageWidth = pageWidth;
            this.pageHeight = pageHeight;
        }

        @Override
        public void beginTextBlock() {

        }

        @Override
        public void renderText(TextRenderInfo textRenderInfo) {
            List<TextRenderInfo> characterRenderInfos = textRenderInfo.getCharacterRenderInfos();
            for (TextRenderInfo renderInfo : characterRenderInfos) {
                String word = renderInfo.getText();
                if (word.length() > 1) {
                    word = word.substring(word.length() - 1, word.length());
                }
                Float rectangle = renderInfo.getAscentLine().getBoundingRectange();
                float x = (float) rectangle.getMinX();
                float y = (float) rectangle.getMinY();
                float charWidth = (float) (rectangle.getMaxX() - rectangle.getMinX());
                //也可以返回坐标相对于pdf页面大小的百分比
                float xPercent = Math.round(x / pageWidth * 10000) / 10000f;
                float yPercent = Math.round((1 - y / pageHeight) * 10000) / 10000f;

                CharPosition charPosition = new CharPosition(pageNum, x, y, charWidth);
                charPositions.add(charPosition);
                contentBuilder.append(word);
            }
        }

        @Override
        public void endTextBlock() {

        }

        @Override
        public void renderImage(ImageRenderInfo renderInfo) {

        }

        public String getContent() {
            return contentBuilder.toString();
        }

        public List<CharPosition> getcharPositions() {
            return charPositions;
        }
    }

    private static class CharPosition {
        private int pageNum = 0;
        private float x = 0;
        private float y = 0;
        private float charWidth = 0;//单个文字的宽度

        public CharPosition(int pageNum, float x, float y, float charWidth) {
            this.pageNum = pageNum;
            this.x = x;
            this.y = y;
            this.charWidth = charWidth;
        }

        public int getPageNum() {
            return pageNum;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getCharWidth() {
            return charWidth;
        }

        @Override
        public String toString() {
            return "[pageNum=" + this.pageNum + ",x=" + this.x + ",y=" + this.y + "]";
        }
    }
}


