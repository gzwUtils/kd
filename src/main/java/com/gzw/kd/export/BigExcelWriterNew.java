package com.gzw.kd.export;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.comparator.IndexedComparator;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.poi.excel.ExcelBase;
import cn.hutool.poi.excel.RowUtil;
import cn.hutool.poi.excel.StyleSet;
import cn.hutool.poi.excel.WorkbookUtil;
import cn.hutool.poi.excel.cell.CellUtil;
import cn.hutool.poi.excel.style.Align;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 大数据量Excel写出(新)
 *  @author 高志伟
 */
@SuppressWarnings("all")
public class BigExcelWriterNew extends ExcelBase<BigExcelWriterNew> {

    public static final int DEFAULT_WINDOW_SIZE = SXSSFWorkbook.DEFAULT_WINDOW_SIZE;

    /** 目标文件 */
    protected File destFile;
    /** 当前行 */
    private AtomicInteger currentRow = new AtomicInteger(0);
    /** 标题行别名 */
    private Map<String, String> headerAlias;
    /** 是否只保留别名对应的字段 */
    private boolean onlyAlias;
    /** 标题顺序比较器 */
    private Comparator<String> aliasComparator;
    /** 样式集，定义不同类型数据样式 */
    private StyleSet styleSet;
    /** 样式集，定义一行内所有单元格不同类型数据样式 */
    private Iterable<StyleSet> styleSets;

    // -------------------------------------------------------------------------- Constructor start
    /**
     * 构造，默认生成xls格式的Excel文件<br>
     * 此构造不传入写出的Excel文件路径，只能调用{@link #flush(OutputStream)}方法写出到流<br>
     * 若写出到文件，还需调用{@link #setDestFile(File)}方法自定义写出的文件，然后调用{@link #flush()}方法写出到文件
     */
    public BigExcelWriterNew() {
        this(DEFAULT_WINDOW_SIZE);
    }

    /**
     * 构造<br>
     * 此构造不传入写出的Excel文件路径，只能调用{@link #flush(OutputStream)}方法写出到流<br>
     * 若写出到文件，需要调用{@link #flush(File)} 写出到文件
     *
     * @param rowAccessWindowSize 在内存中的行数
     */
    public BigExcelWriterNew(int rowAccessWindowSize) {
        this(WorkbookUtil.createSXSSFBook(rowAccessWindowSize), null);
    }

    /**
     * 构造，默认写出到第一个sheet，第一个sheet名为sheet1
     *
     * @param destFilePath 目标文件路径，可以不存在
     */
    public BigExcelWriterNew(String destFilePath) {
        this(destFilePath, null);
    }

    /**
     * 构造<br>
     * 此构造不传入写出的Excel文件路径，只能调用{@link #flush(OutputStream)}方法写出到流<br>
     * 若写出到文件，需要调用{@link #flush(File)} 写出到文件
     *
     * @param rowAccessWindowSize 在内存中的行数
     * @param sheetName sheet名，第一个sheet名并写出到此sheet，例如sheet1
     * @since 4.1.8
     */
    public BigExcelWriterNew(int rowAccessWindowSize, String sheetName) {
        this(WorkbookUtil.createSXSSFBook(rowAccessWindowSize), sheetName);
    }

    /**
     * 构造
     *
     * @param destFilePath 目标文件路径，可以不存在
     * @param sheetName sheet名，第一个sheet名并写出到此sheet，例如sheet1
     */
    public BigExcelWriterNew(String destFilePath, String sheetName) {
        this(FileUtil.file(destFilePath), sheetName);
    }

    /**
     * 构造，默认写出到第一个sheet，第一个sheet名为sheet1
     *
     * @param destFile 目标文件，可以不存在
     */
    public BigExcelWriterNew(File destFile) {
        this(destFile, null);
    }

    /**
     * 构造
     *
     * @param destFile 目标文件，可以不存在
     * @param sheetName sheet名，做为第一个sheet名并写出到此sheet，例如sheet1
     */
    public BigExcelWriterNew(File destFile, String sheetName) {
        this(destFile.exists() ? WorkbookUtil.createSXSSFBook(destFile) : WorkbookUtil.createSXSSFBook(), sheetName);
        this.destFile = destFile;
    }

    /**
     * 构造<br>
     * 此构造不传入写出的Excel文件路径，只能调用{@link #flush(OutputStream)}方法写出到流<br>
     * 若写出到文件，还需调用{@link #setDestFile(File)}方法自定义写出的文件，然后调用{@link #flush()}方法写出到文件
     *
     * @param workbook {@link SXSSFWorkbook}
     * @param sheetName sheet名，做为第一个sheet名并写出到此sheet，例如sheet1
     */
    public BigExcelWriterNew(SXSSFWorkbook workbook, String sheetName) {
        this(WorkbookUtil.getOrCreateSheet(workbook, sheetName));
    }

