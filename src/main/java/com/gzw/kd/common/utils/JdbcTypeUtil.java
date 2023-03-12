package com.gzw.kd.common.utils;

import cn.hutool.core.date.DatePattern;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import com.gzw.kd.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author gaozhiwei
 */
@SuppressWarnings("all")
@Slf4j
public class JdbcTypeUtil {

    public static final  String  TIME_ZONE;
    private static   DateTimeZone DATA_TIME_ZONE;

    static {
        TimeZone localTimeZone = TimeZone.getDefault();
        int rawOffset = localTimeZone.getRawOffset();
        String symbol = "+";
        if (rawOffset < 0) {
            symbol = "-";
        }
        rawOffset = Math.abs(rawOffset);
        int offsetHour = rawOffset / 3600000;
        int offsetMinute = rawOffset % 3600000 / 60000;
        String hour = String.format("%1$02d", offsetHour);
        String minute = String.format("%1$02d", offsetMinute);
        TIME_ZONE = symbol + hour + ":" + minute;
        DATA_TIME_ZONE = DateTimeZone.forID(TIME_ZONE);
        TimeZone.setDefault(TimeZone.getTimeZone("GMT" + TIME_ZONE));
    }

    /**
     * 通用日期时间字符解析
     *
     * @param datetimeStr 日期时间字符串
     * @return Date
     */
    public static java.util.Date parseDate(String datetimeStr) {
        if (StringUtils.isEmpty(datetimeStr)) {
            return null;
        }
        datetimeStr = datetimeStr.trim();
        if (datetimeStr.contains("-")) {
            if (datetimeStr.contains(":")) {
                datetimeStr = datetimeStr.replace(" ", "T");
            }
        } else if (datetimeStr.contains(":")) {
            datetimeStr = "T" + datetimeStr;
        }

        DateTime dateTime = new DateTime(datetimeStr, DATA_TIME_ZONE);

        return dateTime.toDate();
    }

    /**
     * 通用日期时间字符解析
     *
     * @param datetimeStr 日期时间字符串
     * @return Date
     */
    public static LocalTime parseDate2LocalTime(String datetimeStr) {
        if (StringUtils.isEmpty(datetimeStr)) {
            return null;
        }
        datetimeStr = datetimeStr.trim();
        if (!datetimeStr.contains("-") && datetimeStr.contains(":")) {
            return LocalTime.parse(datetimeStr, DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN));
        }
        return null;
    }

    /**
     * 通用日期时间字符解析
     *
     * @param datetimeStr 日期时间字符串
     * @return Date
     */
    public static LocalDateTime parseDate2LocalDateTime(String datetimeStr) {
        if (StringUtils.isEmpty(datetimeStr)) {
            return null;
        }
        datetimeStr = datetimeStr.trim();
        if (datetimeStr.contains("-") && datetimeStr.contains(":")) {
            return LocalDateTime.parse(datetimeStr, Constants.DATE_TIME_FORMAT_S);
        }

        return null;
    }

    /**
     * 通用日期时间字符解析
     *
     * @param datetimeStr 日期时间字符串
     * @return Date
     */
    public static LocalDate parseDate2LocalDate(String datetimeStr) {
        if (StringUtils.isEmpty(datetimeStr)) {
            return null;
        }
        datetimeStr = datetimeStr.trim();
        if (datetimeStr.contains("-") && !datetimeStr.contains(":")) {
            return LocalDate.parse(datetimeStr, Constants.DATE_FORMAT);
        }
        return null;
    }

    public static Object getRSData(ResultSet rs, String columnName, int jdbcType) throws SQLException {
        if (jdbcType == Types.BIT || jdbcType == Types.BOOLEAN) {
            return rs.getByte(columnName);
        } else {
            return rs.getObject(columnName);
        }
    }

    public static Class<?> jdbcType2javaType(int jdbcType) {
        switch (jdbcType) {
            case Types.BIT:
            case Types.BOOLEAN:
                return Boolean.class;
            case Types.TINYINT:
                return Byte.TYPE;
            case Types.SMALLINT:
                return Short.class;
            case Types.INTEGER:
                return Integer.class;
            case Types.BIGINT:
                return Long.class;
            case Types.DECIMAL:
            case Types.NUMERIC:
                return BigDecimal.class;
            case Types.REAL:
                return Float.class;
            case Types.FLOAT:
            case Types.DOUBLE:
                return Double.class;
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                return String.class;
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
            case Types.BLOB:
                return byte[].class;
            case Types.DATE:
                return LocalDate.class;
            case Types.TIME:
                return LocalTime.class;
            case Types.TIMESTAMP:
                return LocalDateTime.class;
            default:
                return String.class;
        }
    }

    private static boolean isText(String columnType) {
        return "LONGTEXT".equalsIgnoreCase(columnType) || "MEDIUMTEXT".equalsIgnoreCase(columnType)
                || "TEXT".equalsIgnoreCase(columnType) || "TINYTEXT".equalsIgnoreCase(columnType);
    }

    public static Object typeConvert(String tableName ,String columnName, String value, int sqlType, String mysqlType) {
        if (value == null
                || (value.equals("") && !(isText(mysqlType) || sqlType == Types.CHAR || sqlType == Types.VARCHAR || sqlType == Types.LONGVARCHAR))) {
            return null;
        }

        try {
            Object res;
            switch (sqlType) {
                case Types.INTEGER:
                    res = Integer.parseInt(value);
                    break;
                case Types.SMALLINT:
                    res = Short.parseShort(value);
                    break;
                case Types.BIT:
                case Types.TINYINT:
                    res = Byte.parseByte(value);
                    break;
                case Types.BIGINT:
                    if (mysqlType.startsWith("bigint") && mysqlType.endsWith("unsigned")) {
                        res = new BigInteger(value);
                    } else {
                        res = Long.parseLong(value);
                    }
                    break;
                case Types.BOOLEAN:
                    res = !"0".equals(value);
                    break;
                case Types.DOUBLE:
                case Types.FLOAT:
                    res = Double.parseDouble(value);
                    break;
                case Types.REAL:
                    res = Float.parseFloat(value);
                    break;
                case Types.DECIMAL:
                case Types.NUMERIC:
                    res = new BigDecimal(value);
                    break;
                case Types.BINARY:
                case Types.VARBINARY:
                case Types.LONGVARBINARY:
                case Types.BLOB:
                    res = value.getBytes("ISO-8859-1");
                    break;
                case Types.DATE:
                    if (!value.startsWith("0000-00-00")) {
                        res = parseDate2LocalDate(value);
                    } else {
                        res = null;
                    }
                    break;
                case Types.TIME: {
                    res = parseDate2LocalTime(value);
                    break;
                }
                case Types.TIMESTAMP:
                    if (!value.startsWith("0000-00-00")) {
                        res = value;
                    } else {
                        res = null;
                    }
                    break;
                case Types.CLOB:
                default:
                    res = value;
                    break;
            }
            return res;
        } catch (Exception e) {
            log.error("table: {} column: {}, failed convert type {} to {}", tableName, columnName, sqlType, value);
            return value;
        }
    }
}