    /**
     * 构造<br>
     * 此构造不传入写出的Excel文件路径，只能调用{@link #flush(OutputStream)}方法写出到流<br>
     * 若写出到文件，还需调用{@link #setDestFile(File)}方法自定义写出的文件，然后调用{@link #flush()}方法写出到文件
     *
     * @param sheet {@link Sheet}
     * @since 4.0.6
     */
    public BigExcelWriterNew(Sheet sheet) {
        super(sheet);
        this.styleSet = new StyleSet(workbook);
    }

    // -------------------------------------------------------------------------- Constructor end

    @Override
    public BigExcelWriterNew setSheet(int sheetIndex) {
        // 切换到新sheet需要重置开始行
        reset();
        return super.setSheet(sheetIndex);
    }

    @Override
    public BigExcelWriterNew setSheet(String sheetName) {
        // 切换到新sheet需要重置开始行
        reset();
        return super.setSheet(sheetName);
    }

    /**
     * 重置Writer，包括：
     *
     * <pre>
     * 1. 当前行游标归零
     * 2. 清空别名比较器
     * </pre>
     *
     * @return this
     */
    public BigExcelWriterNew reset() {
        resetRow();
        this.aliasComparator = null;
        return this;
    }

    /**
     * 重命名当前sheet
     *
     * @param sheetName 新的sheet名
     * @return this
     * @since 4.1.8
     */
    public BigExcelWriterNew renameSheet(String sheetName) {
        return renameSheet(this.workbook.getSheetIndex(this.sheet), sheetName);
    }

    /**
     * 重命名sheet
     *
     * @param sheet sheet需要，0表示第一个sheet
     * @param sheetName 新的sheet名
     * @return this
     * @since 4.1.8
     */
    public BigExcelWriterNew renameSheet(int sheet, String sheetName) {
        this.workbook.setSheetName(sheet, sheetName);
        return this;
    }

    /**
     * 设置所有列为自动宽度，不考虑合并单元格<br>
     * 此方法必须在指定列数据完全写出后调用才有效。<br>
     * 列数计算是通过第一行计算的
     *
     * @return this
     * @since 4.0.12
     */
    public BigExcelWriterNew autoSizeColumnAll() {
        final int columnCount = this.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            autoSizeColumn(i);
        }
        return this;
    }

    /**
     * 设置某列为自动宽度，不考虑合并单元格<br>
     * 此方法必须在指定列数据完全写出后调用才有效。
     *
     * @param columnIndex 第几列，从0计数
     * @return this
     * @since 4.0.12
     */
    public BigExcelWriterNew autoSizeColumn(int columnIndex) {
        this.sheet.autoSizeColumn(columnIndex);
        return this;
    }

    /**
     * 设置某列为自动宽度<br>
     * 此方法必须在指定列数据完全写出后调用才有效。
     *
     * @param columnIndex 第几列，从0计数
     * @param useMergedCells 是否适用于合并单元格
     * @return this
     * @since 3.3.0
     */
    public BigExcelWriterNew autoSizeColumn(int columnIndex, boolean useMergedCells) {
        this.sheet.autoSizeColumn(columnIndex, useMergedCells);
        return this;
    }

    /**
     * 设置样式集，如果不使用样式，传入{@code null}
     *
     * @param styleSet 样式集，{@code null}表示无样式
     * @return this
     * @since 4.1.11
     */
    public BigExcelWriterNew setStyleSet(StyleSet styleSet) {
        this.styleSet = styleSet;
        return this;
    }

    /**
     * 设置样式集，如果不使用样式，传入{@code null}
     *
     * @param styleSets 样式集，{@code null}表示无样式
     * @return this
     * @since 4.1.11
     */
    public BigExcelWriterNew setStyleSets(Iterable<StyleSet> styleSets) {
        this.styleSets = styleSets;
        return this;
    }

    /**
     * 获取样式集，样式集可以自定义包括：<br>
     *
     * <pre>
     * 1. 头部样式
     * 2. 一般单元格样式
     * 3. 默认数字样式
     * 4. 默认日期样式
     * </pre>
     *
     * @return 样式集
     * @since 4.0.0
     */
    public StyleSet getStyleSet() {
        return this.styleSet;
    }

    /**
     * 获取样式集，样式集可以自定义包括：<br>
     *
     * <pre>
     * 1. 头部样式
     * 2. 一般单元格样式
     * 3. 默认数字样式
     * 4. 默认日期样式
     * </pre>
     *
     * @return 样式集
     * @since 4.0.0
     */
    public Iterable<StyleSet> getStyleSets() {
        return this.styleSets;
    }

    /**
     * 获取头部样式，获取样式后可自定义样式
     *
     * @return 头部样式
     */
    public CellStyle getHeadCellStyle() {
        return this.styleSet.getHeadCellStyle();
    }

    /**
     * 获取单元格样式，获取样式后可自定义样式
     *
     * @return 单元格样式
     */
    public CellStyle getCellStyle() {
        return this.styleSet.getCellStyle();
    }

    /**
     * 获得当前行
     *
     * @return 当前行
     */
    public int getCurrentRow() {
        return this.currentRow.get();
    }

    /**
     * 设置当前所在行
     *
     * @param rowIndex 行号
     * @return this
     */
    public BigExcelWriterNew setCurrentRow(int rowIndex) {
        this.currentRow.set(rowIndex);
        return this;
    }

    /**
     * 跳过当前行
     *
     * @return this
     */
    public BigExcelWriterNew passCurrentRow() {
        this.currentRow.incrementAndGet();
        return this;
    }

    /**
     * 跳过指定行数
     *
     * @param rows 跳过的行数
     * @return this
     */
    public BigExcelWriterNew passRows(int rows) {
        this.currentRow.addAndGet(rows);
        return this;
    }

    /**
     * 重置当前行为0
     *
     * @return this
     */
    public BigExcelWriterNew resetRow() {
        this.currentRow.set(0);
        return this;
    }

    /**
     * 设置写出的目标文件
     *
     * @param destFile 目标文件
     * @return this
     */
    public BigExcelWriterNew setDestFile(File destFile) {
        this.destFile = destFile;
        return this;
    }

    /**
     * 设置标题别名，key为Map中的key，value为别名
     *
     * @param headerAlias 标题别名
     * @return this
     * @since 3.2.1
     */
    public BigExcelWriterNew setHeaderAlias(Map<String, String> headerAlias) {
        this.headerAlias = headerAlias;
        return this;
    }

    /**
     * 清空标题别名，key为Map中的key，value为别名
     *
     * @return this
     * @since 4.5.4
     */
    public BigExcelWriterNew clearHeaderAlias() {
        this.headerAlias = null;
        return this;
    }

    /**
     * 设置是否只保留别名中的字段值，如果为true，则不设置alias的字段将不被输出，false表示原样输出
     *
     * @param isOnlyAlias 是否只保留别名中的字段值
     * @return this
     * @since 4.1.22
     */
    public BigExcelWriterNew setOnlyAlias(boolean isOnlyAlias) {
        this.onlyAlias = isOnlyAlias;
        return this;
    }

    /**
     * 增加标题别名
     *
     * @param name 原标题
     * @param alias 别名
     * @return this
     * @since 4.1.5
     */
    public BigExcelWriterNew addHeaderAlias(String name, String alias) {
        Map<String, String> headerAlias = this.headerAlias;
        if (null == headerAlias) {
            headerAlias = new LinkedHashMap<>();
        }
        this.headerAlias = headerAlias;
        headerAlias.put(name, alias);
        return this;
    }

    /**
     * 设置列宽（单位为一个字符的宽度，例如传入width为10，表示10个字符的宽度）
     *
     * @param columnIndex 列号（从0开始计数，-1表示所有列的默认宽度）
     * @param width 宽度（单位1~256个字符宽度）
     * @return this
     * @since 4.0.8
     */
    public BigExcelWriterNew setColumnWidth(int columnIndex, int width) {
        if (columnIndex < 0) {
            this.sheet.setDefaultColumnWidth(width);
        } else {
            this.sheet.setColumnWidth(columnIndex, width * 256);
        }
        return this;
    }

    /**
     * 设置行高，值为一个点的高度
     *
     * @param rownum 行号（从0开始计数，-1表示所有行的默认高度）
     * @param height 高度
     * @return this
     * @since 4.0.8
     */
    public BigExcelWriterNew setRowHeight(int rownum, int height) {
        if (rownum < 0) {
            this.sheet.setDefaultRowHeightInPoints(height);
        } else {
            final Row row = this.sheet.getRow(rownum);
            if(null != row) {
                row.setHeightInPoints(height);
            }
        }
        return this;
    }

    /**
     * 设置Excel页眉或页脚
     *
     * @param text 页脚的文本
     * @param align 对齐方式枚举 {@link Align}
     * @param isFooter 是否为页脚，false表示页眉，true表示页脚
     * @return this
     * @since 4.1.0
     */
    public BigExcelWriterNew setHeaderOrFooter(String text, Align align, boolean isFooter) {
        final HeaderFooter headerFooter = isFooter ? this.sheet.getFooter() : this.sheet.getHeader();
        switch (align) {
            case LEFT:
                headerFooter.setLeft(text);
                break;
            case RIGHT:
                headerFooter.setRight(text);
                break;
            case CENTER:
                headerFooter.setCenter(text);
                break;
            default:
                break;
        }
        return this;
    }

    /**
     * 合并当前行的单元格<br>
     * 样式为默认标题样式，可使用{@link #getHeadCellStyle()}方法调用后自定义默认样式
     *
     * @param lastColumn 合并到的最后一个列号
     * @return this
     */
    public BigExcelWriterNew merge(int lastColumn) {
        return merge(lastColumn, null);
    }

    /**
     * 合并当前行的单元格，并写入对象到单元格<br>
     * 如果写到单元格中的内容非null，行号自动+1，否则当前行号不变<br>
     * 样式为默认标题样式，可使用{@link #getHeadCellStyle()}方法调用后自定义默认样式
     *
     * @param lastColumn 合并到的最后一个列号
     * @param content 合并单元格后的内容
     * @return this
     */
    public BigExcelWriterNew merge(int lastColumn, Object content) {
        return merge(lastColumn, content, true);
    }

    /**
     * 合并某行的单元格，并写入对象到单元格<br>
     * 如果写到单元格中的内容非null，行号自动+1，否则当前行号不变<br>
     * 样式为默认标题样式，可使用{@link #getHeadCellStyle()}方法调用后自定义默认样式
     *
     * @param lastColumn 合并到的最后一个列号
     * @param content 合并单元格后的内容
     * @param isSetHeaderStyle 是否为合并后的单元格设置默认标题样式
     * @return this
     * @since 4.0.10
     */
    public BigExcelWriterNew merge(int lastColumn, Object content, boolean isSetHeaderStyle) {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");

        final int rowIndex = this.currentRow.get();
        merge(rowIndex, rowIndex, 0, lastColumn, content, isSetHeaderStyle);

        // 设置内容后跳到下一行
        if (null != content) {
            this.currentRow.incrementAndGet();
        }
        return this;
    }

    /**
     * 合并某行的单元格，并写入对象到单元格<br>
     * 如果写到单元格中的内容非null，行号自动+1，否则当前行号不变<br>
     * 样式为默认标题样式，可使用{@link #getHeadCellStyle()}方法调用后自定义默认样式
     *
     * @param lastColumn 合并到的最后一个列号
     * @param content 合并单元格后的内容
     * @param isSetHeaderStyle 是否为合并后的单元格设置默认标题样式
     * @return this
     * @since 4.0.10
     */
    public BigExcelWriterNew merge(int firstRow, int lastRow, int firstColumn, int lastColumn, Object content, boolean isSetHeaderStyle) {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");

        final CellStyle style = (isSetHeaderStyle && null != this.styleSet && null != this.styleSet.getHeadCellStyle()) ? this.styleSet.getHeadCellStyle() : this.styleSet.getCellStyle();
        CellUtil.mergingCells(this.sheet, firstRow, lastRow, firstColumn, lastColumn, style);

        // 设置内容
        if (null != content) {
            final Cell cell = getOrCreateCell(firstColumn, firstRow);
            CellUtil.setCellValue(cell, content, this.styleSet, isSetHeaderStyle);
        }
        return this;
    }

    /**
     * 写出数据，本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
     * 写出的起始行为当前行号，可使用{@link #getCurrentRow()}方法调用，根据写出的的行数，当前行号自动增加<br>
     * 样式为默认样式，可使用{@link #getCellStyle()}方法调用后自定义默认样式<br>
     * 默认的，当当前行号为0时，写出标题（如果为Map或Bean），否则不写标题
     *
     * <p>
     * data中元素支持的类型有：
     *
     * <pre>
     * 1. Iterable，既元素为一个集合，元素被当作一行，data表示多行<br>
     * 2. Map，既元素为一个Map，第一个Map的keys作为首行，剩下的行为Map的values，data表示多行 <br>
     * 3. Bean，既元素为一个Bean，第一个Bean的字段名列表会作为首行，剩下的行为Bean的字段值列表，data表示多行 <br>
     * 4. 其它类型，按照基本类型输出（例如字符串）
     * </pre>
     *
     * @param data 数据
     * @return this
     */
    public BigExcelWriterNew write(Iterable<?> data) {
        return write(data, 0 == getCurrentRow());
    }

    /**
     * 写出数据，本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
     * 写出的起始行为当前行号，可使用{@link #getCurrentRow()}方法调用，根据写出的的行数，当前行号自动增加<br>
     * 样式为单元格样式，当当前行号为0时，写出标题（如果为Map或Bean），否则不写标题
     *
     * <p>
     * data中元素支持的类型有：
     *
     * <pre>
     * 1. Iterable，既元素为一个集合，元素被当作一行，data表示多行<br>
     * 2. Map，既元素为一个Map，第一个Map的keys作为首行，剩下的行为Map的values，data表示多行 <br>
     * 3. Bean，既元素为一个Bean，第一个Bean的字段名列表会作为首行，剩下的行为Bean的字段值列表，data表示多行 <br>
     * 4. 其它类型，按照基本类型输出（例如字符串）
     * </pre>
     *
     * @param data 数据
     * @param styleSets 单元格样式
     * @return this
     */
    public BigExcelWriterNew write(Iterable<?> data, Iterable<StyleSet> styleSets) {
        return write(data, styleSets, 0 == getCurrentRow());
    }

    /**
     * 写出数据，本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
     * 写出的起始行为当前行号，可使用{@link #getCurrentRow()}方法调用，根据写出的的行数，当前行号自动增加<br>
     * 样式为默认样式，可使用{@link #getCellStyle()}方法调用后自定义默认样式
     *
     * <p>
     * data中元素支持的类型有：
     *
     * <pre>
     * 1. Iterable，既元素为一个集合，元素被当作一行，data表示多行<br>
     * 2. Map，既元素为一个Map，第一个Map的keys作为首行，剩下的行为Map的values，data表示多行 <br>
     * 3. Bean，既元素为一个Bean，第一个Bean的字段名列表会作为首行，剩下的行为Bean的字段值列表，data表示多行 <br>
     * 4. 其它类型，按照基本类型输出（例如字符串）
     * </pre>
     *
     * @param data 数据
     * @param isWriteKeyAsHead 是否强制写出标题行（Map或Bean）
     * @return this
     */
    public BigExcelWriterNew write(Iterable<?> data, boolean isWriteKeyAsHead) {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
        boolean isFirst = true;
        for (Object object : data) {
            writeRow(object, isFirst && isWriteKeyAsHead);
            if (isFirst) {
                isFirst = false;
            }
        }
        return this;
    }

    /**
     * 写出数据，本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
     * 写出的起始行为当前行号，可使用{@link #getCurrentRow()}方法调用，根据写出的的行数，当前行号自动增加<br>
     * 样式为单元格指定样式
     *
     * <p>
     * data中元素支持的类型有：
     *
     * <pre>
     * 1. Iterable，既元素为一个集合，元素被当作一行，data表示多行<br>
     * 2. Map，既元素为一个Map，第一个Map的keys作为首行，剩下的行为Map的values，data表示多行 <br>
     * 3. Bean，既元素为一个Bean，第一个Bean的字段名列表会作为首行，剩下的行为Bean的字段值列表，data表示多行 <br>
     * 4. 其它类型，按照基本类型输出（例如字符串）
     * </pre>
     *
     * @param data 数据
     * @param styleSets 单元格指定样式
     * @param isWriteKeyAsHead 是否强制写出标题行（Map或Bean）
     * @return this
     */
    public BigExcelWriterNew write(Iterable<?> data, Iterable<StyleSet> styleSets, boolean isWriteKeyAsHead) {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
        boolean isFirst = true;
        for (Object object : data) {
            writeRow(object, styleSets, isFirst && isWriteKeyAsHead);
            if (isFirst) {
                isFirst = false;
            }
        }
        return this;
    }

    /**
     * 写出数据，本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
     * 写出的起始行为当前行号，可使用{@link #getCurrentRow()}方法调用，根据写出的的行数，当前行号自动增加<br>
     * 样式为默认样式，可使用{@link #getCellStyle()}方法调用后自定义默认样式<br>
     * data中元素支持的类型有：
     *
     * <p>
     * 1. Map，既元素为一个Map，第一个Map的keys作为首行，剩下的行为Map的values，data表示多行 <br>
     * 2. Bean，既元素为一个Bean，第一个Bean的字段名列表会作为首行，剩下的行为Bean的字段值列表，data表示多行 <br>
     * </p>
     *
     * @param data 数据
     * @param comparator 比较器，用于字段名的排序
     * @return this
     * @since 3.2.3
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public BigExcelWriterNew write(Iterable<?> data, Comparator<String> comparator) {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
        boolean isFirstRow = true;
        Map<?, ?> map;
        for (Object obj : data) {
            if (obj instanceof Map) {
                map = new TreeMap<>(comparator);
                map.putAll((Map) obj);
            } else {
                map = BeanUtil.beanToMap(obj, new TreeMap<String, Object>(comparator), false, false);
            }
            writeRow(map, isFirstRow);
            if (isFirstRow) {
                isFirstRow = false;
            }
        }
        return this;
    }

    /**
     * 写出一行标题数据<br>
     * 本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
     * 写出的起始行为当前行号，可使用{@link #getCurrentRow()}方法调用，根据写出的的行数，当前行号自动+1<br>
     * 样式为默认标题样式，可使用{@link #getHeadCellStyle()}方法调用后自定义默认样式
     *
     * @param rowData 一行的数据
     * @return this
     */
    public BigExcelWriterNew writeHeadRow(Iterable<?> rowData) {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
        RowUtil.writeRow(this.sheet.createRow(this.currentRow.getAndIncrement()), rowData, this.styleSet, true);
        return this;
    }

    /**
     * 写出一行标题数据<br>
     * 本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
     * 写出的起始行为当前行号，可使用{@link #getCurrentRow()}方法调用，根据写出的的行数，当前行号自动+1<br>
     * 样式为单元格指定样式
     *
     * @param rowData 一行的数据
     * @param rowData 单元格指定样式
     * @return this
     */
    public BigExcelWriterNew writeHeadRow(Iterable<?> rowData, Iterable<StyleSet> styleSets) {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
        int i = 0;
        Cell cell;
        Row row = this.sheet.createRow(this.currentRow.getAndIncrement());
        Iterator<StyleSet> iterator = styleSets.iterator();
        for (Object value : rowData) {
            cell = row.createCell(i);
            StyleSet ss;
            if (iterator.hasNext()) {
                ss = iterator.next();
            } else {
                ss = this.styleSet;
            }
            CellUtil.setCellValue(cell, value, ss, true);
            i++;
        }
        return this;
    }

    /**
     * 写出一行，根据rowBean数据类型不同，写出情况如下：
     *
     * <pre>
     * 1、如果为Iterable，直接写出一行
     * 2、如果为Map，isWriteKeyAsHead为true写出两行，Map的keys做为一行，values做为第二行，否则只写出一行values
     * 3、如果为Bean，转为Map写出，isWriteKeyAsHead为true写出两行，Map的keys做为一行，values做为第二行，否则只写出一行values
     * </pre>
     *
     * @param rowBean 写出的Bean
     * @param isWriteKeyAsHead 为true写出两行，Map的keys做为一行，values做为第二行，否则只写出一行values
     * @return this
     * @see #writeRow(Iterable)
     * @see #writeRow(Map, boolean)
     * @since 4.1.5
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public BigExcelWriterNew writeRow(Object rowBean, boolean isWriteKeyAsHead) {
        if (rowBean instanceof Iterable) {
            return writeRow((Iterable<?>) rowBean);
        }
        Map rowMap = null;
        if (rowBean instanceof Map) {
            if (MapUtil.isNotEmpty(this.headerAlias)) {
                rowMap = MapUtil.newTreeMap((Map) rowBean, getInitedAliasComparator());
            } else {
                rowMap = (Map) rowBean;
            }
        } else if (BeanUtil.isBean(rowBean.getClass())) {
            if (MapUtil.isEmpty(this.headerAlias)) {
                rowMap = BeanUtil.beanToMap(rowBean, new LinkedHashMap<String, Object>(), false, false);
            } else {
                // 别名存在情况下按照别名的添加顺序排序Bean数据
                rowMap = BeanUtil.beanToMap(rowBean, new TreeMap<String, Object>(getInitedAliasComparator()), false, false);
            }
        } else {
            // 其它转为字符串默认输出
            return writeRow(CollUtil.newArrayList(rowBean), isWriteKeyAsHead);
        }
        return writeRow(rowMap, isWriteKeyAsHead);
    }

    /**
     * 写出一行，根据rowBean数据类型不同，写出情况如下：
     *
     * <pre>
     * 1、如果为Iterable，直接写出一行
     * 2、如果为Map，isWriteKeyAsHead为true写出两行，Map的keys做为一行，values做为第二行，否则只写出一行values
     * 3、如果为Bean，转为Map写出，isWriteKeyAsHead为true写出两行，Map的keys做为一行，values做为第二行，否则只写出一行values
     * </pre>
     *
     * @param rowBean 写出的Bean
     * @param styleSets 单元格样式
     * @param isWriteKeyAsHead 为true写出两行，Map的keys做为一行，values做为第二行，否则只写出一行values
     * @return this
     * @see #writeRow(Iterable)
     * @see #writeRow(Map, boolean)
     * @since 4.1.5
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public BigExcelWriterNew writeRow(Object rowBean, Iterable<StyleSet> styleSets, boolean isWriteKeyAsHead) {
        if (rowBean instanceof Iterable) {
            return writeRow((Iterable<?>) rowBean, styleSets);
        }
        Map rowMap = null;
        if (rowBean instanceof Map) {
            if (MapUtil.isNotEmpty(this.headerAlias)) {
                rowMap = MapUtil.newTreeMap((Map) rowBean, getInitedAliasComparator());
            } else {
                rowMap = (Map) rowBean;
            }
        } else if (BeanUtil.isBean(rowBean.getClass())) {
            if (MapUtil.isEmpty(this.headerAlias)) {
                rowMap = BeanUtil.beanToMap(rowBean, new LinkedHashMap<String, Object>(), false, false);
            } else {
                // 别名存在情况下按照别名的添加顺序排序Bean数据
                rowMap = BeanUtil.beanToMap(rowBean, new TreeMap<String, Object>(getInitedAliasComparator()), false, false);
            }
        } else {
            // 其它转为字符串默认输出
            return writeRow(CollUtil.newArrayList(rowBean), styleSets, isWriteKeyAsHead);
        }
        return writeRow(rowMap, styleSets, isWriteKeyAsHead);
    }

    /**
     * 将一个Map写入到Excel，isWriteKeyAsHead为true写出两行，Map的keys做为一行，values做为第二行，否则只写出一行values<br>
     * 如果rowMap为空（包括null），则写出空行
     *
     * @param rowMap 写出的Map，为空（包括null），则写出空行
     * @param isWriteKeyAsHead 为true写出两行，Map的keys做为一行，values做为第二行，否则只写出一行values
     * @return this
     */
    public BigExcelWriterNew writeRow(Map<?, ?> rowMap, boolean isWriteKeyAsHead) {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
        if (MapUtil.isEmpty(rowMap)) {
            // 如果写出数据为null或空，跳过当前行
            return passCurrentRow();
        }

        final Map<?, ?> aliasMap = aliasMap(rowMap);

        if (isWriteKeyAsHead) {
            writeHeadRow(aliasMap.keySet());
        }
        writeRow(aliasMap.values());
        return this;
    }

    /**
     * 将一个Map写入到Excel，isWriteKeyAsHead为true写出两行，Map的keys做为一行，values做为第二行，否则只写出一行values<br>
     * 如果rowMap为空（包括null），则写出空行
     *
     * @param rowMap 写出的Map，为空（包括null），则写出空行
     * @param styleSets 单元格样式
     * @param isWriteKeyAsHead 为true写出两行，Map的keys做为一行，values做为第二行，否则只写出一行values
     * @return this
     */
    public BigExcelWriterNew writeRow(Map<?, ?> rowMap, Iterable<StyleSet> styleSets, boolean isWriteKeyAsHead) {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
        if (MapUtil.isEmpty(rowMap)) {
            // 如果写出数据为null或空，跳过当前行
            return passCurrentRow();
        }

        final Map<?, ?> aliasMap = aliasMap(rowMap);

        if (isWriteKeyAsHead) {
            writeHeadRow(aliasMap.keySet(), styleSets);
        }
        writeRow(aliasMap.values(), styleSets);
        return this;
    }

    /**
     * 写出一行数据<br>
     * 本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
     * 写出的起始行为当前行号，可使用{@link #getCurrentRow()}方法调用，根据写出的的行数，当前行号自动+1<br>
     * 样式为默认样式，可使用{@link #getCellStyle()}方法调用后自定义默认样式
     *
     * @param rowData 一行的数据
     * @return this
     */
    public BigExcelWriterNew writeRow(Iterable<?> rowData) {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
        RowUtil.writeRow(this.sheet.createRow(this.currentRow.getAndIncrement()), rowData, this.styleSet, false);
        return this;
    }

    /**
     * 写出一行数据<br>
     * 本方法只是将数据写入Workbook中的Sheet，并不写出到文件<br>
     * 写出的起始行为当前行号，可使用{@link #getCurrentRow()}方法调用，根据写出的的行数，当前行号自动+1<br>
     * 样式为单元格指定样式
     *
     * @param rowData 一行的数据
     * @param styleSets 单元格指定样式
     * @return this
     */
    public BigExcelWriterNew writeRow(Iterable<?> rowData, Iterable<StyleSet> styleSets) {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
        Row row = this.sheet.createRow(this.currentRow.getAndIncrement());
        int i = 0;
        Cell cell;
        Iterator<StyleSet> iterator = styleSets.iterator();
        for (Object value : rowData) {
            cell = row.createCell(i);
            StyleSet ss;
            if (iterator.hasNext()) {
                ss = iterator.next();
            } else {
                ss = this.styleSet;
            }
            CellUtil.setCellValue(cell, value, ss, false);
            i++;
        }
        return this;
    }

    /**
     * 给指定单元格赋值，使用默认单元格样式
     *
     * @param x X坐标，从0计数，既列号
     * @param y Y坐标，从0计数，既行号
     * @param value 值
     * @return this
     * @since 4.0.2
     */
    public BigExcelWriterNew writeCellValue(int x, int y, Object value) {
        final Cell cell = getOrCreateCell(x, y);
        CellUtil.setCellValue(cell, value, this.styleSet, false);
        return this;
    }

    /**
     * 为指定单元格创建样式
     *
     * @param x X坐标，从0计数，既列号
     * @param y Y坐标，从0计数，既行号
     * @return {@link CellStyle}
     * @since 4.0.9
     * @deprecated 请使用{@link #getOrCreateCellStyle(int, int)}
     */
    @Deprecated
    public CellStyle createStyleForCell(int x, int y) {
        final Cell cell = getOrCreateCell(x, y);
        final CellStyle cellStyle = this.workbook.createCellStyle();
        cell.setCellStyle(cellStyle);
        return cellStyle;
    }

    /**
     * 创建字体
     *
     * @return 字体
     * @since 4.1.0
     */
    public Font createFont() {
        return getWorkbook().createFont();
    }

    /**
     * 将Excel Workbook刷出到预定义的文件<br>
     * 如果用户未自定义输出的文件，将抛出{@link NullPointerException}<br>
     * 预定义文件可以通过{@link #setDestFile(File)} 方法预定义，或者通过构造定义
     *
     * @return this
     * @throws IORuntimeException IO异常
     */
    public BigExcelWriterNew flush() throws IORuntimeException {
        return flush(this.destFile);
    }

    /**
     * 将Excel Workbook刷出到文件<br>
     * 如果用户未自定义输出的文件，将抛出{@link NullPointerException}
     *
     * @param destFile 写出到的文件
     * @return this
     * @throws IORuntimeException IO异常
     * @since 4.0.6
     */
    public BigExcelWriterNew flush(File destFile) throws IORuntimeException {
        Assert.notNull(destFile, "[destFile] is null, and you must call setDestFile(File) first or call flush(OutputStream).");
        return flush(FileUtil.getOutputStream(destFile), true);
    }

    /**
     * 将Excel Workbook刷出到输出流
     *
     * @param out 输出流
     * @return this
     * @throws IORuntimeException IO异常
     */
    public BigExcelWriterNew flush(OutputStream out) throws IORuntimeException {
        return flush(out, false);
    }

    /**
     * 将Excel Workbook刷出到输出流
     *
     * @param out 输出流
     * @param isCloseOut 是否关闭输出流
     * @return this
     * @throws IORuntimeException IO异常
     * @since 4.4.1
     */
    public BigExcelWriterNew flush(OutputStream out, boolean isCloseOut) throws IORuntimeException {
        Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
        try {
            this.workbook.write(out);
            out.flush();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            if (isCloseOut) {
                IoUtil.close(out);
            }
        }
        return this;
    }

    /**
     * 关闭工作簿<br>
     * 如果用户设定了目标文件，先写出目标文件后给关闭工作簿
     */
    @Override
    public void close() {
        if (null != this.destFile) {
            flush();
        }
        ((SXSSFWorkbook) this.workbook).dispose();
        closeWithoutFlush();
    }

    /**
     * 关闭工作簿但是不写出
     */
    protected void closeWithoutFlush() {
        super.close();

        // 清空对象
        this.currentRow = null;
        this.styleSet = null;
    }

    // -------------------------------------------------------------------------- Private method start
    /**
     * 为指定的key列表添加标题别名，如果没有定义key的别名，在onlyAlias为false时使用原key
     *
     * @param rowMap 键列表
     * @return 别名列表
     */
    private Map<?, ?> aliasMap(Map<?, ?> rowMap) {
        if (MapUtil.isEmpty(this.headerAlias)) {
            return rowMap;
        }

        final Map<Object, Object> filteredMap = new LinkedHashMap<>();
        String aliasName;
        for (Map.Entry<?, ?> entry : rowMap.entrySet()) {
            aliasName = this.headerAlias.get(entry.getKey());
            if (null != aliasName) {
                // 别名键值对加入
                filteredMap.put(aliasName, entry.getValue());
            } else if (false == this.onlyAlias) {
                // 保留无别名设置的键值对
                filteredMap.put(entry.getKey(), entry.getValue());
            }
        }
        return filteredMap;
    }

    /**
     * 获取单例的别名比较器，比较器的顺序为别名加入的顺序
     *
     * @return Comparator
     * @since 4.1.5
     */
    private Comparator<String> getInitedAliasComparator() {
        if (MapUtil.isEmpty(this.headerAlias)) {
            return null;
        }
        Comparator<String> aliasComparator = this.aliasComparator;
        if (null == aliasComparator) {
            Set<String> keySet = this.headerAlias.keySet();
            aliasComparator = new IndexedComparator<>(keySet.toArray(new String[keySet.size()]));
            this.aliasComparator = aliasComparator;
        }
        return aliasComparator;
    }
    // -------------------------------------------------------------------------- Private method end
}
